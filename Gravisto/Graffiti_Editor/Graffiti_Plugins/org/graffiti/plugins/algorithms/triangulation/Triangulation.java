package org.graffiti.plugins.algorithms.triangulation;

import java.util.LinkedList;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.planarity.PlanarityAlgorithm;
import org.graffiti.plugins.algorithms.planarity.TestedComponent;

public class Triangulation extends AbstractAlgorithm {

    /**
     * The String shown in the parameter window for
     * <code>BushyTriangulation</code>
     */
    private static final String BUSHY = "(1) Bushy triangulation";

    /**
     * Parameter, indicating which algorithm was chosen: set true for
     * <code>BushyTriangulation</code>
     */
    protected static boolean bushy;

    /** The graph to be drawn */
    private Graph graph;

    /** The algorithm used to triangulate the graph */
    private TriangulationAlgorithm algorithm;

    /** The algorithm to test the graph for planarity */
    private PlanarityAlgorithm pAlgorithm = new PlanarityAlgorithm();

    /** The edges added during triangulation */
    private LinkedList<Edge> addedEdges = new LinkedList<Edge>();

    /**
     * Returns the name of the algorithm
     * 
     * @return name of th algorithm
     */
    public String getName() {
        return "Triangulation Algorithm(s)";
    }

    /**
     * Sets the parameters of the algorithm
     * 
     * @param params
     *            the parameters of the algorithm
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        Triangulation.bushy = false;
        String algSelection = ((StringSelectionParameter) params[0])
                .getSelectedValue();
        if (algSelection.equals(BUSHY)) {
            Triangulation.bushy = true;
        }
        this.parameters = params;
    }

    /**
     * Gets the parameters of the algorithm
     * 
     * @return the parameters of the algorithm
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        Parameter<?>[] parameter = new Parameter[1];
        String[] algParams = { BUSHY };
        StringSelectionParameter algorithm = new StringSelectionParameter(
                algParams, "ALGORITHM", "choose one triangulation");
        parameter[0] = algorithm;
        return parameter;
    }

    /**
     * Checks the algorithms preconditions: - graph is planar - graph is
     * biconnected
     * 
     * @throws PreconditionException
     *             if any of the preconditions is not satisfied.
     */
    @Override
    public void check() throws PreconditionException {
        this.graph = GraffitiSingleton.getInstance().getMainFrame()
                .getActiveSession().getGraph();
        pAlgorithm.attach(graph);
        pAlgorithm.testPlanarity();

        if (!pAlgorithm.isPlanar())
            throw new PreconditionException(
                    "The graph is not planar. Graph must be planar to be triangulated.");

        if (!checkBiconnection(pAlgorithm))
            throw new PreconditionException(
                    "The graph is not biconnected. Graph must be biconnected to be triangulated.");
    }

    /**
     * Decides (from the parameters), which algorithm to perform and exectutes
     * it.
     */
    public void execute() {
        if (Triangulation.bushy) {
            this.algorithm = new BushyTriangulation(this.graph, this.pAlgorithm);
            this.addedEdges = this.algorithm.triangulate();
        }
    }

    /**
     * Checks if the graph is biconnected, by counting the components of the
     * <code>TestedGraph</code>
     * 
     * @return true if the graph is biconnected
     */
    private boolean checkBiconnection(PlanarityAlgorithm pAlgorithm) {
        if (pAlgorithm.getTestedGraph().getNumberOfComponents() > 1)
            return false;
        TestedComponent comp = pAlgorithm.getTestedGraph()
                .getTestedComponents().get(0);
        if (comp.getNumberOfBicomps() > 1)
            return false;
        return true;
    }

    /**
     * Returns the edges added during the triangulation.
     * 
     * @return the added edges.
     */
    public LinkedList<Edge> getAddedEdges() {
        return addedEdges;
    }
}
