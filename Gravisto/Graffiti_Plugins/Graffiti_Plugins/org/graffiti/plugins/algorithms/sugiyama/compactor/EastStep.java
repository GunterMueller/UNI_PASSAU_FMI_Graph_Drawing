// =============================================================================
//
//   EastStep.java
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
public class EastStep extends Step {
    private DummyNode dummyNode;
    
    public EastStep(DummyNode dummyNode) {
        this.dummyNode = dummyNode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void colorize(Color color) {
        dummyNode.color = color;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void contract() {
        RealNode north = dummyNode.north;
        RealNode south = dummyNode.south;
        north.replaceSouth(dummyNode.northInvIndex, south, dummyNode.southInvIndex);
        south.replaceNorth(dummyNode.southInvIndex, north, dummyNode.northInvIndex);
    }
}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
