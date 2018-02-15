// =============================================================================
//
//   AnyNode.java
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
public abstract class AnyNode {
    
    protected int index;
    
    protected int x;
    
    protected RealNode east;
    
    protected abstract NodeType getType();
}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
