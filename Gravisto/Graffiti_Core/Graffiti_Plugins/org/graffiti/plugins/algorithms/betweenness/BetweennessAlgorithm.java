// ==============================================================================
//
//   BetweennessAlgorithm.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: BetweennessAlgorithm.java 5766 2010-05-07 18:39:06Z gleissner $
/*
 * Created on 17.08.2004
 */

package org.graffiti.plugins.algorithms.betweenness;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.graffiti.attributes.AttributeExistsException;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.EdgeLabelAttribute;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.DoubleParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.networkFlow.FlowNetworkSupportAlgorithms;
import org.graffiti.util.Queue;

/**
 * 
 * This algorithm computes the Betweenneess values for nodes and edges of a
 * graph (Betweenness is a centrality value). It is defined as the Sum of all
 * shortest paths that contain the node/edge divided by the total number of
 * shortest paths. Edge directions will be ignored during the run of the
 * algorithm. BetweennessAlgorithm runs in O(|V|(|V|+|E|)).
 * 
 * @author Markus K�ser
 * @version $Revision 1.1 $
 */
public class BetweennessAlgorithm extends AbstractAlgorithm {

    /** The Name of the algorithm */
    private static final String NAME = "Betweenness";

    /** The logger of the algorithm */
    private static final Logger logger = Logger
            .getLogger(BetweennessAlgorithm.class.getName());

    /** The number of parameters for this algorithm */
    private static final int NUMBER_OF_PARAMS = 5;

    /** The index of the for nodes parameter */
    private static final int FOR_NODES_INDEX = 0;

    /** The default value of the for nodes parameter */
    private static final boolean FOR_NODES_DEFAULT = true;

    /** The name of the for nodes parameter */
    private static final String FOR_NODES_NAME = "Compute betweenness for "
            + "nodes";

    /** The description of the for nodes parameter */
    private static final String FOR_NODES_DESCRIPTION = "computes betweenness"
            + "values for all nodes";

    /** The index of the for edges parameter */
    private static final int FOR_EDGES_INDEX = 1;

    /** The default value of the for edges parameter */
    private static final boolean FOR_EDGES_DEFAULT = true;

    /** The name of the for edges parameter */
    private static final String FOR_EDGES_NAME = "Compute betweenness for "
            + "edges";

    /** The description of the for edges parameter */
    private static final String FOR_EDGES_DESCRIPTION = "computes betweenness"
            + "values for all edges";

    /** The index of the show betweenness parameter */
    private static final int SHOW_BETWEENNESS_INDEX = 2;

    /** The default value of the show betweenness parameter */
    private static final boolean SHOW_BETWEENNESS_DEFAULT = true;

    /** The name of the show betweenness parameter */
    private static final String SHOW_BETWEENNESS_NAME = "Shows Betweenness";

    /** The description of the show betweenness parameter */
    private static final String SHOW_BETWEENNESS_DESCRIPTION = "Shows the "
            + "computed betweenness values";

    /** The index of the scale betweenness parameter */
    private static final int SCALE_BETWEENNESS_INDEX = 3;

    /** The default value of the scale betweenness parameter */
    private static final boolean SCALE_BETWEENNESS_DEFAULT = true;

    /** The name of the scale betweenness parameter */
    private static final String SCALE_BETWEENNESS_NAME = "Scales betweenness";

    /** The description of the scale betweenness parameter */
    private static final String SCALE_BETWEENNESS_DESCRIPTION = "Scales "
            + "the betweenness values";

    /** The index of the scaled betweenness sum parameter */
    private static final int SCALED_BETWEENNESS_SUM_INDEX = 4;

    /** The default value of the scaled betweenness sum parameter */
    private static final double SCALED_BETWEENNESS_SUM_DEFAULT = 1.0;

    /** The name of the scaled betweenness sum parameter */
    private static final String SCALED_BETWEENNESS_SUM_NAME = "Scaled "
            + "betweenness sum";

    /** The description of the scaled betweenness sum parameter */
    private static final String SCALED_BETWEENNESS_SUM_DESCRIPTION = "The sum of the scaled betweenness values equals this value";

    /** Base for storing data on nodes and edges */
    private static final String BASE = "betweennessValue.";

    /** Path for storage of the Bfs-number */
    private static final String BFS_NUMBER = BASE + "BfsNumber";

    /** Path for the storage of betweenness LabelAttributes */
    private static final String BETWEENNESS_LABEL = "betweenness"; // no BASE

    /** Path for the storage of the betweenness values */
    private static final String BETWEENNESS = BASE + BETWEENNESS_LABEL;

    /** Path for storage of the value of the number of shortest paths on nodes */
    private static final String NUMBER_OF_SHORTEST_PATHS = BASE
            + "betweennessNumberOfShortestPaths";

    /**
     * Path for storage of the dependency values for the computation of
     * betweenness
     */
    private static final String DEPENDENCY = BASE + "betweennessDependency";

    /** Error message */
    private static final String NO_BETWEENNESS_ERROR = "This graph element"
            + "has no betweeness value";

    /** Error message */
    private static final String NO_NUMBER_OF_SHORTEST_PATHS_ERROR = "This node"
            + "has no number of shortest paths (for the computation of betweenness)";

    /** Error message */
    private static final String NO_DEPENDENCY_NUMBER_ERRROR = "This graph "
            + "element has no dependency value (for the computation of betweenness)";

    /** Error message */
    private static final String NO_BFS_NUMBER_ERROR = "This graph element has"
            + "no bfs-number value";

    /** Error message */
    private static final String COMPUTE_NOTHING_ERROR = "betweenness must be "
            + "computed either for nodes, edges or both";

    /** Error message */
    private static final String SCALED_BETWEENNESS_SUM_TO_LOW_ERROR = "The scaled betweenness sum may not be less than 0.1";

    /** The singleton <code> BetweennessSupportAlgorithms</code> object */
    private BetweennessSupportAlgorithms bsa = BetweennessSupportAlgorithms
            .getBetweennessSupportAlgorithms();

    /** The singleton <code> FlowNetworkSupportAlgorithms</code> object */
    private FlowNetworkSupportAlgorithms nsa = FlowNetworkSupportAlgorithms
            .getFlowNetworkSupportAlgorithms();

    /** if true, the betweenness will be computed for the edges of the graph */
    private boolean forEdges;

    /** if true, the betweenness will be computed for the nodes of the graph */
    private boolean forNodes;

    /** if true, the betweenness will be skaled to values between */
    private boolean scaleBetweenness;

    /** if true, the betweenness will be shown as LabelAttribute */
    private boolean showBetweenness;

    /**
     * If the betweenness values will be scaled, then the sum of the betweenness
     * values of all nodes will be aproximately this value. Identically the same
     * holds for edges
     */
    private double scaledBetweennessSum;

    /**
     * the maximum value of the betweenness on the graph (important for scaling)
     */
    private double sumEdgeBetweenness;

    /**
     * the minimum value of the betweenness on the graph (important for scaling)
     */
    private double sumNodeBetweenness;

    /**
     * Constructs a new instance of a betweenness algorithm
     */
    public BetweennessAlgorithm() {
        super();
        reset();
    }

    /**
     * Sets all data needed for the run of a BetweennessAlgorithm. At least one
     * of the boolean values forN and forE must be set to true, otherwise the
     * algorithm has nothing to compute.
     * 
     * @param graph
     *            the graph, the betweenness should be calculated for
     * @param forN
     *            if true, the betweenness will be calculated for the nodes of
     *            the graph
     * @param forE
     *            if true, the betweenness will be calculated for the edges of
     *            the graph
     * @param show
     *            if true, the betweenness will be shown as
     *            <code>LabelAttributes </code> on the graph
     * @param scale
     *            if true, the total betweenness values will be scaled, so the
     *            sum of the values of all nodes (edges) equals the fiven
     *            scaledSum Parameter
     * @param scaledSum
     *            the sum for scaling. (If scale is set to false, the value of
     *            this parameter will not be used in the algorithm)
     */
    public void setAll(Graph graph, boolean forN, boolean forE, boolean show,
            boolean scale, double scaledSum) {
        attach(graph);
        setForNodes(forN);
        setForEdges(forE);
        setShowBetweenness(show);
        setScaleBetweenness(scale);
        setScaledBetweennessSum(scaledSum);
    }

    /**
     * Sets the betweenness of a node or edge to the given value.
     * 
     * @param e
     *            the node or edge
     * @param betweenness
     *            the value
     */
    public void setBetweenness(GraphElement e, double betweenness) {
        removeBetweenness(e);

        try {
            e.setDouble(BETWEENNESS, betweenness);
        } catch (AttributeExistsException aee) {
        }
    }

    /**
     * Returns the betweenness of a given graph element in its graph.
     * 
     * @param e
     *            the graph element
     * 
     * @return the betweenness of e
     * 
     * @throws BetweennessException
     *             if the element has no betweenness
     */
    public double getBetweenness(GraphElement e) {
        double betweenness = 0.0;

        try {
            betweenness = e.getDouble(BETWEENNESS);
        } catch (AttributeNotFoundException anfe) {
            BetweennessException be = new BetweennessException(
                    NO_BETWEENNESS_ERROR, anfe);
            be.log(logger);
            throw be;
        }

        return betweenness;
    }

    /**
     * Sets the forEdges flag.
     * 
     * @param b
     *            if true, the betweenness will be calculated for the edges of
     *            the graph
     */
    public void setForEdges(boolean b) {
        forEdges = b;
    }

    /**
     * Returns the forEdges flag.
     * 
     * @return the forEdges flag
     */
    public boolean getForEdges() {
        return forEdges;
    }

    /**
     * Sets the forNodes flag
     * 
     * @param b
     *            if true, the betweenness will be calculated for the nodes of
     *            the graph
     */
    public void setForNodes(boolean b) {
        forNodes = b;
    }

    /**
     * Returns the forNodes flag
     * 
     * @return the forNodes flag
     */
    public boolean getForNodes() {
        return forNodes;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return NAME;
    }

    /**
     * Sets the parameters given in the editor to the algorithm.
     * 
     * @param params
     *            the parameter
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        super.setAlgorithmParameters(params);
        forNodes = ((BooleanParameter) params[FOR_NODES_INDEX]).getBoolean()
                .booleanValue();
        forEdges = ((BooleanParameter) params[FOR_EDGES_INDEX]).getBoolean()
                .booleanValue();
        showBetweenness = ((BooleanParameter) params[SHOW_BETWEENNESS_INDEX])
                .getBoolean().booleanValue();
        scaleBetweenness = ((BooleanParameter) params[SCALE_BETWEENNESS_INDEX])
                .getBoolean().booleanValue();
        scaledBetweennessSum = ((DoubleParameter) params[SCALED_BETWEENNESS_SUM_INDEX])
                .getDouble().doubleValue();
    }

    /**
     * Sets the scaleBetweenness flag.
     * 
     * @param b
     *            if true, the total betweenness values will be scaled, so the
     *            sum of the values of all nodes (edges) equals the fiven
     *            scaledSum Parameter
     */
    public void setScaleBetweenness(boolean b) {
        scaleBetweenness = b;
    }

    /**
     * returns the scaleBetweenness flag.
     * 
     * @return scaleBetweenness flag
     */
    public boolean getScaleBetweenness() {
        return scaleBetweenness;
    }

    /**
     * Sets the value of scaledBetweennessSum.
     * 
     * @param scaledSum
     *            the sum for scaling. if scaleSum is set to true, then the
     *            betweenness values will be scaled so that the sum of the
     *            betweenness values of all nodes (edges) is the
     *            scaledBetweennessSum (If scale is set to false, the value of
     *            this parameter will not be used in the algorithm).
     */
    public void setScaledBetweennessSum(double scaledSum) {
        scaledBetweennessSum = scaledSum;
    }

    /**
     * Returns the value of scaledBetweennessSum.
     * 
     * @return scaledBetweennessSum
     */
    public double getScaledBetweennessSum() {
        return scaledBetweennessSum;
    }

    /**
     * Sets the showBetweennessFlag.
     * 
     * @param b
     *            if true, the betweenness will be shown as
     *            <code>LabelAttributes
     *        </code> on the graph
     */
    public void setShowBetweenness(boolean b) {
        showBetweenness = b;
    }

    /**
     * Returns the showBetweennessFlag.
     * 
     * @return the showBetweennessFlag
     */
    public boolean getShowBetweenness() {
        return showBetweenness;
    }

    /**
     * Checks if the parameter settings are OK.
     * 
     * @throws PreconditionException
     *             if forNodes and forEdges are both set to false or if the
     *             scaledBetweennessSum is set to low.
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException pe = new PreconditionException();
        boolean noError = true;

        if (!forNodes && !forEdges) {
            pe.add(COMPUTE_NOTHING_ERROR);
            noError = false;
        }

        if (scaledBetweennessSum < 0.1) {
            pe.add(SCALED_BETWEENNESS_SUM_TO_LOW_ERROR);
            noError = false;
        }

        if (!noError)
            throw pe;
    }

    /**
     * Computes the betweenness of the nodes and edges of a given graph.
     */
    public void computeBetweenness() {
        // Node[] graphNodes = (Node[]) graph.getNodes().toArray(new Node[0]);
        Collection<Node> componentNodes;
        Collection<Edge> componentEdges;
        Node startNode;

        // get nodes and edges of all components of the graph
        Collection<Node>[] components = bsa.getAllConnectedComponents(graph);
        Collection<Edge>[] edgesOfComponents = bsa
                .getEdgesOfAllConnectedComponents(components);

        initBetweenness();

        // for every node -> start the computation of the dependencies
        for (int c = 0; c < components.length; c++) {
            componentNodes = components[c];
            componentEdges = edgesOfComponents[c];

            for (Iterator<?> nodeIt = componentNodes.iterator(); nodeIt
                    .hasNext();) {
                startNode = (Node) nodeIt.next();
                computeDependencies(componentNodes, componentEdges, startNode);
                updateBetweenness(componentNodes, componentEdges);
                removeDependency(componentNodes, componentEdges);
            }
        }
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        graph.getListenerManager().transactionStarted(this);
        try {
            preAlgorithm();
            computeBetweenness();
            postAlgorithm();
        } catch (Throwable t) {
            removeAllAlgorithmData();
            reset();
            BetweennessException be;
            if (t instanceof BetweennessException) {
                be = (BetweennessException) t;
            } else {
                be = BetweennessException.getStandardException(t);
            }
            be.log(logger);
            throw be;
        } finally {
            graph.getListenerManager().transactionFinished(this);
        }
    }

    /**
     * Removes the betweenness of all nodes and edges.
     */
    public void removeBetweenness() {
        removeNodeBetweenness();
        removeEdgeBetweenness();
    }

    /**
     * Removes the betweenness labels from all nodes and edges.
     */
    public void removeBetweennessLabels() {
        Node tempNode;
        Edge tempEdge;

        for (Iterator<?> nodeIt = graph.getNodesIterator(); nodeIt.hasNext();) {
            tempNode = (Node) nodeIt.next();
            removeBetweennessLabel(tempNode);
        }

        for (Iterator<?> edgeIt = graph.getEdgesIterator(); edgeIt.hasNext();) {
            tempEdge = (Edge) edgeIt.next();
            removeBetweennessLabel(tempEdge);
        }
    }

    /**
     * Resets the algorithm.
     */
    @Override
    public void reset() {
        super.reset();
        generateParameters();
        forNodes = FOR_NODES_DEFAULT;
        forEdges = FOR_EDGES_DEFAULT;
        showBetweenness = SHOW_BETWEENNESS_DEFAULT;
        scaleBetweenness = SCALE_BETWEENNESS_DEFAULT;
        sumNodeBetweenness = 0.0;
        sumEdgeBetweenness = 0.0;
        scaledBetweennessSum = SCALED_BETWEENNESS_SUM_DEFAULT;
    }

    /**
     * Sets the Bfs-number of a <code> GraphElement </code> to the given number.
     * 
     * @param e
     *            the element
     * @param number
     *            the number
     */
    private void setBfsNumber(GraphElement e, int number) {
        removeBfsNumber(e);

        try {
            e.setInteger(BFS_NUMBER, number);
        } catch (AttributeExistsException aee) {
        }
    }

    /**
     * Returns the value of the Bfs-number of a given graph element.
     * 
     * @param e
     *            the element
     * 
     * @return the Bfs-number
     * 
     * @throws BetweennessException
     *             if no Bfs-number was stored.
     */
    private int getBfsNumber(GraphElement e) {
        int number = -1;

        try {
            number = e.getInteger(BFS_NUMBER);
        } catch (AttributeNotFoundException anfe) {
            BetweennessException be = new BetweennessException(
                    NO_BFS_NUMBER_ERROR);
            be.log(logger);
            throw be;
        }

        return number;
    }

    /**
     * Sets the dependency of a <code> GraphElement </code> to a given value.
     * 
     * @param e
     *            the element
     * @param dependency
     *            the value
     */
    private void setDependency(GraphElement e, double dependency) {
        removeDependency(e);

        try {
            e.setDouble(DEPENDENCY, dependency);
        } catch (AttributeExistsException aee) {
        }
    }

    /**
     * Returns the value of a given <code> GraphElement </code>
     * 
     * @param e
     *            the element
     * 
     * @return the value
     * 
     * @throws BetweennessException
     *             if no dependency was stored.
     */
    private double getDependency(GraphElement e) {
        double dependency = 0.0;

        try {
            dependency = e.getDouble(DEPENDENCY);
        } catch (AttributeNotFoundException anfe) {
            BetweennessException be = new BetweennessException(
                    NO_DEPENDENCY_NUMBER_ERRROR, anfe);
            be.log(logger);
            throw be;
        }

        return dependency;
    }

    /**
     * Sets the number of shortes paths along a graph element to the given
     * value.
     * 
     * @param e
     *            the element
     * @param numberPaths
     *            the number of shortest paths along e
     */
    private void setNumberOfShortestPaths(GraphElement e, long numberPaths) {
        removeNumberOfShortestPaths(e);

        try {
            e.setLong(NUMBER_OF_SHORTEST_PATHS, numberPaths);
        } catch (AttributeExistsException aee) {
        }
    }

    /**
     * Returns the number of shortest Paths along a <code> GraphElement
     * </code>.
     * 
     * @param e
     *            the element
     * 
     * @return the number of shortest paths along e
     * 
     * @throws BetweennessException
     *             if such a number was not stored at e
     */
    private long getNumberOfShortestPaths(GraphElement e) {
        long number = 0;

        try {
            number = e.getLong(NUMBER_OF_SHORTEST_PATHS);
        } catch (AttributeNotFoundException anfe) {
            BetweennessException be = new BetweennessException(
                    NO_NUMBER_OF_SHORTEST_PATHS_ERROR);
            be.log(logger);
            throw be;
        }

        return number;
    }

    /**
     * Adds the given value to the betweenness of a <code> GraphElement
     * </code>. Negative values
     * will be substracted.
     * 
     * @param e
     *            the element
     * @param addTo
     *            the aditional value
     */
    private void addToBetweenness(GraphElement e, double addTo) {
        double oldValue = getBetweenness(e);
        double newValue = oldValue + addTo;
        setBetweenness(e, newValue);
    }

    /**
     * Adds the given value to the dependency of a <code> GraphElement </code>.
     * Negative values will be substracted.
     * 
     * @param e
     *            the element
     * @param addTo
     *            the additional value
     */
    private void addToDependency(GraphElement e, double addTo) {
        double oldValue = getDependency(e);
        double newValue = oldValue + addTo;
        setDependency(e, newValue);
    }

    /**
     * Adds the given value to the number of shortest paths along a <code>
     * GraphElement </code>
     * . Negative Values will be substracted.
     * 
     * @param e
     *            the element
     * @param toAdd
     *            additional value
     */
    private void addToNumberOfShortestPaths(GraphElement e, long toAdd) {
        long oldNumber = getNumberOfShortestPaths(e);
        long newNumber = oldNumber + toAdd;
        setNumberOfShortestPaths(e, newNumber);
    }

    /**
     * Computes the sum of betweenesses of all nodes and the sum of
     * betweennesses of all edges.
     */
    private void computeBetweennessSum() {
        double tempBetweenness;

        if (forNodes) {
            Node tempNode;

            for (Iterator<?> nodeIt = graph.getNodesIterator(); nodeIt
                    .hasNext();) {
                tempNode = (Node) nodeIt.next();
                tempBetweenness = getBetweenness(tempNode);
                sumNodeBetweenness += tempBetweenness;
            }
        }

        if (forEdges) {
            Edge tempEdge;

            for (Iterator<?> edgeIt = graph.getEdgesIterator(); edgeIt
                    .hasNext();) {
                tempEdge = (Edge) edgeIt.next();
                tempBetweenness = getBetweenness(tempEdge);
                sumEdgeBetweenness += tempBetweenness;
            }
        }
    }

    /*
     * /** Sets a found mark to a given <code> GraphElement </code> or removes
     * it. @param e the <code> GraphElement </code> @param mark if true, the
     * mark will be set, if false it will be removed
     * 
     * private void setFoundMark(GraphElement e, boolean mark){ try {
     * e.removeAttribute(FOUND_MARK); } catch (AttributeNotFoundException anfe)
     * { }
     * 
     * if (mark) { e.setBoolean(FOUND_MARK, true); } }
     * 
     * /** Sets the found marks of the given nodes or removes them. @param
     * elements the nodes @param mark if true, the mark will be set to all
     * nodes, if false all potential marks will be removed.
     * 
     * private void setFoundMark(Collection elements, boolean mark) {
     * GraphElement temp;
     * 
     * for (Iterator nodeIt = elements.iterator(); nodeIt.hasNext();) { temp =
     * (GraphElement) nodeIt.next(); setFoundMark(temp, mark); } }
     * 
     * /** Checks if a <code> GraphElement </code> is marked as found. @param e
     * the element @return true if the element is marked, false otherwise
     * 
     * private boolean isFoundMarked(GraphElement e) { boolean isMarked = false;
     * 
     * try { isMarked = e.getBoolean(FOUND_MARK); } catch
     * (AttributeNotFoundException anfe) { } return isMarked; } /** If <code>
     * mark </code> is set to true this method sets the component mark to a
     * given graph element. All nodes and edges with this mark must belong to
     * the same component of the same graph. If <code> mark </code> is set to
     * false, then the mark will be removed.
     * 
     * @param e the element @param mark if true, the mark will be set, if false
     * it will be removed
     * 
     * public void setComponentMark(GraphElement e, boolean mark) { try {
     * e.removeAttribute(COMPONENT_MARK); } catch (AttributeNotFoundException
     * anfe) { }
     * 
     * if (mark) { e.setBoolean(COMPONENT_MARK, true); } }
     * 
     * /** If <code> mark </code> is set to true this method sets the component
     * mark to the given graph elements. All nodes and edges with this mark must
     * belong to the same component of the same graph. If <code> mark </code> is
     * set to false, then the mark will be removed.
     * 
     * @param elements the elements @param mark if true, the mark will be set,
     * if false it will be removed
     * 
     * public void setComponentMark(Collection elements, boolean mark) {
     * GraphElement temp;
     * 
     * for (Iterator nodeIt = elements.iterator(); nodeIt.hasNext();) { temp =
     * (GraphElement) nodeIt.next(); setComponentMark(temp, mark); } }
     * 
     * /** Checks if a graph element has a component mark. All Nodes and edges
     * with this mark have to belong to the same component of the same graph.
     * 
     * @param e the element
     * 
     * @return true, if the node has the mark, false otherwise
     * 
     * public boolean isComponentMarked(GraphElement e) { boolean isMarked =
     * false;
     * 
     * try { isMarked = e.getBoolean(COMPONENT_MARK); } catch
     * (AttributeNotFoundException anfe) { }
     * 
     * return isMarked; }
     */
    /**
     * Computes the dependencies of all nodes of a given weakly connected
     * component to the start node (All other nodes have dependency zero). This
     * is a variant of a Bfs algorithm.
     * 
     * @param nodes
     *            the nodes of a connected component
     * @param edges
     *            the edges of the same connected component
     * @param startNode
     *            the start node for the Bfs. The dependencies of this node will
     *            be computed in this method.
     */
    private void computeDependencies(Collection<Node> nodes,
            Collection<Edge> edges, Node startNode) {
        // init shortest paths, predesessors and dependencies
        HashMap<Node, LinkedList<Node>> predesessors = initDependencyTempData(
                nodes, edges);

        // init stack of nodes in partial order of their distance to the BFS
        // source
        LinkedList<Node> stack = new LinkedList<Node>();

        // BFS queue
        Queue queue = new Queue();

        // Begin with start node
        bsa.setComponentMark(startNode, true);

        queue.addLast(startNode);
        setBfsNumber(startNode, 0);
        setNumberOfShortestPaths(startNode, 1);

        // System.out.println("Startnode : " + nsa.getLabel(startNode) + " Bfs
        // number: " + getBfsNumber(startNode));
        Node source;
        Edge edge;
        Node target;

        // System.out.println("Starting While Loop\n");
        while (!queue.isEmpty()) {
            source = (Node) queue.removeFirst();

            // System.out.println(" Source : " + nsa.getLabel(source));
            for (Iterator<Edge> edgeIt = source.getEdgesIterator(); edgeIt
                    .hasNext();) {
                edge = edgeIt.next();

                if (bsa.isComponentMarked(edge)) {
                    continue;
                }

                bsa.setComponentMark(edge, true);
                target = nsa.getOtherEdgeNode(source, edge);

                // This is a forward edge (multiedges are forward edges)
                if (bsa.isComponentMarked(target)) {
                    if (getBfsNumber(source) < getBfsNumber(target)) {
                        predesessors.get(target).addLast(source);

                        addToNumberOfShortestPaths(target,
                                getNumberOfShortestPaths(source));

                    }
                } else { // This edge is a tree edge

                    // dependency computations on tree edges
                    predesessors.get(target).addLast(source);

                    addToNumberOfShortestPaths(target,
                            getNumberOfShortestPaths(source));

                    stack.addFirst(target);

                    // managing bfs data for tree edges
                    bsa.setComponentMark(target, true);
                    queue.addLast(target);
                    setBfsNumber(target, getBfsNumber(source) + 1);

                }
            }
        }

        double modDependency;

        while (!stack.isEmpty()) {
            target = stack.removeFirst();

            // for each predesessor of target
            for (Iterator<Node> nodeIt = predesessors.get(target).iterator(); nodeIt
                    .hasNext();) {
                source = nodeIt.next();

                double pathsSource = getNumberOfShortestPaths(source);
                double pathsTarget = getNumberOfShortestPaths(target);
                double dependTarget = getDependency(target);

                modDependency = ((pathsSource / pathsTarget) * (1 + dependTarget));

                addToDependency(source, modDependency);

                // setting dependency for the edges in between
                for (Iterator<Edge> multiIt = graph.getEdges(source, target)
                        .iterator(); multiIt.hasNext();) {
                    // running through this loop for every multiedge
                    edge = multiIt.next();
                    setDependency(edge, modDependency);
                }
            }
        }

        removeDependencyTempData(nodes, edges);
    }

    /**
     * Generates the Parameters of the algorithm.
     */
    private void generateParameters() {
        BooleanParameter forNodesParam = new BooleanParameter(
                FOR_NODES_DEFAULT, FOR_NODES_NAME, FOR_NODES_DESCRIPTION);
        BooleanParameter forEdgesParam = new BooleanParameter(
                FOR_EDGES_DEFAULT, FOR_EDGES_NAME, FOR_EDGES_DESCRIPTION);
        BooleanParameter showParam = new BooleanParameter(
                SHOW_BETWEENNESS_DEFAULT, SHOW_BETWEENNESS_NAME,
                SHOW_BETWEENNESS_DESCRIPTION);
        BooleanParameter scaleParam = new BooleanParameter(
                SCALE_BETWEENNESS_DEFAULT, SCALE_BETWEENNESS_NAME,
                SCALE_BETWEENNESS_DESCRIPTION);
        DoubleParameter scaledSumParam = new DoubleParameter(
                SCALED_BETWEENNESS_SUM_DEFAULT, SCALED_BETWEENNESS_SUM_NAME,
                SCALED_BETWEENNESS_SUM_DESCRIPTION);

        parameters = new Parameter[NUMBER_OF_PARAMS];
        parameters[FOR_NODES_INDEX] = forNodesParam;
        parameters[FOR_EDGES_INDEX] = forEdgesParam;
        parameters[SHOW_BETWEENNESS_INDEX] = showParam;
        parameters[SCALE_BETWEENNESS_INDEX] = scaleParam;
        parameters[SCALED_BETWEENNESS_SUM_INDEX] = scaledSumParam;
    }

    /**
     * Generates a initial zero betweenness on nodes and edges.
     */
    private void initBetweenness() {
        Node tempNode;
        Edge tempEdge;

        // the betweenness of v is the sum of the dependencies of v from
        // all other nodes
        for (Iterator<Node> nodeIt = graph.getNodesIterator(); nodeIt.hasNext();) {
            tempNode = nodeIt.next();
            setBetweenness(tempNode, 0);
        }

        for (Iterator<Edge> edgeIt = graph.getEdgesIterator(); edgeIt.hasNext();) {
            tempEdge = edgeIt.next();
            setBetweenness(tempEdge, 0);
        }
    }

    /**
     * Initializes the data for the dependency-computations on a weakly
     * connected component and returns a HashMap which maps each node to the
     * list of its Predesessors in a run of the computeDependency method.
     * 
     * @param nodes
     *            the nodes of the component.
     * @param edges
     *            the edges of the component.
     * 
     * @return the HashMap of predesessors
     */
    private HashMap<Node, LinkedList<Node>> initDependencyTempData(
            Collection<Node> nodes, Collection<Edge> edges) {
        Node tempNode;
        Edge tempEdge;

        // init numbers of shortest paths to zero
        for (Iterator<Node> nodeIt = nodes.iterator(); nodeIt.hasNext();) {
            tempNode = nodeIt.next();
            setNumberOfShortestPaths(tempNode, 0);
        }

        // init Hashmap nodes -> LinkedList of Predesessors
        HashMap<Node, LinkedList<Node>> predesessors = new HashMap<Node, LinkedList<Node>>(
                (int) Math.ceil(nodes.size() / 0.9) + 5, 0.9f);

        for (Iterator<Node> nodeIt = nodes.iterator(); nodeIt.hasNext();) {
            tempNode = nodeIt.next();

            // a list of predesessors for each node
            predesessors.put(tempNode, new LinkedList<Node>());
        }

        // init dependency values to zero
        for (Iterator<Node> nodeIt = nodes.iterator(); nodeIt.hasNext();) {
            tempNode = nodeIt.next();
            setDependency(tempNode, 0.0);
        }

        for (Iterator<Edge> edgeIt = edges.iterator(); edgeIt.hasNext();) {
            tempEdge = edgeIt.next();
            setDependency(tempEdge, 0.0);
        }

        return predesessors;
    }

    /**
     * Computations to be done after the run of the algorithm.
     */
    private void postAlgorithm() {
        if (!forNodes) {
            removeNodeBetweenness();
        }

        if (!forEdges) {
            removeEdgeBetweenness();
        }

        if (scaleBetweenness) {
            skaleBetweenness();
        }

        roundBetweennessValues();

        if (showBetweenness) {
            showBetweenness();
        }
    }

    /**
     * Computations to be done before the run of the algorithm.
     */
    private void preAlgorithm() {
        removeAllAlgorithmData();
    }

    /**
     * Removes all data that could be created by the algorithm.
     */
    private void removeAllAlgorithmData() {
        removeBetweennessLabels();
        removeBetweenness();
        removeDependency();

        // removes Bfs number, shortest paths & component mark
        removeDependencyTempData();

    }

    /**
     * Removes the betweenness value of a given <code> GraphElement </code>.
     * 
     * @param e
     *            the element
     */
    private void removeBetweenness(GraphElement e) {
        try {
            e.removeAttribute(BETWEENNESS);
        } catch (AttributeNotFoundException anfe) {
        }
    }

    /**
     * Removes the betweenness labels of a given <code> GraphElement </code>.
     * 
     * @param e
     *            the element
     */
    private void removeBetweennessLabel(GraphElement e) {
        nsa.removeLabelAttribute(e, BETWEENNESS_LABEL);
    }

    /**
     * Removes the Bfs-number of a given <code> GraphElement </code>.
     * 
     * @param e
     *            the element
     */
    private void removeBfsNumber(GraphElement e) {
        try {
            e.removeAttribute(BFS_NUMBER);
        } catch (AttributeNotFoundException anfe) {
        }
    }

    /**
     * Removes the dependency value of the given <code> GraphElement </code>.
     * 
     * @param e
     *            the element
     */
    private void removeDependency(GraphElement e) {
        try {
            e.removeAttribute(DEPENDENCY);
        } catch (AttributeNotFoundException anfe) {
        }
    }

    /**
     * Removes the dependency values of a given weakly connected component.
     * 
     * @param nodes
     *            the nodes of the component
     * @param edges
     *            the edges of the same component
     */
    private void removeDependency(Collection<Node> nodes, Collection<Edge> edges) {
        Node tempNode;
        Edge tempEdge;

        for (Iterator<Node> nodeIt = nodes.iterator(); nodeIt.hasNext();) {
            tempNode = nodeIt.next();
            removeDependency(tempNode);
        }

        for (Iterator<Edge> edgeIt = edges.iterator(); edgeIt.hasNext();) {
            tempEdge = edgeIt.next();
            removeDependency(tempEdge);
        }
    }

    /**
     * Removes the dependency values of the graph.
     */
    private void removeDependency() {
        removeDependency(graph.getNodes(), graph.getEdges());
    }

    /**
     * Removes the temporary data from one run of the computeDependeny method
     * from a given <code> GraphElement </code>.
     * 
     * @param e
     *            the element
     */
    private void removeDependencyTempData(GraphElement e) {
        bsa.setComponentMark(e, false);
        removeBfsNumber(e);
        removeNumberOfShortestPaths(e);
    }

    /**
     * Removes the temporary data from one run of the computeDependeny method
     * from a givenweakly connected component.
     * 
     * @param nodes
     *            the nodes of the component
     * @param edges
     *            the edges of the same component
     */
    private void removeDependencyTempData(Collection<Node> nodes,
            Collection<Edge> edges) {
        Node tempNode;
        Edge tempEdge;

        for (Iterator<Node> nodeIt = nodes.iterator(); nodeIt.hasNext();) {
            tempNode = nodeIt.next();
            removeDependencyTempData(tempNode);
        }

        for (Iterator<Edge> edgeIt = edges.iterator(); edgeIt.hasNext();) {
            tempEdge = edgeIt.next();
            removeDependencyTempData(tempEdge);
        }
    }

    /**
     * Removes the temporary data from one run of the computeDependeny method
     * from the graph.
     */
    private void removeDependencyTempData() {
        removeDependencyTempData(graph.getNodes(), graph.getEdges());
    }

    /**
     * Removes the betweenness values from all edges.
     */
    private void removeEdgeBetweenness() {
        Edge tempEdge;

        for (Iterator<Edge> edgeIt = graph.getEdgesIterator(); edgeIt.hasNext();) {
            tempEdge = edgeIt.next();
            removeBetweenness(tempEdge);
        }
    }

    /**
     * Removes the betweenness values from all nodes.
     */
    private void removeNodeBetweenness() {
        Node tempNode;

        for (Iterator<Node> nodeIt = graph.getNodesIterator(); nodeIt.hasNext();) {
            tempNode = nodeIt.next();
            removeBetweenness(tempNode);
        }
    }

    /**
     * Removes the number of shortest paths from a <code> GraphElement </code>.
     * 
     * @param e
     *            the element
     */
    private void removeNumberOfShortestPaths(GraphElement e) {
        try {
            e.removeAttribute(NUMBER_OF_SHORTEST_PATHS);
        } catch (AttributeNotFoundException anfe) {
        }
    }

    /**
     * Rounds the betweenness values to seven digits right of the decimal point.
     * This method must be called after skaling the values but before creating
     * the labels.
     */
    private void roundBetweennessValues() {
        if (forNodes) {
            Node tempNode;

            for (Iterator<Node> nodeIt = graph.getNodesIterator(); nodeIt
                    .hasNext();) {
                tempNode = nodeIt.next();

                // set rounded betweenness
                setBetweenness(tempNode, nsa.round(getBetweenness(tempNode)));
            }
        }

        if (forEdges) {
            Edge tempEdge;

            for (Iterator<Edge> edgeIt = graph.getEdgesIterator(); edgeIt
                    .hasNext();) {
                tempEdge = edgeIt.next();

                // set rounded betweenness
                setBetweenness(tempEdge, nsa.round(getBetweenness(tempEdge)));
            }
        }
    }

    /**
     * Shows the betweenness value as <code> LabelAttribute </code> on the given
     * <code> GraphElement </code>.
     * 
     * @param e
     *            the element
     */
    private void showBetweenness(GraphElement e) {
        // nsa.setDoubleLabelAttribute(e,BETWEENNESS_LABEL,getBetweenness(e));
        nsa.removeLabelAttribute(e, BETWEENNESS_LABEL);

        LabelAttribute betweennessLabel = null;

        if (e instanceof Node) {
            betweennessLabel = new NodeLabelAttribute(BETWEENNESS_LABEL,
                    getBetweenness(e) + "");
        }

        if (e instanceof Edge) {
            betweennessLabel = new EdgeLabelAttribute(BETWEENNESS_LABEL,
                    getBetweenness(e) + "");
        }

        if ((e instanceof Node) || (e instanceof Edge)) {
            betweennessLabel.setTextcolor(new ColorAttribute("color",
                    Color.BLUE));
        }

        e.addAttribute(betweennessLabel, "");
    }

    /**
     * Shows the betweenness values as <code> LabelAttributes </code> on the
     * Graph.
     */
    private void showBetweenness() {
        Node tempNode;
        Edge tempEdge;

        if (forNodes) {
            for (Iterator<Node> nodeIt = graph.getNodesIterator(); nodeIt
                    .hasNext();) {
                tempNode = nodeIt.next();
                showBetweenness(tempNode);
            }
        }

        if (forEdges) {
            for (Iterator<Edge> edgeIt = graph.getEdgesIterator(); edgeIt
                    .hasNext();) {
                tempEdge = edgeIt.next();
                showBetweenness(tempEdge);
            }
        }
    }

    /**
     * Skales the betweenness values, so that the sum of the betweennesses of
     * all nodes (edges) equals the scaledBetweennessSum.
     */
    private void skaleBetweenness() {
        computeBetweennessSum();

        double unscaledBetweenness;
        double scaledBetweenness;

        if (forNodes) {
            Node tempNode;

            for (Iterator<Node> nodeIt = graph.getNodesIterator(); nodeIt
                    .hasNext();) {
                tempNode = nodeIt.next();
                unscaledBetweenness = getBetweenness(tempNode);
                scaledBetweenness = (unscaledBetweenness / sumNodeBetweenness)
                        * scaledBetweennessSum;
                setBetweenness(tempNode, scaledBetweenness);
            }
        }

        if (forEdges) {
            Edge tempEdge;

            for (Iterator<Edge> edgeIt = graph.getEdgesIterator(); edgeIt
                    .hasNext();) {
                tempEdge = edgeIt.next();
                unscaledBetweenness = getBetweenness(tempEdge);
                scaledBetweenness = (unscaledBetweenness / sumEdgeBetweenness)
                        * scaledBetweennessSum;
                setBetweenness(tempEdge, scaledBetweenness);
            }
        }
    }

    /**
     * Updates the betweenness values after a run of the computeDependencies
     * method to a given weakly connected component by adding the dependency of
     * each node to the temporary betweenness value.
     * 
     * @param nodes
     *            the nodes of the component
     * @param edges
     *            the edges of the same component
     */
    private void updateBetweenness(Collection<Node> nodes,
            Collection<Edge> edges) {
        Node tempNode;
        Edge tempEdge;

        // the betweenness of v is the sum of the dependencies of v from
        // all other nodes
        for (Iterator<Node> nodeIt = nodes.iterator(); nodeIt.hasNext();) {
            tempNode = nodeIt.next();
            addToBetweenness(tempNode, getDependency(tempNode));
        }

        for (Iterator<Edge> edgeIt = edges.iterator(); edgeIt.hasNext();) {
            tempEdge = edgeIt.next();
            addToBetweenness(tempEdge, getDependency(tempEdge));
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
