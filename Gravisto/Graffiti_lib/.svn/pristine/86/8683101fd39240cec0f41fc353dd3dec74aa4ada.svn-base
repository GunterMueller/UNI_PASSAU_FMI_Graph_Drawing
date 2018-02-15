// =============================================================================
//
//   EvaluationUtil.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$
package org.graffiti.plugins.algorithms.sugiyama.util;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.ObjectAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.FastGraph;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugin.algorithm.Algorithm;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.generators.RandomGraphGenerator;
import org.graffiti.plugins.algorithms.sugiyama.Sugiyama;
import org.graffiti.plugins.algorithms.sugiyama.SugiyamaAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.crossmin.BaryCenter;
import org.graffiti.plugins.algorithms.sugiyama.decycling.SCCDecycling;
import org.graffiti.plugins.algorithms.sugiyama.incremental.GridStructure;
import org.graffiti.plugins.algorithms.sugiyama.incremental.IncrementalSugiyama;
import org.graffiti.plugins.algorithms.sugiyama.incremental.IncrementalSugiyamaAnimation;
import org.graffiti.plugins.algorithms.sugiyama.incremental.Plane;
import org.graffiti.plugins.algorithms.sugiyama.incremental.SugiyamaDummyNode;
import org.graffiti.plugins.algorithms.sugiyama.incremental.SugiyamaEdge;
import org.graffiti.plugins.algorithms.sugiyama.incremental.SugiyamaNode;
import org.graffiti.plugins.algorithms.sugiyama.layout.SocialBrandesKoepf;
import org.graffiti.plugins.algorithms.sugiyama.levelling.CoffmanGraham;
import org.graffiti.plugins.ios.exporters.graphml.GraphMLWriter;

/**
 * This class offers functions to support evaluation how well the quality of a
 * sugiyama drawing of a graph is.
 */
public class EvaluationUtil {

    private static final String OUTPUT_PATH = "E:\\tests\\";

    private static final String LOG_FILE_NAME = "testData_mode1.txt";

    private static LinkedList<NodesCorrelation> correlationBefore = null;

    // private static LinkedList<NodesCorrelation> correlationAfter = null;

    private static StringBuffer[] logBuffers = new StringBuffer[18];
    private static int bufferIndex = 0;

    private static final int INSTANCES_PER_CONFIGURATION = 20;

    private static final int ALLOWED_ERRORS_PER_CONFIGURATION = 5;

    /**
     * Calculates the crossings between edges of the graph stored in the given
     * <tt>GridStructure</tt>.
     */
    public static int calculateCrossings(GridStructure grid) {
        int result = 0;
        Plane[] planes = grid.getPlanes(GridStructure.LEVEL);
        for (int i = 0; i < planes.length - 1; i++) {
            result += calculateCrossings(planes[i], true);
        }

        return result;
    }

    /**
     * Calculates the crossings between the given <tt>Plane</tt> and the one
     * next to it.
     * 
     * @param plane
     *            first of the two planes to calculate the crossings between
     * @param toNextPlane
     *            <code>true</code> if the second <tt>Plane</tt> should be the
     *            one following the first one,<br>
     *            <code>false</code> if the second <tt>Plane</tt> should be the
     *            one preceding the first one.
     */
    public static int calculateCrossings(Plane plane, boolean toNextPlane) {
        EvaluationUtil eUtil = new EvaluationUtil();
        LinkedList<EdgeSegment> segments = new LinkedList<EdgeSegment>();

        // create EdgeSegments
        Iterator<SugiyamaNode> it = plane.iterator();
        while (it.hasNext()) {
            SugiyamaNode node = it.next();
            if (node.isDummy()) {
                SugiyamaEdge edge = ((SugiyamaDummyNode) node).getEdge();
                SugiyamaNode nextNode = (toNextPlane) ? edge.getNextNode(node)
                        : edge.getPrevNode(node);
                if (nextNode != null) {
                    // edge is in the right direction
                    segments.add(eUtil.new EdgeSegment(node.getColumnNumber(),
                            nextNode.getColumnNumber()));
                }
            } else {
                Iterator<SugiyamaEdge> edgeIt = (toNextPlane) ? node
                        .getEdgesToHigherLevel().iterator() : node
                        .getEdgesToLowerLevel().iterator();
                while (edgeIt.hasNext()) {
                    SugiyamaEdge nextEdge = edgeIt.next();
                    SugiyamaNode nextNode = (toNextPlane) ? nextEdge
                            .getNextNode(node) : nextEdge.getPrevNode(node);
                    segments.add(eUtil.new EdgeSegment(node.getColumnNumber(),
                            nextNode.getColumnNumber()));
                }
            }
        }

        /*
         * System.out.print("Crossing segments between level " +
         * plane.getNumber() + " and "); if (toNextPlane) {
         * System.out.print((plane.getNumber() + 1)); } else {
         * System.out.print((plane.getNumber() - 1)); } System.out.print(" : ");
         */
        return calculateCrossings(segments);
    }

    /**
     * Calculates the crossings of <tt>EdgeSegment</tt>s of two levels next to
     * each other.
     * 
     * @param edges
     *            unsorted list of the <tt>EdgeSegment</tt>s to check
     * @return number of crossings of these segments
     */
    public static int calculateCrossings(LinkedList<EdgeSegment> edges) {
        int crossings = 0;
        LinkedList<EdgeSegment> orderedByStart = new LinkedList<EdgeSegment>();

        LinkedList<EdgeSegment> orderedByEnd = new LinkedList<EdgeSegment>();

        // sort edge segments
        Iterator<EdgeSegment> it = edges.iterator();
        while (it.hasNext()) {
            EdgeSegment nextSeg = it.next();

            // insert segment in the List orderedByStart
            boolean inserted = false;
            int position = 0;
            Iterator<EdgeSegment> startIt = orderedByStart.iterator();
            while (startIt.hasNext()) {
                EdgeSegment nextInStartList = startIt.next();
                if (nextInStartList.getFrom() > nextSeg.getFrom()) {
                    orderedByStart.add(position, nextSeg);
                    inserted = true;
                    break;
                }
                position++;
            }
            if (!inserted) {
                orderedByStart.add(nextSeg);
            }

            // insert segment in the List orderedByEnd
            inserted = false;
            position = 0;
            Iterator<EdgeSegment> endIt = orderedByEnd.iterator();
            while (endIt.hasNext()) {
                EdgeSegment nextInEndList = endIt.next();
                if (nextInEndList.getTo() > nextSeg.getTo()) {
                    orderedByEnd.add(position, nextSeg);
                    inserted = true;
                    break;
                }
                position++;
            }
            if (!inserted) {
                orderedByEnd.add(nextSeg);
            }
        }

        /*
         * for (EdgeSegment seg : orderedByStart) { System.out.print(seg +
         * "\t"); } System.out.println();
         */

        // calculate crossings
        it = orderedByStart.iterator();
        while (it.hasNext()) {
            EdgeSegment nextStart = it.next();

            Iterator<EdgeSegment> endIt = orderedByEnd.iterator();
            while (endIt.hasNext()) {
                EdgeSegment segmentToCheck = endIt.next();
                if (segmentToCheck.getTo() >= nextStart.getTo()) {
                    /*
                     * All following segments of endIt end right of nextStart
                     * (as they are ordered by their end column) and don't
                     * produce any new crossings.
                     */
                    break;
                } else if (segmentToCheck.getFrom() > nextStart.getFrom()) {
                    /*
                     * nextStart starts left from segmentToCheck but ends right
                     * of it so there is a crossing
                     */
                    crossings++;
                }
            }
        }

        return crossings;
    }

    /**
     * Computes the length of the edges of the graph stored in the given
     * GridStructure. The length of one edge is the difference of the level
     * numbers of it's start and it's end node.
     */
    public static int calculateEdgeLength(GridStructure grid) {
        int result = 0;

        // every edge has a basic length of one
        Graph graph = null;

        assert grid.getSize(GridStructure.LEVEL) > 0;
        SugiyamaNode node = grid.getPlane(GridStructure.LEVEL, 0).getNodes()
                .getFirst();
        if (node.isDummy()) {
            graph = ((SugiyamaDummyNode) node).getEdge().getEdge().getGraph();
        } else {
            graph = node.getNode().getGraph();
        }
        result += graph.getEdges().size();

        // as every dummy node increases the length of an edge by one, so count
        // all
        // dummy nodes.
        Plane[] levels = grid.getPlanes(GridStructure.LEVEL);
        for (Plane level : levels) {
            LinkedList<SugiyamaNode> nodes = level.getNodes();
            for (SugiyamaNode nextNode : nodes) {
                if (nextNode.isDummy()) {
                    result++;
                }
            }
        }
        return result;
    }

    public static int calculateHorizontalEdgeAmplitude(GridStructure grid) {
        int result = 0;

        // every edge has a basic length of one
        Graph graph = null;

        assert grid.getSize(GridStructure.LEVEL) > 0;
        SugiyamaNode node = grid.getPlane(GridStructure.LEVEL, 0).getNodes()
                .getFirst();
        if (node.isDummy()) {
            graph = ((SugiyamaDummyNode) node).getEdge().getEdge().getGraph();
        } else {
            graph = node.getNode().getGraph();
        }

        for (Edge edge : graph.getEdges()) {
            try {
                SugiyamaEdge sugEdge = (SugiyamaEdge) ((ObjectAttribute) edge
                        .getAttribute(SugiyamaConstants.PATH_INC_EDGE))
                        .getObject();
                if (sugEdge.getCenteredPosition() != 0) {
                    // edge wants to be moved so it isn't centered
                    int edgeColumn = sugEdge.getNodes().get(1)
                            .getColumnNumber();
                    int n1Col = sugEdge.getNodes().getFirst().getColumnNumber();
                    int n2Col = sugEdge.getNodes().getLast().getColumnNumber();
                    if (edgeColumn < n1Col && edgeColumn < n2Col) {
                        // edge is too far left
                        result += Math.min(n1Col, n2Col) - edgeColumn;
                    } else {
                        // edge is too far right
                        result += edgeColumn - Math.max(n1Col, n2Col);
                    }
                }
            } catch (AttributeNotFoundException e) {
                // ignore edge as it is a loop
            }
        }

        return result;
    }

    /**
     * Computes the correlations of the nodes coordinates stored in the grid
     * structure and adds the according <tt>NodeCorrelation</tt>-Objects to the
     * given LinkedList.
     */
    public static void computeNodeCorrelations(GridStructure grid,
            LinkedList<NodesCorrelation> cors) {
        EvaluationUtil eUtil = new EvaluationUtil();
        LinkedList<SugiyamaNode> allNodes = new LinkedList<SugiyamaNode>();
        for (Plane level : grid.getPlanes(GridStructure.LEVEL)) {
            for (SugiyamaNode node : level.getNodes()) {
                if (!node.isDummy()) {
                    allNodes.add(node);
                }
            }
        }

        SugiyamaNode[] nodeArray = allNodes.toArray(new SugiyamaNode[allNodes
                .size()]);

        for (int i = 0; i < nodeArray.length - 1; i++) {
            for (int j = i + 1; j < nodeArray.length; j++) {
                cors
                        .add(eUtil.new NodesCorrelation(nodeArray[i],
                                nodeArray[j]));
            }
        }
    }

    /**
     * Computes how much the correlation of the nodes coordinates has changed
     * since creating the initial correlations. Calls checkCorrelation() for
     * each NodesCorrelation-Object.
     */
    public static int calculateChangedNodeCorrelations(GridStructure grid) {
        int result = 0;

        for (NodesCorrelation cor : correlationBefore) {
            result += cor.checkCorrelation();
        }
        return result;
    }

    /**
     * Evaluates the given <tt>GridStructure</tt> by computing it's quality
     * according to several esthetics criteria.
     */
    public static void evaluteGridStructure(GridStructure grid) {
        if (logBuffers[0] == null) {
            for (int i = 0; i < 18; i++) {
                logBuffers[i] = new StringBuffer();
            }
        }
        if (bufferIndex >= 18) {
            bufferIndex = 0;
        }
        // System.out.println("Levels: " + );
        logBuffers[bufferIndex]
                .append(grid.getSize(GridStructure.LEVEL) + "\t");
        bufferIndex++;
        // System.out.println("Columns: " +
        logBuffers[bufferIndex].append(grid.getSize(GridStructure.COLUMN)
                + "\t");
        bufferIndex++;
        // System.out.println("Crossings: " +
        logBuffers[bufferIndex].append(calculateCrossings(grid) + "\t");
        bufferIndex++;
        // System.out.println("Length of edges: " +
        logBuffers[bufferIndex].append(calculateEdgeLength(grid) + "\t");
        bufferIndex++;
        // System.out.println("Horizontal amplitudes: "
        logBuffers[bufferIndex].append(calculateHorizontalEdgeAmplitude(grid)
                + "\t");
        bufferIndex++;
        if (correlationBefore != null) {
            // System.out.println("Correlations between nodes changed: "
            logBuffers[bufferIndex]
                    .append(calculateChangedNodeCorrelations(grid) + "\t");
        } else {
            logBuffers[bufferIndex].append("\t");
        }
        bufferIndex++;
    }

    public static void main(String[] args) {
        int errorsAtAll = 0;

        /*
         * for (int n = 10; n < 100; n += 10) { errorsAtAll +=
         * testOneKonfiguration(0, n, n*2); }
         * 
         * for (int n = 100; n <= 1000; n += 100) { errorsAtAll +=
         * testOneKonfiguration(0, n, n*2); }
         */

        for (int n = 10; n < 100; n += 10) {
            errorsAtAll += testOneKonfiguration(1, n, n * 2);
        }
        for (int n = 100; n < 400; n += 100) {
            errorsAtAll += testOneKonfiguration(1, n, n * 2);
        }

        System.out.println("All Konfigurations done with " + errorsAtAll
                + " errors at all.");

        /* 
         * ***************** STATISCHER SUGIYAMA ***********************
         * System.out
         * .println("CG, nodes per layer: "+algs[1].getParameters()[0].
         * getValue());
         * 
         * sugiyama = new Sugiyama(); p = sugiyama.getParameters();
         * p[0].setValue(true); data = sugiyama.getSugiyamaData(); algs = new
         * SugiyamaAlgorithm[4]; algs[0] = new BFSDecycling(); //SCCDecycling();
         * algs[1] = new CoffmanGraham(); algs[2] = new BaryCenter(); algs[3] =
         * new SocialBrandesKoepf(); for(int i = 0; i < 4; i++) {
         * algs[i].attach(g); }
         * 
         * data.setSelectedAlgorithms(algs); sugiyama.setParameters(p);
         * sugiyama.reset(); sugiyama.attach(g); try{ sugiyama.check(); }
         * catch(Exception e) { e.printStackTrace(); } sugiyama.execute();
         * addGraphics(g); ani = sugiyama.getAnimation();
         * 
         * for (int i = 0; i < 4; i++) { ani.nextStep(); writer = new
         * GraphMLWriter(); try { writer.write(g, "Z:\\testdenkas" + i +
         * ".graphml"); } catch (Exception e) { e.printStackTrace(); }
         * 
         * } // System.out.println(((IncrementalSugiyamaAnimation)
         * ani).getGrid());
         */

    }

    private static int testOneKonfiguration(int testMode, int nodes, int edges) {
        StringBuffer[] results = new StringBuffer[18];
        for (int i = 0; i < 18; i++) {
            results[i] = new StringBuffer();
            if (i == 0) {
                results[0].append(nodes + " Knoten, " + edges + " Kanten\t");
            } else {
                results[i].append("\t");
            }

            if (i % 6 != 0) {
                results[i].append("\t");
            }
        }
        results[0].append("static before\t");
        results[6].append("incremental\t");
        results[12].append("static after\t");

        int errorCounter = 0;
        int instance = 0;
        while (instance < INSTANCES_PER_CONFIGURATION
                && errorCounter < ALLOWED_ERRORS_PER_CONFIGURATION) {
            StringBuffer[] instanceResult = test(testMode, nodes, edges,
                    instance);
            if (instanceResult != null) {
                for (int i = 0; i < 18; i++) {
                    results[i].append(instanceResult[i]);
                }
                instance++;
            } else {
                errorCounter++;
            }
        }

        for (int i = 0; i < 18; i++) {
            writeToLogFile(results[i] + "\n");
        }
        writeToLogFile("\n");

        System.out.println("Konfiguration with " + nodes + " nodes and "
                + edges + " edges in Mode " + testMode + " done with "
                + errorCounter + " errors.");
        return errorCounter;
    }

    private static StringBuffer[] test(int testMode, int nodes, int edges,
            int instance) {
        try {
            for (int i = 0; i < 18; i++) {
                logBuffers[i] = new StringBuffer();
            }
            bufferIndex = 0;
            IncrementalSugiyamaAnimation.setTestMode(testMode);

            Graph g = new FastGraph();
            Algorithm generator = new RandomGraphGenerator();
            generator.attach(g);
            Parameter<?>[] params = generator.getParameters();
            ((IntegerParameter) params[0]).setValue(nodes);
            ((IntegerParameter) params[1]).setValue(edges);
            ((BooleanParameter) params[4]).setValue(true);
            ((BooleanParameter) params[7]).setValue(false);
            generator.setParameters(params);
            generator.execute();

            /* Erster Durchlauf inkrementeller Algorithmus */
            Sugiyama sugiyama = new IncrementalSugiyama();
            Parameter<?>[] p = sugiyama.getAlgorithmParameters();
            SugiyamaData data = sugiyama.getSugiyamaData();
            SugiyamaAlgorithm[] algs = new SugiyamaAlgorithm[4];
            algs[0] = new SCCDecycling();
            algs[1] = new CoffmanGraham();
            algs[2] = new BaryCenter();
            algs[3] = new SocialBrandesKoepf();
            for (int i = 0; i < 4; i++) {
                algs[i].attach(g);
            }

            data.setSelectedAlgorithms(algs);
            sugiyama.setAlgorithmParameters(p);
            sugiyama.reset();
            sugiyama.attach(g);
            try {
                sugiyama.check();
            } catch (Exception e) {
                e.printStackTrace();
            }
            sugiyama.execute();
            addGraphics(g);
            IncrementalSugiyamaAnimation ani = (IncrementalSugiyamaAnimation) sugiyama
                    .getAnimation();
            ani.setTesting_numberOfNewNodes(nodes / 10);
            ani.setTesting_numberOfNewEdgesPerNode(2);
            ani.setTesting_numberOfNodesToDelete(nodes / 10);

            ani.nextStep();
            GraphMLWriter writer = new GraphMLWriter();
            String fileName = OUTPUT_PATH + nodes + "x" + edges + "["
                    + instance + "]_";
            if (testMode == 0) {
                fileName += "add_";
            } else if (testMode == 1) {
                fileName += "delete_";
            }

            try {
                writer.write(g, fileName + "before.graphml");
            } catch (Exception e) {
                e.printStackTrace();
            }

            while (ani.hasNextStep()) {
                ani.nextStep();
                addGraphics(g);
            }

            writer = new GraphMLWriter();
            try {
                writer.write(g, fileName + "dynamic.graphml");
            } catch (Exception e) {
                e.printStackTrace();
            }

            /* 2. Durchlauf f�r statische Zeichnung */
            removeSugiyamaAttributes(g);
            p = sugiyama.getAlgorithmParameters();
            // p[0].setValue(true);
            data = sugiyama.getSugiyamaData();
            algs = new SugiyamaAlgorithm[4];
            algs[0] = new SCCDecycling();
            algs[1] = new CoffmanGraham();
            algs[2] = new BaryCenter();
            algs[3] = new SocialBrandesKoepf();
            for (int i = 0; i < 4; i++) {
                algs[i].attach(g);
            }

            data.setSelectedAlgorithms(algs);
            sugiyama.setAlgorithmParameters(p);
            sugiyama.reset();
            sugiyama.attach(g);
            try {
                sugiyama.check();
            } catch (Exception e) {
                e.printStackTrace();
            }
            sugiyama.execute();
            addGraphics(g);
            ani = (IncrementalSugiyamaAnimation) sugiyama.getAnimation();
            ani.nextStep();

            try {
                writer.write(g, fileName + "static.graphml");
            } catch (Exception e) {
                e.printStackTrace();
            }
            correlationBefore = null;
            return logBuffers;

        } catch (Exception e) {
            // There was an exception in one of the test cases so we can't use
            // the results.
            e.printStackTrace();
            correlationBefore = null;
            return null;
        }
    }

    private static void writeToLogFile(String text) {
        try {
            OutputStreamWriter file = new FileWriter(OUTPUT_PATH
                    + LOG_FILE_NAME, true);
            file.write(text);
            file.flush();
            file.close();

        } catch (FileNotFoundException e) {
            System.err.println("EvaluationUtil: Logfile doesn't exist.");
        } catch (IOException e) {
            System.err
                    .println("EvaluationUtil: I/O-Error when writing Logfile");
        }
    }

    public static void addGraphics(Graph g) {
        for (Node n : g.getNodes()) {
            try {
                n.getAttribute("graphics");
            } catch (Exception ex) {
                n.addAttribute(new NodeGraphicAttribute(), "");
            }
        }
        for (Edge e : g.getEdges()) {
            try {
                e.getAttribute("graphics");
            } catch (Exception ex) {
                e.addAttribute(new EdgeGraphicAttribute(), "");
            }
        }
    }

    public static void removeSugiyamaAttributes(Graph g) {
        for (Node n : g.getNodes()) {
            try {
                n.removeAttribute("sugiyama");
            } catch (AttributeNotFoundException e) {
                // there was no sugiyama attribute to delete
            }
        }
        for (Edge e : g.getEdges()) {
            try {
                e.removeAttribute("sugiyama");
            } catch (AttributeNotFoundException ex) {
                // there was no sugiyama attribute to delete
            }
        }
    }

    /**
     * Contains only the numbers of the start and end columns of an edge
     * segment.
     */
    public class EdgeSegment {
        int from;
        int to;

        public EdgeSegment(int from, int to) {
            this.from = from;
            this.to = to;
        }

        public int getFrom() {
            return from;
        }

        public int getTo() {
            return to;
        }

        @Override
        public String toString() {
            return ("(" + getFrom() + ", " + getTo() + ")");
        }
    }

    public class NodesCorrelation {
        Node node1;
        Node node2;

        public static final int HORIZONTAL = 0;
        public static final int VERTICAL = 1;
        /*
         * Given n1, n2 is positioned: -1 left/above of n1 0 at same position as
         * n1 1 right/below on n1
         */
        int relativeNodePosition[] = new int[2];

        public NodesCorrelation(SugiyamaNode n1, SugiyamaNode n2) {
            node1 = n1.getNode();
            node2 = n2.getNode();

            for (int i = 0; i < 2; i++) {
                double posN1 = (i == 0) ? n1.getX() : n1.getY();
                double posN2 = (i == 0) ? n2.getX() : n2.getY();

                if (posN1 < posN2) {
                    relativeNodePosition[i] = -1;
                } else if (posN1 == posN2) {
                    relativeNodePosition[i] = 0;
                } else if (posN1 > posN2) {
                    relativeNodePosition[i] = 1;
                }
            }
        }

        /**
         * Compares the actual correlation with the one at creating this object.
         * For each axis the comparison is: 0 if both correlations are equal 1
         * if the node have the same coordinate in one correlation but not in
         * the other 2 if the correlations switched side from left to right or
         * otherwise
         * 
         * @return Sum of the two comparisons 0 <= result <= 4
         */
        public int checkCorrelation() {
            int result = 0;

            if (node1.getGraph() == null || node2.getGraph() == null)
                // n1 or n2 has been deleted
                return 0;

            for (int i = 0; i < 2; i++) {
                CoordinateAttribute coord = (CoordinateAttribute) node1
                        .getAttribute("graphics.coordinate");
                double posN1 = (i == 0) ? coord.getX() : coord.getY();

                coord = (CoordinateAttribute) node2
                        .getAttribute("graphics.coordinate");
                double posN2 = (i == 0) ? coord.getX() : coord.getY();

                int newPosition = 0;

                if (posN1 < posN2) {
                    newPosition = -1;
                } else if (posN1 == posN2) {
                    newPosition = 0;
                } else if (posN1 > posN2) {
                    newPosition = 1;
                }
                result += Math.abs(newPosition - relativeNodePosition[i]);
            }
            return result;
        }
    }

    public static void calculateCorrelationBefore(GridStructure grid) {
        correlationBefore = new LinkedList<NodesCorrelation>();
        computeNodeCorrelations(grid, correlationBefore);
    }
}
