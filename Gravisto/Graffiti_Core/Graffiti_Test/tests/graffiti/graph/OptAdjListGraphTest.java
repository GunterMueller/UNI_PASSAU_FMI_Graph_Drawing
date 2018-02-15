// =============================================================================
//
//   OptAdjListGraphTest.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: OptAdjListGraphTest.java 5771 2010-05-07 18:46:57Z gleissner $

package tests.graffiti.graph;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.graffiti.attributes.IntegerAttribute;
import org.graffiti.attributes.StringAttribute;
import org.graffiti.graph.AdjListNode;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElementNotFoundException;
import org.graffiti.graph.Node;
import org.graffiti.graph.OptAdjListGraph;

/**
 * Contains test cases for the adjacency list implementation of the
 * <code>org.graffiti.graph.Graph</code> interface.
 * 
 * @version $Revision: 5771 $
 */
public class OptAdjListGraphTest extends TestCase {
    /** The graph for the test cases. */
    private Graph g = new OptAdjListGraph();

    /**
     * Constructs a new test case for the <code>OptAdjListGraph</code> class.
     * 
     * @param name
     *            the name for the test case.
     */
    public OptAdjListGraphTest(String name) {
        super(name);
    }

    /**
     * Main method for running all the test cases of this test class.
     * 
     * @param args
     *            DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(OptAdjListGraphTest.class);
    }

    /**
     * Tests adding an edge to the graph.
     */
    public void testAddEdge() {
        Node n1 = g.addNode();
        assertNotNull("Added node n1 is not null.", n1);

        Node n2 = g.addNode();
        assertNotNull("Added node n2 is not null.", n2);

        int noEdges;
        noEdges = g.getNumberOfEdges();

        Edge e = g.addEdge(n1, n2, Edge.DIRECTED);
        assertNotNull("Added edge e is not null.", e);
        assertTrue("g contains edge e.", g.containsEdge(e));
        assertTrue("number of edges has correctly been modified.", g
                .getNumberOfEdges() == (noEdges + 1));
        assertTrue("number of directed edges is correct.", g
                .getNumberOfDirectedEdges() == 1);
        noEdges = g.getNumberOfEdges();

        Graph otherGraph = new OptAdjListGraph();
        Node noNode = otherGraph.addNode();

        try {
            g.addEdge(n1, noNode, Edge.DIRECTED);
            fail("Edge added onto node that is not in the same graph.");
        } catch (GraphElementNotFoundException ge) {
        }

        assertTrue("adding illegal edge does not modify number of edges.",
                noEdges == g.getNumberOfEdges());
    }

    /**
     * Tests copying an edge into the graph.
     */
    public void testAddEdgeCopy() {
        Graph g1 = new OptAdjListGraph();
        Node n1 = g1.addNode();
        Node n2 = g1.addNode();
        Edge e = g1.addEdge(n1, n2, Edge.DIRECTED);
        IntegerAttribute ia = new IntegerAttribute("id");
        Integer testId = new Integer(4711);
        ia.setValue(testId);
        e.addAttribute(ia, "");
        n1 = g.addNode();
        n2 = g.addNode();
        g.addEdgeCopy(e, n1, n2);

        for (Iterator<Edge> itr = n1.getEdgesIterator(); itr.hasNext();) {
            IntegerAttribute iattr = (IntegerAttribute) (itr.next()
                    .getAttribute("id"));
            assertTrue("id is still the same.", ((Integer) iattr.getValue())
                    .equals(testId));
        }
    }

    /**
     * Tests adding a graph.
     */
    public void testAddGraph() {
        Graph toAdd = new OptAdjListGraph();
        Node n1 = toAdd.addNode();
        IntegerAttribute ia1 = new IntegerAttribute("id");
        Integer testId1 = new Integer(4711);
        ia1.setValue(testId1);
        n1.addAttribute(ia1, "");

        Node n2 = toAdd.addNode();
        IntegerAttribute ia2 = new IntegerAttribute("id");
        Integer testId2 = new Integer(5813);
        ia2.setValue(testId2);
        n2.addAttribute(ia2, "");

        Edge e = toAdd.addEdge(n1, n2, Edge.DIRECTED);
        IntegerAttribute iae = new IntegerAttribute("id");
        Integer testIde = new Integer(6914);
        iae.setValue(testIde);
        e.addAttribute(iae, "");

        int toAddNoNodes = toAdd.getNumberOfNodes();
        int toAddNoEdges = toAdd.getNumberOfEdges();
        int gNoNodes = g.getNumberOfNodes();
        int gNoEdges = g.getNumberOfEdges();
        g.addGraph(toAdd);
        assertTrue("graph is empty.", !g.isEmpty());
        assertTrue("graph to be added is empty before being added.", !toAdd
                .isEmpty());
        assertTrue("number of nodes in g has correctly been modified.", g
                .getNumberOfNodes() == (toAddNoNodes + gNoNodes));
        assertTrue("number of edges in g has correctly been modified.", g
                .getNumberOfEdges() == (toAddNoEdges + gNoEdges));

        for (Iterator<Edge> itr = g.getEdgesIterator(); itr.hasNext();) {
            IntegerAttribute iattr = (IntegerAttribute) (itr.next()
                    .getAttribute("id"));
            assertTrue("id of the edge is still the same.", ((Integer) iattr
                    .getValue()).equals(testIde));
        }

        for (Iterator<Edge> it = g.getEdgesIterator(); it.hasNext();) {
            Edge copyEdge = it.next();
            Node copyn1 = copyEdge.getSource();
            Node copyn2 = copyEdge.getTarget();
            assertEquals(4711, ((IntegerAttribute) copyn1.getAttribute("id"))
                    .getInteger());
            assertEquals(5813, ((IntegerAttribute) copyn2.getAttribute("id"))
                    .getInteger());
        }
    }

    /**
     * Tests adding a node to the graph.
     */
    public void testAddNode() {
        int noNodes = g.getNumberOfNodes();
        Node n = g.addNode();
        assertTrue("g is not empty after inserting a node.", !g.isEmpty());
        assertTrue("g contains added node n.", g.containsNode(n));
        assertTrue("number of nodes has correctly been modified.",
                noNodes == (g.getNumberOfNodes() - 1));
    }

    /**
     * Tests copying a node into the graph.
     */
    public void testAddNodeCopy() {
        Graph tmp = new OptAdjListGraph();

        // Node n = new AdjListNode(tmp);
        Node n = tmp.addNode();
        IntegerAttribute ia = new IntegerAttribute("id");
        Integer testId = new Integer(4711);
        ia.setValue(testId);
        n.addAttribute(ia, "");
        g.addNodeCopy(n);

        Iterator<Node> i = g.getNodesIterator();

        if (!i.hasNext()) {
            fail("added node not in the graph.");
        } else {
            AdjListNode node = (AdjListNode) i.next();
            IntegerAttribute iattr = (IntegerAttribute) node.getAttribute("id");
            assertEquals("added node in the graph.", ((Integer) iattr
                    .getValue()).intValue(), testId.intValue());
        }
    }

    /**
     * Test the areConnected() method.
     */
    public void testAreConnected() {
        Node n1 = g.addNode();
        Node n2 = g.addNode();
        g.addEdge(n1, n2, Edge.DIRECTED);
        assertTrue("n1 and n2 are not recognized as being connected.",
                ((OptAdjListGraph) g).areConnected(n1, n2));
        assertTrue("n1 and n2 are not recognized as being connected.",
                ((OptAdjListGraph) g).areConnected(n2, n1));
    }

    /**
     * Tests if the graph will be cleared correctly.
     */
    public void testClear() {
        g.clear();
        assertTrue("cleared graph contains no nodes.",
                g.getNumberOfNodes() == 0);
        assertTrue("cleared graph contains no edges.",
                g.getNumberOfEdges() == 0);
        assertTrue("cleared graph is empty.", g.isEmpty());
    }

    /**
     * Tests if graph contains edge.
     */
    public void testContainsEdge() {
        Node n1 = g.addNode();
        Node n2 = g.addNode();
        Node n3 = g.addNode();
        Node n4 = g.addNode();
        Edge e1 = g.addEdge(n1, n2, Edge.UNDIRECTED);
        Edge e2 = g.addEdge(n3, n4, Edge.UNDIRECTED);
        g.deleteEdge(e2);
        assertTrue("contains deleted edge e2.", !g.containsEdge(e2));
        assertTrue("does not contain added edge e1.", g.containsEdge(e1));

        // M.S.: won't work anyway with the new package private constructors
        // assertTrue("contains unknown edge.",
        // !g.containsEdge(new AdjListEdge(g, n1, new AdjListNode(g),
        // Edge.DIRECTED)));
    }

    /**
     * Tests if graph contains node.
     */
    public void testContainsNode() {
        Node n1 = g.addNode();
        Node n2 = g.addNode();
        g.deleteNode(n1);
        assertTrue("contains deleted node n1.", !g.containsNode(n1));
        assertTrue("does not contain node n2.", g.containsNode(n2));

        Graph g2 = new OptAdjListGraph();
        Node n3 = g2.addNode();
        assertTrue("contains a node that can't be in the graph!", !g
                .containsNode(n3));
    }

    /**
     * Tests the copy() method.
     */
    public void testCopy() {
        Node n = g.addNode();
        n.setInteger("id", 1);
        g.setString("name", "sepp");

        Graph gCopied = (Graph) g.copy();
        int countNodes = 0;

        for (Iterator<Node> i = gCopied.getNodesIterator(); i.hasNext();) {
            assertEquals(1, i.next().getInteger("id"));
            countNodes++;
        }

        assertEquals(1, countNodes);
        assertEquals("sepp", ((StringAttribute) gCopied.getAttribute("name"))
                .getString());
    }

    /**
     * Tests if edge will be deleted.
     */
    public void testDeleteEdge() {
        Node n1 = g.addNode();
        Node n2 = g.addNode();
        Edge e1 = g.addEdge(n1, n2, Edge.DIRECTED);
        Edge e2 = g.addEdge(n2, n1, Edge.UNDIRECTED);
        int noEdges = g.getNumberOfEdges();
        int noDirEdges = g.getNumberOfDirectedEdges();
        int noUndirEdges = g.getNumberOfUndirectedEdges();
        assertTrue("g does not contain added edge e1.", g.containsEdge(e1));
        assertTrue("g does not contain added edge e2.", g.containsEdge(e2));
        g.deleteEdge(e1);
        assertTrue("still contains deleted edge.", !g.containsEdge(e1));
        assertTrue("number of edges has not been modified correctly.", g
                .getNumberOfEdges() == (noEdges - 1));
        assertTrue("number of directed edges has not been modified correctly.",
                g.getNumberOfDirectedEdges() == (noDirEdges - 1));
        assertTrue("number of undirected edges has been modified incorrectly.",
                g.getNumberOfUndirectedEdges() == noUndirEdges);
        g.deleteEdge(e2);
        assertTrue("still contains deleted edge e2.", !g.containsEdge(e2));
        assertTrue("number of edges has not been modified correctly.", g
                .getNumberOfEdges() == (noEdges - 2));
        assertTrue("number of undirected edges has not been modified "
                + "correctly.",
                g.getNumberOfUndirectedEdges() == (noUndirEdges - 1));
        assertTrue("number of directed edges has been modified incorrectly.", g
                .getNumberOfDirectedEdges() == (noDirEdges - 1));
    }

    /**
     * Tests if node will be deleted.
     */
    public void testDeleteNode() {
        Node n = g.addNode();
        int noNodes = g.getNumberOfNodes();
        g.deleteNode(n);
        assertTrue("contains still deleted node.", !g.containsNode(n));
        assertTrue("number of nodes has not been modified correctly. ", g
                .getNumberOfNodes() == (noNodes - 1));
    }

    /**
     * Tests what happens if the node is deleted from the graph but a reference
     * to the node still exists.
     */
    public void testDeleteNode2() {
        Node n1 = g.addNode();
        Node n2 = g.addNode();
        g.deleteNode(n1);

        try {
            g.addEdge(n1, n2, Edge.DIRECTED);
            fail("A GENF-Exception should have been thrown. One of the nodes "
                    + "has been deleted already from the graph.");
        } catch (GraphElementNotFoundException genf) {
        }
    }

    /**
     * Tests if all the edges are contained in the edge collection.
     */
    public void testGetEdges() {
        Node n1 = g.addNode();
        Node n2 = g.addNode();
        Node n3 = g.addNode();
        Node n4 = g.addNode();
        Edge e1 = g.addEdge(n1, n2, Edge.UNDIRECTED);
        Edge e2 = g.addEdge(n1, n2, Edge.DIRECTED);
        Edge e3 = g.addEdge(n3, n4, Edge.DIRECTED);
        Edge e4 = g.addEdge(n4, n1, Edge.DIRECTED);
        Edge e5 = g.addEdge(n3, n1, Edge.UNDIRECTED);
        Collection<Edge> coll = g.getEdges();
        assertTrue("e1 is contained in the collection.", coll.contains(e1));
        coll.remove(e1);
        assertTrue("e2 is contained in the collection.", coll.contains(e2));
        coll.remove(e2);
        assertTrue("e3 is contained in the collection.", coll.contains(e3));
        coll.remove(e3);
        assertTrue("e4 is contained in the collection.", coll.contains(e4));
        coll.remove(e4);
        assertTrue("e5 is contained in the collection.", coll.contains(e5));
        coll.remove(e5);
        assertTrue("collection does not contain more elements.", coll.isEmpty());
    }

    /**
     * Tests if all the edges between two nodes are contained in the #
     * corresponding collection.
     */
    public void testGetEdgesBetweenTwoNodes() {
        Node n1 = g.addNode();
        Node n2 = g.addNode();
        Node n3 = g.addNode();
        Node n4 = g.addNode();
        Edge e1 = g.addEdge(n1, n2, Edge.UNDIRECTED);
        Edge e2 = g.addEdge(n1, n2, Edge.DIRECTED);
        Edge e3 = g.addEdge(n3, n4, Edge.DIRECTED);
        Edge e4 = g.addEdge(n1, n2, Edge.DIRECTED);
        Edge e5 = g.addEdge(n3, n1, Edge.UNDIRECTED);
        Collection<Edge> coll = g.getEdges(n1, n2);
        assertTrue("e1 is contained in the collection.", coll.contains(e1));
        coll.remove(e1);
        assertTrue("e2 is contained in the collection.", coll.contains(e2));
        coll.remove(e2);
        assertTrue("e3 is not contained in the collection.", !coll.contains(e3));
        coll.remove(e3);
        assertTrue("e4 is contained in the collection.", coll.contains(e4));
        coll.remove(e4);
        assertTrue("e5 is not contained in the collection.", !coll.contains(e5));
        coll.remove(e5);
        assertTrue("collection does not contain more elements.", coll.isEmpty());
    }

    /**
     * Tests if all the edges are contained in the edge iterator.
     */
    public void testGetEdgesIterator() {
        Node n1 = g.addNode();
        Node n2 = g.addNode();
        Node n3 = g.addNode();
        Node n4 = g.addNode();
        Edge e1 = g.addEdge(n1, n2, Edge.UNDIRECTED);
        Edge e2 = g.addEdge(n1, n2, Edge.DIRECTED);
        Edge e3 = g.addEdge(n3, n4, Edge.DIRECTED);
        Edge e4 = g.addEdge(n4, n1, Edge.DIRECTED);
        Edge e5 = g.addEdge(n3, n1, Edge.UNDIRECTED);
        List<Edge> edgeList = new LinkedList<Edge>();
        edgeList.add(e1);
        edgeList.add(e2);
        edgeList.add(e3);
        edgeList.add(e4);
        edgeList.add(e5);

        for (Iterator<Edge> edgeItr = g.getEdgesIterator(); edgeItr.hasNext();) {
            Edge e = edgeItr.next();
            assertTrue("edge found in the iterator.", edgeList.contains(e));

            if (!edgeList.remove(e)) {
                fail("Unable to remove edge.");
            }
        }
    }

    /**
     * Tests if all the nodes are contained in the nodes collection.
     */
    public void testGetNodes() {
        Node n1 = g.addNode();
        Node n2 = g.addNode();
        Node n3 = g.addNode();
        Node n4 = g.addNode();
        Collection<Node> coll = g.getNodes();
        assertTrue("n1 is contained in the collection.", coll.contains(n1));
        coll.remove(n1);
        assertTrue("n2 is contained in the collection.", coll.contains(n2));
        coll.remove(n2);
        assertTrue("n3 is contained in the collection.", coll.contains(n3));
        coll.remove(n3);
        assertTrue("n4 is contained in the collection.", coll.contains(n4));
        coll.remove(n4);
        assertTrue("collection does not contain more elements.", coll.isEmpty());
    }

    /**
     * Tests if all the nodes are contained in the node iterator.
     */
    public void testGetNodesIterator() {
        Node n1 = g.addNode();
        Node n2 = g.addNode();
        Node n3 = g.addNode();
        Node n4 = g.addNode();
        List<Node> nodeList = new LinkedList<Node>();
        nodeList.add(n1);
        nodeList.add(n2);
        nodeList.add(n3);
        nodeList.add(n4);

        for (Iterator<Node> nodeItr = g.getNodesIterator(); nodeItr.hasNext();) {
            Node n = nodeItr.next();
            assertTrue("node found in the iterator.", nodeList.contains(n));

            if (!nodeList.remove(n)) {
                fail("Unable to remove node.");
            }
        }
    }

    /**
     * Tests if number of directed edges is correct.
     */
    public void testGetNumberOfDirectedEdges() {
        Node n1 = g.addNode();
        Node n2 = g.addNode();
        Node n3 = g.addNode();
        Node n4 = g.addNode();
        g.addEdge(n1, n2, Edge.UNDIRECTED);
        g.addEdge(n1, n2, Edge.DIRECTED);
        g.addEdge(n3, n4, Edge.DIRECTED);
        g.addEdge(n4, n1, Edge.DIRECTED);
        g.addEdge(n3, n1, Edge.UNDIRECTED);
        assertEquals("count # directed edges.", 3, g.getNumberOfDirectedEdges());
    }

    /**
     * Tests if number of edges is correct.
     */
    public void testGetNumberOfEdges() {
        Node n1 = g.addNode();
        Node n2 = g.addNode();
        Node n3 = g.addNode();
        Node n4 = g.addNode();
        g.addEdge(n1, n2, Edge.DIRECTED);
        g.addEdge(n1, n2, Edge.DIRECTED);
        g.addEdge(n3, n4, Edge.DIRECTED);
        g.addEdge(n4, n1, Edge.DIRECTED);
        g.addEdge(n3, n1, Edge.DIRECTED);
        assertEquals("count #edges.", 5, g.getNumberOfEdges());
        assertEquals("count # directed edges.", 5, g.getNumberOfDirectedEdges());
        assertEquals("count # undirected edges.", 0, g
                .getNumberOfUndirectedEdges());
    }

    /**
     * Tests if number of nodes is correct.
     */
    public void testGetNumberOfNodes() {
        g.addNode();
        g.addNode();
        g.addNode();
        g.addNode();
        assertEquals("count # nodes.", 4, g.getNumberOfNodes());
    }

    /**
     * Tests if number of undirected edges is correct.
     */
    public void testGetNumberOfUndirectedEdges() {
        Node n1 = g.addNode();
        Node n2 = g.addNode();
        Node n3 = g.addNode();
        Node n4 = g.addNode();
        g.addEdge(n1, n2, Edge.UNDIRECTED);
        g.addEdge(n1, n2, Edge.DIRECTED);
        g.addEdge(n3, n4, Edge.DIRECTED);
        g.addEdge(n4, n1, Edge.DIRECTED);
        g.addEdge(n3, n1, Edge.UNDIRECTED);
        assertEquals("count # undirected edges.", 2, g
                .getNumberOfUndirectedEdges());
    }

    /**
     * Tests if directed graph is directed.
     */
    public void testIsDirected() {
        Node n1 = g.addNode();
        Node n2 = g.addNode();
        Node n3 = g.addNode();
        Node n4 = g.addNode();
        g.addEdge(n1, n2, Edge.DIRECTED);
        g.addEdge(n3, n4, Edge.DIRECTED);

        Edge e = g.addEdge(n2, n3, Edge.UNDIRECTED);
        assertTrue("graph with undirected edge should not be directed.", !g
                .isDirected());
        g.deleteEdge(e);
        assertTrue("isDirected() should have returned true.", g.isDirected());
        g.clear();
        assertTrue("empty graph should be directed.", g.isDirected());
    }

    /**
     * Tests if cleared graph is empty and contains no nodes or edges.
     */
    public void testIsEmpty() {
        g.clear();
        assertTrue("graph is empty.", g.isEmpty());
        assertEquals("graph has no edges.", 0, g.getNumberOfEdges());
        assertEquals("graph has no directed edges.", 0, g
                .getNumberOfDirectedEdges());
        assertEquals("graph has no undirected edges.", 0, g
                .getNumberOfUndirectedEdges());
        assertEquals("graph has no nodes.", 0, g.getNumberOfNodes());
    }

    /**
     * Tests undirected graph is undirected.
     */
    public void testIsUndirected() {
        Node n1 = g.addNode();
        Node n2 = g.addNode();
        Node n3 = g.addNode();
        Node n4 = g.addNode();
        g.addEdge(n1, n2, Edge.UNDIRECTED);
        g.addEdge(n3, n4, Edge.UNDIRECTED);

        Edge e = g.addEdge(n2, n3, Edge.DIRECTED);
        assertTrue("graph with directed edge should not be undirected.", !g
                .isUndirected());
        g.deleteEdge(e);
        assertTrue("isUndirected() should have returned true.", g
                .isUndirected());
        g.clear();
        assertTrue("empty graph should be undirected.", g.isUndirected());
    }

    /**
     * Initializes a new graph for every test case.
     */
    protected void setUp() {
        g = new OptAdjListGraph();
    }

    /**
     * Tears down the test environement.
     */
    protected void tearDown() {
        g.clear();
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
