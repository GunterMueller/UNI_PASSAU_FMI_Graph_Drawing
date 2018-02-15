// =============================================================================
//
//   LowSettingAlgorithm.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treeWidth;

import java.util.ArrayList;

/**
 * An Implementation of the maximal minimum Degree Algorithm for the lower
 * bound. Finds the node with minimal degree as the lower bound in the graph and
 * remove the node from the graph. Repeat the procedure in the subgraph till the
 * low bound is maximal.
 * 
 * @author wangq
 * @version $Revision$ $Date$
 */
public class LowSetting {
    /**
     * the minimal degree in the graph
     */
    private int lowDegree = 1;
    /**
     * the matrixGraph
     */
    private MatrixGraph matrixGraph;

    /**
     * Constructs a new instance.
     * 
     * @param mat
     *            is the matrixGraph
     */
    public LowSetting(MatrixGraph mat) {
        this.matrixGraph = mat;
    }

    /**
     * Sets the lowDegree.
     * 
     * @param lowDegree
     *            the lowDegree to set.
     */

    public void setLowDegree(int lowDegree) {
        this.lowDegree = lowDegree;
    }

    /**
     * Returns the lowDegree.
     * 
     * @return the lowDegree.
     */
    public int getLowDegree() {
        return lowDegree;
    }

    /**
     * Returns the matrixGraph.
     * 
     * @return the matrixGraph.
     */
    public MatrixGraph getMatrixGraph() {
        return matrixGraph;
    }

    /**
     * Sets the matrixGraph.
     * 
     * @param matrixGraph
     *            the matrixGraph to set.
     */
    public void setMatrixGraph(MatrixGraph matrixGraph) {
        this.matrixGraph = matrixGraph;
    }

    /**
     * Compute the lower bound.
     * 
     * @param.
     */
    public void calculatelow() {
        while (!this.matrixGraph.isEmpty()) {
            /** Searches the minimal degree */
            ArrayList<Object> tmpSuNodes = new ArrayList<Object>();
            for (int i = 0; i < matrixGraph.getSuNodes().size(); i++) {
                SuperNode tmpNode = matrixGraph.getSuNodes().get(i);
                if (!tmpNode.isFinish()) {
                    tmpSuNodes.add(tmpNode);
                }
            }

            ArrayList<Object> sortedNode = Heapsort.heapsort(tmpSuNodes,
                    new DegreeComp(DegreeComp.DEG));
            SuperNode suNode = (SuperNode) sortedNode.get(0);

            if (lowDegree < suNode.getDegree() && suNode.getDegree() > 1) {
                this.lowDegree = suNode.getDegree() - 1;
            }

            /** Removes the vertex from the graph */
            for (int i = 0; i < matrixGraph.getNodeSize(); i++) {
                matrixGraph.getMatrixEdge()[matrixGraph.getSuNodes().indexOf(
                        suNode)][i] = 0;
                matrixGraph.getMatrixEdge()[i][matrixGraph.getSuNodes()
                        .indexOf(suNode)] = 0;
                suNode.setFinish(true);
                matrixGraph.degrees(matrixGraph.getSuNodes().get(i));
            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
