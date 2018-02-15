package org.graffiti.plugins.algorithms.phyloTrees.drawingAlgorithms;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.phyloTrees.PhylogeneticTree;
import org.graffiti.plugins.algorithms.phyloTrees.utility.EdgeCounterclockwiseComparator;
import org.graffiti.plugins.algorithms.phyloTrees.utility.GravistoUtil;
import org.graffiti.plugins.algorithms.phyloTrees.utility.OrderedEdges;
import org.graffiti.plugins.algorithms.phyloTrees.utility.PhyloTreeGraphData;
import org.graffiti.plugins.algorithms.phyloTrees.utility.PhyloTreeUtil;

public class Radial extends AbstractTree {

    /** The name of this algorithm. */
    public static final String ALGORITHM_NAME = "Radial";

    /** The maximum spreading buffer in pixel. */
    private static final double MAX_SPREADING_BUFFER = 5;

    /** The actual spreading buffer used in pixel */
    private double spreadingBuffer;

    /**
     * The object containing the information about the current graph and
     * drawing.
     */
    private PhyloTreeGraphData data;

    /** The root Node of the tree, that is currently being drawn. */
    private Node root;

    private double scalingFactor;

    private Map<Node, Double> wedgeSizes;

    /** Map caching the number of leafs in a subtree. */
    private Map<Node, Integer> subtreeLeafCount;

    /**
     * The value to which the tree's edge weights are to be normalized to.
     */
    private int edgeWeightNormalizationValue;

    /**
     * Indicates whether spreading is to be used for the drawing of the tree.
     */
    private boolean spreadingActivated;

    /** Indicates whether to use a central root for spreading. */
    private Boolean useCenterRoot;

    /** The root node used prior to drawing. */
    private Node oldRoot;

    /** The upper bound for drawing of this tree */
    private double upperBound;

    private double horizontalMove = 0;

    /**
     * The Node with the minimal x value after drawing.
     */
    private double minY;

    /**
     * The maximum y value after drawing.
     */
    private double maxY;

    public void drawGraph(Graph graph, PhyloTreeGraphData data) {
        radialLayout(graph, data);
    }

    public void redrawParts(Graph graph, Node tainted, PhyloTreeGraphData data) {
        radialLayout(graph, data);
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

    /**
     * Returns an array of parameters that are necessary for this algorithm.
     * 
     * @return An array of {@link Parameter} objects.
     */
    public Parameter<?>[] getParameters() {
        BooleanParameter spreadParameter = new BooleanParameter(false,
                "Spread tree", "Use spreading algorithm to avoid intersections");

        BooleanParameter rerootParameter = new BooleanParameter(false,
                "Reroot Tree", "Use the center of the Tree as the root.");

        IntegerParameter scalingParamter = new IntegerParameter(50, 1, 300,
                "Scaling Factor", "The scale factor of the drawing");

        Parameter<?>[] parameters = { spreadParameter, rerootParameter,
                scalingParamter };
        return parameters;
    }

    /**
     * Sets the Attributes as indicated by the parameters. PAra
     */
    private void setParameters(Parameter<?>[] parameters) {
        assert parameters != null : "wrong parameters are set";
        assert parameters[0] instanceof BooleanParameter : "first parameter is not of type BooleanParameter";
        assert parameters[1] instanceof BooleanParameter;
        assert parameters[2] instanceof IntegerParameter;

        BooleanParameter bp = (BooleanParameter) parameters[0];
        spreadingActivated = bp.getBoolean();

        BooleanParameter centerRoot = (BooleanParameter) parameters[1];
        useCenterRoot = centerRoot.getBoolean();

        IntegerParameter ip = (IntegerParameter) parameters[2];
        edgeWeightNormalizationValue = ip.getInteger();
    }

    private void radialLayout(Graph g, PhyloTreeGraphData data) {
        Collection<Node> rootNodes = data.getRootNodes();

        for (Node root : rootNodes) {
            initializeAlgorithm(data, root);

            preorderTraversal(this.root, null, this.root, 0, Math.PI * 2);

            if (spreadingActivated) {
                spreadingPostprocessing(this.root);
            }

            postprocessing(this.root, data);
        }
    }

    /**
     * Returns a node with a central position in the tree.
     * 
     * @param root
     *            The root node of a tree.
     * @return A node with a central position in the tree.
     */
    private Node getCentralNode(Node root) {
        Node center = root;
        int treeLeafCount = getNumberOfLeaves(root);

        boolean finalCenter = false;
        do {
            Collection<Node> subtreeRootNodes = center.getOutNeighbors();
            finalCenter = true;

            for (Node node : subtreeRootNodes) {
                int subtreeLeafCount = getNumberOfLeaves(node);
                if (subtreeLeafCount > (treeLeafCount - subtreeLeafCount)) {
                    center = node;
                    finalCenter = false;
                }
            }
        } while (!finalCenter);

        return center;
    }

    private void initializeAlgorithm(PhyloTreeGraphData data, Node root) {
        setParameters(data.getAlgorithmParameters());

        this.data = data;

        if (useCenterRoot) {
            this.oldRoot = root;
            subtreeLeafCount = new HashMap<Node, Integer>(data
                    .getNodeCount(root));
            root = getCentralNode(root);
            PhyloTreeUtil.rerootTree(root);

            data.replaceRootNode(oldRoot, root);
        }

        this.root = root;

        wedgeSizes = new HashMap<Node, Double>();
        subtreeLeafCount = new HashMap<Node, Integer>(data.getNodeCount(root));
        spreadingBuffer = MAX_SPREADING_BUFFER;

        Rectangle2D visibleArea = GravistoUtil.getVisibleAreaBounds();
        upperBound = data.getUpperBound(this.root);

        double rootX = visibleArea.getCenterX();
        GravistoUtil.setCoords(root, 100, 0);
        horizontalMove = rootX;

        minY = 0;
        maxY = 0;

        scalingFactor = getAppropriateScalingFactor(this.root, data);
    }

    private void postprocessing(Node root, PhyloTreeGraphData data) {
        // move tree if it is crossing its boundaries
        double difference = 0;
        double verticalSpaceNeeded = maxY - minY;
        if (minY < upperBound) {
            difference = upperBound - minY;

            move(root, horizontalMove, difference);

            LinkedList<Node> stack = new LinkedList<Node>(root
                    .getOutNeighbors());

            while (!stack.isEmpty()) {
                Node n = stack.pollLast();
                move(n, horizontalMove, difference);
                stack.addAll(n.getAllOutNeighbors());
            }
        }

        // set space needed
        data.setVerticalSpace(root, verticalSpaceNeeded);

        if (useCenterRoot) {
            PhyloTreeUtil.rerootTree(oldRoot);
            this.data.replaceRootNode(root, oldRoot);
            this.root = oldRoot;
            this.oldRoot = null;
        }
    }

    private void preorderTraversal(Node node, Node parentNode, Node root,
            double angle, double wedgeSize) {
        prepareNode(node);

        // only save wedge sizes if needed later on
        if (spreadingActivated) {
            wedgeSizes.put(node, wedgeSize);
        }

        if (node != root) {
            Edge edgeToParent = PhyloTreeUtil.getEdgeToParent(node);
            double weight = getEdgeWeight(edgeToParent);
            Point2D parentCoords = GravistoUtil.getCoords(parentNode);

            double para = angle + wedgeSize / 2;

            double xCoord = parentCoords.getX() + weight * Math.cos(para);
            double yCoord = parentCoords.getY() + weight * Math.sin(para);

            setCoords(node, xCoord, yCoord);
        }

        double angles = angle;

        for (Edge outgoingEdge : OrderedEdges.getOrderedEdges(node)) {
            prepareEdge(outgoingEdge);
            clearEdge(outgoingEdge);

            Node child = outgoingEdge.getTarget();
            double childWedgeSize = (getNumberOfLeaves(child) / (double) getNumberOfLeaves(root))
                    * 2 * Math.PI;
            double childAngle = angles;
            angles += childWedgeSize;
            preorderTraversal(child, node, root, childAngle, childWedgeSize);

            // update spreading buffer for spreading
            if (spreadingActivated) {
                double curEdgeMinSpreading = getEdgeLength(outgoingEdge) / 2;
                spreadingBuffer = Math
                        .min(spreadingBuffer, curEdgeMinSpreading);
            }
        }

        if (isLeaf(node)) {
            rotateLabel(node, parentNode);
        }
    }

    private void spreadingPostprocessing(Node root) {
        for (Edge edgeToChild : root.getAllOutEdges()) {
            spreadingPostorderTraversal(edgeToChild);
        }
    }

    private void spreadingPostorderTraversal(Edge edgeFromParent) {
        Node node = edgeFromParent.getTarget();
        int outDegree = node.getOutDegree(); // k
        int middle = (int) Math.ceil(outDegree / 2.0);

        // order children counterclockwise
        Edge[] edgesToChildren = new Edge[node.getOutDegree()];
        node.getAllOutEdges().toArray(edgesToChildren);
        Arrays.sort(edgesToChildren, new EdgeCounterclockwiseComparator(
                edgeFromParent));

        for (int i = 0; i < edgesToChildren.length; ++i) {
            spreadingPostorderTraversal(edgesToChildren[i]);
            // e_i = edgesToChildren[i]
        }

        if (outDegree >= 2) {
            Node parent = edgeFromParent.getSource(); // = u
            // e = edgeFromParent
            double edgeLength = getEdgeLength(edgeFromParent);
            double distNodeEdge = Math.sin(wedgeSizes.get(node) / 2)
                    * edgeLength - spreadingBuffer; // = d

            Point2D nodeCoords = GravistoUtil.getCoords(node);
            Point2D parentCoords = GravistoUtil.getCoords(parent);

            double alpha = Math.acos((nodeCoords.getX() - parentCoords.getX())
                    / edgeLength);

            if (nodeCoords.getY() > parentCoords.getY()) {
                alpha = 2 * Math.PI - alpha;
            }

            // find leaves of the two subtrees
            Node[] children1 = new Node[Math.max(1, middle - 1)];
            for (int i = 0; i < children1.length; ++i) {
                children1[i] = edgesToChildren[i].getTarget();
            }

            int sizeOfSecondSubtree = outDegree - middle;
            Node[] children2 = new Node[sizeOfSecondSubtree];
            for (int i = 0; i < children2.length; ++i) {
                int edgesToChildrenIndex = edgesToChildren.length - i - 1;
                Edge edgeToChild = edgesToChildren[edgesToChildrenIndex];
                int children2Index = children2.length - i - 1;
                children2[children2Index] = edgeToChild.getTarget();
            }

            // calculate rotation angles
            double smallestAdaptedAngle = getAngleOfFirstSubtree(node,
                    distNodeEdge, nodeCoords, alpha, children1); // orig

            double largestAdaptedAngle = getAngleOfLastSubtree(node,
                    distNodeEdge, nodeCoords, alpha, children2); // orig

            // rotate subtrees
            if (outDegree == 2) {
                rotateSubtree(nodeCoords, edgesToChildren[0].getTarget(),
                        smallestAdaptedAngle);
                rotateSubtree(nodeCoords, edgesToChildren[1].getTarget(),
                        -largestAdaptedAngle);
            } else
            // added
            {
                for (int i = 0; i < children1.length; ++i) {
                    double angle = smallestAdaptedAngle
                            * ((middle - (i + 1)) / (double) (middle - 1));
                    rotateSubtree(nodeCoords, children1[i], angle);
                }

                for (int i = middle + 1; i <= outDegree; ++i) {
                    double angle = largestAdaptedAngle
                            * ((middle - i) / (double) (outDegree - middle));
                    rotateSubtree(nodeCoords, edgesToChildren[i - 1]
                            .getTarget(), angle);
                }

            }
        }
    }

    private double getAngleOfFirstSubtree(Node node, double distNodeEdge,
            Point2D nodeCoords, double alpha, Node[] nodes) {
        double smallestAdaptedAngle = Double.MAX_VALUE;

        for (Node leaf : getLeavesOfSubtrees(nodes)) {
            double beta = Math.PI / 2 - (wedgeSizes.get(node) / 2);

            Point2D leafCoords = getLeafCoords(leaf);

            double distanceNodeToLeaf = nodeCoords.distance(leafCoords);
            if (distanceNodeToLeaf > distNodeEdge) {
                beta += Math.acos(distNodeEdge / distanceNodeToLeaf);
            }

            double gamma2 = alpha
                    + Math.PI
                    + Math.max(beta, Math.asin(spreadingBuffer
                            / distanceNodeToLeaf));

            double horizontalDistance = leafCoords.getX() - nodeCoords.getX();
            double gamma = Math.acos(horizontalDistance / distanceNodeToLeaf);

            if (leafCoords.getY() > nodeCoords.getY()) {
                gamma = 2 * Math.PI - gamma;
            }

            double f = (gamma - gamma2);
            if (f < 0) {
                f += 2 * Math.PI;
            }
            smallestAdaptedAngle = Math.min(f, smallestAdaptedAngle);

            if (smallestAdaptedAngle < 0) {
                smallestAdaptedAngle = 0;
            }
        }

        return smallestAdaptedAngle;
    }

    private double getAngleOfLastSubtree(Node node, double distNodeEdge,
            Point2D nodeCoords, double alpha, Node[] nodes) {
        double largestAdaptedAngle = Double.MAX_VALUE;

        for (Node leaf : getLeavesOfSubtrees(nodes)) {
            double beta = Math.PI / 2 - (wedgeSizes.get(node) / 2);

            Point2D leafCoords = getLeafCoords(leaf);

            double distanceNodeToLeaf = nodeCoords.distance(leafCoords);
            if (distanceNodeToLeaf > distNodeEdge) {
                beta += Math.acos(distNodeEdge / distanceNodeToLeaf);
            }

            double gamma2 = alpha
                    - Math.PI
                    - Math.max(beta, Math.asin(spreadingBuffer
                            / distanceNodeToLeaf));

            double horizontalDistance = leafCoords.getX() - nodeCoords.getX();
            double gamma = Math.acos(horizontalDistance / distanceNodeToLeaf);

            if (leafCoords.getY() > nodeCoords.getY()) {
                gamma = 2 * Math.PI - gamma;
            }

            double foo = (gamma2 - gamma);
            if (foo < 0) {
                foo += 2 * Math.PI;
            }
            largestAdaptedAngle = Math.min(foo, largestAdaptedAngle);

            if (largestAdaptedAngle < 0) {
                largestAdaptedAngle = 0;
            }
        }
        return largestAdaptedAngle;
    }

    /**
     * Returns the coordinates of a leaf or the coordinates of external end of
     * the label.
     * 
     * @param leaf
     *            The leaf.
     * @return The coordinates of the leaf node or its label.
     */
    private Point2D getLeafCoords(Node leaf) {
        Point2D leafCoords = GravistoUtil.getCoords(leaf);
        // add label size if a label exists
        if (leaf.containsAttribute(GraphicAttributeConstants.LABEL)) {
            Point2D parentCoords = GravistoUtil.getCoords(getParent(leaf));
            double leafLabelLength = GravistoUtil.getLabelAttribute(leaf)
                    .getWidth()
                    + GraphicAttributeConstants.LABEL_DISTANCE;
            double newLength = parentCoords.distance(leafCoords)
                    + leafLabelLength;
            leafCoords = PhyloTreeUtil.scaleTo(parentCoords, leafCoords,
                    newLength);
        }
        return leafCoords;
    }

    /**
     * Rotates the subtree of node around the rotationCenter.
     * 
     * @param rotationCenter
     *            The center of rotation.
     * @param node
     *            The Node whose subtree is to be rotated.
     * @param rotationAngle
     *            The angle of the rotation around the center.
     */
    private void rotateSubtree(Point2D rotationCenter, Node node,
            double rotationAngle) {
        Point2D nodeCoords = GravistoUtil.getCoords(node);

        double distCenterToNode = rotationCenter.distance(nodeCoords);
        double distHorizNodeToCenter = nodeCoords.getX()
                - rotationCenter.getX();

        double currentAngle = Math.acos(distHorizNodeToCenter
                / distCenterToNode);

        if (nodeCoords.getY() > rotationCenter.getY()) {
            currentAngle = 2 * Math.PI - currentAngle;
        }

        double newAngle = currentAngle - rotationAngle;

        double newXCoord = rotationCenter.getX() + Math.cos(newAngle)
                * distCenterToNode;
        double newYCoord = rotationCenter.getY() - Math.sin(newAngle)
                * distCenterToNode;

        setCoords(node, newXCoord, newYCoord);

        // rotate label
        if (isLeaf(node)) {
            Node parent = node.getAllInNeighbors().iterator().next();
            this.rotateLabel(node, parent);
        }

        for (Node child : node.getAllOutNeighbors()) {
            rotateSubtree(rotationCenter, child, rotationAngle);
        }
    }

    /**
     * Moves the given Node by a given distance.
     * 
     * @param node
     *            The Node to move.
     * @param horizontal
     *            The distance, the given Node is to be moved horizontally.
     * @param vertical
     *            The distance, the given Node is to be moved vertically.
     */
    private void move(Node node, double horizontal, double vertical) {
        Point2D coords = GravistoUtil.getCoords(node);
        coords
                .setLocation(coords.getX() + horizontal, coords.getY()
                        + vertical);
        GravistoUtil.setCoords(node, coords);
    }

    private void setCoords(Node node, double xCoord, double yCoord) {
        double extremeYcandidate = yCoord;
        if (isLeaf(node)
                && node.containsAttribute(GraphicAttributeConstants.LABEL)) {
            LabelAttribute attr = (LabelAttribute) node
                    .getAttribute(GraphicAttributeConstants.LABEL);
            double labelWidth = attr.getWidth()
                    + GraphicAttributeConstants.LABEL_DISTANCE;
            extremeYcandidate += (extremeYcandidate > 0) ? labelWidth
                    : -labelWidth;
        }

        maxY = Math.max(maxY, extremeYcandidate);
        minY = Math.min(minY, extremeYcandidate);

        GravistoUtil.setCoords(node, xCoord, yCoord);
    }

    private List<Node> getLeavesOfSubtrees(Node[] nodes) {
        List<Node> leaves = new LinkedList<Node>();
        Stack<Node> nodeStack = new Stack<Node>();

        // add the children of nodes to the stack
        for (Node node : nodes) {
            if (isLeaf(node)) {
                leaves.add(node);
            } else {
                nodeStack.add(node);
            }
        }

        // empty the stack and add leaves to the leaves-list
        while (!nodeStack.isEmpty()) {
            Node node = nodeStack.pop();

            for (Node child : node.getOutNeighbors()) {
                if (isLeaf(child)) {
                    leaves.add(child);
                } else {
                    nodeStack.add(child);
                }
            }
        }

        return leaves;
    }

    /**
     * Returns the weight of an edge, adjusted for this drawing algorithm.
     * 
     * @param edge
     *            The Edge whose weight is to be returned.
     * @return The weight of the given edge after preprocessing and scaling.
     */
    private double getEdgeWeight(Edge edge) {
        return data.getShiftedEdgeWeight(root, edge) * this.scalingFactor;
    }

    /**
     * Returns the length of the edge given as a parameter. Includes the length
     * of a leaf's label, if it exists.
     * 
     * @param edge
     *            The edge for which the length is to be returned.
     * @return The edge length including its label length.
     */
    private double getEdgeLength(Edge edge) {
        Point2D sourceCoords = GravistoUtil.getCoords(edge.getSource());

        Node target = edge.getTarget();
        Point2D targetCoords = GravistoUtil.getCoords(target);

        double length = sourceCoords.distance(targetCoords);

        // add label length if it exists
        if (isLeaf(target)) {

            if (target.containsAttribute(GraphicAttributeConstants.LABEL)) {
                LabelAttribute label = (LabelAttribute) target
                        .getAttribute(GraphicAttributeConstants.LABEL);

                length += label.getWidth();
                length += GraphicAttributeConstants.LABEL_DISTANCE;
            }
        }
        return length;
    }

    private double getAppropriateScalingFactor(Node root,
            PhyloTreeGraphData data) {
        scalingFactor = edgeWeightNormalizationValue
                / data.getAverageEdgeWeight(root);
        return scalingFactor;
    }

    /**
     * Rotates the label of a given node thus, that it faces away from its
     * parent node.
     * 
     * @param node
     *            The Node whose label is to be rotated.
     * @param parentNode
     *            The parent node of the node that is to be rotated.
     */
    private void rotateLabel(Node node, Node parentNode) {
        if (node.containsAttribute(GraphicAttributeConstants.LABEL)) {
            PhyloTreeUtil.setLabelRotation(node, GravistoUtil
                    .getCoords(parentNode));
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
     * Resets the edge to a straight line.
     * 
     * @param e
     *            The Edge whose bends are to be deleted.
     */
    private void clearEdge(Edge e) {
        resetEdgeBends(e);
    }

    @Override
    protected void prepareNode(Node node) {
        if (useCenterRoot) {
            if (node == oldRoot) {
                prepareRootNode(node);
            } else if (!isLeaf(node)) {
                prepareInnerNode(node);
            } else {
                prepareLeafNode(node);
            }
        } else {
            super.prepareNode(node);
        }
    }

}
