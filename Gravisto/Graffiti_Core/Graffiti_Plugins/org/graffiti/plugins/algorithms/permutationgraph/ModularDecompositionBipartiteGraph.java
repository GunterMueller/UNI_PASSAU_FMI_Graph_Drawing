// =============================================================================
//
//   ModularDecompositionBipartiteGraph.java
//
//   Copyright (c) 2001-2011, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.permutationgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import org.graffiti.graph.Node;

/**
 * Class represents a bipartite graph, that is built up in the v0-modules step
 * in the permutation graph algorithm by Dalhaus. On this graph, the maximum
 * connected components can be computed, which will be of use in the following
 * steps of the v0-modules step.
 * 
 * @author Emanuel Berndl
 * @version $Revision$ $Date$
 */
public class ModularDecompositionBipartiteGraph {

    private List<ModularDecompositionBipartiteGraphNode> nodes;
    private int amountNeighbors;

    private boolean[][] edges;

    private int maxDfs;

    private List<List<Node>> maxConnectedComponents;

    public ModularDecompositionBipartiteGraph(List<List<Node>> neighbors,
            List<List<Node>> nonNeighbors) {

        this.nodes = new ArrayList<ModularDecompositionBipartiteGraphNode>();
        this.amountNeighbors = 0;

        int index = 0;
        // Convert the neighbors and non-neighbors into
        // ModularDecompositionBipartiteGraphNodes and fill them into the nodes
        // set.
        for (List<Node> setNodes : neighbors) {
            nodes.add(new ModularDecompositionBipartiteGraphNode(setNodes,
                    true, index++));
            amountNeighbors++;
        }
        for (List<Node> setNodes : nonNeighbors) {
            nodes.add(new ModularDecompositionBipartiteGraphNode(setNodes,
                    false, index++));
        }

        edges = new boolean[nodes.size()][nodes.size()];

        determineEdges();

        List<List<ModularDecompositionBipartiteGraphNode>> maxConnectedComponentsFound = findMaxConnectedComponents();

        List<List<ModularDecompositionBipartiteGraphNode>> maxConnectedComponentsInTopologicalOrder = arrangeMaxConnectedComponentsTopologically(maxConnectedComponentsFound);

        this.maxConnectedComponents = changeMaxConnectedComponentsShape(maxConnectedComponentsInTopologicalOrder);
    }

    /**
     * Method arranges the maximal computed components in a topological order.
     * This order is uniquely, because the components can only be arranged in a
     * single path.
     * 
     * @param maxConnectedComponents
     *            The computed max components that are to be ordered.
     * @return A list of the maximal connected components in a topological
     *         order.
     */
    // TODO highly ineffective yet, will be implemented in linear time later on
    private List<List<ModularDecompositionBipartiteGraphNode>> arrangeMaxConnectedComponentsTopologically(
            List<List<ModularDecompositionBipartiteGraphNode>> maxConnectedComponents) {
        List<List<ModularDecompositionBipartiteGraphNode>> topologicalOrder = new ArrayList<List<ModularDecompositionBipartiteGraphNode>>();

        boolean[][] maxConnectedComponentEdges = new boolean[maxConnectedComponents
                .size()][maxConnectedComponents.size()];
        int numberOfConnectedComponent = -1;

        // Fill the edge matrix
        for (List<ModularDecompositionBipartiteGraphNode> connectedComponent : maxConnectedComponents) {
            numberOfConnectedComponent++;
            for (ModularDecompositionBipartiteGraphNode partOfComponent : connectedComponent) {
                for (int i = 0; i < edges.length; i++) {
                    if (edges[partOfComponent.getIndex()][i]) {
                        if (!connectedComponent.contains(nodes.get(i))) {
                            // There is an edge outside of this connected
                            // component, so find the max component the edge
                            // will be directed to
                            for (int j = 0; j < maxConnectedComponents.size(); j++) {
                                if (maxConnectedComponents.get(j).contains(
                                        nodes.get(i))) {
                                    maxConnectedComponentEdges[numberOfConnectedComponent][j] = true;
                                }
                            }
                        }
                    }
                }
            }
        }

        // Find the source for the topological sort
        int sourceNumber = -1;
        for (int i = 0; i < maxConnectedComponentEdges.length; i++) {
            boolean source = true;
            for (int j = 0; j < maxConnectedComponentEdges.length; j++) {
                if (maxConnectedComponentEdges[j][i]) {
                    source = false;
                    break;
                }

            }
            if (source) {
                sourceNumber = i;
            }
        }

        topologicalOrder.add(maxConnectedComponents.get(sourceNumber));
        int actualComponentNumber = sourceNumber;

        for (int i = 0; i < maxConnectedComponents.size() - 1; i++) {
            // Find the component, the last one has an edge to
            for (int j = 0; j < maxConnectedComponents.size(); j++) {
                if (maxConnectedComponentEdges[actualComponentNumber][j]) {
                    actualComponentNumber = j;
                    topologicalOrder.add(maxConnectedComponents.get(j));
                }
            }
        }

        return topologicalOrder;
    }

    /**
     * The maximum connected components are first computed to be
     * ModularDecompositionBipartiteGraphNodes. This representation is not of
     * use for the further algorithm. So this method changes the appearance to
     * graph nodes.
     * 
     * @return A list of the maximum connected components in graph nodes
     *         representation.
     */
    private List<List<Node>> changeMaxConnectedComponentsShape(
            List<List<ModularDecompositionBipartiteGraphNode>> oldConnectedComponentsRepresentation) {
        List<List<Node>> newConnectedComponentsRepresentation = new ArrayList<List<Node>>();

        for (List<ModularDecompositionBipartiteGraphNode> maxComponent : oldConnectedComponentsRepresentation) {
            List<Node> nodeSet = new ArrayList<Node>();

            for (ModularDecompositionBipartiteGraphNode singleNode : maxComponent) {
                nodeSet.addAll(singleNode.getNodes());
            }

            newConnectedComponentsRepresentation.add(nodeSet);
        }

        return newConnectedComponentsRepresentation;
    }

    /**
     * Method to find all the connected components of the
     * ModularDecompositionBipartiteGraph. Uses the algorithm idea of Tarjan to
     * find those.
     * 
     * @return A list of all the maximum connected components of the bipartite
     *         graph.
     */
    private List<List<ModularDecompositionBipartiteGraphNode>> findMaxConnectedComponents() {
        List<List<ModularDecompositionBipartiteGraphNode>> maxConnectedComponents = new ArrayList<List<ModularDecompositionBipartiteGraphNode>>();

        List<ModularDecompositionBipartiteGraphNode> remainingNodes = new ArrayList<ModularDecompositionBipartiteGraphNode>(
                nodes);
        Stack<ModularDecompositionBipartiteGraphNode> stack = new Stack<ModularDecompositionBipartiteGraphNode>();

        while (!remainingNodes.isEmpty()) {
            maxDfs = 0;
            depthSearchForMaxConnectedComponent(maxConnectedComponents,
                    remainingNodes.get(0), remainingNodes, stack);
        }

        return maxConnectedComponents;
    }

    /**
     * Method is used to find the maximum connected component starting from one
     * node of the bipartite graph. It therefore uses the algorithm of Tarjan,
     * which implements a depth search to find the maximum connected components.
     * 
     * @param node
     *            The starting node for the depth search.
     * @param remainingNodes
     *            The nodes which have not been visited by the complete
     *            algorithm to find the maximum connected components.
     * @param stack
     *            Nodes which have been visited will be placed on this stack,
     *            and will be popped if a maximum connected component is found.
     */
    private void depthSearchForMaxConnectedComponent(
            List<List<ModularDecompositionBipartiteGraphNode>> allMaxComponents,
            ModularDecompositionBipartiteGraphNode node,
            List<ModularDecompositionBipartiteGraphNode> remainingNodes,
            Stack<ModularDecompositionBipartiteGraphNode> stack) {

        List<ModularDecompositionBipartiteGraphNode> maxComponent = new ArrayList<ModularDecompositionBipartiteGraphNode>();

        node.setDfsNum(maxDfs);
        node.setLowLink(maxDfs);
        maxDfs++;
        stack.push(node);
        node.setOnStack(true);
        remainingNodes.remove(node);

        // Get the neighbors for this node
        List<ModularDecompositionBipartiteGraphNode> neighbors = new ArrayList<ModularDecompositionBipartiteGraphNode>();
        for (int i = 0; i < nodes.size(); i++) {
            if (edges[node.getIndex()][i]) {
                neighbors.add(nodes.get(i));
            }
        }

        // Do the recursive call for every neighbor
        for (ModularDecompositionBipartiteGraphNode neighbor : neighbors) {
            if (remainingNodes.contains(neighbor)) {
                // Recursive call when node is not already worked
                depthSearchForMaxConnectedComponent(allMaxComponents, neighbor,
                        remainingNodes, stack);
                node.setLowLink(Math.min(node.getLowLink(),
                        neighbor.getLowLink()));
            } else if (neighbor.isOnStack()) {
                node.setLowLink(Math.min(node.getLowLink(),
                        neighbor.getDfsNum()));
            }
        }

        if (node.getLowLink() == node.getDfsNum()) {
            // A max connected component has been found, it consists of the
            // nodes on the stack until this starting node is reached
            ModularDecompositionBipartiteGraphNode poppedNode = stack.pop();
            maxComponent.add(poppedNode);

            while (poppedNode.getIndex() != node.getIndex()) {
                poppedNode = stack.pop();
                maxComponent.add(poppedNode);
            }
            allMaxComponents.add(maxComponent);
        }
    }

    //
    // /**
    // * Method to find all the connected components of the
    // * ModularDecompositionBipartiteGraph. Uses the algorithm idea of Tarjan
    // to
    // * find those.
    // *
    // * @return A list of all the maximum connected components of the bipartite
    // * graph.
    // */
    // private List<List<ModularDecompositionBipartiteGraphNode>>
    // findMaxConnectedComponents() {
    // List<List<ModularDecompositionBipartiteGraphNode>> maxConnectedComponents
    // = new ArrayList<List<ModularDecompositionBipartiteGraphNode>>();
    //
    // List<ModularDecompositionBipartiteGraphNode> remainingNodes = new
    // ArrayList<ModularDecompositionBipartiteGraphNode>(
    // nodes);
    // Stack<ModularDecompositionBipartiteGraphNode> stack = new
    // Stack<ModularDecompositionBipartiteGraphNode>();
    //
    // while (!remainingNodes.isEmpty()) {
    // maxDfs = 0;
    // List<ModularDecompositionBipartiteGraphNode> maxComponent =
    // depthSearchForMaxConnectedComponent(
    // remainingNodes.get(0), remainingNodes, stack);
    // maxConnectedComponents.add(maxComponent);
    // }
    //
    // return maxConnectedComponents;
    // }
    //
    // /**
    // * Method is used to find the maximum connected component starting from
    // one
    // * node of the bipartite graph. It therefore uses the algorithm of Tarjan,
    // * which implements a depth search to find the maximum connected
    // components.
    // *
    // * @param node
    // * The starting node for the depth search.
    // * @param remainingNodes
    // * The nodes which have not been visited by the complete
    // * algorithm to find the maximum connected components.
    // * @param stack
    // * Nodes which have been visited will be placed on this stack,
    // * and will be popped if a maximum connected component is found.
    // * @return The maximum connected component starting at this node.
    // */
    // private List<ModularDecompositionBipartiteGraphNode>
    // depthSearchForMaxConnectedComponent(
    // ModularDecompositionBipartiteGraphNode node,
    // List<ModularDecompositionBipartiteGraphNode> remainingNodes,
    // Stack<ModularDecompositionBipartiteGraphNode> stack) {
    // List<ModularDecompositionBipartiteGraphNode> maxComponent = new
    // ArrayList<ModularDecompositionBipartiteGraphNode>();
    //
    // node.setDfsNum(maxDfs);
    // node.setLowLink(maxDfs);
    // maxDfs++;
    // stack.push(node);
    // node.setOnStack(true);
    // remainingNodes.remove(node);
    //
    // // Get the neighbors for this node
    // List<ModularDecompositionBipartiteGraphNode> neighbors = new
    // ArrayList<ModularDecompositionBipartiteGraphNode>();
    // for (int i = 0; i < nodes.size(); i++) {
    // if (edges[node.getIndex()][i]) {
    // neighbors.add(nodes.get(i));
    // }
    // }
    //
    // // Do the recursive call for every neighbor
    // for (ModularDecompositionBipartiteGraphNode neighbor : neighbors) {
    // if (remainingNodes.contains(neighbor)) {
    // // Recursive call when node is not already worked
    // depthSearchForMaxConnectedComponent(neighbor, remainingNodes,
    // stack);
    // node.setLowLink(Math.min(node.getLowLink(),
    // neighbor.getLowLink()));
    // } else if (neighbor.isOnStack()) {
    // node.setLowLink(Math.min(node.getLowLink(),
    // neighbor.getDfsNum()));
    // }
    // }
    //
    // if (node.getLowLink() == node.getDfsNum()) {
    // // A max connected component has been found, it consists of the
    // // nodes on the stack until this starting node is reached
    // ModularDecompositionBipartiteGraphNode poppedNode = stack.pop();
    // maxComponent.add(poppedNode);
    //
    // while (poppedNode.getIndex() != node.getIndex()) {
    // poppedNode = stack.pop();
    // maxComponent.add(poppedNode);
    // }
    // }
    //
    // return maxComponent;
    // }

    /**
     * Function sets the edges for the bipartite graph. Therefore it checks
     * whether a set of neighbors (meaning a node of these) is adjacent to a
     * node contained in the non-neighbors set. If they are adjacent, the graph
     * contains an edge directed from the neighbor set to the non-neighbor set.
     * If they are not adjacent, the edge is directed counter wise.
     */
    private void determineEdges() {
        // Edges from the neighbors to the non-neighbors
        for (int i = 0; i < amountNeighbors; i++) {
            for (int j = amountNeighbors; j < nodes.size(); j++) {
                ModularDecompositionBipartiteGraphNode bipartiteNodeOne = nodes
                        .get(i);
                ModularDecompositionBipartiteGraphNode bipartiteNodeTwo = nodes
                        .get(j);
                // Get the two sets which have to be checked, if any of their
                // nodes are adjacent
                List<Node> nodeOnesNodes = bipartiteNodeOne.getNodes();
                List<Node> nodeTwosNodes = bipartiteNodeTwo.getNodes();

                // Check if there is an edge from one node of the first to the
                // second set
                boolean edgeFound = false;
                for (int m = 0; m < nodeOnesNodes.size() && !edgeFound; m++) {
                    for (int n = 0; n < nodeTwosNodes.size(); n++) {
                        Node nodeOne = nodeOnesNodes.get(m);
                        Node nodeTwo = nodeTwosNodes.get(n);

                        Collection<Node> nodeOnesNeighbors = nodeOne
                                .getNeighbors();

                        if (nodeOnesNeighbors.contains(nodeTwo)) {
                            // Nodes are adjacent, so there has to be an edge
                            // from the
                            // neighbor set to the non-neighbor set in the
                            // bipartite
                            // graph
                            edges[i][j] = true;
                            edgeFound = true;
                            break;
                        }
                    }
                }
            }
        }

        // Edges from the non-neighbors to the neighbors
        for (int i = amountNeighbors; i < nodes.size(); i++) {
            for (int j = 0; j < amountNeighbors; j++) {
                ModularDecompositionBipartiteGraphNode bipartiteNodeOne = nodes
                        .get(i);
                ModularDecompositionBipartiteGraphNode bipartiteNodeTwo = nodes
                        .get(j);
                // Get the two sets which have to be checked, if any of their
                // nodes are adjacent
                List<Node> nodeOnesNodes = bipartiteNodeOne.getNodes();
                List<Node> nodeTwosNodes = bipartiteNodeTwo.getNodes();

                // Check if there is an edge from one node of the first to the
                // second set
                boolean edgeFound = false;
                for (int m = 0; m < nodeOnesNodes.size() && !edgeFound; m++) {
                    for (int n = 0; n < nodeTwosNodes.size(); n++) {
                        Node nodeOne = nodeOnesNodes.get(m);
                        Node nodeTwo = nodeTwosNodes.get(n);

                        Collection<Node> nodeOnesNeighbors = nodeOne
                                .getNeighbors();

                        if (!nodeOnesNeighbors.contains(nodeTwo)) {
                            // Nodes are adjacent, so there has to be an edge
                            // from the
                            // neighbor set to the non-neighbor set in the
                            // bipartite
                            // graph
                            edges[i][j] = true;
                            edgeFound = true;
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the maxConnectedComponents.
     * 
     * @return the maxConnectedComponents.
     */
    public List<List<Node>> getMaxConnectedComponents() {
        return maxConnectedComponents;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
