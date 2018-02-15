// =============================================================================
//
//   TestNodeShape.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: TestNodeShape.java 5769 2010-05-07 18:42:56Z gleissner $

package de.chris.plugins.shapes.test;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugin.view.NodeShape;
import org.graffiti.util.GraphicHelper;

/**
 * Class representing a rounded rectangle.
 */
public class TestNodeShape implements NodeShape {

    /** The standard width and height of the circle that rounds the corner. */
    protected final double DEFAULT_ARC_WH = 18;

    /** The standard height of the recangular shape. */
    protected final double DEFAULT_HEIGHT = 30;

    /** The standard width of the recangular shape. */
    protected final double DEFAULT_WIDTH = 30;

    /** The graphic attribute of the node this shape represents. */
    protected NodeGraphicAttribute nodeAttr;

    /**
     * The <code>Ellipse2D</code> that is represented by this
     * <code>NodeShape</code>.
     */
    protected RoundRectangle2D roundRect2D;

    /** The bounds including frameThickness. */
    protected RoundRectangle2D thickRoundRect2D;

    /**
     * The constructor creates an ellipse using default values.
     */
    public TestNodeShape() {
        roundRect2D = new RoundRectangle2D.Double(0, 0, DEFAULT_WIDTH,
                DEFAULT_HEIGHT, DEFAULT_ARC_WH, DEFAULT_ARC_WH);
        thickRoundRect2D = new RoundRectangle2D.Double();
        thickRoundRect2D.setRoundRect(roundRect2D);
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public Rectangle getBounds() {
        return thickRoundRect2D.getBounds();
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public Rectangle2D getBounds2D() {
        return thickRoundRect2D.getBounds2D();
    }

    /**
     * Calculates the intersection between this shape and a line.
     * 
     * @param line
     * 
     * @return the intersection point or null if shape and line do not
     *         intersect.
     */
    public Point2D getIntersection(Line2D line) {
        Rectangle2D rect = getRealBounds2D();
        double x = rect.getX();
        double y = rect.getY();
        double w = rect.getWidth();
        double h = rect.getHeight();
        double radius = DEFAULT_ARC_WH / 2d;

        Point2D lowerLeft = new Point2D.Double(x, (y + h) - radius);
        Point2D upperLeft = new Point2D.Double(x, y + radius);
        Point2D leftUpper = new Point2D.Double(x + radius, y);
        Point2D rightUpper = new Point2D.Double((x + w) - radius, y);
        Point2D upperRight = new Point2D.Double(x + w, y + radius);
        Point2D lowerRight = new Point2D.Double(x + w, (y + h) - radius);
        Point2D rightLower = new Point2D.Double((x + w) - radius, y + w);
        Point2D leftLower = new Point2D.Double(x + radius, y + w);

        // turn the round rectangle into 4 lines
        Line2D left = new Line2D.Double(lowerLeft, upperLeft);
        Line2D upper = new Line2D.Double(leftUpper, rightUpper);
        Line2D right = new Line2D.Double(upperRight, lowerRight);
        Line2D lower = new Line2D.Double(rightLower, leftLower);

        // testing with which line intersects with line
        // and then computing the intersection point
        if (left.intersectsLine(line))
            return GraphicHelper.getIntersection(left, line);
        else if (upper.intersectsLine(line))
            return GraphicHelper.getIntersection(upper, line);
        else if (right.intersectsLine(line))
            return GraphicHelper.getIntersection(right, line);
        else if (lower.intersectsLine(line))
            return GraphicHelper.getIntersection(lower, line);

        // intersection with upper left circle
        Ellipse2D upperLeftCircle2D = new Ellipse2D.Double(x, y,
                DEFAULT_ARC_WH, DEFAULT_ARC_WH);
        Point2D intUpperLeftCircle2D = intersectWithCircle(upperLeftCircle2D,
                line, "upper left");

        if (intUpperLeftCircle2D != null)
            return intUpperLeftCircle2D;

        // intersection with upper right circle
        Ellipse2D upperRightCircle2D = new Ellipse2D.Double((x + w)
                - DEFAULT_ARC_WH, y, DEFAULT_ARC_WH, DEFAULT_ARC_WH);
        Point2D intUpperRightCircle2D = intersectWithCircle(upperRightCircle2D,
                line, "upper right");

        if (intUpperRightCircle2D != null)
            return intUpperRightCircle2D;

        // intersection with lower left circle
        Ellipse2D lowerLeftCircle2D = new Ellipse2D.Double(x, (y + h)
                - DEFAULT_ARC_WH, DEFAULT_ARC_WH, DEFAULT_ARC_WH);
        Point2D intLowerLeftCircle2D = intersectWithCircle(lowerLeftCircle2D,
                line, "lower left");

        if (intLowerLeftCircle2D != null)
            return intLowerLeftCircle2D;

        // intersection with lower right circle
        Ellipse2D lowerRightCircle2D = new Ellipse2D.Double((x + w)
                - DEFAULT_ARC_WH, (y + h) - DEFAULT_ARC_WH, DEFAULT_ARC_WH,
                DEFAULT_ARC_WH);
        Point2D intLowerRightCircle2D = intersectWithCircle(lowerRightCircle2D,
                line, "lower right");

        if (intLowerRightCircle2D != null)
            return intLowerRightCircle2D;

        return null;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param at
     *            DOCUMENT ME!
     * @param flatness
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return roundRect2D.getPathIterator(at, flatness);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param at
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public PathIterator getPathIterator(AffineTransform at) {
        return roundRect2D.getPathIterator(at);
    }

    /**
     * Returns a <code>Rectangle2D</code> that represents the encapsulated
     * <code>RoundRectangle2D</code>, but has coordinates transformed relatively
     * to the view (instead of relatively to the <code>NodeComponent</code>).
     * 
     * @return a copy of the encapsulated rectangle but which has the real
     *         coordinates in the view.
     */
    public Rectangle2D getRealBounds2D() {
        Point2D coord = nodeAttr.getCoordinate().getCoordinate();

        Rectangle2D rect = getBounds2D();
        double w = rect.getWidth();
        double h = rect.getHeight();

        double realX = coord.getX() - (w / 2d);
        double realY = coord.getY() - (h / 2d);

        return new Rectangle2D.Double(realX, realY, w, h);
    }

    /**
     * This method sets all necessary properties using the values contained
     * within the <code>CollectionAttribute</code> (like size etc.).
     * 
     * @param nodeAttr
     *            The attribute that contains all necessary information to
     *            construct an circle.
     */
    public void buildShape(NodeGraphicAttribute nodeAttr) {
        this.nodeAttr = nodeAttr;

        DimensionAttribute dim = nodeAttr.getDimension();
        double w = dim.getWidth();
        double h = dim.getHeight();

        double ft = Math.floor(nodeAttr.getFrameThickness());
        double offset = ft / 2d;
        roundRect2D.setFrame(offset, offset, w, h);

        double corrWidth = w + ft;
        double corrHeight = h + ft;

        if (Math.floor(offset) == offset) {
            corrWidth = w + ft + 1;
            corrHeight = h + ft + 1;
        }

        thickRoundRect2D.setFrame(0, 0, corrWidth, corrHeight);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param x
     *            DOCUMENT ME!
     * @param y
     *            DOCUMENT ME!
     * @param w
     *            DOCUMENT ME!
     * @param h
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public boolean contains(double x, double y, double w, double h) {
        return thickRoundRect2D.contains(x, y, w, h);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param x
     *            DOCUMENT ME!
     * @param y
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public boolean contains(double x, double y) {
        return thickRoundRect2D.contains(x, y);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param p
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public boolean contains(Point2D p) {
        return contains(p.getX(), p.getY());
    }

    /**
     * DOCUMENT ME!
     * 
     * @param r
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public boolean contains(Rectangle2D r) {
        return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    /**
     * DOCUMENT ME!
     * 
     * @param x
     *            DOCUMENT ME!
     * @param y
     *            DOCUMENT ME!
     * @param w
     *            DOCUMENT ME!
     * @param h
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public boolean intersects(double x, double y, double w, double h) {
        return thickRoundRect2D.intersects(x, y, w, h);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param rect
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public boolean intersects(Rectangle2D rect) {
        return thickRoundRect2D.intersects(rect);
    }

    /**
     * Calculates a point given by start point s and a factor u times the vector
     * given by end point minus start point. It is a helper of
     * #intersectWithCircle.
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
        double diffX = t.getX() - s.getX();
        double diffY = t.getY() - s.getY();

        return new Point2D.Double(s.getX() + (u * diffX), s.getY()
                + (u * diffY));
    }

    /**
     * DOCUMENT ME!
     * 
     * @param circle
     *            DOCUMENT ME!
     * @param intLine
     *            DOCUMENT ME!
     * @param pos
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    private Point2D intersectWithCircle(Ellipse2D circle, Line2D intLine,
            String pos) {
        if (circle.getWidth() != circle.getHeight())
            throw new IllegalArgumentException(
                    "First parameter must be a circle, i.e. height and width "
                            + "must be equal. Were: width=" + circle.getWidth()
                            + "  height=" + circle.getHeight());

        double cx = circle.getCenterX();
        double cy = circle.getCenterY();
        double radius = circle.getWidth() / 2d;
        double sx = intLine.getX1();
        double sy = intLine.getY1();
        double tx = intLine.getX2();
        double ty = intLine.getY2();

        double a = ((tx - sx) * (tx - sx)) + ((ty - sy) * (ty - sy));
        double b = 2d * (((tx - sx) * (sx - cx)) + ((ty - sy) * (sy - cy)));
        double c = ((cx * cx) + (cy * cy) + (sx * sx) + (sy * sy))
                - (2d * ((cx * sx) + (cy * sy))) - (radius * radius);
        double discr = (b * b) - (4d * a * c);

        if (discr < 0d)
            // line does not intersect
            return null;
        else if (discr <= Double.MIN_VALUE) // epsilon test
        {
            // line is tangent
            double u = (-b) / (2 * a);

            Point2D res = calculatePointOnLine(u, intLine.getP1(), intLine
                    .getP2());

            if (((pos == "upper left") && (res.getX() <= cx) && (res.getY() <= cy))
                    || ((pos == "upper right") && (res.getX() >= cx) && (res
                            .getY() <= cy))
                    || ((pos == "lower right") && (res.getX() >= cx) && (res
                            .getY() >= cy))
                    || ((pos == "lower left") && (res.getX() <= cx) && (res
                            .getY() >= cy)))
                return res;
        } else {
            double discrsqr = Math.sqrt(discr);
            double u1 = (-b + discrsqr) / (2d * a); // first result
            double u2 = (-b - discrsqr) / (2d * a); // second result

            // there should be only one intersection point ...
            if ((0d <= u1) && (u1 <= 1d)) {
                Point2D res = calculatePointOnLine(u1, intLine.getP1(), intLine
                        .getP2());

                if (((pos == "upper left") && (res.getX() <= cx) && (res.getY() <= cy))
                        || ((pos == "upper right") && (res.getX() >= cx) && (res
                                .getY() <= cy))
                        || ((pos == "lower right") && (res.getX() >= cx) && (res
                                .getY() >= cy))
                        || ((pos == "lower left") && (res.getX() <= cx) && (res
                                .getY() >= cy)))
                    return res;
            }

            if ((0d <= u2) && (u2 <= 1d)) {
                Point2D res = calculatePointOnLine(u2, intLine.getP1(), intLine
                        .getP2());

                if (((pos == "upper left") && (res.getX() <= cx) && (res.getY() <= cy))
                        || ((pos == "upper right") && (res.getX() >= cx) && (res
                                .getY() <= cy))
                        || ((pos == "lower right") && (res.getX() >= cx) && (res
                                .getY() >= cy))
                        || ((pos == "lower left") && (res.getX() <= cx) && (res
                                .getY() >= cy)))
                    return res;
            }
        }

        return null;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
