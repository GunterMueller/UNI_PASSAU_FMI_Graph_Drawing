// =============================================================================
//
//   Java2DCommandFactory.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.java2d.label;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.RenderingHints.Key;

import org.graffiti.plugins.views.fast.FastFont;
import org.graffiti.plugins.views.fast.FastImage;
import org.graffiti.plugins.views.fast.java2d.Java2DImage;
import org.graffiti.plugins.views.fast.java2d.label.commands.DrawBorderLineCommand;
import org.graffiti.plugins.views.fast.java2d.label.commands.DrawImageCommand;
import org.graffiti.plugins.views.fast.java2d.label.commands.DrawLineCommand;
import org.graffiti.plugins.views.fast.java2d.label.commands.DrawOvalCommand;
import org.graffiti.plugins.views.fast.java2d.label.commands.DrawRectCommand;
import org.graffiti.plugins.views.fast.java2d.label.commands.DrawStringCommand;
import org.graffiti.plugins.views.fast.java2d.label.commands.FillCommand;
import org.graffiti.plugins.views.fast.java2d.label.commands.FillOvalCommand;
import org.graffiti.plugins.views.fast.java2d.label.commands.FillRectCommand;
import org.graffiti.plugins.views.fast.java2d.label.commands.Java2DLabelCommand;
import org.graffiti.plugins.views.fast.java2d.label.commands.SetClipCommand;
import org.graffiti.plugins.views.fast.java2d.label.commands.SetColorCommand;
import org.graffiti.plugins.views.fast.java2d.label.commands.SetFontCommand;
import org.graffiti.plugins.views.fast.java2d.label.commands.SetRenderingHintCommand;
import org.graffiti.plugins.views.fast.java2d.label.commands.SetStrokeCommand;
import org.graffiti.plugins.views.fast.java2d.label.commands.TranslateCommand;
import org.graffiti.plugins.views.fast.label.CommandFactory;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class Java2DCommandFactory extends
        CommandFactory<Java2DLabel, Java2DLabelCommand> {
    @Override
    public Java2DLabelCommand createDrawString(String string, float x, float y) {
        return new DrawStringCommand(string, x, y);
    }

    @Override
    public Java2DLabelCommand createDrawBorderLine(Rectangle bounds, int side,
            int lineWidth, boolean solid) {
        return new DrawBorderLineCommand(bounds, side, lineWidth, solid);
    }

    @Override
    public Java2DLabelCommand createDrawImage(FastImage<?, ?> fastImage, int x,
            int y) {
        return new DrawImageCommand((Java2DImage) fastImage, x, y);
    }

    @Override
    public Java2DLabelCommand createDrawLine(int x1, int y1, int x2, int y2) {
        return new DrawLineCommand(x1, y1, x2, y2);
    }

    @Override
    public Java2DLabelCommand createDrawOval(int x, int y, int width, int height) {
        return new DrawOvalCommand(x, y, width, height);
    }

    @Override
    public Java2DLabelCommand createDrawRect(int x, int y, int width, int height) {
        return new DrawRectCommand(x, y, width, height);
    }

    @Override
    public Java2DLabelCommand createFill(Shape shape) {
        return new FillCommand(shape);
    }

    @Override
    public Java2DLabelCommand createFillOval(int x, int y, int width, int height) {
        return new FillOvalCommand(x, y, width, height);
    }

    @Override
    public Java2DLabelCommand createFillRect(int x, int y, int width, int height) {
        return new FillRectCommand(x, y, width, height);
    }

    @Override
    public Java2DLabelCommand createSetClip(Shape clip) {
        return new SetClipCommand(clip);
    }

    @Override
    public Java2DLabelCommand createSetColor(Color color) {
        return new SetColorCommand(color);
    }

    @Override
    public Java2DLabelCommand createSetFont(FastFont font) {
        return new SetFontCommand(font);
    }

    @Override
    public Java2DLabelCommand createSetRenderingHint(Key key, Object value) {
        return new SetRenderingHintCommand(key, value);
    }

    @Override
    public Java2DLabelCommand createSetStroke(Stroke stroke) {
        return new SetStrokeCommand(stroke);
    }

    @Override
    public Java2DLabelCommand createTranslate(double tx, double ty) {
        return new TranslateCommand(tx, ty);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
