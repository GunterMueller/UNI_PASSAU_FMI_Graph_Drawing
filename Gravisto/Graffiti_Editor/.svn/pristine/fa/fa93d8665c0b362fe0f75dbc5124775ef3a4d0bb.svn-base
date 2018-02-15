package org.graffiti.plugins.algorithms.phyloTrees.utility;

import java.awt.geom.Point2D;
import java.util.Comparator;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;

/**
 * Compares the angles of two outgoing edges of a single node with a third
 * outgoing edge from this node.
 */
public class EdgeCounterclockwiseComparator implements Comparator<Edge> {
    private static final double EPSILON = 0.00000000000001;

    private Node baseTarget;

    private Point2D baseSourceCoords;

    private Point2D baseTargetCoords;

    /**
     * Constructor to set the base edge of this comparator.
     * 
     * @param baseEdge
     *            The base edge for comparing other edges to.
     */
    public EdgeCounterclockwiseComparator(Edge baseEdge) {
        baseTarget = baseEdge.getTarget();

        baseSourceCoords = GravistoUtil.getCoords(baseEdge.getSource());
        baseTargetCoords = GravistoUtil.getCoords(baseTarget);
    }

    /**
     * Returns a value greater, equal or less than 0, if the angle between the
     * first given edge and the baseEdge is greater, equal or smaller than the
     * angle between the second given edge and the base edge, respectively.
     * 
     * @param edge1
     *            The first edge.
     * @param edge2
     *            The second edge.
     * @return A value greater, equal or less than 0, if the angle between the
     *         first given edge and the baseEdge is greater, equal or smaller
     *         than the angle between the second given edge and the base edge,
     *         respectively.
     */
    public int compare(Edge edge1, Edge edge2) {
        assert edge1.getSource() == baseTarget;
        assert edge2.getSource() == baseTarget;

        double degreeEdge1 = getDegree(edge1.getTarget());
        double degreeEdge2 = getDegree(edge2.getTarget());

        if (Math.abs(degreeEdge1 - degreeEdge2) < EPSILON)
            return 0;
        else if (degreeEdge1 < degreeEdge2)
            return -1;
        else
            return 1;
    }

    private double getDegree(Node node) {
        double degree = 0;

        // calculate lies-right-off
        Point2D nodeCoords = GravistoUtil.getCoords(node);
        double w = (nodeCoords.getY() - baseSourceCoords.getY())
                * (baseTargetCoords.getX() - baseSourceCoords.getX())
                - (nodeCoords.getX() - baseSourceCoords.getX())
                * (baseTargetCoords.getY() - baseSourceCoords.getY());

        if (w == 0) // node lies on the straight line from baseEdge to
                    // baseTarget
        {
            degree = Math.PI;
        } else {
            // calculating the degree between baseSource, baseTarget and
            // baseTarget, node using the law of cosines
            double distSourceTarget = baseSourceCoords
                    .distance(baseTargetCoords);
            double distTargetNode = baseTargetCoords.distance(nodeCoords);
            double distSourceNode = baseSourceCoords.distance(nodeCoords);

            double numerator = square(distTargetNode)
                    + square(distSourceTarget) - square(distSourceNode);
            double denominator = 2 * distSourceTarget * distTargetNode;
            degree = Math.acos(numerator / denominator);

            // node lies left to the straight line
            if (w < 0) {
                degree = 2 * Math.PI - degree;
            }
        }

        return degree;
    }

    /**
     * Returns the square of a given value;
     * 
     * @param a
     *            The value to be squared.
     * @retun The square of the value given.
     */
    private static double square(double a) {
        return a * a;
    }
}
