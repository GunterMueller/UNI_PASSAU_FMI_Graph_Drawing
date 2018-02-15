package tests.graffiti.plugins.algorithms.sugiyama;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.attributes.StringAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.sugiyama.crossmin.BaryCenter;
import org.graffiti.plugins.algorithms.sugiyama.crossmin.BilayerCrossCounter;
import org.graffiti.plugins.algorithms.sugiyama.crossmin.CrossMinAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.crossmin.Median;
import org.graffiti.plugins.algorithms.sugiyama.crossmin.Sifting;
import org.graffiti.plugins.algorithms.sugiyama.crossmin.global.CrossMinObject;
import org.graffiti.plugins.algorithms.sugiyama.crossmin.global.CrossMinObjectCollector;
import org.graffiti.plugins.algorithms.sugiyama.crossmin.global.GlobalBarycenter;
import org.graffiti.plugins.algorithms.sugiyama.crossmin.global.GlobalMedian;
import org.graffiti.plugins.algorithms.sugiyama.crossmin.global.GlobalSifting;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;
import org.graffiti.plugins.ios.exporters.graphml.GraphMLWriter;

public class GlobalSiftingBenchmark {
    private static int curNodes;
    private static int curLoop;
    private static Connection con;
    private static int graphID;
    private static int TESTCASE;
    private static double curDensity;
//    private static double curDummyDensity;
//    private static boolean firstRun;

    @SuppressWarnings("deprecation")
    public static void main(String[] args) {
        setLoggingLevel();
        curNodes = 0;
        curLoop = 0;

        // make an sql connection
        con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            // jdbc:mysql://host:port/db
            String db_url = "jdbc:mysql://" + args[0] + ":3306/" + args[1];
            // url, user, pass
            con = DriverManager.getConnection(db_url, args[2], args[3]);
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Error while loading database driver: "
                    + cnfe.getMessage());
            cnfe.printStackTrace();
            System.exit(1);
        } catch (SQLException sqle) {
            System.out.println("Error while creating database connection: "
                    + sqle.getMessage());
            System.exit(1);
        }
        // create the algorithms
        Median m = new Median();
        BaryCenter b = new BaryCenter();
        Sifting s = new Sifting();
        Sifting os = new Sifting();
        Sifting ks = new Sifting();
        GlobalBarycenter gb = new GlobalBarycenter();
        GlobalMedian gm = new GlobalMedian();
        GlobalSifting gs = new GlobalSifting();
        // ILPCrossMin ilp = new ILPCrossMin();

        CrossMinAlgorithm[] algorithms = new CrossMinAlgorithm[] { b, s, ks,
                os, gb, gm, gs, m };

        System.out.print(new Date().toGMTString() + " Executing dry run");
        dryRun(algorithms);
        System.out.print(" [OK]\n");

        // ILP
        // TESTCASE=910;
        // testCase(false, 9, 50, 1.25, 0.3, false, 1, algorithms, false);

        // nodes, horizontal
        // TESTCASE = 900;
        // testCase(false, 900, 10000, 2, 0.75, true, 100, algorithms, false);

        // nodes, cyclic
        TESTCASE = 911;
        testCase(true, 900, 10000, 2, 0.75, true, 100, algorithms, false);

        // sweeps
        // TESTCASE = 905;
        // testSweeps(true, 2500, 1.25, 0.75, true, 1, 50, algorithms, false);
        // dummy node density, hori, 2500, ed 2, chris
        // TESTCASE = 901;
        // testDummyDensity(false, 2500, 2, 0, 1, algorithms);

        // nodes, horizontal, bcinit
        // TESTCASE = 909;
        // testCase(false, 900, 10000, 2, 0.75, true, 100, algorithms, true);
        // sweeps
        // TESTCASE = 906;
        // testSweeps(true, 2500, 2, 0.75, true, 1, 50, algorithms, false);
        // dummy node density
        // TESTCASE = 902;
        // testDummyDensity(true, 2500, 1.5, 0, 1, algorithms);
        // TESTCASE = 903;
        // testDummyDensity(false, 2500, 1.5, 0, 1, algorithms);
        // TESTCASE = 904;
        // testDummyDensity(true, 2500, 3, 0, 1, algorithms);

        // edge density, cyclic
        // curDensity = 0;
        // TESTCASE = 907;
        // testEdgeDensity(true, 2500, 0.025 , 6, 0.75d, true, 0.025d,
        // algorithms, false);

        curDensity = 0;
        TESTCASE = 908;
        testEdgeDensity(false, 1000, 0.025, 6, 0.5d, true, 0.025d, algorithms,
                false);

        // edge density, cyclic
        // curDensity = 3.675d;
        // TESTCASE = 707;
        // testEdgeDensity(true, 2500, 0.025 , 6, 0.75d, true, 0.025d,
        // algorithms, false);
        // edge density, horizontal
        // curDensity = 7.475d;
        // TESTCASE = 708;
        // testEdgeDensity(false, 1000, 6 , 10, 0.5d, true, 0.025d, algorithms,
        // false);

        // nodes, horizontal, bcinit
        // TESTCASE = 709;
        // testCase(false, 900, 10000, 2, 0.75, true, 100, algorithms, true);

        // TESTCASE = 703;
        // testDummyDensity(false, 2500, 1.5, 0, 1, algorithms);

    }

//    // testDummyDensity(true, 2500, 3, 0, 0.95, algorithms);
//    @SuppressWarnings("deprecation")
//    private static void testDummyDensity(boolean CYCLIC, int NODES,
//            double EDGE_DENSITY, double MIN_DUMMY_DENSITY,
//            double MAX_DUMMY_DENSITY, CrossMinAlgorithm[] algorithms) {
//        curNodes = 0;
//        curDummyDensity = 0;
//        firstRun = true;
//        while (true) {
//            SugiyamaData data = new SugiyamaData();
//            Graph currentGraph;
//            if (CYCLIC) {
//                data.setAlgorithmType(SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA);
//            } else {
//                data
//                        .setAlgorithmType(SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA);
//            }
//            currentGraph = benchmarkDummyDensity(CYCLIC, NODES, EDGE_DENSITY,
//                    MIN_DUMMY_DENSITY, MAX_DUMMY_DENSITY, data);
//
//            if (currentGraph == null) {
//                break;
//            }
//            // insertGraphIntoDB(nodes, edgeDensity, dummyDensity,
//            // nodesPerLayer, cyclic)
//            Graph gCopy = (Graph) currentGraph.copy();
//            SugiyamaData sCopy = copySugiyamaData(data, gCopy);
//
//            CrossMinObjectCollector cmoc = new CrossMinObjectCollector(gCopy,
//                    sCopy);
//            ArrayList<CrossMinObject> obj = cmoc.collectObjects();
//            int innerSegments = cmoc.getNumberOfInnerSegments();
//
//            graphID = insertGraphIntoDB(curNodes, EDGE_DENSITY,
//                    curDummyDensity, true, CYCLIC, TESTCASE, innerSegments, obj
//                            .size());
//
//            // run each graph 10 times for each algorithm
//            System.out.println(new Date().toGMTString() + " Testcase "
//                    + TESTCASE + " " + NODES + " " + EDGE_DENSITY + " "
//                    + curDummyDensity + " " + true + " " + CYCLIC);
//
//            for (int i = 0; i < algorithms.length; i++) {
//                for (int run = 0; run < 2; run++) {
//
//                    Graph graphCopy = (Graph) currentGraph.copy();
//                    SugiyamaData dataCopy = copySugiyamaData(data, graphCopy);
//
//                    int crossingsBefore = 0;
//                    for (int l = 0; l < data.getLayers().getNumberOfLayers(); l++) {
//                        crossingsBefore += new BilayerCrossCounter(graphCopy,
//                                l, dataCopy).getNumberOfCrossings();
//                    }
//
//                    algorithms[i].attach(graphCopy);
//                    algorithms[i].setData(dataCopy);
//                    algorithms[i].getParameters();
//
//                    setSweeps(algorithms, i, false, 10);
//
//                    Date start = new Date();
//                    try {
//                        algorithms[i].execute();
//                        Date end = new Date();
//                        long time = end.getTime() - start.getTime();
//                        algorithms[i].reset();
//
//                        int crossingsAfter = 0;
//                        for (int l = 0; l < data.getLayers()
//                                .getNumberOfLayers(); l++) {
//                            crossingsAfter += new BilayerCrossCounter(
//                                    graphCopy, l, dataCopy)
//                                    .getNumberOfCrossings();
//                        }
//                        saveResult(graphID, algorithms[i].getName(),
//                                crossingsBefore, crossingsAfter, time, 5,
//                                countType2Conflicts(dataCopy));
//                    } catch (Exception e) {
//                        System.out.println("Caught an exception, algorithm: "
//                                + algorithms[i].getName());
//
//                    }
//                }
//            }
//        }
//    }

    @SuppressWarnings("deprecation")
    private static void testEdgeDensity(boolean CYCLIC, int NODES,
            double MIN_EDGE_DENSITY, double MAX_EDGE_DENSITY,
            double DUMMY_DENSITY, boolean WIDE, double STEPPING,
            CrossMinAlgorithm[] algorithms, boolean barycenterInit) {
        curNodes = NODES;
        while (true) {
            SugiyamaData data = new SugiyamaData();
            Graph currentGraph;
            if (CYCLIC) {
                data.setAlgorithmType(SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA);
            } else {
                data
                        .setAlgorithmType(SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA);
            }
            currentGraph = benchmarkEdgeDensity(NODES, MIN_EDGE_DENSITY,
                    MAX_EDGE_DENSITY, DUMMY_DENSITY, WIDE, CYCLIC, STEPPING,
                    data);

            if (currentGraph == null) {
                break;
            }
            // insertGraphIntoDB(nodes, edgeDensity, dummyDensity,
            // nodesPerLayer, cyclic)
            Graph gCopy = (Graph) currentGraph.copy();
            SugiyamaData sCopy = copySugiyamaData(data, gCopy);

            CrossMinObjectCollector cmoc = new CrossMinObjectCollector(gCopy,
                    sCopy);
            ArrayList<CrossMinObject> obj = cmoc.collectObjects();
            int innerSegments = cmoc.getNumberOfInnerSegments();

            graphID = insertGraphIntoDB(curNodes, curDensity, DUMMY_DENSITY,
                    WIDE, CYCLIC, TESTCASE, innerSegments, obj.size());

            // run each graph 10 times for each algorithm
            System.out.println(new Date().toGMTString() + " Testcase "
                    + TESTCASE + " " + curNodes + " " + curDensity + " "
                    + DUMMY_DENSITY + " " + WIDE + " " + CYCLIC);

            for (int i = 0; i < algorithms.length; i++) {
                // {b_1, s_1, os_1, gs_1, b_5, s_5, os_5, gs_5, b_10, s_10,
                // os_10, gs_10};
                for (int run = 0; run < 2; run++) {

                    Graph graphCopy = (Graph) currentGraph.copy();
                    SugiyamaData dataCopy = copySugiyamaData(data, graphCopy);

                    // int crossingsBefore = 0;
                    // for (int l = 0; l < data.getLayers().getNumberOfLayers();
                    // l++) {
                    // crossingsBefore += new BilayerCrossCounter(graphCopy, l,
                    // dataCopy).getNumberOfCrossings();
                    // }
                    int crossingsBefore = countCrossings(dataCopy);

                    algorithms[i].attach(graphCopy);
                    algorithms[i].setData(dataCopy);
                    algorithms[i].getParameters();

                    setSweeps(algorithms, i, barycenterInit, 10);

                    Date start = new Date();
                    try {
                        // System.out.println("Executing " +
                        // algorithms[i].getName());
                        algorithms[i].execute();
                        Date end = new Date();
                        long time = end.getTime() - start.getTime();
                        algorithms[i].reset();

                        int crossingsAfter = countCrossings(dataCopy);
                        // int crossingsAfter = 0;
                        // for (int l = 0; l <
                        // data.getLayers().getNumberOfLayers(); l++) {
                        // crossingsAfter += new BilayerCrossCounter(graphCopy,
                        // l, dataCopy).getNumberOfCrossings();
                        // }

                        saveResult(graphID, algorithms[i].getName(),
                                crossingsBefore, crossingsAfter, time, 5,
                                countType2Conflicts(dataCopy));
                    } catch (Exception e) {
                        System.out.println("Caught an exception, algorithm: "
                                + algorithms[i].getName());
                        e.printStackTrace();
                        //GraphMLWriter w =
                        new GraphMLWriter();
                        try {
                            // System.out.println("Saving failed graph");
                            for (Edge edge : graphCopy.getEdges()) {
                                edge.addAttribute(new HashMapAttribute(
                                        "graphics"), "");
                                edge.addAttribute(new StringAttribute(
                                        "arrowhead"), "graphics");
                                ((StringAttribute) edge
                                        .getAttribute("graphics.arrowhead"))
                                        .setString("org.graffiti.plugins.views.defaults.StandardArrowShape");
                                // edge.addAttribute(new
                                // StringAttribute("org.graffiti.plugins.views.defaults.StandardArrowShape"),
                                // "graphics.arrowhead");
                            }
                            for (Node d : dataCopy.getDummyNodes()) {
                                d
                                        .setString(
                                                GraphicAttributeConstants.SHAPE_PATH,
                                                GraphicAttributeConstants.CIRCLE_CLASSNAME);
                                d
                                        .setDouble(
                                                GraphicAttributeConstants.DIMW_PATH,
                                                10);
                                d
                                        .setDouble(
                                                GraphicAttributeConstants.DIMH_PATH,
                                                10);
                                d
                                        .setInteger(
                                                GraphicAttributeConstants.FILLCOLOR_PATH
                                                        + Attribute.SEPARATOR
                                                        + GraphicAttributeConstants.RED,
                                                0);
                                d
                                        .setInteger(
                                                GraphicAttributeConstants.FILLCOLOR_PATH
                                                        + Attribute.SEPARATOR
                                                        + GraphicAttributeConstants.GREEN,
                                                0);
                                d
                                        .setInteger(
                                                GraphicAttributeConstants.FILLCOLOR_PATH
                                                        + Attribute.SEPARATOR
                                                        + GraphicAttributeConstants.BLUE,
                                                0);
                            }
                            // w.write(graphCopy,
                            // "/home/enkil/sugiyama_benchmark/failed_graph_" +
                            // new Date().getTime() + ".graphml");
                            // System.out.println("Exception stacktrace:");
                            // e.printStackTrace();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        } finally {
                            System.exit(1);
                        }
                    }
                }
            }
        }
    }

//    @SuppressWarnings("deprecation")
//    private static void testSweeps(boolean CYCLIC, int NODES,
//            double EDGE_DENSITY, double DUMMY_DENSITY, boolean WIDE,
//            int MIN_SWEEPS, int MAX_SWEEPS, CrossMinAlgorithm[] algorithms,
//            boolean barycenterInit) {
//        // curNodes = 0;
//        // while (true) {
//        SugiyamaData data = new SugiyamaData();
//        Graph currentGraph;
//        if (CYCLIC) {
//            data.setAlgorithmType(SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA);
//        } else {
//            data.setAlgorithmType(SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA);
//        }
//
//        // if (currentGraph == null) {
//        // break;
//        // }
//        // insertGraphIntoDB(nodes, edgeDensity, dummyDensity, nodesPerLayer,
//        // cyclic)
//
//        // run each graph 10 times for each algorithm
//        // System.out.println(new Date().toGMTString() + " Testcase " + TESTCASE
//        // + " " + curNodes + " " + EDGE_DENSITY
//        // + " " + DUMMY_DENSITY + " " + WIDE + " " + CYCLIC);
//
//        for (int graphs = 0; graphs < 10; graphs++) {
//            currentGraph = benchmarkSweeps(NODES, EDGE_DENSITY, DUMMY_DENSITY,
//                    WIDE, CYCLIC, data);
//            Graph gCopy = (Graph) currentGraph.copy();
//            SugiyamaData sCopy = copySugiyamaData(data, gCopy);
//
//            CrossMinObjectCollector cmoc = new CrossMinObjectCollector(gCopy,
//                    sCopy);
//            ArrayList<CrossMinObject> obj = cmoc.collectObjects();
//            int innerSegments = cmoc.getNumberOfInnerSegments();
//
//            graphID = insertGraphIntoDB(curNodes, EDGE_DENSITY, DUMMY_DENSITY,
//                    WIDE, CYCLIC, TESTCASE, innerSegments, obj.size());
//            for (int sweep = MIN_SWEEPS; sweep <= MAX_SWEEPS; sweep++) {
//                for (int i = 0; i < algorithms.length; i++) {
//                    // {b_1, s_1, os_1, gs_1, b_5, s_5, os_5, gs_5, b_10, s_10,
//                    // os_10, gs_10};
//                    for (int run = 0; run < 2; run++) {
//
//                        System.out
//                                .println(new Date().toGMTString()
//                                        + " Testcase " + TESTCASE + " " + NODES
//                                        + " " + EDGE_DENSITY + " "
//                                        + DUMMY_DENSITY + " " + WIDE + " "
//                                        + CYCLIC + " " + sweep + " sweeps");
//
//                        Graph graphCopy = (Graph) currentGraph.copy();
//                        SugiyamaData dataCopy = copySugiyamaData(data,
//                                graphCopy);
//
//                        int crossingsBefore = 0;
//                        for (int l = 0; l < data.getLayers()
//                                .getNumberOfLayers(); l++) {
//                            crossingsBefore += new BilayerCrossCounter(
//                                    graphCopy, l, dataCopy)
//                                    .getNumberOfCrossings();
//                        }
//
//                        algorithms[i].attach(graphCopy);
//                        algorithms[i].setData(dataCopy);
//                        algorithms[i].getParameters();
//
//                        setSweeps(algorithms, i, barycenterInit, sweep);
//
//                        Date start = new Date();
//                        try {
//                            // System.out.println("Executing " +
//                            // algorithms[i].getName());
//                            algorithms[i].execute();
//                            Date end = new Date();
//                            long time = end.getTime() - start.getTime();
//                            algorithms[i].reset();
//
//                            int crossingsAfter = 0;
//                            for (int l = 0; l < data.getLayers()
//                                    .getNumberOfLayers(); l++) {
//                                crossingsAfter += new BilayerCrossCounter(
//                                        graphCopy, l, dataCopy)
//                                        .getNumberOfCrossings();
//                            }
//                            saveResult(graphID, algorithms[i].getName(),
//                                    crossingsBefore, crossingsAfter, time,
//                                    sweep, countType2Conflicts(dataCopy));
//                        } catch (Exception e) {
//                            System.out
//                                    .println("Caught an exception, algorithm: "
//                                            + algorithms[i].getName());
//                        }
//                    }
//                }
//            }
//        }
//    }

    // }

    @SuppressWarnings("deprecation")
    private static void testCase(boolean CYCLIC, int MIN_NODES, int MAX_NODES,
            double EDGE_DENSITY, double DUMMY_DENSITY, boolean WIDE,
            int STEPPING, CrossMinAlgorithm[] algorithms, boolean barycenterInit) {
        curNodes = 0;
        while (true) {
            SugiyamaData data = new SugiyamaData();
            Graph currentGraph;
            if (CYCLIC) {
                data.setAlgorithmType(SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA);
            } else {
                data
                        .setAlgorithmType(SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA);
            }
            currentGraph = benchmark(MIN_NODES, MAX_NODES, EDGE_DENSITY,
                    DUMMY_DENSITY, WIDE, CYCLIC, STEPPING, data);

            if (currentGraph == null) {
                break;
            }
            // insertGraphIntoDB(nodes, edgeDensity, dummyDensity,
            // nodesPerLayer, cyclic)
            Graph gCopy = (Graph) currentGraph.copy();
            SugiyamaData sCopy = copySugiyamaData(data, gCopy);

            CrossMinObjectCollector cmoc = new CrossMinObjectCollector(gCopy,
                    sCopy);
            ArrayList<CrossMinObject> obj = cmoc.collectObjects();
            int innerSegments = cmoc.getNumberOfInnerSegments();

            graphID = insertGraphIntoDB(curNodes, EDGE_DENSITY, DUMMY_DENSITY,
                    WIDE, CYCLIC, TESTCASE, innerSegments, obj.size());

            // run each graph 10 times for each algorithm
            System.out.println(new Date().toGMTString() + " Testcase "
                    + TESTCASE + " " + curNodes + " " + EDGE_DENSITY + " "
                    + DUMMY_DENSITY + " " + WIDE + " " + CYCLIC);

            for (int i = 0; i < algorithms.length; i++) {
                // {b_1, s_1, os_1, gs_1, b_5, s_5, os_5, gs_5, b_10, s_10,
                // os_10, gs_10};
                for (int run = 0; run < 2; run++) {

                    Graph graphCopy = (Graph) currentGraph.copy();
                    SugiyamaData dataCopy = copySugiyamaData(data, graphCopy);

                    int crossingsBefore = 0;
                    for (int l = 0; l < data.getLayers().getNumberOfLayers(); l++) {
                        crossingsBefore += new BilayerCrossCounter(graphCopy,
                                l, dataCopy).getNumberOfCrossings();
                    }

                    algorithms[i].attach(graphCopy);
                    algorithms[i].setData(dataCopy);
                    algorithms[i].getParameters();

                    setSweeps(algorithms, i, barycenterInit, 10);

                    Date start = new Date();
                    try {
                        // System.out.println("Executing " +
                        // algorithms[i].getName());
                        algorithms[i].execute();
                        Date end = new Date();
                        long time = end.getTime() - start.getTime();
                        algorithms[i].reset();

                        int crossingsAfter = 0;
                        for (int l = 0; l < data.getLayers()
                                .getNumberOfLayers(); l++) {
                            crossingsAfter += new BilayerCrossCounter(
                                    graphCopy, l, dataCopy)
                                    .getNumberOfCrossings();
                        }
                        saveResult(graphID, algorithms[i].getName(),
                                crossingsBefore, crossingsAfter, time, 5,
                                countType2Conflicts(dataCopy));
                    } catch (Exception e) {
                        System.out.println("Caught an exception, algorithm: "
                                + algorithms[i].getName());
                        // e.printStackTrace();
                        // GraphMLWriter w = new GraphMLWriter();
                        // try {
                        // //System.out.println("Saving failed graph");
                        // for (Edge edge : graphCopy.getEdges()) {
                        // edge.addAttribute(new HashMapAttribute("graphics"),
                        // "");
                        // edge.addAttribute(new StringAttribute("arrowhead"),
                        // "graphics");
                        // ((StringAttribute)edge.getAttribute("graphics.arrowhead")).setString("org.graffiti.plugins.views.defaults.StandardArrowShape");
                        // //edge.addAttribute(new
                        // StringAttribute("org.graffiti.plugins.views.defaults.StandardArrowShape"),
                        // "graphics.arrowhead");
                        // }
                        // for (Node d : dataCopy.getDummyNodes()) {
                        // d.setString(GraphicAttributeConstants.SHAPE_PATH,
                        // GraphicAttributeConstants.CIRCLE_CLASSNAME);
                        // d.setDouble(GraphicAttributeConstants.DIMW_PATH, 10);
                        // d.setDouble(GraphicAttributeConstants.DIMH_PATH, 10);
                        // d.setInteger(GraphicAttributeConstants.FILLCOLOR_PATH
                        // +Attribute.SEPARATOR+GraphicAttributeConstants.RED,
                        // 0);
                        // d.setInteger(GraphicAttributeConstants.FILLCOLOR_PATH
                        // +Attribute.SEPARATOR+GraphicAttributeConstants.GREEN,
                        // 0);
                        // d.setInteger(GraphicAttributeConstants.FILLCOLOR_PATH
                        // +Attribute.SEPARATOR+GraphicAttributeConstants.BLUE,
                        // 0);
                        // }
                        // w.write(graphCopy,
                        // "/home/enkil/sugiyama_benchmark/failed_graph_" + new
                        // Date().getTime() + ".graphml");
                        // System.out.println("Exception stacktrace:");
                        // e.printStackTrace();
                        // } catch (Exception ex) {
                        // ex.printStackTrace();
                        // } finally {
                        // System.exit(1);
                    }
                }
            }
        }
    }

    // }

    private static void dryRun(CrossMinAlgorithm[] algorithms) {
        for (int y = 0; y < 10; y++) {
            CrossMinGraphGenerator generator = new CrossMinGraphGenerator(100,
                    2.0, 0.3, true, false);
            SugiyamaData d = new SugiyamaData();
            Graph g = generator.getRandomGraph(d);
            for (int i = 0; i < algorithms.length; i++) {
                Graph graphCopy = (Graph) g.copy();
                SugiyamaData dataCopy = copySugiyamaData(d, graphCopy);
                algorithms[i].attach(graphCopy);
                algorithms[i].setData(dataCopy);
                algorithms[i].getParameters();
                algorithms[i].execute();
                algorithms[i].reset();
            }
        }
    }

    private static void setSweeps(CrossMinAlgorithm[] algorithms, int algo,
            boolean barycenterInit, int sweeps) {

        Median m_1 = (Median) algorithms[7];
        BaryCenter b_1 = (BaryCenter) algorithms[0];
        Sifting s_1 = (Sifting) algorithms[1];
        Sifting ks_1 = (Sifting) algorithms[2];
        Sifting os_1 = (Sifting) algorithms[3];
        GlobalBarycenter gb_1 = (GlobalBarycenter) algorithms[4];
        GlobalMedian gm_1 = (GlobalMedian) algorithms[5];
        GlobalSifting gs_1 = (GlobalSifting) algorithms[6];

        Parameter<?>[] params;
        StringSelectionParameter sel;
        IntegerParameter p;

        // barycenter
        if (algo == 0) {
            params = b_1.getAlgorithmParameters();
            p = (IntegerParameter) params[0];
            p.setValue(sweeps);
            b_1.setParameters(params);
        }
        // sifting
        if (algo == 1) {
            params = s_1.getAlgorithmParameters();
            sel = (StringSelectionParameter) params[0];
            if (barycenterInit) {
                sel.setValue("Barycenter");
            } else {
                sel.setValue("Random");
            }
            p = (IntegerParameter) params[1];
            p.setValue(1); // rounds
            p = (IntegerParameter) params[2];
            p.setValue(sweeps);
            StringSelectionParameter np = (StringSelectionParameter) params[3];
            np.setValue("classic");
            np = (StringSelectionParameter) params[4];
            np.setValue("classic");
            s_1.setAlgorithmParameters(params);
        }
        // k-level sifting
        if (algo == 2) {
            params = ks_1.getAlgorithmParameters();
            sel = (StringSelectionParameter) params[0];
            if (barycenterInit) {
                sel.setValue("Barycenter");
            } else {
                sel.setValue("Random");
            }
            p = (IntegerParameter) params[1];
            p.setValue(1); // rounds
            p = (IntegerParameter) params[2];
            p.setValue(sweeps);
            StringSelectionParameter np = (StringSelectionParameter) params[3];
            np.setValue("k-layer");
            np = (StringSelectionParameter) params[4];
            np.setValue("classic");
            ks_1.setAlgorithmParameters(params);
        }
        // optimized sifting
        if (algo == 3) {
            params = os_1.getAlgorithmParameters();
            sel = (StringSelectionParameter) params[0];
            if (barycenterInit) {
                sel.setValue("Barycenter");
            } else {
                sel.setValue("Random");
            }
            p = (IntegerParameter) params[1];
            p.setValue(1); // rounds
            p = (IntegerParameter) params[2];
            p.setValue(sweeps);
            StringSelectionParameter np = (StringSelectionParameter) params[3];
            np.setValue("classic");
            np = (StringSelectionParameter) params[4];
            np.setValue("optimized");
            os_1.setAlgorithmParameters(params);
        }
        // global barycenter, 1 sweep
        if (algo == 4) {
            params = gb_1.getAlgorithmParameters();
            p = (IntegerParameter) params[0];
            p.setValue(sweeps);
            gb_1.setAlgorithmParameters(params);
        }
        // global median
        if (algo == 4) {
            params = gm_1.getAlgorithmParameters();
            p = (IntegerParameter) params[0];
            p.setValue(sweeps);
            gm_1.setAlgorithmParameters(params);
        }
        // global sifting, 1 sweep
        if (algo == 6) {
            params = gs_1.getAlgorithmParameters();
            p = (IntegerParameter) params[1];
            p.setValue(sweeps); // rounds
            gs_1.setAlgorithmParameters(params);
        }
        // median
        if (algo == 7) {
            params = m_1.getAlgorithmParameters();
            p = (IntegerParameter) params[0];
            p.setValue(sweeps);
            m_1.setParameters(params);
        }
    }

    private static void saveResult(int graphId, String algorithm,
            int crossingsBefore, int crossingsAfter, long time, int algoSweeps,
            int typeTwoConflicts) {
        try {
            PreparedStatement pstmt = con
                    .prepareStatement("INSERT INTO result VALUES(default, ?, ?, ?, ?, ?, ?, ?);");
            pstmt.setInt(1, graphId);
            pstmt.setString(2, algorithm);
            pstmt.setInt(3, algoSweeps);
            pstmt.setLong(4, crossingsBefore);
            pstmt.setLong(5, crossingsAfter);
            pstmt.setLong(6, time);
            pstmt.setInt(7, typeTwoConflicts);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException sqle) {
            System.out.println("SQL Error: " + sqle.getMessage());
        }
    }

    private static int insertGraphIntoDB(int nodes, double edgeDensity,
            double dummyDensity, boolean wide, boolean cyclic, int testCase,
            int innerSegments, int objects) {

        double RATIO = (1 + Math.sqrt(5)) / (3 + Math.sqrt(5));
        double k = Math.sqrt(nodes * RATIO);
        int maxNodesPerLayer = (int) (Math.floor((nodes / k) + 0.5d));

        try {
            int ret;
            PreparedStatement pstmt = con
                    .prepareStatement("INSERT INTO graph VALUES(default, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            pstmt.setInt(1, nodes);
            pstmt.setDouble(2, edgeDensity);
            pstmt.setDouble(3, dummyDensity);
            pstmt.setBoolean(4, wide);
            pstmt.setInt(5, maxNodesPerLayer);
            pstmt.setBoolean(6, cyclic);
            pstmt.setInt(7, testCase);
            pstmt.setInt(8, innerSegments);
            pstmt.setInt(9, objects);
            pstmt.executeUpdate();

            pstmt = con.prepareStatement("SELECT LAST_INSERT_ID() FROM graph;");
            ResultSet rst = pstmt.executeQuery();
            rst.first();
            ret = rst.getInt(1);
            rst.close();
            pstmt.close();
            return ret;
        } catch (SQLException sqle) {
            System.out.println("SQL Error: " + sqle.getMessage());
            return -1;
        }
    }

//    private static Graph benchmarkDummyDensity(boolean cyclic, int nodes,
//            double edgeDensity, double minDummyDensity, double maxDummyDensity,
//            SugiyamaData data) {
//
//        if (firstRun == true) {
//            curDummyDensity = minDummyDensity;
//            firstRun = false;
//            curLoop = 0;
//            CrossMinGraphGenerator generator = new CrossMinGraphGenerator(
//                    nodes, edgeDensity, curDummyDensity, true, cyclic);
//            curLoop++;
//            return generator.getRandomGraph(data);
//        } else {
//
//            if (curDummyDensity < minDummyDensity) {
//                curDummyDensity = minDummyDensity;
//            }
//
//            if (curDummyDensity > maxDummyDensity)
//                return null;
//            else {
//                if (curLoop == 0) {
//                    curDummyDensity = curDummyDensity + 0.025;
//                }
//                if (curLoop < 10) {
//                    CrossMinGraphGenerator generator = new CrossMinGraphGenerator(
//                            nodes, edgeDensity, curDummyDensity, true, cyclic);
//                    curLoop++;
//                    return generator.getRandomGraph(data);
//                } else {
//                    curLoop = 0;
//                    curDummyDensity = curDummyDensity + 0.025;
//                    CrossMinGraphGenerator generator = new CrossMinGraphGenerator(
//                            nodes, edgeDensity, curDummyDensity, true, cyclic);
//                    curLoop++;
//                    return generator.getRandomGraph(data);
//                }
//            }
//        }
//    }

    private static Graph benchmarkEdgeDensity(int nodes, double minEdgeDensity,
            double maxEdgeDensity, double dummyDensity, boolean wide,
            boolean cyclic, double stepping, SugiyamaData data) {
        // public CrossMinGraphGenerator(int numberOfNodes, double edgeDensity,
        // double dummyNodeDensity,
        // int maxNodesPerLayer, boolean cyclicLayout)
        if (curDensity > maxEdgeDensity)
            return null;
        else {
            if (curLoop == 0) {
                curDensity += stepping;
            }
            if (curLoop < 10) {
                CrossMinGraphGenerator generator = new CrossMinGraphGenerator(
                        curNodes, curDensity, dummyDensity, wide, cyclic);
                curLoop++;
                return generator.getRandomGraph(data);
            } else {
                curLoop = 0;
                curDensity += stepping;
                CrossMinGraphGenerator generator = new CrossMinGraphGenerator(
                        curNodes, curDensity, dummyDensity, wide, cyclic);
                curLoop++;
                return generator.getRandomGraph(data);
            }
        }

    }

//    private static Graph benchmarkSweeps(int nodes, double edgeDensity,
//            double dummyDensity, boolean wide, boolean cyclic, SugiyamaData data) {
//        CrossMinGraphGenerator generator = new CrossMinGraphGenerator(nodes,
//                edgeDensity, dummyDensity, wide, cyclic);
//        return generator.getRandomGraph(data);
//
//    }

    private static Graph benchmark(int minNodes, int maxNodes,
            double edgeDensity, double dummyDensity, boolean wide,
            boolean cyclic, int nodeStepping, SugiyamaData data) {
        // public CrossMinGraphGenerator(int numberOfNodes, double edgeDensity,
        // double dummyNodeDensity,
        // int maxNodesPerLayer, boolean cyclicLayout)
        if (curNodes < minNodes) {
            curNodes = minNodes;
        }
        if (curNodes > maxNodes)
            return null;
        else {
            if (curLoop == 0) {
                curNodes = curNodes + nodeStepping;
            }
            if (curLoop < 10) {
                CrossMinGraphGenerator generator = new CrossMinGraphGenerator(
                        curNodes, edgeDensity, dummyDensity, wide, cyclic);
                curLoop++;
                return generator.getRandomGraph(data);
            } else {
                curLoop = 0;
                curNodes = curNodes + nodeStepping;
                CrossMinGraphGenerator generator = new CrossMinGraphGenerator(
                        curNodes, edgeDensity, dummyDensity, wide, cyclic);
                curLoop++;
                return generator.getRandomGraph(data);
            }
        }

    }

    private static SugiyamaData copySugiyamaData(SugiyamaData data,
            Graph copiedGraph) {
        SugiyamaData copy = new SugiyamaData();
        copy.setAlgorithmType(data.getAlgorithmType());
        copy.setGraph(copiedGraph);
        copy.setDummyNodes(new HashSet<Node>());
        int maxLevel = 0;
        int curLevel;

        for (Node n : copiedGraph.getNodes()) {
            curLevel = n.getInteger(SugiyamaConstants.PATH_LEVEL);
            if (curLevel > maxLevel) {
                maxLevel = curLevel;
            }
            try {
                if (n.getBoolean(SugiyamaConstants.PATH_DUMMY)) {
                    copy.getDummyNodes().add(n);
                }
            } catch (AttributeNotFoundException anfe) {
                // that's okay
            }
        }
        for (int i = 0; i < maxLevel; i++) {
            copy.getLayers().addLayer();
        }

        for (Node n : copiedGraph.getNodes()) {
            curLevel = n.getInteger(SugiyamaConstants.PATH_LEVEL);
            copy.getLayers().getLayer(curLevel).add(n);
        }

        return copy;
    }

    private static int countType2Conflicts(SugiyamaData data) {
        int type2 = 0;
        HashSet<Node> dummies = data.getDummyNodes();
        for (int r = 0; r < data.getLayers().getNumberOfLayers(); r++) {
            ArrayList<Node> layer = data.getLayers().getLayer(r);
            ArrayList<Node> nextLayer = null;
            if (r < data.getLayers().getNumberOfLayers() - 1) {
                nextLayer = data.getLayers().getLayer(r + 1);
            } else {
                nextLayer = data.getLayers().getLayer(0);
            }
            for (int i = 0; i < layer.size(); i++) {
                Node iNode = layer.get(i);
                if (!dummies.contains(iNode)) {
                    continue;
                }
                for (int k = i + 1; k < layer.size(); k++) {
                    Node kNode = layer.get(k);
                    if (!dummies.contains(kNode)) {
                        continue;
                    }
                    for (Node jNode : iNode.getOutNeighbors()) {
                        if (!dummies.contains(jNode)) {
                            continue;
                        }
                        int j = nextLayer.indexOf(jNode);
                        if (j == -1)
                            throw new IllegalStateException();
                        for (Node lNode : kNode.getOutNeighbors()) {
                            if (jNode == lNode) {
                                continue;
                            }
                            if (!dummies.contains(lNode)) {
                                continue;
                            }
                            int l = nextLayer.indexOf(lNode);
                            if (l == -1)
                                throw new IllegalStateException();
                            if (j > l) {
                                type2++;
                            }
                        }
                    }
                }
            }
        }
        return type2;
    }

    private static int countCrossings(SugiyamaData data) {
        int crossings = 0;
        for (int r = 0; r < data.getLayers().getNumberOfLayers(); r++) {
            ArrayList<Node> layer = data.getLayers().getLayer(r);
            ArrayList<Node> nextLayer = null;
            if (r < data.getLayers().getNumberOfLayers() - 1) {
                nextLayer = data.getLayers().getLayer(r + 1);
            } else {
                nextLayer = data.getLayers().getLayer(0);
            }
            for (int i = 0; i < layer.size(); i++) {
                Node iNode = layer.get(i);
                for (int k = i + 1; k < layer.size(); k++) {
                    Node kNode = layer.get(k);
                    for (Node jNode : iNode.getOutNeighbors()) {
                        int j = nextLayer.indexOf(jNode);
                        if (j == -1)
                            throw new IllegalStateException();
                        for (Node lNode : kNode.getOutNeighbors()) {
                            if (jNode == lNode) {
                                continue;
                            }
                            int l = nextLayer.indexOf(lNode);
                            if (l == -1)
                                throw new IllegalStateException();
                            if (j > l) {
                                crossings++;
                            }
                        }
                    }
                }
            }
        }
        return crossings;

    }

    private static void setLoggingLevel() {
        Logger.getLogger("org.graffiti.attributes.AbstractAttribute").setLevel(
                Level.WARNING);
        Logger.getLogger("org.graffiti.attributes.AbstractCollectionAttribute")
                .setLevel(Level.WARNING);
        Logger.getLogger("org.graffiti.graph.AbstractGraph").setLevel(
                Level.WARNING);
        Logger.getLogger("org.graffiti.graph.AdjListNode").setLevel(
                Level.WARNING);
        Logger.getLogger("org.graffiti.graph.AdjListGraph").setLevel(
                Level.WARNING);
        Logger
                .getLogger(
                        "org.graffiti.plugins.algorithms.sugiyama.crossmin.global.GlobalSifting")
                .setLevel(Level.WARNING);
        Logger.getLogger(
                "org.graffiti.plugins.algorithms.sugiyama.crossmin.Sifting")
                .setLevel(Level.WARNING);
        Logger
                .getLogger(
                        "org.graffiti.plugins.algorithms.sugiyama.crossmin.SiftingWrapper")
                .setLevel(Level.WARNING);
        Logger
                .getLogger(
                        "org.graffiti.plugins.algorithms.sugiyama.crossmin.global.FastGlobalSifting")
                .setLevel(Level.WARNING);
        Logger
                .getLogger(
                        "org.graffiti.plugins.algorithms.sugiyama.crossmin.global.OriginalGlobalSifting")
                .setLevel(Level.WARNING);
        Logger
                .getLogger(
                        "org.graffiti.plugins.algorithms.sugiyama.layout.cyclic.Toolkit")
                .setLevel(Level.WARNING);
    }
}
