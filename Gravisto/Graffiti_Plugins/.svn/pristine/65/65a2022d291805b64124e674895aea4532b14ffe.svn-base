// =============================================================================
//
//   PermutationTopologicalSortTest.java
//
//   Copyright (c) 2001-2011, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package tests.graffiti.plugins.algorithms.permutationgraph;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.permutationgraph.PermutationTopologicalSort;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Emanuel Berndl
 * @version $Revision$ $Date$
 */
public class PermutationTopologicalSortTest {
    
    private Graph emptyGraph;
    private Graph nodeChain;
    private Node[] nodeChainNodes;
    private Graph circle;
    private Node[] circleNodes;
    private Graph testGraph;
    private Node[] testGraphNodes;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        emptyGraph = new AdjListGraph();
        
        nodeChain = new AdjListGraph();
        nodeChainNodes = new Node[4];        
        for(int i = 0; i < nodeChainNodes.length; i++) {
            nodeChainNodes[i] = nodeChain.addNode();
        } 
        nodeChain.addEdge(nodeChainNodes[3], nodeChainNodes[2], true);
        nodeChain.addEdge(nodeChainNodes[2], nodeChainNodes[1], true);
        nodeChain.addEdge(nodeChainNodes[1], nodeChainNodes[0], true);
        
        circle = new AdjListGraph();
        circleNodes = new Node[3];        
        for(int i = 0; i < circleNodes.length; i++) {
            circleNodes[i] = circle.addNode();
        }
        circle.addEdge(circleNodes[0], circleNodes[1], true);
        circle.addEdge(circleNodes[1], circleNodes[2], true);
        circle.addEdge(circleNodes[2], circleNodes[0], true);
        
        testGraph = new AdjListGraph();
        testGraphNodes = new Node[5];
        for(int i = 0; i < testGraphNodes.length; i++) {
            testGraphNodes[i] = testGraph.addNode();
        }
        testGraph.addEdge(testGraphNodes[1], testGraphNodes[0], true);
        testGraph.addEdge(testGraphNodes[3], testGraphNodes[0], true);
        testGraph.addEdge(testGraphNodes[4], testGraphNodes[0], true);
        testGraph.addEdge(testGraphNodes[4], testGraphNodes[3], true);
        testGraph.addEdge(testGraphNodes[3], testGraphNodes[2], true);
        testGraph.addEdge(testGraphNodes[4], testGraphNodes[2], true);
        testGraph.addEdge(testGraphNodes[0], testGraphNodes[2], true);
        testGraph.addEdge(testGraphNodes[1], testGraphNodes[2], true);
        testGraph.addEdge(testGraphNodes[1], testGraphNodes[4], true);
        testGraph.addEdge(testGraphNodes[1], testGraphNodes[3], true);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void emptyGraphTest () {
        List<Node> topologicalSort = PermutationTopologicalSort.topSort(emptyGraph);
        
        List<Node> referenceOutput = null;
        
        assertEquals(referenceOutput, topologicalSort);
    }

    @Test
    public void nodeChainTest () {
        List<Node> topologicalSort = PermutationTopologicalSort.topSort(nodeChain);
        
        List<Node> referenceOutput = new ArrayList<Node>();
        referenceOutput.add(nodeChainNodes[3]);
        referenceOutput.add(nodeChainNodes[2]);
        referenceOutput.add(nodeChainNodes[1]);
        referenceOutput.add(nodeChainNodes[0]);
        
        assertEquals(referenceOutput, topologicalSort);
    }
    
    @Test
    public void circleTest () {
        List<Node> topologicalSort = PermutationTopologicalSort.topSort(circle);
        
        List<Node> referenceOutput = null;
        
        assertEquals(referenceOutput, topologicalSort);
    }
    
    @Test
    public void sortTest () {
        List<Node> topologicalSort = PermutationTopologicalSort.topSort(testGraph);
        
        List<Node> referenceOutput = new ArrayList<Node>();
        referenceOutput.add(testGraphNodes[1]);
        referenceOutput.add(testGraphNodes[4]);
        referenceOutput.add(testGraphNodes[3]);
        referenceOutput.add(testGraphNodes[0]);
        referenceOutput.add(testGraphNodes[2]);
        
        assertEquals(referenceOutput, topologicalSort);
    }

}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
