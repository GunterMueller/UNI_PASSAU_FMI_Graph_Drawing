// =============================================================================
//
//   DrawStringCommand.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl.label.commands;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class DrawStringCommand extends OpenGLLabelCommand {
    private String string;
    private float x;
    private float y;

    public DrawStringCommand(String string, float x, float y) {
        this.string = string;
        this.x = x;
        this.y = y;
    }

    @Override
    public void execute(FirstPhaseContext context) {
    }

    @Override
    public void execute(SecondPhaseContext context) {
        context.addText(string, x, y);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
