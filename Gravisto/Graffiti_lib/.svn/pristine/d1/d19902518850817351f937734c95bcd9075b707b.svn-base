// =============================================================================
//
//   Renderer.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.PathIterator;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugins.views.fast.FastViewPlugin;
import org.graffiti.plugins.views.fast.opengl.buffer.BufferManager;
import org.graffiti.plugins.views.fast.opengl.buffer.TriangleBuffer;
import org.graffiti.plugins.views.fast.opengl.label.OpenGLLabelManager;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class Renderer implements GLEventListener {
    public static final int REDRAW_LEVEL_PRESENT = 0;
    public static final int REDRAW_LEVEL_REDRAW = 1;
    public static final int REDRAW_LEVEL_REBUILD = 2;
    private GL gl;
    private GLU glu;
    private int width;
    private int height;
    private double[] viewingMatrix;
    private int redrawLevel;
    private OpenGLEngine engine;
    private Map<Node, AbstractNodeRep> nodes;
    private Map<Edge, AbstractEdgeRep> edges;
    private BufferManager bufferManager;
    private TriangleBuffer nodeBuffer;
    private TriangleBuffer edgeBuffer;
    private int displayList;
    private ChangeProcessor changeProcessor;
    private OpenGLLabelManager labelManager;
    private TextBuffer textBuffer;
    private OpenGLFontManager fontManager;
    private OpenGLGestureFeedbackProvider feedbackProvider;
    private Color backgroundColor;
    private Color gridColor;

    Renderer(OpenGLEngine engine, Map<Node, AbstractNodeRep> nodes,
            Map<Edge, AbstractEdgeRep> edges, BufferManager bufferManager,
            TriangleBuffer nodeBuffer, TriangleBuffer edgeBuffer, GLU glu,
            ChangeProcessor changeProcessor, OpenGLLabelManager labelManager,
            OpenGLFontManager fontManager,
            OpenGLGestureFeedbackProvider feedbackProvider) {
        this.engine = engine;
        this.nodes = nodes;
        this.edges = edges;
        this.bufferManager = bufferManager;
        this.nodeBuffer = nodeBuffer;
        this.edgeBuffer = edgeBuffer;
        this.glu = glu;
        this.changeProcessor = changeProcessor;
        this.labelManager = labelManager;
        this.fontManager = fontManager;
        this.feedbackProvider = feedbackProvider;
        textBuffer = new TextBuffer();
        viewingMatrix = new double[16];
        setIdentityViewingMatrix();
        redrawLevel = REDRAW_LEVEL_REBUILD;
        backgroundColor = FastViewPlugin.DEFAULT_BACKGROUND_COLOR;
        gridColor = FastViewPlugin.DEFAULT_GRID_COLOR;
    }

    void setIdentityViewingMatrix() {
        for (int i = 0; i < 16; i++) {
            viewingMatrix[i] = 0.0;
        }
        viewingMatrix[0] = 1.0;
        viewingMatrix[5] = -1.0;// -
        viewingMatrix[10] = 1.0;
        viewingMatrix[15] = 1.0;
    }

    void setViewingMatrix(AffineTransform matrix) {
        double[] flatMatrix = new double[6];
        matrix.getMatrix(flatMatrix);
        viewingMatrix[0] = flatMatrix[0];
        viewingMatrix[1] = flatMatrix[1];
        viewingMatrix[4] = flatMatrix[2];// -
        viewingMatrix[5] = flatMatrix[3];// -
        viewingMatrix[12] = flatMatrix[4];
        viewingMatrix[13] = flatMatrix[5];
    }

    void setOrthoMatrix(double width, double height) {
        double[] matrix = new double[16];
        matrix[0] = 2.0 / width;
        matrix[1] = 0.0;
        matrix[2] = 0.0;
        matrix[3] = 0.0;
        matrix[4] = 0.0;
        matrix[5] = -2.0 / height;
        matrix[6] = 0.0;
        matrix[7] = 0.0;
        matrix[8] = 0.0;
        matrix[9] = 0.0;
        matrix[10] = -2.0;
        matrix[11] = 0.0;
        matrix[12] = -1.0;
        matrix[13] = 1.0;
        matrix[14] = -1.0;
        matrix[15] = 1.0;
        gl.glLoadMatrixd(matrix, 0);
    }

    /**
     * {@inheritDoc}
     */
    /**
     *
     */
    public void display(GLAutoDrawable drawable) {
        // gl.glEnable(GL.GL_CULL_FACE);
        gl.glDisable(GL.GL_CULL_FACE);
        changeProcessor.processChanges();
        if (redrawLevel == REDRAW_LEVEL_REBUILD) {
            engine.rebuildBuffer();
            redrawLevel = REDRAW_LEVEL_REDRAW;
        }
        labelManager.processChanges();
        // gl.glClearColor((float) Math.random(), (float) Math.random(), (float)
        // Math.random(), 1.0f);
        // gl.glClearColor(0.8f, 1.0f, 0.8f, 0.0f);

        gl.glClearColor(backgroundColor.getRed() / 255.0f, backgroundColor
                .getGreen() / 255.0f, backgroundColor.getBlue() / 255.0f, 0.0f);
        // gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearDepth(1.0);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        /*
         * gl.glMatrixMode(GL.GL_PROJECTION); gl.glLoadIdentity();
         * gl.glOrtho(0.0, width, height, 0, 0.0, -1.0);
         */
        // setOrthoMatrix(width, height);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glLoadMatrixd(viewingMatrix, 0);

        drawGrid();

        if (redrawLevel == REDRAW_LEVEL_PRESENT) {
            gl.glCallList(displayList);
        } else // REDRAW_LEVEL_REDRAW
        {
            gl.glNewList(displayList, GL.GL_COMPILE_AND_EXECUTE);

            gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL.GL_COLOR_ARRAY);
            bufferManager.applyBufferPointers(gl);
            edgeBuffer.draw(gl);

            for (AbstractEdgeRep edgeRep : edges.values()) {
                edgeRep.drawLabels(gl, textBuffer);
            }

            gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
            gl.glDisableClientState(GL.GL_COLOR_ARRAY);

            textBuffer.draw(gl);

            gl.glClearDepth(1.0);
            gl.glClear(GL.GL_DEPTH_BUFFER_BIT);

            gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL.GL_COLOR_ARRAY);
            bufferManager.applyBufferPointers(gl);
            nodeBuffer.draw(gl);
            for (AbstractNodeRep nodeRep : nodes.values()) {
                nodeRep.drawLabels(gl, textBuffer);
            }

            gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
            gl.glDisableClientState(GL.GL_COLOR_ARRAY);

            textBuffer.draw(gl);

            gl.glEndList();
        }
        feedbackProvider.drawDummyEdge(gl);
        feedbackProvider.drawDummyHub(gl);
        feedbackProvider.drawSelectionRectangle(gl);
        feedbackProvider.drawCompass(gl);
        redrawLevel = REDRAW_LEVEL_PRESENT;
        engine.calculateBounds();
    }

    private void drawGrid() {
        List<Shape> gridShapes = engine.getGridShapes();
        if (gridShapes.isEmpty())
            return;
        for (Shape shape : gridShapes) {
            FlatteningPathIterator iter = new FlatteningPathIterator(shape
                    .getPathIterator(null), 1.0);
            double[] coords = new double[6];
            double closeX = 0.0;
            double closeY = 0.0;
            glColor(gridColor);
            gl.glBegin(GL.GL_LINE_STRIP);
            while (!iter.isDone()) {
                int seg = iter.currentSegment(coords);
                switch (seg) {
                case PathIterator.SEG_MOVETO:
                    gl.glEnd();
                    gl.glBegin(GL.GL_LINE_STRIP);
                    closeX = coords[0];
                    closeY = coords[1];
                    // Fallthrough
                case PathIterator.SEG_CLOSE:
                    gl.glVertex3d(closeX, closeY, 1.0);
                    break;
                case PathIterator.SEG_LINETO:
                    gl.glVertex3d(coords[0], coords[1], 1.0);
                    break;
                }
                iter.next();
            }
            gl.glEnd();
        }
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
        raiseRedrawLevel(REDRAW_LEVEL_REBUILD);
    }

    public void init(GLAutoDrawable drawable) {
        gl = drawable.getGL();
        gl.glShadeModel(GL.GL_SMOOTH);
        OpenGLConfiguration config = OpenGLConfiguration.get();
        if (config.isAntialiasingPoints()) {
            gl.glEnable(GL.GL_POINT_SMOOTH);
            gl.glHint(GL.GL_POINT_SMOOTH_HINT, GL.GL_NICEST);
        } else {
            gl.glDisable(GL.GL_POINT_SMOOTH);
        }
        if (config.isAntialiasingLines()) {
            gl.glEnable(GL.GL_LINE_SMOOTH);
            gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
        } else {
            gl.glDisable(GL.GL_LINE_SMOOTH);
        }
        if (config.isAntialiasingPolygons()) {
            gl.glEnable(GL.GL_POLYGON_SMOOTH);
            gl.glHint(GL.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST);
        } else {
            gl.glDisable(GL.GL_POLYGON_SMOOTH);
        }
        // gl.glDisable(GL.GL_BLEND);
        if (config.isAntialiasing()) {
            gl.glEnable(GL.GL_BLEND);
            gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        } else {
            gl.glDisable(GL.GL_BLEND);
        }
        gl.glEnable(GL.GL_DEPTH_TEST);
        // gl.glDisable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);
        gl.glDepthRange(0.0, 1.0);

        // gl.glEnable(GL.GLDEP)
        // gl.glEnable(GL.GL_CULL_FACE);

        displayList = gl.glGenLists(1);
        fontManager.resetFonts();
        setProjectionMatrix();
        feedbackProvider.onInitGL(glu);
        redrawLevel = REDRAW_LEVEL_REBUILD;
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
            int height) {
        this.width = Math.max(width, 1);
        this.height = Math.max(height, 1);
        setProjectionMatrix();
        raiseRedrawLevel(REDRAW_LEVEL_REDRAW);
    }

    private void setProjectionMatrix() {
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0.0, width, height, 0, 0.0, -1.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
    }

    public void raiseRedrawLevel(int redrawLevel) {
        this.redrawLevel = Math.max(this.redrawLevel, redrawLevel);
    }

    int getRedrawLevel() {
        return redrawLevel;
    }

    protected Color getBackgroundColor() {
        return backgroundColor;
    }

    protected void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    protected Color getGridColor() {
        return gridColor;
    }

    protected void setGridColor(Color gridColor) {
        this.gridColor = gridColor;
    }

    protected void glColor(Color color) {
        gl.glColor3d(color.getRed() / 255.0, color.getGreen() / 255.0, color
                .getBlue() / 255.0);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
