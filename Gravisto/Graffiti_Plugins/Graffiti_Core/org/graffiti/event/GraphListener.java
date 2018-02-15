// =============================================================================
//
//   GraphListener.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphListener.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.event;

/**
 * Interface that contains methods which are called when a graph is changed.
 * 
 * @version $Revision: 5767 $
 */
public interface GraphListener extends TransactionListener {
    /**
     * Called after an edge has been added to the graph.
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    public void postEdgeAdded(GraphEvent e);

    /**
     * Called after an edge has been removed from the graph.
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    public void postEdgeRemoved(GraphEvent e);

    /**
     * Called after method <code>clear()</code> has been called on a graph. No
     * other events (like remove events) are generated.
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    public void postGraphCleared(GraphEvent e);

    /**
     * Called after an edge has been added to the graph.
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    public void postNodeAdded(GraphEvent e);

    /**
     * Called after a node has been removed from the graph. All edges incident
     * to this node have already been removed (preEdgeRemoved and
     * postEdgeRemoved have been called).
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    public void postNodeRemoved(GraphEvent e);

    /**
     * Called just before an edge is added to the graph.
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    public void preEdgeAdded(GraphEvent e);

    /**
     * Called just before an edge is removed from the graph.
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    public void preEdgeRemoved(GraphEvent e);

    /**
     * Called before method <code>clear()</code> is called on a graph. No other
     * events (like remove events) are generated.
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    public void preGraphCleared(GraphEvent e);

    /**
     * Called just before a node is added to the graph.
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    public void preNodeAdded(GraphEvent e);

    /**
     * Called just before a node is removed from the graph. This method is
     * called before the incident edges are deleted.
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    public void preNodeRemoved(GraphEvent e);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
