package org.graffiti.plugins.algorithms.phyloTrees.utility;

import java.util.Stack;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.LabelAttribute;

/**
 * A DataSet is associated with a single tree. Computes and stores the
 * information of a tree, the computation is done just in time to avoid
 * unnecessary computations.
 */
public class DataSet {
    /** The root Node of the tree, this object is associated with. */
    private Node root;

    /** Indicates whether the cached values in this DataSet are up to date. */
    private boolean valuesAreUpToDate;

    /** The minimum weight found in the tree. */
    private double minWeight;

    /**
     * The shifting value, which is added to the weight of every edge, in order
     * to have only positive weights.
     */
    private double shifting;

    /** The average weight of all edges after shifting. */
    private double averageWeight;

    /** The path with the highest weight. */
    private double maxPathWeight;

    /** The height of the tree. */
    private int treeHeight;

    /** The number of leafs in the tree. */
    private int numberOfLeaves;

    /** The number of nodes in the tree. */
    private int numberOfNodes;

    /**
     * The smallest weight returned by the dataset, when asked for edge weights.
     */
    private double minReturnWeight;

    /** The width of the largest Label set in all leaf Nodes. */
    private double maxLabelWidth;

    /** The height of the highest Label set in all leaf Nodes. */
    private double maxLabelHeight;

    /**
     * Creates a new DataSet that is associated with a given root Node.
     * 
     * @param root
     *            The root Node, this DataSet is to be associated with.
     */
    public DataSet(Node root) {
        assert root != null : "given Node must not be null";
        assert root.getInDegree() == 0 : "given Node is no root Node";

        this.root = root;
        valuesAreUpToDate = false;
    }

    /**
     * Returns the average weight of all edges in the tree, associated with this
     * DataSet.
     * 
     * @return The average Edge weight.
     * @see PhyloTreeConstants#PATH_WEIGHT
     */
    public double getAverageWeight() {
        if (!valuesAreUpToDate) {
            updateValues();
        }

        return averageWeight;
    }

    /**
     * Returns the maximum cumulative weight of a path from root to leaf.
     * 
     * @return The maximum weight path..
     */
    public double getMaxPathWeight() {
        if (!valuesAreUpToDate) {
            updateValues();
        }

        return maxPathWeight;
    }

    /**
     * Returns the minimum weight stored in this DataSet.
     * 
     * @return The minimum weight in this DataSet.
     */
    public double getMinWeight() {
        if (!valuesAreUpToDate) {
            updateValues();
        }

        return this.minWeight;
    }

    /**
     * Returns the number of leaves of the associated tree.
     * 
     * @return The number of leaves of the associated tree.
     */
    public int getNumberOfLeaves() {
        if (!valuesAreUpToDate) {
            updateValues();
        }

        return numberOfLeaves;
    }

    /**
     * Returns the number of Nodes of the associated tree.
     * 
     * @return The number of Nodes of the associated tree.
     */
    public int getNumberOfNodes() {
        if (!valuesAreUpToDate) {
            updateValues();
        }

        return numberOfNodes;
    }

    /**
     * Returns the maximum Label width of all Labels set in the leaves of the
     * associated tree.
     * 
     * @return The maximum leaf Label width of the associated tree.
     */
    public double getMaxLabelWidth() {
        if (!valuesAreUpToDate) {
            updateValues();
        }

        return maxLabelWidth;
    }

    /**
     * Returns the height of the tree. that is the maximum number of edges that
     * connect a leaf with the root node.
     * 
     * @return The height of the tree.
     */
    public int getHeight() {
        if (!valuesAreUpToDate) {
            updateValues();
        }

        return treeHeight;
    }

    /**
     * Returns the maximum Label height of all Labels set in the leaves of the
     * associated tree.
     * 
     * @return The maximum leaf Label height of the associated tree.
     */
    public double getMaxLabelHeight() {
        if (!valuesAreUpToDate) {
            updateValues();
        }

        return maxLabelHeight;
    }

    /**
     * Returns the shifted edge weights.
     * 
     * Weights are shifted to make all weights larger than zero. The smallest
     * weight returned by this method is at least {@link DataSet#getMinWeight()}
     * .
     * 
     * @param edge
     *            The edge whose shifted weight is to be returned.
     * @return The shifted edge weight of the given edge.
     */
    public double getShiftedEdgeWeight(Edge edge) {
        if (!valuesAreUpToDate) {
            updateValues();
        }

        double weight = PhyloTreeUtil.getEdgeWeight(edge) + shifting;
        weight = Math.max(weight, this.minReturnWeight);
        return weight;
    }

    /**
     * Signals that the tree has changed and thus the cached values may no
     * longer be valid.
     */
    public void update() {
        valuesAreUpToDate = false;
    }

    private void updateValues() {
        updateValues1();
        valuesAreUpToDate = true; // must be over updateValues2
        updateValues2();
    }

    private void updateValues1() {
        Stack<Edge> edges = new Stack<Edge>();

        double minWeight = Double.MAX_VALUE;
        double sum = 0;
        int numberOfNodes = 0;
        int numberOfLeaves = 0;

        double maxLabelWidth = 0;
        double maxLabelHeight = 0;

        for (Edge edge : root.getAllOutEdges()) {
            edges.add(edge);
        }

        while (!edges.isEmpty()) {
            Edge edge = edges.pop();
            double edgeWeight = PhyloTreeUtil.getEdgeWeight(edge);

            // find minWeight
            minWeight = Math.min(minWeight, edgeWeight);

            // calculate overall sum
            sum += edgeWeight;

            // counter number of nodes
            numberOfNodes += 1;

            // find label attributes
            Node target = edge.getTarget();
            if (target.getOutDegree() == 0) {
                numberOfLeaves += 1;

                if (target.containsAttribute(GraphicAttributeConstants.LABEL)) {
                    LabelAttribute la = GravistoUtil.getLabelAttribute(target);
                    maxLabelWidth = Math.max(la.getWidth(), maxLabelWidth);
                    maxLabelHeight = Math.max(la.getHeight(), maxLabelHeight);
                }
            }

            // do traversal
            for (Edge childEdge : target.getAllOutEdges()) {
                edges.add(childEdge);
            }
        }

        // shift values
        if (sum == 0) {
            sum = numberOfNodes * PhyloTreeConstants.MIN_EDGE_WEIGHT;
            minWeight = PhyloTreeConstants.MIN_EDGE_WEIGHT;
        }

        double shifting = 0;
        if (minWeight < 0) {
            shifting = -minWeight;
            sum += shifting * numberOfNodes;
        }

        // set values
        this.minWeight = minWeight;
        this.shifting = shifting;
        this.averageWeight = sum / numberOfNodes;
        this.numberOfLeaves = numberOfLeaves;
        this.numberOfNodes = numberOfNodes;
        this.maxLabelWidth = maxLabelWidth;
        this.maxLabelHeight = maxLabelHeight;

        this.minReturnWeight = Math.max(PhyloTreeConstants.MIN_EDGE_WEIGHT,
                averageWeight * PhyloTreeConstants.MIN_EDGE_WEIGHT_FACTOR);
    }

    private void updateValues2() {
        Stack<Edge> edges = new Stack<Edge>();
        Stack<Double> weightStack = new Stack<Double>();
        Stack<Integer> depthStack = new Stack<Integer>();

        double maxPath = 0;
        int maxDepth = 0;

        for (Edge edge : root.getAllOutEdges()) {
            edges.add(edge);
            weightStack.add(0d);
            depthStack.add(0);
        }

        while (!edges.isEmpty()) {
            Edge edge = edges.pop();
            double edgeWeight = getShiftedEdgeWeight(edge);

            // find max path length
            double currentMaxPath = weightStack.pop();
            double newWeight = edgeWeight + currentMaxPath;
            if (maxPath < newWeight) {
                maxPath = newWeight;
            }

            // find max path depth
            int currentMaxDepth = depthStack.pop();
            int newDepth = currentMaxDepth + 1;
            maxDepth = Math.max(newDepth, maxDepth);

            // do traversal
            Node target = edge.getTarget();
            for (Edge childEdge : target.getAllOutEdges()) {
                edges.add(childEdge);
                weightStack.add(newWeight);
                depthStack.add(newDepth);
            }
        }

        // set values
        this.maxPathWeight = maxPath;
        this.treeHeight = maxDepth;
    }
}
