// =============================================================================
//
//   RectAngleStrategy.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.radialTreeDrawing;

import java.awt.geom.Point2D;

/**
 * This class implements a strategy for calculating geometric values for a
 * rectangle. Therefor the rectangle is transformated in the first quadrant
 * using either a point reflection or a rotation in respect of the zero point.
 * After calculating the center the polar angle is computed using the accordant
 * back transformation. The center is calculated by solving a equation system of
 * three conditions: - the left bottom corner is placed on the circle (x^2 + y^2
 * = radius^2) - a linear equation for the startAngle (y = tan(startAngle) * x)
 * - the width of the rectangle
 * 
 * @author Andreas Schindler
 * @version $Revision$ $Date$
 */
public class RectangleStrategy extends ShapeStrategy {

    /**
     * the center of the rectangle
     */
    private Point2D center;

    /**
     * the quadrant of the startangle. The following table explains the values
     * for (quadrant mod 4) 0 = right top 1 = left top 2 = left bottom 3 = right
     * bottom
     */
    private int quadrant;

    /*
     * @see
     * org.graffiti.plugins.algorithms.radialDrawing.ShapeStrategy#handleShape()
     */
    @Override
    public void handleShape() {

        // distinguish between the quadrant of the start angle and
        // calculate the center of the rectangle
        quadrant = Math.round((float) Math.floor(startAngle / (Math.PI / 2)));
        if (quadrant % 2 == 0) {

            // right top or left bottom quadrant
            computeCenter(startAngle - quadrant * Math.PI / 2, w, h);
        } else {

            // left top or right bottom quadrant
            computeCenter(startAngle - quadrant * Math.PI / 2, h, w);
        }

        // Distiguish the quadrants for calculating the polarAngle and avoid
        // division by 0
        if (center.getX() < 0.0) {

            // center is in the left top quadrant
            polarAngle = Math.PI - Math.atan(center.getY() / -center.getX());
        } else if (0.0 <= center.getX() && center.getX() < Constants.EPSILON) {

            // center is near by the y axis
            polarAngle = Math.PI / 2;
        } else {

            // center is in the first quadrant
            polarAngle = Math.atan(center.getY() / center.getX());
        }
        polarAngle += quadrant * Math.PI / 2;
        polarRadius = center.distance(0.0, 0.0);
        apexAngle = apexAngle();
        borderingRadius = borderingRadius();
    }

    /**
     * Calculate the cartesian coordinates of the rectangles center by solving
     * the equation system mentioned above in the class description.
     * 
     * @param startAngle
     *            startAngle
     * @param width
     *            the width of the rectangle
     * @param height
     *            the height of the rectangle
     */
    private void computeCenter(double startAngle, double width, double height) {

        center = new Point2D.Double();
        double a = Math.tan(startAngle);

        double xLeft;
        double xRight;
        double yBottom;
        double yTop;

        if (startAngle < Constants.EPSILON) {

            // place the rectangle horizontally tangent to the circle
            xLeft = radius;
            xRight = xLeft + width;
            yBottom = 0.0;
        } else {

            double criticalWidth = radius * Math.tan(Math.PI / 2 - startAngle);
            if (width < criticalWidth) {

                // the rectangle lies entirely in the first quadrant
                double discriminant = 4 * a * a * a * a * width * width - 4
                        * (a * a + 1)
                        * (a * a * width * width - radius * radius);
                xLeft = -2 * a * a * width;
                xLeft += Math.sqrt(discriminant);
                xLeft /= 2 * (a * a + 1);
                xRight = xLeft + width;
                yBottom = a * xRight;
            } else {

                // place the rectangle vertically tangent to the circle
                yBottom = radius;
                xRight = yBottom / a;
                xLeft = xRight - width;
            }
        }

        yTop = yBottom + height;
        center.setLocation((xLeft + xRight) / 2, (yBottom + yTop) / 2);
    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.radialDrawing.ShapeStrategy#apexAngle()
     */
    @Override
    protected double apexAngle() {

        // the coordinates of the left top corner of the rectangle after
        // transformation in the right top quadrant
        double x, y;

        if (quadrant % 2 == 0) {

            // quadrant is right top or left bottom
            x = center.getX() - w / 2;
            y = center.getY() + h / 2;
        } else {

            // quadrant is left top or right bottom
            x = center.getX() - h / 2;
            y = center.getY() + w / 2;
        }

        double endAngle;
        if (x < 0.0) {

            // take the left bottom corner instead of the left top corner
            // because they are placed in the left top quadrant
            if (quadrant % 2 == 0) {

                // quadrant is right top or left bottom
                y = center.getY() - h / 2;
            } else {

                // quadrant is left top or right bottom
                y = center.getY() - w / 2;
            }
            endAngle = Math.PI - Math.atan(y / -x);
        } else if (0.0 <= x && x < Constants.EPSILON) {

            endAngle = Math.PI / 2;
        } else {

            endAngle = Math.atan(y / x);
        }
        double apexAngle = endAngle + quadrant * Math.PI / 2 - startAngle;
        return apexAngle;
    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.radialDrawing.ShapeStrategy#borderingRadius
     * ()
     */
    @Override
    protected double borderingRadius() {

        if (radius < Constants.EPSILON)
            // node = root
            return Math.sqrt(w * w + h * h) / 2;
        else {

            // node != root
            Point2D leftTop = new Point2D.Double();
            Point2D rightTop = new Point2D.Double();

            if (quadrant % 2 == 0) {

                // quadrant is right top or left bottom
                leftTop.setLocation(center.getX() - w / 2, center.getY() + h
                        / 2);
                rightTop.setLocation(center.getX() + w / 2, center.getY() + h
                        / 2);
            } else {

                // quadrant is left top or right bottom
                leftTop.setLocation(center.getX() - h / 2, center.getY() + w
                        / 2);
                rightTop.setLocation(center.getX() + h / 2, center.getY() + w
                        / 2);
            }
            double borderingRadius = Math.max(leftTop.distance(0.0, 0.0),
                    rightTop.distance(0.0, 0.0));
            return borderingRadius;
        }
    }

}
