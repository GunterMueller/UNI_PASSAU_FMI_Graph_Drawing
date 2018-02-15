// =============================================================================
//
//   FillOvalCommand.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl.label.commands;

import java.awt.geom.Ellipse2D;

import org.graffiti.plugins.views.fast.opengl.TesselationData;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class FillOvalCommand extends OpenGLLabelCommand {
    private int x;
    private int y;
    private int width;
    private int height;
    private TesselationData data;

    public FillOvalCommand(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void execute(FirstPhaseContext context) {
        data = context.fillShape(new Ellipse2D.Double(x, y, width, height));
    }

    @Override
    public void execute(SecondPhaseContext context) {
        context.addData(data);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
