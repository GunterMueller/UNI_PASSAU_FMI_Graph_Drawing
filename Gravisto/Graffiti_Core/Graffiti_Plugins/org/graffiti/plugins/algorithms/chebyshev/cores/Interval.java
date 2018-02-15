// =============================================================================
//
//   Interval.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.chebyshev.cores;

import org.graffiti.plugins.algorithms.chebyshev.AuxNode;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
final class Interval {
    private int lower;
    private int upper;
    private AuxNode node;

    public Interval(int lower, int upper, AuxNode node) {
        this.lower = lower;
        this.upper = upper;
        this.node = node;
    }

    public int getLower() {
        return lower;
    }

    public int getUpper() {
        return upper;
    }

    public AuxNode getNode() {
        return node;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
