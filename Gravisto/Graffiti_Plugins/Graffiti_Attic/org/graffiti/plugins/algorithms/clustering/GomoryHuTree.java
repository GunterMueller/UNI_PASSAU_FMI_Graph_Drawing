// ==============================================================================
//
//   GomoryHuTree.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GomoryHuTree.java 5772 2010-05-07 18:47:22Z gleissner $

/*
 * Created on 09.06.2004
 */

package org.graffiti.plugins.algorithms.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.util.Queue;

/**
 * This class represents a gomory-hu-tree implemented as a graph object. The
 * construction of the tree needs |V|-1 runs of a max-flow-algorithm and so has
 * a running time of O(|V|^4).
 * 
 * @author Markus K�ser
 * @version $Revision 1.1 $
 */
public class GomoryHuTree {

    /** The path for the storage of the flag determining a node as graph node */
    private static final String IS_GRAPH_NODE = ClusteringSupportAlgorithms.BASE
            + "IsGraphNode";

    /** The path for the storage of the flag determining a node as tree node */
    private static final String IS_TREE_NODE = ClusteringSupportAlgorithms.BASE
            + "IsTreeNode";

    /**
     * The path for the storage of the flag determining a node as temp graph
     * node
     */
    private static final String IS_TEMP_GRAPH_NODE = ClusteringSupportAlgorithms.BASE
            + "IsTempGraphNode";

    /**
     * The path for the storage of the flag determining if a temp node is a copy
     * of a graph node
     */
    private static final String IS_COPY_OF_GRAPH_NODE = ClusteringSupportAlgorithms.BASE
            + "IsCopyOfGraphNode";

    /** The path for the storage of the graph node index */
    private static final String GRAPH_NODE_INDEX = ClusteringSupportAlgorithms.BASE
            + "GraphNodeIndex";

    /** The path for the storage of the tree node index */
    private static final String TREE_NODE_INDEX = ClusteringSupportAlgorithms.BASE
            + "TreeNodeIndex";

    /** The path for the storage of the temp graph node index */
    private static final String TEMP_GRAPH_NODE_INDEX = ClusteringSupportAlgorithms.BASE
            + "TempGraphNodeIndex";

    /**
     * The path for the storage of the number of stored graph nodes at a tree
     * node
     */
    private static final String NUMBER_STORED_GRAPH_NODES = ClusteringSupportAlgorithms.BASE
            + "NumberStoredGraphNodes";

    /** The basis path for the storage of graph nodes at a tree node */
    private static final String BASE_STORED_GRAPH_NODES = ClusteringSupportAlgorithms.BASE
            + "BaseStoredGraphNodes.Nr";

    /** The path for the storage of edge marks */
    private static final String EDGE_MARK = ClusteringSupportAlgorithms.BASE
            + "EdgeMark";

    /** The path for the storage of sub tree root marks */
    private static final String SUBTREE_ROOT_MARK = ClusteringSupportAlgorithms.BASE
            + "SubTreeRootMark";

    /** The path for the storage of subtree numbers */
    private static final String SUBTREE_ROOT_NUMBER = ClusteringSupportAlgorithms.BASE
            + "SubTreeRootNumber";

    /**
     * Tha path for the storage of the index on graph node pointing to the
     * corresponding temp graph node. This temp graph node is either a copy of
     * the graph node or the subtree root, the graph node is contained in.
     */
    private static final String GRAPH_TO_TEMP_GRAPH_MAPPING = ClusteringSupportAlgorithms.BASE
            + "GraphToTempGraphMapping";

    /** Error Message */
    private static final String WRONG_NUMBER_STORED_GRAPH_NODES_ERROR = "Wrong number of graph nodes stored at tree node";

    /** Error Message */
    private static final String GET_GRAPH_NODES_FROM_NON_TREE_NODE_ERROR = "Tried to get graph nodes from a non-tree node";

    /** Error Message */
    private static final String ASK_NON_TEMP_NODE_IF_GRAPH_NODE = "Tried to ask a non-temp node i it is a copy of a graph node";

    /** Error Message */
    private static final String NO_GRAPH_NODE_INDEX = "Node has no GraphNodeIndex";

    /** Error Message */
    private static final String NOT_COPY_OF_GRAPH_NODE_ERROR = "Tried to get a graph node from temp graph node which was not a copy"
            + " of a graph node";

    /** Error Message */
    private static final String NO_MORE_SPLITABLE_TREE_NODES_ERROR = "No more tree nodes with enough graph nodes";

    /** Error Message */
    private static final String NON_TREE_NODE_NR_GRAPH_NODES_ERROR = "Tried to get NumberStoredGraphNodes from a non-Treenode";

    /** Error Message */
    private static final String TEMP_GRAPH_NODE_IS_NO_SUBTREE_ROOT = "Tried to get a tree node from temp graph node which was a copy of a graph node";

    /** Error Message */
    private static final String NO_TEMP_GRAPH_INDEX_ERROR = "Node has no TempGraphNodeIndex";

    /** Error Message */
    private static final String NO_TREE_NODE_INDEX_ERROR = "Node has no TreeNodeIndex";

    /** Error Message */
    private static final String NODE_HAS_NO_TREE_NODE_ERROR = "Tried to get a tree node from an non-graph node";

    /** Error Message */
    private static final String GET_SUBTREES_OF_NON_TREE_NODE_ERROR = "Tried to get subtrees of a non tree node";

    /** Error Message */
    private static final String NO_GRAPH_NODE_AT_TREE_NODE_ERROR = "After the"
            + "construction of the tree no graph node was stored at this "
            + "tree node";

    /** the singleton ClusteringSupportAlgorithms object */
    private ClusteringSupportAlgorithms csa = ClusteringSupportAlgorithms
            .getClusteringSupportAlgorithms();

    /** the singleton FlowNetworkSupportAlgorithms object */
    private FlowNetworkSupportAlgorithms nsa = FlowNetworkSupportAlgorithms
            .getFlowNetworkSupportAlgorithms();

    /** The graph, the gomory hu tree belongs to */
    private Graph graph;

    /**
     * the temp graph, newly generated in every iteration of the construction of
     * the tree
     */
    private Graph tempGraph;

    /**
     * the tree, growing to become a gomory hu tree, represented as a graph
     * object
     */
    private Graph tree;

    /** the flow sink of the temp graph (network) */
    private Node tempNetworkFlowSink;

    /** the flow source of the temp graph (network) */
    private Node tempNetworkFlowSource;

    /** the array of all graph nodes */
    private Node[] graphNodes;

    /**
     * the array of all temp graph nodes. The nodes from index 0 to index
     * lastCopyOfOriginalGraph are all copies of the original graph nodes. The
     * nodes from lastCopyOfOriginalGraph+1 to tempGraphNodes.length are subtree
     * roots
     */
    private Node[] tempGraphNodes;

    /** the array of all tree nodes */
    private Node[] treeNodes;

    /** Flag that stores if the graph was directed or not */
    private boolean graphDirected;

    /**
     * the index of the first tree node with at least two stored graph nodes in
     * the tree nodes array
     */
    private int firstTreeNodeWithTwoGraphNodes;

    /**
     * the index of the last copy of the original graph in the tempGraphNodes
     * array.
     */
    private int lastCopyOfOriginalGraph;

    /** the index of the currently last tree node in the treeNodes array */
    private int lastTreeNode;

    /** the number of nodes of the graph */
    private int numberOfNodes;

    private GomoryHuTree() {
    }

    /**
     * constucts an instance of a gomory-hu-tree for a given undirected graph.
     * 
     * @param graph
     *            an undirected graph
     */
    public GomoryHuTree(Graph graph) {
        initDataStructures(graph);

        Collection sourceComponent = null;
        Collection otherComponent = null;
        Collection convertedSourceComponent = null;
        Collection convertedOtherComponent = null;

        for (int i = 0; i < (numberOfNodes - 1); i++) {
            Node treeNode = getNextSplitableTreeNode();

            shrinkRestOfGraph(treeNode);

            chooseTempSourceAndSink();

            csa.runPreflowPush(tempGraph, tempNetworkFlowSource,
                    tempNetworkFlowSink);

            sourceComponent = csa.residualNetBFS(tempGraph,
                    tempNetworkFlowSource);

            otherComponent = csa.getComplementNodes(tempGraph, sourceComponent); // only

            double cutSize = csa.getCutSize(tempGraph, sourceComponent);

            // contains original graph nodes as well as the subtree roots in
            // this component
            convertedSourceComponent = convertTempToGraphAndSubTreeRoots(sourceComponent);

            convertedOtherComponent = convertTempToGraphAndSubTreeRoots(otherComponent);

            tempGraph = null;

            Node[] convertedSourceComponentArray = (Node[]) convertedSourceComponent
                    .toArray(new Node[0]);
            Node[] convertetOtherComponentArray = (Node[]) convertedOtherComponent
                    .toArray(new Node[0]);

            splitTreeNode(convertedSourceComponentArray,
                    convertetOtherComponentArray, cutSize);

        }

        restoreGraphData();

    }

    /**
     * Returns the array of nodes of the graph.
     * 
     * @return graph-nodes
     */
    public Node[] getGraphNodes() {
        return graphNodes;
    }

    /**
     * Returns the generated gomory-hu-tree in the graph representation.
     * 
     * @return the gomory-hu-tree
     */
    public Graph getTree() {
        return tree;
    }

    /**
     * Returns the array of nodes of the gomory-hu-tree.
     * 
     * @return tree-nodes
     */
    public Node[] getTreeNodes() {
        return treeNodes;
    }

    /**
     * Convertes a given graph node to the corresponding tree node. This method
     * may be called after the construction of the tree is finished.
     * 
     * @param graphNode
     *            the graph node
     * 
     * @return the corresponding tree node
     */
    public Node convertGraphNodeToTreeNode(Node graphNode) {
        return getTreeNode(graphNode);
    }

    /**
     * Converts the given Clusters of the tree to the corresponding clusters of
     * the graph. If not all tree nodes or tree node components are given in the
     * arguments, then their corresponding graph nodes or components will not be
     * returned. This method may be called after the construction of the tree is
     * finished.
     * 
     * @param treeClusters
     *            the clusters of the tree
     * 
     * @return the corresponding clusters of the graph
     */
    public Collection[] convertTreeClustersToGraphClusters(
            Collection[] treeClusters) {
        Collection[] graphClusters = new Collection[treeClusters.length];

        for (int i = 0; i < treeClusters.length; i++) {
            graphClusters[i] = convertTreeComponentToGraphComponent(treeClusters[i]);
        }

        return graphClusters;
    }

    /**
     * Converts a given component of the tree to its corresponding component in
     * the graph. This method may be called after the construction of the tree
     * is finished.
     * 
     * @param treeComponent
     *            the tree component
     * 
     * @return the corresponding graph component
     */
    public Collection convertTreeComponentToGraphComponent(
            Collection treeComponent) {
        LinkedList graphComponent = new LinkedList();

        Node treeNode;

        for (Iterator nodeIt = treeComponent.iterator(); nodeIt.hasNext();) {
            treeNode = (Node) nodeIt.next();
            graphComponent.addLast(convertTreeNodeToGraphNode(treeNode));
        }

        return graphComponent;
    }

    /**
     * Converts a given tree node to the corresponding graph node. This method
     * may be called after the construction of the tree is finished.
     * 
     * @param treeNode
     *            the tree node
     * 
     * @return the corresponding graph node
     * 
     * @throws ClusteringException
     *             if no graph node is stored at this tree node
     */
    public Node convertTreeNodeToGraphNode(Node treeNode) {
        Node graphNode;

        try {
            graphNode = getGraphNodes(treeNode)[0];
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            throw new ClusteringException(NO_GRAPH_NODE_AT_TREE_NODE_ERROR);
        }

        return graphNode;
    }

    /**
     * Removes temprorary data stored at the graph nodes, while constructing the
     * gomory-hu-tree. This data may still be of use after the construction of
     * the tree is finished. So this method must be called from the user of this
     * class.
     */
    public void removeGraphData() {
        Node tempNode;
        for (int i = 0; i < graphNodes.length; i++) {
            tempNode = graphNodes[i];
            try {
                tempNode.removeAttribute(IS_GRAPH_NODE);
            } catch (AttributeNotFoundException anfe) {
            }
            try {
                tempNode.removeAttribute(GRAPH_NODE_INDEX);
            } catch (AttributeNotFoundException anfe) {
            }
            try {
                tempNode.removeAttribute(TREE_NODE_INDEX);
            } catch (AttributeNotFoundException anfe) {
            }
        }
    }

    /**
     * Restores the Graph data.
     */
    public void restoreGraphData() {
        if (graphDirected) {
            graph.setDirected(true);
        }
    }

    /**
     * Returns the graph index stored at a given node (graph nodes, tree nodes
     * and temp graph nodes that are copies of graph nodes)
     * 
     * @param n
     *            the node
     * 
     * @return index the graph node index
     * 
     * @throws ClusteringException
     *             if the node has no graph node index
     */
    private int getGraphIndex(Node n) {
        int index = -1;

        try {
            index = n.getInteger(GRAPH_NODE_INDEX);
        } catch (AttributeNotFoundException anfe) {
            throw new ClusteringException(NO_GRAPH_NODE_INDEX, anfe);
        }

        return index;
    }

    /**
     * Returns the graph nodes stored in a given tree node.
     * 
     * @param treeNode
     *            the tree node
     * 
     * @return resultGraphNodes the array of graph nodes
     * 
     * @throws ClusteringException
     *             if a wrong number of graph nodes is stored or it is tried to
     *             get graph nodes from a non tree node
     */
    Node[] getGraphNodes(Node treeNode) {
        Node[] resultGraphNodes = null;

        if (isTreeNode(treeNode)) {
            int numberStoredNodes = getNumberStoredGraphNodes(treeNode);
            resultGraphNodes = new Node[numberStoredNodes];

            for (int i = 0; i < numberStoredNodes; i++) {
                try {
                    resultGraphNodes[i] = graphNodes[treeNode
                            .getInteger(BASE_STORED_GRAPH_NODES + i)];
                } catch (AttributeNotFoundException anfe) {
                    throw new ClusteringException(
                            WRONG_NUMBER_STORED_GRAPH_NODES_ERROR, anfe);
                }
            }
        } else
            throw new ClusteringException(
                    GET_GRAPH_NODES_FROM_NON_TREE_NODE_ERROR);

        return resultGraphNodes;
    }

    /**
     * Returns the tree index of a given node, be it a tree node, graph node or
     * a temp graph subtree root.
     * 
     * @param n
     *            the node
     * 
     * @return the tree index
     * 
     * @throws ClusteringException
     *             if no tree node index is stored on this node
     */
    private int getTreeIndex(Node n) {
        int index = -1;

        try {
            index = n.getInteger(TREE_NODE_INDEX);
        } catch (AttributeNotFoundException anfe) {
            throw new ClusteringException(NO_TREE_NODE_INDEX_ERROR);
        }

        return index;
    }

    /**
     * checks, if a given temp graph node is a copy of a graph node
     * 
     * @param tempNode
     * 
     * @return isCopy true, if it is a copy of a temp graph node
     * 
     * @throws ClusteringException
     *             if a non temp node is checked if it is a copy of a graph node
     */
    private boolean isCopyOfGraphNode(Node tempNode) {
        boolean isCopy = false;

        if (isTempGraphNode(tempNode)) {
            try {
                isCopy = tempNode.getBoolean(IS_COPY_OF_GRAPH_NODE);
            } catch (AttributeNotFoundException anfe) {
            }
        } else
            throw new ClusteringException(ASK_NON_TEMP_NODE_IF_GRAPH_NODE);

        return isCopy;
    }

    /**
     * Sets to a given graph or tree node the corresponding temp node.
     * 
     * @param node
     *            the graph or tree node
     * @param tempNode
     *            the corresponding temp node
     */
    private void setCorrespondingTempNode(Node node, Node tempNode) {
        removeCorrespondingTempNode(node);
        node.setInteger(GRAPH_TO_TEMP_GRAPH_MAPPING,
                getTempGraphIndex(tempNode));
    }

    /**
     * Returns the tempGraph node corresponding to the graph node or tree node.
     * If a copy of the graph node is part of the temp graph, this node is
     * returned. Otherwise the subtree root that contains the graph node or tree
     * node is returned.
     * 
     * @param node
     *            the node
     * 
     * @return the corresponding temp graph node or null if there is no
     *         corresponding temp graph node.
     */
    private Node getCorrespondingTempNode(Node node) {
        int tempIndex = -1;

        try {
            tempIndex = node.getInteger(GRAPH_TO_TEMP_GRAPH_MAPPING);
        } catch (AttributeNotFoundException anfe) {
            return null;
        }

        return tempGraphNodes[tempIndex];
    }

    /**
     * Checks if a given node is a graph node.
     * 
     * @param n
     *            the node
     * 
     * @return true if the node is a graph node, false otherwise
     */
    private boolean isGraphNode(Node n) {
        boolean is = false;

        try {
            is = n.getBoolean(IS_GRAPH_NODE);
        } catch (AttributeNotFoundException anfe) {
        }

        return is;
    }

    /**
     * Returns the graph node, of which the given temp graph node is th copy.
     * (the graph node index stored at the node is the index of the graph node)
     * 
     * @param copyOfGraphTempNode
     * 
     * @return resultGraphNode the graph node
     * 
     * @throws ClusteringException
     *             if the node is not a copy of a graph node
     */
    private Node getGraphNodeFromTempGraphNode(Node copyOfGraphTempNode) {
        Node resultGraphNode = null;

        if (isCopyOfGraphNode(copyOfGraphTempNode)) {
            resultGraphNode = graphNodes[getGraphIndex(copyOfGraphTempNode)];
        } else
            throw new ClusteringException(NOT_COPY_OF_GRAPH_NODE_ERROR);

        return resultGraphNode;
    }

    /**
     * Checks if a graph edge is marked.
     * 
     * @param graphEdge
     *            the edge
     * 
     * @return true if the edge is marked, false otherwise
     */
    private boolean isMarked(Edge graphEdge) {
        boolean is = false;

        try {
            is = graphEdge.getBoolean(EDGE_MARK);
        } catch (AttributeNotFoundException anfe) {
        }

        return is;
    }

    /**
     * Searches the tree nodes and returns a splittable one.
     * 
     * @return a tree node with at least two stored graph nodes.
     * 
     * @throws ClusteringException
     *             if there are no more splitable tree nodes
     */
    private Node getNextSplitableTreeNode() {
        Node aTreeNode = treeNodes[firstTreeNodeWithTwoGraphNodes];

        try {
            while (getGraphNodes(aTreeNode).length < 2) {
                firstTreeNodeWithTwoGraphNodes++;
                aTreeNode = treeNodes[firstTreeNodeWithTwoGraphNodes];
            }
        } catch (RuntimeException e) {
            throw new ClusteringException(NO_MORE_SPLITABLE_TREE_NODES_ERROR, e);
        }

        return aTreeNode;
    }

    /**
     * Returns the number of graph nodes that is stored in the given tree node
     * 
     * @param treeNode
     *            the tree node
     * 
     * @return the number of stored graph nodes
     * 
     * @throws ClusteringException
     *             if the given node is not a tree node
     */
    private int getNumberStoredGraphNodes(Node treeNode) {
        int number = 0;

        if (isTreeNode(treeNode)) {
            try {
                number = treeNode.getInteger(NUMBER_STORED_GRAPH_NODES);
            } catch (AttributeNotFoundException anfe) {
            }
        } else
            throw new ClusteringException(NON_TREE_NODE_NR_GRAPH_NODES_ERROR);

        return number;
    }

    /**
     * Returns the sub tree root (in the tree), the given <code>
     * tempSubTreeRoot </code>
     * is representing in the temp graph.
     * 
     * @param tempSubTreeRoot
     *            the sub tree root in the temp graph
     * 
     * @return the sub tree root (as a tree node)
     * 
     * @throws ClusteringException
     *             if the given tempGraph node is no subtree root
     */
    private Node getSubTreeRootFromTempGraphNode(Node tempSubTreeRoot) {
        Node resultSubTreeRoot = null;

        if (!isCopyOfGraphNode(tempSubTreeRoot)) {
            resultSubTreeRoot = treeNodes[getTreeIndex(tempSubTreeRoot)];
        } else
            throw new ClusteringException(TEMP_GRAPH_NODE_IS_NO_SUBTREE_ROOT);

        return resultSubTreeRoot;
    }

    /**
     * Searches the tree from the root until a <code>searchedNode</code> is
     * found and returns the index of the subtree in the given <code>
     * subTreeRoots </code>
     * array.
     * 
     * @param root
     *            the root of the tree
     * @param subTreeRoots
     *            the array of subtree roots
     * @param searchedTreeNode
     *            the tree node that is searched for
     * 
     * @return the index of the sub tree in which the <code>searchedNode</code>
     *         is.
     */

    // returns the tree index of the subtree, in which the searched node is
    private int getSubTreeRootIndex(Node root, Node[] subTreeRoots,
            Node searchedTreeNode) {
        int foundSubTreeRootIndex = -1;
        // Start a kind of BFS not directly from root, but from the subTreeRoots
        Queue queue = new Queue();
        root.setBoolean(SUBTREE_ROOT_MARK, true);
        root.setInteger(SUBTREE_ROOT_NUMBER, -1);

        // put subTreeRoots in the queue an mark them
        for (int i = 0; i < subTreeRoots.length; i++) {
            Node subTreeRoot = subTreeRoots[i];
            subTreeRoot.setBoolean(SUBTREE_ROOT_MARK, true);
            subTreeRoot.setInteger(SUBTREE_ROOT_NUMBER, i);
            queue.addLast(subTreeRoot);
        }

        Node tempNode = null;
        Node tempTarget = null;
        int subTreeNumber = -1;

        // normal BFS
        while (!queue.isEmpty()) {
            tempNode = (Node) queue.removeFirst();
            subTreeNumber = tempNode.getInteger(SUBTREE_ROOT_NUMBER);

            // if the searched Node is found, the subTreeRoot of it is returned
            if (tempNode == searchedTreeNode) {
                foundSubTreeRootIndex = subTreeNumber;

                break;
            } else {
                for (Iterator neighbourIt = tempNode.getNeighborsIterator(); neighbourIt
                        .hasNext();) {
                    tempTarget = (Node) neighbourIt.next();

                    // vorsicht bei nicht markiert
                    boolean marked = false;

                    try {
                        marked = tempTarget.getBoolean(SUBTREE_ROOT_MARK);
                    } catch (AttributeNotFoundException anfe) {
                    }

                    if (!marked) {
                        tempTarget.setBoolean(SUBTREE_ROOT_MARK, true);
                        tempTarget.setInteger(SUBTREE_ROOT_NUMBER,
                                subTreeNumber);
                        queue.addLast(tempTarget);
                    }
                }
            }
        }

        for (Iterator nodesIt = tree.getNodesIterator(); nodesIt.hasNext();) {
            tempNode = (Node) nodesIt.next();

            try {
                tempNode.removeAttribute(SUBTREE_ROOT_MARK);
                tempNode.removeAttribute(SUBTREE_ROOT_NUMBER);
            } catch (AttributeNotFoundException anfe) {
            }
        }

        return foundSubTreeRootIndex;
    }

    /**
     * Interprets a given tree node as the root of the tree and returns all its
     * neighbours as its subtree roots.
     * 
     * @param root
     *            the root node
     * 
     * @return subtreeRoots
     * 
     * @throws ClusteringException
     *             if the given root node is not a node of the tree
     */
    private Node[] getSubTrees(Node root) {
        if (isTreeNode(root))
            return (root.getUndirectedNeighbors().toArray(new Node[0]));
        else
            throw new ClusteringException(GET_SUBTREES_OF_NON_TREE_NODE_ERROR);
    }

    /**
     * Returns the temp graph index stored at temp graph nodes
     * 
     * @param n
     *            the node
     * 
     * @return the temp graph index
     * 
     * @throws ClusteringException
     *             if no temp graph indes is stored at the node
     */
    private int getTempGraphIndex(Node n) {
        int index = -1;

        try {
            index = n.getInteger(TEMP_GRAPH_NODE_INDEX);
        } catch (AttributeNotFoundException anfe) {
            throw new ClusteringException(NO_TEMP_GRAPH_INDEX_ERROR);
        }

        return index;
    }

    /**
     * Checks if a given node is a temp graph node.
     * 
     * @param n
     *            the node
     * 
     * @return true, if the node is a temp graph node, false otherwise
     */
    private boolean isTempGraphNode(Node n) {
        boolean is = false;

        try {
            is = n.getBoolean(IS_TEMP_GRAPH_NODE);
        } catch (AttributeNotFoundException anfe) {
        }

        return is;
    }

    /**
     * Returns the tree node, a given graph node is contained in
     * 
     * @param graphNode
     *            the graph node
     * 
     * @return resultTreeNode the tree node
     * 
     * @throws ClusteringException
     *             if a node has no stored tree node
     */
    private Node getTreeNode(Node graphNode) {
        Node resultTreeNode = null;

        if (isGraphNode(graphNode)) {
            resultTreeNode = treeNodes[getTreeIndex(graphNode)];
        } else
            throw new ClusteringException(NODE_HAS_NO_TREE_NODE_ERROR);

        return resultTreeNode;
    }

    /**
     * Checks if a node is a tree node
     * 
     * @param n
     *            the node
     * 
     * @return true, if the node is a tree node, false otherwise
     */
    private boolean isTreeNode(Node n) {
        boolean is = false;

        try {
            is = n.getBoolean(IS_TREE_NODE);
        } catch (AttributeNotFoundException anfe) {
        }

        return is;
    }

    /**
     * Takes an array of subtree roots, disconnects its edges from the <code>
     * oldTreeNode </code>
     * and reconnects it to the <code> newTreeNode
     * </code> with the same edges and capacities.
     * 
     * @param oldTreeNode
     * @param newTreeNode
     * @param changeSubTreeRoots
     */
    private void changeSubTreesToNewTreeNode(Node oldTreeNode,
            Node newTreeNode, Node[] changeSubTreeRoots) {
        Edge changeToNewTreeNodeEdge = null;

        for (int i = 0; i < changeSubTreeRoots.length; i++) {
            Node changeRoot = changeSubTreeRoots[i];
            Collection edges = tree.getEdges(oldTreeNode, changeRoot);
            changeToNewTreeNodeEdge = (Edge) edges.iterator().next();

            if (changeToNewTreeNodeEdge.getSource() == oldTreeNode) {
                changeToNewTreeNodeEdge.setSource(newTreeNode);
            }

            if (changeToNewTreeNodeEdge.getTarget() == oldTreeNode) {
                changeToNewTreeNodeEdge.setTarget(newTreeNode);
            }
        }
    }

    /**
     * Chooses two different temp nodes, which are copies of graph nodes as
     * source and sink of a network-flow problem
     */
    private void chooseTempSourceAndSink() {
        // only choose nodes that are copies of graph nodes
        int sourceTempGraphNodeIndex = csa
                .getRandomPositiveInt(lastCopyOfOriginalGraph + 1);
        int sinkTempGraphNodeIndex = csa
                .getRandomPositiveInt(lastCopyOfOriginalGraph);

        if (sinkTempGraphNodeIndex == sourceTempGraphNodeIndex) {
            sinkTempGraphNodeIndex = lastCopyOfOriginalGraph;
        }

        tempNetworkFlowSource = tempGraphNodes[sourceTempGraphNodeIndex];
        tempNetworkFlowSink = tempGraphNodes[sinkTempGraphNodeIndex];
    }

    /**
     * Converts temp graph nodes back to graph nodes and subtree roots (in the
     * tree), using <code> getGraphNodeFromTempGraphNode </code> and <code>
     * getSubTreeRootFromTempGraphNode </code>
     * 
     * @param tempNodes
     *            the nodes of the temporary graph
     * 
     * @return convertedNodes, a mixed <code> collection </code> with the
     *         converted graph nodes and subtree roots
     */
    private Collection convertTempToGraphAndSubTreeRoots(Collection tempNodes) {
        Collection convertedNodes = new LinkedList();
        Node tempNode;
        Node convert;

        for (Iterator tempIt = tempNodes.iterator(); tempIt.hasNext();) {
            tempNode = (Node) tempIt.next();

            if (isCopyOfGraphNode(tempNode)) {
                convert = getGraphNodeFromTempGraphNode(tempNode);
            } else {
                convert = getSubTreeRootFromTempGraphNode(tempNode);
            }

            convertedNodes.add(convert);
        }

        return convertedNodes;
    }

    /**
     * Creates a new tree node and a tree edge between the given old tree node.
     * 
     * @param oldTreeNode
     *            the old tree node
     * @param capacity
     *            the capacity of the new edge
     * 
     * @return the new tree node
     */
    private Node createTreeNodeAndEdgeBetween(Node oldTreeNode, double capacity) {
        // create treenode and init data
        Node newTreeNode = tree.addNode();
        lastTreeNode++;
        treeNodes[lastTreeNode] = newTreeNode;
        newTreeNode.setBoolean(IS_TREE_NODE, true);
        newTreeNode.setInteger(TREE_NODE_INDEX, lastTreeNode);

        // create new edge between old and new tree nodes with capacity
        // newEdgeCapacity
        Edge edge = tree.addEdge(oldTreeNode, newTreeNode, false);
        nsa.setCapacity(edge, capacity);

        return newTreeNode;
    }

    /**
     * Filters out graph nodes from an array with mixed nodes and places them in
     * a new array.
     * 
     * @param mixedNodes
     *            the array
     * 
     * @return an array with all graph nodes filtered from <code> mixedNodes
     *         </code>
     */
    private Node[] filterOutGraphNodes(Node[] mixedNodes) {
        ArrayList gNodes = new ArrayList();
        Node tempNode;

        for (int i = 0; i < mixedNodes.length; i++) {
            tempNode = mixedNodes[i];

            if (isGraphNode(tempNode)) {
                gNodes.add(tempNode);
            }
        }

        return (Node[]) gNodes.toArray(new Node[0]);
    }

    /**
     * Filters out tree nodes from an array with mixed nodes and places them in
     * a new array.
     * 
     * @param mixedNodes
     *            the array
     * 
     * @return an array with all tree nodes filtered from <code> mixedNodes
     *         </code>
     */
    private Node[] filterOutTreeNodes(Node[] mixedNodes) {
        ArrayList trNodes = new ArrayList();
        Node tempNode;

        for (int i = 0; i < mixedNodes.length; i++) {
            tempNode = mixedNodes[i];

            if (isTreeNode(tempNode)) {
                trNodes.add(tempNode);
            }
        }

        return (Node[]) trNodes.toArray(new Node[0]);
    }

    /**
     * Generates the edges of the tempGraph. Edges between nodes in the root
     * will be copied. Edges from a node in the root to a node in another
     * subtree will be transformed to an edge from the tempNode to the
     * subtreeRoot. Edges from one subtree to another subtree will be
     * transformed to edges form one subtree root to the other. The only edges
     * ignored by this generation are loops in the graph and edges with both
     * endpoints in one subtree.
     * 
     * @param root
     *            the root of the tree
     * @param gNodes
     *            the graph nodes in the root
     * @param subTreeRoots
     *            the subtrees of the tree all connected with the root
     * @param temphNodes
     *            the array of temp graph nodes already created
     */
    private void generateTempGraphEdges(Node root, Node[] gNodes,
            Node[] subTreeRoots, Node[] temphNodes) {
        // copy the Edges of the original Graph and create edges for the
        // subtrees

        Edge graphEdge;
        double cap;
        Node source;
        Node target;
        Node tempSource;
        Node tempTarget;
        Edge newEdge;

        for (Iterator edgeIt = graph.getEdgesIterator(); edgeIt.hasNext();) {
            graphEdge = (Edge) edgeIt.next();
            cap = nsa.getCapacity(graphEdge);

            // the two endpoints of the edge
            source = graphEdge.getSource();
            target = graphEdge.getTarget();

            // the corresponding temp nodes to this endpoints
            tempSource = getCorrespondingTempNode(source);
            tempTarget = getCorrespondingTempNode(target);

            /*
             * if both nodes are copies of graph nodes, not equal => generate
             * edge if both nodes are copies of graph nodes and equals => it was
             * a loop => do not generate edge if one node is a copy of a graph
             * node and not the other => generate edge if both nodes are
             * different subtree roots => generate edge if both nodes are equal
             * subtrees => do not generate edge ==>
             * 
             * if the two nodes are not equal => generate edge otherwise do
             * nothing
             */
            if (tempSource != tempTarget) {
                newEdge = tempGraph.addEdge(tempSource, tempTarget, false);
                nsa.setCapacity(newEdge, cap);
            }
        }
    }

    /**
     * Generates the nodes of the tempGraph. The given graph nodes are copied
     * 1:1 and for each given subtree-root a single node is created. An index
     * will be stored at each graph node. If a copy of the graph node is
     * contained in the tempGraph, the index points to this node. Otherwise the
     * index points to the compressed subtree-root that contains the graph node.
     * 
     * @param treeRoot
     *            the root of the tree
     * @param graphNodesInTreeNode
     *            the graph nodes to be copied
     * @param subTreeRoots
     *            the subtree roots of the tree
     */
    private void generateTempGraphNodes(Node treeRoot,
            Node[] graphNodesInTreeNode, Node[] subTreeRoots) {
        int numberOfGraphNodes = graphNodesInTreeNode.length;
        int numberOfSubTrees = subTreeRoots.length;
        lastCopyOfOriginalGraph = numberOfGraphNodes - 1;

        // number of tempGraph nodes = number of graph nodes in tree node +
        // number of subtrees of tree node
        int numberOfTotalTempNodes = numberOfGraphNodes + numberOfSubTrees;
        tempGraphNodes = new Node[numberOfTotalTempNodes];

        Node graphNode;
        Node treeNode;
        Node tempNode;

        // first create copies of the graph nodes
        for (int i = 0; i < numberOfGraphNodes; i++) {
            // tempGraphNodes[i] --stands for-- graphNodesInTreeNode[i]
            graphNode = graphNodesInTreeNode[i];
            tempNode = tempGraph.addNode();
            tempGraphNodes[i] = tempNode;

            // information of the temp graph node
            tempNode.setBoolean(IS_TEMP_GRAPH_NODE, true);
            tempNode.setBoolean(IS_COPY_OF_GRAPH_NODE, true);
            tempNode.setInteger(TEMP_GRAPH_NODE_INDEX, i);

            // information about the graph node, the tempnode stands for
            tempNode.setInteger(GRAPH_NODE_INDEX, getGraphIndex(graphNode));

            // store at the graph node the corresponding tempGraph node
            setCorrespondingTempNode(graphNode, tempNode);
        }

        // second create nodes for each subtree
        for (int i = numberOfGraphNodes; i < numberOfTotalTempNodes; i++) {
            // tempGraphNodes[numberOfGraphNodes + i] --stands for--
            // subTreeRoots[i]
            treeNode = subTreeRoots[i - numberOfGraphNodes];
            tempNode = tempGraph.addNode();
            tempGraphNodes[i] = tempNode;

            // information of the temp graph node
            tempNode.setBoolean(IS_TEMP_GRAPH_NODE, true);
            tempNode.setInteger(TEMP_GRAPH_NODE_INDEX, i);

            // information about which subtree this tempnode stands for
            tempNode.setInteger(TREE_NODE_INDEX, getTreeIndex(treeNode));

            // store at the subtree roots the corresponding tempGraph node
            setCorrespondingTempNode(treeNode, tempNode);
        }

        // Third, iterate over all other graph nodes and store at them the
        // subtree root, they are located in
        for (int i = 0; i < graphNodes.length; i++) {
            graphNode = graphNodes[i];

            if (getCorrespondingTempNode(graphNode) == null) {
                Node tNode = getTreeNode(graphNode);
                // get the subtree root, this graph node ist stored in
                int subTreeRootIndex = getSubTreeRootIndex(treeRoot,
                        subTreeRoots, tNode);
                Node subTreeRoot = subTreeRoots[subTreeRootIndex];

                // then get the corresponding temp graph node
                Node correspTempNode = getCorrespondingTempNode(subTreeRoot);

                // store this temp graph node at the graph node
                setCorrespondingTempNode(graphNode, correspTempNode);
            }
        }
    }

    /**
     * initializes the datastructures for the construction of the tree.
     * 
     * @param owner
     *            the graph this tree belongs to.
     */
    private void initDataStructures(Graph owner) {
        // init graph
        graph = owner;
        numberOfNodes = graph.getNumberOfNodes();
        graphNodes = new Node[numberOfNodes];
        graphNodes = graph.getNodes().toArray(graphNodes);
        graphDirected = graph.isDirected();

        if (graphDirected) {
            graph.setDirected(false);
        }

        // init trivial Tree
        tree = new AdjListGraph();
        tree.setDirected(false);
        treeNodes = new Node[numberOfNodes];
        treeNodes[0] = tree.addNode(); // rest = null
        lastTreeNode = 0;
        firstTreeNodeWithTwoGraphNodes = 0;

        // init Information at the Graph nodes
        for (int i = 0; i < numberOfNodes; i++) {
            Node tempGraphNode = graphNodes[i];

            // store graph node indizes on the Nodes
            tempGraphNode.setBoolean(IS_GRAPH_NODE, true);
            tempGraphNode.setInteger(GRAPH_NODE_INDEX, i);

            // All graph nodes contained in Tree Node with index 0
            tempGraphNode.setInteger(TREE_NODE_INDEX, 0);
        }

        // init information at the single tree node
        Node tempTreeNode = treeNodes[0];
        tempTreeNode.setBoolean(IS_TREE_NODE, true);
        tempTreeNode.setInteger(TREE_NODE_INDEX, 0);
        tempTreeNode.setInteger(NUMBER_STORED_GRAPH_NODES, numberOfNodes);

        for (int i = 0; i < numberOfNodes; i++) {
            tempTreeNode.setInteger(BASE_STORED_GRAPH_NODES + i, i);
        }

        tempGraph = null;
        tempGraphNodes = null;
        tempNetworkFlowSource = null;
        tempNetworkFlowSink = null;
    }

    /**
     * Marks an edge.
     * 
     * @param graphEdge
     *            the edge to be marked
     */
    private void mark(Edge graphEdge) {
        graphEdge.setBoolean(EDGE_MARK, true);
    }

    /**
     * Removes the data of corresponding temp nodes from the given graph or tree
     * node.
     * 
     * @param node
     *            the graph or tree node
     */
    private void removeCorrespondingTempNode(Node node) {
        try {
            node.removeAttribute(GRAPH_TO_TEMP_GRAPH_MAPPING);
        } catch (AttributeNotFoundException anfe) {
        }
    }

    /**
     * Removes the corresponding temp nodes from all graph and tree nodes.
     */
    private void removeCorrespondingTempNode() {
        Node node;
        for (Iterator graphIt = graph.getNodesIterator(); graphIt.hasNext();) {
            node = (Node) graphIt.next();
            removeCorrespondingTempNode(node);
        }
        for (Iterator treeIt = tree.getNodesIterator(); treeIt.hasNext();) {
            node = (Node) treeIt.next();
            removeCorrespondingTempNode(node);
        }
    }

    /**
     * Removes all edge marks from the edges incident to the nodes in the given
     * array.
     * 
     * @param nodes
     *            the node array
     */
    private void removeEdgeMarks(Node[] nodes) {
        for (int i = 0; i < nodes.length; i++) {
            Node temp = nodes[i];

            for (Iterator edgesIt = temp.getUndirectedEdgesIterator(); edgesIt
                    .hasNext();) {
                Edge tempEdge = (Edge) edgesIt.next();

                try {
                    tempEdge.removeAttribute(EDGE_MARK);
                } catch (AttributeNotFoundException anfe) {
                }
            }
        }
    }

    /**
     * Creates a temporary graph for further computations. The subgraph stored
     * in the given tree node is copied into the tempgraph. All subtrees in the
     * tree with root <code> treeNode </code> are shrunken to single temp graph
     * nodes. All edges from graph nodes in <code> treeNode </code> to nodes
     * outside it are set to the corresponding tree nodes in the tempgraph.
     * Multiple edges are unified with the sum of the capacities
     * 
     * @param treeNode
     *            a tree node with at least two stored graph nodes
     */
    private void shrinkRestOfGraph(Node treeNode) {
        // init temp data
        tempGraph = new AdjListGraph();

        Node[] graphNodesInTreeNode = getGraphNodes(treeNode);
        Node[] subTreeRoots = getSubTrees(treeNode);

        // creates the nodes of the temp graph
        generateTempGraphNodes(treeNode, graphNodesInTreeNode, subTreeRoots);

        // creates the edges of the temp graph
        generateTempGraphEdges(treeNode, graphNodesInTreeNode, subTreeRoots,
                tempGraphNodes);

        removeCorrespondingTempNode();
        removeEdgeMarks(graphNodes);
    }

    /**
     * Splits an tree node and creates a second node the <code>changeNodes
     * </code> are moved from
     * its old tree node to the new node the edges from the old subtree must be
     * placed correctly. An Edge is created between the old node and the new one
     * with the given capacity.
     * 
     * @param changeNodes
     *            nodes that are moved to the new tree node
     * @param stayNodes
     *            nodes that stay in the old tree node
     * @param newEdgeCapacity
     *            the capacity of the new tree edge between the old and new tree
     *            nodes
     * 
     */
    private void splitTreeNode(Node[] changeNodes, Node[] stayNodes,
            double newEdgeCapacity) {
        // dividing up stay nodes
        Node[] stayGraphNodes = filterOutGraphNodes(stayNodes);
        Node[] staySubTreeRoots = filterOutTreeNodes(stayNodes);

        // dividing up change nodes
        Node[] changeGraphNodes = filterOutGraphNodes(changeNodes);
        Node[] changeSubTreeRoots = filterOutTreeNodes(changeNodes);

        // old tree node and new tree node
        Node oldTreeNode = getTreeNode(changeNodes[0]);
        Node newTreeNode = createTreeNodeAndEdgeBetween(oldTreeNode,
                newEdgeCapacity);

        Edge newEdge = (tree.getEdges(oldTreeNode, newTreeNode)).iterator()
                .next();

        //
        changeSubTreesToNewTreeNode(oldTreeNode, newTreeNode,
                changeSubTreeRoots);

        // delete old Information on old tree node
        int numberOfOldStoredNodes = getNumberStoredGraphNodes(oldTreeNode);

        for (int i = 0; i < numberOfOldStoredNodes; i++) {
            oldTreeNode.removeAttribute(BASE_STORED_GRAPH_NODES + i);
        }

        // now put the stayNodes in the old tree node and
        // the changeNodes in the new tree node
        // write new Information on old tree node
        for (int i = 0; i < stayGraphNodes.length; i++) {
            Node graphNode = stayGraphNodes[i];
            oldTreeNode.setInteger((BASE_STORED_GRAPH_NODES + i),
                    getGraphIndex(graphNode));

            // the stayGraphNodes already habe oldTreeNode stored at them
        }

        // write new number of stored nodes on old tree node
        oldTreeNode
                .setInteger(NUMBER_STORED_GRAPH_NODES, stayGraphNodes.length);

        // write new Information on new tree node
        for (int i = 0; i < changeGraphNodes.length; i++) {
            Node changeNode = changeGraphNodes[i];

            // write the new tree index at the graph nodes
            changeNode.setInteger(TREE_NODE_INDEX, getTreeIndex(newTreeNode));

            // write the new graph node indizes at the new tree node
            newTreeNode.setInteger((BASE_STORED_GRAPH_NODES + i),
                    getGraphIndex(changeNode));
        }

        // write new number of stored nodes on new tree node
        newTreeNode.setInteger(NUMBER_STORED_GRAPH_NODES,
                changeGraphNodes.length);
    }

    /**
     * Swaps two tree nodes in the <code> treeNodes </code> array
     * 
     * @param index1
     *            index of first tree node
     * @param index2
     *            index of second tree node
     */
    private void swapTreeNodes(int index1, int index2) {
        Node temp = treeNodes[index1];
        treeNodes[index1] = treeNodes[index2];
        treeNodes[index2] = temp;

        // change tree node index to the new values
        treeNodes[index1].setInteger(TREE_NODE_INDEX, index1);
        treeNodes[index2].setInteger(TREE_NODE_INDEX, index2);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
