// =============================================================================
//
//   Visited.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.planarAngleGraph;

/**
 * A <code>VisitedEdge</code> object stores for every
 * <code>org.graffiti.graph.Edge</code> whether it is visited already and if in
 * which direction.
 * 
 * @author Mirka Kossak
 * 
 */
public class VisitedEdge {
    private boolean forward;

    private boolean backward;

    /**
     * Constructs a <code>VisitedEdge</code>
     */
    public VisitedEdge() {
        this.forward = false;
        this.backward = false;
    }

    /**
     * Returns wheater the edge has been visited forward.
     * 
     * @return wheater the edge has been visited forward.
     */
    public boolean getForward() {
        return this.forward;
    }

    /**
     * Returns wheater the edge has been visited backward.
     * 
     * @return wheater the edge has been visited backward.
     */
    public boolean getBackward() {
        return this.backward;
    }

    /**
     * Sets wheater the edge has been visited forward.
     * 
     * @param forward
     */
    public void setForward(boolean forward) {
        this.forward = forward;
    }

    /**
     * Sets wheater the edge has been visited backward.
     * 
     * @param backward
     */
    public void setBackward(boolean backward) {
        this.backward = backward;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
