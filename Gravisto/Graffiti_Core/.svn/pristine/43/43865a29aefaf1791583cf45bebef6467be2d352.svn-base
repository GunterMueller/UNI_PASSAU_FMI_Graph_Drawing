// =============================================================================
//
//   PermutationCalcTest.java
//
//   Copyright (c) 2001-2011, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package tests.graffiti.plugins.algorithms.permutationgraph;


import static org.junit.Assert.*;

import java.util.List;

import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.permutationgraph.PermutationCalc;
import org.graffiti.plugins.algorithms.permutationgraph.PermutationGraphChangings;
import org.graffiti.plugins.algorithms.permutationgraph.PermutationTopologicalSort;
import org.graffiti.plugins.algorithms.permutationgraph.PermutationTransitiveOrientation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Emanuel Berndl
 * @version $Revision$ $Date$
 */
public class PermutationCalcTest {
    
    private Graph simpleGraph;
    private Node[] simpleGraphNodes;
    private Graph testGraph;
    private Node[] testGraphNodes;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        simpleGraph = new AdjListGraph();
        simpleGraphNodes = new Node[2];
        for(int i = 0; i < simpleGraphNodes.length; i++) {
            simpleGraphNodes[i] = simpleGraph.addNode();
        }
        simpleGraph.addEdge(simpleGraphNodes[0], simpleGraphNodes[1], true);
        
        testGraph = new AdjListGraph();
        testGraphNodes = new Node[5];
        for(int i = 0; i < testGraphNodes.length; i++) {
            testGraphNodes[i] = testGraph.addNode();
        }
        testGraph.addEdge(testGraphNodes[1], testGraphNodes[0], false);
        testGraph.addEdge(testGraphNodes[3], testGraphNodes[0], false);
        testGraph.addEdge(testGraphNodes[4], testGraphNodes[0], false);
        testGraph.addEdge(testGraphNodes[4], testGraphNodes[3], false);
        testGraph.addEdge(testGraphNodes[3], testGraphNodes[2], false);
        testGraph.addEdge(testGraphNodes[4], testGraphNodes[2], false);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void simpleGraphTest () {
        Graph complement = PermutationGraphChangings.complementGraph(simpleGraph);
        
        PermutationTransitiveOrientation.transitivelyOrientGraph(simpleGraph);
        PermutationTransitiveOrientation.transitivelyOrientGraph(complement);
        
        Graph combinedGraphOne = PermutationGraphChangings.combineGraphs(simpleGraph, complement);
        Graph inverse = PermutationGraphChangings.invertGraph(simpleGraph);
        Graph combinedGraphTwo = PermutationGraphChangings.combineGraphs(inverse, complement);
        
        List<Node> topologicalSort = PermutationTopologicalSort.topSort(combinedGraphOne);
        List<Node> topologicalSortTwo = PermutationTopologicalSort.topSort(combinedGraphTwo);
        
        int[] permutation = PermutationCalc.calculatePermutation(combinedGraphOne, combinedGraphTwo, topologicalSort, topologicalSortTwo);
        
        int[] referenceOutput = {1, 0};
        
        assertEquals(referenceOutput[0], permutation[0]);
        assertEquals(referenceOutput[1], permutation[1]);
    }
    
    @Test
    public void testGraph () {
        Graph complement = PermutationGraphChangings.complementGraph(testGraph);
        
        PermutationTransitiveOrientation.transitivelyOrientGraph(testGraph);
        PermutationTransitiveOrientation.transitivelyOrientGraph(complement);
        
        Graph combinedGraphOne = PermutationGraphChangings.combineGraphs(testGraph, complement);
        Graph inverse = PermutationGraphChangings.invertGraph(testGraph);
        Graph combinedGraphTwo = PermutationGraphChangings.combineGraphs(inverse, complement);
        
        List<Node> topologicalSort = PermutationTopologicalSort.topSort(combinedGraphOne);
        List<Node> topologicalSortTwo = PermutationTopologicalSort.topSort(combinedGraphTwo);
        
        int[] permutation = PermutationCalc.calculatePermutation(combinedGraphOne, combinedGraphTwo, topologicalSort, topologicalSortTwo);
        
        int[] referenceOutput = {1, 4, 3, 0, 2};
        
        assertEquals(referenceOutput[0], permutation[0]);
        assertEquals(referenceOutput[1], permutation[1]); 
        assertEquals(referenceOutput[2], permutation[2]);
        assertEquals(referenceOutput[3], permutation[3]);
        assertEquals(referenceOutput[4], permutation[4]);
    }

}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
