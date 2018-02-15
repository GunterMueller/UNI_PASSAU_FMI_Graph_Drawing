// =============================================================================
//
//   PolyLineEdgeShape.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: PolyLineEdgeShape.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.views.defaults;

import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.graffiti.attributes.Attribute;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.plugin.view.NodeShape;
import org.graffiti.plugin.view.ShapeNotFoundException;

/**
 * Represents an edge with several segments separated by bends.
 * 
 * @version $Revision: 5766 $
 */
public class PolyLineEdgeShape extends LineEdgeShape {
    /** the <code>Collection</code> of bends of this edge. */
    protected Collection<Attribute> bends;

    /**
     * Flatness value used for the <code>PathIterator</code> used to calculate
     * contains method for non-linear edge shapes like splines.
     */
    protected final double flatness = 1.0d;

    /**
     * This method sets all necessary properties of an edge using the values
     * contained within the <code>CollectionAttribute</code> (like coordinates
     * etc.). It also uses information about ports. It attaches arrows if there
     * are any.
     * 
     * @param edgeAttr
     *            the attribute that contains all necessary information to
     *            construct a line.
     * @param sourceShape
     *            DOCUMENT ME!
     * @param targetShape
     *            DOCUMENT ME!
     * 
     * @throws ShapeNotFoundException
     *             DOCUMENT ME!
     */
    @Override
    public void buildShape(EdgeGraphicAttribute edgeAttr,
            NodeShape sourceShape, NodeShape targetShape)
            throws ShapeNotFoundException {
        this.graphicsAttr = edgeAttr;
        Collection<Attribute> bends = edgeAttr.getBends().getCollection()
                .values();

        LinkedList<Point2D> bendsList = new LinkedList<Point2D>();
        for (Attribute a : bends) {
            bendsList.add(((CoordinateAttribute) a).getCoordinate());
        }
        bendsList.addFirst(getSourceDockingCoords(edgeAttr, sourceShape));
        bendsList.addLast(getTargetDockingCoords(edgeAttr, targetShape));
        trimToActualStartAndEndPoints(edgeAttr, sourceShape, targetShape,
                bendsList);

        this.linePath = new GeneralPath();
        for (Point2D p : bendsList) {
            if (p == bendsList.getFirst()) {
                this.linePath.moveTo((float) p.getX(), (float) p.getY());
            } else {
                this.linePath.lineTo((float) p.getX(), (float) p.getY());
            }
        }

        getThickBounds(this.linePath, edgeAttr);

        if (headArrow != null) {
            this.realBounds.add(headArrow.getBounds2D());
        }

        if (tailArrow != null) {
            this.realBounds.add(tailArrow.getBounds2D());
        }
    }

    /**
     * Returns true if the edge has been hit.
     * 
     * @param x
     *            x coordinate relative to the coordinates of this shape.
     * @param y
     *            y coordinate relative to the coordinates of this shape.
     * 
     * @return true if the edge has been hit else false.
     */
    @Override
    public boolean contains(double x, double y) {
        // could probably be optimized ...
        return pathContains(this.linePath, x + 1, y + 1);
    }

    /**
     * Calculates if a given <code>GeneralPath</code> object contains the given
     * point. It approximates the path using a
     * <code>FlatteningPathIterator</code> and uses the method
     * <code>lineContains</code> that uses a certain tolerance.
     * 
     * @param path
     * @param x
     * @param y
     * 
     * @return true is point is near to the <code>path</code> object
     */
    protected boolean pathContains(GeneralPath path, double x, double y) {
        // System.out.println("pc----------------------------");
        PathIterator pi = path.getPathIterator(null, 10d);
        if (pi.isDone())
            return false;
        double[] seg = new double[6];
        int type = pi.currentSegment(seg);

        Point2D veryfirst = new Point2D.Double(seg[0], seg[1]);

        Point2D start = veryfirst;
        Point2D end = null;

        // GeneralPath newGP = new GeneralPath(path);
        try {
            while (!pi.isDone()) {
                // System.out.println("type " + type + " ");
                switch (type) {
                case PathIterator.SEG_MOVETO:
                    start = new Point2D.Double(seg[0], seg[1]);

                    // newGP.append(new
                    // java.awt.geom.Ellipse2D.Double(seg[0]-3, seg[1]-3,
                    // 6d, 6d), false);
                    end = null;

                    break;

                case PathIterator.SEG_LINETO:
                    end = new Point2D.Double(seg[0], seg[1]);

                    break;

                case PathIterator.SEG_QUADTO:
                    end = new Point2D.Double(seg[2], seg[3]);

                    break;

                case PathIterator.SEG_CUBICTO:
                    end = new Point2D.Double(seg[4], seg[5]);

                    break;

                case PathIterator.SEG_CLOSE:
                    end = veryfirst;

                    break;
                }

                if (end != null) {
                    // not a moveto
                    // System.out.println("checking " + start + " to " + end + "
                    // for x="+x + " / y= "+y);
                    // newGP.append(new java.awt.geom.Line2D.Double(start, end),
                    // false);
                    // newGP.append(new
                    // java.awt.geom.Ellipse2D.Double(end.getX()-3,
                    // end.getY()-3, 6d, 6d), false);
                    if (lineContains(new Line2D.Double(start, end), x, y))
                        // this.linePath = newGP;
                        // System.out.println("hit ");
                        return true;

                    start = end;
                }

                pi.next();
                type = pi.currentSegment(seg);
            }
        } catch (NoSuchElementException nsee) {
        }

        // this.linePath = newGP;
        return false;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
