package org.graffiti.plugins.tools.benchmark.generators;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Random;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.FastGraph;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugins.tools.benchmark.sampler.AssignmentList;
import org.graffiti.plugins.tools.benchmark.sampler.SamplingContext;
import org.graffiti.plugins.tools.benchmark.sampler.SamplingException;
import org.graffiti.plugins.views.defaults.StandardArrowShape;

public abstract class DeterministicGraphGenerator {
    protected AssignmentList assignments;

    public final Graph generate(Random random) {
        Graph graph = new FastGraph();
        generate(random, graph);
        return graph;
    }

    public final Graph generate(Random random, Graph graph) {
        if (assignments == null)
            throw new SamplingException("error.graphGeneratorNotAssigned");
        graph.getListenerManager().transactionStarted(this);
        SamplingContext context = assignments.createContext(Collections
                .<String, Double> emptyMap(), random);

        Node[] nodes = generate(graph, context);

        addGraphicAttributes(graph, nodes, context);
        graph.getListenerManager().transactionFinished(this);
        return graph;
    }

    protected abstract Node[] generate(Graph graph, SamplingContext context);

    private void addGraphicAttributes(Graph graph, Node[] nodes,
            SamplingContext context) {
        int length = nodes.length;
        for (int i = 0; i < length; i++) {
            try {
                nodes[i].getAttribute(GraphicAttributeConstants.GRAPHICS);
            } catch (AttributeNotFoundException e) {
                nodes[i].addAttribute(new NodeGraphicAttribute(), "");
            }

            for (Edge edge : nodes[i].getDirectedOutEdges()) {
                EdgeGraphicAttribute ega = null;
                try {
                    ega = (EdgeGraphicAttribute) edge
                            .getAttribute(GraphicAttributeConstants.GRAPHICS);
                } catch (AttributeNotFoundException e) {
                    ega = new EdgeGraphicAttribute(true);
                    edge.addAttribute(ega, "");
                }
                ega.setArrowhead(StandardArrowShape.class.getCanonicalName());
            }
        }
        assignCoordinates(nodes, context);
    }

    protected void assignCoordinates(Node[] nodes, SamplingContext context) {
        int length = nodes.length;
        double arc = (2.0 * Math.PI) / length;
        double radius = 100.0 / arc;
        for (int i = 0; i < length; i++) {
            assignCoordinate(nodes[i], new Point2D.Double(Math.round(radius
                    * Math.cos(arc * i)), Math
                    .round(radius * Math.sin(arc * i))));
        }
    }

    protected final void assignCoordinate(Node node, Point2D coordinate) {
        ((CoordinateAttribute) node
                .getAttribute(GraphicAttributeConstants.COORD_PATH))
                .setCoordinate(coordinate);
    }

    public void setAssignments(AssignmentList assignments) {
        this.assignments = assignments;
    }
}
