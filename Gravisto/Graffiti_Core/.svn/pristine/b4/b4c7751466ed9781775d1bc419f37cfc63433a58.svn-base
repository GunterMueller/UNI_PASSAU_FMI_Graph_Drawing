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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Emanuel Berndl
 * 
 */
public class ModularDecompositionTreeV0ModuleTest {

	private Graph bigGraph;
	private Node[] bigGraphNodes;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
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
	public void testV0ModulesOneSingletonOneEmptySet()
			throws ModularDecompositionNodeException {
		List<Node> neighbors = new ArrayList<Node>();
		neighbors.add(bigGraphNodes[8]);

		List<Node> nonNeighbors = new ArrayList<Node>();

		Node pivot = bigGraphNodes[7];

		ModularDecompositionNode moduleNine = new ModularDecompositionNode(neighbors, null);
		List<ModularDecompositionNode> restrictForest = new ArrayList<ModularDecompositionNode>();
		restrictForest.add(moduleNine);

		List<ModularDecompositionNode> v0path = ModularDecompositionTree
				.v0modules(neighbors, nonNeighbors, pivot, restrictForest);

		// Reference output should be a list containing two elements. The first
		// one is the MDnode containing both nodes, the second element
		// represents the module consisting of the pivot
		List<ModularDecompositionNode> refrenceOutput = new ArrayList<ModularDecompositionNode>();
		List<Node> eightAndNine = new ArrayList<Node>();
		eightAndNine.add(bigGraphNodes[7]);
		eightAndNine.add(bigGraphNodes[8]);
		ModularDecompositionNode moduleEightAndNine = new ModularDecompositionNode(
				eightAndNine, null);

		List<Node> eight = new ArrayList<Node>();
		eight.add(bigGraphNodes[7]);
		ModularDecompositionNode moduleEight = new ModularDecompositionNode(
				eight, null);

		refrenceOutput.add(moduleEightAndNine);
		refrenceOutput.add(moduleEight);
		refrenceOutput.get(0).addChild(refrenceOutput.get(1));

		assertEquals(refrenceOutput, v0path);
	}

	@Test
	public void testV0ModulesAnotherSingletonAndEmptySet() throws ModularDecompositionNodeException {
		List<Node> neighbors = new ArrayList<Node>();

		List<Node> nonNeighbors = new ArrayList<Node>();
		nonNeighbors.add(bigGraphNodes[0]);

		Node pivot = bigGraphNodes[10];
		
		ModularDecompositionNode moduleOne = new ModularDecompositionNode(nonNeighbors, null);
		List<ModularDecompositionNode> restrictForest = new ArrayList<ModularDecompositionNode>();
		restrictForest.add(moduleOne);
		
		List<ModularDecompositionNode> v0path = ModularDecompositionTree
		.v0modules(neighbors, nonNeighbors, pivot, restrictForest);
		
		// Reference output should be a list containing two elements. The first
		// one is the MDnode containing both nodes, the second element
		// represents the module consisting of the pivot
		List<ModularDecompositionNode> refrenceOutput = new ArrayList<ModularDecompositionNode>();
		List<Node> oneAndEleven = new ArrayList<Node>();
		oneAndEleven.add(bigGraphNodes[0]);
		oneAndEleven.add(bigGraphNodes[10]);
		ModularDecompositionNode moduleOneAndEleven = new ModularDecompositionNode(
				oneAndEleven, null);

		List<Node> one = new ArrayList<Node>();
		one.add(bigGraphNodes[10]);
		ModularDecompositionNode moduleEleven2 = new ModularDecompositionNode(
				one, null);

		refrenceOutput.add(moduleOneAndEleven);
		refrenceOutput.add(moduleEleven2);
		refrenceOutput.get(0).addChild(refrenceOutput.get(1));

		assertEquals(refrenceOutput, v0path);
	}
	
	@Test
	public void testV0ModulesWithTwoSingletonSets () throws ModularDecompositionNodeException {
		List<Node> neighbors = new ArrayList<Node>();
		neighbors.add(bigGraphNodes[3]);
		
		List<Node> nonNeighbors = new ArrayList<Node>();
		nonNeighbors.add(bigGraphNodes[6]);
		
		Node pivot = bigGraphNodes[5];
		
		ModularDecompositionNode moduleFour = new ModularDecompositionNode(neighbors, null);
		ModularDecompositionNode moduleSeven = new ModularDecompositionNode(nonNeighbors, null);
		List<ModularDecompositionNode> restrictForest = new ArrayList<ModularDecompositionNode>();
		restrictForest.add(moduleFour);
		restrictForest.add(moduleSeven);
		
		List<ModularDecompositionNode> v0path = ModularDecompositionTree.v0modules(neighbors, nonNeighbors, pivot, restrictForest);
		
		List<ModularDecompositionNode> referenceOutput = new ArrayList<ModularDecompositionNode>();
		List<Node> fourSixAndSeven = new ArrayList<Node>();
		fourSixAndSeven.add(bigGraphNodes[3]);
		fourSixAndSeven.add(bigGraphNodes[5]);
		fourSixAndSeven.add(bigGraphNodes[6]);
		ModularDecompositionNode moduleFourSixAndSeven = new ModularDecompositionNode(fourSixAndSeven, null);
		
		List<Node> sixAndSeven = new ArrayList<Node>();
		sixAndSeven.add(bigGraphNodes[5]);
		sixAndSeven.add(bigGraphNodes[6]);
		ModularDecompositionNode moduleSixAndSeven = new ModularDecompositionNode(sixAndSeven, null);
		
		List<Node> six = new ArrayList<Node>();
		six.add(bigGraphNodes[5]);
		ModularDecompositionNode moduleSix = new ModularDecompositionNode(six, null);
		
		referenceOutput.add(moduleFourSixAndSeven);
		referenceOutput.add(moduleSixAndSeven);
		referenceOutput.add(moduleSix);
		for(int i = 0; i < referenceOutput.size() - 1; i++) {
	            referenceOutput.get(i).addChild(referenceOutput.get(i + 1));
	        }
		
		assertEquals(referenceOutput, v0path);
	}
	
	@Test
	public void testV0ModulesWithTwoBiggerSets () throws ModularDecompositionNodeException {
		List<Node> neighbors = new ArrayList<Node>();
		neighbors.add(bigGraphNodes[7]);
		neighbors.add(bigGraphNodes[8]);
		
		List<Node> nonNeighbors = new ArrayList<Node>();
		nonNeighbors.add(bigGraphNodes[0]);
		nonNeighbors.add(bigGraphNodes[10]);
		
		Node pivot = bigGraphNodes[9];
		
		List<Node> eight = new ArrayList<Node>();
		eight.add(bigGraphNodes[7]);
		ModularDecompositionNode moduleEight = new ModularDecompositionNode(eight, null);
		
		List<Node> nine = new ArrayList<Node>();
                nine.add(bigGraphNodes[7]);
                ModularDecompositionNode moduleNine = new ModularDecompositionNode(nine, null);
                
                ModularDecompositionNode moduleEightAndNine = new ModularDecompositionNode(neighbors, null);
                moduleEightAndNine.addChild(moduleEight);
                moduleEightAndNine.addChild(moduleNine);
                
                List<Node> eleven = new ArrayList<Node>();
                eleven.add(bigGraphNodes[7]);
                ModularDecompositionNode moduleEleven = new ModularDecompositionNode(eleven, null);
                
                List<Node> one = new ArrayList<Node>();
                one.add(bigGraphNodes[7]);
                ModularDecompositionNode moduleOne = new ModularDecompositionNode(one, null);
                
                List<ModularDecompositionNode> restrictForest = new ArrayList<ModularDecompositionNode>();
                restrictForest.add(moduleEightAndNine);
                restrictForest.add(moduleOne);
                restrictForest.add(moduleEleven);
		
		List<ModularDecompositionNode> v0path = ModularDecompositionTree.v0modules(neighbors, nonNeighbors, pivot, restrictForest);
		
		List<ModularDecompositionNode> referenceOutput = new ArrayList<ModularDecompositionNode>();
		List<Node> oneEightNineTenAndEleven = new ArrayList<Node>();
		oneEightNineTenAndEleven.add(bigGraphNodes[0]);
		oneEightNineTenAndEleven.add(bigGraphNodes[7]);
		oneEightNineTenAndEleven.add(bigGraphNodes[8]);
		oneEightNineTenAndEleven.add(bigGraphNodes[9]);
		oneEightNineTenAndEleven.add(bigGraphNodes[10]);
		ModularDecompositionNode moduleOneEightNineTenAndEleven = new ModularDecompositionNode(oneEightNineTenAndEleven, null);
		
		List<Node> eightNineTenAndEleven = new ArrayList<Node>();
		eightNineTenAndEleven.add(bigGraphNodes[7]);
		eightNineTenAndEleven.add(bigGraphNodes[8]);
		eightNineTenAndEleven.add(bigGraphNodes[9]);
		eightNineTenAndEleven.add(bigGraphNodes[10]);
		ModularDecompositionNode moduleEightNineTenAndEleven = new ModularDecompositionNode(eightNineTenAndEleven, null);
		
		List<Node> tenAndEleven = new ArrayList<Node>();
		tenAndEleven.add(bigGraphNodes[9]);
		tenAndEleven.add(bigGraphNodes[10]);
		ModularDecompositionNode moduleTenAndEleven = new ModularDecompositionNode(tenAndEleven, null);
		
		List<Node> ten = new ArrayList<Node>();
		ten.add(bigGraphNodes[9]);
		ModularDecompositionNode moduleTen = new ModularDecompositionNode(ten, null);
		
		referenceOutput.add(moduleOneEightNineTenAndEleven);
		referenceOutput.add(moduleEightNineTenAndEleven);
		referenceOutput.add(moduleTenAndEleven);
		referenceOutput.add(moduleTen);
		for(int i = 0; i < referenceOutput.size() - 1; i++) {
                    referenceOutput.get(i).addChild(referenceOutput.get(i + 1));
                }
		
		assertEquals(referenceOutput, v0path);
	}
	
	@Test
	public void testV0ModulesWithCompleteBigGraph () throws ModularDecompositionNodeException {
		List<Node> neighbors = new ArrayList<Node>();
		neighbors.add(bigGraphNodes[1]);
		neighbors.add(bigGraphNodes[2]);
		neighbors.add(bigGraphNodes[3]);
		neighbors.add(bigGraphNodes[5]);
		neighbors.add(bigGraphNodes[6]);
		
		List<Node> nonNeighbors = new ArrayList<Node>();
		nonNeighbors.add(bigGraphNodes[0]);
		nonNeighbors.add(bigGraphNodes[7]);
		nonNeighbors.add(bigGraphNodes[8]);
		nonNeighbors.add(bigGraphNodes[9]);
		nonNeighbors.add(bigGraphNodes[10]);
		
		Node pivot = bigGraphNodes[4];
		
		List<Node> twoThreeAndFour = new ArrayList<Node>();
		twoThreeAndFour.add(bigGraphNodes[1]);
		twoThreeAndFour.add(bigGraphNodes[2]);
		twoThreeAndFour.add(bigGraphNodes[3]);
		ModularDecompositionNode moduleTwoThreeAndFour = new ModularDecompositionNode(twoThreeAndFour, null);
		
		List<Node> sixAndSeven = new ArrayList<Node>();
		sixAndSeven.add(bigGraphNodes[5]);
		sixAndSeven.add(bigGraphNodes[6]);
		ModularDecompositionNode moduleSixAndSeven = new ModularDecompositionNode(sixAndSeven, null);
		
		List<Node> one = new ArrayList<Node>();
		one.add(bigGraphNodes[0]);
		ModularDecompositionNode moduleOne = new ModularDecompositionNode(one, null);
		
		List<Node> eightNineTenAndEleven = new ArrayList<Node>();
		eightNineTenAndEleven.add(bigGraphNodes[7]);
		eightNineTenAndEleven.add(bigGraphNodes[8]);
		eightNineTenAndEleven.add(bigGraphNodes[9]);
		eightNineTenAndEleven.add(bigGraphNodes[10]);
		ModularDecompositionNode moduleEightNineTenAndEleven = new ModularDecompositionNode(eightNineTenAndEleven, null);
		
		List<ModularDecompositionNode> restrictForest = new ArrayList<ModularDecompositionNode>();
		restrictForest.add(moduleTwoThreeAndFour);
		restrictForest.add(moduleSixAndSeven);
		restrictForest.add(moduleOne);
		restrictForest.add(moduleEightNineTenAndEleven);
		
		List<ModularDecompositionNode> v0path = ModularDecompositionTree.v0modules(neighbors, nonNeighbors, pivot, restrictForest);
		
		List<ModularDecompositionNode> referenceOutput = new ArrayList<ModularDecompositionNode>();
		List<Node> allNodes = new ArrayList<Node>();
		for(int i = 0; i < 11; i++) {
			allNodes.add(bigGraphNodes[i]);
		}
		ModularDecompositionNode moduleAllNodes = new ModularDecompositionNode(allNodes, null);
		
		List<Node> five = new ArrayList<Node>();
		five.add(bigGraphNodes[4]);
		ModularDecompositionNode moduleFive = new ModularDecompositionNode(five, null);
		
		referenceOutput.add(moduleAllNodes);
		referenceOutput.add(moduleFive);
		for(int i = 0; i < referenceOutput.size() - 1; i++) {
                    referenceOutput.get(i).addChild(referenceOutput.get(i + 1));
                }
		
		assertEquals(referenceOutput, v0path);
	} 

}
