// =============================================================================
//
//   AbstractJava2DRep.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.java2d;

import java.awt.Graphics2D;

import org.graffiti.plugins.views.fast.AbstractRep;
import org.graffiti.plugins.views.fast.java2d.label.Java2DLabel;
import org.graffiti.plugins.views.fast.java2d.label.commands.Java2DLabelCommand;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class AbstractJava2DRep extends
        AbstractRep<Java2DLabel, Java2DLabelCommand> {

    protected AbstractJava2DRep() {
        super();
    }

    public final void draw(Graphics2D g, DrawingSet set) {
        onDraw(g, set);
        if (labels != null) {
            for (Java2DLabel label : labels) {
                label.draw(g, set);
                g.setTransform(set.affineTransform);
            }
        }
    }

    protected abstract void onDraw(Graphics2D g, DrawingSet set);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
