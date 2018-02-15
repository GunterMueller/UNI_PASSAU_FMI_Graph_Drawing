package org.graffiti.plugins.algorithms.core;

import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.plugin.view.NodeShape;
import org.graffiti.plugin.view.ShapeNotFoundException;
import org.graffiti.plugins.views.defaults.LineEdgeShape;

/**
 * represents an intra-level edge as a circle segment
 */
public class IntraLevelShape extends LineEdgeShape {

    /**
     * defines the distance of the edge to the outer radius
     */
    private static final int ALPHA = 10;

    /**
     * minimum node distance
     */
    private static final int MIN_NODE_DIST = 80;

    /**
     * default constructor
     */
    public IntraLevelShape() {
        super();
    }

    /**
     * This method is called when one of the incident nodes of the edge has
     * changed. It also uses information about ports and coordinates. It
     * attaches arrows if there are any.
     * 
     * @param edgeAttr
     *            the attribute that contains all necessary information to
     *            construct a spiral
     * @param sourceShape
     *            the <code>NodeShape</code> of the source node
     * @param targetShape
     *            the <code>NodeShape</code> of the target node
     */
    @Override
    public void buildShape(EdgeGraphicAttribute edgeAttr,
            NodeShape sourceShape, NodeShape targetShape)
            throws ShapeNotFoundException {

        this.graphicsAttr = edgeAttr;

        // get the center of the drawing
        Params p = new Params();
        Point2D center = p.getCenter();

        // docking
        Point2D start = getSourceDockingCoords(edgeAttr, sourceShape);
        Point2D end = getTargetDockingCoords(edgeAttr, targetShape);

        // set new start and end docking coordinates to the inner side of the
        // circle
        this.line2D.setLine(start, end);// center
        Point2D newStart = sourceShape.getIntersection(this.line2D);
        this.line2D.setLine(end, start);
        Point2D newEnd = targetShape.getIntersection(this.line2D);

        // if no intersection was found, just draw from/to docking
        if (newStart != null) {
            start = newStart;
        }
        if (newEnd != null) {
            end = newEnd;
        }

        // attach arrows
        start = attachSourceArrow(edgeAttr, start, end);
        end = attachTargetArrow(edgeAttr, end, start);

        // compute radius and start/end angle of the edge
        double startRadius = start.distance(center);
        double endRadius = end.distance(center);
        double startAngle, endAngle;
        if (start.getY() - center.getY() < 0) {
            startAngle = Math
                    .acos((start.getX() - center.getX()) / startRadius);
        } else {
            startAngle = Math.acos(-(start.getX() - center.getX())
                    / startRadius)
                    + Math.PI;
        }
        if (end.getY() - center.getY() < 0) {
            endAngle = Math.acos((end.getX() - center.getX()) / endRadius);
        } else {
            endAngle = Math.acos(-(end.getX() - center.getX()) / endRadius)
                    + Math.PI;
        }

        // get the angle in the middle of start and end angle, to compute a help
        // point (mid) on the circle
        double midAngle;
        if (Math.abs(startAngle - endAngle) <= Math.PI) {
            midAngle = (startAngle + endAngle) / 2;
        } else {
            midAngle = (startAngle + endAngle) / 2 - Math.PI;
        }

        // get the distance between two levels
        Double levelDist = p.getLevelDist();

        // compute the point where the edge should be drawn (edgepoint) - this
        // point lies in the section leveldistance/alpha near the outer radius
        // depending on the angle difference - it lies on the inner or the outer
        // side of this section
        Double distCenterEdge = center.distance(start)
                - (levelDist * MIN_NODE_DIST / ALPHA * Math
                        .abs((startAngle - endAngle) / Math.PI));
        Point2D edgePoint = new Point2D.Double(center.getX() + distCenterEdge
                * Math.cos(midAngle), center.getY() - distCenterEdge
                * Math.sin(midAngle));

        // compute the center point of the circle defined by the three points
        // start, end and edgepoint
        Point2D cc = computeCenter(start, edgePoint, end);

        // center is defined - means the points are not on one line -
        // approximate a circle segment by a polyline using quad curves
        if (cc != null) {

            // get the radius
            double r = cc.distance(edgePoint);

            // compute start and end-angle
            if (start.getY() - cc.getY() < 0) {
                startAngle = Math.acos((start.getX() - cc.getX()) / r);
            } else {
                startAngle = Math.acos(-(start.getX() - cc.getX()) / r)
                        + Math.PI;
            }
            if (end.getY() - cc.getY() < 0) {
                endAngle = Math.acos((end.getX() - cc.getX()) / r);
            } else {
                endAngle = Math.acos(-(end.getX() - cc.getX()) / r) + Math.PI;
            }

            // draw from (firstAngle) smaller to (lastAngle) bigger angle - the
            // angles have to be adapted because they can cross the ray (0�)
            Point2D first, last;
            Double firstAngle, lastAngle;
            if (startAngle < endAngle) {
                if (Math.abs(startAngle - endAngle) < Math.PI) {
                    first = start;
                    firstAngle = startAngle;
                    last = end;
                    lastAngle = endAngle;
                } else {
                    first = end;
                    firstAngle = endAngle;
                    last = start;
                    lastAngle = startAngle + 2 * Math.PI;
                }
            } else {
                if (Math.abs(startAngle - endAngle) < Math.PI) {
                    first = end;
                    firstAngle = endAngle;
                    last = start;
                    lastAngle = startAngle;
                } else {
                    first = start;
                    firstAngle = startAngle;
                    last = end;
                    lastAngle = endAngle + 2 * Math.PI;
                }
            }

            // angle difference smaller than 10� - just one quad curve..
            if (Math.abs(startAngle - endAngle) / Math.PI < Math.PI / 18) {
                this.linePath
                        .moveTo((float) start.getX(), (float) start.getY());
                this.linePath.quadTo((float) edgePoint.getX(),
                        (float) edgePoint.getY(), (float) end.getX(),
                        (float) end.getY());
            } else {

                // ..else approximate the circle segment drawing a quad curve
                // each 10�
                this.linePath
                        .moveTo((float) first.getX(), (float) first.getY());

                for (double i = firstAngle + Math.PI / 18; i < lastAngle
                        - Math.PI / 18; i += Math.PI / 18) {
                    Point2D help = new Point2D.Double(cc.getX() + r
                            * Math.cos(i), cc.getY() - r * Math.sin(i));
                    Point2D help2 = new Point2D.Double(cc.getX() + r
                            * Math.cos(i + Math.PI / 36), cc.getY() - r
                            * Math.sin(i + Math.PI / 36));
                    this.linePath
                            .quadTo((float) help.getX(), (float) help.getY(),
                                    (float) help2.getX(), (float) help2.getY());
                }

                this.linePath.lineTo((float) last.getX(), (float) last.getY());
            }

            // points on one line - just one quad curve
        } else {
            this.linePath.moveTo((float) start.getX(), (float) start.getY());
            this.linePath.quadTo((float) edgePoint.getX(), (float) edgePoint
                    .getY(), (float) end.getX(), (float) end.getY());
        }

        this.realBounds = this.linePath.getBounds2D();
        this.realBounds = getThickBounds(this.linePath, edgeAttr);

        if (this.headArrow != null) {
            this.realBounds.add(this.headArrow.getBounds2D());
        }

        if (this.tailArrow != null) {
            this.realBounds.add(this.tailArrow.getBounds2D());
        }

        AffineTransform at = new AffineTransform();
        at.setToTranslation(-this.realBounds.getX(), -this.realBounds.getY());
        this.headArrow = at.createTransformedShape(this.headArrow);
        this.tailArrow = at.createTransformedShape(this.tailArrow);
        this.linePath = new GeneralPath(this.linePath
                .createTransformedShape(at));

    }

    /**
     * A circle segment is an open curve, not a closed area, so always return
     * false
     */
    @Override
    public boolean contains(double x, double y) {
        return false;
    }

    /**
     * Calculate center and radius of circle given three points
     */
    private Point2D computeCenter(Point2D p1, Point2D p2, Point2D p3) {

        float m11, m12, m13; // , m14;
        float[][] p = new float[3][2];
        p[0][0] = (float) p1.getX();
        p[0][1] = (float) p1.getY();
        p[1][0] = (float) p2.getX();
        p[1][1] = (float) p2.getY();
        p[2][0] = (float) p3.getX();
        p[2][1] = (float) p3.getY();

        float a[][] = new float[3][3];

        for (int i = 0; i < 3; i++) // find minor 11
        {
            a[i][0] = p[i][0];
            a[i][1] = p[i][1];
            a[i][2] = 1;
        }
        m11 = determinant(a, 3);

        for (int i = 0; i < 3; i++) // find minor 12
        {
            a[i][0] = p[i][0] * p[i][0] + p[i][1] * p[i][1];
            a[i][1] = p[i][1];
            a[i][2] = 1;
        }
        m12 = determinant(a, 3);

        for (int i = 0; i < 3; i++) // find minor 13
        {
            a[i][0] = p[i][0] * p[i][0] + p[i][1] * p[i][1];
            a[i][1] = p[i][0];
            a[i][2] = 1;
        }
        m13 = determinant(a, 3);

        for (int i = 0; i < 3; i++) // find minor 14
        {
            a[i][0] = p[i][0] * p[i][0] + p[i][1] * p[i][1];
            a[i][1] = p[i][0];
            a[i][2] = p[i][1];
        }
        // m14 = determinant(a, 3);

        if (m11 == 0)
            return null; // not a circle - points lie on one line
        else
            return new Point2D.Double(0.5 * m12 / m11, -0.5 * m13 / m11);
    }

    /**
     * Recursive definition of determinate using expansion by minors.
     */
    private float determinant(float[][] a, int n) {

        float d = 0;
        float[][] m = new float[3][3];

        if (n == 2) // terminate recursion
        {
            d = a[0][0] * a[1][1] - a[1][0] * a[0][1];
        } else {
            d = 0;
            for (int j1 = 0; j1 < n; j1++) // do each column
            {
                for (int i = 1; i < n; i++) // create minor
                {
                    int j2 = 0;
                    for (int j = 0; j < n; j++) {
                        if (j == j1) {
                            continue;
                        }
                        m[i - 1][j2] = a[i][j];
                        j2++;
                    }
                }

                // sum (+/-)cofactor * minor
                d = d + (float) Math.pow(-1.0, j1) * a[0][j1]
                        * determinant(m, n - 1);
            }
        }

        return d;
    }

}
