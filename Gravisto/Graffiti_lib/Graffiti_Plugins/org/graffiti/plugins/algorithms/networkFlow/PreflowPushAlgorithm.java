// ==============================================================================
//
//   PreflowPushAlgorithm.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
// 
// =============================================================================
// $Id: PreflowPushAlgorithm.java 5766 2010-05-07 18:39:06Z gleissner $
/*
 * Created on 16.06.2004
 */

package org.graffiti.plugins.algorithms.networkFlow;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringParameter;

/**
 * An Implementation of a preflow-push algorithm for solving network-flow
 * problems. It uses the lift-to-front method to compute a maximum flow in
 * O(V^3) as long as there is an finite upper bound on the number of edges
 * between two nodes. The algorithm works with directed and undirected networks
 * with loops and multiedges. If any flow values are stored at the graph before
 * the execution of this algorithm, they will be deleted.
 * 
 * @author Markus K�ser
 * @version $Revision 1.1 $
 */
public class PreflowPushAlgorithm extends AbstractAlgorithm {

    /** Name of the algorithm */
    private static final String NAME = "Preflow Push Algorithmus";

    /** The logger of the algorithm */
    private static final Logger logger = Logger
            .getLogger(PreflowPushAlgorithm.class.getName());

    /** The id for the storage of the node height */
    private static final String NODE_HEIGHT_ID = "NodeHeight";

    /** The path of the storage of the node height */
    private static final String NODE_HEIGHT = FlowNetworkSupportAlgorithms.BASE
            + NODE_HEIGHT_ID;

    /** The id for the storage of the node index */
    private static final String NODE_INDEX_ID = "node_index";

    /** The path of the storage of the node index */
    private static final String NODE_INDEX = FlowNetworkSupportAlgorithms.BASE
            + NODE_INDEX_ID;

    /** The id of the storage of the excess */
    private static final String EXCESS_ID = "Excess";

    /** The path of the storage of the excess */
    private static final String EXCESS = FlowNetworkSupportAlgorithms.BASE
            + EXCESS_ID;

    /** The number of parameters of the algorithm */
    private static final int NUMBER_OF_PARAMS = 4;

    /** index of the parameter 'source label' */
    private static final int SOURCE_LABEL_INDEX = 0;

    /** default source label value */
    public static final String SOURCE_LABEL_DEFAULT = "s";

    /** name of of the source label parameter */
    private static final String SOURCE_LABEL_NAME = "Source node label";

    /** description of the source label parameter */
    private static final String SOURCE_LABEL_DESCRIPTION = "The label of the"
            + "source in this flow network";

    /** index of the parameter 'sink label' */
    private static final int SINK_LABEL_INDEX = 1;

    /** default sink label value */
    public static final String SINK_LABEL_DEFAULT = "t";

    /** name of of the sink label parameter */
    private static final String SINK_LABEL_NAME = "Sink node label";

    /** description of the sink label parameter */
    private static final String SINK_LABEL_DESCRIPTION = "The label of the"
            + "sink in this flow network";

    /** index of the parameter 'show flow' */
    private static final int SHOW_FLOW_INDEX = 2;

    /** default color show flow value */
    private static final boolean SHOW_FLOW_DEFAULT = true;

    /** name of of the show flow parameter! */
    private static final String SHOW_FLOW_NAME = "Show flow";

    /** description of the show flow parameter */
    private static final String SHOW_FLOW_DESCRIPTION = "Colors the flow edges"
            + " and shows the flow values";

    /** index of the parameter 'remove redundant cyclic flows' */
    private static final int REMOVE_REDUNDANT_CYCLIC_FLOWS_INDEX = 3;

    /** default remove redundant cyclic flows value */
    private static final boolean REMOVE_REDUNDANT_CYCLIC_FLOWS_DEFAULT = false;

    /** name of of the remove redundant cyclic flows parameter */
    private static final String REMOVE_REDUNDANT_CYCLIC_FLOWS_NAME = "Remove "
            + "cyclic flow";

    /** description of the remove redundant cyclic flows parameter */
    private static final String REMOVE_REDUNDANT_CYCLIC_FLOWS_DESCRIPTION = "Removes redundant cyclic flows, that do not add to the total network"
            + " flow.";

    /** Error message */
    private static final String WRONG_SOURCE_OR_SINK_ERROR = "Source and sink "
            + "nodes have been set wrongly. They must belong the attached graph.";

    /** Error message */
    private static final String SOURCE_OR_SINK_BEFORE_GRAPH_ERROR = "The graph"
            + "has to be attatchend to the algorithm at first. Then it is possible"
            + "to set source and sink nodes";

    /** Error message */
    private static final String SOURCE_OR_SINK_NOT_FOUND_ERROR = "Source or"
            + "sink with the given labels could not be found in graph.";

    /** Error message */
    private static final String ALGORITHM_NOT_RUN_ERROR = "The algorithm has "
            + "to be executed before the results can be obtained.";

    /** Error message */
    private static final String SOURCE_OR_SINK_NULL_ERROR = "Source and sink "
            + "nodes may not be set to null values";

    /** Error message */
    private static final String SOURCE_AND_SINK_EQUAL_ERROR = "Source and sink "
            + "nodes may not be equal.";

    /** Error message */
    private static final String LIFTING_NODE_TO_A_LESSER_HEIGHT_ERROR = "Nodes"
            + " may not be lifted to a height less than their old height";

    /** the network support algorithms heavily used in this algorithm */
    private FlowNetworkSupportAlgorithms nsa = FlowNetworkSupportAlgorithms
            .getFlowNetworkSupportAlgorithms();

    /**
     * List that carries all nodes of the graph. A special iteration is used for
     * this list in order to get the Lift-to-front effincency of O(|V|^3)
     */
    private LinkedList<Node> liftToFrontList;

    /** The sink node of the network */
    private Node sink;

    /** The source node of the network */
    private Node source;

    /** The label of the sink node */
    private String sinkLabel;

    /** The label of the source node */
    private String sourceLabel;

    /** All nodes of the graph */
    private Node[] nodes;

    /** true, if cyclic flows should be removed */
    private boolean removeCyclicFlow;

    /**
     * true, if the edges with flow should be colored and flow values shoul be
     * displayed at the edges
     */
    private boolean showFlow;

    /** The total network flow after the run of the algorithm */
    private double networkFlow;

    /**
     * Constructs a new instance of the preflow-push algorithm
     */
    public PreflowPushAlgorithm() {
        super();
        reset();
    }

    /**
     * Resets the Algorithms data, then sets the flow network, source and sink.
     * 
     * @param network
     *            the flow network
     * @param source
     *            the source node
     * @param sink
     *            the sink node
     * @param showFlow
     *            sets <code> showFlow </code>
     * @param remCyclicFlows
     *            sets <code> removeCyclicFlows </code>
     */
    public void setAll(Graph network, Node source, Node sink, boolean showFlow,
            boolean remCyclicFlows) {
        this.attach(network);
        this.setSourceAndSink(source, sink);
        this.setShowFlow(showFlow);
        this.setRemoveCyclicFlow(remCyclicFlows);
    }

    /**
     * Returns the name of the algorithm
     * 
     * @return name the name
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return NAME;
    }

    /**
     * Returns the total flow in the network after the execution of the
     * algorithm.
     * 
     * @return the total network flow
     * 
     * @throws NetworkFlowException
     *             if the algorithm was not executed before.
     */
    public double getNetworkFlow() {
        if (networkFlow != Double.MIN_VALUE)
            return networkFlow;
        else {
            NetworkFlowException nfe = new NetworkFlowException(
                    ALGORITHM_NOT_RUN_ERROR);
            nfe.log(logger);
            throw nfe;
        }
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(org.graffiti.plugin.parameter.Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        super.setAlgorithmParameters(params);

        sourceLabel = ((StringParameter) params[SOURCE_LABEL_INDEX])
                .getString();

        sinkLabel = ((StringParameter) params[SINK_LABEL_INDEX]).getString();

        showFlow = ((BooleanParameter) params[SHOW_FLOW_INDEX]).getBoolean()
                .booleanValue();
        removeCyclicFlow = ((BooleanParameter) params[REMOVE_REDUNDANT_CYCLIC_FLOWS_INDEX])
                .getBoolean().booleanValue();
    }

    /**
     * Sets the value of of <code> removeCyclicFlow </code>, which determines,
     * if redundant cyclic flows (that do not add to the total network flow)
     * will be deleted after the run of the algorithm.
     * 
     * @param remove
     *            removeCyclicFlow
     */
    public void setRemoveCyclicFlow(boolean remove) {
        removeCyclicFlow = remove;
    }

    /**
     * Returns the value of of <code> removeCyclicFlow </code>, which
     * determines, if redundant cyclic flows (that do not add to the total
     * network flow) will be deleted after the run of the algorithm.
     * 
     * @return removeCyclicFlow
     */
    public boolean getRemoveCyclicFlow() {
        return removeCyclicFlow;
    }

    /**
     * Sets the value of of <code> showFlow </code>, which determines, if edges
     * with flow will be colored and the flow values should be displayed after
     * the run of the algorithm.
     * 
     * @param show
     */
    public void setShowFlow(boolean show) {
        showFlow = show;
    }

    /**
     * Returns <code> showFlow </code>, which determines, if edges with flow
     * will be colored and the flow values will be displayed after the run of
     * the algorithm.
     * 
     * @return showFlow
     */
    public boolean getShowFlow() {
        return showFlow;
    }

    /**
     * Returns the sink node of the network-flow problem.
     * 
     * @return the sink node
     */
    public Node getSinkNode() {
        return sink;
    }

    /**
     * Sets source node and sink node to the given nodes. The algorithm can not
     * be executed when source and sink nodes are not set. When applied in the
     * GUI both nodes are set by the parameters.
     * 
     * @param sourceNode
     *            the new source node
     * @param sinkNode
     *            the new sink node
     * 
     * @throws NetworkFlowException
     *             if no graph has been attached or <code>
     *         sourceNode </code>
     *             or <code> sinkNode </code> are not of the graph
     */
    public void setSourceAndSink(Node sourceNode, Node sinkNode) {
        if ((sourceNode == null) || (sinkNode == null)) {
            NetworkFlowException nfe = new NetworkFlowException(
                    SOURCE_OR_SINK_NULL_ERROR);
            nfe.log(logger);
            throw nfe;
        }

        if (graph == null) {
            NetworkFlowException nfe = new NetworkFlowException(
                    SOURCE_OR_SINK_BEFORE_GRAPH_ERROR);
            nfe.log(logger);
            throw nfe;
        } else {
            if ((sourceNode.getGraph() == graph)
                    && (sinkNode.getGraph() == graph)) {

                source = sourceNode;
                sink = sinkNode;
            } else {
                NetworkFlowException nfa = new NetworkFlowException(
                        WRONG_SOURCE_OR_SINK_ERROR);
                nfa.log(logger);
                throw nfa;
            }
        }
    }

    /**
     * Returns the source node of the network-flow problem.
     * 
     * @return the source node
     */
    public Node getSourceNode() {
        return source;
    }

    /**
     * Checks if the attached graph is a flow network with source and sink
     * 
     * @throws PreconditionException
     *             if not all preconditions are satisfied
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException pe = new PreconditionException();
        boolean error = false;

        if (!nsa.checkDirectedOrUndirected(graph, pe)) {
            error = true;
        }

        if (!nsa.checkAtLeastTwoNodes(graph, pe)) {
            error = true;
        }

        if (!nsa.checkPositiveCapacities(graph, pe)) {
            error = true;
        }

        if (!nsa.checkCapacityPrecision(graph, pe)) {
            error = true;
        }

        // source and sink are not set via set methods
        if ((source == null) || (sink == null)) {
            if (!nsa
                    .checkSourceAndSinkLabels(graph, pe, sourceLabel, sinkLabel)) {
                error = true;
            }
        }

        if (error)
            throw pe;
    }

    /**
     * Starts the execution of the preflow-push algorithm and computes a maximum
     * flow. The flow is stored as <code> LabelAttribute </code> with ID "flow"
     * on the graph edges. If applied to an undirected graph, the algorithm
     * creates an Attribute with ID "node_number" at the graph nodes and stores
     * the direction of the flow on the edges with another Edge Attribute with
     * ID "flow_source". These flow source numbers refer to the node numbers
     * stored at the nodes.
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        graph.getListenerManager().transactionStarted(this);
        try {
            preAlgorithm();
            algorithm();
            postAlgorithm();
        } catch (RuntimeException re) {
            // if anything is not OK, tidy up, log and throw network-flow
            // exception
            removeTemporaryData();
            removeFlow();
            reset();
            NetworkFlowException nfe;
            if (re instanceof NetworkFlowException) {
                nfe = (NetworkFlowException) re;
            } else {
                nfe = NetworkFlowException.getStandardException(re);
            }
            nfe.log(logger);
            throw nfe;
        } finally {
            graph.getListenerManager().transactionFinished(this);
        }
    }

    /**
     * Removes the flow from the graph. <code> getNetworkFlow() </code> still
     * returns the last computed flow.
     */
    public void removeFlow() {
        nsa.removeFlow(graph);
    }

    /**
     * Resets the algorithm, so it can be applied again with new data.
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        super.reset();
        generateParameters();
        sourceLabel = SOURCE_LABEL_DEFAULT;
        sinkLabel = SINK_LABEL_DEFAULT;
        source = null;
        sink = null;
        nodes = null;
        liftToFrontList = null;
        networkFlow = Double.MIN_VALUE;
        showFlow = SHOW_FLOW_DEFAULT;
        removeCyclicFlow = REMOVE_REDUNDANT_CYCLIC_FLOWS_DEFAULT;
    }

    /**
     * Sets the excess of a node to a new value. The excess of a node is the sum
     * of all ingoing flows minus the sum of all outgoing flows.
     * 
     * @param n
     *            a node
     * @param exc
     *            the new value for the excess
     */
    private void setExcess(Node n, double exc) {
        n.setDouble(EXCESS, exc);
    }

    /**
     * Returns the excess of a node. The excess of a node is the sum of all
     * ingoing flows minus the sum of all outgoing flows.
     * 
     * @param n
     *            a node
     * 
     * @return the excess of <code> n </code>
     */
    private double getExcess(Node n) {
        double excess = 0.0;

        try {
            excess = n.getDouble(EXCESS);
        } catch (AttributeNotFoundException anfe) {
        }

        return excess;
    }

    /**
     * Sets the flow source of an edge to a given node. The <code> nodeNumber
     * </code> of the node
     * will be stored as flow source number of the edge.
     * 
     * @param edge
     *            the edge
     * @param sourceOfEdge
     *            the source node of the flow on this edge
     */
    private void setFlowSource(Edge edge, Node sourceOfEdge) {
        removeFlowSource(edge);

        // nsa tests if the data is consistent
        nsa.setFlowSourceNumber(edge, getNodeIndex(sourceOfEdge));
    }

    /**
     * Returns the source node of the flow on a given edge.
     * 
     * @param edge
     *            the edge
     * 
     * @return the source of the flow on this edge
     */
    private Node getFlowSource(Edge edge) {
        Node flowSource = null;

        try {
            flowSource = nodes[nsa.getFlowSourceNumber(edge)];
        } catch (NetworkFlowException nfe) {
        }

        return flowSource;
    }

    /**
     * Returns the height of a given node
     * 
     * @param n
     *            a node
     * 
     * @return the height of <code> n </code>
     */
    private int getHeight(Node n) {
        int height = -1;
        height = n.getInteger(NODE_HEIGHT);

        return height;
    }

    /**
     * Finds the lowest neighbour with an edge, admissable to pushing
     * 
     * @param pushSource
     *            the node
     * 
     * @return lowestPushTarget the lowest possible push target
     */
    private Node getLowestPushTarget(Node pushSource) {
        Edge pushEdge = null;
        Node pushTarget = null;
        Node flowSource = null;
        double flow = 0.0;
        double residualCap = 0.0;
        double reverseResidualCap = 0.0;

        Node foundPushTarget = null;
        int foundPushTargetHeight = -1;
        Node lowestPushTarget = null;
        int lowestPushTargetHeight = Integer.MAX_VALUE;

        for (Iterator<Edge> edgeIt = pushSource.getEdgesIterator(); edgeIt
                .hasNext();) {
            pushEdge = edgeIt.next();
            pushTarget = nsa.getOtherEdgeNode(pushSource, pushEdge);
            flowSource = getFlowSource(pushEdge);
            flow = nsa.getFlow(pushEdge);
            residualCap = nsa.getResidualCapacity(pushEdge);

            // for the reverseResCap the direction is important!!!
            reverseResidualCap = nsa.getReverseResidualCapacity(pushEdge);

            if (flow == 0.0) {
                if (pushEdge.isDirected()) {
                    if (pushEdge.getSource() == pushSource) {
                        foundPushTarget = pushTarget;
                    }
                } else {
                    foundPushTarget = pushTarget;
                }
            } else {
                if ((pushSource == flowSource) && (residualCap > 0.0)) {
                    foundPushTarget = pushTarget;
                }

                if ((pushTarget == flowSource) && (reverseResidualCap > 0.0)) {
                    foundPushTarget = pushTarget;
                }
            }

            if (foundPushTarget != null) {
                foundPushTargetHeight = getHeight(foundPushTarget);

                if (foundPushTargetHeight < lowestPushTargetHeight) {
                    lowestPushTarget = foundPushTarget;
                    lowestPushTargetHeight = foundPushTargetHeight;
                }
            }
        }

        return lowestPushTarget;
    }

    /**
     * Returns the index of the node in the nodes array of this class.
     * 
     * @param n
     *            the node
     * 
     * @return the index of the node
     */
    private int getNodeIndex(Node n) {
        return n.getInteger(NODE_INDEX);
    }

    /**
     * Checks if a node is overflowing (if it has an excess > 0).
     * 
     * @param n
     *            a node
     * 
     * @return <code>true</code> if <code> n </code> is overflowing,
     *         <code>false</code> otherwise.
     */
    private boolean isOverflowing(Node n) {
        return (getExcess(n) > 0.0);
    }

    /**
     * Makes sure, source and sink are set. If setter methods were used to set
     * source and sink, nothing happens. Else the graph is searched for nodes
     * with the source label and nodes with the sink label. These nodes are
     * taken as source and sink.
     * 
     * @throws NetworkFlowException
     *             if source and sink cannot be determined or source and sink
     *             are equal.
     */
    private void assertSourceAndSinkAreSet() {
        if ((source == null) && (sink == null)) {
            try {
                source = (nsa.getNodesWithLabel(graph, sourceLabel))[0];
                sink = (nsa.getNodesWithLabel(graph, sinkLabel))[0];
            } catch (ArrayIndexOutOfBoundsException aioobe) {
                throw new NetworkFlowException(SOURCE_OR_SINK_NOT_FOUND_ERROR);
            }
        } else {
            if (source == sink)
                throw new NetworkFlowException(SOURCE_AND_SINK_EQUAL_ERROR);
        }
    }

    /**
     * Adds a given value to a nodes excess. The excess of a node is the sum of
     * all ingoing flows minus the sum of all outgoing flows.
     * 
     * @param n
     *            a node
     * @param addToExcess
     *            the value to be added to the excess of <code> n
     *        </code>
     */
    private void addToExcess(Node n, double addToExcess) {
        double oldExcess = getExcess(n);
        double newExcess = oldExcess + addToExcess;
        setExcess(n, newExcess);
    }

    /**
     * The main preflow push and lift to front algorithm
     */
    private void algorithm() {
        Node oNode = null;
        int oldHeight;
        int newHeight;

        Iterator<Node> liftToFrontIt = liftToFrontList.iterator();

        while (liftToFrontIt.hasNext()) {
            oNode = liftToFrontIt.next();

            oldHeight = getHeight(oNode);
            discharge(oNode);
            newHeight = getHeight(oNode);

            if (newHeight > oldHeight) {
                // Move to the front of the list and
                // restart list run with a new iterator
                liftToFrontIt.remove();
                liftToFrontList.addFirst(oNode);
                liftToFrontIt = liftToFrontList.iterator();
            }
        }
    }

    /**
     * Discharges a node by repeatedly pushing the flow away from it and lifting
     * it, until the excess becomes 0.0 .
     * 
     * @param overflowingNode
     *            the node to be discharged
     */
    private void discharge(Node overflowingNode) {
        while (isOverflowing(overflowingNode)) {
            pushExcessAway(overflowingNode);

            if (isOverflowing(overflowingNode)) {
                liftUp(overflowingNode);
            }
        }
    }

    /**
     * Generates the parameters.
     */
    private void generateParameters() {
        StringParameter sourceLabelParam = new StringParameter(
                SOURCE_LABEL_DEFAULT, SOURCE_LABEL_NAME,
                SOURCE_LABEL_DESCRIPTION);
        StringParameter sinkLabelParam = new StringParameter(
                SINK_LABEL_DEFAULT, SINK_LABEL_NAME, SINK_LABEL_DESCRIPTION);
        BooleanParameter showFlowParam = new BooleanParameter(
                SHOW_FLOW_DEFAULT, SHOW_FLOW_NAME, SHOW_FLOW_DESCRIPTION);
        BooleanParameter removeCyclicFlowsParam = new BooleanParameter(
                REMOVE_REDUNDANT_CYCLIC_FLOWS_DEFAULT,
                REMOVE_REDUNDANT_CYCLIC_FLOWS_NAME,
                REMOVE_REDUNDANT_CYCLIC_FLOWS_DESCRIPTION);

        parameters = new Parameter[NUMBER_OF_PARAMS];
        parameters[SOURCE_LABEL_INDEX] = sourceLabelParam;
        parameters[SINK_LABEL_INDEX] = sinkLabelParam;
        parameters[SHOW_FLOW_INDEX] = showFlowParam;
        parameters[REMOVE_REDUNDANT_CYCLIC_FLOWS_INDEX] = removeCyclicFlowsParam;
    }

    /**
     * initializes the datastructures and generates a initial preflow. All
     * non-loop edges from the source are filled with a flow equal to their
     * capacity. The source height is set to |V| and all other heights to 0.
     */
    private void initializeHeightAndPreflow() {
        Node tempNode = null;
        Edge tempEdge = null;
        nodes = new Node[graph.getNumberOfNodes()];

        int index = 0;

        // set all Nodes to height 0, excess 0 and their node index
        for (Iterator<Node> nodeIt = graph.getNodesIterator(); nodeIt.hasNext();) {
            tempNode = nodeIt.next();
            nodes[index] = tempNode;
            tempNode.setInteger(NODE_INDEX, index);
            nsa.setNodeNumber(tempNode, index);
            tempNode.setInteger(NODE_HEIGHT, 0);

            if (getExcess(tempNode) != 0.0) {
                setExcess(tempNode, 0.0);
            }

            index++;
        }

        // set source to height n
        source.setInteger(NODE_HEIGHT, graph.getNumberOfNodes());

        // generate initial preflow for all Edges from source except selfloops
        liftToFrontList = new LinkedList<Node>();

        for (Iterator<Node> nodeIt = graph.getNodesIterator(); nodeIt.hasNext();) {
            tempNode = nodeIt.next();

            if ((tempNode != source) && (tempNode != sink)) {
                liftToFrontList.addFirst(tempNode);
            }
        }

        for (Iterator<Edge> edgeIt = source.getAllOutEdges().iterator(); edgeIt
                .hasNext();) {
            tempEdge = edgeIt.next();

            if (tempEdge.getSource() != tempEdge.getTarget()) {
                double tempCap = nsa.getResidualCapacity(tempEdge);
                nsa.addToFlow(tempEdge, tempCap);

                // store flow direction at undirected edges
                setFlowSource(tempEdge, source);

                Node targetNode = nsa.getOtherEdgeNode(source, tempEdge);
                addToExcess(source, -tempCap);
                addToExcess(targetNode, tempCap);
            }
        }
    }

    /**
     * lifts a node to a new height
     * 
     * @param n
     *            the node
     * @param newHeight
     *            the new height
     * 
     * @throws NetworkFlowException
     *             if a node ist lifted to a lesser height
     */
    private void lift(Node n, int newHeight) {
        int oldHeight = 0;

        try {
            oldHeight = n.getInteger(NODE_HEIGHT);
        } catch (AttributeNotFoundException anfe) {
        }

        if (newHeight > oldHeight) {
            n.setInteger(NODE_HEIGHT, newHeight);
        } else
            throw new NetworkFlowException(
                    LIFTING_NODE_TO_A_LESSER_HEIGHT_ERROR);
    }

    /**
     * Lifts a overflowing node, so that at least one push becomes possible.
     * 
     * @param overflowingNode
     *            a given node with excess > 0.0
     */
    private void liftUp(Node overflowingNode) {
        Node lowestPushTarget = getLowestPushTarget(overflowingNode);
        int newHeight = getHeight(lowestPushTarget) + 1;
        lift(overflowingNode, newHeight);
    }

    /**
     * Computations after the run of the algorithm, like rounding flow values,
     * perhaps removing cyclic flows, removing temporary data and coloring edges
     * with positive flow.
     */
    private void postAlgorithm() {
        nsa.roundFlow(graph);
        networkFlow = nsa.round(Math.abs(getExcess(source)));

        if (removeCyclicFlow) {
            removeCyclicFlow();
        }

        tidyUpData();

        if (showFlow) {
            nsa.colorFlowEdges(graph);
            nsa.generateVisibleFlowLabels(graph);
        }
    }

    /**
     * Initializes the algorithm and creates an initial preflow.
     */
    private void preAlgorithm() {
        // makes sure, source and sink are set via set methods or parameters
        assertSourceAndSinkAreSet();

        // deletes all Attributes with path flow
        removeFlow();

        // deletes all data stored at the paths reserved for this algorithm
        removeTemporaryData();

        // initalizes the heights and a starting preflow
        initializeHeightAndPreflow();
    }

    /**
     * Pushs as much flow as possible from pushSource to pushTarget
     * 
     * @param pushSource
     *            the source of the push
     * @param pushTarget
     *            the target of the push
     */
    private void push(Node pushSource, Node pushTarget) {
        // get excess of pushSource
        double excess = getExcess(pushSource);

        Edge pushEdge = null;
        Node flowSource = null;
        double cap = 0.0;
        double flow = 0.0;
        double residualCap = 0.0;
        double reverseResidualCap = 0.0;
        double pushCap = 0.0;
        double pushAmount = 0.0;
        Collection<Edge> edgesBetween = graph.getEdges(pushSource, pushTarget);
        for (Iterator<Edge> edgeIt = edgesBetween.iterator(); edgeIt.hasNext();) {
            pushEdge = edgeIt.next();
            flowSource = getFlowSource(pushEdge);
            cap = nsa.getCapacity(pushEdge);
            flow = nsa.getFlow(pushEdge);
            residualCap = nsa.getResidualCapacity(pushEdge);

            // for the reverseResCap the direction is important!!!
            reverseResidualCap = nsa.getReverseResidualCapacity(pushEdge);

            // push on an edge without flow
            if (flow == 0.0) {
                if (pushEdge.isDirected()) {
                    if (pushEdge.getSource() == pushSource) {
                        pushCap = cap;
                        pushAmount = Math.min(pushCap, excess);
                        pushAlongFlow(pushSource, pushTarget, pushEdge,
                                pushAmount);
                        excess -= pushAmount;
                    }
                } else {
                    pushCap = cap;
                    pushAmount = Math.min(pushCap, excess);

                    pushAlongFlow(pushSource, pushTarget, pushEdge, pushAmount);
                    excess -= pushAmount;
                }
            } else {
                // push along the flow (directed and undirected)
                if ((pushSource == flowSource) && (residualCap > 0.0)) {
                    pushCap = residualCap;
                    pushAmount = Math.min(pushCap, excess);
                    pushAlongFlow(pushSource, pushTarget, pushEdge, pushAmount);
                    excess -= pushAmount;
                }

                // push against the flow (directed and undirected)
                if ((pushTarget == flowSource) && (reverseResidualCap > 0.0)) {
                    pushCap = reverseResidualCap;
                    pushAmount = Math.min(pushCap, excess);
                    pushAgainstFlow(pushSource, pushTarget, pushEdge,
                            pushAmount);
                    excess -= pushAmount;
                }
            }

            if (excess <= 0.0)
                return;
        }
    }

    /**
     * Low level push method which pushes a given amount from source to target
     * along an edge in the opposite direction as the previous flow.
     * 
     * @param pushSource
     *            the source of the push
     * @param pushTarget
     *            the target of the push
     * @param pushEdge
     *            the edge for the push
     * @param pushAmount
     *            the amount of flow to be pushed
     */
    private void pushAgainstFlow(Node pushSource, Node pushTarget,
            Edge pushEdge, double pushAmount) {
        double oldFlow = nsa.getFlow(pushEdge);
        double newFlow = oldFlow - pushAmount;

        // test which of the two flows is greater
        if (newFlow < 0.0) {
            setFlowSource(pushEdge, pushSource);
            newFlow = Math.abs(newFlow);
        }

        nsa.setFlow(pushEdge, newFlow);
        addToExcess(pushSource, -pushAmount);
        addToExcess(pushTarget, pushAmount);

    }

    /**
     * Low level push method which pushes a given amount from source to target
     * along an edge in the same direction as the previous flow.
     * 
     * @param pushSource
     *            the source of the push
     * @param pushTarget
     *            the target of the push
     * @param pushEdge
     *            the edge for the push
     * @param pushAmount
     *            the amount of flow to be pushed
     */
    private void pushAlongFlow(Node pushSource, Node pushTarget, Edge pushEdge,
            double pushAmount) {
        setFlowSource(pushEdge, pushSource);

        nsa.addToFlow(pushEdge, pushAmount);
        addToExcess(pushSource, -pushAmount);
        addToExcess(pushTarget, pushAmount);
    }

    /**
     * Tries to push all excess from a overflowing node to neighbour nodes which
     * are one unit lower.
     * 
     * @param overflowingNode
     *            a given node with excess > 0.0
     */
    private void pushExcessAway(Node overflowingNode) {

        int overFlowingHeight = getHeight(overflowingNode);
        Node neighbor = null;
        int neighborHeight = -1;

        for (Iterator<Node> neighborIt = overflowingNode.getNeighborsIterator(); neighborIt
                .hasNext();) {
            if (!isOverflowing(overflowingNode))
                return;

            neighbor = neighborIt.next();
            neighborHeight = getHeight(neighbor);

            if (overFlowingHeight == (neighborHeight + 1)) {
                push(overflowingNode, neighbor);
            }
        }
    }

    /**
     * Removes cyclic flows, that do not add to the total flow from the network.
     */
    private void removeCyclicFlow() {
        nsa.removeCyclicFlows(graph);
    }

    /**
     * Removes the flow source on this edge.
     * 
     * @param edge
     *            the edge.
     */
    private void removeFlowSource(Edge edge) {
        nsa.removeFlowSourceNumber(edge);
    }

    /**
     * Removes temporary data like node indizes, node heights and excess values
     * from the nodes.
     */
    private void removeTemporaryData() {
        Node node = null;

        for (Iterator<Node> nodeIt = graph.getNodesIterator(); nodeIt.hasNext();) {
            // iterating over all Nodes
            node = nodeIt.next();

            // trying to remove node index, node height and excess
            try {
                node.removeAttribute(NODE_HEIGHT);
            } catch (AttributeNotFoundException anfe) {
            }

            try {
                node.removeAttribute(NODE_INDEX);
            } catch (AttributeNotFoundException anfe) {
            }

            try {
                node.removeAttribute(EXCESS);
            } catch (AttributeNotFoundException anfe) {
            }
        }
    }

    /**
     * Removes temporary data from nodes and edges and makes sure, that there
     * are no <code> flowSourceNumbers </code> and <code> nodeNumbers </code> on
     * directed graph anymore.
     */
    private void tidyUpData() {
        Edge edge;
        Node tempNode;

        removeTemporaryData();

        for (Iterator<Edge> edgeIt = graph.getEdgesIterator(); edgeIt.hasNext();) {
            // iterating over all edges and their flow sources
            edge = edgeIt.next();

            // on edges with flow 0.0 => flow and flow sources are removed
            if (nsa.getFlow(edge) == 0.0) {
                nsa.removeFlow(edge);
            } else {
                if (edge.isDirected()) {
                    nsa.removeFlowSourceNumber(edge);
                }
            }
        }

        if (graph.isDirected()) {
            for (Iterator<Node> nodeIt = graph.getNodesIterator(); nodeIt
                    .hasNext();) {
                tempNode = nodeIt.next();
                nsa.removeNodeNumber(tempNode);
            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
