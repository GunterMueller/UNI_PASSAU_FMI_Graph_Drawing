package tests.graffiti.io.ontology;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ClassTest {

    @Test
    public void label() {
        String id = "id";
        Class aClass = new Class(id, null);
        assertEquals(id, aClass.label());
    }

    @Test
    public void importTo() {
        String id = "Class";
        ClassBase classBase = new ClassBase();
        Class aClass = new Class(id, classBase);

        Graph graph = new Graph();
        aClass.importTo(graph);
        assertEquals(1, graph.nodeCount());
        assertEquals(0, graph.edgeCount());

        String superClassId = "SuperClass";
        Class superClass = new Class(superClassId, classBase);
        superClass.importTo(graph);
        assertEquals(2, graph.nodeCount());

        aClass.addSuperClassId(superClassId);
        aClass.importTo(graph);
        assertEquals(0, graph.edgeCount());

        aClass.importSuperClassPropertiesTo(graph);
        assertEquals(1, graph.edgeCount());

    }

}
