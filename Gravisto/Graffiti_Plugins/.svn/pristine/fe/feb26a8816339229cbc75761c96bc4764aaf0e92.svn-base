// =============================================================================
//
//   Leveling.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.radialTreeDrawing;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * This class provides operations for global leveling, i.e. positioning nodes of
 * the same graph theoretic depth on the same radius. Furthermore equidistant
 * leveling is possible.
 * 
 * @author Andreas Schindler
 * @version $Revision$ $Date$
 */
public class Leveling {

    /**
     * the father-son distance
     */
    private double fatherSonDistance;

    /**
     * this array contains in the ith entry the max node sizes on depth level i.
     */
    private double[] maxNodeSizes;

    /**
     * this array contains the radius for each depth level.
     */
    private double[] radii;

    /**
     * This method positions nodes of the same graph theoretic depth on the same
     * radius.
     * 
     * @param graph
     *            a graph
     * @param root
     *            the root node
     * @param fatherSonDistance
     *            the father son distance
     * @param equidistant
     *            make radii equidistant
     */
    public void globalLeveling(Graph graph, Node root,
            double fatherSonDistance, boolean equidistant) {

        this.fatherSonDistance = fatherSonDistance;

        resetVisited(graph);

        int treeDepth = calculateTreeDepth(root, 1);

        maxNodeSizes = new double[treeDepth];
        radii = new double[treeDepth];

        for (int i = 0; i < treeDepth; i++) {

            maxNodeSizes[i] = 0.0;
            radii[i] = 0.0;
        }

        resetVisited(graph);

        calculateMaxNodeSizes(root, 0);
        if (equidistant) {

            calculateEquidistantRadii();
        } else {

            calculateRadii();
        }

        resetVisited(graph);
        shiftNode(root, 0);
    }

    /**
     * calculates the tree depth recursivly
     * 
     * @param n
     *            a node
     * @param level
     *            current depth
     * @return the depth of the subtree n
     */
    private int calculateTreeDepth(Node n, int level) {

        n.setBoolean(Constants.VISITED, true);
        int maxDepth = level;
        for (Node x : n.getNeighbors()) {

            if (!x.getBoolean(Constants.VISITED)) {

                maxDepth = Math.max(maxDepth, calculateTreeDepth(x, level + 1));
            }
        }
        return maxDepth;
    }

    /**
     * calculates the maximum node sizes for each level
     * 
     * @param n
     *            a node
     * @param level
     *            the current level
     */
    private void calculateMaxNodeSizes(Node n, int level) {

        n.setBoolean(Constants.VISITED, true);

        double nodeSize = n.getDouble(Constants.SIZE);

        maxNodeSizes[level] = Math.max(maxNodeSizes[level], nodeSize);

        for (Node x : n.getNeighbors()) {

            if (!x.getBoolean(Constants.VISITED)) {

                calculateMaxNodeSizes(x, level + 1);
            }
        }
    }

    /**
     * calcutates the global radii for each level
     * 
     */
    private void calculateRadii() {

        for (int i = 1; i < maxNodeSizes.length; i++) {

            radii[i] = radii[i - 1] + maxNodeSizes[i - 1] + fatherSonDistance;
        }
    }

    /**
     * calculates the global radii for each level on equidistant circles
     * 
     */
    private void calculateEquidistantRadii() {

        double maxNodeSize = 0.0;
        for (int i = 0; i < maxNodeSizes.length; i++) {

            maxNodeSize = Math.max(maxNodeSize, maxNodeSizes[i]);
        }

        for (int i = 1; i < maxNodeSizes.length; i++) {

            radii[i] = radii[i - 1] + maxNodeSize + fatherSonDistance;
        }
    }

    /**
     * shifts the subtree n recursivly according to the calculated values in the
     * array radii.
     * 
     * @param n
     *            a node
     * @param level
     *            the depth level in the tree
     */
    private void shiftNode(Node n, int level) {

        n.setBoolean(Constants.VISITED, true);

        double nodeSize = n.getDouble(Constants.SIZE);

        if (level > 0) {

            n.setDouble(Constants.POLAR_RADIUS, radii[level] + (nodeSize / 2));
            n.setDouble(Constants.BORDERING_RADIUS, radii[level] + nodeSize);
        }

        for (Node x : n.getNeighbors()) {

            if (!x.getBoolean(Constants.VISITED)) {

                shiftNode(x, level + 1);
            }
        }
    }

    /**
     * resets the visited flag in the tree
     * 
     * @param graph
     *            a graph
     */
    private void resetVisited(Graph graph) {

        for (Node x : graph.getNodes()) {

            x.setBoolean(Constants.VISITED, false);
        }
    }
}
