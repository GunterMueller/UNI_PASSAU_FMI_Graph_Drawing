// =============================================================================
//
//   GraphicHelper.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphicHelper.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.util;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * 
 */
public class GraphicHelper {

    /**
     * Returns the point where both lines intersect.
     * 
     * @param line1
     *            line to compute the intersection with l2
     * @param line2
     *            line to compute the intersection with l1
     * 
     * @return the intersection point of the given lines.
     */
    public static Point2D getIntersection(Line2D line1, Line2D line2) {
        // that would not be a line but a point
        // and this would crash the computing (division by zero)
        assert (!line1.getP1().equals(line1.getP2()));
        assert (!line2.getP1().equals(line2.getP2()));

        // the factor with which to multiply the direction in order
        // to get the intersection point:
        // intersectionPoint = line1.P1 + factor* line1Direction
        double factor;

        // the starting points of the two given lines
        Point2D p1 = line1.getP1();
        Point2D p2 = line2.getP1();

        // computing the direction of line1
        double l1Direction_x = line1.getP2().getX() - line1.getP1().getX();
        double l1Direction_y = line1.getP2().getY() - line1.getP1().getY();

        // computing the direction of line2
        double l2Direction_x = line2.getP2().getX() - line2.getP1().getX();
        double l2Direction_y = line2.getP2().getY() - line2.getP1().getY();

        if (l2Direction_x != 0.0) {
            factor = p2.getY()
                    - p1.getY()
                    + ((l2Direction_y / l2Direction_x) * (p1.getX() - p2.getX()));

            double div = (l1Direction_y - ((l2Direction_y / l2Direction_x) * l1Direction_x));

            if (div != 0.0) {
                factor /= div;
            } else {
                factor = 0.0;
            }
        } else { // then l2Direction_y has to be != 0.0
            factor = p2.getX()
                    - p1.getX()
                    + ((l2Direction_x / l2Direction_y) * (p1.getY() - p2.getY()));

            double div = (l1Direction_x - ((l2Direction_x / l2Direction_y) * l1Direction_y));

            if (div != 0.0) {
                factor /= div;
            } else {
                factor = 0.0;
            }
        }

        // constructing the intersection point:
        // startpoint_line1 + factor * direction_line1
        Point2D intersectionPoint = new Point2D.Double(p1.getX()
                + (factor * l1Direction_x), p1.getY()
                + (factor * l1Direction_y));

        return intersectionPoint;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
