// =============================================================================
//
//   NodeAdapterTest.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package tests.graffiti.plugins.algorithms.mst;

import java.util.Collection;

import junit.framework.TestCase;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.mst.Heap;
import org.graffiti.plugins.algorithms.mst.adapters.EdgeAdapter;
import org.graffiti.plugins.algorithms.mst.adapters.HeapAdapter;
import org.graffiti.plugins.algorithms.mst.adapters.NodeAdapter;
import org.graffiti.plugins.algorithms.mst.adapters.NodeAdapterFactory;

/**
 * @author Harald
 * @version $Revision$ $Date$
 */
public class NodeAdapterTest extends TestCase {

    private Heap<Node, Float> heap;

    private HeapAdapter heapAdapter;

    private GraphFixture graphFixture;

    private Node node;

    private NodeAdapter nodeAdapter;

    /*
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        graphFixture = new GraphFixture();
        graphFixture.setUpConnectedCircles();
        node = graphFixture.getNodes().iterator().next();
        heap = new org.graffiti.util.heap.DebugHeap<Node, Float>();
        heapAdapter = new HeapAdapter(heap);
        nodeAdapter = new NodeAdapterFactory().createNodeAdapter(node);
    }

    /*
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testNodeAdapterContainsExactlyTheAdjacentNodesOfAdaptee() {
        Collection<Node> adjacentAdaptees = null;
        for (Node n : graphFixture.getNodes())
            if (nodeAdapter.equalsNode(n)) {
                adjacentAdaptees = n.getAllOutNeighbors();
                break;
            }
        assertEquals(adjacentAdaptees.size(), nodeAdapter.adjacentNodes()
                .size());
        boolean foundAll = true;
        for (NodeAdapter n : nodeAdapter.adjacentNodes()) {
            for (Node m : adjacentAdaptees)
                if (n.equalsNode(m)) {
                    foundAll = foundAll && true;
                    break;
                }
        }
        assertTrue(foundAll);
    }

    public void testNodeAdapterContainsNoAttributesAfterACallToClearCompletes() {
        nodeAdapter.init(heapAdapter);
        nodeAdapter.clear();
        assertTrue(attributeDoesNotExist(node, "mst"));
    }

    private boolean attributeDoesNotExist(Node node, String path) {
        try {
            node.getAttribute(path);
        } catch (AttributeNotFoundException expected) {
            return true;
        }
        return false;
    }

    public void testEdgeToAnotherNodeDoesExistInTheAdaptee() {
        NodeAdapter target = nodeAdapter.adjacentNodes().iterator().next();
        EdgeAdapter edge = nodeAdapter.edgeTo(target);
        for (Edge e : node.getAllOutEdges())
            if (edge.equalsEdge(e))
                return;
        fail();
    }

    public void testNoNodeAdapterEqualsNull() {
        assertFalse(nodeAdapter.equalsNode(null));
    }

    public void testANodeAdapterEqualsItsAdaptee() {
        assertTrue(nodeAdapter.equalsNode(node));
    }

    public void testNodeAdapterEqualsNoOtherNode() {
        for (Node n : graphFixture.getNodes()) {
            if (n.equals(node)) {
                continue;
            }
            assertFalse(nodeAdapter.equalsNode(n));
        }
    }

    public void testAfterInitializationNodeAdaptersHoldAKeyEqualToPositiveInifinity() {
        nodeAdapter.init(heapAdapter);
        assertEquals(Float.POSITIVE_INFINITY, nodeAdapter.getKey());
    }

    public void testAfterInitializationNodeAdaptersHoldAParentReferenceEqualToANullNodeAdapter() {
        nodeAdapter.init(heapAdapter);
        assertTrue(nodeAdapter.getParent().isNull());
    }

    public void testNodeAdapterContainsMstAttributesAfterInitialization() {
        nodeAdapter.init(heapAdapter);
        assertTrue(attributeExists(node, "mst"));
        assertTrue(attributeExists(node, "mst.parent"));
        assertTrue(attributeExists(node, "mst.heapEntry"));
        assertTrue(attributeExists(node, "mst.selectionFlag"));
    }

    public void testAfterInitializationNodeAdaptersAreNotSelected() {
        nodeAdapter.init(heapAdapter);
        assertFalse(nodeAdapter.isSelected());
    }

    public void testAfterInitializationNoNodeAdapterIsRoot() {
        nodeAdapter.init(heapAdapter);
        assertFalse(nodeAdapter.isRoot());
    }

    public void testAfterSelectionANodeAdapterIsSelected() {
        nodeAdapter.init(heapAdapter);
        nodeAdapter.select();
        assertTrue(nodeAdapter.isSelected());
    }

    private boolean attributeExists(Node node, String path) {
        try {
            node.getAttribute(path);
        } catch (AttributeNotFoundException unexpected) {
            return false;
        }
        return true;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
