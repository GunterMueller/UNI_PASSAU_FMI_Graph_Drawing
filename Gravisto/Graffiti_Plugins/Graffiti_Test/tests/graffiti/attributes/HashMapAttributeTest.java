// =============================================================================
//
//   HashMapAttributeTest.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: HashMapAttributeTest.java 5771 2010-05-07 18:46:57Z gleissner $

package tests.graffiti.attributes;

import junit.framework.TestCase;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeExistsException;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.BooleanAttribute;
import org.graffiti.attributes.ByteAttribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.attributes.FloatAttribute;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.attributes.IllegalIdException;
import org.graffiti.attributes.IntegerAttribute;
import org.graffiti.attributes.LongAttribute;
import org.graffiti.attributes.ShortAttribute;
import org.graffiti.attributes.StringAttribute;
import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;

/**
 * DOCUMENT ME!
 * 
 * @author $Author: gleissner $
 * @version $Revision: 5771 $ $Date: 2006-01-10 12:25:10 +0100 (Di, 10 Jan 2006)
 *          $
 */
public class HashMapAttributeTest extends TestCase {

    /** The HashMapAttribute for the test cases */
    private CollectionAttribute h = new HashMapAttribute("h");

    /**
     * Constructs a test case with the given name.
     * 
     * @param name
     *            The name of the test case.
     */
    public HashMapAttributeTest(String name) {
        super(name);
    }

    /**
     * Main method for running all the test cases of this test class.
     * 
     * @param args
     *            DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(HashMapAttributeTest.class);
    }

    /**
     * Tests the add method.
     */
    public void testAdd() {
        IntegerAttribute i = new IntegerAttribute("i");

        try {
            h.add(i);
        } catch (AttributeExistsException e) {
            fail("AttributeExistsException thrown illegaly!");
        }

        assertEquals(i, h.getAttribute("i"));
    }

    /**
     * Test the AttributeExistsException when adding attributes
     */
    public void testAttributeExistsException() {
        IntegerAttribute i = new IntegerAttribute("i");
        IntegerAttribute i2 = new IntegerAttribute("i");
        h.add(i);

        try {
            h.add(i2);
            fail("Should raise an AttributeExistsException");
        } catch (AttributeExistsException e) {
        }

        CollectionAttribute hma = new HashMapAttribute("i");

        try {
            h.add(hma);
            fail("Should raise an AttributeExistsException");
        } catch (AttributeExistsException e) {
        }

        // attribute with same id allowed when not on same level
        hma = new HashMapAttribute("hma");
        hma.add(i2);
        h.add(hma);
    }

    /**
     * Test the AttributeNotFoundException
     */
    public void testAttributeNotFoundException() {
        try {
            h.remove("i");
            fail("Should raise an AttributeNotFoundException");
        } catch (AttributeNotFoundException e) {
        }

        try {
            h.remove("");
            fail("Should raise an AttributeNotFoundException");
        } catch (AttributeNotFoundException e) {
        }
    }

    /**
     * Tests the copy method.
     */
    public void testComplexCopy() {
        CollectionAttribute i = new HashMapAttribute("i");
        IntegerAttribute j = new IntegerAttribute("j");
        i.add(j);
        h.add(i);

        CollectionAttribute clone = (HashMapAttribute) (h.copy());

        assertEquals(((Integer) (h
                .getAttribute("i" + Attribute.SEPARATOR + "j").getValue()))
                .intValue(), ((Integer) (clone.getAttribute("i"
                + Attribute.SEPARATOR + "j").getValue())).intValue());
    }

    /**
     * Tests if the constructor rejects ids containing the separator char
     */
    public void testConstructor() {
        // wondering if the id is checked for correctness
        try {
            new HashMapAttribute("such.a.bad" + Attribute.SEPARATOR + "id");
            fail("Expected an IllegalIdException since an id must not contain "
                    + "the SEPARATOR character!");
        } catch (IllegalIdException e) {
        }
    }

    /**
     * Tests the copy method.
     */
    public void testCopy() {
        IntegerAttribute i = new IntegerAttribute("i");
        IntegerAttribute j = new IntegerAttribute("j");
        h.add(i);
        h.add(j);

        CollectionAttribute clone = (HashMapAttribute) (h.copy());
        assertEquals(((Integer) (h.getAttribute("i").getValue())).intValue(),
                ((Integer) (clone.getAttribute("i").getValue())).intValue());

        assertEquals(((Integer) (h.getAttribute("j").getValue())).intValue(),
                ((Integer) (clone.getAttribute("j").getValue())).intValue());
    }

    /**
     * Tests the getAttribute method
     */
    public void testGetAttribute() {
        CollectionAttribute graphics = new HashMapAttribute("graphics");
        CollectionAttribute coords = new HashMapAttribute("coords");
        BooleanAttribute round = new BooleanAttribute("round");
        BooleanAttribute square = new BooleanAttribute("square");
        BooleanAttribute high = new BooleanAttribute("high");
        IntegerAttribute oneSpaceInId = new IntegerAttribute("one space", 1);
        round.setBoolean(true);
        square.setBoolean(false);

        h.add(graphics);
        h.add(high);
        graphics.add(coords);
        coords.add(round);
        coords.add(square);
        coords.add(oneSpaceInId);

        assertTrue("attribute at coords.round should have true as value",
                ((BooleanAttribute) graphics.getAttribute("coords"
                        + Attribute.SEPARATOR + "round")).getBoolean());
        assertTrue(
                "attribute at coords.square should have false as value",
                ((BooleanAttribute) graphics.getAttribute("coords"
                        + Attribute.SEPARATOR + "square")).getBoolean() == false);
        assertEquals("coords", h.getAttribute(
                "graphics" + Attribute.SEPARATOR + "coords").getId());
        assertTrue(((Boolean) ((CollectionAttribute) ((CollectionAttribute) h
                .getAttribute("graphics")).getAttribute("coords"))
                .getAttribute("round").getValue()).booleanValue());

        assertTrue(((IntegerAttribute) h.getAttribute("graphics"
                + Attribute.SEPARATOR + "coords" + Attribute.SEPARATOR
                + "one space")).getInteger() == 1);

        try {
            h.getAttribute("coords" + Attribute.SEPARATOR + "round");
            fail("AttributeNotFoundException expected for coords.round");
        } catch (AttributeNotFoundException anfe) {
        }

        try {
            h.getAttribute("graphics" + Attribute.SEPARATOR + "coords"
                    + Attribute.SEPARATOR + "dumb");
            fail("AttributeNotFoundException expected for "
                    + "graphics.coords.dumb");
        } catch (AttributeNotFoundException anfe) {
        }

        // "high" is no CollectionAttribute ...
        try {
            h.getAttribute("high.coords.dumb");
            fail("AttributeNotFoundException expected "
                    + "for high.coords.dumb");
        } catch (AttributeNotFoundException anfe) {
        }
    }

    /**
     * Tests the add method.
     */
    public void testHashMapAttrAdd() {
        IntegerAttribute i = new IntegerAttribute("i");
        CollectionAttribute j = new HashMapAttribute("j");
        j.add(i);
        h.add(j);
        assertEquals(i, h.getAttribute("j" + Attribute.SEPARATOR + "i"));
    }

    /**
     * Tests the copy method.
     */
    public void testMoreComplexCopy() {
        // h - graphix - coords - x
        // | | L___ y
        // | L_____ shape - round
        // | | L__ box
        // | L____ dotted
        // L__graphixdesc
        CollectionAttribute graphix = new HashMapAttribute("graphix");
        StringAttribute graphixdesc = new StringAttribute("gr properties",
                "empty");
        CollectionAttribute coords = new HashMapAttribute("coords");
        IntegerAttribute x = new IntegerAttribute("x", 1);
        IntegerAttribute y = new IntegerAttribute("y");
        h.add(graphix);
        coords.add(x);
        coords.add(y);
        graphix.add(coords);
        graphix.add(graphixdesc);

        CollectionAttribute shape = new HashMapAttribute("shape");
        BooleanAttribute round = new BooleanAttribute("round", true);
        BooleanAttribute box = new BooleanAttribute("box", false);
        BooleanAttribute dotted = new BooleanAttribute("dotted", false);
        shape.add(round);
        shape.add(box);
        shape.add(dotted);
        graphix.add(shape);

        CollectionAttribute clone = (HashMapAttribute) (h.copy());

        assertEquals(
                ((Integer) (h.getAttribute("graphix" + Attribute.SEPARATOR
                        + "coords" + Attribute.SEPARATOR + "x").getValue()))
                        .intValue(), ((Integer) (clone.getAttribute("graphix"
                        + Attribute.SEPARATOR + "coords" + Attribute.SEPARATOR
                        + "x").getValue())).intValue());
        assertEquals(
                ((Integer) (h.getAttribute("graphix" + Attribute.SEPARATOR
                        + "coords" + Attribute.SEPARATOR + "y").getValue()))
                        .intValue(), ((Integer) (clone.getAttribute("graphix"
                        + Attribute.SEPARATOR + "coords" + Attribute.SEPARATOR
                        + "y").getValue())).intValue());
        assertEquals((h.getAttribute("graphix" + Attribute.SEPARATOR
                + "gr properties").getValue()), (clone.getAttribute("graphix"
                + Attribute.SEPARATOR + "gr properties").getValue()));
        assertEquals(((Boolean) (h.getAttribute("graphix" + Attribute.SEPARATOR
                + "shape" + Attribute.SEPARATOR + "round").getValue()))
                .booleanValue(), ((Boolean) (clone
                .getAttribute("graphix" + Attribute.SEPARATOR + "shape"
                        + Attribute.SEPARATOR + "round").getValue()))
                .booleanValue());
        assertEquals(((Boolean) (h.getAttribute("graphix" + Attribute.SEPARATOR
                + "shape" + Attribute.SEPARATOR + "box").getValue()))
                .booleanValue(), ((Boolean) (clone.getAttribute("graphix"
                + Attribute.SEPARATOR + "shape" + Attribute.SEPARATOR + "box")
                .getValue())).booleanValue());
        assertEquals(((Boolean) (h.getAttribute("graphix" + Attribute.SEPARATOR
                + "shape" + Attribute.SEPARATOR + "dotted").getValue()))
                .booleanValue(), ((Boolean) (clone.getAttribute("graphix"
                + Attribute.SEPARATOR + "shape" + Attribute.SEPARATOR
                + "dotted").getValue())).booleanValue());
    }

    /**
     * Tests the remove(String id) method
     */
    public void testRemove1() {
        h.add(new IntegerAttribute("ia"));
        h.add(new BooleanAttribute("ba"));
        h.add(new StringAttribute("sa"));
        h.add(new DoubleAttribute("da"));
        h.add(new FloatAttribute("fa"));
        h.add(new ShortAttribute("sha"));
        h.add(new LongAttribute("la"));
        h.add(new ByteAttribute("bya"));

        CollectionAttribute hma = new HashMapAttribute("hma");
        hma.add(new IntegerAttribute("ia2"));
        h.add(hma);

        assertTrue("root attribute should not be empty!", !h.isEmpty());

        h.remove("ia");
        h.remove("ba");
        h.remove("sa");
        h.remove("da");
        h.remove("fa");
        h.remove("hma");
        h.remove("sha");
        h.remove("la");
        h.remove("bya");
        assertTrue("HashMap should be empty after removes", h.isEmpty());
    }

    /**
     * Test the remove(Attribute a) method
     */
    public void testRemove2() {
        IntegerAttribute ia = new IntegerAttribute("ia");
        FloatAttribute fa = new FloatAttribute("fa");
        DoubleAttribute da = new DoubleAttribute("da");
        StringAttribute sa = new StringAttribute("sa");
        BooleanAttribute ba = new BooleanAttribute("ba");
        ShortAttribute sha = new ShortAttribute("sha");
        LongAttribute la = new LongAttribute("la");
        ByteAttribute bya = new ByteAttribute("bya");
        CollectionAttribute hma = new HashMapAttribute("hma");
        IntegerAttribute ia2 = new IntegerAttribute("ia2");

        h.add(ia);
        h.add(fa);
        h.add(da);
        h.add(sa);
        h.add(ba);
        hma.add(ia2);
        h.add(sha);
        h.add(la);
        hma.add(bya);
        h.add(hma);

        assertTrue("HashMap should not be empty!", !h.isEmpty());

        h.remove(ia);
        h.remove(fa);
        h.remove(da);
        h.remove(sa);
        h.remove(ba);
        h.remove(hma);
        h.remove(sha);
        h.remove(la);
        h.remove(bya);

        assertTrue("HashMap should be empty after removes", h.isEmpty());
    }

    /**
     * Tests the setCollection method.
     */
    public void testSetCollection() {
        CollectionAttribute bends = new HashMapAttribute("bends");
        h.add(bends);
        bends.add(new CoordinateAttribute("coo1"), false);
        bends.add(new CoordinateAttribute("coo2"), false);

        Graph g = new AdjListGraph();
        Node n1 = g.addNode();
        g.addNode(h);

        // next line is the actual test!
        n1.getAttributes().setCollection(bends.getCollection());
    }

    /**
     * Tests several attribute operations.
     */
    public void testSeveralAttributeOperations() {
        CollectionAttribute i = new HashMapAttribute("i");
        h.add(i);
        assertTrue("The HashMap attribute should not be empty.", !h.isEmpty());

        CollectionAttribute icopy = (HashMapAttribute) h.copy();
        assertTrue("The HashMap attribute icopy should not be empty.", !icopy
                .isEmpty());
        icopy.remove("i");
        assertTrue("The HashMap attribute icopy should be empty.", icopy
                .isEmpty());
    }

    /**
     * Initializes a new HashMapAttribute for every TestCase
     */
    protected void setUp() {
        h = new HashMapAttribute("h");
    }

    // pph: sollte man nochmal klaeren, was damit gemeint war ... :
    // /**
    // * Tests, if a cloned HashMap attribute's contents can be modified
    // * outside of the collection attribute, and if there are any
    // * dependencies due to an incorrect copy operation.
    // */
    // public void testHashMapCloneContents() {
    // CollectionAttribute i = new HashMapAttribute("i");
    // h.add(i);
    // CollectionAttribute clone = (HashMapAttribute)h.copy();
    // CollectionAttribute other = (HashMapAttribute)h.getAttribute("i");
    // other = null;
    // other = (HashMapAttribute)h.getAttribute("i");
    // assertTrue("other should be null, should'nt it?", other == null);
    // assertTrue("there should still be a non null i attribute in clone",
    // clone.getAttribute("i") != null);
    // }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
