// =============================================================================
//
//   SetFontCommand.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.java2d.label.commands;

import org.graffiti.plugins.views.fast.FastFont;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class SetFontCommand extends Java2DLabelCommand {
    private FastFont font;

    public SetFontCommand(FastFont font) {
        this.font = font;
    }

    @Override
    public void execute(CommandContext commandContext) {
        commandContext.graphics.setFont(font.getFont());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
