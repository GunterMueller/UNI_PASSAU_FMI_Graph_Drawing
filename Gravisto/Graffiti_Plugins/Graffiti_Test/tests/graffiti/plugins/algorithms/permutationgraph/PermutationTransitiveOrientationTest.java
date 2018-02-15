// =============================================================================
//
//   PermutationTransitiveOrientationTest.java
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
import org.graffiti.plugins.algorithms.permutationgraph.ModularDecompositionNodeException;
import org.graffiti.plugins.algorithms.permutationgraph.ModularDecompositionTree;
import org.graffiti.plugins.algorithms.permutationgraph.PermutationTransitiveOrientation;
import org.graffiti.plugins.algorithms.permutationgraph.RestrictFailedExcpetion;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Emanuel Berndl
 * @version $Revision$ $Date$
 */
public class PermutationTransitiveOrientationTest {

    private Graph edgelessGraph;
    private List<Node> edgelessGraphNodes; 
    private Graph completeGraph;
    private List<Node> completeGraphNodes;
    private Graph testGraph;
    private Node[] testGraphNodes;
    private Graph testGraph2;
    private Node[] testGraph2Nodes;
    private Graph testGraph3;
    private Node[] testGraph3Nodes;
    private Graph testGraph4;
    private Node[] testGraph4Nodes;
    private Graph bigGraph;
    private Node [] bigGraphNodes; 
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        edgelessGraph = new AdjListGraph();
        edgelessGraphNodes = new ArrayList<Node>();
        for(int i = 0; i < 5; i++) {
            edgelessGraphNodes.add(edgelessGraph.addNode());
        }
        
        completeGraph = new AdjListGraph();
        completeGraphNodes = new ArrayList<Node>();
        for(int i = 0; i < 5; i++) {
            completeGraphNodes.add(completeGraph.addNode());
        }
        
        testGraph = new AdjListGraph();
        testGraphNodes = new Node[8];
        for(int i = 0; i < 8; i++) {
            testGraphNodes[i] = testGraph.addNode();
        }
        testGraph.addEdge(testGraphNodes[0], testGraphNodes[1], false);
        testGraph.addEdge(testGraphNodes[0], testGraphNodes[7], false);
        testGraph.addEdge(testGraphNodes[1], testGraphNodes[6], false);
        testGraph.addEdge(testGraphNodes[1], testGraphNodes[2], false);
        testGraph.addEdge(testGraphNodes[1], testGraphNodes[7], false);
        testGraph.addEdge(testGraphNodes[2], testGraphNodes[3], false);
        testGraph.addEdge(testGraphNodes[2], testGraphNodes[7], false);
        testGraph.addEdge(testGraphNodes[3], testGraphNodes[4], false);
        testGraph.addEdge(testGraphNodes[3], testGraphNodes[7], false);
        testGraph.addEdge(testGraphNodes[4], testGraphNodes[5], false);
        testGraph.addEdge(testGraphNodes[5], testGraphNodes[6], false);
        
        testGraph2 = new AdjListGraph();
        testGraph2Nodes = new Node[4];
        for(int i = 0; i < 4; i++) {
            testGraph2Nodes[i] = testGraph2.addNode();
        }
        testGraph2.addEdge(testGraph2Nodes[0], testGraph2Nodes[2], false);
        testGraph2.addEdge(testGraph2Nodes[1], testGraph2Nodes[2], false);
        testGraph2.addEdge(testGraph2Nodes[1], testGraph2Nodes[3], false);
        
        testGraph3 = new AdjListGraph();
        testGraph3Nodes = new Node[5];
        for(int i = 0; i < 5; i++) {
            testGraph3Nodes[i] = testGraph3.addNode();
        }
        testGraph3.addEdge(testGraph3Nodes[0], testGraph3Nodes[1], false);
        testGraph3.addEdge(testGraph3Nodes[0], testGraph3Nodes[3], false);
        testGraph3.addEdge(testGraph3Nodes[0], testGraph3Nodes[4], false);
        testGraph3.addEdge(testGraph3Nodes[2], testGraph3Nodes[3], false);
        testGraph3.addEdge(testGraph3Nodes[2], testGraph3Nodes[4], false);
        testGraph3.addEdge(testGraph3Nodes[3], testGraph3Nodes[4], false);
        
        testGraph4 = new AdjListGraph();
        testGraph4Nodes = new Node[8];
        for(int i = 0; i < 8; i++) {
            testGraph4Nodes[i] = testGraph4.addNode();
        }
        testGraph4.addEdge(testGraph4Nodes[0], testGraph4Nodes[1], false);
        testGraph4.addEdge(testGraph4Nodes[0], testGraph4Nodes[2], false);
        testGraph4.addEdge(testGraph4Nodes[1], testGraph4Nodes[6], false);
        testGraph4.addEdge(testGraph4Nodes[1], testGraph4Nodes[2], false);
        testGraph4.addEdge(testGraph4Nodes[1], testGraph4Nodes[3], false);
        testGraph4.addEdge(testGraph4Nodes[2], testGraph4Nodes[3], false);
        testGraph4.addEdge(testGraph4Nodes[2], testGraph4Nodes[4], false);
        testGraph4.addEdge(testGraph4Nodes[3], testGraph4Nodes[4], false);
        testGraph4.addEdge(testGraph4Nodes[4], testGraph4Nodes[5], false);
        testGraph4.addEdge(testGraph4Nodes[5], testGraph4Nodes[7], false);
        testGraph4.addEdge(testGraph4Nodes[6], testGraph4Nodes[7], false);
        
        bigGraph = new AdjListGraph();
        bigGraphNodes = new Node[11];
        for (int i = 0; i < 11; i++) {
            bigGraphNodes[i] = bigGraph.addNode();
        }
        
        bigGraph.addEdge(bigGraphNodes[0], bigGraphNodes[1], false);
        bigGraph.addEdge(bigGraphNodes[0], bigGraphNodes[2], false);
        bigGraph.addEdge(bigGraphNodes[0], bigGraphNodes[3], false);
        
        bigGraph.addEdge(bigGraphNodes[1], bigGraphNodes[3], false);
        bigGraph.addEdge(bigGraphNodes[1], bigGraphNodes[4], false);
        bigGraph.addEdge(bigGraphNodes[1], bigGraphNodes[5], false);
        bigGraph.addEdge(bigGraphNodes[1], bigGraphNodes[6], false);
        
        bigGraph.addEdge(bigGraphNodes[2], bigGraphNodes[3], false);
        bigGraph.addEdge(bigGraphNodes[2], bigGraphNodes[4], false);
        bigGraph.addEdge(bigGraphNodes[2], bigGraphNodes[5], false);
        bigGraph.addEdge(bigGraphNodes[2], bigGraphNodes[6], false);
        
        bigGraph.addEdge(bigGraphNodes[3], bigGraphNodes[4], false);
        bigGraph.addEdge(bigGraphNodes[3], bigGraphNodes[5], false);
        bigGraph.addEdge(bigGraphNodes[3], bigGraphNodes[6], false);
        
        bigGraph.addEdge(bigGraphNodes[4], bigGraphNodes[5], false);
        bigGraph.addEdge(bigGraphNodes[4], bigGraphNodes[6], false);
        
        bigGraph.addEdge(bigGraphNodes[5], bigGraphNodes[7], false);
        bigGraph.addEdge(bigGraphNodes[5], bigGraphNodes[8], false);
        bigGraph.addEdge(bigGraphNodes[5], bigGraphNodes[9], false);
        bigGraph.addEdge(bigGraphNodes[5], bigGraphNodes[10], false);
        
        bigGraph.addEdge(bigGraphNodes[6], bigGraphNodes[7], false);
        bigGraph.addEdge(bigGraphNodes[6], bigGraphNodes[8], false);
        bigGraph.addEdge(bigGraphNodes[6], bigGraphNodes[9], false);
        bigGraph.addEdge(bigGraphNodes[6], bigGraphNodes[10], false);
        
        bigGraph.addEdge(bigGraphNodes[7], bigGraphNodes[8], false);
        bigGraph.addEdge(bigGraphNodes[7], bigGraphNodes[9], false);
        bigGraph.addEdge(bigGraphNodes[7], bigGraphNodes[10], false);
        
        bigGraph.addEdge(bigGraphNodes[8], bigGraphNodes[9], false);
        bigGraph.addEdge(bigGraphNodes[8], bigGraphNodes[10], false);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void orientEdgelessGraphTest () {
        List<Node> orientation = PermutationTransitiveOrientation.vertexSort(edgelessGraph);
        
        List<Node> referenceOutput = new ArrayList<Node>();
        for(Node node : edgelessGraphNodes) {
            referenceOutput.add(node);
        }
        
        assertEquals(referenceOutput, orientation);
    }
    
    @Test
    public void orientCompleteGraphTest () {
        List<Node> orientation = PermutationTransitiveOrientation.vertexSort(completeGraph);
        
        List<Node> referenceOutput = new ArrayList<Node>();
        for(Node node : completeGraphNodes) {
            referenceOutput.add(node);
        }
        
        assertEquals(referenceOutput, orientation);
    }
    
    @Test
    public void orientTestGraphTest () {
        List<Node> orientation = PermutationTransitiveOrientation.vertexSort(testGraph);
        
        List<Node> referenceOutput = new ArrayList<Node>();
        referenceOutput.add(testGraphNodes[3]);
        referenceOutput.add(testGraphNodes[1]);
        referenceOutput.add(testGraphNodes[0]);
        referenceOutput.add(testGraphNodes[2]);
        referenceOutput.add(testGraphNodes[5]);
        referenceOutput.add(testGraphNodes[6]);
        referenceOutput.add(testGraphNodes[4]);
        referenceOutput.add(testGraphNodes[7]);
        
        assertEquals(referenceOutput, orientation);
        
        PermutationTransitiveOrientation.transitivelyOrientGraph(testGraph);
        for(int i = 0; i < orientation.size(); i++) {
            System.out.println("Node " + i + ": Inneighbors = " + testGraphNodes[i].getInDegree() + ", Outneighbors = " + testGraphNodes[i].getOutDegree());
        }
    }
    
    @Test
    public void orientTestGraph2Test () {
        List<Node> orientation = PermutationTransitiveOrientation.vertexSort(testGraph2);
        
        List<Node> referenceOutput = new ArrayList<Node>();
        referenceOutput.add(testGraph2Nodes[1]);
        referenceOutput.add(testGraph2Nodes[0]);
        referenceOutput.add(testGraph2Nodes[3]);
        referenceOutput.add(testGraph2Nodes[2]);
        
        assertEquals(referenceOutput, orientation);
        
        PermutationTransitiveOrientation.transitivelyOrientGraph(testGraph2);
        System.out.println("------");
        for(int i = 0; i < orientation.size(); i++) {
            System.out.println("Node " + i + ": Inneighbors = " + testGraph2Nodes[i].getInDegree() + ", Outneighbors = " + testGraph2Nodes[i].getOutDegree());
        }
    }
    
    @Test
    public void orientTestGraph3Test () {
        PermutationTransitiveOrientation.transitivelyOrientGraph(testGraph3);
        System.out.println("------");
        for(int i = 0; i < testGraph3Nodes.length; i++) {
            System.out.println("Node " + i + ": Inneighbors = " + testGraph3Nodes[i].getInDegree() + ", Outneighbors = " + testGraph3Nodes[i].getOutDegree());
        }
    }
    
    @Test
    public void orientTestGraph4Test () {
        List<Node> orientation = PermutationTransitiveOrientation.vertexSort(testGraph4);
        
        List<Node> referenceOutput = new ArrayList<Node>();
        referenceOutput.add(testGraph4Nodes[4]);
        referenceOutput.add(testGraph4Nodes[1]);
        referenceOutput.add(testGraph4Nodes[0]);
        referenceOutput.add(testGraph4Nodes[3]);
        referenceOutput.add(testGraph4Nodes[7]);
        referenceOutput.add(testGraph4Nodes[6]);
        referenceOutput.add(testGraph4Nodes[5]);
        referenceOutput.add(testGraph4Nodes[2]);
        
        assertEquals(referenceOutput, orientation);
    }
    
    @Test
    public void calculateModuleOrderTest () throws ModularDecompositionNodeException, RestrictFailedExcpetion {
        ModularDecompositionTree tree = new ModularDecompositionTree(testGraph, testGraph.getNodes());
        
        List<Integer> orientation = PermutationTransitiveOrientation.calculateModulesOrder(tree.getRoot());
        
        List<Integer> referenceOutput = new ArrayList<Integer>();
        referenceOutput.add(4);
        referenceOutput.add(1);
        referenceOutput.add(0);
        referenceOutput.add(3);
        referenceOutput.add(7);
        referenceOutput.add(6);
        referenceOutput.add(5);
        referenceOutput.add(2);
        
        assertEquals(referenceOutput, orientation);
    }
    
    @Test
    public void orientBigGraphTest () {
        PermutationTransitiveOrientation.transitivelyOrientGraph(bigGraph);
        System.out.println("------");
        for(int i = 0; i < bigGraphNodes.length; i++) {
            System.out.println("Node " + i + ": Inneighbors = " + bigGraphNodes[i].getInDegree() + ", Outneighbors = " + bigGraphNodes[i].getOutDegree());
        }
    }
}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
