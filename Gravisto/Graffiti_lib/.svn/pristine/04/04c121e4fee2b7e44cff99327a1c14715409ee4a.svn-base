// =============================================================================
//
//   SouthStep.java
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
public class SouthStep extends Step {
//    private AnyNode nwNode;
    private RealNode neNode;
    private RealNode swNode;
//    private RealNode seNode;
    
    public SouthStep(AnyNode fromNode, RealNode toNode) {
//        this.nwNode = fromNode;
        this.neNode = fromNode.east;
        this.swNode = toNode;
//        this.seNode = toNode.east;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void contract() {
        if (swNode != null) {
            swNode.east = neNode;
        }
        
        if (neNode != null) {
            neNode.west = swNode;
        }
    }
}
// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
