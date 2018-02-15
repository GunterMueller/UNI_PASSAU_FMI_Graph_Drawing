// =============================================================================
//
//   LineEquation.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: LineEquation.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.springembedderFR;

/**
 * This class, is a line equation in R^2, like in analytical geometrie.
 * 
 * @author matzeder
 * @version $Revision: 5766 $ $Date: 2006-05-01 12:12:57 +0200 (Mo, 01 Mai 2006)
 *          $
 */
public class LineEquation {

    /**
     * Position Vector of this straight line (means in german: Ortsvektor)
     */
    private GeometricalVector positionVector;

    /**
     * Gradient Vector of the straigt line (means in german: Richtungsvektor)
     */
    private GeometricalVector gradientVector;

    /**
     * Creates a new Object with positionVector point1 and gradientVector
     * point2.
     */
    public LineEquation(GeometricalVector point1, GeometricalVector point2) {
        this.positionVector = point1;
        this.gradientVector = point2;
    }

    /**
     * Returns the GeometricalVector of the intersection point of the two given
     * lines.
     * 
     * @param line1
     *            Line 1 to check the intersection
     * @param line2
     *            Line 2 to check the intersection
     * @return The GeometricalVector of the intersection point of the two given
     *         lines
     */
    public static GeometricalVector getIntersectionPoint(LineEquation line1,
            LineEquation line2) {

        // Form of
        // line: ( 4 ) + x * ( 1 )
        // ______( 1 )______ ( -2 )
        // and
        // orthLine: ( 1 ) + y * ( 4 )
        // __________( 2 )______ ( 2 )

        // line = orthLine
        // ( 4 ) + x * ( 1 ) = ( 1 ) + y * ( 4 )
        // ( 1 ) _____( -2 ) = ( 2 ) ______( 2 )

        // 4 + 1x ______= ___1 + 4y (1st equation)
        // 1 + (-2)x ___= ___2 * 2y (2nd equation)
        // ==>
        // 1x + (-4)y = 1 - 4 (1st)
        // a____ b_______ e
        // (-2)x + (-2)y = 2 - 1 (2nd)
        // _c______ d_______ f

        // a x + b y = e
        // c x + d y = f
        // ==>
        // s = (e*d-b*f)/(a*d-b*c)
        // t = (a*f-e*c)/(a*d-b*c)

        // then line1 and line2 are parallel or identical
        if (line1.getGradientVector().getX() / line1.getGradientVector().getY() == line2
                .getGradientVector().getX()
                / line2.getGradientVector().getY())
            return null;

        double a = line1.getGradientVector().getX();
        double b = -line2.getGradientVector().getX();
        double c = line1.getGradientVector().getY();
        double d = -line2.getGradientVector().getY();
        double e = line2.getPositionVector().getX()
                - line1.getPositionVector().getX();
        double f = line2.getPositionVector().getY()
                - line1.getPositionVector().getY();

        double s = (e * d - b * f) / (a * d - b * c);
        // double t = (a * f - e * c) / (a * d - b * c);

        double x = line1.getPositionVector().getX() + s
                * line1.getGradientVector().getX();

        double y = line1.getPositionVector().getY() + s
                * line1.getGradientVector().getY();

        return new GeometricalVector(x, y);

    }

    /**
     * Returns the gradientVector.
     * 
     * @return the gradientVector.
     */
    public GeometricalVector getGradientVector() {
        return gradientVector;
    }

    /**
     * Returns the positionVector.
     * 
     * @return the positionVector.
     */
    public GeometricalVector getPositionVector() {
        return positionVector;
    }

    /**
     * Returns the orthogonal vector of this LineEquation.
     * 
     * @return The orthogonal vector of this LineEquation.
     */
    public GeometricalVector getOrthogonalVector() {

        // choose x=1, because only the direction is important
        double x = 1;
        double y;

        if (this.gradientVector.getY() == 0.0d) {
            x = 0;
            y = 1;
        }
        // in this case, y!=0, therefore no division/0 is possible
        else {
            y = -(this.getGradientVector().getX() * x)
                    / this.getGradientVector().getY();
        }
        return new GeometricalVector(x, y);

    }

    /**
     * Returns true, if the given vector point is element of this line.
     * 
     * @param vector
     *            The given GeometricalVector, which represents the point to
     *            check.
     * @return True, if the given vector point is element of this line.
     */
    public boolean isElementOf(GeometricalVector vector) {

        double a = this.getPositionVector().getX();
        double b = this.getGradientVector().getX();
        double e = vector.getX();

        double c = this.getPositionVector().getY();
        double d = this.getGradientVector().getY();
        double f = vector.getY();

        // gradient vector has no direction
        if (b == 0.0d && d == 0.0d) {
            // not possible, because this is not a straight line
            // if self-loop edges are not eliminated, then the error is
            // thinkable
            System.err.println(this + " is not line");

        } else if (b == 0.0d) {
            // b == 0.0 and d != 0.0
            if (a == e)
                return true;
            else
                return false;
        } else if (d == 0.0d) {
            // d == 0.0 and b != 0.0
            if (c == f)
                return true;
            else
                return false;
        } else {
            // d != 0.0 and b != 0.0
            double s1 = (e - a) / b;
            double s2 = (f - c) / d;

            if (s1 == s2)
                return true;
            else
                return false;
        }
        return false;
    }

    /**
     * Output of the line equation.
     */
    @Override
    public String toString() {
        return this.getPositionVector() + " + t * " + this.getGradientVector();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
