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
 * Hexagonal grid.
 * 
 * @author Thomas Unfried
 * @version $Revision$ $Date$
 */
public class HexagonalGrid extends AbstractGrid {
    /**
     * Distance of horizontal grid lines.
     */
    @GridParameter(min = 2, sliderMin = 2, sliderMax = 100)
    private int hexCellWidth = 50;

    /**
     * Distance of diagonal grid lines.
     */
    @GridParameter(min = 2, sliderMin = 2, sliderMax = 100)
    private double hexCellHeight = (Math.sqrt(3) / 2) * hexCellWidth;

    /**
     * Denotes if the horizontal grid lines are visible.
     */
    @GridParameter
    private boolean isHorizontalVisible = true;

    /**
     * Denotes if the diagonal downward grid lines are visible.
     */
    @GridParameter
    private boolean isDiagonalDownVisible = true;

    /**
     * Denotes if the diagonal upward grid lines are visible.
     */
    @GridParameter
    private boolean isDiagonalUpVisible = true;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Shape> getShapes(Rectangle2D area) {
        LinkedList<Shape> list = new LinkedList<Shape>();
        if (!isHorizontalVisible && !isDiagonalDownVisible
                && !isDiagonalUpVisible)
            return list;
        GeneralPath path = new GeneralPath();
        double minX = area.getMinX() - 5.0;
        double maxX = area.getMaxX() + 5.0;
        double minY = area.getMinY() - 5.0;
        double maxY = area.getMaxY() + 5.0;
        if (isDiagonalDownVisible) {
            double x = origin.getX()
                    + Math.floor((minX - origin.getX()) / hexCellWidth)
                    * hexCellWidth;
            double y = origin.getY()
                    + Math.floor((minY - origin.getY()) / hexCellHeight)
                    * hexCellHeight;
            if (Math
                    .abs(Math.floor((minY - origin.getY()) / hexCellHeight) % 2) == 0) {
                y -= hexCellHeight;
            }

            while (x <= maxX) {
                double x1 = x;
                double y1 = y;
                path.moveTo((float) x, (float) y);
                while (x1 <= maxX && y1 <= maxY) {
                    x1 += hexCellWidth / 2.0;
                    y1 += hexCellHeight;
                }
                path.lineTo((float) x1, (float) y1);
                x += hexCellWidth;
            }

            x = origin.getX()
                    + Math.floor((minX - origin.getX()) / hexCellWidth)
                    * hexCellWidth;
            y = origin.getY()
                    + Math.floor((minY - origin.getY()) / hexCellHeight)
                    * hexCellHeight;
            if (Math
                    .abs(Math.floor((minY - origin.getY()) / hexCellHeight) % 2) == 0) {
                y -= hexCellHeight;
            }
            y += 2 * hexCellHeight;
            while (y <= maxY) {
                double x1 = x;
                double y1 = y;
                path.moveTo((float) x, (float) y);
                while (x1 <= maxX && y1 <= maxY) {
                    x1 += hexCellWidth / 2.0;
                    y1 += hexCellHeight;
                }
                path.lineTo((float) x1, (float) y1);
                y += 2 * hexCellHeight;
            }
        }

        if (isDiagonalUpVisible) {
            double x = origin.getX()
                    + Math.floor((minX - origin.getX()) / hexCellWidth)
                    * hexCellWidth;
            double y = origin.getY()
                    + Math.floor((minY - origin.getY()) / hexCellHeight)
                    * hexCellHeight;
            if (Math
                    .abs(Math.floor((minY - origin.getY()) / hexCellHeight) % 2) == 0) {
                y -= hexCellHeight;
            }

            while (x <= maxX) {
                double x1 = x;
                double y1 = y;
                path.moveTo((float) x, (float) y);
                while (x1 >= minX && y1 <= maxY) {
                    x1 -= hexCellWidth / 2.0;
                    y1 += hexCellHeight;
                }
                path.lineTo((float) x1, (float) y1);
                x += hexCellWidth;
            }

            x = origin.getX()
                    + Math.floor((maxX - origin.getX()) / hexCellWidth)
                    * hexCellWidth + 2 * hexCellWidth;
            y = origin.getY()
                    + Math.floor((minY - origin.getY()) / hexCellHeight)
                    * hexCellHeight - 2 * hexCellHeight;
            if (Math
                    .abs(Math.floor((minY - origin.getY()) / hexCellHeight) % 2) == 0) {
                y -= hexCellHeight;
            }
            while (y <= maxY) {
                double x1 = x;
                double y1 = y;
                path.moveTo((float) x, (float) y);
                while (x1 >= minX && y1 <= maxY) {
                    x1 -= hexCellWidth / 2.0;
                    y1 += hexCellHeight;
                }
                path.lineTo((float) x1, (float) y1);
                y += 2 * hexCellHeight;
            }
        }

        if (isHorizontalVisible) {
            double x = origin.getX()
                    + Math.floor((minX - origin.getX()) / hexCellWidth)
                    * hexCellWidth;
            double y = origin.getY()
                    + Math.floor((minY - origin.getY()) / hexCellHeight)
                    * hexCellHeight;
            if (Math
                    .abs(Math.floor((minY - origin.getY()) / hexCellHeight) % 2) == 0) {
                y -= hexCellHeight;
            }
            while (y <= maxY) {
                path.moveTo((float) x, (float) y);
                path.lineTo((float) maxX, (float) y);
                y += hexCellHeight;
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
        double y = point.getY();
        double vx = (x - origin.getX()) / hexCellWidth;
        double vy = (y - origin.getY()) / hexCellHeight;
        double ix = Math.floor(vx);
        double fracX = vx - ix;
        double iy = Math.floor(vy);
        double fracY = vy - iy;

        if (iy % 2 == 0) {
            // ascending diagonal
            if (fracX <= 0.5) {
                double fracDiag = fracX + fracY / 2;
                if (Math.abs(fracDiag - 0.5) < tolerance / hexCellWidth) {
                    if (fracY < tolerance / hexCellHeight && fracY <= 0.5) {
                        x = hexCellWidth * (ix + 0.5);
                    } else if (fracY > 1 - tolerance / hexCellHeight) {
                        x = hexCellWidth * ix;
                    } else {
                        x += hexCellWidth * (0.5 - fracDiag);
                    }
                }

            }
            // descending diagonal
            else {
                double fracDiag = (fracX - 0.5) + (0.5 - fracY / 2);
                if (Math.abs(fracDiag - 0.5) < tolerance / hexCellWidth) {
                    if (fracY < tolerance / hexCellHeight && fracY <= 0.5) {
                        x = hexCellWidth * (ix + 0.5);
                    } else if (fracY > 1 - tolerance / hexCellHeight) {
                        x = hexCellWidth * (ix + 1);
                    } else {
                        x += hexCellWidth * (0.5 - fracDiag);
                    }

                }

            }
        } else {
            // ascending diagonal
            if (fracX <= 0.5) {
                double fracDiag = fracX + (0.5 - fracY / 2);
                if (Math.abs(fracDiag - 0.5) < tolerance / hexCellWidth) {
                    if (fracY < tolerance / hexCellHeight && fracY <= 0.5) {
                        x = hexCellWidth * (ix);
                    } else if (fracY > 1 - tolerance / hexCellHeight) {
                        x = hexCellWidth * (ix + 0.5);
                    } else {
                        x += hexCellWidth * (0.5 - fracDiag);
                    }
                }

            }
            // descending diagonal
            else {
                double fracDiag = (fracX - 0.5) + fracY / 2;
                if (Math.abs(fracDiag - 0.5) < tolerance / hexCellWidth) {
                    if (fracY < tolerance / hexCellHeight && fracY <= 0.5) {
                        x = hexCellWidth * (ix + 1);
                    } else if (fracY > 1 - tolerance / hexCellHeight) {
                        x = hexCellWidth * (ix + 0.5);
                    } else {
                        x += hexCellWidth * (0.5 - fracDiag);
                    }
                }
            }
        }

        if (fracY < tolerance / hexCellHeight && fracY <= 0.5) {
            y = origin.getY() + hexCellHeight * iy;
        } else if (fracY > 1 - tolerance / hexCellHeight) {
            y = origin.getY() + hexCellHeight * (1 + iy);
        }

        return new Point2D.Double(x + origin.getX(), y);
    }
}
