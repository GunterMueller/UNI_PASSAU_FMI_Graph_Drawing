/**
 * 
 */
package tests.graffiti.plugins.algorithms.permutationgraph;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.permutationgraph.ModularDecompositionNode;
import org.graffiti.plugins.algorithms.permutationgraph.ModularDecompositionNodeException;
import org.graffiti.plugins.algorithms.permutationgraph.ModularDecompositionTree;
import org.graffiti.plugins.algorithms.permutationgraph.RestrictFailedExcpetion;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Emanuel Berndl
 *
 */
public class ModularDecompositionTreeTest {

	private Graph bigGraph;
	private Node[] bigGraphNodes;
	
	private Graph pathWithThreeNodes;
	private Node[] pathWithThreeNodesNodes;
	
	private Graph twoConnectedNodes;
	private Node[] twoConnectedNodesNodes;
	
	private Graph twoUnconnectedNodes;
	private Node[] twoUnconnectedNodesNodes;
	
	private Graph midGraphOne;
	private Node[] midGraphOneNodes;
	
	private Graph testGraph;
	private Node[] testGraphNodes;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	    twoConnectedNodes = new AdjListGraph();
	    twoConnectedNodesNodes = new Node[2];
	    twoConnectedNodesNodes[0] = twoConnectedNodes.addNode();
	    twoConnectedNodesNodes[1] = twoConnectedNodes.addNode();
	    
	    twoConnectedNodes.addEdge(twoConnectedNodesNodes[0], twoConnectedNodesNodes[1], false);
	    
	    twoUnconnectedNodes = new AdjListGraph();
	    twoUnconnectedNodesNodes = new Node[2];
	    twoUnconnectedNodesNodes[0] = twoUnconnectedNodes.addNode();
	    twoUnconnectedNodesNodes[1] = twoUnconnectedNodes.addNode();
	    
	    pathWithThreeNodes = new AdjListGraph();
	    pathWithThreeNodesNodes = new Node[3];
	    for(int i = 0; i < 3; i++) {
	        pathWithThreeNodesNodes[i] = pathWithThreeNodes.addNode();
	    }
	    
	    pathWithThreeNodes.addEdge(pathWithThreeNodesNodes[0], pathWithThreeNodesNodes[1], false);
	    pathWithThreeNodes.addEdge(pathWithThreeNodesNodes[1], pathWithThreeNodesNodes[2], false);
	    
	    midGraphOne = new AdjListGraph();
	    midGraphOneNodes = new Node[5];
	    for(int i = 0; i < 5; i++) {
	        midGraphOneNodes[i] = midGraphOne.addNode();
	    }
	    
	    midGraphOne.addEdge(midGraphOneNodes[1], midGraphOneNodes[2], false);
	    midGraphOne.addEdge(midGraphOneNodes[1], midGraphOneNodes[3], false);
	    midGraphOne.addEdge(midGraphOneNodes[1], midGraphOneNodes[4], false);
	    midGraphOne.addEdge(midGraphOneNodes[2], midGraphOneNodes[4], false);
	    midGraphOne.addEdge(midGraphOneNodes[2], midGraphOneNodes[3], false);
	    
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
	public void ModularDecompositionTwoConnectedNodesTest () throws ModularDecompositionNodeException, RestrictFailedExcpetion {
	    List<Node> graphNodes = new ArrayList<Node>();
            for(int i = 0; i < twoConnectedNodesNodes.length; i++) {
                graphNodes.add(twoConnectedNodesNodes[i]);
            }
            ModularDecompositionTree tree = new ModularDecompositionTree(twoConnectedNodes, graphNodes);
            
            List<Node> eightAndNine = new ArrayList<Node>();
            eightAndNine.add(twoConnectedNodesNodes[0]);
            eightAndNine.add(twoConnectedNodesNodes[1]);
            ModularDecompositionNode moduleEightAndNine = new ModularDecompositionNode(eightAndNine, null);
            moduleEightAndNine.setType(ModularDecompositionNode.Type.SERIES);
            
            List<Node> eight = new ArrayList<Node>();
            eight.add(twoConnectedNodesNodes[0]);
            ModularDecompositionNode moduleEight = new ModularDecompositionNode(eight, null);
            moduleEight.setType(ModularDecompositionNode.Type.PRIME);
            moduleEightAndNine.addChild(moduleEight);
            
            List<Node> nine = new ArrayList<Node>();
            nine.add(twoConnectedNodesNodes[1]);
            ModularDecompositionNode moduleNine = new ModularDecompositionNode(nine, null);
            moduleNine.setType(ModularDecompositionNode.Type.PRIME);
            moduleEightAndNine.addChild(moduleNine);
            
            assertEquals(moduleEightAndNine, tree.getRoot());
	}
	
	@Test
	public void modularDecompositionTwoUnconnectedNodesTest () throws ModularDecompositionNodeException, RestrictFailedExcpetion {
	    List<Node> graphNodes = new ArrayList<Node>();
            for(int i = 0; i < twoUnconnectedNodesNodes.length; i++) {
                graphNodes.add(twoUnconnectedNodesNodes[i]);
            }
            ModularDecompositionTree tree = new ModularDecompositionTree(twoUnconnectedNodes, graphNodes);
            
            List<Node> oneAndEleven = new ArrayList<Node>();
            oneAndEleven.add(twoUnconnectedNodesNodes[0]);
            oneAndEleven.add(twoUnconnectedNodesNodes[1]);
            ModularDecompositionNode moduleOneAndEleven = new ModularDecompositionNode(oneAndEleven, null);
            moduleOneAndEleven.setType(ModularDecompositionNode.Type.PARALLEL);
            
            List<Node> one = new ArrayList<Node>();
            one.add(twoUnconnectedNodesNodes[0]);
            ModularDecompositionNode moduleOne = new ModularDecompositionNode(one, null);
            moduleOne.setType(ModularDecompositionNode.Type.PRIME);
            moduleOneAndEleven.addChild(moduleOne);
            
            List<Node> eleven = new ArrayList<Node>();
            eleven.add(twoUnconnectedNodesNodes[1]);
            ModularDecompositionNode moduleEleven = new ModularDecompositionNode(eleven, null);
            moduleEleven.setType(ModularDecompositionNode.Type.PRIME);
            moduleOneAndEleven.addChild(moduleEleven);
            
            assertEquals(moduleOneAndEleven, tree.getRoot());
	}
	
	@Test
	public void modularDecompositionMidTreeOneTest () throws ModularDecompositionNodeException, RestrictFailedExcpetion {
	    List<Node> graphNodes = new ArrayList<Node>();
            for(int i = 0; i < midGraphOneNodes.length; i++) {
                graphNodes.add(midGraphOneNodes[i]);
            }
            ModularDecompositionTree tree = new ModularDecompositionTree(midGraphOne, graphNodes);
            
            List<Node> allNodes = new ArrayList<Node>();
            for(int i = 0; i < 5; i++) {
                allNodes.add(midGraphOneNodes[i]);
            }
            ModularDecompositionNode allNodeModule = new ModularDecompositionNode(allNodes, null);
            allNodeModule.setType(ModularDecompositionNode.Type.PARALLEL);
            
            List<Node> one = new ArrayList<Node>();
            one.add(midGraphOneNodes[0]);
            ModularDecompositionNode moduleOne = new ModularDecompositionNode(one, null);
            moduleOne.setType(ModularDecompositionNode.Type.PRIME);
            allNodeModule.addChild(moduleOne);
            
            List<Node> eightNineTenAndEleven = new ArrayList<Node>();
            for(int i = 1; i < 5; i++) {
                eightNineTenAndEleven.add(midGraphOneNodes[i]);
            }
            ModularDecompositionNode moduleEightNineTenAndEleven = new ModularDecompositionNode(eightNineTenAndEleven, null);
            moduleEightNineTenAndEleven.setType(ModularDecompositionNode.Type.SERIES);
            allNodeModule.addChild(moduleEightNineTenAndEleven);
            
            List<Node> eight = new ArrayList<Node>();
            eight.add(midGraphOneNodes[1]);
            ModularDecompositionNode moduleEight = new ModularDecompositionNode(eight, null);
            moduleEight.setType(ModularDecompositionNode.Type.PRIME);
            moduleEightNineTenAndEleven.addChild(moduleEight);
            
            List<Node> nine = new ArrayList<Node>();
            nine.add(midGraphOneNodes[2]);
            ModularDecompositionNode moduleNine = new ModularDecompositionNode(nine, null);
            moduleNine.setType(ModularDecompositionNode.Type.PRIME);
            moduleEightNineTenAndEleven.addChild(moduleNine);
            
            List<Node> tenAndEleven = new ArrayList<Node>();
            tenAndEleven.add(midGraphOneNodes[3]);
            tenAndEleven.add(midGraphOneNodes[4]);
            ModularDecompositionNode moduleTenAndEleven = new ModularDecompositionNode(tenAndEleven, null);
            moduleTenAndEleven.setType(ModularDecompositionNode.Type.PARALLEL);
            moduleEightNineTenAndEleven.addChild(moduleTenAndEleven);
            
            List<Node> ten = new ArrayList<Node>();
            ten.add(midGraphOneNodes[3]);
            ModularDecompositionNode moduleTen = new ModularDecompositionNode(ten, null);
            moduleTen.setType(ModularDecompositionNode.Type.PRIME);
            moduleTenAndEleven.addChild(moduleTen);
            
            List<Node> eleven = new ArrayList<Node>();
            eleven.add(midGraphOneNodes[4]);
            ModularDecompositionNode moduleEleven = new ModularDecompositionNode(eleven, null);
            moduleEleven.setType(ModularDecompositionNode.Type.PRIME);
            moduleTenAndEleven.addChild(moduleEleven);
            
            assertEquals(allNodeModule, tree.getRoot());
            
            
	}
	
	@Test
	public void modularDecompositionTreeBigGraph () throws ModularDecompositionNodeException, RestrictFailedExcpetion {
	    List<Node> graphNodes = new ArrayList<Node>();
	    for(int i = 0; i < bigGraphNodes.length; i++) {
	        graphNodes.add(bigGraphNodes[i]);
	    }
	    ModularDecompositionTree tree = new ModularDecompositionTree(bigGraph, graphNodes);
	    
	    List<Node> allNodes = new ArrayList<Node>();
	    for(int i = 0; i < bigGraphNodes.length; i++) {
	        allNodes.add(bigGraphNodes[i]);
	    }
	    ModularDecompositionNode allNodeModule = new ModularDecompositionNode (allNodes, null);
	    allNodeModule.setType(ModularDecompositionNode.Type.PRIME);
	    
	    List<Node> one = new ArrayList<Node>();
	    one.add(bigGraphNodes[0]);
	    ModularDecompositionNode moduleOne = new ModularDecompositionNode(one, null);
	    moduleOne.setType(ModularDecompositionNode.Type.PRIME);
	    allNodeModule.addChild(moduleOne);
	    
	    List<Node> twoThreeAndFour = new ArrayList<Node>();
	    twoThreeAndFour.add(bigGraphNodes[1]);
	    twoThreeAndFour.add(bigGraphNodes[2]);
	    twoThreeAndFour.add(bigGraphNodes[3]);
	    ModularDecompositionNode moduleTwoThreeAndFour = new ModularDecompositionNode(twoThreeAndFour, null);
	    moduleTwoThreeAndFour.setType(ModularDecompositionNode.Type.SERIES);
	    allNodeModule.addChild(moduleTwoThreeAndFour);
	    
	    List<Node> twoAndThree = new ArrayList<Node>();
	    twoAndThree.add(bigGraphNodes[1]);
	    twoAndThree.add(bigGraphNodes[2]);
	    ModularDecompositionNode moduleTwoAndThree = new ModularDecompositionNode(twoAndThree, null);
	    moduleTwoAndThree.setType(ModularDecompositionNode.Type.PARALLEL);
	    moduleTwoThreeAndFour.addChild(moduleTwoAndThree);
	    
	    List<Node> two = new ArrayList<Node>();
	    two.add(bigGraphNodes[1]);
	    ModularDecompositionNode moduleTwo = new ModularDecompositionNode(two, null);
	    moduleTwo.setType(ModularDecompositionNode.Type.PRIME);
	    moduleTwoAndThree.addChild(moduleTwo);
	    
	    List<Node> three = new ArrayList<Node>();
            three.add(bigGraphNodes[2]);
            ModularDecompositionNode moduleThree = new ModularDecompositionNode(three, null);
            moduleThree.setType(ModularDecompositionNode.Type.PRIME);
            moduleTwoAndThree.addChild(moduleThree);
            
            List<Node> four = new ArrayList<Node>();
            four.add(bigGraphNodes[3]);
            ModularDecompositionNode moduleFour = new ModularDecompositionNode(four, null);
            moduleFour.setType(ModularDecompositionNode.Type.PRIME);
            moduleTwoThreeAndFour.addChild(moduleFour);
            
            List<Node> five = new ArrayList<Node>();
            five.add(bigGraphNodes[4]);
            ModularDecompositionNode moduleFive = new ModularDecompositionNode(five, null);
            moduleFive.setType(ModularDecompositionNode.Type.PRIME);
            allNodeModule.addChild(moduleFive);
            
            List<Node> sixAndSeven = new ArrayList<Node>();
            sixAndSeven.add(bigGraphNodes[5]);
            sixAndSeven.add(bigGraphNodes[6]);
            ModularDecompositionNode moduleSixAndSeven = new ModularDecompositionNode(sixAndSeven, null);
            moduleSixAndSeven.setType(ModularDecompositionNode.Type.PARALLEL);
            allNodeModule.addChild(moduleSixAndSeven);
            
            List<Node> six = new ArrayList<Node>();
            six.add(bigGraphNodes[5]);
            ModularDecompositionNode moduleSix = new ModularDecompositionNode(six, null);
            moduleSix.setType(ModularDecompositionNode.Type.PRIME);
            moduleSixAndSeven.addChild(moduleSix);
            
            List<Node> seven = new ArrayList<Node>();
            seven.add(bigGraphNodes[6]);
            ModularDecompositionNode moduleSeven = new ModularDecompositionNode(seven, null);
            moduleSeven.setType(ModularDecompositionNode.Type.PRIME);
            moduleSixAndSeven.addChild(moduleSeven);
            
            List<Node> eightNineTenAndEleven = new ArrayList<Node>();
            eightNineTenAndEleven.add(bigGraphNodes[7]);
            eightNineTenAndEleven.add(bigGraphNodes[8]);
            eightNineTenAndEleven.add(bigGraphNodes[9]);
            eightNineTenAndEleven.add(bigGraphNodes[10]);
            ModularDecompositionNode moduleEightNineTenAndEleven = new ModularDecompositionNode(eightNineTenAndEleven, null);
            moduleEightNineTenAndEleven.setType(ModularDecompositionNode.Type.SERIES);
            allNodeModule.addChild(moduleEightNineTenAndEleven);
            
            List<Node> eight = new ArrayList<Node>();
            eight.add(bigGraphNodes[7]);
            ModularDecompositionNode moduleEight = new ModularDecompositionNode(eight, null);
            moduleEight.setType(ModularDecompositionNode.Type.PRIME);
            moduleEightNineTenAndEleven.addChild(moduleEight);
            
            List<Node> nine = new ArrayList<Node>();
            nine.add(bigGraphNodes[8]);
            ModularDecompositionNode moduleNine = new ModularDecompositionNode(nine, null);
            moduleNine.setType(ModularDecompositionNode.Type.PRIME);
            moduleEightNineTenAndEleven.addChild(moduleNine);
            
            List<Node> tenAndEleven = new ArrayList<Node>();
            tenAndEleven.add(bigGraphNodes[9]);
            tenAndEleven.add(bigGraphNodes[10]);
            ModularDecompositionNode moduleTenAndEleven = new ModularDecompositionNode(tenAndEleven, null);
            moduleTenAndEleven.setType(ModularDecompositionNode.Type.PARALLEL);
            moduleEightNineTenAndEleven.addChild(moduleTenAndEleven);
            
            List<Node> ten = new ArrayList<Node>();
            ten.add(bigGraphNodes[9]);
            ModularDecompositionNode moduleTen = new ModularDecompositionNode(ten, null);
            moduleTen.setType(ModularDecompositionNode.Type.PRIME);
            moduleTenAndEleven.addChild(moduleTen);
            
            List<Node> eleven = new ArrayList<Node>();
            eleven.add(bigGraphNodes[10]);
            ModularDecompositionNode moduleEleven = new ModularDecompositionNode(eleven, null);
            moduleEleven.setType(ModularDecompositionNode.Type.PRIME);
            moduleTenAndEleven.addChild(moduleEleven);
            
            assertEquals(allNodeModule, tree.getRoot());
            
            
	}
	
	@Test
	public void testGraph () throws ModularDecompositionNodeException, RestrictFailedExcpetion {
	    List<Node> graphNodes = new ArrayList<Node>();
            for(int i = 0; i < testGraphNodes.length; i++) {
                graphNodes.add(testGraphNodes[i]);
            }
            ModularDecompositionTree tree = new ModularDecompositionTree(testGraph, graphNodes);
            
            List<Node> allNodes = new ArrayList<Node>();
            allNodes.add(testGraphNodes[0]);
            allNodes.add(testGraphNodes[1]);
            allNodes.add(testGraphNodes[2]);
            allNodes.add(testGraphNodes[3]);
            allNodes.add(testGraphNodes[4]);
            ModularDecompositionNode allNodeModule = new ModularDecompositionNode(allNodes, null);
            allNodeModule.setType(ModularDecompositionNode.Type.PRIME);
            
            List<Node> one = new ArrayList<Node>();
            one.add(testGraphNodes[0]);
            ModularDecompositionNode moduleOne = new ModularDecompositionNode(one, null);
            moduleOne.setType(ModularDecompositionNode.Type.PRIME);
            allNodeModule.addChild(moduleOne);
            
            List<Node> two = new ArrayList<Node>();
            two.add(testGraphNodes[1]);
            ModularDecompositionNode moduleTwo = new ModularDecompositionNode(two, null);
            moduleTwo.setType(ModularDecompositionNode.Type.PRIME);
            allNodeModule.addChild(moduleTwo);
            
            List<Node> three = new ArrayList<Node>();
            three.add(testGraphNodes[2]);
            ModularDecompositionNode moduleThree = new ModularDecompositionNode(three, null);
            moduleThree.setType(ModularDecompositionNode.Type.PRIME);
            allNodeModule.addChild(moduleThree);
            
            List<Node> fourAndFive = new ArrayList<Node>();
            fourAndFive.add(testGraphNodes[3]);
            fourAndFive.add(testGraphNodes[4]);
            ModularDecompositionNode moduleFourAndFive = new ModularDecompositionNode(fourAndFive, null);
            moduleFourAndFive.setType(ModularDecompositionNode.Type.SERIES);
            allNodeModule.addChild(moduleFourAndFive);
            
            List<Node> four = new ArrayList<Node>();
            four.add(testGraphNodes[3]);
            ModularDecompositionNode moduleFour = new ModularDecompositionNode(four, null);
            moduleFour.setType(ModularDecompositionNode.Type.PRIME);
            moduleFourAndFive.addChild(moduleFour);
            
            List<Node> five = new ArrayList<Node>();
            five.add(testGraphNodes[4]);
            ModularDecompositionNode moduleFive = new ModularDecompositionNode(five, null);
            moduleFive.setType(ModularDecompositionNode.Type.PRIME);
            moduleFourAndFive.addChild(moduleFive);
            
            assertEquals(allNodeModule, tree.getRoot());
	}

}
