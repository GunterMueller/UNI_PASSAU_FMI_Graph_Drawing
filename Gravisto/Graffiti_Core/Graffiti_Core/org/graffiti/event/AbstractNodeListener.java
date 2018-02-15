// =============================================================================
//
//   AbstractNodeListener.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractNodeListener.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.event;

/**
 * An abstract adapter class for receiving node events. The methods in this
 * class are empty. This class exists as convenience for creating listener
 * objects.
 * 
 * <p>
 * Extend this class to create a <code>NodeEvent</code> listener and override
 * the methods for the events of interest. (If you implement the
 * <code>NodeListener</code> interface, you have to define all of the methods in
 * it. This abstract class defines <code>null</code> methods for them all, so
 * you can only have to define methods for events you care about.)
 * </p>
 * 
 * <p>
 * Create a listener object using the extended class and then register it with a
 * component using the component's <code>addNodeEventListener</code> method.
 * When a node is added, removed or the edge of a node is added ore removed, a
 * transaction of graph structure changes is started or finished, the relevant
 * method in the listener object is invoked and the <code>NodeEvent</code> is
 * passed to it.
 * </p>
 * 
 * @version $Revision: 5767 $
 * 
 * @see ListenerManager
 * @see NodeEvent
 */
public abstract class AbstractNodeListener implements NodeListener {
    /**
     * Called just after an incoming edge has been added to the node. (For
     * undirected edges postUndirectedEdgeAdded is called instead.)
     * 
     * @param e
     *            The NodeEvent detailing the changes.
     */
    public void postInEdgeAdded(NodeEvent e) {
    }

    /**
     * Called after an incoming edge has been removed from the node. (For
     * undirected edges postUndirectedEdgeRemoved is called.)
     * 
     * @param e
     *            The NodeEvent detailing the changes.
     */
    public void postInEdgeRemoved(NodeEvent e) {
    }

    /**
     * Called after an outgoing edge has been added to the node. (For undirected
     * edges postUndirectedEdgeAdded is called instead.)
     * 
     * @param e
     *            The NodeEvent detailing the changes.
     */
    public void postOutEdgeAdded(NodeEvent e) {
    }

    /**
     * Called after an outgoing edge has been removed from the node. (For
     * undirected edges postUndirectedEdgeRemoved is called.)
     * 
     * @param e
     *            The NodeEvent detailing the changes.
     */
    public void postOutEdgeRemoved(NodeEvent e) {
    }

    /**
     * Called after an (undirected) edge has been added to the node. (For
     * directed edges pre- In/Out- EdgeAdded is called.)
     * 
     * @param e
     *            The NodeEvent detailing the changes.
     */
    public void postUndirectedEdgeAdded(NodeEvent e) {
    }

    /**
     * Called after an (undirected) edge has been removed from the node. (For
     * directed edges pre- In/Out- EdgeRemoved is called.)
     * 
     * @param e
     *            The NodeEvent detailing the changes.
     */
    public void postUndirectedEdgeRemoved(NodeEvent e) {
    }

    /**
     * Called just before an incoming edge is added to the node. (For undirected
     * edges preUndirectedEdgeAdded is called instead.)
     * 
     * @param e
     *            The NodeEvent detailing the changes.
     */
    public void preInEdgeAdded(NodeEvent e) {
    }

    /**
     * Called just before an incoming edge is removed from the node. (For
     * undirected edges preUndirectedEdgeRemoved is called.)
     * 
     * @param e
     *            The NodeEvent detailing the changes.
     */
    public void preInEdgeRemoved(NodeEvent e) {
    }

    /**
     * Called just before an outgoing edge is added to the node. (For undirected
     * edges preUndirectedEdgeAdded is called instead.)
     * 
     * @param e
     *            The NodeEvent detailing the changes.
     */
    public void preOutEdgeAdded(NodeEvent e) {
    }

    /**
     * Called just before an outgoing edge is removed from the node. (For
     * undirected edges preUndirectedEdgeRemoved is called.)
     * 
     * @param e
     *            The NodeEvent detailing the changes.
     */
    public void preOutEdgeRemoved(NodeEvent e) {
    }

    /**
     * Called just before an (undirected) edge is added to the node. (For
     * directed edges pre- In/Out- EdgeAdded is called.)
     * 
     * @param e
     *            The NodeEvent detailing the changes.
     */
    public void preUndirectedEdgeAdded(NodeEvent e) {
    }

    /**
     * Called just before an (undirected) edge is removed from the node. (For
     * directed edges pre- In/Out- EdgeRemoved is called.)
     * 
     * @param e
     *            The NodeEvent detailing the changes.
     */
    public void preUndirectedEdgeRemoved(NodeEvent e) {
    }

    /**
     * Called after a transaction has been finished.
     * 
     * @param e
     *            gives details about the transaction.
     */
    public void transactionFinished(TransactionEvent e) {
    }

    /**
     * Called after a transaction has been started.
     * 
     * @param e
     *            gives details about the transaction.
     */
    public void transactionStarted(TransactionEvent e) {
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
