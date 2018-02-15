// =============================================================================
//
//   AbstractEdgeListener.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractEdgeListener.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.event;

/**
 * An abstract adapter class for receiving edge events. The methods in this
 * class are empty. This class exists as convenience for creating listener
 * objects.
 * 
 * <p>
 * Extend this class to create a <code>EdgeEvent</code> listener and override
 * the methods for the events of interest. (If you implement the
 * <code>EdgeListener</code> interface, you have to define all of the methods in
 * it. This abstract class defines <code>null</code> methods for them all, so
 * you can only have to define methods for events you care about.)
 * </p>
 * 
 * <p>
 * Create a listener object using the extended class and then register it with a
 * component using the component's <code>addEdgeEventListener</code> method.
 * When the structure of the graph is changed (e.g.: an edge is added, reversed,
 * an edge's source or target node changes, or an edge is removed) or a
 * transaction of graph structure changes is started or finished, the relevant
 * method in the listener object is invoked and the <code>EdgeEvent</code> is
 * passed to it.
 * </p>
 * 
 * @version $Revision: 5767 $
 * 
 * @see ListenerManager
 * @see EdgeEvent
 */
public abstract class AbstractEdgeListener implements EdgeListener {
    /**
     * Called after the edge was set directed or undirected.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    public void postDirectedChanged(EdgeEvent e) {
    }

    /**
     * Called after the edge has been reversed.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    public void postEdgeReversed(EdgeEvent e) {
    }

    /**
     * Called after the source node of an edge has changed.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    public void postSourceNodeChanged(EdgeEvent e) {
    }

    /**
     * Called after the target node of an edge has changed.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    public void postTargetNodeChanged(EdgeEvent e) {
    }

    /**
     * Called before the edge is set directed or undirected.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    public void preDirectedChanged(EdgeEvent e) {
    }

    /**
     * Called before the edge is going to be reversed.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    public void preEdgeReversed(EdgeEvent e) {
    }

    /**
     * Called before a change of the source node of an edge takes place.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    public void preSourceNodeChanged(EdgeEvent e) {
    }

    /**
     * Called before a change of the target node of an edge takes place.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    public void preTargetNodeChanged(EdgeEvent e) {
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
