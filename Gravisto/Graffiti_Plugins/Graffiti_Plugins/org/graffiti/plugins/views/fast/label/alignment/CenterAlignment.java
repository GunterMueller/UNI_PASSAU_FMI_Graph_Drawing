// =============================================================================
//
//   CenteringStrategy.java
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
public class CenterAlignment extends LabelAlignment {
    private static CenterAlignment singleton = new CenterAlignment();

    public static CenterAlignment get() {
        return singleton;
    }

    private CenterAlignment() {
    }

    @Override
    public double calculateAlignment(double nodeWidth, double nodeHeight,
            double labelWidth, double labelHeight, double frameThickness) {
        return (nodeWidth - labelWidth) / 2.0;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
