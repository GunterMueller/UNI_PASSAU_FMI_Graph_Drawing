// ==============================================================================
//
//   FlowNetworkSupportAlgorithms.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: FlowNetworkSupportAlgorithms.java 5766 2010-05-07 18:39:06Z gleissner $

/*
 * Created on 16.06.2004
 */

package org.graffiti.plugins.algorithms.networkFlow;

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.attributes.Attributable;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.EdgeLabelAttribute;
import org.graffiti.graphics.EdgeLabelPositionAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.plugin.algorithm.PreconditionException;

/**
 * This singleton class is a collection of algorithms that are frequently needed
 * in flow computations, especially by the <code>
 * PreflowPushAlgorithm </code>.
 * 
 * @author Markus K�ser
 * @version $Revision 1.1 $
 */
public class FlowNetworkSupportAlgorithms {

    /** Base path for the storage of temporary data at nodes and edges */
    static final String BASE = "networkFlow.";

    /** The maximum possible capacity accepted by the max-flow-algorithms */
    public static final double MAXIMUM_CAPACITY = 9999999.9999999;

    /** The path, the labels are stored at nodes and edges */
    public static final String LABEL_PATH = "label.label";

    /** Path of edge capacity */
    public static final String CAPACITY = "capacity";

    /** Path of edge network-flow */
    public static final String FLOW = "flow";

    /** Path, the node numbers are stored at */
    public static final String NODE_NUMBER = "node_number";

    /** Path, the flow source nodes are stored at */
    public static final String FLOW_SOURCE = "flow_source";

    /** Path of the fillcolor */
    private static final String PATH_FILLCOLOR = "graphics.fillcolor";

    /** Path of the framecolor */
    private static final String PATH_FRAMECOLOR = "graphics.framecolor";

    /** Path of the dfs visited flag */
    private static final String DFS_VISITED_NODE_PATH = BASE + "DFS_visited";

    /** Path of the dfs finished flag */
    private static final String DFS_FINISHED_NODE_PATH = BASE + "DFS_finished";

    /** Path of the dfs node index */
    private static final String DFS_NODE_INDEX = BASE + "DFS_node_index";

    /** Path of the dfs predesessor */
    private static final String DFS_PREDESESSOR_PATH = BASE + "DFS_predesessor";

    /** Path of the dfs back edges */
    private static final String DFS_BACK_EDGE_PATH = BASE + "DFS_backedge";

    /** maximum color intensity for coloring flow edges */
    private static final int MAXIMUM_RGB_COLOR_INTENSITY = 255;

    /** minimum color intensity for coloring flow edges */
    private static final int MINIMUM_EDGE_G_COLOR_INTENSITY = 64;

    /**
     * the factor to be multiplied with flow / cap to get the resulting color
     * intesity
     */
    private static final int RELATIVE_EDGE_COLORING_FACTOR = MAXIMUM_RGB_COLOR_INTENSITY
            - MINIMUM_EDGE_G_COLOR_INTENSITY;

    /**
     * The maximum precision in places in front of the decimal point and after
     * the decimal point
     */
    private static final int CAP_AND_FLOW_PRECISION = 7;

    /** a factor of 10^7 for rounding */
    private static final double factor = Math.round(Math.pow(10,
            CAP_AND_FLOW_PRECISION));

    /** Error message */
    private static final String GRAPH_NOT_DIRECTED_AND_NOT_UNDIRECTED_ERROR = "Graph must be directed or undirected and may not contain both sorts"
            + "of edges";

    /** Error message */
    private static final String CAPACITIY_PRECISION_OUT_OF_RANGE = ""
            + "Capacity may have no more than " + CAP_AND_FLOW_PRECISION
            + " decimal"
            + "places in front of the decimal point and no more than "
            + CAP_AND_FLOW_PRECISION
            + " decimal places after the decimal point.";

    /** Error message */
    private static final String ATTRIBUTE_NOT_FOUND_ERROR = "No Attribute"
            + " could be found on path: ";

    /** Error message */
    private static final String NOT_LABEL_ATTRIBUTE_ERROR = "Attribute is not"
            + " a LabelAttribute on path: ";

    /** Error message */
    private static final String ATTRIBUTE_INT_PARSE_ERROR = "Attribute could"
            + " not be parsed as int at path: ";

    /** Error message */
    private static final String ATTRIBUTE_DOUBLE_PARSE_ERROR = "Attribute "
            + "could not be parsed as double at path: ";

    /** Error message */
    private static final String CAPACITIES_NOT_OK_ERROR = "Edge capacity "
            + "must be a positive double.";

    /** Error message */
    private static final String FLOW_NOT_OK_ERROR = "Flow "
            + "must be a non-negative double.";

    /** Error message */
    private static final String EQUAL_SOURCE_AND_SINK_LABEL_ERROR = "The given"
            + " source and sink labels may not be equal.";

    /** Error message */
    private static final String NO_SOURCE_ERROR = "The graph must have a node"
            + " with source label.";

    /** Error message */
    private static final String TO_MUCH_SOURCES_ERROR = "The graph may not have"
            + " more than one source.";

    /** Error message */
    private static final String NO_SINK_ERROR = "The graph must have a node"
            + " with sink label.";

    /** Error message */
    private static final String TO_MUCH_SINKS_ERROR = "The graph may not have"
            + " more than one sink.";

    /** Error message */
    private static final String FLOW_SOURCE_NUMBER_INCORRECT_ERROR = "Flow"
            + " source number was not set to this node correctly.";

    /** Error message */
    private static final String DFS_DOUBLE_VISIT_ERROR = "Node was visited"
            + " twice in Dfs";

    /** Error message */
    private static final String DFS_DOUBLE_FINISH_ERROR = "Node was finished"
            + " twice in DFS";

    /** Error message */
    private static final String DFS_DOUBLE_PREDESESSOR_ERROR = "For a node more"
            + " than one predesessor was set in DFS";

    /** Error message */
    private static final String GRAPH_EMPTY_ERROR = "Graph may not be empty";

    /** Error message */
    private static final String GRAPH_NOT_AT_LEAST_TWO_NODE_ERROR = "Graph "
            + "must have at least two nodes";

    /** the singleton FlowNetworkSupportAlgorithms object */
    private static FlowNetworkSupportAlgorithms nsa = null;

    /**
     * Creates a new FlowNetworkSupportAlgorithms object.
     */
    private FlowNetworkSupportAlgorithms() {
    }

    /**
     * Returns the singleton <code> FlowNetworkSupportAlgorithms </code> object
     * 
     * @return the singelton
     */
    public static FlowNetworkSupportAlgorithms getFlowNetworkSupportAlgorithms() {
        if (nsa == null) {
            nsa = new FlowNetworkSupportAlgorithms();
        }

        return nsa;
    }

    /**
     * Sets a new double valued capacity to a given edge. If another capacity
     * was stored before the operation, the old value will be overwritten.
     * 
     * @param edge
     *            the edge
     * @param cap
     *            the new capacity
     * 
     * @return the old capacity value
     * 
     * @throws NetworkFlowException
     *             if a capacity less than 0.0 was set
     */
    public double setCapacity(Edge edge, double cap) {
        if (cap >= 0)
            return setDoubleLabelAttribute(edge, CAPACITY, cap);
        else
            throw new NetworkFlowException(CAPACITIES_NOT_OK_ERROR);
    }

    /**
     * Returns the capacity stored at a given edge, or 0.0 if no capacity was
     * set.
     * 
     * @param edge
     *            the edge
     * 
     * @return the capacity at this edge
     */
    public double getCapacity(Edge edge) {
        return getDoubleLabelAttribute(edge, CAPACITY, false); // perhaps true
    }

    /**
     * Sets the color of the given graph element to the given color.
     * 
     * @param e
     *            graph element.
     * @param p
     *            path to the color attribute.
     * @param r
     *            red color value.
     * @param g
     *            green color value.
     * @param b
     *            blur color value.
     * @param t
     *            transparency value.
     */
    public void setColor(GraphElement e, String p, int r, int g, int b, int t) {
        e.setInteger(p + ".transparency", t);
        e.setInteger(p + ".red", r);
        e.setInteger(p + ".green", g);
        e.setInteger(p + ".blue", b);
    }

    /**
     * Sets an double as <code> LabelAttribute </code> to an Attributable at a
     * given path.
     * 
     * @param attributable
     *            the object, the double will be stored at
     * @param path
     *            the path of the <code> LabelAttribute </code>
     * @param value
     *            the double to be stored
     * 
     * @return the old double value stored at this path
     */
    public double setDoubleLabelAttribute(Attributable attributable,
            String path, double value) {
        double oldValue = getDoubleLabelAttribute(attributable, path, false);
        removeLabelAttribute(attributable, path);

        LabelAttribute doubleLabelAttr = new LabelAttribute(path, value + "");
        attributable.addAttribute(doubleLabelAttr, "");

        return oldValue;
    }

    /**
     * Tries to extract a double value from a <code> LabelAttribute </code> from
     * an <code> Attributable </code> at the given path.
     * 
     * @param attributable
     *            the object from which the double will be extracted
     * @param path
     *            the path of the <code> LabelAttribute </code>
     * @param throwException
     *            if true, the method will rethrow caught
     *            <code>AttributeNotFoundExceptions </code>, if false, it will
     *            ingnore them
     * 
     * @return the double if no Error occurs, 0.0 otherwise
     * 
     * @throws NetworkFlowException
     *             if the attribute could not be found or parsed
     */
    public double getDoubleLabelAttribute(Attributable attributable,
            String path, boolean throwException) {
        String resultString;
        double result = 0.0;

        try {
            LabelAttribute attr = (LabelAttribute) attributable
                    .getAttribute(path);
            resultString = attr.getLabel();

            if (!resultString.equals("")) {
                result = Double.parseDouble(resultString);
            }
        } catch (AttributeNotFoundException anfe) {
            if (throwException)
                throw new NetworkFlowException(
                        ATTRIBUTE_NOT_FOUND_ERROR + path, anfe);
        } catch (NumberFormatException nfe) {
            throw new NetworkFlowException(ATTRIBUTE_DOUBLE_PARSE_ERROR + path,
                    nfe);
        } catch (ClassCastException cce) {
            throw new NetworkFlowException(NOT_LABEL_ATTRIBUTE_ERROR + path,
                    cce);
        }

        return result;
    }

    /**
     * Sets the fillcolor of a <code>GraphElement</code>.
     * 
     * @param e
     *            the graph element
     * @param r
     *            red RGB value
     * @param g
     *            green RGB value
     * @param b
     *            blue RGB value
     */
    public void setFillColor(GraphElement e, int r, int g, int b) {
        setColor(e, PATH_FILLCOLOR, r, g, b, 255);
    }

    /**
     * Sets a new double flow label to a given edge. If another flow was stored
     * before the operation, the old value will be overwritten.
     * 
     * @param edge
     *            the edge
     * @param flow
     *            the new flow
     * 
     * @return the old flow value
     * 
     * @throws NetworkFlowException
     *             if the flow is less than 0.0
     */
    public double setFlow(Edge edge, double flow) {
        if (flow >= 0)
            return setDoubleLabelAttribute(edge, FLOW, flow);
        else
            throw new NetworkFlowException(FLOW_NOT_OK_ERROR);
    }

    /**
     * Returns the flow stored at a given edge, or 0.0 if no flow was set.
     * 
     * @param edge
     *            the edge
     * 
     * @return the flow at this edge
     */
    public double getFlow(Edge edge) {
        return getDoubleLabelAttribute(edge, FLOW, false);
    }

    /**
     * Sets the <code> flowSourceNumber </code> on a given edge. This number
     * indicates the direction of the flow on an undirected edge. It adresses to
     * the <code> nodeNumber </code> stored on the nodes of the graph.
     * 
     * @param edgeWithFlow
     *            the edge
     * @param flowSourceNumber
     *            the <code> flowSourceNumber </code>
     * 
     * @throws NetworkFlowException
     *             if the flow source number is set to a node which cannot be
     *             the the flow source or if <code> nodeNumbers
     *         </code> have not been set
     *             correctly.
     */
    public void setFlowSourceNumber(Edge edgeWithFlow, int flowSourceNumber) {
        // even with a flow value of 0 this may be set.
        int sourceNumber = getNodeNumber(edgeWithFlow.getSource());
        int targetNumber = getNodeNumber(edgeWithFlow.getTarget());

        // The flow source on a directed edge must be the source of the edge
        if ((edgeWithFlow.isDirected() && (sourceNumber == flowSourceNumber))
                || (!edgeWithFlow.isDirected() && ((sourceNumber == flowSourceNumber) || (targetNumber == flowSourceNumber)))) {
            // then set the flow source number
            setIntLabelAttribute(edgeWithFlow, FLOW_SOURCE, flowSourceNumber);
        } else
            throw new NetworkFlowException(FLOW_SOURCE_NUMBER_INCORRECT_ERROR);
    }

    /**
     * Returns the <code> flowSourceNumber </code> stored on a given edge with
     * positive flow.
     * 
     * @param edgeWithFlow
     * 
     * @return the flow source number
     */
    public int getFlowSourceNumber(Edge edgeWithFlow) {
        return getIntLabelAttribute(edgeWithFlow, FLOW_SOURCE, true);
    }

    /**
     * Checks if a given node is the flow source of the flow on an edge.
     * 
     * @param node
     *            the node to be checked
     * @param edge
     *            the edge
     * 
     * @return true if the node is the flow source, false otherwise
     */
    public boolean isFlowSourceOnEdge(Node node, Edge edge) {
        int flowSourceNumber = getFlowSourceNumber(edge);
        int nodeNumber = getNodeNumber(node);

        return (nodeNumber == flowSourceNumber);
    }

    /**
     * Sets the frame color of a <code> GraphElement </code> to the given rgb
     * values
     * 
     * @param e
     *            the graph element
     * @param r
     *            red
     * @param g
     *            green
     * @param b
     *            blue
     */
    public void setFrameColor(GraphElement e, int r, int g, int b) {
        setColor(e, PATH_FRAMECOLOR, r, g, b, 255);
    }

    /**
     * Sets an int as <code> LabelAttribute </code> to an Attributable at a
     * given path.
     * 
     * @param attributable
     *            the object, the int will be stored at
     * @param path
     *            the path of the <code> LabelAttribute </code>
     * @param value
     *            the int to be stored
     * 
     * @return the old int value stored at this path
     */
    public int setIntLabelAttribute(Attributable attributable, String path,
            int value) {
        int oldValue = getIntLabelAttribute(attributable, path, false);
        removeLabelAttribute(attributable, path);

        LabelAttribute intLabelAttr = new LabelAttribute(path, value + "");
        attributable.addAttribute(intLabelAttr, "");

        return oldValue;
    }

    /**
     * Tries to extract an int value from a <code> LabelAttribute </code> from
     * an <code> Attributable </code> at the given path.
     * 
     * @param attributable
     *            the object from which the int will be extracted
     * @param path
     *            the path of the <code> LabelAttribute </code>
     * @param throwException
     *            if true, the method will rethrow caught
     *            <code>AttributeNotFoundExceptions </code>, if false, it will
     *            ingnore them
     * 
     * @return the integer if no Error occurs, 0 otherwise
     * 
     * @throws NetworkFlowException
     *             if there is no attribute or the attribute could not be parsed
     */
    public int getIntLabelAttribute(Attributable attributable, String path,
            boolean throwException) {
        String resultString;
        int result = 0;

        try {
            LabelAttribute attr = (LabelAttribute) attributable
                    .getAttribute(path);
            resultString = attr.getLabel();

            if (!resultString.equals("")) {
                result = Integer.parseInt(resultString);
            }
        } catch (AttributeNotFoundException anfe) {
            if (throwException)
                throw new NetworkFlowException(
                        ATTRIBUTE_NOT_FOUND_ERROR + path, anfe);
        } catch (NumberFormatException nfe) {
            throw new NetworkFlowException(ATTRIBUTE_INT_PARSE_ERROR + path,
                    nfe);
        } catch (ClassCastException cce) {
            throw new NetworkFlowException(NOT_LABEL_ATTRIBUTE_ERROR + path,
                    cce);
        }

        return result;
    }

    /**
     * Returns the label of a given <code> Attributable </code>
     * 
     * @param a
     *            the <code>Attributable</code>
     * 
     * @return the label
     */
    public String getLabel(Attributable a) {
        String label = "";

        try {
            label = a.getString(LABEL_PATH);
        } catch (AttributeNotFoundException anfe) {
        }

        return label;
    }

    /**
     * Sets a <code> nodeNumber </code> to a given node. The <code> flow
     * source number </code> stored on undirected edges adresses these numbers.
     * 
     * @param node
     *            the node, the number belongs to
     * @param number
     *            the number
     */
    public void setNodeNumber(Node node, int number) {
        setIntLabelAttribute(node, NODE_NUMBER, number);
    }

    /**
     * Returns the <code> nodeNumber </code> stored on a given node. The <code>
     * flowSourceNumber </code>
     * stored on undirected edges adresses these numbers.
     * 
     * @param node
     *            the node, the number belongs to
     * 
     * @return the node number
     */
    public int getNodeNumber(Node node) {
        return getIntLabelAttribute(node, NODE_NUMBER, true);
    }

    /**
     * Returns the array of nodes of a graph which are markt with a given label.
     * If an empty Sting is given as label, an empty array is returned by this
     * method.
     * 
     * @param graph
     *            the graph
     * @param searchedLabel
     *            the label to be searched for
     * 
     * @return the array of nodes with the label
     */
    public Node[] getNodesWithLabel(Graph graph, String searchedLabel) {
        return getNodesWithLabel(graph.getNodes(), searchedLabel);
    }

    /**
     * Returns the array of nodes of the given <code> Collection </code> which
     * are markt with a given label. If an empty Sting is given as label, an
     * empty array is returned by this method.
     * 
     * @param nodes
     *            the nodes
     * @param searchedLabel
     *            the label to be searched for
     * 
     * @return the array of nodes with the label
     */
    public Node[] getNodesWithLabel(Collection<Node> nodes, String searchedLabel) {
        LinkedList<Node> found = new LinkedList<Node>();

        if (!searchedLabel.equals("")) {
            for (Iterator<Node> nodeIt = nodes.iterator(); nodeIt.hasNext();) {
                Node tempNode = nodeIt.next();
                String label = "";

                try {
                    label = nsa.getLabel(tempNode);
                } catch (AttributeNotFoundException anfe) {
                }

                // check on searched Label
                if (label.equals(searchedLabel)) {
                    found.add(tempNode);
                }
            }
        }

        Node[] foundArray = found.toArray(new Node[found.size()]);

        return foundArray;
    }

    /**
     * Given an Edge e and one of the two Nodes incident to e, this method
     * returns the other node.
     * 
     * @param oneNode
     *            one node incident to the edge
     * @param edge
     *            the edge
     * 
     * @return the other node incident to the edge
     */
    public Node getOtherEdgeNode(Node oneNode, Edge edge) {
        Node otherNode;

        if (oneNode == edge.getSource()) {
            otherNode = edge.getTarget();
        } else {
            otherNode = edge.getSource();
        }

        return otherNode;
    }

    /**
     * Returns the residual capacity of an edge, which is given by (capacity -
     * flow).
     * 
     * @param edge
     *            the edge
     * 
     * @return the residual capacity
     */
    public double getResidualCapacity(Edge edge) {
        double cap = getCapacity(edge);
        double flow = getFlow(edge);

        return (cap - flow);
    }

    /**
     * Returns the residual capacity of an edge in the reverse direction. On
     * directed edges, this is simply the flow on this edge, on undirected edges
     * it is (capacity + flow).
     * 
     * @param edge
     *            the edge
     * 
     * @return the reverse residual capacity
     */
    public double getReverseResidualCapacity(Edge edge) {
        double cap = getCapacity(edge);
        double flow = getFlow(edge);

        if (edge.isDirected())
            // when pushing the wrong direction of a directed edge, only the
            // flow can be pushed back
            return flow;
        else
            // when pushing against a flow in an andirected edge, it can be
            // pushed back and the edge can be filled with flow from this side
            return (cap + flow);
    }

    /**
     * Adds a given value to the capacity of an edge. If no capacity was set,
     * than a value of 0.0 will be asumed. Negative values in the second
     * argument will be substracted from the old capacity. The resulting
     * Capacity must be greater than 0.0
     * 
     * @param edge
     *            the edge
     * @param addToCap
     *            the amount, the capacity rises
     * 
     * @return the old capacity value
     */
    public double addToCapacity(Edge edge, double addToCap) {
        double oldCap = getCapacity(edge);
        double newCap = oldCap + addToCap;

        return setCapacity(edge, newCap);
    }

    /**
     * Adds a given value to the flow of an edge. If no flow was set, than an
     * old value of 0.0 will be asumed. Negative values in the second argument
     * will be substracted from the old flow. The resulting Capacity must be
     * greater than 0.0
     * 
     * @param edge
     *            the edge
     * @param addToFlow
     *            the amount, the flow rises
     * 
     * @return the old flow value
     */
    public double addToFlow(Edge edge, double addToFlow) {
        double oldFlow = getFlow(edge);
        double newFlow = oldFlow + addToFlow;

        return setFlow(edge, newFlow);
    }

    /**
     * Checks if the capacity values stored on the network edges meet the
     * precision constraints.
     * 
     * @param network
     *            the network
     * @param pe
     *            the <code>PreconditionException</code>, where all errors
     *            during the check will be stored
     * 
     * @return true if everything is OK, false otherwise
     */
    public boolean checkCapacityPrecision(Graph network,
            PreconditionException pe) {
        boolean noError = true;
        Edge tempEdge;

        for (Iterator<Edge> edgeIt = network.getEdgesIterator(); edgeIt
                .hasNext();) {
            tempEdge = edgeIt.next();

            if (!checkCapacityPrecisionOfEdge(tempEdge)) {
                noError = false;
                // System.out.println("Cap : " + nsa.getCapacity(tempEdge));
                pe.add(CAPACITIY_PRECISION_OUT_OF_RANGE, tempEdge);
            }
        }

        return noError;
    }

    /**
     * Checks if a given graph contains at least one node.
     * 
     * @param graph
     *            the graph to be tested
     * @param pe
     *            the <code>PreconditionException</code>, where errors during
     *            the check will be stored
     * @return true if everything is OK, false otherwise
     */
    public boolean checkEmptyGraph(Graph graph, PreconditionException pe) {
        boolean noError = true;
        if (graph.getNumberOfNodes() == 0) {
            noError = false;
            pe.add(GRAPH_EMPTY_ERROR);
        }
        return noError;
    }

    /**
     * Checks if a given graph contains at least two nodes.
     * 
     * @param graph
     *            the graph to be tested
     * @param pe
     *            the <code>PreconditionException</code>, where errors during
     *            the check will be stored
     * @return true if everything is OK, false otherwise
     */
    public boolean checkAtLeastTwoNodes(Graph graph, PreconditionException pe) {
        boolean noError = true;
        if (graph.getNumberOfNodes() < 2) {
            noError = false;
            pe.add(GRAPH_NOT_AT_LEAST_TWO_NODE_ERROR);
        }
        return noError;
    }

    /**
     * Checks if a graph is directed or undirected. If not, the graph contains
     * both directed and undirected edges.
     * 
     * @param graph
     *            the graph
     * @param pe
     *            the PreconditionException, where all errors during the check
     *            will be stored
     * 
     * @return true if everything is OK, false otherwise
     */
    public boolean checkDirectedOrUndirected(Graph graph,
            PreconditionException pe) {
        boolean noError = true;

        if (!graph.isDirected() && !graph.isUndirected()) {
            noError = false;
            pe.add(GRAPH_NOT_DIRECTED_AND_NOT_UNDIRECTED_ERROR);
        }

        return noError;
    }

    /**
     * Checks, if a given graph has positive capacity values on each edge.
     * 
     * @param graph
     *            the graph
     * @param pe
     *            the <code>PreconditionException</code>, where all errors
     *            during the check will be stored
     * 
     * @return true if everything is OK, false otherwise
     */
    public boolean checkPositiveCapacities(Graph graph, PreconditionException pe) {
        boolean noError = true;

        if (pe == null) {
            pe = new PreconditionException();
        }

        for (Iterator<Edge> edgeIt = graph.getEdgesIterator(); edgeIt.hasNext();) {
            Edge tempEdge = edgeIt.next();
            double cap = 0.0;

            // if wrong data was set to the capacities
            try {
                cap = getCapacity(tempEdge);
            } catch (NetworkFlowException nfe) {
                pe.add(CAPACITIES_NOT_OK_ERROR, nfe.getCause());
                noError = false;
            }

            // if no capacity was set or capacities were not positive
            if (cap <= 0) {
                pe.add(CAPACITIES_NOT_OK_ERROR, tempEdge);
                noError = false;

                // System.out.println(getLabel(tempEdge.getSource())+
                // ","+getLabel(tempEdge.getTarget()));
            }
        }

        return noError;
    }

    /**
     * Checks if a given graph has exactly one source and one sink.
     * 
     * @param graph
     *            the graph
     * @param pe
     *            the <code>PreconditionException</code>, where all errors
     *            during the check will be stored
     * @param sourceLabel
     *            the label of the source node
     * @param sinkLabel
     *            the label of the sink node
     * 
     * @return true if exactly one source and one sink were found
     */
    public boolean checkSourceAndSinkLabels(Graph graph,
            PreconditionException pe, String sourceLabel, String sinkLabel) {
        boolean noError = true;

        // System.out.println("searching for Nodes with source label " +
        // sourceLabel + " and sink label " + sinkLabel);
        Node[] sources = getNodesWithLabel(graph, sourceLabel);

        // System.out.println("found " + sources.length + " with source label");
        Node[] sinks = getNodesWithLabel(graph, sinkLabel);

        // System.out.println("found " + sinks.length + " with sink label");
        if (sourceLabel.equals(sinkLabel)) {
            pe.add(EQUAL_SOURCE_AND_SINK_LABEL_ERROR);
            noError = false;
        }

        if (sources.length == 0) {
            pe.add(NO_SOURCE_ERROR);
            noError = false;
        }

        if (sources.length > 1) {
            pe.add(TO_MUCH_SOURCES_ERROR);
            noError = false;
        }

        if (sinks.length == 0) {
            pe.add(NO_SINK_ERROR);
            noError = false;
        }

        if (sinks.length > 1) {
            pe.add(TO_MUCH_SINKS_ERROR);
            noError = false;
        }

        // if sourceLabel != sinkLabel -> source != sink
        return noError;
    }

    /**
     * Colors edges of the network with positive flow green. The intensity of
     * the green ist relative to the rate flow / capacity.
     * 
     * @param network
     *            the network with the edges to be colored
     */
    public void colorFlowEdges(Graph network) {
        Edge edge = null;
        double flow;
        double cap;

        for (Iterator<Edge> edgeIt = network.getEdgesIterator(); edgeIt
                .hasNext();) {
            edge = edgeIt.next();
            cap = nsa.getCapacity(edge);
            flow = nsa.getFlow(edge);

            // calculating the intensity of the color for this edge
            int ratio = MINIMUM_EDGE_G_COLOR_INTENSITY
                    + ((int) Math.round(flow / cap
                            * RELATIVE_EDGE_COLORING_FACTOR));

            // color green, if positive flow
            if (flow > 0.0) {
                setFrameColor(edge, 0, ratio, 0);
                setFillColor(edge, 0, ratio, 0);
            } else {
                // color black, if no flow
                setFrameColor(edge, 0, 0, 0);
                setFillColor(edge, 0, 0, 0);
            }
        }
    }

    /**
     * Generates colored flow labels, that are displayed on the edges with
     * positive flow.
     * 
     * @param network
     *            the network
     */
    public void generateVisibleFlowLabels(Graph network) {
        Edge edge;
        double flow;

        for (Iterator<Edge> edgeIt = network.getEdgesIterator(); edgeIt
                .hasNext();) {
            edge = edgeIt.next();
            flow = getFlow(edge);

            if (flow > 0.0) {
                removeFlowAndNotFlowSource(edge);

                EdgeLabelAttribute flowLabel = new EdgeLabelAttribute(FLOW,
                        flow + "");
                edge.addAttribute(flowLabel, "");
                flowLabel
                        .setTextcolor(new ColorAttribute("color", Color.GREEN));
                flowLabel.setPosition(new EdgeLabelPositionAttribute(
                        GraphicAttributeConstants.POSITION, 0.5d, 0, 10, -10));
            }
        }
    }

    /**
     * Removes all capacities from the edges of a given graph. Possibly stored
     * network flow values and <code> flowSourceNumber </code> remain untouched.
     * 
     * @param graph
     *            the graph
     */
    public void removeCapacities(Graph graph) {
        Edge tempEdge;

        for (Iterator<Edge> edgeIt = graph.getEdgesIterator(); edgeIt.hasNext();) {
            tempEdge = edgeIt.next();
            removeCapacity(tempEdge);
        }
    }

    /**
     * Removes the capacity from a given Edge, but keeps a possibly stored
     * network flow value and <code> flowSourceNumber </code>.
     * 
     * @param e
     *            the edge
     * 
     * @return the value of the deleted capacity
     */
    public double removeCapacity(Edge e) {
        double cap = getCapacity(e);
        removeLabelAttribute(e, CAPACITY);

        return cap;
    }

    /**
     * Removes redundant cyclic flows, that do not add to the total network
     * flow, from a given flow network.
     * 
     * @param network
     *            the flow network
     */
    public void removeCyclicFlows(Graph network) {
        // System.out.println("********** Beginning to remove cyclic Flows
        // **********");
        Node tempNode;
        int i = 0;

        // construct node array and store dfs node index at the nodes
        // System.out.println("- Building nodes Array");
        Node[] nodes = new Node[network.getNumberOfNodes()];

        for (Iterator<Node> nodeIt = network.getNodesIterator(); nodeIt
                .hasNext();) {
            tempNode = nodeIt.next();
            nodes[i] = tempNode;
            setDfsNodeIndex(tempNode, i);
            i++;
        }

        // System.out.println();
        int totallyFoundBackEdges = Integer.MAX_VALUE;
        int run = 1;

        // while loop that iterates the for loop, until no more back edges are
        // found!
        // System.out.println("================== Starting to remove cycles
        // ===================");
        while (totallyFoundBackEdges > 0) {
            // System.out.println(" ------- Remove Cycles, run " + run);
            totallyFoundBackEdges = 0;
            run++;

            // start DFS from every node, that has not yet been visited
            for (int j = 0; j < nodes.length; j++) {
                if (!isDfsVisited(nodes[j])) {
                    // System.out.println(" ***** Starting run on node " + i + "
                    // with label " + nsa.getLabel(nodes[j]));
                    totallyFoundBackEdges += removeCyclicFlowsOnComponent(
                            network, nodes, nodes[j]);

                    // System.out.println();
                    // System.out.println();
                }
            }

            // remove all stored data except dfs node indizes (in the node
            // array)
            removeDfsTemporaryData(network);

            // removeZeroFlows(network);
        }

        // now the dfs node indizes are no longer nessesary
        removeDfsNodeIndizes(network);

        // rounding flow
        roundFlow(network);
        removeZeroFlows(network);
    }

    /**
     * Removes flow and <code>flowSourceNumber</code> from a given edge.
     * 
     * @param edge
     *            the edge.
     */
    public void removeFlow(Edge edge) {
        removeFlowAndNotFlowSource(edge);
        removeFlowSourceNumber(edge);
    }

    /**
     * Removes all the flow of a whole graph, including <code>nodeNumbers</code>
     * and <code>flowSourceNumbers</code>.
     * 
     * @param network
     *            the network
     */
    public void removeFlow(Graph network) {
        Edge tempEdge = null;
        Node tempNode = null;

        for (Iterator<Edge> edgesIt = network.getEdgesIterator(); edgesIt
                .hasNext();) {
            tempEdge = edgesIt.next();
            removeFlow(tempEdge);
        }

        for (Iterator<Node> nodesIt = network.getNodesIterator(); nodesIt
                .hasNext();) {
            tempNode = nodesIt.next();
            removeNodeNumber(tempNode);
        }
    }

    /**
     * Removes the <code> flowSourceNumber </code> from a given edge.
     * 
     * @param edge
     */
    public void removeFlowSourceNumber(Edge edge) {
        removeLabelAttribute(edge, FLOW_SOURCE);
    }

    /**
     * Removes the label of given <code> Attributable </code>
     * 
     * @param a
     *            the <code>Attributable</code>
     * 
     * @return the removed label
     */
    public String removeLabel(Attributable a) {
        String label = getLabel(a);
        removeLabelAttribute(a, LABEL_PATH);

        return label;
    }

    /**
     * Removes a <code>LabelAttribute</code> from an Attributable at a given
     * path
     * 
     * @param attributable
     *            the Attributable
     * @param path
     *            the path, the Label ist stored at
     */
    public void removeLabelAttribute(Attributable attributable, String path) {
        try {
            attributable.removeAttribute(path);
        } catch (AttributeNotFoundException anfe) {
        }
    }

    /**
     * Removes the stored <code> nodeNumber </code> from a given node.
     * 
     * @param node
     *            the node
     */
    public void removeNodeNumber(Node node) {
        removeLabelAttribute(node, NODE_NUMBER);
    }

    /**
     * Removes all flow <code> LabelAttribute </code> with a flow value of 0.0
     * 
     * @param network
     *            the network
     */
    public void removeZeroFlows(Graph network) {
        Edge tempEdge;

        for (Iterator<Edge> edgeIt = network.getEdgesIterator(); edgeIt
                .hasNext();) {
            tempEdge = edgeIt.next();

            if (nsa.getFlow(tempEdge) == 0.0) {
                nsa.removeFlow(tempEdge);
            }
        }
    }

    /**
     * Rounds a double to a maximum of <code> roundingPrecision </code> decimal
     * places in front of the point and after the point.
     * 
     * @param d
     *            the double
     * 
     * @return a double with no more than <code> roundingPrecision </code>
     *         decimal places
     */
    public double round(double d) {
        int integralPart = (int) d;

        // cut of last digits
        double rest = d - integralPart;
        double temp = Math.round(rest * factor);
        rest = temp / factor;

        return integralPart + rest;
    }

    /**
     * Rounds all flow values to <code> roundingPrecision </code> decimal places
     * behind the point
     * 
     * @param network
     *            the network
     */
    public void roundFlow(Graph network) {
        Edge tempEdge;
        double flow;

        for (Iterator<Edge> edgeIt = network.getEdgesIterator(); edgeIt
                .hasNext();) {
            tempEdge = edgeIt.next();
            flow = nsa.getFlow(tempEdge);
            flow = round(flow);
            nsa.setFlow(tempEdge, flow);
        }
    }

    /**
     * Important for <code> removeCyclicFlowsOnComponent </code> Marks an edge
     * to be a back edge or not at the current run of dfs.
     * 
     * @param edge
     *            the edge
     * @param mark
     *            if true, the mark will be set, if false it will be removed
     */
    private void setDfsBackEdgeMark(Edge edge, boolean mark) {
        try {
            edge.removeAttribute(DFS_BACK_EDGE_PATH);
        } catch (AttributeNotFoundException anfe) {
        }

        if (mark) {
            edge.setBoolean(DFS_BACK_EDGE_PATH, true);
        }
    }

    /**
     * Important for <code> removeCyclicFlowsOnComponent </code> Checks if the
     * edge is marked as a back edge by the current run of dfs.
     * 
     * @param edge
     *            the edge
     * 
     * @return true if the edge is marked as a back edge, false otherwise
     */
    private boolean isDfsBackEdgeMarked(Edge edge) {
        boolean mark = false;

        try {
            mark = edge.getBoolean(DFS_BACK_EDGE_PATH);
        } catch (AttributeNotFoundException anfe) {
        }

        return mark;
    }

    /**
     * Important for <code> removeCyclicFlowsOnComponent </code> Checks if a
     * node was 'finished' by dfs.
     * 
     * @param n
     *            the node
     * 
     * @return true, if the node was already finished, false otherwise
     */
    private boolean isDfsFinished(Node n) {
        boolean fin = false;

        try {
            fin = n.getBoolean(DFS_FINISHED_NODE_PATH);
        } catch (AttributeNotFoundException anfe) {
        }

        return fin;
    }

    /**
     * Important for <code> removeCyclicFlowsOnComponent </code> Stores the node
     * index of the node in the node array.
     * 
     * @param n
     *            the node
     * @param i
     *            the index
     */
    private void setDfsNodeIndex(Node n, int i) {
        n.setInteger(DFS_NODE_INDEX, i);
    }

    /**
     * Important for <code> removeCyclicFlowsOnComponent </code> Returns the
     * node index of the node in the node array.
     * 
     * @param n
     *            the node
     * 
     * @return the index of the node
     */
    private int getDfsNodeIndex(Node n) {
        int index = -1;

        try {
            index = n.getInteger(DFS_NODE_INDEX);
        } catch (AttributeNotFoundException anfe) {
        }

        return index;
    }

    /**
     * Important for <code> removeCyclicFlowsOnComponent </code> Stores the
     * index of the predesessor node of the dfs run.
     * 
     * @param target
     *            the node
     * @param sourceIndex
     *            the index
     * 
     * @throws NetworkFlowException
     *             if a called twice upon a node
     */
    private void setDfsPredesessor(Node target, int sourceIndex) {
        if (getDfsPredesessor(target) == -1) {
            target.setInteger(DFS_PREDESESSOR_PATH, sourceIndex);
        } else
            // for each node the predesessor is set only once
            throw new NetworkFlowException(DFS_DOUBLE_PREDESESSOR_ERROR);
    }

    /**
     * Important for <code> removeCyclicFlowsOnComponent </code> Returns the
     * index of the predesessor node of the dfs run.
     * 
     * @param target
     *            the node, the predesessor is searched of
     * 
     * @return the index of the predesessor
     */
    private int getDfsPredesessor(Node target) {
        int sourceIndex = -1;

        try {
            sourceIndex = target.getInteger(DFS_PREDESESSOR_PATH);
        } catch (AttributeNotFoundException anfe) {
        }

        return sourceIndex;
    }

    /**
     * Important for <code> removeCyclicFlowsOnComponent </code> Checks if a
     * node was 'visited' by dfs.
     * 
     * @param n
     *            the node
     * 
     * @return true, if the node was already visited, false otherwise
     */
    private boolean isDfsVisited(Node n) {
        boolean vis = false;

        try {
            vis = n.getBoolean(DFS_VISITED_NODE_PATH);
        } catch (AttributeNotFoundException anfe) {
        }

        return vis;
    }

    /**
     * Checks the capacity constraints for a single edge
     * 
     * @param edge
     *            the edge
     * 
     * @return true if everything is OK, false otherwise
     */
    private boolean checkCapacityPrecisionOfEdge(Edge edge) {
        boolean noError = true;
        double cap = nsa.getCapacity(edge);
        double capInt = (int) cap;
        double capIntRightShift = capInt / factor;

        // check on too much decimals after the floating point
        if (cap != round(cap)) {
            noError = false;
        }

        // check on too much decimals before the floating point
        if (capIntRightShift > 1.0) {
            noError = false;
        }

        return noError;
    }

    /**
     * Important for <code> removeCyclicFlowsOnComponent </code> Marks a node as
     * 'finished' by dfs.
     * 
     * @param n
     *            the node
     * 
     * @throws NetworkFlowException
     *             in a node is finished twice
     */
    private void dfsFinish(Node n) {
        if (!isDfsFinished(n)) {
            n.setBoolean(DFS_FINISHED_NODE_PATH, true);
        } else
            // each Node may be finished only once
            throw new NetworkFlowException(DFS_DOUBLE_FINISH_ERROR);
    }

    /**
     * Important for <code> removeCyclicFlowsOnComponent </code> Marks a node as
     * 'visited' by dfs.
     * 
     * @param n
     *            the node
     * 
     * @throws NetworkFlowException
     *             if a node is visited twice
     */
    private void dfsVisit(Node n) {
        if (!isDfsVisited(n)) {
            n.setBoolean(DFS_VISITED_NODE_PATH, true);
        } else
            // each Node may be visited only once
            throw new NetworkFlowException(DFS_DOUBLE_VISIT_ERROR);
    }

    /**
     * This method removes one flow cycle of the network, by collecting all
     * edges of the cycle and removing the minimum flow found in this cycle from
     * all edges.
     * 
     * @param network
     *            the flow network
     * @param nodes
     *            the graph nodes
     * @param target
     *            target node of the backedge that closes the cycle
     * @param source2
     *            source node of the backedge that closes the cycle
     */
    private void killDfsFlowCycle(Graph network, Node[] nodes, Node target,
            Node source2) {
        // System.out.println (" - killing the flow cycle !!!!!!!!!!!");
        Collection<Edge> edgesBetween;
        Edge edge;
        double cycleFlowValue = Double.MAX_VALUE;
        double flowBetweenTwoNodes = 0.0;
        double flowOnEdge = 0.0;

        // build up cycle list
        // Put into cycle path alternating nodes and edges. The edges between
        // two
        // nodes in the list correspond to the edges between two nodes in the
        // graph
        // System.out.println();
        // System.out.println (" - creating cycle path");
        LinkedList<GraphElement> cyclePath = new LinkedList<GraphElement>();
        cyclePath.addFirst(target);

        Node tempSource = source2;
        Node tempTarget = target;

        do {
            // System.out.println(" * tempsource = " + nsa.getLabel(tempSource)
            // + " temptarget = " + nsa.getLabel(tempTarget));
            edgesBetween = network.getEdges(tempSource, tempTarget);
            flowBetweenTwoNodes = 0.0;

            for (Iterator<Edge> edgeIt = edgesBetween.iterator(); edgeIt
                    .hasNext();) {
                edge = edgeIt.next();
                flowOnEdge = getFlow(edge);

                // System.out.print(" * Edge with flow " + flowOnEdge + " ");
                if (edge.isDirected()) {
                    if ((edge.getSource() == tempSource) && (flowOnEdge > 0.0)) {
                        cyclePath.addFirst(edge);
                        flowBetweenTwoNodes += flowOnEdge;

                        // System.out.println("added to cycle path, flow between
                        // the nodes now = " + flowBetweenTwoNodes);
                    } else {
                        // System.out.println("ignored");
                    }

                    // undirected edge
                } else {
                    // these values must be stored on undirected edges with flow
                    if (flowOnEdge > 0.0) {
                        if (nsa.getFlowSourceNumber(edge) == nsa
                                .getNodeNumber(tempSource)) {
                            cyclePath.addFirst(edge);
                            flowBetweenTwoNodes += flowOnEdge;

                            // System.out.println("added to cycle path, flow
                            // between the nodes now = " + flowBetweenTwoNodes);
                        }
                    } else {
                        // System.out.println("ignored");
                    }
                }
            }

            cycleFlowValue = Math.min(cycleFlowValue, flowBetweenTwoNodes);

            // System.out.println(" -> cycle flow value now at " +
            // cycleFlowValue);
            cyclePath.addFirst(tempSource);
            tempTarget = tempSource;

            try {
                tempSource = nodes[getDfsPredesessor(tempSource)];
            } catch (ArrayIndexOutOfBoundsException aioobe) {
                // do nothing
            }
        } while (tempTarget != target);

        // now iterate over the cyclePath and substract the cycleFlowValue
        // from the flow between two nodes!
        if (cycleFlowValue > 0.0) {
            // System.out.println();
            // System.out.println(" - Beginning to run around cycle path and
            // remove " + cycleFlowValue + " units of flow from the edges");
            tempSource = null;
            tempTarget = null;
            edgesBetween = new LinkedList<Edge>();
            edge = null;
            tempSource = (Node) cyclePath.removeFirst();

            // System.out.println(" * found node: " + nsa.getLabel(tempSource));
            flowOnEdge = 0.0;

            double flowMinusBetweenNodes = 0.0;
            double singleEdgeFlowMinus = 0.0;
            GraphElement next = null;

            // iterate over the complete cycle Path
            for (Iterator<GraphElement> it = cyclePath.iterator(); it.hasNext();) {
                flowMinusBetweenNodes = cycleFlowValue;

                // iterate over the edges between two nodes
                next = it.next();

                while (next instanceof Edge) {
                    edge = (Edge) next;
                    flowOnEdge = nsa.getFlow(edge);

                    // System.out.println(" * found edge with flow "+
                    // flowOnEdge);
                    if (((flowMinusBetweenNodes > 0.0) && (flowOnEdge > 0.0))) {
                        singleEdgeFlowMinus = Math.min(flowMinusBetweenNodes,
                                flowOnEdge);
                        nsa.addToFlow(edge, -singleEdgeFlowMinus);
                        flowMinusBetweenNodes -= singleEdgeFlowMinus;

                        // System.out.println(" -> removing "
                        // +singleEdgeFlowMinus + " Units of flow,");
                        // System.out.println(" there are still " +
                        // flowMinusBetweenNodes + " to remove between the two
                        // nodes an a resulting flow of " + nsa.getFlow(edge));
                    }

                    next = it.next();
                }

                if (next instanceof Node) {
                    // System.out.println(" * found node: " +
                    // nsa.getLabel((Node)next));
                    // do nothing!
                }
            }
        }
    }

    /**
     * Implemented as an advanced form of a dfs on the 'flowgraph' this
     * subalgorithm of <code> removeCyclicFlows </code> removes cyclic flows of
     * a connected component of the graph. For each 'backedge' found by the dfs,
     * one cycle, this edge is part of will be removed, so this method must be
     * called repeatedly for removing all cycles, for all 'backedges'
     * 
     * @param network
     *            the network
     * @param networkNodes
     *            the nodes of the network
     * @param startNode
     *            the node the dfs starts
     * 
     * @return the number of found backedges
     */
    private int removeCyclicFlowsOnComponent(Graph network,
            Node[] networkNodes, Node startNode) {
        Node source;
        Node target;
        Edge edge;
        double flow;
        LinkedList<Edge> backEdges = new LinkedList<Edge>();
        LinkedList<Node> backEdgeSources = new LinkedList<Node>();
        LinkedList<Node> backEdgeTargets = new LinkedList<Node>();

        LinkedList<Node> stack = new LinkedList<Node>();
        int nrOfFoundBackEdges = 0;

        dfsVisit(startNode);
        setDfsPredesessor(startNode, -2); // -2 means this node has no
        // predesessor
        stack.addFirst(startNode);

        // System.out.println(" - Visiting : " + nsa.getLabel(startNode));
        // System.out.println(" - Putting into stack : " +
        // nsa.getLabel(startNode));
        while (!stack.isEmpty()) {
            source = stack.getFirst(); // not remove first like in BFS

            // System.out.println(" - Taking of stack : " +
            // nsa.getLabel(source));
            for (Iterator<Edge> edgeIt = source.getAllOutEdges().iterator(); edgeIt
                    .hasNext();) {
                edge = edgeIt.next();

                // if (!edge.isDirected)
                // if (flowSource == source) -> OK else ignore!
                flow = getFlow(edge);
                target = getOtherEdgeNode(source, edge);

                // System.out.println(" - edge from " + nsa.getLabel(source) + "
                // to " + nsa.getLabel(target) + " with flow : " + flow);
                // only work with edges with positive flow and in the direction
                // of the flow
                if ((edge.isDirected() && (flow > 0.0))
                        || (!edge.isDirected() && (flow > 0.0) && (nsa
                                .getFlowSourceNumber(edge) == nsa
                                .getNodeNumber(source)))) {
                    if (!isDfsVisited(target)) {
                        // System.out.println(" - target " +
                        // nsa.getLabel(target) + " is unvisited, visiting,
                        // adding to stack");
                        // visit, set Predesessor and put in the stack
                        dfsVisit(target);
                        setDfsPredesessor(target, getDfsNodeIndex(source));
                        stack.addFirst(target);

                        // jump out of the for-loop -> continue with the next
                        // top stack element
                        break;
                    } else {
                        // System.out.print(" - target " + nsa.getLabel(target)
                        // + " already visited");
                        // if (isDfsFinished(target)) System.out.print(" and
                        // finished");
                        // if ((getDfsPredesessor(target) ==
                        // getDfsNodeIndex(source))) System.out.print(" and the
                        // source is the predesessor");
                        // System.out.println();
                        // Tests on DFS-Back-Edges, because: backedge -> cycle!
                        // on a selfloop with flow, source and target are the
                        // same
                        if (!isDfsFinished(target)
                                && (getDfsPredesessor(target) != getDfsNodeIndex(source))
                                && (!isDfsBackEdgeMarked(edge))) {
                            // storedSources.addFirst(source);
                            // storedTargets.addFirst(target);
                            // System.out.println(" - target creates a CYCLE,
                            // storing source " + nsa.getLabel(source) + " and
                            // target " + nsa.getLabel(target));
                            // store back edge, mark it and kill cycle later.
                            nrOfFoundBackEdges++;
                            setDfsBackEdgeMark(edge, true);
                            backEdges.addFirst(edge);
                            backEdgeSources.addFirst(source);
                            backEdgeTargets.addFirst(target);

                        }
                    }
                }
            }

            if (stack.getFirst() == source) {
                // if no unvisited node has been found -> finisg & backtrack
                dfsFinish(source);

                // System.out.println(" -> Finishing : " +
                // nsa.getLabel(source));
                stack.removeFirst();
            }

            // System.out.println();
        }

        Iterator<Node> itSources = backEdgeSources.iterator();
        Iterator<Node> itTargets = backEdgeTargets.iterator();
        Iterator<Edge> itEdges = backEdges.iterator();

        for (; (itSources.hasNext() && itTargets.hasNext());) {
            source = itSources.next();
            target = itTargets.next();
            edge = itEdges.next();
            killDfsFlowCycle(network, networkNodes, target, source);
            setDfsBackEdgeMark(edge, false);
        }

        return nrOfFoundBackEdges;
    }

    /**
     * Removes the data stored by <code> removeCyclicFlows </code> of the
     * network, that was not already removed by <code> removeDfsTemporaryData
     * </code>.
     * 
     * @param network
     *            the network
     */
    private void removeDfsNodeIndizes(Graph network) {
        Node node;

        for (Iterator<Node> nodeIt = network.getNodesIterator(); nodeIt
                .hasNext();) {
            node = nodeIt.next();

            try {
                node.removeAttribute(DFS_NODE_INDEX);
            } catch (AttributeNotFoundException anfe) {
            }
        }
    }

    /**
     * Removes temporary data generated by a run of <code>
     * removeCyclicFlowsOnComponent </code>
     * on the network. It is important to remove this data before another run of
     * <code>
     * removeCyclicFlowsOnComponent </code>.
     * 
     * @param network
     */
    private void removeDfsTemporaryData(Graph network) {
        Node node;
        Edge edge;

        for (Iterator<Node> nodeIt = network.getNodesIterator(); nodeIt
                .hasNext();) {
            node = nodeIt.next();

            try {
                node.removeAttribute(DFS_VISITED_NODE_PATH);
            } catch (AttributeNotFoundException anfe) {
            }

            try {
                node.removeAttribute(DFS_FINISHED_NODE_PATH);
            } catch (AttributeNotFoundException anfe) {
            }

            try {
                node.removeAttribute(DFS_PREDESESSOR_PATH);
            } catch (AttributeNotFoundException anfe) {
            }
        }

        for (Iterator<Edge> edgeIt = network.getEdgesIterator(); edgeIt
                .hasNext();) {
            edge = edgeIt.next();

            try {
                edge.removeAttribute(DFS_BACK_EDGE_PATH);
            } catch (AttributeNotFoundException anfe) {
            }
        }
    }

    /**
     * Removes the flow of a given edge but leaves the flow source
     * 
     * @param edge
     *            the edge
     */
    private void removeFlowAndNotFlowSource(Edge edge) {
        removeLabelAttribute(edge, FLOW);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
