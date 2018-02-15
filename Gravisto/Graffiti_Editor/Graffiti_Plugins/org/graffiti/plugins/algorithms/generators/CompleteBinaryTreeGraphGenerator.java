// =============================================================================
//
//   CompleteBinaryTreeGraphGenerator.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: CompleteBinaryTreeGraphGenerator.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.generators;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.IntegerParameter;

/**
 * This generator creates a graph with n nodes. The nodes and edges build a
 * complete binary tree.
 */
public class CompleteBinaryTreeGraphGenerator extends AbstractGenerator {

    /** number of nodes */
    private IntegerParameter nodesParam;

    /** Should the tree be directed? */
    private BooleanParameter isDirectedParam;

    /**
     * Constructs a new instance.
     */
    public CompleteBinaryTreeGraphGenerator() {
        super();
        addNodeLabelingOption();
        addEdgeLabelingOption();
        nodesParam = new IntegerParameter(new Integer(5), new Integer(0),
                new Integer(100), "number of nodes",
                "the number of nodes to generate");
        isDirectedParam = new BooleanParameter(true, "directed",
                "Should the tree be directed?");
        parameterList.addFirst(isDirectedParam);
        parameterList.addFirst(nodesParam);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Graph Generator: Complete Binary Tree";
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        if (nodesParam.getValue().compareTo(new Integer(1)) < 0) {
            errors.add("The number of nodes may not be smaller than two.");
        }

        if (graph == null) {
            errors.add("The graph instance may not be null.");
        }

        if (!errors.isEmpty())
            throw errors;
    }

    /**
     * Adds a new node and ensures all needed attributes exist.
     * 
     * @param nodes
     *            Collection to add new node to.
     */
    protected void addNode(Collection<Node> nodes) {
        Node node = graph.addNode();
        try {
            node.getAttribute(GraphicAttributeConstants.GRAPHICS);
        } catch (AttributeNotFoundException e) {
            node.addAttribute(new NodeGraphicAttribute(), "");
        }
        nodes.add(node);
    }

    /**
     * Adds a new edge and ensures all needed attributes exist.
     */
    private Edge addEdge(Node node, Node node2, boolean directed) {
        Edge newEdge = this.graph.addEdge(node, node2, directed);
        try {
            newEdge.getAttribute(GraphicAttributeConstants.GRAPHICS);
        } catch (AttributeNotFoundException e) {
            newEdge.addAttribute(new EdgeGraphicAttribute(), "");
        }

        return newEdge;
    }

    /**
     * Sets the numOfNodes.
     * 
     * @param numOfNodes
     *            the numOfNodes to set.
     */
    public void setNumOfNodes(int numOfNodes) {
        nodesParam.setValue(numOfNodes);
    }

    /**
     * Sets the directed.
     * 
     * @param directed
     *            the directed to set.
     */
    public void setDirected(boolean directed) {
        isDirectedParam.setValue(directed);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        // add nodes
        int numberOfNodes = nodesParam.getValue().intValue();
        boolean directed = (isDirectedParam.getValue()).booleanValue();
        graph.setDirected(directed);

        ArrayList<Node> nodes = new ArrayList<Node>(numberOfNodes);
        Collection<Edge> edges = new LinkedList<Edge>();

        graph.getListenerManager().transactionStarted(this);

        if (numberOfNodes == 1) {
            addNode(nodes);

            CoordinateAttribute ca = (CoordinateAttribute) nodes.get(0)
                    .getAttribute(
                            GraphicAttributeConstants.GRAPHICS
                                    + Attribute.SEPARATOR
                                    + GraphicAttributeConstants.COORDINATE);
            ca.setCoordinate(new Point2D.Double(100.0, 50.0));
        } else {
            double spaceFactor = 12.0;

            double leftXStart = numberOfNodes * spaceFactor;
            double xSpace = leftXStart * 4;
            double ySpace = 50.0;

            double yStart = 50.0;
            double xStart = 200.0 + (numberOfNodes * spaceFactor);

            int temp = 0;
            double x = xStart;
            double y = yStart;

            for (int i = 0; i < numberOfNodes; i++) {
                addNode(nodes);

                if (temp == i) {
                    temp = (temp * 2) + 1;
                    y += ySpace;
                    xSpace /= 2;

                    if (i > 0) {
                        x = xStart - leftXStart;
                    }
                } else {
                    x += xSpace;
                }

                CoordinateAttribute ca = (CoordinateAttribute) nodes.get(i)
                        .getAttribute(
                                GraphicAttributeConstants.GRAPHICS
                                        + Attribute.SEPARATOR
                                        + GraphicAttributeConstants.COORDINATE);
                ca.setCoordinate(new Point2D.Double(x, y));
            }

            int stop = (numberOfNodes / 2) - 1;

            for (int i = 0; i < stop; i++) {
                edges.add(addEdge(nodes.get(i), nodes.get((i * 2) + 1),
                        directed));
                edges.add(addEdge(nodes.get(i), nodes.get((i * 2) + 2),
                        directed));
            }

            edges.add(addEdge(nodes.get((numberOfNodes / 2) - 1), nodes
                    .get(numberOfNodes - 1), directed));

            if ((numberOfNodes % 2) != 0) {
                edges.add(addEdge(nodes.get((numberOfNodes / 2) - 1), nodes
                        .get(numberOfNodes - 2), directed));
            }
        }

        if (directed) {
            setEdgeArrows(graph);
        }

        graph.getListenerManager().transactionFinished(this);

        // label the nodes
        if (nodeLabelParam.getBoolean().booleanValue()) {
            labelNodes(nodes, startNumberParam.getValue().intValue());
        }

        // label the edges
        if (edgeLabelParam.getBoolean().booleanValue()) {
            labelEdges(edges, edgeLabelNameParam.getString(), edgeMin
                    .getValue().intValue(), edgeMax.getValue().intValue());
        }
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        super.reset();
        nodesParam.setValue(new Integer(5));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
