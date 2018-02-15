// =============================================================================
//
//   EllipseNodeShape.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: EllipseNodeShape.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.views.defaults;

import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Concrete class representing an ellipse.
 */
public class EllipseNodeShape extends CircularNodeShape {

    /**
     * Calculates the intersection between this ellipse and a line.
     * 
     * @param line
     * 
     * @return DOCUMENT ME!
     * 
     * @throws RuntimeException
     *             DOCUMENT ME!
     */
    @Override
    public Point2D getIntersection(Line2D line) {
        Rectangle2D realRect = getRealBounds2D();
        Point2D topleft = new Point2D.Double(realRect.getX(), realRect.getY());
        Point2D size = new Point2D.Double(realRect.getWidth(), realRect
                .getHeight());

        // transform ellipse to circle
        AffineTransform at = new AffineTransform();
        at.setToScale(1d, realRect.getWidth() / realRect.getHeight());

        Point2D transTopleft = at.transform(topleft, null);
        Point2D transSize = at.transform(size, null);

        // set size X and Y both to X to assure that both are the same
        Ellipse2D circ2D = new Ellipse2D.Double(transTopleft.getX(),
                transTopleft.getY(), transSize.getX(), transSize.getX());

        // do the same transform with line
        Line2D transLine = new Line2D.Double(at.transform(line.getP1(), null),
                at.transform(line.getP2(), null));

        // Shape lineShape = at.createTransformedShape(line);
        // Rectangle2D lineBounds = lineShape.getBounds2D();
        // Line2D transLine = new Line2D.Double
        // (lineBounds.getMinX(), lineBounds.getMinY(),
        // lineBounds.getMaxX(), lineBounds.getMaxY());
        Point2D point = getIntersectionWithCircle(circ2D, transLine);

        if (point != null) {
            try {
                return at.inverseTransform(point, null);
            } catch (java.awt.geom.NoninvertibleTransformException nite) {
                throw new RuntimeException(
                        "Could not invert transformation from ellipse to circle. "
                                + "Very strange! " + nite);
            }
        }

        return null;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
