// =============================================================================
//
//   Range.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.compactor;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class Range {
    protected RealNode minNode;
    protected RealNode maxNode;
    
    public Range(RealNode minNode, RealNode maxNode) {
        this.minNode = minNode;
        this.maxNode = maxNode;
    }
}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
