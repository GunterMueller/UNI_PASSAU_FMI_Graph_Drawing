// =============================================================================
//
//   NodeEventTest.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: NodeEventTest.java 5771 2010-05-07 18:46:57Z gleissner $

package tests.graffiti.event;

import junit.framework.TestCase;

import org.graffiti.event.NodeEvent;
import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * Tests class NodeEvent.
 * 
 * @version $Revision: 5771 $
 */
public class NodeEventTest extends TestCase {

    /** DOCUMENT ME! */
    private TestNodeListener nodeListener;

    /**
     * Constructs a test case with the given name.
     * 
     * @param name
     *            The name of the test case.
     */
    public NodeEventTest(String name) {
        super(name);
    }

    /**
     * Main method for running all the test cases of this test class.
     * 
     * @param args
     *            DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(NodeEventTest.class);
    }

    /**
     * Test the number of times, a node event method has been called and the
     * order of methods invocation.
     */
    public void testNodeEventCalled() {
        Graph g = new AdjListGraph();
        g.getListenerManager().addStrictNodeListener(nodeListener);

        Node n1 = g.addNode();
        Node n2 = g.addNode();

        Edge e1 = g.addEdge(n1, n2, Edge.UNDIRECTED);
        assertEquals("Didn't call the node listener four times (twice for "
                + "each node)!", 4, nodeListener.called);
        assertEquals("Did not call preUndirectedEdgeAdded first.",
                "preUndirectedEdgeAdded", nodeListener.methodsCalled.get(0));
        assertEquals("Did not call postUndirectedEdgeAdded after "
                + "preUndirectedEdgeAdded.", "postUndirectedEdgeAdded",
                nodeListener.methodsCalled.get(1));

        assertEquals("Did not call preUndirectedEdgeAdded first.",
                "preUndirectedEdgeAdded", nodeListener.methodsCalled.get(2));
        assertEquals("Did not call postUndirectedEdgeAdded after "
                + "preUndirectedEdgeAdded.", "postUndirectedEdgeAdded",
                nodeListener.methodsCalled.get(3));

        Edge e2 = g.addEdge(n2, n1, Edge.DIRECTED);
        assertEquals("Didn't call the node listener another four times.", 8,
                nodeListener.called);

        assertEquals("Did not call preOutEdgeAdded first.", "preOutEdgeAdded",
                nodeListener.methodsCalled.get(4));
        assertEquals("Did not call postOutEdgeAdded after "
                + "preOutEdgeAdded.", "postOutEdgeAdded",
                nodeListener.methodsCalled.get(5));

        assertEquals("Did not call preInEdgeAdded first.", "preInEdgeAdded",
                nodeListener.methodsCalled.get(6));
        assertEquals("Did not call postInEdgeAdded after " + "preInEdgeAdded.",
                "postInEdgeAdded", nodeListener.methodsCalled.get(7));

        e1.reverse();
        assertEquals("The node listener should have been called 8 times "
                + "(two times per node for deleting the edge, and two "
                + "times per node for adding the edge in the right list)", 16,
                nodeListener.called);

        // assertEquals("Called the node listener although you shouldn't have.",
        // 8, nodeListener.called);
        e2.reverse();
        assertEquals("The node listener should have been called 8 times "
                + "(two times per node for deleting the edge, and two "
                + "times per node for adding the edge in the right list)", 24,
                nodeListener.called);

        assertEquals("Did not call preOutEdgeRemoved first.",
                "preOutEdgeRemoved", nodeListener.methodsCalled.get(16));
        assertEquals("Did not call postOutEdgeRemoved after "
                + "preOutEdgeRemoved.", "postOutEdgeRemoved",
                nodeListener.methodsCalled.get(17));

        assertEquals("Did not call preOutEdgeAdded first.", "preOutEdgeAdded",
                nodeListener.methodsCalled.get(18));
        assertEquals("Did not call postOutEdgeAdded after "
                + "preOutEdgeAdded.", "postOutEdgeAdded",
                nodeListener.methodsCalled.get(19));

        assertEquals("Did not call preInEdgeRemoved first.",
                "preInEdgeRemoved", nodeListener.methodsCalled.get(20));
        assertEquals("Did not call postInEdgeRemoved after "
                + "preInEdgeRemoved.", "postInEdgeRemoved",
                nodeListener.methodsCalled.get(21));

        assertEquals("Did not call preInEdgeAdded first.", "preInEdgeAdded",
                nodeListener.methodsCalled.get(22));
        assertEquals("Did not call postInEdgeAdded after " + "preInEdgeAdded.",
                "postInEdgeAdded", nodeListener.methodsCalled.get(23));
    }

    /**
     * Tests the NodeEvent constructor.
     */
    public void testNodeEventConstructor() {
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();
        Edge edge = graph.addEdge(node, node, Edge.DIRECTED);

        NodeEvent event = new NodeEvent(node, edge);

        assertEquals("Failed to create a correct NodeEvent."
                + "The node reference is not the same", node, event.getNode());
        assertEquals("Failed to create a correct NodeEvent."
                + "The edge reference is not the same", edge, event.getEdge());
    }

    /**
     * Sets up the text fixture. Called before every test case method.
     */
    @Override
    protected void setUp() {
        nodeListener = new TestNodeListener();
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
