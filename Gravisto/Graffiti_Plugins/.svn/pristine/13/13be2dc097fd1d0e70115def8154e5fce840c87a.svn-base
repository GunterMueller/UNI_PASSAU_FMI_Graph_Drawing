// =============================================================================
//
//   SetClipCommand.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.java2d.label.commands;

import java.awt.Shape;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class SetClipCommand extends Java2DLabelCommand {
    private Shape clip;

    public SetClipCommand(Shape clip) {
        this.clip = clip;
    }

    @Override
    public void execute(CommandContext commandContext) {
        commandContext.graphics.setClip(clip);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
