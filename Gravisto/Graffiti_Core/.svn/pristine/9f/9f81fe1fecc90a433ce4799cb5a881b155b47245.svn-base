package org.graffiti.plugins.algorithms.planarity;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.AlgorithmResult;
import org.graffiti.plugin.algorithm.CalculatingAlgorithm;
import org.graffiti.plugin.algorithm.DefaultAlgorithmResult;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.Parameter;

/**
 * The interface to the Gravisto plugin mechanism
 * 
 * @author Wolfgang Brunner
 */
public class PlanarityAlgorithm extends AbstractAlgorithm implements
        CalculatingAlgorithm {

    /**
     * Stores whether the graph is planar
     */
    private boolean isPlanar;

    /**
     * Determines whether the graph has already been tested
     */
    private boolean planarityTested;

    /**
     * The mapping between <code>org.graffiti.graph.Node</code> and
     * <code>RealNode</code> objects
     */
    private HashMap<Node, RealNode> map;

    /**
     * Stores the calculated embedding
     */
    private TestedGraph testedGraph;

    /**
     * The list of <code>ConnectedComponent</code> objects
     */
    private List<ConnectedComponent> connectedComponents;

    /**
     * If set to <code>true</code> the result of the planarity test is printed
     * as a text and the nodes and edges of the Kuratowski subgraph get colored
     */
    private boolean GUIMode = false;

    public PlanarityAlgorithm() {
        parameters = new Parameter[0];
        isPlanar = false;
        planarityTested = false;
        this.GUIMode = false;
    }

    /**
     * Creates a new <code>PlanarityAlgorithm</code>
     */
    public PlanarityAlgorithm(boolean GUIMode) {
        parameters = new Parameter[0];
        isPlanar = false;
        planarityTested = false;
        this.GUIMode = GUIMode;
    }

    /**
     * Returns the name of the algorithm
     * 
     * @return The name of the algorithm
     */
    public String getName() {
        return "Test planarity" + (GUIMode ? " (show details)" : "");
    }

    /**
     * Checks the preconditions for the planarity test. In this case the only
     * possibility of failure is an empty graph.
     */
    @Override
    public void check() throws PreconditionException {
        if (graph.getNumberOfNodes() <= 0)
            throw new PreconditionException(
                    "The graph is empty. Cannot run planarity test.");
    }

    /**
     * Adds a label to each <code>org.graffiti.graph.Node</code> containing its
     * depth first search index.
     * 
     * @param map
     *            The mapping between <code>org.graffiti.graph.Node</code> and
     *            <code>RealNode</code> objects
     */
    private void labelNodes(HashMap<Node, RealNode> map) {
        graph.getListenerManager().transactionStarted(this);
        for (Iterator<Node> nodes = graph.getNodesIterator(); nodes.hasNext();) {
            Node n = nodes.next();

            if (map.containsKey(n)) {
                RealNode pN = map.get(n);
                setLabel(n, pN.DFI + pN.DFSStartNumber + "");
            }
        }
        graph.getListenerManager().transactionFinished(this);
    }

    /**
     * Gives the result of the planarity test
     * 
     * @return <code>true</code> if the graph is planar
     */
    public boolean isPlanar() {
        if (!planarityTested) {
            testPlanarity();
        }
        return isPlanar;
    }

    /**
     * Executes the planarity test
     */
    public void testPlanarity() {
        if (GUIMode) {
            graph.getListenerManager().transactionStarted(this);
            unMarkNodes();
            unMarkEdges();
            graph.getListenerManager().transactionFinished(this);
        }
        testedGraph = null;
        planarityTested = true;
        map = new LinkedHashMap<Node, RealNode>();
        connectedComponents = new LinkedList<ConnectedComponent>();

        // split the graph in connected components
        int numberOfProcessedNodes = 0;
        for (Iterator<Node> i = graph.getNodesIterator(); i.hasNext();) {
            Node current = i.next();
            if (map.get(current) == null) {
                ConnectedComponent comp = new ConnectedComponent(current,
                        numberOfProcessedNodes, map, graph, GUIMode);
                comp.testPlanarity();
                connectedComponents.add(comp);
                numberOfProcessedNodes += comp.numberOfNodes;
            }
        }
        if (GUIMode) {
            labelNodes(map);
        }

        int components = connectedComponents.size();
        int planarComponents = 0;

        for (Iterator<ConnectedComponent> i = connectedComponents.iterator(); i
                .hasNext();) {
            ConnectedComponent comp = i.next();
            if (comp.isPlanar()) {
                planarComponents++;
            }
        }
        isPlanar = (components == planarComponents);
        if (GUIMode) {
            // System.out.println(getTestedGraph());
        }
    }

    /**
     * Executes the planarity test in GUI mode
     */
    public void execute() {
        // GUIMode = true;
        testPlanarity();
        // GUIMode = false;
    }

    /**
     * Gives the complete result of the planarity test
     * 
     * @return The <code>TestedGraph</code> object containing the embeddings and
     *         the Kuratowski subgraphs
     */
    public TestedGraph getTestedGraph() {
        if (!planarityTested) {
            testPlanarity();
        }
        if (testedGraph == null) {
            testedGraph = new TestedGraph(connectedComponents, map);
        }
        return testedGraph;
    }

    /**
     * Sets a Label to a node.
     * 
     * Taken from <code>org.graffiti.plugins.algorithms.bfs.BFS</code>
     * 
     * @param n
     *            The node to label
     * @param val
     *            The label
     */
    private void setLabel(Node n, String val) {
        LabelAttribute labelAttr;
        try {
            labelAttr = (LabelAttribute) n.getAttribute("label");
            labelAttr.setLabel(val);
        } catch (AttributeNotFoundException e) {
            labelAttr = new NodeLabelAttribute("label");
            labelAttr.setLabel(val);
            n.addAttribute(labelAttr, "");
        }
    }

    /**
     * Resets the algorithm
     */
    @Override
    public void reset() {
        super.reset();
        planarityTested = false;
        testedGraph = null;

    }

    /**
     * Sets the color of all nodes to blue.
     */
    public void unMarkNodes() {
        Color c = GraphicAttributeConstants.DEFAULT_NODE_FILLCOLOR;
        for (Iterator<Node> i = graph.getNodesIterator(); i.hasNext();) {
            Node node = i.next();
            setNodeColor(node, c);
        }
    }

    /**
     * Sets the color of all nodes to black.
     */
    public void unMarkEdges() {
        for (Iterator<Edge> i = graph.getEdgesIterator(); i.hasNext();) {
            Edge edge = i.next();
            setEdgeColor(edge.getSource(), edge.getTarget(), Color.BLACK);
        }
    }

    /**
     * Sets the color of the node.
     * 
     * @param node
     *            The node to color
     * @param c
     *            The new color
     */
    public static void setNodeColor(Node node, Color c) {
        ColorAttribute ca = (ColorAttribute) node
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.FILLCOLOR);
        if ((ca.getColor() == Color.RED) && (c == Color.RED)) {
            ca.setColor(Color.PINK);
        } else {
            ca.setColor(c);
        }
    }

    /**
     * Sets the color of a edge
     * 
     * @param node1
     *            The source node
     * @param node2
     *            The target node
     * @param c
     *            The new color
     */
    public static void setEdgeColor(Node node1, Node node2, Color c) {
        for (Iterator<Edge> i = node1.getEdgesIterator(); i.hasNext();) {
            Edge e = i.next();
            if ((e.getTarget() == node2) || (e.getSource() == node2)) {
                ColorAttribute ca = (ColorAttribute) e
                        .getAttribute(GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.FILLCOLOR);

                ca.setColor(c);
                ca = (ColorAttribute) e
                        .getAttribute(GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.FRAMECOLOR);

                ca.setColor(c);
                return;
            }
        }
    }

    /**
     * @see org.graffiti.plugin.algorithm.CalculatingAlgorithm#getResult()
     */
    public AlgorithmResult getResult() {
        AlgorithmResult result = new DefaultAlgorithmResult();
        javax.swing.JTextArea area = new javax.swing.JTextArea(getTestedGraph()
                .toString());
        area.setEditable(false);
        area.setRows(10);
        Object[] toDisplay = null;
        if (GUIMode) {
            toDisplay = new Object[] {
                    "The graph is " + (isPlanar() ? "" : "not ")
                            + "planar.\n\nDetails:",
                    new javax.swing.JScrollPane(area) };
        } else {
            toDisplay = new Object[] { "The graph is "
                    + (isPlanar() ? "" : "not ") + "planar." };
        }
        result.setComponentForJDialog(toDisplay);
        result.addToResult("planar", isPlanar());
        return result;
    }
}
