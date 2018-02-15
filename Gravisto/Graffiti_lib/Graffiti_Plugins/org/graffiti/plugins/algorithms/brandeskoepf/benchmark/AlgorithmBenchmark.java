//==============================================================================
//
//   GraphTest.java
//
//   Copyright (c) 2001-2003 Graffiti Team, Uni Passau
//
//==============================================================================
// $Id: AlgorithmBenchmark.java 5766 2010-05-07 18:39:06Z gleissner $

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
public class AlgorithmBenchmark {
    // ~ Methods
    // ================================================================

    /**
     * This is the main method.
     * 
     * @param args
     *            Paramters from console
     */
    public static void main(String[] args) {
        int sideLenght = 5;
        BKConst.setDefault();

        // File creation
        FileWriter fileWriter;
        PrintWriter writer;

        try {
            Algorithm bkLayout = new BKLayoutAlgorithm();

            // Andreas G.: Should not be hard wired.
            fileWriter = new FileWriter(
                    "c:\\Florian\\Uni\\Diplomarbeit\\benchmarkResults.txt");
            writer = new PrintWriter(new BufferedWriter(fileWriter));

            while (sideLenght < 100) {

                Matrix2Dim<Node> level = new Matrix2Dim<Node>(sideLenght - 1);

                Graph graph = new AdjListGraph();
                // Build a quadratic node graph
                for (int i = 0; i < sideLenght; i++) {
                    for (int j = 0; j < sideLenght; j++) {
                        Node node = graph.addNode();

                        node.setInteger("graphics.level", i);

                        node.setInteger("graphics.order", j);
                        node.setInteger("graphics.dummy", 0);

                        level.set(GraffitiGraph.getNodeLevel(node),
                                GraffitiGraph.getNodeOrder(node), node);
                    }
                }

                for (int i = 0; i < (sideLenght - 1); i++) {
                    for (int j = 0; j < sideLenght; j++) {
                        Node node = level.get(i, j);

                        // the first node has edges to the first an second node
                        // of the lower level
                        if (j == 0) {
                            for (int k = j; k < (j + 2); k++) {
                                Edge edge = graph.addEdge(node, level.get(
                                        i + 1, k), true);
                                edge.setInteger("graphics.cutEdge", 0);
                            }
                        }

                        // the last node has edges to the last and the last but
                        // one node of the lower level
                        else if (j == (sideLenght - 1)) {
                            for (int k = j - 1; k < (j + 1); k++) {
                                Edge edge = graph.addEdge(node, level.get(
                                        i + 1, k), true);
                                edge.setInteger("graphics.cutEdge", 0);
                            }
                        }

                        // the other nodes have three edges to the lower
                        // neighbours j-1, j, j+1
                        else {
                            for (int k = j - 1; k < (j + 2); k++) {
                                Edge edge = graph.addEdge(node, level.get(
                                        i + 1, k), true);
                                edge.setInteger("graphics.marked", 0);
                                edge.setInteger("graphics.cutEdge", 0);
                            }
                        }
                    }
                }

                bkLayout.attach(graph);

                try {
                    // check the created graph
                    bkLayout.check();
                } catch (PreconditionException e) {
                    System.out.println(e.getMessage());
                }

                // take the start time
                long time1 = System.currentTimeMillis();

                // run the algorithm
                bkLayout.execute();

                // take the end time
                Time timer = new Time(System.currentTimeMillis() - time1);

                // write the result to the file
                writer.println((sideLenght * (4 * sideLenght - 5) + 2) + "\t"
                        + timer.getTime());

                writer.flush();

                sideLenght += 1;

                graph = null;
                level = null;
                bkLayout = null;
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
