// =============================================================================
//
//   CyclicMSTLeveling.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.cyclicLeveling.sugiyama;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.cyclicLeveling.MSTHeap;

/**
 * @author Gergoe Lovasz
 * @version $Revision$ $Date$
 */
public class CyclicMSTLeveling extends AbstractCyclicLeveling {

    /* quadratic weighting of the length of an edge */
    private boolean quadratic;

    /* the gray nodes block the level */
    private boolean blocking;

    /*
     * strategy to compute the distance of a node to the already placed (BLACK)
     * nodes
     */
    private String distanceFunction;

    /* data structures for the MST algorithm */
    /* status of the nodes which are already placed */
    private static final int BLACK = 0;

    /* status of the nodes which were not visited */
    private static final int WHITE = 1;

    /* status of the nodes which are in 'Rand' */
    private static final int GRAY = 2;

    /* data structure for MST */
    private MSTHeap heap;

    /* contains the unmarked nodes */
    private HashSet<Node> white;

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "CyclicMSTLeveling";
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @SuppressWarnings("unchecked")
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        selection = ((SelectionParameter) params[0]).getSelection();
        numberOfLevels = ((IntegerParameter) params[1]).getInteger();
        width = ((IntegerParameter) params[2]).getInteger();
        distanceFunction = ((StringSelectionParameter) params[3]).getValue();
        quadratic = ((BooleanParameter) params[4]).getValue();
        blocking = ((BooleanParameter) params[5]).getValue();
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @SuppressWarnings("unchecked")
    public Parameter<?>[] getAlgorithmParameters() {

        SelectionParameter selParam = new SelectionParameter("Start node",
                "CyclicMST will start with the only selected node.");

        String[] params = { "MINA", "MAXA", "MIN", "MAX" };

        IntegerParameter numberOfLayers = new IntegerParameter(7, "Layers",
                "Number of layers", 1, 100, 2, 100);

        IntegerParameter nodesPerLayer = new IntegerParameter(6,
                "Nodes / Layer", "Maximum number of nodes for a level", 1, 50,
                1, 50);

        StringSelectionParameter distanceFunction = new StringSelectionParameter(
                params,
                "Method",
                "Choose the method for the computation of the distance function of the MST algorithm: MIN, MAX");

        BooleanParameter quadratic = new BooleanParameter(false, "quadratic",
                "quadratic weighting of the distance between 2 nodes");

        BooleanParameter blocking = new BooleanParameter(false, "blocking",
                "gray nodes block their current position");

        this.parameters = new Parameter[] { selParam, numberOfLayers,
                nodesPerLayer, distanceFunction, quadratic, blocking };

        return this.parameters;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    public void check() throws PreconditionException {
        if (graph.getNumberOfNodes() <= 0)
            throw new PreconditionException(
                    "The graph is empty. Cannot run CyclicMST.");

        if (numberOfLevels <= 0)
            throw new PreconditionException(
                    "the number of levels must be at least 1");

        if (width <= 0)
            throw new PreconditionException(
                    "Each level must contain at least 1 node");

        if (graph.getNumberOfNodes() > width * numberOfLevels)
            throw new PreconditionException(
                    "Not enough levels or too small width");

        if ((selection == null) || (selection.getNodes().size() != 1)) {
            int numberOfNodes = this.graph.getNumberOfNodes();
            int randomNode = (int) Math
                    .round((Math.random() * (numberOfNodes - 1)));
            sourceNode = graph.getNodes().get(randomNode);
        } else {
            sourceNode = selection.getNodes().get(0);
        }
    }

    /* assigns a level to each node, the heart of the MST algorithm */
    protected void levelNodes() {

        /* initialize containers for the nodes of each level */
        levels = new HashSet[numberOfLevels];
        for (int i = 0; i < levels.length; i++) {
            levels[i] = new HashSet<Node>();
        }

        /* initialize data structures for the MST algorithm */
        white = new HashSet<Node>();

        int nodeID = 0;
        Iterator<Node> it = graph.getNodesIterator();
        while (it.hasNext()) {
            Node n = it.next();
            /* Set nodeID */
            n.setInteger("nodeID", nodeID);
            nodeID++;
            /* Set status to WHITE */
            n.setInteger("status", WHITE);
            white.add(n);
            /* Set distance to infinity */
            n.setDouble("distance", Double.MAX_VALUE);
            /* Set all nodes to level 0 */
            setNodeLevel(n, 0);
        }

        sourceNode.setDouble("distance", 0);

        /* initialize heap */
        heap = new MSTHeap(new MSTNodeComperator());
        heap.add(sourceNode);

        Node v;

        while (heap.size() != 0 || !white.isEmpty()) {
            /* nodes with status WHITE exist */
            if (heap.size() == 0) {
                Node nextWhite = getNextUnvisited();
                nextWhite.setDouble("distance", 0);
                heap.add(nextWhite);
            }

            v = heap.removeMin();
            if (v.getInteger("status") == WHITE) {
                white.remove(v);
            } else if (blocking && v.getInteger("status") == GRAY) {
                levels[getNodeLevel(v)].remove(v);
            }
            v.setInteger("status", BLACK);
            setNodeLevel(v, computeOptimalLevel(v));
            levels[getNodeLevel(v)].add(v);

            Iterator<Node> it2 = v.getNeighborsIterator();
            while (it2.hasNext()) {
                Node w = it2.next();

                if (w.getInteger("status") == WHITE) {
                    /* compute optimal level & length of the edges */
                    int level = computeOptimalLevel(w);
                    double dist = computeDistance(w, level);

                    white.remove(w);
                    w.setInteger("status", GRAY);
                    w.setDouble("distance", dist);
                    heap.add(w);

                    /* block the computed level */
                    if (blocking) {
                        setNodeLevel(w, level);
                        levels[level].add(w);
                    }
                } else if (w.getInteger("status") == GRAY) {
                    /* compute optimal level & length of the edges */
                    int level = computeOptimalLevel(w);
                    double dist = computeDistance(w, level);

                    /* remove node from old level */
                    if (blocking) {
                        levels[getNodeLevel(w)].remove(w);
                    }

                    /*
                     * the distance will be set in the update method of the heap
                     * data structure
                     */
                    heap.update(w, dist);

                    /* block the computed level */
                    if (blocking) {
                        setNodeLevel(w, level);
                        levels[level].add(w);
                    }
                }
            }
        }
    }

    /**
     * Returns a node with status WHITE. Needed if the graph is not connected
     * 
     * @return Node
     */
    private Node getNextUnvisited() {
        Iterator<Node> it = white.iterator();
        if (it.hasNext())
            return it.next();
        else
            return null;
    }

    /* compares the distance attribute of 2 nodes */
    private class MSTNodeComperator implements Comparator<Node> {
        /*
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Node n1, Node n2) {
            if (n1.getDouble("distance") < n2.getDouble("distance"))
                return -1;
            else if (n1.getDouble("distance") > n2.getDouble("distance"))
                return 1;
            else {
                if (n1.getInteger("nodeID") < n2.getInteger("nodeID"))
                    return -1;
                else if (n1.getInteger("nodeID") > n2.getInteger("nodeID"))
                    return 1;
                else
                    return 0;
            }
        }
    }

    /**
     * This method computes for w on level the distance which is needed to
     * determine which node will be placed next
     * 
     * @param w
     *            node
     * @param level
     *            level
     * @return double
     */
    private double computeDistance(Node w, int level) {
        if (distanceFunction.equals("MIN"))
            return computeMinDist(w, level);
        else if (distanceFunction.equals("MAX"))
            return computeMaxDist(w, level);
        else if (distanceFunction.equals("MINA"))
            return computeMinADist(w, level);
        else if (distanceFunction.equals("MAXA"))
            return computeMaxADist(w, level);
        return Double.MAX_VALUE;
    }

    /**
     * The MAX strategy prefers the nodes which have a high average distance to
     * the BLACK nodes. These nodes will be placed first.
     * 
     * @param w
     * @param level
     */
    private double computeMaxADist(Node w, int level) {
        double distance = 0.0;
        int numberOfEdges = 0;
        Iterator<Node> iterator = w.getInNeighborsIterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            if (node.getInteger("status") == BLACK) {
                distance += length(getNodeLevel(node), level);
                numberOfEdges++;
            }
        }
        Iterator<Node> iterator2 = w.getOutNeighborsIterator();
        while (iterator2.hasNext()) {
            Node node = iterator2.next();
            if (node.getInteger("status") == BLACK) {
                distance += length(level, getNodeLevel(node));
                numberOfEdges++;
            }
        }
        distance = distance / numberOfEdges;
        return 1 / distance;
    }

    /**
     * The MINA strategy prefers nodes with a small average distance to the
     * BLACK nodes. Nodes with the smallest average distance will be placed
     * first.
     * 
     * @param w
     * @param level
     */
    private double computeMinADist(Node w, int level) {
        double distance = 0.0;
        int numberOfEdges = 0;
        Iterator<Node> iterator = w.getInNeighborsIterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            if (node.getInteger("status") == BLACK) {

                distance += length(getNodeLevel(node), level);
                numberOfEdges++;
            }
        }
        Iterator<Node> iterator2 = w.getOutNeighborsIterator();
        while (iterator2.hasNext()) {
            Node node = iterator2.next();
            if (node.getInteger("status") == BLACK) {
                distance += length(level, getNodeLevel(node));
                numberOfEdges++;
            }
        }
        return distance / numberOfEdges;
    }

    /**
     * The MIN strategy prefers nodes with a small average distance to the BLACK
     * nodes. Nodes with the smallest distance will be placed first.
     * 
     * @param w
     * @param level
     */
    private double computeMinDist(Node w, int level) {
        double distance = 0.0;
        Iterator<Node> iterator = w.getInNeighborsIterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            if (node.getInteger("status") == BLACK) {

                distance += length(getNodeLevel(node), level);
            }
        }
        Iterator<Node> iterator2 = w.getOutNeighborsIterator();
        while (iterator2.hasNext()) {
            Node node = iterator2.next();
            if (node.getInteger("status") == BLACK) {
                distance += length(level, getNodeLevel(node));
            }
        }
        return distance;
    }

    /**
     * The MAX strategy prefers the nodes which have a high average distance to
     * the BLACK nodes. These nodes will be placed first.
     * 
     * @param w
     * @param level
     */
    private double computeMaxDist(Node w, int level) {
        double distance = 0.0;
        Iterator<Node> iterator = w.getInNeighborsIterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            if (node.getInteger("status") == BLACK) {
                distance += length(getNodeLevel(node), level);
            }
        }
        Iterator<Node> iterator2 = w.getOutNeighborsIterator();
        while (iterator2.hasNext()) {
            Node node = iterator2.next();
            if (node.getInteger("status") == BLACK) {
                distance += length(level, getNodeLevel(node));
            }
        }
        return 1 / distance;
    }

    /**
     * Computes an optimal level for w. Optimal means that the sum of the length
     * of the incoming and outgoing edges is smaller than on any other level.
     * 
     * @param w
     * @return int level
     */
    private int computeOptimalLevel(Node w) {
        int optimalLevel = 0;
        double optimalDist = Double.MAX_VALUE;
        LinkedList<Integer> levelsWithOptDist = new LinkedList<Integer>();

        for (int i = 0; i < numberOfLevels; i++) {
            if (levels[i].size() < width) {
                double temp = sumOfEdgeslength(w, i);
                if (temp < optimalDist) {
                    optimalLevel = i;
                    optimalDist = temp;
                    levelsWithOptDist.clear();
                } else if (temp == optimalDist) {
                    levelsWithOptDist.add(i);
                }
            }
        }

        /*
         * If quadratic weighting of the edges test the levels with same
         * distance and chose the one with the minimum quadratic distance
         */
        if (quadratic && w.getInDegree() == w.getOutDegree()) {
            double quadOptimalDist = sumOfEdgeslengthQuad(w, optimalLevel);

            Iterator<Integer> it = levelsWithOptDist.iterator();
            while (it.hasNext()) {
                int currentLevel = it.next();
                double quadTemp = sumOfEdgeslengthQuad(w, currentLevel);
                if (quadTemp < quadOptimalDist) {
                    optimalLevel = currentLevel;
                    quadOptimalDist = quadTemp;
                }
            }
        }

        return optimalLevel;
    }

    /**
     * @return the length of all incoming and outgoing edges of w to the set of
     *         all BLACK nodes if w is on level i
     */
    private double sumOfEdgeslength(Node w, int i) {
        int sum = 0;
        Iterator<Node> it1 = w.getInNeighborsIterator();
        while (it1.hasNext()) {
            Node node = it1.next();
            if (node.getInteger("status") == BLACK) {
                sum += length(getNodeLevel(node), i);
            }
        }
        Iterator<Node> it2 = w.getOutNeighborsIterator();
        while (it2.hasNext()) {
            Node node = it2.next();
            if (node.getInteger("status") == BLACK) {
                sum += length(i, getNodeLevel(node));
            }
        }

        return sum;
    }

    /**
     * @return the quadratic length of all incoming and outgoing edges of w to
     *         the set of all BLACK nodes if w is on level i
     */
    private double sumOfEdgeslengthQuad(Node w, int i) {
        int sum = 0;
        Iterator<Node> it1 = w.getInNeighborsIterator();
        while (it1.hasNext()) {
            Node node = it1.next();
            if (node.getInteger("status") == BLACK) {
                sum += Math.pow(length(getNodeLevel(node), i), 2);
            }
        }
        Iterator<Node> it2 = w.getOutNeighborsIterator();
        while (it2.hasNext()) {
            Node node = it2.next();
            if (node.getInteger("status") == BLACK) {
                sum += Math.pow(length(i, getNodeLevel(node)), 2);
            }
        }

        return sum;
    }

    /**
     * @param algorithm
     * @return a new instance of this class
     */
    public static AbstractCyclicLeveling getInstance(String algorithm,
            int levels, int width) {
        CyclicMSTLeveling leveling = new CyclicMSTLeveling();
        leveling.numberOfLevels = levels;
        leveling.width = width;
        leveling.blocking = true;

        StringTokenizer tokenizer = new StringTokenizer(algorithm, "_");
        /* MST */
        tokenizer.nextToken();

        leveling.distanceFunction = tokenizer.nextToken();

        /* quadratic? */
        if (tokenizer.hasMoreTokens()) {
            leveling.quadratic = true;
        }
        return leveling;
    }

    public boolean supportsAlgorithmType(String algorithmType) {
        return algorithmType.equals(SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA);
    }

    public boolean supportsBigNodes() {
        return false;
    }

    public boolean supportsConstraints() {
        return false;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
