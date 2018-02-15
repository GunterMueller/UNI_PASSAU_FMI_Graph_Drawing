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
public class ToricalGrid extends AbstractGrid {
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

    @GridParameter
    public int horizontalCells = 5;

    @GridParameter
    public int verticalCells = 3;

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

    @GridParameter
    private boolean isBorderVisible = true;

    private static final int BORDER_LINE_LENGTH = 3;
    private static final int BORDER_LINE_GAP = 3;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Shape> getShapes(Rectangle2D area) {
        LinkedList<Shape> list = new LinkedList<Shape>();
        if (!isHorizontalVisible && !isVerticalVisible && !isBorderVisible)
            return list;
        GeneralPath path = new GeneralPath();
        double minX = area.getMinX() - 5.0;
        double maxX = area.getMaxX() + 5.0;
        double minY = area.getMinY() - 5.0;
        double maxY = area.getMaxY() + 5.0;
        int torusHeight = cellHeight * verticalCells;
        int torusWidth = cellWidth * horizontalCells;
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
        if (isBorderVisible) {
            double startY = origin.getY()
                    + Math.floor((minY - origin.getY()) / torusHeight)
                    * torusHeight - cellHeight / 2;
            double startX = origin.getX()
                    + Math.floor((minX - origin.getX()) / torusWidth)
                    * torusWidth - cellWidth / 2;
            double y = startY;
            while (y <= maxY) {
                double dx = startX - BORDER_LINE_LENGTH / 2;
                while (dx <= maxX) {
                    path.moveTo((float) dx, (float) y);
                    path.lineTo((float) dx + BORDER_LINE_LENGTH, (float) y);
                    dx += BORDER_LINE_LENGTH + BORDER_LINE_GAP;
                }
                y += torusHeight;
            }
            double x = startX;
            while (x <= maxX) {
                double dy = startY - BORDER_LINE_LENGTH / 2;
                while (dy <= maxY) {
                    path.moveTo((float) x, (float) dy);
                    path.lineTo((float) x, (float) dy + BORDER_LINE_LENGTH);
                    dy += BORDER_LINE_LENGTH + BORDER_LINE_GAP;
                }
                x += torusWidth;
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
