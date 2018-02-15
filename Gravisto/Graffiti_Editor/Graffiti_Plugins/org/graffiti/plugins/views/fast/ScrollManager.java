// =============================================================================
//
//   ScrollManager.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.editor.MainFrame;
import org.graffiti.managers.ViewportEventDispatcher;
import org.graffiti.plugin.view.Viewport;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class ScrollManager extends Viewport {
    public static final int UNIT_INCREMENT = 10;
    private static final double ANGLE_SNAP = Math.PI / 12.0; // 15�
    private static final double ZOOM_SNAP = 0.05;
    private static final double LOW_ZOOM_BOUND = 0.05;
    private static final double HIGH_ZOOM_BOUND = 100.0;
    private JScrollBar horizontalScrollBar;
    private JScrollBar verticalScrollBar;
    private boolean isAdjusting;
    private FastView fastView;
    private ViewportEventDispatcher eventDispatcher;

    private double desiredRotation;
    private double rotation;
    private double desiredZoom;
    private double zoom;
    private Point2D translation;
    private AffineTransform viewingMatrix;
    private AffineTransform inverseViewingMatrix;
    private AffineTransform zoomRotationMatrix;

    // in display coordinates
    private Rectangle2D physicalDisplayBounds;

    // in logical (attribute system) coordinates
    private Rectangle2D logicalDisplayBounds;

    // Elements only
    private Rectangle2D logicalElementsBounds;

    ScrollManager(FastView fastView, JPanel scrollingPanel,
            final JComponent drawingComponent) {
        this.fastView = fastView;
        viewingMatrix = new AffineTransform();
        inverseViewingMatrix = new AffineTransform();
        physicalDisplayBounds = new Rectangle2D.Double(-0.5, -0.5, 1.0, 1.0);
        logicalElementsBounds = new Rectangle2D.Double(-0.5, -0.5, 1.0, 1.0);
        verticalScrollBar = new JScrollBar(Adjustable.VERTICAL);
        scrollingPanel.add(verticalScrollBar, BorderLayout.EAST);
        JPanel horizontalScrollPanel = new JPanel(new BorderLayout());
        horizontalScrollBar = new JScrollBar(Adjustable.HORIZONTAL);
        horizontalScrollPanel.add(horizontalScrollBar, BorderLayout.CENTER);
        ConsoleToggleButton consoleButton = new ConsoleToggleButton(fastView);
        consoleButton.setPreferredSize(new Dimension((int) verticalScrollBar
                .getPreferredSize().getWidth(), (int) horizontalScrollBar
                .getPreferredSize().getHeight()));
        horizontalScrollPanel.add(consoleButton, BorderLayout.EAST);
        scrollingPanel.add(horizontalScrollPanel, BorderLayout.SOUTH);
        horizontalScrollBar.setValues(50, 50, 0, 100);
        desiredRotation = 0.0;
        rotation = 0.0;
        desiredZoom = 1.0;
        zoom = 1.0;
        translation = new Point2D.Double(0.0, 0.0);
        isAdjusting = false;
        horizontalScrollBar.setUnitIncrement(UNIT_INCREMENT);
        horizontalScrollBar.addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if (isAdjusting)
                    return;
                ScrollManager.this.fastView.getGraphicsEngine()
                        .setBoundsCalculation(!e.getValueIsAdjusting());
                setTranslation(-e.getValue(), translation.getY());
            }
        });
        verticalScrollBar.setUnitIncrement(UNIT_INCREMENT);
        verticalScrollBar.addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if (isAdjusting)
                    return;
                ScrollManager.this.fastView.getGraphicsEngine()
                        .setBoundsCalculation(!e.getValueIsAdjusting());
                setTranslation(translation.getX(), -e.getValue());
            }
        });
        drawingComponent.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension dimension = drawingComponent.getSize();
                physicalDisplayBounds = new Rectangle2D.Double(0.0, 0.0,
                        dimension.getWidth(), dimension.getHeight());
                recalculateViewMatrix();
            }
        });
        MainFrame mainFrame = GraffitiSingleton.getInstance().getMainFrame();
        if (mainFrame != null) {
            eventDispatcher = mainFrame.getViewportEventDispatcher();
        }
    }

    public AffineTransform getZoomTransform() {
        return AffineTransform.getScaleInstance(zoom, zoom);
    }

    protected AffineTransform getTransform() {
        return viewingMatrix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getZoom() {
        return zoom;
    }

    /**
     * {@inheritDoc}
     */
    public void setZoom(double factor) {
        setZoom(factor, inverseViewingMatrix.transform(new Point2D.Double(
                physicalDisplayBounds.getCenterX(), physicalDisplayBounds
                        .getCenterY()), null), false);
    }

    public void setZoom(double factor, Point2D center, boolean snap) {
        zoom(factor / desiredZoom, center, snap);
    }

    public void zoom(double factor, Point2D center, boolean snap) {
        desiredZoom = Math.max(desiredZoom * factor, LOW_ZOOM_BOUND);
        double newZoom = desiredZoom;
        if (snap) {
            newZoom = ZOOM_SNAP * Math.ceil(newZoom / ZOOM_SNAP);
        }
        newZoom = Math.min(Math.max(newZoom, LOW_ZOOM_BOUND), HIGH_ZOOM_BOUND);
        AffineTransform mz = new AffineTransform(viewingMatrix);
        double f = newZoom / zoom;
        mz.scale(f, f);
        Point2D p1 = viewingMatrix.transform(center, null);
        Point2D p0 = mz.transform(center, null);
        zoom = newZoom;
        pan(p1.getX() - p0.getX(), p1.getY() - p0.getY());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getRotation() {
        return rotation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRotation(double angle) {
        rotation = angle;
        recalculateViewMatrix();
    }

    public void setRotation(double angle, Point2D center, boolean snap) {
        rotate(angle - desiredRotation, center, snap);
    }

    public void rotate(double angle, Point2D center, boolean snap) {
        desiredRotation += angle;
        double newRotation = desiredRotation;
        if (snap) {
            newRotation = ANGLE_SNAP * Math.round(newRotation / ANGLE_SNAP);
        }
        AffineTransform mr = new AffineTransform(viewingMatrix);
        mr.rotate(newRotation - rotation);
        Point2D p1 = viewingMatrix.transform(center, null);
        Point2D p0 = mr.transform(center, null);
        rotation = newRotation;
        pan(p1.getX() - p0.getX(), p1.getY() - p0.getY());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Point2D getTranslation() {
        return translation;
    }

    /**
     * {@inheritDoc}
     * 
     * @param translation
     */
    @Override
    public void setTranslation(Point2D translation) {
        // Prevent injections of Point2Ds that are afterwards modified by the
        // caller.
        setTranslation(translation.getX(), translation.getY());
    }

    public void setTranslation(double x, double y) {
        translation.setLocation(x, y);
        recalculateViewMatrix();
    }

    public void pan(double dx, double dy) {
        translation.setLocation(translation.getX() + dx, translation.getY()
                + dy);
        recalculateViewMatrix();
    }

    @Override
    public void setSimultanously(double zoomFactor, double angle,
            Point2D translation) {
        zoom = zoomFactor;
        rotation = angle;
        setTranslation(translation);
    }

    @Override
    public Rectangle2D getDisplayBounds() {
        return physicalDisplayBounds;
    }

    @Override
    public Rectangle2D getLogicalElementsBounds() {
        return logicalElementsBounds;
    }

    private void recalculateViewMatrix() {
        viewingMatrix = AffineTransform.getTranslateInstance(
                translation.getX(), translation.getY());
        zoomRotationMatrix = AffineTransform.getRotateInstance(rotation);
        zoomRotationMatrix.scale(zoom, zoom);
        viewingMatrix.concatenate(zoomRotationMatrix);
        try {
            inverseViewingMatrix = viewingMatrix.createInverse();
        } catch (NoninvertibleTransformException e) {
            inverseViewingMatrix = new AffineTransform();
        }
        logicalDisplayBounds = MathUtil.getTransformedBounds(
                physicalDisplayBounds, inverseViewingMatrix);
        fastView.getGraphicsEngine().setViewingMatrix(viewingMatrix,
                inverseViewingMatrix, zoom, logicalDisplayBounds);
        if (eventDispatcher != null) {
            eventDispatcher.onViewportChange(this);
        }
        fastView.refresh();
    }

    public void setLogicalBounds(Rectangle2D elementsBounds) {
        this.logicalElementsBounds = elementsBounds;
        Rectangle2D commonPhysicalBound = MathUtil.getTransformedBounds(
                elementsBounds, zoomRotationMatrix);
        commonPhysicalBound.add(new Rectangle2D.Double(-translation.getX(),
                -translation.getY(), physicalDisplayBounds.getWidth(),
                physicalDisplayBounds.getHeight()));
        isAdjusting = true;
        horizontalScrollBar.setValues((int) -translation.getX(),
                (int) physicalDisplayBounds.getWidth(),
                (int) (commonPhysicalBound.getMinX() - 2 * UNIT_INCREMENT),
                (int) (commonPhysicalBound.getMaxX() + 2 * UNIT_INCREMENT));
        verticalScrollBar.setValues((int) -translation.getY(),
                (int) physicalDisplayBounds.getHeight(),
                (int) (commonPhysicalBound.getMinY() - 2 * UNIT_INCREMENT),
                (int) (commonPhysicalBound.getMaxY() + 2 * UNIT_INCREMENT));
        isAdjusting = false;
    }

    @Override
    public Point2D transform(Point2D point) {
        return viewingMatrix.transform(point, null);
    }

    @Override
    public Point2D inverseTransform(Point2D point) {
        return inverseViewingMatrix.transform(point, null);
    }

    @Override
    public AffineTransform getZoomRotationTransform() {
        return zoomRotationMatrix;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
