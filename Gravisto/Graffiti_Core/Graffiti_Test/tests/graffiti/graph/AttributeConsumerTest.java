// =============================================================================
//
//   AttributeConsumerTest.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AttributeConsumerTest.java 5771 2010-05-07 18:46:57Z gleissner $

/*
 * $Id: AttributeConsumerTest.java 5771 2010-05-07 18:46:57Z gleissner $
 */

package tests.graffiti.graph;

import java.awt.Color;

import junit.framework.TestCase;

import org.graffiti.attributes.AttributeConsumer;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.attributes.StringAttribute;
import org.graffiti.attributes.UnificationException;
import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;

import tests.graffiti.attributes.AbstractAttributableTest;

/**
 * Tests the behavior of the attribute consumer mechanism.
 * 
 * @version $Revision: 5771 $
 */
public class AttributeConsumerTest extends TestCase {

    /**
     * Constructs a test case with the given name.
     * 
     * @param name
     *            The name of the test case.
     */
    public AttributeConsumerTest(String name) {
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
     * Tests the unification algorithm.
     */
    public void test1() {
        Graph g = new AdjListGraph();

        try {
            g.addAttributeConsumer(new A());
        } catch (UnificationException ue) {
            assertTrue("UnificationException should not be thrown: "
                    + ue.getMessage(), false);
        }

        try {
            g.addAttributeConsumer(new B());
        } catch (UnificationException ue) {
            assertTrue("UnificationException should not be thrown: "
                    + ue.getMessage(), false);
        }

        Node a = g.addNode();

        try {
            a.getAttribute("graphics.mycolor");
        } catch (AttributeNotFoundException anfe) {
            assertTrue(anfe.getMessage(), false);
        } catch (ClassCastException cce) {
            assertTrue(cce.getMessage(), false);
        }
    }

    /**
     * Tests the unification algorithm.
     */
    public void test2() {
        Graph g = new AdjListGraph();

        try {
            g.addAttributeConsumer(new B());
        } catch (UnificationException ue) {
            assertTrue("UnificationException should not be thrown: "
                    + ue.getMessage(), false);
        }

        try {
            g.addAttributeConsumer(new A());
        } catch (UnificationException ue) {
            assertTrue("UnificationException should not be thrown: "
                    + ue.getMessage(), false);
        }

        Node a = g.addNode();

        try {
            a.getAttribute("graphics.mycolor");
        } catch (AttributeNotFoundException anfe) {
            assertTrue(anfe.getMessage(), false);
        } catch (ClassCastException cce) {
            assertTrue(cce.getMessage(), false);
        }
    }

    /**
     * Tests, if the <code>Graph.addAtributeConsumer</code> method adds all
     * attributes of the new consumer to the current graph objects (nodes and
     * edges) correctly.
     */
    public void test3() {
        Graph g = new AdjListGraph();
        Node a = g.addNode();

        try {
            g.addAttributeConsumer(new B());
        } catch (UnificationException ue) {
            assertTrue("UnificationException should not be thrown: "
                    + ue.getMessage(), false);
        }

        try {
            a.getAttribute("graphics.mycolor");
        } catch (AttributeNotFoundException anfe) {
            assertTrue("g.addNode() did not create the default attributes "
                    + "for the new node: " + anfe.getMessage(), false);
        } catch (ClassCastException cce) {
            assertTrue(cce.getMessage(), false);
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @author $Author: gleissner $
     * @version $Revision: 5771 $ $Date: 2008-01-02 15:54:46 +0100 (Mi, 02 Jan
     *          2008) $
     */
    class A implements AttributeConsumer {
        /**
         * @see org.graffiti.attributes.AttributeConsumer#getUndirectedEdgeAttribute()
         */
        public CollectionAttribute getUndirectedEdgeAttribute() {
            return new EdgeGraphicAttribute(false);
        }

        /**
         * @see org.graffiti.attributes.AttributeConsumer#getDirectedEdgeAttribute()
         */
        public CollectionAttribute getDirectedEdgeAttribute() {
            return new EdgeGraphicAttribute(true);
        }

        /**
         * @see org.graffiti.attributes.AttributeConsumer#getGraphAttribute()
         */
        public CollectionAttribute getGraphAttribute() {
            return null;
        }

        /**
         * @see org.graffiti.attributes.AttributeConsumer#getNodeAttribute()
         */
        public CollectionAttribute getNodeAttribute() {
            return new NodeGraphicAttribute();
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @author $Author: gleissner $
     * @version $Revision: 5771 $ $Date: 2008-01-02 15:54:46 +0100 (Mi, 02 Jan
     *          2008) $
     */
    class B implements AttributeConsumer {
        /**
         * @see org.graffiti.attributes.AttributeConsumer#getUndirectedEdgeAttribute()
         */
        public CollectionAttribute getUndirectedEdgeAttribute() {
            return null;
        }

        /**
         * @see org.graffiti.attributes.AttributeConsumer#getDirectedEdgeAttribute()
         */
        public CollectionAttribute getDirectedEdgeAttribute() {
            return null;
        }

        /**
         * @see org.graffiti.attributes.AttributeConsumer#getGraphAttribute()
         */
        public CollectionAttribute getGraphAttribute() {
            return null;
        }

        /**
         * @see org.graffiti.attributes.AttributeConsumer#getNodeAttribute()
         */
        public CollectionAttribute getNodeAttribute() {
            CollectionAttribute c = new HashMapAttribute("graphics");
            c.add(new ColorAttribute("mycolor", Color.PINK));

            return c;
        }
    }

    /**
     * His consumer contains an attribute, which conflicts with the
     * <code>ColorAttribute</code> in consumer <code>B</code>. This consumer is
     * compatible with consumer <code>A</code>, but not consumer <code>B</code>.
     */
    class C implements AttributeConsumer {
        /**
         * @see org.graffiti.attributes.AttributeConsumer#getUndirectedEdgeAttribute()
         */
        public CollectionAttribute getUndirectedEdgeAttribute() {
            return null;
        }

        /**
         * @see org.graffiti.attributes.AttributeConsumer#getDirectedEdgeAttribute()
         */
        public CollectionAttribute getDirectedEdgeAttribute() {
            return null;
        }

        /**
         * @see org.graffiti.attributes.AttributeConsumer#getGraphAttribute()
         */
        public CollectionAttribute getGraphAttribute() {
            return null;
        }

        /**
         * @see org.graffiti.attributes.AttributeConsumer#getNodeAttribute()
         */
        public CollectionAttribute getNodeAttribute() {
            CollectionAttribute c = new HashMapAttribute("graphics");
            c.add(new StringAttribute("mycolor", "#p i n k"));

            return c;
        }
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
