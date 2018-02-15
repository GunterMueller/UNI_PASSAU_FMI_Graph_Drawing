// =============================================================================
//
//   OpenGLFont.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl;

import java.awt.Font;

import org.graffiti.plugins.views.fast.FastFont;

import com.sun.opengl.util.j2d.TextRenderer;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class OpenGLFont extends FastFont {
    private TextRenderer textRenderer;

    protected OpenGLFont(Font font) {
        super(font);
        reset();
    }

    public TextRenderer getTextRenderer() {
        return textRenderer;
    }

    protected void reset() {
        textRenderer = new TextRenderer(font);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
