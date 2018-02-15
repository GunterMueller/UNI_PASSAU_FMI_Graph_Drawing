// =============================================================================
//
//   GraphEditing.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: CoreGraphEditing.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.selection.Selection;

/**
 * This class offers some static methods for graph editing.
 * 
 * @author Marek Piorkowski
 * @version $Revision: 5767 $ $Date: 2010-04-27 18:37:22 +0200 (Di, 27 Apr 2010)
 *          $
 */
public class CoreGraphEditing {
    private CoreGraphEditing() {
    }

    /**
     * Removes isolated nodes.
     * 
     * @param nodes
     *            The isolated nodes from these collection will be removed.
     */
    public static void removeIsolatedNodes(Collection<Node> nodes) {
        // muss hier so umst�ndlich sein, sonst kann ne nullpointerexception
        // kommen (bug?)
        LinkedList<Node> nodesToDelete = new LinkedList<Node>();
        for (Node node : nodes) {
            if (node.getEdges().isEmpty()) {
                nodesToDelete.add(node);
            }
        }
        for (Node node : nodesToDelete) {
            node.getGraph().deleteNode(node);
        }
    }

    /**
     * Removes isolated nodes from the specified graph.
     * 
     * @param graph
     *            All isolated nodes will be reomeved from this graph.
     */
    public static void removeIsolatedNodes(Graph graph) {
        removeIsolatedNodes(graph.getNodes());
    }

    /**
     * Removes multiple edges and self loops from the specified graph.
     * 
     * @param graph
     *            All multiple edges and self loops will be removed from this
     *            graph.
     */
    public static void removeMultipleEdgesAndLoops(Graph graph) {
        Collection<Edge> edges = new LinkedList<Edge>();
        edges.addAll(graph.getEdges());
        Iterator<Edge> edgeIt = edges.iterator();
        while (edgeIt.hasNext()) {
            Edge e = edgeIt.next();

            if (e.getSource() == e.getTarget()) {
                graph.deleteEdge(e);
            } else {
                Collection<Edge> multiEdges = new LinkedList<Edge>();
                multiEdges.addAll(graph.getEdges(e.getSource(), e.getTarget()));
                if (edges.size() > 1) {
                    Iterator<Edge> multiEdgeIt = multiEdges.iterator();
                    multiEdgeIt.next();
                    while (multiEdgeIt.hasNext()) {
                        Edge multiEdge = multiEdgeIt.next();
                        graph.deleteEdge(multiEdge);
                    }
                }
            }
        }
    }

    /**
     * Removes multiple edges from the specified graph.
     * 
     * @param graph
     *            All multiple edges will be removed from this graph.
     */
    public static void removeMultipleEdges(Graph graph) {
        Collection<Edge> edges = new LinkedList<Edge>();
        edges.addAll(graph.getEdges());
        Iterator<Edge> edgeIt = edges.iterator();
        while (edgeIt.hasNext()) {
            Edge e = edgeIt.next();

            Collection<Edge> multiEdges = new LinkedList<Edge>();
            multiEdges.addAll(graph.getEdges(e.getSource(), e.getTarget()));
            if (edges.size() > 1) {
                Iterator<Edge> multiEdgeIt = multiEdges.iterator();
                multiEdgeIt.next();
                while (multiEdgeIt.hasNext()) {
                    Edge multiEdge = multiEdgeIt.next();
                    graph.deleteEdge(multiEdge);
                }
            }

        }
    }

    /**
     * Removes self loops.
     * 
     * @param edges
     *            All self loops in this collection will be removed.
     */
    public static Collection<Edge> removeSelfLoops(Collection<Edge> edges) {
        LinkedList<Edge> edgesToDelete = new LinkedList<Edge>();
        for (Edge edge : edges) {
            if (edge.getSource() == edge.getTarget()) {
                edgesToDelete.add(edge);
            }
        }
        for (Edge edge : edgesToDelete) {
            edge.getGraph().deleteEdge(edge);
        }
        return edgesToDelete;
    }

    /**
     * Removes self loops from the specified graph.
     * 
     * @param graph
     *            All self loops will be removed from this graph.
     */
    public static void removeSelfLoops(Graph graph) {
        removeSelfLoops(graph.getEdges());
    }

    /**
     * Returns a selection containing all graph elements of the specified graph.
     * 
     * @param graph
     *            This graph's elements will be selected.
     * @return A selection containing all graph elements of the specified graph.
     */
    public static Selection selectAll(Graph graph) {
        Selection sel = new Selection("all");
        sel.addAll(graph.getGraphElements());
        return sel;
    }

    /**
     * Returns a selection containing edges of the specified graph.
     * 
     * @param graph
     *            This graph's edges will be selected.
     * @return A selection containing all edges of the specified graph.
     */
    public static Selection selectAllEdges(Graph graph) {
        Selection sel = new Selection("all edges");
        sel.addAll(graph.getEdges());
        return sel;
    }

    /**
     * Returns a selection containing all nodes of the specified graph.
     * 
     * @param graph
     *            This graph's nodes will be selected.
     * @return A selection containing all nodes of the specified graph.
     */
    public static Selection selectAllNodes(Graph graph) {
        Selection sel = new Selection("all nodes");
        sel.addAll(graph.getNodes());
        return sel;
    }

    /**
     * Returns a selection containing all neighbours of the specified nodes.
     * 
     * @param nodes
     *            This nodes' neighbours will be selected.
     * @return A selection containing containing all neighbours of the specified
     *         nodes.
     */
    public static Selection selectAllNeighbours(Collection<Node> nodes) {
        Selection sel = new Selection("all neighbours");
        for (Node node : nodes) {
            sel.addAll(node.getNeighbors());
        }
        return sel;
    }

    /**
     * Returns a selection containing all neighbours of the specified node.
     * 
     * @param node
     *            This node's neighbours will be selected.
     * @return A selection containing containing all neighbours of the specified
     *         node.
     */
    public static Selection selectAllNeighbours(Node node) {
        Selection sel = new Selection("all neighbours");
        sel.addAll(node.getNeighbors());
        return sel;
    }

    /**
     * Returns a selection containing all in-neighbours of the specified nodes.
     * 
     * @param nodes
     *            This nodes' in-neighbours will be selected.
     * @return A selection containing containing all in-neighbours of the
     *         specified nodes.
     */
    public static Selection selectAllIncomingEdgeNeighbours(
            Collection<Node> nodes) {
        Selection sel = new Selection("in neighbours");
        for (Node node : nodes) {
            sel.addAll(node.getInNeighbors());
        }
        return sel;
    }

    /**
     * Returns a selection containing all in-neighbours of the specified node.
     * 
     * @param node
     *            This node's in-neighbours will be selected.
     * @return A selection containing containing all in-neighbours of the
     *         specified node.
     */
    public static Selection selectAllInNeighbours(Node node) {
        Selection sel = new Selection("in neighbours");
        sel.addAll(node.getInNeighbors());
        return sel;
    }

    /**
     * Returns a selection containing all out-neighbours of the specified nodes.
     * 
     * @param nodes
     *            This nodes' out-neighbours will be selected.
     * @return A selection containing containing all out-neighbours of the
     *         specified nodes.
     */
    public static Selection selectOutgoingEdgeNeighbors(Collection<Node> nodes) {
        Selection sel = new Selection("out neighbours");
        for (Node node : nodes) {
            sel.addAll(node.getOutNeighbors());
        }
        return sel;
    }

    /**
     * Returns a selection containing the reachable subgraph of the specified
     * node.
     * 
     * @param root
     *            The root node.
     * @return A selection containing the reachable subgraph of the specified
     *         node.
     */
    public static Selection selectReachableSubgraph(Node root) {
        Selection sel = new Selection("reachable subgraph");
        Set<GraphElement> result = new HashSet<GraphElement>();
        java.util.Queue<Node> queue = new LinkedList<Node>();
        queue.add(root);
        while (!queue.isEmpty()) {
            Node node = queue.poll();
            if (!result.contains(node)) {
                result.add(node);
                for (Edge toNeighbor : node.getAllOutEdges()) {
                    result.add(toNeighbor);
                }
                for (Node neighbors : node.getOutNeighbors()) {
                    queue.add(neighbors);
                }
            }
        }
        sel.addAll(result);
        return sel;
    }

    /**
     * Returns a selection containing the connected component of the specified
     * node.
     * 
     * @param root
     *            The root node.
     * @return A selection containing the connected component of the specified
     *         node.
     */
    public static Selection selectConnectedComponent(Node root) {
        Selection sel = new Selection("connected component");
        Set<GraphElement> result = new HashSet<GraphElement>();
        java.util.Queue<Node> queue = new LinkedList<Node>();
        queue.add(root);
        while (!queue.isEmpty()) {
            Node node = queue.poll();
            if (!result.contains(node)) {
                result.add(node);
                for (Edge toNeighbor : node.getEdges()) {
                    result.add(toNeighbor);
                }
                for (Node neighbors : node.getNeighbors()) {
                    queue.add(neighbors);
                }
            }
        }
        sel.addAll(result);
        return sel;
    }

    /**
     * Returns a selection containing all out-neighbours of the specified node.
     * 
     * @param node
     *            This node's out-neighbours will be selected.
     * @return A selection containing containing all out-neighbours of the
     *         specified node.
     */
    public static Selection selectAllOutNeighbours(Node node) {
        Selection sel = new Selection("out neighbours");
        sel.addAll(node.getOutNeighbors());
        return sel;
    }

    /**
     * Returns a selection containing all incident edges of the specified nodes.
     * 
     * @param nodes
     *            This nodes' incident edges will be selected.
     * @return A selection containing containing all incident edges of the
     *         specified nodes.
     */
    public static Selection selectIncidentEdges(Collection<Node> nodes) {
        Selection sel = new Selection("incident edges");
        for (Node node : nodes) {
            sel.addAll(node.getEdges());
        }
        return sel;
    }

    /**
     * Returns a selection containing all incident edges of the specified node.
     * 
     * @param node
     *            This node's incident edges will be selected.
     * @return A selection containing containing all incident edges of the
     *         specified node.
     */
    public static Selection selectIncidentEdges(Node node) {
        Selection sel = new Selection("incident edges");
        sel.addAll(node.getEdges());
        return sel;
    }

    /**
     * Returns a selection containing all ingoing edges of the specified nodes.
     * 
     * @param nodes
     *            This nodes' ingoing edges will be selected.
     * @return A selection containing containing all ingoing edges of the
     *         specified nodes.
     */
    public static Selection selectIncomingEdges(Collection<Node> nodes) {
        Selection sel = new Selection("ingoing edges");
        for (Node node : nodes) {
            sel.addAll(node.getAllInEdges());
        }
        return sel;
    }

    /**
     * Returns a selection containing all ingoing edges of the specified node.
     * 
     * @param node
     *            This node's ingoing edges will be selected.
     * @return A selection containing containing all ingoing edges of the
     *         specified node.
     */
    public static Selection selectIngoingEdges(Node node) {
        Selection sel = new Selection("ingoing edges");
        sel.addAll(node.getAllInEdges());
        return sel;
    }

    /**
     * Returns a selection containing all otgoing edges of the specified nodes.
     * 
     * @param nodes
     *            This nodes' outgoing edges will be selected.
     * @return A selection containing containing all outgoing edges of the
     *         specified nodes.
     */
    public static Selection selectOutgoingEdges(Collection<Node> nodes) {
        Selection sel = new Selection("outgoing edges");
        for (Node node : nodes) {
            sel.addAll(node.getAllOutEdges());
        }
        return sel;
    }

    /**
     * Returns a selection containing all outgoing edges of the specified node.
     * 
     * @param node
     *            This node's outgoing edges will be selected.
     * @return A selection containing containing all outgoing edges of the
     *         specified node.
     */
    public static Selection selectOutgoingEdges(Node node) {
        Selection sel = new Selection("outgoing edges");
        sel.addAll(node.getAllOutEdges());
        return sel;
    }

    /**
     * Returns a selection containing all isolated nodes of the specified graph.
     * 
     * @param selectedNodes
     *            This collections's isolated nodes will be selected.
     * @return A selection containing all isolated nodes of the specified graph.
     */
    public static Selection selectIsolatedNodes(Collection<Node> selectedNodes) {
        Selection sel = new Selection("isolated nodes");
        for (Node node : selectedNodes) {
            if (node.getEdges().isEmpty()) {
                sel.add(node);
            }
        }
        return sel;
    }

    /**
     * Returns a selection containing all self loops of the specified nodes.
     * 
     * @param nodes
     *            This nodes' self loops will be selected.
     * @return A selection containing all self loops of the specified nodes.
     */
    public static Selection selectSelfLoops(Collection<Node> nodes) {
        Selection sel = new Selection("self loops");
        for (Node node : nodes) {
            for (Edge edge : node.getEdges()) {
                if (edge.getSource() == edge.getTarget()) {
                    sel.add(edge);
                }
            }
        }
        return sel;
    }

    /**
     * Returns a selection containing all incident multiple edges of the
     * specified nodes.
     * 
     * @param nodes
     *            This nodes' incident multiple edges will be selected.
     * @return A selection containing all incident multiple edges of the
     *         specified nodes.
     */
    public static Selection selectMultipleEdges(Collection<Node> nodes) {
        Selection sel = new Selection("multiple edges");
        for (Node node : nodes) {
            for (Edge edge : node.getEdges()) {
                for (Edge otherEdge : node.getEdges()) {
                    if (edge.getTarget() == otherEdge.getTarget()
                            && edge != otherEdge && node != edge.getTarget()) {
                        sel.add(otherEdge);
                    }
                }
            }
        }
        return sel;
    }

    /**
     * Returns a selection containing all multiple edges (including multiple
     * edge loops) except one.
     * 
     * @param nodes
     *            a collection containing nodes
     * @return a selection containing all multiple edges for removal
     */
    public static Selection selectMultipleEdgesForRemoval(Collection<Node> nodes) {
        Selection sel = new Selection("multiple edges for removal");
        HashSet<Edge> edges = new LinkedHashSet<Edge>();
        // gets all incident edges
        for (Node node : nodes) {
            edges.addAll(node.getEdges());
        }

        HashSet<Edge> rest = new LinkedHashSet<Edge>();
        rest.addAll(edges);
        // selects all multiple edges except one so that after removing the
        // selected elements there will be only single edges between nodes
        for (Edge edge : edges) {
            HashSet<Edge> multipleEdges = new HashSet<Edge>();
            rest.remove(edge);
            for (Edge otherEdge : rest) {
                if (edge.getSource().equals(otherEdge.getSource())
                        && edge.getTarget().equals(otherEdge.getTarget())) {
                    multipleEdges.add(otherEdge);
                }
            }
            sel.addAll(multipleEdges);
        }
        return sel;
    }

    /**
     * Removes the specified attribute from the specified nodes.
     * 
     * @param graphElements
     *            The attribute of these nodes will be removed.
     * @param attributePath
     *            The attribute with this path will be removed.
     */
    public static void removeAttribute(
            Collection<? extends GraphElement> graphElements,
            String attributePath) throws AttributeNotFoundException {
        int i = 0;
        for (GraphElement ge : graphElements) {
            try {
                ge.removeAttribute(attributePath);
            } catch (AttributeNotFoundException e) {
                i++;
            }
        }
        if (i == graphElements.size())
            throw new AttributeNotFoundException(
                    "No one of the graph elements has such an attribute.");
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
