// =============================================================================
//
//   TreeWidthTest.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treeWidth;

import java.awt.geom.Point2D;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.util.Queue;

/**
 * Tests the algorithms and write the results in the datebase.
 * 
 * @author wangq
 * @version $Revision$ $Date$
 */
public class TreeWidthTest {
    public static ArrayList<Object> result1;
    public static ArrayList<Object> result2;

    public static void main(String[] args) throws SQLException {
        TreeWidthTest test = new TreeWidthTest();
        Graph graph;

        int i = 0;
        DataToDB dbtest = new DataToDB();
        dbtest.writeToDB();

        for (int numOfnode = 20; numOfnode <= 20; numOfnode += 2) {
            for (int edgeP = 80; edgeP <= 80; edgeP += 10) {

                double edgeProb = edgeP / 100.0;
                graph = test.generator(numOfnode, edgeProb);
                if (zusammenhang(graph)) {
                    System.out.println("number of Node: " + numOfnode
                            + "edge Probe : " + edgeProb);
                    test.executeTest(graph);
                    i++;
                    System.out.println(i + " " + numOfnode + " " + edgeP + " "
                            + result2.get(0) + " " + result1.get(0) + " "
                            + result1.get(1) + " " + result2.get(1) + " "
                            + result2.get(2));
                    dbtest.write(i, numOfnode, edgeP, (Integer) result2.get(0),
                            (Integer) result1.get(0), (Long) result1.get(1),
                            (Integer) result2.get(1), (Long) result2.get(2));

                } else {
                    System.out.println("nicht zusammenhang");
                    break;
                }
            }
        }
        dbtest.lesen();
    }

    /**
     * Tests the if the graph is connected.
     * 
     * @param graph
     * @return true, if the graph is connected.
     */
    private static boolean zusammenhang(Graph graph) {
        boolean zusammenhang = false;
        Node sourceNode = graph.getNodes().get(0);
        Queue q = new Queue();
        // d contains a mapping from node to an integer, the bfsnum
        Set<Node> visited = new HashSet<Node>();
        q.addLast(sourceNode);
        visited.add(sourceNode);
        while (!q.isEmpty()) {
            Node v = (Node) q.removeFirst();
            // mark all neighbors and add all unmarked neighbors
            // of v to the queue
            for (Iterator<Node> neighbours = v.getNeighborsIterator(); neighbours
                    .hasNext();) {
                Node neighbour = neighbours.next();

                if (!visited.contains(neighbour)) {
                    visited.add(neighbour);
                    q.addLast(neighbour);

                }
            }
        }

        if (graph.getNodes().size() == visited.size()) {
            zusammenhang = true;
        }
        return zusammenhang;
    }

    /**
     * @param graph
     */
    public void executeTest(Graph graph) {
        MatrixGraph graphMap = new MatrixGraph(graph);
        graphMap.createMatrixGraph();
        MinimumFillIn minFill = new MinimumFillIn(graphMap, graph);
        result1 = minFill.calculateFillin();

        MatrixGraph graphMapForAlg2 = new MatrixGraph(graph);
        graphMapForAlg2.createMatrixGraph();
        MinimumFillIn minFill1 = new MinimumFillIn(graphMapForAlg2, graph);
        result2 = minFill1.calculateFillin1();
    }

    public int getRandomPos(int length) {
        double random = Math.random();
        Float flo = new Float((length - 1) * random);
        int pos = Math.round(flo.floatValue());
        return pos;
    }

    public void addNodes(int nodeNumber, Graph graph) {
        Node[] node = new Node[nodeNumber];

        HashMap<Double, Object> xCoor = new HashMap<Double, Object>();
        HashMap<Double, Object> yCoor = new HashMap<Double, Object>();

        // start a transaction
        graph.getListenerManager().transactionStarted(this);

        // generate nodes and assign coordinates to them
        for (int i = 0; i < nodeNumber; ++i) {
            node[i] = graph.addNode();

            double x = getXYCoordinate(xCoor);
            double y = getXYCoordinate(yCoor);

            HashMapAttribute cc = new HashMapAttribute("graphics");

            node[i].addAttribute(cc, "");

            CoordinateAttribute ca = new CoordinateAttribute(
                    GraphicAttributeConstants.COORDINATE, new Point2D.Double(x,
                            y));

            node[i].addAttribute(ca, GraphicAttributeConstants.GRAPHICS);

        }

        graph.getListenerManager().transactionFinished(this);

    }

    private double getXYCoordinate(HashMap<Double, ?> xyCoor) {
        boolean seek = true;
        double result = 0.0;
        while (seek) {
            result = Math.random();
            result = result * 1000;
            Double key = new Double(result);
            if (!xyCoor.containsKey(key)) {
                xyCoor.put(key, null);
                seek = false;
            }
        }
        return result;
    }

    public void addEdge(Graph graph, int edgeNumber) {

        List<Node> nodesList = graph.getNodes();

        graph.getListenerManager().transactionStarted(this);

        while (edgeNumber > 0) {
            int pos = getRandomPos(nodesList.size());
            Node n1 = nodesList.get(pos);
            pos = getRandomPos(nodesList.size());
            Node n2 = nodesList.get(pos);
            if (!n1.getAllInNeighbors().contains(n2) && !n1.equals(n2)) {
                graph.addEdge(n1, n2, false);
                --edgeNumber;
            } else {
                continue;
            }

        }
        graph.setDirected(false);
        graph.getListenerManager().transactionFinished(this);
    }

    private Graph generator(int nodeNr, double procentOfEdge) {

        // add nodes
        Graph graph = new AdjListGraph();
        Node[] nodes = new Node[nodeNr];
        graph.getListenerManager().transactionStarted(this);
        this.addNodes(nodeNr, graph);
        int edgeNr = new Integer((int) Math.round((nodeNr * (nodeNr - 1)) / 2
                * procentOfEdge)).intValue();
        this.addEdge(graph, edgeNr);
        graph.getListenerManager().transactionFinished(this);
        return graph;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
