// =============================================================================
//
//   DrawLineCommand.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl.label.commands;

import java.awt.geom.GeneralPath;

import org.graffiti.plugins.views.fast.opengl.TesselationData;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class DrawLineCommand extends OpenGLLabelCommand {
    protected int x1;
    protected int y1;
    protected int x2;
    protected int y2;
    private TesselationData data;

    public DrawLineCommand() {
    }

    public DrawLineCommand(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public void execute(FirstPhaseContext context) {
        GeneralPath path = new GeneralPath();
        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        data = context.drawShape(path);
    }

    @Override
    public void execute(SecondPhaseContext context) {
        context.addData(data);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
