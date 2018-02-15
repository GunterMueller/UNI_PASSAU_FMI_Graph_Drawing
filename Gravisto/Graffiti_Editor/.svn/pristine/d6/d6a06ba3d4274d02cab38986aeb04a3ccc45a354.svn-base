package org.graffiti.plugins.algorithms.phyloTrees.drawingAlgorithms;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.phyloTrees.PhylogeneticTree;
import org.graffiti.plugins.algorithms.phyloTrees.utility.CircleScaling;
import org.graffiti.plugins.algorithms.phyloTrees.utility.GravistoUtil;
import org.graffiti.plugins.algorithms.phyloTrees.utility.OrderedEdges;
import org.graffiti.plugins.algorithms.phyloTrees.utility.Pair;
import org.graffiti.plugins.algorithms.phyloTrees.utility.PhyloTreeGraphData;

public class Circle extends AbstractTree implements GraphicAttributeConstants {

    /** The name of this algorithm. */
    public static final String ALGORITHM_NAME = "Circle";

    /**
     * The object containing the information about the current graph and
     * drawing.
     */
    private PhyloTreeGraphData data;

    /** The root Node of the tree, that is currently being drawn. */
    private Node root;

    private Map<Node, Double> coefficients;

    private Map<Node, Point2D> offset;

    private Map<Edge, Double> weighting;

    private Map<Node, Point2D> coordinatesInUnitCircle;

    private double overalSumOfWeight;

    private double overalSumOfEdgeLengths;

    /**
     * The number of leaf nodes in the tree.
     */
    private int numberOfLeaves;

    private int i;

    /** Map caching the number of leafs in a subtree. */
    private Map<Node, Integer> subtreeLeafCount;

    private CircleScaling scaling;

    public void drawGraph(Graph graph, PhyloTreeGraphData data) {
        // read parameters
        Parameter<?>[] parameters = data.getAlgorithmParameters();

        BooleanParameter autoScalePara = (BooleanParameter) parameters[0];
        boolean useAutoScaling = autoScalePara.getBoolean();

        // draw all graphs
        Collection<Node> rootNodes = data.getRootNodes();
        for (Node root : rootNodes) {
            initializeAlgorithm(root, data);

            scaling = new CircleScaling(root, data, useAutoScaling, true, 0);

            postorderTraversal(root);
            preorderTraversal(root);

            colorizeEdges(root, overalSumOfEdgeLengths / overalSumOfWeight);
        }
    }

    public void redrawParts(Graph graph, Node tainted, PhyloTreeGraphData data) {
        drawGraph(graph, data);
    }

    public Parameter<?>[] getParameters() {
        BooleanParameter autoScale = new BooleanParameter(true, "Autoscaling",
                "Scales the trees to fit the window size");

        Parameter<?>[] parameters = { autoScale };

        return parameters;
    }

    /**
     * Returns the name of this algorithm.
     * 
     * @return The name of this algorithm.
     * @see PhylogeneticTree
     */
    public String getName() {
        return ALGORITHM_NAME;
    }

    private void initializeAlgorithm(Node root, PhyloTreeGraphData data) {
        this.data = data;
        this.root = root;

        this.coefficients = new HashMap<Node, Double>();
        this.offset = new HashMap<Node, Point2D>();
        this.weighting = new HashMap<Edge, Double>();
        this.coordinatesInUnitCircle = new HashMap<Node, Point2D>();
        this.subtreeLeafCount = new HashMap<Node, Integer>();

        // reset attributes needed for coloring
        this.overalSumOfWeight = 0d;
        this.overalSumOfEdgeLengths = 0d;

        this.numberOfLeaves = getNumberOfLeaves(root);
        if (root.getOutDegree() == 1) {
            this.numberOfLeaves += 1;
        }

        this.i = 0;
    }

    private void postorderTraversal(Node node) {
        prepareNode(node);

        Edge[] sortedOutgoingEdges = OrderedEdges.getOrderedEdges(node);
        for (Edge outgoingEdge : sortedOutgoingEdges) {
            prepareEdge(outgoingEdge);
            clearEdge(outgoingEdge);

            Node child = outgoingEdge.getTarget();
            postorderTraversal(child);
        }

        if (isLeaf(node) || (isRoot(node) && node.getOutDegree() == 1)) {
            coefficients.put(node, 0d);

            // ﬁx vertex on circle
            double temp = (2d * Math.PI * this.i) / this.numberOfLeaves;

            Point2D nodeOffset = new Point2D.Double(Math.cos(temp), Math
                    .sin(temp));
            offset.put(node, nodeOffset);

            ++this.i;
        } else { // node is not a leaf

            // sums up the weights adjacent to an edge
            // no sorting is necessary here
            double nodeWeightSum = 0;
            for (Edge adjacentEdge : node.getEdges()) {
                double nodeWeighting;
                double edgeWeight = getEdgeWeight(adjacentEdge);

                // if v = root(T ) or w = parent(v) then
                if (isRoot(node) || adjacentEdge.getTarget() == node) {
                    nodeWeighting = 1 / edgeWeight;
                } else {
                    int numberOfChildren = node.getOutDegree();
                    nodeWeighting = 1 / (edgeWeight * numberOfChildren);
                }

                weighting.put(adjacentEdge, nodeWeighting);
                nodeWeightSum += nodeWeighting;

                if (Double.isInfinite(nodeWeightSum)) {
                    System.out
                            .println("Circle error: node weight sum is infinite");
                }
            }

            // t←t'←0
            double t = 0;
            Pair<Double, Double> t2 = new Pair<Double, Double>(0d, 0d);

            for (Edge edge : OrderedEdges.getOrderedEdges(node)) {
                Node target = edge.getTarget();
                double normalizedWeight = weighting.get(edge) / nodeWeightSum;
                t += normalizedWeight * coefficients.get(target);
                Point2D dTarget = offset.get(target);
                t2.setFirst(t2.getFirst() + normalizedWeight * dTarget.getX());
                t2
                        .setSecond(t2.getSecond() + normalizedWeight
                                * dTarget.getY());
            }

            if (!isRoot(node)) {
                Edge edgeToParent = getEdgeToParent(node);
                double coefficient = weighting.get(edgeToParent)
                        / (nodeWeightSum * (1 - t));
                coefficients.put(node, coefficient);
            }

            // dv ← t' / 1−t
            double first = t2.getFirst() / (1 - t);
            double second = t2.getSecond() / (1 - t);
            Point2D nodeOffset = new Point2D.Double(first, second);
            offset.put(node, nodeOffset);
        }

    }

    private void preorderTraversal(Node node) {
        if (isRoot(node)) {
            Point2D coords = offset.get(node);
            setCoords(node, coords.getX(), coords.getY());
            coordinatesInUnitCircle.put(node, coords);
        } else {
            Edge edgeToParent = getEdgeToParent(node);
            Node parent = edgeToParent.getSource();

            Point2D parentCoords = coordinatesInUnitCircle.get(parent);
            double nodeCoefficient = coefficients.get(node);

            Point2D nodeOffset = offset.get(node);

            double xCoord = nodeCoefficient * parentCoords.getX()
                    + nodeOffset.getX();
            double yCoord = nodeCoefficient * parentCoords.getY()
                    + nodeOffset.getY();

            Point2D coords = new Point2D.Double(xCoord, yCoord);
            coordinatesInUnitCircle.put(node, coords);

            setCoords(node, xCoord, yCoord);
        }

        for (Edge outgoingEdge : OrderedEdges.getOrderedEdges(node)) {
            Node child = outgoingEdge.getTarget();
            preorderTraversal(child);
        }

        // sum calculation
        if (!isRoot(node)) {
            Edge edgeToParent = getEdgeToParent(node);
            Node parent = edgeToParent.getSource();

            overalSumOfWeight += getEdgeWeight(edgeToParent);
            overalSumOfEdgeLengths += calcDistanceOfTwoNodes(node, parent);
        }
    }

    private double getEdgeWeight(Edge edge) {
        return data.getShiftedEdgeWeight(root, edge);
    }

    private void colorizeEdges(Node node, double sigma) {
        for (Edge edge : node.getAllOutEdges()) {
            Node target = edge.getTarget();

            // calculate f
            double f = calcDistanceOfTwoNodes(node, target)
                    / (sigma * getEdgeWeight(edge));

            // determine color
            Color edgeColor;
            if (f <= 0.5) {
                edgeColor = new Color(0, 0, 1f);
            } else if (f < 1) {
                float yellow = (float) (-Math.log(f) / Math.log(2));
                edgeColor = new Color(0, 0, yellow);
            } else if (f < 2) {
                float red = (float) (Math.log(f) / Math.log(2));
                edgeColor = new Color(red, 0, 0);
            } else {
                edgeColor = new Color(1f, 0, 0);
            }

            GravistoUtil.setEdgeColor(edge, edgeColor);

            colorizeEdges(target, sigma);
        }
    }

    /**
     * Returns the number of leaves of the subtree of a given Node.
     * 
     * @param node
     *            The node whose number of leaves is to be returned.
     * @return The number of leaves of the subtree of the Node given as a
     *         parameter.
     */
    private int getNumberOfLeaves(Node node) {
        // special case to avoid unnecessary caching
        if (isLeaf(node))
            return 1;

        // caching information
        if (!subtreeLeafCount.containsKey(node)) {
            int leaves = 0;
            for (Node child : node.getOutNeighbors()) {
                leaves += getNumberOfLeaves(child);
            }
            subtreeLeafCount.put(node, leaves);
        }

        // using cached information
        return subtreeLeafCount.get(node);
    }

    /**
     * Returns the Edge from the parent to the given Node.
     * 
     * @param node
     *            The Node whose edge to its parent is to be returned. The Node
     *            must not be the root Node of a tree.
     * @return The Edge from the parent Node of the given Node to the given
     *         Node.
     */
    private Edge getEdgeToParent(Node node) {
        assert !isRoot(node) : "node must not be a root Node";

        return node.getAllInEdges().iterator().next();
    }

    /**
     * Calculates the distance of two nodes. Both nodes must be in the
     * coordinatesInUnitCircle Map.
     * 
     * @param n1
     *            The first node.
     * @param n2
     *            The second node.
     * @return The distance between the two given Nodes.
     */
    private double calcDistanceOfTwoNodes(Node n1, Node n2) {
        assert n1 != null;
        assert n2 != null;

        Point2D firstCoords = coordinatesInUnitCircle.get(n1);
        Point2D secondCoords = coordinatesInUnitCircle.get(n2);
        return firstCoords.distance(secondCoords);
    }

    private void setCoords(Node node, double xCoord, double yCoord) {
        this.scaling.setCoord(node, xCoord, yCoord);
    }

    private void clearEdge(Edge e) {
        resetEdgeBends(e);
    }
}