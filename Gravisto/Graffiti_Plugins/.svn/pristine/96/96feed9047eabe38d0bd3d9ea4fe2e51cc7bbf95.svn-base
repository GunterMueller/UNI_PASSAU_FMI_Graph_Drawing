package org.graffiti.plugins.algorithms.phyloTrees.drawingAlgorithms;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.DoubleParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.phyloTrees.exceptions.UnknownNodePlacementException;
import org.graffiti.plugins.algorithms.phyloTrees.utility.GravistoUtil;
import org.graffiti.plugins.algorithms.phyloTrees.utility.OrderedEdges;
import org.graffiti.plugins.algorithms.phyloTrees.utility.PhyloTreeConstants;
import org.graffiti.plugins.algorithms.phyloTrees.utility.PhyloTreeGraphData;

public abstract class AbstractGramTree extends AbstractGramAlgorithm {
    /**
     * The object containing the information about the current graph and
     * drawing.
     */
    private PhyloTreeGraphData data;

    private Node root;

    private double weightNorm;

    private Map<Node, Double> widthCache;

    private Map<Node, Double> heightCache;

    /**
     * The vertical distance of two successive Nodes.
     */
    private double verticalNodeDistance;

    private String straight = "Straight";
    private String rectangular = "Rectangular";
    private String swoopogram = "Swoopogram";
    private String curvogram = "Curvogram";
    private String eurogram = "Eurogram";

    /**
     * The edge type to be used.
     */
    private String edgeType;

    /**
     * Indicates whether to use auto scaling.
     */
    private Boolean autoScaling;

    private double scaleFactor;

    private String nodePlacement;

    public Parameter<?>[] getParameters() {
        String[] edges = { straight, rectangular, swoopogram, curvogram,
                eurogram };
        StringSelectionParameter edgeType = new StringSelectionParameter(edges,
                "Edge type", "The shape of the edge.");

        BooleanParameter autoScaling = new BooleanParameter(true,
                "Use auto scaling",
                "Automatically scale the tree to the visible space");

        DoubleParameter manualScalingFactor = new DoubleParameter(25d,
                "Scaling Factor", "?", 1d, 250d, 0.2, 2000d);
        manualScalingFactor.setDependency(autoScaling, Boolean.FALSE);

        StringSelectionParameter nodePlacement = new StringSelectionParameter(
                PhyloTreeConstants.NODE_PLACEMENTS, "Node Placement",
                "The placement of inner nodes");

        Parameter<?>[] parameters = { edgeType, autoScaling,
                manualScalingFactor, nodePlacement };
        return parameters;
    }

    /**
     * {@inheritDoc}
     */
    public void drawGraph(Graph graph, PhyloTreeGraphData data) {
        assert data != null;
        assert graph.isDirected();

        this.data = data;

        // use parameters

        Parameter<?>[] parameters = data.getAlgorithmParameters();

        StringSelectionParameter stringSelPara = (StringSelectionParameter) parameters[0];
        this.edgeType = stringSelPara.getSelectedValue();

        BooleanParameter autoScalePara = (BooleanParameter) parameters[1];
        this.autoScaling = autoScalePara.getBoolean();

        DoubleParameter scaleFactorPara = (DoubleParameter) parameters[2];
        this.scaleFactor = scaleFactorPara.getDouble();

        StringSelectionParameter nodePlacementPara = (StringSelectionParameter) parameters[3];
        this.nodePlacement = nodePlacementPara.getSelectedValue();

        // draw graph
        for (Node root : data.getRootNodes()) {
            this.root = root;
            this.weightNorm = 1 / data.getMaxTreePathLength(root);

            this.heightCache = new HashMap<Node, Double>();
            this.widthCache = new HashMap<Node, Double>();

            this.verticalNodeDistance = Math.max(
                    data.getMaxLabelHeight(root) + 3,
                    PhyloTreeConstants.MIN_VERTICAL_NODE_DISTANCE);

            setXCoords(root, 0);

            double nextYCoord = data.getUpperBound(root)
                    + PhyloTreeConstants.TOP_PANEL_BUFFER;
            nextYCoord = setLeavesYCoords(root, nextYCoord);

            data.setVerticalSpace(root, nextYCoord - data.getUpperBound(root));

            try {
                setCoordinates(root);
            } catch (UnknownNodePlacementException e) {
                e.printStackTrace();
            }
        }
    }

    public void redrawParts(Graph graph, Node tainted, PhyloTreeGraphData data) {
        // TODO
        drawGraph(graph, data);
    }

    protected void setXCoords(Node n, double x) {
        setWidth(n, x);

        // CoordinateAttribute ca =
        // (CoordinateAttribute)n.getAttribute(GraphicAttributeConstants.COORD_PATH);
        // ca.setX(x);
    }

    protected void setYCoords(Node n, double y) {
        setHeight(n, y);

        // CoordinateAttribute ca =
        // (CoordinateAttribute)n.getAttribute(GraphicAttributeConstants.COORD_PATH);
        // ca.setY(y);
    }

    @Override
    public double getHeight(Node node) {
        return heightCache.get(node);
    }

    @Override
    public double getNormalizedWeight(Edge edge) {
        return weightNorm * data.getShiftedEdgeWeight(root, edge);
    }

    @Override
    public double getWidth(Node node) {
        return widthCache.get(node);
    }

    @Override
    public void setCoords(Node node, double width, double height) {
        Rectangle2D r2d = GravistoUtil.getVisibleAreaBounds();

        double xCoord;

        if (autoScaling) {
            double maxLabelWidth = data.getMaxLabelWidth(root);
            if (maxLabelWidth > 0) {
                maxLabelWidth += GraphicAttributeConstants.LABEL_DISTANCE;
            }

            double drawableWidth = r2d.getWidth() - maxLabelWidth
                    - PhyloTreeConstants.LEFT_PANEL_BUFFER
                    - PhyloTreeConstants.RIGHT_PANEL_BUFFER;

            xCoord = width * drawableWidth
                    + PhyloTreeConstants.LEFT_PANEL_BUFFER;
        } else {
            xCoord = ((width * data.getMaxTreePathLength(root)) / data
                    .getAverageEdgeWeight(root))
                    * scaleFactor + PhyloTreeConstants.LEFT_PANEL_BUFFER;
        }

        GravistoUtil.setCoords(node, xCoord, height);
    }

    @Override
    public void setHeight(Node node, double height) {
        heightCache.put(node, height);
    }

    @Override
    public void setWidth(Node node, double width) {
        widthCache.put(node, width);
    }

    @Override
    public String getNodePlacement() {
        return this.nodePlacement;
    }

    @Override
    public void setEdge(Edge edge) {
        if (edgeType.equals(straight)) {
            resetEdgeBends(edge);
        } else if (edgeType.equals(rectangular) || edgeType.equals(swoopogram)) {
            // get Coordinates of Nodes
            Node source = edge.getSource();
            double xCoord = source
                    .getDouble(GraphicAttributeConstants.COORDX_PATH);

            Node target = edge.getTarget();
            double yCoord = target
                    .getDouble(GraphicAttributeConstants.COORDY_PATH);

            if (edgeType.equals(rectangular)) {
                setRectangularEdge(edge, xCoord, yCoord);
            } else {
                setCurvogramEdge(edge, xCoord, yCoord);
            }

        } else if (edgeType.equals(curvogram)) {
            setSwoopogramEdge(edge);
        } else if (edgeType.equals(eurogram)) {
            setEurogramEdge(edge);
        }
    }

    /**
     * TODO
     * 
     * @param n
     *            The Node whose Leaves y-coordinates are to be set.
     * @param startingPosition
     *            The y-coordinate of the next leaf node.
     * @return The next Node's y-coordinates.
     */
    private double setLeavesYCoords(Node n, double startingPosition) {
        double nextStartingPosition = startingPosition;

        if (isLeaf(n)) {
            // set label to the right of the node
            if (n.containsAttribute(LABEL_X_ALIGNMENT_PATH)) {
                n.changeString(LABEL_X_ALIGNMENT_PATH,
                        GraphicAttributeConstants.RIGHT_OUTSIDE);
            }

            resetLabelRotation(n);

            // set coordinates
            setYCoords(n, startingPosition);
            nextStartingPosition = startingPosition + verticalNodeDistance;
        }
        // is inner node
        else {
            Edge[] sortedOutEdges = OrderedEdges.getOrderedEdges(n);
            for (Edge outgoingEdge : sortedOutEdges) {
                prepareEdge(outgoingEdge);

                Node child = outgoingEdge.getTarget();
                nextStartingPosition = setLeavesYCoords(child,
                        nextStartingPosition);
            }
        }

        return nextStartingPosition;
    }

    private void setRectangularEdge(Edge edge, double xCoord, double yCoord) {
        // set edge type
        edge.changeString(GraphicAttributeConstants.SHAPE_PATH,
                GraphicAttributeConstants.POLYLINE_CLASSNAME);

        // set bend
        EdgeGraphicAttribute ega = (EdgeGraphicAttribute) edge
                .getAttribute(GraphicAttributeConstants.GRAPHICS);
        String bendsId = ega.getBends().getId();
        SortedCollectionAttribute bends = new LinkedHashMapAttribute(bendsId);

        String newId = GraphicAttributeConstants.BEND + "PHYL0";
        Attribute newBend = new CoordinateAttribute(newId, xCoord, yCoord);

        bends.add(newBend);
        ega.setBends(bends);
    }

    private void setCurvogramEdge(Edge edge, double xCoord, double yCoord) {
        // set edge type
        edge.changeString(GraphicAttributeConstants.SHAPE_PATH,
                GraphicAttributeConstants.SMOOTH_CLASSNAME);

        // set bend
        EdgeGraphicAttribute ega = (EdgeGraphicAttribute) edge
                .getAttribute(GraphicAttributeConstants.GRAPHICS);
        String bendsId = ega.getBends().getId();
        SortedCollectionAttribute bends = new LinkedHashMapAttribute(bendsId);

        String newId = GraphicAttributeConstants.BEND + "PHYL0";
        Attribute newBend = new CoordinateAttribute(newId, xCoord, yCoord);

        bends.add(newBend);
        ega.setBends(bends);
    }

    private void setEurogramEdge(Edge edge) {
        // get Coordinates of Nodes
        Node source = edge.getSource();
        double xCoordSource = source
                .getDouble(GraphicAttributeConstants.COORDX_PATH);

        Node target = edge.getTarget();
        double yCoord = target.getDouble(GraphicAttributeConstants.COORDY_PATH);
        double xCoordTarget = target
                .getDouble(GraphicAttributeConstants.COORDX_PATH);

        double adjacent = (xCoordTarget - xCoordSource)
                * PhyloTreeConstants.SHAPE_EUROGRAM_RATIO;
        double xCoord = xCoordSource + adjacent;

        // set edge type to polyline
        edge.changeString(GraphicAttributeConstants.SHAPE_PATH,
                GraphicAttributeConstants.POLYLINE_CLASSNAME);

        // set bend
        EdgeGraphicAttribute ega = (EdgeGraphicAttribute) edge
                .getAttribute(GraphicAttributeConstants.GRAPHICS);
        String bendsId = ega.getBends().getId();
        SortedCollectionAttribute bends = new LinkedHashMapAttribute(bendsId);

        String newId = GraphicAttributeConstants.BEND + "PHYL0";
        Attribute newBend = new CoordinateAttribute(newId, xCoord, yCoord);

        bends.add(newBend);
        ega.setBends(bends);
    }

    private void setSwoopogramEdge(Edge edge) {
        final double EDGE1_RATIO = 0.33;

        final double EDGE2_RATIO = 0.5;

        // get Coordinates of Nodes
        Node source = edge.getSource();
        double yCoordSource = source
                .getDouble(GraphicAttributeConstants.COORDY_PATH);
        double xCoordSource = source
                .getDouble(GraphicAttributeConstants.COORDX_PATH);

        Node target = edge.getTarget();
        double yCoordTarget = target
                .getDouble(GraphicAttributeConstants.COORDY_PATH);
        double xCoordTarget = target
                .getDouble(GraphicAttributeConstants.COORDX_PATH);

        double xDistance = xCoordTarget - xCoordSource;
        double xCoord1 = xCoordSource + xDistance * EDGE1_RATIO;
        double xCoord2 = xCoordSource + xDistance * EDGE2_RATIO;

        // set edge type
        edge.changeString(GraphicAttributeConstants.SHAPE_PATH,
                GraphicAttributeConstants.SMOOTH_CLASSNAME);

        // set bend
        EdgeGraphicAttribute ega = (EdgeGraphicAttribute) edge
                .getAttribute(GraphicAttributeConstants.GRAPHICS);
        String bendsId = ega.getBends().getId();
        SortedCollectionAttribute bends = new LinkedHashMapAttribute(bendsId);

        String newId = GraphicAttributeConstants.BEND + "PHYL0";
        Attribute newBend = new CoordinateAttribute(newId, xCoord1,
                yCoordSource);
        bends.add(newBend);

        newId = GraphicAttributeConstants.BEND + "PHYL1";
        newBend = new CoordinateAttribute(newId, xCoord2, yCoordTarget);
        bends.add(newBend);

        ega.setBends(bends);
    }
}
