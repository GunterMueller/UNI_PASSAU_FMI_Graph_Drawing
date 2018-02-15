// =============================================================================
//
//   BufferBlock.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl.buffer;

import java.awt.Color;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.Collection;

import org.graffiti.plugins.views.fast.opengl.TesselationData;
import org.graffiti.plugins.views.fast.opengl.TesselationDataList;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class BufferBlock {
    private BufferIndirection bufferIndirection;
    private int vertexOffset;
    private int vertexCount;
    private int indexOffset;
    private int indexCount;

    protected BufferBlock(BufferIndirection bufferIndirection,
            int vertexOffset, int vertexCount, int indexOffset, int indexCount) {
        this.bufferIndirection = bufferIndirection;
        this.vertexOffset = vertexOffset;
        this.vertexCount = vertexCount;
        this.indexOffset = indexOffset;
        this.indexCount = indexCount;
    }

    public boolean isValid() {
        return bufferIndirection != null && bufferIndirection.isValid();
    }

    public void free() {
        if (bufferIndirection == null)
            throw new InvalidBufferBlockException();
        IntBuffer indexBuffer = bufferIndirection.get().getIndexBuffer();
        for (int i = indexOffset; i < indexOffset + indexCount; i++) {
            indexBuffer.put(i, 0);
        }
        bufferIndirection = null;
    }

    public void fillWith(TesselationData data, double depth) {
        fillWith(new TesselationDataList(data), depth);
    }

    public void fillWith(TesselationDataList dataList, double depth) {
        Collection<TesselationData> dataCollection = dataList.getCollection();

        if (dataList.getVertexCount() > vertexCount
                || dataList.getIndexCount() > indexCount)
            throw new BufferOverflowException();
        int i = vertexOffset;
        TriangleBuffer buffer = bufferIndirection.get();
        DoubleBuffer vertexBuffer = buffer.getVertexBuffer();
        for (TesselationData data : dataCollection) {
            Double[] vertices = data.getVertices();
            int vertexCount = data.getVertexCount();
            for (int k = 0; k < vertexCount; k++) {
                vertexBuffer.put(3 * i, vertices[2 * k]);
                vertexBuffer.put(3 * i + 1, vertices[2 * k + 1]);
                vertexBuffer.put(3 * i + 2, depth);
                i++;
            }
        }
        i = vertexOffset;
        int j = indexOffset;
        IntBuffer indexBuffer = buffer.getIndexBuffer();
        for (TesselationData data : dataCollection) {
            Integer[] indices = data.getIndices();
            int indexCount = data.getIndexCount();
            for (int k = 0; k < indexCount; k++) {
                indexBuffer.put(j, i + indices[k]);
                j++;
            }
            i += data.getVertexCount();
        }
    }

    public void setColor(Color color) {
        byte r = (byte) color.getRed();
        byte g = (byte) color.getGreen();
        byte b = (byte) color.getBlue();
        byte a = (byte) color.getAlpha();
        ByteBuffer colorBuffer = bufferIndirection.get().getColorBuffer();
        for (int i = vertexOffset; i < vertexOffset + vertexCount; i++) {
            colorBuffer.put(4 * i, r);
            colorBuffer.put(4 * i + 1, g);
            colorBuffer.put(4 * i + 2, b);
            colorBuffer.put(4 * i + 3, a);
        }
    }

    public void setDepth(double depth) {
        DoubleBuffer vertexBuffer = bufferIndirection.get().getVertexBuffer();
        for (int i = vertexOffset; i < vertexOffset + vertexCount; i++) {
            vertexBuffer.put(3 * i + 2, depth);
        }
    }

    public void translate(double dx, double dy) {
        DoubleBuffer vertexBuffer = bufferIndirection.get().getVertexBuffer();
        for (int i = vertexOffset; i < vertexOffset + vertexCount; i++) {
            vertexBuffer.put(3 * i, vertexBuffer.get(3 * i) + dx);
            vertexBuffer.put(3 * i + 1, vertexBuffer.get(3 * i + 1) + dy);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
