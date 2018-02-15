package org.graffiti.plugins.algorithms.springembedderFR;

import java.util.LinkedList;

import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.DoubleParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.selection.Selection;

/**
 * Spring Embedder Algorithm with extension of Bertault. No new edge crossings
 * will be produced. The edge crossing properties are preserved.
 * 
 * @author matzeder
 */
public class FRSpringAlgorithmStandardPreserveEdgeCrossings extends
        FRSpringAlgorithmAllParams {

    /**
     * Constructs a new FRSpringAlgorithmStandardPreserveEdgeCrossings, the
     * specified variables are defined. The values of the instance variables are
     * found experimentally.
     * 
     */
    protected FRSpringAlgorithmStandardPreserveEdgeCrossings() {
        super();
    }

    /**
     * Returns an array with the parameters of this algorithm
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {

        // initial variants
        p.isGravity = false;
        p.isGridVariant = true;
        p.isNodesGrowing = true;
        p.isCalculationWithNodeSizes = true;

        p.isCalculationWithNodesAsRectangles = true;
        p.isCalculationWangMiyamoto = false;
        p.isLocalTemperature = true;
        // initial constants
        p.gravityConstant = 200;
        p.repulsionConstantNodeNode = 50.0;
        p.repulsionConstantNodeEdge = 3;
        p.attractionConstant = 0.001;
        p.normalizingIdealLengthFactor = 0.025d;
        p.quenchingTemperatureStart = 100;
        p.quenchingTemperatureEnd = 10;
        p.simmeringTemperatureStart = 10;
        p.simmeringTemperatureEnd = 2;

        // initial values for algorithm
        p.quenchingIterations = 50;
        p.simmeringIterations = 50;
        p.optimalNodeDistance = 10.0;
        // p.idealEdgeLength = 10.0;

        SelectionParameter selParam = new SelectionParameter("Selection",
                "<html>The selection to work on.<p>If empty, "
                        + "the whole graph is used.</html>");
        selParam.setSelection(new Selection("_temp_"));

        BooleanParameter isCalculationIdealLengthsParam = new BooleanParameter(
                p.isCalculationIdealLengths,
                "algorithm calculates ideal lengths?",
                "If set, algorithms calculates ideal lengths (e.g. node-node distance, edge lengths).");

        String[] arr = new String[] { AbstractSEForce.NO_GV_NO_NODE_SIZE,
                AbstractSEForce.GV, AbstractSEForce.NODE_SIZE,
                AbstractSEForce.NODE_SIZE_GROWING,
                AbstractSEForce.GV_NODE_SIZE,
                AbstractSEForce.GV_NODE_SIZE_GROWING };

        StringSelectionParameter variantsParam = new StringSelectionParameter(
                arr, "variants of algorithm",
                "The algorithm uses the chosen variant for calculation.");
        variantsParam.setValue(AbstractSEForce.NODE_SIZE_GROWING);

        BooleanParameter isGlobalTempOrigFRParam = new BooleanParameter(
                p.isGlobalTempConceptFR,
                "original temperature clipping method (FR)",
                "Set true, then force vector cipping method is used in calculation, else false.");

        BooleanParameter isQuenchingPhaseParam = new BooleanParameter(
                p.isQuenchingPhase, "with quenching phase?",
                "If set, algorithm is executed with quenching phase.");

        String[] magnArr = new String[] { AbstractSEForce.NO_MAGNETIC_FIELD,
                AbstractSEForce.NORTH, AbstractSEForce.SOUTH,
                AbstractSEForce.WEST, AbstractSEForce.EAST,
                AbstractSEForce.POLAR, AbstractSEForce.CONCENTRIC_CLOCK };
        StringSelectionParameter magnVariantsParam = new StringSelectionParameter(
                magnArr, "direction of the magnetic field",
                "Determines the direction of the magnetic field.");
        magnVariantsParam.setValue(AbstractSEForce.NO_MAGNETIC_FIELD);

        DoubleParameter magneticSpringConstantParam = new DoubleParameter(
                10.0d, "magnetic field strength",
                "Strength of the magnetic field");

        return new Parameter[] { selParam, this.viewAlgoritmStepsParam,
                this.nrQuenchingParam, this.startQuenchingTempParam,
                this.endQuenchingTempParam, this.nrSimmeringParam,
                this.startSimmeringTempParam, this.endSimmeringTempParam,
                isCalculationIdealLengthsParam, this.nodeDistParam,
                variantsParam, isGlobalTempOrigFRParam, isQuenchingPhaseParam,
                magnVariantsParam, magneticSpringConstantParam };
    }

    /**
     * Sets the parameters, the user has specified.
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;

        listOfForces = new LinkedList<AbstractSEForce>();

        listOfForces.add(new RepulsiveNodeNodeForce(p));
        listOfForces.add(new RepulsiveNodeEdgeForce(p));
        listOfForces.add(new AttractiveAdjacentNodesForce(p));
        if (p.isGravity) {
            listOfForces.add(new GravityForce(p));
        }

        selection = ((SelectionParameter) params[0]).getSelection();

        viewAlgorithmSteps = ((BooleanParameter) params[1]).getBoolean()
                .booleanValue();

        p.quenchingIterations = ((IntegerParameter) params[2]).getInteger()
                .intValue();
        p.quenchingTemperatureStart = ((DoubleParameter) params[3]).getDouble()
                .doubleValue();
        p.quenchingTemperatureEnd = ((DoubleParameter) params[4]).getDouble()
                .doubleValue();
        p.simmeringIterations = ((IntegerParameter) params[5]).getInteger()
                .intValue();
        p.simmeringTemperatureStart = ((DoubleParameter) params[6]).getDouble()
                .doubleValue();
        p.simmeringTemperatureEnd = ((DoubleParameter) params[7]).getDouble()
                .doubleValue();
        p.isCalculationIdealLengths = ((BooleanParameter) params[8])
                .getBoolean().booleanValue();
        p.optimalNodeDistance = ((DoubleParameter) params[9]).getDouble()
                .doubleValue();
        auswahl = (StringSelectionParameter) params[10];

        p.isGlobalTempConceptFR = ((BooleanParameter) params[11]).getBoolean()
                .booleanValue();

        p.isQuenchingPhase = ((BooleanParameter) params[12]).getBoolean()
                .booleanValue();

        if (auswahl.getSelectedValue().equals(AbstractSEForce.NODE_SIZE)) {
            p.isGridVariant = false;
            p.isCalculationWithNodeSizes = true;
            p.isNodesGrowing = false;
        } else if (auswahl.getSelectedValue().equals(
                AbstractSEForce.NODE_SIZE_GROWING)) {
            p.isGridVariant = false;
            p.isCalculationWithNodeSizes = true;
            p.isNodesGrowing = true;
        } else if (auswahl.getSelectedValue().equals(
                AbstractSEForce.NO_GV_NO_NODE_SIZE)) {
            p.isGridVariant = false;
            p.isCalculationWithNodeSizes = false;
            p.isNodesGrowing = false;
        }

        p.magneticFieldParameter = (StringSelectionParameter) params[13];

        if (!p.magneticFieldParameter.getSelectedValue().equals(
                AbstractSEForce.NO_MAGNETIC_FIELD)) {

            listOfForces.add(new MagneticForce(p));
        }
        p.magneticSpringConstant = ((DoubleParameter) params[14]).getDouble();
    }

    /**
     * Checks the preconditions (no empty graph, not simultaneous nodesGrowing
     * and not calculationWithNodeSizes variants)
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

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    @Override
    public String getName() {
        return "Preserving Edge Crossing Properties (parameters adjustable)";
    }

    /**
     * Returns the sector, which the vector v intersects. The sectors are
     * specified by int values between 0 and 8.
     * 
     * @param v
     *            The given vector.
     * @return Sector of v's direction
     */
    private int getSector(GeometricalVector v) {
        double x = v.getX();
        double y = v.getY();

        double absX = Math.abs(x);
        double absY = Math.abs(y);

        // 1. quadrant right, down because of the y-axis in down-direction
        if (x >= 0 && y >= 0) {
            // 0. octant
            if (absX >= absY)
                return 0;
            else
                return 1;
        }
        // 2. quadrant (left,top)
        else if (x < 0 && y >= 0) {

            // 2. octant
            if (absX < absY)
                return 2;
            else
                return 3;

        }
        // 3. quadrant (left, down)
        else if (x < 0 && y < 0) {

            // 4. octant
            if (absX > absY)
                return 4;
            else
                return 5;

        }
        // 4. quadrant (right, down)
        else
        // (x >= 0 && y < 0)
        {
            // 6. octant
            if (absX < absY)
                return 6;
            else
                return 7;

        }

    }

    /**
     * The needed forces are calculated
     */
    @Override
    protected void calculateForces() {
        super.calculateForces();

        // calculate the zones surrounding the nodes, to preserve the edge
        // crossing properties
        calcZoneValues();
    }

    /**
     * Calculates the new zone values. Every pair of node-edge is considered.
     * 
     */
    private void calcZoneValues() {
        // all nodes of the graph
        for (FRNode node : p.fRGraph.getFRNodes()) {
            // the vector of the node position
            GeometricalVector nodeVector = new GeometricalVector(node);

            // all edges of the graph
            for (FREdge edge : p.fRGraph.getFREdges()) {

                // node ist nicht source oder target von edge
                if (edge.getSource() != node && edge.getTarget() != node) {
                    // the vector of the source position
                    GeometricalVector sourceVector = new GeometricalVector(edge
                            .getSource().getXPos(), edge.getSource().getYPos());

                    // the vector of the target position
                    GeometricalVector targetVector = new GeometricalVector(edge
                            .getTarget().getXPos(), edge.getTarget().getYPos());

                    // the vector of the i_node position (Bertault)
                    GeometricalVector i_node = AbstractSEForce
                            .getOrthogonalIntersectionPoint(sourceVector,
                                    targetVector, nodeVector);

                    // distance between i_node and node
                    double distanceI_nodeToNodeVector = GeometricalVector
                            .getDistance(i_node, nodeVector);

                    boolean i_nodeIsOnEdge = GeometricalVector
                            .isPointBetweenSourceAndTarget(i_node,
                                    sourceVector, targetVector);

                    // i_node befindet sich auf der Kante edge
                    if (i_nodeIsOnEdge) {

                        GeometricalVector nodeToI_node = GeometricalVector
                                .subtract(i_node, nodeVector);

                        // sektor der von node nach i_node geschnitten wird
                        int actualSector = getSector(nodeToI_node);

                        // sets the specified sector radii of node
                        for (int sector = actualSector - 2; sector <= actualSector + 2; sector++) {
                            // sectorradius (Bertault)
                            double sectorRadius = Math.min(
                                    distanceI_nodeToNodeVector / 3, node
                                            .getZone().getSectorRadius(sector));
                            node.getZone().setRadiusOfSector(sector,
                                    sectorRadius);
                        }

                        // sets the specified sector radii of source and target
                        for (int sector = actualSector + 2; sector <= actualSector + 6; sector++) {

                            double sectorRadiusSource = Math.min(
                                    distanceI_nodeToNodeVector / 3, edge
                                            .getSource().getZone()
                                            .getSectorRadius(sector));

                            edge.getSource().getZone().setRadiusOfSector(
                                    sector, sectorRadiusSource);

                            double sectorRadiusTarget = Math.min(
                                    distanceI_nodeToNodeVector / 3, edge
                                            .getTarget().getZone()
                                            .getSectorRadius(sector));

                            edge.getTarget().getZone().setRadiusOfSector(
                                    sector, sectorRadiusTarget);
                        }
                    }
                    // i_node ist nicht auf der kante edge
                    else {
                        // variables nominated after Bertault
                        double a_v = GeometricalVector.getDistance(
                                sourceVector, nodeVector);
                        double b_v = GeometricalVector.getDistance(
                                targetVector, nodeVector);

                        // sets the specified radii of node
                        for (int sector = 0; sector < 8; sector++) {

                            double sectorRadius = Math.min(node.getZone()
                                    .getSectorRadius(sector), Math
                                    .min(a_v, b_v) / 3.0);

                            node.getZone().setRadiusOfSector(sector,
                                    sectorRadius);
                        }
                        // sets the specified radii of source and target
                        for (int sector = 0; sector < 8; sector++) {

                            double sectorRadiusSource = Math.min(a_v / 3, edge
                                    .getSource().getZone().getSectorRadius(
                                            sector));

                            double sectorRadiusTarget = Math.min(b_v / 3, edge
                                    .getTarget().getZone().getSectorRadius(
                                            sector));

                            edge.getSource().getZone().setRadiusOfSector(
                                    sector, sectorRadiusSource);
                            edge.getTarget().getZone().setRadiusOfSector(
                                    sector, sectorRadiusTarget);
                        }
                    }
                }
            }
        }
    }

    /**
     * Executes the moving of the given node.
     * 
     */
    @Override
    protected FRNode moveNodeOriginal(FRNode nodeToMove) {
        // to know in which gridsquare the node is
        double oldPosX = nodeToMove.getXPos();
        double oldPosY = nodeToMove.getYPos();
        GeometricalVector forceVector = nodeToMove.getSumOfForces();

        int sector = getSector(forceVector);

        // limits force vector by temperature
        // forceVector = limitForceVectorByTemperature(forceVector,
        // temperature);

        double localTemperature = 1.0d;
        if (p.isLocalTemperature) {
            nodeToMove.setLocalTemperature(forceVector);
            localTemperature = nodeToMove.getLocalTemperature();
        }

        forceVector.setGeometricalVector(forceVector.getX() * localTemperature,
                forceVector.getY() * localTemperature);

        double sectorRadius = nodeToMove.getZone().getSectorRadius(sector);
        double forceVectorLength = GeometricalVector.getLength(forceVector);

        // if sector radius is smaller than the real force vector, then
        // cut the force vector to the length of the sector radius
        if (sectorRadius < forceVectorLength) {

            forceVector.setGeometricalVector(forceVector.getX() * sectorRadius
                    / forceVectorLength, forceVector.getY() * sectorRadius
                    / forceVectorLength);

        }

        forceVector = limitForceVectorByTemperature(forceVector, temperature);

        // only move nodeToMove, if the node is movable
        if (nodeToMove.isMovable()) {
            nodeToMove.setXPos(nodeToMove.getXPos() + forceVector.getX());
            nodeToMove.setYPos(nodeToMove.getYPos() + forceVector.getY());
        }

        // after the node is moved, reset the force
        nodeToMove.resetForces();

        // move node to another gridsquare, if necessary
        if (p.isGridVariant) {
            p.grid.movedNodeInGrid(nodeToMove, oldPosX, oldPosY);
        }

        return nodeToMove;
    }

    /**
     * Executes the moving of the given node after Coleman&Parker (AGLO
     * approach).
     * 
     */
    @Override
    protected void moveNode(FRNode nodeToMove, double lengthOfMovement) {
        // to know in which gridsquare the node is
        double oldPosX = nodeToMove.getXPos();
        double oldPosY = nodeToMove.getYPos();
        GeometricalVector forceVector = nodeToMove.getSumOfForces();

        int sector = getSector(forceVector);

        // limits force vector by temperature
        forceVector = limitForceVectorByTemperature(forceVector, temperature);

        // System.out.println(nodeToMove.printDifferentForces());

        double localTemperature = 1.0d;
        if (p.isLocalTemperature) {
            nodeToMove.setLocalTemperature(forceVector);
            localTemperature = nodeToMove.getLocalTemperature();
        }

        forceVector.setGeometricalVector(forceVector.getX() * localTemperature,
                forceVector.getY() * localTemperature);

        double sectorRadius = nodeToMove.getZone().getSectorRadius(sector);
        double forceVectorLength = GeometricalVector.getLength(forceVector);

        // if sector radius is smaller than the real force vector, then
        // cut the force vector to the length of the sector radius
        if (sectorRadius < forceVectorLength) {
            forceVector.setGeometricalVector(forceVector.getX() * sectorRadius
                    / forceVectorLength, forceVector.getY() * sectorRadius
                    / forceVectorLength);
        }

        // only move nodeToMove, if the node is movable
        if (nodeToMove.isMovable()) {
            nodeToMove.setXPos((nodeToMove.getXPos() + (forceVector.getX())));
            nodeToMove.setYPos((nodeToMove.getYPos() + (forceVector.getY())));
        }

        // after the node is moved, reset the force
        nodeToMove.resetForces();

        // move node to another gridsquare, if necessary
        if (p.isGridVariant) {
            p.grid.movedNodeInGrid(nodeToMove, oldPosX, oldPosY);
        }
    }
}
