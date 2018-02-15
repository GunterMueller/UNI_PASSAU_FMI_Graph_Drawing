package org.graffiti.plugins.algorithms.circulardrawing;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElementNotFoundException;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.AlgorithmResult;
import org.graffiti.plugin.algorithm.DefaultAlgorithmResult;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;

/**
 * A implementation of the Algorithm Circular of Six and Tollis Created on Jan
 * 11, 2005
 * 
 * @author demirci
 * 
 */

public class Circular extends AbstractAlgorithm {
    /**
     * The String shown in the parameter window for "circular"
     */
    private static final String CIRCULAR = "(1) circular";

    /**
     * The String shown in the parameter window for "circular"
     */
    private static final String CIRCULAR1 = "(2) circular1";

    /**
     * The String shown in the parameter window for "circular3"
     */
    private static final String CIRCULAR2 = "(3) circular2";

    /**
     * The String shown in the parameter window for "circular"
     */
    private static final String CIRCULAR_DFS = "(4) dfs-circular";

    /**
     * The String shown in the parameter window for "analyze runtime"
     */
    private static final String ANALYSE_RUNTIME = "(3) analyze runtime";

    /**
     * The String shown in the parameter window for "cross enumeration"
     */
    private static final String ANALYSE_CROSS_ENUM = "(1) cross enumeration";

    /**
     * The String shown in the parameter window for "analyse DFS tree"
     */
    private static final String ANALYSE_DFS_TREE = "(2) analyse DFS tree";

    /**
     * The String shown in the parameter window for "in clockwise order"
     */
    private static final String ORDER_CW = "(1) in clockwise order";

    /**
     * The String shown in the parameter window for "in counterclockwise order"
     */
    private static final String ORDER_CCW = "(2) in counterclockwise order";

    /**
     * The String shown in the parameter window for "by degree (ascending)"
     */
    private static final String ORDER_DEG_ASC = "(3) by degree (ascending)";

    /**
     * The String shown in the parameter window for "by degree (descending)"
     */
    private static final String ORDER_DEG_DESC = "(4) by degree (descending)";

    /**
     * The String shown in the parameter window for "in random order"
     */
    private static final String ORDER_RANDOM = "(5) in random order";

    /**
     * Class that performs the test for biconnectivity.
     */
    private TestBiconnection biconTest;

    // ***********************************************************************//

    /** The map after the permutation of the adjacency lists of the nodes */
    Map nodeParmutedNeighbors = new HashMap();

    /**
     * Actual processed node which has lowest degree in the graph
     */
    private static Node currentNode = null;

    /**
     * The keys are the nodes of the copying Graph and the values are the nodes
     * of the original graph.
     */
    private static Map copyAndOriginalNodes = new HashMap();

    /**
     * Comment for <code>removalList</code> The List of the pair edges. The
     * edges will be removed of the graph.
     */
    private static List removalList = new ArrayList();

    /**
     * Comment for <code>longestPathNodes</code> The List contains all nodes in
     * the copying graph, which to be find after LongestPath Algorithm
     * 
     */
    private static List longestPathNodes = new ArrayList();

    /**
     * Comment for <code>remainingNodes</code> The list contains all nodes, in
     * the copying Graph, which not occurrences in the longest path.
     */
    private static Collection remainingNodes = new ArrayList();

    /**
     * Comment for <code>orgGraphNodesPath</code> The list of the nodes in the
     * original graph, wich are in the longest path.
     */
    public static List orgGraphNodesPath = new ArrayList();

    /**
     * Comment for <code>orgGraphRemainingNodes</code> The list of the nodes in
     * the original graph which not occurrences in the longest path.
     */
    private static List orgGraphRemainingNodes = new ArrayList();

    /**
     * Comment for <code>remNodesPath</code> A path of the remaining nodes.
     * Either first or last node in the path has a neighbor on the circle.
     */
    private static List remNodesPath;

    /**
     * Comment for <code>leftNodeNeighborOnCircle</code>
     */
    private static Node leftNodeNeighborOnCircle = null;

    /**
     * Comment for <code>nodeCategorieTable</code> The table of the lists which
     * contains the nodes with equals degree.
     */
    private static Hashtable nodeCategorieTable = null;

    /**
     * @see org.graffiti.plugins.algorithms.circulardrawing.CircularConst
     */
    private static CircularConst circularConst = new CircularConst();

    /** Number of the crossing produced after circular */
    int numberOfCrossing = 0;

    /** Number of the nodes in the longest path */
    int cirPathL = 0;

    // ***********************************************************************//

    /**
     * Gets the parameters of the algorithm
     * 
     * @return the parameters of the algorithm
     */
    public Parameter<?>[] getAlgorithmParameters() {
        Parameter[] parameter = new Parameter[6];
        String[] algParams = { CIRCULAR, CIRCULAR1, CIRCULAR2, CIRCULAR_DFS };
        String[] analyseParams = { ANALYSE_CROSS_ENUM, ANALYSE_DFS_TREE,
                ANALYSE_RUNTIME };
        String[] orderOfNodes = { ORDER_CW, ORDER_CCW, ORDER_DEG_ASC,
                ORDER_DEG_DESC, ORDER_RANDOM };

        StringSelectionParameter algorithm = new StringSelectionParameter(
                algParams, "ALGORITHM:", "Choose version of the algorithm");
        StringSelectionParameter analyse = new StringSelectionParameter(
                analyseParams, "ANALYSE:", "What should be analyzed");
        StringSelectionParameter nodeOrder = new StringSelectionParameter(
                orderOfNodes, "ORDER OF NODES", "Nodes are processed...");
        BooleanParameter permuteAdjList = new BooleanParameter(false,
                "Permute:", "Adjacence Lists");
        BooleanParameter permuteRestOfNodes = new BooleanParameter(false,
                "Permute:", "Rest Of the nodes");
        BooleanParameter permuteBuckets = new BooleanParameter(false,
                "Permute:", "Buckets");

        parameter[0] = algorithm;
        parameter[1] = analyse;
        parameter[2] = nodeOrder;
        parameter[3] = permuteAdjList;
        parameter[4] = permuteRestOfNodes;
        parameter[5] = permuteBuckets;
        return parameter;
    }

    /**
     * Checks the preconditions of the algorithms. Preconditions are: - Graph
     * must not be empty. - Graph must contain more than three nodes. - Graph
     * must be biconnected
     */
    public void check() throws PreconditionException {

        if (graph.isEmpty())
            throw new PreconditionException(
                    "The graph is empty. Can't run Circular-Drawing!");
        else if (graph.getNodes().size() < 3)
            throw new PreconditionException(
                    "Less than three nodes. Can't run Circular-Drawing!");
        else {
            if (circularConst.getDfsTree() == 0) {
                this.biconTest = new TestBiconnection(this.graph);
                if (!this.biconTest.isBiconnected())
                    throw new PreconditionException(
                            "The graph is not biconnected!  Can't run Circular-Drawing!");
            }
        }
    }

    /**
     * Returns the name of the algorithm
     */
    public String getName() {
        return "Circular-Drawing by Six and Tollis";
    }

    /**
     * Sets the parameters of the algorithm
     * 
     * @param params
     *            the parameters of the algorithm
     */
    public void setAlgorithmParameters(Parameter<?>[] params) {
        String whichAlgorithm = ((StringSelectionParameter) params[0])
                .getSelectedValue();
        circularConst.deactivateAlgorithm("0");
        circularConst.deactivateAlgorithm("1");
        circularConst.deactivateAlgorithm("2");
        circularConst.deactivateAlgorithm("3");
        if (whichAlgorithm.equals(CIRCULAR)) {
            circularConst.activateAlgorithm("0");
        } else if (whichAlgorithm.equals(CIRCULAR1)) {
            circularConst.activateAlgorithm("1");

        } else if (whichAlgorithm.equals(CIRCULAR2)) {
            circularConst.activateAlgorithm("2");
        } else {
            circularConst.activateAlgorithm("3");
        }

        String whichAnalyse = ((StringSelectionParameter) params[1])
                .getSelectedValue();

        if (whichAnalyse.equals(ANALYSE_CROSS_ENUM)) {
            circularConst.deactivateRuntime();
            circularConst.setDfsTree(0);
            circularConst.activateCrossEnum();
        } else if (whichAnalyse.equals(ANALYSE_RUNTIME)) {
            circularConst.activateRuntime();
            circularConst.deactivateCrossEnum();
            circularConst.setDfsTree(0);
        } else {
            circularConst.setDfsTree(1);
            System.out.println(" circularConst.DFSTREE ist "
                    + circularConst.getDfsTree());
        }
        if (((BooleanParameter) params[3]).getBoolean().booleanValue()) {
            if (CircularConst.PERMUTATION_STATE == 0) {
                System.out.println("adjazenzlisten permutieren");
                CircularConst.PERMUTATION_STATE = 1;
                circularConst.activatePermutation();
            } else if (CircularConst.PERMUTATION_STATE == 1) {
                System.out.println("adjazenzlisten nicht permutieren");
                CircularConst.PERMUTATION_STATE = 0;
                circularConst.deactivatePermutation();
            }
        }
        if (((BooleanParameter) params[4]).getBoolean().booleanValue()) {
            if (CircularConst.REM_NODES_STATE == 0) {
                System.out.println("�briggebleibenen Knoten permutieren");
                CircularConst.REM_NODES_STATE = 1;
                circularConst.activatePermutationOfRemnodes();
            } else if (CircularConst.REM_NODES_STATE == 1) {
                System.out.println("�briggebleibenen Knoten nicht permutieren");
                CircularConst.REM_NODES_STATE = 0;
                circularConst.deactivatePermutationOfRemnodes();
            }
        }
        if (((BooleanParameter) params[5]).getBoolean().booleanValue()) {
            if (CircularConst.NODE_CATEGORIE_TABLE_STATE == 0) {
                System.out.println("node categorie table permutieren");
                CircularConst.NODE_CATEGORIE_TABLE_STATE = 1;
                circularConst.activatePermutationOfNodeCategorieTable();
            } else if (CircularConst.NODE_CATEGORIE_TABLE_STATE == 1) {
                System.out.println("node categorie table nicht permutieren");
                CircularConst.NODE_CATEGORIE_TABLE_STATE = 0;
                circularConst.deactivatePermutationOfNodeCategorieTable();
            }
        }
        this.parameters = params;
    }

    /**
     * @return the node ordering after executing of the algorithm circular
     */
    public List getPath() {
        return orgGraphNodesPath;
    }

    public int getNumberOfCrossing() {
        return numberOfCrossing;
    }

    private void setNumberOfCrossing(int cross) {
        numberOfCrossing = cross;
    }

    /**
     * Output the nodeCategorieTable
     */
    private void printNodeCategorieTable() {

        System.out
                .println("NodeCtegorieTable Eintrag repr�sentiert einer Knoten, seiner (key = grad, nodeId) ");
        Iterator it = nodeCategorieTable.values().iterator();
        while (it.hasNext()) {
            List l = (LinkedList) it.next();
            System.out.print("[ ");
            for (int j = 0; j < l.size(); j++) {
                Node n = (Node) l.get(j);
                int label = n.getInteger("node.id");
                if (j < l.size() - 1) {
                    System.out.print("(" + n.getInteger("table.key") + " , "
                            + label + ") ; ");
                } else {
                    System.out.print("(" + n.getInteger("table.key") + " , "
                            + label + ")");
                }
            }
            System.out.println(" ]");
        }
    }

    /**
     * convert a collection to a list
     * 
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
     * Permutate the adjacentcy list of a node
     * 
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
     * Permute a list
     * 
     * @return a permutated list
     */
    private List permutateList() {
        List permutatedList = new ArrayList();
        int length = orgGraphRemainingNodes.size();
        for (int i = 0; i < length; i++) {
            int size = orgGraphRemainingNodes.size();
            double zufall = Math.random();
            Float pos = new Float((size - 1) * zufall);
            int position = Math.round(pos.floatValue());
            Node n = (Node) orgGraphRemainingNodes.remove(position);
            permutatedList.add(n);
        }
        return permutatedList;
    }

    /**
     * @param list
     *            a list to permutation
     * @return a permutated list
     */
    private List permutateList(List list) {
        List permutatedList = new ArrayList();
        int length = list.size();
        for (int i = 0; i < length; i++) {
            int size = list.size();
            double zufall = Math.random();
            Float pos = new Float((size - 1) * zufall);
            int position = Math.round(pos.floatValue());
            Node n = (Node) list.remove(position);
            permutatedList.add(n);
        }
        return permutatedList;
    }

    /**
     * Perform the first step in the algorithm Circular. Sort the nodes by
     * ascending degree in to <code>nodeCategorieTable</code>
     * 
     * @param g
     * @see org.graffiti.graph.Graph
     */
    private void bucketSort(Graph g) {

        nodeCategorieTable = new Hashtable();
        List nodesList = g.getNodes();
        Node n = (Node) nodesList.get(0);
        int lowestDegree = n.getInDegree();
        g.setInteger("degree.lowest", lowestDegree);

        Iterator nodeIt = nodesList.iterator();
        for (; nodeIt.hasNext();) {
            Node node = (Node) nodeIt.next();
            int nodeDegree = node.getInDegree();
            node.setInteger("tmpTable.key", nodeDegree);
            node.setInteger("table.key", nodeDegree);
            lowestDegree = g.getInteger("degree.lowest");
            if (nodeDegree < lowestDegree) {
                g.setInteger("degree.lowest", nodeDegree);
            }
            List degreeList = (List) nodeCategorieTable.get(new Integer(
                    nodeDegree));
            if (degreeList == null) {
                degreeList = new LinkedList();
                degreeList.add(node);
                nodeCategorieTable.put(new Integer(nodeDegree), degreeList);
            } else {
                degreeList.add(node);
                nodeCategorieTable.put(new Integer(nodeDegree), degreeList);
            }
        }
        // printNodeCategorieTable();
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
            if (circularConst.getPermutation() == 1) {
                neighbors = permutateNodeNeighbors(node);
            } else {
                neighbors = node.getNeighbors();
            }
            m.put(node, neighbors);
        }
        return m;
    }

    /**
     * @param g
     * @see org.graffiti.graph.Graph
     * @return the map of the nodes and its incident edges in the graph g.
     */
    private Map nodeEdges(Graph g) {
        Map m = new HashMap();
        Iterator nodes = g.getNodesIterator();
        while (nodes.hasNext()) {
            Node node = (Node) nodes.next();
            Collection inzEdges = new LinkedList();
            inzEdges.addAll(node.getEdges());
            m.put(node, inzEdges);
        }
        return m;
    }

    /**
     * @param g
     * @see org.graffiti.graph.Graph
     * @see org.graffiti.graph.Node
     * @return map of all nodes from the graph g.
     */
    private Map nodesMap(Graph g) {
        Map m = new HashMap();
        Iterator it = g.getNodesIterator();
        for (int i = 0; it.hasNext(); i++) {
            Node node = (Node) it.next();
            m.put(new Integer(i), node);
        }
        return m;
    }

    /**
     * @param g
     * @see org.graffiti.graph.Graph
     * @see org.graffiti.graph.Edge
     * @return map of all edges from the Graph g.
     */
    private Map edgesMap(Graph g) {
        Map m = new HashMap();
        Iterator edgeIt = g.getEdgesIterator();
        for (int i = 0; edgeIt.hasNext(); i++) {
            Edge edge = (Edge) edgeIt.next();
            m.put(new Integer(i), edge);
        }
        return m;
    }

    /**
     * @param m1
     *            map of the nodes from the copying Graph.
     * @param m2
     *            map of the nodes from the originaly Graph.
     * @return map of the nodes form the copying and originaly Graph.
     */
    private Map nodeIdentifier(Map m1, Map m2) {
        Map m = new HashMap();
        for (int i = 0; i < m1.size(); i++) {
            Node n1 = (Node) m1.get(new Integer(i));
            Node n2 = (Node) m2.get(new Integer(i));
            m.put(n1, n2);
        }
        return m;
    }

    /**
     * @param m1
     *            map of the edges from the copying Graph.
     * @param m2
     *            map of the edges from the originaly Graph.
     * @return map of the edges form the copying and originaly Graph.
     */
    private Map edgeIdentifier(Map m1, Map m2) {
        Map m = new HashMap();
        for (int i = 0; i < m1.size(); i++) {
            Edge e1 = (Edge) m1.get(new Integer(i));
            Edge e2 = (Edge) m2.get(new Integer(i));
            m.put(e1, e2);
        }
        return m;
    }

    /**
     * Places node between two neighbors.
     * 
     * @param node
     *            a remaining node.
     * @return position, node will be placed at the position.
     */
    private int calculatePositionI(Node node) {
        int size = orgGraphNodesPath.size();
        // System.out.println("remNode ist " +
        // node.getInteger("dfsParam.dfsNum"));
        int position = -1;
        Object[] neighbors = node.getNeighbors().toArray();

        for (int i = 0; i < neighbors.length - 1; i++) {
            Node v1 = (Node) neighbors[i];

            if (orgGraphNodesPath.indexOf(v1) != -1) {
                // System.out.println("v1 ist " +
                // v1.getInteger("dfsParam.dfsNum"));
                // System.out.println("v1 position ist " +
                // orgGraphNodesPath.indexOf(v1));
                for (int j = i + 1; j < neighbors.length; j++) {
                    Node v2 = (Node) neighbors[j];
                    if (orgGraphNodesPath.indexOf(v2) != -1) {
                        // System.out.println("v2 ist " +
                        // v2.getInteger("dfsParam.dfsNum"));
                        // System.out.println("v2 position ist " +
                        // orgGraphNodesPath.indexOf(v2));
                        int modPos = (orgGraphNodesPath.indexOf(v1) - orgGraphNodesPath
                                .indexOf(v2))
                                % (size);
                        if (modPos < 0) {
                            modPos = modPos + size;
                        }
                        // System.out.println("modPos " + modPos);
                        int modPos2 = (orgGraphNodesPath.indexOf(v2) - orgGraphNodesPath
                                .indexOf(v1))
                                % (size);

                        if (modPos2 < 0) {
                            modPos2 = modPos2 + size;
                        }
                        // System.out.println("modPos2 " + modPos2);
                        if (modPos == 1) {
                            position = orgGraphNodesPath.indexOf(v1);
                            // System.out.println("position ist " + position);
                            break;
                        }

                        else if (modPos2 == 1) {
                            position = orgGraphNodesPath.indexOf(v2);
                            // System.out.println("position ist " + position);
                            break;
                        } else {
                            ;
                        }
                    }
                }
            }
            if (position != -1) {
                break;
            }
        }
        return position;
    }

    /**
     * @param node
     *            a remaining node.
     * @see org.graffiti.graph.Node
     * @return a next to position of a neighbor node on the circle.
     */
    private int calculatePositionII(Node node) {
        int pos = -1;
        int size = orgGraphNodesPath.size();
        Object[] neighbors = node.getNeighbors().toArray();
        if (pos == -1) {
            for (int i = 0; i < neighbors.length; i++) {
                Node v1 = (Node) neighbors[i];
                if (orgGraphNodesPath.indexOf(v1) != -1) {
                    pos = (orgGraphNodesPath.indexOf(v1) + 1) % size;
                    // System.out.println(" Knoten " +
                    // node.getInteger("dfsParam.dfsNum") +
                    // " wurde an den Knoten " +
                    // v1.getInteger("dfsParam.dfsNum") + " platziert");
                    break;
                }
            }
        }
        return pos;
    }

    /**
     * Place the remainig node on the the neighbor node which has got minmal
     * degree.
     * 
     * @param node
     *            remaining node.
     * @see org.graffiti.graph.Node
     * @return the position of the neighbor on the circle.
     */
    private int placeOnMinGradNeighbor(Node node) {
        int pos = -1;
        int l = orgGraphNodesPath.size();
        Node minGradNeighbor = (Node) node.getNeighbors().toArray()[0];
        // System.out.println("minGradNode ist " +
        // minGradNeighbor.getInteger("dfsParam.dfsNum") +
        // " hat den position " + orgGraphNodesPath.indexOf(minGradNeighbor));
        Iterator neighbors = node.getNeighborsIterator();
        while (neighbors.hasNext()) {
            Node neighbor = (Node) neighbors.next();
            // System.out.println("nachbar knoten von " +
            // node.getInteger("dfsParam.dfsNum")
            // + " hat den position " + orgGraphNodesPath.indexOf(neighbor));
            if (neighbor.getInDegree() < minGradNeighbor.getInDegree()) {
                if (isOnCircle(neighbor)) {
                    minGradNeighbor = neighbor;
                }
            } else if (isOnCircle(neighbor) && !isOnCircle(minGradNeighbor)) {
                minGradNeighbor = neighbor;
            }
        }
        int tmpPos1 = 0;
        int tmpPos2 = 0;
        if (isOnCircle(minGradNeighbor)) {
            // pos = (orgGraphNodesPath.indexOf(minGradNeighbor) + 1) % l;
            tmpPos1 = (orgGraphNodesPath.indexOf(minGradNeighbor) + 1) % l;
            tmpPos2 = (orgGraphNodesPath.indexOf(minGradNeighbor)) % l;
            neighbors = node.getNeighborsIterator();
            int tmpX = 0;
            int tmpY = 0;
            while (neighbors.hasNext()) {
                Node neighbor = (Node) neighbors.next();
                int x = 0;
                int y = 0;
                int neighborPos = orgGraphNodesPath.indexOf(neighbor);
                x = Math.abs(tmpPos1 - neighborPos);
                y = l - x;
                if (x <= y) {
                    tmpX = tmpX + x;
                } else {
                    tmpX = tmpX + y;
                }
                x = Math.abs(tmpPos2 - neighborPos);
                y = l - x;
                if (x <= y) {
                    tmpY = tmpY + x;
                } else {
                    tmpY = tmpY + y;
                }
            }
            if (tmpX <= tmpY) {
                pos = tmpPos1;
            } else {
                pos = tmpPos2;
            }

        }
        return pos;
    }

    /**
     * Defined a random position for a remaining node which has'nt a neihbor
     * node on the circle.
     * 
     * @see org.graffiti.graph.Node
     * @return a random position on the circle.
     */
    private int calculatePositionIIIRandom() {
        // defined random position for the node at the circle.
        int n = orgGraphNodesPath.size();
        double zufall = Math.random();
        Float floatPos = new Float(n * zufall);
        int pos = Math.round(floatPos.floatValue());
        return pos;
    }

    /**
     * @param node
     *            actual processed remainig node.
     * @see org.graffiti.graph.Node
     * @return null if the nod has'nt got a neighbor on the circle, a neighbor
     *         node on the circle otherwise.
     */
    private Node getNeighborOnCircle(Node node) {
        Node circleNeighbor = null;
        Iterator neighbors = node.getNeighborsIterator();
        while (neighbors.hasNext()) {
            Node neighbor = (Node) neighbors.next();
            if (neighbor.getBoolean("in.lpath")) {

                // System.out.print("Die Knoten " +
                // node.getInteger("dfsParam.dfsNum") +
                // " hat einen nachbar auf dem kreis");
                circleNeighbor = neighbor;
                // System.out.println(" die nachbar ist " +
                // circleNeighbor.getInteger("dfsParam.dfsNum"));
                break;
            }
        }
        return circleNeighbor;
    }

    /**
     * @param node
     * @see org.graffiti.graph.Node
     * @return true if the node placed on the circle, otherwise false
     */
    private boolean isOnCircle(Node node) {
        boolean bol = false;
        if (node.getBoolean("in.lpath")) {
            bol = true;
        }
        return bol;

    }

    /**
     * @param startNode
     * @see org.graffiti.graph.Node
     * @return a list of the phat, the list contins ....
     */
    private List calculatePhat(Node startNode) {
        List phatList = new ArrayList();
        boolean bol = false;
        while (!bol) {
            phatList.add(startNode);
            Iterator it = startNode.getNeighborsIterator();
            while (it.hasNext()) {
                Node neighbor = (Node) it.next();
                // System.out.println("neighbor ist " +
                // neighbor.getInteger("dfsParam.dfsNum"));
                if (!phatList.contains(neighbor)) {
                    // System.out.println("neighbor ist nicht in phat list
                    // enthalten");
                    if (!isOnCircle(neighbor)) {
                        // System.out.println("neighbor ist nicht auf dem
                        // kreis");
                        leftNodeNeighborOnCircle = getNeighborOnCircle(neighbor);
                        if (leftNodeNeighborOnCircle != null) {
                            phatList.add(neighbor);
                            bol = true;
                            break;
                        } else {
                            startNode = neighbor;
                            break;
                        }
                    }
                }
            }
        }
        return phatList;
    }

    /**
     * place the remaining nodes in the longestPath.
     */
    public void placeRemainingNodes() {

        Object[] remNodes = orgGraphRemainingNodes.toArray();
        List placesRemNodesList = new ArrayList();
        for (int k = 0; k < remNodes.length;) {
            Node remNode = (Node) remNodes[k];
            // System.out.println("Circular remNode ist " +
            // remNode.getInteger("dfsParam.dfsNum")
            // + "/" + remNode.getInteger("node.id"));

            int position = calculatePositionI(remNode);
            // i
            if (position != -1) {
                remNode.setBoolean("in.lpath", true);
                orgGraphNodesPath.add(position, remNode);
                placesRemNodesList.add(remNode);
                k++;
            }
            // (ii)
            else {
                // 0 = "Circular" Implementierung des orginal Algorithmus
                if (circularConst.getAlgorithm(0).equals("1")) {
                    // System.out.println("selected Algorithm is Circular");
                    // (ii)
                    position = calculatePositionII(remNode);
                    if (position != -1) {
                        remNode.setBoolean("in.lpath", true);
                        orgGraphNodesPath.add(position, remNode);
                        placesRemNodesList.add(remNode);
                        k++;
                    }
                    // (iii)
                    else {
                        position = calculatePositionIIIRandom();
                        remNode.setBoolean("in.lpath", true);
                        orgGraphNodesPath.add(position, remNode);
                        // F�ge es am Anfang der Longestpath ein
                        // orgGraphNodesPath.add(0,remNode);
                        placesRemNodesList.add(remNode);
                        k++;
                    }
                }

                // 1 = "Circular I"
                // Schritt (i) und (iii) wie in Circular,
                // Verbesserungsvorschlag f�r Schritt (ii).
                else if (circularConst.getAlgorithm(1).equals("1")) {
                    // System.out.println("selected Algorithm is CircularI");
                    // (ii)
                    position = placeOnMinGradNeighbor(remNode);
                    // System.out.println("position ist " + position);
                    if (position != -1) {
                        remNode.setBoolean("in.lpath", true);
                        orgGraphNodesPath.add(position, remNode);
                        // System.out.println("Die Knoten " +
                        // remNode.getInteger("dfsParam.dfsNum") +
                        // " wurde an den position " + position + " platziert"
                        // );
                        placesRemNodesList.add(remNode);
                        k++;
                    }
                    // (iii)
                    else {
                        position = calculatePositionIIIRandom();
                        remNode.setBoolean("in.lpath", true);
                        orgGraphNodesPath.add(position, remNode);
                        // orgGraphNodesPath.add(0,remNode);
                        placesRemNodesList.add(remNode);
                        k++;
                    }
                }

                // 2 = "Circular II"
                // Schritt (i) und (ii)wie in orginal Algorithmus
                // Verbeserungsvorschlag f�r den Schritt (iii).
                else if (circularConst.getAlgorithm(2).equals("1")) {
                    // System.out.println("selected Algorithm is Circular II");
                    // (ii)
                    position = calculatePositionII(remNode);
                    if (position != -1) {
                        remNode.setBoolean("in.lpath", true);
                        orgGraphNodesPath.add(position, remNode);
                        placesRemNodesList.add(remNode);
                        k++;
                    }

                    /*
                     * position = placeOnMinGradNeighbor(remNode); //
                     * System.out.println("position ist " + position);
                     * if(position != -1) { remNode.setBoolean("in.lpath",
                     * true); orgGraphNodesPath.add(position,remNode); //
                     * System.out.println("Die Knoten " +
                     * remNode.getInteger("dfsParam.dfsNum") + // " wurde an den
                     * position " + position + " platziert" );
                     * placesRemNodesList.add(remNode); k++; }
                     */

                    // (iii)
                    else {
                        List phatList = calculatePhat(remNode);
                        /*
                         * for (int i = 0; i < phatList.size(); i++) { Node node
                         * = (Node)phatList.get(i);
                         * System.out.println("knoten + " +
                         * node.getInteger("dfsParam.dfsNum") + " kommt in phat
                         * list vor"); }
                         */

                        remNodesPath = phatList;
                        int leftNodeLocationOnCirle = orgGraphNodesPath
                                .indexOf(leftNodeNeighborOnCircle);
                        // System.out.println("leftNodeLocationOnCirle ist "+
                        // leftNodeLocationOnCirle);
                        for (int i = remNodesPath.size() - 1; i >= 0; i--) {
                            Node phatNode = (Node) remNodesPath.get(i);
                            int circleNodesSize = orgGraphNodesPath.size();
                            orgGraphNodesPath.add((leftNodeLocationOnCirle + 1)
                                    % circleNodesSize, phatNode);
                            phatNode.setBoolean("in.lpath", true);
                            placesRemNodesList.add(phatNode);
                            // System.out.println("phat Node "+
                            // phatNode.getInteger("dfsParam.dfsNum") +
                            // " wurde an den Location " +
                            // (leftNodeLocationOnCirle + 1) % circleNodesSize +
                            // " eingef�gt");
                            leftNodeLocationOnCirle += 1;
                            // phatNode.setBoolean("in.lpath", true);
                            k++;
                        }

                        for (int i = 0; i < placesRemNodesList.size(); i++) {
                            Node n = (Node) placesRemNodesList.get(i);
                            orgGraphRemainingNodes.remove(n);
                        }
                        remNodes = orgGraphRemainingNodes.toArray();
                        k = 0;
                    }
                } else if (circularConst.getAlgorithm(3).equals("1")) {
                    break;
                } else {
                    break;
                }

            }

        }
    }

    /**
     * place the remaining nodes in the longestPath.
     */
    /*
     * private void placeRemainingNodesIntoEmbeddingCircle() { Object []
     * remNodes = orgGraphRemainingNodes.toArray(); List newRemNodes = new
     * ArrayList(); // int size = remNodes.length; boolean bol = true;
     * while(bol) { for(int k = 0; k < remNodes.length;k++) { Node remNode =
     * (Node)remNodes[k]; int position = calculatePosition(remNode); if
     * (position != -1) { orgGraphNodesPath.add(position,remNode); } else{
     * //System.out.println("dieser Knoten hat keine benachbarte Nachbarn in
     * Kreis"); newRemNodes.add(remNode); } } if (newRemNodes.size() == 0) { bol
     * = false; } else { remNodes = newRemNodes.toArray(); newRemNodes = new
     * ArrayList(); } } }
     */

    /**
     * @see org.graffiti.plugin.algorithm.CalculatingAlgorithm#getResult()
     */
    public AlgorithmResult getResult() {
        AlgorithmResult aresult = new DefaultAlgorithmResult();
        aresult.addToResult("nodeOrdering", orgGraphNodesPath);
        aresult.addToResult("numberOfCrossing", new Integer(numberOfCrossing));
        aresult.addToResult("cirPathL", new Integer(cirPathL));
        aresult.addToResult("circularLayout", this.graph);
        return aresult;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    public void reset() {
        nodeCategorieTable = null;
        copyAndOriginalNodes = null;
        removalList = new ArrayList();
        orgGraphNodesPath = new ArrayList();
        orgGraphRemainingNodes = new ArrayList();
        List remNodesPath = new ArrayList();
        leftNodeNeighborOnCircle = null;
        numberOfCrossing = 0;
        cirPathL = 0;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */

    public void execute() {
        FileWriter fileWriter;
        PrintWriter writer;
        File file;

        String graphHashCode = new Integer(graph.hashCode()).toString();

        // try {

        // file = new
        // File("/home/cip/demirci/workspace/Graffiti_Plugins/org/graffiti/" +
        // "plugins/algorithms/circulardrawing/statistic.txt");
        // System.out.println("Graph hash code ist " + graphHashCode);

        // new session
        if (CircularConst.SESSION_STATE == 0) {
            // System.out.println("New session!");
            // file.delete();
            CircularConst.SESSION_STATE = 1;
            CircularConst.STATISTIC_FILE_STATE = 0;
            CircularConst.GRAPH_STATE = 0;
        }

        // The same session and new graph
        else if (!graphHashCode.equals(CircularConst.GRAPH_HASHCODE)
                && CircularConst.SESSION_STATE == 1) {
            // System.out.println("The same sassion and new graph!");
            CircularConst.GRAPH_HASHCODE = graphHashCode;
            // file.delete();
            CircularConst.CIRCULAR_STATE = 0;
            CircularConst.CIRCULARI_STATE = 0;
            CircularConst.CIRCULARII_STATE = 0;
            CircularConst.STATISTIC_FILE_STATE = 0;
            CircularConst.GRAPH_STATE = 0;
            orgGraphNodesPath = new ArrayList();
        }

        // The same session and same graph.
        else {
            // System.out.println("The same sasion and same graph!");
            CircularConst.STATISTIC_FILE_STATE = 1;
            CircularConst.GRAPH_STATE = 1;
            orgGraphNodesPath = new ArrayList();
        }

        // fileWriter = new FileWriter(file,true);
        // writer = new PrintWriter(new BufferedWriter(fileWriter));
        int nodeNumber = graph.getNumberOfNodes();
        int edgeOfNumber = graph.getNumberOfEdges();

        if (circularConst.getAlgorithm(0) == "1"
                && CircularConst.CIRCULAR_STATE == 0) {
            // writer.println("#circular");
            // writer.flush();
            CircularConst.CIRCULAR_STATE = 1;
            CircularConst.GRAPH_STATE = 0;
            /*
             * writer.println(new Integer(nodeNumber).toString());
             * writer.println(new Integer(edgeOfNumber).toString());
             * writer.flush();
             */
        } else if (circularConst.getAlgorithm(1) == "1") {
            // writer.println("#circularI");
            // writer.flush();
            CircularConst.CIRCULARI_STATE = 1;
            CircularConst.GRAPH_STATE = 0;
            /*
             * writer.println(new Integer(nodeNumber).toString());
             * writer.println(new Integer(edgeOfNumber).toString());
             * writer.flush();
             */
        } else if (circularConst.getAlgorithm(2) == "1"
                && CircularConst.CIRCULARII_STATE == 0) {
            // writer.println("#circularII");
            // writer.flush();
            CircularConst.CIRCULARII_STATE = 1;
            CircularConst.GRAPH_STATE = 0;
        } else {
            // writer.println("#dfsCircular");
            // writer.flush();
            CircularConst.DFSCIRCULAR_STATE = 1;
            CircularConst.GRAPH_STATE = 0;

        }
        // writer.println(new Integer(nodeNumber).toString());
        // writer.println(new Integer(edgeOfNumber).toString());
        // writer.flush();

        if (circularConst.getAlgorithm(3) == "1") {
            DFSCircular dfsCircular = new DFSCircular();
            dfsCircular.attach(graph);
            dfsCircular.execute();
            setNumberOfCrossing(dfsCircular.getNumberOfCrossing());
            cirPathL = ((Integer) (dfsCircular.getResult().getResult()
                    .get("dfsPathL"))).intValue();
            // numberOfCrossing =
            // ((Integer)(dfsCircular.getResult().getResult().
            // get("numberOfCrossing"))).intValue();

        } else {
            // Test of runningtime
            long time1 = System.currentTimeMillis();
            // Implementation of agorithm circular.
            CircularLayout layout = new CircularLayout();
            Collection waveFrontNodeList = new LinkedList();

            Iterator nodeIt = graph.getNodesIterator();

            for (int i = 1; nodeIt.hasNext(); i++) {
                Node node = (Node) nodeIt.next();
                node.setInteger("node.id", i);
                node.setInteger("graphics.sortId", node.getInDegree());
            }

            Iterator egIt = graph.getEdgesIterator();
            for (int i = 1; egIt.hasNext(); i++) {
                Edge eg = (Edge) egIt.next();
                eg.setBoolean("in.removalList", false);
                eg.setInteger("label.label", i);
                /*
                 * if (circularConst.getRuntime() == 0) { layout.setLabel(eg,
                 * new Integer(i)); }
                 */
            }
            Graph copyG = (Graph) graph.copy();
            Map copyNodes = nodesMap(copyG);
            // remainingNodes = copyNodes.values();
            remainingNodes = new LinkedList(copyG.getNodes());
            Map graphNodes = nodesMap(this.graph);
            copyAndOriginalNodes = nodeIdentifier(copyNodes, graphNodes);
            Map nodeNeighbors = nodeNeighbors(copyG);
            Map nodeEdges = nodeEdges(copyG);

            Map edgeIdent = new HashMap();
            Map copyGraphEdges = new HashMap();
            Map orgGraphEdges = new HashMap();
            if (circularConst.getDfsTree() == 1) {
                copyGraphEdges = edgesMap(copyG);
                orgGraphEdges = edgesMap(this.graph);
                edgeIdent = edgeIdentifier(copyGraphEdges, orgGraphEdges);
            }

            int currentCrossing = 0;
            // activate the crossing enumeration befor circular
            if (circularConst.getCrossEnum() == 1) {
                // CountAllCrossing allCrossing = new
                // CountAllCrossing(graph.getNodes());
                // currentCrossing =
                // allCrossing.calculateNumberOfCrossing(true);
            }

            // step 1
            // Bucket sort the nodes by ascending degree in to a table T
            bucketSort(copyG);
            if (CircularConst.PERMUTATION_CATEGORIE_TABLE == 1) {
                Iterator keySetIt = nodeCategorieTable.keySet().iterator();
                for (int i = 0; keySetIt.hasNext(); i++) {
                    Integer key = (Integer) keySetIt.next();
                    List list = (List) nodeCategorieTable.get(key);
                    List permList = permutateList(list);
                    nodeCategorieTable.put(key, permList);
                }

            }
            // step 2
            int counter = 1;

            // select the current node with lowest degre.
            int lowestDegree = copyG.getInteger("degree.lowest");
            // System.out.println("lowest degree ist:" + lowestDegree);
            List liste = (List) nodeCategorieTable
                    .get(new Integer(lowestDegree));

            // set currentNode to a lowest degree node of the graph.
            currentNode = (Node) liste.get(0);

            // enumeration of the triangulation edges.
            int edgeNumber = copyG.getNumberOfEdges() + 1;
            int n = copyG.getNumberOfNodes();

            // copyG.getListenerManager().transactionStarted(this);
            int numberOfPairEdges = 0;
            // step 3
            while (counter <= n - 3) {

                // System.out.println("currentNode ist " +
                // currentNode.getInteger("node.id") + " und hat Grad: " +
                // currentNode.getInteger("table.key"));
                waveFrontNodeList = (Collection) nodeNeighbors.get(currentNode);
                Iterator waveFrontNodeListIt = waveFrontNodeList.iterator();
                Node neighbor1 = (Node) waveFrontNodeListIt.next();

                for (; waveFrontNodeListIt.hasNext();) {
                    // System.out.println("neighbor1 ist " +
                    // neighbor1.getInteger("node.id"));
                    Collection neighbor1Col = (Collection) nodeNeighbors
                            .get(neighbor1);
                    if (waveFrontNodeListIt.hasNext()) {
                        Node neighbor2 = (Node) waveFrontNodeListIt.next();
                        // System.out.println("neighbor2 ist " +
                        // neighbor2.getInteger("node.id"));
                        Edge pe = null;
                        if (neighbor1Col.contains(neighbor2)) {
                            // System.out.println("Suche nach pairEdge: ");
                            Iterator neigbor1Edges = ((Collection) nodeEdges
                                    .get(neighbor1)).iterator();
                            while (neigbor1Edges.hasNext()) {
                                Edge e = (Edge) neigbor1Edges.next();
                                if (e.getSource().equals(neighbor2)
                                        || e.getTarget().equals(neighbor2)) {
                                    pe = e;

                                    break;
                                }
                            }
                            try {
                                if (!pe.getBoolean("in.removalList")) {
                                    removalList.add(pe);
                                    numberOfPairEdges++;
                                    pe.setBoolean("in.removalList", true);
                                }
                            } catch (AttributeNotFoundException e) {
                                System.out.println(e.getMessage());
                            }
                        }

                        else {
                            String id = "";
                            CollectionAttribute ca = new HashMapAttribute(id);
                            Edge te = copyG.createEdge(neighbor1, neighbor2,
                                    false, ca);
                            te.setInteger("label.label", edgeNumber);
                            te.setBoolean("in.removalList", true);

                            neighbor1Col = (Collection) nodeNeighbors
                                    .remove(neighbor1);
                            neighbor1Col.add(neighbor2);
                            nodeNeighbors.put(neighbor1, neighbor1Col);
                            Collection n1Edges = (Collection) nodeEdges
                                    .remove(neighbor1);
                            n1Edges.add(te);
                            nodeEdges.put(neighbor1, n1Edges);

                            Collection neighbor2Col = (Collection) nodeNeighbors
                                    .remove(neighbor2);
                            neighbor2Col.add(neighbor1);
                            nodeNeighbors.put(neighbor2, neighbor2Col);
                            Collection n2Edges = (Collection) nodeEdges
                                    .remove(neighbor2);
                            n2Edges.add(te);
                            nodeEdges.put(neighbor2, n2Edges);

                            edgeNumber++;

                            neighbor1.setInteger("tmpTable.key", neighbor1
                                    .getInteger("tmpTable.key") + 1);
                            neighbor2.setInteger("tmpTable.key", neighbor2
                                    .getInteger("tmpTable.key") + 1);
                        }

                        neighbor1 = neighbor2;

                    }
                }

                // Schritt 11
                // remove currentNode and incident edges from G.
                // update the neighbor list of the neighbors of currentNode.
                Collection currentNodeNeighbors = (Collection) nodeNeighbors
                        .remove(currentNode);
                Object[] o = currentNodeNeighbors.toArray();
                for (int i = 0; i < o.length; i++) {
                    Node neighbor = (Node) o[i];
                    Collection neighborNeighbors = (Collection) nodeNeighbors
                            .remove(neighbor);
                    if (neighborNeighbors != null
                            && neighborNeighbors.contains(currentNode)) {
                        neighborNeighbors.remove(currentNode);
                        nodeNeighbors.put(neighbor, neighborNeighbors);
                        // update the temporial location of currentNode
                        // neighbors in nodeCategorieTable
                        neighbor.setInteger("tmpTable.key", neighbor
                                .getInteger("tmpTable.key") - 1);
                    }
                }

                // remove the incident edges of the current node and
                // remove the incident edges of current node from its neighbor
                // nodes.
                Collection currentNodeEdges = (Collection) nodeEdges
                        .remove(currentNode);
                Iterator currentNodeEdgesIt = currentNodeEdges.iterator();
                while (currentNodeEdgesIt.hasNext()) {
                    Edge edge = (Edge) currentNodeEdgesIt.next();
                    if (edge.getTarget().equals(currentNode)) {
                        Node source = edge.getSource();
                        Collection sourceEdges = (Collection) nodeEdges
                                .get(source);
                        if (sourceEdges != null && sourceEdges.contains(edge)) {
                            sourceEdges.remove(edge);
                            nodeEdges.put(source, sourceEdges);
                        }
                    } else {
                        Node target = edge.getTarget();
                        Collection targetEdges = (Collection) nodeEdges
                                .get(target);
                        targetEdges.remove(edge);
                        nodeEdges.put(target, targetEdges);
                    }
                }

                // step 10
                // remove current node from node categoriy table.
                int cnTableKey = currentNode.getInteger("table.key");
                List ll = (List) nodeCategorieTable.remove(new Integer(
                        cnTableKey));
                ll.remove(currentNode);
                // if (ll.size() > 0) {
                nodeCategorieTable.put(new Integer(cnTableKey), ll);
                // }
                // System.out.println("Nachdem Entfernung der currentNode: -->
                // nodeCategorieTable: ");
                // printNodeCategorieTable();

                // Update the location of currentNode neighbors in
                // nodeCategorieTable
                Iterator currentNodeNeighborsIt = currentNodeNeighbors
                        .iterator();
                for (; currentNodeNeighborsIt.hasNext();) {
                    Node wfNode = (Node) currentNodeNeighborsIt.next();
                    int tableKey = wfNode.getInteger("table.key");
                    List list = (List) nodeCategorieTable.remove(new Integer(
                            tableKey));
                    list.remove(wfNode);
                    nodeCategorieTable.put(new Integer(tableKey), list);
                    int tmpTableKey = wfNode.getInteger("tmpTable.key");
                    wfNode.changeInteger("table.key", wfNode
                            .getInteger("tmpTable.key"));

                    List list2 = (List) nodeCategorieTable.remove(new Integer(
                            tmpTableKey));
                    if (list2 == null) {
                        list2 = new LinkedList();
                        list2.add(0, wfNode);
                        nodeCategorieTable.put(new Integer(tmpTableKey), list2);

                    } else {
                        if (!list2.contains(wfNode)) {
                            list2.add(0, wfNode);
                            nodeCategorieTable.put(new Integer(tmpTableKey),
                                    list2);

                        } else {
                            list2.remove(wfNode);
                            list2.add(0, wfNode);
                            nodeCategorieTable.put(new Integer(tmpTableKey),
                                    list2);

                        }
                    }
                }
                // System.out.println("Nachdem Aktualisierung der wfNodes in
                // nodeCategorieTable: ");
                // printNodeCategorieTable();

                // actualize the currentNode
                Iterator it = nodeCategorieTable.values().iterator();
                for (int i = 0; it.hasNext(); i++) {
                    liste = (List) nodeCategorieTable.get(new Integer(i));
                    if (liste != null && liste.size() > 0) {
                        Node node = (Node) liste.get(0);
                        currentNode = node;
                        break;
                    }
                }

                // step 12
                counter++;

            } // End of while

            // step 13
            // Restor graph to its original topology.

            // step 14
            // Remove the edges in removalList from copyG.
            Iterator removalListIt = removalList.iterator();
            for (; removalListIt.hasNext();) {
                Edge rEdge = (Edge) removalListIt.next();
                try {
                    copyG.deleteEdge(rEdge);
                } catch (GraphElementNotFoundException e) {
                    System.out.println(e.getMessage());
                }
            }
            // Schritt 15
            // perform a modified DFS for finding a longest Path on G.

            Node startNode = (Node) copyG.getNodes().get(0);

            LongestPath dfs = new LongestPath(copyG);
            dfs.attach(copyG);
            dfs.setSourceNode(startNode);

            // System.out.println("DFS started! ");
            long dfsTime = System.currentTimeMillis();
            dfs.execute();
            Time dfsTimer = new Time(System.currentTimeMillis() - dfsTime);

            longestPathNodes = (List) dfs.getResult().getResult().get(
                    "longestPath");
            cirPathL = longestPathNodes.size();
            System.out.println("Die L�nge des Gefundenen l�ngsten Weg ist "
                    + cirPathL + "/" + graph.getNumberOfNodes());

            Hashtable levels = (Hashtable) dfs.getResult().getResult().get(
                    "levels");
            int maxLevelSize = ((Integer) dfs.getResult().getResult().get(
                    "maxLevel")).intValue();
            Hashtable orgGraphLevels = new Hashtable();
            dfs.reset();
            // System.out.println("DFS finshed!");

            Iterator longestPathIt = longestPathNodes.iterator();
            while (longestPathIt.hasNext()) {
                Node node = (Node) longestPathIt.next();
                Node orgNode = (Node) copyAndOriginalNodes.get(node);
                orgNode.setInteger("dfsParam.dfsNum", node
                        .getInteger("dfsParam.dfsNum"));
                orgNode.setBoolean("in.lpath", true);
                remainingNodes.remove(node);
                orgGraphNodesPath.add(orgNode);

                if (circularConst.getDfsTree() == 1) {
                    layout.setNodeColor(orgNode, Color.GREEN);
                }
            }

            Iterator remNodesIt = remainingNodes.iterator();
            while (remNodesIt.hasNext()) {
                Node remNode = (Node) remNodesIt.next();
                Node orgRemNode = (Node) copyAndOriginalNodes.get(remNode);
                orgRemNode.setInteger("dfsParam.dfsNum", remNode
                        .getInteger("dfsParam.dfsNum"));
                orgRemNode.setBoolean("in.lpath", false);
                orgRemNode.setBoolean("in.path", false);
                orgRemNode.setBoolean("left.path", false);
                orgRemNode.setBoolean("right.path", false);
                orgGraphRemainingNodes.add(orgRemNode);
            }

            // activated analyse of the dfstree.
            if (circularConst.getDfsTree() == 1) {

                CircularLayout dfsTree = new CircularLayout();
                for (int t = 0; t < levels.size(); t++) {
                    List level = (List) levels.get(new Integer(t));
                    List orgLevel = new ArrayList();
                    int levelSize = level.size();

                    for (int k = 0; k < levelSize; k++) {
                        Node node = (Node) level.get(k);
                        Node orgGraphNode = (Node) copyAndOriginalNodes
                                .get(node);
                        int order = node.getInteger("order");
                        int lev = node.getInteger("level");
                        orgGraphNode.setInteger("order", order);
                        orgGraphNode.setInteger("level", lev);
                        int dfsNum = orgGraphNode.getInteger("dfsParam.dfsNum");
                        String label = new Integer(dfsNum).toString();
                        dfsTree.setNodeLabel(orgGraphNode, label);
                        orgLevel.add(orgGraphNode);

                        orgGraphNode.setInteger("graphics.level", lev);
                        orgGraphNode.setInteger("graphics.order", order);
                        orgGraphNode.setInteger("graphics.dummy", 0);
                    }
                    orgGraphLevels.put(new Integer(t), orgLevel);
                }
                dfsTree.treeLayout(orgGraphLevels, maxLevelSize);
            }

            // step 16
            // Place the remaining nodes into the embedding cicle.
            if (CircularConst.PERMUTATION_REM_NODES == 1) {
                orgGraphRemainingNodes = permutateList();
            }
            placeRemainingNodes();

            Iterator it = orgGraphNodesPath.iterator();
            int numberOfNodes = graph.getNumberOfNodes();
            for (int x = 0; it.hasNext(); x++) {
                Node node = (Node) it.next();
                node.setInteger("longestPath.position", numberOfNodes - x);
            }

            // activate the crossing enumeration befor circular
            if (circularConst.getCrossEnum() == 1) {
                CountAllCrossing allCrossing = new CountAllCrossing(
                        orgGraphNodesPath);
                ClockwiseEdgeOrdering cweo = new ClockwiseEdgeOrdering(graph,
                        orgGraphNodesPath);
                List cwoEdgeOrdering = cweo.edgeOrdering();
                int actualCrossing = allCrossing
                        .calculateNumberOfCrossing(cwoEdgeOrdering);
                setNumberOfCrossing(actualCrossing);
                System.out.println("Number of corssing after circular: "
                        + actualCrossing);
                if (CircularConst.GRAPH_STATE == 0) {
                    // writer.println(new Integer(actualCrossing).toString());
                }
            }

            // take the end time
            Time timer = new Time(System.currentTimeMillis() - time1);
            System.out.println("Gesamt Zeit: " + timer.getTime() + " ms");
            System.out.println("Dfs Zeitbedarf: " + dfsTimer.getTime() + " ms");
            if (circularConst.getAlgorithm(0) == "1") {
                CircularConst.ALGO_RUNTIME[0] = timer;
            } else if (circularConst.getAlgorithm(1) == "1") {
                CircularConst.ALGO_RUNTIME[1] = timer;
            } else if (circularConst.getAlgorithm(2) == "1") {
                CircularConst.ALGO_RUNTIME[2] = timer;
            }

            // step 17
            // Place the resulting longest path onto embedding circle
            if (CircularConst.TEST == 0) {
                if (circularConst.getDfsTree() == 0) {
                    CircularLayout layout2 = new CircularLayout(
                            orgGraphNodesPath);
                    long embeddingTime = System.currentTimeMillis();

                    layout2.embeddingPathOnToCircle();
                    Time embeddingTimer = new Time(System.currentTimeMillis()
                            - dfsTime);
                    System.out.println("Einbettung in Gravisto  Zeitbedarf: "
                            + embeddingTimer.getTime() + " ms");
                }
            }
            // System.out.println("Die Anzahl der pair edges sind " +
            // numberOfPairEdges);
            // ///////////////////////
            // writer.close();
            // fileWriter.close();
            // //////////////////

            // reset the global varible
            // reset();
        }
        // } // end of try
        // catch (IOException ex) {
        // System.out.println(ex.getMessage());
        // }

    }
}
