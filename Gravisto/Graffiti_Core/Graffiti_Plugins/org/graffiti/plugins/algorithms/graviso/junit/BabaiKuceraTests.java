package org.graffiti.plugins.algorithms.graviso.junit;

import static org.junit.Assert.assertTrue;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.algorithm.AlgorithmResult;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.ObjectParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.isomorphism.AbstractIsomorphism;
import org.graffiti.plugins.algorithms.isomorphism.BabaiKucera;
import org.junit.Before;
import org.junit.Test;

/**
 * @author lenhardt
 * 
 */
public class BabaiKuceraTests {

    @SuppressWarnings("unchecked")
    private Parameter[] parameters;

    private static final String SUCCESS_MSG = "The graphs are isomorphic!";

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

        BooleanParameter direction = new BooleanParameter(false, "direction",
                "Ignore direction of edges.");
        parameters = new Parameter[3];
        parameters[2] = direction;
    }

    private boolean runSimple(Graph g1, Graph g2) {
        ObjectParameter o1 = new ObjectParameter(g1, null, null);
        ObjectParameter o2 = new ObjectParameter(g2, null, null);

        parameters[0] = o1;
        parameters[1] = o2;

        AbstractIsomorphism r = new BabaiKucera();
        r.setAlgorithmParameters(parameters);
        long startTime, endTime;
        startTime = System.currentTimeMillis();
        r.execute();
        endTime = System.currentTimeMillis();
        AlgorithmResult res = r.getResult();
        java.util.Map<String, Object> m = res.getResult();
        r.reset();
        System.out.println("Execution Time: " + (endTime - startTime));
        Object mo = m.get("Result");
        String resultString = mo.toString();
        return resultString.equals(SUCCESS_MSG);
    }

    @Test
    public void testGrid10x10() throws Exception {
        System.out.print("Grid 10x10:");
        Graph g[] = GraphGenerator.getGrid10();
        assertTrue(runSimple(g[0], g[1]));
    }

    @Test
    public void testGrid11x11() throws Exception {
        System.out.print("Grid 11x11:");
        Graph g[] = GraphGenerator.getGrid11();
        assertTrue(runSimple(g[0], g[1]));
    }

    @Test
    public void testGrid13x13() throws Exception {
        System.out.print("Grid 13x13:");
        Graph g[] = GraphGenerator.getGrid13();
        assertTrue(runSimple(g[0], g[1]));
    }

    @Test
    public void testGrid15x15() throws Exception {
        System.out.print("Grid 15x15:");
        Graph g[] = GraphGenerator.getGrid15();
        assertTrue(runSimple(g[0], g[1]));
    }

    @Test
    public void testGrid20x20() throws Exception {
        System.out.print("Grid 20x20:");
        Graph g[] = GraphGenerator.getGrid20();
        assertTrue(runSimple(g[0], g[1]));
    }

    @Test
    public void testGrid21x21() throws Exception {
        System.out.print("Grid 21x21:");
        Graph g[] = GraphGenerator.getGrid21();
        assertTrue(runSimple(g[0], g[1]));
    }

    @Test
    public void testGrid24x24() throws Exception {
        System.out.print("Grid 24x24:");
        Graph g[] = GraphGenerator.getGrid24();
        assertTrue(runSimple(g[0], g[1]));
    }

    @Test
    public void testGrid27x27() throws Exception {
        System.out.print("Grid 27x27:");
        Graph g[] = GraphGenerator.getGrid27();
        assertTrue(runSimple(g[0], g[1]));
    }

    @Test
    public void testGrid30x30() throws Exception {
        System.out.print("Grid 30x30:");
        Graph g[] = GraphGenerator.getGrid30();
        assertTrue(runSimple(g[0], g[1]));
    }

    @Test
    public void testGrid31x31() throws Exception {
        System.out.print("Grid 31x31:");
        Graph g[] = GraphGenerator.getGrid31();
        assertTrue(runSimple(g[0], g[1]));
    }

    @Test
    public void testSRG05a() throws Exception {
        System.out.println("SRG05a:");
        Graph g[] = GraphGenerator.getSRG05a();
        assertTrue(runSimple(g[0], g[1]));
    }

    @Test
    public void testSRG05b() throws Exception {
        System.out.println("SRG05b:");

        Graph g[] = GraphGenerator.getSRG05b();
        assertTrue(runSimple(g[0], g[1]));
    }

    @Test
    public void testSRG09() throws Exception {
        System.out.println("SRG09:");
        Graph g[] = GraphGenerator.getSRG09();
        assertTrue(runSimple(g[0], g[1]));
    }

    public void testSRG17() throws Exception {
        System.out.println("SRG17:");
        Graph g[] = GraphGenerator.getSRG17();
        assertTrue(runSimple(g[0], g[1]));
    }

    public void testSRG25() throws Exception {
        System.out.println("SRG25:");
        Graph g[] = GraphGenerator.getSRG25();
        assertTrue(runSimple(g[0], g[1]));
    }

    @Test
    public void testSRG36a() throws Exception {
        System.out.println("SRG36a:");
        Graph g[] = GraphGenerator.getSRG36a();
        assertTrue(runSimple(g[0], g[1]));
    }

    @Test
    public void testSRG36b() throws Exception {
        System.out.println("SRG36b:");
        Graph g[] = GraphGenerator.getSRG36b();
        assertTrue(runSimple(g[0], g[1]));
    }

    @Test
    public void testSRG40() throws Exception {
        System.out.println("SRG40:");
        Graph g[] = GraphGenerator.getSRG40();
        assertTrue(runSimple(g[0], g[1]));
    }

    @Test
    public void testSRG45a() throws Exception {
        System.out.println("SRG45a:");
        Graph g[] = GraphGenerator.getSRG45a();
        assertTrue(runSimple(g[0], g[1]));
    }

    @Test
    public void testSRG45b() throws Exception {
        System.out.println("SRG45b:");
        Graph g[] = GraphGenerator.getSRG45b();
        assertTrue(runSimple(g[0], g[1]));
    }

    @Test
    public void testSRG64() throws Exception {
        System.out.println("SRG64:");
        Graph g[] = GraphGenerator.getSRG64();
        assertTrue(runSimple(g[0], g[1]));
    }

    @Test
    public void testSRG64b() throws Exception {
        System.out.println("SRG64b:");
        Graph g[] = GraphGenerator.getSRG64b();
        assertTrue(runSimple(g[0], g[1]));
    }

    @Test
    public void testSRG36c() throws Exception {
        System.out.println("SRG36c:");
        Graph g[] = GraphGenerator.getSRG36c();
        assertTrue(runSimple(g[0], g[1]));
    }
}
