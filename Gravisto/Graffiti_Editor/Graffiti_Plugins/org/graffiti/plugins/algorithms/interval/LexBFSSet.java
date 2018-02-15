// =============================================================================
//
//   LexBFSSet.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.interval;

/**
 * This class is a container-class containing a number of LexBFSNodes.
 * 
 * @author struckmeier
 */
public class LexBFSSet {

    private LexBFSNode first;

    private LexBFSNode last;

    private LexBFSSet nachfolger;

    private LexBFSSet vorgaenger;

    private int timestamp;

    private int oldStamp = -1;

    /**
     * Returns the timestamp.
     * 
     * @return the timestamp.
     */
    public int getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp.
     * 
     * @param timestamp
     *            the timestamp to set.
     */
    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Returns the nachfolger.
     * 
     * @return the nachfolger.
     */
    public LexBFSSet getNachfolger() {
        return nachfolger;
    }

    /**
     * Sets the nachfolger.
     * 
     * @param nachfolger
     *            the nachfolger to set.
     */
    public void setNachfolger(LexBFSSet nachfolger) {
        this.nachfolger = nachfolger;
    }

    /**
     * Returns the vorgaenger.
     * 
     * @return the vorgaenger.
     */
    public LexBFSSet getVorgaenger() {
        return vorgaenger;
    }

    /**
     * Sets the vorgaenger.
     * 
     * @param vorgaenger
     *            the vorgaenger to set.
     */
    public void setVorgaenger(LexBFSSet vorgaenger) {
        this.vorgaenger = vorgaenger;
    }

    /**
     * Returns the first.
     * 
     * @return the first.
     */
    public LexBFSNode getFirst() {
        return first;
    }

    /**
     * Sets the first.
     * 
     * @param first
     *            the first to set.
     */
    public void setFirst(LexBFSNode first) {
        this.first = first;
    }

    /**
     * Returns the last.
     * 
     * @return the last.
     */
    public LexBFSNode getLast() {
        return last;
    }

    /**
     * Sets the last.
     * 
     * @param last
     *            the last to set.
     */
    public void setLast(LexBFSNode last) {
        this.last = last;
    }

    /**
     * Sets the oldStamp.
     * 
     * @param oldStamp
     *            the oldStamp to set.
     */
    public void setOldStamp(int oldStamp) {
        this.oldStamp = oldStamp;
    }

    /**
     * Returns the oldStamp.
     * 
     * @return the oldStamp.
     */
    public int getOldStamp() {
        return oldStamp;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
