// =============================================================================
//
//   FillCommand.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl.label.commands;

import java.awt.Shape;

import org.graffiti.plugins.views.fast.opengl.TesselationData;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class FillCommand extends OpenGLLabelCommand {
    private Shape shape;
    private TesselationData data;

    public FillCommand(Shape shape) {
        this.shape = shape;
    }

    @Override
    public void execute(FirstPhaseContext context) {
        data = context.fillShape(shape);
    }

    @Override
    public void execute(SecondPhaseContext context) {
        context.addData(data);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
