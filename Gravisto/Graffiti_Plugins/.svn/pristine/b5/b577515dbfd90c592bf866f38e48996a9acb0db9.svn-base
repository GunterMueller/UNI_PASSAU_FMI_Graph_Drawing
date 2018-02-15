// =============================================================================
//
//   AdvancedRadialDrawing.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.radialTreeDrawing;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.DoubleParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugins.algorithms.treedrawings.GraphChecker;

/**
 * This class implements an algorithm for advanced radial tree drawings allowing
 * nodes of arbitrarely size
 * 
 * @author Andreas Schindler
 * @version 1.0 date 07. Maerz 2007
 */
public class AdvancedRadialDrawing extends AbstractAlgorithm {

    private static final Logger logger = Logger
            .getLogger(AdvancedRadialDrawing.class.getName());

    /**
     * The root of the tree
     */
    private Node root;

    /**
     * The minimal distance between father nodes and son nodes
     */
    private double fatherSonDistance = 75.0;

    /**
     * A multiplication factor for the fatherSonDistance, if the radius is not
     * sufficient
     */
    private double fatherSonIncFactor = 1.2;

    /**
     * The polar start angle for the drawing
     */
    private double startAngleDefault = 0.0;

    /**
     * defines whether the father should be centered above his sons
     */
    private boolean localSymmetry = true;

    /**
     * defines whether free space should be distributed evenly to the sons
     */
    private boolean globalSymmetry = true;

    /**
     * defines whether nodes of the same depth should be placed on the same
     * radius
     */
    private boolean globalLeveling = false;

    /**
     * defines whether the radii for all levels should be equidistant
     */
    private boolean equidistantLeveling = false;

    /**
     * defines whether the size optimal drawing should be calculated
     */
    private boolean optimiseDrawingSize = false;

    /**
     * step size for the search for an optimal drawing size by incrementing the
     * startAngle about startAngleStep
     */
    private double startAngleStep = 0.1;

    /**
     * minimum angle between two subtrees
     */
    private double clearanceDistance = 0.0;

    /**
     * selected nodes by the user
     */
    // private Selection selectedRootNodes = new Selection();

    /**
     * Instance for calculating the apex area of a node
     */
    private ApexArea apexArea = new ApexArea();

    /**
     * Instance for calculating coordinate operations
     */
    private Coordinates coordinates = new Coordinates();

    /**
     * Instance for calculating symmetry operations
     */
    private Symmetry symmetry = new Symmetry();

    /**
     * Instace for special leveling operations
     */
    private Leveling leveling = new Leveling();

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {

        return "AdvancedRadialDrawing";
    }

    /**
     * @see org.graffiti.plugin.algorithm.AbstractAlgorithm#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        SelectionParameter selParam = new SelectionParameter("Start node",
                "Eades will start with the only selected node.");

        Double min = new Double(20.0);
        Double max = new Double(300.0);
        this.fatherSonDistance = Math.round(fatherSonDistance * 100) / 100;
        DoubleParameter fatherSonParam = new DoubleParameter(new Double(
                this.fatherSonDistance), "Father-son Dist.",
                "Choose min father-son distance", min, max);

        min = new Double(1.01);
        max = new Double(3.00);
        DoubleParameter fatherSonIncParam = new DoubleParameter(new Double(
                this.fatherSonIncFactor), "Radius Inc. Factor",
                "% Factor for radius auto increment", min, max);

        min = new Double(0.0);
        max = new Double(2 * Math.PI);
        DoubleParameter startAngleParam = new DoubleParameter(new Double(
                this.startAngleDefault), "Start angle",
                "Start angle for the drawing", min, max);

        BooleanParameter localSymmetryParam = new BooleanParameter(new Boolean(
                this.localSymmetry), "local symmetry",
                "Center the father node above the sons");

        BooleanParameter globalSymmetryParam = new BooleanParameter(
                new Boolean(this.globalSymmetry), "global symmetry",
                "Distribute free space evenly to the sons");

        BooleanParameter globalLevelingParam = new BooleanParameter(
                new Boolean(this.globalLeveling), "global leveling",
                "Place nodes of the same depth on the same radius");

        BooleanParameter equidistantLevelingParam = new BooleanParameter(
                new Boolean(this.equidistantLeveling), "equidistant leveling",
                "Set the radii equidistant");

        BooleanParameter optimiseDrawingSizeParam = new BooleanParameter(
                new Boolean(this.optimiseDrawingSize), "optimise size",
                "Calculate an optimal drawing size");

        min = new Double(0.01);
        max = new Double(0.5);
        DoubleParameter startAngleStepParam = new DoubleParameter(new Double(
                this.startAngleStep), "step size",
                "step size for optimising the drawing size", min, max);

        min = new Double(0.0);
        max = new Double(0.2);
        DoubleParameter clearanceDistanceParam = new DoubleParameter(
                new Double(this.clearanceDistance), "clearance dist.",
                "minimum angle between two subtrees", min, max);

        return new Parameter[] { selParam, fatherSonParam, fatherSonIncParam,
                startAngleParam, localSymmetryParam, globalSymmetryParam,
                globalLevelingParam, equidistantLevelingParam,
                optimiseDrawingSizeParam, startAngleStepParam,
                clearanceDistanceParam };
    }

    /**
     * @see org.graffiti.plugin.algorithm.AbstractAlgorithm#setAlgorithmParameters(org.graffiti.plugin.parameter.Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {

        this.parameters = params;

        // this.selectedRootNodes = ((SelectionParameter)
        // params[0]).getSelection();

        this.fatherSonDistance = ((DoubleParameter) params[1]).getDouble();

        this.fatherSonIncFactor = ((DoubleParameter) params[2]).getDouble();

        this.startAngleDefault = ((DoubleParameter) params[3]).getDouble();

        this.localSymmetry = ((BooleanParameter) params[4]).getBoolean();

        this.globalSymmetry = ((BooleanParameter) params[5]).getBoolean();

        this.globalLeveling = ((BooleanParameter) params[6]).getBoolean();

        this.equidistantLeveling = ((BooleanParameter) params[7]).getBoolean();

        this.optimiseDrawingSize = ((BooleanParameter) params[8]).getBoolean();

        this.startAngleStep = ((DoubleParameter) params[9]).getDouble();

        this.clearanceDistance = ((DoubleParameter) params[10]).getDouble();
        Constants.CLEARANCE_DISTANCE = clearanceDistance;
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {

        graph.getListenerManager().transactionStarted(this);

        if (optimiseDrawingSize) {
            optimiseDrawingSize();
        } else {
            startAlgo();
            if (localSymmetry) {
                symmetry.localSymmetry(graph);
            }
            if (globalSymmetry) {
                symmetry.globalSymmetry(graph, root);
            }
            if (globalLeveling) {
                leveling.globalLeveling(graph, root, fatherSonDistance,
                        equidistantLeveling);
            }
            coordinates.calculateCartesianCoordinates(graph);
        }

        // TODO ZoomChangeComponent moved to Graffiti_Attic;
        // use new zoom tool instead?
        // new ZoomChangeComponent("").moveAndResizeGraph();

        graph.getListenerManager().transactionFinished(this);
    }

    /*
     * @see org.graffiti.plugin.algorithm.AbstractAlgorithm#check()
     */
    @Override
    public void check() throws PreconditionException {

        root = GraphChecker.checkTree(graph, Integer.MAX_VALUE);
    }

    /**
     * main algorithm for radial drawing
     * 
     */
    private void startAlgo() {

        double endAngle = 0.0;
        double startAngle;

        // assures that the drawing fits into a 2 * PI area by autoincrementing
        // the fatherSonDistance if necessary
        do {

            initialize();
            startAngle = startAngleDefault;
            endAngle = draw(root, 0.0, startAngle);

            if (endAngle > 2 * Math.PI + startAngleDefault) {

                fatherSonDistance *= fatherSonIncFactor;
            }

        } while (endAngle > 2 * Math.PI + startAngleDefault);
    }

    /**
     * A recursive method for drawing the nodes in DFS order
     * 
     * @param n
     *            the actual node to draw
     * @param radius
     *            the radius on which n should be placed
     * @param startAngle
     *            the start angle of the wedge for the subtree n
     * @return the end angle of the wedge for the subtree n
     */
    private double draw(Node n, double radius, double startAngle) {

        n.setDouble(Constants.WEDGE_FROM, startAngle);

        apexArea.calculateApexArea(n, radius, startAngle);

        // the bordering circle for n
        double borderingRadius = n.getDouble(Constants.BORDERING_RADIUS);

        // the end angle of the wedge for n
        double endAngle;

        if (n.equals(root)) {

            endAngle = 0.0;
        } else {

            endAngle = n.getDouble(Constants.WEDGE_TO);
        }

        // centers n between his start and end angle on level level
        // coordinates.calculatePolarCoordinates(n, radius, (startAngle +
        // endAngle) / 2);
        n.setBoolean(Constants.VISITED, true);

        for (Node x : n.getNeighbors()) {

            // visit all sons
            if (!x.getBoolean(Constants.VISITED)) {

                // draw the son and get his wedge end angle
                double endAngleSubtreeX = draw(x, borderingRadius
                        + fatherSonDistance, startAngle);
                // refresh the end angle for n
                endAngle = Math.max(endAngle, endAngleSubtreeX);
                // sets the start angle for the next node to the end angle of
                // the current son
                startAngle = endAngleSubtreeX;
            }
        }

        if (n.equals(root)) {

            // root has an angle range of 2 * Pi
            n.setDouble(Constants.WEDGE_TO, 2 * Math.PI + startAngleDefault);
        } else {

            n.setDouble(Constants.WEDGE_TO, endAngle);
        }

        return endAngle;
    }

    /**
     * initializes the graph before drawing
     * 
     */
    private void initialize() {

        for (Node n : graph.getNodes()) {

            n.setBoolean(Constants.VISITED, false);
            n.setDouble(Constants.POLAR_RADIUS, 0);
            n.setDouble(Constants.POLAR_ANGLE, 0);
        }
    }

    private void optimiseDrawingSize() {

        GraphSize graphSize = new BoundingRectangleSize();
        double size;
        double optSize = Double.MAX_VALUE;
        double optAngle = 0.0;

        int steps = Math.round((float) ((Math.PI / 2) / startAngleStep));

        for (int i = 1; i < steps; i++) {

            startAlgo();
            if (localSymmetry) {
                symmetry.localSymmetry(graph);
            }
            if (globalSymmetry) {
                symmetry.globalSymmetry(graph, root);
            }
            if (globalLeveling) {
                leveling.globalLeveling(graph, root, fatherSonDistance,
                        equidistantLeveling);
            }
            coordinates.calculateCartesianCoordinates(graph);
            size = graphSize.getGraphSize(graph);
            logger.log(Level.WARNING, startAngleDefault + " liefert Fl�che "
                    + size);
            if (size < optSize) {

                optSize = size;
                optAngle = startAngleDefault;
            }
            startAngleDefault += startAngleStep;
        }

        startAngleDefault = optAngle;
        startAlgo();
        if (localSymmetry) {
            symmetry.localSymmetry(graph);
        }
        if (globalSymmetry) {
            symmetry.globalSymmetry(graph, root);
        }
        if (globalLeveling) {
            leveling.globalLeveling(graph, root, fatherSonDistance,
                    equidistantLeveling);
        }
        coordinates.calculateCartesianCoordinates(graph);

        logger.log(Level.WARNING, "Optimal size: " + optSize
                + ", Optimal start angle: " + optAngle);
    }
}
