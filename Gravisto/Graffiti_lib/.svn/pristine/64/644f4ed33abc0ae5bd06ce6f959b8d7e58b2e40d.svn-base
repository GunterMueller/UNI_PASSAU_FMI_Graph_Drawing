// =============================================================================
//
//   TextBuffer.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL;

import com.sun.opengl.util.j2d.TextRenderer;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class TextBuffer {
    private Map<OpenGLFont, LinkedList<TextEntry>> map;

    public TextBuffer() {
        map = new HashMap<OpenGLFont, LinkedList<TextEntry>>();
    }

    public void addTexts(List<TextEntry> entries, double dx, double dy,
            double depth) {
        for (TextEntry entry : entries) {
            entry.setPosition(dx, dy, depth);
            addText(entry);
        }
    }

    private void addText(TextEntry entry) {
        OpenGLFont font = entry.getFont();
        LinkedList<TextEntry> list = map.get(font);
        if (list == null) {
            list = new LinkedList<TextEntry>();
            map.put(font, list);
        }
        list.add(entry);
    }

    public void draw(GL gl) {
        gl.glPushAttrib(GL.GL_CURRENT_BIT);
        gl.glPushMatrix();
        gl.glScaled(1.0, -1.0, 1.0);
        for (Map.Entry<OpenGLFont, LinkedList<TextEntry>> entry : map
                .entrySet()) {
            TextRenderer textRenderer = entry.getKey().getTextRenderer();
            textRenderer.begin3DRendering();
            for (TextEntry text : entry.getValue()) {
                text.draw(gl, textRenderer);
            }
            textRenderer.end3DRendering();
        }
        gl.glPopMatrix();
        gl.glPopAttrib();
        map.clear();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
