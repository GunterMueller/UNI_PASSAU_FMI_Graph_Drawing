// =============================================================================
//
//   STep.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.compactor;

import java.awt.Color;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class Step {
    protected void colorize(Color color) {
    }
    
    protected abstract void contract();
}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
