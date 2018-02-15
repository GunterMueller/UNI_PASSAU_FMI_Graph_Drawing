// =============================================================================
//
//   AddNodeAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AddNodeAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced.create;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.undo.UndoableEditSupport;

import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugins.modes.advanced.AbstractFunctionAction;
import org.graffiti.plugins.modes.advanced.FunctionActionEvent;
import org.graffiti.undo.AddNodeEdit;

/**
 * Action for the function add-node.
 * 
 * @deprecated
 */
@Deprecated
class AddNodeAction extends AbstractFunctionAction implements
        GraphicAttributeConstants {
    /**
     * 
     */
    private static final long serialVersionUID = -4859299748064243459L;

    private static final Logger logger = Logger.getLogger(AddNodeAction.class
            .getName());

    /** CreateTool-instance assigned to this instance */
    private CreateTool createTool;

    /**
     * Constructs a new AddNodeAction.
     * 
     * @param createTool
     *            the CreateTool-instance controlling the function
     */
    public AddNodeAction(CreateTool createTool) {
        this.createTool = createTool;
    }

    /**
     * Returns a map with the valid parameters
     * 
     * @return a map with the valid parameters
     */
    @Override
    public Map<String, Set<Object>> getValidParameters() {
        return construct2To23ParamMap("over-existing-node", "yes", "no",
                "mark-node", "only", "additionally", "no");

    }

    /**
     * The action of this class
     * 
     * @param e
     *            The given FunctionActionEvent
     */
    @Override
    public void actionPerformed(FunctionActionEvent e) {
        // parameter-values
        Object overExistingNode = getValue("over-existing-node");

        if (overExistingNode == null) {
            // default-value
            overExistingNode = "no";
        }

        Object markNode = getValue("mark-node");

        if (markNode == null) {
            // default-value
            markNode = "only";
        }

        if (createTool.isInDefaultMode() && !createTool.hasAddEdgeChangedMode()) {
            Point position = e.getPosition();

            if (position != null) {
                if ((createTool.getTopNode(position) == null)
                        || overExistingNode.equals("yes")) {
                    Node addedNode = addNode(createTool, position);
                    // createTool.unmarkAll(); // [MH]

                    // update selection
                    if (markNode.equals("only")) {
                        createTool.unmarkAll();
                        createTool.mark(addedNode);
                    } else if (markNode.equals("additionally")) {
                        createTool.mark(addedNode);
                    } else if (markNode.equals("no")) {
                        createTool.unmarkAll();
                        // nothing to be done here
                    } else {
                        // should not happen
                    }
                }
            } else {
                logger.finer("AddNodeAction says: Warning: Can't "
                        + "operate without position given in event!");
            }
        }
    }

    /**
     * Sets dash phase
     * 
     * @param nodeGraphicAttribute
     *            The given NodeGraphicAttribute
     * @param prefs
     *            The given Preferences
     */
    static void setDashPhase(NodeGraphicAttribute nodeGraphicAttribute,
            Preferences prefs) {
        nodeGraphicAttribute.getLineMode().setDashPhase(
                prefs.getFloat("dashPhase", 0.0f));
    }

    /**
     * Adds a new Node to the graph. Static because it is used by multiple
     * functions.
     * 
     * @param createTool
     *            tool-instance to be used
     * @param position
     *            position of the new node
     * 
     * @return the newly added Node
     */
    static Node addNode(CreateTool createTool, Point position) {
        Preferences prefs = createTool.getPrefs();
        Graph graph = createTool.getGraph();
        Map<GraphElement, GraphElement> geMap = createTool.getGEMap();
        UndoableEditSupport undoSupport = createTool.getUndoSupport();

        NodeGraphicAttribute nodeGraphicAttribute = new NodeGraphicAttribute();

        // set position and size of the new node
        Dimension nodeDimension = getNodeDimension(prefs);
        CoordinateAttribute coordAttribute = nodeGraphicAttribute
                .getCoordinate();
        coordAttribute.setCoordinate(position);
        nodeGraphicAttribute.getDimension().setDimension(nodeDimension);

        setFrameThickness(nodeGraphicAttribute, prefs);
        setFrameColor(nodeGraphicAttribute, prefs);
        setFillColor(nodeGraphicAttribute, prefs);
        setShape(nodeGraphicAttribute, prefs);
        setDashArray(nodeGraphicAttribute, prefs);
        setDashPhase(nodeGraphicAttribute, prefs);

        Node newNode = addNodeToGraph(createTool, nodeGraphicAttribute, graph);
        generateAddNodeUndoInfo(newNode, graph, geMap, undoSupport);

        return newNode;
    }

    /**
     * Adds a node to the graph
     * 
     * @param createTool
     *            the given CreateTool
     * @param nodeGraphicAttribute
     *            The given NodeGraphicAttribute
     * @param graph
     *            The given Graph
     * 
     * @return The added Node
     */
    static Node addNodeToGraph(CreateTool createTool,
            NodeGraphicAttribute nodeGraphicAttribute, Graph graph) {
        CollectionAttribute collectionAttribute = new HashMapAttribute("");
        collectionAttribute.add(nodeGraphicAttribute, false);

        Node node = graph.addNode(collectionAttribute);
        createTool.nodeAdded(node);
        return node;
    }

    /**
     * Generates the undo info for the added node
     * 
     * @param node
     *            The given node
     * @param graph
     *            Given Graph
     * @param geMap
     *            Given GEMap
     * @param undoSupport
     *            Given UndoSupport
     */
    static void generateAddNodeUndoInfo(Node node, Graph graph,
            Map<GraphElement, GraphElement> geMap,
            UndoableEditSupport undoSupport) {
        AddNodeEdit edit = new AddNodeEdit(node, graph, geMap);
        undoSupport.postEdit(edit);
    }

    /**
     * Sets the dash array
     * 
     * @param nodeGraphicAttribute
     *            Given NodeGraphicAttribute
     * @param prefs
     *            Given Preferences
     */
    private static void setDashArray(NodeGraphicAttribute nodeGraphicAttribute,
            Preferences prefs) {
        Preferences dashArrayPrefs = prefs.node("dashArray");

        String[] keys;

        try {
            keys = dashArrayPrefs.keys();
        } catch (BackingStoreException bse) {
            keys = new String[0];
        }

        // no dashArray exists
        if (keys.length == 0) {
            nodeGraphicAttribute.getLineMode().setDashArray(null);
        } else {
            float[] values = new float[keys.length];

            for (int i = keys.length - 1; i >= 0; i--) {
                values[i] = dashArrayPrefs.getFloat(keys[i], 10);
            }

            nodeGraphicAttribute.getLineMode().setDashArray(values);
        }
    }

    /**
     * Sets the fill color of an node
     * 
     * @param nodeGraphicAttribute
     *            Given NodeGraphicAttribute
     * @param prefs
     *            Given Preferences
     */
    private static void setFillColor(NodeGraphicAttribute nodeGraphicAttribute,
            Preferences prefs) {
        Preferences fillColorPrefs = prefs.node("fillcolor");
        int red = fillColorPrefs.getInt("red", DEFAULT_NODE_FILLCOLOR.getRed());
        int green = fillColorPrefs.getInt("green", DEFAULT_NODE_FILLCOLOR
                .getGreen());
        int blue = fillColorPrefs.getInt("blue", DEFAULT_NODE_FILLCOLOR
                .getBlue());
        int alpha = fillColorPrefs.getInt("alpha", DEFAULT_NODE_FILLCOLOR
                .getAlpha());
        nodeGraphicAttribute.getFillcolor().setColor(
                new Color(red, green, blue, alpha));
    }

    /**
     * Sets the frame color
     * 
     * @param nodeGraphicAttribute
     *            Given NodeGraphicAttribute
     * @param prefs
     *            Given Preferences
     */
    private static void setFrameColor(
            NodeGraphicAttribute nodeGraphicAttribute, Preferences prefs) {
        Preferences frameColorPrefs = prefs.node("framecolor");
        int red = frameColorPrefs.getInt("red", DEFAULT_NODE_FRAMECOLOR
                .getRed());
        int green = frameColorPrefs.getInt("green", DEFAULT_NODE_FRAMECOLOR
                .getGreen());
        int blue = frameColorPrefs.getInt("blue", DEFAULT_NODE_FRAMECOLOR
                .getBlue());
        int alpha = frameColorPrefs.getInt("alpha", DEFAULT_NODE_FRAMECOLOR
                .getAlpha());
        nodeGraphicAttribute.getFramecolor().setColor(
                new Color(red, green, blue, alpha));
    }

    /**
     * Sets frame thickness
     * 
     * @param nodeGraphicAttribute
     *            Given NodeGraphicAttribute
     * @param prefs
     *            Given Preferences
     */
    private static void setFrameThickness(
            NodeGraphicAttribute nodeGraphicAttribute, Preferences prefs) {
        double frameThickness = prefs.getDouble("frameThickness",
                DEFAULT_NODE_FRAMETHICKNESS);
        nodeGraphicAttribute.setFrameThickness(frameThickness);
    }

    /**
     * Returns the dimension of nodes
     * 
     * @param prefs
     *            The given Preferences
     * 
     * @return the dimension of nodes
     */
    private static Dimension getNodeDimension(Preferences prefs) {
        Preferences dimensionPrefs = prefs.node("dimension");

        double height = dimensionPrefs.getDouble("height", DEFAULT_NODE_SIZE
                .getHeight());
        double width = dimensionPrefs.getDouble("width", DEFAULT_NODE_SIZE
                .getWidth());

        return new Dimension((int) Math.round(height), (int) Math.round(width));
    }

    /**
     * Sets the shape�
     * 
     * @param nodeGraphicAttribute
     *            Given NodeGraphicAttribute
     * @param prefs
     *            Given Preferences
     */
    private static void setShape(NodeGraphicAttribute nodeGraphicAttribute,
            Preferences prefs) {
        nodeGraphicAttribute.setShape(prefs.get("shape", DEFAULT_NODE_SHAPE));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
