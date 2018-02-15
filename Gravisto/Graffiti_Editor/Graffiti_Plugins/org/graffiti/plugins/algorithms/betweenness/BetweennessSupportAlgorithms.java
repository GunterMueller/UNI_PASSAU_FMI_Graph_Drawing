// =============================================================================
//
//   BetweennessSupportAlgorithms.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: BetweennessSupportAlgorithms.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.betweenness;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.util.Queue;

/*
 * Created on 24.10.2004
 */
/**
 * @author Markus K�ser
 * @version $Revision 1.0 $
 * 
 *          This singleton class is a collection of algorithms that are
 *          frequently needed in betweenness and clustering algorithms.
 */
public class BetweennessSupportAlgorithms {
    /** The base path for storing betweenness information on graph elements */
    private static final String BASE = "betweenness";

    /**
     * Path for storage of temporary marks on nodes that signal that all nodes
     * with this mark belong to one component
     */
    private static final String COMPONENT_MARK = BASE + "componentMark";

    /**
     * Path for storage of marks on edges that are to be ignored by undirected
     * bfs
     */
    private static final String IGNORED_BY_UNDIRECTED_BFS = BASE
            + "IgnoredByUndirectedBFS";

    /** Error message */
    private static final String FOUND_NODE_OF_OTHER_COMPONENT_ERROR = "The "
            + "given Collection of nodes is not a weakly connected component. An"
            + "Edge that leads out of the component was found";

    /** Error message */
    private static final String NODE_NOT_IN_GRAPH_ERROR = "This node is not"
            + " an element of the graph.";

    /** The sinlgeton object of this class. */
    private static BetweennessSupportAlgorithms bsa = null;

    /**
     * Returns the singleton <code> BetweennessSupportAlgorithms </code> object.
     * 
     * @return the object
     */
    public static BetweennessSupportAlgorithms getBetweennessSupportAlgorithms() {
        if (bsa == null) {
            bsa = new BetweennessSupportAlgorithms();
        }
        return bsa;
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
     * If <code> mark </code> is set to true this method sets the component mark
     * to a given graph element. All nodes and edges with this mark must belong
     * to the same component of the same graph. If <code> mark  </code> is set
     * to false, then the mark will be removed.
     * 
     * @param e
     *            the element
     * @param mark
     *            if true, the mark will be set, if false it will be removed
     */
    public void setComponentMark(GraphElement e, boolean mark) {
        try {
            e.removeAttribute(COMPONENT_MARK);
        } catch (AttributeNotFoundException anfe) {
        }

        if (mark) {
            e.setBoolean(COMPONENT_MARK, true);
        }
    }

    /**
     * If <code> mark </code> is set to true this method sets the component mark
     * to the given graph elements. All nodes and edges with this mark must
     * belong to the same component of the same graph. If <code> mark
     * </code> is set to
     * false, then the mark will be removed.
     * 
     * @param elements
     *            the elements
     * @param mark
     *            if true, the mark will be set, if false it will be removed
     */
    public void setComponentMark(Collection<? extends GraphElement> elements,
            boolean mark) {
        GraphElement temp;

        for (Iterator<? extends GraphElement> nodeIt = elements.iterator(); nodeIt
                .hasNext();) {
            temp = nodeIt.next();
            setComponentMark(temp, mark);
        }
    }

    /**
     * Checks if a graph element has a component mark. All Nodes and edges with
     * this mark have to belong to the same component of the same graph.
     * 
     * @param e
     *            the element
     * 
     * @return true, if the node has the mark, false otherwise
     */
    public boolean isComponentMarked(GraphElement e) {
        boolean isMarked = false;

        try {
            isMarked = e.getBoolean(COMPONENT_MARK);
        } catch (AttributeNotFoundException anfe) {
        }

        return isMarked;
    }

    /**
     * Removes the component mark from all elements of the given graph
     * 
     * @param graph
     *            the graph
     */
    public void removeComponentMark(Graph graph) {
        setComponentMark(graph.getNodes(), false);
        setComponentMark(graph.getEdges(), false);
    }

    /**
     * Collects all edges of a given connected component and returns them. This
     * algorithm assumes, that no component marks are set to the graph nodes and
     * edges. The markes will be removed afterwards.
     * 
     * @param component
     *            a connected component of a graph
     * 
     * @return the edges of this component
     */
    public Collection<Edge> getEdgesOfComponent(Collection<Node> component) {
        Collection<Edge> componentEdges = new LinkedList<Edge>();
        Node source;
        Edge edge;
        Node target;
        Collection<Edge> tempEdges;

        // first mark all nodes of the component
        setComponentMark(component, true);

        for (Iterator<Node> nodeIt = component.iterator(); nodeIt.hasNext();) {
            source = nodeIt.next();
            tempEdges = source.getEdges();

            for (Iterator<Edge> edgeIt = tempEdges.iterator(); edgeIt.hasNext();) {
                edge = edgeIt.next();
                target = getOtherEdgeNode(source, edge);

                // may not happen if given nodes really form a weakly connected
                // component
                if (!isComponentMarked(target))
                    throw new RuntimeException(
                            FOUND_NODE_OF_OTHER_COMPONENT_ERROR);
                else {
                    // found new edge
                    if (!isComponentMarked(edge)) {
                        componentEdges.add(edge);
                        setComponentMark(edge, true);
                    }
                }
            }
        }

        setComponentMark(component, false);
        setComponentMark(componentEdges, false);

        return componentEdges;
    }

    /**
     * Given an array of weakly connected components of a graph, this method
     * returns an array of the edges contained on the components. The indizes
     * for the array of a component in the components array is the same as for
     * the edges in this component in the returned array
     * 
     * @param components
     *            the array of components
     * 
     * @return the array of edges of the components
     */
    public Collection<Edge>[] getEdgesOfAllConnectedComponents(
            Collection<Node>[] components) {
        @SuppressWarnings("unchecked")
        Collection<Edge>[] edgesOfComponents = (Collection<Edge>[]) new Collection<?>[components.length];

        for (int i = 0; i < components.length; i++) {
            edgesOfComponents[i] = getEdgesOfComponent(components[i]);
        }

        return edgesOfComponents;
    }

    /**
     * Calculates the weakly connected components in the given graph using
     * <code>ignoringBFS</code> ignoring edge directions. Edges marked by method
     * <code> setIgnoredByBFS </code> will be ignored in this algorithm.
     * Component marks will be removed after using them. Returns an array of
     * components or an empty array if the graph contains no nodes.
     * 
     * @param graph
     *            the graph
     * 
     * @return the weakly connected components
     */
    public Collection<Node>[] getAllConnectedComponents(Graph graph) {
        int number = 0;
        Node tempNode = null;
        Collection<Collection<Node>> components = new LinkedList<Collection<Node>>();
        Collection<Node> tempComponent;

        for (Iterator<Node> nodeIt = graph.getNodesIterator(); nodeIt.hasNext();) {
            tempNode = nodeIt.next();

            if (!isComponentMarked(tempNode)) {
                tempComponent = ignoringBFS(graph, tempNode, true);
                components.add(tempComponent);
                number++;
            }
        }

        removeComponentMark(graph);

        @SuppressWarnings("unchecked")
        Collection<Node>[] result = components
                .toArray((Collection<Node>[]) new Collection<?>[number]);

        return result;
    }

    /**
     * Runs a breadth-first-search on a graph, ignoring all edges which were set
     * to be ignored by the <code> setIgnoredByBFS </code> method. All found
     * nodes are marked with a <code> componentMark </code> that can be checked
     * by the <code> isComponentMarked </code> method and removed by the
     * <code> removeComponentMarks </code> method. If the boolean parameter is
     * set to true, the direction of the edges will be ignored too. Before a new
     * run of residualNetBFS or ignoringBFS on the same network, the
     * <code> componentMark </code> are removed automaticly.
     * 
     * @param graph
     *            the graph
     * @param startNode
     *            the starting node of the BFS
     * @param ignoreEdgeDirection
     *            if true, the direction of the edges will be ignored while the
     *            BFS
     * 
     * @return the Collection of found nodes
     */
    public Collection<Node> ignoringBFS(Graph graph, Node startNode,
            boolean ignoreEdgeDirection) {
        // NOT removing component marks because of multiple runs of BFS
        if (startNode.getGraph() != graph)
            throw new RuntimeException(NODE_NOT_IN_GRAPH_ERROR);

        Collection<Node> startNodeComponent = new LinkedList<Node>();
        Queue queue = new Queue();
        queue.addLast(startNode);
        setComponentMark(startNode, true);
        startNodeComponent.add(startNode);

        while (!queue.isEmpty()) {
            Node sourceNode = (Node) queue.removeFirst();

            Collection<Edge> edges;

            if (ignoreEdgeDirection) {
                // get all ingoing, outgoing and undirected edges
                edges = sourceNode.getEdges();
            } else {
                // get all outgoing and undirected edges
                edges = sourceNode.getAllOutEdges();
            }

            for (Iterator<Edge> edgeIt = edges.iterator(); edgeIt.hasNext();) {
                Edge tempEdge = edgeIt.next();
                Node targetNode = getOtherEdgeNode(sourceNode, tempEdge);

                // if the target is not yet marked and the edge not to be igored
                if (!isComponentMarked(targetNode) && !isIgnoredByBFS(tempEdge)) {
                    queue.addLast(targetNode);
                    setComponentMark(targetNode, true);
                    startNodeComponent.add(targetNode);
                }
            }
        }
        return startNodeComponent;
    }

    /**
     * Checks if a given edge is marked to be ignored by <code>
     * ignoringBFS</code>
     * 
     * @param edge
     *            the edge
     * 
     * @return true if it is set to be ignored, false otherwise
     */
    public boolean isIgnoredByBFS(Edge edge) {
        boolean ignored = false;

        try {
            ignored = edge.getBoolean(IGNORED_BY_UNDIRECTED_BFS);
        } catch (AttributeNotFoundException anfe) {
        }

        return ignored;
    }

    /**
     * Marks all given edges to be ignored or not ignored by all following runs
     * of <code>ignoringBFS</code> until the property is set again.
     * 
     * @param edges
     *            the edges
     * @param ignored
     *            if true, the edge will be ignored, if false it will not be
     *            ignored.
     */
    public void setIgnoredByBFS(Collection<Edge> edges, boolean ignored) {
        Edge tempEdge;

        for (Iterator<Edge> edgeIt = edges.iterator(); edgeIt.hasNext();) {
            tempEdge = edgeIt.next();
            setIgnoredByBFS(tempEdge, ignored);
        }
    }

    /**
     * Marks an edge to be ignored or not ignored by all following runs of
     * <code>ignoringBFS</code> until the property is set again.
     * 
     * @param edge
     *            the edge
     * @param ignored
     *            if true, the edge will be ignored, if false it will not be
     *            ignored.
     */
    public void setIgnoredByBFS(Edge edge, boolean ignored) {
        try {
            if (ignored) {
                edge.setBoolean(IGNORED_BY_UNDIRECTED_BFS, true);
            } else {
                edge.removeAttribute(IGNORED_BY_UNDIRECTED_BFS);
            }
        } catch (AttributeNotFoundException anfe) {
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
