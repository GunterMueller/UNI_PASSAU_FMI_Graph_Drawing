// =============================================================================
//
//   CompleteBinaryTreeGraphGenerator.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: CompleteBinaryTreeGraphGenerator.java 1524 2006-10-18 00:13:56Z keilhaue $

package org.graffiti.plugins.algorithms.generators;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;

/**
 * This generator creates a graph with n nodes. The nodes and edges build a
 * complete binary tree.
 */
public class CompleteTernaryTreeGraphGenerator extends AbstractGenerator {

    Graph graph;

    /**
     * The number of nodes in the graph
     */
    private int numberOfNodes;

    /** Should the tree be directed? */
    private boolean directed;

    /**
     * Constructs a new instance.
     */
    public CompleteTernaryTreeGraphGenerator(Graph graph, int numberOfNodes,
            boolean directed) {
        super();
        this.graph = graph;
        this.numberOfNodes = numberOfNodes;
        this.directed = directed;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Graph Generator: Complete Ternary Tree";
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
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {

        ArrayList<Node> nodes = new ArrayList<Node>(numberOfNodes);
        Collection<Edge> edges = new LinkedList<Edge>();

        if (numberOfNodes == 1) {
            addNode(nodes);

            CoordinateAttribute ca = (CoordinateAttribute) nodes.get(0)
                    .getAttribute(
                            GraphicAttributeConstants.GRAPHICS
                                    + Attribute.SEPARATOR
                                    + GraphicAttributeConstants.COORDINATE);
            ca.setCoordinate(new Point2D.Double(100.0, 50.0));
        } else if (numberOfNodes == 2) {
            addNode(nodes);

            CoordinateAttribute ca = (CoordinateAttribute) nodes.get(0)
                    .getAttribute(
                            GraphicAttributeConstants.GRAPHICS
                                    + Attribute.SEPARATOR
                                    + GraphicAttributeConstants.COORDINATE);
            ca.setCoordinate(new Point2D.Double(100.0, 50.0));

            addNode(nodes);

            ca = (CoordinateAttribute) nodes.get(1).getAttribute(
                    GraphicAttributeConstants.GRAPHICS + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COORDINATE);
            ca.setCoordinate(new Point2D.Double(200.0, 50.0));

            edges.add(addEdge(nodes.get(0), nodes.get(1), directed));
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

            int stop = (numberOfNodes / 3) - 1;

            if (numberOfNodes % 3 == 2) {
                stop++;
            }

            for (int i = 0; i < stop; i++) {
                edges.add(addEdge(nodes.get(i), nodes.get((i * 3) + 1),
                        directed));
                edges.add(addEdge(nodes.get(i), nodes.get((i * 3) + 2),
                        directed));
                edges.add(addEdge(nodes.get(i), nodes.get((i * 3) + 3),
                        directed));
            }

            int incompleteNode;

            if (numberOfNodes % 3 != 2) {
                incompleteNode = (numberOfNodes / 3) - 1;
            } else {
                incompleteNode = (numberOfNodes / 3);
            }

            edges.add(addEdge(nodes.get(incompleteNode), nodes
                    .get(numberOfNodes - 1), directed));

            switch (numberOfNodes % 3) {
            case 1:
                edges.add(addEdge(nodes.get(incompleteNode), nodes
                        .get(numberOfNodes - 3), directed));
            case 0:
                edges.add(addEdge(nodes.get(incompleteNode), nodes
                        .get(numberOfNodes - 2), directed));
            }
        }

        if (directed) {
            setEdgeArrows(graph);
        }

    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        super.reset();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
