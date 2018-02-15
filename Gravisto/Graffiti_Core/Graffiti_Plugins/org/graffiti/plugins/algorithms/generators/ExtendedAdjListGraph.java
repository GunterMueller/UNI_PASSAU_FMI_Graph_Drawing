// =============================================================================
//
//   ExtendedAdjListGraph.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ExtendedAdjListGraph.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.generators;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.event.GraphEvent;
import org.graffiti.event.ListenerManager;
import org.graffiti.graph.AdjListEdge;
import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElementNotFoundException;
import org.graffiti.graph.Node;

/**
 * This class extends <code>AdjListGraph</code>. The inserting of edges to a
 * node's internal edge lists is extended. Edges can be inserted at specified
 * positions now relatively to another edge.
 * 
 * @author $Marek Piorkowski$
 * @version $1.0$ $3.9.2005$
 */
public class ExtendedAdjListGraph extends AdjListGraph {

    /** The logger for the current class. */
    private static final Logger logger = Logger
            .getLogger(ExtendedAdjListGraph.class.getName());

    /**
     * Constructs a new instance of an <code>ExtendedAdjListGraph</code>. Sets
     * the <code>ListenerManager</code> of the new instance to the default
     * <code>ListenerManager</code>.
     */
    public ExtendedAdjListGraph() {
        super();
    }

    /**
     * Constructs a new instance of an <code>ExtendedAdjListGraph</code>. Sets
     * the <code>ListenerManager</code> of the new instance to the default
     * <code>ListenerManager</code>.
     * 
     * @param coll
     *            the <code>CollectionAttribute</code> of the currently created
     *            <code>AdjListGraph</code> instance.
     */
    public ExtendedAdjListGraph(CollectionAttribute coll) {
        super(coll);
    }

    /**
     * Constructs a new instance of an <code>ExtendedAdjListGraph</code>. Sets
     * the <code>ListenerManager</code> of the new instance to the specified
     * one.
     * 
     * @param listenerManager
     *            listener manager for the graph.
     */
    public ExtendedAdjListGraph(ListenerManager listenerManager) {
        super(listenerManager);
    }

    /**
     * Constructs a new instance of an <code>ExtendedAdjListGraph</code>. Sets
     * the <code>ListenerManager</code> of the new instance to the specified
     * one.
     * 
     * @param listenerManager
     *            listener manager for the graph.
     * @param coll
     *            the <code>CollectionAttribute</code> of the currently created
     *            <code>AdjListGraph</code> instance.
     */
    public ExtendedAdjListGraph(ListenerManager listenerManager,
            CollectionAttribute coll) {
        super(listenerManager, coll);
    }

    /**
     * Constructs a new instance of an <code>ExtendedAdjListGraph</code> from an
     * instance of any <code>Graph</code> implementation. Copies all nodes and
     * edges from g into the new graph.
     * 
     * @param g
     *            any <code>Graph</code> implementation out of which an
     *            <code>AdjListGraph</code> shall be generated.
     * @param listenerManager
     *            listener manager for the graph.
     */
    public ExtendedAdjListGraph(Graph g, ListenerManager listenerManager) {
        super(g, listenerManager);
    }

    /**
     * Constructs a new instance of an <code>ExtendedAdjListGraph</code> from an
     * instance of any <code>Graph</code> implementation. Copies all nodes and
     * edges from g into the new graph.
     * 
     * @param g
     *            any <code>Graph</code> implementation out of which an
     *            <code>AdjListGraph</code> shall be generated.
     * @param listenerManager
     *            listener manager for the graph.
     * @param coll
     *            the <code>CollectionAttribute</code> of the currently created
     *            <code>AdjListGraph</code> instance.
     */
    public ExtendedAdjListGraph(Graph g, ListenerManager listenerManager,
            CollectionAttribute coll) {
        super(g, listenerManager, coll);
    }

    /**
     * Adds an edge x to this graph. The source of the edge is the source of the
     * specified edge e and the target is the specified node target. The edge x
     * is inserted before or after edge e in the outgoining edges list of the
     * node 'source(e)' as it is specified in the parameter 'position'.
     * 
     * @param e
     *            The created edge's source is the source of e.
     * @param target
     *            The created edge's target node.
     * @param directed
     *            <code>true</code> if the edge shall be directed,
     *            <code>false</code> otherwise.
     * @param position
     *            The created edge's position in the the outgoing edges list of
     *            the node 'source(e)'.
     * 
     * @return A new edge.
     * 
     * @throws GraphElementNotFoundException
     *             If any of the nodes cannot be found in the graph.
     */
    public Edge addEdge(Edge e, Node target, boolean directed, int position)
            throws GraphElementNotFoundException {
        assert (e != null) && (target != null);

        Node source = e.getSource();

        ListenerManager listMan = this.getListenerManager();

        if (this != source.getGraph()) {
            logger.severe("throwing GENFException, because the given source "
                    + "was not in the same graph");
            throw new GraphElementNotFoundException(
                    "source is not in the same graph as the edge");
        }

        if (this != target.getGraph()) {
            logger.severe("throwing GENFException, because the given target "
                    + "was not in the same graph");
            throw new GraphElementNotFoundException(
                    "target is not in the same graph as the edge");
        }

        listMan.preEdgeAdded(new GraphEvent(source, target));

        Edge edge = doAddEdge(e, target, directed, position);

        // add the edge's default attribute
        if (directed) {
            if (defaultDirectedEdgeAttribute != null) {
                edge.addAttribute((Attribute) defaultDirectedEdgeAttribute
                        .copy(), "");
            }
        } else {
            if (defaultUndirectedEdgeAttribute != null) {
                edge.addAttribute((Attribute) defaultUndirectedEdgeAttribute
                        .copy(), "");
            }
        }

        listMan.postEdgeAdded(new GraphEvent(edge));

        return edge;
    }

    /**
     * Adds an edge x to this graph. The source of the edge is the source of the
     * specified edge e and the target is the specified node target. The edge x
     * is inserted before or after edge e in the outgoining edges list of the
     * node 'source(e)' as it is specified in the parameter 'position'.
     * 
     * @param e
     *            The created edge's source is the source of e.
     * @param target
     *            The created edge's target node.
     * @param directed
     *            <code>true</code> if the edge shall be directed,
     *            <code>false</code> otherwise.
     * @param position
     *            The created edge's position in the the outgoing edges list of
     *            the node 'source(e)'.
     * @param col
     *            The <code>CollectionAttribute</code> that will be added to the
     *            new edge.
     * 
     * @return A new edge.
     * 
     * @throws GraphElementNotFoundException
     *             If any of the nodes cannot be found in the graph.
     */
    public Edge addEdge(Edge e, Node target, boolean directed, int position,
            CollectionAttribute col) throws GraphElementNotFoundException {
        assert (e != null) && (target != null) && (col != null);

        Node source = e.getSource();

        if (this != source.getGraph()) {
            logger.severe("throwing GENFException, because the given source "
                    + "was not in the same graph");
            throw new GraphElementNotFoundException(
                    "source is not in the same graph as the edge");
        }

        if (this != target.getGraph()) {
            logger.severe("throwing GENFException, because the given target "
                    + "was not in the same graph");
            throw new GraphElementNotFoundException(
                    "target is not in the same graph as the edge");
        }

        // logger.info("adding a new edge to the graph");
        ListenerManager listMan = this.getListenerManager();

        listMan.preEdgeAdded(new GraphEvent(source, target));

        Edge edge = doAddEdge(e, target, directed, position, col);
        listMan.postEdgeAdded(new GraphEvent(edge));

        return edge;
    }

    /**
     * Creates a new <code>ExtendedAdjListEdge</code> that is in the current
     * graph. And initializes it with the given <code>CollectionAttribute</code>
     * .
     * 
     * @param source
     *            the source of the edge to add.
     * @param target
     *            the target of the edge to add.
     * @param directed
     *            <code>true</code> if the edge shall be directed,
     *            <code>false</code> otherwise.
     * @param col
     *            The <code>CollectionAttribute</code> that will be added to the
     *            new edge.
     * 
     * @return the new edge.
     */
    @Override
    public Edge createEdge(Node source, Node target, boolean directed,
            CollectionAttribute col) {
        assert col != null;

        setModified(true);

        return new ExtendedAdjListEdge(this, source, target, directed, col);
    }

    /**
     * Creates a new <code>ExtendedAdjListNode</code> that is in the current
     * graph. And initializes it with the given <code>CollectionAttribute</code>
     * .
     * 
     * @param col
     *            The <code>CollectionAttribute</code> that will be added to the
     *            new edge.
     * 
     * @return the newly created node.
     */
    @Override
    public Node createNode(CollectionAttribute col) {
        assert col != null;

        setModified(true);

        return new ExtendedAdjListNode(this, col);
    }

    /**
     * Returns the face cycle successor of the specified
     * <code>ExtendedAdjListEdge</code>.
     * 
     * @param e
     *            The specified edge, whose face cycle successor has to be
     *            computed.
     * 
     * @return The face cycle successor of the specified
     *         <code>ExtendedAdjListEdge</code>.
     */
    public Edge face_cycle_succ(ExtendedAdjListEdge e) {
        // get the reversal edge
        Edge reversalEdge = e.getReversal();

        ArrayList<Edge> out_edges = (ArrayList<Edge>) reversalEdge.getSource()
                .getDirectedOutEdges();

        // get the reversal edge's position in its source's outgoing edges
        int index = out_edges.indexOf(reversalEdge);

        Edge edge;

        // if the position is zero, take the last edge in this list
        if (index == 0) {
            edge = (out_edges.get(out_edges.size() - 1));
        }

        // else take the predecessor
        else {
            edge = (out_edges.get(index - 1));
        }

        return edge;
    }

    /**
     * Adds a new edge to the current graph. Informs the ListenerManager about
     * the new node.
     * 
     * @param source
     *            The source of the edge to add.
     * @param target
     *            The target of the edge to add.
     * @param positionEdge
     *            The new edges position is setted relatively to this edge.
     * @param directed
     *            <code>true</code> if the edge shall be directed,
     *            <code>false</code> otherwise.
     * @param position
     *            The created edge's position in the the outgoing edges list of
     *            the source node.
     * @param col
     *            The <code>CollectionAttribute</code> that will be added to the
     *            new edge.
     * 
     * @return the new edge.
     */
    protected Edge createAndAddEdge(Node source, Node target,
            Edge positionEdge, boolean directed, int position,
            CollectionAttribute col) {
        assert (source != null) && (target != null) && (positionEdge != null);

        AdjListEdge edge;

        if (col == null) {
            edge = (AdjListEdge) createEdge(source, target, directed);
        } else {
            edge = (AdjListEdge) createEdge(source, target, directed, col);
        }

        ((ExtendedAdjListNode) source).addOutEdgeAt(edge,
                (AdjListEdge) positionEdge, position);
        logger
                .info("ADDING NEW EDGE TO SOURCE :outgoing edge was added to the source node");
        ((ExtendedAdjListNode) target).addInEdgeAt(edge,
                (AdjListEdge) positionEdge, position);
        logger
                .info("ADDING NEW EDGE TO TARGET:ingoing edge was added to the target node");

        return edge;
    }

    /**
     * Creates a new <code>ExtendedAdjListEdge</code> that is in the current
     * graph.
     * 
     * @param source
     *            the source of the edge to add.
     * @param target
     *            the target of the edge to add.
     * @param directed
     *            <code>true</code> if the edge shall be directed,
     *            <code>false</code> otherwise.
     * 
     * @return the newly created edge.
     */
    @Override
    protected Edge createEdge(Node source, Node target, boolean directed) {
        setModified(true);

        return new ExtendedAdjListEdge(this, source, target, directed);
    }

    /**
     * Creates a new <code>ExtendedAdjListNode</code> that is in the current
     * graph.
     * 
     * @return the newly created node.
     */
    @Override
    protected Node createNode() {
        setModified(true);

        return new ExtendedAdjListNode(this);
    }

    /**
     * Adds a new edge to the current graph. Informs the ListenerManager about
     * the new node.
     * 
     * @param e
     *            The source of the edge to add is this edge's source.
     * @param target
     *            The target of the edge to add.
     * @param directed
     *            <code>true</code> if the edge shall be directed,
     *            <code>false</code> otherwise.
     * @param position
     *            The created edge's position in the the outgoing edges list of
     *            the source node.
     * 
     * @return the new edge.
     */
    protected Edge doAddEdge(Edge e, Node target, boolean directed, int position) {
        assert (e != null) && (target != null);

        Node source = e.getSource();

        return createAndAddEdge(source, target, e, directed, position, null);
    }

    /**
     * Adds a new edge to the current graph. Informs the ListenerManager about
     * the new node.
     * 
     * @param e
     *            The source of the edge to add is this edge's source.
     * @param target
     *            The target of the edge to add.
     * @param directed
     *            <code>true</code> if the edge shall be directed,
     *            <code>false</code> otherwise.
     * @param position
     *            The created edge's position in the the outgoing edges list of
     *            the source node.
     * @param col
     *            The <code>CollectionAttribute</code> that will be added to the
     *            new edge.
     * 
     * @return the new edge.
     */
    protected Edge doAddEdge(Edge e, Node target, boolean directed,
            int position, CollectionAttribute col) {
        assert (e != null) && (target != null) && (col != null);

        Node source = e.getSource();

        return createAndAddEdge(source, target, e, directed, position, col);
    }

    /**
     * Adds a new edge to the current graph. Informs the ListenerManager about
     * the new node.
     * 
     * @param source
     *            The source of the edge to add.
     * @param e
     *            The source of the edge to add is this edge's target.
     * @param directed
     *            <code>true</code> if the edge shall be directed,
     *            <code>false</code> otherwise.
     * @param position
     *            The created edge's position in the the outgoing edges list of
     *            the source node.
     * 
     * @return the new edge.
     */
    protected Edge doAddEdge(Node source, Edge e, boolean directed, int position) {
        assert (e != null) && (source != null);

        Node target = e.getTarget();

        return createAndAddEdge(source, target, e, directed, position, null);
    }

    /**
     * Adds a new edge to the current graph. Informs the ListenerManager about
     * the new node.
     * 
     * @param source
     *            The source of the edge to add.
     * @param e
     *            The target of the edge to add is this edge's target.
     * @param directed
     *            <code>true</code> if the edge shall be directed,
     *            <code>false</code> otherwise.
     * @param position
     *            The created edge's position in the the outgoing edges list of
     *            the source node.
     * @param col
     *            The <code>CollectionAttribute</code> that will be added to the
     *            new edge.
     * 
     * @return the new edge.
     */
    protected Edge doAddEdge(Node source, Edge e, boolean directed,
            int position, CollectionAttribute col) {
        assert (e != null) && (source != null) && (col != null);

        Node target = e.getTarget();

        return createAndAddEdge(source, target, e, directed, position, col);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
