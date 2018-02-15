// =============================================================================
//
//   EllipseNodeResizer.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: EllipseNodeResizer.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced.nodeResize;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import org.graffiti.graphics.NodeGraphicAttribute;

/**
 * @deprecated
 */
@Deprecated
public class EllipseNodeResizer implements NodeResizer {

    /** DOCUMENT ME! */
    private NodeGraphicAttribute nodeGraphicAttribute;

    /** DOCUMENT ME! */
    private Point direction;

    /** DOCUMENT ME! */
    private Point oldPosition;

    /**
     * Creates a new EllipseNodeResizer object.
     * 
     * @param nodeGraphicAttribute
     *            DOCUMENT ME!
     * @param position
     *            DOCUMENT ME!
     * @param direction
     *            DOCUMENT ME!
     */
    private EllipseNodeResizer(NodeGraphicAttribute nodeGraphicAttribute,
            Point position, Point direction) {
        this.nodeGraphicAttribute = nodeGraphicAttribute;
        this.oldPosition = position;
        this.direction = direction;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param nodeGraphicAttribute
     *            DOCUMENT ME!
     * @param sensitiveAreaSize
     *            DOCUMENT ME!
     * @param position
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public static EllipseNodeResizer createResizer(
            NodeGraphicAttribute nodeGraphicAttribute,
            double sensitiveAreaSize, Point position,
            NodeResizeTool nodeResizeTool) {
        Point2D center = nodeGraphicAttribute.getCoordinate().getCoordinate();
        Dimension size = nodeGraphicAttribute.getDimension().getDimension();

        Ellipse2D smallEllipse = new Ellipse2D.Double(center.getX()
                - (size.getWidth() / 2) + sensitiveAreaSize, center.getY()
                - (size.getHeight() / 2) + sensitiveAreaSize, size.getWidth()
                - (2 * sensitiveAreaSize), size.getHeight()
                - (2 * sensitiveAreaSize));

        if (!smallEllipse.contains(position)) {
            int xDir;
            int yDir;

            if (position.getX() < center.getX()) {
                xDir = -1;
            } else {
                xDir = 1;
            }

            if (position.getY() < center.getY()) {
                yDir = -1;
            } else {
                yDir = 1;
            }

            Point direction = new Point(xDir, yDir);

            // System.out.println("directino:" + direction);

            if ((direction.getX() == -1) && (direction.getY() == -1)) {
                Cursor c = new Cursor(Cursor.NW_RESIZE_CURSOR);
                nodeResizeTool.getActiveJComponent().setCursor(c);
            } else if ((direction.getX() == 1) && (direction.getY() == -1)) {
                Cursor c = new Cursor(Cursor.NE_RESIZE_CURSOR);
                nodeResizeTool.getActiveJComponent().setCursor(c);
            } else if ((direction.getX() == 1) && (direction.getY() == 1)) {
                Cursor c = new Cursor(Cursor.SE_RESIZE_CURSOR);
                nodeResizeTool.getActiveJComponent().setCursor(c);
            } else if ((direction.getX() == -1) && (direction.getY() == 1)) {
                Cursor c = new Cursor(Cursor.SW_RESIZE_CURSOR);
                nodeResizeTool.getActiveJComponent().setCursor(c);
            }
            nodeResizeTool.switchToResizeMode();
            return new EllipseNodeResizer(nodeGraphicAttribute, position,
                    direction);
        } else {
            nodeResizeTool.switchToDefaultMode();
            Cursor c = new Cursor(Cursor.DEFAULT_CURSOR);
            nodeResizeTool.getActiveJComponent().setCursor(c);
            return null;
        }
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
        double xOffset = position.getX() - oldPosition.getX();
        double yOffset = position.getY() - oldPosition.getY();

        double dx = 0;
        double dy = 0;

        Dimension oldDim = nodeGraphicAttribute.getDimension().getDimension();
        double newWidth = oldDim.getWidth();
        double newHeight = oldDim.getHeight();

        if (direction.getX() == -1) {
            newWidth = Math.max(minNodeSize, oldDim.getWidth() - xOffset);
            dx = -(newWidth - oldDim.getWidth()) / 2;
        } else if (direction.getX() == 1) {
            newWidth = Math.max(minNodeSize, oldDim.getWidth() + xOffset);
            dx = (newWidth - oldDim.getWidth()) / 2;
        }

        if (direction.getY() == -1) {
            newHeight = Math.max(minNodeSize, oldDim.getHeight() - yOffset);
            dy = -(newHeight - oldDim.getHeight()) / 2;
        } else if (direction.getY() == 1) {
            newHeight = Math.max(minNodeSize, oldDim.getHeight() + yOffset);
            dy = (newHeight - oldDim.getHeight()) / 2;
        }

        Dimension newDim = new Dimension((int) newWidth, (int) (newHeight));

        nodeGraphicAttribute.getDimension().setDimension(newDim);

        Point2D oldCenter = nodeGraphicAttribute.getCoordinate()
                .getCoordinate();
        Point2D newCenter = new Point2D.Double(oldCenter.getX() + dx, oldCenter
                .getY()
                + dy);
        nodeGraphicAttribute.getCoordinate().setCoordinate(newCenter);

        oldPosition = position;

        // double xOffset = position.getX() - oldPosition.getX();
        // double yOffset = position.getY() - oldPosition.getY();
        // Point2D oldCenter =
        // nodeGraphicAttribute.getCoordinate().getCoordinate();
        // Dimension oldSize =
        // nodeGraphicAttribute.getDimension().getDimension();
        // if (oldSize.getWidth() + xOffset < minNodeSize) {
        // xOffset = minNodeSize - oldSize.getWidth();
        // }
        // if (oldSize.getHeight() + yOffset < minNodeSize) {
        // yOffset = minNodeSize - oldSize.getHeight();
        // }
        // Point2D newCenter = new Point2D.Double(oldCenter.getX() + xOffset /
        // 2,
        // oldCenter.getY() + yOffset / 2);
        // Dimension newSize = new Dimension((int)(oldSize.getWidth() +
        // xOffset),
        // (int)(oldSize.getHeight() + yOffset));
        // nodeGraphicAttribute.getCoordinate().setCoordinate(newCenter);
        // nodeGraphicAttribute.getDimension().setDimension(newSize);
        // oldPosition = position;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
