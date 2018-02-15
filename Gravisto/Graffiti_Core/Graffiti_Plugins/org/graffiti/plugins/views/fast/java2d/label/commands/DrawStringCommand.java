// =============================================================================
//
//   DrawStringCommand.java
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
public class DrawStringCommand extends Java2DLabelCommand {
    private String string;
    private float x;
    private float y;

    public DrawStringCommand(String string, float x, float y) {
        this.string = string;
        this.x = x;
        this.y = y;
    }

    @Override
    public void execute(CommandContext commandContext) {
        commandContext.graphics.drawString(string, x, y);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
