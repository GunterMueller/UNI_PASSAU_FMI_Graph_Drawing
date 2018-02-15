// =============================================================================
//
//   ModularDecompositionTree.java
//
//   Copyright (c) 2001-2011, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.permutationgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * A tree representation of the modular decomposition of a given graph. From the
 * modular decomposition tree one can derive the needed modules of a given
 * graph, which have to be oriented in order to get the transitive orientation
 * for the given graph and thus the solution for the permutation graph problem.
 * 
 * @author Emanuel Berndl
 * @version $Revision$ $Date$
 */
public class ModularDecompositionTree {

    private ModularDecompositionNode root;
    private List<Node> nodeSet;
    private Graph graph;

    /**
     * Constructor for the modular decomposition tree. The modular decomposition
     * tree will be computed for the given graph.
     * 
     * @param graph
     *            Graph whose modular decomposition is to be computed.
     * @throws ModularDecompositionNodeException
     * @throws RestrictFailedExcpetion
     */
    public ModularDecompositionTree(Graph graph, List<Node> graphNodes)
            throws ModularDecompositionNodeException, RestrictFailedExcpetion {

        // TODO Adjazenzmatrix ?
        this.graph = graph;

        if (graphNodes.isEmpty()) {
            this.nodeSet = new ArrayList<Node>();
            return;
        }

        this.root = new ModularDecompositionNode(graphNodes, null);
        this.nodeSet = new ArrayList<Node>(graphNodes);

        // Choose pivot node for the splitting of the graph. The decomposition
        // tree will be the same, no matter what node will be chosen as pivot,
        // so the first node of the set will be picked as pivot.
        Node pivot = graphNodes.get(0);

        // Get the two sets corresponding to the neighbors and non-neighbors of
        // the pivot.
        Collection<Node> pivotNeighborsCollection = pivot.getNeighbors();
        List<Node> pivotNeighbors = new ArrayList<Node>();
        List<Node> pivotNonNeighbors = new ArrayList<Node>(graphNodes);
        pivotNonNeighbors.remove(pivot);

        // TODO: is O(n^2)
        for (Node node : pivotNeighborsCollection) {
            if (graphNodes.contains(node)) {
                pivotNeighbors.add(node);
                pivotNonNeighbors.remove(node);
            }
        }

        ModularDecompositionTree neighborTree = null;
        ModularDecompositionTree nonNeighborTree = null;

        if (pivotNeighbors.size() == 1) {
            // neighborTree is a singleton node
            // this creates a simplified MDT without further recursion
            neighborTree = new ModularDecompositionTree(graph,
                    pivotNeighbors.get(0));
        } else {
            // Recursion step for the set of neighbors
            neighborTree = new ModularDecompositionTree(graph, pivotNeighbors);
        }
        if (pivotNonNeighbors.size() == 1) {
            // nonNeighborTree is a singleton node
            // this creates a simplified MDT without further recursion
            nonNeighborTree = new ModularDecompositionTree(graph,
                    pivotNonNeighbors.get(0));
        } else {
            // Recursion step for the set of non-neighbors
            nonNeighborTree = new ModularDecompositionTree(graph,
                    pivotNonNeighbors);
        }

        List<ModularDecompositionNode> restrictForest = restrict(neighborTree,
                nonNeighborTree);

        List<ModularDecompositionNode> v0Path = v0modules(pivotNeighbors,
                pivotNonNeighbors, pivot, restrictForest);

        assemble(restrictForest, v0Path);
    }

    /**
     * Constructor to construct a modular decomposition tree of one singleton
     * node. There is no further splitting needed in this tree.
     * 
     * @param graph
     *            The graph which the node belongs to.
     * @param singletonNode
     *            The singleton node the tree is supposed to be built up.
     */
    private ModularDecompositionTree(Graph graph, Node singletonNode) {
        ModularDecompositionNode singletonMDNode = new ModularDecompositionNode(
                singletonNode, null);
        singletonMDNode.setType(ModularDecompositionNode.Type.PRIME);
        this.root = singletonMDNode;

        List<Node> nodeSet = new LinkedList<Node>();
        nodeSet.add(singletonNode);
        this.nodeSet = nodeSet;

        this.graph = graph;
    }

    /**
     * Constructor is for test cases only.
     * 
     * @param graph
     *            Underlying graph for the MDTree.
     * @param nodeSet
     *            Underlying nodeset of the graph.
     * @param root
     *            Rootnode for the tree.
     */
    public ModularDecompositionTree(Graph graph, List<Node> nodeSet,
            ModularDecompositionNode root) {
        this.graph = graph;
        this.root = root;
        this.nodeSet = new ArrayList<Node>(nodeSet);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
        if (!(o instanceof ModularDecompositionTree)) {
            return false;
        }
        if (!this.getRoot().equals(((ModularDecompositionTree) o).getRoot())) {
            return false;
        }
        return true;
    }

    /**
     * Final step of the Dalhaus permutation graph algorithm. Uses the forest
     * retrieved from the restrict step and the path received from the
     * v0-modules step and puts them together to a tree. After this is done, the
     * method labels those nodes without labels, which are the nodes on the
     * v0-modules path.
     * 
     * @param restrictForest
     *            Forest of ModularDecompositionNodes computed in the restrict
     *            step.
     * @param v0modules
     *            Path of ModularDecompositionNodes that is computed in the
     *            v0-modules step.
     */
    private void assemble(List<ModularDecompositionNode> restrictForest,
            List<ModularDecompositionNode> v0modules) {
        this.root = v0modules.get(0);

        // Connect every module from the restrict forest to the node in the
        // v0path, that contains at least the same nodes as the module
        for (ModularDecompositionNode restrictModule : restrictForest) {
            for (int i = v0modules.size() - 1; i >= 0; i--) {
                if (moduleContainsAnotherModule(v0modules.get(i),
                        restrictModule)) {
                    v0modules.get(i).addChild(restrictModule);
                    break;
                }
            }
        }

        // v0path has to be labeled accordingly to the nodes' children and their
        // adjacency towards each other
        for (int i = v0modules.size() - 1; i >= 0; i--) {
            List<ModularDecompositionNode> children = v0modules.get(i)
                    .getChildren();

            if (children.isEmpty() || children.size() > 2) {
                // Node is a leaf
                v0modules.get(i).setType(ModularDecompositionNode.Type.PRIME);
            } else {
                // The actual v0path node can only have 2 children consequently,
                // and these two have to be checked for their adjacency
                ModularDecompositionNode nodeOne = v0modules.get(i)
                        .getChildren().get(0);
                ModularDecompositionNode nodeTwo = v0modules.get(i)
                        .getChildren().get(1);

                if (nodeOne.getNodes().get(0).getNeighbors()
                        .contains(nodeTwo.getNodes().get(0))) {
                    v0modules.get(i).setType(
                            ModularDecompositionNode.Type.SERIES);
                } else {
                    v0modules.get(i).setType(
                            ModularDecompositionNode.Type.PARALLEL);
                }
            }

            if (v0modules.get(i).getType()
                    .equals(ModularDecompositionNode.Type.SERIES)
                    || v0modules.get(i).getType()
                            .equals(ModularDecompositionNode.Type.PARALLEL)) {
                // Now check, if one of the nodes' children has the same type
                // (parallel or series) as this node. If yes, these nodes have
                // to be
                // collapsed and put together
                List<ModularDecompositionNode> newParent = new ArrayList<ModularDecompositionNode>();
                List<ModularDecompositionNode> newChild = new ArrayList<ModularDecompositionNode>();

                List<ModularDecompositionNode> oldParent = new ArrayList<ModularDecompositionNode>();
                List<ModularDecompositionNode> oldChild = new ArrayList<ModularDecompositionNode>();

                for (ModularDecompositionNode child : children) {
                    if (v0modules.get(i).getType() == child.getType()) {
                        for (ModularDecompositionNode childsChild : child
                                .getChildren()) {
                            newParent.add(v0modules.get(i));
                            newChild.add(childsChild);
                        }
                        oldParent.add(v0modules.get(i));
                        oldChild.add(child);
                    }
                }
                for (int j = 0; j < newParent.size(); j++) {
                    newParent.get(j).addChild(newChild.get(j));
                }
                for (int j = 0; j < oldParent.size(); j++) {
                    oldParent.get(j).removeChild(oldChild.get(j));
                }
            }
        }
    }

    /**
     * Method checks if a certain module contains another one, meaning that the
     * first module needs to contain all the nodes of the other one.
     * 
     * @param nodeOne
     *            Node one that is tested to contain the other module.
     * @param nodeTwo
     *            Node that is checked to be contained in the first module.
     * @return True, if the nodes of the second node are also in the first one.
     */
    private boolean moduleContainsAnotherModule(
            ModularDecompositionNode nodeOne, ModularDecompositionNode nodeTwo) {
        boolean containsModule = true;

        for (Node node : nodeTwo.getNodes()) {
            if (!nodeOne.getNodes().contains(node)) {
                containsModule = false;
                break;
            }
        }

        return containsModule;
    }

    /**
     * Method computes the modules of the actual recursion step that can be
     * formed together with the pivot node. It returns these in a path, which
     * reaches from the single pivot node module to the module that represents
     * the whole node set of the graph. The path starts with the biggest module
     * and ends with the smallest, the single pivot module.
     * 
     * @param neighbors
     *            The set of neighbors of the current recursion step.
     * @param nonNeighbors
     *            The set of non-neighbors of the current recursion step.
     * @param pivot
     *            The node that was used to split up the current subgraph into
     *            neighbors and non-neighbors.
     * @param restrictForest
     *            A forest of modules computed by the preceding restrict step.
     * @return A list/path of modules.
     * @throws ModularDecompositionNodeException
     */
    public static List<ModularDecompositionNode> v0modules(
            List<Node> neighbors, List<Node> nonNeighbors, Node pivot,
            List<ModularDecompositionNode> restrictForest)
            throws ModularDecompositionNodeException {
        List<ModularDecompositionNode> v0path = new ArrayList<ModularDecompositionNode>();

        if (neighbors.isEmpty() && nonNeighbors.isEmpty()) {
            List<Node> pivotNode = new ArrayList<Node>();
            pivotNode.add(pivot);
            ModularDecompositionNode pivotModule = new ModularDecompositionNode(
                    pivotNode, null);
            v0path.add(pivotModule);
        } else {

            List<List<Node>> neighborConnectedComponents = findConnectedComponents(
                    neighbors, true);
            List<List<Node>> nonNeighborConnectedComponents = findConnectedComponents(
                    nonNeighbors, false);

            List<List<Node>> neighborConnectedComponentsAndModules = combineConnectedComponents(
                    neighborConnectedComponents, restrictForest);
            List<List<Node>> nonNeighborConnectedComponentsAndModules = combineConnectedComponents(
                    nonNeighborConnectedComponents, restrictForest);

            ModularDecompositionBipartiteGraph bipartiteGraph = new ModularDecompositionBipartiteGraph(
                    neighborConnectedComponentsAndModules,
                    nonNeighborConnectedComponentsAndModules);

            List<List<Node>> maxConnectedComponentsOfBipartiteGraph = bipartiteGraph
                    .getMaxConnectedComponents();

            v0path = transformTopologicalOrderIntoModularDecompositionNodePath(maxConnectedComponentsOfBipartiteGraph);

            // Add the pivot node as a single node to the end of the v0path
            List<Node> pivotSet = new ArrayList<Node>();
            pivotSet.add(pivot);
            ModularDecompositionNode pivotNode = new ModularDecompositionNode(
                    pivotSet, v0path.get(v0path.size() - 1));
            v0path.add(pivotNode);

            // Run up the path and add the nodes of lower nodes to their parents
            for (int i = v0path.size() - 1; i > 0; i--) {
                v0path.get(i - 1).getNodes().addAll(v0path.get(i).getNodes());
            }

            // As the last step, make the nodes a child of each other, according
            // to
            // the path
            for (int i = 0; i < v0path.size() - 1; i++) {
                v0path.get(i).addChild(v0path.get(i + 1));
            }
        }

        return v0path;
    }

    private static List<ModularDecompositionNode> transformTopologicalOrderIntoModularDecompositionNodePath(
            List<List<Node>> maxConnectedComponentsInTopologicalOrder)
            throws ModularDecompositionNodeException {
        List<ModularDecompositionNode> path = new ArrayList<ModularDecompositionNode>();

        for (int i = 0; i < maxConnectedComponentsInTopologicalOrder.size(); i++) {
            ModularDecompositionNode mDNode = null;
            if (i == 0) {
                mDNode = new ModularDecompositionNode(
                        maxConnectedComponentsInTopologicalOrder.get(0), null);
            } else {
                mDNode = new ModularDecompositionNode(
                        maxConnectedComponentsInTopologicalOrder.get(i),
                        path.get(i - 1));
            }
            path.add(mDNode);
        }

        return path;
    }

    /**
     * Method checks, if some of the already found connected components must be
     * combined, because there is already a bigger module, that contains both of
     * them.
     * 
     * @param connectedComponents
     *            The set of already found connected components.
     * @param restrictForest
     *            Modules found by the preceding restrict forest.
     * @return A list of combined connected components.
     */
    private static List<List<Node>> combineConnectedComponents(
            List<List<Node>> connectedComponents,
            List<ModularDecompositionNode> restrictForest) {
        List<List<Node>> combinedList = new ArrayList<List<Node>>();

        List<List<Node>> oldConnectedComponents = new ArrayList<List<Node>>(
                connectedComponents);
        List<ModularDecompositionNode> restrictModules = new ArrayList<ModularDecompositionNode>(
                restrictForest);

        boolean[] workedComponent = new boolean[oldConnectedComponents.size()];
        int componentNumber = 0;

        for (List<Node> oldComponent : oldConnectedComponents) {
            if (!workedComponent[componentNumber]) {

                Node compareNode = oldComponent.get(0);

                List<Node> combinedConnectedComponent = new ArrayList<Node>(
                        oldComponent);
                // Find the module which contains this node
                for (int i = 0; i < restrictModules.size(); i++) {
                    if (restrictModules.get(i).getNodes().contains(compareNode)) {
                        // Module contains the comparing node and thus contains
                        // the
                        // whole component and maybe other components
                        if (oldComponent.size() < restrictModules.get(i)
                                .getNodes().size()) {
                            // Module is bigger - more connected components must
                            // be
                            // put together

                            List<Node> modulesNodes = restrictModules.get(i)
                                    .getNodes();
                            List<Node> copyNodes = new ArrayList<Node>();

                            // Delete the nodes of the module, which have
                            // already
                            // been covered by the already found connected
                            // component
                            modulesNodes.removeAll(oldComponent);
                            copyNodes.addAll(oldComponent);

                            while (!modulesNodes.isEmpty()) {

                                // Get a node which will be contained in another
                                // component, in order to find that component
                                Node compareNode2 = modulesNodes.get(0);

                                int componentCounter = 0;

                                // Find the connected component containing this
                                // node
                                for (List<Node> oldComponent2 : oldConnectedComponents) {
                                    if (oldComponent2.contains(compareNode2)) {
                                        // This component also belongs to the
                                        // module, so a bigger element has to be
                                        // formed
                                        combinedConnectedComponent
                                                .addAll(oldComponent2);

                                        modulesNodes.removeAll(oldComponent2);
                                        copyNodes.addAll(oldComponent2);
                                        workedComponent[componentCounter] = true;
                                    }
                                    componentCounter++;

                                }
                            }
                            modulesNodes.addAll(copyNodes);
                        }
                    }
                }
                combinedList.add(combinedConnectedComponent);
            }
            componentNumber++;
        }

        return combinedList;
    }

    /**
     * Method finds the connected components for the given list of nodes. The
     * boolean specifies, if it is currently operating on the neighbors or
     * non-neighbors. This is important, because when searching on the set of
     * neighbors, CO-COMPONENTS are the components that are to be found.
     * 
     * @param nodes
     *            Set of nodes that is to be seperated in connected components.
     * @param neighbors
     *            True, if the set of nodes represents the neighbors in the
     *            current recursion step.
     * @return A list of connected components.
     */
    // TODO not in linear time yet, maybe subgraph-function from gravisto?
    private static List<List<Node>> findConnectedComponents(List<Node> nodes,
            boolean neighbors) {
        List<List<Node>> allConnectedComponents = new ArrayList<List<Node>>();

        List<Node> remainingNodes = new ArrayList<Node>(nodes);

        while (!remainingNodes.isEmpty()) {
            List<Node> connectedComponent = new ArrayList<Node>();

            // Find the connected component starting at the first node of the
            // remaining ones
            ModularDecompositionTree.findConnectedComponent(connectedComponent,
                    remainingNodes, remainingNodes.get(0), neighbors);

            allConnectedComponents.add(connectedComponent);
        }

        return allConnectedComponents;
    }

    /**
     * Method finds the connected component (if neighbors if false) or the
     * connected co-component (if neighbors is true) for the tree starting at
     * node. Therefore it fills the needed nodes into the connectedComponent
     * List.
     * 
     * @param connectedComponent
     *            The list of nodes that represents the connected component or
     *            co-component.
     * @param remainingNodes
     *            The nodes that have not been put into its correct component
     *            yet.
     * @param node
     *            The node to start the procedure to find the component.
     * @param neighbors
     *            True, if the component is to be found on the set of neighbors
     *            in the current recursion step.
     */
    private static void findConnectedComponent(List<Node> connectedComponent,
            List<Node> remainingNodes, Node node, boolean neighbors) {
        if (node != null && !remainingNodes.isEmpty()) {
            connectedComponent.add(node);
            remainingNodes.remove(node);
            if (!neighbors) {
                // non-neighbors are being considered, so normal connected
                // components are to be found
                for (Node child : node.getNeighbors()) {
                    if (remainingNodes.contains(child)) {
                        findConnectedComponent(connectedComponent,
                                remainingNodes, child, neighbors);
                    }
                }
            } else {
                // neighbors are being considered, so CO-COMPONENTS are to be
                // found
                // Get the non neighbors of the actually worked node
                List<Node> nodesNonNeighbors = new ArrayList<Node>(
                        remainingNodes);
                for (Node actualNeighbor : node.getNeighbors()) {
                    nodesNonNeighbors.remove(actualNeighbor);
                }

                // Search for the connected co-component
                for (Node nonNeighbor : nodesNonNeighbors) {
                    findConnectedComponent(connectedComponent, remainingNodes,
                            nonNeighbor, neighbors);
                }
            }
            // The node itself has to be added to the component and be removed
            // at the remainingNodes
        }
    }

    /**
     * Method represents the first step of the recursive calls in the modular
     * decomposition algorithm. It therefore checks the tree of neighbors with
     * the nodes of non-neighbors and does the same counter wise. Running up the
     * tree, the method checks every internal node, if its children have the
     * same adjacencies according to those nodes of the activeEdges set. These
     * are the nodes contained in the tree, which is the counterpart of the
     * actually checked tree in one recursion call of the whole algorithm. If
     * the children of a node got the same adjacencies, it is still a module and
     * will be kept in the tree. Otherwise there are nodes which are able to
     * distinguish the local module (the node that is checked), so it has to be
     * deleted. After the checking process of all nodes, the deletion of some
     * nodes remain a forest which will be returned and used later on in the
     * whole algorithm.
     * 
     * @param neighborTree
     *            Tree of neighbors whose modules are to be checked.
     * @param nonNeighborTree
     *            Tree of non-neighbors whose modules are to be checked.
     * @return A list of roots, consecutively a forest.
     * @throws RestrictFailedExcpetion
     */
    public static List<ModularDecompositionNode> restrict(
            ModularDecompositionTree neighborTree,
            ModularDecompositionTree nonNeighborTree)
            throws RestrictFailedExcpetion {
        List<ModularDecompositionNode> forest = new ArrayList<ModularDecompositionNode>();

        if (neighborTree.isEmpty() && nonNeighborTree.isEmpty()) {
            return forest;
        } else if (neighborTree.isEmpty()) {
            // forest will consist only of the nonNeighborTree
            forest.add(nonNeighborTree.getRoot());
        } else if (nonNeighborTree.isEmpty()) {
            // forest will consist only of the neighborTree
            forest.add(neighborTree.getRoot());
        } else {
            List<ModularDecompositionNode> neighborForest = helpRestrict(
                    neighborTree, nonNeighborTree.getNodeSet());
            List<ModularDecompositionNode> nonNeighborForest = helpRestrict(
                    nonNeighborTree, neighborTree.getNodeSet());

            forest.addAll(neighborForest);
            forest.addAll(nonNeighborForest);
        }

        return forest;
    }
    
    /**
     * After the restrict forest has been found, all of its elements have to be checked for their active edges. Those elements with the same active edges have to be put together under a new parental node.
     * 
     * @param oldList   The list that has to be checked and combined.
     * @param otherNodes        The Nodes of the other tree.
     * @return  A list with the combined elements.
     */
    private static List<ModularDecompositionNode> combineNodesWithSameNeighbors (List<ModularDecompositionNode> oldList, List<Node> otherNodes) {
        List<ModularDecompositionNode> combinedNeighbors = new ArrayList<ModularDecompositionNode>();
        
        // Create an array that indicates, which element of the old list is already combined in the new list
        boolean[] elementCombined = new boolean[oldList.size()];
        for(int i = 0; i < elementCombined.length; i++) {
            elementCombined[i] = false;
        }
        
        for(int i = 0; i < oldList.size(); i++) {
            if(!elementCombined[i]) {
                // Remember the positions of those elements with the same active edges
                List<Integer> elementsToCombine = new ArrayList<Integer>();
                elementsToCombine.add(i);
                elementCombined[i] = true;
                Collection<Node> neighborsCollection = oldList.get(i).getNodes().get(0).getNeighbors();
                List<Node> neighbors = new ArrayList<Node>();
                for(Node node : neighborsCollection) {
                    if(otherNodes.contains(node)) {
                        neighbors.add(node);                        
                    }
                }
                for(int j = i+1; j < oldList.size(); j++) {
                    if(!elementCombined[j]) {
                        Collection<Node> neighborsCollection2 = oldList.get(j).getNodes().get(0).getNeighbors();
                        List<Node> neighbors2 = new ArrayList<Node>();
                        for(Node node : neighborsCollection2) {
                            if(otherNodes.contains(node)) {
                                neighbors2.add(node);                                
                            }
                        }
                        
                        if(neighbors.equals(neighbors2)) {
                            elementsToCombine.add(j);
                            elementCombined[j] = true;
                        }                    
                    }
                }
                
                if(elementsToCombine.size() > 1) {
                    // There are more elements with the same active edges
                    
                    // Get all the nodes of the new children
                    List<Node> parentsNodes = new ArrayList<Node>();
                    for(Integer position : elementsToCombine) {
                        parentsNodes.addAll(oldList.get(position).getNodes());
                    }
                    
                    // Check if the new parental node needs to be series or parallel
                    Node firstNode = oldList.get(elementsToCombine.get(1)).getNodes().get(0);
                    ModularDecompositionNode parent = null;
                    try {
                        parent = new ModularDecompositionNode(parentsNodes, null);
                    } catch (ModularDecompositionNodeException e) {
                    }
                    
                    if(parent != null) {
                        if(neighbors.contains(firstNode)) {
                            parent.setType(ModularDecompositionNode.Type.SERIES);
                        } else {
                            parent.setType(ModularDecompositionNode.Type.PARALLEL);
                        }
                        
                    }
                    
                    combinedNeighbors.add(parent);
                    
                } else {
                    combinedNeighbors.add(oldList.get(i));
                }
            }
        }
        
        return combinedNeighbors;
    }

    /**
     * Method is used in the restrict method and does one of two tree checks of
     * the restrict step. It therefore runs through the given tree and checks
     * every node of that tree (so every module) if its children got the same
     * adjacencies according to the nodes of the counterPartNodes set. If the
     * children do not have the same adjacencies, the module must be deleted
     * from the tree. The deletion of modules inside of the tree leaves a forest
     * of ModularDecompositionNodes.
     * 
     * @param tree
     *            The tree whose modules are to be checked.
     * @param counterPartNodes
     *            The set of nodes to which the adjacencies of the modules have
     *            to be checked.
     * @return A forest of root nodes.
     * @throws RestrictFailedExcpetion
     */
    private static List<ModularDecompositionNode> helpRestrict(
            ModularDecompositionTree tree, List<Node> counterPartNodes)
            throws RestrictFailedExcpetion {
        List<ModularDecompositionNode> forest = new ArrayList<ModularDecompositionNode>();

        try {
            verifyChildrenHaveSameActiveEdges(tree.getRoot(), forest,
                    counterPartNodes);
        } catch (NotALeafException e) {
            throw new RestrictFailedExcpetion(
                    "Restrict failed due to NotALeafException", e);
        }

        if (forest.isEmpty()) {
            // if the forest is empty then we haven't removed any nodes from the
            // tree
            // therefore the whole tree must be added to the forest
            forest.add(tree.getRoot());
        }

        return forest;
    }

    private static boolean verifyChildrenHaveSameActiveEdges(
            ModularDecompositionNode node,
            List<ModularDecompositionNode> forest, List<Node> counterPartNodes)
            throws NotALeafException {
        boolean sameActiveEdges = true;

        if (node.getChildren().isEmpty()) {
            List<Node> activeEdges = getActiveEdgesToLeaf(node,
                    counterPartNodes);
            node.setActiveEdgeNodes(activeEdges);
            return true;
        }
        // build up two lists for those children, whose children themselves have
        // the same active edges or not
        List<ModularDecompositionNode> conformingChildren = new ArrayList<ModularDecompositionNode>();
        List<ModularDecompositionNode> nonConformingChildren = new ArrayList<ModularDecompositionNode>();
        // check the children whether their children themselves have the same
        // active edges
        for (ModularDecompositionNode MDnode : node.getChildren()) {
            if (verifyChildrenHaveSameActiveEdges(MDnode, forest,
                    counterPartNodes)) {
                conformingChildren.add(MDnode);
            } else {
                nonConformingChildren.add(MDnode);
            }
        }
        if (nonConformingChildren.isEmpty()) {
            // every child is conform itself, so its children have the same
            // active edges. Now this node is to be checked whether its children
            // have the same active edges
            if (allChildrenGotSameActiveEdges(node.getChildren())) {
                // this nodes' children have the same active edges, so this node
                // must be assigned the same active edges as well
                node.setActiveEdgeNodes(node.getChildren().get(0)
                        .getActiveEdgeNodes());
            } else {
                // children do not have the same active edges, so this nodes'
                // children have to be added to the forest
                List<ModularDecompositionNode> combinedConformingChildren = combineNodesWithSameNeighbors(conformingChildren, counterPartNodes);         
                
                forest.addAll(combinedConformingChildren);
                sameActiveEdges = false;
            }
        } else {
            // one children of the node is not conform, so the node itself
            // cannot be conform and must be deleted which results in an adding
            // of the conforming children to the forest
            List<ModularDecompositionNode> combinedConformingChildren = combineNodesWithSameNeighbors(conformingChildren, counterPartNodes);
            
            forest.addAll(combinedConformingChildren);
        }

        return sameActiveEdges;
    }

    private static boolean allChildrenGotSameActiveEdges(
            List<ModularDecompositionNode> children) {
        if (!children.isEmpty()) {
            HashSet<Node> hash = new HashSet<Node>();
            for (Node node : children.get(0).getActiveEdgeNodes()) {
                hash.add(node);
            }
            for (int i = 1; i < children.size(); i++) {
                HashSet<Node> hashTwo = new HashSet<Node>();
                for (Node node : children.get(i).getActiveEdgeNodes()) {
                    hashTwo.add(node);
                }

                if (!hash.equals(hashTwo)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Method checks one node for its acitve edges. Active edges are adjacent
     * nodes in the counterPartNodes set.
     * 
     * @param MDnode
     *            The ModularDecompositionNode that is to be checked.
     * @param counterPartNodes
     *            The set of nodes which the adjacency to the MDnode is to be
     *            checked.
     * @return A list of nodes of the counterPartNodes set which are adjacent to
     *         the node corresponding to the ModularDecompositionNode.
     * @throws NotALeafException
     *             When invoked on a non-leaf.
     */
    private static List<Node> getActiveEdgesToLeaf(
            ModularDecompositionNode MDnode, List<Node> counterPartNodes)
            throws NotALeafException {
        List<Node> activeEdges = new ArrayList<Node>();
        if (MDnode.isLeaf()) {

            // MDnode's set of nodes consists only of one node, because only
            // single
            // nodes can be checked for their active edges
            Node node = MDnode.getNodes().get(0);
            Collection<Node> allNodeNeighbors = node.getNeighbors();
            for (Node counterNode : counterPartNodes) {
                if (allNodeNeighbors.contains(counterNode)) {
                    activeEdges.add(counterNode);
                }
            }

        } else {
            throw new NotALeafException(
                    "Function assignActiveEdgesToLeaf cannot be used on a non-leaf node");
        }
        return activeEdges;
    }

    /**
     * Returns the graph.
     * 
     * @return the graph.
     */
    public Graph getGraph() {
        return graph;
    }

    /**
     * Returns the nodeSet.
     * 
     * @return the nodeSet.
     */
    public List<Node> getNodeSet() {
        return nodeSet;
    }

    /**
     * Returns the root.
     * 
     * @return the root.
     */
    public ModularDecompositionNode getRoot() {
        return root;
    }

    /**
     * Decide whether this tree is empty
     * 
     * @return true if this tree contains no nodes (root is null)
     */
    public boolean isEmpty() {
        return this.root == null;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
