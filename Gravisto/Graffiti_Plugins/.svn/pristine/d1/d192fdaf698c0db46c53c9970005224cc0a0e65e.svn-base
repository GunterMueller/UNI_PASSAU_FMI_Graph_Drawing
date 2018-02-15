// =============================================================================
//
//   OpenGLGestureFeedbackProvider.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.plugins.views.fast.FastViewGestureFeedbackProvider;
import org.graffiti.plugins.views.fast.FastViewPlugin;
import org.graffiti.plugins.views.fast.ScrollManager;

import com.sun.opengl.util.j2d.TextRenderer;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class OpenGLGestureFeedbackProvider extends
        FastViewGestureFeedbackProvider {
    private Renderer renderer;
    private GLU glu;
    private GLUquadric quadric;
    private TextRenderer textRenderer;
    private TesselationData compassData;

    protected OpenGLGestureFeedbackProvider(FastView fastView,
            OpenGLNodeChangeListener nodeChangeListener,
            OpenGLEdgeChangeListener edgeChangeListener) {
        super(fastView, nodeChangeListener, edgeChangeListener);
    }

    protected void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    protected void onInitGL(GLU glu) {
        if (quadric != null) {
            this.glu.gluDeleteQuadric(quadric);
        }
        this.glu = glu;
        quadric = glu.gluNewQuadric();
        textRenderer = new TextRenderer(new Font(null, Font.PLAIN, 12));
        compassData = Tesselator.get().tesselate(COMPASS, COMPASS_STROKE);
    }

    protected void drawSelectionRectangle(GL gl) {
        if (selectionRectangle == null)
            return;
        double x1 = selectionRectangle.getMinX();
        double y1 = selectionRectangle.getMinY();
        double x2 = selectionRectangle.getMaxX();
        double y2 = selectionRectangle.getMaxY();
        gl.glColor3d(0.0, 0.0, 0.0);
        gl.glBegin(GL.GL_LINE_STRIP);
        gl.glVertex2d(x1, y1);
        gl.glVertex2d(x2, y1);
        gl.glVertex2d(x2, y2);
        gl.glVertex2d(x1, y2);
        gl.glVertex2d(x1, y1);
        gl.glEnd();
    }

    protected void drawDummyEdge(GL gl) {
        if (dummyEdgePoints == null || dummyEdgePoints.size() == 0)
            return;
        gl.glColor3d(0.0, 0.0, 0.0);
        gl.glBegin(GL.GL_LINE_STRIP);
        for (Point2D point : dummyEdgePoints) {
            gl.glVertex2d(point.getX(), point.getY());
        }
        gl.glEnd();
        Color hoverColor = FastViewPlugin.HOVER_COLOR;
        renderer.glColor(hoverColor);
        gl.glBegin(GL.GL_QUADS);
        for (Point2D point : dummyEdgePoints) {
            double x1 = point.getX() - 2.5;
            double y1 = point.getY() - 2.5;
            double x2 = point.getX() + 2.5;
            double y2 = point.getY() + 2.5;
            gl.glVertex2d(x1, y2);
            gl.glVertex2d(x1, y1);
            gl.glVertex2d(x2, y1);
            gl.glVertex2d(x2, y2);
        }
        gl.glEnd();
    }

    protected void drawDummyHub(GL gl) {
        if (hubPosition == null)
            return;
        double hubRadius = FastViewGestureFeedbackProvider.HUB_SIZE / 2.0;

        gl.glPushMatrix();
        gl.glTranslated(hubPosition.getX(), hubPosition.getY(), 0.0);
        renderer.glColor(isHoveringHub ? FastViewPlugin.HOVER_COLOR
                : FastViewPlugin.ROTATION_HUB_COLOR);
        glu.gluDisk(quadric, 0.0, hubRadius, 16, 1);
        gl.glColor3d(0.0, 0.0, 0.0);
        glu.gluDisk(quadric, 0.0, hubRadius / 2.0, 16, 1);
        gl.glPopMatrix();
    }

    protected void drawCompass(GL gl) {
        if (compassPosition == null)
            return;
        ScrollManager sm = fastView.getViewport();
        Point2D center = sm.transform(compassPosition);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glTranslated(center.getX(), center.getY(), 0.0);
        gl.glColor3d(0.0, 0.0, 0.0);
        double r = COMPASS_CIRCLE_SIZE / 2.0;
        glu.gluDisk(quadric, r - 0.6, r + 0.6, 16, 1);
        gl.glPushMatrix();
        gl.glScaled(1.0, -1.0, 1.0);
        textRenderer.begin3DRendering();
        textRenderer.setColor(Color.BLACK);
        textRenderer.draw3D(String.format("%d%%", (int) (sm.getZoom() * 100)),
                5.0f, 5.0f, 0.0f, 1.0f);
        textRenderer.end3DRendering();
        gl.glPopMatrix();
        gl.glRotated(sm.getRotation() * 180.0 / Math.PI, 0.0, 0.0, 1.0);
        compassData.draw(gl, Color.BLACK, 0.0);
        gl.glPopMatrix();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
