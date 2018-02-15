// =============================================================================
//
//   GridGeneratorAlgorithm.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GridGeneratorAlgorithm.java 5766 2010-05-07 18:39:06Z gleissner $

/*
 * $Id: GridGeneratorAlgorithm.java 5766 2010-05-07 18:39:06Z gleissner $
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
 * An implementation of a grid generator algorithm.
 * 
 * @version $Revision: 5766 $
 */
public class GridGeneratorAlgorithm extends AbstractGenerator {

    /** The width and height of the grid. */
    private IntegerParameter heightParam;

    /** The width and height of the grid. */
    private IntegerParameter widthParam;

    /**
     * Constructs a new instance.
     */
    public GridGeneratorAlgorithm() {
        super();
        addNodeLabelingOption();
        addEdgeLabelingOption();
        widthParam = new IntegerParameter(new Integer(12), new Integer(0),
                new Integer(100), "width", "the width of the grid");
        heightParam = new IntegerParameter(new Integer(12), new Integer(0),
                new Integer(100), "height", "the height of the grid");
        parameterList.addFirst(heightParam);
        parameterList.addFirst(widthParam);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Graph Generator: Grid";
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        if (widthParam.getValue().compareTo(new Integer(0)) < 0) {
            errors.add("The width may not be smaller than zero.");
        }

        if (heightParam.getValue().compareTo(new Integer(0)) < 0) {
            errors.add("The height may not be smaller than zero.");
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
        int w = widthParam.getValue().intValue();
        int h = heightParam.getValue().intValue();

        Node[][] n = new Node[w][h];
        Collection<Edge> edges = new LinkedList<Edge>();

        graph.getListenerManager().transactionStarted(this);

        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y) {
                n[x][y] = graph.addNode();

                CoordinateAttribute ca = (CoordinateAttribute) n[x][y]
                        .getAttribute(GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.COORDINATE);

                ca.setCoordinate(new Point2D.Double((x * 60) + 25,
                        (y * 60) + 25));

                // edges
                if (y > 0) {
                    edges.add(graph.addEdge(n[x][y - 1], n[x][y], true));
                }

                if (x > 0) {
                    edges.add(graph.addEdge(n[x - 1][y], n[x][y], true));
                }
            }
        }

        graph.getListenerManager().transactionFinished(this);

        // label the nodes
        if (nodeLabelParam.getBoolean().booleanValue()) {
            Collection<Node> nodeList = new LinkedList<Node>();

            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    nodeList.add(n[j][i]);
                }
            }

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
        widthParam.setValue(new Integer(12));
        heightParam.setValue(new Integer(12));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
