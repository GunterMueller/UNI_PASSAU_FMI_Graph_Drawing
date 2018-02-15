// =============================================================================
//
//   FRSpringLabelingAlgorithmStandard.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.labeling;

import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugins.algorithms.labeling.forces.AbstractSEForce;
import org.graffiti.plugins.algorithms.labeling.forces.AttractiveAdjacentNodesForce;
import org.graffiti.plugins.algorithms.labeling.forces.EdgeLabelRepulsionForce;
import org.graffiti.plugins.algorithms.labeling.forces.GravityForce;
import org.graffiti.plugins.algorithms.labeling.forces.LabelEdgesRepulsionForce;
import org.graffiti.plugins.algorithms.labeling.forces.NodeLabelAdjacentEdgesRepulsionForce;
import org.graffiti.plugins.algorithms.labeling.forces.RepulsiveNodeEdgeForce;
import org.graffiti.plugins.algorithms.labeling.forces.RepulsiveNodeNodeForce;
import org.graffiti.plugins.algorithms.springembedderFR.FRNode;
import org.graffiti.plugins.algorithms.springembedderFR.FRSpringAlgorithmStandard;
import org.graffiti.plugins.algorithms.springembedderFR.GeometricalVector;
import org.graffiti.selection.Selection;

/**
 * Labeling by Fruchtermann & Reingold spring embedder.
 * <p>
 * <b><i>Implementation:</i></b> The algorihm extends
 * <code>FRSpringAlgorithmStandard</code> by Marco Matzeder.
 * <p>
 * Removed "extends FRSpringAlgorithmStandard" - applied changes make usage
 * uncomfortable (e.g. extractFRGraphToGraph and various others)
 * 
 * @author scholz
 * @see FRSpringAlgorithmStandard
 */
public class FRSpringLabelingAlgorithmStandard extends AbstractAlgorithm {

    /**
     * If, user clicks the next iteration step.
     */
    public final static String NEXT_STEP = " >> ";

    /**
     * If, user clicks complete without step view. Algorithm runs without the
     * rest steps.
     */
    public final static String COMPLETE_WITHOUT_STEP_VIEW = "complete without stepview";

    /**
     * If, user clicks cancel. Algorithms terminates at this step.
     */
    public final static String CANCEL_ALGORITHM = "cancel";

    /**
     * Saves the nodepostion of the node, which is the most left one
     */
    public static double smallestX;

    /**
     * Saves the nodeposition of the node, which is the most upper one
     */
    public static double smallestY;

    /**
     * Saves the nodepostion of the node, which is the most right one
     */
    public static double biggestX = Double.NEGATIVE_INFINITY;

    /**
     * Saves the nodeposition of the node, which is the most down one
     */
    public static double biggestY = Double.NEGATIVE_INFINITY;

    /**
     * temperature of the "graph" (Fruchterman&Reingold, Coleman&Parker)
     */
    public static double temperature;

    /**
     * Nodes, the user has selected
     */
    public static Selection selection;

    /**
     * Parameter to know if the algorithm is executed in single steps or in one
     * step
     */
    public BooleanParameter viewAlgoritmStepsParam;

    /**
     * The list of the forces, which are applied during the algorithm
     */
    protected LinkedList<AbstractSEForce> listOfForces;

    /**
     * The parameters of the spring embedder algorithm
     */
    protected SELabelingAlgorithmParameters p;

    /**
     * Is set true, if user has clicked to cancel during step view execution,
     * else false.
     */
    public static boolean cancelAlgorithm;

    /**
     * True, if algorithm should be performed in singular steps
     */
    public static boolean viewAlgorithmSteps;

    protected FRSpringLabelingAlgorithmStandard() {
        // a new spring embedder parameter for calculation
        p = new SELabelingAlgorithmParameters();

        viewAlgoritmStepsParam = new BooleanParameter(
                viewAlgorithmSteps,
                "view steps of the algorithm",
                "Is set true, if algorithm should show every iteration step, to go to next step.");
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Spring Embedder (no parameters adjustable)";
    }

    /**
     * Returns an array with the parameters of this algorithm
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        SelectionParameter selParam = new SelectionParameter("Selection",
                "<html>Additionally selected nodes are also moved.<p>If empty, "
                        + "all nodes are static.</html>");
        selParam.setSelection(new Selection("_temp_"));

        return new Parameter[] { selParam, this.viewAlgoritmStepsParam };
    }

    /**
     * Sets the parameters, the user has specified
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        selection = ((SelectionParameter) params[0]).getSelection();
        // selection = new Selection(); //will be empty, as not supported
        viewAlgorithmSteps = ((BooleanParameter) params[1]).getBoolean()
                .booleanValue();

        // new list of the applied forces during the algorithm
        listOfForces = new LinkedList<AbstractSEForce>();

        // node-node repulsive force
        // also: repulsion between label nodes and standard nodes
        // repulsion between label nodes
        // repulsion between node label node and corresponding node
        listOfForces.add(new RepulsiveNodeNodeForce(p));

        // node-edge repulsive force
        listOfForces.add(new RepulsiveNodeEdgeForce(p));

        // attractive forces betweeen adjacent nodes
        // also: edge label own edge attraction
        // node label own node attraction
        listOfForces.add(new AttractiveAdjacentNodesForce(p));

        // Labeling forces:

        // repulsion from other edges
        if (p.isLabelNodeEdgeRepulsion) {
            listOfForces.add(new LabelEdgesRepulsionForce(p));
        }

        // repulsion from own edges (node label node)
        if (p.isLabelNodeEdgeRepulsion) {
            listOfForces.add(new NodeLabelAdjacentEdgesRepulsionForce(p));
        }

        // repulsion from own edge (edge label node)
        if (p.isEdgeLabelNodeRepulsionForce) {
            listOfForces.add(new EdgeLabelRepulsionForce(p));
        }

        if (p.isGravity) {
            listOfForces.add(new GravityForce(p));
        }
    }

    /**
     * Checks the preconditions (none at the moment)
     */
    @Override
    public void check() throws PreconditionException {

        PreconditionException errors = new PreconditionException();

        if (graph == null) {
            errors.add("The graph instance may not be null.");
        }

        if (graph.getNodes().size() == 0) {
            errors.add("There are no nodes existing in the graph.");
        }

        if (p.isNodesGrowing && !p.isCalculationWithNodeSizes) {
            errors
                    .add("Nodes Growing variant can only be used with the calculation with node sizes variant.");
        }
        if (!errors.isEmpty())
            throw errors;
    }

    public void execute() {
        cancelAlgorithm = false;

        long nanoTime = System.nanoTime();

        // Set up graph:
        p.fRGraph = new FRGraphLabeling(graph, selection);

        // to calculate k (variable of Fruchterman & Reingold)
        int numberOfNodes = graph.getNodes().size();

        if (numberOfNodes != 0) {
            // variable of Fruchterman & Reingold agorithm
            p.k = Math.sqrt(p.height * p.width / numberOfNodes);

            if (p.isCalculationIdealLengths) {
                setOptimalNodeDistance();
            }
        }

        // Set nodes not movable
        // {
        // Iterator<FRNode> itNodes = p.fRGraph.getFRNodesIterator();
        // while (itNodes.hasNext()) {
        // itNodes.next().setMovable(false);
        // }
        // }
        //        
        // // Add movable label nodes and springs:
        // // node labels
        // {
        // Iterator<Node> itNodes = graph.getNodesIterator();
        // Node node;
        // FRLabelNode labelNode;
        // Set<FRNode> fRnodes = p.fRGraph.getFRNodes();
        // while (itNodes.hasNext()) {
        // node = itNodes.next();
        // for (Attribute attr: node.getAttributes().getCollection().values())
        // {
        // if (attr instanceof NodeLabelAttribute)
        // {
        // // NodeLabelAttribute nla = (NodeLabelAttribute)a;
        // // Label l = lm.acquireLabel(node, nla);
        // // System.out.println(nla.getId()); // the name of the label
        // // System.out.println(nla.getLabel()); // the displayed text
        // // //System.out.println(l..getWidth()); // width of the enclosing
        // // // rectangle
        // // //System.out.println(l.getHeight()); // height of the enclosing
        // // // rectangle
        // // double dx = nla.getPosition().getRelHor();
        // // double dy = nla.getPosition().getRelVert();
        // // System.out.println(nodeX + (nodeWidth / 2) * dx); // x-coordinate
        // of the center of the label
        // // System.out.println(nodeY + (nodeHeight / 2) * dy); // y-coordinate
        // ...
        //                    
        // // add node label to FR graph
        // labelNode = new FRLabelNode(
        // node, true, ((LabelAttribute)attr).getLabel(),
        // ((LabelAttribute)attr).getFont(),
        // ((LabelAttribute)attr).getTextcolor());
        // fRnodes.add(labelNode);
        //                        
        //                        
        // }
        // }
        //
        // }
        // }
        //        
        // // edge labels

        // run spring embedder

        // if quenching phase has to be executed
        if (p.isQuenchingPhase && p.quenchingIterations > 0) {
            // set the phase
            p.phase = AbstractSEForce.QUENCHING_PHASE;

            // start temperature for quenching phase
            temperature = p.quenchingTemperatureStart;

            // for temperature calculation (p.quenchingIterations > 0)
            double lambda = Math.pow(
                    (p.quenchingTemperatureStart / p.quenchingTemperatureEnd),
                    1.0d / p.quenchingIterations);

            // call the first phase (quenching phase)
            algorithmFruchtermanReingold(p.quenchingIterations, lambda);
            // Fehler: Grid.initGrid - Typ von graph.getFRNodes() stimmt nicht
        }

        // double quenchingTime = System.currentTimeMillis();

        // simmering phase (for finetuning)
        p.phase = AbstractSEForce.SIMMERING_PHASE;

        if (p.simmeringIterations > 0) {

            double lambda = Math.pow(
                    (p.simmeringTemperatureStart / p.simmeringTemperatureEnd),
                    1.0d / p.simmeringIterations);
            temperature = p.simmeringTemperatureStart;

            // call the second phase (simmering phase)
            algorithmFruchtermanReingold(p.simmeringIterations, lambda);
        }

        extractFRGraphToGraph();

        nanoTime = System.nanoTime() - nanoTime;

        // output
        if (p.isConsoleStatistics) {
            System.out.println(properties_Of_Algorithm());
        }
        System.out.println("Algorithm running time: "
                + (nanoTime / 1000000000d) + "s");
    }

    /**
     * The algorithm of Fruchterman and Reingold, with the specified number of
     * iterations and the start temperature. Administrates the variants (e.g.
     * grid variant), the phases (quenching, simmering), temperature calculation
     * and the most important the calculation of the forces.
     * 
     * 
     * @param iterations
     *            Number of iterations the algorithm executes.
     * @param lambda
     *            The quotient to calculate the current temperature (compare
     *            Coleman/Parker 1996).
     */
    protected void algorithmFruchtermanReingold(int iterations, double lambda) {

        // if grid variant is used, then initialize the grid
        if (p.isGridVariant) {
            initializeGrid();
        }

        // number of iterations the algorithm has to do, in every phase
        for (int i = 1; i <= iterations && !cancelAlgorithm; i++) {
            if (i % 10 == 0) {
                System.out.println("Aktuelle Temperatur: " + temperature
                        + " im " + i + "-ten Durchlauf!");
            }
            if (viewAlgorithmSteps) {
                extractFRGraphToGraph();

                String nextAction = stepHandle(i);
                if (nextAction == NEXT_STEP) {
                    // nothing to do here
                } else if (nextAction == COMPLETE_WITHOUT_STEP_VIEW) {

                    viewAlgorithmSteps = false;

                } else if (nextAction == CANCEL_ALGORITHM) {

                    cancelAlgorithm = true;
                    break;

                }
            }

            if (p.phase == AbstractSEForce.SIMMERING_PHASE) {
                p.simmeringIteration = i;
            }

            // sets the actual temperature
            setActualTemperature(lambda);

            if (p.isConsoleFRNodeTracking) {
                System.out
                        .println("Calculating forces... node listing follows:");
                for (FRNode node : p.fRGraph.fRNodes) {
                    System.out.println("FRNode: " + node.getLabel() + " ("
                            + node.getXPos() + ", " + node.getYPos() + ")");
                }
            }

            // calculates the required forces
            calculateForces();

            // moves the nodes after calculation of the forces for the nodes
            moveNodesOriginal();

            if (p.isConsoleFRNodeTracking) {
                System.out
                        .println("Finished force calculation: node listing follows:");
                for (FRNode node : p.fRGraph.fRNodes) {
                    System.out.println("FRNode: " + node.getLabel() + " ("
                            + node.getXPos() + ", " + node.getYPos() + ")");
                }
                System.out.println();
            }

        } // end for (#iterations)

    }

    /**
     * Returns the parameter adjustment of the algorithm.
     * 
     * @return parameter adjustment
     */
    protected String properties_Of_Algorithm() {
        System.out.println(this.getName());
        return p.toString();
    }

    /**
     * Initializes the grid, used in algorithm (Fruchterman&Reingold)
     */
    protected void initializeGrid() {
        // gridLength nach F&R (k is already defined)
        int gridLength = (int) (2 * p.k);

        // erstellen des Grids mit gridLength und dem gegebenen Graphen
        p.grid = new Grid(gridLength, p.fRGraph);
    }

    protected void extractFRGraphToGraph() {
        p.fRGraph.extractFRGraphToGraph();
    }

    /**
     * Calculates the forces of the nodes in the graph (e.g. repulsive forces,
     * attractive forces, ...)
     */
    protected void calculateForces() {
        // all forces calculate and add its force vectors
        for (AbstractSEForce force : listOfForces) {
            if (p.isBasicForceLogging) {
                System.out.println(force.getClass().getSimpleName());
            }
            force.calculateForce();
        }
    }

    /**
     * Sets the actual time of the algorithm after Coleman&Parker.
     * 
     * @param lambda
     *            The constant of the formula.
     * @return The actual temperature.
     */
    protected double setActualTemperature(double lambda) {
        return temperature = temperature / lambda;
    }

    /**
     * Creates a new FRGraph for a more efficient calculation during the
     * algorithm.
     * 
     * @param graph
     *            The given graph.
     * @param selection
     *            The selected nodes.
     * @return The created FRGraphLabeling
     */
    protected FRGraphLabeling createFRGraph(Graph graph, Selection selection) {
        return new FRGraphLabeling(graph, selection);
    }

    /**
     * Removes self loops in the graph.
     * 
     * @param g
     *            The given Graph
     */
    protected void removeSelfLoops(Graph g) {

        Iterator<Edge> edgeIt = graph.getEdgesIterator();
        while (edgeIt.hasNext()) {
            Edge e = edgeIt.next();

            if (e.getSource() == e.getTarget()) {
                graph.deleteEdge(e);
            }
        }
    }

    /**
     * Returns the correspondent int value, the user has chosen.
     * 
     * @param iteration
     * @return Returns true, if cancel was clicked, else false
     */
    protected String stepHandle(int iteration) {

        String outputPhase = "simmering";
        if (p.phase == AbstractSEForce.QUENCHING_PHASE) {
            outputPhase = "quenching";
        }
        String[] options = { NEXT_STEP, COMPLETE_WITHOUT_STEP_VIEW,
                CANCEL_ALGORITHM };
        int chosen = JOptionPane.showOptionDialog(null, "Go to next ("
                + iteration + "-th) iteration in " + outputPhase + " phase",
                "Next step in Spring Embedder", JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE, null, options, options[0]);

        if (chosen == 0)
            return NEXT_STEP;
        else if (chosen == 1)
            return COMPLETE_WITHOUT_STEP_VIEW;
        else {
            // only chosen == 2 possible
            chosen = 0; // DEBUG: avoid cancels at further calls
            return CANCEL_ALGORITHM;
        }

    }

    /**
     * Limits the movement of a node by temperature.
     * 
     * @param forceVector
     *            The vector the node should be moved along.
     * @param temperature
     *            The temperature to limit the movement
     * @return The shorten vector
     */
    protected GeometricalVector limitForceVectorByTemperature(
            GeometricalVector forceVector, double temperature) {
        if (p.isGlobalTempConceptFR) {
            double maxDisp = temperature;
            if (GeometricalVector.getLength(forceVector) > maxDisp) {
                // clip the force vector to the length of the maxDisp value
                forceVector
                        .setGeometricalVector(forceVector.getUnitVector()
                                .getX()
                                * maxDisp, forceVector.getUnitVector().getY()
                                * maxDisp);
            }
        } else {
            // test version limit the force with the factor temperature (not
            // really good)
            forceVector = GeometricalVector.mult(forceVector, temperature);
        }
        return forceVector;
    }

    /**
     * Sets the ideal node distance and the ideal edge length, dependent on
     * variable k of algorithm
     */
    protected void setOptimalNodeDistance() {
        p.optimalNodeDistance = p.normalizingIdealLengthFactor * p.k;
    }

    /**
     * Move the nodes accordingly to the calculated forces, at end of an
     * iteration.
     */
    protected void moveNodesCP() {
        // a sum of all node coordinates to calculate then the barycenter
        // (needed for gravity)
        GeometricalVector barycenterSum = new GeometricalVector();

        // calculate the movement sqrt (AGLO)
        double lengthOfMovement = 0.0d;
        for (FRNode nodeToMove : p.fRGraph.getFRNodes()) {
            GeometricalVector force = nodeToMove.getSumOfForces();
            lengthOfMovement += (force.getX() * force.getX());
            lengthOfMovement += (force.getY() * force.getY());
        }
        lengthOfMovement = Math.sqrt(lengthOfMovement);

        // move every node
        for (FRNode nodeToMove : p.fRGraph.getFRNodes()) {
            // moves the node according to force
            moveNode(nodeToMove, lengthOfMovement);

            // adding the position of the node to the sum of the barycenter
            barycenterSum = GeometricalVector.add(barycenterSum,
                    new GeometricalVector(nodeToMove));

        } // end while (nodesIt3)

        // barycenter of the graph after moving all nodes accordingly their
        // forces
        if (graph.getNodes().size() != 0) {
            p.barycenter = GeometricalVector.mult(barycenterSum,
                    1.0d / p.fRGraph.getFRNodes().size());
        }
    }

    /**
     * Moves the given node in terms of Coleman&Parker.
     * 
     * @param nodeToMove
     *            The node to move.
     * @param lengthOfMovement
     *            The length of the total movement
     */
    protected void moveNode(FRNode nodeToMove, double lengthOfMovement) {
        // to know in which gridsquare the node is
        double oldPosX = nodeToMove.getXPos();
        double oldPosY = nodeToMove.getYPos();

        // System.out.println(nodeToMove.getForces() + ", lengthOfMovement:" +
        // lengthOfMovement);

        // sum of all different forces
        GeometricalVector forceVector = nodeToMove.getSumOfForces();

        // System.out.println(nodeToMove.printDifferentForces());

        if (nodeToMove.isMovable()) {
            double factor = Math.min(lengthOfMovement, temperature);

            forceVector.setGeometricalVector(forceVector.getX()
                    / lengthOfMovement, forceVector.getY() / lengthOfMovement);

            double localTemperature = 1.0d;
            if (p.isLocalTemperature) {

                // avoid oscillation
                nodeToMove.setLocalTemperature(forceVector);
                localTemperature = nodeToMove.getLocalTemperature();
            }
            forceVector.setGeometricalVector(forceVector.getX()
                    * localTemperature, forceVector.getY() * localTemperature);

            nodeToMove.setXPos((nodeToMove.getXPos() + (forceVector.getX())
                    * factor));
            nodeToMove.setYPos((nodeToMove.getYPos() + (forceVector.getY())
                    * factor));

        }

        // after the node is moved, reset the force
        nodeToMove.resetForces();

        // attach node to another gridsquare, if necessary
        if (p.isGridVariant) {
            p.grid.movedNodeInGrid(nodeToMove, oldPosX, oldPosY);
        }
    }

    /**
     * Move the nodes accordingly to the calculated forces, at end of an
     * iteration.
     */
    protected void moveNodesOriginal() {
        // a sum of all node coordinates to calculate then the barycenter
        // (needed for gravity)
        GeometricalVector barycenterSum = new GeometricalVector();

        // move every node
        for (FRNode nodeToMove : p.fRGraph.getFRNodes()) {

            if (nodeToMove.isMovable()) {

                // moves the node according to force
                nodeToMove = moveNodeOriginal(nodeToMove);
            }

            // adding the position of the node to the sum of the barycenter
            barycenterSum = GeometricalVector.add(barycenterSum,
                    new GeometricalVector(nodeToMove));

        } // end while (nodesIt3)

        // barycenter of the graph after moving all nodes accordingly their
        // forces
        if (graph.getNodes().size() != 0) {
            p.barycenter = GeometricalVector.mult(barycenterSum, 1.0d / graph
                    .getNodes().size());
        }

        // // resets the value of the longest edge
        // fRGraph.resetLongestEdge();
        // fRGraph.setLongestEdge(getLongestEdge(fRGraph));

    }

    protected FRNode moveNodeOriginal(FRNode nodeToMove) {

        // to know in which gridsquare the node is
        double oldPosX = nodeToMove.getXPos();
        double oldPosY = nodeToMove.getYPos();

        // System.out.println(nodeToMove.getForces());

        // sum of all different forces
        GeometricalVector forceVector = nodeToMove.getSumOfForces();

        // System.out.println(nodeToMove.printDifferentForces());

        forceVector = limitForceVectorByTemperature(forceVector, temperature);

        double localTemperature = 1.0d;
        if (p.isLocalTemperature) {

            // wenn der neue GeometricalVector in die andere Richtung
            // zeigt und dabei noch einen gr��eren Betrag hat, dann
            // muss die lokale Temperatur angeglichen werden, damit
            // der Knoten nicht oszilliert
            nodeToMove.setLocalTemperature(forceVector);

            localTemperature = nodeToMove.getLocalTemperature();
        }

        // System.out.println("localTemp: " + localTemperature);
        forceVector.setGeometricalVector(forceVector.getX() * localTemperature,
                forceVector.getY() * localTemperature);

        nodeToMove.setXPos(nodeToMove.getXPos() + forceVector.getX());
        nodeToMove.setYPos(nodeToMove.getYPos() + forceVector.getY());

        // System.out.println("magn-force von " + nodeToMove + ": "
        // + nodeToMove.getForce("FORCE: MAGNETIC"));
        // after the node is moved, reset the force
        nodeToMove.resetForces();
        // move node to another gridsquare, if necessary
        if (p.isGridVariant) {
            p.grid.movedNodeInGrid(nodeToMove, oldPosX, oldPosY);
        }

        return nodeToMove;
    }

    /**
     * Returns the time required in seconds.
     * 
     * @param start
     *            Start time of algorithm
     * @param quenchingTime
     *            End time of quenching phase
     * @param finish
     *            End time of algorithm
     * @return Required time in seconds
     */
    protected String timeOfAlgorithm(double start, double quenchingTime,
            double finish) {

        String s = "";

        // Zeitausgabe
        s += " - Algorithm-Time: " + ((finish - start) / 1000) + "("
                + ((quenchingTime - start) / 1000) + ", "
                + ((finish - quenchingTime) / 1000) + ")";
        s += " for " + p.fRGraph.getFRNodes().size() + " nodes and "
                + p.fRGraph.getFREdges().size() + " edges.";

        return s;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
