// =============================================================================
//
//   Direction.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.gridsifting;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public enum Direction {
    In, Out;

    public Direction getOpposite() {
        switch (this) {
        case In:
            return Out;
        case Out:
            return In;
        default:
            return null;
        }
    }

    public int getInc() {
        switch (this) {
        case In:
            return -1;
        case Out:
            return 1;
        default:
            return 0;
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
