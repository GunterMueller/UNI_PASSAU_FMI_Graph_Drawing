package org.graffiti.plugins.algorithms.phyloTrees.drawingAlgorithms;

import java.util.Stack;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.phyloTrees.exceptions.UnknownNodePlacementException;
import org.graffiti.plugins.algorithms.phyloTrees.utility.OrderedEdges;
import org.graffiti.plugins.algorithms.phyloTrees.utility.Pair;
import org.graffiti.plugins.algorithms.phyloTrees.utility.PhyloTreeConstants;
import org.graffiti.plugins.algorithms.phyloTrees.utility.PhyloTreeUtil;

public abstract class AbstractGramAlgorithm extends AbstractTree {

    abstract public boolean getIgnoreLastEdgeWeight();

    abstract public void setCoords(Node node, double width, double height);

    abstract public void setHeight(Node node, double height);

    abstract public void setEdge(Edge edge);

    abstract public double getNormalizedWeight(Edge edge);

    abstract public double getWidth(Node node);

    abstract public void setWidth(Node node, double width);

    abstract public double getHeight(Node node);

    abstract public String getNodePlacement();

    /**
     * This algorithm sets the coordinates of the given node's subtree.
     * 
     * @return The upper and lower bound of the given nodes subtree.
     * @throws UnknownNodePlacementException
     *             Thrown if {@link #getNodePlacement()} returns an unknown
     *             placement.
     */
    public Pair<Double, Double> setCoordinates(Node node)
            throws UnknownNodePlacementException {
        prepareNode(node);

        double upperSubtreeLimit = -Double.MAX_VALUE;
        double lowerSubtreeLimit = Double.MAX_VALUE;

        // set radius
        if (!isRoot(node)) {
            if (getIgnoreLastEdgeWeight() && isLeaf(node)) {
                // this.nodeRadius.put(node, 1d);
                setWidth(node, 1);
            } else {
                Edge edgeToParent = PhyloTreeUtil.getEdgeToParent(node);
                Node parent = edgeToParent.getSource();

                double parentRadius = getWidth(parent);
                double edgeWeight = getNormalizedWeight(edgeToParent);
                double newRadius = parentRadius + edgeWeight;

                setWidth(node, newRadius);
            }
        }

        // recursion
        if (!isLeaf(node)) {
            double min = Double.MAX_VALUE;
            double max = -Double.MAX_VALUE;

            Stack<Pair<Double, Double>> coordWeightPairs = new Stack<Pair<Double, Double>>();
            double inverseWeightSum = 0;

            for (Edge outgoingEdge : OrderedEdges.getOrderedEdges(node)) {
                Node child = outgoingEdge.getTarget();
                // recursive call
                Pair<Double, Double> subtreeLimits = setCoordinates(child);

                upperSubtreeLimit = Math.max(upperSubtreeLimit, subtreeLimits
                        .getFirst());
                lowerSubtreeLimit = Math.min(lowerSubtreeLimit, subtreeLimits
                        .getSecond());

                double childHeight = getHeight(child);
                double edgeWeight = getNormalizedWeight(outgoingEdge);

                coordWeightPairs.add(new Pair<Double, Double>(childHeight,
                        1 / edgeWeight));
                inverseWeightSum += 1 / edgeWeight;

                max = Math.max(max, childHeight);
                min = Math.min(min, childHeight);
            }

            double newHeight;
            String nodePlacement = getNodePlacement();
            if (nodePlacement.equals(PhyloTreeConstants.NODES_CENTERED_SUBTREE)) {
                newHeight = ((upperSubtreeLimit - lowerSubtreeLimit) / 2)
                        + lowerSubtreeLimit;
            } else if (nodePlacement
                    .equals(PhyloTreeConstants.NODES_CENTERED_INTERMEDIATE)) {
                newHeight = ((max - min) / 2) + min;
            } else if (nodePlacement.equals(PhyloTreeConstants.NODES_WEIGHTED)) {
                double halfDist = (max - min) / 2;
                double center = halfDist + min;

                double vectorSum = 0;

                for (Pair<Double, Double> heightWeightPair : coordWeightPairs) {
                    double childHeight = heightWeightPair.getFirst();
                    double childCenterDist = Math.abs(center - childHeight);
                    double childRelativeWeight = childCenterDist / halfDist; // =
                                                                             // r
                    double childNormWeight = heightWeightPair.getSecond()
                            / inverseWeightSum; // = w

                    double vector = childNormWeight / 2 * childRelativeWeight;
                    if (center > childHeight) {
                        vector = -vector;
                    }

                    vectorSum += vector;
                }

                vectorSum *= halfDist;
                newHeight = center + vectorSum;
            } else
                throw new UnknownNodePlacementException(nodePlacement);

            setHeight(node, newHeight);
        }

        double radius = getWidth(node);
        double height = getHeight(node);
        radius = Math.min(radius, 1);
        setCoords(node, radius, height);

        upperSubtreeLimit = Math.max(upperSubtreeLimit, height);
        lowerSubtreeLimit = Math.min(lowerSubtreeLimit, height);

        // set edges
        for (Edge edge : node.getAllOutEdges()) {
            setEdge(edge);
        }

        return new Pair<Double, Double>(upperSubtreeLimit, lowerSubtreeLimit);
    }
}
