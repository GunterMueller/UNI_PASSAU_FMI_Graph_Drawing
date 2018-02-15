// =============================================================================
//
//   NorthStep.java
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
public class NorthStep extends Step {
    private RealNode nwNode;
//    private RealNode neNode;
//    private AnyNode swNode;
    private RealNode seNode;
    
    public NorthStep(AnyNode fromNode, RealNode toNode) {
        this.nwNode = toNode;
//        this.neNode = toNode.east;
//        this.swNode = fromNode;
        this.seNode = fromNode.east;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void contract() {
        if (nwNode != null) {
            nwNode.east = seNode;
        }
        
        if (seNode != null) {
            seNode.west = nwNode;
        }
    }
}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
