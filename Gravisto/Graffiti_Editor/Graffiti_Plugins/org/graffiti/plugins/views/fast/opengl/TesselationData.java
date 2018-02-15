// =============================================================================
//
//   TesselationData.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl;

import java.awt.Color;

import javax.media.opengl.GL;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class TesselationData {
    Double[] vertices;
    Integer[] indices;

    TesselationData() {
        vertices = new Double[0];
        indices = new Integer[0];
    }

    TesselationData(Double[] vertices, Integer[] indices) {
        this.vertices = vertices;
        this.indices = indices;
    }

    public int getVertexCount() {
        return vertices.length / 2;
    }

    public Double[] getVertices() {
        return vertices;
    }

    public Integer[] getIndices() {
        return indices;
    }

    public int getIndexCount() {
        return indices.length;
    }

    // For debugging.
    public void draw(GL gl, Color color, double depth) {
        if (indices.length == 0)
            return;
        gl.glBegin(GL.GL_TRIANGLES);
        gl.glColor3ub((byte) color.getRed(), (byte) color.getGreen(),
                (byte) color.getBlue());
        for (Integer index : indices) {
            gl.glVertex3d(vertices[2 * index], vertices[2 * index + 1], depth);
        }
        gl.glEnd();
    }

    /*
     * public void writeToBuffers(VertexBufferBlock vertexBufferBlock,
     * IndexBufferBlock indexBufferBlock, double depth) {
     * writeToBuffers(vertexBufferBlock, indexBufferBlock, 0, 0, depth); }
     * 
     * public void writeToBuffers(VertexBufferBlock vertexBufferBlock,
     * IndexBufferBlock indexBufferBlock, Point2D position, double depth) {
     * writeToBuffers(vertexBufferBlock, indexBufferBlock, 0, 0, position,
     * depth); }
     * 
     * public void writeToBuffers(VertexBufferBlock vertexBufferBlock,
     * IndexBufferBlock indexBufferBlock, int vertexOffset, int indexOffset,
     * double depth) { writeToBuffers(vertexBufferBlock, indexBufferBlock,
     * vertexOffset, indexOffset, new Point2D.Double(), depth); }
     * 
     * public void writeToBuffers(VertexBufferBlock vertexBufferBlock,
     * IndexBufferBlock indexBufferBlock, int vertexOffset, int indexOffset,
     * Point2D position, double depth) { if (indexList.isEmpty()) return;
     * Iterator<Double> iter = vertexList.iterator(); for (int i = 0;
     * iter.hasNext(); i++) { double x = iter.next() + position.getX(); double y
     * = iter.next() + position.getY(); vertexBufferBlock.writeVertex(i +
     * vertexOffset, x, y, depth); } int i = 0; for (Integer index : indexList)
     * { indexBufferBlock.writeIndex(i + indexOffset, vertexBufferBlock, index +
     * vertexOffset); i++; } }
     */
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
