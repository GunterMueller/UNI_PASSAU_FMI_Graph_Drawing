// =============================================================================
//
//   OpenGLCommandFactory.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl.label;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.RenderingHints.Key;

import org.graffiti.plugins.views.fast.FastFont;
import org.graffiti.plugins.views.fast.FastImage;
import org.graffiti.plugins.views.fast.label.CommandFactory;
import org.graffiti.plugins.views.fast.opengl.OpenGLFont;
import org.graffiti.plugins.views.fast.opengl.OpenGLImage;
import org.graffiti.plugins.views.fast.opengl.label.commands.DrawBorderLineCommand;
import org.graffiti.plugins.views.fast.opengl.label.commands.DrawImageCommand;
import org.graffiti.plugins.views.fast.opengl.label.commands.DrawLineCommand;
import org.graffiti.plugins.views.fast.opengl.label.commands.DrawOvalCommand;
import org.graffiti.plugins.views.fast.opengl.label.commands.DrawRectCommand;
import org.graffiti.plugins.views.fast.opengl.label.commands.DrawStringCommand;
import org.graffiti.plugins.views.fast.opengl.label.commands.FillCommand;
import org.graffiti.plugins.views.fast.opengl.label.commands.FillOvalCommand;
import org.graffiti.plugins.views.fast.opengl.label.commands.FillRectCommand;
import org.graffiti.plugins.views.fast.opengl.label.commands.OpenGLLabelCommand;
import org.graffiti.plugins.views.fast.opengl.label.commands.SetClipCommand;
import org.graffiti.plugins.views.fast.opengl.label.commands.SetColorCommand;
import org.graffiti.plugins.views.fast.opengl.label.commands.SetFontCommand;
import org.graffiti.plugins.views.fast.opengl.label.commands.SetRenderingHintCommand;
import org.graffiti.plugins.views.fast.opengl.label.commands.SetStrokeCommand;
import org.graffiti.plugins.views.fast.opengl.label.commands.TranslateCommand;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class OpenGLCommandFactory extends
        CommandFactory<OpenGLLabel, OpenGLLabelCommand> {

    @Override
    public OpenGLLabelCommand createDrawBorderLine(Rectangle bounds, int side,
            int lineWidth, boolean solid) {
        return new DrawBorderLineCommand(bounds, side, lineWidth, solid);
    }

    @Override
    public OpenGLLabelCommand createDrawImage(FastImage<?, ?> fastImage, int x,
            int y) {
        return new DrawImageCommand((OpenGLImage) fastImage, x, y);
    }

    @Override
    public OpenGLLabelCommand createDrawLine(int x1, int y1, int x2, int y2) {
        return new DrawLineCommand(x1, y1, x2, y2);
    }

    @Override
    public OpenGLLabelCommand createDrawOval(int x, int y, int width, int height) {
        return new DrawOvalCommand(x, y, width, height);
    }

    @Override
    public OpenGLLabelCommand createDrawRect(int x, int y, int width, int height) {
        return new DrawRectCommand(x, y, width, height);
    }

    @Override
    public OpenGLLabelCommand createDrawString(String string, float x, float y) {
        return new DrawStringCommand(string, x, y);
    }

    @Override
    public OpenGLLabelCommand createFill(Shape shape) {
        return new FillCommand(shape);
    }

    @Override
    public OpenGLLabelCommand createFillOval(int x, int y, int width, int height) {
        return new FillOvalCommand(x, y, width, height);
    }

    @Override
    public OpenGLLabelCommand createFillRect(int x, int y, int width, int height) {
        return new FillRectCommand(x, y, width, height);
    }

    @Override
    public OpenGLLabelCommand createSetClip(Shape clip) {
        return new SetClipCommand(clip);
    }

    @Override
    public OpenGLLabelCommand createSetColor(Color color) {
        return new SetColorCommand(color);
    }

    @Override
    public OpenGLLabelCommand createSetFont(FastFont font) {
        return new SetFontCommand((OpenGLFont) font);
    }

    @Override
    public OpenGLLabelCommand createSetRenderingHint(Key key, Object value) {
        return new SetRenderingHintCommand(key, value);
    }

    @Override
    public OpenGLLabelCommand createSetStroke(Stroke stroke) {
        return new SetStrokeCommand(stroke);
    }

    @Override
    public OpenGLLabelCommand createTranslate(double tx, double ty) {
        return new TranslateCommand(tx, ty);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
