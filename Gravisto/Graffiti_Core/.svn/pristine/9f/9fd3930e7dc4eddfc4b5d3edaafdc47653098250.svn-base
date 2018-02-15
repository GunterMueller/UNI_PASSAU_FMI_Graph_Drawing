// =============================================================================
//
//   CyclicSELeveling.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.cyclicLeveling;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.DoubleParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.cyclicLeveling.test.Config;

/**
 * @author Gerg� Lov�sz
 * @version $Revision$ $Date$
 */
public class CyclicSELeveling extends AbstractCyclicLeveling {

    /**
     * @param algorithm
     * @return a new instance of this class
     */
    public static AbstractCyclicLeveling getInstance(String algorithm,
            int levels, int width) {
        CyclicSELeveling leveling = new CyclicSELeveling();
        leveling.numberOfLevels = new IntegerParameter(levels,
                "Number of levels", "Number of levels", 1, 100, 2, 100);
        leveling.width = new IntegerParameter(width, "width", "width", 1, 100,
                2, 100);

        StringTokenizer tokenizer = new StringTokenizer(algorithm, "_");
        /* SE */
        tokenizer.nextToken();

        /* The method */
        String method = tokenizer.nextToken();
        if (method.equals("RANDOM")) {
            String[] param = { "RANDOM" };
            StringSelectionParameter initMethod = new StringSelectionParameter(
                    param, "", "");
            initMethod.setValue("RANDOM");
            leveling.initializationMethod = initMethod;
        } else if (method.equals("ZERO")) {
            String[] param = { "ZERO" };
            StringSelectionParameter initMethod = new StringSelectionParameter(
                    param, "", "");
            initMethod.setValue("ZERO");
            leveling.initializationMethod = initMethod;
        } else if (method.equals("MST")) {
            String[] param = { "MST" };
            StringSelectionParameter initMethod = new StringSelectionParameter(
                    param, "", "");
            initMethod.setValue("MST");
            leveling.initializationMethod = initMethod;
        }

        return leveling;
    }

    /* the number of iterations */
    private IntegerParameter numberOfIterations;

    /* strategy to initialize a start leveling */
    private StringSelectionParameter initializationMethod;

    /* contains the nodes, sorted by the force attribute */
    private SEHeap heap;

    /* the best result so far (will be the current result) */
    private int bestLengthSoFar = Integer.MAX_VALUE;

    public CyclicSELeveling() {

        String[] params = { "MST", "RANDOM", "ZERO" };

        numberOfLevels = new IntegerParameter(7, "Number of Levels",
                "Number of Levels", 1, 100, 2, 100);
        width = new IntegerParameter(6, "The width of a level",
                "Maximum number of nodes for a level", 1, 50, 1, 50);
        centerX = new DoubleParameter(0d, "center (x)",
                "x coordinate of the center of the graph", 0d, 1000d, 0d, 1000d);
        centerY = new DoubleParameter(0d, "center (y)",
                "y coordinate of the center of the graph", 0d, 1000d, 0d, 1000d);
        minDistance = new IntegerParameter(50, "node distance",
                "minimum distance between two nodes", 10, 100, 10, 100);
        numberOfIterations = new IntegerParameter(100, "Number of Iterations",
                "Number of Iterations", 1, 1000, 1, 1000);
        initializationMethod = new StringSelectionParameter(params, "Method",
                "Choose the method for the initialization of the Spring Embedder");
    }

    /**
     * Computes the force for each node, and adds them to a heap.
     */
    private void buildHeap() {

        heap = new SEHeap(new SENodeComperator());

        Iterator<Node> it = graph.getNodesIterator();
        while (it.hasNext()) {
            Node n = it.next();
            heap.add(n);
        }
    }

    /* compares the force attribute of 2 nodes */
    private class SENodeComperator implements Comparator<Node> {
        /*
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Node n1, Node n2) {
            if (n1.getDouble("force") < n2.getDouble("force"))
                return -1;
            else if (n1.getDouble("force") > n2.getDouble("force"))
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
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        if (graph.getNumberOfNodes() <= 0)
            throw new PreconditionException(
                    "The graph is empty. Cannot run CyclicSE.");

        if (numberOfLevels.getInteger() <= 0)
            throw new PreconditionException(
                    "the number of levels must be at least 1");

        if (width.getInteger() <= 0)
            throw new PreconditionException(
                    "Each level must contain at least 1 node");

        if (graph.getNumberOfNodes() > width.getInteger()
                * numberOfLevels.getInteger())
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

    /*
     * @see
     * org.graffiti.plugins.algorithms.cyclicLeveling.AbstractCyclicLeveling
     * #computeLevels()
     */
    @SuppressWarnings("unchecked")
    @Override
    public long computeLevels() {
        long start = System.currentTimeMillis();

        /* initialize containers for the nodes of each level */
        levels = new HashSet[numberOfLevels.getInteger()];
        for (int i = 0; i < levels.length; i++) {
            levels[i] = new HashSet<Node>();
        }

        /* initialize SE */
        if (initializationMethod.getValue().equals("RANDOM")) {
            randomInit();

            /* initialize data structures for SE */
            int nodeID = 0;
            Iterator<Node> it = graph.getNodesIterator();
            while (it.hasNext()) {
                Node n = it.next();
                /* Set nodeID */
                n.setInteger("nodeID", nodeID);
                nodeID++;
            }
        } else if (initializationMethod.getValue().equals("MST")) {
            MSTInit();
        } else if (initializationMethod.getValue().equals("ZERO")) {
            ZeroInit();
        }

        /* the simulation of the spring embedder */

        /* compute the initial forces */
        Iterator<Node> it = graph.getNodesIterator();
        while (it.hasNext()) {
            Node n = it.next();
            n.setDouble("force", getForce(n));
        }

        boolean improvement = true;
        int i = 0;
        while (improvement && i < numberOfIterations.getInteger()) {

            buildHeap();
            Node currentNode = null;

            while (!heap.isEmpty()) {
                currentNode = heap.removeMax();
                int oldLevel = currentNode.getInteger("level");
                setOptimalLevel(currentNode);
                updateNeighbours(currentNode, oldLevel);
            }

            int l = lengthOfEdges();
            if (l == bestLengthSoFar) {
                improvement = false;
            }
            bestLengthSoFar = l;
            i++;
        }
        long end = System.currentTimeMillis();
        return (end - start);
    }

    /**
     * @param w
     * @return length of all incident edges of node w
     */
    private int edgesLength(Node w) {
        Iterator<Edge> it = w.getEdgesIterator();
        int edgesLength = 0;

        while (it.hasNext()) {
            Edge edge = it.next();
            int sLevel = edge.getSource().getInteger("level");
            int tLevel = edge.getTarget().getInteger("level");
            edgesLength += length(sLevel, tLevel);
        }

        return edgesLength;
    }

    /**
     * @param n
     * @return the force for node n
     */
    private double getForce(Node n) {
        return edgesLength(n);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "CyclicSELeveling";
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        SelectionParameter selParam = new SelectionParameter("Start node",
                "SE will start with the only selected node.");

        return new Parameter[] { selParam, numberOfLevels, width, centerX,
                centerY, minDistance, numberOfIterations, initializationMethod };
    }

    /**
     * Execute first the MST heuristics
     */
    private void MSTInit() {
        CyclicMSTLeveling mst = (CyclicMSTLeveling) CyclicMSTLeveling
                .getInstance(Config.MST_MINA, numberOfLevels.getInteger(),
                        width.getInteger());

        mst.attach(graph);

        try {
            mst.check();
        } catch (PreconditionException e) {
            e.printStackTrace();
        }
        mst.sourceNode = sourceNode;

        mst.computeLevels();

        this.levels = mst.levels;

    }

    /**
     * Set for each node a random level
     */
    private void randomInit() {
        Iterator<Node> it = graph.getNodesIterator();
        while (it.hasNext()) {
            Node node = it.next();
            node.setInteger("level", randomLevel());
            levels[node.getInteger("level")].add(node);

        }
    }

    /**
     * @return random level
     */
    private int randomLevel() {
        int randomLevel = 0;
        int level = (int) Math.round(Math.random()
                * (numberOfLevels.getInteger() - 1));
        boolean freeLevelFound = false;
        while (!freeLevelFound) {
            if (levels[level].size() < width.getInteger()) {
                freeLevelFound = true;
                randomLevel = level;
            } else {
                level = (level + 1) % numberOfLevels.getInteger();
            }
        }
        return randomLevel;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        graph = null;
    }

    /**
     * Computes and sets an optimal level for w. Optimal means that the force on
     * the level is smaller than on any other level.
     * 
     * @param w
     */
    private void setOptimalLevel(Node w) {
        levels[w.getInteger("level")].remove(w);
        int optimalLevel = 0;
        int edgesLength = Integer.MAX_VALUE;
        for (int i = 0; i < numberOfLevels.getInteger(); i++) {
            if (levels[i].size() < width.getInteger()) {
                w.setInteger("level", i);
                int lengthOfEdgesOfW = edgesLength(w);
                if (lengthOfEdgesOfW < edgesLength) {
                    optimalLevel = i;
                    edgesLength = lengthOfEdgesOfW;
                }
            }
        }
        w.setInteger("level", optimalLevel);
        w.setDouble("force", edgesLength);
        levels[optimalLevel].add(w);

    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        selection = ((SelectionParameter) params[0]).getSelection();
    }

    /**
     * Computes the force for each neighbor of n
     * 
     * @param n
     *            Node
     */
    private void updateNeighbours(Node n, int nOldLevel) {
        Iterator<Node> out = n.getOutNeighborsIterator();
        while (out.hasNext()) {
            Node node = out.next();
            double oldForce = node.getDouble("force");
            int oldEdgeLength = length(nOldLevel, node.getInteger("level"));
            int newEdgeLength = length(n.getInteger("level"), node
                    .getInteger("level"));
            int difference = newEdgeLength - oldEdgeLength;
            double newForce = oldForce + difference;
            /* update force and position in heap */
            heap.update(node, newForce);
        }

        Iterator<Node> in = n.getInNeighborsIterator();
        while (in.hasNext()) {
            Node node = in.next();
            double oldForce = node.getDouble("force");
            int oldEdgeLength = length(node.getInteger("level"), nOldLevel);
            int newEdgeLength = length(node.getInteger("level"), n
                    .getInteger("level"));
            int difference = newEdgeLength - oldEdgeLength;
            double newForce = oldForce + difference;
            /* update force and position in heap */
            heap.update(node, newForce);
        }
    }

    /**
     * Initialize each node on level 0
     */
    private void ZeroInit() {
        Iterator<Node> it = graph.getNodesIterator();
        while (it.hasNext()) {
            Node node = it.next();
            node.setInteger("level", 0);
            levels[0].add(node);

        }
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
