// =============================================================================
//
//   OpenGLLabel.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl.label;

import java.util.List;

import javax.media.opengl.GL;

import org.graffiti.graph.GraphElement;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.plugins.views.fast.label.Label;
import org.graffiti.plugins.views.fast.opengl.TextBuffer;
import org.graffiti.plugins.views.fast.opengl.TextEntry;
import org.graffiti.plugins.views.fast.opengl.buffer.BufferManager;
import org.graffiti.plugins.views.fast.opengl.buffer.TriangleBuffer;
import org.graffiti.plugins.views.fast.opengl.label.commands.OpenGLLabelCommand;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class OpenGLLabel extends Label<OpenGLLabel, OpenGLLabelCommand> {
    private BufferManager bufferManager;
    private TriangleBuffer buffer;
    private List<TextEntry> textList;

    OpenGLLabel(GraphElement element, LabelAttribute attribute,
            OpenGLLabelManager factory) {
        super(element, attribute, factory);
    }

    @Override
    protected OpenGLLabel getThis() {
        return this;
    }

    protected void setBufferManager(BufferManager bufferManager) {
        this.bufferManager = bufferManager;
    }

    protected void setBuffer(TriangleBuffer buffer) {
        this.buffer = buffer;
    }

    protected void setTextList(List<TextEntry> textList) {
        this.textList = textList;
    }

    public void draw(GL gl, double depth) {
        if (buffer.isEmpty())
            return;
        gl.glPushMatrix();
        gl.glTranslated(0.5 + left, 0.5 + top, depth);
        bufferManager.applyBufferPointers(gl);
        buffer.draw(gl);
        gl.glPopMatrix();
    }

    public void addTextsToBuffer(TextBuffer textBuffer, double depth) {
        textBuffer.addTexts(textList, left, top, depth);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
