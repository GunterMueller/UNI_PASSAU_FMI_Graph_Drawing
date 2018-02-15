// =============================================================================
//
//   AdjListGraphBenchmark.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AdjListGraphBenchmark.java 5771 2010-05-07 18:46:57Z gleissner $

package tests.graffiti.graph;

import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * Contains benchmarks for the adjacency list implementation of the
 * <code>org.graffiti.graph.Graph</code> interface. Call this class with the
 * "-Xmx100M" option.
 */
public class AdjListGraphBenchmark extends TestCase {
    /** The logger for the current class. */
    private static final Logger logger = Logger
            .getLogger(AdjListGraphBenchmark.class.getName());

    /** The graph for the benchmarks. */
    private Graph g;

    /**
     * Constructs a new benchmark for the <code>AdjListGraph</code> class.
     * 
     * @param name
     *            the name for the test case.
     */
    public AdjListGraphBenchmark(String name) {
        super(name);
    }

    /**
     * Main method for running all the benchmarks of this test class.
     * 
     * @param args
     *            DOCUMENT ME!
     */
    public static void main(String[] args) {
        Logger.getLogger("org.graffiti").setLevel(Level.WARNING);
        junit.textui.TestRunner.run(AdjListGraphBenchmark.class);
    }

    /**
     * Tests adding an edge to the graph.
     */
    public void testAddLotsOfNodesNEdges() {
        long nodes = 100000;
        long edges = 10000;

        logger.info("Adding " + nodes + " nodes and " + edges
                + " edges to a graph " + "object.");

        HashMap<Integer, Node> hm = new HashMap<Integer, Node>((int) nodes);

        long start = System.currentTimeMillis();

        for (int i = 0; i <= nodes; i++) {
            Node n = g.addNode();
            n.setInteger("id", i);
            n.setFloat("data.a", i / 1000);
            n.setFloat("data.b", i / 10000);
            hm.put(new Integer(i), n);
            printInfo(i, (int) (nodes / 10), "nodes");
        }

        long stop = System.currentTimeMillis();

        logger.info("Adding " + nodes + " nodes took "
                + ((stop - start) / 1000) + " secs.");

        Random r = new Random();
        start = System.currentTimeMillis();

        for (int i = 0; i <= edges; i++) {
            Node n1 = hm.get(new Integer(r.nextInt((int) nodes) + 1));
            Node n2 = hm.get(new Integer(r.nextInt((int) nodes) + 1));

            Edge e = g.addEdge(n1, n2, Edge.DIRECTED);
            e.setInteger("id", i);
            e.setFloat("data.a", i / 1000);
            e.setFloat("data.b", i / 10000);
            printInfo(i, (int) (edges / 10), "edges");
        }

        stop = System.currentTimeMillis();

        logger.info("Adding " + edges + " edges took "
                + ((stop - start) / 1000) + " secs.");

        start = System.currentTimeMillis();

        int tmp = 0;

        for (Node node : g.getNodes()) {
            // int neighbors = ((Node)it.next()).getNeighbors().size();
            g.deleteNode(node);
            tmp++;

            if (tmp > 10000) {
                break;
            }
        }

        stop = System.currentTimeMillis();

        // logger.info("Getting the neighbors of " + nodes + " nodes took " +
        // (stop - start) + "millisecs.");
        logger.info("Deleting 10000 nodes took " + (stop - start)
                + " millisecs.");
    }

    /**
     * Initializes a new graph for every test case.
     */
    @Override
    protected void setUp() {
        g = new AdjListGraph();
    }

    /**
     * Tears down the test environement.
     */
    @Override
    protected void tearDown() {
        g.clear();
    }

    /**
     * Prints a status about the current calculation.
     * 
     * @param status
     *            DOCUMENT ME!
     * @param trigger
     *            DOCUMENT ME!
     * @param msg
     *            DOCUMENT ME!
     */
    private void printInfo(int status, int trigger, String msg) {
        Runtime rt = Runtime.getRuntime();

        if ((status % trigger) == 0) {
            logger.info("current mem usage: "
                    + (rt.totalMemory() / 1024 / 1024) + "MB after adding "
                    + status + " " + msg + ".");
        }
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
