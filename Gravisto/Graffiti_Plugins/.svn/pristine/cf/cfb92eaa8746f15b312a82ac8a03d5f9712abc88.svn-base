package org.graffiti.plugins.algorithms.labeling.finitePositions;

import java.util.LinkedList;
import java.util.List;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugins.algorithms.springembedderFR.GeometricalVector;

public abstract class LabelCandidateCollisionStructure {

    /**
     * Out of the given locators this routine calls <tt>testCollision</tt> among
     * all those pairs that are evaluated to <tt>true</tt>. The number of calls
     * is expected to be as low as possible (the optimum is the number of
     * collisions existing - at worst this is O(#labels�), if every label
     * overlaps every other one).
     * <p>
     * <b><i>Note:</i></b> This routine has side effects on the position
     * candidates of the given locators. If a collision is found, the
     * appropriate markers are set there.
     * 
     * @param locators
     *            - find collisions among these label locators with position
     *            candidates
     * @return a subset of the given locators, which have position candidates
     *         that don't collide with others
     */
    public abstract LinkedList<LabelLocator> collisionCalculation(
            List<LabelLocator> locators, double interLabelGap);

    /**
     * Tests for overlap for two axis-parallel rectangular label positions.
     * 
     * @return true - if there is an overlap <br>
     *         false - else
     */
    protected final boolean testCollision(LabelPosition a, LabelPosition b,
            double gap) {
        GeometricalVector aLR = a.getCoordinateLowerRight();
        GeometricalVector aUL = a.getCoordinateUpperLeft();
        GeometricalVector bLR = b.getCoordinateLowerRight();
        GeometricalVector bUL = b.getCoordinateUpperLeft();

        Statistics.numCollisionTests++;

        // test overlap among x-axis and y axis
        return (aLR.getX() + gap >= bUL.getX()
                && bLR.getX() + gap >= aUL.getX()
                && aLR.getY() + gap >= bUL.getY() && bLR.getY() + gap >= aUL
                .getY());
    }

    /**
     * checks if this position overlaps with the given node
     * <p>
     * <b><i>Developer's note</i></b>: This routine was added at a development
     * stage with little time until deadline. To my shame I have not been able
     * integrate it into the <tt>LabelCandidateCollisionStructure</tt>. It would
     * have been necessary to add graph nodes and graph edges to accelerated
     * collision detection functionality. So this is a job to be done by future
     * work - maybe by you who is still reading this note (though I am not that
     * much addicted to illusions to not realize that the most probable future
     * of this work rather is to be vegetating forlornly on some dusty rust
     * bucket).
     */
    public static boolean checkOverlapWithNode(LabelPosition label, Node node) {
        GeometricalVector labelLR = label.getCoordinateLowerRight();
        GeometricalVector labelUL = label.getCoordinateUpperLeft();
        NodeGraphicAttribute nodeGraphicAttributes = (NodeGraphicAttribute) node
                .getAttribute(GraphicAttributeConstants.GRAPHICS);
        GeometricalVector nodePos = new GeometricalVector(nodeGraphicAttributes
                .getCoordinate().getX(), nodeGraphicAttributes.getCoordinate()
                .getY());
        GeometricalVector halfNodeSize = GeometricalVector.mult(
                new GeometricalVector(nodeGraphicAttributes.getDimension()
                        .getWidth(), nodeGraphicAttributes.getDimension()
                        .getHeight()), 0.5d);
        GeometricalVector nodeLR = GeometricalVector.add(nodePos, halfNodeSize);
        GeometricalVector nodeUL = GeometricalVector.subtract(nodePos,
                halfNodeSize);

        Statistics.numCollisionTestsWithNodes++;

        // > 0 to not collide with parent node if placed adjacent to it
        double shrinkSize = 1d;

        // test overlap among x-axis and y axis
        return (labelLR.getX() - shrinkSize >= nodeUL.getX()
                && nodeLR.getX() - shrinkSize >= labelUL.getX()
                && labelLR.getY() - shrinkSize >= nodeUL.getY() && nodeLR
                .getY()
                - shrinkSize >= labelUL.getY());
    }

    /**
     * checks if this position overlaps with the given edge
     * <p>
     * <b><i>Developer's note</i></b>: This routine was added at a development
     * stage with little time until deadline. To my shame I have not been able
     * integrate it into the <tt>LabelCandidateCollisionStructure</tt>. It would
     * have been necessary to add graph nodes and graph edges to accelerated
     * collision detection functionality. So this is a job to be done by future
     * work - maybe by you who is still reading this note (though I am not that
     * much addicted to illusions to not realize that the most probable future
     * of this work rather is to be vegetating forlornly on some dusty rust
     * bucket).
     */
    public static boolean checkOverlapWithEdge(LabelPosition label, Edge edge) {
        GeometricalVector labelLR = label.getCoordinateLowerRight();
        GeometricalVector labelUL = label.getCoordinateUpperLeft();
        GeometricalVector labelLL = new GeometricalVector(labelUL.getX(),
                labelLR.getY());
        GeometricalVector labelUR = new GeometricalVector(labelLR.getX(),
                labelUL.getY());
        GeometricalVector labelCenter = new GeometricalVector(labelUL.getX()
                + 0.5d * (labelLR.getX() - labelUL.getX()), labelUL.getY()
                + 0.5d * (labelLR.getY() - labelUL.getY()));

        NodeGraphicAttribute sourceGraphicAttributes = (NodeGraphicAttribute) edge
                .getSource().getAttribute(GraphicAttributeConstants.GRAPHICS);
        NodeGraphicAttribute targetGraphicAttributes = (NodeGraphicAttribute) edge
                .getTarget().getAttribute(GraphicAttributeConstants.GRAPHICS);
        GeometricalVector edgeSrc = new GeometricalVector(
                sourceGraphicAttributes.getCoordinate().getX(),
                sourceGraphicAttributes.getCoordinate().getY());
        GeometricalVector edgeTgt = new GeometricalVector(
                targetGraphicAttributes.getCoordinate().getX(),
                targetGraphicAttributes.getCoordinate().getY());

        Statistics.numCollisionTestsWithEdges++;

        double centerDistance = calculateDistanceLinePoint(labelCenter,
                edgeSrc, edgeTgt);

        boolean onLineSegment = isProjectedPointOnLineSegment(labelLR, edgeSrc,
                edgeTgt)
                || isProjectedPointOnLineSegment(labelLL, edgeSrc, edgeTgt)
                || isProjectedPointOnLineSegment(labelUL, edgeSrc, edgeTgt)
                || isProjectedPointOnLineSegment(labelUR, edgeSrc, edgeTgt);

        if (!onLineSegment)
            return false; // return if out of edge segment

        // > 0 to not collide with parent edge if placed adjacent to it
        double shrinkSize = 1d;
        double closestDistance;
        if (centerDistance >= 0) {
            closestDistance = min4(calculateDistanceLinePoint(labelLR, edgeSrc,
                    edgeTgt)
                    + shrinkSize, calculateDistanceLinePoint(labelLL, edgeSrc,
                    edgeTgt)
                    + shrinkSize, calculateDistanceLinePoint(labelUL, edgeSrc,
                    edgeTgt)
                    + shrinkSize, calculateDistanceLinePoint(labelUR, edgeSrc,
                    edgeTgt)
                    + shrinkSize);
        } else {
            closestDistance = -max4(calculateDistanceLinePoint(labelLR,
                    edgeSrc, edgeTgt)
                    - shrinkSize, calculateDistanceLinePoint(labelLL, edgeSrc,
                    edgeTgt)
                    - shrinkSize, calculateDistanceLinePoint(labelUL, edgeSrc,
                    edgeTgt)
                    - shrinkSize, calculateDistanceLinePoint(labelUR, edgeSrc,
                    edgeTgt)
                    - shrinkSize);
        }

        return (closestDistance < 0);
    }

    /**
     * Projects the given point to the given line's normal.
     * 
     * @return the distance from given point to given line
     *         <p>
     *         If the given edge is facing to the right hand side and the given
     *         point is below the edge, the resulting distance will be positive.
     * @param point2D
     *            - Vector to calculate distance to edge
     * @param lineSrc
     *            - edge starting point
     * @param lineTgt
     *            - edge ending point
     * @author scholz
     */
    protected static double calculateDistanceLinePoint(
            GeometricalVector point2D, GeometricalVector lineSrc,
            GeometricalVector lineTgt) {
        // normalized edge direction (source -> target)
        GeometricalVector edgeDirection = GeometricalVector.subt(lineTgt,
                lineSrc);
        // Vector from edge source to given point
        GeometricalVector relativePosition = GeometricalVector.subt(point2D,
                lineSrc);
        // calculate the vector product of relativePosition and edgeDirection
        double distance = edgeDirection.getX() * relativePosition.getY()
                - edgeDirection.getY() * relativePosition.getX();
        return distance;
    }

    /**
     * Projects the given point to the given line.
     * 
     * @return true - after projection, point is on the line <br>
     *         false - after projection, point is not on the line
     */
    private static boolean isProjectedPointOnLineSegment(
            GeometricalVector point2D, GeometricalVector lineSrc,
            GeometricalVector lineTgt) {
        GeometricalVector lineDir = GeometricalVector.subt(lineTgt, lineSrc);
        double lineLength = GeometricalVector.getLength(lineDir);
        lineDir = GeometricalVector.div(lineDir, lineLength);

        GeometricalVector pointDir = GeometricalVector.div(GeometricalVector
                .subt(point2D, lineSrc), lineLength);

        // Calculate scalar product
        double scalar = pointDir.getX() * lineDir.getX() + pointDir.getY()
                * lineDir.getY();

        // System.out.println("point projection: "
        // + point2D + " -> " + scalar);

        return (scalar > 0d) && (scalar < 1d);
    }

    private static double min4(double a, double b, double c, double d) {
        double min = a;
        if (b < min) {
            min = b;
        }
        if (c < min) {
            min = c;
        }
        if (d < min) {
            min = d;
        }
        return min;
    }

    private static double max4(double a, double b, double c, double d) {
        double max = a;
        if (b > max) {
            max = b;
        }
        if (c > max) {
            max = c;
        }
        if (d > max) {
            max = d;
        }
        return max;
    }

}
