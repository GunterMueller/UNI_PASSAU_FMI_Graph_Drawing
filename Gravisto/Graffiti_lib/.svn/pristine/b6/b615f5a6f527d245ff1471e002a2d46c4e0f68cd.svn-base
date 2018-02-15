// =============================================================================
//
//   SetStrokeCommand.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.java2d.label.commands;

import java.awt.Stroke;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class SetStrokeCommand extends Java2DLabelCommand {
    private Stroke stroke;

    public SetStrokeCommand(Stroke stroke) {
        this.stroke = stroke;
    }

    @Override
    public void execute(CommandContext commandContext) {
        commandContext.graphics.setStroke(stroke);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
