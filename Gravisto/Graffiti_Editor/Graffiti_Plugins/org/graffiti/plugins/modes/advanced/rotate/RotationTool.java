// =============================================================================
//
//   RotationTool.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: RotationTool.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced.rotate;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.GraphElementNotFoundException;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugin.view.GraphElementComponent;
import org.graffiti.plugins.modes.advanced.AbstractEditingTool;
import org.graffiti.plugins.modes.advanced.FunctionAction;
import org.graffiti.plugins.modes.advanced.InvalidInputEventException;
import org.graffiti.plugins.modes.advanced.InvalidParameterException;
import org.graffiti.plugins.modes.advanced.NoSuchFunctionActionException;
import org.graffiti.plugins.modes.advanced.PositionInfo;
import org.graffiti.plugins.modes.advanced.ToolPlugin;
import org.graffiti.plugins.views.defaults.DummySupportView;
import org.graffiti.plugins.views.defaults.NodeComponent;

/**
 * This tool allows to rotate marked graph elements around a reference node,
 * that can be moved freely. Note that you have to mark the edges, if you want
 * to rotate their bends. If an edge is marked, but its source and target node
 * are not, only one of the bends can be rotated (the nearest one). If an edge
 * is not marked, has no bends, but its aource and target are marked and
 * rotated, this edge is rotated, too.
 * 
 * @author Marek Piorkowski
 * @version $Revision: 5766 $ $Date: 2008-12-31 05:02:05 +0100 (Mi, 31 Dez 2008)
 *          $
 * @deprecated
 */
@Deprecated
public class RotationTool extends AbstractEditingTool {

    /** Logger for the class SelectionTool */
    private static final Logger logger = Logger.getLogger(RotationTool.class
            .getName());

    /** all elements can be rotated around this free-movable reference node */
    private Node referenceNode;

    /** the reference node's dimension */
    private final Dimension referenceNodeDim = new Dimension(25, 25);

    /** the reference node's coordinates */
    private CoordinateAttribute referenceNodeCoords;

    /**
     * the reference node's position when this tool is activated for the first
     * time
     */
    private final Point2D referenceNodeStartingPosition = new Point2D.Double(
            20, 20);

    /**
     * the reference node's last graphic attribute before deactivating this tool
     */
    private Point2D referenceNodeLastPosition = null;

    /**
     * A bend's coordinates, if only one bend is moved, or a node's coordinates,
     * if more garph elements are moved and the mouse is over this node. These
     * are the current coordinates, that change while moving the mouse with a
     * pressed mouse button.
     */
    private CoordinateAttribute rotateElementCurrentCA;

    /**
     * A bend's coordinates, if only one bend is moved, or a node's coordinates,
     * if more garph elements are moved and the mouse is over this node. These
     * are the initial coordinates, which do not change until the mouse button
     * is released.
     */
    private CoordinateAttribute rotateElementInitialCA;

    /** selection rectangle mode */
    public static final int RECT = 1;

    /** rotate-marked-elements-mode */
    public static final int ROTATE_MARKED_ELEMENTS = 2;

    /** move-only-reference-node mode */
    public static final int MOVE_REFERENCE_NODE = 3;

    /** rotate-only-bend-mode */
    public static final int ROTATE_ONLY_BEND = 5;

    /** If the tool is in no special mode it is in DEFAULT_MODE */
    private static final int DEFAULT_MODE = 0;

    /**
     * Current mode of the tool. Determines how (and if) some of the tools
     * functions work
     */
    private int mode = DEFAULT_MODE;

    /** true if tool is during a single-bend-rotation update */
    private boolean during_update_bend_rotation;

    /** true if tool is during a rotation update */
    private boolean during_update_rotation;

    /** true, if tool is during a move-reference-node update */
    private boolean during_update_move;

    /** Position of the current mouse position */
    private Point currentSelectRectPosition = null;

    /** The old position of the mouse */
    private Point oldPosition;

    /** Position of the point where the rectangle starts */
    private Point startSelectRectPosition = null;

    /** The currently selected elements */
    private Set<GraphElementComponent> currentSelectedElements = new HashSet<GraphElementComponent>();

    /** The elements which was former selected */
    private Set<GraphElement> formerSelectedElements = new HashSet<GraphElement>();

    /**
     * The initial coordinates of the nodes which are not changed until the
     * mouse button is released.
     */
    private CoordinateAttribute[] initialNodeCoords;

    /**
     * The initial coordinates of the bends which are not changed until the
     * mouse button is released
     */
    private ArrayList<CoordinateAttribute> initialBendCoords;

    /**
     * Creates a new RotationTool.
     * 
     * @param toolPlugin
     *            The plugin, from which this tool is started.
     * @param positionInfo
     *            The position info.
     */
    public RotationTool(ToolPlugin toolPlugin, PositionInfo positionInfo) {
        super(toolPlugin, positionInfo);
        try {
            functionManager.addFunction(
                    "mouse 1x not_shift not_ctrl clicked button1",
                    "select-element", "remove-old-selection", "yes",
                    "connections", "no");
            functionManager.addFunction(
                    "mouse 1x shift not_ctrl clicked button1",
                    "select-element", "remove-old-selection", "no",
                    "connections", "yes");
            functionManager.addFunction(
                    "mouse 1x not_shift ctrl clicked button1",
                    "select-element", "remove-old-selection", "no",
                    "connections", "no");

            functionManager.addFunction(
                    "mouse not_shift not_ctrl pressed button1",
                    "start-select-region", "remove-old-selection", "yes");

            functionManager.addFunction("mouse not_shift ctrl pressed button1",
                    "start-select-region", "remove-old-selection", "no");
            functionManager.addFunction(
                    "movedMouse not_shift not_ctrl button1",
                    "update-selection-region", "remove-old-selection", "yes");
            functionManager.addFunction("movedMouse not_shift ctrl button1",
                    "update-selection-region", "remove-old-selection", "no");
            functionManager.addFunction("mouse 0x released button1",
                    "finish-select-region");
            functionManager.addFunction("mouse 1x released button1",
                    "finish-select-region");

            functionManager
                    .addFunction("mouse 1x not_shift not_ctrl pressed button1",
                            "start-move");
            functionManager.addFunction(
                    "movedMouse not_shift  not_ctrl button1", "update-move");
            functionManager.addFunction("mouse 1x released button1",
                    "finish-move");
            functionManager.addFunction("mouse 0x released button1",
                    "finish-move");
        } catch (InvalidInputEventException e) {
            logger.finer("Input-event " + e.getEvent()
                    + ", assigned to function " + e.getFunction()
                    + " has invalid syntax!");
            System.exit(-1);
        } catch (NoSuchFunctionActionException e) {
            logger.finer("Can't find " + " Action assigned to function "
                    + e.getFunction());
            System.exit(-1);
        } catch (InvalidParameterException e) {
            logger.finer(e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * @see org.graffiti.plugins.modes.advanced.AbstractEditingTool#getFunctionAction(java.lang.String)
     */
    @Override
    public FunctionAction getFunctionAction(String functionName) {
        FunctionAction superAction = super.getFunctionAction(functionName);

        if (superAction != null)
            return superAction;
        else if (functionName.equals("select-element"))
            return new SelectionAction(this);
        else if (functionName.equals("finish-select-region"))
            return new FinishSelectRectAction(this);
        else if (functionName.equals("update-selection-region"))
            return new UpdateSelectionRectAction(this);
        else if (functionName.equals("start-select-region"))
            return new StartSelectRectAction(this);
        else if (functionName.equals("start-move"))
            return new StartRotateAction(this);
        else if (functionName.equals("update-move"))
            return new UpdateRotateAction(this);
        else if (functionName.equals("finish-move"))
            return new FinishRotateAction(this);
        else
            return null;
    }

    /**
     * @see org.graffiti.plugins.modes.deprecated.Tool#deactivate()
     */
    @Override
    public void deactivate() {
        super.deactivate();

        try {
            // remember old position of the reference node
            referenceNodeLastPosition = new Point2D.Double(referenceNodeCoords
                    .getX(), referenceNodeCoords.getY());

            List<DummySupportView> views = getDummySupportViews();
            for (DummySupportView view : views) {
                if (referenceNode != null) {
                    view.removeViewForNode(referenceNode);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (GraphElementNotFoundException e) {
            e.printStackTrace();
        }

        // erase the reference node's existence
        referenceNode = null;

        switchToDefaultMode();
    }

    /**
     * @see org.graffiti.plugins.modes.advanced.AbstractEditingTool#activate()
     */
    @Override
    public void activate() {
        super.activate();
        if (referenceNode == null) {
            // create a new reference node
            NodeGraphicAttribute nga = null;
            nga = new NodeGraphicAttribute();
            nga.getDimension().setDimension(referenceNodeDim);
            nga.getFillcolor().setColor(Color.BLACK);
            nga.getFramecolor().setColor(Color.yellow);
            nga.setFrameThickness(5d);
            nga.setShape(GraphicAttributeConstants.CIRCLE_CLASSNAME);
            referenceNodeCoords = nga.getCoordinate();
            if (referenceNodeLastPosition == null) {
                referenceNodeCoords
                        .setCoordinate(referenceNodeStartingPosition);
            } else {
                referenceNodeCoords.setCoordinate(referenceNodeLastPosition);
            }
            CollectionAttribute dummyCol = new HashMapAttribute("");
            dummyCol.add(nga, true);

            referenceNode = GraffitiSingleton.getInstance().getMainFrame()
                    .getActiveEditorSession().getGraph().createNode(dummyCol);

            List<DummySupportView> views = getDummySupportViews();

            for (DummySupportView view : views) {
                view.addViewForNode(referenceNode);
            }
        }
    }

    /**
     * Method that delivers a Map with bends of a certain edge
     * 
     * @param edge
     *            The given edge
     * 
     * @return A Map with the bends of the edge edge
     */
    public Map<String, Attribute> getBendsOfEdge(Edge edge) {
        CollectionAttribute attributesEdge = edge.getAttributes();
        EdgeGraphicAttribute edgeAttributes = (EdgeGraphicAttribute) attributesEdge
                .getAttribute(GraphicAttributeConstants.GRAPHICS);
        Map<String, Attribute> bends = edgeAttributes.getBends()
                .getCollection();

        return bends;
    }

    /*
     * @see
     * org.graffiti.plugins.modes.advanced.AbstractEditingTool#activateKeyBindings
     * (javax.swing.JComponent)
     */
    @Override
    protected void activateKeyBindings(JComponent viewComponent) {
        functionManager.activateAllKeyBindings(viewComponent);
    }

    /*
     * @see
     * org.graffiti.plugins.modes.advanced.AbstractEditingTool#deactivateKeyBindings
     * (javax.swing.JComponent)
     */
    @Override
    protected void deactivateKeyBindings(JComponent viewComponent) {
        functionManager.deactivateAllKeyBindings(viewComponent);
    }

    /**
     * Returns a set of components in a given container
     * 
     * @param container
     *            The given Container
     * 
     * @return a set of components in a given container
     */
    public Set<Component> getContainerComponents(Container container) {
        Component[] components = container.getComponents();

        Set<Component> retSet = new HashSet<Component>();

        for (Component element : components) {
            retSet.add(element);
        }

        return retSet;
    }

    /**
     * Sets the current selection rectangle positiion
     * 
     * @param point
     *            Position which has to be set to the current selection
     *            rectangle position
     */
    public void setCurrentSelectRectPosition(Point point) {
        currentSelectRectPosition = point;
    }

    /**
     * Returns the currently selected elements
     * 
     * @return The currently selected elements
     */
    public Set<GraphElementComponent> getCurrentSelectedElements() {
        return currentSelectedElements;
    }

    /**
     * Set the currently selected elements.
     * 
     * @param currentSelectedElements
     *            Currently selected elements.
     */
    public void setCurrentSelectedElements(
            Set<GraphElementComponent> currentSelectedElements) {
        this.currentSelectedElements = currentSelectedElements;
    }

    /**
     * Sets the variable during_update_rotation to b True, if tool is during an
     * update of a graph element.
     * 
     * @param b
     *            boolean value
     */
    public void setDuring_update_rotation(boolean b) {
        during_update_rotation = b;
    }

    /**
     * Returns value of during_update_rotation. True, if tool is during an
     * update of a graph element.
     * 
     * @return <code>true</code> if an element is updated, <code>false</code>
     *         otherwise.
     */
    public boolean isDuring_update_rotation() {
        return during_update_rotation;
    }

    /**
     * Sets the variable during_update_bend_rotation to b. True, if tool is
     * during the update move of a bend.
     * 
     * @param b
     *            the given boolean value
     */
    public void setDuring_update_bend_rotation(boolean b) {
        during_update_bend_rotation = b;
    }

    /**
     * Returns value of during_update_bend_rotation. True, if tool is during an
     * update of a bend.
     * 
     * @return <code>true</code> if bend is updated, <code>false</code>
     *         otherwise.
     */
    public boolean isDuring_update_bend_rotation() {
        return during_update_bend_rotation;
    }

    /**
     * Sets the variable during_update_move to b True, if tool is during an
     * update of a graph element.
     * 
     * @param b
     *            boolean value
     */
    public void setDuring_update_move(boolean b) {
        during_update_move = b;
    }

    /**
     * Returns value of during_update_move. True, if tool is during an update of
     * a graph element.
     * 
     * @return <code>true</code> if an element is updated, <code>false</code>
     *         otherwise.
     */
    public boolean isDuring_update_move() {
        return during_update_move;
    }

    /**
     * Returns the former selected elements
     * 
     * @return the former selected elements
     */
    public Set<GraphElement> getFormerSelectedElements() {
        return formerSelectedElements;
    }

    /**
     * Returns the mode of this tool
     * 
     * @return the mode of this tool
     */
    @Override
    public int getMode() {
        return this.mode;
    }

    /**
     * Sets the mode to default
     */
    @Override
    public void setModeToDefault() {
        this.mode = DEFAULT;
        setHighlightPointedElements(true);
    }

    /**
     * Sets the mode to rotate marked elements mode
     */
    public void setModeToRotateMarkedElements() {
        this.mode = ROTATE_MARKED_ELEMENTS;
    }

    /**
     * Sets the mode to totate only bend mode
     */
    public void setModeToRotateOnlyBend() {
        this.mode = ROTATE_ONLY_BEND;
    }

    /**
     * Sets the mode to move the reference node mode
     */
    public void setModeToMoveReferenceNode() {
        this.mode = MOVE_REFERENCE_NODE;
    }

    /**
     * Sets the mode to select rectangle mode
     */
    public void setModeToRect() {
        this.mode = RECT;
        setHighlightPointedElements(false);
    }

    /**
     * Returns the selection rectangle
     * 
     * @return The selection rectangle
     */
    public Rectangle getSelectionRectangle() {
        Point p1 = startSelectRectPosition;
        Point p2 = currentSelectRectPosition;

        int tlx;
        int tly;
        int w;
        int h;
        tlx = (int) Math.min(p1.getX(), p2.getX());
        tly = (int) Math.min(p1.getY(), p2.getY());
        w = (int) Math.abs(p1.getX() - p2.getX());
        h = (int) Math.abs(p1.getY() - p2.getY());

        return new Rectangle(tlx, tly, w, h);
    }

    /**
     * Sets the start position of the selection rectangle to point
     * 
     * @param point
     *            the new start position of the selection rectangle
     */
    public void setStartSelectRectPosition(Point point) {
        startSelectRectPosition = point;
    }

    /**
     * Filters the GEComponents out of the given Set components
     * 
     * @param components
     *            The given Set.
     */
    public Set<GraphElementComponent> filterGEComponents(
            Set<Component> components) {
        Set<GraphElementComponent> result = new HashSet<GraphElementComponent>();

        for (Component component : components)
            if (component instanceof GraphElementComponent) {
                if (component instanceof NodeComponent) {
                    NodeComponent nc = (NodeComponent) component;
                    if (nc.getGraphElement() != referenceNode) {
                        result.add((GraphElementComponent) component);
                    }
                } else {
                    result.add((GraphElementComponent) component);
                }
            }

        return result;
    }

    /**
     * Filters the components, where rectangle intersects.
     * 
     * @param components
     *            The given components
     * @param rectangle
     *            The given rectangle
     */
    public void filterRectComponents(Set<Component> components,
            Rectangle rectangle) {
        Iterator<Component> iterator = components.iterator();
        while (iterator.hasNext()) {
            Component currComponent = iterator.next();

            if (!rectangle.intersects(currComponent.getBounds())) {
                iterator.remove();
            }
        }
    }

    /**
     * For undo/redo (saves the position)
     * 
     * @param oldPosition
     *            The oldPosition to set.
     */
    protected void setOldPosition(Point oldPosition) {
        this.oldPosition = oldPosition;
    }

    /**
     * For undo/redo (saves the position)
     * 
     * @return Returns the oldPosition.
     */
    protected Point getOldPosition() {
        return oldPosition;
    }

    /**
     * Returns true if the tool is in default mode
     * 
     * @return true if the tool is in default mode
     */
    boolean isDefaultMode() {
        return mode == DEFAULT;
    }

    /**
     * Returns the referenceNode.
     * 
     * @return the referenceNode.
     */
    public Node getReferenceNode() {
        return referenceNode;
    }

    /**
     * Returns the referenceNodeCoords.
     * 
     * @return the referenceNodeCoords.
     */
    public CoordinateAttribute getReferenceNodeCoords() {
        return referenceNodeCoords;
    }

    /**
     * Sets the referenceNodeCoords.
     * 
     * @param referenceNodeCoords
     *            the referenceNodeCoords to set.
     */
    public void setReferenceNodeCoords(CoordinateAttribute referenceNodeCoords) {
        this.referenceNodeCoords = referenceNodeCoords;
    }

    /**
     * Returns the referenceNodeDim.
     * 
     * @return the referenceNodeDim.
     */
    public Dimension getReferenceNodeDim() {
        return referenceNodeDim;
    }

    /**
     * Returns the rotateElementCurrentCA.
     * 
     * @return the rotateElementCurrentCA.
     */
    public CoordinateAttribute getRotateElementCurrentCA() {
        return rotateElementCurrentCA;
    }

    /**
     * Sets the rotateElementCurrentCA.
     * 
     * @param rotateElementCurrentCA
     *            the rotateElementCurrentCA to set.
     */
    public void setRotateElementCurrentCA(
            CoordinateAttribute rotateElementCurrentCA) {
        this.rotateElementCurrentCA = rotateElementCurrentCA;
    }

    /**
     * Returns the rotateElementInitialCA.
     * 
     * @return the rotateElementInitialCA.
     */
    public CoordinateAttribute getRotateElementInitialCA() {
        return rotateElementInitialCA;
    }

    /**
     * Sets the rotateElementInitialCA.
     * 
     * @param rotateElementInitialCA
     *            the rotateElementInitialCA to set.
     */
    public void setRotateElementInitialCA(
            CoordinateAttribute rotateElementInitialCA) {
        this.rotateElementInitialCA = (CoordinateAttribute) rotateElementInitialCA
                .copy();
    }

    /**
     * Returns the initialBendCoords.
     * 
     * @return the initialBendCoords.
     */
    public ArrayList<CoordinateAttribute> getInitialBendCoords() {
        return initialBendCoords;
    }

    /**
     * Sets the initialBendCoords.
     * 
     * @param initialBendCoords
     *            the initialBendCoords to set.
     */
    public void setInitialBendCoords(
            ArrayList<CoordinateAttribute> initialBendCoords) {
        this.initialBendCoords = initialBendCoords;
    }

    /**
     * Returns the initialNodeCoords.
     * 
     * @return the initialNodeCoords.
     */
    public CoordinateAttribute[] getInitialNodeCoords() {
        return initialNodeCoords;
    }

    /**
     * Sets the initialNodeCoords.
     * 
     * @param initialNodeCoords
     *            the initialNodeCoords to set.
     */
    public void setInitialNodeCoords(CoordinateAttribute[] initialNodeCoords) {
        this.initialNodeCoords = initialNodeCoords;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
