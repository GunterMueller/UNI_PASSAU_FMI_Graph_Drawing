// =============================================================================
//
//   AbstractGenerator.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractGenerator.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.generators;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.EdgeLabelAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringParameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.util.GeneralUtils;

/**
 * Abstract class for generators.
 * 
 * @author Marek Piorkowski
 */
public abstract class AbstractGenerator extends AbstractAlgorithm {

    /** Dynamic circle form */
    public static final String DYNAMIC_CIRCLE = "Dynamic Circle";

    /** Static circle form */
    public static final String STATIC_CIRCLE = "Static Circle";

    /** Random form */
    public static final String RANDOM = "Random";

    /** Should the edges be bended ? */
    protected BooleanParameter edgeBendingParam;

    /** Should the edges be labeled ? */
    protected BooleanParameter edgeLabelParam;

    /** Should the nodes be labeled ? */
    protected BooleanParameter nodeLabelParam;

    /** The maximal edge label value */
    protected IntegerParameter edgeMax;

    /** The minimal edge label value */
    protected IntegerParameter edgeMin;

    /** The start number when labeling the nodes */
    protected IntegerParameter startNumberParam;

    /** The algorithm's parameters */
    protected LinkedList<Parameter<?>> parameterList;

    /** The edge labels' name */
    protected StringParameter edgeLabelNameParam;

    /** From of the graph */
    protected StringSelectionParameter form;

    /** Should the edges be bended ? */
    protected boolean edgeBending;

    /** Should the edge labeling be done ? */
    protected boolean edgeLabeling;

    /** Should the form be chosen ? */
    protected boolean formSelection;

    /** Should the node labeling be done ? */
    protected boolean nodeLabeling;

    /**
     * Creates a new AbstractGenerator object.
     */
    protected AbstractGenerator() {
        parameterList = new LinkedList<Parameter<?>>();
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        parameters = new Parameter[parameterList.size()];
        return parameterList.toArray(parameters);
    }

    @Override
    protected void setAlgorithmParameters(Parameter<?>[] params) {
        int i = 0;
        for (Parameter<?> p : parameterList) {
            @SuppressWarnings("unchecked")
            Parameter<Object> pp = (Parameter<Object>) p;
            pp.setValue(params[i].getValue());
            i++;
        }
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        graph = null;

        if (nodeLabeling) {
            nodeLabelParam.setValue(new Boolean(false));
            startNumberParam.setValue(new Integer(1));
        }

        if (edgeLabeling) {
            edgeLabelParam.setValue(new Boolean(false));
            edgeMin.setValue(new Integer(1));
            edgeMax.setValue(new Integer(100));
            edgeLabelNameParam.setValue("label");
        }

        if (edgeBending) {
            edgeBendingParam.setValue(new Boolean(true));
        }

        if (formSelection) {
            form.setSelectedValue(0);
        }
    }

    /**
     * Sets the arrow heads to the standard arrow heads, if the graph is
     * directed.
     */
    protected void setEdgeArrows(Graph myGraph) {
        if (myGraph.isDirected()) {
            for (Iterator<Edge> it = myGraph.getEdgesIterator(); it.hasNext();) {
                EdgeGraphicAttribute ega = (EdgeGraphicAttribute) (it.next())
                        .getAttribute(GraphicAttributeConstants.GRAPHICS);
                ega.setArrowhead("org.graffiti.plugins.views.defaults."
                        + "StandardArrowShape");
            }
        }
    }

    /**
     * Adds a parameter for bending the edges.
     */
    protected void addEdgeBendingOption() {
        if (edgeBending)
            return;

        edgeBending = true;
        edgeBendingParam = new BooleanParameter(true, "bend the edges",
                "Bend the edges. Self loops and multiple edges can be seen then.");
        parameterList.add(edgeBendingParam);
    }

    /**
     * Adds parameters for labeling the edges.
     */
    protected void addEdgeLabelingOption() {
        if (edgeLabeling)
            return;

        edgeLabeling = true;
        edgeLabelParam = new BooleanParameter(false, "label the edges",
                "label the edges with numbers between min and max");
        edgeMin = new IntegerParameter(new Integer(1), new Integer(0),
                new Integer(100), "min weight", "the edges' minimum weight");
        edgeMax = new IntegerParameter(new Integer(100), new Integer(0),
                new Integer(1000), "max weight", "the edges' maximum weight");

        edgeLabelNameParam = new StringParameter("label", "label name",
                "edge label name");

        edgeMin.setDependency(edgeLabelParam, true);
        edgeMax.setDependency(edgeLabelParam, true);
        edgeLabelNameParam.setDependency(edgeLabelParam, true);

        parameterList.add(edgeLabelParam);
        parameterList.add(edgeMin);
        parameterList.add(edgeMax);
        parameterList.add(edgeLabelNameParam);
    }

    /**
     * Adds a form selection option.
     */
    protected void addFormSelOption() {
        if (formSelection)
            return;

        formSelection = true;

        String[] options = { DYNAMIC_CIRCLE, STATIC_CIRCLE, RANDOM };
        form = new StringSelectionParameter(options, "form",
                "the graphical form");
        parameterList.add(form);
    }

    /**
     * Adds parameters for labeling the nodes.
     */
    protected void addNodeLabelingOption() {
        if (nodeLabeling)
            return;

        nodeLabeling = true;
        nodeLabelParam = new BooleanParameter(false, "label the nodes",
                "label the nodes with numbers, beginning with start number");
        startNumberParam = new IntegerParameter(new Integer(1), new Integer(0),
                new Integer(100), "start number",
                "the number of the first node");
        startNumberParam.setDependency(nodeLabelParam, true);
        parameterList.add(nodeLabelParam);
        parameterList.add(startNumberParam);
    }

    /**
     * Bends the graph's edges considering multi edges.
     * 
     * @param allEdges
     *            The edges to bend.
     */
    protected void bendMultiEdges(Collection<Edge> allEdges) {
        // collect the edges in lists. self loops are in an own list.
        ArrayList<ArrayList<Edge>> edgeLists = new ArrayList<ArrayList<Edge>>();
        ArrayList<ArrayList<Edge>> selfloopLists = new ArrayList<ArrayList<Edge>>();

        // multi edges (=same source and same target) are each collected in one
        // list
        for (Edge edge : allEdges) {
            boolean found = false;

            for (int i = 0; i < edgeLists.size(); i++) {
                ArrayList<Edge> edges = edgeLists.get(i);
                Edge edge2 = edges.get(0);

                // add same multi edges to one list
                if ((edge.getSource() == edge2.getSource())
                        && (edge.getTarget() == edge2.getTarget())) {
                    edges.add(edge);
                    found = true;
                }
            }

            for (int i = 0; i < selfloopLists.size(); i++) {
                ArrayList<Edge> edges = selfloopLists.get(i);
                Edge edge2 = edges.get(0);

                // add same multi edges (self loops here) to one list
                if ((edge.getSource() == edge2.getSource())
                        && (edge.getSource() == edge.getTarget())) {
                    edges.add(edge);
                    found = true;
                }
            }

            // if an edge like the searched one was not found yet..
            if (!found) {
                // if it is a self loop...
                if (edge.getSource() == edge.getTarget()) {
                    // create a new list for these self loops
                    ArrayList<Edge> loops = new ArrayList<Edge>();
                    loops.add(edge);
                    selfloopLists.add(loops);
                }

                // if it is a normal edge
                else {
                    // create a new list for these edges
                    ArrayList<Edge> edges = new ArrayList<Edge>();
                    edges.add(edge);
                    edgeLists.add(edges);
                }
            }
        }

        // bend normal edges
        for (int i = 0; i < edgeLists.size(); i++) {
            ArrayList<Edge> edges = edgeLists.get(i);

            boolean hasReversal = false;

            if (edges.size() > 0) {
                hasReversal = hasReversal(edges.get(0));
            }

            for (int j = 0; j < edges.size(); j++) {
                Edge edge = edges.get(j);
                EdgeGraphicAttribute ega = (EdgeGraphicAttribute) edge
                        .getAttribute(GraphicAttributeConstants.GRAPHICS);
                SortedCollectionAttribute bends = new LinkedHashMapAttribute(
                        GraphicAttributeConstants.BENDS);

                if (hasReversal) {
                    bends.add(new CoordinateAttribute("bend0",
                            computeBendPosition(edge, (j + 1) * 30.0)));
                } else {
                    bends.add(new CoordinateAttribute("bend0",
                            computeBendPosition(edge, j * 30.0)));
                }

                ega.setShape("org.graffiti.plugins.views.defaults."
                        + "SmoothLineEdgeShape");

                if (edges.size() > 1) {
                    ega.setBends(bends);
                }
            }
        }

        // bend self loops
        for (int i = 0; i < selfloopLists.size(); i++) {
            ArrayList<Edge> edges = selfloopLists.get(i);
            int k = 0;

            for (int j = 0; j < edges.size(); j++) {
                Edge loopingEdge = edges.get(j);
                EdgeGraphicAttribute ega = (EdgeGraphicAttribute) loopingEdge
                        .getAttribute(GraphicAttributeConstants.GRAPHICS);
                SortedCollectionAttribute bends = new LinkedHashMapAttribute(
                        GraphicAttributeConstants.BENDS);
                CoordinateAttribute ca = (CoordinateAttribute) loopingEdge
                        .getSource().getAttribute(
                                GraphicAttributeConstants.GRAPHICS
                                        + Attribute.SEPARATOR
                                        + GraphicAttributeConstants.COORDINATE);

                double nodeXPos = ca.getX();
                double nodeYPos = ca.getY();

                /*
                 * if there are less than five self loop edges, they are ordered
                 * like this: (star is the node) | - * - |
                 */
                if (edges.size() <= 4) {
                    double bendPointDistance = 20.0;
                    double distFromNode = 60.0;
                    double x1 = nodeXPos - bendPointDistance;
                    double x2 = nodeXPos + bendPointDistance;
                    double y1 = nodeYPos - bendPointDistance;
                    double y2 = nodeYPos + bendPointDistance;

                    if (j == 0) {
                        y1 = nodeYPos + distFromNode;
                        y2 = nodeYPos + distFromNode;
                    } else if (j == 1) {
                        y1 = nodeYPos - distFromNode;
                        y2 = nodeYPos - distFromNode;
                    } else if (j == 2) {
                        x1 = nodeXPos + distFromNode;
                        x2 = nodeXPos + distFromNode;
                    } else if (j == 3) {
                        x1 = nodeXPos - distFromNode;
                        x2 = nodeXPos - distFromNode;
                    }

                    bends.add(new CoordinateAttribute("bend0",
                            new Point2D.Double(x1, y1)));
                    bends.add(new CoordinateAttribute("bend1",
                            new Point2D.Double(x2, y2)));
                }

                /*
                 * if there are more than four self loops, they are positioned
                 * on an imaginary circle around the node
                 */
                else {
                    double x = (Math.sin((0.5 * k) / (2.0 * edges.size())
                            * Math.PI * 8.0) * 80.0)
                            + nodeXPos;
                    double y = (Math.cos((0.5 * k) / (2.0 * edges.size())
                            * Math.PI * 8.0) * 80.0)
                            + nodeYPos;

                    bends.add(new CoordinateAttribute("bend0",
                            new Point2D.Double(x, y)));
                    k++;
                    x = (Math.sin((0.5 * k) / (2.0 * edges.size()) * Math.PI
                            * 8.0) * 80.0)
                            + nodeXPos;
                    y = (Math.cos((0.5 * k) / (2.0 * edges.size()) * Math.PI
                            * 8.0) * 80.0)
                            + nodeYPos;
                    bends.add(new CoordinateAttribute("bend1",
                            new Point2D.Double(x, y)));
                }

                ega.setShape("org.graffiti.plugins.views.defaults."
                        + "SmoothLineEdgeShape");
                ega.setBends(bends);
            }
        }
    }

    /**
     * Computes the bend point position in relation to the specified edge. The
     * position is a point on the "Mittelsenkrechte" between the edge's source
     * node and the edge's target node.
     * 
     * @param edge
     *            This edges bend point position is computed.
     * @param distanceFactor
     *            The point has to be in this distance from the edge.
     * 
     * @return The bend point position in relation to the specified edge.
     */
    private Point2D.Double computeBendPosition(Edge edge, double distanceFactor) {
        /*
         * let the edge's source be node a and the edge's target node b. first
         * get the nodes' positions.
         */
        CoordinateAttribute ca = (CoordinateAttribute) edge.getSource()
                .getAttribute(
                        GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.COORDINATE);
        double u = ca.getX();
        double v = ca.getY();
        ca = (CoordinateAttribute) edge.getTarget().getAttribute(
                GraphicAttributeConstants.GRAPHICS + Attribute.SEPARATOR
                        + GraphicAttributeConstants.COORDINATE);

        double x = ca.getX();
        double y = ca.getY();

        // compute the coordinates of point in the middle between node a and
        // node b
        double middlePointXPos = (u + x) / 2.0;
        double middlePointYPos = (v + y) / 2.0;

        // compute the x and y coordinate of the vertical (to the line between a
        // and b) vector
        double verticalVectorX = y - v;
        double verticalVectorY = u - x;

        // norm tthis vector dependent on the specified distance factor
        double normedXPos = (distanceFactor * verticalVectorX)
                / Math.sqrt((Math.pow(verticalVectorX, 2) + Math.pow(
                        verticalVectorY, 2)));
        double normedYPos = (distanceFactor * verticalVectorY)
                / Math.sqrt((Math.pow(verticalVectorX, 2) + Math.pow(
                        verticalVectorY, 2)));

        /*
         * the (x,y)-position of the point that lies on the vertical vector with
         * the distance distanceFactor from the middle point.
         */
        double xPos = middlePointXPos + normedXPos;
        double yPos = middlePointYPos + normedYPos;

        return new Point2D.Double(xPos, yPos);
    }

    /**
     * Checks whether this edge has an reversal edge or not.
     * 
     * @param edge
     *            The edge to check.
     * 
     * @return <code>true</code> if the edge has a reversal edge,
     *         <code>false</code> otherwise.
     */
    private boolean hasReversal(Edge edge) {
        Iterator<Edge> it;

        if (edge.isDirected()) {
            it = edge.getTarget().getDirectedOutEdgesIterator();
        } else {
            it = edge.getTarget().getUndirectedEdgesIterator();
        }

        while (it.hasNext()) {
            Edge outEdge = it.next();

            if (outEdge.getTarget() == edge.getSource())
                return true;
        }

        return false;
    }

    /**
     * 
     * @param nodes
     */
    protected void addGraphicAttributeToNodes(Node[] nodes) {
        for (Node node : nodes) {
            try {
                node.getAttribute(GraphicAttributeConstants.GRAPHICS);
            } catch (AttributeNotFoundException e) {
                node.addAttribute(new NodeGraphicAttribute(), "");
            }
        }
    }

    /**
     * 
     * @param nodes
     */
    protected void addGraphicAttributeToNodes(Collection<Node> nodes) {
        for (Node node : nodes) {
            try {
                node.getAttribute(GraphicAttributeConstants.GRAPHICS);
            } catch (AttributeNotFoundException e) {
                node.addAttribute(new NodeGraphicAttribute(), "");
            }
        }
    }

    /**
     * 
     * @param edges
     */
    protected void addGraphicAttributeToEdges(Collection<Edge> edges) {
        for (Edge edge : edges) {
            try {
                edge.getAttribute(GraphicAttributeConstants.GRAPHICS);
            } catch (AttributeNotFoundException e) {
                edge.addAttribute(new EdgeGraphicAttribute(edge.isDirected()),
                        "");
            }
        }
    }

    /**
     * Sets the nodes' labels beginning with the specified start number.
     * 
     * @param nodes
     *            The nodes to label.
     * @param startNumber
     *            The labeling starts with this number.
     */
    protected void labelNodes(Collection<Node> nodes, int startNumber) {
        int number = startNumber;

        graph.getListenerManager().transactionStarted(this);

        for (Node node : nodes) {
            NodeLabelAttribute labelAttr = new NodeLabelAttribute(
                    GraphicAttributeConstants.LABEL, String.valueOf(number));
            node.addAttribute(labelAttr,
                    GraphicAttributeConstants.LABEL_ATTRIBUTE_PATH);
            number++;
        }

        graph.getListenerManager().transactionFinished(this);
    }

    /**
     * Places the nodes graphically in a specified form.
     * 
     * @param nodes
     *            The nodes to place.
     * @param type
     *            The form type.
     */
    protected void formGraph(Collection<Node> nodes, String type) {
        if (type == STATIC_CIRCLE) {
            buildCircle(nodes, false);
        } else if (type == DYNAMIC_CIRCLE) {
            buildCircle(nodes, true);
        } else if (type == RANDOM) {
            buildRandomForm(nodes);
        }
    }

    protected void setCoordinate(Node n, double x, double y) {
        CoordinateAttribute ca = (CoordinateAttribute) n
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.COORDINATE);
        ca.setCoordinate(new Point2D.Double(x, y));
    }

    /**
     * The nodes build a circle form in a static or dynamic size dependent on
     * the number of nodes.
     * 
     * @param nodes
     *            The nodes to place.
     * @param dynamic
     *            The size of the circle can be build dynamically dependent on
     *            the number of nodes.
     */
    private void buildCircle(Collection<Node> nodes, boolean dynamic) {
        graph.getListenerManager().transactionStarted(this);

        int dynamicFactor = 0;

        if (dynamic) {
            dynamicFactor = nodes.size();
        }
        int i = 0;
        for (Node node : nodes) {
            double x = (Math.sin((1.0 * i) / (1.0 * nodes.size()) * Math.PI
                    * 2.0) * (180.0 + (dynamicFactor * 5.0)))
                    + 250.0 + (dynamicFactor * 5.0);
            double y = (Math.cos((1.0 * i) / (1.0 * nodes.size()) * Math.PI
                    * 2.0) * (180.0 + (dynamicFactor * 5.0)))
                    + 250.0 + (dynamicFactor * 5.0);
            setCoordinate(node, x, y);
            i++;
        }

        graph.getListenerManager().transactionFinished(this);
    }

    /**
     * Orders the nodes randomly on the graphical plane.
     * 
     * @param nodes
     *            Nodes to order.
     */
    private void buildRandomForm(Collection<Node> nodes) {
        double max = 100.0 + (30.0 * nodes.size());

        for (Node node : nodes) {
            double x = Math.random() * max;
            double y = Math.random() * max;
            setCoordinate(node, x, y);
        }
    }

    /**
     * Labels the edges.
     * 
     * @param edges
     *            The edges to label.
     * @param labelName
     *            The labels' name.
     * @param min
     *            The minimal label value.
     * @param max
     *            The maximal label value.
     */
    protected void labelEdges(Collection<Edge> edges, String labelName,
            int min, int max) {
        Random random = new Random(System.currentTimeMillis());

        graph.getListenerManager().transactionStarted(this);

        for (Edge edge : edges) {
            int value = min + random.nextInt(max - min + 1);
            LabelAttribute labelAttr = null;
            String val = value + "";
            List<Attribute> attributesList = new LinkedList<Attribute>();

            GeneralUtils.searchForAttributes(edge.getAttribute(""),
                    LabelAttribute.class, attributesList);

            Iterator<Attribute> listIterator = attributesList.iterator();
            boolean found = false;

            while (listIterator.hasNext() && !found) {
                Attribute attr = listIterator.next();

                if ((attr.getId().equals(labelName))
                        && (attr.getParent().getId().equals(""))) {
                    labelAttr = (LabelAttribute) attr;
                    found = true;
                }
            }

            if (labelAttr != null) {
                labelAttr.setLabel(val);
            } else { // no label found
                labelAttr = new EdgeLabelAttribute(labelName);
                labelAttr.setLabel(val);
                edge.addAttribute(labelAttr, "");
            }
        }

        graph.getListenerManager().transactionFinished(this);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
