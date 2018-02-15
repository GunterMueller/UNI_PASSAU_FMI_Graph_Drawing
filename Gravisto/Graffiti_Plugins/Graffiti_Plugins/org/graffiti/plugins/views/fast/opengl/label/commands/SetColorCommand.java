// =============================================================================
//
//   SetColorCommand.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl.label.commands;

import java.awt.Color;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class SetColorCommand extends OpenGLLabelCommand {
    private Color color;

    public SetColorCommand(Color color) {
        this.color = color;
    }

    @Override
    public void execute(FirstPhaseContext context) {
    }

    @Override
    public void execute(SecondPhaseContext context) {
        context.setColor(color);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
