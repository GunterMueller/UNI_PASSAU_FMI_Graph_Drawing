// =============================================================================
//
//   DrawBorderLineCommand.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl.label.commands;

import java.awt.Rectangle;

import org.xhtmlrenderer.render.BorderPainter;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class DrawBorderLineCommand extends OpenGLLabelCommand {
    private DrawLineCommand drawLine;

    public DrawBorderLineCommand(Rectangle bounds, int side, int lineWidth,
            boolean solid) {
        // TODO: this is currently just a copy from Java2DOutputDevice.
        int x = bounds.x;
        int y = bounds.y;
        int w = bounds.width;
        int h = bounds.height;

        int adj = solid ? 1 : 0;

        if (side == BorderPainter.TOP) {
            drawLine = new DrawLineCommand(x, y + lineWidth / 2, x + w - adj, y
                    + lineWidth / 2);
        } else if (side == BorderPainter.LEFT) {
            drawLine = new DrawLineCommand(x + lineWidth / 2, y, x + lineWidth
                    / 2, y + h - adj);
        } else if (side == BorderPainter.RIGHT) {
            int offset = lineWidth / 2;
            if (lineWidth % 2 == 1) {
                offset += 1;
            }
            drawLine = new DrawLineCommand(x + w - offset, y, x + w - offset, y
                    + h - adj);
        } else if (side == BorderPainter.BOTTOM) {
            int offset = lineWidth / 2;
            if (lineWidth % 2 == 1) {
                offset += 1;
            }
            drawLine = new DrawLineCommand(x, y + h - offset, x + w - adj, y
                    + h - offset);
        }
    }

    @Override
    public void execute(FirstPhaseContext context) {
        drawLine.execute(context);
    }

    @Override
    public void execute(SecondPhaseContext context) {
        drawLine.execute(context);
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
