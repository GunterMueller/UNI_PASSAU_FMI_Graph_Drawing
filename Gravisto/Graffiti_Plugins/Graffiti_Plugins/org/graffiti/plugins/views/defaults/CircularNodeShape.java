// =============================================================================
//
//   CircularNodeShape.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: CircularNodeShape.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.views.defaults;

import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;

import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;

/**
 * Concrete class representing an ellipse.
 */
public abstract class CircularNodeShape extends RectangularNodeShape {

    /**
     * The <code>Ellipse2D</code> that is represented by this
     * <code>NodeShape</code>.
     */
    protected Ellipse2D ell2D;

    /**
     * Small value used to compare with zero. Propably silly but doesn't matter.
     */
    private final double EPSILON = Double.MIN_VALUE;

    /**
     * The constructor creates an ellipse using default values.
     */
    public CircularNodeShape() {
        this.ell2D = new Ellipse2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.thickShape = new Ellipse2D.Double();
        ((RectangularShape) this.thickShape).setFrame(ell2D.getBounds2D());
    }

    /**
     * Calculates the intersection between this shape and a line.
     * 
     * @param line
     * 
     * @return the intersection point or null if shape and line do not
     *         intersect.
     */
    @Override
    public abstract Point2D getIntersection(Line2D line);

    /**
     * DOCUMENT ME!
     * 
     * @param t
     *            DOCUMENT ME!
     * @param d
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public PathIterator getPathIterator(AffineTransform t, double d) {
        return this.ell2D.getPathIterator(t, d);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param t
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public PathIterator getPathIterator(AffineTransform t) {
        return this.ell2D.getPathIterator(t);
    }

    /**
     * This method sets all necessary properties using the values contained
     * within the <code>CollectionAttribute</code> (like size etc.).
     * 
     * @param nodeAttr
     *            The attribute that contains all necessary information to
     *            construct an ellipse.
     */
    public void buildShape(NodeGraphicAttribute nodeAttr) {
        this.nodeAttr = nodeAttr;

        DimensionAttribute dim = nodeAttr.getDimension();
        double w = Math.round(dim.getWidth());
        double h = Math.round(dim.getHeight());

        double ft = Math.round(Math.min(nodeAttr.getFrameThickness(), Math.min(
                w / 2d, h / 2d)));

        double offset = Math.round(ft / 2d);

        this.ell2D.setFrame(offset, offset, w - 2 * offset - 1d, h - 2 * offset
                - 1d);

        ((RectangularShape) this.thickShape).setFrame(0, 0, w, h);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param a
     *            DOCUMENT ME!
     * @param b
     *            DOCUMENT ME!
     * @param c
     *            DOCUMENT ME!
     * @param d
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public boolean contains(double a, double b, double c, double d) {
        return this.thickShape.contains(a, b, c, d);

        // return this.ell2D.contains(a,b,c,d);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param a
     *            DOCUMENT ME!
     * @param b
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public boolean contains(double a, double b) {
        return this.thickShape.contains(a, b);

        // return this.ell2D.contains(a,b);
    }

    /**
     * Calculates the intersection between a circle and a line.
     * 
     * @param circle
     * @param intLine
     * 
     * @return the intersection point or null if shape and line do not
     *         intersect.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    protected Point2D getIntersectionWithCircle(Ellipse2D circle, Line2D intLine) {
        if (circle.getWidth() != circle.getHeight())
            throw new IllegalArgumentException(
                    "First parameter must be a circle, i.e. height and width "
                            + "must be equal. Were: width=" + circle.getWidth()
                            + "  height=" + circle.getHeight());

        double cx = circle.getCenterX() - 0.5;
        double cy = circle.getCenterY() - 0.5;
        double rad = (circle.getWidth() - 2) / 2;
        double sx = intLine.getX1();
        double sy = intLine.getY1();
        double tx = intLine.getX2();
        double ty = intLine.getY2();

        double a = ((tx - sx) * (tx - sx)) + ((ty - sy) * (ty - sy));
        double b = 2 * (((tx - sx) * (sx - cx)) + ((ty - sy) * (sy - cy)));
        double c = ((cx * cx) + (cy * cy) + (sx * sx) + (sy * sy))
                - (2 * ((cx * sx) + (cy * sy))) - (rad * rad);
        double deter = (b * b) - (4 * a * c);

        if (deter < 0)
            // line does not intersect
            return null;
        else if (deter <= EPSILON) {
            // line is tangent
            double u = (-b) / (2 * a);

            return calculatePointOnLine(u, intLine.getP1(), intLine.getP2());
        } else {
            double detersqr = Math.sqrt(deter);
            double u1 = (-b + detersqr) / (2 * a);
            double u2 = (-b - detersqr) / (2 * a);

            // there should be only one intersection point ...
            if ((u1 >= 0) && (u1 <= 1))
                // System.out.println(" first");
                return calculatePointOnLine(u1, intLine.getP1(), intLine
                        .getP2());

            if ((u2 >= 0) && (u2 <= 1))
                // System.out.println(" second");
                return calculatePointOnLine(u2, intLine.getP1(), intLine
                        .getP2());

            // line does not intersect
            return null;
        }
    }

    /**
     * Calculates a point given by start point s and a factor u times the vector
     * given by end point minus start point.
     * 
     * @param u
     *            factor
     * @param s
     *            start point
     * @param t
     *            end point
     * 
     * @return new point s + u(t-s)
     */
    private Point2D calculatePointOnLine(double u, Point2D s, Point2D t) {
        double diffx = t.getX() - s.getX();
        double diffy = t.getY() - s.getY();

        return new Point2D.Double(s.getX() + (u * diffx), s.getY()
                + (u * diffy));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
