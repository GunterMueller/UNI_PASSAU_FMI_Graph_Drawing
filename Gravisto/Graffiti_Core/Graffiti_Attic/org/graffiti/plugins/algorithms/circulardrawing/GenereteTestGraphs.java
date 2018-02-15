package org.graffiti.plugins.algorithms.circulardrawing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * Generete biconnected Graphs in graph format.
 * 
 * @author demirci Created on Aug 23, 2005
 */
public class GenereteTestGraphs {
    /** Konstruktur */

    public GenereteTestGraphs() {
    }

    /**
     * @param length
     * @return random number
     */
    public int getRandomPos(int length) {
        double random = Math.random();
        Float flo = new Float((length - 1) * random);
        int pos = Math.round(flo.floatValue());
        return pos;
    }

    /**
     * @param size
     * @return a random position for the node
     */
    public double getRandomNodePos(int size) {
        double random = Math.random();
        double pos = random * size;
        return pos;
    }

    /**
     * generate a circle Graph
     * 
     * @param circleNodeNumber
     */
    public void generateCircle(Graph graph, int circleNodeNumber) {
        Node[] circleNodes = new Node[circleNodeNumber];
        for (int i = 0; i < circleNodeNumber; i++) {
            Node node = graph.addNode();
            node.setInteger("node.id", i + 1);
            circleNodes[i] = node;
        }
        int length = circleNodes.length;
        for (int i = 0; i < length; i++) {
            Node node1 = circleNodes[i];
            Node node2 = circleNodes[(i + 1) % length];
            graph.addEdge(node1, node2, false);
        }
        graph.setDirected(false);
    }

    /**
     * add the generete path to the graph at the random nodes in the Graph
     * 
     * @param path
     */
    public void addPathToGraph(Graph graph, int pathLength) {
        int graphNodeNumber = graph.getNumberOfNodes();
        int firstNodePos = getRandomPos(graphNodeNumber);
        int secondNodePos = getRandomPos(graphNodeNumber);
        boolean bol = firstNodePos == secondNodePos;
        while (bol) {
            secondNodePos = getRandomPos(graphNodeNumber);
            bol = firstNodePos == secondNodePos;
        }
        Object[] graphNodes = graph.getNodes().toArray();
        Node node1 = (Node) graphNodes[firstNodePos];
        Node node2 = (Node) graphNodes[secondNodePos];

        Node[] pathNodes = new Node[pathLength];
        for (int i = 0; i < pathLength; i++) {
            Node pathNode = graph.addNode();
            pathNode.setInteger("node.id", graphNodeNumber + 1);
            pathNodes[i] = pathNode;
            graphNodeNumber++;
        }

        for (int i = 0; i < pathLength - 1; i++) {
            Node pathNode1 = pathNodes[i];
            Node pathNode2 = pathNodes[i + 1];
            graph.addEdge(pathNode1, pathNode2, false);
        }

        Node pathStartNode = pathNodes[0];
        Node pathEndNode = pathNodes[pathLength - 1];
        graph.addEdge(node1, pathStartNode, false);
        graph.addEdge(pathEndNode, node2, false);
    }

    public void reset(Graph g) {
        g = null;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        int[] proz = { 1, 2, 3, 4, 5 };
        double[] dichte = { 0.01, 0.02, 0.03, 0.04, 0.05 };
        for (int i = 0; i < dichte.length; i++) {
            double d = dichte[i];
            int prozent = proz[i];
            int maxNodeNumber = 40;
            int nodeNumber = 10;

            try {
                while (nodeNumber <= maxNodeNumber) {
                    Graph graph = new AdjListGraph();
                    GenereteTestGraphs lpTest = new GenereteTestGraphs();

                    int circleNodeNumber = 3;
                    int randomCircleNodeNumber = lpTest
                            .getRandomPos(nodeNumber);
                    if (circleNodeNumber < randomCircleNodeNumber) {
                        circleNodeNumber = randomCircleNodeNumber;
                    }
                    // System.out.println("circle node number ist " +
                    // circleNodeNumber);
                    lpTest.generateCircle(graph, circleNodeNumber);

                    int graphNodeNumber = graph.getNumberOfNodes();
                    // System.out.println("graphNodeNumber ist " +
                    // graphNodeNumber);
                    while (graphNodeNumber < nodeNumber) {
                        if (circleNodeNumber != nodeNumber) {
                            int pathNodeNumber = 1;
                            int possibleNodeNumber = nodeNumber
                                    - graphNodeNumber;
                            // System.out.println("possibleNodeNumber ist " +
                            // possibleNodeNumber);
                            int randomPathNodeNumber = lpTest
                                    .getRandomPos(possibleNodeNumber);
                            // System.out.println("randomPathNodeNumber ist " +
                            // randomPathNodeNumber);
                            if (pathNodeNumber < randomPathNodeNumber) {
                                pathNodeNumber = randomPathNodeNumber;
                            }
                            // System.out.println("path l�nge ist " +
                            // pathNodeNumber);
                            lpTest.addPathToGraph(graph, pathNodeNumber);
                            // System.out.println("path wurde an graph eingef�gt
                            // ");
                            graphNodeNumber = graph.getNumberOfNodes();
                            // System.out.println("nach dem einf�gen des pfades
                            // hat der graph " +
                            // graphNodeNumber + " knoten");
                        }
                    }

                    int maxEdgeNumber = (int) Math.round((nodeNumber
                            * (nodeNumber - 1) / 2)
                            * d);
                    int edgeNumber = graph.getNumberOfEdges();

                    String dirName = new Integer(prozent).toString()
                            + "prozDichteGraphen50Knoten";

                    File dirFile = new File(
                            "/home/cip/demirci/Graffiti_CircularDrawing/"
                                    + "generatedTestGraphs2/" + dirName);

                    if (!dirFile.exists()) {
                        dirFile.mkdir();
                    }

                    String dateiName = new Integer(nodeNumber).toString()
                            + "Kn" + new Integer(maxEdgeNumber).toString()
                            + "Kan";
                    File dateiFile = new File(dirFile.getPath(), dateiName
                            + ".graph");
                    if (!dateiFile.exists()) {
                        dateiFile.createNewFile();
                    }
                    FileWriter fileWriter = new FileWriter(dateiFile);
                    PrintWriter writer = new PrintWriter(new BufferedWriter(
                            fileWriter));

                    Object[] graphNodes = graph.getNodes().toArray();
                    int allEdgesNumber = nodeNumber * (nodeNumber - 1) * 1 / 2;
                    while (edgeNumber <= maxEdgeNumber) {
                        int j = lpTest.getRandomPos(nodeNumber);
                        int k = lpTest.getRandomPos(nodeNumber);
                        if (j != k) {
                            Node no1 = (Node) graphNodes[j];
                            // System.out.println("no1 ist " + no1);
                            Node no2 = (Node) graphNodes[k];
                            // System.out.println("no2 ist " + no2);
                            if (edgeNumber != allEdgesNumber) {
                                if (!no1.getNeighbors().contains(no2)) {
                                    edgeNumber++;
                                    graph.addEdge(no1, no2, false);
                                    // System.out.println("Kante wurde
                                    // eingef�gt");
                                    if (edgeNumber == allEdgesNumber) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    System.out.println("Graph wurde erzeugt");
                    System.out.print("Erzeugte Graph hat "
                            + graph.getNumberOfNodes() + " Knoten");
                    System.out.println(" und " + graph.getNumberOfEdges()
                            + " Kanten");
                    writer.println(graph.getNumberOfNodes() + " "
                            + graph.getNumberOfEdges());
                    writer.flush();
                    Iterator nodes = graph.getNodesIterator();
                    for (; nodes.hasNext();) {
                        Node n = (Node) nodes.next();
                        String nodeInfo = "";
                        Iterator neighbors = n.getNeighborsIterator();
                        while (neighbors.hasNext()) {
                            Node neighbor = (Node) neighbors.next();
                            Edge edge = graph.getEdges(n, neighbor).iterator()
                                    .next();
                            Node source = edge.getSource();
                            if (n.equals(source)) {
                                nodeInfo = nodeInfo
                                        + neighbor.getInteger("node.id") + " ";
                            }
                        }
                        if (!nodeInfo.equals("")) {
                            writer.println(nodeInfo);
                            writer.flush();
                        }
                    }
                    nodeNumber += 10;
                } // End of while 1
            } // End of try
            catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        } // End of for
    } // End of main
}
