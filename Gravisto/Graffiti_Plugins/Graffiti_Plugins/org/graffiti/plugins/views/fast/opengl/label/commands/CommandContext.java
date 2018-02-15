// =============================================================================
//
//   CommandContext.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl.label.commands;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.plugins.views.fast.opengl.OpenGLFont;
import org.graffiti.plugins.views.fast.opengl.TesselationData;
import org.graffiti.plugins.views.fast.opengl.Tesselator;
import org.graffiti.plugins.views.fast.opengl.TextEntry;
import org.graffiti.plugins.views.fast.opengl.buffer.BufferBlock;
import org.graffiti.plugins.views.fast.opengl.buffer.BufferManager;
import org.graffiti.plugins.views.fast.opengl.buffer.RebuildBufferException;
import org.graffiti.plugins.views.fast.opengl.buffer.TriangleBuffer;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class CommandContext implements FirstPhaseContext, SecondPhaseContext {
    private Area clip;
    private Color color;
    private Stroke stroke;
    private OpenGLFont font;
    private double dx;
    private double dy;
    private BufferManager bufferManager;
    private TriangleBuffer buffer;
    private Tesselator tesselator;
    private LinkedList<TextEntry> textList;

    public CommandContext() {
        clip = null;
        color = Color.BLACK;
        stroke = new BasicStroke(1.0f);
        dx = 0;
        dy = 0;
        bufferManager = new BufferManager();
        buffer = bufferManager.createBuffer();
        tesselator = Tesselator.get();
        textList = new LinkedList<TextEntry>();
    }

    public void finishFirstPhase() {
        bufferManager.finishEstimate();
    }

    public TesselationData drawShape(Shape shape) {
        return fillShape(stroke.createStrokedShape(shape));
    }

    public TesselationData fillShape(Shape shape) {
        Shape clippedShape = shape;
        if (clip != null) {
            Area area = new Area(shape);
            area.intersect(clip);
            clippedShape = area;
        }
        TesselationData data = tesselator.tesselate(clippedShape);
        buffer.incEstimate(data.getVertexCount(), data.getIndexCount());
        return data;
    }

    public void addData(TesselationData data) {
        try {
            BufferBlock block = buffer.allocate(data.getVertexCount(), data
                    .getIndexCount());
            block.fillWith(data, 0.0);
            block.setColor(color);
            block.translate(dx, dy);
        } catch (RebuildBufferException e) {
            assert (false);
        }
    }

    public void addText(String string, double x, double y) {
        textList.add(new TextEntry(x + dx, y + dy, string, color, font));
    }

    public void setClip(Shape clip) {
        this.clip = new Area(clip);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setFont(OpenGLFont font) {
        this.font = font;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    public void translate(double dx, double dy) {
        this.dx += dx;
        this.dy += dy;
    }

    public List<TextEntry> getTextList() {
        return textList;
    }

    public TriangleBuffer getBuffer() {
        return buffer;
    }

    public BufferManager getBufferManager() {
        return bufferManager;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
