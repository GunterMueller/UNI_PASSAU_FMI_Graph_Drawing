// =============================================================================
//
//   AbstractAttributableTest.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractAttributableTest.java 5771 2010-05-07 18:46:57Z gleissner $

package tests.graffiti.attributes;

import junit.framework.TestCase;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeExistsException;
import org.graffiti.attributes.BooleanAttribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.attributes.IntegerAttribute;
import org.graffiti.attributes.NoCollectionAttributeException;
import org.graffiti.attributes.StringAttribute;
import org.graffiti.graph.AdjListGraph;

/**
 * DOCUMENT ME!
 * 
 * @author $Author: gleissner $
 * @version $Revision: 5771 $ $Date: 2006-01-10 12:25:10 +0100 (Di, 10 Jan 2006)
 *          $
 */
public class AbstractAttributableTest extends TestCase {

    /**
     * Constructs a test case with the given name.
     * 
     * @param name
     *            The name of the test case.
     */
    public AbstractAttributableTest(String name) {
        super(name);
    }

    /**
     * Main method for running all the test cases of this test class.
     * 
     * @param args
     *            DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(AbstractAttributableTest.class);
    }

    /**
     * Tests the add/get/setBoolean methods of <code>Attributable</code>.
     */
    public void testAddGetSetBoolean() {
        AdjListGraph g = new AdjListGraph();
        g.addBoolean("", "cool", true);
        assertEquals(true, g.getBoolean("cool"));
        g.setBoolean("cool", false);
        assertEquals(false, g.getBoolean("cool"));
    }

    /**
     * Tests the add/get/set/changeInteger methods of <code>Attributable</code>.
     */
    public void testAddGetSetChangeInteger() {
        AdjListGraph g = new AdjListGraph();
        g.addInteger("", "id", 1);
        assertEquals(1, g.getInteger("id"));
        g.changeInteger("id", 10);
        assertEquals(10, g.getInteger("id"));
        g.setInteger("id", 4711);
        assertEquals(4711, g.getInteger("id"));

        try {
            g.setInteger("id" + Attribute.SEPARATOR + "name", 1234);
            fail("an exception should have been thrown (yet I do not know "
                    + "which one ;-) you tried to add an attribute who's path "
                    + "partially exists but that part is no CollectionAttribute");
        } catch (NoCollectionAttributeException e) {
        }
    }

    /**
     * Tests the add/get/setDouble methods of <code>Attributable</code>.
     */
    public void testAddGetSetDouble() {
        AdjListGraph g = new AdjListGraph();
        g.addDouble("", "id", 3.415);
        assertEquals(3.415, g.getDouble("id"), 0.000000001);
        g.setDouble("id", 1.0);
        assertEquals(1.0, g.getDouble("id"), 0.0000000001);
    }

    /**
     * Tests the add/get/setFloat methods of <code>Attributable</code>.
     */
    public void testAddGetSetFloat() {
        AdjListGraph g = new AdjListGraph();
        g.addFloat("", "x", 15.3f);
        assertEquals(15.3f, g.getFloat("x"), 0.0000000001);
        g.setFloat("x", 1.0f);
        assertEquals(1.0f, g.getFloat("x"), 0.00000000001);
    }

    /**
     * Tests the add/get/setInteger methods of <code>Attributable</code>.
     */
    public void testAddGetSetIntegerComplex() {
        AdjListGraph g = new AdjListGraph();
        g.setInteger("id" + Attribute.SEPARATOR + "fett" + Attribute.SEPARATOR
                + "krass" + Attribute.SEPARATOR + "tief" + Attribute.SEPARATOR
                + "pfad", 10);
        assertEquals(10, g.getInteger("id" + Attribute.SEPARATOR + "fett"
                + Attribute.SEPARATOR + "krass" + Attribute.SEPARATOR + "tief"
                + Attribute.SEPARATOR + "pfad"));
        g.setInteger("id" + Attribute.SEPARATOR + "fett" + Attribute.SEPARATOR
                + "krass" + Attribute.SEPARATOR + "tief" + Attribute.SEPARATOR
                + "er", 20);
        assertEquals(20, g.getInteger("id" + Attribute.SEPARATOR + "fett"
                + Attribute.SEPARATOR + "krass" + Attribute.SEPARATOR + "tief"
                + Attribute.SEPARATOR + "er"));

        try {
            g.addInteger("id" + Attribute.SEPARATOR + "fett"
                    + Attribute.SEPARATOR + "krass" + Attribute.SEPARATOR
                    + "tief" + Attribute.SEPARATOR + "er", "pfad", 30);
            fail("Should have thrown a NoCollectionAttributeException");
        } catch (NoCollectionAttributeException e) {
        }
    }

    /**
     * Tests the add/get/setString methods of <code>Attributable</code>.
     */
    public void testAddGetSetString() {
        AdjListGraph g = new AdjListGraph();
        g.addAttribute(new HashMapAttribute("this"), "");
        g.addString("this", "id", "testGraph");
        assertEquals("testGraph", g.getString("this" + Attribute.SEPARATOR
                + "id"));
        g.setString("this" + Attribute.SEPARATOR + "id", "graph 1");
        assertEquals("graph 1", g
                .getString("this" + Attribute.SEPARATOR + "id"));
    }

    /**
     * Tests the add/removeAttribute methods of <code>Attributable</code>.
     */
    public void testAddRemoveAttribute() {
        AdjListGraph g = new AdjListGraph();
        IntegerAttribute ia = new IntegerAttribute("test");
        g.addAttribute(ia, "");
        assertEquals(ia, g.getAttribute("test"));
        g.removeAttribute("test");

        CollectionAttribute ca = (CollectionAttribute) g.getAttribute("");
        ca.remove("directed");
        assertTrue("Should be empty!", ca.isEmpty());

        DoubleAttribute da = new DoubleAttribute("test2", 5.45);
        g.addAttribute(da, "");
        assertEquals(da.getId(), g.getAttribute("test2").getId());

        StringAttribute sa = new StringAttribute("test3", "does it work");

        try {
            g.addAttribute(sa, "test2");
            fail("Should have thrown a NoCollectionAttributeException");
        } catch (NoCollectionAttributeException e) {
        }
    }

    /**
     * Test if addAttribute throws a AttributeExistsException if necessary.
     */
    public void testAttributeExistesException() {
        AdjListGraph g = new AdjListGraph();
        BooleanAttribute ba = new BooleanAttribute("hier", false);
        StringAttribute sa = new StringAttribute("hier", "auch noch");
        g.addAttribute(ba, "");

        try {
            g.addAttribute(sa, "");
            fail("Should have thrown an AttributeExistsException!");
        } catch (AttributeExistsException e) {
        }
    }

    /**
     * Tests if addAttribute throws a NoCollectionAttributeException if
     * necessary.
     */
    public void testNoCollectionAttributeException() {
        AdjListGraph g = new AdjListGraph();
        BooleanAttribute ba = new BooleanAttribute("hier", false);
        StringAttribute sa = new StringAttribute("hier", "auch noch");
        g.addAttribute(ba, "");

        try {
            g.addAttribute(sa, "hier");
            fail("Should have thrown a NoCollectionAttributeException!");
        } catch (NoCollectionAttributeException e) {
        }
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
