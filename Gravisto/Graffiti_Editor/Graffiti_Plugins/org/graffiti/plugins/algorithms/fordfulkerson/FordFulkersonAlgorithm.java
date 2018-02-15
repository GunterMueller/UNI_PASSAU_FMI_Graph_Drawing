// =============================================================================
//
//   FordFulkersonAlgorithm.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: FordFulkersonAlgorithm.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.fordfulkerson;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeExistsException;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.EdgeLabelAttribute;
import org.graffiti.graphics.EdgeLabelPositionAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugin.parameter.StringParameter;
import org.graffiti.selection.Selection;
import org.graffiti.util.Queue;

/**
 * This is an implementation of ford fulkerson algorithm with the method of
 * J.Edmonds and R. Karp, which takes into account the shortest augmenting path
 * for calculation of the network flow.
 * 
 * @version $Revision: 5766 $
 */
public class FordFulkersonAlgorithm extends AbstractAlgorithm {

    /** The id of label attribute "capacity" for edges. */
    public String capacity = "capacity";

    /**
     * The id for the new label attributes on edges which arrise as result of
     * this algorithm
     */
    public static final String FLOW = "flow";

    /** The expected path of node label attribute. */
    public String nodesLabelPath = ".label";

    /** The position of "only selection parameter" in the parameter array. */
    public static final int ONLY_SELECTION_PARAM = 5;

    /** The position of "selection parameter" in the parameter array. */
    public static final int SELECTION_PARAM = 0;

    /** The position of "sink node parameter" in the parameter array. */
    public static final int SINK_PARAM = 2;

    /** The position of "source node parameter" in the parameter array. */
    public static final int SOURCE_PARAM = 1;

    public static final int NODE_NAME_PARAM = 3;

    public static final int EDGE_NAME_PARAM = 4;

    /**
     * The id of temporary integer attribute "currentResidualCap" for nodes.
     * This attribute exists only at runtime of this algorithm.
     */
    private final String CURRENT_RESIDUAL_CAP = "currentResidualCap";

    /**
     * The id of temporary boolean attribute "onPath" for edges, which has value
     * "true" if the appropriate edge is on the augmenting path. This attribute
     * exists only at runtime of this algorithm.
     */
    private final String ON_PATH = "onPath";

    /**
     * The id of temporary boolean attribute "reduceFlow" for nodes, which has
     * value "true" if the appropriate node is connected to an edge on the
     * augmenting path, which flow has to be reduced. This attribute exists only
     * at runtime of this algorithm.
     */
    private final String REDUCE_FLOW = "reduceFlow";

    /**
     * The id of temporary integer attribute "tmpCap" for edges. This attribute
     * exists only at runtime of this algorithm. It correspond to the value of
     * the label attribute "capacity". This procedure saves conversion each time
     * this attribute is needed.
     */
    private final String TEMPCAP = "tmpCap";

    /**
     * The id of temporary integer attribute "tmpFlow" for edges. This attribute
     * exists only at runtime of this algorithm.
     */
    private final String TEMPFLOW = "tmpFlow";

    /**
     * Collection of edges based on whom capacities the network flow will be
     * calculated
     */
    private Collection<Edge> edgesCollection = null;

    /** Collection of nodes for which is contained in the network. */
    private Collection<Node> nodesCollection = null;

    /**
     * The sink node. Defaults to <code>null</code>. May be set by
     * <code>setSinkNode</code>.
     */
    private Node sinkNode = null;

    /**
     * The source node. Defaults to <code>null</code>. May be set by
     * <code>setSourceNode</code>.
     */
    private Node sourceNode = null;

    /**
     * The selection of nodes and edges for which the network flow will be
     * calculated. If it is null or the parameter "only Selection" is not set
     * the whole graph will be used for calculation.
     */
    private Selection selection = null;

    /** DOCUMENT ME! */
    private SelectionParameter selectionParam;

    /** DOCUMENT ME! */
    private StringParameter sinkParam;

    /** DOCUMENT ME! */
    private StringParameter sourceParam;

    private StringParameter nodeLabelName;

    private StringParameter edgeLabelName;

    /**
     * Parameter which if set lets algorithm calculate only on the selected set
     * of nodes and edges.
     */
    private boolean onlySelection = false;

    /** The number of parameters in the parameter array. */
    private final int NUMBER_OF_PARAMS = 6;

    /** DOCUMENT ME! */
    private BooleanParameter onlySelectionParam;

    /**
     * The flag will be set true if more than one node has the label of source
     * or sink node.
     */
    private boolean tooManySourceOrSinkNodes = false;

    /** Field for saving current network flow value */
    private int networkFlow = 0;

    /**
     * Constructs a new instance.
     */
    public FordFulkersonAlgorithm() {
        selectionParam = new SelectionParameter("Selection",
                "If any graph elements are selected, algorithm can be started "
                        + "only on these selected elements.");
        sourceParam = new StringParameter("s", "source node label",
                "the regular expression for the source node");
        sinkParam = new StringParameter("t", "sink node label",
                "the regular expression for the sink node");
        nodeLabelName = new StringParameter("label0", "nodes' label name",
                "the nodes' label name for the source and sink");
        edgeLabelName = new StringParameter("capacity", "edges' label name",
                "the edges' label name used as capacity");

        onlySelectionParam = new BooleanParameter(false, "only on selection?",
                "indicator if the algorithm have to calculate only on selected elements");
    }

    /**
     * Sets the new edges collection.
     * 
     * @param edges
     *            edges collection to be set.
     */
    public void setEdgesCollection(Collection<Edge> edges) {
        this.edgesCollection = edges;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Ford-Fulkerson";
    }

    /**
     * Retutns the value of the total network flow.
     * 
     * @return the value of the total network flow.
     */
    public int getNetworkFlow() {
        return networkFlow;
    }

    /**
     * Sets the new nodes collection.
     * 
     * @param nodes
     *            nodes collection to be set.
     */
    public void setNodesCollection(Collection<Node> nodes) {
        this.nodesCollection = nodes;
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        super.setAlgorithmParameters(params);

        selection = ((SelectionParameter) parameters[SELECTION_PARAM])
                .getSelection();

        onlySelection = ((BooleanParameter) parameters[ONLY_SELECTION_PARAM])
                .getBoolean().booleanValue();

        nodesLabelPath = "."
                + ((StringParameter) parameters[NODE_NAME_PARAM]).getString();
        capacity = ((StringParameter) parameters[EDGE_NAME_PARAM]).getString();

        // moved to attach(Graph g)
        // sourceNode = findNodeWithLabelAndPath(((StringParameter)
        // parameters[SOURCE_PARAM]).getString(),
        // nodesLabelPath);
        //
        // sinkNode = findNodeWithLabelAndPath(((StringParameter)
        // parameters[SINK_PARAM]).getString(),
        // nodesLabelPath);

        if (onlySelection && (selection != null)) {
            edgesCollection = selection.getEdges();
            nodesCollection = selection.getNodes();
        }
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#getParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        parameters = new Parameter[NUMBER_OF_PARAMS];
        parameters[SELECTION_PARAM] = selectionParam;
        parameters[SOURCE_PARAM] = sourceParam;
        parameters[SINK_PARAM] = sinkParam;
        parameters[NODE_NAME_PARAM] = nodeLabelName;
        parameters[EDGE_NAME_PARAM] = edgeLabelName;
        parameters[ONLY_SELECTION_PARAM] = onlySelectionParam;

        return super.getAlgorithmParameters();
    }

    /**
     * Sets the sink node to the given value.
     * 
     * @param sinkNode
     *            the new sink node.
     */
    public void setSinkNode(Node sinkNode) {
        this.sinkNode = sinkNode;
    }

    /**
     * Sets the source node to the given value.
     * 
     * @param sourceNode
     *            the new source node.
     */
    public void setSourceNode(Node sourceNode) {
        this.sourceNode = sourceNode;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#attach(org.graffiti.graph.Graph)
     */
    @Override
    public void attach(Graph g) {
        super.attach(g);

        if (!onlySelection || (selection == null)) {
            edgesCollection = graph.getEdges();
            nodesCollection = graph.getNodes();
        }

        sourceNode = findNodeWithLabelAndPath(
                ((StringParameter) parameters[SOURCE_PARAM]).getString(),
                nodesLabelPath);

        sinkNode = findNodeWithLabelAndPath(
                ((StringParameter) parameters[SINK_PARAM]).getString(),
                nodesLabelPath);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        if (onlySelection && (selection == null)) {
            errors
                    .add("You have not selected any graph elements, but because your choice in "
                            + "the algorithm dialog a selection is required!");
        }

        if (sourceNode == null) {
            errors.add("The source node is not set.");
        }

        if (sinkNode == null) {
            errors.add("The sink node is not set.");
        }

        if (!nodesCollection.contains(sourceNode)
                || !nodesCollection.contains(sinkNode)) {
            errors
                    .add("The sink or source node is not selected. Please include "
                            + "both nodes, source and sink, in the selection or choose other "
                            + "nodes as source or sink.");
        }

        // if there are more than one nodes with the same labels which
        // are selected for
        // source or sink the user will be asked for making her/his
        // choice unequivocal.
        if (tooManySourceOrSinkNodes) {
            errors
                    .add("The label of source or sink node is not unique. "
                            + "Please assign for source and sink nodes only unique labels");
        }

        if ((sourceNode != null) && (sinkNode != null)
                && sourceNode.equals(sinkNode)) {
            errors.add("The source and sink nodes are the same.");
        }

        if (!areDirected(edgesCollection)) {
            errors
                    .add("The selected graph has to be directed, i.e. all edges have"
                            + "to be directed.");
        }

        if (!areConnected(sourceNode, sinkNode, nodesCollection)) {
            errors.add("The selected graph has to be connected.");
        }

        Iterator<Edge> edgesIterator = edgesCollection.iterator();

        for (; edgesIterator.hasNext();) {
            Edge edge = edgesIterator.next();

            int capacity = -1;
            Attribute attr = null;

            try {
                attr = edge.getAttribute(this.capacity);
                capacity = Integer.parseInt(((LabelAttribute) attr).getLabel());

                if (capacity < 0) {
                    errors
                            .add("A negative capacity was detected at an edge. Please set "
                                    + " positive capacity values for all edges.");
                }
            } catch (AttributeNotFoundException anfe) {
                errors.add("The attribute was not found at an edge. Please set"
                        + " positive capacity values for all edges.");
            } catch (NumberFormatException nfe) {
                errors.add("Attribute " + attr.getName()
                        + " could not parsed as int");
            } catch (ClassCastException cce) {
                errors.add("Attribute " + attr.getName()
                        + " is not a LabelAttribute");
            }
        }

        if (!errors.isEmpty())
            throw errors;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        // setColor(sourceNode, "graphics.fillcolor", 0, 255, 0, 255);
        // setColor(sinkNode, "graphics.fillcolor", 0, 255, 0, 255);
        graph.getListenerManager().transactionStarted(this);

        // initialize the network flow to 0
        networkFlow = 0;

        // initilize all edges with start flow, residual capacity
        // values and
        // set boolean flags which mark back or tree edges.
        init();

        // a map which contains as key-value pairs nodes with their
        // predecessors
        // on the augmentig path.
        Map<Node, Node> nodeToPredecessorOnPath = new HashMap<Node, Node>();

        // initialize the map with source node.
        nodeToPredecessorOnPath.put(sourceNode, null);

        // calculate the residual capacity.
        int resCap = getResidualCapacity(nodeToPredecessorOnPath);

        while (resCap != 0) {
            networkFlow = networkFlow + resCap;

            // augment flow with calculated residual capacity of
            // the graph/network.
            augmentFlow(resCap, nodeToPredecessorOnPath);

            // start next iteration of BFS
            // initialize nodes' and edges' attributes to start
            // values.
            // they are nodes' attribute with residual capacity
            // value and
            // edges' attribute for isBackEdge flag.
            initializeToStartValues();

            // make the map empty for a new search for augmenting
            // path
            // and initialize it with source node.
            nodeToPredecessorOnPath.clear();
            nodeToPredecessorOnPath.put(sourceNode, null);

            // calculate the new residual capacity.
            resCap = getResidualCapacity(nodeToPredecessorOnPath);
        }

        manifestResultAndCleanUp();
        graph.getListenerManager().transactionFinished(this);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        super.reset();
        sinkNode = null;
        sourceNode = null;
        edgesCollection = null;
        nodesCollection = null;
        selection = null;
        tooManySourceOrSinkNodes = false;
        onlySelection = false;
        networkFlow = 0;
    }

    // /**
    // * Sets the color of the given graph element to the given color.
    // *
    // * @param e graph element.
    // * @param p path to the color attribute.
    // * @param r red color value.
    // * @param g green color value.
    // * @param b blur color value.
    // * @param t transparency value.
    // */
    // private void setColor(GraphElement e, String p, int r, int g, int b, int
    // t)
    // {
    // e.setInteger(p + ".transparency", t);
    // e.setInteger(p + ".red", r);
    // e.setInteger(p + ".green", g);
    // e.setInteger(p + ".blue", b);
    // }

    /**
     * Calculates the resudual capacity in the current network.
     * 
     * @param nodeToPredecessorOnPath
     *            Map from successor to predecessor for reconstruction of
     *            augmenting path along whom the residual capacity will be
     *            added.
     * 
     * @return the value of residual capacity.
     */
    private int getResidualCapacity(Map<Node, Node> nodeToPredecessorOnPath) {
        // set which contains all reached nodes.
        Set<Node> reached = new HashSet<Node>();

        // set source node as reached
        reached.add(sourceNode);

        // queue of nodes for breadth first search on the given graph
        Queue queue = new Queue();

        // put source node in the queue
        queue.addLast(sourceNode);

        while (!(sinkNode.getInteger(CURRENT_RESIDUAL_CAP) > 0)
                && !queue.isEmpty()) {
            Node currentNode = (Node) queue.removeFirst();

            for (Iterator<Edge> iter = currentNode.getAllOutEdges().iterator(); iter
                    .hasNext();) {
                Edge edge = iter.next();

                if (!edgesCollection.contains(edge)) {
                    continue;
                }

                Node successorNode = edge.getTarget();
                int flow = edge.getInteger(TEMPFLOW);
                int capacity = edge.getInteger(TEMPCAP);

                if (!reached.contains(successorNode) && (flow < capacity)) {
                    reached.add(successorNode);
                    queue.addLast(successorNode);

                    successorNode.setInteger(CURRENT_RESIDUAL_CAP, Math.min(
                            capacity - flow, currentNode
                                    .getInteger(CURRENT_RESIDUAL_CAP)));

                    nodeToPredecessorOnPath.put(successorNode, currentNode);
                    successorNode.setBoolean(REDUCE_FLOW, false);
                    edge.setBoolean(ON_PATH, true);
                }
            }

            for (Iterator<Edge> iter = currentNode.getAllInEdges().iterator(); iter
                    .hasNext();) {
                Edge edge = iter.next();

                if (!edgesCollection.contains(edge)) {
                    continue;
                }

                Node predecessorNode = edge.getSource();
                int flow = edge.getInteger(TEMPFLOW);

                if (!reached.contains(predecessorNode) && (flow > 0)) {
                    reached.add(predecessorNode);
                    queue.addLast(predecessorNode);

                    predecessorNode
                            .setInteger(CURRENT_RESIDUAL_CAP, Math.min(flow,
                                    currentNode
                                            .getInteger(CURRENT_RESIDUAL_CAP)));

                    nodeToPredecessorOnPath.put(predecessorNode, currentNode);
                    predecessorNode.setBoolean(REDUCE_FLOW, true);
                    edge.setBoolean(ON_PATH, true);
                }
            }
        }

        return sinkNode.getInteger(CURRENT_RESIDUAL_CAP);
    }

    /**
     * Checks if there is a way betweeen two nodes source and sink which
     * consists of nodes are contained in the selected nodes collection.
     * Warning: sink and source nodes must be contained in the given nodes
     * collection.
     * 
     * @param source
     *            start node.
     * @param sink
     *            end node.
     * @param nodes
     *            collection of nodes.
     * 
     * @return true if source and sink node are connected and the sink can be
     *         reached from source over the nodes contained only in the given
     *         nodes collection.
     */
    private boolean areConnected(Node source, Node sink, Collection<Node> nodes) {
        if (!nodes.contains(source) || !nodes.contains(sink))
            return false;

        // set which contains all reached nodes.
        Set<Node> reached = new HashSet<Node>();

        // set source node as reached
        reached.add(source);

        // queue of nodes for breadth first search on the given graph
        Queue queue = new Queue();

        // put source node in the queue
        queue.addLast(source);

        while (!queue.isEmpty()) {
            Node currentNode = (Node) queue.removeFirst();

            for (Iterator<Edge> iter = currentNode.getAllOutEdges().iterator(); iter
                    .hasNext();) {
                Edge edge = iter.next();

                if (!edgesCollection.contains(edge)) {
                    continue;
                }

                Node successorNode = edge.getTarget();

                if (successorNode.equals(sink))
                    return true;

                if (!reached.contains(successorNode)
                        && nodes.contains(successorNode)) {
                    reached.add(successorNode);
                    queue.addLast(successorNode);
                }
            }
        }

        return false;
    }

    /**
     * Returns true if all edges are directed.
     * 
     * @param edges
     *            collection of edges has to be checked.
     * 
     * @return true if all edges are connected.
     */
    private boolean areDirected(Collection<Edge> edges) {
        Iterator<Edge> edgesIter = edges.iterator();

        while (edgesIter.hasNext()) {
            Edge edge = edgesIter.next();

            if (!edge.isDirected())
                return false;
        }

        return true;
    }

    /**
     * Augments the edges' flow along the augementing path.
     * 
     * @param resCap
     *            with this value the edges will be augmented.
     * @param nodeToPredecessorOnPath
     *            map from successor to predecessor for reconstruction of
     *            augmenting path.
     */
    private void augmentFlow(int resCap, Map<Node, Node> nodeToPredecessorOnPath) {
        Node currentNodeOnPath = sinkNode;

        while (nodeToPredecessorOnPath.get(currentNodeOnPath) != null) {
            Node predecessorNodeOnPath = nodeToPredecessorOnPath
                    .get(currentNodeOnPath);
            Collection<Edge> edgesCol = graph.getEdges(currentNodeOnPath,
                    predecessorNodeOnPath);

            for (Iterator<Edge> iter = edgesCol.iterator(); iter.hasNext();) {
                Edge edge = iter.next();

                // exclude all edges that are not selected.
                if (!edgesCollection.contains(edge)
                        || !edge.getBoolean(ON_PATH)) {
                    continue;
                }

                if (!currentNodeOnPath.getBoolean(REDUCE_FLOW)) {
                    edge.setInteger(TEMPFLOW, edge.getInteger(TEMPFLOW)
                            + resCap);
                } else {
                    edge.setInteger(TEMPFLOW, edge.getInteger(TEMPFLOW)
                            - resCap);
                }
            }

            currentNodeOnPath = predecessorNodeOnPath;
        }
    }

    /**
     * Returns the node, which has the specified label at the given path. May
     * return <code>null</code> if nothing is found.
     * 
     * @param label
     *            the regular expression.
     * @param labelPath
     *            path of the label attribute.
     * 
     * @return the node, which has the specified label at the given path.
     */
    private Node findNodeWithLabelAndPath(String label, String labelPath) {
        Node node = null;

        List<Node> nodesList = new LinkedList<Node>();

        for (Iterator<Node> i = graph.getNodesIterator(); i.hasNext();) {
            Node currentNode = i.next();

            try {
                LabelAttribute labelAttribute = (LabelAttribute) currentNode
                        .getAttribute(labelPath);

                if (labelAttribute.getLabel().equals(label)) {
                    nodesList.add(currentNode);
                }
            } catch (AttributeNotFoundException anfe) {
            } catch (ClassCastException cce) {
            }
        }

        if (!nodesList.isEmpty()) {
            node = nodesList.get(0);
        }

        if (nodesList.size() > 1) {
            tooManySourceOrSinkNodes = true;
        }

        return node;
    }

    /**
     * Initializes the algorithm.
     */
    private void init() {
        networkFlow = 0;

        Iterator<Edge> edgesIterator = edgesCollection.iterator();

        for (; edgesIterator.hasNext();) {
            Edge edge = edgesIterator.next();

            try {
                edge.removeAttribute(Attribute.SEPARATOR + FLOW);
            } catch (AttributeNotFoundException e) {
            }

            int capacity = Integer.parseInt(((LabelAttribute) edge
                    .getAttribute(this.capacity)).getLabel());

            // create Attributes needed for algorithm
            edge.setInteger(TEMPCAP, capacity);
            edge.setInteger(TEMPFLOW, 0);
            edge.setBoolean(ON_PATH, false);
        }

        Iterator<Node> nodesIterator = nodesCollection.iterator();

        for (; nodesIterator.hasNext();) {
            Node node = nodesIterator.next();
            int resCapValue = 0;

            if (node == sourceNode) {
                resCapValue = Integer.MAX_VALUE;
            }

            node.setInteger(CURRENT_RESIDUAL_CAP, resCapValue);
            node.setBoolean(REDUCE_FLOW, false);
        }
    }

    /**
     * Initialize the algorithm to the start values for next pass.
     */
    private void initializeToStartValues() {
        // initialize all nodes' attribute "resCap" to the start
        // values.
        Iterator<Node> nodesIterator = nodesCollection.iterator();

        for (; nodesIterator.hasNext();) {
            Node node = nodesIterator.next();
            int resCap = 0;

            if (node == sourceNode) {
                resCap = Integer.MAX_VALUE;
            }

            node.setInteger(CURRENT_RESIDUAL_CAP, resCap);
            node.setBoolean(REDUCE_FLOW, false);
        }

        // initialize all edges' attribute "isBackEdge" to the start
        // value.
        Iterator<Edge> edgesIterator = edgesCollection.iterator();

        for (; edgesIterator.hasNext();) {
            Edge edge = edgesIterator.next();
            edge.setBoolean(ON_PATH, false);
        }
    }

    /**
     * Presents the algorithm result and cleans up all temporary created
     * attributes.
     */
    private void manifestResultAndCleanUp() {
        Iterator<Edge> edgesIterator = edgesCollection.iterator();

        for (; edgesIterator.hasNext();) {
            Edge edge = edgesIterator.next();

            int flow = edge.getInteger(TEMPFLOW);

            if (flow > 0) {
                EdgeLabelAttribute flowLabel = new EdgeLabelAttribute(FLOW, ""
                        + flow);

                try {
                    edge.addAttribute(flowLabel, "");
                } catch (AttributeExistsException e) {
                    try {
                        flowLabel = (EdgeLabelAttribute) edge
                                .getAttribute(Attribute.SEPARATOR + FLOW);
                        flowLabel.setLabel("" + flow);
                    } catch (ClassCastException cce) {
                        Random random = new Random(System.currentTimeMillis());
                        flowLabel = new EdgeLabelAttribute(FLOW
                                + random.nextInt(100000), "" + flow);
                    }
                }

                flowLabel
                        .setTextcolor(new ColorAttribute("color", Color.GREEN));
                flowLabel.setPosition(new EdgeLabelPositionAttribute(
                        GraphicAttributeConstants.POSITION, 0.5d, 0, 10, -10));

                // setColor(edge, "graphics.framecolor", 0, 255, 0, 255);
            }

            edge.removeAttribute(TEMPCAP);
            edge.removeAttribute(TEMPFLOW);
            edge.removeAttribute(ON_PATH);
        }

        Iterator<Node> nodesIterator = nodesCollection.iterator();

        for (; nodesIterator.hasNext();) {
            Node node = nodesIterator.next();
            node.removeAttribute(CURRENT_RESIDUAL_CAP);
            node.removeAttribute(REDUCE_FLOW);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
