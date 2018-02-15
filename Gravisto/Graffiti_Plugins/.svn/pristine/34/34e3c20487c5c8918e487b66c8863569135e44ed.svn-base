// =============================================================================
//
//   BufferManager.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl.buffer;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.LinkedList;
import java.util.List;

import javax.media.opengl.GL;

import com.sun.opengl.util.BufferUtil;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class BufferManager {
    private static final double OVERSIZE = 2.0; // > 1 !!
    private static final double INVISIBLE_Z = 2.0;
    private DoubleBuffer vertexBuffer;
    private ByteBuffer colorBuffer;
    private List<TriangleBuffer> buffers;
    int capacity;
    int size;

    public BufferManager() {
        buffers = new LinkedList<TriangleBuffer>();
        reset();
    }

    public TriangleBuffer createBuffer() {
        TriangleBuffer buffer = new TriangleBuffer(this);
        buffers.add(buffer);
        return buffer;
    }

    public void reset() {
        vertexBuffer = null;
        colorBuffer = null;
        capacity = 1;
        for (TriangleBuffer buffer : buffers) {
            buffer.reset();
        }
    }

    protected void incEstimate(int vertexCount) {
        capacity += vertexCount;
    }

    public void finishEstimate() {
        if (vertexBuffer != null)
            throw new IllegalBufferStateException();
        capacity = (int) (capacity * OVERSIZE);
        vertexBuffer = BufferUtil.newDoubleBuffer(capacity * 3);
        vertexBuffer.put(0, 0.0);
        vertexBuffer.put(1, 0.0);
        vertexBuffer.put(2, INVISIBLE_Z);
        colorBuffer = BufferUtil.newByteBuffer(capacity * 4);
        size = 1;
        for (TriangleBuffer buffer : buffers) {
            buffer.finishEstimate();
        }
    }

    /**
     * 
     * @param vertexCount
     *            the number of vertices to allocate.
     * @return the index of the first allocated vertex.
     * @throws RebuildBufferException
     *             if the capacity is too small.
     */
    protected int allocate(int vertexCount) throws RebuildBufferException {
        int result = size;
        size += vertexCount;
        if (size > capacity)
            throw new RebuildBufferException();
        return result;
    }

    protected DoubleBuffer getVertexBuffer() {
        return vertexBuffer;
    }

    protected ByteBuffer getColorBuffer() {
        return colorBuffer;
    }

    public void applyBufferPointers(GL gl) {
        gl.glVertexPointer(3, GL.GL_DOUBLE, 0, vertexBuffer);
        gl.glColorPointer(4, GL.GL_UNSIGNED_BYTE, 0, colorBuffer);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
