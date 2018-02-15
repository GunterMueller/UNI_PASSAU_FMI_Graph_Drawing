package tests.graffiti.io.ontology;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.graffiti.graph.AdjListGraph;
import org.junit.Test;

public class OntologyTest {

    @Test
    public void constructor() {
        Ontology ontology = new Ontology();
        assertEquals(0, ontology.classCount());
        assertEquals(0, ontology.propertyCount());
    }

    @Test(expected = java.util.NoSuchElementException.class)
    public void objectPropertyForIdThatDoesNotExist() {
        new Ontology().objectPropertyFor("id");
    }

    @Test
    public void propertyCount() {
        Ontology ontology = new Ontology();
        String propertyId = "id";
        String domain = "Domain";
        String range = "Range";

        ontology.defineObjectProperty(propertyId, domain, null, range);
        assertEquals(0, ontology.propertyCount());

        String superClassId = "SuperClass";
        String subClass1 = "SubClass1";
        String subClass2 = "SubClass2";

        ontology.defineClass(superClassId, superClassId);
        ontology.defineClass(subClass1, subClass1);
        ontology.defineClass(subClass2, subClass2);
        ontology.defineSuperClassProperty(subClass1, superClassId);
        ontology.defineSuperClassProperty(subClass2, superClassId);

        int superClassCount = ontology.superClassPropertyCount();
        int objectPropertyCount = ontology.objectPropertyCount();
        assertEquals(superClassCount + objectPropertyCount, ontology
                .propertyCount());
    }

    @Test
    public void propertyCountForMissingDomain() {
        Ontology ontology = new Ontology();
        Object id = "id";
        Object domainId = "domain";
        Object rangeId = "range";
        String label = "label";
        ontology.defineObjectProperty(id, domainId, label, rangeId);
        assertEquals(0, ontology.propertyCount());
    }

    @Test
    public void countSuperClasses() {
        Ontology ontology = new Ontology();
        String classId = "Class";
        String superClassId = "SuperClass";

        ontology.defineClass(classId, classId);
        ontology.defineSuperClassProperty(classId, superClassId);
        assertEquals(0, ontology.countSuperClasses(classId));

        ontology.defineClass(superClassId, superClassId);
        assertEquals(1, ontology.countSuperClasses(classId));
    }

    @Test
    public void addClass() {
        Ontology ontology = new Ontology();
        String id = "http://test/Class";
        String label = "Class";
        ontology.defineClass(id, label);
        int classCount = ontology.classCount();
        assertEquals(1, classCount);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void addClassWithNullId() {
        new Ontology().defineClass(null, null);
    }

    @Test
    public void addSuperClassProperty() {
        Ontology ontology = new Ontology();
        String classId = "Class";
        String superClass1 = "SuperClass1";
        String superClass2 = "SuperClass2";

        ontology.defineSuperClassProperty(classId, superClass1);
        assertEquals(0, ontology.classCount());
        assertEquals(0, ontology.countSuperClasses(classId));

        ontology.defineClass(classId, classId);
        ontology.defineClass(superClass1, superClass1);
        assertEquals(2, ontology.classCount());
        assertEquals(1, ontology.countSuperClasses(classId));

        ontology.defineSuperClassProperty(classId, superClass2);
        assertEquals(2, ontology.classCount());

        ontology.defineClass(superClass2, superClass2);
        assertEquals(3, ontology.classCount());
        assertEquals(2, ontology.countSuperClasses(classId));

        int classCountBefore = ontology.classCount();
        ontology.defineClass(classId, classId);
        int classCountNow = ontology.classCount();
        boolean classWasAddedTwice = classCountBefore < classCountNow;
        assertFalse(classWasAddedTwice);

        int superClassCountBefore = ontology.countSuperClasses(classId);
        ontology.defineSuperClassProperty(classId, superClass1);
        int superClassCountNow = ontology.countSuperClasses(classId);
        boolean inheritsTwiceFromTheSameSuperClass = superClassCountBefore < superClassCountNow;
        assertFalse(inheritsTwiceFromTheSameSuperClass);
    }

    @Test
    public void containsSuperClassProperty() {
        Ontology ontology = new Ontology();
        String subClassId = "SubClass";
        String superClassId = "SuperClass";
        assertFalse(ontology
                .definesSuperClassProperty(subClassId, superClassId));

        ontology.defineClass(subClassId, null);
        assertFalse(ontology
                .definesSuperClassProperty(subClassId, superClassId));

        ontology.defineClass(superClassId, null);
        assertFalse(ontology
                .definesSuperClassProperty(subClassId, superClassId));

        ontology.defineSuperClassProperty(subClassId, superClassId);
        assertTrue(ontology.definesSuperClassProperty(subClassId, superClassId));

        String superClass2 = "SuperClass2";
        ontology.defineSuperClassProperty(subClassId, superClass2);
        assertFalse(ontology.definesSuperClassProperty(subClassId, superClass2));

        ontology.defineClass(superClass2, null);
        assertTrue(ontology.definesSuperClassProperty(subClassId, superClass2));

    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void addSuperClassNull() {
        new Ontology().defineSuperClassProperty("Class", null);
    }

    @Test
    public void addObjectProperty() {
        Ontology ontology = new Ontology();
        String id = "Property";
        String domain = "Domain";
        String range = "Range";
        ontology.defineClass(domain);
        ontology.defineClass(range);
        ontology.defineObjectProperty(id, domain, null, range);
        assertEquals(1, ontology.propertyCount());

        Property firstOne = ontology.objectPropertyFor(id);
        ontology.defineObjectProperty(id, domain, null, range);
        Property secondOne = ontology.objectPropertyFor(id);
        assertSame(firstOne, secondOne);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void addPropertyNull() {
        Ontology ontology = new Ontology();
        String domain = "Domain";
        String label = "Label";
        String range = "Range";
        ontology.defineObjectProperty(null, domain, label, range);
    }

    @Test
    public void countAsDomain() {
        Ontology ontology = new Ontology();
        String domain = "Class";
        ontology.defineClass(domain, domain);

        int domainCount = ontology.countAsDomain(domain);
        assertEquals(0, domainCount);

        String property = "property";
        String range = "Range";
        String label = "label";
        ontology.defineObjectProperty(property, domain, label, range);
        assertEquals(1, ontology.countAsDomain(domain));

        String property2 = "property2";
        ontology.defineObjectProperty(property2, domain, label, range);
        assertEquals(2, ontology.countAsDomain(domain));

    }

    @Test
    public void propertiesForDomain() {
        Ontology ontology = new Ontology();
        String domainId = "domain";
        String rangeId = "range";
        String property1 = "property1";
        String property2 = "property2";

        ontology.defineObjectProperty(property1, domainId, null, rangeId);
        ontology.defineObjectProperty(property2, domainId, null, rangeId);

        Set<String> expected = new java.util.HashSet<String>(java.util.Arrays
                .asList(property1, property2));
        assertEquals(expected, ontology.propertyIdSetForDomain(domainId));
    }

    @Test
    public void importTo() {
        Graph graph = new Graph(new AdjListGraph());
        Ontology ontology = new Ontology();

        String domain = "Class1";
        ontology.defineClass(domain, domain);
        ontology.importTo(graph);
        assertEquals(1, graph.nodeCount());

        // String nodeLabel =
        // ((NodeLabelAttribute)graph.getNodes().iterator().next().getAttribute("label")).getLabel();
        // assertEquals(domain, nodeLabel);

        String range = "Class2";
        ontology.defineClass(range, range);
        ontology.importTo(graph);
        assertEquals(2, graph.nodeCount());

        String propertyId = "property";
        String label = "label";
        ontology.defineObjectProperty(propertyId, domain, label, range);
        ontology.importTo(graph);
        assertEquals(1, graph.edgeCount());

        String superClass = "SuperClass";
        String subClass = "SubClass";
        ontology.defineClass(superClass, superClass);
        ontology.defineClass(subClass, subClass);
        ontology.importTo(graph);
        assertEquals(4, graph.nodeCount());
        assertEquals(1, graph.edgeCount());

        ontology.defineSuperClassProperty(subClass, superClass);
        ontology.importTo(graph);
        assertEquals(2, graph.edgeCount());

        String superClass2 = "SuperClass2";
        ontology.defineClass(superClass2, null);
        ontology.importTo(graph);
        assertEquals(5, graph.nodeCount());

        ontology.defineSuperClassProperty(subClass, superClass2);
        ontology.importTo(graph);
        assertEquals(3, graph.edgeCount());

        String subClass2 = "SubClass2";
        ontology.defineClass(subClass2, null);
        ontology.importTo(graph);
        assertEquals(6, graph.nodeCount());

        ontology.defineSuperClassProperty(subClass2, superClass);
        ontology.importTo(graph);
        assertEquals(4, graph.edgeCount());
    }

    @Test
    public void importToNonEmptyGraph() {
        Graph graph = new Graph();
        graph.addNode("id", "label");
        Ontology ontology = new Ontology();
        ontology.defineClass("Class", "Class");
        ontology.importTo(graph);
        assertEquals(2, graph.nodeCount());
    }
}
