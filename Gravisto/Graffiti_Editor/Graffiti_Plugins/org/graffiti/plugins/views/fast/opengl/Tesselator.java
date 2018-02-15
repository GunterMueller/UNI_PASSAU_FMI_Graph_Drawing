// =============================================================================
//
//   Tesselator.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl;

import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;
import javax.media.opengl.glu.GLUtessellatorCallback;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class Tesselator implements GLUtessellatorCallback {
    private static Tesselator singleton;

    public static Tesselator get() {
        if (singleton == null) {
            singleton = new Tesselator();
        }
        return singleton;
    }

    private GLU glu;
    private GLUtessellator tesselator;
    private int type;
    private ArrayList<Double> vertexList;
    private ArrayList<Integer> indexList;
    private int startIndex;
    private int vertexIndex;

    private Tesselator() {
        glu = new GLU();
        vertexList = new ArrayList<Double>();
        indexList = new ArrayList<Integer>();
        tesselator = glu.gluNewTess();
        glu.gluTessCallback(tesselator, GLU.GLU_TESS_BEGIN, this);
        glu.gluTessCallback(tesselator, GLU.GLU_TESS_VERTEX, this);
        glu.gluTessCallback(tesselator, GLU.GLU_TESS_COMBINE, this);
        glu.gluTessCallback(tesselator, GLU.GLU_TESS_END, this);
        glu.gluTessCallback(tesselator, GLU.GLU_TESS_ERROR, this);
        glu.gluTessProperty(tesselator, GLU.GLU_TESS_WINDING_RULE,
        // GLU.GLU_TESS_WINDING_ODD);
                GLU.GLU_TESS_WINDING_NONZERO);
    }

    /**
     * @{inheritdoc
     */
    @Override
    protected void finalize() throws Throwable {
        glu.gluDeleteTess(tesselator);
    }

    public TesselationData tesselate(Shape shape) {
        if (shape == null)
            return new TesselationData();
        FlatteningPathIterator iter = new FlatteningPathIterator(shape
                .getPathIterator(null), 1.0);
        if (iter.isDone())
            return new TesselationData();
        vertexList = new ArrayList<Double>();
        indexList = new ArrayList<Integer>();
        startIndex = 0;
        vertexIndex = 0;
        glu.gluTessBeginPolygon(tesselator, null);
        Point2D positionOfLastMoveTo = new Point2D.Double();
        double coords[] = new double[6];
        iter.currentSegment(coords);
        iter.next();
        glu.gluTessBeginContour(tesselator);
        double position[] = new double[3];
        position[0] = coords[0];
        position[1] = coords[1];
        glu.gluTessVertex(tesselator, position, 0, position);
        for (; !iter.isDone(); iter.next()) {
            int type = iter.currentSegment(coords);
            switch (type) {
            case PathIterator.SEG_CLOSE:
                coords[0] = positionOfLastMoveTo.getX();
                coords[1] = positionOfLastMoveTo.getY();
                break;
            case PathIterator.SEG_MOVETO:
                positionOfLastMoveTo = new Point2D.Double(coords[0], coords[1]);
                glu.gluTessEndContour(tesselator);
                glu.gluTessBeginContour(tesselator);
                break;
            }
            if (type == PathIterator.SEG_CLOSE) {
                continue; // HACK
            }
            position = new double[3];
            position[0] = coords[0];
            position[1] = coords[1];
            glu.gluTessVertex(tesselator, position, 0, position);
        }
        glu.gluTessEndContour(tesselator);
        glu.gluTessEndPolygon(tesselator);
        TesselationData result = new TesselationData(vertexList
                .toArray(new Double[vertexList.size()]), indexList
                .toArray(new Integer[indexList.size()]));
        vertexList = null;
        indexList = null;
        return result;
    }

    public TesselationData tesselate(Shape shape, Stroke stroke) {
        if (shape == null)
            return new TesselationData();
        return tesselate(stroke.createStrokedShape(shape));
    }

    /**
     * @{inheritdoc
     */
    public void begin(int type) {
        this.type = type;
        startIndex = vertexIndex;
    }

    /**
     * @{inheritdoc
     */
    public void beginData(int arg0, Object arg1) {
        begin(arg0);
    }

    /**
     * @{inheritdoc
     */
    public void combine(double[] coords, Object[] data, float[] weight,
            Object[] outData) {
        double[] vertex = new double[3];
        vertex[0] = coords[0];
        vertex[1] = coords[1];
        vertex[2] = coords[2];
        outData[0] = vertex;
    }

    /**
     * @{inheritdoc
     */
    public void combineData(double[] coords, Object[] data, float[] weight,
            Object[] outData, Object polygonData) {
    }

    /**
     * @{inheritdoc
     */
    public void edgeFlag(boolean arg0) {
    }

    /**
     * @{inheritdoc
     */
    public void edgeFlagData(boolean arg0, Object arg1) {
    }

    /**
     * @{inheritdoc
     */
    public void end() {
    }

    /**
     * @{inheritdoc
     */
    public void endData(Object arg0) {
    }

    /**
     * @{inheritdoc
     */
    public void error(int arg0) {
    }

    /**
     * @{inheritdoc
     */
    public void errorData(int arg0, Object arg1) {
    }

    /**
     * @{inheritdoc
     */
    public void vertex(Object vertexData) {
        double[] position = (double[]) vertexData;
        vertexList.add(position[0]);
        vertexList.add(position[1]);
        switch (type) {
        case GL.GL_TRIANGLES:
            indexList.add(vertexIndex);
            break;
        case GL.GL_TRIANGLE_STRIP:
            if (vertexIndex - startIndex > 1) {
                if (((vertexIndex - startIndex) & 1) == 0) {
                    indexList.add(vertexIndex - 2);
                    indexList.add(vertexIndex - 1);
                    indexList.add(vertexIndex);
                } else {
                    indexList.add(vertexIndex - 2);
                    indexList.add(vertexIndex);
                    indexList.add(vertexIndex - 1);
                }

            }
            break;
        case GL.GL_TRIANGLE_FAN:
            if (vertexIndex - startIndex > 1) {
                indexList.add(startIndex);
                indexList.add(vertexIndex - 1);
                indexList.add(vertexIndex);
            }
            break;
        }
        vertexIndex++;
    }

    /**
     * @{inheritdoc
     */
    public void vertexData(Object arg0, Object arg1) {
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
