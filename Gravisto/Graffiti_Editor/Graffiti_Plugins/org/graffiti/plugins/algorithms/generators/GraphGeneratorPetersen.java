// =============================================================================
//
//   GraphGeneratorPetersen.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphGeneratorPetersen.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.generators;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.IntegerParameter;

/**
 * This generator creates a Petersen graph P(n, k). For n > 1 und k > 0 it has 2
 * n nodes (v[0], ... , v[n - 1], u[0], ... , u[n - 1]) and edges of the form
 * (u[i], u[i + 1]), (u[i], v[i]) and (v[i], v[i + k]). The u edges are placed
 * on an outer circle, the v edges build the inner circle.
 */
public class GraphGeneratorPetersen extends AbstractGenerator {

    /**
     * The edges of the inner circle are of the form (v[i], v[i + k]). k is this
     * parameter.
     */
    private IntegerParameter kParam;

    /** a half of the number of nodes */
    private IntegerParameter nodesParam;

    /**
     * Constructs a new instance.
     */
    public GraphGeneratorPetersen() {
        super();
        addNodeLabelingOption();
        addEdgeLabelingOption();

        nodesParam = new IntegerParameter(new Integer(10), new Integer(0),
                new Integer(100), "half the number of nodes",
                "the number of nodes in one circle");
        kParam = new IntegerParameter(new Integer(3), new Integer(0),
                new Integer(100), "edge connect param k",
                "the edges in the inner circle are of the form (u[i], u[i+k])");
        parameterList.addFirst(kParam);
        parameterList.addFirst(nodesParam);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Graph Generator: Petersen";
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        if (nodesParam.getValue().intValue() < 3) {
            errors.add("The number of nodes may not be less than three.");
        }

        if (kParam.getValue().intValue() < 1) {
            errors.add("The connectivity may not be less than one.");
        }

        if (kParam.getValue().intValue() > nodesParam.getValue().intValue()) {
            errors
                    .add("The k-parameter may not be greater than the number of nodes.");
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
        int n = nodesParam.getValue().intValue();
        int k = kParam.getValue().intValue();

        Node[] outerNodes = new Node[n];
        Node[] innerNodes = new Node[n];
        Collection<Edge> edges = new LinkedList<Edge>();

        graph.getListenerManager().transactionStarted(this);

        int dynamicFactor = outerNodes.length;

        for (int i = 0; i < outerNodes.length; i++) {
            outerNodes[i] = graph.addNode();
            innerNodes[i] = graph.addNode();
            edges.add(graph.addEdge(outerNodes[i], innerNodes[i], false));

            if (i > 0) {
                edges.add(graph
                        .addEdge(outerNodes[i - 1], outerNodes[i], false));
            }

            CoordinateAttribute outerCa = (CoordinateAttribute) outerNodes[i]
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COORDINATE);
            double outerX = (Math.sin((1.0 * i) / (1.0 * outerNodes.length)
                    * Math.PI * 2.0) * (180.0 + (dynamicFactor * 5.0)))
                    + 250.0 + (dynamicFactor * 5.0);
            double outerY = (Math.cos((1.0 * i) / (1.0 * outerNodes.length)
                    * Math.PI * 2.0) * (180.0 + (dynamicFactor * 5.0)))
                    + 250.0 + (dynamicFactor * 5.0);
            outerCa.setCoordinate(new Point2D.Double(outerX, outerY));

            CoordinateAttribute innerCa = (CoordinateAttribute) innerNodes[i]
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COORDINATE);
            double innerX = (Math.sin((1.0 * i) / (1.0 * innerNodes.length)
                    * Math.PI * 2.0) * (90.0 + (dynamicFactor * 5.0)))
                    + 250.0 + (dynamicFactor * 5.0);
            double innerY = (Math.cos((1.0 * i) / (1.0 * innerNodes.length)
                    * Math.PI * 2.0) * (90.0 + (dynamicFactor * 5.0)))
                    + 250.0 + (dynamicFactor * 5.0);
            innerCa.setCoordinate(new Point2D.Double(innerX, innerY));
        }

        edges.add(graph.addEdge(outerNodes[outerNodes.length - 1],
                outerNodes[0], false));

        for (int i = 0; i < innerNodes.length; i++) {
            edges.add(graph.addEdge(innerNodes[i], innerNodes[(i + k) % n],
                    false));
        }

        graph.getListenerManager().transactionFinished(this);

        // label the nodes
        if (nodeLabelParam.getBoolean().booleanValue()) {
            Collection<Node> nodeList = new LinkedList<Node>();
            Collections.addAll(nodeList, outerNodes);
            Collections.addAll(nodeList, innerNodes);
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
        nodesParam.setValue(new Integer(10));
        kParam.setValue(new Integer(3));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
