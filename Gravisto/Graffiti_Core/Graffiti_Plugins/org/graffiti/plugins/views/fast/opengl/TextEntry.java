// =============================================================================
//
//   TextEntry.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl;

import java.awt.Color;

import javax.media.opengl.GL;

import com.sun.opengl.util.j2d.TextRenderer;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class TextEntry {
    private double x;
    private double y;
    private double dx;
    private double dy;
    private double depth;
    private String string;
    private Color color;
    private OpenGLFont font;

    public TextEntry(double x, double y, String string, Color color,
            OpenGLFont font) {
        this.x = x;
        this.y = y;
        this.string = string;
        this.color = color;
        this.font = font;
    }

    public void draw(GL gl, TextRenderer textRenderer) {
        textRenderer.setColor(color);
        textRenderer.draw3D(string, (float) (x + dx), (float) -(y + dy),
                (float) depth, 1.0f);
    }

    public void setPosition(double dx, double dy, double depth) {
        this.dx = dx;
        this.dy = dy;
        this.depth = depth;
    }

    public OpenGLFont getFont() {
        return font;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
