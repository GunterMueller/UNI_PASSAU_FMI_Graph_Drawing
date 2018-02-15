// =============================================================================
//
//   Java2DGestureFeedbackProvider.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.java2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import org.graffiti.plugins.views.fast.EdgeChangeListener;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.plugins.views.fast.FastViewGestureFeedbackProvider;
import org.graffiti.plugins.views.fast.FastViewPlugin;
import org.graffiti.plugins.views.fast.NodeChangeListener;
import org.graffiti.plugins.views.fast.ScrollManager;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class Java2DGestureFeedbackProvider extends
        FastViewGestureFeedbackProvider {
    public Java2DGestureFeedbackProvider(FastView fastView,
            NodeChangeListener<?> nodeChangeListener,
            EdgeChangeListener<?> edgeChangeListener) {
        super(fastView, nodeChangeListener, edgeChangeListener);
    }

    public void drawSelectionRectangle(Graphics2D g, DrawingSet set) {
        if (selectionRectangle != null) {
            g.setColor(Color.BLACK);
            g.setStroke(set.defaultStroke);
            g.draw(selectionRectangle);
        }
    }

    public Shape drawDummyEdge(Graphics2D g, DrawingSet set) {
        GeneralPath path = null;
        if (dummyEdgePoints != null && dummyEdgePoints.size() > 1) {
            g.setColor(Color.BLACK);
            g.setStroke(set.defaultStroke);
            path = new GeneralPath();
            Iterator<Point2D> iter = dummyEdgePoints.iterator();
            Point2D point = iter.next();
            path.moveTo((float) point.getX(), (float) point.getY());
            while (iter.hasNext()) {
                point = iter.next();
                path.lineTo((float) point.getX(), (float) point.getY());
            }
            g.draw(path);
            if (isDummyEdgeHovered) {
                g.setColor(FastViewPlugin.HOVER_COLOR);
                for (Point2D p : dummyEdgePoints) {
                    g.fill(new Rectangle2D.Double(p.getX() - 3, p.getY() - 3,
                            5, 5));
                }
            }
        }
        return path;
    }

    public void drawDummyHub(Graphics2D g, DrawingSet set) {
        if (hubPosition == null)
            return;
        int hubSize = FastViewGestureFeedbackProvider.HUB_SIZE;
        int hubSize2 = hubSize / 2;
        int x = (int) hubPosition.getX();
        int y = (int) hubPosition.getY();
        g.setColor(isHoveringHub ? FastViewPlugin.HOVER_COLOR
                : FastViewPlugin.ROTATION_HUB_COLOR);
        g.fillOval(x - hubSize2, y - hubSize2, hubSize, hubSize);
        g.setColor(Color.BLACK);
        hubSize = hubSize2;
        hubSize2 /= 2;
        g.fillOval(x - hubSize2, y - hubSize2, hubSize, hubSize);
    }

    public void drawCompass(Graphics2D g, DrawingSet set, int width,
            int height, AffineTransform initialTransform) {
        if (compassPosition == null)
            return;
        ScrollManager sm = fastView.getViewport();
        Point2D center = sm.transform(compassPosition);
        g.setTransform(initialTransform);
        g.translate(center.getX(), center.getY());
        g.setColor(Color.BLACK);
        int r = COMPASS_CIRCLE_SIZE / 2;
        g.drawOval(-r, -r, COMPASS_CIRCLE_SIZE, COMPASS_CIRCLE_SIZE);
        g.drawString(String.format("%d%%", (int) (sm.getZoom() * 100)), 5.0f,
                -5.0f);
        g.rotate(sm.getRotation());
        g.setStroke(COMPASS_STROKE);
        g.draw(COMPASS);
        g.setTransform(set.affineTransform);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
