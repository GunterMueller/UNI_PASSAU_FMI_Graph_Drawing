// =============================================================================
//
//   Generator.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.cyclicLeveling.test;

import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Graph;
import org.graffiti.plugins.algorithms.generators.RandomGraphGenerator;

/**
 * @author lovasz
 * @version $Revision$ $Date$
 */
public class Generator {

    private boolean directed;

    /**
     * Constructor.
     */
    public Generator(boolean directed) {
        this.directed = directed;
    }

    public Graph create(int numberOfNodes, int numberOfEdges) {

        /* the real generator */
        RandomGraphGenerator generator = new RandomGraphGenerator();

        Graph g = new AdjListGraph();

        generator.attach(g);
        generator.setNumOfEdges(numberOfEdges);
        generator.setNumOfNodes(numberOfNodes);
        generator.setDirected(this.directed);

        generator.execute();

        return g;
    }

    /**
     * creates random graphs
     * 
     * @param args
     */
    public static void main(String[] args) {

        Generator g = new Generator(true);
        Graph graph = null;
        String prefix = "";
        String fileName = "";
        int currentNumberOfEdges = 0;

        int numberOfGraphs = 10;
        double edges1 = 1;
        double edges2 = 1.5;
        double edges3 = 2;
        double edges4 = 5;
        double edges5 = 10;

        /* 4 - 20 */
        for (int i = 4; i <= 20; i++) {
            prefix = i + "_1_";
            currentNumberOfEdges = getNumberOfEdges(i, edges1);
            for (int j = 0; j < numberOfGraphs; j++) {
                graph = g.create(i, currentNumberOfEdges);
                fileName = prefix + j + ".graphml";
                GraphIO.saveGraph(graph, fileName);
            }

            prefix = i + "_1.5_";
            currentNumberOfEdges = getNumberOfEdges(i, edges2);
            for (int j = 0; j < numberOfGraphs; j++) {
                graph = g.create(i, currentNumberOfEdges);
                fileName = prefix + j + ".graphml";
                GraphIO.saveGraph(graph, fileName);
            }

            prefix = i + "_2_";
            currentNumberOfEdges = getNumberOfEdges(i, edges3);
            for (int j = 0; j < numberOfGraphs; j++) {
                graph = g.create(i, currentNumberOfEdges);
                fileName = prefix + j + ".graphml";
                GraphIO.saveGraph(graph, fileName);
            }

            prefix = i + "_5_";
            currentNumberOfEdges = getNumberOfEdges(i, edges4);
            for (int j = 0; j < numberOfGraphs; j++) {
                graph = g.create(i, currentNumberOfEdges);
                fileName = prefix + j + ".graphml";
                GraphIO.saveGraph(graph, fileName);
            }

            prefix = i + "_10_";
            currentNumberOfEdges = getNumberOfEdges(i, edges5);
            for (int j = 0; j < numberOfGraphs; j++) {
                graph = g.create(i, currentNumberOfEdges);
                fileName = prefix + j + ".graphml";
                GraphIO.saveGraph(graph, fileName);
            }
            System.out.println(i + " fertig ");
        }

        /* 30 - 100 */
        for (int i = 30; i < 100; i = i + 10) {
            prefix = i + "_1_";
            currentNumberOfEdges = getNumberOfEdges(i, edges1);
            for (int j = 0; j < numberOfGraphs; j++) {
                graph = g.create(i, currentNumberOfEdges);
                fileName = prefix + j + ".graphml";
                GraphIO.saveGraph(graph, fileName);
            }

            prefix = i + "_1.5_";
            currentNumberOfEdges = getNumberOfEdges(i, edges2);
            for (int j = 0; j < numberOfGraphs; j++) {
                graph = g.create(i, currentNumberOfEdges);
                fileName = prefix + j + ".graphml";
                GraphIO.saveGraph(graph, fileName);
            }

            prefix = i + "_2_";
            currentNumberOfEdges = getNumberOfEdges(i, edges3);
            for (int j = 0; j < numberOfGraphs; j++) {
                graph = g.create(i, currentNumberOfEdges);
                fileName = prefix + j + ".graphml";
                GraphIO.saveGraph(graph, fileName);
            }

            prefix = i + "_5_";
            currentNumberOfEdges = getNumberOfEdges(i, edges4);
            for (int j = 0; j < numberOfGraphs; j++) {
                graph = g.create(i, currentNumberOfEdges);
                fileName = prefix + j + ".graphml";
                GraphIO.saveGraph(graph, fileName);
            }

            prefix = i + "_10_";
            currentNumberOfEdges = getNumberOfEdges(i, edges5);
            for (int j = 0; j < numberOfGraphs; j++) {
                graph = g.create(i, currentNumberOfEdges);
                fileName = prefix + j + ".graphml";
                GraphIO.saveGraph(graph, fileName);
            }
            System.out.println(i + " fertig ");
        }

        /* 100 - 500 */
        for (int i = 100; i <= 500; i = i + 50) {
            prefix = i + "_1_";
            currentNumberOfEdges = getNumberOfEdges(i, edges1);
            for (int j = 0; j < numberOfGraphs; j++) {
                graph = g.create(i, currentNumberOfEdges);
                fileName = prefix + j + ".graphml";
                GraphIO.saveGraph(graph, fileName);
            }

            prefix = i + "_1.5_";
            currentNumberOfEdges = getNumberOfEdges(i, edges2);
            for (int j = 0; j < numberOfGraphs; j++) {
                graph = g.create(i, currentNumberOfEdges);
                fileName = prefix + j + ".graphml";
                GraphIO.saveGraph(graph, fileName);
            }

            prefix = i + "_2_";
            currentNumberOfEdges = getNumberOfEdges(i, edges3);
            for (int j = 0; j < numberOfGraphs; j++) {
                graph = g.create(i, currentNumberOfEdges);
                fileName = prefix + j + ".graphml";
                GraphIO.saveGraph(graph, fileName);
            }

            prefix = i + "_5_";
            currentNumberOfEdges = getNumberOfEdges(i, edges4);
            for (int j = 0; j < numberOfGraphs; j++) {
                graph = g.create(i, currentNumberOfEdges);
                fileName = prefix + j + ".graphml";
                GraphIO.saveGraph(graph, fileName);
            }

            prefix = i + "_10_";
            currentNumberOfEdges = getNumberOfEdges(i, edges5);
            for (int j = 0; j < numberOfGraphs; j++) {
                graph = g.create(i, currentNumberOfEdges);
                fileName = prefix + j + ".graphml";
                GraphIO.saveGraph(graph, fileName);
            }
            System.out.println(i + " fertig ");
        }
    }

    /**
     * @param numberOfNodes
     * @return numberOfEdges
     */
    private static int getNumberOfEdges(int numberOfNodes, double edges) {
        int numberOfEdges = (int) Math.round(numberOfNodes * edges);
        if (numberOfEdges > (numberOfNodes * (numberOfNodes - 1)))
            return (numberOfNodes * (numberOfNodes - 1));
        else
            return numberOfEdges;

    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
