/**
 * 
 */
package org.graffiti.plugins.algorithms.graviso.junit;

import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AlgorithmResult;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.ObjectParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringParameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.graviso.AbstractRefinementAlgorithm;
import org.graffiti.plugins.algorithms.graviso.RefinementAlgorithmSimple;
import org.graffiti.plugins.algorithms.isomorphism.AbstractIsomorphism;
import org.graffiti.plugins.algorithms.isomorphism.VF2;
import org.junit.Before;
import org.junit.Test;

/**
 * @author lenhardt
 * 
 */
public class DatabaseGraphTests {
    private static final String BASE_PATH = "/Users/lenhardt/Documents/workspace/GravISO/graphs/iso/";
    private static final String LOG_PATH = "/Users/lenhardt/Documents/workspace/";

    private static String logPath;
    private List<File> graphs;

    @SuppressWarnings("unchecked")
    private Parameter[] parameters;

    private static final int P_VISUALIZE = 0 + 2;
    private static final int P_REGARD_DIRECTIONS = 1 + 2;
    private static final int P_USE_BFS_INFO = 2 + 2;
    private static final int P_REGARD_NODE_LABELS = 3 + 2;
    private static final int P_NODE_LABEL_PATH = 4 + 2;
    private static final int P_REGARD_EDGE_LABELS = 5 + 2;
    private static final int P_EDGE_LABEL_PATH = 6 + 2;
    private static final String SUCCESS_MSG = "Graphs are ISO";
    protected static final Logger logger = Logger
            .getLogger(AbstractRefinementAlgorithm.class.getName());

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        Logger.getLogger("org.graffiti.plugins.ios.gml.gmlReader").setLevel(
                Level.OFF);
        Logger.getLogger("org.graffiti.attributes.AbstractCollectionAttribute")
                .setLevel(Level.OFF);
        Logger.getLogger("org.graffiti.attributes.AbstractAttribute").setLevel(
                Level.OFF);
        Logger.getLogger("org.graffiti.graph.AdjListNode").setLevel(
                Level.WARNING);
        Logger.getLogger("org.graffiti.graph.AdjListGraph").setLevel(
                Level.WARNING);
        Logger.getLogger("org.graffiti.graph.AbstractGraph").setLevel(
                Level.WARNING);
        Logger.getLogger("org.graffiti.graph.AbstractEdge").setLevel(Level.OFF);

        Logger.getLogger(
                "org.graffiti.plugins.algorithms.graviso.RefinementAlgorithm")
                .setLevel(Level.WARNING);

        BooleanParameter regardDirections = new BooleanParameter(false,
                "regard Direction",
                "check if you want to take edges' direction into account");
        BooleanParameter regardEdgeLabels = new BooleanParameter(false,
                "regard edge-labels",
                "check if you want to take edges' labels into account");
        BooleanParameter regardNodeLabels = new BooleanParameter(false,
                "regard node-labels",
                "check if you want to take nodes' labels into account");
        BooleanParameter useBFSInfo = new BooleanParameter(true,
                "use BFS to speed up computation",
                "check if you want to use BFS to calculate resutl");
        StringParameter nodeLabelPath = new StringParameter("", "node label",
                "which node label should be used for computation");
        StringParameter edgeLabelPath = new StringParameter("", "edge label",
                "which edge label should be used for computation");

        String[] visOpts = { "no animation", "final animation", "step-by-step" };
        StringSelectionParameter visualize = new StringSelectionParameter(
                visOpts, "level of visual feedback",
                "select if you want to see no colors, colors at the end, or step-by-step");
        visualize.setSelectedValue(0);

        parameters = new Parameter[9];

        parameters[P_VISUALIZE] = visualize;
        parameters[P_REGARD_DIRECTIONS] = regardDirections;
        parameters[P_USE_BFS_INFO] = useBFSInfo;
        parameters[P_REGARD_NODE_LABELS] = regardNodeLabels;
        parameters[P_NODE_LABEL_PATH] = nodeLabelPath;
        parameters[P_EDGE_LABEL_PATH] = edgeLabelPath;
        parameters[P_REGARD_EDGE_LABELS] = regardEdgeLabels;

        String path = System.getProperty("graphpath", BASE_PATH);
        graphs = GraphGenerator.getGraphFiles(path);

        logPath = System.getProperty("logpath", LOG_PATH);

        System.out.println("------- Starting Tests, " + graphs.size()
                + " Graphs to test ------- ");
    }

    private long runBFS(Graph g1, Graph g2) {
        ObjectParameter o1 = new ObjectParameter(g1, null, null);
        ObjectParameter o2 = new ObjectParameter(g2, null, null);

        parameters[0] = o1;
        parameters[1] = o2;
        ((BooleanParameter) parameters[P_USE_BFS_INFO]).setValue(true);

        RefinementAlgorithmSimple r = new RefinementAlgorithmSimple();
        r.setAlgorithmParameters(parameters);
        long startTime, endTime;
        startTime = System.nanoTime();
        r.execute();
        endTime = System.nanoTime();
        AlgorithmResult res = r.getResult();
        java.util.Map<String, Object> m = res.getResult();
        r.reset();
        // System.out.println(" " + (endTime - startTime));
        Object mo = m.get("Result");
        String resultString = mo.toString();
        System.gc();
        if (resultString.equals(SUCCESS_MSG))
            return endTime - startTime;
        else
            return -1;
    }

    private long runNonIsoBFS(Graph g1, Graph g2) {
        ObjectParameter o1 = new ObjectParameter(g1, null, null);
        ObjectParameter o2 = new ObjectParameter(g2, null, null);

        parameters[0] = o1;
        parameters[1] = o2;
        ((BooleanParameter) parameters[P_USE_BFS_INFO]).setValue(true);

        RefinementAlgorithmSimple r = new RefinementAlgorithmSimple();
        r.setAlgorithmParameters(parameters);
        long startTime, endTime;
        startTime = System.nanoTime();
        r.execute();
        endTime = System.nanoTime();
        AlgorithmResult res = r.getResult();
        java.util.Map<String, Object> m = res.getResult();
        r.reset();
        // System.out.println(" " + (endTime - startTime));
        Object mo = m.get("Result");
        String resultString = mo.toString();
        System.gc();
        if (!resultString.equals(SUCCESS_MSG))
            return endTime - startTime;
        else
            return -1;
    }

    private long runSimple(Graph g1, Graph g2) {
        ObjectParameter o1 = new ObjectParameter(g1, null, null);
        ObjectParameter o2 = new ObjectParameter(g2, null, null);

        parameters[0] = o1;
        parameters[1] = o2;

        ((BooleanParameter) parameters[P_USE_BFS_INFO]).setValue(false);

        RefinementAlgorithmSimple r = new RefinementAlgorithmSimple();
        r.setAlgorithmParameters(parameters);
        long startTime, endTime;
        startTime = System.nanoTime();
        r.execute();
        endTime = System.nanoTime();
        AlgorithmResult res = r.getResult();
        java.util.Map<String, Object> m = res.getResult();
        r.reset();
        // System.out.println(" " + (endTime - startTime));
        Object mo = m.get("Result");
        String resultString = mo.toString();
        System.gc();
        if (resultString.equals(SUCCESS_MSG))
            return endTime - startTime;
        else
            return -1;
    }

    private long runNonIsoSimple(Graph g1, Graph g2) {
        ObjectParameter o1 = new ObjectParameter(g1, null, null);
        ObjectParameter o2 = new ObjectParameter(g2, null, null);

        parameters[0] = o1;
        parameters[1] = o2;

        ((BooleanParameter) parameters[P_USE_BFS_INFO]).setValue(false);

        RefinementAlgorithmSimple r = new RefinementAlgorithmSimple();
        r.setAlgorithmParameters(parameters);
        long startTime, endTime;
        startTime = System.nanoTime();
        r.execute();
        endTime = System.nanoTime();
        AlgorithmResult res = r.getResult();
        java.util.Map<String, Object> m = res.getResult();
        r.reset();
        // System.out.println(" " + (endTime - startTime));
        Object mo = m.get("Result");
        String resultString = mo.toString();
        System.gc();
        if (!resultString.equals(SUCCESS_MSG))
            return endTime - startTime;
        else
            return -1;
    }

    private void test(String type) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        java.util.Date date = new java.util.Date();
        System.out.println("\n\n---- Start " + type + " Test: "
                + dateFormat.format(date));
        logger.warning("---- Start " + type + " Test: "
                + dateFormat.format(date) + "\n\n");
        boolean result = true;
        int i = 0;
        long sum = 0;
        int j = 0;
        List<File> ggraphs = graphs;
        long[] dev = new long[graphs.size() + 1];
        Writer output = null;
        for (File g1File : ggraphs) {
            i++; // counter all graphs
            j++; // counter graphs of 1 type, will be reset if new graph with
            // .b00 suffix is opened

            String g2FileName = g1File.getAbsolutePath();
            char[] g2FileAr = g2FileName.toCharArray();
            g2FileAr[g2FileName.length() - 3] = 'B';
            g2FileName = new String(g2FileAr);
            try {
                if (g2FileName.endsWith("B00")) {
                    if (output != null) {
                        long avg = (sum / j);
                        long dif = 0;
                        for (int idx = i - j + 1; idx < i; idx++) {
                            dif += Math.abs((dev[idx] - avg));
                        }
                        output.write("-------------------\n");
                        output.write("Summe: " + Long.toString(sum)
                                + " Average: " + Long.toString(avg)
                                + " StdDeviation: " + Long.toString(dif / j));
                        output.write("\n-------------------\n");
                        output.close();
                        sum = 0;
                        j = 0;
                    }
                    File logFile = new File(logPath + type + "."
                            + g1File.getName() + ".log");
                    output = new BufferedWriter(new FileWriter(logFile));

                }

                File g2File = new File(g2FileName);
                Graph g1 = GraphGenerator.getLibGraph(g1File);
                Graph g2 = GraphGenerator.getLibGraph(g2File);

                logger.warning(type + "." + g1File.getAbsolutePath());
                output.write(i + " / " + graphs.size() + ": " + g2FileName
                        + ": ");

                long res = -1;
                if (type.equals("BFS")) {
                    res = runBFS(g1, g2);
                } else if (type.equals("Simple")) {
                    res = runSimple(g1, g2);
                } else if (type.equals("VF2")) {
                    res = runVF2(g1, g2);
                }

                if (res < 0) {
                    result = false;
                    output.write("  !!! NOT PASSED !!! ");
                } else {
                    sum += res;
                    output.write(Long.toString(res) + "\t "
                            + g1.getNumberOfNodes());
                    dev[i] = res;
                }
                output.write("\n");
                output.flush();
            } catch (Throwable e) {
                try {
                    i--;
                    j--;
                    output.write("  !!! EXCEPTION !!! ");
                    output.write(e.toString() + "\n");
                    output.flush();
                } catch (Exception e2) {
                    // error occurred writing
                    System.out.println("Damn.");
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
        }
        try {
            if (output != null) {
                long avg = (sum / j);
                long dif = 0;
                for (int idx = i - j + 1; idx < i; idx++) {
                    dif += Math.abs((dev[idx] - avg));
                }
                output.write("-------------------\n");
                output.write("Summe: " + Long.toString(sum) + " Average: "
                        + Long.toString(avg) + " StdDeviation: "
                        + Long.toString(dif / j));
                output.write("\n-------------------\n");
                output.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        date = new java.util.Date();
        System.out.println("---- End BFS Test: " + dateFormat.format(date));
        assertTrue("BFS Tests", result);
    }

    @Test
    public void testBFS() {
        test("BFS");
    }

    @Test
    public void testSimple() {
        test("Simple");
    }

    public void testNonIsoSimple() {
        testNonIsomorphic("Simple");
    }

    public void testNonIsoBFS() {
        testNonIsomorphic("BFS");
    }

    public void testVF2() {
        test("VF2");
    }

    private long runVF2(Graph g1, Graph g2) {

        BooleanParameter direction = new BooleanParameter(false, "direction",
                "Ignore direction of edges.");
        parameters = new Parameter[3];
        parameters[2] = direction;

        ObjectParameter o1 = new ObjectParameter(g1, null, null);
        ObjectParameter o2 = new ObjectParameter(g2, null, null);

        parameters[0] = o1;
        parameters[1] = o2;

        AbstractIsomorphism r = new VF2();
        r.setAlgorithmParameters(parameters);
        long startTime, endTime;
        startTime = System.nanoTime();
        r.execute();
        endTime = System.nanoTime();
        AlgorithmResult res = r.getResult();
        java.util.Map<String, Object> m = res.getResult();
        r.reset();
        // System.out.println(" " + (endTime - startTime));
        Object mo = m.get("Result");
        String resultString = mo.toString();
        if (resultString.equals(SUCCESS_MSG))
            return endTime - startTime;
        else
            return -1;
    }

    public void testDegrees() {
        List<File> ggraphs = graphs;
        System.out.println();
        int prevIn = 0;
        int prevOut = 0;
        for (File g1File : ggraphs) {
            Graph g1 = GraphGenerator.getLibGraph(g1File);
            Node prev = g1.getNodes().get(0);
            if (prev.getInDegree() != prevIn || prevOut != prev.getOutDegree()) {
                System.out.println(g1File.getName() + "  " + prev.getInDegree()
                        + "  " + prev.getOutDegree());
            }
            prevIn = prev.getInDegree();
            prevOut = prev.getOutDegree();
            for (Node n : g1.getNodes()) {
                if (prev.getInDegree() != n.getInDegree()
                        || prev.getOutDegree() != n.getOutDegree()) {
                    System.out.println(g1File.getName() + "  "
                            + n.getInDegree() + "\t" + n.getOutDegree());
                }
                prev = n;
            }
        }
    }

    private void testNonIsomorphic(String type) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        java.util.Date date = new java.util.Date();
        System.out.println("\n\n---- Start " + type + " Test: "
                + dateFormat.format(date));
        logger.warning("---- Start " + type + " Test: "
                + dateFormat.format(date) + "\n\n");
        boolean result = true;
        int i = 0;
        long sum = 0;
        int j = 0;
        List<File> ggraphs = graphs;
        long[] dev = new long[graphs.size() + 1];
        Writer output = null;
        for (File g1File : ggraphs) {
            if (g1File.getName().contains("1000")) {
                continue;
            }
            if (g1File.getName().contains("800")) {
                continue;
            }
            if (g1File.getName().contains("700")) {
                continue;
            }
            if (g1File.getName().contains("900")) {
                continue;
            }
            if (g1File.getName().contains("600")) {
                continue;
            }
            if (g1File.getName().contains("500")) {
                continue;
            }
            if (g1File.getName().contains("400")) {
                continue;
            }
            if (g1File.getName().contains("300")) {
                continue;
            }
            if (g1File.getName().contains("200")) {
                continue;
            }

            if (!(g1File.getName().endsWith("A00")
                    || g1File.getName().endsWith("A04")
                    || g1File.getName().endsWith("A07") || g1File.getName()
                    .endsWith("A20"))) {
                continue;
            }

            i++; // counter all graphs
            j++; // counter graphs of 1 type, will be reset if new graph with
            // .b00 suffix is opened

            try {
                if (g1File.getName().endsWith("A00")) {
                    if (output != null) {
                        long avg = (sum / j);
                        long dif = 0;
                        for (int idx = i - j + 1; idx < i; idx++) {
                            dif += Math.abs((dev[idx] - avg));
                        }
                        output.write("-------------------\n");
                        output.write("Summe: " + Long.toString(sum)
                                + " Average: " + Long.toString(avg)
                                + " StdDeviation: " + Long.toString(dif / j));
                        output.write("\n-------------------\n");
                        output.close();
                        sum = 0;
                        j = 0;
                    }
                    File logFile = new File(logPath + type + "."
                            + g1File.getName() + ".log");
                    output = new BufferedWriter(new FileWriter(logFile));

                }

                Graph g1 = GraphGenerator.getLibGraph(g1File);
                Graph g2 = GraphGenerator.swap2edges(g1);

                logger.warning(type + "." + g1File.getAbsolutePath());
                output.write(i + " / " + graphs.size() + ": "
                        + g1File.getName() + ": ");

                long res = -1;
                if (type.equals("BFS")) {
                    res = runNonIsoBFS(g1, g2);
                } else if (type.equals("Simple")) {
                    res = runNonIsoSimple(g1, g2);
                } else if (type.equals("VF2")) {
                    res = runVF2(g1, g2);
                }

                if (res < 0) {
                    result = false;
                    output.write("  !!! NOT PASSED !!! ");
                } else {
                    sum += res;
                    output.write(Long.toString(res) + "\t "
                            + g1.getNumberOfNodes());
                    dev[i] = res;
                }
                output.write("\n");
                output.flush();
            } catch (Throwable e) {
                try {
                    i--;
                    j--;
                    output.write("  !!! EXCEPTION !!! ");
                    output.write(e.toString() + "\n");
                    output.flush();
                } catch (Exception e2) {
                    // error occurred writing
                    System.out.println("Damn.");
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
        }
        try {
            if (output != null) {
                long avg = (sum / j);
                long dif = 0;
                for (int idx = i - j + 1; idx < i; idx++) {
                    dif += Math.abs((dev[idx] - avg));
                }
                output.write("-------------------\n");
                output.write("Summe: " + Long.toString(sum) + " Average: "
                        + Long.toString(avg) + " StdDeviation: "
                        + Long.toString(dif / j));
                output.write("\n-------------------\n");
                output.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        date = new java.util.Date();
        System.out.println("---- End BFS Test: " + dateFormat.format(date));
        assertTrue("BFS Tests", result);
    }
}
