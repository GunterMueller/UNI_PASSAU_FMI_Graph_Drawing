// =============================================================================
//
//   SetRenderingHintCommand.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.java2d.label.commands;

import java.awt.RenderingHints.Key;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class SetRenderingHintCommand extends Java2DLabelCommand {
    private Key key;
    private Object value;

    public SetRenderingHintCommand(Key key, Object value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public void execute(CommandContext commandContext) {
        System.out.println(key.toString() + " => " + value.toString());
        commandContext.graphics.setRenderingHint(key, value);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
