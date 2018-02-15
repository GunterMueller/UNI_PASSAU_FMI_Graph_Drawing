package org.graffiti.plugins.algorithms.circulardrawing;

import org.graffiti.graph.Graph;

/**
 * This class tests a graph, wether it is biconnected or not.
 * 
 * @author demirci Created on Jul 20, 2005
 */
public class TestBiconnection {

    /** The graph to be tested. */
    private Graph graph;

    /** Creates a new instance of the class */
    public TestBiconnection(Graph graph) {
        this.graph = graph;
    }

    /**
     * Test wether the graph is biconnected or not. At the moment this is done
     * via the test for biconnection from the <code>PlanarityAlgorithm</code>.
     * Should be replaced by an independent algorithm for testing /creating
     * biconnectivity later.
     * 
     * @return true if the graph is biconnected, false otherwise.
     */
    public boolean isBiconnected() {
        PlanarityAlgorithm pAlgorithm = new PlanarityAlgorithm();
        pAlgorithm.attach(this.graph);
        pAlgorithm.testPlanarity();
        if (pAlgorithm.getTestedGraph().getNumberOfComponents() > 1)
            return false;
        TestedComponent comp = pAlgorithm.getTestedGraph()
                .getTestedComponents().get(0);
        if (comp.getNumberOfBicomps() > 1)
            return false;
        return true;
    }
}
