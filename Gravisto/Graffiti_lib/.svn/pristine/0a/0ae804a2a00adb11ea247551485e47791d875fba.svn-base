// =============================================================================
//
//   OpenGLFontManager.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl;

import java.awt.Font;

import org.graffiti.plugins.views.fast.FontManager;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class OpenGLFontManager extends FontManager<OpenGLFont> {
    public OpenGLFontManager() {
        //
    }

    @Override
    protected OpenGLFont createFont(Font font) {
        return new OpenGLFont(font);
    }

    @Override
    protected void onDeleteFont(OpenGLFont font) {
        //
    }

    public void resetFonts() {
        for (OpenGLFont font : map.values()) {
            font.reset();
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
