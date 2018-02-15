// =============================================================================
//
//   FillCommand.java
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
public class FillCommand extends Java2DLabelCommand {
    private Shape shape;

    public FillCommand(Shape shape) {
        this.shape = shape;
    }

    @Override
    public void execute(CommandContext commandContext) {
        commandContext.graphics.fill(shape);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
