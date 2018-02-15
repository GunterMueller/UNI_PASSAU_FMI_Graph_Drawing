// =============================================================================
//
//   SetFontCommand.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl.label.commands;

import org.graffiti.plugins.views.fast.opengl.OpenGLFont;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class SetFontCommand extends OpenGLLabelCommand {
    private OpenGLFont font;

    public SetFontCommand(OpenGLFont font) {
        this.font = font;
    }

    @Override
    public void execute(FirstPhaseContext context) {
    }

    @Override
    public void execute(SecondPhaseContext context) {
        context.setFont(font);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
