package org.graffiti.plugins.algorithms.circulardrawing;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Stack;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.AlgorithmResult;
import org.graffiti.plugin.algorithm.DefaultAlgorithmResult;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.selection.Selection;
import org.graffiti.util.Queue;

/**
 * An modified implementation of the DFS algorithm. The algorithm return a
 * longets path in a graph.
 * 
 * @author demirci Created on Jan 21, 2005
 */
public class LongestPath extends AbstractAlgorithm {

    /** Source node */
    private Node startNode = null;

    /** DOCUMENT ME! */
    private Selection selection;

    /** List of nodes in the longest path */
    private List longestPathNodes = new ArrayList();

    /** @see org.graffiti.plugins.algorithms.circulardrawing.CircularConst */
    private CircularConst circularConst = new CircularConst();

    /** Table of the node with some level in the tree */
    private Hashtable levels = new Hashtable();

    /** Number of the nodes in the maximum level of the dfs treee */
    private int maxLevelSize;

    /** List of the dfs tree edges */
    List treeEdges = new ArrayList();

    /** The map after the permutation of the adjacency lists of the nodes */
    Map nodeParmutedNeighbors = new HashMap();

    /** Pointer of the parent node in the dfs tree of a node */
    Map nodeParentMap = new HashMap();

    /** The map of the childs in the dfs tree of a node */
    Map nodeChildMap = new HashMap();

    /** Konstruktur */
    public LongestPath() {
    }

    /**
     * Konstruktur
     * 
     * @param gr
     */
    public LongestPath(Graph gr) {
        this.graph = gr;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "LongestPath";
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        selection = ((SelectionParameter) params[0]).getSelection();
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        SelectionParameter selParam = new SelectionParameter("Start node",
                "DFS will start with the only selected node.");

        return new Parameter[] { selParam };
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        if (graph.getNumberOfNodes() <= 0)
            throw new PreconditionException(
                    "The graph is empty. Can't run DFS.");

        if ((selection == null) || (selection.getNodes().size() != 1))
            throw new PreconditionException(
                    "DFS needs exactly one selected node.");

        startNode = selection.getNodes().get(0);
    }

    /**
     * @see org.graffiti.plugin.algorithm.CalculatingAlgorithm#getResult()
     */
    public AlgorithmResult getResult() {
        AlgorithmResult aresult = new DefaultAlgorithmResult();
        aresult.addToResult("longestPath", this.longestPathNodes);
        aresult.addToResult("nodeParents", this.nodeParentMap);
        aresult.addToResult("levels", this.levels);
        aresult.addToResult("maxLevel", new Integer(this.maxLevelSize));
        aresult.addToResult("treeEdges", treeEdges);
        return aresult;
    }

    /**
     * Set the source node for dfs to start with.
     * 
     * @param n
     *            Node to be labeled
     */
    public void setSourceNode(Node n) {
        startNode = n;
    }

    /**
     * @param col
     *            The collection will converted to the list
     * @return converted list
     */
    private List collectionToList(Collection col) {
        List list = new LinkedList();
        Iterator it = col.iterator();
        while (it.hasNext()) {
            list.add(it.next());
        }
        return list;
    }

    /**
     * @param actualNode
     * @return Node a unmarked neighbour of the actual processed node. if is'nt
     *         exist, return null.
     * @see org.graffiti.graph.Node
     */
    private Node findUnmarkedNeighbor(Node actualNode) {
        Collection neighbors = new ArrayList();
        if (circularConst.getPermutation() == 1) {
            neighbors = ((Collection) nodeParmutedNeighbors.get(actualNode));
        }

        else {
            neighbors = actualNode.getNeighbors();
        }
        Iterator neighborsIt = neighbors.iterator();

        Node unmarkedNeighbor = null;
        while (neighborsIt.hasNext()) {
            Node neighbor = (Node) neighborsIt.next();
            if (!neighbor.getBoolean("dfsParam.marked")) {
                unmarkedNeighbor = neighbor;
                break;
            }
        }
        return unmarkedNeighbor;
    }

    /**
     * @param g
     * @see org.graffiti.graph.Graph
     * @return the map of the nodes and its neighbors in the graph g.
     */
    private Map nodeNeighbors(Graph g) {
        Map m = new HashMap();

        Iterator nodes = g.getNodesIterator();
        while (nodes.hasNext()) {
            Node node = (Node) nodes.next();
            Collection neighbors = new LinkedList();
            Collection permutated = permutateNodeNeighbors(node);
            m.put(node, permutated);
        }
        return m;
    }

    /**
     * @param node
     * @return collection
     */
    private Collection permutateNodeNeighbors(Node node) {

        Collection permutatedNeighbors = new LinkedList();
        Collection neighbors = node.getNeighbors();
        List neighborList = collectionToList(neighbors);

        int length = neighborList.size();
        for (int i = 0; i < length; i++) {
            int size = neighborList.size();
            double zufall = Math.random();
            Float pos = new Float((size - 1) * zufall);
            int position = Math.round(pos.floatValue());
            Node n = (Node) neighborList.remove(position);
            permutatedNeighbors.add(n);
        }

        return permutatedNeighbors;
    }

    /**
     * @param s
     *            source node.
     * @see org.graffiti.graph.Node
     * @return a hash table, the keys are levels and the values are a list of
     *         all nodes which in the level occurrences
     */
    private Hashtable bfs(Node s) {

        System.out.println("bfs will be perrformed!");
        Queue q = new Queue();
        // d contains a mapping from node to an integer, the bfsnum
        Map d = new HashMap();
        Hashtable levelTable = new Hashtable();
        q.addLast(s);
        d.put(s, new Integer(0));
        s.setInteger("level", 0);
        while (!q.isEmpty()) {
            Node v = (Node) q.removeFirst();
            List levelList = (List) levelTable.remove(new Integer(v
                    .getInteger("level")));
            if (levelList == null) {
                levelList = new ArrayList();
                levelList.add(v);
                levelTable.put(new Integer(v.getInteger("level")), levelList);
            } else {
                levelList.add(v);
                levelTable.put(new Integer(v.getInteger("level")), levelList);
            }
            Collection neighbors = new ArrayList();
            if (circularConst.getAlgorithm(3) == "1"
                    && circularConst.getPermutation() == 1) {
                Collection neigh = ((Collection) nodeParmutedNeighbors.get(v));
                Iterator it = neigh.iterator();
                while (it.hasNext()) {
                    Node n = (Node) it.next();
                    Edge e = graph.getEdges(v, n).iterator().next();
                    if (treeEdges.contains(e)) {
                        neighbors.add(n);
                    }
                }
            } else {
                Collection neigh = v.getNeighbors();
                Iterator it = neigh.iterator();
                while (it.hasNext()) {
                    Node n = (Node) it.next();
                    Edge e = graph.getEdges(v, n).iterator().next();
                    if (treeEdges.contains(e)) {
                        neighbors.add(n);
                    }
                }
            }
            Iterator neighborsIt = neighbors.iterator();
            for (; neighborsIt.hasNext();) {
                Node neighbor = (Node) neighborsIt.next();
                if (!d.containsKey(neighbor)) {
                    Integer bfsNum = new Integer(((Integer) d.get(v))
                            .intValue() + 1);
                    d.put(neighbor, bfsNum);
                    neighbor.setInteger("level", v.getInteger("level") + 1);
                    q.addLast(neighbor);
                }
            }
        }
        return levelTable;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {

        if (startNode == null)
            throw new RuntimeException("Must call method \"check\" before "
                    + " calling \"execute\".");

        nodeParmutedNeighbors = nodeNeighbors(graph);
        Map markedMap = new HashMap();
        // Stack for ModifiedDFS algorithm.
        Stack stack = new Stack();
        // Stor DFS tree nodes and its neighbors.
        Map dfsTree = new HashMap();
        // The node in DFS tree which has the two most distant descandants.
        int maxWidthNodeWidth = 0;
        Node maxWidthNode = null;
        Map longestPathNodesMap = new HashMap();
        Map longestPathNodesMap2 = new HashMap();
        List backEdges = new ArrayList();

        graph.getListenerManager().transactionStarted(this);
        // do mark all nodes than not visited and initilize the parameters.
        Iterator nodeIt = graph.getNodesIterator();
        for (int i = 1; nodeIt.hasNext(); i++) {
            Node node = (Node) nodeIt.next();
            node.setBoolean("dfsParam.marked", false);
            node.setInteger("dfsParam.height", 0);
            node.setInteger("dfsParam.width", 0);
            node.setInteger("dfsParam.dfsNum", 0);
            node.setInteger("dfsParam.compNum", 0);
            node.setInteger("node.id", i);
            Node mostDistantDescandantChild = null;
            Node secondDistantDescandantChild = null;
            longestPathNodesMap.put(node, mostDistantDescandantChild);
            longestPathNodesMap2.put(node, secondDistantDescandantChild);
            Collection nodeDfsTreeNeighbors = new LinkedList();
            dfsTree.put(node, nodeDfsTreeNeighbors);
            node.setInteger("longestPath.position", -1);
        }

        // to assign dfs nummber of the source node and marked it than visited.
        startNode.setInteger("dfsParam.dfsNum", 1);
        startNode.setBoolean("dfsParam.marked", true);
        stack.push(startNode);
        // set the compnum to 0.
        int compNum = 0;

        for (int dfsNum = 1; !stack.empty();) {
            Node nextNode = (Node) stack.pop();
            // to assign dfs nummber of the next node and marked it than
            // visited.
            Node unmarkedNeighbor = findUnmarkedNeighbor(nextNode);
            if (unmarkedNeighbor != null) {
                dfsNum++;
                unmarkedNeighbor.setBoolean("dfsParam.marked", true);
                unmarkedNeighbor.setInteger("dfsParam.dfsNum", dfsNum);
                stack.push(nextNode);
                stack.push(unmarkedNeighbor);

                Collection col = (Collection) dfsTree.remove(nextNode);
                col.add(unmarkedNeighbor);
                dfsTree.put(nextNode, col);

                try {
                    Edge dfsTreeEdge = null;
                    Iterator it = graph.getEdges(nextNode, unmarkedNeighbor)
                            .iterator();
                    if (it.hasNext()) {
                        dfsTreeEdge = (Edge) it.next();
                    } else {
                        it = graph.getEdges(unmarkedNeighbor, nextNode)
                                .iterator();
                        dfsTreeEdge = (Edge) it.next();
                    }

                    treeEdges.add(dfsTreeEdge);
                    dfsTreeEdge.setBoolean("tree.edge", true);
                } catch (NoSuchElementException e) {
                    System.out
                            .println("ModifiedDFS: Die Kante existiert nicht: "
                                    + e.getMessage());
                }
            }

            // to assign complition nummber of the nodes
            else {
                compNum++;
                nextNode.setInteger("dfsParam.compNum", compNum);
                int nextNodewidth = nextNode.getInteger("dfsParam.width");

                if (maxWidthNodeWidth < nextNodewidth) {
                    maxWidthNode = nextNode;
                    maxWidthNodeWidth = nextNodewidth;
                }

                if (!stack.empty()) {

                    Node stackTopNode = (Node) stack.peek();
                    nodeParentMap.put(nextNode, stackTopNode);

                    int stackTopNodeHeight = stackTopNode
                            .getInteger("dfsParam.height");
                    int nextNodeHeight = nextNode.getInteger("dfsParam.height");
                    int stackTopNodeWidth = stackTopNode
                            .getInteger("dfsParam.width");

                    // to assign the heights and widths of the nodes
                    if (stackTopNodeHeight <= nextNodeHeight) {
                        stackTopNode.setInteger("dfsParam.height",
                                nextNodeHeight + 1);
                        stackTopNode.setInteger("dfsParam.width",
                                stackTopNodeHeight + nextNodeHeight + 1);

                        Node secondNode = (Node) longestPathNodesMap
                                .remove(stackTopNode);
                        longestPathNodesMap.put(stackTopNode, nextNode);
                        longestPathNodesMap2.remove(stackTopNode);
                        longestPathNodesMap2.put(stackTopNode, secondNode);
                    }

                    else if (stackTopNodeHeight + nextNodeHeight + 1 >= stackTopNodeWidth) {
                        stackTopNode.setInteger("dfsParam.width",
                                stackTopNodeHeight + nextNodeHeight + 1);
                        longestPathNodesMap2.remove(stackTopNode);
                        longestPathNodesMap2.put(stackTopNode, nextNode);
                    }
                }
            }
        }

        CircularLayout layout = new CircularLayout();

        longestPathNodes.add(maxWidthNode);
        if (circularConst.getDfsTree() == 1) {
            layout.setNodeColor(maxWidthNode, Color.GREEN);
            layout.setNodeLabel(maxWidthNode, new Integer(maxWidthNode
                    .getInteger("dfsParam.dfsNum")).toString());
        }
        // The first and second child of the maxWidthNode which has most
        // distance descendants
        // until a leaf node.
        Node firstNode = (Node) longestPathNodesMap.get(maxWidthNode);
        Node secondNode = (Node) longestPathNodesMap2.get(maxWidthNode);
        while (firstNode != null) {
            longestPathNodes.add(0, firstNode);
            firstNode = (Node) longestPathNodesMap.get(firstNode);
            if (firstNode != null) {
                if (circularConst.getDfsTree() == 1) {
                    layout.setNodeColor(firstNode, Color.GREEN);
                    layout.setNodeLabel(firstNode, new Integer(firstNode
                            .getInteger("dfsParam.dfsNum")).toString());
                }
            }
            if (secondNode != null) {
                longestPathNodes.add(secondNode);
                secondNode = (Node) longestPathNodesMap.get(secondNode);
                if (secondNode != null) {
                    if (circularConst.getDfsTree() == 1) {
                        layout.setNodeColor(secondNode, Color.GREEN);
                        layout.setNodeLabel(secondNode, new Integer(secondNode
                                .getInteger("dfsParam.dfsNum")).toString());
                    }
                }
            }
        }

        Iterator lpIt = longestPathNodes.iterator();
        for (int p = 0; lpIt.hasNext(); p++) {
            Node longestPathNode = (Node) lpIt.next();
            longestPathNode.setInteger("longestPath.position", p);
        }
        CircularConst circularConst = new CircularConst();
        if (circularConst.getDfsTree() == 1) {
            Iterator edges = graph.getEdgesIterator();
            levels = bfs(startNode);
            maxLevelSize = 0;
            for (int i = 0; i < levels.size(); i++) {
                List level = (List) levels.get(new Integer(i));
                int levelSize = level.size();
                if (maxLevelSize < levelSize) {
                    maxLevelSize = levelSize;
                }
                for (int j = 0; j < levelSize; j++) {
                    Node node = (Node) level.get(j);
                    node.setInteger("order", j);
                }
            }
        }
        graph.getListenerManager().transactionFinished(this);
    } // End of execute

    /**
     * @see org.graphaffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        longestPathNodes = new LinkedList();
        nodeParentMap = new HashMap();
        nodeParmutedNeighbors = new HashMap();
        levels = new Hashtable();
        maxLevelSize = 0;
        treeEdges = new ArrayList();
        graph = null;
    }
}
