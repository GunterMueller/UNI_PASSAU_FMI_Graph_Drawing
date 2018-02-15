// =============================================================================
//
//   TranslateCommand.java
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
public class TranslateCommand extends OpenGLLabelCommand {
    private double tx;
    private double ty;

    public TranslateCommand(double tx, double ty) {
        this.tx = tx;
        this.ty = ty;
    }

    @Override
    public void execute(FirstPhaseContext context) {
    }

    @Override
    public void execute(SecondPhaseContext context) {
        context.translate(tx, ty);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
