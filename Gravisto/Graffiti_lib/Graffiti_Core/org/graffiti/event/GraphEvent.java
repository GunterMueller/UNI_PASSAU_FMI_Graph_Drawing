// =============================================================================
//
//   GraphEvent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphEvent.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.event;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * Contains a graph event. A <code>GraphEvent</code> object is passed to every
 * <code>GraphListener</code> or <code>AbstractGraphListener</code> object which
 * is registered to receive the "interesting" graph events using the component's
 * <code>addGraphListener</code> method. (<code>AbstractGraphListener</code>
 * objects implement the <code>GraphListener</code> interface.) Each such
 * listener object gets a <code>GraphEvent</code> containing the graph event.
 * 
 * @version $Revision: 5767 $
 * 
 * @see GraphListener
 * @see AbstractGraphListener
 */
public class GraphEvent extends AbstractEvent {
    /**
     * 
     */
    private static final long serialVersionUID = -3412052488827875658L;

    /**
     * The edge that has been changed by the event. <code>Null</code> if no edge
     * is concerned.
     */
    private Edge edge = null;

    /**
     * The graph that has been changed by the event. <code>Null</code> if no
     * graph is associated with the changed graph element (e.g. after an element
     * has been deleted) or if a different source has been specified.
     */
    private Graph graph = null;

    /**
     * The node that has been changed by the event. <code>Null</code> if no node
     * is concerned.
     */
    private Node node = null;

    /**
     * <code>secondNode</code> holds another node for events that are originated
     * from two nodes (like the <code>preEdgeAdded</code> event). Is
     * <code>null</code> if it is not needed.
     */
    private Node secondNode = null;

    /**
     * Constructs a graph event object with the specified source component.
     * 
     * @param graph
     *            the graph that originated the event.
     */
    public GraphEvent(Graph graph) {
        super(graph);
        this.graph = graph;
    }

    /**
     * Constructs a graph event object with the specified source component.
     * 
     * @param edge
     *            the edge that originated the event.
     */
    public GraphEvent(Edge edge) {
        super(edge);
        this.edge = edge;
    }

    /**
     * Constructs a graph event object with the specified source component.
     * 
     * @param node
     *            the node that originated the event.
     */
    public GraphEvent(Node node) {
        super(node);
        this.node = node;
    }

    /**
     * Constructs a graph event object with the graph of the first node as
     * source and saves the two given nodes.
     * 
     * @param node
     *            the node that originated the event.
     * @param secondNode
     *            the second node that originated the event.
     */
    public GraphEvent(Node node, Node secondNode) {
        super(node);
        assert node != null;
        assert secondNode != null;
        this.node = node;
        this.secondNode = secondNode;
    }

    /**
     * Returns the edge that originates this event, e.g.: the edge that has been
     * added. Might return <code>null</code> if no edge is concerned.
     * 
     * @return the edge that is concerned with the event
     */
    public Edge getEdge() {
        return edge;
    }

    /**
     * Returns the graph that has been changed.
     * 
     * @return the graph that has been changed by this event.
     */
    public Graph getGraph() {
        return graph;
    }

    /**
     * Returns the node that is concerned with that event, e.g.: the node that
     * has been / is to be removed. Might return <code>null</code> if no node is
     * concerned.
     * 
     * @return the edge that is concerned with the event.
     */
    public Node getNode() {
        return node;
    }

    /**
     * Returns the node that is concerned with that event, e.g.: the node that
     * has been / is to be removed. Might return <code>null</code> if no node is
     * concerned.
     * 
     * @return the edge that is concerned with the event.
     */
    public Node getSecondNode() {
        return secondNode;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
