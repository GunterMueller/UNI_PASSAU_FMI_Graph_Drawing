// =============================================================================
//
//   DrawLineCommand.java
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
public class DrawLineCommand extends Java2DLabelCommand {
    protected int x1;
    protected int y1;
    protected int x2;
    protected int y2;

    public DrawLineCommand() {
    }

    public DrawLineCommand(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.java2d.label.commands.AbstractLabelCommand
     * #
     * execute(org.graffiti.plugins.views.fast.java2d.label.commands.CommandContext
     * )
     */
    @Override
    public void execute(CommandContext commandContext) {
        commandContext.graphics.drawLine(x1, y1, x2, y2);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
