// =============================================================================
//
//   TriangleBuffer.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl.buffer;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;

import com.sun.opengl.util.BufferUtil;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class TriangleBuffer {
    private static final double OVERSIZE = 2.0; // > 1 !!
    private BufferManager bufferManager;
    private IntBuffer indexBuffer;
    private int capacity;
    private int size;
    private BufferIndirection indirection;

    protected TriangleBuffer(BufferManager bufferManager) {
        this.bufferManager = bufferManager;
        reset();
    }

    protected void reset() {
        capacity = 0;
        indexBuffer = null;
        if (indirection != null) {
            indirection.invalidate();
        }
        indirection = new BufferIndirection(this);
    }

    public void incEstimate(int vertexCount, int indexCount) {
        if (indexBuffer != null)
            throw new IllegalBufferStateException();
        capacity += indexCount;
        bufferManager.incEstimate(vertexCount);
    }

    protected void finishEstimate() {
        capacity = (int) (capacity * OVERSIZE);
        indexBuffer = BufferUtil.newIntBuffer(capacity);
        size = 0;
    }

    public BufferBlock allocate(int vertexCount, int indexCount)
            throws RebuildBufferException {
        int indexOffset = size;
        size += indexCount;
        if (size > capacity)
            throw new RebuildBufferException();
        int vertexOffset = bufferManager.allocate(vertexCount);
        return new BufferBlock(indirection, vertexOffset, vertexCount,
                indexOffset, indexCount);
    }

    protected IntBuffer getIndexBuffer() {
        return indexBuffer;
    }

    protected DoubleBuffer getVertexBuffer() {
        return bufferManager.getVertexBuffer();
    }

    protected ByteBuffer getColorBuffer() {
        return bufferManager.getColorBuffer();
    }

    public void draw(GL gl) {
        gl.glDrawElements(GL.GL_TRIANGLES, size, GL.GL_UNSIGNED_INT,
                indexBuffer);
    }

    public boolean isEmpty() {
        return size == 0;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
