/**
 * 
 */
package org.graffiti.plugins.algorithms.graviso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

import org.graffiti.graph.Graph;

/**
 * This class implements the refinement portion of the isomorphism algorithm
 * utilizing BFS.
 * 
 * @author lenhardt
 * 
 */
public class IsoGraphBFS extends IsoGraphSimple {

    // bfsLevels[start node][level of node]
    private int[][] bfsLevels;

    // singletons[node number] = true if size(color class(node)) = 1
    private boolean[] singletons;

    // this is a hack to be able to memorize BFS levels; for the two graphs, one
    // IsoGraphsBFS is instantiated with 0, the other with 1 for that value
    private int instance;

    // "cache control": in other branches of the Backtracking Tree, we might
    // already have computed BFS from some nodes; no need to recompute them / or
    // discard them, because here we memorize if they have already been
    // computed; the levels for this instance have already been computed, then
    // bfsLevelsComputedFor[instance] = true.
    private static boolean[][] bfsLevelsComputedFor;

    /**
     * 
     * @param g
     * @param directed
     *            true if edge direction is to be regarded
     * @param instance
     *            0 for one graph, 1 for the other
     * @throws GravIsoException
     */
    public IsoGraphBFS(Graph g, boolean directed, int instance)
            throws GravIsoException {
        super(g, directed);
        bfsLevels = new int[g.getNumberOfNodes()][g.getNumberOfNodes()];
        singletons = new boolean[g.getNumberOfNodes()];
        Arrays.fill(singletons, false);
        if (instance != 1 && instance != 0)
            throw new GravIsoException(
                    "Programmfehler. IsoGraphBFS wurde mit identifier != 0, 1 instanziert");
        this.instance = instance;
        bfsLevelsComputedFor = new boolean[2][numberOfNodes];
    }

    @Override
    protected void recolour() {

        // search new nodes with |color class| = 1
        boolean resort = false;
        for (LinkedList<SortableNode> cClass : colorClasses) {
            int nodeNum = cClass.getFirst().getNodeNumber();
            // but only the ones we havn't computed yet
            if ((cClass.size() == 1) && (singletons[nodeNum] == false)) {
                if (bfsLevelsComputedFor[instance][nodeNum] == false) {
                    bfsLevels[nodeNum] = computeBFSLevels(nodeNum);
                    bfsLevelsComputedFor[instance][nodeNum] = true;
                    resort = true;
                }
                singletons[nodeNum] = true;
            }
        }

        if (resort) {
            // we have to resort the nodes within the color class since new BFS
            // info has been found
            for (LinkedList<SortableNode> nodes : colorClasses) {
                Collections.sort(nodes);
            }
        }
        super.recolour();
    }

    @Override
    public IsoGraphBFS clone() throws CloneNotSupportedException {
        IsoGraphBFS c = (IsoGraphBFS) super.clone();
        c.bfsLevels = bfsLevels.clone();
        c.singletons = singletons.clone();
        return c;
    }

    @Override
    public LinkedList<SortableNode> computeBestColorClass() {
        // in each color class, all nodes have equal distance to the singletons
        // for which bfs has been computed from
        int maxSum = -1;
        LinkedList<SortableNode> result = new LinkedList<SortableNode>();
        for (LinkedList<SortableNode> cClass : colorClasses) {
            if (cClass.size() > 1) {
                int sum = 0;
                SortableNodeBFS bfsNode = (SortableNodeBFS) cClass.getFirst();
                int[] dist = bfsNode.getDistanceFromSingletons();
                for (int i : dist) {
                    sum += i;
                }
                if (sum > maxSum) {
                    maxSum = sum;
                    result = cClass;
                }
            }
        }
        return result;
    }

    /**
     * Returns an array of the form colorClass[color number]->List(node 1, node
     * 2, node 3) with the nodes being wrapped in the SortableNode class, so
     * that they are sorted according to their degree vectors
     * 
     * The -BFS type implementation is the same as the -Simple, except that the
     * SortableNodes are SortableNodesBFS, which are also sorted by BFSLevels
     */
    @Override
    protected void computeColorClasses() {
        colorClasses = new ArrayList<LinkedList<SortableNode>>(
                (super.numberOfColors));
        colorClasses.ensureCapacity(super.numberOfColors);
        for (int i = 0; i < super.numberOfColors; i++) {
            colorClasses.add(i, new LinkedList<SortableNode>());
        }
        for (int nodeNum = 0; nodeNum < nodeColors.length; nodeNum++) {
            int color = nodeColors[nodeNum];
            colorClasses.get(color).add(new SortableNodeBFS(nodeNum));
        }
        for (LinkedList<SortableNode> nodes : colorClasses) {
            Collections.sort(nodes);
        }
    }

    /**
     * computes the BFS Level from startNode on the graph given
     * 
     * @return an int[] array holding the BFS level of the nodes
     */
    private int[] computeBFSLevels(int startNode) {
        LinkedList<Integer> q = new LinkedList<Integer>();
        // logger.warning("bfs'ing" + bfsCount + " node: " + startNode
        // + " single: " + Arrays.toString(singletons));
        int[] result = new int[super.numberOfNodes];
        // start node is on BFS level 0
        int level = 0;
        q.addLast(new Integer(startNode));
        result[startNode] = level;
        do {
            int node = (q.removeFirst()).intValue();
            level++;
            for (int i = 0; i < super.numberOfNodes; i++) {
                if (super.g[node][i] > 0) {
                    if ((result[i] <= 0) && (i != startNode)) { // unmarked node
                        q.addLast(new Integer(i));
                        result[i] = (result[node] + 1);
                    }
                }
            }
        } while (!q.isEmpty());
        return result;
    }

    /**
     * generates the color-class distance matrix, containing the distance from
     * singleton color class i to color class j at m[i,j]
     */
    protected int[][] computeColorClassBFSLevels() {
        int[][] colorClassBfsLevels = new int[numberOfColors][numberOfColors];

        for (int i = 0; i < numberOfNodes; i++) {
            if (singletons[i] == true) {
                int color1 = nodeColors[i];
                for (int j = 0; j < numberOfNodes; j++) {
                    int color2 = nodeColors[j];
                    colorClassBfsLevels[color1][color2] = bfsLevels[i][j];
                }
            }
        }
        return colorClassBfsLevels;
    }

    /**
     * Wrapper so that the nodes can be sorted not only according to their
     * degree vectors, but also their distances to other nodes.
     * 
     * @author lenhardt
     * 
     */
    protected class SortableNodeBFS implements SortableNode {

        private int nodeNumber;

        public SortableNodeBFS(int number) {
            nodeNumber = number;
        }

        @Override
        public SortableNodeBFS clone() {
            return new SortableNodeBFS(nodeNumber);
        }

        public boolean equals(SortableNodeBFS o) {
            return compareTo(o) == 0;
        }

        public int[] getDegreeVector() {
            return degreeVectors[nodeNumber];
        }

        public int compareTo(SortableNodeBFS o) {
            int res = 0;
            for (int i = 0; i < getDegreeVector().length; i++) {
                if (getDegreeVector()[i] < o.getDegreeVector()[i]) {
                    res = -1;
                    break;
                } else if (getDegreeVector()[i] > o.getDegreeVector()[i]) {
                    res = 1;
                    break;
                }
            }
            if (res != 0)
                return res;
            else {
                // arrays save distance of node from node with color class i at
                // position i; there is only one, because we only do bfs from
                // singletons
                int[] myLevels = getDistanceFromSingletons();
                int[] oLevels = o.getDistanceFromSingletons();
                for (int i = 0; i < myLevels.length; i++) {
                    if (myLevels[i] < oLevels[i])
                        return -1;
                    else if (myLevels[i] > oLevels[i])
                        return 1;
                }
                return 0;
            }
        }

        /**
         * Returns an array containing the distances to that node from the
         * singletons (for only which the bfs nums have been computed)
         * 
         * At index i of the array is the distance from singleton with color
         * number i, or 0 if i is not a singleton or unreachable ????
         */
        public int[] getDistanceFromSingletons() {
            int node = this.getNodeNumber();
            int[] distances = new int[numberOfColors];
            for (int i = 0; i < bfsLevels.length; i++) {
                if (singletons[i]) {
                    distances[nodeColors[i]] = bfsLevels[i][node];
                }
            }
            return distances;
        }

        public int compareTo(SortableNode o) {
            if (o instanceof SortableNodeBFS) {
                SortableNodeBFS newO = (SortableNodeBFS) o;
                return compareTo(newO);
            } else
                throw new ClassCastException();
        }

        public boolean equals(SortableNode o) {
            if (o instanceof SortableNodeBFS) {
                SortableNodeBFS newO = (SortableNodeBFS) o;
                return equals(newO);
            } else
                throw new ClassCastException();
        }

        public int getNodeNumber() {
            return nodeNumber;
        }

    }
}
