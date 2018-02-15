// =============================================================================
//
//   OutsideStrategy.java
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
public class OutsideAlignment extends LabelAlignment {
    private static OutsideAlignment singleton = new OutsideAlignment();

    public static OutsideAlignment get() {
        return singleton;
    }

    private OutsideAlignment() {
    }

    @Override
    public double calculateAlignment(double nodeWidth, double nodeHeight,
            double labelWidth, double labelHeight, double frameThickness) {
        return -labelWidth - GraphicAttributeConstants.LABEL_DISTANCE;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
