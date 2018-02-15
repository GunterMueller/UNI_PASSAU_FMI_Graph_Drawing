// =============================================================================
//
//   MathUtil.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import org.graffiti.util.Pair;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class MathUtil {
    private static final double FLATNESS = 1.0;

    public static Point2D footShift(double x, double y, double dx, double dy,
            double alpha, double norm) {
        double vX = -dy / norm;
        double vY = dx / norm;
        return new Point2D.Double(x + alpha * vX, y + alpha * vY);
    }

    public static double norm(double dx, double dy) {
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static double distance(double[] p1, double[] p2) {
        return norm(p2[0] - p1[0], p2[1] - p1[1]);
    }

    public static Point2D footShift(double x, double y, double dx, double dy,
            double alpha) {
        return footShift(x, y, dx, dy, alpha, norm(dx, dy));
    }

    public static Point2D interpolate(double x1, double y1, double x2,
            double y2, double alpha) {
        return new Point2D.Double(x1 * (1 - alpha) + x2 * alpha, y1
                * (1 - alpha) + y2 * alpha);
    }

    public static Point2D interpolate(Point2D point1, Point2D point2,
            double alpha) {
        return interpolate(point1.getX(), point1.getY(), point2.getX(), point2
                .getY(), alpha);
    }

    /**
     * 
     * @param path
     *            the path. Must contain at least one point.
     * @param alpha
     *            the interpolation factor. It is clamped to the closed interval
     *            [0, 1].
     * @return the interpolated point.
     */
    public static Point2D interpolate(Shape path, double alpha) {
        if (path == null)
            return null;
        double[][] seg = new double[2][6];
        double lastMoveX = 0.0;
        double lastMoveY = 0.0;

        // First calculate total length.
        double length = 0;

        // As arrays of generics are disallowed...
        class DoublePointPair extends Pair<Double, Point2D> {
            public DoublePointPair(double first, Point2D second) {
                super(first, second);
            }
        }
        ;
        ArrayList<DoublePointPair> points = new ArrayList<DoublePointPair>();
        int i = 0;
        for (PathIterator iter = path.getPathIterator(null, FLATNESS); !iter
                .isDone(); iter.next(), i = 1 - i) {
            int segmentType = iter.currentSegment(seg[i]);
            switch (segmentType) {
            case PathIterator.SEG_MOVETO:
                lastMoveX = seg[i][0];
                lastMoveY = seg[i][1];
                break;
            case PathIterator.SEG_CLOSE:
                seg[i][0] = lastMoveX;
                seg[i][1] = lastMoveY;
                // fall through
            case PathIterator.SEG_LINETO:
                length += distance(seg[0], seg[1]);
                break;
            }
            points.add(new DoublePointPair(length, new Point2D.Double(
                    seg[i][0], seg[i][1])));
        }
        int pointCount = points.size();
        if (pointCount <= 1) {
            if (pointCount == 0)
                // throw new IllegalArgumentException();
                return new Point2D.Double();
            else
                return points.get(0).getSecond();
        }
        DoublePointPair[] pairs = new DoublePointPair[2];
        pairs[1] = points.get(0);
        double goalDistance = alpha * length;

        // Find the line segment containing the point which is goalDistance away
        // from the start of the path.
        i = 0;
        for (i = 0; i < pointCount; i++) {
            pairs[i & 1] = points.get(i);
            double secondDistance = pairs[i & 1].getFirst();
            if (secondDistance >= goalDistance) {
                // pairs[(i + 1) & 1].getSecond() is first point.
                // pairs[i & 1].getSecond() is second point.
                double firstDistance = pairs[(i + 1) & 1].getFirst();
                if (firstDistance == secondDistance)
                    return pairs[i & 1].getSecond();
                return interpolate(pairs[(i + 1) & 1].getSecond(), pairs[i & 1]
                        .getSecond(), (goalDistance - firstDistance)
                        / (secondDistance - firstDistance));
            }
        }
        return null;
    }

    public static Point2D interpolate(Shape path, int segmentNumber,
            double alpha) {
        if (segmentNumber == 0)
            return interpolate(path, alpha);
        double[][] seg = new double[2][6];
        double lastMoveX = 0.0;
        double lastMoveY = 0.0;
        int currentSegmentNumber = 0;
        int i = 0;
        int[] segmentType = new int[2];
        for (PathIterator iter = path.getPathIterator(null); !iter.isDone(); iter
                .next(), i++) {
            segmentType[i & 1] = iter.currentSegment(seg[i & 1]);
            switch (segmentType[i & 1]) {
            case PathIterator.SEG_MOVETO:
                lastMoveX = seg[i & 1][0];
                lastMoveY = seg[i & 1][1];
                break;
            case PathIterator.SEG_CLOSE:
                seg[i & 1][0] = lastMoveX;
                seg[i & 1][1] = lastMoveY;
                // fall through
            case PathIterator.SEG_LINETO:
            case PathIterator.SEG_QUADTO:
            case PathIterator.SEG_CUBICTO:
                currentSegmentNumber++;
                break;
            }
            if (currentSegmentNumber == segmentNumber) {
                GeneralPath pathSegment = new GeneralPath();
                switch (segmentType[(i + 1) & 1]) {
                case PathIterator.SEG_MOVETO:
                case PathIterator.SEG_CLOSE:
                case PathIterator.SEG_LINETO:
                    pathSegment.moveTo((float) seg[(i + 1) & 1][0],
                            (float) seg[(i + 1) & 1][1]);
                    break;
                case PathIterator.SEG_QUADTO:
                    pathSegment.moveTo((float) seg[(i + 1) & 1][2],
                            (float) seg[(i + 1) & 1][3]);
                    break;
                case PathIterator.SEG_CUBICTO:
                    pathSegment.moveTo((float) seg[(i + 1) & 1][4],
                            (float) seg[(i + 1) & 1][5]);
                }
                switch (segmentType[i & 1]) {
                case PathIterator.SEG_CLOSE:
                case PathIterator.SEG_LINETO:
                    pathSegment.lineTo((float) seg[i & 1][0],
                            (float) seg[i & 1][1]);
                    break;
                case PathIterator.SEG_QUADTO:
                    pathSegment.quadTo((float) seg[i & 1][0],
                            (float) seg[i & 1][1], (float) seg[i & 1][2],
                            (float) seg[i & 1][3]);
                    break;
                case PathIterator.SEG_CUBICTO:
                    pathSegment.curveTo((float) seg[i & 1][0],
                            (float) seg[i & 1][1], (float) seg[i & 1][2],
                            (float) seg[i & 1][3], (float) seg[i & 1][4],
                            (float) seg[i & 1][5]);
                    break;
                }
                return interpolate(pathSegment, alpha);
            }
        }
        if (i == 0)
            // Path must be nonempty.
            throw new IllegalArgumentException();
        switch (segmentType[(i + 1) & 1]) {
        case PathIterator.SEG_MOVETO:
        case PathIterator.SEG_CLOSE:
        case PathIterator.SEG_LINETO:
            return new Point2D.Double(seg[(i + 1) & 1][0], seg[(i + 1) & 1][1]);
        case PathIterator.SEG_QUADTO:
            return new Point2D.Double(seg[(i + 1) & 1][2], seg[(i + 1) & 1][3]);
        case PathIterator.SEG_CUBICTO:
            return new Point2D.Double(seg[(i + 1) & 1][4], seg[(i + 1) & 1][5]);
        default:
            return null;
        }

    }

    public static Rectangle2D getTransformedBounds(Shape shape,
            AffineTransform transform) {
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double[] coords = new double[6];
        for (PathIterator iter = shape.getPathIterator(transform); !iter
                .isDone(); iter.next()) {
            switch (iter.currentSegment(coords)) {
            case PathIterator.SEG_CUBICTO:
                coords[2] = coords[4];
                coords[3] = coords[5];
                // Fallthrough
            case PathIterator.SEG_QUADTO:
                coords[0] = coords[2];
                coords[1] = coords[3];
                // Fallthrough
            case PathIterator.SEG_MOVETO:
            case PathIterator.SEG_LINETO:
                minX = Math.min(minX, coords[0]);
                minY = Math.min(minY, coords[1]);
                maxX = Math.max(maxX, coords[0]);
                maxY = Math.max(maxY, coords[1]);
                break;
            }
        }
        // +1 for width, height?
        return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
    }

    public static Point2D subtract(Point2D p1, Point2D p2) {
        return new Point2D.Double(p1.getX() - p2.getX(), p1.getY() - p2.getY());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
