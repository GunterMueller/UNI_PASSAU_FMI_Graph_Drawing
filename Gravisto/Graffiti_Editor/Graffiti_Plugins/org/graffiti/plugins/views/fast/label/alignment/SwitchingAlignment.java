// =============================================================================
//
//   SwitchingStrategy.java
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
public class SwitchingAlignment extends LabelAlignment {
    LabelAlignment alignment;

    public SwitchingAlignment(LabelAlignment alignment) {
        this.alignment = alignment;
    }

    @Override
    public double calculateAlignment(double nodeWidth, double nodeHeight,
            double labelWidth, double labelHeight, double frameThickness) {
        return alignment.calculateAlignment(nodeHeight, nodeWidth, labelHeight,
                labelWidth, frameThickness);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
