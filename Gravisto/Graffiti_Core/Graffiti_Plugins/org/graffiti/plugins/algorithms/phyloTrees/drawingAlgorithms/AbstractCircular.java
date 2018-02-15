package org.graffiti.plugins.algorithms.phyloTrees.drawingAlgorithms;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.phyloTrees.exceptions.UnknownNodePlacementException;
import org.graffiti.plugins.algorithms.phyloTrees.utility.CircleScaling;
import org.graffiti.plugins.algorithms.phyloTrees.utility.GravistoUtil;
import org.graffiti.plugins.algorithms.phyloTrees.utility.OrderedEdges;
import org.graffiti.plugins.algorithms.phyloTrees.utility.PhyloTreeConstants;
import org.graffiti.plugins.algorithms.phyloTrees.utility.PhyloTreeGraphData;

public abstract class AbstractCircular extends AbstractGramAlgorithm {
    /**
     * The object containing the information about the current graph and
     * drawing.
     */
    private PhyloTreeGraphData data;

    /** The root Node of the tree, that is currently being drawn. */
    private Node root;

    /** The angles of the nodes. */
    private HashMap<Node, Double> nodeAngles;

    /** The radii of the nodes in the unit circle. */
    private HashMap<Node, Double> nodeRadius;

    private double weightNorm;
    private CircleScaling scaling;

    private String straightEdge = "Straight";
    private String rectangularEdge = "Rectangular";

    private String edgeType;

    private String nodePlacement;

    public Parameter<?>[] getParameters() {
        BooleanParameter autoScale = new BooleanParameter(true, "Autoscaling",
                "Scales the trees to fit the window size");

        String[] edges = { straightEdge, rectangularEdge };
        StringSelectionParameter edgeType = new StringSelectionParameter(edges,
                "Edge type", "The shape of the edge.");

        StringSelectionParameter nodePlacement = new StringSelectionParameter(
                PhyloTreeConstants.NODE_PLACEMENTS, "Node Placement",
                "The placement of inner nodes");

        BooleanParameter spacingParameter = new BooleanParameter(true,
                "Spacing", "Use spacing between last and first leaf");

        Parameter<?>[] parameters = { autoScale, edgeType, nodePlacement,
                spacingParameter };
        return parameters;
    }

    public void redrawParts(Graph graph, Node tainted, PhyloTreeGraphData data) {
        drawGraph(graph, data);
    }

    public void drawGraph(Graph graph, PhyloTreeGraphData data) {
        // read parameters
        Parameter<?>[] parameters = data.getAlgorithmParameters();

        BooleanParameter autoScalePara = (BooleanParameter) parameters[0];
        boolean useAutoScaling = autoScalePara.getBoolean();

        StringSelectionParameter stringSelPara = (StringSelectionParameter) parameters[1];
        this.edgeType = stringSelPara.getSelectedValue();

        StringSelectionParameter nodePlacementPara = (StringSelectionParameter) parameters[2];
        this.nodePlacement = nodePlacementPara.getSelectedValue();

        BooleanParameter spacingPara = (BooleanParameter) parameters[3];
        boolean useSpacing = spacingPara.getBoolean();

        // draw all graphs
        Collection<Node> rootNodes = data.getRootNodes();
        for (Node root : rootNodes) {
            initializeAlgorithm(root, data);

            int numberOfLeaves = data.getLeafCount(root);

            double angle;
            double startingAngle;
            if (useSpacing) {
                angle = (Math.PI * 2 - PhyloTreeConstants.CIRCULAR_SPACING)
                        / numberOfLeaves;
                startingAngle = angle / 2 + PhyloTreeConstants.CIRCULAR_SPACING
                        / 2;
            } else {
                angle = (Math.PI * 2) / numberOfLeaves;
                startingAngle = angle / 2;
            }

            setLeafAngles(root, angle, startingAngle);
            setWidth(root, 0);

            double wedge = useSpacing ? PhyloTreeConstants.CIRCULAR_SPACING : 0;
            this.scaling = new CircleScaling(root, data, useAutoScaling, true,
                    wedge);

            try {
                setCoordinates(root);
            } catch (UnknownNodePlacementException e) {
                e.printStackTrace();
            }
        }
    }

    private void initializeAlgorithm(Node root, PhyloTreeGraphData data) {
        this.root = root;
        this.data = data;

        this.nodeAngles = new HashMap<Node, Double>();
        this.nodeRadius = new HashMap<Node, Double>();

        this.weightNorm = 1 / data.getMaxTreePathLength(root);
    }

    private double setLeafAngles(Node node, double angle, double startingAngle) {
        node.getAllOutEdges();
        Edge[] sortedOutEdges = OrderedEdges.getOrderedEdges(node);

        for (Edge edge : sortedOutEdges) {
            prepareEdge(edge);

            startingAngle = setLeafAngles(edge.getTarget(), angle,
                    startingAngle);
        }

        if (isLeaf(node)) {
            this.nodeAngles.put(node, startingAngle);
            startingAngle += angle;
        }

        return startingAngle;
    }

    @Override
    public void setEdge(Edge edge) {
        if (edgeType.equals(straightEdge)) {
            resetEdgeBends(edge);
        } else {
            Point2D circleCenter = getCenter();

            if (edge
                    .containsAttribute(GraphicAttributeConstants.CIRCLE_CENTER_PATH)) {
                CoordinateAttribute ca = (CoordinateAttribute) edge
                        .getAttribute(GraphicAttributeConstants.CIRCLE_CENTER_PATH);
                ca.setCoordinate(circleCenter);
            } else {
                CoordinateAttribute ca = new CoordinateAttribute(
                        GraphicAttributeConstants.CIRCLE_CENTER, circleCenter);
                edge.addAttribute(ca, GraphicAttributeConstants.GRAPHICS);
            }

            GravistoUtil
                    .setEdgeShape(
                            edge,
                            GraphicAttributeConstants.CIRCLE_LINE_SEGMENTATION_CLASSNAME);
        }
    }

    @Override
    public void setCoords(Node node, double radius, double angle) {
        assert radius <= 1 : "radius must not be larger than 1 (is " + radius
                + ")";

        while (angle < 0) {
            angle += 2 * Math.PI;
        }

        if (angle >= 2 * Math.PI) {
            angle = angle % (2 * Math.PI);
        }

        double x = Math.sin(angle) * radius;
        double y = -Math.cos(angle) * radius;

        this.scaling.setCoord(node, x, y);
    }

    @Override
    public double getNormalizedWeight(Edge edge) {
        return weightNorm * data.getShiftedEdgeWeight(root, edge);
    }

    @Override
    public double getHeight(Node node) {
        return nodeAngles.get(node);
    }

    @Override
    public double getWidth(Node node) {
        return nodeRadius.get(node);
    }

    @Override
    public void setHeight(Node node, double height) {
        nodeAngles.put(node, height);
    }

    @Override
    public void setWidth(Node node, double width) {
        nodeRadius.put(node, width);
    }

    @Override
    public String getNodePlacement() {
        return this.nodePlacement;
    }

    /**
     * Returns the center of the tree that is currently being drawn.
     * 
     * @return The center of the tree currently being drawn.
     */
    protected Point2D getCenter() {
        return scaling.getCenter();
    }
}
