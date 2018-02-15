/**
 * 
 */
package org.graffiti.plugins.algorithms.graviso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * 
 * This class implements the most basic form of the Refinement Algorithm for
 * Graph Isomorphisms. A graph along with two optional parameters to take node
 * labels or edge labels into account is passed with the constructor.
 * 
 * Then, upon calling refine() from outside, the graph is subsequently colored,
 * until the coloring stabilizes (no more new colors can be assigned, the
 * refinement process has come to an end).
 * 
 * During the process of refining, the user can perform the same process on
 * another graph simultaneously, comparing the two (or more) graphs of interest
 * after each refinement() step. This method will yield R_NOT_ISO if the graphs
 * are definitely not isomorphic, R_ISO if they definitely are, or R_MAYBE if
 * they might be isomorphic.
 * 
 * @author lenhardt
 * @version $Revision: 1002 $
 */
public class IsoGraphSimple implements Cloneable {

    /** number of nodes in the graph: */
    protected int numberOfNodes;

    /** adjacency matrix of the graph: */
    protected int[][] g;

    /** degree vectors of each node; deg(v) = [|N(v)cutC1|, ..., |N(v)cutCm|] */
    protected int[][] degreeVectors;

    /** stores the color of each node at nodeColors[nodenumber] */
    protected int[] nodeColors;

    /** the total number of distinct colors at the current refinement step */
    protected int numberOfColors;

    /** constants for return values of isIsomorphicTo; cf. RefinementAlgorithm */
    protected static final int R_ISO = 1;
    protected static final int R_MAYBE = 0;
    protected static final int R_NOT_ISO = -1;

    /** stores whether the graph should be treated as undirected or directed */
    protected boolean regardDirections;

    /**
     * the color classes of the graph, color number i is at posticion i of the
     * arraylist
     */
    ArrayList<LinkedList<SortableNode>> colorClasses;

    /**
     * Constructor
     * 
     * Limitations: Doesn't support multiple edges between nodes; supports only
     * one edge attribute, only one type of node attribute (aka one path)
     * 
     * @param graph
     *            we will perform the coloring on this graph
     * @throws GravIsoException
     * 
     */
    public IsoGraphSimple(Graph graph, boolean regardDirections)
            throws GravIsoException {
        // at first all nodes have the same color (i.e. color # 0)
        numberOfColors = 1;

        numberOfNodes = graph.getNumberOfNodes();
        nodeColors = new int[numberOfNodes];

        if (!regardDirections) {
            // this is an easy hack to disregard edge directions;
            // however, we have to operate on a copy, otherwise the original
            // graph loses its property
            Graph newGraph = (Graph) graph.copy();
            newGraph.setDirected(false);
            graph = newGraph;
        }

        g = new int[numberOfNodes][numberOfNodes];
        degreeVectors = new int[numberOfNodes][1];

        int i = 0;
        for (Node n : graph.getNodes()) {
            n.setInteger("node number", i);
            // first degree vector is easy: we have only 1 color, so the number
            // of neighbours of n with color 0 is simply the degree of n
            degreeVectors[i][0] = n.getOutDegree();
            i++;
        }

        // transformation from gravisto structure to adjacency matrix:
        for (Node n : graph.getNodes()) {
            for (Node neigh : n.getAllInNeighbors()) {
                g[n.getInteger("node number")][neigh.getInteger("node number")] = -1;

            }

            for (Node neigh : n.getAllOutNeighbors()) {
                g[n.getInteger("node number")][neigh.getInteger("node number")] = 1;

            }
        }
    }

    /**
     * tries to find new color classes in the graph and computes the new degree
     * vectors. if the new degree vectors differ from the old ones, the
     * recolouring yielded new colors and the graph hasn't stabilized yet
     * 
     * @return true, if the refinement stabilized the coloring
     */
    public boolean refine() {
        computeColorClasses();
        recolour();
        int[][] newDegVectors = computeDegreeVectors();

        if (Arrays.deepEquals(newDegVectors, degreeVectors))
            return true;
        else {
            degreeVectors = newDegVectors;
            return false;
        }
    }

    /**
     * Colors the nodes of the graph according to the degree vectors
     */
    protected void recolour() {
        // try to find new distinguishable properties among the nodes

        // 1. Degree Vectors
        // we have to look at all nodes with the same color, to see if we can
        // distinguish even further
        for (LinkedList<SortableNode> nodes : colorClasses) {
            // analyze nodes: all nodes with the same degree vector
            // receive the same color
            SortableNode node = nodes.getFirst();

            for (SortableNode nextNode : nodes) {
                if (!node.equals(nextNode)) {
                    // we have a new color
                    // set this and all following nodes in the list to the
                    // new color. this works, because the node list is sorted
                    // according to the nodes' degree vectors
                    nodeColors[nextNode.getNodeNumber()] = numberOfColors;
                    numberOfColors++;
                } else {
                    nodeColors[nextNode.getNodeNumber()] = nodeColors[node
                            .getNodeNumber()];
                }
                node = nextNode;
            }

        }
    }

    /**
     * returns the color classes under the current coloring
     * 
     * @return the color classes
     */
    public ArrayList<LinkedList<SortableNode>> getColorClasses() {
        return colorClasses;
    }

    /**
     * Returns an array of the form colorClass[color number]->List(node 1, node
     * 2, node 3) with the nodes being wrapped in the SortableNode class, so
     * that they are sorted according to their degree vectors
     */
    protected void computeColorClasses() {
        colorClasses = new ArrayList<LinkedList<SortableNode>>((numberOfColors));
        colorClasses.ensureCapacity(numberOfColors);
        for (int i = 0; i < numberOfColors; i++) {
            colorClasses.add(i, new LinkedList<SortableNode>());
        }
        for (int nodeNum = 0; nodeNum < nodeColors.length; nodeNum++) {
            int color = nodeColors[nodeNum];
            colorClasses.get(color).add(new SortableNodeSimple(nodeNum));
        }
        for (LinkedList<SortableNode> nodes : colorClasses) {
            Collections.sort(nodes);
        }
    }

    /**
     * returns the color class which seems suited best as pivot; in this
     * implementation, it is the one with highest connectivity (highest degree)
     * 
     * @return the color class with the highest degree
     */
    public LinkedList<SortableNode> computeBestColorClass() {
        LinkedList<SortableNode> result = new LinkedList<SortableNode>();
        int[][] colorClassMatrix = getColorClassMatrix();
        int maxDegree = 0;

        if (colorClasses.size() != colorClassMatrix.length) {
            computeColorClasses();
        }
        for (int i = 0; i < colorClassMatrix.length; i++) {
            if (colorClasses.get(i).size() > 1) {
                int deg = 0;
                for (int j = 0; j < colorClassMatrix[i].length; j++) {
                    deg += colorClassMatrix[i][j];
                }
                if (deg > maxDegree) {
                    maxDegree = deg;
                    result = colorClasses.get(i);
                }
            }
        }

        return result;
    }

    /**
     * Computes degree vector of the given node; deg(v) = [|N(v)cutC1|, ...,
     * |N(v)cutCm|]
     * 
     * @param nodeNum
     *            number of the node
     * @return the degree vector of the given node
     */
    private int[] computeDegreeVector(int nodeNum) {
        int[] degVector = new int[numberOfColors];
        for (int i = 0; i < g[nodeNum].length; i++) {
            if (g[nodeNum][i] > 0) {
                degVector[nodeColors[i]]++;
            }
        }
        return degVector;
    }

    /**
     * Computes the Degree Vectors of all nodes;
     * 
     * @return an int[node number][number of neighbours with that color]
     */
    protected int[][] computeDegreeVectors() {
        int[][] newDegVectors = new int[numberOfNodes][numberOfColors];
        for (int i = 0; i < degreeVectors.length; i++) {
            newDegVectors[i] = computeDegreeVector(i);
        }
        return newDegVectors;
    }

    /**
     * returns the color class vector, showing at position i how many nodes are
     * assigned to color i
     * 
     * @return the color class vector
     */
    public int[] getColorClassVector() {
        int[] vector = new int[numberOfColors];
        for (int i = 0; i < nodeColors.length; i++) {
            vector[nodeColors[i]]++;
        }
        return vector;
    }

    /**
     * computes the color class adjacency matrix
     */
    public int[][] getColorClassMatrix() {
        int colorMatrix[][] = new int[numberOfColors][numberOfColors];
        for (int i = 0; i < g.length; i++) {
            for (int j = 0; j < g[i].length; j++) {
                if (g[i][j] > 0) {
                    colorMatrix[nodeColors[i]][nodeColors[j]]++;
                }
            }
        }
        return colorMatrix;
    }

    public int getNumberOfColors() {
        return numberOfColors;
    }

    public int[] getNodeColors() {
        return nodeColors;
    }

    /**
     * Compares the color class adjacency matrix and the color class size
     * vectors of the two graphs under the current coloring. If they are not
     * equal, R_NOT_ISO is returned. If they are equal and the number of colors
     * equals the number of vertices, a mapping is established and R_ISO is
     * returned, R_MAYBE otherwise
     * 
     * @param o
     *            another graph
     * @return R_ISO or R_MAYBE or R_NOT_ISO
     * @throws GravIsoException
     */
    public int isIsomorphicTo(IsoGraphSimple o) throws GravIsoException {
        int[] vec1 = getColorClassVector();
        int[] vec2 = o.getColorClassVector();
        boolean r1 = Arrays.equals(vec1, vec2);
        vec1 = null;
        vec2 = null;
        int[][] mat1 = getColorClassMatrix();
        int[][] mat2 = o.getColorClassMatrix();
        boolean r2 = Arrays.deepEquals(mat1, mat2);
        if (r1 && r2) {
            if (numberOfColors == o.getNumberOfColors()
                    && (numberOfNodes == numberOfColors))
                // class vectors & matrix matches, same number of colors as
                // nodes => unique mapping => iso!
                return R_ISO;
            else
                // vectors & matrix match, but ambiguous nodes left => might be
                // iso
                return R_MAYBE;
        } else
            // vectors & matrix don't match => not iso
            return R_NOT_ISO;
    }

    @Override
    public String toString() {
        String res = "\nnumberOfNodes :" + numberOfNodes + "\ndegreeVectors: ";
        res += Arrays.deepToString(degreeVectors);
        res += "\nnodeColors: ";
        res += Arrays.toString(nodeColors);
        res += "\nnumberOfColors: " + numberOfColors;
        return res;
    }

    /**
     * After one branch of the backtracking tree proves to be unfruitful, the
     * search tracks back to an earlier stage. This is why the graphs have to be
     * cloned during backtracking.
     */
    @Override
    public IsoGraphSimple clone() throws CloneNotSupportedException {
        IsoGraphSimple clone = (IsoGraphSimple) super.clone();
        clone.degreeVectors = degreeVectors.clone();
        clone.g = g.clone();
        if (nodeColors != null) {
            clone.nodeColors = nodeColors.clone();
        }

        if (colorClasses != null) {
            clone.colorClasses = new ArrayList<LinkedList<SortableNode>>();
            int index = 0;
            for (LinkedList<SortableNode> nodes : colorClasses) {
                clone.colorClasses.add(new LinkedList<SortableNode>());
                for (SortableNode sortableNode : nodes) {
                    clone.colorClasses.get(index).add(sortableNode.clone());
                }
                index++;
            }
        }

        return clone;
    }

    /**
     * Wrapper class for the nodes, so that they can be sorted according to
     * their degree vectors.
     * 
     * @author lenhardt
     * 
     */
    protected class SortableNodeSimple implements SortableNode {
        /**
         * the number of that node
         */
        private int nodeNumber;

        public SortableNodeSimple(int number) {
            nodeNumber = number;
        }

        @Override
        public SortableNodeSimple clone() {
            return new SortableNodeSimple(nodeNumber);
        }

        public int getNodeNumber() {
            return nodeNumber;
        }

        public int[] getDegreeVector() {
            return degreeVectors[nodeNumber];
        }

        /**
         * Two nodes are compared by their degree vectors.
         * 
         * @return -1 the degree vector of this node is smaller, +1 if the
         *         degree vector is bigger, and 0 if they are equal
         */
        public int compareTo(SortableNodeSimple o) {
            for (int i = 0; i < getDegreeVector().length; i++) {
                if (getDegreeVector()[i] < o.getDegreeVector()[i])
                    return -1;
                else if (getDegreeVector()[i] > o.getDegreeVector()[i])
                    return 1;
            }
            return 0;
        }

        public boolean equals(SortableNodeSimple o) {
            return compareTo(o) == 0;
        }

        public int compareTo(SortableNode o) {
            if (o instanceof SortableNodeSimple) {
                SortableNodeSimple newO = (SortableNodeSimple) o;
                return compareTo(newO);
            } else
                throw new ClassCastException();
        }

        public boolean equals(SortableNode o) {
            if (o instanceof SortableNodeSimple) {
                SortableNodeSimple newO = (SortableNodeSimple) o;
                return equals(newO);
            } else
                throw new ClassCastException();
        }
    }

}
