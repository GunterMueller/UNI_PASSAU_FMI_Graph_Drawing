// =============================================================================
//
//   CyclicSELeveling.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.cyclicLeveling.sugiyama;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.cyclicLeveling.SEHeap;
import org.graffiti.plugins.algorithms.cyclicLeveling.test.Config;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;

/**
 * @author Gergoe Lovasz
 * @version $Revision$ $Date$
 */
public class CyclicSELeveling extends AbstractCyclicLeveling {

    /* strategy to initialize a start leveling */
    private String initializationMethod;

    /* the limit for the number of iterations */
    private int numberOfIterations;

    /* contains the nodes, sorted by the force attribute */
    private SEHeap heap;

    /* the best result so far (will be the current result) */
    private int bestLengthSoFar = Integer.MAX_VALUE;

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

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        if (graph.getNumberOfNodes() <= 0)
            throw new PreconditionException(
                    "The graph is empty. Cannot run CyclicSE.");

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

    /**
     * @param w
     * @return length of all incident edges of node w
     */
    private int edgesLength(Node w) {
        Iterator<Edge> it = w.getEdgesIterator();
        int edgesLength = 0;

        while (it.hasNext()) {
            Edge edge = it.next();
            int sLevel = getNodeLevel(edge.getSource());
            int tLevel = getNodeLevel(edge.getTarget());
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
    @Override
    public Parameter<?>[] getAlgorithmParameters() {

        SelectionParameter selParam = new SelectionParameter("Start node",
                "SE will start with the only selected node.");

        String[] params = { "MST", "RANDOM", "ZERO" };

        IntegerParameter numberOfLayers = new IntegerParameter(7, "Layers",
                "Number of layers", 1, 100, 2, 100);

        IntegerParameter nodesPerLayer = new IntegerParameter(6,
                "Nodes / Layer", "Maximum number of nodes for a level", 1, 50,
                1, 50);

        StringSelectionParameter initMethod = new StringSelectionParameter(
                params, "Initialization method",
                "Choose the method for the initialization of the Spring Embedder");

        this.parameters = new Parameter[] { selParam, numberOfLayers,
                nodesPerLayer, initMethod };
        return this.parameters;

    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        selection = ((SelectionParameter) params[0]).getSelection();
        numberOfLevels = ((IntegerParameter) params[1]).getInteger();
        width = ((IntegerParameter) params[2]).getInteger();
        initializationMethod = ((StringSelectionParameter) params[3])
                .getValue();
    }

    /**
     * Execute first the MST heuristics
     */
    private void MSTInit() {
        CyclicMSTLeveling mst = (CyclicMSTLeveling) CyclicMSTLeveling
                .getInstance(Config.MST_MIN, numberOfLevels, width);

        mst.attach(graph);

        try {
            mst.check();
        } catch (PreconditionException e) {
            e.printStackTrace();
        }
        mst.sourceNode = sourceNode;

        mst.levelNodes();

        Iterator<Node> it = graph.getNodesIterator();
        while (it.hasNext()) {
            Node node = it.next();
            levels[getNodeLevel(node)].add(node);

        }
    }

    /**
     * Set for each node a random level
     */
    private void randomInit() {
        Iterator<Node> it = graph.getNodesIterator();
        while (it.hasNext()) {
            Node node = it.next();
            setNodeLevel(node, randomLevel());
            levels[getNodeLevel(node)].add(node);
        }
    }

    /**
     * @return random level
     */
    private int randomLevel() {
        int randomLevel = 0;
        int level = (int) Math.round(Math.random() * (numberOfLevels - 1));
        boolean freeLevelFound = false;
        while (!freeLevelFound) {
            if (levels[level].size() < width) {
                freeLevelFound = true;
                randomLevel = level;
            } else {
                level = (level + 1) % numberOfLevels;
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
        levels[getNodeLevel(w)].remove(w);
        int optimalLevel = 0;
        int edgesLength = Integer.MAX_VALUE;
        for (int i = 0; i < numberOfLevels; i++) {
            if (levels[i].size() < width) {
                setNodeLevel(w, i);
                int lengthOfEdgesOfW = edgesLength(w);
                if (lengthOfEdgesOfW < edgesLength) {
                    optimalLevel = i;
                    edgesLength = lengthOfEdgesOfW;
                }
            }
        }
        setNodeLevel(w, optimalLevel);
        w.setDouble("force", edgesLength);
        levels[optimalLevel].add(w);

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
            int oldEdgeLength = length(nOldLevel, getNodeLevel(node));
            int newEdgeLength = length(getNodeLevel(n), getNodeLevel(node));
            int difference = newEdgeLength - oldEdgeLength;
            double newForce = oldForce + difference;
            /* update force and position in heap */
            heap.update(node, newForce);
        }

        Iterator<Node> in = n.getInNeighborsIterator();
        while (in.hasNext()) {
            Node node = in.next();
            double oldForce = node.getDouble("force");
            int oldEdgeLength = length(getNodeLevel(node), nOldLevel);
            int newEdgeLength = length(getNodeLevel(node), getNodeLevel(n));
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
            setNodeLevel(node, 0);
            levels[0].add(node);

        }
    }

    /**
     * Assigns a level to each node according to the spring embedder heuristic.
     */
    @Override
    public void levelNodes() {

        /* initialize containers for the nodes of each level */
        @SuppressWarnings("unchecked")
        HashSet<Node>[] levelsC = (HashSet<Node>[]) new HashSet<?>[numberOfLevels];
        levels = levelsC;
        for (int i = 0; i < levels.length; i++) {
            levels[i] = new HashSet<Node>();
        }

        /* initialize SE */
        if (initializationMethod.equals("RANDOM")) {
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
        } else if (initializationMethod.equals("MST")) {
            MSTInit();
        } else if (initializationMethod.equals("ZERO")) {
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
        while (improvement && i < numberOfIterations) {

            buildHeap();
            Node currentNode = null;

            while (!heap.isEmpty()) {
                currentNode = heap.removeMax();
                int oldLevel = getNodeLevel(currentNode);
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
