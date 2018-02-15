// =============================================================================
//
//   HTMLRenderer.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.label;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.RenderingHints.Key;

import org.graffiti.plugins.views.fast.FastFont;
import org.graffiti.plugins.views.fast.FastImage;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class CommandFactory<L extends Label<L, LC>, LC extends LabelCommand> {
    public abstract LC createDrawString(String string, float x, float y);

    public abstract LC createDrawBorderLine(Rectangle bounds, int side,
            int lineWidth, boolean solid);

    public abstract LC createDrawImage(FastImage<?, ?> fastImage, int x, int y);

    public abstract LC createDrawLine(int x1, int y1, int x2, int y2);

    public abstract LC createDrawOval(int x, int y, int width, int height);

    public abstract LC createDrawRect(int x, int y, int width, int height);

    public abstract LC createFill(Shape shape);

    public abstract LC createFillOval(int x, int y, int width, int height);

    public abstract LC createFillRect(int x, int y, int width, int height);

    public abstract LC createSetClip(Shape clip);

    public abstract LC createSetColor(Color color);

    public abstract LC createSetFont(FastFont font);

    public abstract LC createSetRenderingHint(Key key, Object value);

    public abstract LC createSetStroke(Stroke stroke);

    public abstract LC createTranslate(double tx, double ty);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
