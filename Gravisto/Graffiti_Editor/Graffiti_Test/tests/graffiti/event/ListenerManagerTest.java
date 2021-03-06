// =============================================================================
//
//   ListenerManagerTest.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ListenerManagerTest.java 5771 2010-05-07 18:46:57Z gleissner $

package tests.graffiti.event;

import junit.framework.TestCase;

import org.graffiti.attributes.BooleanAttribute;
import org.graffiti.event.AttributeEvent;
import org.graffiti.event.AttributeListener;
import org.graffiti.event.EdgeEvent;
import org.graffiti.event.EdgeListener;
import org.graffiti.event.GraphEvent;
import org.graffiti.event.GraphListener;
import org.graffiti.event.ListenerManager;
import org.graffiti.event.ListenerNotFoundException;
import org.graffiti.event.NodeEvent;
import org.graffiti.event.NodeListener;
import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElementNotFoundException;
import org.graffiti.graph.Node;

/**
 * Tests class ListenerManager.
 * 
 * @version $Revision: 5771 $
 */
public class ListenerManagerTest extends TestCase {

    /** DOCUMENT ME! */
    private AttributeEvent attrEvent;

    /** DOCUMENT ME! */
    private AttributeListener attrListener;

    /** DOCUMENT ME! */
    private EdgeEvent edgeEvent;

    /** DOCUMENT ME! */
    private EdgeListener edgeListener;

    /** DOCUMENT ME! */
    private GraphEvent graphEvent;

    /** DOCUMENT ME! */
    private GraphListener graphListener;

    /** DOCUMENT ME! */
    private ListenerManager lm;

    /** DOCUMENT ME! */
    private NodeEvent nodeEvent;

    /** DOCUMENT ME! */
    private NodeListener nodeListener;

    /**
     * Constructs a test case with the given name.
     * 
     * @param name
     *            The name of the test case.
     */
    public ListenerManagerTest(String name) {
        super(name);
    }

    /**
     * Main method for running all the test cases of this test class.
     * 
     * @param args
     *            DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(ListenerManagerTest.class);
    }

    // ---------------------- AttributeListener ----------------------

    /**
     * Tests the addStrictAttributeListener method by adding an
     * AttributeListener and seeing if it receives events
     */
    public void testAddAttributeListener() {
        lm.addStrictAttributeListener(attrListener);
        lm.preAttributeChanged(new AttributeEvent(new BooleanAttribute("id")));
        assertTrue("added an AttributeListener but didn't seem to work ...",
                ((TestAttrListener) attrListener).lastMethodCalled
                        .equals("preAttributeChanged"));
    }

    // ---------------------- EdgeListener ----------------------

    /**
     * Tests the addStrictEdgeListener method by adding an EdgeListener and
     * seeing if he receives events
     */
    public void testAddEdgeListener() {
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();

        Edge edge = null;

        try {
            edge = graph.addEdge(node, node, Edge.DIRECTED);
        } catch (GraphElementNotFoundException e) {
        }

        lm.addStrictEdgeListener(edgeListener);
        lm.preSourceNodeChanged(new EdgeEvent(edge));
        assertTrue("added an EdgeListener but didn't seem to work ...",
                ((TestEdgeListener) edgeListener).lastMethodCalled
                        .equals("preSourceNodeChanged"));
    }

    // ---------------------- GraphListener ----------------------

    /**
     * Tests the addStrictGraphListener method by adding a GraphListener and
     * seeing if it receives events
     */
    public void testAddGraphListener() {
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();

        lm.addStrictGraphListener(graphListener);
        lm.preEdgeAdded(new GraphEvent(node));
        assertTrue("added a GraphListener but didn't seem to work ...",
                ((TestGraphListener) graphListener).lastMethodCalled
                        .equals("preEdgeAdded"));
    }

    // ---------------------- NodeListener ----------------------

    /**
     * Tests the addStrictNodeListener method by adding a NodeListener and
     * seeing if he receives events
     */
    public void testAddNodeListener() {
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();

        Edge edge = null;

        try {
            edge = graph.addEdge(node, node, Edge.DIRECTED);
        } catch (GraphElementNotFoundException e) {
        }

        lm.addStrictNodeListener(nodeListener);
        lm.preInEdgeAdded(new NodeEvent(node, edge));
        assertTrue("added a NodeListener but didn't seem to work ...",
                ((TestNodeListener) nodeListener).lastMethodCalled
                        .equals("preInEdgeAdded"));
    }

    // ------------------ Tests all methods ListenerManager knows of
    // -------------
    // Testprototyp fuer copy paste ;) zum Wegschmeissen ....
    // /**
    // * Tests if the method calls the same method in all relevant listeners.
    // */
    // public void testPrototyp() {
    // lm.addAttributeListener(attrListener);
    // lm.addEdgeListener(edgeListener);
    // lm.addStrictNodeListener(nodeListener);
    // lm.addGraphListener(graphListener);
    // // generate EdgeEvent
    // Graph graph = new AdjListGraph();
    // Node node = new AdjListNode(graph);
    // Edge edge = new AdjListEdge(graph, node, node, true);
    // edgeEvent = new EdgeEvent(edge);
    // // generate AttributeEvent
    // attrEvent = new AttributeEvent(new BooleanAttribute("id"));
    // // generate NodeEvent
    // Graph graph = new AdjListGraph();
    // Node node = new AdjListNode(graph);
    // Edge edge = new AdjListEdge(graph, node, node,true);
    // nodeEvent = new NodeEvent(node,edge);
    // // generate GraphEvent
    // Graph graph = new AdjListGraph();
    // Node node = new AdjListNode(graph);
    // graphEvent = new GraphEvent(node);
    // lm.$($Event);
    // assertTrue("Did not call method in the correct (Attribute) Listener",
    // ((TestAttrListener)attrListener).lastMethodCalled.equals("$"));
    // assertTrue("Unnecessarily called in EdgeListener",
    // ((TestEdgeListener)edgeListener).lastMethodCalled.equals(""));
    // assertTrue("Unnecessarily called in NodeListener",
    // ((TestNodeListener)nodeListener).lastMethodCalled.equals(""));
    // assertTrue("Unnecessarily called in GraphListener",
    // ((TestGraphListener)graphListener).lastMethodCalled.equals(""));
    // }
    // -------------- Methods representing a NodeEvent --------

    /**
     * Test if several listeners work
     */
    public void testMultipleListeners() {
        AttributeListener attrListener2 = new TestAttrListener();
        AttributeListener attrListener3 = new TestAttrListener();
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictAttributeListener(attrListener2);
        lm.addStrictAttributeListener(attrListener3);

        attrEvent = new AttributeEvent(new BooleanAttribute("id"));

        lm.preAttributeAdded(attrEvent);
        assertTrue("Did not call method in the correct (Attribute) Listener",
                ((TestAttrListener) attrListener).lastMethodCalled
                        .equals("preAttributeAdded"));
        assertTrue("Did not call method in the correct (Attribute) Listener",
                ((TestAttrListener) attrListener2).lastMethodCalled
                        .equals("preAttributeAdded"));
        assertTrue("Did not call method in the correct (Attribute) Listener",
                ((TestAttrListener) attrListener3).lastMethodCalled
                        .equals("preAttributeAdded"));
    }

    /**
     * Tests if the method calls the same method in all relevant listeners.
     */
    public void testPostAttributeAdded() {
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictEdgeListener(edgeListener);
        lm.addStrictNodeListener(nodeListener);
        lm.addStrictGraphListener(graphListener);

        // generate AttributeEvent
        attrEvent = new AttributeEvent(new BooleanAttribute("id"));

        lm.postAttributeAdded(attrEvent);
        assertTrue("Did not call method in the correct (Attribute) Listener",
                ((TestAttrListener) attrListener).lastMethodCalled
                        .equals("postAttributeAdded"));
        assertEquals("Didn't call the attribute listerner's method once only",
                1, ((TestAttrListener) attrListener).called);
        assertTrue("Unnecessarily called in EdgeListener",
                ((TestEdgeListener) edgeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in NodeListener",
                ((TestNodeListener) nodeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in GraphListener",
                ((TestGraphListener) graphListener).lastMethodCalled.equals(""));
    }

    /**
     * Tests if the method calls the same method in all relevant listeners.
     */
    public void testPostAttributeChanged() {
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictEdgeListener(edgeListener);
        lm.addStrictNodeListener(nodeListener);
        lm.addStrictGraphListener(graphListener);

        // generate AttributeEvent
        attrEvent = new AttributeEvent(new BooleanAttribute("id"));

        lm.postAttributeChanged(attrEvent);
        assertTrue("Did not call method in the correct (Attribute) Listener",
                ((TestAttrListener) attrListener).lastMethodCalled
                        .equals("postAttributeChanged"));
        assertEquals("Didn't call the attribute listerner's method once only",
                1, ((TestAttrListener) attrListener).called);
        assertTrue("Unnecessarily called in EdgeListener",
                ((TestEdgeListener) edgeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in NodeListener",
                ((TestNodeListener) nodeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in GraphListener",
                ((TestGraphListener) graphListener).lastMethodCalled.equals(""));
    }

    /**
     * Tests if the method calls the same method in all relevant listeners.
     */
    public void testPostAttributeRemoved() {
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictEdgeListener(edgeListener);
        lm.addStrictNodeListener(nodeListener);
        lm.addStrictGraphListener(graphListener);

        // generate AttributeEvent
        attrEvent = new AttributeEvent(new BooleanAttribute("id"));

        lm.postAttributeRemoved(attrEvent);
        assertTrue("Did not call method in the correct (Attribute) Listener",
                ((TestAttrListener) attrListener).lastMethodCalled
                        .equals("postAttributeRemoved"));
        assertEquals("Didn't call the attribute listerner's method once only",
                1, ((TestAttrListener) attrListener).called);
        assertTrue("Unnecessarily called in EdgeListener",
                ((TestEdgeListener) edgeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in NodeListener",
                ((TestNodeListener) nodeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in GraphListener",
                ((TestGraphListener) graphListener).lastMethodCalled.equals(""));
    }

    /**
     * Tests if the method calls the same method in all relevant listeners.
     */
    public void testPostDirectedChanged() {
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictEdgeListener(edgeListener);
        lm.addStrictNodeListener(nodeListener);
        lm.addStrictGraphListener(graphListener);

        // generate EdgeEvent
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();
        Edge edge = graph.addEdge(node, node, Edge.DIRECTED);
        edgeEvent = new EdgeEvent(edge);

        lm.postDirectedChanged(edgeEvent);
        assertTrue("Did not call method in the correct (Edge) Listener",
                ((TestEdgeListener) edgeListener).lastMethodCalled
                        .equals("postDirectedChanged"));
        assertEquals("Didn't call the edge listerner's method once only", 1,
                ((TestEdgeListener) edgeListener).called);
        assertTrue("Unnecessarily called in AttributeListener",
                ((TestAttrListener) attrListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in NodeListener",
                ((TestNodeListener) nodeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in GraphListener",
                ((TestGraphListener) graphListener).lastMethodCalled.equals(""));
    }

    /**
     * Tests if the method calls the same method in all relevant listeners.
     */
    public void testPostEdgeAdded() {
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictEdgeListener(edgeListener);
        lm.addStrictNodeListener(nodeListener);
        lm.addStrictGraphListener(graphListener);

        // generate GraphEvent
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();
        graphEvent = new GraphEvent(node);

        lm.postEdgeAdded(graphEvent);
        assertTrue("Did not call method in the correct (Graph) Listener",
                ((TestGraphListener) graphListener).lastMethodCalled
                        .equals("postEdgeAdded"));
        assertEquals("Didn't call the graph listerner's method once only", 1,
                ((TestGraphListener) graphListener).called);
        assertTrue("Unnecessarily called in EdgeListener",
                ((TestEdgeListener) edgeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in NodeListener",
                ((TestNodeListener) nodeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in AttributeListener",
                ((TestAttrListener) attrListener).lastMethodCalled.equals(""));
    }

    /**
     * Tests if the method calls the same method in all relevant listeners.
     */
    public void testPostEdgeRemoved() {
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictEdgeListener(edgeListener);
        lm.addStrictNodeListener(nodeListener);
        lm.addStrictGraphListener(graphListener);

        // generate GraphEvent
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();
        graphEvent = new GraphEvent(node);

        lm.postEdgeRemoved(graphEvent);
        assertTrue("Did not call method in the correct (Graph) Listener",
                ((TestGraphListener) graphListener).lastMethodCalled
                        .equals("postEdgeRemoved"));
        assertEquals("Didn't call the graph listerner's method once only", 1,
                ((TestGraphListener) graphListener).called);
        assertTrue("Unnecessarily called in EdgeListener",
                ((TestEdgeListener) edgeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in NodeListener",
                ((TestNodeListener) nodeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in AttributeListener",
                ((TestAttrListener) attrListener).lastMethodCalled.equals(""));
    }

    /**
     * Tests if the method calls the same method in all relevant listeners.
     */
    public void testPostInEdgeAdded() {
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictEdgeListener(edgeListener);
        lm.addStrictNodeListener(nodeListener);
        lm.addStrictGraphListener(graphListener);

        // generate NodeEvent
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();
        Edge edge = graph.addEdge(node, node, Edge.DIRECTED);
        nodeEvent = new NodeEvent(node, edge);

        lm.postInEdgeAdded(nodeEvent);
        assertTrue("Did not call method in the correct (Node) Listener",
                ((TestNodeListener) nodeListener).lastMethodCalled
                        .equals("postInEdgeAdded"));
        assertEquals("Didn't call the node listerner's method once only", 1,
                ((TestNodeListener) nodeListener).called);
        assertTrue("Unnecessarily called in EdgeListener",
                ((TestEdgeListener) edgeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in AttributeListener",
                ((TestAttrListener) attrListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in GraphListener",
                ((TestGraphListener) graphListener).lastMethodCalled.equals(""));
    }

    /**
     * Tests if the method calls the same method in all relevant listeners.
     */
    public void testPostInEdgeRemoved() {
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictEdgeListener(edgeListener);
        lm.addStrictNodeListener(nodeListener);
        lm.addStrictGraphListener(graphListener);

        // generate NodeEvent
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();
        Edge edge = graph.addEdge(node, node, Edge.DIRECTED);
        nodeEvent = new NodeEvent(node, edge);

        lm.postInEdgeRemoved(nodeEvent);
        assertTrue("Did not call method in the correct (Node) Listener",
                ((TestNodeListener) nodeListener).lastMethodCalled
                        .equals("postInEdgeRemoved"));
        assertEquals("Didn't call the node listerner's method once only", 1,
                ((TestNodeListener) nodeListener).called);
        assertTrue("Unnecessarily called in EdgeListener",
                ((TestEdgeListener) edgeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in AttributeListener",
                ((TestAttrListener) attrListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in GraphListener",
                ((TestGraphListener) graphListener).lastMethodCalled.equals(""));
    }

    /**
     * Tests if the method calls the same method in all relevant listeners.
     */
    public void testPostNodeAdded() {
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictEdgeListener(edgeListener);
        lm.addStrictNodeListener(nodeListener);
        lm.addStrictGraphListener(graphListener);

        // generate GraphEvent
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();
        graphEvent = new GraphEvent(node);

        lm.postNodeAdded(graphEvent);
        assertTrue("Did not call method in the correct (Graph) Listener",
                ((TestGraphListener) graphListener).lastMethodCalled
                        .equals("postNodeAdded"));
        assertEquals("Didn't call the graph listerner's method once only", 1,
                ((TestGraphListener) graphListener).called);
        assertTrue("Unnecessarily called in EdgeListener",
                ((TestEdgeListener) edgeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in NodeListener",
                ((TestNodeListener) nodeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in AttributeListener",
                ((TestAttrListener) attrListener).lastMethodCalled.equals(""));
    }

    /**
     * Tests if the method calls the same method in all relevant listeners.
     */
    public void testPostNodeRemoved() {
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictEdgeListener(edgeListener);
        lm.addStrictNodeListener(nodeListener);
        lm.addStrictGraphListener(graphListener);

        // generate GraphEvent
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();
        graphEvent = new GraphEvent(node);

        lm.postNodeRemoved(graphEvent);
        assertTrue("Did not call method in the correct (Graph) Listener",
                ((TestGraphListener) graphListener).lastMethodCalled
                        .equals("postNodeRemoved"));
        assertEquals("Didn't call the graph listerner's method once only", 1,
                ((TestGraphListener) graphListener).called);
        assertTrue("Unnecessarily called in EdgeListener",
                ((TestEdgeListener) edgeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in NodeListener",
                ((TestNodeListener) nodeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in AttributeListener",
                ((TestAttrListener) attrListener).lastMethodCalled.equals(""));
    }

    /**
     * Tests if the method calls the same method in all relevant listeners.
     */
    public void testPostOutEdgeAdded() {
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictEdgeListener(edgeListener);
        lm.addStrictNodeListener(nodeListener);
        lm.addStrictGraphListener(graphListener);

        // generate NodeEvent
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();
        Edge edge = graph.addEdge(node, node, Edge.DIRECTED);
        nodeEvent = new NodeEvent(node, edge);

        lm.postOutEdgeAdded(nodeEvent);
        assertTrue("Did not call method in the correct (Node) Listener",
                ((TestNodeListener) nodeListener).lastMethodCalled
                        .equals("postOutEdgeAdded"));
        assertTrue("Unnecessarily called in EdgeListener",
                ((TestEdgeListener) edgeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in AttributeListener",
                ((TestAttrListener) attrListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in GraphListener",
                ((TestGraphListener) graphListener).lastMethodCalled.equals(""));
    }

    /**
     * Tests if the method calls the same method in all relevant listeners.
     */
    public void testPostOutEdgeRemoved() {
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictEdgeListener(edgeListener);
        lm.addStrictNodeListener(nodeListener);
        lm.addStrictGraphListener(graphListener);

        // generate NodeEvent
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();
        Edge edge = graph.addEdge(node, node, Edge.DIRECTED);
        nodeEvent = new NodeEvent(node, edge);

        lm.postOutEdgeRemoved(nodeEvent);
        assertTrue("Did not call method in the correct (Node) Listener",
                ((TestNodeListener) nodeListener).lastMethodCalled
                        .equals("postOutEdgeRemoved"));
        assertEquals("Didn't call the node listerner's method once only", 1,
                ((TestNodeListener) nodeListener).called);
        assertTrue("Unnecessarily called in EdgeListener",
                ((TestEdgeListener) edgeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in AttributeListener",
                ((TestAttrListener) attrListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in GraphListener",
                ((TestGraphListener) graphListener).lastMethodCalled.equals(""));
    }

    /**
     * Tests if the method calls the same method in all relevant listeners.
     */
    public void testPostSourceNodeChanged() {
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictEdgeListener(edgeListener);
        lm.addStrictNodeListener(nodeListener);
        lm.addStrictGraphListener(graphListener);

        // generate EdgeEvent
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();
        Edge edge = graph.addEdge(node, node, Edge.DIRECTED);
        edgeEvent = new EdgeEvent(edge);

        lm.postSourceNodeChanged(edgeEvent);
        assertTrue("Did not call method in the correct (Edge) Listener",
                ((TestEdgeListener) edgeListener).lastMethodCalled
                        .equals("postSourceNodeChanged"));
        assertEquals("Didn't call the edge listerner's method once only", 1,
                ((TestEdgeListener) edgeListener).called);
        assertTrue("Unnecessarily called in AttributeListener",
                ((TestAttrListener) attrListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in NodeListener",
                ((TestNodeListener) nodeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in GraphListener",
                ((TestGraphListener) graphListener).lastMethodCalled.equals(""));
    }

    /**
     * Tests if the method calls the same method in all relevant listeners.
     */
    public void testPostTargetNodeChanged() {
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictEdgeListener(edgeListener);
        lm.addStrictNodeListener(nodeListener);
        lm.addStrictGraphListener(graphListener);

        // generate EdgeEvent
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();
        Edge edge = graph.addEdge(node, node, Edge.DIRECTED);
        edgeEvent = new EdgeEvent(edge);

        lm.postTargetNodeChanged(edgeEvent);
        assertTrue("Did not call method in the correct (Edge) Listener",
                ((TestEdgeListener) edgeListener).lastMethodCalled
                        .equals("postTargetNodeChanged"));
        assertEquals("Didn't call the edge listerner's method once only", 1,
                ((TestEdgeListener) edgeListener).called);
        assertTrue("Unnecessarily called in AttributeListener",
                ((TestAttrListener) attrListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in NodeListener",
                ((TestNodeListener) nodeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in GraphListener",
                ((TestGraphListener) graphListener).lastMethodCalled.equals(""));
    }

    /**
     * Tests if the method calls the same method in all relevant listeners.
     */
    public void testPostUndirectedEdgeAdded() {
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictEdgeListener(edgeListener);
        lm.addStrictNodeListener(nodeListener);
        lm.addStrictGraphListener(graphListener);

        // generate NodeEvent
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();
        Edge edge = graph.addEdge(node, node, Edge.DIRECTED);
        nodeEvent = new NodeEvent(node, edge);

        lm.postUndirectedEdgeAdded(nodeEvent);
        assertTrue("Did not call method in the correct (Node) Listener",
                ((TestNodeListener) nodeListener).lastMethodCalled
                        .equals("postUndirectedEdgeAdded"));
        assertEquals("Didn't call the node listerner's method once only", 1,
                ((TestNodeListener) nodeListener).called);
        assertTrue("Unnecessarily called in EdgeListener",
                ((TestEdgeListener) edgeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in AttributeListener",
                ((TestAttrListener) attrListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in GraphListener",
                ((TestGraphListener) graphListener).lastMethodCalled.equals(""));
    }

    /**
     * Tests if the method calls the same method in all relevant listeners.
     */
    public void testPostUndirectedEdgeRemoved() {
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictEdgeListener(edgeListener);
        lm.addStrictNodeListener(nodeListener);
        lm.addStrictGraphListener(graphListener);

        // generate NodeEvent
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();
        Edge edge = graph.addEdge(node, node, Edge.DIRECTED);
        nodeEvent = new NodeEvent(node, edge);

        lm.postUndirectedEdgeRemoved(nodeEvent);
        assertTrue("Did not call method in the correct (Node) Listener",
                ((TestNodeListener) nodeListener).lastMethodCalled
                        .equals("postUndirectedEdgeRemoved"));
        assertEquals("Didn't call the node listerner's method once only", 1,
                ((TestNodeListener) nodeListener).called);
        assertTrue("Unnecessarily called in EdgeListener",
                ((TestEdgeListener) edgeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in AttributeListener",
                ((TestAttrListener) attrListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in GraphListener",
                ((TestGraphListener) graphListener).lastMethodCalled.equals(""));
    }

    // -------------- Methods representing an AttributeEvent --------

    /**
     * Tests if the method calls the same method in all relevant listeners.
     */
    public void testPreAttributeAdded() {
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictEdgeListener(edgeListener);
        lm.addStrictNodeListener(nodeListener);
        lm.addStrictGraphListener(graphListener);

        // generate AttributeEvent
        attrEvent = new AttributeEvent(new BooleanAttribute("id"));

        lm.preAttributeAdded(attrEvent);
        assertTrue("Did not call method in the correct (Attribute) Listener",
                ((TestAttrListener) attrListener).lastMethodCalled
                        .equals("preAttributeAdded"));
        assertEquals("Didn't call the attribute listerner's method once only",
                1, ((TestAttrListener) attrListener).called);
        assertTrue("Unnecessarily called in EdgeListener",
                ((TestEdgeListener) edgeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in NodeListener",
                ((TestNodeListener) nodeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in GraphListener",
                ((TestGraphListener) graphListener).lastMethodCalled.equals(""));
    }

    /**
     * Tests if the method calls the same method in all relevant listeners.
     */
    public void testPreAttributeChanged() {
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictEdgeListener(edgeListener);
        lm.addStrictNodeListener(nodeListener);
        lm.addStrictGraphListener(graphListener);

        // generate AttributeEvent
        attrEvent = new AttributeEvent(new BooleanAttribute("id"));

        lm.preAttributeChanged(attrEvent);
        assertTrue("Did not call method in the correct (Attribute) Listener",
                ((TestAttrListener) attrListener).lastMethodCalled
                        .equals("preAttributeChanged"));
        assertEquals("Didn't call the attribute listerner's method once only",
                1, ((TestAttrListener) attrListener).called);
        assertTrue("Unnecessarily called in EdgeListener",
                ((TestEdgeListener) edgeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in NodeListener",
                ((TestNodeListener) nodeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in GraphListener",
                ((TestGraphListener) graphListener).lastMethodCalled.equals(""));
    }

    /**
     * Tests if the method calls the same method in all relevant listeners.
     */
    public void testPreAttributeRemoved() {
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictEdgeListener(edgeListener);
        lm.addStrictNodeListener(nodeListener);
        lm.addStrictGraphListener(graphListener);

        // generate AttributeEvent
        attrEvent = new AttributeEvent(new BooleanAttribute("id"));

        lm.preAttributeRemoved(attrEvent);
        assertTrue("Did not call method in the correct (Attribute) Listener",
                ((TestAttrListener) attrListener).lastMethodCalled
                        .equals("preAttributeRemoved"));
        assertEquals("Didn't call the attribute listerner's method once only",
                1, ((TestAttrListener) attrListener).called);
        assertTrue("Unnecessarily called in EdgeListener",
                ((TestEdgeListener) edgeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in NodeListener",
                ((TestNodeListener) nodeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in GraphListener",
                ((TestGraphListener) graphListener).lastMethodCalled.equals(""));
    }

    /**
     * Tests if the method calls the same method in all relevant listeners.
     */
    public void testPreDirectedEdgeChanged() {
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictEdgeListener(edgeListener);
        lm.addStrictNodeListener(nodeListener);
        lm.addStrictGraphListener(graphListener);

        // generate EdgeEvent
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();
        Edge edge = graph.addEdge(node, node, Edge.DIRECTED);
        edgeEvent = new EdgeEvent(edge);

        lm.postTargetNodeChanged(edgeEvent);
        assertTrue("Did not call method in the correct (Edge) Listener",
                ((TestEdgeListener) edgeListener).lastMethodCalled
                        .equals("postTargetNodeChanged"));
        assertEquals("Didn't call the edge listerner's method once only", 1,
                ((TestEdgeListener) edgeListener).called);
        assertTrue("Unnecessarily called in AttributeListener",
                ((TestAttrListener) attrListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in NodeListener",
                ((TestNodeListener) nodeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in GraphListener",
                ((TestGraphListener) graphListener).lastMethodCalled.equals(""));
    }

    // -------------- Methods representing a GraphEvent --------

    /**
     * Tests if the method calls the same method in all relevant listeners.
     */
    public void testPreEdgeAdded() {
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictEdgeListener(edgeListener);
        lm.addStrictNodeListener(nodeListener);
        lm.addStrictGraphListener(graphListener);

        // generate GraphEvent
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();
        graphEvent = new GraphEvent(node);

        lm.preEdgeAdded(graphEvent);
        assertTrue("Did not call method in the correct (Graph) Listener",
                ((TestGraphListener) graphListener).lastMethodCalled
                        .equals("preEdgeAdded"));
        assertEquals("Didn't call the graph listerner's method once only", 1,
                ((TestGraphListener) graphListener).called);
        assertTrue("Unnecessarily called in EdgeListener",
                ((TestEdgeListener) edgeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in NodeListener",
                ((TestNodeListener) nodeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in AttributeListener",
                ((TestAttrListener) attrListener).lastMethodCalled.equals(""));
    }

    /**
     * Tests if the method calls the same method in all relevant listeners.
     */
    public void testPreEdgeRemoved() {
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictEdgeListener(edgeListener);
        lm.addStrictNodeListener(nodeListener);
        lm.addStrictGraphListener(graphListener);

        // generate GraphEvent
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();
        graphEvent = new GraphEvent(node);

        lm.preEdgeRemoved(graphEvent);
        assertTrue("Did not call method in the correct (Graph) Listener",
                ((TestGraphListener) graphListener).lastMethodCalled
                        .equals("preEdgeRemoved"));
        assertEquals("Didn't call the graph listerner's method once only", 1,
                ((TestGraphListener) graphListener).called);
        assertTrue("Unnecessarily called in EdgeListener",
                ((TestEdgeListener) edgeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in NodeListener",
                ((TestNodeListener) nodeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in AttributeListener",
                ((TestAttrListener) attrListener).lastMethodCalled.equals(""));
    }

    /**
     * Tests if the method calls the same method in all relevant listeners.
     */
    public void testPreInEdgeAdded() {
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictEdgeListener(edgeListener);
        lm.addStrictNodeListener(nodeListener);
        lm.addStrictGraphListener(graphListener);

        // generate NodeEvent
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();
        Edge edge = graph.addEdge(node, node, Edge.DIRECTED);
        nodeEvent = new NodeEvent(node, edge);

        lm.preInEdgeAdded(nodeEvent);
        assertTrue("Did not call method in the correct (Node) Listener",
                ((TestNodeListener) nodeListener).lastMethodCalled
                        .equals("preInEdgeAdded"));
        assertEquals("Didn't call the node listerner's method once only", 1,
                ((TestNodeListener) nodeListener).called);
        assertTrue("Unnecessarily called in EdgeListener",
                ((TestEdgeListener) edgeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in AttributeListener",
                ((TestAttrListener) attrListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in GraphListener",
                ((TestGraphListener) graphListener).lastMethodCalled.equals(""));
    }

    /**
     * Tests if the method calls the same method in all relevant listeners.
     */
    public void testPreInEdgeRemoved() {
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictEdgeListener(edgeListener);
        lm.addStrictNodeListener(nodeListener);
        lm.addStrictGraphListener(graphListener);

        // generate NodeEvent
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();
        Edge edge = graph.addEdge(node, node, Edge.DIRECTED);
        nodeEvent = new NodeEvent(node, edge);

        lm.preInEdgeRemoved(nodeEvent);
        assertTrue("Did not call method in the correct (Node) Listener",
                ((TestNodeListener) nodeListener).lastMethodCalled
                        .equals("preInEdgeRemoved"));
        assertEquals("Didn't call the node listerner's method once only", 1,
                ((TestNodeListener) nodeListener).called);
        assertTrue("Unnecessarily called in EdgeListener",
                ((TestEdgeListener) edgeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in AttributeListener",
                ((TestAttrListener) attrListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in GraphListener",
                ((TestGraphListener) graphListener).lastMethodCalled.equals(""));
    }

    /**
     * Tests if the method calls the same method in all relevant listeners.
     */
    public void testPreNodeAdded() {
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictEdgeListener(edgeListener);
        lm.addStrictNodeListener(nodeListener);
        lm.addStrictGraphListener(graphListener);

        // generate GraphEvent
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();
        graphEvent = new GraphEvent(node);

        lm.preNodeAdded(graphEvent);
        assertTrue("Did not call method in the correct (Graph) Listener",
                ((TestGraphListener) graphListener).lastMethodCalled
                        .equals("preNodeAdded"));
        assertEquals("Didn't call the graph listerner's method once only", 1,
                ((TestGraphListener) graphListener).called);
        assertTrue("Unnecessarily called in EdgeListener",
                ((TestEdgeListener) edgeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in NodeListener",
                ((TestNodeListener) nodeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in AttributeListener",
                ((TestAttrListener) attrListener).lastMethodCalled.equals(""));
    }

    /**
     * Tests if the method calls the same method in all relevant listeners.
     */
    public void testPreNodeRemoved() {
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictEdgeListener(edgeListener);
        lm.addStrictNodeListener(nodeListener);
        lm.addStrictGraphListener(graphListener);

        // generate GraphEvent
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();
        graphEvent = new GraphEvent(node);

        lm.preNodeRemoved(graphEvent);
        assertTrue("Did not call method in the correct (Graph) Listener",
                ((TestGraphListener) graphListener).lastMethodCalled
                        .equals("preNodeRemoved"));
        assertEquals("Didn't call the graph listerner's method once only", 1,
                ((TestGraphListener) graphListener).called);
        assertTrue("Unnecessarily called in EdgeListener",
                ((TestEdgeListener) edgeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in NodeListener",
                ((TestNodeListener) nodeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in AttributeListener",
                ((TestAttrListener) attrListener).lastMethodCalled.equals(""));
    }

    /**
     * Tests if the method calls the same method in all relevant listeners.
     */
    public void testPreOutEdgeAdded() {
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictEdgeListener(edgeListener);
        lm.addStrictNodeListener(nodeListener);
        lm.addStrictGraphListener(graphListener);

        // generate NodeEvent
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();
        Edge edge = graph.addEdge(node, node, Edge.DIRECTED);
        nodeEvent = new NodeEvent(node, edge);

        lm.preOutEdgeAdded(nodeEvent);
        assertTrue("Did not call method in the correct (Node) Listener",
                ((TestNodeListener) nodeListener).lastMethodCalled
                        .equals("preOutEdgeAdded"));
        assertEquals("Didn't call the node listerner's method once only", 1,
                ((TestNodeListener) nodeListener).called);
        assertTrue("Unnecessarily called in EdgeListener",
                ((TestEdgeListener) edgeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in AttributeListener",
                ((TestAttrListener) attrListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in GraphListener",
                ((TestGraphListener) graphListener).lastMethodCalled.equals(""));
    }

    /**
     * Tests if the method calls the same method in all relevant listeners.
     */
    public void testPreOutEdgeRemoved() {
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictEdgeListener(edgeListener);
        lm.addStrictNodeListener(nodeListener);
        lm.addStrictGraphListener(graphListener);

        // generate NodeEvent
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();
        Edge edge = graph.addEdge(node, node, Edge.DIRECTED);
        nodeEvent = new NodeEvent(node, edge);

        lm.preOutEdgeRemoved(nodeEvent);
        assertTrue("Did not call method in the correct (Node) Listener",
                ((TestNodeListener) nodeListener).lastMethodCalled
                        .equals("preOutEdgeRemoved"));
        assertEquals("Didn't call the node listerner's method once only", 1,
                ((TestNodeListener) nodeListener).called);
        assertTrue("Unnecessarily called in EdgeListener",
                ((TestEdgeListener) edgeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in AttributeListener",
                ((TestAttrListener) attrListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in GraphListener",
                ((TestGraphListener) graphListener).lastMethodCalled.equals(""));
    }

    // -------------- Methods representing an EdgeEvent --------

    /**
     * Tests if the method calls the same method in all relevant listeners.
     */
    public void testPreSourceNodeChanged() {
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictEdgeListener(edgeListener);
        lm.addStrictNodeListener(nodeListener);
        lm.addStrictGraphListener(graphListener);

        // generate EdgeEvent
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();
        Edge edge = graph.addEdge(node, node, Edge.DIRECTED);
        edgeEvent = new EdgeEvent(edge);

        lm.preSourceNodeChanged(edgeEvent);
        assertTrue("Did not call method in the correct (Edge) Listener",
                ((TestEdgeListener) edgeListener).lastMethodCalled
                        .equals("preSourceNodeChanged"));
        assertEquals("Didn't call the edge listerner's method once only", 1,
                ((TestEdgeListener) edgeListener).called);
        assertTrue("Unnecessarily called in AttributeListener",
                ((TestAttrListener) attrListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in NodeListener",
                ((TestNodeListener) nodeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in GraphListener",
                ((TestGraphListener) graphListener).lastMethodCalled.equals(""));
    }

    /**
     * Tests if the method calls the same method in all relevant listeners.
     */
    public void testPreTargetNodeChanged() {
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictEdgeListener(edgeListener);
        lm.addStrictNodeListener(nodeListener);
        lm.addStrictGraphListener(graphListener);

        // generate EdgeEvent
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();
        Edge edge = graph.addEdge(node, node, Edge.DIRECTED);
        edgeEvent = new EdgeEvent(edge);

        lm.preTargetNodeChanged(edgeEvent);
        assertTrue("Did not call method in the correct (Edge) Listener",
                ((TestEdgeListener) edgeListener).lastMethodCalled
                        .equals("preTargetNodeChanged"));
        assertEquals("Didn't call the edge listerner's method once only", 1,
                ((TestEdgeListener) edgeListener).called);
        assertTrue("Unnecessarily called in AttributeListener",
                ((TestAttrListener) attrListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in NodeListener",
                ((TestNodeListener) nodeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in GraphListener",
                ((TestGraphListener) graphListener).lastMethodCalled.equals(""));
    }

    /**
     * Tests if the method calls the same method in all relevant listeners.
     */
    public void testPreUndirectedEdgeAdded() {
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictEdgeListener(edgeListener);
        lm.addStrictNodeListener(nodeListener);
        lm.addStrictGraphListener(graphListener);

        // generate NodeEvent
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();
        Edge edge = graph.addEdge(node, node, Edge.DIRECTED);
        nodeEvent = new NodeEvent(node, edge);

        lm.preUndirectedEdgeAdded(nodeEvent);
        assertTrue("Did not call method in the correct (Node) Listener",
                ((TestNodeListener) nodeListener).lastMethodCalled
                        .equals("preUndirectedEdgeAdded"));
        assertEquals("Didn't call the node listerner's method once only", 1,
                ((TestNodeListener) nodeListener).called);
        assertTrue("Unnecessarily called in EdgeListener",
                ((TestEdgeListener) edgeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in AttributeListener",
                ((TestAttrListener) attrListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in GraphListener",
                ((TestGraphListener) graphListener).lastMethodCalled.equals(""));
    }

    /**
     * Tests if the method calls the same method in all relevant listeners.
     */
    public void testPreUndirectedEdgeRemoved() {
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictEdgeListener(edgeListener);
        lm.addStrictNodeListener(nodeListener);
        lm.addStrictGraphListener(graphListener);

        // generate NodeEvent
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();
        Edge edge = graph.addEdge(node, node, Edge.DIRECTED);
        nodeEvent = new NodeEvent(node, edge);

        lm.preUndirectedEdgeRemoved(nodeEvent);
        assertTrue("Did not call method in the correct (Node) Listener",
                ((TestNodeListener) nodeListener).lastMethodCalled
                        .equals("preUndirectedEdgeRemoved"));
        assertEquals("Didn't call the node listerner's method once only", 1,
                ((TestNodeListener) nodeListener).called);
        assertTrue("Unnecessarily called in EdgeListener",
                ((TestEdgeListener) edgeListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in AttributeListener",
                ((TestAttrListener) attrListener).lastMethodCalled.equals(""));
        assertTrue("Unnecessarily called in GraphListener",
                ((TestGraphListener) graphListener).lastMethodCalled.equals(""));
    }

    /**
     * Tests the removeAttributeListener method by adding and removing an
     * AttributeListener
     */
    public void testRemoveAttributeListener() {
        // add a listener (twice; should not make a difference)
        lm.addStrictAttributeListener(attrListener);
        lm.addStrictAttributeListener(attrListener);

        // remove the listener
        try {
            lm.removeAttributeListener(attrListener);
        } catch (Exception e) {
            fail("Could not remove listener that has been added previously:"
                    + e.getMessage());
        }

        // has it really been removed
        lm.preAttributeChanged(new AttributeEvent(new BooleanAttribute("id")));
        assertTrue("removed AttributeListener but is still there ...",
                ((TestAttrListener) attrListener).lastMethodCalled.equals(""));
    }

    /**
     * Tests the removeEdgeListener method by adding and removing an
     * EdgeListener
     */
    public void testRemoveEdgeListener() {
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();

        Edge edge = null;

        try {
            edge = graph.addEdge(node, node, Edge.DIRECTED);
        } catch (GraphElementNotFoundException e) {
        }

        // add a listener (twice; should not make a difference)
        lm.addStrictEdgeListener(edgeListener);
        lm.addStrictEdgeListener(edgeListener);

        // remove the listener
        try {
            lm.removeEdgeListener(edgeListener);
        } catch (Exception e) {
            fail("Could not remove listener that has been added previously:"
                    + e.getMessage());
        }

        // has it really been removed
        lm.preSourceNodeChanged(new EdgeEvent(edge));
        assertTrue("removed EdgeListener but is still there ...",
                ((TestEdgeListener) edgeListener).lastMethodCalled.equals(""));
    }

    /**
     * Tests the removeGraphListener method by adding and removing an
     * GraphListener
     */
    public void testRemoveGraphListener() {
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();
        Edge edge = null;

        try {
            edge = graph.addEdge(node, node, Edge.DIRECTED);
        } catch (GraphElementNotFoundException e) {
        }

        // add a listener (twice; should not make a difference)
        lm.addStrictGraphListener(graphListener);
        lm.addStrictGraphListener(graphListener);

        // remove the listener
        try {
            lm.removeGraphListener(graphListener);
        } catch (Exception e) {
            fail("Could not remove listener that has been added previously:"
                    + e.getMessage());
        }

        // has it really been removed -- to be discussed if it is necessary
        lm.preEdgeAdded(new GraphEvent(edge));
        assertTrue("removed GraphListener but is still there ...",
                ((TestGraphListener) graphListener).lastMethodCalled.equals(""));
    }

    /**
     * Tests the removeNodeListener method by adding and removing an
     * NodeListener
     */
    public void testRemoveNodeListener() {
        Graph graph = new AdjListGraph();
        Node node = graph.addNode();

        Edge edge = null;

        try {
            edge = graph.addEdge(node, node, Edge.DIRECTED);
        } catch (GraphElementNotFoundException e) {
        }

        // add a listener (twice; should not make a difference)
        lm.addStrictNodeListener(nodeListener);
        lm.addStrictNodeListener(nodeListener);

        // remove the listener
        try {
            lm.removeNodeListener(nodeListener);
        } catch (Exception e) {
            fail("Could not remove listener that has been added previously:"
                    + e.getMessage());
        }

        // has it really been removed
        lm.preInEdgeAdded(new NodeEvent(node, edge));
        assertTrue("removed NodeListener but is still there ...",
                ((TestNodeListener) nodeListener).lastMethodCalled.equals(""));
    }

    /**
     * Tests the removeAttributeListener method by trying to remove an
     * AttributeListener that has not been previously inserted
     */
    public void testRemoveNotExistentAttributeListener() {
        try {
            lm.removeAttributeListener(attrListener);
            fail("Should raise a ListenerNotFoundException");
        } catch (ListenerNotFoundException lnfe) {
        } catch (Exception e) {
            fail("Should raise a ListenerNotFoundException, not this: "
                    + e.getMessage());
        }
    }

    /**
     * Tests the removeEdgeListener method by trying to remove an EdgeListener
     * that has not been previously inserted
     */
    public void testRemoveNotExistentEdgeListener() {
        try {
            lm.removeEdgeListener(edgeListener);
            fail("Should raise a ListenerNotFoundException");
        } catch (ListenerNotFoundException lnfe) {
        } catch (Exception e) {
            fail("Should raise a ListenerNotFoundException, not this: "
                    + e.getMessage());
        }
    }

    /**
     * Tests the removeGraphListener method by trying to remove a GraphListener
     * that has not been previously inserted
     */
    public void testRemoveNotExistentGraphListener() {
        try {
            lm.removeGraphListener(graphListener);
            fail("Should raise a ListenerNotFoundException");
        } catch (ListenerNotFoundException lnfe) {
        } catch (Exception e) {
            fail("Should raise a ListenerNotFoundException, not this: "
                    + e.getMessage());
        }
    }

    /**
     * Tests the removeNodeListener method by trying to remove a NodeListener
     * that has not been previously inserted
     */
    public void testRemoveNotExistentNodeListener() {
        try {
            lm.removeNodeListener(nodeListener);
            fail("Should raise a ListenerNotFoundException");
        } catch (ListenerNotFoundException lnfe) {
        } catch (Exception e) {
            fail("Should raise a ListenerNotFoundException, not this: "
                    + e.getMessage());
        }
    }

    /**
     * Sets up the test fixture. Called before every test case method.
     */
    protected void setUp() {
        lm = new ListenerManager();
        attrListener = new TestAttrListener();
        edgeListener = new TestEdgeListener();
        nodeListener = new TestNodeListener();
        graphListener = new TestGraphListener();
    }

    /**
     * Tears down the test fixture. Called after all test case methods hava run.
     */
    protected void tearDown() {
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
