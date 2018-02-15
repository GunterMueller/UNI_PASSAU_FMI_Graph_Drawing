// =============================================================================
//
//   AbstractGraphListener.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractGraphListener.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.event;

/**
 * An abstract adapter class for receiving graph events. The methods in this
 * class are empty. This class exists as convenience for creating listener
 * objects.
 * 
 * <p>
 * Extend this class to create a <code>GraphEvent</code> listener and override
 * the methods for the events of interest. (If you implement the
 * <code>GraphListener</code> interface, you have to define all of the methods
 * in it. This abstract class defines <code>null</code> methods for them all, so
 * you can only have to define methods for events you care about.)
 * </p>
 * 
 * <p>
 * Create a listener object using the extended class and then register it with a
 * component using the component's <code>addGraphEventListener</code> method.
 * When the structure of the graph object is changed or a transaction of graph
 * changes is started or finished, the relevant method in the listener object is
 * invoked and the <code>GraphEvent</code> is passed to it.
 * </p>
 * 
 * @version $Revision: 5767 $
 * 
 * @see ListenerManager
 * @see GraphEvent
 */
public abstract class AbstractGraphListener implements GraphListener {
    /**
     * Called after an edge has been added to the graph.
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    public void postEdgeAdded(GraphEvent e) {
    }

    /**
     * Called after an edge has been removed from the graph.
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    public void postEdgeRemoved(GraphEvent e) {
    }

    /**
     * Called after method <code>clear()</code> has been called on a graph. No
     * other events (like remove events) are generated.
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    public void postGraphCleared(GraphEvent e) {
    }

    /**
     * Called after an edge has been added to the graph.
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    public void postNodeAdded(GraphEvent e) {
    }

    /**
     * Called after a node has been removed from the graph. All edges incident
     * to this node have already been removed (preEdgeRemoved and
     * postEdgeRemoved have been called).
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    public void postNodeRemoved(GraphEvent e) {
    }

    /**
     * Called just before an edge is added to the graph.
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    public void preEdgeAdded(GraphEvent e) {
    }

    /**
     * Called just before an edge is removed from the graph.
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    public void preEdgeRemoved(GraphEvent e) {
    }

    /**
     * Called before method <code>clear()</code> is called on a graph. No other
     * events (like remove events) are generated.
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    public void preGraphCleared(GraphEvent e) {
    }

    /**
     * Called just before a node is added to the graph.
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    public void preNodeAdded(GraphEvent e) {
    }

    /**
     * Called just before a node is removed from the graph. This method is
     * called before the incident edges are deleted.
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    public void preNodeRemoved(GraphEvent e) {
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
     * Called before a transaction has been started.
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
