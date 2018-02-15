// =============================================================================
//
//   SCCDecycling.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.decycling;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.sugiyama.PhasePreconditionException;
import org.graffiti.plugins.algorithms.sugiyama.util.EdgeUtil;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * This class implements an algorithm to decycle a directed graph by locating
 * its strongly-connected-components (<code>SCC</code>).
 * <p>
 * One edge in each <code>SCC</code> will be temporarily removed and meanwhile
 * stored in <code>SugiyamaData</code>. They will be inserted after the
 * <code>leveling</code> phase.
 * 
 * @author scheu
 * @version $Revision$ $Date$
 */
public class SCCDecycling extends AbstractAlgorithm implements
        DecyclingAlgorithm {
    /** The algorithm's name */
    private final String ALGORITHM_NAME = "SCCDecycling";

    /** Bean that stores the deleted edges of this phase */
    private SugiyamaData data;

    /**
     * Boolean indicating whether the method <code>check()</code> has been
     * called
     */
    private boolean checked;

    /** Temporary container for located <code>SCC</code>'s */
    private ArrayList<SCC> sccList;

    /**
     * Parameter that stores the choice of the priority list for locating the
     * <code>bestEdge</code> for temporary removal.
     */
    private StringSelectionParameter prioritySel;

    /**
     * Class constructor.
     */
    public SCCDecycling() {
        checked = false;
        sccList = new ArrayList<SCC>();
    }

    /**
     * Accessor for the name of this algorithm
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     * @return Returns the name of this algorithm
     */
    public String getName() {
        return this.ALGORITHM_NAME;
    }

    /*
     * @see org.graffiti.plugins.algorithms.sugiyama.SugiyamaAlgorithm#getData()
     */
    public SugiyamaData getData() {
        return data;
    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.sugiyama.SugiyamaAlgorithm#setData(org
     * .graffiti.plugins.algorithms.sugiyama.util.SugiyamaData)
     */
    public void setData(SugiyamaData data) {
        this.data = data;
    }

    /**
     * This method checks if all preconditions for executing SCC are satisfied.
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
    public void check() throws PreconditionException {
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
     * Executes the <code>SCCDecycling</code> algorithm. An <code>sccRun</code>
     * on the complete <code>graph</code> will be triggered to check for any
     * <code>SCC</code>'s. After having collected all <code>SCC</code>s in the
     * graph, one edge will be temporarily deleted in each <code>SCC</code> with
     * the result that there won't be any cycles left and the graph is acyclic.
     */
    public void execute() {
        Node startNode;

        if (!checked)
            throw new RuntimeException(SugiyamaConstants.ERROR_PREC_NOT_CHECKED);

        graph.getListenerManager().transactionStarted(this);

        if (data.getStartNode() != null) {
            startNode = data.getStartNode();
        } else {
            startNode = graph.getNodes().get(0);
        }

        // Check for SCC
        new SCCRun(startNode, new HashSet<Node>(graph.getNodes()),
                new HashSet<Edge>(graph.getEdges()));

        while (!sccList.isEmpty()) {
            SCC tmpSCC = sccList.remove(0);
            tmpSCC.decycle();
        }

        // set the hasBeenDecycled-bit to the graph
        try {
            graph.setBoolean(SugiyamaConstants.PATH_HASBEENDECYCLED, true);
        } catch (AttributeNotFoundException anfe) {
            graph.addBoolean(SugiyamaConstants.PATH_SUGIYAMA,
                    SugiyamaConstants.SUBPATH_HASBEENDECYCLED, true);
        }
        graph.getListenerManager().transactionFinished(this);
    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.sugiyama.decycling.DecyclingAlgorithm
     * #undo()
     */
    public void undo() {
        data.setGraph(this.graph);
        EdgeUtil.reverseBendedEdge(data);
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        super.reset();
        checked = false;
        sccList.clear();

    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#getParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        String[] priorities = new String[] { SugiyamaConstants.PRIORITY1,
                SugiyamaConstants.PRIORITY2, SugiyamaConstants.PRIORITY3,
                SugiyamaConstants.PRIORITY4, SugiyamaConstants.PRIORITY5 };
        StringSelectionParameter prioritySel = new StringSelectionParameter(
                priorities,
                "Edge locating",
                "Set the priority list, by which you want to locate "
                        + "the edge that will be temporarily removed in each circle");
        prioritySel.setValue(priorities[0]);

        this.parameters = new Parameter[] { prioritySel };
        return this.parameters;
    }

    /*
     * @see
     * org.graffiti.plugin.algorithm.Algorithm#setParameters(org.graffiti.plugin
     * .parameter.Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        prioritySel = (StringSelectionParameter) params[0];
    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.sugiyama.SugiyamaAlgorithm#supportsBigNodes
     * ()
     */
    public boolean supportsBigNodes() {
        return false;
    }

    /*
     * @seeorg.graffiti.plugins.algorithms.sugiyama.SugiyamaAlgorithm#
     * supportsConstraints()
     */
    public boolean supportsConstraints() {
        return false;
    }

    public boolean supportsAlgorithmType(String algorithmType) {
        return algorithmType
                .equals(SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA);
    }

    /**
     * This private class represents a <code>SCC</code>. It knows its nodes, its
     * edges and the <code>bestEdge</code> that will be temporarily deleted from
     * this <code>SCC</code> in order to break the cycle.
     * 
     * @author scheu
     * @version $Revision$ $Date$
     */
    private class SCC {
        // Nodes that belong to this SCC
        private HashSet<Node> sccNodes;

        // Edges that belong to this SCC
        private HashSet<Edge> sccEdges;

        // Edge that fits best to decycle the SCC
        private Edge bestEdge;

        /**
         * Class constructor.
         * 
         * @param nodes
         *            this scc's nodes
         * @param edges
         *            this scc's edges
         */
        public SCC(HashSet<Node> nodes, HashSet<Edge> edges) {
            sccNodes = nodes;
            sccEdges = edges;
        }

        /**
         * This method decycles this <code>SCC</code> by removing the given
         * <code>bestEdge</code>.
         * <p>
         * After this edge is removed the remaining <code>SCC</code> has to be
         * checked for a cycle again. Therefore a new <code>sccRun</code> is
         * triggered.
         */
        private void decycle() {
            bestEdge = getBestEdge();
            Node newStart = bestEdge.getTarget();

            sccEdges.remove(bestEdge);
            data.getDeletedEdges().add(bestEdge);
            graph.deleteEdge(bestEdge);

            // Start a new sccRun with a smaller sccEdges set reduced by one
            // edge (bestEdge has been removed)
            new SCCRun(newStart, new HashSet<Node>(sccNodes),
                    new HashSet<Edge>(sccEdges));
        }// decycle

        /**
         * This method locates the <code>bestEdge</code> that will be
         * temporarily remove from this <code>SCC</code>.
         * <p>
         * Based on the chosen priority list, the minimal out-degree of all
         * source-nodes, the minimal in-degree of all target-nodes, the maximal
         * in-degree of all source-nodes and the maximal out-degree of all
         * target-nodes within this <code>SCC</code> will be considered.
         * 
         * @return <code>bestEdge</code> to remove
         */
        private Edge getBestEdge() {
            Edge e = sccEdges.iterator().next();
            HashSet<Edge> tmpEdges = new HashSet<Edge>(sccEdges);
            tmpEdges.addAll(sccEdges);

            if (prioritySel.getSelectedValue().equals(
                    SugiyamaConstants.PRIORITY1))
                return maxOutDegTarget(
                        maxInDegSource(minInDegTarget(minOutDegSource(tmpEdges))))
                        .iterator().next();
            else if (prioritySel.getSelectedValue().equals(
                    SugiyamaConstants.PRIORITY2))
                return maxInDegSource(
                        maxOutDegTarget(minInDegTarget(minOutDegSource(tmpEdges))))
                        .iterator().next();
            else if (prioritySel.getSelectedValue().equals(
                    SugiyamaConstants.PRIORITY3))
                return maxOutDegTarget(
                        maxInDegSource(minOutDegSource(minInDegTarget(tmpEdges))))
                        .iterator().next();
            else if (prioritySel.getSelectedValue().equals(
                    SugiyamaConstants.PRIORITY4))
                return maxInDegSource(
                        maxOutDegTarget(minOutDegSource(minInDegTarget(tmpEdges))))
                        .iterator().next();
            else if (prioritySel.getSelectedValue().equals(
                    SugiyamaConstants.PRIORITY5)) {
                int upper = tmpEdges.size();
                int lower = 0;

                Random rand = new java.security.SecureRandom();
                // inclusive lower, exclusive upper
                int randNum = rand.nextInt(upper - lower) + lower;
                int i = 0;

                for (Edge eRand : sccEdges) {
                    if (i == randNum)
                        return eRand;
                    i++;
                }
            }

            return e;
        }

        /**
         * This method locates and collects all edges which have the minimal
         * out-degree of their source-node. They are return in a new edge set.
         * 
         * @param edges
         *            this edge set to be worked on
         * @return edges with minimal out-degree of their source-node
         */
        private HashSet<Edge> minOutDegSource(HashSet<Edge> edges) {
            int outDegSource;
            int minOutDegSource = Integer.MAX_VALUE;
            HashSet<Edge> goodEdges = new HashSet<Edge>(edges);
            goodEdges.addAll(edges);

            for (Edge e : edges) {
                outDegSource = e.getSource().getOutDegree();
                if (outDegSource < minOutDegSource) {
                    minOutDegSource = outDegSource;
                    // Found a better edge
                    goodEdges.clear();
                    goodEdges.add(e);
                } else if (outDegSource <= minOutDegSource) {
                    goodEdges.add(e);
                }
            }

            return goodEdges;
        }

        /**
         * This method locates and collects all edges which have the minimal
         * in-degree of their target-node. They are return in a new edge set.
         * 
         * @param edges
         *            this edge set to be worked on
         * @return edges with minimal in-degree of their target-node
         */
        private HashSet<Edge> minInDegTarget(HashSet<Edge> edges) {
            HashSet<Edge> goodEdges = new HashSet<Edge>(edges);
            goodEdges.addAll(edges);

            int inDegTarget;
            int minInDegTarget = Integer.MAX_VALUE;

            for (Edge e : edges) {
                inDegTarget = e.getTarget().getInDegree();
                if (inDegTarget < minInDegTarget) {
                    minInDegTarget = inDegTarget;
                    goodEdges.clear();
                    goodEdges.add(e);
                } else if (inDegTarget <= minInDegTarget) {
                    goodEdges.add(e);
                }
            }

            return goodEdges;
        }

        /**
         * This method locates and collects all edges which have the maximal
         * in-degree of their source-node. They are return in a new edge set.
         * 
         * @param edges
         *            this edge set to be worked on
         * @return edges with maximal in-degree of their source-node
         */
        private HashSet<Edge> maxInDegSource(HashSet<Edge> edges) {
            int inDegSource;
            int maxInDegSource = 0;
            HashSet<Edge> goodEdges = new HashSet<Edge>(edges);
            goodEdges.addAll(edges);

            for (Edge e : edges) {
                inDegSource = e.getSource().getInDegree();
                if (inDegSource > maxInDegSource) {
                    maxInDegSource = inDegSource;
                    goodEdges.clear();
                    goodEdges.add(e);
                } else if (inDegSource >= maxInDegSource) {
                    goodEdges.add(e);
                }
            }

            return goodEdges;
        }

        /**
         * This method locates and collects all edges which have the maximal
         * out-degree of their target-node. They are return in a new edge set.
         * 
         * @param edges
         *            this edge set to be worked on
         * @return edges with maximal out-degree of their target-node
         */
        private HashSet<Edge> maxOutDegTarget(HashSet<Edge> edges) {
            int outDegTarget;
            int maxOutDegTarget = 0;
            HashSet<Edge> goodEdges = new HashSet<Edge>(edges);
            goodEdges.addAll(edges);
            for (Edge e : edges) {
                outDegTarget = e.getTarget().getOutDegree();
                if (outDegTarget > maxOutDegTarget) {
                    maxOutDegTarget = outDegTarget;
                    goodEdges.clear();
                    goodEdges.add(e);
                } else if (outDegTarget >= maxOutDegTarget) {
                    goodEdges.add(e);
                }
            }

            return goodEdges;
        }
    }

    /**
     * This private class runs a <code>depth-first-search</code> (dfs) for two
     * times to determine the <code>SCC</code>s of this graph.
     * <p>
     * The method <code>dfs1</code> will compute a completion number
     * <code>compNum</code> for each node. This is needed for the second run.
     * The method <code>dfs2</code> calls its node visiting method by decreasing
     * <code>compNum</code> order. This way, the nodes of each dfs-tree in the
     * second run belong to the same <code>SCC</code>.
     * 
     * @author scheu
     * @version $Revision$ $Date$
     */
    private class SCCRun {
        // Dfs number counter for newly-discovered nodes
        private int dfsNum;

        // Completion number counter for processed nodes
        private int compNum;

        // Contains nodes with increasing compNum. Highest compNum at the end
        private ArrayList<Node> compNumList;

        // Contains all visited nodes in first dfs run
        private HashSet<Node> dfs1Nodes;

        // Contains all visited nodes in second dfs run
        private HashSet<Node> dfs2Nodes;

        // Temporarily contains all nodes that belong to the same SCC
        private HashSet<Node> sccNodes;

        // Temporarily contains all edges that belong to the same SCC
        private HashSet<Edge> sccEdges;

        // Contains all valid nodes of the graph for this SCCRun
        private HashSet<Node> validNodes;

        // Contains all valid edges of the graph for this SCCRun
        private HashSet<Edge> validEdges;

        /**
         * Class constructor.
         * 
         * @param start
         *            this start node
         * @param nodes
         *            valid nodes for this SCCRun
         * @param edges
         *            valid edges for this SCCRun
         */
        public SCCRun(Node start, HashSet<Node> nodes, HashSet<Edge> edges) {
            dfsNum = 0;
            compNum = 0;
            compNumList = new ArrayList<Node>();
            dfs1Nodes = new HashSet<Node>();
            dfs2Nodes = new HashSet<Node>();
            sccNodes = new HashSet<Node>();
            sccEdges = new HashSet<Edge>();
            validNodes = nodes;
            validEdges = edges;
            sccRun(start);
        }

        /**
         * This method runs a dfs for two times to determine the
         * <code>SCC</code>s of this graph. The method <code>dfs1</code> will
         * compute a <code>compNum</code> for each node. This is needed by the
         * second run. The method <code>dfs2</code> calls its node visiting
         * method by decreasing <code>compNum</code> order. This way, the nodes
         * of each dfs-tree in the second run belong to the same
         * <code>SCC</code>.
         * 
         * @param node
         *            this start node
         */
        private void sccRun(Node node) {
            if (validNodes.contains(node)) {
                // First run of dfs to get compNumList
                dfs1(node);

                // Second run of dfs on transponent graph to determine the SCC's
                // Don't process trivial scc's (at least two nodes)
                if (compNumList.size() > 1) {
                    dfs2();
                }
            }
        }

        /**
         * First dfs run starting at the given <code>node</code>. It visits all
         * <code>validNodes</code>.
         * 
         * @param node
         *            this start node
         */
        private void dfs1(Node node) {
            dfs1Visit(node);

            // Process left non-connected nodes
            if (dfs1Nodes.size() != validNodes.size()) {
                Iterator<Node> nIt = validNodes.iterator();
                while (nIt.hasNext()) {
                    Node n = nIt.next();
                    if (!dfs1Nodes.contains(n)) {
                        // Node n is root of a new dfs-tree
                        dfs1Visit(n);
                    }
                }
            }
        }// dfs1

        /**
         * Second dfs run. Calls <code>dfs2Visit</code> starting with highest
         * <code>compNum</code> node.
         * <p>
         * Whenever there are no more reachable neighbors from this start node
         * in <code>dfs2Visit</code>, the algorithm returns to <code>dfs2</code>
         * . The nodes that have been processed so far belong to the same
         * dfs-tree and therefore to the same discrete <code>SCC</code>. This
         * <code>SCC</code> is added to the <code>sccList</code> in order to
         * decycle it later on.
         */
        private void dfs2() {
            // Run dfs on transponent graph with decreasing compNum in loop
            for (int i = compNumList.size(); i > 0; i--) {
                // Catch nodes of each dfs-tree if there are any
                // Those are the nodes of a separate SCC in the graph
                if (!sccNodes.isEmpty()) {
                    if (sccNodes.size() > 1) {
                        // Find valid sccEdges
                        Iterator<Node> nIt = sccNodes.iterator();
                        while (nIt.hasNext()) {
                            Node n = nIt.next();
                            Iterator<Edge> eIt = n
                                    .getDirectedOutEdgesIterator();
                            while (eIt.hasNext()) {
                                Edge e = eIt.next();
                                if (validEdges.contains(e)) {
                                    Node target = e.getTarget();

                                    if (sccNodes.contains(target)) {
                                        sccEdges.add(e);
                                    }
                                }
                            }
                        }

                        // Found a SCC
                        sccList.add(new SCC(new HashSet<Node>(sccNodes),
                                new HashSet<Edge>(sccEdges)));
                    }
                    // Clear to be empty for next separate scc
                    sccNodes.clear();
                    sccEdges.clear();
                }

                Node tmpNode = compNumList.get(i - 1);

                if (!dfs2Nodes.contains(tmpNode)) {
                    dfs2Visit(tmpNode);
                }
            }
        }

        /**
         * This method processes all neighbors of a node recursively. It assigns
         * a <code>dfsNum</code> to each unvisited node when first discovered
         * and its <code>compNum</code> when no more unvisited neighbors of this
         * node exist. When assigning the <code>compNum</code> to a node, it
         * also gets added to the <code>compNumList</code> for the second dfs
         * run.
         * 
         * @param node
         *            the node to process
         */
        private void dfs1Visit(Node node) {
            // Set dfsNum at this node
            try {
                node.setInteger(SugiyamaConstants.PATH_DFSNUM, dfsNum);
            } catch (AttributeNotFoundException anfe) {
                node.addInteger(SugiyamaConstants.PATH_SUGIYAMA,
                        SugiyamaConstants.SUBPATH_DFSNUM, dfsNum);
            }

            dfs1Nodes.add(node);

            // Increase dfsNum for next node
            dfsNum++;

            // Process all valid unvisited out-neighbor nodes of this node
            Iterator<Node> neighborIt = node.getOutNeighborsIterator();
            while (neighborIt.hasNext()) {
                Node tmpNode = neighborIt.next();
                if (validNodes.contains(tmpNode)
                        && !dfs1Nodes.contains(tmpNode)) {
                    dfs1Visit(tmpNode);
                }
            }

            // Set compNum at this node
            // Node is processed
            try {
                node.setInteger(SugiyamaConstants.PATH_COMPNUM, compNum);
            } catch (AttributeNotFoundException anfe) {
                node.addInteger(SugiyamaConstants.PATH_SUGIYAMA,
                        SugiyamaConstants.SUBPATH_COMPNUM, compNum);
            }

            compNumList.add(node);
            // Increase compNum for next node
            compNum++;

        }// dfs1Visit

        /**
         * This method processes all neighbors of a node recursively. It
         * collects all processed nodes in <code>sccNodes</code>.
         * 
         * @param node
         *            node to process
         */
        private void dfs2Visit(Node node) {
            dfs2Nodes.add(node);
            sccNodes.add(node);

            // Process all valid unvisited in-neighbor nodes of this node
            Iterator<Node> neighborIt = node.getInNeighborsIterator();
            while (neighborIt.hasNext()) {
                Node tmpNode = neighborIt.next();
                if (validNodes.contains(tmpNode)
                        && !dfs2Nodes.contains(tmpNode)) {
                    // Found valid unvisited neighbor node
                    dfs2Visit(tmpNode);
                }
            }
        }
    }// SCCRun
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
