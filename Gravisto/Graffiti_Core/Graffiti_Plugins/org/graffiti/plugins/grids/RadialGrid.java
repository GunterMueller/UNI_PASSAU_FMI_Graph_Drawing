package org.graffiti.plugins.grids;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.plugin.view.AbstractGrid;
import org.graffiti.plugin.view.GridParameter;

/**
 * Radial grid.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class RadialGrid extends AbstractGrid {
    /**
     * Distance of circles.
     */
    @GridParameter
    public double circleDistance = 100.0;

    /**
     * Count of sectors.
     */
    @GridParameter
    public int sectorCount = 8;

    /**
     * Rotation of the grid in degrees.
     */
    @GridParameter(min = 0.0, max = 360.0, sliderMin = 0.0, sliderMax = 359.0)
    private double rotation = 0.0;

    /**
     * Denotes if the circles are visible.
     */
    @GridParameter
    private boolean areCirclesVisible = true;

    /**
     * Denotes if the radii are visible.
     */
    @GridParameter
    private boolean areRadiiVisible = true;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Shape> getShapes(Rectangle2D area) {
        LinkedList<Shape> list = new LinkedList<Shape>();
        double minR2 = Double.MAX_VALUE;
        double maxR2 = 0.0;
        double x0 = area.getMinX();
        double y0 = area.getMinY();
        double x1 = area.getMaxX();
        double y1 = area.getMaxY();
        for (Point2D p : new Point2D[] { new Point2D.Double(x0, y0),
                new Point2D.Double(x1, y0), new Point2D.Double(x0, y1),
                new Point2D.Double(x1, y1) }) {
            double dx = p.getX() - origin.getX();
            double dy = p.getY() - origin.getY();
            double r2 = dx * dx + dy * dy;
            minR2 = Math.min(minR2, r2);
            maxR2 = Math.max(maxR2, r2);
        }
        if (area.contains(origin)) {
            minR2 = 0.0;
        }
        if (areCirclesVisible) {
            // int iFrom = Math.max(1, (int) Math.floor(Math.sqrt(minR2)
            // / circleDistance));
            int iFrom = 0;
            int iTo = (int) Math.ceil(Math.sqrt(maxR2) / circleDistance);
            for (int i = iFrom; i <= iTo; i++) {
                double r = i * circleDistance;
                list.add(new Ellipse2D.Double(origin.getX() - r, origin.getY()
                        - r, 2 * r, 2 * r));
            }
        }
        if (areRadiiVisible) {
            double maxR = Math.sqrt(maxR2);
            double rot = rotation / 180.0 * Math.PI;
            double phi = Math.PI * 2.0 / sectorCount;
            for (int i = 0; i < sectorCount; i++) {
                double v0 = Math.cos(rot + phi * i);
                double v1 = Math.sin(rot + phi * i);
                list.add(new Line2D.Double(origin.getX(), origin.getY(), origin
                        .getX()
                        + v0 * maxR, origin.getY() + v1 * maxR));
            }
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Point2D snap(Point2D point, double tolerance) {
        double dx = point.getX() - origin.getX();
        double dy = point.getY() - origin.getY();
        double r = Math.sqrt(dx * dx + dy * dy);
        double phi = Math.PI * 2.0 / sectorCount;
        double rot = rotation / 180.0 * Math.PI;
        double alpha = (Math.atan2(dy, dx) - rot) % (2.0 * Math.PI);
        if (alpha < 0) {
            alpha += 2.0 * Math.PI;
        }
        double beta = alpha % phi;
        int i = (int) Math.floor(alpha / phi);
        double epsilon = tolerance / r;
        double v0;
        double v1;
        if (beta < epsilon && beta < phi / 2.0) {
            v0 = Math.cos(rot + phi * i);
            v1 = Math.sin(rot + phi * i);
        } else if (beta > phi - epsilon) {
            v0 = Math.cos(rot + phi * (i + 1));
            v1 = Math.sin(rot + phi * (i + 1));
        } else {
            v0 = dx / r;
            v1 = dy / r;
        }
        double q = r / circleDistance;
        int ir = (int) Math.floor(q);
        double fracR = q - ir;
        if (fracR < tolerance / circleDistance && fracR < 0.5) {
            r = ir * circleDistance;
        } else if (fracR > 1 - tolerance / circleDistance) {
            r = (ir + 1) * circleDistance;
        }
        return new Point2D.Double(Math.floor(origin.getX() + v0 * r), Math
                .floor(origin.getY() + v1 * r));
    }
}
