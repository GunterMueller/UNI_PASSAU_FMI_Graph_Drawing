// =============================================================================
//
//   OptimalLeveling.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.cyclicLeveling;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.DoubleParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugins.algorithms.cyclicLeveling.test.Config;

/**
 * @author Gerg� Lov�sz
 * @version $Revision$ $Date$
 */
public class OptimalLeveling extends AbstractCyclicLeveling {

    /* number of iterations */
    private long numberOfRecursions;

    /* an array of edges(source, target) */
    private int[][] graphArray;

    /* the number of levels */
    private int k;

    /*
     * an array with a list of neighbors for the efficient computation of the
     * recursion. A list at position i contains all neighbors which are
     * connected with incoming edges with node i
     */
    private LinkedList<Integer>[] inNeighbors;

    /*
     * an array with a list of neighbors for the efficient computation of the
     * recursion. A list at position i contains all neighbors which are
     * connected with outgoing edges with node i
     */
    private LinkedList<Integer>[] outNeighbors;

    /* the nodes with the assigned levels */
    private int[] levelsArray;

    /* the best leveling which was found */
    private int[] bestLeveling;

    /* the length of the edges of bestLeveling */
    private int bestLevelingEdgesLength;

    /*
     * contains the number of edges which are already placed when node i is
     * placed
     */
    private int[] placedSoFar;

    /* Contains the number of nodes on each level */
    private int[] numberOfNodesOnEachLevel;

    /**
     * Constructor
     */
    public OptimalLeveling() {

        numberOfLevels = new IntegerParameter(7, "Number of Levels",
                "Number of Levels", 1, 100, 2, 100);
        width = new IntegerParameter(6, "The width of a level",
                "Maximum number of nodes for a level", 1, 50, 1, 50);
        centerX = new DoubleParameter(0d, "center (x)",
                "x coordinate of the center of the graph", 0d, 1000d, 0d, 1000d);
        centerY = new DoubleParameter(0d, "center (y)",
                "y coordinate of the center of the graph", 0d, 1000d, 0d, 1000d);
        minDistance = new IntegerParameter(50, "node distance",
                "minimum distance between two nodes", 10, 60, 10, 60);

    }

    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        SelectionParameter selParam = new SelectionParameter("Start node",
                "BFS will start with the only selected node.");

        return new Parameter[] { selParam, numberOfLevels, width, centerX,
                centerY, minDistance };
    }

    /**
     * Computes random levelings
     * 
     * @param repeats
     */
    public void computeRandomLevelings(int repeats) {
        int randomLevel = 0;
        int sum = 0;
        for (int i = 0; i < repeats; i++) {
            for (int j = 0; j < levelsArray.length; j++) {
                randomLevel = (int) Math.round(Math.random() * (k - 1));
                levelsArray[j] = randomLevel;
            }

            sum = lengthOfEdgesArray();
            if (sum < bestLevelingEdgesLength) {
                bestLevelingEdgesLength = sum;
                bestLeveling = levelsArray.clone();
            }
        }
    }

    /**
     * Computes the sum of the length of the Edges
     */
    private int lengthOfEdgesArray() {
        int sum = 0;
        int sLevel, tLevel, edgeLength;
        for (int i = 0; i < graphArray.length; i++) {
            sLevel = levelsArray[graphArray[i][0]];
            tLevel = levelsArray[graphArray[i][1]];

            if (tLevel > sLevel) {
                edgeLength = tLevel - sLevel;
            } else {
                edgeLength = k - (sLevel - tLevel);
            }

            sum += edgeLength;
        }
        return sum;
    }

    /**
     * Computes the length of each incoming outgoing edge for node and returns
     * the sum
     * 
     * @param node
     * @return sum of the length of the incoming edges
     */
    public int lengthOfEdges(int node) {
        int sum = 0;
        int sLevel, tLevel, edgeLength;
        Iterator<Integer> it = inNeighbors[node].iterator();
        while (it.hasNext()) {
            int neighbor = it.next();

            sLevel = levelsArray[neighbor];
            tLevel = levelsArray[node];

            if (tLevel > sLevel) {
                edgeLength = tLevel - sLevel;
            } else {
                edgeLength = k - (sLevel - tLevel);
            }

            sum += edgeLength;
        }
        Iterator<Integer> it2 = outNeighbors[node].iterator();
        while (it2.hasNext()) {
            int neighbor = it2.next();

            sLevel = levelsArray[node];
            tLevel = levelsArray[neighbor];

            if (tLevel > sLevel) {
                edgeLength = tLevel - sLevel;
            } else {
                edgeLength = k - (sLevel - tLevel);
            }

            sum += edgeLength;
        }
        return sum;
    }

    /**
     * Computes the optimal leveling
     */
    public void computeOptimalLeveling() {
        levelsArray[0] = 0;
        numberOfNodesOnEachLevel[0] = 1;
        permutate(1, 0);
    }

    /**
     * The heart of the computation of the optimal leveling. It tests
     * recursively the possible levelings and compares them to the best leveling
     * which was found so far.
     * 
     * @param node
     *            the node which will be placed in this step of the recursion
     * @param sum
     *            the sum of the already placed edges
     */
    private void permutate(int node, int sum) {
        numberOfRecursions++;

        /* test all positions for node */
        for (int position = 0; position < k; position++) {
            /* assign level position to node if level not full else break */
            if (numberOfNodesOnEachLevel[position] < width.getInteger()) {
                numberOfNodesOnEachLevel[position]++;
                levelsArray[node] = position;

                /*
                 * compute sum of the length of the edges which will be added if
                 * level=position is assigned to node
                 */
                int lengthOfNewEdges = lengthOfEdges(node);

                /* all nodes finished */
                if (node == levelsArray.length - 1) {
                    int length = lengthOfEdgesArray();
                    if (length < bestLevelingEdgesLength) {
                        bestLevelingEdgesLength = length;
                        bestLeveling = levelsArray.clone();
                    }
                }

                /* the number of edges which were not placed yet */
                int unplaced = graphArray.length - placedSoFar[node];

                /*
                 * check if the recursion is finished. If the sum of the length
                 * of the already placed edges + newEdgesLength are smaller than
                 * the length of the edges of the best leveling then stop the
                 * recursion. A further improvement is to add the minimum
                 * possible length of the not placed edges. These edges must
                 * have at least length 1. If sum + unplaced + newEdgesLength <
                 * bestLevelingEdgesLength the recursion can be stopped.
                 */
                if ((node < levelsArray.length - 1)
                        && (sum + unplaced + lengthOfNewEdges < bestLevelingEdgesLength)) {
                    permutate(node + 1, sum + lengthOfNewEdges);
                }

                /*
                 * the node will be placed on the next level and has to be
                 * removed from the current level 'position'
                 */
                numberOfNodesOnEachLevel[position]--;
            }
        }
    }

    /**
     * Returns a String representation of the optimal leveling
     */
    @Override
    public String toString() {
        String result = "levels: [";

        for (int i = 0; i < levelsArray.length; i++) {
            result += levelsArray[i] + " ";
        }
        result += "]" + bestLevelingEdgesLength;

        return result;
    }

    /**
     * compute the best leveling
     */
    @Override
    @SuppressWarnings("unchecked")
    public long computeLevels() {

        long start = System.currentTimeMillis();

        numberOfRecursions = 0;

        /* set k */
        this.k = numberOfLevels.getInteger();

        /* A list which contains all the nodes which are not connected */
        LinkedList<Node> notConnected = new LinkedList();

        /* compute the number of nodes which have a degree > 0 */
        int numberOfConnectedNodes = 0;
        Iterator<Node> it = graph.getNodesIterator();

        while (it.hasNext()) {
            Node node = it.next();
            if (node.getInDegree() != 0 || node.getOutDegree() != 0) {
                numberOfConnectedNodes++;
            } else {
                notConnected.add(node);
            }
        }

        /*
         * contains the assigned levels of the nodes except the nodes which have
         * no edges at all.
         */
        levelsArray = new int[numberOfConnectedNodes];

        /* worst case: each edge has length k */
        bestLevelingEdgesLength = k * graph.getNumberOfEdges();

        /* assign a number (name) to each node */
        Iterator<Node> iterator = graph.getNodesIterator();
        int nodeName = 0;
        while (iterator.hasNext()) {
            Node node = iterator.next();
            if (node.getInDegree() != 0 || node.getOutDegree() != 0) {
                node.setInteger("node", nodeName);
                nodeName++;
            }
        }

        /* initialize inNeighbors array */
        inNeighbors = new LinkedList[numberOfConnectedNodes];
        for (int i = 0; i < inNeighbors.length; i++) {
            inNeighbors[i] = new LinkedList<Integer>();
        }
        Iterator<Edge> edgeIt = graph.getEdgesIterator();
        while (edgeIt.hasNext()) {
            Edge e = edgeIt.next();
            if (e.getSource().getInteger("node") <= e.getTarget().getInteger(
                    "node")) {
                inNeighbors[e.getTarget().getInteger("node")].add(e.getSource()
                        .getInteger("node"));
            }
        }

        /* initialize outNeighbors array */
        outNeighbors = new LinkedList[numberOfConnectedNodes];
        for (int i = 0; i < outNeighbors.length; i++) {
            outNeighbors[i] = new LinkedList<Integer>();
        }
        Iterator<Edge> edgeIt2 = graph.getEdgesIterator();
        while (edgeIt2.hasNext()) {
            Edge e = edgeIt2.next();
            if (e.getSource().getInteger("node") > e.getTarget().getInteger(
                    "node")) {
                outNeighbors[e.getSource().getInteger("node")].add(e
                        .getTarget().getInteger("node"));
            }
        }

        /*
         * the nodes are added always in the same order. This array contains the
         * number of edges that are already placed when node i is placed
         */
        placedSoFar = new int[numberOfConnectedNodes];
        placedSoFar[0] = 0;
        for (int i = 1; i < placedSoFar.length; i++) {
            placedSoFar[i] = placedSoFar[i - 1] + inNeighbors[i].size()
                    + outNeighbors[i].size();
        }

        /* initialize the graph data structure */
        int h = 0;
        graphArray = new int[graph.getNumberOfEdges()][2];
        Iterator<Edge> it2 = graph.getEdgesIterator();
        while (it2.hasNext()) {
            Edge edge = it2.next();
            graphArray[h][0] = edge.getSource().getInteger("node");
            graphArray[h][1] = edge.getTarget().getInteger("node");
            h++;
        }

        /*
         * initialize the array which contains the current number of nodes for
         * each Level
         */
        numberOfNodesOnEachLevel = new int[numberOfLevels.getInteger()];

        /* initialize containers for the nodes of each level */
        levels = new HashSet[numberOfLevels.getInteger()];
        for (int i = 0; i < levels.length; i++) {
            levels[i] = new HashSet<Node>();
        }

        doCyclicMST();
        computeOptimalLeveling();
        long end = System.currentTimeMillis();
        long duration = (end - start);

        /* set levels */
        Iterator<Node> nodeIt = graph.getNodesIterator();
        while (nodeIt.hasNext()) {
            Node node = nodeIt.next();
            if (node.getInDegree() != 0 || node.getOutDegree() != 0) {
                node.setInteger("level", bestLeveling[node.getInteger("node")]);
                levels[node.getInteger("level")].add(node);
            }
        }

        /* place the not connected nodes if existing */
        if (notConnected.size() != 0) {
            for (Iterator listIterator = notConnected.iterator(); listIterator
                    .hasNext();) {
                Node node = (Node) listIterator.next();
                node.setInteger("level", nextFreeLevel());
                levels[node.getInteger("level")].add(node);
            }
        }

        return duration;
    }

    private int nextFreeLevel() {
        for (int i = 0; i < levels.length; i++) {
            if (levels[i].size() < width.getInteger())
                return i;
        }
        return 0;
    }

    /**
     * Initialization
     */
    private void doCyclicMST() {
        CyclicMSTLeveling mst = (CyclicMSTLeveling) CyclicMSTLeveling
                .getInstance(Config.MST_MIN, numberOfLevels.getInteger(), width
                        .getInteger());

        mst.attach(graph);

        try {
            mst.check();
        } catch (PreconditionException e) {
            e.printStackTrace();
        }

        mst.computeLevels();

        /* copy results to the array */
        bestLevelingEdgesLength = lengthOfEdges();

        Iterator<Node> it = graph.getNodesIterator();
        while (it.hasNext()) {
            Node node = it.next();
            if (node.getInDegree() != 0 || node.getOutDegree() != 0) {
                levelsArray[node.getInteger("node")] = node.getInteger("level");
            }
        }

        bestLeveling = levelsArray.clone();

    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Optimal Leveling";
    }

    /**
     * @param algorithm
     * @return a new instance of this class
     */
    public static AbstractCyclicLeveling getInstance(String algorithm,
            int levels, int width) {
        OptimalLeveling leveling = new OptimalLeveling();
        leveling.numberOfLevels = new IntegerParameter(levels,
                "Number of levels", "Number of levels", 1, 100, 2, 100);
        leveling.width = new IntegerParameter(width, "width", "width", 1, 100,
                2, 100);
        return leveling;
    }

    /**
     * Returns the numberOfRecursions.
     * 
     * @return the numberOfRecursions.
     */
    public long getNumberOfRecursions() {
        return numberOfRecursions;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
