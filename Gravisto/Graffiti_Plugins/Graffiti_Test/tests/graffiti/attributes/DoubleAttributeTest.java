// =============================================================================
//
//   DoubleAttributeTest.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DoubleAttributeTest.java 5771 2010-05-07 18:46:57Z gleissner $

package tests.graffiti.attributes;

import junit.framework.TestCase;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Graph;

/**
 * DOCUMENT ME!
 * 
 * @author $Author: gleissner $
 * @version $Revision: 5771 $ $Date: 2006-01-10 12:25:10 +0100 (Di, 10 Jan 2006)
 *          $
 */
public class DoubleAttributeTest extends TestCase {

    /** The tolerance value concerned when comparing two doubles */
    double delta = java.lang.Double.MIN_VALUE;

    /** A DoubleAttribute for all test cases. */
    private DoubleAttribute d;

    /**
     * Constructs a test case with the given name.
     * 
     * @param name
     *            The name of the test case.
     */
    public DoubleAttributeTest(String name) {
        super(name);
    }

    /**
     * Main method for running all the test cases of this test class.
     * 
     * @param args
     *            DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(DoubleAttributeTest.class);
    }

    /**
     * Tests if the comfortable methods correspond to the basic methods.
     */
    public void testAttributesAccessDouble2() {
        Graph g = new AdjListGraph();
        double v = 10.3;
        g.getAttributes().getAttributable().setDouble(
                "Hallo" + Attribute.SEPARATOR + "Test", v);

        double d = ((Double) (g.getAttributes().getAttribute("Hallo")
                .getAttributable().getAttribute(
                        "Hallo" + Attribute.SEPARATOR + "Test").getValue()))
                .doubleValue();
        assertEquals(v, d, java.lang.Double.MIN_VALUE);
    }

    /**
     * Tests the copy method.
     */
    public void testCopy() {
        d.setDouble(10);

        DoubleAttribute clone = (DoubleAttribute) (d.copy());
        assertEquals(d.getDouble(), clone.getDouble(), delta);
    }

    /**
     * Tests the getDouble() and SetDouble() methods.
     */
    public void testGetSetDouble() {
        d.setDouble(10.5);
        assertEquals(10.5, d.getDouble(), delta);
    }

    /**
     * Tests the getValue() and setValue() methods.
     */
    public void testGetSetValue() {
        d.setValue(new Double(10.5));
        assertEquals(10.5, ((Double) d.getValue()).doubleValue(), delta);
    }

    /**
     * Tests the constructor that sets the value.
     */
    public void testValueConstructor() {
        DoubleAttribute test = new DoubleAttribute("test", 1.985538);
        assertEquals(1.985538, test.getDouble(), delta);
    }

    /**
     * Initializes a DoubleAttribute for all test cases.
     */
    @Override
    protected void setUp() {
        d = new DoubleAttribute("d");
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
