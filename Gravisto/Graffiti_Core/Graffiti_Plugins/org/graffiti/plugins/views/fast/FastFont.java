// =============================================================================
//
//   AbstractFont.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast;

import java.awt.Font;

import org.xhtmlrenderer.render.FSFont;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class FastFont implements FSFont {
    protected Font font;

    public FastFont(Font font) {
        this.font = font;
    }

    public float getSize2D() {
        return font.getSize2D();
    }

    public Font getFont() {
        return font;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
