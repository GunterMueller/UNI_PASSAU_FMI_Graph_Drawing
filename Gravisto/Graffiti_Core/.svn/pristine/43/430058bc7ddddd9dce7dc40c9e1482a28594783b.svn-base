// =============================================================================
//
//   DrawImageCommand.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.java2d.label.commands;

import org.graffiti.plugins.views.fast.java2d.Java2DImage;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class DrawImageCommand extends Java2DLabelCommand {
    private Java2DImage image;
    private int x;
    private int y;

    public DrawImageCommand(Java2DImage image, int x, int y) {
        this.image = image;
        this.x = x;
        this.y = y;
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
        commandContext.graphics.drawImage(image.getImage(), x, y, null);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
