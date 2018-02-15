// =============================================================================
//
//   AbstractSEForce.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractSEForce.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.springembedderFR;

/**
 * Abstract class with one abstract method, where the force should be
 * calculated.
 * 
 * @author matzeder
 * @version $Revision: 5766 $ $Date: 2006-06-12 07:52:18 +0200 (Mo, 12 Jun 2006)
 *          $
 */
public abstract class AbstractSEForce {

    protected SEAlgorithmParameters parameters;

    /**
     * No variants, e.g. grid variant, node sizes or nodes growing
     */
    public final static String NO_GV_NO_NODE_SIZE = "no variants";

    /**
     * Only grid variant is used in algorithm.
     */
    public final static String GV = "grid variant";

    /**
     * Only node size variant is used.
     */
    public final static String NODE_SIZE = "node sizes";

    /**
     * Node size and nodes growing variant is used.
     */
    public final static String NODE_SIZE_GROWING = NODE_SIZE + " - growing";

    /**
     * Grid variant and node size variants are used (no nodes growing variant).
     */
    public final static String GV_NODE_SIZE = GV + " - " + NODE_SIZE;

    /**
     * Grid variant, node size and nodes growing variants are used.
     */
    public final static String GV_NODE_SIZE_GROWING = GV_NODE_SIZE
            + " - growing nodes";

    /**
     * Quenching phase of the algorithm, where temperature is set very high
     */
    public final static int QUENCHING_PHASE = 1;

    /**
     * Simmering phase of the algorithm, wher temperature cools down fast
     */
    public final static int SIMMERING_PHASE = 2;

    /**
     * String constant for the attraction force
     */
    public final static String ATT_FORCE = "FORCE: ATT";

    /**
     * String constant for the repulsion force
     */
    public final static String REP_FORCE = "FORCE: REP";

    /**
     * String constant for the gravitation force
     */
    public final static String GRAV_FORCE = "FORCE: GRAV";

    /**
     * String constant for the force between nodes and edges
     */
    public final static String NODE_EDGE_FORCE = " FORCE: N-E REP";

    /**
     * Constant for the case of node edge intersection, defines the strength of
     * repulsion
     */
    public static final double REPULSIVE_INTERSECTION_STRENGTH_CONSTANT = 1d;

    /**
     * String constant for the magnetic spring force
     */
    public final static String MAGN_FORCE = "FORCE: MAGNETIC";

    /**
     * Constant for the user interaction
     */
    public final static String NO_MAGNETIC_FIELD = "NO MAGNETIC FIELD";

    /**
     * For magnetic direction
     */
    public final static String NORTH = "north";

    /**
     * For magnetic direction
     */
    public final static String SOUTH = "south";

    /**
     * For magnetic direction
     */
    public final static String WEST = "west";

    /**
     * For magnetic direction
     */
    public final static String EAST = "east";

    /**
     * For magnetic direction
     */
    public final static String POLAR = "polar";

    /**
     * For magnetic direction
     */
    public final static String CONCENTRIC_CLOCK = "concentric clockwise";

    /**
     * Constructor for this class.
     * 
     * @param p
     *            Parameters of the algorithms.
     */
    public AbstractSEForce(SEAlgorithmParameters p) {
        parameters = p;
    }

    /**
     * This method should be implemented to calculate the specific type of
     * force.
     * 
     */
    public abstract void calculateForce();

    /**
     * Moves a node a little bit in a random direction.
     * 
     * @param node
     *            The node to move random.
     */
    protected void moveRandom(FRNode node) {

        double x = (Math.random() - 0.5) * 10;
        double y = (Math.random() - 0.5) * 10;

        node.setXPos(node.getXPos() + x);
        node.setYPos(node.getYPos() + y);
    }

    /**
     * Calculation of the distance vector between two nodes.
     * 
     * @param currNode1
     *            The first given node.
     * @param currNode2
     *            The second given node.
     * @return The distance vector between two nodes.
     */
    protected GeometricalVector calculationDistanceVector(FRNode currNode1,
            FRNode currNode2) {

        if (parameters.isCalculationWithNodesAsRectangles)
            return calculateDistanceVectorOfNodesAsRectangles(currNode1,
                    currNode2);
        else
            return calculateDistanceVectorOfNodesAsCircles(currNode1, currNode2);

    }

    /**
     * This method calculates the distance vector between nodes with the nodes
     * simulating as rectangles.
     * 
     * @param currNode1
     *            The first given node.
     * @param currNode2
     *            The second given node.
     * @return Calculated distance vector
     */
    protected GeometricalVector calculateDistanceVectorOfNodesAsRectangles(
            FRNode currNode1, FRNode currNode2) {

        GeometricalVector delta;
        if (parameters.isCalculationWithNodeSizes) {
            double leftBoundCurrNode1;
            double upperBoundCurrNode1;
            double lowerBoundCurrNode1;
            double rightBoundCurrNode1;

            double upperBoundCurrNode2;
            double lowerBoundCurrNode2;
            double leftBoundCurrNode2;
            double rightBoundCurrNode2;

            if (parameters.isNodesGrowing) {
                double factor = 1.0d;

                if ((double) parameters.simmeringIteration
                        / (double) parameters.simmeringIterations < 0.5d) {
                    factor = ((double) parameters.simmeringIteration / (double) parameters.simmeringIterations) * 2;
                }

                leftBoundCurrNode1 = currNode1.getLeftBound(factor);
                upperBoundCurrNode1 = currNode1.getUpperBound(factor);
                lowerBoundCurrNode1 = currNode1.getLowerBound(factor);
                rightBoundCurrNode1 = currNode1.getRightBound(factor);

                upperBoundCurrNode2 = currNode2.getUpperBound(factor);
                lowerBoundCurrNode2 = currNode2.getLowerBound(factor);
                leftBoundCurrNode2 = currNode2.getLeftBound(factor);
                rightBoundCurrNode2 = currNode2.getRightBound(factor);
            } else {
                leftBoundCurrNode1 = currNode1.getLeftBound();
                upperBoundCurrNode1 = currNode1.getUpperBound();
                lowerBoundCurrNode1 = currNode1.getLowerBound();
                rightBoundCurrNode1 = currNode1.getRightBound();

                upperBoundCurrNode2 = currNode2.getUpperBound();
                lowerBoundCurrNode2 = currNode2.getLowerBound();
                leftBoundCurrNode2 = currNode2.getLeftBound();
                rightBoundCurrNode2 = currNode2.getRightBound();

            }

            // minimal distances between the borders, x and y direction
            double xDist;
            double yDist;
            // currNode2 liegt rechts von currNode1
            if (leftBoundCurrNode2 > rightBoundCurrNode1) {
                // currNode2 rechts unterhalb von currNode1
                if (upperBoundCurrNode2 > lowerBoundCurrNode1) {
                    // System.out.println(currNode2 + " rechts unterhalb
                    // von" +
                    // currNode1);
                    xDist = leftBoundCurrNode2 - rightBoundCurrNode1;
                    yDist = upperBoundCurrNode2 - lowerBoundCurrNode1;

                    return new GeometricalVector(-xDist, -yDist);

                }
                // currNode2 rechts oberhalb von currNode1
                else if (lowerBoundCurrNode2 < upperBoundCurrNode1) {
                    // System.out.println(currNode2 + " rechts oberhalb von"
                    // +
                    // currNode1);
                    xDist = leftBoundCurrNode2 - rightBoundCurrNode1;
                    yDist = upperBoundCurrNode1 - lowerBoundCurrNode2;

                    return new GeometricalVector(-xDist, yDist);
                }
                // currNode2 liegt "genau" rechts von currNode1
                else {
                    // System.out.println(currNode2 + " genau rechts von" +
                    // currNode1);
                    xDist = leftBoundCurrNode2 - rightBoundCurrNode1;

                    double deltaX = currNode2.getXPos() - currNode1.getXPos();
                    double deltaY = currNode2.getYPos() - currNode1.getYPos();

                    yDist = (xDist / deltaX) * deltaY;
                    return new GeometricalVector(-xDist, -yDist);
                }

            }
            // currNode2 liegt links von currNode1
            else if (rightBoundCurrNode2 < leftBoundCurrNode1) {

                // currNode2 links unterhalb von currNode1
                if (upperBoundCurrNode2 > lowerBoundCurrNode1) {
                    // System.out.println(currNode2 + " links unterhalb von"
                    // +
                    // currNode1);
                    xDist = leftBoundCurrNode1 - rightBoundCurrNode2;
                    yDist = upperBoundCurrNode2 - lowerBoundCurrNode1;

                    return new GeometricalVector(xDist, -yDist);

                }
                // currNode2 links oberhalb von currNode1
                else if (lowerBoundCurrNode2 < upperBoundCurrNode1) {
                    // System.out.println(currNode2 + " links oberhalb von"
                    // +
                    // currNode1);
                    xDist = leftBoundCurrNode1 - rightBoundCurrNode2;
                    yDist = upperBoundCurrNode1 - lowerBoundCurrNode2;

                    return new GeometricalVector(xDist, yDist);
                } else {
                    // System.out.println(currNode2 + " genau links von" +
                    // currNode1);
                    xDist = leftBoundCurrNode1 - rightBoundCurrNode2;

                    double deltaX = currNode2.getXPos() - currNode1.getXPos();
                    double deltaY = currNode2.getYPos() - currNode1.getYPos();

                    yDist = (xDist / deltaX) * deltaY;
                    return new GeometricalVector(xDist, yDist);
                }
            }
            // currNode2 liegt oberhalb von currNode1
            else if (lowerBoundCurrNode2 < upperBoundCurrNode1) {
                // System.out.println(currNode2 + " genau oberhalb von" +
                // currNode1);

                yDist = upperBoundCurrNode1 - lowerBoundCurrNode2;

                double deltaX = currNode2.getXPos() - currNode1.getXPos();
                double deltaY = currNode2.getYPos() - currNode1.getYPos();

                xDist = (yDist / deltaY) * deltaX;
                return new GeometricalVector(xDist, yDist);

            }
            // currNode2 liegt unterhalb von currNode1
            else if (upperBoundCurrNode2 > lowerBoundCurrNode1) {
                // System.out.println(currNode2 + " genau unterhalb von" +
                // currNode1);
                yDist = upperBoundCurrNode2 - lowerBoundCurrNode1;

                double deltaX = currNode2.getXPos() - currNode1.getXPos();
                double deltaY = currNode2.getYPos() - currNode1.getYPos();

                xDist = (yDist / deltaY) * deltaX;
                return new GeometricalVector(-xDist, -yDist);
            } else
                return new GeometricalVector();
        }
        // without node sizes
        else {
            // Abstandsvektor zwischen den 2 betrachteten Knoten
            // (von Knotenmittelpunkten)
            delta = new GeometricalVector(currNode1, currNode2);
        }
        // System.out.println("delta: " + delta);
        return delta;

    }

    /**
     * Calculates the real distance between nodes, dependent on the phase. If
     * there is calculation with node sizes is required, then it is
     * incorporated.
     * 
     * @param currNode1
     *            The first given node.
     * @param currNode2
     *            The second given node.
     * @return the GeometricalVector between the two nodes
     */
    protected GeometricalVector calculateDistanceVectorOfNodesAsCircles(
            FRNode currNode1, FRNode currNode2) {

        GeometricalVector delta;
        if (parameters.isCalculationWithNodeSizes) {
            // radius surrounding the node, with radius = 1/2*diagonal
            double radiusCurrNode1 = currNode1.getDimension();

            double radiusCurrNode2 = currNode2.getDimension();

            // to let the nodes in simmering phase slowly growing
            if (parameters.isNodesGrowing) {

                double factor = 1.0;

                if ((double) parameters.simmeringIteration
                        / (double) parameters.simmeringIterations < 0.5d) {
                    factor = ((double) parameters.simmeringIteration / (double) parameters.simmeringIterations) * 2;
                }

                radiusCurrNode1 *= factor;
                radiusCurrNode2 *= factor;
            }

            double sumRadius = radiusCurrNode1 + radiusCurrNode2;

            double x = currNode1.getXPos() - currNode2.getXPos();
            double y = currNode1.getYPos() - currNode2.getYPos();

            double distSquare = x * x + y * y;

            double pointDistance = Math.sqrt(distSquare);

            // if nodes intersect
            if (distSquare <= (sumRadius * sumRadius)) {
                delta = new GeometricalVector();
            } else {
                // border distance ist the length of the edge from the border of
                // the circle currNode1 to border of the circle to currNode2
                // (positive because
                // of the if(...))
                double d_out = pointDistance - sumRadius;

                double deltaX = x * d_out / pointDistance;
                double deltaY = y * d_out / pointDistance;

                delta = new GeometricalVector(deltaX, deltaY);
            }
        } else {
            // distance vector between two nodes (with node centers)
            delta = new GeometricalVector(currNode1, currNode2);
        }
        return delta;
    }

    /**
     * Calculates the vector of the orthogonal intersection point of the edge
     * (source, target) and the orthogonal projection of node.
     * 
     * @param sourceVector
     *            source vector of the edge
     * @param targetVector
     *            target vector of the edge
     * @param nodeVector
     *            vector of the node
     * @return orthogonal intersection point
     */
    protected static GeometricalVector getOrthogonalIntersectionPoint(
            GeometricalVector sourceVector, GeometricalVector targetVector,
            GeometricalVector nodeVector) {

        GeometricalVector diff = GeometricalVector.subtract(targetVector,
                sourceVector);

        // line equation to know the straightline of the edge
        LineEquation line = new LineEquation(targetVector, diff);

        GeometricalVector orthogonalDirectionVector = line
                .getOrthogonalVector();

        LineEquation orthogonalLine = new LineEquation(nodeVector,
                orthogonalDirectionVector);

        return LineEquation.getIntersectionPoint(line, orthogonalLine);

    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
