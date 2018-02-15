// =============================================================================
//
//   MirroringStrategy.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.label.alignment;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class MirroringAlignment extends LabelAlignment {
    private LabelAlignment alignment;

    public MirroringAlignment(LabelAlignment alignment) {
        this.alignment = alignment;
    }

    @Override
    public double calculateAlignment(double nodeWidth, double nodeHeight,
            double labelWidth, double labelHeight, double frameThickness) {
        return nodeWidth
                - labelWidth
                - alignment.calculateAlignment(nodeWidth, nodeHeight,
                        labelWidth, labelHeight, frameThickness);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
