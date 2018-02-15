package org.graffiti.plugins.algorithms.phyloTrees.utility;

import java.awt.geom.Point2D;
import java.util.LinkedList;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeLabelPositionAttribute;

/**
 * This class contains utility methods specifically for drawing of phylogenetic
 * trees. It is used throughout the Phylogenetic Tree Plugins.
 */
final public class PhyloTreeUtil {
    /**
     * Scales the vector from source to target to newLength.
     * 
     * @param source
     *            The source of the vector to be scaled.
     * @param target
     *            The target of the vector to be scaled.
     * @param newLength
     *            The new distance from source to the new target.
     * @return The coordinate of the given vector's target scaled to the given
     *         length.
     */
    public static Point2D scaleTo(Point2D source, Point2D target,
            double newLength) {
        double initialDistance = source.distance(target);

        double initialHorizontalDistance = target.getX() - source.getX();
        double newHorizontalDistance = (initialHorizontalDistance / initialDistance)
                * newLength;
        double newX = source.getX() + newHorizontalDistance;

        double initialVerticalDistance = target.getY() - source.getY();
        double newVerticalDistance = (initialVerticalDistance / initialDistance)
                * newLength;
        double newY = source.getY() + newVerticalDistance;

        return new Point2D.Double(newX, newY);
    }

    /**
     * Returns the weight as set in {@link PhyloTreeConstants#PATH_WEIGHT}.
     * 
     * @param edge
     *            The edge whose weight is to be returned. Must not be null.
     * @return The weight saved in path {@link PhyloTreeConstants#PATH_WEIGHT}
     *         or 0, if none is set.
     */
    public static double getEdgeWeight(Edge edge) {
        assert edge != null : "Given Edge is null reference";

        double weight = 0;

        if (edge.containsAttribute(PhyloTreeConstants.PATH_WEIGHT)) {
            weight = edge.getDouble(PhyloTreeConstants.PATH_WEIGHT);
        }

        return weight;
    }

    /**
     * Sets the weight of the given Edge to the given value.
     * 
     * @param edge
     *            The edge whose weight is to be set. Must not be null.
     * @param weight
     *            The weight that is to be saved in the path
     *            {@link PhyloTreeConstants#PATH_WEIGHT}.
     * @see PhyloTreeConstants#PATH_WEIGHT
     */
    public static void setEdgeWeight(Edge edge, double weight) {
        assert edge != null : "Given Edge is null reference";

        edge.setDouble(PhyloTreeConstants.PATH_WEIGHT, weight);
    }

    /**
     * Sets or updates the number of an Edge.
     * 
     * @param edge
     *            The Edge whose number is to be set.
     * @param number
     *            The given Edge's new number.
     * 
     * @see PhyloTreeConstants#PATH_EDGE_NUMBER
     */
    public static void setEdgeNumber(Edge edge, int number) {
        assert edge != null;

        if (edge.containsAttribute(PhyloTreeConstants.PATH_EDGE_NUMBER)) {
            edge.changeInteger(PhyloTreeConstants.PATH_EDGE_NUMBER, number);
        } else {
            edge.setInteger(PhyloTreeConstants.PATH_EDGE_NUMBER, number);
        }
    }

    /**
     * Returns the Edge from the parent of the given Node to the given Node. The
     * given Node must have exactly one incoming Edge.
     * 
     * @param node
     *            The Node whose Edge to its parent is to be returned. The
     *            Node's number of incoming Edges must be one.
     * @return The Edge from the given Node's parent to the given Node.
     */
    public static Edge getEdgeToParent(Node node) {
        assert node.getInDegree() == 1 : "Number of incoming edges is not 1";

        Edge edge = node.getAllInEdges().iterator().next();
        return edge;
    }

    /**
     * Rotates the label of a node to face away from a reference point. The
     * label is aligned and rotated, to face away from the reference point.
     * 
     * @param reference
     *            The reference point.
     * @param node
     *            The Node, to which the Label belongs.
     * @throws AttributeNotFoundException
     *             Thrown, if the given node contains no Label attribute.
     */
    public static void setLabelRotation(Node node, Point2D reference) {
        assert reference != null : "reference must not be null";
        assert node != null : "null reference";

        if (!node.containsAttribute(GraphicAttributeConstants.LABEL))
            throw new AttributeNotFoundException("Given node contains no label");

        LabelAttribute label = (LabelAttribute) node
                .getAttribute(GraphicAttributeConstants.LABEL);
        NodeLabelPositionAttribute attr = (NodeLabelPositionAttribute) label
                .getAttribute(GraphicAttributeConstants.POSITION);

        Point2D nodePoint = GravistoUtil.getCoords(node);

        if (reference.distance(nodePoint) != 0) {
            boolean nodeIsRightToReference = reference.getX() < nodePoint
                    .getX();
            boolean nodeIsOverReference = nodePoint.getY() < reference.getY();

            if (nodeIsRightToReference) {
                attr.setAlignmentX(GraphicAttributeConstants.RIGHT_OUTSIDE);
            } else {
                attr.setAlignmentX(GraphicAttributeConstants.LEFT_OUTSIDE);
            }

            double radian = 0;

            if (nodePoint.getX() != reference.getX()
                    && nodePoint.getY() != reference.getY()) {
                double opposite = reference.getY() - nodePoint.getY();
                double adjacent = reference.getX() - nodePoint.getX();
                radian = Math.atan(opposite / adjacent);
            } else if (nodePoint.getX() == reference.getX()) {
                if (nodeIsOverReference) {
                    radian = Math.PI / 2;
                } else {
                    radian = -Math.PI / 2;
                }
            }

            attr.setRotationRadian(radian);
        }
    }

    /**
     * Returns whether the given Node is the root Node of a tree.
     * 
     * @param node
     *            The Node to be tested. Must not be null.
     * @return <code>true</code> if node is a root node, <code>false</code>
     *         otherwise.
     */
    public static boolean isRoot(Node node) {
        assert node != null : "node must not be null";

        return (node.getInDegree() == 0);
    }

    /**
     * Sets the given Node as the new root Node of the given tree. Returns the
     * old root Node.
     * 
     * @param newRoot
     *            The Node of the tree, which is to be set as the new Root of
     *            the tree it belongs to.
     * @return The old root Node of the tree.
     */
    public static Node rerootTree(Node newRoot) {
        assert newRoot != null : "node must not be null";

        // find the path from the root to the new root
        Node node = newRoot;

        LinkedList<Edge> edgesToParent = new LinkedList<Edge>();

        while (!isRoot(node)) {
            Edge edgeToParent = getEdgeToParent(node);
            // edgesToParent.add(getEdgeToParent(node));
            edgesToParent.addFirst(getEdgeToParent(node));
            node = edgeToParent.getSource();
        }

        final String oldEdgeNumberPath = "old_number";
        final String numberPath = PhyloTreeConstants.PATH_EDGE_NUMBER;

        // int nodeNumber = 1;

        for (Edge edge : edgesToParent) {

            if (!edge.containsAttribute(oldEdgeNumberPath)) {
                Edge[] previouslyOutgoingEdges = OrderedEdges
                        .getOrderedEdges(edge.getSource());

                edge.reverse();
                int oldEdgeNumber = edge.getInteger(numberPath);

                // set new edge number
                int newEdgeNumber = edge.getTarget().getOutDegree();
                edge.setInteger(numberPath, newEdgeNumber);

                // save old number
                edge.setInteger(oldEdgeNumberPath, oldEdgeNumber);

                // correct edge numbering of the previous root
                for (int i = 0; i < previouslyOutgoingEdges.length; ++i) {
                    int oldNumber = i + 1;
                    if (oldNumber != oldEdgeNumber) {
                        int oldLength = previouslyOutgoingEdges.length + 1;
                        int newNumber = (oldNumber - oldEdgeNumber + oldLength)
                                % oldLength;
                        previouslyOutgoingEdges[i].setInteger(numberPath,
                                newNumber);
                    }
                }
            } else {
                Edge[] previouslyOutgoingEdges = OrderedEdges
                        .getOrderedEdges(edge.getTarget());

                edge.reverse();

                // set new edge number
                int newEdgeNumber = edge.getInteger(oldEdgeNumberPath);
                edge.removeAttribute(oldEdgeNumberPath);
                edge.setInteger(numberPath, newEdgeNumber);

                for (int i = 0; i < previouslyOutgoingEdges.length; ++i) {
                    int oldNumber = i + 1;
                    int degree = edge.getSource().getOutDegree();

                    // calculate new number
                    int newNumber = (oldNumber + newEdgeNumber + degree)
                            % degree;
                    if (newNumber == 0) {
                        newNumber = degree;
                    }

                    // set new number
                    previouslyOutgoingEdges[i]
                            .setInteger(numberPath, newNumber);
                }
            }
        }

        return node;
    }
}
