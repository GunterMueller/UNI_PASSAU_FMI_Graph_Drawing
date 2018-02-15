package org.graffiti.plugins.algorithms.fpp;

/**
 * @author Le Pham Hai Dang
 */

import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.connectivity.Triconnect;
import org.graffiti.plugins.algorithms.planarity.PlanarityAlgorithm;
import org.graffiti.plugins.algorithms.planarity.TestedGraph;

/**
 * This is an implementation of Fraysseix Pach Pollack algorithm with the method
 * of G. Kant.
 */
public class FPP extends AbstractAlgorithm {

    // ~ Instance fields
    // ========================================================

    private PlanarityAlgorithm planar = new PlanarityAlgorithm();

    private TestedGraph tGraph;

    private Face[] faces;

    private int outerfaceIndex;

    private OrderNode[] reverseInduction;

    private CalculateFace calculatefaces;

    private CalculateOrder calculateorder;

    private LMCOrdering lmc;

    private int grid = 30;

    private boolean isChecked = false;

    private Integer initGrid = new Integer(30);

    private Integer minGrid = new Integer(1);

    private Integer maxGrid = new Integer(100);

    private IntegerParameter gridParamter = new IntegerParameter(initGrid,
            "Grid size",
            "This value is meant for the distance between two nodes.", minGrid,
            maxGrid, minGrid, Integer.MAX_VALUE);

    // ~ Methods
    // ================================================================

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        return new Parameter<?>[] { gridParamter };
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(org.graffiti.plugin.parameter.Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] parameters) {
        grid = ((IntegerParameter) parameters[0]).getInteger().intValue();
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "FPP";
    }

    /**
     * The default value of the grid size is 30.
     * 
     * @param grid
     */
    public void setGridSize(int grid) {
        this.grid = grid;
    }

    /**
     * @return true if the method check() is invoked, otherwise false;
     */
    public boolean getIsChecked() {
        return isChecked;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check() Checks the
     *      preconditions for the FPP algorithm. The graph is not allow to be
     *      empty and shall be triconnect and planar.
     */
    @Override
    public void check() throws PreconditionException {
        if (!isChecked) {
            planar.attach(graph);
            tGraph = planar.getTestedGraph();

            if (tGraph.getNumberOfDoubleEdges() > 0)
                throw new PreconditionException(
                        "The graph contains double edges. Cannot run FPP.");

            if (tGraph.getNumberOfLoops() > 0)
                throw new PreconditionException(
                        "The graph contains loops. Cannot run FPP.");

            if (!planar.isPlanar())
                throw new PreconditionException(
                        "The graph is not planar. Cannot run FPP.");
            if (graph.getNumberOfNodes() <= 0)
                throw new PreconditionException(
                        "The graph is empty. Cannot run FPP.");
            if (graph.getNumberOfNodes() < 3)
                throw new PreconditionException(
                        "The graph is too small and not triconnected. Cannot run FPP.");

            Triconnect triconnect = new Triconnect();
            triconnect.attach(graph);
            triconnect.testTriconnect();
            if (!triconnect.isTriconnected())
                throw new PreconditionException(
                        "The graph is not triconnected. Cannot run FPP.");
            isChecked = true;
        }
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        planar.reset();
        super.reset();
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        if (!isChecked)
            throw new IllegalStateException("Method check() was not invoked!");
        startFPP();
        isChecked = false;
    }

    private void startFPP() {
        graph.getListenerManager().transactionStarted(this);
        initiation();
        order();
        drawing();
        graph.getListenerManager().transactionFinished(this);
    }

    /**
     * This method invokes <code>CalculateFace</code> in order to calculates the
     * number of faces and the outerface.
     */
    private void initiation() {
        calculatefaces = new CalculateFace(graph, tGraph);
        faces = calculatefaces.getFaces();
        outerfaceIndex = calculatefaces.getOutIndex();
    }

    /**
     * This method calls <code>CalculateOrder</code> and
     * <code>LMCOrdering</code> in order to calculates the Canonical Ordering
     * and the lmc Ordering.
     */
    private void order() {
        calculateorder = new CalculateOrder(faces, outerfaceIndex, graph,
                calculatefaces, tGraph);
        reverseInduction = calculateorder.getReverseInduction();
        lmc = new LMCOrdering(reverseInduction, graph.getNodes().size(),
                calculateorder);
    }

    /**
     * Drawing the graph dependent on the lmc Ordering. Method invokes
     * <code>Drawing</code>.
     */
    private void drawing() {
        new Drawing(graph, lmc.getLMCOrdering(), calculateorder, grid);
    }

}
