// =============================================================================
//
//   NodePartition.java
//
//   Copyright (c) 2001-2011, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.permutationgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * Class is used to retrieve the transitive orientation of a given prime graph.
 * Therefore it uses a procedure called vertex partitioning. This procedure is
 * called twice. The first call returns a list of the graphnodes, where the
 * first element is a sink in the transitive orientation. Then, the second call
 * is done with this sink element. The resulting partitioning then is a
 * transitive order for the given graph.
 * 
 * @author Emanuel Berndl
 * @version $Revision$ $Date$
 */
public class PermutationTransitiveOrientation {

    /**
     * Whole method to get the transitive orientation order of the nodes of a
     * given prime graph. Class the vertexPartition algorithm twice. The first
     * call with a random node returns a sink of the transitive orientation. The
     * second call with that sink as input then returns the order of the nodes,
     * in which the edges have to be oriented in order to transitively orient
     * the given graph.
     * 
     * @param graph
     *            The graph that is to be transitively oriented.
     * @return An order of the nodes if the graph is transitively orientable or
     *         null, if it is not.
     */
    public static List<Node> vertexSort(Graph graph) {
        List<Node> transitiveOrder = new ArrayList<Node>();

        List<Node> graphNodes = graph.getNodes();

        if (graph != null && !graphNodes.isEmpty()) {
            Node firstNode = null;

            // Get the first node and make sure it distinguishes the graph
            // If such a node is not found, the graph is complete or edgeless
            // and must be treated in a special case
            for (int i = 0; i < graphNodes.size(); i++) {
                if (!((graphNodes.get(i).getNeighbors().size() == (graphNodes
                        .size() - 1)) || (graphNodes.get(i).getNeighbors()
                        .size() == 0))) {
                    firstNode = graphNodes.get(i);
                    break;
                }
            }

            if (firstNode != null) {
                // First call to vertexPartition to retrieve a sink
                List<PartitionClass> prePartition = vertexPartition(graph,
                        firstNode);

                // Get the sink
                Node sink = prePartition.get(0).getNodes().get(0);

                // Second call to vertexPartition returns the transitive order
                List<PartitionClass> partition = vertexPartition(graph, sink);

                if (partition != null) {
                    // Break down the partition classes into their nodes
                    transitiveOrder = transformPartitionClassIntoListOfNodes(partition);
                } else {
                    // The transitive orientation went wrong, so the graph is
                    // not
                    // transitively orientable
                    return null;
                }

                return transitiveOrder;
            } else {
                // Graph is complete or edgeless
                return graphNodes;
            }

        } else {
            return null;
        }

    }

    /**
     * Base method of the whole prime graph transitive orientation algorithm.
     * This algorithm picks one node out of the partition classes and splits all
     * the other classes that are distinguished by that node into two seperate
     * new partition classes. This splitting goes on until all classes consist
     * of only a single node left. This list is then returned.
     * 
     * @param graph
     *            The graph that is to be transitively oriented.
     * @param node
     *            The node which is used for the first splitting of the
     *            partition classes.
     * @return A list of partition classes that have been split and ordered.
     */
    private static List<PartitionClass> vertexPartition(Graph graph, Node node) {
        List<PartitionClass> partitionedList = new ArrayList<PartitionClass>();

        List<Node> firstElementsNodes = graph.getNodes();
        firstElementsNodes.remove(node);

        // Initialize the partition
        PartitionClass firstElement = new PartitionClass(firstElementsNodes,
                Integer.MAX_VALUE);
        PartitionClass secondElement = new PartitionClass(node,
                Integer.MAX_VALUE);
        partitionedList.add(firstElement);
        partitionedList.add(secondElement);

        // Partition the classes
        boolean remainPartitioning = true;
        while (remainPartitioning) {
            int splitClassNumber = -1;

            // Find a class that will split the others
            for (int i = 0; i < partitionedList.size(); i++) {
                if (partitionedList.get(i).getNodes().size() <= (partitionedList
                        .get(i).getLastUsed() / 2)) {
                    splitClassNumber = i;
                    break;
                }
            }

            if (splitClassNumber == -1) {
                // No fitting class for further splitting has been found
                remainPartitioning = false;
            } else {
                PartitionClass splitClass = partitionedList
                        .get(splitClassNumber);
                splitClass.setLastUsed(splitClass.getNodes().size());

                for (Node splitNode : splitClass.getNodes()) {
                    int oldPartitionListSize = partitionedList.size();
                    
                    partitionedList = splitClasses(partitionedList, splitNode,
                            splitClassNumber);
                    
                    // Check if there has been new classes in front of the split class. If yes, the splitClassNumber has to be corrected
                    if(oldPartitionListSize != partitionedList.size()) {
                        for(int i = 0; i < partitionedList.size(); i++) {
                            if(partitionedList.get(i).getNodes().contains(splitNode)) {
                                splitClassNumber = i;
                            }
                        }
                    }
                }
            }
        }
        return partitionedList;
    }

    /**
     * Method is used to split the given partition according to the given node.
     * It cycles through all other partition class than its own and splits it
     * into two new partition classes, one containing the adjacent nodes, the
     * other one containing the non-adjacent nodes.
     * 
     * @param partition
     *            The partition that is to be further refined.
     * @param node
     *            The node according to which the partition is to be refined.
     * @param ownClass
     *            The number of the partition class that contains the node
     *            itself.
     * @return A refined partition.
     */
    private static List<PartitionClass> splitClasses(
            List<PartitionClass> partition, Node node, int ownClass) {
        List<PartitionClass> newPartition = new ArrayList<PartitionClass>();
        Collection<Node> splitNeighbors = node.getNeighbors();

        for (int i = 0; i < partition.size(); i++) {
            if (i != ownClass) {
                PartitionClass partitionAdjacentClass = new PartitionClass(
                        new ArrayList<Node>(), Integer.MAX_VALUE);
                PartitionClass partitionNonAdjacentClass = new PartitionClass(
                        new ArrayList<Node>(), Integer.MAX_VALUE);

                for (Node partitionNode : partition.get(i).getNodes()) {
                    if (splitNeighbors.contains(partitionNode)) {
                        partitionAdjacentClass.addNode(partitionNode);
                    } else {
                        partitionNonAdjacentClass.addNode(partitionNode);
                    }
                }

                int oldLastUsed = partition.get(i).getLastUsed();
                partitionNonAdjacentClass.setLastUsed(oldLastUsed);
                partitionAdjacentClass.setLastUsed(oldLastUsed);

                if (ownClass > i) {
                    if (partitionNonAdjacentClass.getNodes().isEmpty()
                            || partitionAdjacentClass.getNodes().isEmpty()) {
                        newPartition.add(partition.get(i));
                    } else {
                        newPartition.add(partitionAdjacentClass);
                        newPartition.add(partitionNonAdjacentClass);
                    }
                } else {
                    if (partitionNonAdjacentClass.getNodes().isEmpty()
                            || partitionAdjacentClass.getNodes().isEmpty()) {
                        newPartition.add(partition.get(i));
                    } else {
                        newPartition.add(partitionNonAdjacentClass);
                        newPartition.add(partitionAdjacentClass);
                    }
                }
            } else {
                newPartition.add(partition.get(i));
            }
        }

        return newPartition;
    }

    /**
     * Transforms a given list of partitionClasses into a List of nodes to
     * represent the transitive orientation order.
     * 
     * @param partitionClassList
     *            List of partition classes that is to be formed into a list of
     *            their nodes.
     * @return A list of nodes, if every partition class contains only one node,
     *         as it is supposed to be, or null, if the transitive orientation
     *         went wrong.
     */
    private static List<Node> transformPartitionClassIntoListOfNodes(
            List<PartitionClass> partitionClassList) {
        List<Node> transformedList = new ArrayList<Node>();

        for (PartitionClass partitionClass : partitionClassList) {
            List<Node> partitionClassNodes = partitionClass.getNodes();
            if (partitionClassNodes.size() == 1) {
                transformedList.add(partitionClass.getNodes().get(0));
            } else {
                return null;
            }
        }

        return transformedList;
    }

    /**
     * Method transitively orients the given graph. To do this, it starts to
     * compute the ModularDecompositionTree of the graph. It then orients the
     * root of the tree, which is a module. To transitively orient a module, you
     * have to transitively orient the children of this module. If the given
     * module is labeled prime, then use the vertexPartition method. If it is
     * labeled series, then the underlying subgraph is complete, so any linear
     * order of the nodes that are to be transitively ordered is accepted. If
     * it's a parallel node, then the nodes are not connected at all, which is a
     * transitive order as well.
     * 
     * @param graph
     *            The graph that is to be transitively oriented.
     */
    public static void transitivelyOrientGraph(Graph graph) {
        ModularDecompositionTree tree = null;
        try {
            tree = new ModularDecompositionTree(graph,
                    graph.getNodes());
        } catch (ModularDecompositionNodeException e) {
            // TODO
        } catch (RestrictFailedExcpetion e) {
            // TODO
        }
        
        if(tree != null) {
            // Transitively orient the root of the ModularDecompositionTree, which will orient the whole graph
            transitivelyOrientModule(tree.getRoot(), graph);            
        }

    }

    /**
     * Method transitively orients the given module and the edges in the graph
     * that correspond to that module. In order to transitively orient a module,
     * you have to orient all its children first and the orient the modules
     * edges according to its labeling.
     * 
     * @param module
     *            The module that is to be transitively oriented.
     * @param graph
     *            The underlying graph that is to be oriented transitively.
     */
    private static void transitivelyOrientModule(
            ModularDecompositionNode module, Graph graph) {
        if (module != null && graph != null) {
            if (module.getNodes().size() == 1) {
                // Module is a singleton set
                return;
            } else {
                for (ModularDecompositionNode child : module.getChildren()) {
                    transitivelyOrientModule(child, graph);
                }
            }

            if (module.getType() == ModularDecompositionNode.Type.PRIME) {
                // Module is prime, so the children have to be oriented
                // according to the vertexPartition
                List<Integer> childOrderedList = calculateModulesOrder(module);

                // Direct the edges from preceding to following classes of the
                // orderedList
                for (int i = 0; i < childOrderedList.size(); i++) {
                    for (int j = i + 1; j < childOrderedList.size(); j++) {
                        int childOrderOne = childOrderedList.get(i);
                        int childOrderTwo = childOrderedList.get(j);
                        
                        Node mdNodeOne = module.getChildren().get(childOrderOne).getNodes().get(0);
                        Node mdNodeTwo = module.getChildren().get(childOrderTwo).getNodes().get(0);
                        
                        if (mdNodeOne.getNeighbors().contains(mdNodeTwo)) {
                            for (Node node1 : module.getChildren()
                                    .get(childOrderOne).getNodes()) {
                                for (Node node2 : module.getChildren()
                                        .get(childOrderTwo)
                                        .getNodes()) {
                                    for (Edge edge : graph.getEdges(node1,
                                            node2)) {
                                        edge.setDirected(true);
                                        edge.setSource(node1);
                                        edge.setTarget(node2);
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (module.getType() == ModularDecompositionNode.Type.SERIES) {
                // Module is labeled series, so any total ordering of the
                // children nodes will do the transitive orientation
                for (int i = 0; i < module.getChildren().size(); i++) {
                    for (int j = i + 1; j < module.getChildren().size(); j++) {
                        // Orient the edges from lower children to higher ones                       
                        for (Node node : module.getChildren().get(i)
                                .getNodes()) {
                            for (Node node2 : module.getChildren().get(j)
                                    .getNodes()) {
                                for (Edge edge : graph
                                        .getEdges(node, node2)) {
                                    edge.setSource(node);
                                    edge.setTarget(node2);
                                    edge.setDirected(true);
                                }
                            }
                        }
                        
                        
                    }
                }
            }

        }
    }

    /**
     * Method builds up an ordering of the children of the given
     * ModularDecompositionNode according to the vertexPartiton algorithm in
     * order to transitively orient the children of the
     * ModularDecompositionNode, hence the ModularDecompositionNode itself and
     * the nodes of the underlying graph it corresponds to.
     * 
     * @param mdNode
     *            The ModularDecompositionNode whose corresponding nodes are to
     *            be transitively oriented.
     * @return An ordering of the children implying how the children have to be
     *         ordered to get a transitive orientation.
     */
    public static List<Integer> calculateModulesOrder(
            ModularDecompositionNode mdNode) {
        List<Integer> childrenOrder = new ArrayList<Integer>();

        // Build up a subgraph that gets a supernode for every child of the
        // mdNode. Those are then ordered according to the vertexPartition
        // algorithm.
        Graph subGraph = new AdjListGraph();
        Node[] subGraphNodes = new Node[mdNode.getChildren().size()];

        for (int i = 0; i < mdNode.getChildren().size(); i++) {
            subGraphNodes[i] = subGraph.addNode();
        }

        // Determine the edges of the subgraph
        for (int i = 0; i < mdNode.getChildren().size(); i++) {
            for (int j = i + 1; j < mdNode.getChildren().size(); j++) {
                List<Node> firstChildsNodes = mdNode.getChildren().get(i).getNodes();
                List<Node> secondChildsNodes = mdNode.getChildren().get(j).getNodes();
                
                Node firstNode = firstChildsNodes.get(0);
                Node secondNode = secondChildsNodes.get(0);
                
                if (firstNode.getNeighbors().contains(secondNode)) {
                    subGraph.addEdge(subGraphNodes[i], subGraphNodes[j],
                            false);
                }
                
                
            }
        }

        // Order the nodes of the subgraph with vertexPartition
        List<Node> vertexPartitionOrderNodes = vertexSort(subGraph);

        // Build up a list with the indexes of the nodes in the vertex
        // partitioned order
        for (int i = 0; i < vertexPartitionOrderNodes.size(); i++) {
            for (int j = 0; j < subGraphNodes.length; j++) {
                if (vertexPartitionOrderNodes.get(i) == subGraphNodes[j]) {
                    childrenOrder.add(j);
                }
            }
        }

        return childrenOrder;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------