package org.graffiti.plugins.algorithms.graviso.junit;

import static org.junit.Assert.assertTrue;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.algorithm.AlgorithmResult;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.ObjectParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringParameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.graviso.RefinementAlgorithmSimple;
import org.junit.Before;
import org.junit.Test;

/**
 * @author lenhardt
 * 
 */
public class RefinementBFSTests {

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

        parameters = new Parameter[9];
        parameters[P_VISUALIZE] = visualize;

        parameters[P_REGARD_DIRECTIONS] = regardDirections;
        parameters[P_USE_BFS_INFO] = useBFSInfo;
        parameters[P_REGARD_NODE_LABELS] = regardNodeLabels;
        parameters[P_NODE_LABEL_PATH] = nodeLabelPath;
        parameters[P_EDGE_LABEL_PATH] = edgeLabelPath;
        parameters[P_REGARD_EDGE_LABELS] = regardEdgeLabels;
    }

    private boolean runBFS(Graph g1, Graph g2) {
        ObjectParameter o1 = new ObjectParameter(g1, null, null);
        ObjectParameter o2 = new ObjectParameter(g2, null, null);

        parameters[0] = o1;
        parameters[1] = o2;

        RefinementAlgorithmSimple r = new RefinementAlgorithmSimple();
        r.setAlgorithmParameters(parameters);
        long startTime, endTime;
        startTime = System.currentTimeMillis();
        r.execute();
        endTime = System.currentTimeMillis();
        AlgorithmResult res = r.getResult();
        java.util.Map<String, Object> m = res.getResult();
        r.reset();
        System.out.println(" " + (endTime - startTime));
        Object mo = m.get("Result");
        String resultString = mo.toString();
        System.gc();
        return resultString.equals(SUCCESS_MSG);
    }

    @Test
    public void testGrid10x10() throws Exception {
        System.out.print("Grid 10x10:");
        Graph g[] = GraphGenerator.getGrid10();
        assertTrue(runBFS(g[0], g[1]));
    }

    @Test
    public void testGrid11x11() throws Exception {
        System.out.print("Grid 11x11:");
        Graph g[] = GraphGenerator.getGrid11();
        assertTrue(runBFS(g[0], g[1]));
    }

    @Test
    public void testGrid13x13() throws Exception {
        System.out.print("Grid 13x13:");
        Graph g[] = GraphGenerator.getGrid13();
        assertTrue(runBFS(g[0], g[1]));
    }

    @Test
    public void testGrid15x15() throws Exception {
        System.out.print("Grid 15x15:");
        Graph g[] = GraphGenerator.getGrid15();
        assertTrue(runBFS(g[0], g[1]));
    }

    @Test
    public void testGrid20x20() throws Exception {
        System.out.print("Grid 20x20:");
        Graph g[] = GraphGenerator.getGrid20();
        assertTrue(runBFS(g[0], g[1]));
    }

    @Test
    public void testGrid21x21() throws Exception {
        System.out.print("Grid 21x21:");
        Graph g[] = GraphGenerator.getGrid21();
        assertTrue(runBFS(g[0], g[1]));
    }

    @Test
    public void testGrid24x24() throws Exception {
        System.out.print("Grid 24x24:");
        Graph g[] = GraphGenerator.getGrid24();
        assertTrue(runBFS(g[0], g[1]));
    }

    @Test
    public void testGrid27x27() throws Exception {
        System.out.print("Grid 27x27:");
        Graph g[] = GraphGenerator.getGrid27();
        assertTrue(runBFS(g[0], g[1]));
    }

    @Test
    public void testGrid30x30() throws Exception {
        System.out.print("Grid 30x30:");
        Graph g[] = GraphGenerator.getGrid30();
        assertTrue(runBFS(g[0], g[1]));
    }

    @Test
    public void testGrid31x31() throws Exception {
        System.out.print("Grid 31x31:");
        Graph g[] = GraphGenerator.getGrid31();
        assertTrue(runBFS(g[0], g[1]));
    }

    public void testGrid40x40() throws Exception {
        System.out.print("Grid 40x40:");
        Graph g[] = GraphGenerator.getGrid40();
        assertTrue(runBFS(g[0], g[1]));
    }

    @Test
    public void testSRG05a() throws Exception {
        System.out.print("SRG05a:");
        Graph g[] = GraphGenerator.getSRG05a();
        assertTrue(runBFS(g[0], g[1]));
    }

    @Test
    public void testSRG05b() throws Exception {
        System.out.print("SRG05b:");

        Graph g[] = GraphGenerator.getSRG05b();
        assertTrue(runBFS(g[0], g[1]));
    }

    @Test
    public void testSRG09() throws Exception {
        System.out.print("SRG09:");
        Graph g[] = GraphGenerator.getSRG09();
        assertTrue(runBFS(g[0], g[1]));
    }

    @Test
    public void testSRG17() throws Exception {
        System.out.print("SRG17:");
        Graph g[] = GraphGenerator.getSRG17();
        assertTrue(runBFS(g[0], g[1]));
    }

    @Test
    public void testSRG25() throws Exception {
        System.out.print("SRG25:");
        Graph g[] = GraphGenerator.getSRG25();
        assertTrue(runBFS(g[0], g[1]));
    }

    @Test
    public void testSRG36a() throws Exception {
        System.out.print("SRG36a:");
        Graph g[] = GraphGenerator.getSRG36a();
        assertTrue(runBFS(g[0], g[1]));
    }

    @Test
    public void testSRG36b() throws Exception {
        System.out.print("SRG36b:");
        Graph g[] = GraphGenerator.getSRG36b();
        assertTrue(runBFS(g[0], g[1]));
    }

    @Test
    public void testSRG40() throws Exception {
        System.out.print("SRG40:");
        Graph g[] = GraphGenerator.getSRG40();
        assertTrue(runBFS(g[0], g[1]));
    }

    @Test
    public void testSRG45a() throws Exception {
        System.out.print("SRG45a:");
        Graph g[] = GraphGenerator.getSRG45a();
        assertTrue(runBFS(g[0], g[1]));
    }

    @Test
    public void testSRG45b() throws Exception {
        System.out.print("SRG45b:");
        Graph g[] = GraphGenerator.getSRG45b();
        assertTrue(runBFS(g[0], g[1]));
    }

    @Test
    public void testSRG64() throws Exception {
        System.out.print("SRG64:");
        Graph g[] = GraphGenerator.getSRG64();
        assertTrue(runBFS(g[0], g[1]));
    }

    @Test
    public void testSRG64b() throws Exception {
        System.out.print("SRG64b:");
        Graph g[] = GraphGenerator.getSRG64b();
        assertTrue(runBFS(g[0], g[1]));
    }

    @Test
    public void testSRG36c() throws Exception {
        System.out.print("SRG36c:");
        Graph g[] = GraphGenerator.getSRG36c();
        assertTrue(runBFS(g[0], g[1]));
    }
}
