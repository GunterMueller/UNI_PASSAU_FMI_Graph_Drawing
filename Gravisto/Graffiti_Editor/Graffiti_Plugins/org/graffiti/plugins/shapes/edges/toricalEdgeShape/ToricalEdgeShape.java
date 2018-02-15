// =============================================================================
//
//   StraightLineEdgeShape.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: StraightLineEdgeShape.java 1600 2006-11-26 19:30:42Z piorkows $

package org.graffiti.plugins.shapes.edges.toricalEdgeShape;

import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.plugin.view.NodeShape;
import org.graffiti.plugin.view.ShapeNotFoundException;
import org.graffiti.plugins.views.defaults.LineEdgeShape;

/**
 * Concrete class representing an edge as one straight line.
 * 
 * @version $Revision: 1600 $
 */
public class ToricalEdgeShape extends LineEdgeShape {

    double torusWidth = 50 * 5;

    double torusHeight = 50 * 3;

    public void buildShape(EdgeGraphicAttribute edgeAttr,
            NodeShape sourceShape, NodeShape targetShape, double torusWidth,
            double torusHeight) throws ShapeNotFoundException {
        this.torusWidth = torusWidth;
        this.torusHeight = torusHeight;
        buildShape(edgeAttr, sourceShape, targetShape);

    }

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

        // docking
        Point2D start = getSourceDockingCoords(edgeAttr, sourceShape);
        Point2D end = getTargetDockingCoords(edgeAttr, targetShape);

        double dx = 0;
        double dy = 0;

        try {
            dx = ((Integer) edgeAttr.getAttribute("windX").getValue())
                    * torusWidth;
        } catch (AttributeNotFoundException e) {
        }
        try {
            dy = ((Integer) edgeAttr.getAttribute("windY").getValue())
                    * torusHeight;
        } catch (AttributeNotFoundException e) {
        }

        start.setLocation(start.getX() - dx, start.getY() - dy);

        end = calculateActualEndPoint(edgeAttr, targetShape, end, start);

        start.setLocation(start.getX() + dx, start.getY() + dy);
        end.setLocation(end.getX() + dx, end.getY() + dy);

        start = calculateActualStartPoint(edgeAttr, sourceShape, start, end);

        this.line2D = new Line2D.Double(start, end);
        this.linePath = new GeneralPath(line2D);

        getThickBounds(this.linePath, edgeAttr);

        if (headArrow != null) {
            this.realBounds.add(headArrow.getBounds2D());
        }

        if (tailArrow != null) {
            this.realBounds.add(tailArrow.getBounds2D());
        }
    }

    /**
     * Decides whether or not a point lies within this shape.
     * 
     * @param x
     *            the x-coordinate of the point to check.
     * @param y
     *            the y-coordinate of the point to check.
     * 
     * @return true if the point lies within this shape.
     */
    @Override
    public boolean contains(double x, double y) {
        // TODO: check why this method is called to often
        return lineContains(this.line2D, x, y);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
