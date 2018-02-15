// =============================================================================
//
//   ThreeDimGridGraphGenerator.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ThreeDimGridGraphGenerator.java 5766 2010-05-07 18:39:06Z gleissner $

/*
 * $Id: ThreeDimGridGraphGenerator.java 5766 2010-05-07 18:39:06Z gleissner $
 */

package org.graffiti.plugins.algorithms.generators;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.LinkedList;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.IntegerParameter;

/**
 * This generator creates a three dimensional grid.
 */
public class ThreeDimGridGraphGenerator extends AbstractGenerator {

    /** The grid's depth. */
    private IntegerParameter depthParam;

    /** The grid's height. */
    private IntegerParameter heightParam;

    /** The grid's width. */
    private IntegerParameter widthParam;

    /**
     * Constructs a new instance.
     */
    public ThreeDimGridGraphGenerator() {
        super();
        addNodeLabelingOption();
        addEdgeLabelingOption();
        widthParam = new IntegerParameter(new Integer(5), new Integer(0),
                new Integer(100), "width", "the grid's width");
        heightParam = new IntegerParameter(new Integer(5), new Integer(0),
                new Integer(100), "height", "the grid's height");
        depthParam = new IntegerParameter(new Integer(5), new Integer(0),
                new Integer(100), "depth", "the grid's depth");
        parameterList.addFirst(depthParam);
        parameterList.addFirst(heightParam);
        parameterList.addFirst(widthParam);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Graph Generator: 3D-Grid";
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        if (widthParam.getValue().compareTo(new Integer(0)) == 0) {
            errors.add("A 3D-graph cannot have width == 0.");
        } else if (widthParam.getValue().compareTo(new Integer(0)) < 0) {
            errors.add("The width may not be smaller than zero.");
        }

        if (heightParam.getValue().compareTo(new Integer(0)) == 0) {
            errors.add("A 3D-graph cannot have height == 0.");
        } else if (heightParam.getValue().compareTo(new Integer(0)) < 0) {
            errors.add("The height may not be smaller than zero.");
        }

        if (depthParam.getValue().compareTo(new Integer(0)) == 0) {
            errors.add("A 3D-graph cannot have depth == 0.");
        } else if (depthParam.getValue().compareTo(new Integer(0)) < 0) {
            errors.add("The depth may not be smaller than zero.");
        }

        if (graph == null) {
            errors.add("The graph instance may not be null.");
        }

        if (!errors.isEmpty())
            throw errors;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        int width = widthParam.getValue().intValue();
        int height = heightParam.getValue().intValue();
        int depth = depthParam.getValue().intValue();

        // add nodes
        Node[][][] nodes = new Node[width][height][depth];
        Collection<Node> nodeList = new LinkedList<Node>();
        Collection<Edge> edges = new LinkedList<Edge>();

        graph.getListenerManager().transactionStarted(this);

        double xStart = 100.0;
        double yStart = 100.0;
        double space = 100.0;

        double xPos = xStart;
        double yPos = yStart;

        for (int z = 0; z < depth; z++) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    nodes[x][y][z] = graph.addNode();
                    nodeList.add(nodes[x][y][z]);

                    CoordinateAttribute ca = (CoordinateAttribute) nodes[x][y][z]
                            .getAttribute(GraphicAttributeConstants.GRAPHICS
                                    + Attribute.SEPARATOR
                                    + GraphicAttributeConstants.COORDINATE);
                    ca.setCoordinate(new Point2D.Double(xPos, yPos));
                    xPos += space;
                }

                xPos = xStart;
                yPos += space;
            }

            xStart += (space / 2.0);
            yStart += (space / 2.0);
            xPos = xStart;
            yPos = yStart;
        }

        // add edges
        for (int z = 0; z < depth; z++) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if ((x + 1) < width) {
                        edges.add(graph.addEdge(nodes[x][y][z],
                                nodes[x + 1][y][z], false));
                    }

                    if ((y + 1) < height) {
                        edges.add(graph.addEdge(nodes[x][y][z],
                                nodes[x][y + 1][z], false));
                    }

                    if ((z + 1) < depth) {
                        edges.add(graph.addEdge(nodes[x][y][z],
                                nodes[x][y][z + 1], false));
                    }
                }
            }
        }

        graph.getListenerManager().transactionFinished(this);

        // label the nodes
        if (nodeLabelParam.getBoolean().booleanValue()) {
            labelNodes(nodeList, startNumberParam.getValue().intValue());
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
        widthParam.setValue(new Integer(5));
        heightParam.setValue(new Integer(5));
        depthParam.setValue(new Integer(5));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
