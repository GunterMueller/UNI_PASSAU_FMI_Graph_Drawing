// =============================================================================
//
//   SetStrokeCommand.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl.label.commands;

import java.awt.Stroke;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class SetStrokeCommand extends OpenGLLabelCommand {
    private Stroke stroke;

    public SetStrokeCommand(Stroke stroke) {
        this.stroke = stroke;
    }

    @Override
    public void execute(FirstPhaseContext context) {
        context.setStroke(stroke);
    }

    @Override
    public void execute(SecondPhaseContext context) {
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
