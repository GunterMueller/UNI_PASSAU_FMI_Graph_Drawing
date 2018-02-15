//==============================================================================
//
//   GraphTest.java
//
//   Copyright (c) 2001-2003 Graffiti Team, Uni Passau
//
//==============================================================================
// $Id: AlgorithmBenchmarkRadial.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.brandeskoepf.benchmark;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Time;

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
public class AlgorithmBenchmarkRadial {
    // ~ Methods
    // ================================================================

    /**
     * This is the main method.
     * 
     * @param args
     *            Paramters from console
     */
    public static void main(String[] args) {
        int high = 5;
        BKConst.setDefault();

        // File creation
        FileWriter fileWriter;
        PrintWriter writer;

        try {
            // Andreas G.: ...
            fileWriter = new FileWriter(
                    "c:\\Florian\\Uni\\Diplomarbeit\\benchmarkResults.txt");
            writer = new PrintWriter(new BufferedWriter(fileWriter));

            Algorithm bkLayout = new BKLayoutAlgorithm();

            // Choose the radial layout
            BKConst.setDRAW(5);

            while (high < 15) {

                Matrix2Dim<Node> level = new Matrix2Dim<Node>(high - 1);
                Graph graph = new AdjListGraph();

                // Build a tree graph
                for (int i = 0; i < high; i++) {
                    for (int j = 0; j < (int) Math.pow(2.0, i); j++) {
                        Node node = graph.addNode();

                        node.setInteger("graphics.level", i);

                        node.setInteger("graphics.order", j);
                        node.setInteger("graphics.dummy", 0);

                        level.set(GraffitiGraph.getNodeLevel(node),
                                GraffitiGraph.getNodeOrder(node), node);
                    }
                }

                for (int i = 0; i < (high - 1); i++) {
                    for (int j = 0; j < (int) Math.pow(2.0, i); j++) {
                        Node node = level.get(i, j);

                        // every node, exept the leafs, has two children
                        for (int k = 2 * j; k < 2 * j + 2; k++) {
                            Edge edge = graph.addEdge(node,
                                    level.get(i + 1, k), true);
                            edge.setInteger("graphics.cutEdge", 0);
                            edge.setDouble("graphics.bends.super_temp", 0.0);
                            // bends remains
                            edge.removeAttribute("graphics.bends.super_temp");
                        }
                    }
                }

                bkLayout.attach(graph);

                try {
                    // check the created graph
                    bkLayout.check();
                } catch (PreconditionException e) {
                    System.out.println("Der Grund: " + e.getMessage());
                }

                // take the start time
                long time1 = System.currentTimeMillis();

                // run the algorithm
                bkLayout.execute();

                // take the end time
                Time timer = new Time(System.currentTimeMillis() - time1);

                // write the result to the file
                writer.println((graph.getNodes().size() + graph.getEdges()
                        .size())
                        + "\t" + timer.getTime());

                writer.flush();

                high += 1;

                graph = null;
                level = null;
            }

            bkLayout = null;
            writer.close();
            fileWriter.close();
        } catch (IOException io) {
            System.out.println("Datei konnte nicht erstellt werden.");
        } catch (OutOfMemoryError io) {
            System.out.println("Speicher reicht nicht aus.");
        }
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
