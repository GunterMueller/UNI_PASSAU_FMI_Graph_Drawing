// =============================================================================
//
//   CyclicBFSLeveling.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.cyclicLeveling.sugiyama;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.util.Queue;

/**
 * @author Gergoe Lovasz
 * @version $Revision$ $Date$
 */
public class CyclicBFSLeveling extends AbstractCyclicLeveling {

    private int[] nextFreeLevel;

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    public void check() throws PreconditionException {
        if (graph.getNumberOfNodes() <= 0)
            throw new PreconditionException(
                    "The graph is empty. Cannot run BFS.");

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

    private void updateNextFreeLevel(int position) {
        /* no update necessary */
        if (levels[position].size() < width)
            return;

        /* find the next free level */
        boolean levelFound = false;
        int currentLevel = (position + 1) % levels.length;

        while (!levelFound) {
            if (levels[currentLevel].size() < width) {
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
    protected void levelNodes() {

        /* initialize containers for the nodes of each level */
        levels = new HashSet[numberOfLevels];
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
            setNodeLevel(v, bfsnum.get(v));

            /* add the node to the level */
            levels[getNodeLevel(v)].add(v);

            /* update nextFreeLevel if necessary */
            updateNextFreeLevel(getNodeLevel(v));

            while (!q.isEmpty()) {
                Node u = (Node) q.removeFirst();

                for (Iterator<Node> neighbors = u.getOutNeighborsIterator(); neighbors
                        .hasNext();) {
                    Node w = neighbors.next();
                    if (unmarkedNodes.contains(w)) {
                        unmarkedNodes.remove(w);
                        int level = computeBFSnum(bfsnum.get(u) + 1);
                        setNodeLevel(w, level);
                        bfsnum.put(w, level);

                        /* add the node to the level */
                        levels[level].add(w);
                        updateNextFreeLevel(level);
                        q.addLast(w);
                    }
                }
            }
        }
    }

    private class UnmarkedNodes extends HashSet<Node> {
        /**
         * 
         */
        private static final long serialVersionUID = 8716455388718325141L;

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

    private int getNextFreeLevel() {
        return nextFreeLevel[0];
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "CyclicBFSLeveling";
    }

    public Parameter<?>[] getAlgorithmParameters() {
        SelectionParameter selParam = new SelectionParameter("Start node",
                "BFS will start with the only selected node.");

        IntegerParameter numberOfLayers = new IntegerParameter(7, "Layers",
                "Number of layers", 1, 100, 2, 100);

        IntegerParameter nodesPerLayer = new IntegerParameter(6,
                "Nodes / Layer", "Maximum number of nodes for a level", 1, 50,
                1, 50);

        this.parameters = new Parameter[] { selParam, numberOfLayers,
                nodesPerLayer };
        return this.parameters;
    }

    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        selection = ((SelectionParameter) params[0]).getSelection();
        numberOfLevels = ((IntegerParameter) params[1]).getInteger();
        width = ((IntegerParameter) params[2]).getInteger();
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
