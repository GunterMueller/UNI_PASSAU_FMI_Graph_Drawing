// =============================================================================
//
//   SelectionTool.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SelectionTool.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced.selection;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.plugin.view.GraphElementComponent;
import org.graffiti.plugins.modes.advanced.AbstractEditingTool;
import org.graffiti.plugins.modes.advanced.FunctionAction;
import org.graffiti.plugins.modes.advanced.InvalidInputEventException;
import org.graffiti.plugins.modes.advanced.InvalidParameterException;
import org.graffiti.plugins.modes.advanced.NoSuchFunctionActionException;
import org.graffiti.plugins.modes.advanced.PositionInfo;
import org.graffiti.plugins.modes.advanced.ShortCutAction;
import org.graffiti.plugins.modes.advanced.ShowPopupMenuAction;
import org.graffiti.plugins.modes.advanced.ToolPlugin;

/**
 * An editing-tool for selecting and moving graph-elements. Note: A (the) region
 * is an area of the graph during the process of selecting elements (currently
 * always a rectangle).
 * 
 * @deprecated
 */
@Deprecated
public class SelectionTool extends AbstractEditingTool {
    /** Logger for the class SelectionTool */
    private static final Logger logger = Logger.getLogger(SelectionTool.class
            .getName());

    /** selection rectangle mode */
    public static final int RECT = 1;

    /** move marked elements mode */
    public static final int MOVE_MARKED_ELEMENTS = 2;

    /** move only node mode */
    public static final int MOVE_ONLY_NODE = 4;

    /** move only bend mode */
    public static final int MOVE_ONLY_BEND = 5;

    /** If the tool is in no special mode it is in DEFAULT_MODE */
    private static final int DEFAULT_MODE = 0;

    /** true if tool ist during a move bend update */
    boolean during_update_move_bend;

    /** CoordinateAttribute of the element to move */
    private CoordinateAttribute moveElement;

    /** GraphElement to move */
    private GraphElement graphElementToMove;

    /** List of edges which have to be moved */
    private List<Edge> moveEdges;

    /** List of nodes which have to be moved */
    private List<Node> moveNodes;

    /** Position of the current mouse position [MH] */
    private Point currentSelectRectPosition = null;

    /** The old position of the mouse */
    private Point oldPosition;

    /** Position of the Point where the rectangle starts [MH] */
    private Point startSelectRectPosition = null;

    /** The currently selected elements */
    private Set<GraphElementComponent> currentSelectedElements = new HashSet<GraphElementComponent>();

    /** The elements which was former selected */
    private Set<GraphElement> formerSelectedElements = new HashSet<GraphElement>();

    /** true if tool is during a move update */
    private boolean during_update_move;

    /** true if tool is during a move only bend update */
    private boolean during_update_move_only_node;

    /**
     * Current mode of the tool. Determines how (and if) some of the tools
     * functions work
     */
    private int mode = DEFAULT_MODE;

    /**
     * Creates a new SelectionTool object.
     * 
     * @param toolPlugin
     *            The given ToolPlugin
     * @param positionInfo
     *            The given PositionInfo
     */
    public SelectionTool(ToolPlugin toolPlugin, PositionInfo positionInfo) {
        super(toolPlugin, positionInfo);

        try {
            functionManager.addFunction("popupTrigger", "show-popup-menu");
            functionManager.addFunction("typed m", "show-popup-menu");

            // functions for Alignment
            functionManager.addFunction("typed r", "align", "alignDirection",
                    RIGHT);
            functionManager.addFunction("typed v", "align", "alignDirection",
                    CENTER_VERTICAL);
            functionManager.addFunction("typed l", "align", "alignDirection",
                    LEFT);
            functionManager.addFunction("typed t", "align", "alignDirection",
                    TOP);
            functionManager.addFunction("typed h", "align", "alignDirection",
                    CENTER_HORIZONTAL);
            functionManager.addFunction("typed b", "align", "alignDirection",
                    BOTTOM);
            functionManager.addFunction("typed c", "align", "alignDirection",
                    CENTER);

            functionManager.addFunction("typed n", "mark-underlying-node",
                    "remove-old-selection", "yes");
            functionManager.addFunction("typed x", "mark-underlying-node",
                    "remove-old-selection", "no");

            // TODO: does not work with alt, we don't know why [MH]
            // functionManager.addFunction("mouse 1x alt clicked button1",
            // "mark-underlying-node",
            // "remove-old-selection", "no");
            functionManager.addFunction("mouse 1x shift clicked button2",
                    "mark-underlying-node", "remove-old-selection", "no");
            functionManager.addFunction("mouse 1x ctrl clicked button2",
                    "mark-underlying-node", "remove-old-selection", "no");
            functionManager.addFunction(
                    "mouse 1x not_shift not_ctrl clicked button2",
                    "mark-underlying-node", "remove-old-selection", "yes");

            // functions for selecting
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

            // move nodes || bends
            functionManager
                    .addFunction("mouse 1x not_shift not_ctrl pressed button1",
                            "start-move");
            functionManager.addFunction(
                    "movedMouse not_shift  not_ctrl button1", "update-move");
            functionManager.addFunction("mouse 1x released button1",
                    "finish-move");
            functionManager.addFunction("mouse 0x released button1",
                    "finish-move");

            // move nodes && bends
            functionManager.addFunction(
                    "mouse 1x shift not_ctrl pressed button1",
                    "start-move-with-bends");
            functionManager.addFunction("movedMouse shift not_ctrl button1",
                    "update-move");

            // functions for moving with cursors (fast & slow)
            functionManager.addFunction("UP", "move-node", "direction", "up",
                    "moveSteps", "smallSteps");
            functionManager.addFunction("RIGHT", "move-node", "direction",
                    "right", "moveSteps", "smallSteps");
            functionManager.addFunction("DOWN", "move-node", "direction",
                    "down", "moveSteps", "smallSteps");
            functionManager.addFunction("LEFT", "move-node", "direction",
                    "left", "moveSteps", "smallSteps");

            functionManager.addFunction("ctrl LEFT", "move-node", "direction",
                    "left", "moveSteps", "largeSteps");
            functionManager.addFunction("ctrl RIGHT", "move-node", "direction",
                    "right", "moveSteps", "largeSteps");
            functionManager.addFunction("ctrl UP", "move-node", "direction",
                    "up", "moveSteps", "largeSteps");
            functionManager.addFunction("ctrl DOWN", "move-node", "direction",
                    "down", "moveSteps", "largeSteps");
            functionManager.addFunction("shift LEFT", "move-node", "direction",
                    "left", "moveSteps", "veryLargeSteps");
            functionManager.addFunction("shift RIGHT", "move-node",
                    "direction", "right", "moveSteps", "veryLargeSteps");
            functionManager.addFunction("shift UP", "move-node", "direction",
                    "up", "moveSteps", "veryLargeSteps");
            functionManager.addFunction("shift DOWN", "move-node", "direction",
                    "down", "moveSteps", "veryLargeSteps");
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
            functionManager.addFunction("shift DELETE", "linux-shortcuts",
                    "action", "cut");
            functionManager.addFunction("ctrl DELETE", "linux-shortcuts",
                    "action", "copy");
            functionManager.addFunction("shift INSERT", "linux-shortcuts",
                    "action", "paste");
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
                .getAttribute(EdgeGraphicAttribute.GRAPHICS);
        Map<String, Attribute> bends = edgeAttributes.getBends()
                .getCollection();

        return bends;
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
     * Sets the variable during_update_move to b True, if tool is during an
     * update of a graph element. Else, false
     * 
     * @param b
     *            boolean value
     */
    public void setDuring_update_move(boolean b) {
        during_update_move = b;
    }

    /**
     * Returns value of during_update_move. True, if tool is during an update of
     * a graph element. Else, false
     * 
     * @return <code>true</code> if an element is updated, <code>false</code>
     *         otherwise.
     */
    public boolean isDuring_update_move() {
        return during_update_move;
    }

    /**
     * Sets the variable during_update_rotate_bend to b. True, if tool is during
     * the update move of a bend. Else, false
     * 
     * @param b
     *            the given boolean value
     */
    public void setDuring_update_move_bend(boolean b) {
        during_update_move_bend = b;
    }

    /**
     * Returns value of during_update_rotate_bend. True, if tool is during an
     * update of a bend. Else, false
     * 
     * @return <code>true</code> if bend is updated, <code>false</code>
     *         otherwise.
     */
    public boolean isDuring_update_move_bend() {
        return during_update_move_bend;
    }

    /**
     * Sets the variable during_update_move to b True, if tool is during an
     * update of a graph element. Else, false
     * 
     * @param b
     *            boolean value
     */
    public void setDuring_update_move_only_node(boolean b) {
        during_update_move_only_node = b;
    }

    /**
     * True if tool is during an move only node update, else false
     * 
     * @return True if tool is during an move only node update, else false
     */
    public boolean isDuring_update_move_only_node() {
        return during_update_move_only_node;
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
     * Returns the FunctionAction of a given function
     * 
     * @param functionName
     *            Given name of the function
     * 
     * @return The action of the function (if not exists: then null)
     */
    public FunctionAction getFunctionAction(String functionName) {
        FunctionAction superAction = super.getFunctionAction(functionName);

        if (superAction != null)
            return superAction;
        else if (functionName.equals("show-popup-menu"))
            return new ShowPopupMenuAction(this);
        else if (functionName.equals("select-element"))
            return new SelectionAction(this);
        else if (functionName.equals("start-move"))
            return new StartMoveAction(this);
        else if (functionName.equals("update-move"))
            return new UpdateMoveAction(this);
        else if (functionName.equals("finish-move"))
            return new FinishMoveAction(this);
        else if (functionName.equals("start-move-with-bends"))
            return new StartMoveWithBendsAction(this);
        else if (functionName.equals("move-node"))
            return new MoveAction(this);
        else if (functionName.equals("finish-select-region"))
            return new FinishSelectRectAction(this);
        else if (functionName.equals("update-selection-region"))
            return new UpdateSelectionRectAction(this);
        else if (functionName.equals("start-select-region"))
            return new StartSelectRectAction(this);
        else if (functionName.equals("align"))
            return new AlignAction(this);
        else if (functionName.equals("mark-underlying-node"))
            return new MarkUnderlyingNodeAction(this);
        else if (functionName.equals("linux-shortcuts"))
            return new ShortCutAction(this);
        else
            return null;
    }

    /**
     * Sets the graphelement to move to graphElement
     * 
     * @param graphElement
     */
    public void setGraphElementToMove(GraphElement graphElement) {
        graphElementToMove = graphElement;
    }

    /**
     * Returns the graphelement to move
     * 
     * @return the graphelement to move
     */
    public GraphElement getGraphElementToMove() {
        return graphElementToMove;
    }

    /**
     * Returns the mode of the actual tool
     * 
     * @return the mode of the actual tool
     */
    public int getMode() {
        return this.mode;
    }

    /**
     * Sets the mode to default
     */
    public void setModeToDefault() {
        this.mode = DEFAULT;
        setHighlightPointedElements(true);
    }

    /**
     * Sets the mode to move marked elements mode
     */
    public void setModeToMoveMarkedElements() {
        this.mode = MOVE_MARKED_ELEMENTS;
    }

    /**
     * Sets the mode to move only bend mode
     */
    public void setModeToMoveOnlyBend() {
        this.mode = MOVE_ONLY_BEND;
    }

    /**
     * Sets the mode to move only node mode
     */
    public void setModeToMoveOnlyNode() {
        this.mode = MOVE_ONLY_NODE;
    }

    /**
     * Sets the mode to select rectangle mode
     */
    public void setModeToRect() {
        this.mode = RECT;
        setHighlightPointedElements(false);
    }

    /**
     * Sets the moveEdges to list.
     * 
     * @param list
     *            The given List of edges.
     */
    public void setMoveEdges(List<Edge> list) {
        moveEdges = list;
    }

    /**
     * Returns the moveNodes
     * 
     * @return the moveNodes
     */
    public List<Edge> getMoveEdges() {
        return moveEdges;
    }

    /**
     * Sets the list moveNodes to list
     * 
     * @param list
     *            The given List of nodes
     */
    public void setMoveNodes(List<Node> list) {
        moveNodes = list;
    }

    /**
     * Returns the moveNodes
     * 
     * @return the moveNodes
     */
    public List<Node> getMoveNodes() {
        return moveNodes;
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
                result.add((GraphElementComponent) component);
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
     * Activates the key bindings
     * 
     * @param viewComponent
     *            The given view component
     */
    protected void activateKeyBindings(JComponent viewComponent) {
        functionManager.activateAllKeyBindings(viewComponent);
    }

    /**
     * Activates the key bindings
     * 
     * @param viewComponent
     *            The given view component
     */
    protected void deactivateKeyBindings(JComponent viewComponent) {
        functionManager.deactivateAllKeyBindings(viewComponent);
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
     * Sets the moveElement to moveElement
     * 
     * @param moveElement
     *            the element to move
     */
    void setMoveElement(CoordinateAttribute moveElement) {
        this.moveElement = moveElement;
    }

    /**
     * Returns the element which has to be moved.
     * 
     * @return the element which has to be moved.
     */
    CoordinateAttribute getMoveElement() {
        return moveElement;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
