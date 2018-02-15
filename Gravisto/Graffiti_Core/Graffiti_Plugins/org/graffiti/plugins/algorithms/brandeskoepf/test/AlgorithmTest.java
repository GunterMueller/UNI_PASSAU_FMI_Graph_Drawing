//==============================================================================
//
//   GraphTest.java
//
//   Copyright (c) 2001-2003 Graffiti Team, Uni Passau
//
//==============================================================================
// $Id: AlgorithmTest.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.brandeskoepf.test;

import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.Algorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugins.algorithms.brandeskoepf.BKConst;
import org.graffiti.plugins.algorithms.brandeskoepf.BKLayoutAlgorithm;
import org.graffiti.plugins.algorithms.brandeskoepf.GraffitiGraph;
import org.graffiti.plugins.algorithms.brandeskoepf.Matrix2Dim;

/**
 * This is a class which tests the graph data structure
 * 
 * @author chris
 */
public class AlgorithmTest {
    // ~ Methods
    // ================================================================

    /**
     * This is the main method.
     * 
     * @param args
     *            Paramters from console
     */
    public static void main(String[] args) {
        int sideLenght = 10;
        BKConst.setDefault();

        Matrix2Dim<Node> level = new Matrix2Dim<Node>(sideLenght);

        Graph graph = new AdjListGraph();
        // Build a quadratic node graph
        for (int i = 0; i < sideLenght; i++) {
            for (int j = 0; j < sideLenght; j++) {
                Node node = graph.addNode();

                node.setInteger("graphics.level", i);

                node.setInteger("graphics.order", j);
                node.setInteger("graphics.dummy", 0);

                level.set(GraffitiGraph.getNodeLevel(node), GraffitiGraph
                        .getNodeOrder(node), node);
            }
        }

        for (int i = 0; i < (sideLenght - 1); i++) {
            for (int j = 0; j < sideLenght; j++) {
                Node node = level.get(i, j);

                // the first node has edges to the first an second node of the
                // lower level
                if (j == 0) {
                    for (int k = j; k < (j + 2); k++) {
                        Edge edge = graph.addEdge(node, level.get(i + 1, k),
                                true);
                        edge.setInteger("graphics.cutEdge", 0);
                    }
                }

                // the last node has edges to the last and the last but one node
                // of the lower level
                else if (j == (sideLenght - 1)) {
                    for (int k = j - 1; k < (j + 1); k++) {
                        Edge edge = graph.addEdge(node, level.get(i + 1, k),
                                true);
                        edge.setInteger("graphics.cutEdge", 0);
                    }
                }

                // the other nodes have three edges to the lower neighbours j-1,
                // j, j+1
                else {
                    for (int k = j - 1; k < (j + 2); k++) {
                        Edge edge = graph.addEdge(node, level.get(i + 1, k),
                                true);
                        edge.setInteger("graphics.cutEdge", 0);
                    }
                }
            }
        }

        Algorithm bkLayout = new BKLayoutAlgorithm();

        bkLayout.attach(graph);
        try {
            bkLayout.check();
        } catch (PreconditionException e) {
            e.printStackTrace();
        }
        bkLayout.execute();
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
