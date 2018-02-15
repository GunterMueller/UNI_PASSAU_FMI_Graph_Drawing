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
 * Orthogonal grid.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class OrthogonalGrid extends AbstractGrid {
    /**
     * Distance of horizontal grid lines.
     */
    @GridParameter(min = 2, sliderMin = 2, sliderMax = 100)
    public int cellHeight = 50;

    /**
     * Distance of vertical grid lines.
     */
    @GridParameter(min = 2, sliderMin = 2, sliderMax = 100)
    public int cellWidth = 50;

    /**
     * Denotes if the horizontal grid lines are visible.
     */
    @GridParameter
    public boolean isHorizontalVisible = true;

    /**
     * Denotes if the vertical grid lines are visible.
     */
    @GridParameter
    public boolean isVerticalVisible = true;

    @GridParameter
    public int xMin = Integer.MIN_VALUE;

    @GridParameter
    public int xMax = Integer.MAX_VALUE;

    @GridParameter
    public int yMin = Integer.MIN_VALUE;

    @GridParameter
    public int yMax = Integer.MAX_VALUE;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Shape> getShapes(Rectangle2D area) {
        LinkedList<Shape> list = new LinkedList<Shape>();
        if (!isHorizontalVisible && !isVerticalVisible)
            return list;
        GeneralPath path = new GeneralPath();
        double minX = Math.max(area.getMinX() - 5.0, xMin);
        double maxX = Math.min(area.getMaxX() + 5.0, xMax);
        double minY = Math.max(area.getMinY() - 5.0, yMin);
        double maxY = Math.min(area.getMaxY() + 5.0, yMax);
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
        list.add(path);
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Point2D snap(Point2D point, double tolerance) {
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
