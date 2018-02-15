// =============================================================================
//
//   TextListener.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.label;

import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.util.LinkedList;

import org.graffiti.plugins.views.fast.FastFont;
import org.xhtmlrenderer.extend.FontContext;
import org.xhtmlrenderer.extend.OutputDevice;
import org.xhtmlrenderer.extend.TextRenderer;
import org.xhtmlrenderer.render.FSFont;
import org.xhtmlrenderer.render.FSFontMetrics;
import org.xhtmlrenderer.render.LineMetricsAdapter;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class TextListener<L extends Label<L, LC>, LC extends LabelCommand>
        implements TextRenderer {
    private static final float INITIAL_SCALE = 1.0f;
    private static final int INITIAL_LEVEL = HIGH;
    private CommandFactory<L, LC> commandFactory;
    private LinkedList<LC> commands;
    private Graphics2D graphics;
    private FontRenderContext fontRenderContext;
    private float scale;
    private int level;

    public TextListener(CommandFactory<L, LC> commandFactory,
            LinkedList<LC> commands, Graphics2D graphics,
            FontRenderContext fontRenderContext) {
        this.commandFactory = commandFactory;
        this.commands = commands;
        this.graphics = graphics;
        this.fontRenderContext = fontRenderContext;
        scale = INITIAL_SCALE;
        level = INITIAL_LEVEL;
    }

    public void drawString(OutputDevice outputDevice, String string, float x,
            float y) {
        commands.addLast(commandFactory.createDrawString(string, x, y));
    }

    public FSFontMetrics getFSFontMetrics(FontContext context, FSFont font,
            String string) {
        return new LineMetricsAdapter(((FastFont) font).getFont()
                .getLineMetrics(string, fontRenderContext));
    }

    public float getFontScale() {
        return scale;
    }

    public int getSmoothingLevel() {
        return level;
    }

    public int getWidth(FontContext context, FSFont font, String string) {
        return (int) Math.ceil(graphics.getFontMetrics(
                ((FastFont) font).getFont()).getStringBounds(string, graphics)
                .getWidth());
    }

    public void setFontScale(float scale) {
        this.scale = scale;
    }

    public void setSmoothingLevel(int level) {
        this.level = level;
    }

    public void setSmoothingThreshold(float threshold) {
    }

    public void setup(FontContext context) {
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
