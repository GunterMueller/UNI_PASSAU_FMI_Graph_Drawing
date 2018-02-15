package org.graffiti.plugins.grids;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.plugin.view.AbstractGrid;
import org.graffiti.plugin.view.GridParameter;

/**
 * Octogonal grid.
 * 
 * @author matzeder
 * @version $Revision$ $Date$
 */
public class OctogonalGrid extends AbstractGrid {
    /**
     * Distance of horizontal grid lines.
     */
    @GridParameter(min = 2, sliderMin = 2, sliderMax = 100)
    private int cellHeight = 50;

    /**
     * Distance of vertical grid lines.
     */
    @GridParameter(min = 2, sliderMin = 2, sliderMax = 100)
    private int cellWidth = 50;

    /**
     * Denotes if the horizontal grid lines are visible.
     */
    @GridParameter
    private boolean isHorizontalVisible = true;

    /**
     * Denotes if the vertical grid lines are visible.
     */
    @GridParameter
    private boolean isVerticalVisible = true;

    /**
     * Denotes if the diagonal downward grid lines are visible.
     */
    @GridParameter
    private boolean isDiagonalDownVisible = true;

    /**
     * Denotes if the diagonal downward grid lines are visible.
     */
    @GridParameter
    private boolean isDiagonalUpVisible = true;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Shape> getShapes(Rectangle2D area) {
        LinkedList<Shape> list = new LinkedList<Shape>();
        if (!isHorizontalVisible && !isVerticalVisible)
            return list;
        GeneralPath path = new GeneralPath();
        double minX = area.getMinX() - 5.0;
        double maxX = area.getMaxX() + 5.0;
        double minY = area.getMinY() - 5.0;
        double maxY = area.getMaxY() + 5.0;

        if (isVerticalVisible) {
            double x = origin.getX()
                    + Math.floor((minX - origin.getX()) / cellWidth)
                    * cellWidth;

            while (x <= maxX) {
                path.moveTo((float) x, (float) minY);
                path.lineTo((float) x, (float) maxY);
                x += cellWidth;
            }
        }
        if (isHorizontalVisible) {
            double y = origin.getY()
                    + Math.floor((minY - origin.getY()) / cellHeight)
                    * cellHeight;
            while (y <= maxY) {
                path.moveTo((float) minX, (float) y);
                path.lineTo((float) maxX, (float) y);
                y += cellHeight;
            }
        }
        // third direction
        if (isDiagonalDownVisible) {
            double x = origin.getX()
                    + Math.floor((minX - origin.getX()) / cellWidth)
                    * cellWidth;
            double y = origin.getY()
                    + Math.floor((minY - origin.getY()) / cellHeight)
                    * cellHeight;
            if (Math.abs(Math.floor((minY - origin.getY()) / cellHeight) % 2) == 0) {
                y -= cellHeight;
            }

            while (x <= maxX) {
                double x1 = x;
                double y1 = y;
                path.moveTo((float) x, (float) y);
                while (x1 <= maxX && y1 <= maxY) {
                    x1 += cellWidth;
                    y1 += cellHeight;
                }
                path.lineTo((float) x1, (float) y1);
                x += cellWidth;
            }

            x = origin.getX() + Math.floor((minX - origin.getX()) / cellWidth)
                    * cellWidth;
            y = origin.getY() + Math.floor((minY - origin.getY()) / cellHeight)
                    * cellHeight;
            if (Math.abs(Math.floor((minY - origin.getY()) / cellHeight) % 2) == 0) {
                y -= cellHeight;
            }
            y += cellHeight;
            while (y <= maxY) {
                double x1 = x;
                double y1 = y;
                path.moveTo((float) x, (float) y);
                while (x1 <= maxX && y1 <= maxY) {
                    x1 += cellWidth;
                    y1 += cellHeight;
                }
                path.lineTo((float) x1, (float) y1);
                y += cellHeight;
            }
        }

        // fourth direction
        if (isDiagonalUpVisible) {
            double startX = origin.getX()
                    + Math.floor((minX - origin.getX()) / cellWidth)
                    * cellWidth;
            double startY = origin.getY()
                    + Math.floor((minY - origin.getY()) / cellHeight)
                    * cellHeight;

            double y = startY;
            double x = startX;

            while (y <= maxY) {

                path.moveTo((float) startX, (float) (y));
                path.lineTo((float) x, (float) (startY));
                y += cellHeight;
                x += cellWidth;
            }

            double nextX = 0;
            while (startX + nextX <= maxX) {

                path.moveTo((float) startX + nextX, (float) (y));
                path.lineTo((float) x + nextX, (float) (startY));
                nextX += cellWidth;
            }
        }
        list.add(path);
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Point2D snap(Point2D point, double tolerance) {
        double x = point.getX();
        double v = (x - origin.getX()) / cellWidth;
        double ix = Math.floor(v);
        double fracX = v - ix;
        if (fracX < tolerance / cellWidth && fracX <= 0.5) {
            x = origin.getX() + cellWidth * ix;
        } else if (fracX > 1 - tolerance / cellWidth) {
            x = origin.getX() + cellWidth * (1 + ix);
        }
        double y = point.getY();
        v = (y - origin.getY()) / cellHeight;
        double iy = Math.floor(v);
        double fracY = v - iy;
        if (fracY < tolerance / cellHeight && fracY <= 0.5) {
            y = origin.getY() + cellHeight * iy;
        } else if (fracY > 1 - tolerance / cellHeight) {
            y = origin.getY() + cellHeight * (1 + iy);
        }
        return new Point2D.Double(Math.floor(x), Math.floor(y));
    }
}
