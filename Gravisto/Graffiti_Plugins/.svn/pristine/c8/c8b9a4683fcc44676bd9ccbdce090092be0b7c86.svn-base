// =============================================================================
//
//   TreeChecker.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================

package org.graffiti.plugins.algorithms.treedrawings;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.selection.Selection;

/**
 * Several methods to check certain properties of graphs.
 * 
 * @author Andreas Keilhauer
 */
public class GraphChecker {

    /**
     * Check that the given <code>graph</code> has no multiple edges (multiple
     * edges from one Node back to another one).
     * 
     * @param graph
     * @throws PreconditionException
     *             if the given <code>graph</code> has multiple edges
     */
    public synchronized static void checkMultipleEdges(Graph graph)
            throws PreconditionException {

        HashSet<Edge> multipleEdges = new HashSet<Edge>();
        HashMap<Node, HashMap<Node, Edge>> edgeMemory = new HashMap<Node, HashMap<Node, Edge>>();

        for (Edge currentEdge : graph.getEdges()) {
            HashMap<Node, Edge> fromEdgeMemory = edgeMemory.get(currentEdge
                    .getSource());
            if (fromEdgeMemory == null) {
                fromEdgeMemory = new HashMap<Node, Edge>();
                edgeMemory.put(currentEdge.getSource(), fromEdgeMemory);
            }

            Edge toEdgeEntry = fromEdgeMemory.get(currentEdge.getTarget());

            if (toEdgeEntry != null) {
                multipleEdges.add(toEdgeEntry);
                multipleEdges.add(currentEdge);
            } else {
                fromEdgeMemory.put(currentEdge.getTarget(), currentEdge);
            }
        }

        if (multipleEdges.size() > 0) {
            PreconditionException error = new PreconditionException();
            Selection selection = new Selection();
            selection.addAll(multipleEdges);

            error
                    .add(
                            "There are multiple edges in the graph. They will be selected.",
                            selection);
            throw error;
        }
    }

    /**
     * Check that the given <code>graph</code> has no self loops (edges from one
     * Node back to the same Node)
     * 
     * @param graph
     * @throws PreconditionException
     *             if the given <code>graph</code> has self loops.
     */
    public static synchronized void checkSelfLoops(Graph graph)
            throws PreconditionException {
        HashSet<Edge> selfLoops = new HashSet<Edge>();

        for (Edge currentEdge : graph.getEdges()) {
            if (currentEdge.getSource() == currentEdge.getTarget()) {
                selfLoops.add(currentEdge);
            }
        }

        if (selfLoops.size() > 0) {
            PreconditionException error = new PreconditionException();
            Selection selection = new Selection();
            selection.addAll(selfLoops);

            error
                    .add(
                            "There are self loops in the graph. They will be selected.",
                            selection);
            throw error;
        }

    }

    /**
     * Check if the given <code>graph</code> is a tree of a degree smaller than
     * <code>maxDegree</code>.
     * 
     * @param graph
     * @param maxDegree
     * @return the root Node
     * @throws PreconditionException
     *             if the given <code>graph</code> is not a tree or its degree
     *             is to big.
     */
    public synchronized static Node checkTree(Graph graph, int maxDegree)
            throws PreconditionException {

        PreconditionException errors = new PreconditionException();

        if ((graph == null) || (graph.getNumberOfNodes() <= 0)) {
            errors.add("The graph has no Nodes!");
            throw errors;
        }

        if ((graph.getNumberOfEdges() == 0) && (graph.getNumberOfNodes() >= 2)) {
            errors.add("This graph has no edges!");
            throw errors;
        }

        if (graph.isUndirected() && (graph.getNumberOfNodes() >= 2)) {
            errors.add("Please make this graph directed!");
            throw errors;
        }

        LinkedList<Node> rootList = new LinkedList<Node>();
        LinkedList<Node> nodesWithMoreThanOneEdgeList = new LinkedList<Node>();
        LinkedList<Node> nodesWithTooManyOutEdgesList = new LinkedList<Node>();

        Iterator<Node> nodesIt = graph.getNodesIterator();
        while (nodesIt.hasNext()) {

            Node n = nodesIt.next();
            if (n.getOutDegree() > maxDegree) {
                nodesWithTooManyOutEdgesList.add(n);
            }
            if (n.getInDegree() == 0) {
                rootList.add(n);
            } else if (n.getInDegree() > 1) {
                nodesWithMoreThanOneEdgeList.add(n);
            }
        }

        if (rootList.size() == 0) {
            errors.add("Hints: This graph has no root.");
        } else if (rootList.size() == 1) {
            Selection selection = new Selection();
            selection.addAll(rootList);
            if (rootList.getFirst().getOutDegree() == 0
                    && graph.getNumberOfNodes() > 1) {
                errors
                        .add(
                                "Hint: The graph is not weakly connected. Relevant node(s) will be selected.",
                                selection);
            }
        } else if (rootList.size() > 1) {
            Selection selection = new Selection();
            selection.addAll(rootList);
            errors
                    .add(
                            "Hints: There are more than one possible root-nodes. They will be selected.",
                            selection);
        }

        if (nodesWithMoreThanOneEdgeList.size() > 0) {
            Selection selection = new Selection();
            selection.addAll(nodesWithMoreThanOneEdgeList);
            errors
                    .add(
                            "Hints: There are node(s) with more than one incoming edges. They will be selected.",
                            selection);
        }

        if (nodesWithTooManyOutEdgesList.size() > 0) {
            Selection selection = new Selection();
            selection.addAll(nodesWithTooManyOutEdgesList);
            errors
                    .add(
                            "Hints: There are nodes with too many outgoing edges. They will be selected.",
                            selection);
        }

        if (!errors.isEmpty())
            throw errors;

        return rootList.getFirst();

    }

    /**
     * Check if the given Graph is a DAG. In order to do that we perform a
     * simple bfs-based topoligical sort. We also remember the order of the
     * nodes, because we might need it for the duplicate strategy
     * "SUBTREE_FOR_EACH_DUPLICATE"...
     * 
     * @param graph
     * @return the nodes in the given <code>graph</code> in topological order.
     * @throws PreconditionException
     */
    public synchronized static LinkedList<Node> checkDAG(Graph graph)
            throws PreconditionException {
        LinkedList<Node> nodesInTopSortOrder = new LinkedList<Node>();

        // make a simplified copy of this graph...
        HashMap<Node, Integer> nodeToInDegreeMapping = new HashMap<Node, Integer>();
        for (Node currentNode : graph.getNodes()) {
            nodeToInDegreeMapping.put(currentNode, currentNode.getInDegree());
        }

        Collection<Node> sourceCandidates = nodeToInDegreeMapping.keySet();

        while (nodeToInDegreeMapping.size() > 0) {
            Node sourceNode = null;

            for (Node currentNode : sourceCandidates) {
                if (nodeToInDegreeMapping.get(currentNode) == 0) {
                    sourceCandidates.remove(currentNode);
                    sourceNode = currentNode;
                    break;
                }
            }

            if (sourceNode == null)
                throw new PreconditionException(
                        "This graph is not a directed acyclic graph.");

            nodesInTopSortOrder.addLast(sourceNode);

            for (Edge currentEdge : sourceNode.getAllOutEdges()) {
                Node currentTargetNode = currentEdge.getTarget();
                Integer targetNodeInDegree = nodeToInDegreeMapping
                        .get(currentTargetNode);
                if (targetNodeInDegree != null) {
                    nodeToInDegreeMapping.put(currentTargetNode,
                            targetNodeInDegree - 1);
                }
            }
        }

        return nodesInTopSortOrder;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
