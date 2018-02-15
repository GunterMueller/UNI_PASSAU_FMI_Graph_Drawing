// =============================================================================
//
//   CircleNodeShape.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: CircleNodeShape.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.views.defaults;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;

/**
 * Class representing a circle.
 */
public class CircleNodeShape extends CircularNodeShape {

    /**
     * The constructor creates a circle using default values.
     */
    public CircleNodeShape() {
        super();
        this.ell2D = new Ellipse2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_WIDTH);
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
    public Point2D getIntersection(Line2D line) {
        Rectangle2D rect = getRealBounds2D();
        Ellipse2D realEll2D = new Ellipse2D.Double(rect.getX(), rect.getY(),
                rect.getWidth(), rect.getHeight());

        return getIntersectionWithCircle(realEll2D, line);
    }

    /**
     * This method sets all necessary properties using the values contained
     * within the <code>CollectionAttribute</code> (like size etc.).
     * 
     * @param nodeAttr
     *            The attribute that contains all necessary information to
     *            construct an circle.
     */
    @Override
    public void buildShape(NodeGraphicAttribute nodeAttr) {
        this.nodeAttr = nodeAttr;

        DimensionAttribute dim = nodeAttr.getDimension();
        double r = Math.round(Math.min(dim.getWidth(), dim.getHeight()));
        double ft = Math.round(Math.min(nodeAttr.getFrameThickness(), r / 2d));

        double offset = Math.round(ft / 2d);

        this.ell2D.setFrame(offset, offset, r - 2 * offset - 1d, r - 2 * offset
                - 1d);

        ((RectangularShape) this.thickShape).setFrame(0, 0, r, r);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
