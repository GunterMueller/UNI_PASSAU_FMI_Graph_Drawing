/*
 * GeoThicknessTest.java
 *
 * Copyright (c) 2001-2006 Gravisto Team, University of Passau
 *
 * Created on Oct 16, 2005
 *
 */

package org.graffiti.plugins.algorithms.GeoThickness;

import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.selection.Selection;

/**
 * @author ma
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class GeoThicknessTest {
    /**
     * 
     * @uml.property name="graph"
     * @uml.associationEnd
     * @uml.property name="graph" multiplicity="(0 -1)"
     *               elementType="org.graffiti.graph.Edge"
     */

    int maxNodeNumber = 100;

    double porcentOfEdge = 0.0;

    public GeoThicknessTest() {
    }

    public void setPorcentOfEdge(double porcenteOfEdge) {
        this.porcentOfEdge = porcenteOfEdge;
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

    private double getXYCoordinate(HashMap<Double, Object> xyCoor) {
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

    private void addEdgeZusammen(Graph graph, int edgeNumber) {
        List<Node> nodesList = graph.getNodes();

        graph.getListenerManager().transactionStarted(this);

        int position = getRandomPos(nodesList.size());

        Node node = nodesList.remove(position);

        while (nodesList.size() > 0) {
            int position2 = getRandomPos(nodesList.size());
            Node n2 = nodesList.remove(position2);
            graph.addEdge(node, n2, false);
            --edgeNumber;
            node = n2;
        }

        nodesList = graph.getNodes();

        while (edgeNumber > 0) {
            int pos = getRandomPos(nodesList.size());
            Node n1 = nodesList.get(pos);
            pos = getRandomPos(nodesList.size());
            Node n2 = nodesList.get(pos);
            if (!n1.getAllInNeighbors().contains(n2)
                    && !n1.getAllOutNeighbors().contains(n2) && !n1.equals(n2)) {
                graph.addEdge(n1, n2, false);
                --edgeNumber;
            } else {
                continue;
            }

        }
        graph.setDirected(false);
        graph.getListenerManager().transactionFinished(this);
    }

    // main method
    public static void main(String[] args) {
        GeoThicknessTest geoTest = new GeoThicknessTest();
        // geoTest.graphClass(1);
        // geoTest.graphClass(2);
        // geoTest.graphClass(3);
        // geoTest.graphClassDunne();
        // geoTest.graphClassDunne();
        geoTest.compareErgebnisse();
    }

    @SuppressWarnings("unused")
    private void graphClassDunne() {

        String hauptPath = "/home/cip/ma/diplomarbeit/GraphTesten/";

        int edge = 0;

        FileWriter fileWriter;

        PrintWriter writer;

        try {

            fileWriter = new FileWriter(hauptPath + "2.5nGraph/"
                    + "Graphdunner" + ".dat");
            writer = new PrintWriter(new BufferedWriter(fileWriter));

            writer.println("# approch " + " Node Number: " + " Edge Number "
                    + " Cross Number " + " Geometric Thickness "
                    + " run time for crossnummber  "
                    + " run time for thickness " + "run time");
            writer.flush();

            for (int i = 10; i <= 100; i += 10) {

                edge = new Integer((int) Math.round((i * (i - 1)) / 2 * 0.50))
                        .intValue();

                for (int j = 0; j < 5; j++) {
                    Graph graph = new AdjListGraph();
                    this.addNodes(i, graph);
                    writerTest(graph, i, edge, writer);
                }
            }
            fileWriter.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    private void graphClass(int classTyp) {

        String hauptPath = "/home/cip/ma/diplomarbeit/GraphTesten/";

        int edgeN1 = 0, edgeN2 = 0, edgeN3 = 0;

        FileWriter fileWriter1, fileWriter2, fileWriter3;

        PrintWriter writer1, writer2, writer3;

        try {

            fileWriter1 = new FileWriter(hauptPath + "dunnerGraphen/"
                    + " dunnerGraph100" + ".dat");
            fileWriter2 = new FileWriter(hauptPath + "mittleGraphen/"
                    + " mittleGraph100" + ".dat");
            fileWriter3 = new FileWriter(hauptPath + "completeGraphen/"
                    + " completeGraph100" + ".dat");

            writer1 = new PrintWriter(new BufferedWriter(fileWriter1));
            writer2 = new PrintWriter(new BufferedWriter(fileWriter2));
            writer3 = new PrintWriter(new BufferedWriter(fileWriter3));

            writer1.println("# approch " + " Node Number: " + " Edge Number "
                    + " Cross Number " + " Geometric Thickness "
                    + " run time for crossnummber  "
                    + " run time for thickness " + "run time");
            writer1.flush();
            writer2.println("# approch " + " Node Number: " + " Edge Number "
                    + " Cross Number " + " Geometric Thickness "
                    + " run time for crossnummber  "
                    + " run time for thickness " + "run time");
            writer2.flush();
            writer3.println("# approch " + " Node Number: " + " Edge Number "
                    + " Cross Number " + " Geometric Thickness "
                    + " run time for crossnummber  "
                    + " run time for thickness " + "run time");
            writer3.flush();

            // for (int i = 90; i <= 100; i+=10) {
            int i = 100;

            edgeN1 = (int) Math.round((i * (i - 1)) / 2 * 0.50);

            edgeN2 = (int) Math.round((i * (i - 1)) / 2 * 0.75);

            edgeN3 = Math.round((i * (i - 1)) / 2);

            for (int j = 0; j < 5; j++) {
                Graph graph = new AdjListGraph();
                this.addNodes(i, graph);
                writerTest(graph, i, edgeN1, writer1);
                writerTest(graph, i, edgeN2, writer2);
                writerTest(graph, i, edgeN3, writer3);
            }
            // }
            fileWriter1.close();
            fileWriter2.close();
            fileWriter3.close();
            writer1.close();
            writer2.close();
            writer3.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writerTest(Graph g, int numberOfNode, int numberOfEdge,
            PrintWriter writer) {

        for (int k = 0; k < 5; k++) {

            this.resetEdge(g);

            this.addEdgeZusammen(g, numberOfEdge);

            GeoThicknessCalculationAlgorithm geoAlg = new GeoThicknessCalculationAlgorithm();
            geoAlg.attach(g);

            try {
                geoAlg.check();
            } catch (PreconditionException ex) {
                ex.printStackTrace();
            }

            int crossNumber, geoThickness;
            long runtime, runtimeforcross, fulltime;

            System.out.println("graph: " + " node: " + g.getNodes().size()
                    + " edge: " + g.getEdges().size());

            for (int l = 1; l <= 4; l++) {
                geoAlg.setApporch(l);
                geoAlg.execute();
                crossNumber = geoAlg.getCross();
                geoThickness = geoAlg.getThickness();
                runtime = geoAlg.getRuntime();
                if (l != 1) {
                    runtimeforcross = geoAlg.getRuntimeforCross();
                } else {
                    runtimeforcross = 0;
                }
                fulltime = runtime + runtimeforcross;
                writer.println(l + "  " + numberOfNode + "  " + numberOfEdge
                        + "  " + crossNumber + "  " + geoThickness + "  "
                        + runtimeforcross + "  " + runtime + "  " + fulltime);
                writer.flush();
            }
        }
    }

    private void resetEdge(Graph g) {
        g.getListenerManager().transactionStarted(this);

        Iterator<Edge> edgeIt = g.getEdgesIterator();
        while (edgeIt.hasNext()) {
            Edge edge = edgeIt.next();
            g.deleteEdge(edge);
        }

        g.getListenerManager().transactionFinished(this);
    }

    private void compareErgebnisse() {

        String hauptPath = "/home/cip/ma/diplomarbeit/GraphTesten/";

        int edge = 0;

        FileWriter fileWriter1, fileWriter2;

        PrintWriter writer1, writer2;

        try {

            fileWriter1 = new FileWriter(hauptPath + "2.5High/" + "2.5Graph"
                    + ".dat");

            writer1 = new PrintWriter(new BufferedWriter(fileWriter1));

            fileWriter2 = new FileWriter(hauptPath + "2.5High/" + "2.5High"
                    + ".dat");

            writer2 = new PrintWriter(new BufferedWriter(fileWriter2));

            writer1.println("# approch " + " Node Number: " + " Edge Number "
                    + " Cross Number" + " Geometric Thickness "
                    + " run time for crossnummber "
                    + " run time for thickness " + " run time ");
            writer1.flush();

            writer2.println("# approch " + " Node Number: " + " Edge Number "
                    + " Cross Number" + " Geometric Thickness "
                    + " run time for crossnummber "
                    + " run time for thickness " + " run time ");
            writer2.flush();

            for (int i = 10; i <= 100; i += 10) {

                edge = new Integer((int) Math.round(i * 2.5)).intValue();

                for (int j = 0; j < 5; j++) {
                    Graph graph = new AdjListGraph();
                    this.addNodes(i, graph);
                    writerTestHigh(i, edge, writer1, writer2, graph);
                }
            }
            fileWriter1.close();
            writer1.close();
            fileWriter2.close();
            writer2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writerTestHigh(int numberOfNode, int numberOfEdge,
            PrintWriter writer1, PrintWriter writer2, Graph g) {

        for (int k = 0; k < 5; k++) {

            this.resetEdge(g);

            this.addEdgeZusammen(g, numberOfEdge);

            GeoThicknessCalculationAlgorithm geoAlg = new GeoThicknessCalculationAlgorithm();
            geoAlg.attach(g);

            try {
                geoAlg.check();
            } catch (PreconditionException ex) {
                ex.printStackTrace();
            }

            int crossNumber1, crossNumber2, geoThickness1, geoThickness2;
            long runtime1, runtimeforcross1, fulltime1;
            long runtime2, runtimeforcross2, fulltime2;

            System.out.println("graph: " + " node: " + g.getNodes().size()
                    + " edge: " + g.getEdges().size());

            System.out.println("graph: " + " node: " + g.getNodes().size()
                    + " edge: " + g.getEdges().size());

            geoAlg.setApporch(2);
            geoAlg.execute();
            crossNumber1 = geoAlg.getCross();
            geoThickness1 = geoAlg.getThickness();
            runtime1 = geoAlg.getRuntime();
            runtimeforcross1 = geoAlg.getRuntimeforCross();

            fulltime1 = runtime1 + runtimeforcross1;
            writer1.println("2" + "  " + numberOfNode + "  " + numberOfEdge
                    + "  " + crossNumber1 + "  " + geoThickness1 + "  "
                    + runtimeforcross1 + "  " + runtime1 + "  " + fulltime1);
            writer1.flush();

            HighDimEmbedAlgorithm highAlg = new HighDimEmbedAlgorithm();
            highAlg.attach(g);

            Parameter<?>[] params = new Parameter<?>[6];

            SelectionParameter selParam = new SelectionParameter("",
                    "<html>The selection to work on.<p>If empty, "
                            + "the whole graph is used.</html>");
            selParam.setSelection(new Selection(" "));

            params[0] = selParam;

            IntegerParameter targetDimParam = new IntegerParameter(new Integer(
                    1), new Integer(1), new Integer(Integer.MAX_VALUE),
                    "Target dimensions",
                    "the number of dimensions (or axis) to "
                            + "be computed for drawing output");

            targetDimParam.setValue(new Integer(numberOfNode));

            params[1] = targetDimParam;

            IntegerParameter highEmbedDimParam = new IntegerParameter(
                    new Integer(1), new Integer(1), new Integer(
                            Integer.MAX_VALUE), "High-Embedding dimensions",
                    "the number of dimensions to embed the "
                            + "graph in order to scatter properly (interstep)");

            highEmbedDimParam.setValue(new Integer(numberOfNode));
            params[2] = highEmbedDimParam;

            IntegerParameter scale = new IntegerParameter(new Integer(100),
                    new Integer(0), new Integer(Integer.MAX_VALUE),
                    "Graph scale",
                    "the value the output graph is multiplied with"
                            + "to define final size");

            scale.setValue(new Integer(100));
            params[3] = scale;

            params[4] = new BooleanParameter(false, "Pivot-labeling",
                    "Label Pivots ascendingly");

            params[5] = new BooleanParameter(false, "Dijkstra",
                    "use Dijkstra instead of BFS");

            highAlg.setAlgorithmParameters(params);

            try {
                highAlg.check();
            } catch (PreconditionException ex) {
                ex.printStackTrace();
            }

            highAlg.execute();

            GeoThicknessCalculationAlgorithm geoAlg1 = new GeoThicknessCalculationAlgorithm();
            geoAlg1.attach(g);
            try {
                geoAlg1.check();
            } catch (PreconditionException ex) {
                ex.printStackTrace();
            }
            geoAlg1.setApporch(2);
            geoAlg1.execute();

            crossNumber2 = geoAlg1.getCross();
            geoThickness2 = geoAlg1.getThickness();
            runtime2 = geoAlg1.getRuntime();
            runtimeforcross2 = geoAlg1.getRuntimeforCross();
            fulltime2 = runtime2 + runtimeforcross2;
            writer2.println("2" + "  " + numberOfNode + "  " + numberOfEdge
                    + "  " + crossNumber2 + "  " + geoThickness2 + "  "
                    + runtimeforcross2 + "  " + runtime2 + "  " + fulltime2);
            writer2.flush();

        }
    }

} // End of class
