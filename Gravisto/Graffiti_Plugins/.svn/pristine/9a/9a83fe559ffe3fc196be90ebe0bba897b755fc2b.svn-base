// =============================================================================
//
//   CyclicBFSLeveling.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.cyclicLeveling;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.DoubleParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.util.Queue;

/**
 * @author Gerg� Lov�sz
 * @version $Revision$ $Date$
 */
public class CyclicBFSLeveling extends AbstractCyclicLeveling {

    private int[] nextFreeLevel;

    public CyclicBFSLeveling() {

        numberOfLevels = new IntegerParameter(7, "Number of levels",
                "Number of levels", 1, 100, 2, 100);
        width = new IntegerParameter(6, "The width of a level",
                "Maximum number of nodes for a level", 1, 50, 1, 50);
        centerX = new DoubleParameter(0d, "center (x)",
                "x coordinate of the center of the graph", 0d, 1000d, 0d, 1000d);
        centerY = new DoubleParameter(0d, "center (y)",
                "y coordinate of the center of the graph", 0d, 1000d, 0d, 1000d);
        minDistance = new IntegerParameter(50, "node distance",
                "minimum distance between two nodes", 10, 60, 10, 60);

    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    public void check() throws PreconditionException {
        if (graph.getNumberOfNodes() <= 0)
            throw new PreconditionException(
                    "The graph is empty. Cannot run BFS.");

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

    private void updateNextFreeLevel(int position) {
        /* no update necessary */
        if (levels[position].size() < width.getInteger())
            return;

        /* find the next free level */
        boolean levelFound = false;
        int currentLevel = (position + 1) % levels.length;

        while (!levelFound) {
            if (levels[currentLevel].size() < width.getInteger()) {
                levelFound = true;
            } else {
                currentLevel = (currentLevel + 1) % levels.length;
            }
        }
        nextFreeLevel[position] = currentLevel;

        /* update entries in nextFreeLevel which point to position */
        for (int i = 0; i < nextFreeLevel.length; i++) {
            if (nextFreeLevel[i] == position) {
                nextFreeLevel[i] = currentLevel;
            }
        }
    }

    /**
     * @param currentLevel
     * @return level
     */
    private int computeBFSnum(int currentLevel) {
        currentLevel = currentLevel % levels.length;
        return nextFreeLevel[currentLevel];
    }

    /**
     * Computes a leveling according to this heuristics
     */
    @Override
    @SuppressWarnings("unchecked")
    public long computeLevels() {

        /* the start time */
        long start = System.currentTimeMillis();

        /* initialize containers for the nodes of each level */
        levels = new HashSet[numberOfLevels.getInteger()];
        for (int i = 0; i < levels.length; i++) {
            levels[i] = new HashSet<Node>();
        }

        /* initialize the pointers to the next free level */
        nextFreeLevel = new int[levels.length];
        for (int i = 0; i < nextFreeLevel.length; i++) {
            nextFreeLevel[i] = i;
        }

        /* contains the bfsnum */
        Map<Node, Integer> bfsnum = new HashMap<Node, Integer>();

        /* queue Q */
        Queue q = new Queue();

        /* foreach v do v.marked = false */
        UnmarkedNodes unmarkedNodes = new UnmarkedNodes();

        Iterator<Node> nodeIt = graph.getNodesIterator();
        while (nodeIt.hasNext()) {
            Node n = nodeIt.next();
            unmarkedNodes.add(n);
        }

        /*
         * needed to determine whether the source node has been leveled or not
         */
        boolean firstTime = true;

        /* while not all nodes are marked do */
        while (!unmarkedNodes.isEmpty()) {
            /* v = next_unmarked_node */
            Node v;
            if (firstTime) {
                v = sourceNode;
                unmarkedNodes.remove(sourceNode);
                firstTime = false;
            } else {
                v = unmarkedNodes.removeNextUnmarked();
            }
            /* Q.append(v) */
            q.addLast(v);
            /* v.bfsnum = numbfs */
            bfsnum.put(v, getNextFreeLevel());
            v.setInteger("level", bfsnum.get(v));

            /* add the node to the level */
            levels[v.getInteger("level")].add(v);

            /* update nextFreeLevel if necessary */
            updateNextFreeLevel(v.getInteger("level"));

            while (!q.isEmpty()) {
                Node u = (Node) q.removeFirst();

                for (Iterator<Node> neighbors = u.getOutNeighborsIterator(); neighbors
                        .hasNext();) {
                    Node w = neighbors.next();
                    if (unmarkedNodes.contains(w)) {
                        unmarkedNodes.remove(w);
                        int level = computeBFSnum(bfsnum.get(u) + 1);
                        w.setInteger("level", level);
                        bfsnum.put(w, level);

                        /* add the node to the level */
                        levels[level].add(w);
                        updateNextFreeLevel(level);
                        q.addLast(w);
                    }
                }
            }
        }

        /* The time after the computation of the levels */
        long end = System.currentTimeMillis();
        return (end - start);
    }

    private class UnmarkedNodes extends HashSet<Node> {
        /**
         * 
         */
        private static final long serialVersionUID = 6846781844458926710L;

        public UnmarkedNodes() {
            super();
        }

        public Node removeNextUnmarked() {
            Iterator<Node> it = this.iterator();
            if (it.hasNext()) {
                Node n = it.next();
                it.remove();
                return n;
            } else
                return null;
        }
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "CyclicBFSLeveling";
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @SuppressWarnings("unchecked")
    public Parameter<?>[] getAlgorithmParameters() {
        SelectionParameter selParam = new SelectionParameter("Start node",
                "BFS will start with the only selected node.");

        return new Parameter[] { selParam, numberOfLevels, width, centerX,
                centerY, minDistance };
    }

    private int getNextFreeLevel() {
        return nextFreeLevel[0];
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @SuppressWarnings("unchecked")
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        selection = ((SelectionParameter) params[0]).getSelection();

    }

    /**
     * @param algorithm
     * @return a new instance of this class
     */
    public static AbstractCyclicLeveling getInstance(String algorithm,
            int levels, int width) {
        CyclicBFSLeveling leveling = new CyclicBFSLeveling();
        leveling.numberOfLevels = new IntegerParameter(levels,
                "Number of levels", "Number of levels", 1, 100, 2, 100);
        leveling.width = new IntegerParameter(width, "width", "width", 1, 100,
                2, 100);
        return leveling;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
