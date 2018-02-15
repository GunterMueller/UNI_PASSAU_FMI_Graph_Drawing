// =============================================================================
//
//   AdjListNode.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AdjListNode.java 5779 2010-05-10 20:31:37Z gleissner $

package org.graffiti.graph;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.event.ListenerManager;
import org.graffiti.event.NodeEvent;
import org.graffiti.util.MultipleIterator;
import org.graffiti.util.logging.GlobalLoggerSetting;

/**
 * Implements a graph node with adjacency list representation.
 * 
 * @version $Revision: 5779 $
 * 
 * @see AdjListGraph
 * @see AdjListEdge
 */
public class AdjListNode extends AbstractNode implements Node, GraphElement {

    /** The logger for the AdjListNode class. */
    private static final Logger logger = Logger.getLogger(AdjListNode.class
            .getName());
    
    static {
        logger.setLevel(GlobalLoggerSetting.LOGGER_LEVEL);
    }

    /**
     * Contains all the directed ingoing edges of the current <code>Node</code>.
     */
    protected List<Edge> directedInEdges;

    /**
     * Contains all the directed outgoing edges of the current <code>Node</code>
     * .
     */
    protected List<Edge> directedOutEdges;

    /**
     * Contains all the undirected edges connected to the current
     * <code>Node</code>.
     */
    protected List<Edge> undirectedEdges;

    /**
     * Constructs a new <code>AdjListNode</code>.
     * 
     * @param graph
     *            the <code>Graph</code> the <code>Node</code> belongs to.
     */
    protected AdjListNode(Graph graph) {
        super(graph);
        logger.fine("Creating new instance of AdjListNode");
        directedInEdges = new LinkedList<Edge>();
        undirectedEdges = new LinkedList<Edge>();
        directedOutEdges = new LinkedList<Edge>();
    }

    /**
     * Constructs a new <code>AdjListNode</code>.
     * 
     * @param graph
     *            the <code>Graph</code> the <code>Node</code> belongs to.
     * @param coll
     *            the <code>CollectionAttribute</code> of the newly created
     *            <code>AdjListNode</code>.
     */
    protected AdjListNode(Graph graph, CollectionAttribute coll) {
        super(graph, coll);
        logger.fine("Creating new instance of AdjListNode");
        directedInEdges = new LinkedList<Edge>();
        undirectedEdges = new LinkedList<Edge>();
        directedOutEdges = new LinkedList<Edge>();
    }

    /**
     * Returns an iterator containing the directed ingoing edges of the
     * <code>Node</code>.
     * 
     * @return an iterator containing the directed ingoing edges of the
     *         <code>Node</code>.
     */
    public Iterator<Edge> getDirectedInEdgesIterator() {
        return new MultipleIterator<Edge>(directedInEdges.iterator());
    }

    /**
     * Returns an iterator containing the outgoing directed edges of the
     * <code>Node</code>.
     * 
     * @return an iterator containing the outgoing directed edges of the
     *         <code>Node</code>.
     */
    public Iterator<Edge> getDirectedOutEdgesIterator() {
        return new MultipleIterator<Edge>(directedOutEdges.iterator());
    }

    /**
     * Returns an iterator containing all the ingoing and outgoing directed and
     * undirected edges of the current <code>Node</code>. Ingoing and outgoing
     * edges will not be separated and there will be no ordering on the
     * collection.
     * 
     * @return an iterator containing all ingoing and outgoing directed and
     *         undirected edges of the current <code>Node</code>.
     */
    public Iterator<Edge> getEdgesIterator() {
        return new MultipleIterator<Edge>(directedInEdges.iterator(),
                undirectedEdges.iterator(), directedOutEdges.iterator());
    }

    /**
     * Returns the in-degree of the current <code>Node</code>. The in-degree is
     * defined as the number of ingoing, directed edges plus the number of
     * undirected edges.
     * 
     * @return the in-degree of the current <code>Node</code>.
     */
    @Override
    public int getInDegree() {
        return directedInEdges.size() + undirectedEdges.size();
    }

    /**
     * Returns the out-degree of the current <code>Node</code>. The out-degree
     * is defined as the number of outgoing, directed edges plus the number of
     * undirected edges.
     * 
     * @return the out-degree of the current <code>Node</code>.
     */
    @Override
    public int getOutDegree() {
        return directedOutEdges.size() + undirectedEdges.size();
    }

    /**
     * Returns an iterator containing the undirected ingoing and outgoing edges
     * of the <code>Node</code>.
     * 
     * @return a iterator containing the undirected ingoing and outgoing edges
     *         of the <code>Node</code>.
     */
    public Iterator<Edge> getUndirectedEdgesIterator() {
        return new MultipleIterator<Edge>(undirectedEdges.iterator());
    }

    /**
     * Sets the <code>graph</code> member variable to <code>null</code>. <b>Be
     * Careful:</b> This function should only be called when the node gets
     * deleted.
     */
    void setGraphToNull() {
        this.graph = null;
    }

    /**
     * Adds a new ingoing <code>Edge</code> to the corresponding
     * <code>Edge</code> list. Informs the ListenerManageer about the change.
     * 
     * @param edge
     *            the <code>Edge</code> to be added.
     */
    void addInEdge(AdjListEdge edge) {
        assert edge != null;

        ListenerManager listMan = getListenerManager();

        if (edge.isDirected()) {
            logger.fine("adding an ingoing edge to this node");
            listMan.preInEdgeAdded(new NodeEvent(this, edge));
            this.directedInEdges.add(edge);
            listMan.postInEdgeAdded(new NodeEvent(this, edge));
        } else {
            logger.fine("adding an undirected edge to this node");
            listMan.preUndirectedEdgeAdded(new NodeEvent(this, edge));
            this.undirectedEdges.add(edge);
            listMan.postUndirectedEdgeAdded(new NodeEvent(this, edge));
        }

        logger.fine("exiting doAddEdge()");
    }

    /**
     * Adds a new outgoing <code>Edge</code> to the corresponding
     * <code>Edge</code> list. Informs the ListenerManageer about the change.
     * 
     * @param edge
     *            the <code>Edge</code> to be added.
     */
    void addOutEdge(AdjListEdge edge) {
        assert edge != null;

        ListenerManager listMan = getListenerManager();

        if (edge.isDirected()) {
            logger.info("adding an outgoing edge to this node");
            listMan.preOutEdgeAdded(new NodeEvent(this, edge));
            this.directedOutEdges.add(edge);
            listMan.postOutEdgeAdded(new NodeEvent(this, edge));
        } else {
            logger.info("adding an undirected edge to this node");
            listMan.preUndirectedEdgeAdded(new NodeEvent(this, edge));
            this.undirectedEdges.add(edge);
            listMan.postUndirectedEdgeAdded(new NodeEvent(this, edge));
        }

        logger.fine("exiting addEdge()");
    }

    /**
     * Removes an ingoing <code>Edge</code> from the corresponding
     * <code>Edge</code> list. Informs the ListenerManager about the change.
     * 
     * @param edge
     *            the <code>Edge</code> to remove.
     * 
     * @exception GraphElementNotFoundException
     *                if the <code>Edge</code> cannot be found in any of the
     *                <code>Edge</code> lists.
     */
    void removeInEdge(AdjListEdge edge) throws GraphElementNotFoundException {
        assert edge != null;

        ListenerManager listMan = getListenerManager();

        if (edge.isDirected()) {
            logger.fine("removing an inEdge");

            int idx = this.directedInEdges.indexOf(edge);

            if (idx != -1) {
                listMan.preInEdgeRemoved(new NodeEvent(this, edge));
                this.directedInEdges.remove(idx);
                listMan.postInEdgeRemoved(new NodeEvent(this, edge));
            } else {
                logger.severe("Throwing GraphElementNotFoundException, "
                        + "because the edge was not found in the "
                        + "(apropriate) list of the node");
                throw new GraphElementNotFoundException(
                        "The edge was not found in the (apropriate) list in "
                                + "the node");
            }
        } else {
            logger.fine("removing an undirected edge");

            int idx = this.undirectedEdges.indexOf(edge);

            if (idx != -1) {
                listMan.preUndirectedEdgeRemoved(new NodeEvent(this, edge));
                this.undirectedEdges.remove(idx);
                listMan.postUndirectedEdgeRemoved(new NodeEvent(this, edge));
            } else {
                logger.severe("Throwing GraphElementNotFoundException, "
                        + "because the edge was not found in the "
                        + "(apropriate) list of the node");
                throw new GraphElementNotFoundException(
                        "The edge was not found in the (apropriate) list in "
                                + "the node");
            }
        }

        logger.fine("exiting removeEdge()");
    }

    /**
     * Removes an outgoing <code>Edge</code> from the corresponding
     * <code>Edge</code> list. Informs the ListenerManager about the change.
     * 
     * @param edge
     *            the <code>Edge</code> to remove.
     * 
     * @exception GraphElementNotFoundException
     *                if the <code>Edge</code> cannot be found in any of the
     *                <code>Edge</code> lists.
     */
    void removeOutEdge(AdjListEdge edge) throws GraphElementNotFoundException {
        assert edge != null;

        ListenerManager listMan = getListenerManager();

        if (edge.isDirected()) {
            logger.fine("removing a directed outEdge");

            int idx = this.directedOutEdges.indexOf(edge);

            if (idx != -1) {
                listMan.preOutEdgeRemoved(new NodeEvent(this, edge));
                this.directedOutEdges.remove(idx);
                listMan.postOutEdgeRemoved(new NodeEvent(this, edge));
            } else {
                logger.severe("Throwing GraphElementNotFoundException, "
                        + "because the edge was not found in the "
                        + "(apropriate) list of the node");
                throw new GraphElementNotFoundException(
                        "The edge was not found in the (apropriate) list in "
                                + "the node");
            }
        } else {
            logger.fine("removing an undirected outEdge");

            int idx = this.undirectedEdges.indexOf(edge);

            if (idx != -1) {
                listMan.preUndirectedEdgeRemoved(new NodeEvent(this, edge));
                this.undirectedEdges.remove(idx);
                listMan.postUndirectedEdgeRemoved(new NodeEvent(this, edge));
            } else {
                logger.severe("Throwing GraphElementNotFoundException, "
                        + "because the edge was not found in the "
                        + "(apropriate) list of the node");
                throw new GraphElementNotFoundException(
                        "The edge was not found in the (apropriate) list in "
                                + "the node");
            }
        }

        logger.fine("exiting removeEdge()");
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
