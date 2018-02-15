// =============================================================================
//
//   TranslateCommand.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.java2d.label.commands;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class TranslateCommand extends Java2DLabelCommand {
    private double tx;
    private double ty;

    public TranslateCommand(double tx, double ty) {
        this.tx = tx;
        this.ty = ty;
    }

    @Override
    public void execute(CommandContext commandContext) {
        commandContext.graphics.translate(tx, ty);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
