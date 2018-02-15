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
public class ModularDecompositionTreeRestrictTest {

	private Graph emptyGraph;
	private Graph singletonGraph;
	private Graph singleNodeGraph;
	private Graph bigGraph;
	private ModularDecompositionTree emptyTree;
	private ModularDecompositionTree emptyTree2;
	private ModularDecompositionTree singletonTree;
	private ModularDecompositionTree singletonTree2;
	private Node[] bigGraphNodes;
	
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// construct testgraph
		emptyGraph = new AdjListGraph();
		
		singletonGraph = new AdjListGraph();
		singletonGraph.addNode();
		
		emptyTree = new ModularDecompositionTree(emptyGraph, emptyGraph.getNodes());
		emptyTree2 = new ModularDecompositionTree(emptyGraph, emptyGraph.getNodes());
		
		singleNodeGraph = new AdjListGraph();
		Node node1 = singleNodeGraph.addNode();
		Node node2 = singleNodeGraph.addNode();
		
		ModularDecompositionNode mdNode1 = new ModularDecompositionNode(node1, null);
		ModularDecompositionNode mdNode2 = new ModularDecompositionNode(node2, null);
		List<Node> node1List = new ArrayList<Node>();
		node1List.add(node1);
		List<Node> node2List = new ArrayList<Node>();
		node2List.add(node2);
		singletonTree = new ModularDecompositionTree(singleNodeGraph, node1List, mdNode1);
		singletonTree2 = new ModularDecompositionTree(singleNodeGraph, node2List, mdNode2);
		
		bigGraph = new AdjListGraph();
		bigGraphNodes = new Node[11];
		for(int i = 0; i < 11; i++) {
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
	public void testConstructorWithEmptyNodeList() throws ModularDecompositionNodeException, RestrictFailedExcpetion {
		List<Node> nodes = emptyGraph.getNodes();
		ModularDecompositionTree testTree = new ModularDecompositionTree(emptyGraph, nodes);
		
		assertNotNull(testTree);
	}
	
	@Test
	public void testConstructorWithOneNode() throws ModularDecompositionNodeException, RestrictFailedExcpetion {
		List<Node> nodes = singletonGraph.getNodes();
		ModularDecompositionTree testTree = new ModularDecompositionTree(singletonGraph, nodes);
		
		assertEquals(1, testTree.getNodeSet().size());
		assertEquals(nodes.get(0), testTree.getRoot().getNodes().get(0));
	}
	
	@Test
	public void testRestrictEmptyTrees() throws RestrictFailedExcpetion {
		List<ModularDecompositionNode> twoEmptyTrees = ModularDecompositionTree.restrict(emptyTree, emptyTree2);
		assertEquals(0, twoEmptyTrees.size());
	}
	
	@Test
	public void testRestrictOneEmptyOneSingletonTree() throws RestrictFailedExcpetion {
		List<ModularDecompositionNode> oneEmptyAndOneSingletonTrees = ModularDecompositionTree.restrict(singletonTree, emptyTree);
		assertEquals(oneEmptyAndOneSingletonTrees.get(0), singletonTree.getRoot());
		
		List<ModularDecompositionNode> anotherEmptyAndSingletonTree = ModularDecompositionTree.restrict(emptyTree, singletonTree);
		assertEquals(anotherEmptyAndSingletonTree.get(0), singletonTree.getRoot());
	}
	
	@Test
	public void testRestrictTwoSingletonTrees () throws RestrictFailedExcpetion {
		List<ModularDecompositionNode> twoSingletonTrees = ModularDecompositionTree.restrict(singletonTree, singletonTree2);
		assertTrue("Resulting forest does not contain the singleton tree 1", 
				twoSingletonTrees.contains(singletonTree.getRoot()));
		assertTrue("Resulting forest does not contain the singleton tree 2", 
				twoSingletonTrees.contains(singletonTree2.getRoot()));
	}
	
	@Test
	public void testRestrictOne () throws ModularDecompositionNodeException, RestrictFailedExcpetion {
		// Referencing step III from the Wikipedia Dalhaus example
		ModularDecompositionNode eight = new ModularDecompositionNode(bigGraphNodes[7], null);
		ModularDecompositionNode nine = new ModularDecompositionNode(bigGraphNodes[8], null);
		List<Node> eightAndNine = new ArrayList<Node>();
		eightAndNine.add(bigGraphNodes[7]);
		eightAndNine.add(bigGraphNodes[8]);
		ModularDecompositionNode moduleEightAndNine = new ModularDecompositionNode(eightAndNine, null);
		moduleEightAndNine.addChild(eight);
		moduleEightAndNine.addChild(nine);
		
		ModularDecompositionNode one = new ModularDecompositionNode(bigGraphNodes[0], null);
		ModularDecompositionNode eleven = new ModularDecompositionNode(bigGraphNodes[10], null);
		List<Node> oneAndEleven = new ArrayList<Node>();
		oneAndEleven.add(bigGraphNodes[0]);
		oneAndEleven.add(bigGraphNodes[10]);
		ModularDecompositionNode moduleOneAndEleven = new ModularDecompositionNode(oneAndEleven, null);
		moduleOneAndEleven.addChild(one);
		moduleOneAndEleven.addChild(eleven);
		
		ModularDecompositionTree treeEightAndNine = new ModularDecompositionTree(bigGraph, eightAndNine, moduleEightAndNine);
		ModularDecompositionTree treeOneAndEleven = new ModularDecompositionTree(bigGraph, oneAndEleven, moduleOneAndEleven);
		
		List<ModularDecompositionNode> restrictTestOne = ModularDecompositionTree.restrict(treeEightAndNine, treeOneAndEleven);
		
		assertTrue(restrictTestOne.contains(moduleEightAndNine));
		assertTrue(restrictTestOne.contains(one));
		assertTrue(restrictTestOne.contains(eleven));
	}
	
	@Test
	public void restrictTestTwo () throws ModularDecompositionNodeException, RestrictFailedExcpetion {
		ModularDecompositionNode four = new ModularDecompositionNode(bigGraphNodes[3], null);
		ModularDecompositionNode six = new ModularDecompositionNode(bigGraphNodes[5], null);
		ModularDecompositionNode seven = new ModularDecompositionNode(bigGraphNodes[6], null);
		
		List<Node> sixAndSeven = new ArrayList<Node>();
		sixAndSeven.add(bigGraphNodes[5]);
		sixAndSeven.add(bigGraphNodes[6]);
		
		List<Node> fourSixAndSeven = new ArrayList<Node>();
		fourSixAndSeven.add(bigGraphNodes[3]);
		fourSixAndSeven.add(bigGraphNodes[5]);
		fourSixAndSeven.add(bigGraphNodes[6]);
		
		ModularDecompositionNode moduleSixAndSeven = new ModularDecompositionNode(sixAndSeven, null);
		ModularDecompositionNode moduleFourSixAndSeven = new ModularDecompositionNode(fourSixAndSeven, null);
		
		moduleSixAndSeven.addChild(six);
		moduleSixAndSeven.addChild(seven);
		
		moduleFourSixAndSeven.addChild(four);
		moduleFourSixAndSeven.addChild(moduleSixAndSeven);
		
		List<Node> listTwo = new ArrayList<Node>();
		listTwo.add(bigGraphNodes[1]);
		ModularDecompositionNode two = new ModularDecompositionNode(bigGraphNodes[1], null);
		
		ModularDecompositionTree treeFourSixAndSeven = new ModularDecompositionTree(bigGraph, fourSixAndSeven, moduleFourSixAndSeven);
		ModularDecompositionTree treeTwo = new ModularDecompositionTree(bigGraph, listTwo, two);
		
		List<ModularDecompositionNode> restrictTestTwo = ModularDecompositionTree.restrict(treeFourSixAndSeven, treeTwo);
		
		assertTrue(restrictTestTwo.contains(moduleFourSixAndSeven));
		assertTrue(restrictTestTwo.contains(two));
	}

}
