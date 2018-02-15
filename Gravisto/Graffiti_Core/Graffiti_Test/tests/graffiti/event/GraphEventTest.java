// =============================================================================
//
//   GraphEventTest.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphEventTest.java 5771 2010-05-07 18:46:57Z gleissner $

package tests.graffiti.event;

import junit.framework.TestCase;

import org.graffiti.event.GraphEvent;
import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * Tests class GraphEvent.
 * 
 * @version $Revision: 5771 $
 */
public class GraphEventTest extends TestCase {

    /** DOCUMENT ME! */
    TestGraphListener graphListener;

    /**
     * Constructs a test case with the given name.
     * 
     * @param name
     *            The name of the test case.
     */
    public GraphEventTest(String name) {
        super(name);
    }

    /**
     * Main method for running all the test cases of this test class.
     * 
     * @param args
     *            DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(GraphEventTest.class);
    }

    /**
     * Tests the number of times, a graph event method has been called. and the
     * order of listener notification with this graph event.
     */
    public void testGraphEventCalled() {
        Graph g = new AdjListGraph();
        g.getListenerManager().addStrictGraphListener(graphListener);

        Node n1 = g.addNode();

        assertEquals("Didn't call the graph listerner's method exactly twice!",
                2, graphListener.called);

        Edge e = g.addEdge(n1, n1, Edge.DIRECTED);

        assertEquals("Didn't call the graph listerner's method exactly twice!",
                4, graphListener.called);

        assertEquals("Did not call preNodeAdded first.", "preNodeAdded",
                graphListener.methodsCalled.get(0));
        assertEquals("Did not call postNodeAdded after " + "preNodeAdded.",
                "postNodeAdded", graphListener.methodsCalled.get(1));
        assertEquals("Did not call preEdgeAdded first.", "preEdgeAdded",
                graphListener.methodsCalled.get(2));
        assertEquals("Did not call postEdgeAdded after " + "preEdgeAdded.",
                "postEdgeAdded", graphListener.methodsCalled.get(3));

        g.deleteEdge(e);

        assertEquals("Didn't call the graph listerner's method exactly twice!",
                6, graphListener.called);

        g.deleteNode(n1);

        assertEquals("Didn't call the graph listerner's method exactly twice!",
                8, graphListener.called);

        assertEquals("Did not call preEdgeRemoved first.", "preEdgeRemoved",
                graphListener.methodsCalled.get(4));
        assertEquals("Did not call postEdgeRemoved after " + "preEdgeRemoved.",
                "postEdgeRemoved", graphListener.methodsCalled.get(5));
        assertEquals("Did not call preNodeRemoved first.", "preNodeRemoved",
                graphListener.methodsCalled.get(6));
        assertEquals("Did not call postNodeRemoved after " + "preNodeRemoved.",
                "postNodeRemoved", graphListener.methodsCalled.get(7));

        g.addNode();

        g.clear();

        assertEquals("Didn't call the graph listerner's method exactly twice!",
                12, graphListener.called);

        assertEquals("Did not call preGraphCleared first.", "preGraphCleared",
                graphListener.methodsCalled.get(10));

        assertEquals("Did not call postGraphCleared after "
                + "preGraphCleared.", "postGraphCleared",
                graphListener.methodsCalled.get(11));
    }

    /**
     * Tests the graph event constructor with one edge as parameter.
     */
    public void testGraphEventConstructorWithOneEdge() {
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();
        Edge edge = graph.addEdge(node, node, Edge.DIRECTED);

        GraphEvent event = new GraphEvent(edge);

        // assertEquals("Failed to create a correct GraphEvent." +
        // "The graph reference is not the same", graph, event.getGraph());
        assertEquals("Failed to create a correct GraphEvent."
                + "The edge reference is not the same", edge, event.getEdge());
    }

    /**
     * Tests the graph event constructor with one node as parameter.
     */
    public void testGraphEventConstructorWithOneNode() {
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();

        GraphEvent event = new GraphEvent(node);

        // assertEquals("Failed to create a correct GraphEvent." +
        // "The graph reference is not the same", graph, event.getGraph());
        assertEquals("Failed to create a correct GraphEvent."
                + "The node reference is not the same", node, event.getNode());
    }

    /**
     * Tests the graph event constructor with two nodes as parameters.
     */
    public void testGraphEventConstructorWithTwoNodes() {
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();
        Node secondNode = graph.addNode();

        GraphEvent event = new GraphEvent(node, secondNode);

        // assertEquals("Failed to create a correct GraphEvent." +
        // "The graph reference is not the same", graph, event.getGraph());
        assertEquals("Failed to create a correct GraphEvent."
                + "The node reference is not the same", node, event.getNode());
        assertEquals("Failed to create a correct GraphEvent."
                + "The second node reference is not the same", secondNode,
                event.getSecondNode());
    }

    /**
     * Sets up the text fixture. Called before every test case method.
     */
    @Override
    protected void setUp() {
        graphListener = new TestGraphListener();
    }

    /**
     * Tears down the text fixture. Called after all test case methods hava run.
     */
    @Override
    protected void tearDown() {
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
