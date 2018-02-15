// =============================================================================
//
//   CircleNodeResizer.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: CircleNodeResizer.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced.nodeResize;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;

import org.graffiti.graphics.NodeGraphicAttribute;

/**
 * NodeResizer for resizing circles.
 * 
 * @deprecated
 */
@Deprecated
public class CircleNodeResizer implements NodeResizer {

    /** Properties of the node being resized. */
    private NodeGraphicAttribute nodeGraphicAttribute;

    /** Last known distance of given position to the center of the circle */
    private double oldDistToCenter;

    /**
     * Creates a new CircleNodeResizer object.
     * 
     * @param nodeGraphicAttribute
     *            DOCUMENT ME!
     * @param oldDistToCenter
     *            DOCUMENT ME!
     */
    private CircleNodeResizer(NodeGraphicAttribute nodeGraphicAttribute,
            double oldDistToCenter) {
        this.nodeGraphicAttribute = nodeGraphicAttribute;
        this.oldDistToCenter = oldDistToCenter;
    }

    /**
     * Creates a new CircleNodeResizer using the given information. Returns null
     * if the given position is not in the sensitive area.
     * 
     * @param nodeGraphicAttribute
     *            properties of the node we want to resize
     * @param sensitiveAreaSize
     *            size of the sensitive area
     * @param position
     *            position given by some input-event
     * 
     * @return CircleNodeResizer for resizing the given node, if the given
     *         position was inside the sensitive area, null otherwise.
     */
    public static CircleNodeResizer createResizer(
            NodeGraphicAttribute nodeGraphicAttribute,
            double sensitiveAreaSize, Point position,
            NodeResizeTool nodeResizeTool) {
        double distToCenter = getDistToCenter(nodeGraphicAttribute, position);

        if (((getRadius(nodeGraphicAttribute) - distToCenter) >= 0)
                && ((getRadius(nodeGraphicAttribute) - distToCenter) < sensitiveAreaSize)) {
            Cursor c = new Cursor(Cursor.MOVE_CURSOR);
            nodeResizeTool.getActiveJComponent().setCursor(c);

            return new CircleNodeResizer(nodeGraphicAttribute, distToCenter);
        } else
            return null;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param minNodeSize
     *            DOCUMENT ME!
     * @param position
     *            DOCUMENT ME!
     */
    public void updateNode(double minNodeSize, Point position) {
        double currDistToCenter = getDistToCenter(nodeGraphicAttribute,
                position);
        double dRadius = currDistToCenter - oldDistToCenter;
        double oldRadius = getRadius(nodeGraphicAttribute);

        Dimension newDimension;

        if ((2 * (oldRadius + dRadius)) >= minNodeSize) {
            newDimension = new Dimension(2 * (int) (oldRadius + dRadius),
                    2 * (int) (oldRadius + dRadius));
        } else {
            newDimension = new Dimension((int) minNodeSize, (int) minNodeSize);
        }

        nodeGraphicAttribute.getDimension().setDimension(newDimension);
        oldDistToCenter = currDistToCenter;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param nodeGraphicAttribute
     *            DOCUMENT ME!
     * @param position
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    private static double getDistToCenter(
            NodeGraphicAttribute nodeGraphicAttribute, Point position) {
        Point2D center = nodeGraphicAttribute.getCoordinate().getCoordinate();
        double dx = center.getX() - position.getX();
        double dy = center.getY() - position.getY();

        return Math.sqrt((dx * dx) + (dy * dy));
    }

    /**
     * DOCUMENT ME!
     * 
     * @param nodeGraphicAttribute
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    private static double getRadius(NodeGraphicAttribute nodeGraphicAttribute) {
        Dimension d = nodeGraphicAttribute.getDimension().getDimension();
        return Math.min(d.getWidth() / 2, d.getHeight() / 2);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
