// =============================================================================
//
//   ConcreteFontResolver.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.label;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.HashSet;
import java.util.Set;

import org.graffiti.plugins.views.fast.FontManager;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.value.FontSpecification;
import org.xhtmlrenderer.extend.FontResolver;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.render.FSFont;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class LabelFontResolver implements FontResolver {
    private double scale;
    private FontManager<?> fontManager;
    private static Set<String> fontNames;

    public LabelFontResolver(double scale, FontManager<?> fontManager) {
        this.scale = scale;
        this.fontManager = fontManager;
        if (fontNames == null) {
            fontNames = new HashSet<String>();
            for (String fontName : GraphicsEnvironment
                    .getLocalGraphicsEnvironment()
                    .getAvailableFontFamilyNames()) {
                fontNames.add(fontName);
            }
        }

    }

    public void flushCache() {
    }

    public FSFont resolveFont(SharedContext sharedContext,
            FontSpecification fontSpecification) {
        // Derived from AWTFontResolver.createFont
        // and AWTFontResolver.resolveFont.
        IdentValue fontStyle = fontSpecification.fontStyle;
        IdentValue fontWeight = fontSpecification.fontWeight;
        String fontName = getAvailableFontName(fontSpecification.families,
                fontStyle);
        int style = Font.PLAIN;
        if (fontWeight == IdentValue.BOLD
                || fontWeight == IdentValue.FONT_WEIGHT_700
                || fontWeight == IdentValue.FONT_WEIGHT_800
                || fontWeight == IdentValue.FONT_WEIGHT_900) {
            style |= Font.BOLD;
        }
        if (fontStyle == IdentValue.ITALIC || fontStyle == IdentValue.OBLIQUE) {
            style |= Font.ITALIC;
        }
        double size = fontSpecification.size * scale;
        return fontManager.acquireFont(fontName, style, (int) size);
    }

    private String getAvailableFontName(String[] families, IdentValue style) {
        for (String fontName : families) {
            if (fontName.length() == 0) {
                continue;
            }
            // Derived from AWTFontResolver.createFont
            // and AWTFontResolver.resolveFont.
            if (fontName.charAt(0) == '"') {
                fontName = fontName.substring(1);
            }
            if (fontName.charAt(fontName.length() - 1) == '"') {
                fontName = fontName.substring(0, fontName.length() - 1);
            }
            if (fontName.equals("serif")) {
                fontName = "Serif";
            } else if (fontName.equals("sans-serif")) {
                fontName = "SansSerif";
            } else if (fontName.equals("monospace")) {
                fontName = "Monospaced";
            }
            if (fontName.equals("Serif") && style == IdentValue.OBLIQUE) {
                fontName = "SansSerif";
            } else if (fontName.equals("SansSerif")
                    && style == IdentValue.ITALIC) {
                fontName = "Serif";
            }
            if (fontNames.contains(fontName))
                return fontName;
        }
        if (style == IdentValue.ITALIC)
            return "Serif";
        else
            return "SansSerif";
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
