// =============================================================================
//
//   RectNodeResizer.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: RectNodeResizer.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced.nodeResize;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;

import org.graffiti.graphics.NodeGraphicAttribute;

/**
 * @deprecated
 */
@Deprecated
public class RectNodeResizer implements NodeResizer {

    /** DOCUMENT ME! */
    private NodeGraphicAttribute nodeGraphicAttribute;

    /** DOCUMENT ME! */
    private Point direction = null;

    /** DOCUMENT ME! */
    private Point oldPosition = null;

    /**
     * Creates a new RectNodeResizer object.
     * 
     * @param nodeGraphicAttribute
     *            DOCUMENT ME!
     * @param direction
     *            DOCUMENT ME!
     * @param oldPosition
     *            DOCUMENT ME!
     */
    private RectNodeResizer(NodeGraphicAttribute nodeGraphicAttribute,
            Point direction, Point oldPosition) {
        this.nodeGraphicAttribute = nodeGraphicAttribute;
        this.direction = direction;
        this.oldPosition = oldPosition;
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
    public static Point getDirection(NodeGraphicAttribute nodeGraphicAttribute,
            double sensitiveAreaSize, Point position) {
        double posX = position.getX();
        double posY = position.getY();

        Point2D compPos = nodeGraphicAttribute.getCoordinate().getCoordinate();
        Dimension compSize = nodeGraphicAttribute.getDimension().getDimension();

        // System.out.println("Start: Position = " + position
        // + "; compPos = " + compPos + "; compSize = " + compSize);
        double middleX = compPos.getX();
        double middleY = compPos.getY();
        double halfWidth = 0.5 * compSize.getWidth();
        double halfHeight = 0.5 * compSize.getHeight();

        int xDirection;
        int yDirection;

        if ((posX >= (middleX - halfWidth))
                && (posX < (middleX - halfWidth + sensitiveAreaSize))) {
            xDirection = -1;
        } else if ((posX >= (middleX - halfWidth + sensitiveAreaSize))
                && (posX < ((middleX + halfWidth) - sensitiveAreaSize))) {
            xDirection = 0;
        } else if ((posX >= ((middleX + halfWidth) - sensitiveAreaSize))
                && (posX <= (middleX + halfWidth))) {
            xDirection = 1;
        } else
            // position not inside node (then, in fact, this method shouldn't
            // even be called)
            return null;

        if ((posY >= (middleY - halfHeight))
                && (posY < (middleY - halfHeight + sensitiveAreaSize))) {
            yDirection = -1;
        } else if ((posY >= (middleY - halfHeight + sensitiveAreaSize))
                && (posY < ((middleY + halfHeight) - sensitiveAreaSize))) {
            yDirection = 0;
        } else if ((posY >= ((middleY + halfHeight) - sensitiveAreaSize))
                && (posY <= (middleY + halfHeight))) {
            yDirection = 1;
        } else
            return null;

        if ((xDirection == 0) && (yDirection == 0))
            return null;
        else
            return new Point(xDirection, yDirection);
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
     * @param nodeResizeTool
     * 
     * @return DOCUMENT ME!
     */
    public static RectNodeResizer createResizer(
            NodeGraphicAttribute nodeGraphicAttribute,
            double sensitiveAreaSize, Point position,
            NodeResizeTool nodeResizeTool) {

        Point direction = getDirection(nodeGraphicAttribute, sensitiveAreaSize,
                position);

        if (direction != null) {
            if ((direction.getX() == -1) && (direction.getY() == -1)) {
                Cursor c = new Cursor(Cursor.NW_RESIZE_CURSOR);
                nodeResizeTool.getActiveJComponent().setCursor(c);
            } else if ((direction.getX() == 0) && (direction.getY() == -1)) {
                Cursor c = new Cursor(Cursor.N_RESIZE_CURSOR);
                nodeResizeTool.getActiveJComponent().setCursor(c);
            } else if ((direction.getX() == 1) && (direction.getY() == -1)) {
                Cursor c = new Cursor(Cursor.NE_RESIZE_CURSOR);
                nodeResizeTool.getActiveJComponent().setCursor(c);
            } else if ((direction.getX() == 1) && (direction.getY() == 0)) {
                Cursor c = new Cursor(Cursor.E_RESIZE_CURSOR);
                nodeResizeTool.getActiveJComponent().setCursor(c);
            } else if ((direction.getX() == 1) && (direction.getY() == 1)) {
                Cursor c = new Cursor(Cursor.SE_RESIZE_CURSOR);
                nodeResizeTool.getActiveJComponent().setCursor(c);
            } else if ((direction.getX() == 0) && (direction.getY() == 1)) {
                Cursor c = new Cursor(Cursor.S_RESIZE_CURSOR);
                nodeResizeTool.getActiveJComponent().setCursor(c);
            } else if ((direction.getX() == -1) && (direction.getY() == 1)) {
                Cursor c = new Cursor(Cursor.SW_RESIZE_CURSOR);
                nodeResizeTool.getActiveJComponent().setCursor(c);
            } else if ((direction.getX() == -1) && (direction.getY() == 0)) {
                Cursor c = new Cursor(Cursor.W_RESIZE_CURSOR);
                nodeResizeTool.getActiveJComponent().setCursor(c);
            } else {
                // System.out.println("???????????????????");
                Cursor c = new Cursor(Cursor.DEFAULT_CURSOR);
                nodeResizeTool.getActiveJComponent().setCursor(c);
            }

            // System.out.print("RectNodeResizer: ");
            nodeResizeTool.switchToResizeMode();
            return new RectNodeResizer(nodeGraphicAttribute, direction,
                    position);
        } else {

            // System.out.print("RectNodeResizer: ");
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

        // System.out.println("direction:" + direction);
        oldPosition = position;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
