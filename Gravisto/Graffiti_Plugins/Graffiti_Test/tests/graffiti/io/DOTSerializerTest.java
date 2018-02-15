// =============================================================================
//
//   DOTSerializerTest.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DOTSerializerTest.java 5771 2010-05-07 18:46:57Z gleissner $

package tests.graffiti.io;

import junit.framework.TestCase;

import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * A test suite for the dot serializer.
 * 
 * @version $Revision: 5771 $
 */
public class DOTSerializerTest extends TestCase {
    /** The graph for the tests. */
    Graph g;

    /**
     * Constructs a new test class for the <code>DOTSerializer</code> class.
     * 
     * @param name
     *            the name for the test case.
     */
    public DOTSerializerTest(String name) {
        super(name);
    }

    /**
     * Runs the test case.
     * 
     * @param args
     *            DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(DOTSerializerTest.class);
    }

    /**
     * DOCUMENT ME!
     */
    public void testSimpleGraphWriter() {
        // nothing yet
    }

    /**
     * Initializes a new graph for every test case.
     */
    @Override
    protected void setUp() {
        g = new AdjListGraph();
        g.setString("id", "my_graph");

        Node n1 = g.addNode();
        n1.setInteger("id", 1);
        n1.setString("label", "Start");

        Node n2 = g.addNode();
        n2.setInteger("id", 2);
        n2.setString("label", "Stop");

        Edge e1 = g.addEdge(n1, n1, Edge.DIRECTED);
        e1.setInteger("id", 1);
        e1.setString("label", "go");

        Edge e2 = g.addEdge(n1, n2, Edge.UNDIRECTED);
        e2.setInteger("id", 2);
        e2.setString("label", "halt");
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
