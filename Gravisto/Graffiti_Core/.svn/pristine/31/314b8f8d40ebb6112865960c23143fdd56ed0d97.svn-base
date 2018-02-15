package org.graffiti.plugins.algorithms.circulardrawing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * @author demirci Created on Sep 17, 2005
 */
public class LongestPathTest {

    public LongestPathTest() {
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
     * @param graph
     * @return a node with maximal degree.
     */
    public Node getMaxGradNode(Graph graph) {
        Node maxGradNode = null;
        int maxGrad = 0;
        Iterator nodes = graph.getNodesIterator();
        if (nodes.hasNext()) {
            maxGradNode = (Node) nodes.next();
            maxGrad = maxGradNode.getInDegree();
        }
        while (nodes.hasNext()) {
            Node n = (Node) nodes.next();
            int tmpNodeGrad = n.getInDegree();
            if (tmpNodeGrad > maxGrad) {
                maxGradNode = n;
            }
        }
        return maxGradNode;
    }

    /**
     * @param graph
     * @return a node with minimal degree.
     */
    public Node getMinGradNode(Graph graph) {
        Node minGradNode = null;
        int minGrad = 0;
        Iterator nodes = graph.getNodesIterator();
        if (nodes.hasNext()) {
            minGradNode = (Node) nodes.next();
            minGrad = minGradNode.getInDegree();
        }
        while (nodes.hasNext()) {
            Node n = (Node) nodes.next();
            int tmpNodeGrad = n.getInDegree();
            if (minGrad > tmpNodeGrad) {
                minGradNode = n;
            }
        }
        return minGradNode;
    }

    /**
     * @param graph
     * @return a node with average degree.
     */
    public Node getAverageGradNode(Graph graph) {
        Node avGradNode = null;
        int edgeNumber = graph.getNumberOfEdges();
        int nodeNumber = graph.getNumberOfNodes();
        int avGrad = (int) Math.floor(2 * edgeNumber / nodeNumber);
        Iterator nodes = graph.getNodesIterator();
        if (nodes.hasNext()) {
            avGradNode = (Node) nodes.next();
        }
        while (nodes.hasNext()) {
            Node n = (Node) nodes.next();
            int tmpNodeGrad = n.getInDegree();
            if (avGrad == tmpNodeGrad) {
                avGradNode = n;
            }
        }
        return avGradNode;
    }

    /**
     * @param graph
     * @return a node with random position
     */
    public Node getRandomSourceNode(Graph graph) {
        List nodes = graph.getNodes();
        int nodeNumber = graph.getNumberOfNodes();
        int randomPos = getRandomPos(nodeNumber);
        Node randomSourceNode = (Node) nodes.get(randomPos);
        return randomSourceNode;
    }

    public static void main(String[] args) {

        Graph graph = new AdjListGraph();
        LongestPathTest lpTest = new LongestPathTest();

        int[] proz = { 4, 5 };
        for (int j = 0; j < proz.length; j++) {
            int prozent = proz[j];

            try {
                String dirName1 = new Integer(prozent).toString();
                String dirName2 = "prozDichteGraphen";
                String dirName = dirName1 + dirName2;
                File dir = new File("/home/cip/demirci/"
                        + "Graffiti_CircularDrawing/generatedTestGraphs2/"
                        + dirName);
                File outputDir = new File("/home/cip/demirci/"
                        + "Graffiti_CircularDrawing/longestPathTest/");
                File[] allFile = dir.listFiles();
                // File for runtime analyse
                FileWriter fileWriter = new FileWriter(
                        "/home/cip/demirci/Graffiti_CircularDrawing/"
                                + "longestPathTest/" + dirName + ".dat");
                PrintWriter writer = new PrintWriter(new BufferedWriter(
                        fileWriter));
                writer.println("# NodeNumber " + " EdgeNumber " + " maxDegree "
                        + " minDegree " + " averageDegree" + " randomNode");
                writer.println("# ----------- " + " ---------- "
                        + " --------- " + " --------- " + " -------------"
                        + " ----------");
                writer.flush();
                int i = 0;
                if (prozent == 1) {
                    i = 20;
                } else if (prozent == 2) {
                    i = 10;
                } else if (prozent == 3) {
                    i = 6;
                } else if (prozent == 4) {
                    i = 5;
                } else {
                    i = 4;
                }
                for (; i < allFile.length; i++) {
                    File nextFile = allFile[i];
                    String fileName = dir.toString() + "/" + nextFile.getName();
                    String outputFileName = outputDir.toString() + "/"
                            + nextFile.getName();
                    Graph2Gml graph2Gml = new Graph2Gml(fileName,
                            outputFileName);
                    graph2Gml.toConvert();

                    GmlReader gr = new GmlReader();
                    String data = graph2Gml.getOutputFileName();
                    File file = new File(data);

                    InputStream is = null;

                    try {
                        is = new FileInputStream(file);
                    } catch (FileNotFoundException exp) {
                        exp.printStackTrace();
                    }

                    try {
                        graph = gr.read(is);
                    } catch (IOException exp) {
                        exp.printStackTrace();
                    }

                    int nodeNumber = graph.getNumberOfNodes();
                    int edgeNumber = graph.getNumberOfEdges();
                    LongestPath lp = new LongestPath();
                    // System.out.println("Es wurde von einem Knoten mit
                    // maximaler Grad gestartet und ");
                    Node startNode = lpTest.getMaxGradNode(graph);
                    lp.setSourceNode(startNode);
                    lp.attach(graph);

                    lp.execute();
                    int pathLengthWithMaxDegree = ((Integer) lp.getResult()
                            .getResult().get("pathLength")).intValue();
                    // System.out.println("Die L�nge des gefundenes Weges ist "
                    // + pathLengthWithMaxDegree);
                    lp.reset();
                    // lp = new LongestPath();
                    // lp.attach(graph);
                    // System.out.println("Es wurde von einem Knoten mit
                    // minimaler Grad gestartet und ");
                    startNode = lpTest.getMinGradNode(graph);
                    lp.setSourceNode(startNode);

                    lp.execute();
                    int pathLengthWithMinDegree = ((Integer) lp.getResult()
                            .getResult().get("pathLength")).intValue();
                    // System.out.println("Die L�nge des gefundenes Weges ist "
                    // + pathLengthWithMinDegree);
                    lp.reset();
                    // lp = new LongestPath();
                    // lp.attach(graph);
                    // System.out.println("Es wurde von einem Knoten mit
                    // durchschnittlicher Grad gestartet und ");
                    startNode = lpTest.getAverageGradNode(graph);
                    lp.setSourceNode(startNode);

                    lp.execute();
                    int pathLengthWithAvDegree = ((Integer) lp.getResult()
                            .getResult().get("pathLength")).intValue();
                    // System.out.println("Die L�nge des gefundenes Weges ist "
                    // + pathLengthWithAvDegree);
                    lp.reset();

                    startNode = lpTest.getRandomSourceNode(graph);
                    lp.execute();
                    int pathLengthWithRandomNode = ((Integer) lp.getResult()
                            .getResult().get("pathLength")).intValue();

                    lp.reset();

                    writer.println(nodeNumber
                            + " "
                            + edgeNumber
                            + " "
                            + 100
                            + " "
                            + new Double((double) 100 * pathLengthWithMaxDegree
                                    / nodeNumber).doubleValue()
                            + " "
                            + new Double((double) 100 * pathLengthWithMinDegree
                                    / nodeNumber).doubleValue()
                            + " "
                            + new Double((double) 100 * pathLengthWithAvDegree
                                    / nodeNumber).doubleValue()
                            + " "
                            + new Double((double) 100
                                    * pathLengthWithRandomNode / nodeNumber)
                                    .doubleValue() + " "
                            + pathLengthWithMaxDegree + " "
                            + pathLengthWithMinDegree + " "
                            + pathLengthWithAvDegree + " "
                            + pathLengthWithRandomNode);
                    writer.flush();

                    file.delete();
                    graph = null;
                } // end of For
                writer.close();
            } // End Of try
            catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        } // End of �ussere ForSchleife
    }// End of main
}
