// =============================================================================
//
//   AbstractArrowShape.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractArrowShape.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.views.defaults;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.graffiti.plugin.view.ArrowShape;

/**
 * DOCUMENT ME!
 * 
 * @author $Author: gleissner $
 * @version $Revision: 5766 $ $Date: 2006-01-04 10:21:57 +0100 (Mi, 04 Jan 2006)
 *          $
 */
public abstract class AbstractArrowShape implements ArrowShape {

    /**
     * The shape represented by this <code>ArrowShape</code> The arrow should
     * (before rotation) point horizontally from left to right. The size of the
     * arrow is therefore its height.
     */
    protected Shape arrowShape = this;

    /**
     * If set to <code>true</code>, the arrow is enlarged proportionally to the
     * width of the edge (as soon as the width is larger than the size of the
     * arrow). If <code>false</code>, it is (only) guaranteed, that the arrow is
     * larger than the edge but is only enlarged the moment the edge gets
     * broader than the arrow. In this case, <code>SCALE_FACTOR</code> is used.
     */
    protected final boolean DOSCALE = false;

    /**
     * If line is larger than arrow, then arrow is scaled to be
     * <code>factor</code> times larger than the line.
     */
    protected final float SCALE_FACTOR = 1.4f;

    /** The size of the arrow; */
    protected final float SIZE = 10.0f;

    /** The point of the arrow where it is attached to the edge. */
    protected Point2D anchor = new Point2D.Double(0, SIZE / 2d);

    /**
     * The head of the arrow, i.e. the point that will be attached to the node.
     */
    protected Point2D head = new Point2D.Double(SIZE, SIZE / 2d);

    /**
     * The size of the arrow. Normally defined as the breadth of the arrow when
     * looking from anchor to head.
     */
    protected double arrowWidth = 10.0d;

    /** The width of the line to which the arrow will be attached. */
    protected double lineWidth = 1d;

    /**
     * Returns the anchor of the arrow, i.e. the point where the line should be
     * attached to the arrow. This is only valid after a call to
     * <code>affix</code>.
     * 
     * @return the anchor of the arrow.
     */
    public Point2D getAnchor() {
        return this.anchor;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public Rectangle getBounds() {
        return this.arrowShape.getBounds();
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public Rectangle2D getBounds2D() {
        return this.arrowShape.getBounds2D();
    }

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
    public PathIterator getPathIterator(AffineTransform t, double d) {
        return this.arrowShape.getPathIterator(t, d);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param t
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public PathIterator getPathIterator(AffineTransform t) {
        return this.arrowShape.getPathIterator(t);
    }

    /**
     * Sets this arrow to the target point and rotates it according to the line
     * given by the conenction between points target and other. Ensures that the
     * arrow is always larger than the line.
     * 
     * @param target
     *            the point where the arrow should be put.
     * @param other
     *            needed to calculate the direction in which the arrow should
     *            point to. The line is given by the two points target and
     *            other.
     * @param lineWidth
     *            The total width of the line. May be used to scale the arrow so
     *            as to be larger than the line.
     * 
     * @return the point where the line meets the arrow
     */
    public Shape affix(Point2D target, Point2D other, double lineWidth) {
        this.lineWidth = lineWidth;

        // adjust the size so that the arrow is always larger than the line
        if (this.arrowWidth < lineWidth) {
            AffineTransform at = new AffineTransform();
            double factor = (lineWidth * SCALE_FACTOR) / arrowWidth;
            at.scale(factor, factor);
            this.arrowShape = at.createTransformedShape(this.arrowShape);
            at.transform(this.head, this.head);
            at.transform(this.anchor, this.anchor);

            if (!DOSCALE) {
                this.arrowWidth = lineWidth * SCALE_FACTOR;
            }
        }

        // rotate arrow to be parallel to line from other to target
        double w = target.getX() - other.getX();
        double h = target.getY() - other.getY();
        double diag = Math.sqrt((w * w) + (h * h));
        double cosa = w / diag;
        double sina = h / diag;

        // next lines move head to origin and rotate there
        AffineTransform at = new AffineTransform(cosa, sina, -sina, cosa, 0, 0);
        at.translate(-this.head.getX(), -this.head.getY());
        this.arrowShape = at.createTransformedShape(this.arrowShape);
        at.transform(this.anchor, this.anchor);

        // move head of arrow to meet target
        at.setToTranslation(target.getX(), target.getY());
        this.arrowShape = at.createTransformedShape(this.arrowShape);
        at.transform(this.anchor, this.anchor);

        return this.arrowShape;
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
        return this.arrowShape.contains(x, y, w, h);
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
        return this.arrowShape.contains(x, y);
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
        return this.arrowShape.contains(p);
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
        return this.arrowShape.contains(r);
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
        return this.arrowShape.intersects(x, y, w, h);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param r
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public boolean intersects(Rectangle2D r) {
        return this.arrowShape.intersects(r);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
