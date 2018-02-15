// =============================================================================
//
//   PQEntry.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: PQEntry.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.apsp;

import org.graffiti.graph.Edge;

/**
 * An entry in the priority queue.
 * 
 * @version $Revision: 5766 $
 */
public class PQEntry implements Comparable<PQEntry> {

    /** The edge, which is associated with &quot;distance&quot;. */
    private Edge e;

    /** The cost. */
    private double distance;

    /**
     * Constructs a new priority queue entry.
     * 
     * @param e
     *            the associated edge.
     * @param distance
     *            the associated cost.
     */
    public PQEntry(Edge e, double distance) {
        this.e = e;
        this.distance = distance;
    }

    /**
     * Sets the distance.
     * 
     * @param distance
     *            The distance to set
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * Returns the distance.
     * 
     * @return double
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Sets the e.
     * 
     * @param e
     *            The e to set
     */
    public void setEdge(Edge e) {
        this.e = e;
    }

    /**
     * Returns the e.
     * 
     * @return Edge
     */
    public Edge getEdge() {
        return e;
    }

    /**
     * @see java.lang.Comparable#compareTo(Object)
     */
    public int compareTo(PQEntry o) {
        return (int) (distance - o.distance);
    }

    /**
     * Returns a human readable string of this object.
     * 
     * @return a human readable string of this object.
     */
    @Override
    public String toString() {
        return "" + distance;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
