// =============================================================================
//
//   SetColorCommand.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.java2d.label.commands;

import java.awt.Color;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class SetColorCommand extends Java2DLabelCommand {
    private Color color;

    public SetColorCommand(Color color) {
        this.color = color;
    }

    @Override
    public void execute(CommandContext commandContext) {
        commandContext.graphics.setColor(color);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
