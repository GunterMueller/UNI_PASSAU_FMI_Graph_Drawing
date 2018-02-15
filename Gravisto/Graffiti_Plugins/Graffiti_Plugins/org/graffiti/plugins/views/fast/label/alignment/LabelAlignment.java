// =============================================================================
//
//   NodeLabelAlignment.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.label.alignment;

import java.util.HashMap;
import java.util.Map;

import org.graffiti.graphics.GraphicAttributeConstants;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class LabelAlignment implements GraphicAttributeConstants {
    private static Map<String, LabelAlignment> mapX;
    private static Map<String, LabelAlignment> mapY;

    static {

        // TODO move initialization closer to alignment string declaratons

        mapX = new HashMap<String, LabelAlignment>();

        mapX.put(CENTERED, CenterAlignment.get());
        mapX.put(RIGHT_OUTSIDE, new MirroringAlignment(OutsideAlignment.get()));
        mapX.put(RIGHT_INSIDE, new MirroringAlignment(InsideAlignment.get()));
        mapX.put(LEFT_OUTSIDE, OutsideAlignment.get());
        mapX.put(LEFT_INSIDE, InsideAlignment.get());

        mapY = new HashMap<String, LabelAlignment>();

        mapY.put(CENTERED, new SwitchingAlignment(CenterAlignment.get()));
        mapY.put(BOTTOM_OUTSIDE, new SwitchingAlignment(new MirroringAlignment(
                OutsideAlignment.get())));
        mapY.put(BOTTOM_INSIDE, new SwitchingAlignment(new MirroringAlignment(
                InsideAlignment.get())));
        mapY.put(TOP_OUTSIDE, new SwitchingAlignment(OutsideAlignment.get()));
        mapY.put(TOP_INSIDE, new SwitchingAlignment(InsideAlignment.get()));

    }

    public static Double calculateX(String alignment, double nodeWidth,
            double nodeHeight, double labelWidth, double labelHeight,
            double frameThickness) {
        LabelAlignment la = mapX.get(alignment);
        if (la == null)
            return null;
        else
            return la.calculateAlignment(nodeWidth, nodeHeight, labelWidth,
                    labelHeight, frameThickness);
    }

    public static Double calculateY(String alignment, double nodeWidth,
            double nodeHeight, double labelWidth, double labelHeight,
            double frameThickness) {
        LabelAlignment la = mapY.get(alignment);
        if (la == null)
            return null;
        else
            return la.calculateAlignment(nodeWidth, nodeHeight, labelWidth,
                    labelHeight, frameThickness);
    }

    public abstract double calculateAlignment(double nodeWidth,
            double nodeHeight, double labelWidth, double labelHeight,
            double frameThickness);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
