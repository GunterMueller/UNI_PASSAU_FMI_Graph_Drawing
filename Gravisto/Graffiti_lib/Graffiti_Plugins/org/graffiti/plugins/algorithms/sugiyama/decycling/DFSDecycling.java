// =============================================================================
//
//   DFSDecycling.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DFSDecycling.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.decycling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Logger;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugins.algorithms.sugiyama.PhasePreconditionException;
import org.graffiti.plugins.algorithms.sugiyama.util.EdgeUtil;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * This class implements a method to decycle a DAG using depth-first-search.
 * <p>
 * All nodes get numbered according to DFS. After running DFS on the graph, all
 * backwards-edges get reversed to decycle the graph.
 * 
 * @author Ferdinand H&uuml;bner
 */
public class DFSDecycling extends AbstractAlgorithm implements
        DecyclingAlgorithm {

    /** The logger for this class. */
    private static final Logger logger = Logger.getLogger(DFSDecycling.class
            .getName());

    /** The algorithm's name */
    private final String ALGORITHM_NAME = "DFSDecycling";

    /**
     * This variable stores, whether the method <code>check()</code> has been
     * called or not.
     */
    private boolean checked = false;

    /** Counter for the DFS-numbers in the graph */
    private int dfsNum;

    /** Counter for the DFS-completion-number in the graph */
    private int compNum;

    /** Bean that stores the results of each phase */
    private SugiyamaData data;

    /**
     * This boolean controls, if only one possible DFS-tree is used. This
     * boolean will be set to <tt>true</tt>, if the user has selected a
     * <tt>Node</tt>. The algorithm assumes, that the user wants to start DFS
     * from the selected node
     */
    boolean singleSource;

    /**
     * Controls which "DFS-result" (depends on the Node DFS was started from) is
     * used. If this is set to <tt>true</tt>, the DFS-Result with the minimal
     * longest path is used. If this is set to <tt>false</tt>, the result that
     * produces the longest path is used! If there are multiple results that
     * create a minimal/maximal longest path, minimizeBackwardsEdges decides
     * which result is used
     */
    private boolean minimizeLongestPath = true;

    /**
     * Controls which result is chosen to actually decycle the graph. If this is
     * set to <tt>true</tt>, the result with the minimal number of
     * backwards-edges is used. If this is set to <tt>false</tt>, the result
     * with the maximal number of backwards-edges is used!
     */
    private boolean minimizeBackwardsEdges = true;

    /**
     * Default constructor for a <code>DFSDecycling</code>-Object.
     * 
     * Initialize the dfs-counter and completion-counter with 0
     * 
     */
    public DFSDecycling() {
        super();
        dfsNum = 0;
        compNum = 0;
    }

    /**
     * Getter-method to access the <code>SugiyamaData</code>-bean
     * 
     * @return Returns the attached <code>SugiyamaData</code>-bean
     */
    public SugiyamaData getData() {
        return this.data;
    }

    /**
     * Setter-method to store the <code>SugiyamaData</code>-bean
     * 
     * @param theData
     *            The <code>SugiyamaData</code>-bean
     */
    public void setData(SugiyamaData theData) {
        this.data = theData;
    }

    /**
     * Accessor for the name of this algorithm
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     * @return Returns the name of this algorithm
     */
    public String getName() {
        return ALGORITHM_NAME;
    }

    /**
     * This method checks if all preconditions for executing DFS are satisfied.
     * 
     * In this case, the algorithm needs the following preconditions:
     * <ul>
     * <li>The reference to the attached graph is not <code>null</code>
     * <li>A graph with at least one node
     * <li>A directed graph
     * </ul>
     * 
     * @throws PhasePreconditionException
     *             A <code>PhasePreconditionException</code> is thrown if the
     *             graph is null, empty or undirected
     */
    @Override
    public void check() throws PhasePreconditionException {
        if (graph == null)
            throw new PhasePreconditionException(
                    SugiyamaConstants.ERROR_GRAPH_IS_NULL);
        if (graph.getNumberOfNodes() <= 0)
            throw new PhasePreconditionException(
                    SugiyamaConstants.ERROR_GRAPH_IS_EMPTY);
        if (graph.isUndirected())
            throw new PhasePreconditionException(
                    SugiyamaConstants.ERROR_GRAPH_UNDIRECTED);

        checked = true;
    }

    /**
     * This method executes the DFSDecycling.
     * <p>
     * An arbitrary <code>Node</cody> of the <code>Graph</code> acts as a
     * starting node. From this node on, DFS is performed and all
     * <code>Nodes</code> are numbered according to their DFS-number.
     * <p>
     * After DFS has reached all <code>Nodes</code> in the <code>Graph</code>,
     * all <code>Edges</code> are tested. If an edge is a backwards-edge (i.e.
     * the DFS-number of the <code>edge's</code> target is less than the DFS-number of
     * the source), it gets reversed and colored red.
     * 
     */
    public void execute() {

        // Initialize and/or declare needed data-structures
        Iterator<Edge> edgeIterator;
        Edge temp;
        Node tmp;
        DFSResult best = null;

        // Has the graph been checked
        if (!checked)
            throw new RuntimeException(SugiyamaConstants.ERROR_PREC_NOT_CHECKED);

        graph.getListenerManager().transactionStarted(this);

        // Begin with a selected start-node if one has been selected
        if (data.getStartNode() != null) {
            singleSource = true;
            System.out.println("Starting with selected node");
            dfs(data.getStartNode());
            DFSResult res = new DFSResult();
            computeBackwardsEdges(res);
            computeLongestPath(res);
            best = res;
            // Try to find an optimal DFS-Result if no node has been selected:
            // Run DFS from every node in the graph, save the "result" in a
            // DFSResult and compute the longest path
        } else {
            singleSource = false;
            HashSet<DFSResult> results = new HashSet<DFSResult>();
            DFSResult res;

            Iterator<Node> nodeIter = graph.getNodesIterator();

            while (nodeIter.hasNext()) {
                tmp = nodeIter.next();
                dfs(tmp);
                res = new DFSResult();
                computeBackwardsEdges(res);
                computeLongestPath(res);
                results.add(res);
            }

            Iterator<DFSResult> resIter = results.iterator();
            DFSResult tempResult;
            // Find the "best" DFS-instance
            while (resIter.hasNext()) {
                tempResult = resIter.next();
                if (best != null) {

                    if (minimizeLongestPath) {
                        if (tempResult.getLongestPath() <= best
                                .getLongestPath()) {
                            if (minimizeBackwardsEdges) {
                                if (tempResult.backEdges.size() < best.backEdges
                                        .size()) {
                                    best = tempResult;
                                }
                            } else {
                                if (tempResult.backEdges.size() > best.backEdges
                                        .size()) {
                                    best = tempResult;
                                }
                            }
                        }
                    } else {
                        if (tempResult.getLongestPath() >= best
                                .getLongestPath()) {
                            if (minimizeBackwardsEdges) {
                                if (tempResult.backEdges.size() < best.backEdges
                                        .size()) {
                                    best = tempResult;
                                }
                            } else {
                                if (tempResult.backEdges.size() > best.backEdges
                                        .size()) {
                                    best = tempResult;
                                }
                            }
                        }
                    }
                } else {
                    best = tempResult;
                }
            }
        }

        // Reverse the backwards-edges
        edgeIterator = best.backEdges.iterator();
        while (edgeIterator.hasNext()) {
            temp = edgeIterator.next();
            try {
                temp.getAttribute(SugiyamaConstants.PATH_SUGIYAMA);
            } catch (AttributeNotFoundException afne) {
                temp.addAttribute(new HashMapAttribute(
                        SugiyamaConstants.PATH_SUGIYAMA), "");
            }
            try {
                temp.setBoolean(SugiyamaConstants.PATH_REVERSED, true);
            } catch (AttributeNotFoundException anfe) {
                temp.addBoolean(SugiyamaConstants.PATH_SUGIYAMA,
                        SugiyamaConstants.SUBPATH_REVERSED, true);
            }
            temp.reverse();
            data.getReversedEdges().add(temp);
        }

        // set the hasBeenDecycled-bit to the graph
        try {
            graph.setBoolean(SugiyamaConstants.PATH_HASBEENDECYCLED, true);
        } catch (AttributeNotFoundException anfe) {
            graph.addBoolean(SugiyamaConstants.PATH_SUGIYAMA,
                    SugiyamaConstants.SUBPATH_HASBEENDECYCLED, true);
        }
        graph.getListenerManager().transactionFinished(this);
        dfsNum = 0;
        compNum = 0;

    }

    /**
     * This method is used to compute the longest path in the graph that would
     * exist, if the graph would have been decycled using a specific start-node.
     * 
     * The graph gets sorted topologically first, after that, the longest path
     * can be computed in linear time.
     * 
     * @param res
     *            The result of DFS
     */
    private void computeLongestPath(DFSResult res) {

        Iterator<Node> nodeIter = graph.getNodesIterator();
        ArrayList<Node> topoNodes;
        Iterator<Edge> edgeIterator;
        Node tmp;

        // Reverse all back-edges first, otherwise we have a cyclic graph and
        // cannot do toposort
        edgeIterator = res.backEdges.iterator();
        while (edgeIterator.hasNext()) {
            edgeIterator.next().reverse();
        }

        TopoSort tSort = new TopoSort();
        tSort.setData(data);
        tSort.attach(graph);
        tSort.execute();

        edgeIterator = res.backEdges.iterator();

        nodeIter = graph.getNodesIterator();
        while (nodeIter.hasNext()) {
            tmp = nodeIter.next();
            try {
                tmp.setInteger(SugiyamaConstants.PATH_DIST, -1);
            } catch (AttributeNotFoundException anfe) {
                tmp.addInteger(SugiyamaConstants.PATH_SUGIYAMA,
                        SugiyamaConstants.SUBPATH_DIST, -1);
            }
            if (tmp.getInDegree() == 0) {
                tmp.setInteger(SugiyamaConstants.PATH_DIST, 0);
            }
        }
        while (edgeIterator.hasNext()) {
            edgeIterator.next().reverse();
        }

        // Sort the nodes according to their topological numbering
        topoNodes = new ArrayList<Node>(graph.getNodes());
        Collections.sort(topoNodes, new TopologicalComparator());

        int cur_dist;
        int neighbor_dist;

        // calculate the longest path
        for (int i = 0; i < topoNodes.size(); i++) {

            nodeIter = topoNodes.get(i).getOutNeighborsIterator();
            cur_dist = topoNodes.get(i).getInteger(SugiyamaConstants.PATH_DIST);
            while (nodeIter.hasNext()) {
                tmp = nodeIter.next();
                neighbor_dist = tmp.getInteger(SugiyamaConstants.PATH_DIST);
                if (neighbor_dist < cur_dist + 1) {
                    tmp.setInteger(SugiyamaConstants.PATH_DIST, cur_dist + 1);
                    if (res.getLongestPath() < cur_dist + 1) {
                        res.setLongestPath(cur_dist + 1);
                    }
                }
            }
        }
    }

    /**
     * Find all backwards-edges that this DFS-Result would produce
     * 
     * @param res
     *            The DFS-Result
     */
    private void computeBackwardsEdges(DFSResult res) {

        Iterator<Edge> edgeIterator = graph.getEdgesIterator();

        int source_dfsNum;
        int target_dfsNum;
        int source_compNum;
        int target_compNum;
        Edge temp;

        while (edgeIterator.hasNext()) {
            temp = edgeIterator.next();

            try {
                source_dfsNum = temp.getSource().getInteger(
                        SugiyamaConstants.PATH_DFSNUM);
                target_dfsNum = temp.getTarget().getInteger(
                        SugiyamaConstants.PATH_DFSNUM);
                source_compNum = temp.getSource().getInteger(
                        SugiyamaConstants.PATH_COMPNUM);
                target_compNum = temp.getTarget().getInteger(
                        SugiyamaConstants.PATH_COMPNUM);

                if (source_dfsNum > target_dfsNum
                        && source_compNum < target_compNum) {
                    res.backEdges.add(temp);
                }

            } catch (AttributeNotFoundException anfe) {
                logger.warning("Cannot check for backwards-edge. At least one"
                        + " node is missing a needed attribute.");
            }
        }
    }

    /**
     * Run DFS on the <code>Node</code> start.
     * 
     * @param start
     *            The <code>Node</code> that is the start-node of DFS
     */
    private void dfs(Node start) {

        dfsNum = 0;
        compNum = 0;
        HashSet<Node> visited = new HashSet<Node>();
        Iterator<Node> nodeIter;

        try {
            start.getAttribute(SugiyamaConstants.PATH_SUGIYAMA);
        } catch (AttributeNotFoundException anfe) {
            start.addAttribute(new HashMapAttribute(
                    SugiyamaConstants.PATH_SUGIYAMA), "");
        }
        try {
            start.setInteger(SugiyamaConstants.PATH_DFSNUM, dfsNum);
        } catch (AttributeNotFoundException anfe) {
            start.addInteger(SugiyamaConstants.PATH_SUGIYAMA,
                    SugiyamaConstants.SUBPATH_DFSNUM, dfsNum);
        }
        dfsNum++;
        visited.add(start);
        processNode(start, visited);
        nodeIter = graph.getNodesIterator();
        while (nodeIter.hasNext()) {
            dfsIteration(nodeIter.next(), visited);
        }

    }

    /**
     * This method starts a dfs-sub-iteration from the node
     * 
     * @param start
     *            The node to start a dfs-subiteration from
     * @param visited
     *            Hashset, that contains all visited nodes
     */
    public void dfsIteration(Node start, Set<Node> visited) {

        if (!visited.contains(start)) {
            try {
                start.getAttribute(SugiyamaConstants.PATH_SUGIYAMA);
            } catch (AttributeNotFoundException anfe) {
                start.addAttribute(new HashMapAttribute(
                        SugiyamaConstants.PATH_SUGIYAMA), "");
            }
            try {
                start.setInteger(SugiyamaConstants.PATH_DFSNUM, dfsNum);
            } catch (AttributeNotFoundException anfe) {
                start.addInteger(SugiyamaConstants.PATH_SUGIYAMA,
                        SugiyamaConstants.SUBPATH_DFSNUM, dfsNum);
            }
            dfsNum++;
            visited.add(start);
            processNode(start, visited);
        }

    }

    /**
     * This method processes all neighbors of a given <code>Node</code> for
     * recursive DFS.
     * 
     * @param n
     *            The node to process
     * @param visited
     *            <code>HashSet</code> that contains already visited nodes
     */
    private void processNode(Node n, Set<Node> visited) {

        Node tmp;
        Collection<Node> neighbors = n.getOutNeighbors();
        Iterator<Node> neighborIterator = neighbors.iterator();

        while (neighborIterator.hasNext()) {
            tmp = neighborIterator.next();
            try {
                tmp.getAttribute(SugiyamaConstants.PATH_SUGIYAMA);
            } catch (AttributeNotFoundException anfe) {
                tmp.addAttribute(new HashMapAttribute(
                        SugiyamaConstants.PATH_SUGIYAMA), "");
            }
            if (!visited.contains(tmp)) {
                visited.add(tmp);
                try {
                    tmp.setInteger(SugiyamaConstants.PATH_DFSNUM, dfsNum);
                } catch (AttributeNotFoundException anfe) {
                    tmp.addInteger(SugiyamaConstants.PATH_SUGIYAMA,
                            SugiyamaConstants.SUBPATH_DFSNUM, dfsNum);
                }
                dfsNum++;
                processNode(tmp, visited);
            }
        }
        try {
            n.setInteger(SugiyamaConstants.PATH_COMPNUM, compNum);
        } catch (AttributeNotFoundException anfe) {
            try {
                n.addInteger(SugiyamaConstants.PATH_SUGIYAMA,
                        SugiyamaConstants.SUBPATH_COMPNUM, compNum);
            } catch (AttributeNotFoundException yaanfe) {
                n.addAttribute(new HashMapAttribute(
                        SugiyamaConstants.PATH_SUGIYAMA), "");
                n.addInteger(SugiyamaConstants.PATH_SUGIYAMA,
                        SugiyamaConstants.SUBPATH_COMPNUM, compNum);
            }
        }
        compNum++;

    }

    /**
     * Reverse all edges that have been reversed to break cycles in the graph
     */
    public void undo() {

        data.setGraph(this.graph);
        EdgeUtil.reverseBendedEdge(data);

    }

    /**
     * This private class provides an easy data-structure for different
     * DFS-searches in the graph - It stores the longest path that this DFS
     * would produce as well as the backwards-edges
     * 
     * @author Ferdinand H&uuml;bner
     */
    private class DFSResult {

        private int longestPath;

        private Collection<Edge> backEdges;

        public DFSResult() {
            this.longestPath = 0;
            this.backEdges = new LinkedList<Edge>();
        }

        public void setLongestPath(int n) {
            this.longestPath = n;
        }

        public int getLongestPath() {
            return this.longestPath;
        }

    }

    /**
     * Reset the internal state of the algorithm
     */
    @Override
    public void reset() {
        super.reset();
        checked = false;
        dfsNum = 0;
        compNum = 0;
        singleSource = false;
        graph = null;
    }

    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        SelectionParameter sel = new SelectionParameter("startNode",
                "DFS will start with selected node");
        BooleanParameter b1 = new BooleanParameter(true,
                "Minimize Longest path",
                "Minimize the longest path in the graph");
        BooleanParameter b2 = new BooleanParameter(true,
                "Minimize backwards edges",
                "Minimize the backwards-edges in the graph");

        this.parameters = new Parameter[] { sel, b1, b2 };
        return this.parameters;
    }

    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        minimizeLongestPath = ((BooleanParameter) params[1]).getValue();
        minimizeBackwardsEdges = ((BooleanParameter) params[2]).getValue();
        System.out.println("minimize longest path: " + minimizeLongestPath);
        System.out.println("minimize backwards: " + minimizeBackwardsEdges);
    }

    public boolean supportsBigNodes() {
        return true;
    }

    public boolean supportsConstraints() {
        return true;
    }

    public boolean supportsAlgorithmType(String algorithmType) {
        return algorithmType
                .equals(SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA)
                || algorithmType
                        .equals(SugiyamaConstants.PARAM_RADIAL_SUGIYAMA);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
