// =============================================================================
//
//   RectangleNodeShape.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: RectangleNodeShape.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.views.defaults;

import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.util.GraphicHelper;

/**
 * Class representing a rectangle.
 * 
 * @version $Revision: 5766 $
 */
public class RectangleNodeShape extends RectangularNodeShape {

    /**
     * The <code>Rectangle2D</code> that is represented by this
     * <code>NodeShape</code>.
     */
    private Rectangle2D rect2D;

    /**
     * The constructor creates a rectangle using default values.
     */
    public RectangleNodeShape() {
        this.rect2D = new Rectangle2D.Double(0, 0, DEFAULT_WIDTH,
                DEFAULT_HEIGHT);
        this.thickShape = new Rectangle2D.Double();
        ((RectangularShape) this.thickShape).setFrame(rect2D);
    }

    /**
     * @see org.graffiti.plugin.view.NodeShape#getIntersection(Line2D)
     */
    @Override
    public Point2D getIntersection(Line2D line) {
        Rectangle2D rect = getRealBounds2D();

        double width = rect.getWidth();
        double height = rect.getHeight();

        // the four corners of the encapsulated rectangle
        Point2D upperLeft = new Point2D.Double(rect.getX(), rect.getY());
        Point2D lowerLeft = new Point2D.Double(rect.getX(), upperLeft.getY()
                + height);
        Point2D upperRight = new Point2D.Double(upperLeft.getX() + width, rect
                .getY());
        Point2D lowerRight = new Point2D.Double(upperLeft.getX() + width,
                upperLeft.getY() + height);

        // turn the rectangle into 4 lines
        // and test which one intersects with the given line
        Line2D left = new Line2D.Double(upperLeft, lowerLeft);
        Line2D bottom = new Line2D.Double(lowerLeft, lowerRight);
        Line2D right = new Line2D.Double(upperRight, lowerRight);
        Line2D top = new Line2D.Double(upperLeft, upperRight);

        // testing with which side of the rectangle the given line intersects
        // and then computing the intersection point
        if (left.intersectsLine(line) && (left.getX1() != line.getX1()))
            return GraphicHelper.getIntersection(left, line);
        else if (bottom.intersectsLine(line)
                && (bottom.getY1() != line.getY1()))
            return GraphicHelper.getIntersection(bottom, line);
        else if (right.intersectsLine(line) && (right.getX1() != line.getX1()))
            return GraphicHelper.getIntersection(right, line);
        else if (top.intersectsLine(line) && (top.getY1() != line.getY1()))
            return GraphicHelper.getIntersection(top, line);
        else
            return null;
    }

    // implemented in RectangularNodeShape:
    // public boolean contains(Point2D p) {
    // return this.rect2D.contains(p);
    // }
    // implemented in RectangularNodeShape:
    // public boolean contains(Rectangle2D r) {
    // return this.rect2D.contains(r);
    // }

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
        return this.rect2D.getPathIterator(t, d);
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
        return this.rect2D.getPathIterator(t);
    }

    /**
     * This method sets all necessary properties using the values contained
     * within the <code>CollectionAttribute</code>. This includes
     * 
     * @param graphics
     *            The attribute that contains all necessary information to
     *            construct a rectangle.
     */
    public void buildShape(NodeGraphicAttribute graphics) {
        this.nodeAttr = graphics;

        DimensionAttribute dim = graphics.getDimension();
        double w = dim.getWidth();
        double h = dim.getHeight();

        this.rect2D.setRect(0, 0, w - 0.25, h - 0.25);

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
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
