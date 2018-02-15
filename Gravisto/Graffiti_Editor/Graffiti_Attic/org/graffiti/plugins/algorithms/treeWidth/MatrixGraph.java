// =============================================================================
//
//   MatrixGraph.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treeWidth;

import java.util.ArrayList;
import java.util.Iterator;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * Creates the MatrixGraph, containing a (0,1) matrix for all edges in the graph
 * and an ArrayList for all SuperNodes in the graph. Every SuperNode has an
 * ArrayList, which stores all its neighbors.
 * 
 * @author wangq
 * @version $Revision$ $Date$
 */
public class MatrixGraph {
    /**
     * the original graph
     */
    private Graph graph;
    /**
     * the (0,1) matrix for edges
     */
    private int[][] matrixEdge;
    /**
     * the number of SuperNodes in the graph.
     */
    private int nodeSize;
    /**
     * the ArrayList of all SuperNodes in the graph
     */
    private ArrayList<SuperNode> suNodes = new ArrayList<SuperNode>();
    /**
     * the ArrayList of the neighbors.
     */
    private ArrayList<SuperNode> neighbors = new ArrayList<SuperNode>();

    /**
     * Constructs a new instance.
     * 
     * @param graph
     */

    public MatrixGraph(Graph graph) {
        this.graph = graph;
        this.nodeSize = graph.getNodes().size();
    }

    public void createMatrixGraph() {
        /** create the EdgeMatrix */
        matrixEdge = new int[nodeSize][nodeSize];
        for (int i = 0; i < nodeSize; i++) {
            for (int j = 0; j < nodeSize; j++) {
                Node m = (graph.getNodes().get(i));
                Node n = (graph.getNodes().get(j));
                if (!graph.getEdges(m, n).isEmpty()) {
                    matrixEdge[i][j] = 1;
                } else {
                    matrixEdge[i][j] = 0;
                }
            }
        }

        /** Creates the SuperNode list */
        for (int i = 0; i < nodeSize; i++) {
            SuperNode suNode = new SuperNode(graph.getNodes().get(i));
            suNodes.add(suNode);
        }

        /** Creates the degree and the neighbors for every node */
        for (int i = 0; i < nodeSize; i++) {
            SuperNode suNode1 = suNodes.get(i);
            degrees(suNode1);
            neighbors(suNode1);
        }

        /**
         * computes the number of Fill-in edges and the number of fill-in
         * excluding one neighbor edges for every node.
         */
        for (int i = 0; i < nodeSize; i++) {
            SuperNode suNode1 = suNodes.get(i);
            comLackOfEdgesSim(suNode1);
            comLackOfEdgesFaSim(suNode1);
        }
    }

    /**
     * Returns the graph.
     * 
     * @return the graph.
     */
    public Graph getGraph() {
        return graph;
    }

    /**
     * Sets the graph.
     * 
     * @param graph
     *            the graph to set.
     */
    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    /**
     * Returns the matrixEdge.
     * 
     * @return the matrixEdge.
     */
    public int[][] getMatrixEdge() {
        return matrixEdge;
    }

    /**
     * Sets the matrixEdge.
     * 
     * @param matrixEdge
     *            the matrixEdge to set.
     */
    public void setMatrixEdge(int[][] matrixEdge) {
        this.matrixEdge = matrixEdge;
    }

    /**
     * Returns the neighbors.
     * 
     * @return the neighbors.
     */
    public ArrayList<SuperNode> getNeighbors() {
        return neighbors;
    }

    /**
     * Sets the neighbors.
     * 
     * @param neighbors
     *            the neighbors to set.
     */
    public void setNeighbors(ArrayList<SuperNode> neighbors) {
        this.neighbors = neighbors;
    }

    /**
     * Returns the nodeSize.
     * 
     * @return the nodeSize.
     */
    public int getNodeSize() {
        return nodeSize;
    }

    /**
     * Sets the nodeSize.
     * 
     * @param nodeSize
     *            the nodeSize to set.
     */
    public void setNodeSize(int nodeSize) {
        this.nodeSize = nodeSize;
    }

    /**
     * Returns the suNodes.
     * 
     * @return the suNodes.
     */
    public ArrayList<SuperNode> getSuNodes() {
        return suNodes;
    }

    /**
     * Sets the suNodes.
     * 
     * @param suNodes
     *            the suNodes to set.
     */
    public void setSuNodes(ArrayList<SuperNode> suNodes) {
        this.suNodes = suNodes;
    }

    /**
     * Computes the degrees.
     * 
     * @param suNode
     *            the suNode to compute.
     */
    public void degrees(SuperNode suNode) {
        int degree = 0;
        for (int j = 0; j < nodeSize; j++) {
            if (matrixEdge[suNodes.indexOf(suNode)][j] != 0) {
                degree++;
            }
        }
        suNode.setDegree(degree);
    }

    /**
     * Computes the neighbors.
     * 
     * @param suNode
     *            the suNode to compute.
     */
    public void neighbors(SuperNode suNode) {
        ArrayList<SuperNode> suNeighbors = new ArrayList<SuperNode>();
        for (int i = 0; i < nodeSize; i++) {
            if (matrixEdge[suNodes.indexOf(suNode)][i] != 0
                    && !suNode.isFinish()) {
                suNeighbors.add(suNodes.get(i));
            }
        }
        suNode.setNeighbors(suNeighbors);
    }

    /**
     * Computes the comLackOfEdgesSim. Counts the zeros in the (0,1)
     * NeighborsMatrix
     * 
     * @param suNode
     *            the suNode to compute.
     */
    public void comLackOfEdgesSim(SuperNode suNode) {
        if (!suNode.isFinish()) {
            int lackOfEdgeSim = 0;
            for (int m = 0; m < graph.getNodes().size(); m++) {
                if (this.matrixEdge[suNodes.indexOf(suNode)][m] != 0) {
                    for (int n = 0; n < graph.getNodes().size(); n++) {
                        if (this.matrixEdge[suNodes.indexOf(suNode)][n] != 0) {
                            if (this.matrixEdge[m][n] == 0 && m != n) {
                                lackOfEdgeSim++;
                            }
                        }
                    }
                }
            }
            suNode.setLackOfEdgeSim(lackOfEdgeSim);
        }
    }

    /**
     * Computes the comLackOfEdgesFaSim. Cancels a neighbor from the
     * neighborMatrix, which has the minimal number of neighbors in the
     * neighborMatrix. Counts the zeros in the (0,1) excluding one neighbor
     * neighborsMatrix
     * 
     * @param suNode
     *            the suNode to compute.
     */

    public void comLackOfEdgesFaSim(SuperNode suNode) {
        if (suNode.getNeighbors().size() < 2) {
            suNode.setLackOfEdgeFaSim(0);
        } else {
            ArrayList<SuperNode> nehSuNodes = suNode.getNeighbors();
            ArrayList<Object> tempList = new ArrayList<Object>();

            // how many neighbors each vertex has in the clique
            for (int i = 0; i < nehSuNodes.size(); i++) {
                int temLack1 = 0;
                SuperNode nehSuNode = nehSuNodes.get(i);
                Iterator<SuperNode> nehSuItr = nehSuNode.getNeighbors()
                        .iterator();
                // search the vertex, if it has minimum number of neighbors in
                // the clique
                while (nehSuItr.hasNext()) {
                    if (suNode.getNeighbors().contains(nehSuItr.next())) {
                        temLack1++;
                    }
                }
                nehSuNode.setNodeSizeInClique(temLack1);
                tempList.add(nehSuNode);
            }

            ArrayList<Object> sortedNodes = new ArrayList<Object>();
            sortedNodes = Heapsort.heapsort(tempList, new DegreeComp(
                    DegreeComp.NEIGHSIZE));
            SuperNode suNode2 = (SuperNode) sortedNodes.get(0);
            // the node is eliminated
            int[][] temMatrix = matrixEdge.clone();
            for (int a = 0; a < matrixEdge.length; a++) {
                if (this.matrixEdge != null) {
                    temMatrix[a] = matrixEdge[a].clone();
                }
            }

            for (int i = 0; i < nodeSize; i++) {
                temMatrix[suNodes.indexOf(suNode2)][i] = 0;
                temMatrix[i][suNodes.indexOf(suNode2)] = 0;
            }

            int lackOfEdgeFaSim = 0;
            for (int m = 0; m < graph.getNodes().size(); m++) {
                if (temMatrix[suNodes.indexOf(suNode)][m] != 0) {
                    for (int n = 0; n < graph.getNodes().size(); n++) {
                        if (temMatrix[suNodes.indexOf(suNode)][n] != 0) {
                            if (this.matrixEdge[m][n] == 0 && m != n) {
                                lackOfEdgeFaSim++;
                            }
                        }
                    }
                }
            }
            suNode.setLackOfEdgeFaSim(lackOfEdgeFaSim);
        }
    }

    /**
     * Checks GraphMatrix is empty.
     * 
     * @param.
     */

    public boolean isEmpty() {
        boolean empty = true;
        for (int i = 0; i < suNodes.size(); i++) {
            SuperNode suNode = suNodes.get(i);
            if (suNode.isFinish() == false) {
                empty = false;
            }
        }
        return empty;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
