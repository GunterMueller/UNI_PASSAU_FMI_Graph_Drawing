// =============================================================================
//
//   CreateTool.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: CreateTool.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced.create;

import java.awt.Color;
import java.awt.Point;
import java.util.List;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JComponent;
import javax.swing.undo.UndoableEditSupport;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.DockingAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugins.modes.advanced.AbstractEditingTool;
import org.graffiti.plugins.modes.advanced.FunctionAction;
import org.graffiti.plugins.modes.advanced.FunctionComponent;
import org.graffiti.plugins.modes.advanced.InvalidInputEventException;
import org.graffiti.plugins.modes.advanced.InvalidParameterException;
import org.graffiti.plugins.modes.advanced.NoSuchFunctionActionException;
import org.graffiti.plugins.modes.advanced.PositionInfo;
import org.graffiti.plugins.modes.advanced.ShortCutAction;
import org.graffiti.plugins.modes.advanced.ShowPopupMenuAction;
import org.graffiti.plugins.modes.advanced.ToolPlugin;
import org.graffiti.plugins.views.defaults.DummySupportView;
import org.graffiti.undo.AddEdgeEdit;

/**
 * Editing tool for creating, deleting and moving (single) nodes and edges.
 * 
 * @deprecated
 */
@Deprecated
public class CreateTool extends AbstractEditingTool implements
        GraphicAttributeConstants {
    /** DOCUMENT ME! */
    private static final Logger logger = Logger.getLogger(CreateTool.class
            .getName());

    /**
     * (Dummy) edge painted between the start-node and the current
     * mouse-position while the tool is in MULTI_COMMAND_MODE and an edge is
     * currently being added. Not added to the graph.
     */
    private Edge dummyEdge = null;

    /** If an edge is just being created, this stores its source-node. */
    private Node addEdgeSourceNode = null;

    /**
     * Dummy-node used as an end-point of the edge from the start-node to the
     * current mouse-position while the tool is in MULTI_COMMAND_MODE and an
     * edge is currently being added. Not added to the graph.
     */
    private Node dummyNode = null;

    /**
     * Indicates that one of the functions dealing with adding edges changed or
     * wanted to change (for example if finish-add-edge can't switch back to
     * DEFAULT_MODE because moveNode != null indicates node-movement) mode.
     */
    private boolean addEdgeChangedMode = false;

    /**
     * Constructs a new CreateTool. Sets up all functions. _Currently_, also the
     * bindings are defined here (hard-coded).
     * 
     * @param toolPlugin
     *            ToolPlugin-instance this tool belongs to.
     * @param positionInfo
     *            Information of the position of the mouse
     */
    public CreateTool(ToolPlugin toolPlugin, PositionInfo positionInfo) {
        super(toolPlugin, positionInfo);

        try {
            // functionManager.addFunction("typed b", "add-node",
            // "over-existing-node", "yes", "mark-node", "no");
            //
            // functionManager.addFunction("mouse 1x not_ctrl clicked button1",
            // "add-node", "over-existing-node", "no", "mark-node", "no");
            //            
            functionManager.addFunction("typed b", "add-node",
                    "over-existing-node", "no", "mark-node", "additionally");

            functionManager.addFunction("mouse 1x not_ctrl clicked button1",
                    "add-node", "over-existing-node", "no", "mark-node",
                    "additionally");

            functionManager.addFunction("mouse 1x  not_ctrl clicked button1",
                    "start-add-edge", "start-in-multi-command-mode", "yes");

            functionManager.addFunction("typed s", "start-add-edge");

            functionManager.addFunction("movedMouse", "update-add-edge");

            functionManager.addFunction("mouse 1x not_shift clicked button1",
                    "add-bend-add-edge");

            // functionManager.addFunction("typed d", "finish-add-edge",
            // "add-node-if-necessary", "yes", "mark-added-node", "no",
            // "mark-added-edge", "no");
            //
            // functionManager.addFunction("mouse 1x shift clicked button1",
            // "finish-add-edge", "add-node-if-necessary", "yes",
            // "mark-added-node", "no", "mark-added-edge", "no");
            functionManager.addFunction("typed d", "finish-add-edge",
                    "add-node-if-necessary", "yes", "mark-added-node",
                    "additionally", "mark-added-edge", "additionally");

            functionManager.addFunction("mouse 1x shift clicked button1",
                    "finish-add-edge", "add-node-if-necessary", "yes",
                    "mark-added-node", "additionally", "mark-added-edge",
                    "additionally");

            // functionManager.addFunction("mouse 1x clicked button1",
            // "finish-add-edge", "mark-added-node", "additionally",
            // "mark-added-edge", "additionally");

            functionManager.addFunction("typed b", "add-bend-add-edge");

            functionManager.addFunction("mouse 1x clicked button2",
                    "abort-add-edge");

            functionManager.addFunction("released ESCAPE", "abort-add-edge");

            functionManager.addFunction("popupTrigger", "show-popup-menu");
            functionManager.addFunction("typed m", "show-popup-menu");

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
     * Sets the source-node of an edge now to be added by the tool.
     * 
     * @param node
     *            source-node of the new edge
     */
    public void setAddEdgeSourceNode(Node node) {
        this.addEdgeSourceNode = node;
    }

    /**
     * Returns the action of a function.
     * 
     * @param functionName
     *            The given name of a function.
     * 
     * @return the action of a function.
     */
    public FunctionAction getFunctionAction(String functionName) {
        FunctionAction superAction = super.getFunctionAction(functionName);

        if (superAction != null)
            return superAction;
        else if (functionName.equals("add-node"))
            return new AddNodeAction(this);
        else if (functionName.equals("start-add-edge"))
            return new StartAddEdgeAction(this);
        else if (functionName.equals("update-add-edge"))
            return new UpdateAddEdgeAction(this);
        else if (functionName.equals("finish-add-edge"))
            return new FinishAddEdgeAction(this);
        else if (functionName.equals("add-bend-add-edge"))
            return new AddBendAddEdgeAction(this);
        else if (functionName.equals("abort-add-edge"))
            return new AbortAddEdgeAction(this);
        else if (functionName.equals("show-popup-menu"))
            return new ShowPopupMenuAction(this);
        else if (functionName.equals("linux-shortcuts"))
            return new ShortCutAction(this);
        else
            return null;
    }

    /**
     * Implementation of FunctionComponent
     * 
     * @param name
     *            The given name of the FunctionComponent
     * 
     * @return FunctionComponent
     */
    public FunctionComponent getSubComponent(String name) {
        return null;
    }

    /**
     * moved from the FinishAddEdgeAction [MH]
     * 
     * @param destNode
     *            destination node
     * 
     * @return The currently added node
     */
    public Edge addEdge(Node destNode) {
        Node sourceNode = this.getAddEdgeSourceNode();

        Preferences prefs = this.getPrefs();
        Graph graph = this.getGraph();

        CollectionAttribute col = new HashMapAttribute("");
        EdgeGraphicAttribute edgeGraphics = new EdgeGraphicAttribute();
        col.add(edgeGraphics, false);

        setDocking(edgeGraphics);

        // setting the graphic attributes to the default values stored
        // in the preferences
        edgeGraphics.setThickness(prefs.getDouble("thickness",
                DEFAULT_EDGE_THICKNESS));
        edgeGraphics.setFrameThickness(prefs.getDouble("frameThickness",
                DEFAULT_EDGE_FRAMETHICKNESS));

        // setting the framecolor
        Preferences fc = prefs.node("framecolor");
        int red = fc.getInt("red", DEFAULT_EDGE_FRAMECOLOR.getRed());
        int green = fc.getInt("green", DEFAULT_EDGE_FRAMECOLOR.getGreen());
        int blue = fc.getInt("blue", DEFAULT_EDGE_FRAMECOLOR.getBlue());
        int alpha = fc.getInt("alpha", DEFAULT_EDGE_FRAMECOLOR.getAlpha());
        edgeGraphics.getFramecolor().setColor(
                new Color(red, green, blue, alpha));

        // setting the fillcolor
        fc = prefs.node("fillcolor");
        red = fc.getInt("red", DEFAULT_EDGE_FILLCOLOR.getRed());
        green = fc.getInt("green", DEFAULT_EDGE_FILLCOLOR.getGreen());
        blue = fc.getInt("blue", DEFAULT_EDGE_FILLCOLOR.getBlue());
        alpha = fc.getInt("alpha", DEFAULT_EDGE_FILLCOLOR.getAlpha());
        edgeGraphics.getFillcolor()
                .setColor(new Color(red, green, blue, alpha));

        edgeGraphics.setArrowhead(prefs.get("arrowhead",
                "org.graffiti.plugins.views.defaults." + "StandardArrowShape"));

        // setting the lineMode
        Preferences da = prefs.node("dashArray");
        String[] daEntries;

        try {
            daEntries = da.keys();
        } catch (BackingStoreException bse) {
            daEntries = new String[0];
        }

        // no dashArray exists
        if (daEntries.length == 0) {
            edgeGraphics.getLineMode().setDashArray(null);
        } else {
            float[] newDA = new float[daEntries.length];

            for (int i = daEntries.length - 1; i >= 0; i--) {
                newDA[i] = da.getFloat(daEntries[i], 10);
            }

            edgeGraphics.getLineMode().setDashArray(newDA);
        }

        edgeGraphics.getLineMode().setDashPhase(
                prefs.getFloat("dashPhase", 0.0f));

        Edge dummyEdge = this.getDummyEdge();
        CollectionAttribute attributes = dummyEdge.getAttributes();
        EdgeGraphicAttribute edgeAttributes = (EdgeGraphicAttribute) attributes
                .getAttribute(EdgeGraphicAttribute.GRAPHICS);

        if (edgeAttributes.getNumberOfBends() > 0) {
            SortedCollectionAttribute bends = edgeAttributes.getBends();
            edgeGraphics.setShape(prefs.get("shape",
                    "org.graffiti.plugins.views.defaults."
                            + "PolyLineEdgeShape"));
            edgeGraphics.setBends(bends);
        } else {
            edgeGraphics.setShape(prefs.get("shape",
                    "org.graffiti.plugins.views.defaults."
                            + "StraightLineEdgeShape"));
        }

        Edge edge = graph.addEdge(sourceNode, destNode, true, col);

        AddEdgeEdit edit = new AddEdgeEdit(edge, this.getGraph(), this
                .getGEMap());

        UndoableEditSupport undoSupport = this.getUndoSupport();
        undoSupport.postEdit(edit);

        this.setAddEdgeSourceNode(null);
        this.removeDummyObjects();

        return edge;
    }

    /**
     * Is called after an event.
     * 
     * @param position
     *            The given position
     */
    public void afterEvent(Point position) {
        super.afterEvent(position);

        addEdgeChangedMode = false;

        // moveNodeChangedMode = false;
    }

    /**
     * Classes that overwrite this method should call super.deactive first.
     * 
     * @see org.graffiti.plugins.modes.deprecated.Tool#deactivate()
     */
    public void deactivate() {
        super.deactivate();

        if (isInMultiCommandMode() && (getAddEdgeSourceNode() != null)
                && !hasAddEdgeChangedMode()) {
            setAddEdgeSourceNode(null);
            removeDummyObjects();

            addEdgeChangedMode();
        }

        switchToDefaultMode();
    }

    /**
     * Moves the given Node to the given position.
     * 
     * @param node
     *            any Node
     * @param newPosition
     *            the new position of the given node
     */
    public void moveNode(Node node, Point newPosition) {
        CoordinateAttribute coord = (CoordinateAttribute) node
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.COORDINATE);
        coord.setCoordinate(newPosition);

        getViewComponent().repaint();
    }

    /**
     * Sets the docking at source and target and edge.
     * 
     * @param edgeGraphics
     *            The given EdgeGraphicAttribute
     */
    protected void setDocking(EdgeGraphicAttribute edgeGraphics) {
        DockingAttribute dock = edgeGraphics.getDocking();
        dock.setSource("");
        dock.setTarget("");
        edgeGraphics.setDocking(dock);
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
     * If currently, a new edge is being added, this returns its source-node.
     * Otherwise, null is returned.
     * 
     * @return source-node of the edge just being created
     */
    Node getAddEdgeSourceNode() {
        return addEdgeSourceNode;
    }

    /**
     * Returns a reference to the {@link #dummyEdge}.
     * 
     * @return a reference to the {@link #dummyEdge}
     */
    Edge getDummyEdge() {
        return dummyEdge;
    }

    /**
     * Returns a reference to the {@link #dummyNode}.
     * 
     * @return reference to the {@link #dummyNode}.
     */
    Node getDummyNode() {
        return dummyNode;
    }

    /**
     * Sets the position of the {@link #dummyNode}.
     * 
     * @param position
     *            new position of the {@link #dummyNode}.
     */
    void setDummyNodePosition(Point position) {
        moveNode(dummyNode, position);
    }

    /**
     * Sets the edge changed mode.
     */
    void addEdgeChangedMode() {
        addEdgeChangedMode = true;
    }

    /**
     * Creates the {@link #dummyEdge}. Returns a reference to it.
     * 
     * @param sourceNode
     *            source-node of the new dummy-edge
     * @param targetNode
     *            target-node of the new dummy-edge. Usually the
     *            {@link #dummyNode}.
     * @param col
     *            CollectionAttribute to be used for the new dummy-edge.
     * 
     * @return reference to the new dummy-edge
     */
    Edge createDummyEdge(Node sourceNode, Node targetNode,
            CollectionAttribute col) {
        dummyEdge = graph.createEdge(sourceNode, targetNode, true, col);

        return dummyEdge;
    }

    /**
     * Creates the {@link #dummyNode}. Returns a reference to it.
     * 
     * @param col
     *            CollectionAttribute to be used for the new dummy-node.
     * 
     * @return reference to the dummy-node
     */
    Node createDummyNode(CollectionAttribute col) {
        dummyNode = graph.createNode(col);

        return dummyNode;
    }

    /**
     * Returns the add edge changed mode
     * 
     * @return the add edge changed mode
     */
    boolean hasAddEdgeChangedMode() {
        return addEdgeChangedMode;
    }

    /**
     * Deletes the dummy-node (if it exists) and the dummy-edge (if it exists).
     * Does especially update the view.
     */
    void removeDummyObjects() {
        // Implemented here because it is used both by finish-add-edge and
        // abort-add-edge
        // the usual view, in its role as a view supporting dummy-nodes/edges
        List<DummySupportView> views = getDummySupportViews();
        for (DummySupportView view : views) {
            if (dummyNode != null) {
                view.removeViewForNode(dummyNode);
            }

            if (dummyEdge != null) {
                view.removeViewForEdge(dummyEdge);
            }
        }
        if (views.isEmpty()) {
            // should not happen, except the structure of the editor changes in
            // some way
            logger.finer("No view supporting dummy-objects!");
        }

        dummyNode = null;
        dummyEdge = null;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
