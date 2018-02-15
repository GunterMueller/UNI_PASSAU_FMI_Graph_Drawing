// =============================================================================
//
//   GraphAdapterTest.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package tests.graffiti.plugins.algorithms.mst;

import junit.framework.TestCase;

import org.graffiti.attributes.Attributable;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.mst.Heap;
import org.graffiti.plugins.algorithms.mst.adapters.EdgeAdapter;
import org.graffiti.plugins.algorithms.mst.adapters.GraphAdapter;
import org.graffiti.plugins.algorithms.mst.adapters.HeapAdapter;
import org.graffiti.plugins.algorithms.mst.adapters.NodeAdapter;

/**
 * @author Harald
 * @version $Revision$ $Date$
 */
public class GraphAdapterTest extends TestCase {

    private Heap<Node, Float> heap = null;

    private HeapAdapter heapAdapter = null;

    private GraphFixture graphFixture = null;

    private GraphAdapter graphAdapter = null;

    /*
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        graphFixture = new GraphFixture();
        graphFixture.setUpConnectedCircles();
        graphAdapter = new GraphAdapter(graphFixture.getGraph());
        heap = new org.graffiti.util.heap.DebugHeap<Node, Float>();
        heapAdapter = new HeapAdapter(heap);
    }

    /*
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        heap = null;
        heapAdapter = null;
        graphFixture = null;
        graphAdapter = null;
    }

    public void testNewGraphAdapterIsEmpty() {
        assertTrue(new GraphAdapter(createEmptyGraph()).isEmpty());
    }

    public void testGraphAdaptersAreNotEmptyAfterAddingAnElement() {

        graphAdapter = new GraphAdapter(createGraph(1));
        assertFalse(graphAdapter.isEmpty());
    }

    public void testSingletonGraphAdaptersContainExactlyOneNode() {
        Graph g = new GraphStub(2);
        graphAdapter = new GraphAdapter(g);
        assertTrue(graphAdapter.isEmpty());
        assertFalse(graphAdapter.isSingleton());
        g.addNode();
        assertTrue(graphAdapter.isSingleton());
        g.addNode();
        assertFalse(graphAdapter.isSingleton());
    }

    public void testEmptyGraphAdaptersDoNotHaveAStartNode() {
        try {
            new GraphAdapter(createEmptyGraph()).startNode();
        } catch (java.util.NoSuchElementException expected) {
            return;
        }
        fail();
    }

    public void testNonEmptyGraphAdaptersDoHaveAStartNode() {
        Graph g = createGraph(4);
        graphAdapter = new GraphAdapter(g);
        graphAdapter.startNode();
    }

    public void testGraphAdaptersHaveTheSameSizeAsTheGraphsTheyAdapt() {
        assertTrue(graphFixture.getNodes().size() == graphAdapter.nodes()
                .size());
    }

    public void testAdapterContainsExactlyTheNodesInGraph() {
        assertTrue(graphFixture.getNodes().size() == graphAdapter.nodes()
                .size());
        boolean foundAll = true;
        for (Node n : graphFixture.getNodes()) {
            boolean found = false;
            for (NodeAdapter m : graphAdapter.nodes())
                if (m.equalsNode(n)) {
                    found = true;
                    break;
                }
            foundAll = foundAll && found;
        }
        assertTrue(foundAll);
    }

    public void testAdapterHasTheSameDirectionAsGraph() {
        assertEquals(graphFixture.getGraph().isUndirected(), graphAdapter
                .isUndirected());
    }

    public void testEmptyAdaptersAreConnected() {
        assertTrue(new GraphAdapter(createEmptyGraph()).isConnected());
    }

    /**
     * 
     */
    public void testSingletonAdaptersAreConnected() {
        assertTrue(new GraphAdapter(createGraph(1)).isConnected());
    }

    public void testSetUpAdapterIsConnected() {
        assertTrue(graphAdapter.isConnected());
    }

    public void testAdapterContainsExactlyTheEdgesInGraph() {
        assertEquals(graphFixture.getEdges().size(), graphAdapter.edges()
                .size());
        boolean foundAll = true;
        for (Edge e : graphFixture.getEdges()) {
            for (EdgeAdapter f : graphAdapter.edges()) {
                if (f.equalsEdge(e)) {
                    foundAll = foundAll && true;
                    break;
                }
            }
        }
        assertTrue(foundAll);
    }

    public void testAttributesAreCorrectlyCreatedAfterInit() {
        graphAdapter.init(heapAdapter);
        for (Node n : graphFixture.getNodes()) {
            n.getAttribute("mst");
        }
        for (Edge e : graphFixture.getEdges()) {
            e.getAttribute("mst");
        }
    }

    public void testNoMstAttributesRemainAfterClear() {
        this.testAttributesAreCorrectlyCreatedAfterInit();
        graphAdapter.clear();
        for (Node n : graphFixture.getNodes()) {
            assertTrue(attributeDoesNotExist(n, "mst"));
        }
        for (Edge e : graphFixture.getEdges()) {
            assertTrue(attributeDoesNotExist(e, "mst"));
        }

    }

    public void testOnlyIsTreeEdgeAttributeRemainsAfterACallToClean() {
        this.testAttributesAreCorrectlyCreatedAfterInit();
        graphAdapter.clean();
        for (Edge e : graphFixture.getEdges()) {
            assertTrue(attributeExists(e, "mst.isTreeEdge"));
            assertFalse(attributeExists(e, "mst.weight"));
        }
        for (Node n : graphFixture.getNodes()) {
            assertTrue(attributeDoesNotExist(n, "mst"));
        }
    }

    public void testColoredAdapterContainsOnlyColoredEdges() {
        graphAdapter = new GraphAdapter(graphFixture.getGraph(),
                new ColoredEdgeAdapterStubFactory(),
                new NodeAdapterStubFactory());
        for (EdgeAdapter e : graphAdapter.edges()) {
            assertTrue(e.isColored());
        }
    }

    public void testUncoloredAdapterContainsOnlyUncoloredEdges() {
        graphAdapter = new GraphAdapter(graphFixture.getGraph(),
                new UncoloredEdgeAdapterStubFactory(),
                new NodeAdapterStubFactory());
        for (EdgeAdapter e : graphAdapter.edges()) {
            assertFalse(e.isColored());
        }
    }

    private boolean attributeExists(Edge e, String path) {
        try {
            e.getAttribute(path);
        } catch (AttributeNotFoundException notExpected) {
            return false;
        }
        return true;
    }

    private boolean attributeDoesNotExist(Attributable a, String path) {
        try {
            a.getAttribute(path);
        } catch (AttributeNotFoundException expected) {
            return true;
        }
        return false;
    }

    private Graph createEmptyGraph() {
        return createGraph(0);
    }

    private Graph createGraph(int noOfNodes) {
        Graph g = new GraphStub(noOfNodes);
        for (int i = 0; i < noOfNodes; i++) {
            g.addNode();
        }
        return g;
    }
}
