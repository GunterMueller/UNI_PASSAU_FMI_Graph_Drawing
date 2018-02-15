// =============================================================================
//
//   PermutationGraphChangingTest.java
//
//   Copyright (c) 2001-2011, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package tests.graffiti.plugins.algorithms.permutationgraph;


import static org.junit.Assert.*;

import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.permutationgraph.PermutationGraphChangings;
import org.graffiti.plugins.algorithms.permutationgraph.PermutationTransitiveOrientation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Emanuel Berndl
 * @version $Revision$ $Date$
 */
public class PermutationGraphChangingTest {
    
    private Graph twoNodes;
    private Node[] twoNodesNodes;
    private Graph undirectedGraph;
    private Node[] undirectedGraphNodes;
    private Graph testGraph;
    private Node[] testGraphNodes;
    private Graph combineGraphOne;
    private Graph combineGraphTwo;
    private Node[] combineGraphOneNodes;
    private Node[] combineGraphTwoNodes;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        twoNodes = new AdjListGraph();
        twoNodesNodes = new Node[2];
        twoNodesNodes[0] = twoNodes.addNode();
        twoNodesNodes[1] = twoNodes.addNode();
        twoNodes.addEdge(twoNodesNodes[0], twoNodesNodes[1], true);
        
        undirectedGraph = new AdjListGraph();
        undirectedGraphNodes = new Node[2];
        undirectedGraphNodes[0] = undirectedGraph.addNode();
        undirectedGraphNodes[1] = undirectedGraph.addNode();
        undirectedGraph.addEdge(undirectedGraphNodes[0], undirectedGraphNodes[1], false);
        
        testGraph = new AdjListGraph();
        testGraphNodes = new Node[5];
        for(int i = 0; i < 5; i++) {
            testGraphNodes[i] = testGraph.addNode();
        }
        testGraph.addEdge(testGraphNodes[0], testGraphNodes[1], true);
        testGraph.addEdge(testGraphNodes[0], testGraphNodes[3], true);
        testGraph.addEdge(testGraphNodes[0], testGraphNodes[4], true);
        testGraph.addEdge(testGraphNodes[2], testGraphNodes[3], true);
        testGraph.addEdge(testGraphNodes[2], testGraphNodes[4], true);
        testGraph.addEdge(testGraphNodes[3], testGraphNodes[4], true);
        
        combineGraphOne = new AdjListGraph();
        combineGraphTwo = new AdjListGraph();
        combineGraphOneNodes = new Node[3];
        combineGraphTwoNodes = new Node[3];
        for(int i = 0; i < combineGraphOneNodes.length; i++) {
            combineGraphOneNodes[i] = combineGraphOne.addNode();
            combineGraphTwoNodes[i] = combineGraphTwo.addNode();
        }
        combineGraphOne.addEdge(combineGraphOneNodes[0], combineGraphOneNodes[1], true);
        combineGraphTwo.addEdge(combineGraphTwoNodes[1], combineGraphTwoNodes[2], true);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void twoNodesTest () {
        Graph invertedGraph = PermutationGraphChangings.invertGraph(twoNodes);
        
        assertEquals(invertedGraph.getNodes().get(0).getAllInEdges().size(), 1);
        assertEquals(invertedGraph.getNodes().get(0).getAllOutNeighbors().size(), 0);
        
        assertEquals(invertedGraph.getNodes().get(1).getAllInEdges().size(), 0);
        assertEquals(invertedGraph.getNodes().get(1).getAllOutNeighbors().size(), 1);
    }
    
    @Test
    public void undirectedGraphTest () {
        Graph invertedGraph = PermutationGraphChangings.invertGraph(undirectedGraph);
        
        assertEquals(null, invertedGraph);
    }
    
    @Test
    public void testGraphTest () {
        Graph invertedGraph = PermutationGraphChangings.invertGraph(testGraph);
        
        assertEquals(invertedGraph.getNodes().get(0).getAllInNeighbors().size(), 3);
        assertEquals(invertedGraph.getNodes().get(0).getAllOutNeighbors().size(), 0);
        assertEquals(invertedGraph.getNodes().get(1).getAllInNeighbors().size(), 0);
        assertEquals(invertedGraph.getNodes().get(1).getAllOutNeighbors().size(), 1);
        assertEquals(invertedGraph.getNodes().get(2).getAllInNeighbors().size(), 2);
        assertEquals(invertedGraph.getNodes().get(2).getAllOutNeighbors().size(), 0);
        assertEquals(invertedGraph.getNodes().get(3).getAllInNeighbors().size(), 1);
        assertEquals(invertedGraph.getNodes().get(3).getAllOutNeighbors().size(), 2);
        assertEquals(invertedGraph.getNodes().get(4).getAllInNeighbors().size(), 0);
        assertEquals(invertedGraph.getNodes().get(4).getAllOutNeighbors().size(), 3);
    }
    
    @Test
    public void twoNodesComplementTest () {
        Graph complement = PermutationGraphChangings.complementGraph(twoNodes);
        
        assertEquals(0, complement.getNodes().get(0).getNeighbors().size());
        assertEquals(0, complement.getNodes().get(1).getNeighbors().size());
    }
    
    @Test
    public void testGraphComplement () {
        Graph complement = PermutationGraphChangings.complementGraph(testGraph);
        
        assertEquals(1, complement.getNodes().get(0).getNeighbors().size());
        assertEquals(3, complement.getNodes().get(1).getNeighbors().size());
        assertEquals(2, complement.getNodes().get(2).getNeighbors().size());
        assertEquals(1, complement.getNodes().get(3).getNeighbors().size());
        assertEquals(1, complement.getNodes().get(4).getNeighbors().size());
    }
    
    @Test
    public void combineGraphTest () {
        Graph combination = PermutationGraphChangings.combineGraphs(combineGraphOne, combineGraphTwo);
        
        assertEquals(1, combination.getNodes().get(0).getAllOutNeighbors().size());
        assertEquals(1, combination.getNodes().get(1).getAllInEdges().size());
        assertEquals(1, combination.getNodes().get(1).getAllOutNeighbors().size());
        assertEquals(1, combination.getNodes().get(2).getAllInEdges().size());
    }
    
    @Test
    public void combineComplementTest () {
        Graph complement = PermutationGraphChangings.complementGraph(combineGraphOne);
        PermutationTransitiveOrientation.transitivelyOrientGraph(complement);
        Graph combination = PermutationGraphChangings.combineGraphs(combineGraphOne, complement);
        
        // Combining a graph with its complement should lead to a complete graph (with oriented edges), which should have 3 edges at 3 nodes
        assertEquals(3, combination.getEdges().size());
    }

}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
