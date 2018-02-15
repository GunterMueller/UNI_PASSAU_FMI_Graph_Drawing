// =============================================================================
//
//   InsideStrategy.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.label.alignment;

import org.graffiti.graphics.GraphicAttributeConstants;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class InsideAlignment extends LabelAlignment {
    private static InsideAlignment singleton = new InsideAlignment();

    public static InsideAlignment get() {
        return singleton;
    }

    private InsideAlignment() {
    }

    @Override
    public double calculateAlignment(double nodeWidth, double nodeHeight,
            double labelWidth, double labelHeight, double frameThickness) {
        return GraphicAttributeConstants.LABEL_DISTANCE + frameThickness;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
