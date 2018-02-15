package org.graffiti.plugins.algorithms.planarAngleGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import lpsolve.LpSolveException;

import org.graffiti.graph.Edge;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.planarity.PlanarityAlgorithm;
import org.graffiti.plugins.algorithms.planarity.TestedGraph;
import org.graffiti.util.CoreGraphEditing;

/**
 * The interface to the Gravisto plugin mechanism.
 * 
 * @author Mirka Kossak
 */
public class PlanarAngleGraphAlgorithm extends AbstractAlgorithm {

    // Logger used to print information
    public static final Logger logger = Logger
            .getLogger(PlanarAngleGraphAlgorithm.class.getPackage().getName());

    // the minimum value that every angle should have.
    private int minAngle;

    // the minimum length that every edge should have.
    private int minEdge;

    // the maximum sum that all edge lengths together could have.
    private int maxEdgeSum;

    // stores, if the faces should be calculated from the planar embedding of
    // the planarity test or from the drawing
    private boolean facesFromDrawing;

    // stores all edges of the graph (including multiedges and selfloops)
    private List<Edge> allEdges;

    private static boolean lpsolve55Loaded = false;

    /**
     * Constructor for the <code>PlanarAngleGraphAlgorithm</code>.
     */
    public PlanarAngleGraphAlgorithm() {
        if (!lpsolve55Loaded) {
            System.loadLibrary("lpsolve55");
            lpsolve55Loaded = true;
        }
        parameters = new Parameter[2];
        minAngle = 15;
        minEdge = 50;
        facesFromDrawing = false;
        allEdges = new ArrayList<Edge>();
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        minAngle = ((IntegerParameter) params[0]).getValue();
        minEdge = ((IntegerParameter) params[1]).getValue();
        facesFromDrawing = ((BooleanParameter) params[2]).getValue();
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        IntegerParameter angleParam = new IntegerParameter(minAngle,
                "minAngle", "this will be the smallest angle");
        IntegerParameter edgeParam = new IntegerParameter(minEdge, "minEdge",
                "this will be the shortest edge");
        BooleanParameter facesFromDrawingParam = new BooleanParameter(
                facesFromDrawing, "facesFromDrawing",
                "should the new drawing have the same faces?");
        return new Parameter[] { angleParam, edgeParam, facesFromDrawingParam };
    }

    /**
     * Returns the name of the algorithm
     * 
     * @return The name of the algorithm
     */
    public String getName() {
        return "Drawing planar angle graph";
    }

    /**
     * Resets the algorithm.
     */
    @Override
    public void reset() {
        super.reset();
        minAngle = 15;
        minEdge = 50;
        facesFromDrawing = false;
        allEdges = new ArrayList<Edge>();
    }

    /**
     * Saves all edges of the graph (including the multiedges and loops).
     * 
     * @param collectionEdges
     *            Collection of all edges of the graph.
     */
    public void saveEdges(Collection<Edge> collectionEdges) {
        Iterator<Edge> edgeIt = collectionEdges.iterator();
        while (edgeIt.hasNext()) {
            Edge currentEdge = edgeIt.next();
            allEdges.add(currentEdge);
        }

    }

    /**
     * Executes the algorithm.
     */
    public void execute() {
        PlanarityAlgorithm planarityTest = new PlanarityAlgorithm();
        planarityTest.attach(graph);
        TestedGraph testedGraph = planarityTest.getTestedGraph();
        Collection<Edge> collectionEdges = graph.getEdges();
        saveEdges(collectionEdges);
        if (testedGraph.getNumberOfDoubleEdges() != 0
                || testedGraph.getNumberOfLoops() != 0) {
            CoreGraphEditing.removeMultipleEdgesAndLoops(graph);
        }
        boolean startAlgorithm = true;

        CalculateFaces calculatedFaces = null;
        if (facesFromDrawing) {
            TestPlanarDrawing planarDrawing = new TestPlanarDrawing(
                    testedGraph, graph);
            if (!planarDrawing.planarityTest()) {
                logger.info("Cannot run algorithm. The drawing is not planar.");
                startAlgorithm = false;
            } else {
                calculatedFaces = new CalculateFacesFromDrawing(graph,
                        testedGraph, planarDrawing);
            }
        } else {
            calculatedFaces = new CalculateFaces(graph, testedGraph);
        }
        if (startAlgorithm) {
            maxEdgeSum = (graph.getNumberOfEdges() * 400)
                    - (graph.getNumberOfNodes() * 200);

            Face[] faces = calculatedFaces.getAllFaces();

            AngleMatrix angleMatrix = new AngleMatrix(faces, graph, testedGraph);
            angleMatrix.makeMatrix();
            AngleLP angleLp = new AngleLP(angleMatrix, testedGraph, minAngle);
            try {
                angleLp.run();
            } catch (LpSolveException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (angleLp.getSol() == 0) {
                LineGraph lineGraph = new LineGraph(testedGraph, graph, angleLp
                        .getNodeWithAngles(), calculatedFaces);
                lineGraph.makeLineGraph();
                EdgeLP edgeLp = new EdgeLP(lineGraph.getEdgeMatrix(),
                        maxEdgeSum, minEdge);
                try {
                    edgeLp.run();
                } catch (LpSolveException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (edgeLp.getSol() == 0) {
                    Drawing drawing = new Drawing(edgeLp.getLPSolution(),
                            graph, testedGraph, allEdges);
                    drawing.drawSolution();
                    SweepLine sweep = new SweepLine(drawing
                            .getNodeWithCoordinates(), testedGraph,
                            calculatedFaces);
                    sweep.planarityTest();
                }
            }
        }
    }

    /**
     * Checks the preconditions for the drawing planar angle algorithm.
     * Possibilities of failure are: the graph is empty, the graph is not
     * connected, or the graph is not biconnected
     */
    @Override
    public void check() throws PreconditionException {
        if (graph.getNumberOfNodes() <= 0)
            throw new PreconditionException(
                    "The graph is empty. Cannot run drawing planar angle graph algorithm.");
        PlanarityAlgorithm planarityTest = new PlanarityAlgorithm();
        planarityTest.attach(graph);
        planarityTest.testPlanarity();
        planarityTest.execute();
        if (!planarityTest.isPlanar())
            throw new PreconditionException("The graph is not planar.");
        TestedGraph testedGraph = planarityTest.getTestedGraph();
        if (testedGraph.getNumberOfComponents() != 1)
            throw new PreconditionException("The graph is not connected.");
        if (graph.getNumberOfNodes() < 3)
            throw new PreconditionException("The graph has less than 3 nodes.");
    }
}
