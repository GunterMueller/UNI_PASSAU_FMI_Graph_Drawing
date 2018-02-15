// =============================================================================
//
//   GraphGeneratorHypercube.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphGeneratorHypercube.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.generators;

import java.awt.geom.Point2D;
import java.util.ArrayList;
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
 * Generates an n-dimensional hypercube graph.
 */
public class GraphGeneratorHypercube extends AbstractGenerator {

    /** The hypercube's number of dimensions. */
    private IntegerParameter dimensionsParam;

    /**
     * Constructs a new instance.
     */
    public GraphGeneratorHypercube() {
        super();
        addNodeLabelingOption();
        addEdgeLabelingOption();
        dimensionsParam = new IntegerParameter(new Integer(4), new Integer(0),
                new Integer(100), "dimensions", "the number of dimensions");
        parameterList.addFirst(dimensionsParam);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Graph Generator: Hypercube";
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        if (dimensionsParam.getValue().compareTo(new Integer(0)) < 0) {
            errors.add("The number of dimensions must not be smaller than 0.");
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
        int dimensions = dimensionsParam.getValue().intValue();

        if (dimensions == 0)
            return;

        int numOfNodes = (int) Math.pow(2, dimensions);
        ArrayList<Node> nodes = new ArrayList<Node>(numOfNodes + 1);
        Collection<Edge> edges = new LinkedList<Edge>();

        graph.getListenerManager().transactionStarted(this);

        double xStart = 100.0;
        double yStart = 100.0;
        double x = xStart;
        double y = yStart;
        double space = 100.0;

        int a = 1;
        nodes.add(graph.addNode());
        for (int i = 1; i < numOfNodes + 1; i++) {
            nodes.add(graph.addNode());

            if (a == 1) {
                x = xStart;
                y = yStart;
            } else if (a == 2) {
                x = xStart + space;
                y = yStart;
            } else if (a == 3) {
                x = xStart;
                y = yStart + space;
            } else if (a == 4) {
                x = xStart + space;
                y = yStart + space;
                xStart += (space / 2);
                yStart += (space / 2);
                a = 0;
            }

            if ((i % 8) == 0) {
                xStart += space;
                yStart += space;
            }

            a++;

            CoordinateAttribute ca = (CoordinateAttribute) nodes.get(i)
                    .getAttribute(
                            GraphicAttributeConstants.GRAPHICS
                                    + Attribute.SEPARATOR
                                    + GraphicAttributeConstants.COORDINATE);
            ca.setCoordinate(new Point2D.Double(x, y));
        }

        int jumpDistance = 1;

        for (int i = 0; i <= dimensions; i++) {
            jumpDistance = (int) Math.pow(2, i);

            int k = 1;

            while ((k + jumpDistance) < nodes.size()) {
                edges.add(graph.addEdge(nodes.get(k), nodes.get(k
                        + jumpDistance), false));

                if ((k % jumpDistance) == 0) {
                    k = k + jumpDistance + 1;
                } else {
                    k++;
                }
            }
        }
        graph.deleteNode(nodes.get(0));
        nodes.remove(0);
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
        dimensionsParam.setValue(new Integer(4));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
