// =============================================================================
//
//   ExtendedAdjListNode.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ExtendedAdjListNode.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.generators;

import java.util.List;
import java.util.logging.Logger;

import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.event.ListenerManager;
import org.graffiti.event.NodeEvent;
import org.graffiti.graph.AdjListEdge;
import org.graffiti.graph.AdjListNode;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;

/**
 * This class extends the <code>AdjListNode</code>. The inserting of edges to a
 * node's internal edge lists is extended. Edges can be inserted at specified
 * positions now relatively to another edge.
 * 
 * @author $Marek Piorkowski$
 * @version $1.0$ $3.9.2005$
 */
public class ExtendedAdjListNode extends AdjListNode {

    /** The logger for the AdjListNode class. */
    private static final Logger logger = Logger
            .getLogger(ExtendedAdjListNode.class.getName());

    /** An edge's position in an edge's list. */
    public static final int BEFORE = -1;

    /** An edge's position in an edge's list. */
    public static final int AFTER = 1;

    /** An edge's position in an edge's list. */
    public static final int LAST = 10;

    /**
     * Constructs a new <code>ExtendedAdjListNode</code>.
     * 
     * @param graph
     *            the <code>Graph</code> the <code>Node</code> belongs to.
     */
    protected ExtendedAdjListNode(Graph graph) {
        super(graph);
    }

    /**
     * Constructs a new <code>ExtendedAdjListNode</code>.
     * 
     * @param graph
     *            the <code>Graph</code> the <code>Node</code> belongs to.
     * @param coll
     *            the <code>CollectionAttribute</code> of the newly created
     *            <code>AdjListNode</code>.
     */
    protected ExtendedAdjListNode(Graph graph, CollectionAttribute coll) {
        super(graph, coll);
    }

    /**
     * Adds a new ingoing <code>Edge</code> to the corresponding
     * <code>Edge</code> list. Informs the ListenerManageer about the change.
     * The new edge is positioned as specified at the parameter position
     * relatively to the <code>Edge</code> positionEdge.
     * 
     * @param edge
     *            the <code>Edge</code> to be added.
     * @param positionEdge
     *            The position of <code>Edge</code> edge is set relatively to
     *            this positionEdge.
     * @param position
     *            The position the <code>Edge</code> edge.
     */
    public void addInEdgeAt(AdjListEdge edge, AdjListEdge positionEdge,
            int position) {
        assert edge != null;

        ListenerManager listMan = getListenerManager();

        int index = getIndexOfEdge(directedInEdges, positionEdge);

        if (edge.isDirected()) {
            logger.fine("adding an ingoing edge to this node");
            listMan.preInEdgeAdded(new NodeEvent(this, edge));

            if (position == BEFORE) {
                this.directedInEdges.add(index, edge);
            } else if (position == AFTER) {
                this.directedInEdges.add(index + 1, edge);
            } else if (position == LAST) {
                this.directedInEdges.add(edge);
            }

            listMan.postInEdgeAdded(new NodeEvent(this, edge));
        } else {
            logger.fine("adding an undirected edge to this node");
            listMan.preUndirectedEdgeAdded(new NodeEvent(this, edge));

            if (position == BEFORE) {
                this.undirectedEdges.add(index, edge);
            } else if (position == AFTER) {
                this.undirectedEdges.add(index + 1, edge);
            } else if (position == LAST) {
                this.undirectedEdges.add(edge);
            }

            listMan.postUndirectedEdgeAdded(new NodeEvent(this, edge));
        }

        logger.fine("exiting doAddEdge()");
    }

    /**
     * Adds a new outgoing <code>Edge</code> to the corresponding
     * <code>Edge</code> list. Informs the ListenerManageer about the change.
     * The new edge is positioned as specified at the parameter position
     * relatively to the <code>Edge</code> positionEdge.
     * 
     * @param edge
     *            the <code>Edge</code> to be added.
     * @param positionEdge
     *            The position of <code>Edge</code> edge is set relatively to
     *            this positionEdge.
     * @param position
     *            The position the <code>Edge</code> edge.
     */
    void addOutEdgeAt(AdjListEdge edge, AdjListEdge positionEdge, int position) {
        assert edge != null;

        ListenerManager listMan = getListenerManager();

        int index = getIndexOfEdge(directedOutEdges, positionEdge);

        if (edge.isDirected()) {
            logger.info("adding an outgoing edge to this node");
            listMan.preOutEdgeAdded(new NodeEvent(this, edge));

            if (position == BEFORE) {
                this.directedOutEdges.add(index, edge);
            } else if (position == AFTER) {
                this.directedOutEdges.add(index + 1, edge);
            } else if (position == LAST) {
                this.directedOutEdges.add(edge);
            }

            listMan.postOutEdgeAdded(new NodeEvent(this, edge));
        } else {
            logger.info("adding an undirected edge to this node");
            listMan.preUndirectedEdgeAdded(new NodeEvent(this, edge));

            if (position == BEFORE) {
                this.undirectedEdges.add(index, edge);
            } else if (position == AFTER) {
                this.undirectedEdges.add(index + 1, edge);
            } else if (position == LAST) {
                this.undirectedEdges.add(edge);
            }

            listMan.postUndirectedEdgeAdded(new NodeEvent(this, edge));
        }

        logger.fine("exiting addEdge()");
    }

    /**
     * Computes the index of the specified edges position in the specified list.
     * 
     * @param list
     *            The list, in that the specified edge has to be found.
     * @param edge
     *            The edge to be found.!
     * 
     * @return The index of the edge's position in the list. -1, if the edge is
     *         not in the list.
     */
    private int getIndexOfEdge(List<Edge> list, Edge edge) {
        int i = 0;

        for (Edge e : list) {
            if (e == edge)
                return i;

            i++;
        }

        return -1;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
