// =============================================================================
//
//   PolygonalNodeShape.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: PolygonalNodeShape.java 1296 2006-06-17 14:14:46Z piorkows $

package org.graffiti.plugins.shapes.nodes;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugin.view.ShapeNotFoundException;
import org.graffiti.plugins.views.defaults.RectangularNodeShape;
import org.graffiti.util.GraphicHelper;

/**
 * DOCUMENT ME!
 */
public abstract class ArbitraryNodeShape extends RectangularNodeShape {

    /** DOCUMENT ME! */
    protected GeneralPath shape;

    protected GeneralPath boundaryShape;

    protected Shape master;

    protected Shape boundaryMaster;

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public Rectangle getBounds() {
        return this.thickShape.getBounds();
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public Rectangle2D getBounds2D() {
        return this.thickShape.getBounds2D();
    }

    /**
     * @see org.graffiti.plugin.view.NodeShape#getIntersection(Line2D)
     */
    @Override
    public Point2D getIntersection(Line2D line) {
        Rectangle2D bounds = getRealBounds2D();
        AffineTransform at = new AffineTransform(1, 0, 0, 1, bounds.getX(),
                bounds.getY());

        Line2D polyLine;
        List<Point2D> intPoints = new LinkedList<Point2D>();

        PathIterator pi = boundaryShape.getPathIterator(at, 5);
        double first[] = new double[6];
        double current[] = new double[6];
        double next[] = new double[6];

        pi.currentSegment(first);
        pi.next();
        current[0] = first[0];
        current[1] = first[1];
        while (!pi.isDone()) {
            int status = pi.currentSegment(next);
            pi.next();
            if (status != PathIterator.SEG_CLOSE) {
                polyLine = new Line2D.Double(current[0], current[1], next[0],
                        next[1]);
            } else {
                polyLine = new Line2D.Double(current[0], current[1], first[0],
                        first[1]);
            }
            if (polyLine.intersectsLine(line)) {
                intPoints.add(GraphicHelper.getIntersection(polyLine, line));
            }
            current[0] = next[0];
            current[1] = next[1];
        }

        if (intPoints.isEmpty())
            return null;

        // per convention, the second point is the outer one:
        Point2D outer = line.getP2();
        double minDist = Double.MAX_VALUE;
        Point2D minIntPoint = null;
        double dist;

        for (Point2D pnt : intPoints) {
            dist = pnt.distance(outer);

            if (dist < minDist) {
                minDist = dist;
                minIntPoint = pnt;
            }
        }

        return minIntPoint;

    }

    /**
     * @see java.awt.Shape#getPathIterator(java.awt.geom.AffineTransform)
     */
    @Override
    public PathIterator getPathIterator(AffineTransform at) {
        return shape.getPathIterator(at);
    }

    /**
     * @see java.awt.Shape#getPathIterator(java.awt.geom.AffineTransform,
     *      double)
     */
    @Override
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return shape.getPathIterator(at, flatness);
    }

    /**
     * @see org.graffiti.plugin.view.NodeShape#buildShape(NodeGraphicAttribute)
     */
    public void buildShape(NodeGraphicAttribute graphics)
            throws ShapeNotFoundException {

        this.nodeAttr = graphics;

        DimensionAttribute dim = graphics.getDimension();
        double w = dim.getWidth();
        double h = dim.getHeight();

        double ft = Math.min(nodeAttr.getFrameThickness(), Math.min(w / 2d,
                h / 2d));

        if (master == null) {
            master = getShape();
        }
        if (boundaryMaster == null) {
            boundaryMaster = getBoundary();
        }

        shape = scaleMaster(master, w, h, ft);
        boundaryShape = scaleMaster(boundaryMaster, w, h, ft);

        setThickShape(w, h);
        return;
    }

    private GeneralPath scaleMaster(Shape m, double w, double h, double ft) {
        GeneralPath shape = new GeneralPath();
        PathIterator pi = m.getPathIterator(null);
        float coords[] = new float[6];
        while (!pi.isDone()) {
            int type = pi.currentSegment(coords);
            coords[0] = calcXCoordinate(coords[0], w, ft);
            coords[1] = calcYCoordinate(coords[1], h, ft);
            coords[2] = calcXCoordinate(coords[2], w, ft);
            coords[3] = calcYCoordinate(coords[3], h, ft);
            coords[4] = calcXCoordinate(coords[4], w, ft);
            coords[5] = calcYCoordinate(coords[5], h, ft);
            switch (type) {
            case PathIterator.SEG_MOVETO:
                shape.moveTo(coords[0], coords[1]);
                break;
            case PathIterator.SEG_LINETO:
                shape.lineTo(coords[0], coords[1]);
                break;
            case PathIterator.SEG_QUADTO:
                shape.quadTo(coords[0], coords[1], coords[2], coords[3]);
                break;
            case PathIterator.SEG_CUBICTO:
                shape.curveTo(coords[0], coords[1], coords[2], coords[3],
                        coords[4], coords[5]);
                break;
            case PathIterator.SEG_CLOSE:
                shape.closePath();
                break;
            }
            pi.next();
        }
        return shape;
    }

    protected float calcXCoordinate(double x, double w, double ft) {
        return (float) (x * (w - ft) + w) / 2;
    }

    protected float calcYCoordinate(double y, double h, double ft) {
        return (float) (y * (h - ft) + h) / 2;
    }

    /**
     * @see java.awt.Shape#contains(double, double)
     */
    @Override
    public boolean contains(double x, double y) {
        return boundaryShape.contains(x, y);
    }

    /**
     * @see java.awt.Shape#contains(double, double, double, double)
     */
    @Override
    public boolean contains(double x, double y, double w, double h) {
        return boundaryShape.contains(x, y, w, h);
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
    @Override
    public boolean intersects(double x, double y, double w, double h) {
        return boundaryShape.intersects(x, y, w, h);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param r
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public boolean intersects(Rectangle2D r) {
        return boundaryShape.intersects(r);
    }

    protected abstract GeneralPath getShape();

    protected GeneralPath getBoundary() {
        return getShape();
    }

    /**
     * DOCUMENT ME!
     * 
     * @param w
     *            DOCUMENT ME!
     * @param h
     *            DOCUMENT ME!
     */
    protected void setThickShape(double w, double h) {
        this.thickShape = new Rectangle2D.Double(0, 0, w, h);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
