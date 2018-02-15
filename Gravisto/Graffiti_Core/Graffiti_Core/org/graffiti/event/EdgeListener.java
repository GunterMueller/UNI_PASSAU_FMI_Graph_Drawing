// =============================================================================
//
//   EdgeListener.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: EdgeListener.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.event;

/**
 * Interface that contains methods which are called when an edge is changed.
 * 
 * @version $Revision: 5767 $
 */
public interface EdgeListener extends TransactionListener {
    /**
     * Called after the edge was set directed or undirected.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    public void postDirectedChanged(EdgeEvent e);

    /**
     * Called after the edge has been reversed.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    public void postEdgeReversed(EdgeEvent e);

    /**
     * Called after the source node of an edge has changed.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    public void postSourceNodeChanged(EdgeEvent e);

    /**
     * Called after the target node of an edge has changed.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    public void postTargetNodeChanged(EdgeEvent e);

    /**
     * Called before the edge is set directed or undirected.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    public void preDirectedChanged(EdgeEvent e);

    /**
     * Called before the edge is going to be reversed.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    public void preEdgeReversed(EdgeEvent e);

    /**
     * Called before a change of the source node of an edge takes place.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    public void preSourceNodeChanged(EdgeEvent e);

    /**
     * Called before a change of the target node of an edge takes place.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    public void preTargetNodeChanged(EdgeEvent e);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
