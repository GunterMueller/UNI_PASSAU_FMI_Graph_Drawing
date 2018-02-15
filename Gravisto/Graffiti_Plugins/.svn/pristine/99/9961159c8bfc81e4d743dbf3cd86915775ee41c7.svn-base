package org.graffiti.plugins.algorithms.labeling;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeSet;

import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.DoubleParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.labeling.finitePositionsIndividualWeighting.FinitePositionsAlgorithmIndividualWeighting;
import org.graffiti.plugins.algorithms.labeling.finitePositionsIndividualWeighting.LabelCandidateCollisionStructure;
import org.graffiti.plugins.algorithms.labeling.finitePositionsIndividualWeighting.LabelLocator;
import org.graffiti.plugins.algorithms.labeling.finitePositionsIndividualWeighting.NaiveLabelCandidateCollisionStructure;
import org.graffiti.plugins.algorithms.labeling.finitePositionsIndividualWeighting.Statistics;
import org.graffiti.plugins.algorithms.labeling.finitePositionsIndividualWeighting.ViewNotSupportedException;
import org.graffiti.plugins.algorithms.labeling.forces.AbstractSEForce;
import org.graffiti.plugins.algorithms.labeling.forces.AttractiveAdjacentNodesForce;
import org.graffiti.plugins.algorithms.labeling.forces.EdgeLabelRepulsionForce;
import org.graffiti.plugins.algorithms.labeling.forces.GravityForce;
import org.graffiti.plugins.algorithms.labeling.forces.LabelEdgesRepulsionForce;
import org.graffiti.plugins.algorithms.labeling.forces.MagneticForce;
import org.graffiti.plugins.algorithms.labeling.forces.NodeLabelAdjacentEdgesRepulsionForce;
import org.graffiti.plugins.algorithms.labeling.forces.RepulsiveNodeEdgeForce;
import org.graffiti.plugins.algorithms.labeling.forces.RepulsiveNodeNodeForce;
import org.graffiti.selection.Selection;

public class FinitePositionsAndSpringEmbedderAlgorithm extends
        FRSpringLabelingAlgorithmStandard {

    /**
     * Parameter for number of quenching iterations
     */
    protected IntegerParameter nrQuenchingParam;

    /**
     * Parameter for start temperature at quenching phase
     */
    protected DoubleParameter startQuenchingTempParam;

    /**
     * Parameter for end temperature at quenching phase
     */
    protected DoubleParameter endQuenchingTempParam;

    /**
     * Parameter for number of simmering iterations
     */
    protected IntegerParameter nrSimmeringParam;

    /**
     * Parameter for start temperature at simmering phase
     */
    protected DoubleParameter startSimmeringTempParam;

    /**
     * Parameter for end temperature at simmering phase
     */
    protected DoubleParameter endSimmeringTempParam;

    /**
     * Parameter for the distance between nodes (specified by user)
     */
    protected DoubleParameter nodeDistParam;

    // Labeling parameters - see SELabelingAlgorithmParameters for explanation
    protected DoubleParameter attractionConstantToLabelParam;
    protected DoubleParameter repulsionConstantNodeToLabelParam;
    protected DoubleParameter repulsionConstantBothLabelsParam;
    protected DoubleParameter optimalNodeDistanceToLabelParam;
    protected BooleanParameter isEdgeLabelNodeRepulsionForceParam;
    protected BooleanParameter isLabelNodeEdgeRepulsionParam;
    protected BooleanParameter isNodeLabelAdjacentEdgesRepulsionParam;
    protected DoubleParameter edgeLabelForceRepulsionMaxParam;
    protected DoubleParameter edgeLabelForceSoftOuterBorderParam;
    protected DoubleParameter edgeLabelForceSoftInnerBorderParam;
    protected DoubleParameter repulsionConstantLabelNodeEdgeParam;
    protected DoubleParameter NodeLabelAdjacentEdgesRepulsionMaxForceParam;
    protected DoubleParameter NodeLabelAdjacentEdgesRepulsionForceSoftInnerBorderParam;

    // Labeling parameters - finite positions
    private IntegerParameter numberOfCandidatePositionsParam;
    private int numberOfCandidatePositions = 8;

    private BooleanParameter isUseOriginalLabelPositionParam;
    private boolean isUseOriginalLabelPosition = false;

    private BooleanParameter penalizeOverlapsWithOutgoingEdgesParam;
    private boolean penalizeOverlapsWithOutgoingEdges = true;

    private BooleanParameter penalizeOverlapsWithAnyEdgesParam;
    private boolean penalizeOverlapsWithAnyEdges = true;

    private BooleanParameter penalizeOverlapsWithNodesParam;
    private boolean penalizeOverlapsWithNodes = true;

    private DoubleParameter interLabelGapParam;
    private double interLabelGap = 1d;

    private DoubleParameter labelOverlapWeightParam;
    static double labelOverlapWeight = 12d;
    private DoubleParameter nodeOverlapWeightParam;
    static double nodeOverlapWeight = 10d;
    private DoubleParameter edgeOverlapWeightParam;
    static double edgeOverlapWeight = 4d;
    private DoubleParameter candidateOverlapWeightParam;
    static double candidateOverlapWeight = 2d;
    private DoubleParameter positionPreferenceWeightParam;
    static double positionPreferenceWeight = 1d;

    /**
     * Parameter for the choice of the variant (specified by user)
     */
    protected static StringSelectionParameter auswahl;

    /**
     * Constructs a new instance of an Spring Embedder Algorithm, where the user
     * can specify many parameters.
     * 
     */
    public FinitePositionsAndSpringEmbedderAlgorithm() {
        super();

        // Alternative parameters
        selection = new Selection(); // will be empty, as not supported
        p.quenchingIterations = 0;

        // Regular parameters

        nrQuenchingParam = new IntegerParameter(p.quenchingIterations,
                "number of iterations in quenching phase",
                "Sets the number of iterations during quenching phase.", 0,
                200, 0, Integer.MAX_VALUE);

        startQuenchingTempParam = new DoubleParameter(
                "start temperature of quenching phase",
                "Sets the start temperature of the quenching phase.",
                new Double(0.0), new Double(100000));
        startQuenchingTempParam.setDouble(p.quenchingTemperatureStart);

        endQuenchingTempParam = new DoubleParameter(
                "end temperature of quenching phase",
                "Sets the end temperature of the quenching phase.", new Double(
                        0.0), new Double(1000));
        endQuenchingTempParam.setDouble(p.quenchingTemperatureEnd);

        nrSimmeringParam = new IntegerParameter(p.simmeringIterations,
                "number of iterations in simmering phase",
                "Sets the start temperature of the simmering phase.", 0, 200,
                0, Integer.MAX_VALUE);

        startSimmeringTempParam = new DoubleParameter(
                "start temperature of simmering phase",
                "Sets the start temperature of the simmering phase.",
                new Double(0.0), new Double(1000));
        startSimmeringTempParam.setDouble(p.simmeringTemperatureStart);

        endSimmeringTempParam = new DoubleParameter(
                "end temperature of simmering phase",
                "Sets the end temperature of the simmering phase.", new Double(
                        0.0), new Double(100.0));
        endSimmeringTempParam.setDouble(p.simmeringTemperatureEnd);

        nodeDistParam = new DoubleParameter("ideal node distance",
                "Sets the ideal node distance in algorithm.", new Double(10.0),
                new Double(100.0));
        nodeDistParam.setDouble(Math.round(p.optimalNodeDistance * 100) / 100d);

        // Labeling parameters

        attractionConstantToLabelParam = new DoubleParameter(
                p.attractionConstantToLabel,
                "parent node -> label attraction",
                "Scaling factor for the parent node -> label attraction force.",
                0d, 100d);
        repulsionConstantNodeToLabelParam = new DoubleParameter(
                p.repulsionConstantNodeToLabel,
                "parent node -> label repulsion",
                "Scaling factor for the parent node -> label repulsion force.",
                0d, 100d);
        repulsionConstantBothLabelsParam = new DoubleParameter(
                p.repulsionConstantBothLabels, "label -> label repulsion",
                "Scaling factor for the label -> label repulsion force.", 0d,
                200d);
        optimalNodeDistanceToLabelParam = new DoubleParameter(
                p.optimalNodeDistanceToLabel,
                "parent node -> node label: optimal distance",
                "Desired distance between node labels and corresponding nodes.",
                0d, 40d, 0d, Double.MAX_VALUE);

        isEdgeLabelNodeRepulsionForceParam = new BooleanParameter(
                p.isEdgeLabelNodeRepulsionForce,
                "parent edge -> edge label repulsion",
                "Forces edge labels off parent edges at close range.");
        edgeLabelForceRepulsionMaxParam = new DoubleParameter(
                p.edgeLabelForceRepulsionMax,
                "parent edge -> edge label: max force",
                "Scaling factor for the parent edge -> edge label force.", 0d,
                100d);
        edgeLabelForceSoftOuterBorderParam = new DoubleParameter(
                p.edgeLabelForceSoftOuterBorder,
                "parent edge -> edge label: soft outer border",
                "Desired distance between edge label and corresponding edge.",
                1d, 20d, 1d, 50d);
        edgeLabelForceSoftInnerBorderParam = new DoubleParameter(
                p.edgeLabelForceSoftInnerBorder,
                "parent edge -> edge label: soft inner border",
                "Punishes minor edge overlaps less severely.", 0d, 10d, 0d, 25d);

        isLabelNodeEdgeRepulsionParam = new BooleanParameter(
                p.isLabelNodeEdgeRepulsion, "edge -> label repulsion",
                "Repulsion force between not parent edges and labels");
        repulsionConstantLabelNodeEdgeParam = new DoubleParameter(
                p.repulsionConstantLabelNodeEdge,
                "edge -> label repulsion constant",
                "Constant to normalize the repulsion between label nodes and edges.",
                0d, 100d);

        isNodeLabelAdjacentEdgesRepulsionParam = new BooleanParameter(
                p.isNodeLabelAdjacentEdgesRepulsion,
                "adjacent edges -> node label: repulsion",
                "Circular repulsion force between outgoing edges of parent nodes "
                        + "and label nodes");
        NodeLabelAdjacentEdgesRepulsionMaxForceParam = new DoubleParameter(
                p.NodeLabelAdjacentEdgesRepulsionMaxForce,
                "adjacent edges -> node label repulsion: max force",
                "Scaling factor for the node label - emerging edges repulsion force.",
                0d, 10d);
        NodeLabelAdjacentEdgesRepulsionForceSoftInnerBorderParam = new DoubleParameter(
                p.NodeLabelAdjacentEdgesRepulsionForceSoftInnerBorder,
                "adjacent edges -> node label repulsion: soft inner border",
                "Punishes minor edge overlaps less severely.", 0d, 10d, 0d, 25d);

        // Labeling parameters - finite positions

        this.isUseOriginalLabelPositionParam = new BooleanParameter(
                isUseOriginalLabelPosition,
                "consider original label positions",
                "creates a candidate position at the current position of a label");
        this.numberOfCandidatePositionsParam = new IntegerParameter(
                numberOfCandidatePositions, "number of generated candidates",
                "number of additionally created candidate positions per label",
                0, 8, 0, Integer.MAX_VALUE);
        this.penalizeOverlapsWithOutgoingEdgesParam = new BooleanParameter(
                penalizeOverlapsWithOutgoingEdges,
                "node labels: avoid overlaps with outgoing edges",
                "penalizes node label positions that overlap with outgoing edges");
        this.penalizeOverlapsWithAnyEdgesParam = new BooleanParameter(
                penalizeOverlapsWithAnyEdges, "avoid overlaps with edges",
                "penalizes label positions that overlap with edges");
        this.penalizeOverlapsWithNodesParam = new BooleanParameter(
                penalizeOverlapsWithNodes, "avoid overlaps with nodes",
                "penalizes label positions that overlap with nodes");
        this.interLabelGapParam = new DoubleParameter(interLabelGap,
                "inter label gab",
                "additional space required by each label to not be treated "
                        + "as overlapping another label", -5d, 20d);

        this.labelOverlapWeightParam = new DoubleParameter(labelOverlapWeight,
                "label overlap weight",
                "position quality malus for each overlap with a placed label",
                0d, 20d);
        this.nodeOverlapWeightParam = new DoubleParameter(nodeOverlapWeight,
                "node overlap weight",
                "position quality malus for each overlap with a node", 0d, 20d);
        this.edgeOverlapWeightParam = new DoubleParameter(edgeOverlapWeight,
                "edge overlap weight",
                "position quality malus for each overlap with an edge", 0d, 20d);
        this.candidateOverlapWeightParam = new DoubleParameter(
                candidateOverlapWeight, "candidate overlap weight",
                "position quality malus for each overlap "
                        + "with a label position candidate", 0d, 20d);
        this.positionPreferenceWeightParam = new DoubleParameter(
                positionPreferenceWeight, "position preference weight",
                "position quality bonus range for orientation preferences", 0d,
                20d);

    }

    /**
     * Returns the Name of the Algorithm
     */
    @Override
    public String getName() {
        return "Finite Positions & Spring Embedder";
    }

    /**
     * Returns an array with the parameters of this algorithm
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {

        // BooleanParameter isCalculationIdealLengthsParam =
        new BooleanParameter(
                p.isCalculationIdealLengths,
                "algorithm calculates ideal lengths?",
                "If set, algorithms calculates ideal lengths (e.g. node-node distance, edge lengths).");

        BooleanParameter localTempParam = new BooleanParameter(
                p.isLocalTemperature, "calculation with local temperatures?",
                "Is set, if algorithm has to calculate the drawing with local temperatures.");

        // BooleanParameter gravityParam =
        new BooleanParameter(p.isGravity, "calcualtion with gravity?",
                "Is set, if algorithm should calculate with gravity.");

        // IntegerParameter gravityConstantParam =
        new IntegerParameter(p.gravityConstant, "strength of gravity",
                "Sets the strength of gravity.");

        String[] arr = new String[] { AbstractSEForce.NO_GV_NO_NODE_SIZE,
                AbstractSEForce.GV, AbstractSEForce.NODE_SIZE,
                AbstractSEForce.NODE_SIZE_GROWING,
                AbstractSEForce.GV_NODE_SIZE,
                AbstractSEForce.GV_NODE_SIZE_GROWING };
        StringSelectionParameter variantsParam = new StringSelectionParameter(
                arr, "variants of algorithm",
                "The algorithm uses the chosen variant for calculation.");
        variantsParam.setValue(AbstractSEForce.NODE_SIZE);

        // BooleanParameter isGlobalTempOrigFRParam =
        new BooleanParameter(
                p.isGlobalTempConceptFR,
                "original temperature clipping method (FR)",
                "Set true, then force vector cipping method is used in calculation, else false ");

        // BooleanParameter isQuenchingPhaseParam =
        new BooleanParameter(p.isQuenchingPhase, "with quenching phase?",
                "If set, algorithm is executed with quenching phase.");

        // BooleanParameter isCalculationWithNodesAsRectanglesParam =
        new BooleanParameter(p.isCalculationWithNodesAsRectangles,
                "calculation with nodes as rect?",
                "If set, calculation with nodes as rect.");

        // BooleanParameter isCalculationWangMiyamotoParam =
        new BooleanParameter(p.isCalculationWangMiyamoto,
                "calculation wang/miyamoto variant?",
                "If set, calculation with wang/miyamoto variant.");

        // BooleanParameter isCalculationWithNodeEdgeRepulsionParam =
        new BooleanParameter(p.isCalculationWithNodeEdgeRepulsion,
                "node edge repulsion?",
                "If set, calculation with node edge repulsion (avoids node edge intersection).");

        String[] magnArr = new String[] { AbstractSEForce.NO_MAGNETIC_FIELD,
                AbstractSEForce.NORTH, AbstractSEForce.SOUTH,
                AbstractSEForce.WEST, AbstractSEForce.EAST,
                AbstractSEForce.POLAR, AbstractSEForce.CONCENTRIC_CLOCK };
        StringSelectionParameter magnVariantsParam = new StringSelectionParameter(
                magnArr, "direction of the magnetic field",
                "Determines the direction of the magnetic field.");
        magnVariantsParam.setValue(AbstractSEForce.NO_MAGNETIC_FIELD);

        DoubleParameter magneticSpringConstantParam = new DoubleParameter(
                p.magneticSpringConstant, "magnetic field strength",
                "Strength of the magnetic field");

        return new Parameter[] {
                numberOfCandidatePositionsParam,
                isUseOriginalLabelPositionParam,
                penalizeOverlapsWithOutgoingEdgesParam,
                penalizeOverlapsWithAnyEdgesParam,
                penalizeOverlapsWithNodesParam,
                interLabelGapParam,
                labelOverlapWeightParam,
                nodeOverlapWeightParam,
                edgeOverlapWeightParam,
                candidateOverlapWeightParam,
                positionPreferenceWeightParam,
                this.viewAlgoritmStepsParam,
                // this.nrQuenchingParam,
                // this.startQuenchingTempParam,
                // this.endQuenchingTempParam,
                this.nrSimmeringParam, this.startSimmeringTempParam,
                this.endSimmeringTempParam, localTempParam, variantsParam,
                magnVariantsParam, magneticSpringConstantParam,
                attractionConstantToLabelParam,
                repulsionConstantNodeToLabelParam,
                repulsionConstantBothLabelsParam,
                optimalNodeDistanceToLabelParam,
                isEdgeLabelNodeRepulsionForceParam,
                edgeLabelForceRepulsionMaxParam,
                edgeLabelForceSoftOuterBorderParam,
                edgeLabelForceSoftInnerBorderParam,
                isLabelNodeEdgeRepulsionParam,
                repulsionConstantLabelNodeEdgeParam,
                isNodeLabelAdjacentEdgesRepulsionParam,
                NodeLabelAdjacentEdgesRepulsionMaxForceParam,
                NodeLabelAdjacentEdgesRepulsionForceSoftInnerBorderParam };
    }

    /**
     * Sets the parameters, the user has specified
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;

        numberOfCandidatePositions = ((IntegerParameter) params[0])
                .getInteger().intValue();
        isUseOriginalLabelPosition = ((BooleanParameter) params[1])
                .getBoolean().booleanValue();
        penalizeOverlapsWithNodes = ((BooleanParameter) params[4]).getBoolean()
                .booleanValue();
        penalizeOverlapsWithAnyEdges = ((BooleanParameter) params[3])
                .getBoolean().booleanValue();
        penalizeOverlapsWithOutgoingEdges = ((BooleanParameter) params[2])
                .getBoolean().booleanValue()
                && !penalizeOverlapsWithAnyEdges; // if any edges is set, then
                                                  // not
        interLabelGap = ((DoubleParameter) params[5]).getDouble().doubleValue();
        labelOverlapWeight = ((DoubleParameter) params[6]).getDouble()
                .doubleValue();
        nodeOverlapWeight = ((DoubleParameter) params[7]).getDouble()
                .doubleValue();
        edgeOverlapWeight = ((DoubleParameter) params[8]).getDouble()
                .doubleValue();
        candidateOverlapWeight = ((DoubleParameter) params[9]).getDouble()
                .doubleValue();
        positionPreferenceWeight = ((DoubleParameter) params[10]).getDouble()
                .doubleValue();

        viewAlgorithmSteps = ((BooleanParameter) params[11]).getBoolean()
                .booleanValue();
        // p.quenchingIterations = ((IntegerParameter)params[6]).getInteger()
        // .intValue();
        // p.quenchingTemperatureStart =
        // ((DoubleParameter)params[7]).getDouble()
        // .doubleValue();
        // p.quenchingTemperatureEnd = ((DoubleParameter)params[8]).getDouble()
        // .doubleValue();
        p.simmeringIterations = ((IntegerParameter) params[12]).getInteger()
                .intValue();
        p.simmeringTemperatureStart = ((DoubleParameter) params[13])
                .getDouble().doubleValue();
        p.simmeringTemperatureEnd = ((DoubleParameter) params[14]).getDouble()
                .doubleValue();

        p.isLocalTemperature = ((BooleanParameter) params[15]).getBoolean()
                .booleanValue();

        auswahl = (StringSelectionParameter) params[16];

        listOfForces = new LinkedList<AbstractSEForce>();

        if (p.isGravity) {
            listOfForces.add(new GravityForce(p));
        }
        listOfForces.add(new RepulsiveNodeNodeForce(p));
        listOfForces.add(new AttractiveAdjacentNodesForce(p));

        // calculation with node edge repulsive forces
        if (p.isCalculationWithNodeEdgeRepulsion) {
            listOfForces.add(new RepulsiveNodeEdgeForce(p));
        }

        if (auswahl.getSelectedValue().equals(AbstractSEForce.GV)) {
            p.isGridVariant = true;
            p.isCalculationWithNodeSizes = false;
            p.isNodesGrowing = false;
        } else if (auswahl.getSelectedValue().equals(AbstractSEForce.NODE_SIZE)) {
            p.isGridVariant = false;
            p.isCalculationWithNodeSizes = true;
            p.isNodesGrowing = false;
        } else if (auswahl.getSelectedValue().equals(
                AbstractSEForce.GV_NODE_SIZE)) {
            p.isGridVariant = true;
            p.isCalculationWithNodeSizes = true;
            p.isNodesGrowing = false;
        } else if (auswahl.getSelectedValue().equals(
                AbstractSEForce.NODE_SIZE_GROWING)) {
            p.isGridVariant = false;
            p.isCalculationWithNodeSizes = true;
            p.isNodesGrowing = true;
        } else if (auswahl.getSelectedValue().equals(
                AbstractSEForce.GV_NODE_SIZE_GROWING)) {
            p.isGridVariant = true;
            p.isCalculationWithNodeSizes = true;
            p.isNodesGrowing = true;
        } else if (auswahl.getSelectedValue().equals(
                AbstractSEForce.NO_GV_NO_NODE_SIZE)) {
            p.isGridVariant = false;
            p.isCalculationWithNodeSizes = false;
            p.isNodesGrowing = false;
        }

        final int paramNr = 17;
        p.magneticFieldParameter = (StringSelectionParameter) params[paramNr];

        if (!p.magneticFieldParameter.getSelectedValue().equals(
                AbstractSEForce.NO_MAGNETIC_FIELD)) {

            listOfForces.add(new MagneticForce(p));
        }
        p.magneticSpringConstant = ((DoubleParameter) params[paramNr + 1])
                .getDouble();

        // Labeling params
        p.attractionConstantToLabel = ((DoubleParameter) params[paramNr + 2])
                .getDouble().doubleValue();
        p.repulsionConstantNodeToLabel = ((DoubleParameter) params[paramNr + 3])
                .getDouble().doubleValue();
        p.repulsionConstantBothLabels = ((DoubleParameter) params[paramNr + 4])
                .getDouble().doubleValue();
        p.optimalNodeDistanceToLabel = ((DoubleParameter) params[paramNr + 5])
                .getDouble().doubleValue();
        p.isEdgeLabelNodeRepulsionForce = ((BooleanParameter) params[paramNr + 6])
                .getBoolean().booleanValue();
        p.edgeLabelForceRepulsionMax = ((DoubleParameter) params[paramNr + 7])
                .getDouble().doubleValue();
        p.edgeLabelForceSoftOuterBorder = ((DoubleParameter) params[paramNr + 8])
                .getDouble().doubleValue();
        p.edgeLabelForceSoftInnerBorder = ((DoubleParameter) params[paramNr + 9])
                .getDouble().doubleValue();
        p.isLabelNodeEdgeRepulsion = ((BooleanParameter) params[paramNr + 10])
                .getBoolean().booleanValue();
        p.repulsionConstantLabelNodeEdge = ((DoubleParameter) params[paramNr + 11])
                .getDouble().doubleValue();
        p.isNodeLabelAdjacentEdgesRepulsion = ((BooleanParameter) params[paramNr + 12])
                .getBoolean().booleanValue();
        p.NodeLabelAdjacentEdgesRepulsionMaxForce = ((DoubleParameter) params[paramNr + 13])
                .getDouble().doubleValue();
        p.NodeLabelAdjacentEdgesRepulsionForceSoftInnerBorder = ((DoubleParameter) params[paramNr + 14])
                .getDouble().doubleValue();

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
    }

    @Override
    public void execute() {

        // 1ST: RUN FINITE POSITIONS ALGORITHM

        // Flush statistics (as different runs share the same algorithm)
        Statistics.reset();
        long nanoTime = System.nanoTime();

        // Step 1: extract position candidates for all labels

        // contains a label locator for every label of the graph
        ArrayList<LabelLocator> locators;

        try {
            locators = FinitePositionsAlgorithmIndividualWeighting
                    .generateLabelPositionCandidates(graph,
                            numberOfCandidatePositions,
                            penalizeOverlapsWithOutgoingEdges,
                            isUseOriginalLabelPosition);
        } catch (ViewNotSupportedException e) {
            throw new RuntimeException(e.toString()
                    + " - algorithm execution stopped ");
        }

        // Step 2: add label collision information

        LabelCandidateCollisionStructure collisionStructure = new NaiveLabelCandidateCollisionStructure(
                locators, graph, penalizeOverlapsWithNodes,
                penalizeOverlapsWithAnyEdges, interLabelGap);

        // contains label locators, which have candidate positions without
        // collisions.
        TreeSet<LabelLocator> sortedLocators;

        sortedLocators = collisionStructure.getSortedLocators();

        // Step 3: mount labels to appropriate positions
        FinitePositionsAlgorithmIndividualWeighting
                .applyLabelPositions(sortedLocators);

        // 2ND: RUN SPRING EMBEDDER
        super.execute();

        // Statistics
        nanoTime = System.nanoTime() - nanoTime;
        System.out.println("Total running time: " + (nanoTime / 1000000000d)
                + "s");
    }
}
