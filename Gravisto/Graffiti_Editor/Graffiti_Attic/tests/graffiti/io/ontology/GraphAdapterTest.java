package tests.graffiti.io.ontology;

import static org.junit.Assert.assertEquals;

import org.graffiti.graph.Edge;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.junit.Test;

public class GraphAdapterTest {

    @Test
    public void addSuperClassEdge() {
        Graph graph = new Graph();
        String superClass = "SuperClass";
        String subClass = "SubClass";
        graph.addNode(superClass, superClass);
        graph.addNode(subClass, subClass);
        graph.addSuperClassEdge(subClass, superClass);
        Edge edge = graph.superClassEdge(subClass, superClass);
        double edgeThickness = ((EdgeGraphicAttribute) edge
                .getAttribute(GraphicAttributeConstants.GRAPHICS))
                .getFrameThickness();
        assertEquals(3.0, edgeThickness);
    }

    @Test
    public void addEdge() {
        Graph graph = new Graph();
        String source = "Source";
        String target = "Target";
        graph.addNode(source, source);
        graph.addNode(target, target);
        String id = "Edge";
        String label = "EdgeLabel";
        graph.addEdge(source, id, label, target);
        Edge edge = graph.edge(id);
        double edgeThickness = ((EdgeGraphicAttribute) edge
                .getAttribute(GraphicAttributeConstants.GRAPHICS))
                .getFrameThickness();
        assertEquals(1.0, edgeThickness);

    }

}
