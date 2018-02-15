package org.graffiti.plugins.algorithms.labeling;

import java.util.LinkedList;

import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.DoubleParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
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

/**
 * An implementation of Fruchtermann's and Reingold's spring embedder, adjusted
 * to the needs of positioning labels.
 * 
 * Adapted from:
 * <p>
 * An advanced SpringEmbedder (Fruchterman & Reingold) <br>
 * from matzeder
 * 
 * @author scholz
 */
public class FRSpringLabelingAlgorithmAllParams extends
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

    /**
     * Parameter for the choice of the variant (specified by user)
     */
    protected static StringSelectionParameter auswahl;

    /**
     * Constructs a new instance of an Spring Embedder Algorithm, where the user
     * can specify many parameters.
     * 
     */
    public FRSpringLabelingAlgorithmAllParams() {
        super();
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
    }

    /**
     * Returns the Name of the Algorithm
     */
    @Override
    public String getName() {
        return "Spring Embedder (all parameters adjustable)";
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

        BooleanParameter isCalculationIdealLengthsParam = new BooleanParameter(
                p.isCalculationIdealLengths,
                "algorithm calculates ideal lengths?",
                "If set, algorithms calculates ideal lengths (e.g. node-node distance, edge lengths).");

        BooleanParameter localTempParam = new BooleanParameter(
                p.isLocalTemperature, "calculation with local temperatures?",
                "Is set, if algorithm has to calculate the drawing with local temperatures.");

        BooleanParameter gravityParam = new BooleanParameter(p.isGravity,
                "calcualtion with gravity?",
                "Is set, if algorithm should calculate with gravity.");

        IntegerParameter gravityConstantParam = new IntegerParameter(
                p.gravityConstant, "strength of gravity",
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

        BooleanParameter isGlobalTempOrigFRParam = new BooleanParameter(
                p.isGlobalTempConceptFR,
                "original temperature clipping method (FR)",
                "Set true, then force vector cipping method is used in calculation, else false ");

        BooleanParameter isQuenchingPhaseParam = new BooleanParameter(
                p.isQuenchingPhase, "with quenching phase?",
                "If set, algorithm is executed with quenching phase.");

        BooleanParameter isCalculationWithNodesAsRectanglesParam = new BooleanParameter(
                p.isCalculationWithNodesAsRectangles,
                "calculation with nodes as rect?",
                "If set, calculation with nodes as rect.");

        BooleanParameter isCalculationWangMiyamotoParam = new BooleanParameter(
                p.isCalculationWangMiyamoto,
                "calculation wang/miyamoto variant?",
                "If set, calculation with wang/miyamoto variant.");

        BooleanParameter isCalculationWithNodeEdgeRepulsionParam = new BooleanParameter(
                p.isCalculationWithNodeEdgeRepulsion, "node edge repulsion?",
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

        return new Parameter[] { selParam, this.viewAlgoritmStepsParam,
                this.nrQuenchingParam, this.startQuenchingTempParam,
                this.endQuenchingTempParam, this.nrSimmeringParam,
                this.startSimmeringTempParam, this.endSimmeringTempParam,
                isCalculationIdealLengthsParam, nodeDistParam, localTempParam,
                gravityParam, gravityConstantParam, variantsParam,
                isGlobalTempOrigFRParam, isQuenchingPhaseParam,
                isCalculationWithNodesAsRectanglesParam,
                isCalculationWangMiyamotoParam,
                isCalculationWithNodeEdgeRepulsionParam, magnVariantsParam,
                magneticSpringConstantParam, attractionConstantToLabelParam,
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

        p.isLocalTemperature = ((BooleanParameter) params[10]).getBoolean()
                .booleanValue();
        p.isGravity = ((BooleanParameter) params[11]).getBoolean()
                .booleanValue();

        p.gravityConstant = ((IntegerParameter) params[12]).getInteger()
                .intValue();

        auswahl = (StringSelectionParameter) params[13];
        p.isGlobalTempConceptFR = ((BooleanParameter) params[14]).getBoolean()
                .booleanValue();
        p.isQuenchingPhase = ((BooleanParameter) params[15]).getBoolean()
                .booleanValue();
        p.isCalculationWithNodesAsRectangles = ((BooleanParameter) params[16])
                .getBoolean().booleanValue();

        p.isCalculationWangMiyamoto = ((BooleanParameter) params[17])
                .getBoolean().booleanValue();

        p.isCalculationWithNodeEdgeRepulsion = ((BooleanParameter) params[18])
                .getBoolean().booleanValue();

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

        p.magneticFieldParameter = (StringSelectionParameter) params[19];

        if (!p.magneticFieldParameter.getSelectedValue().equals(
                AbstractSEForce.NO_MAGNETIC_FIELD)) {

            listOfForces.add(new MagneticForce(p));
        }
        p.magneticSpringConstant = ((DoubleParameter) params[20]).getDouble();

        // Labeling params
        p.attractionConstantToLabel = ((DoubleParameter) params[21])
                .getDouble().doubleValue();
        p.repulsionConstantNodeToLabel = ((DoubleParameter) params[22])
                .getDouble().doubleValue();
        p.repulsionConstantBothLabels = ((DoubleParameter) params[23])
                .getDouble().doubleValue();
        p.optimalNodeDistanceToLabel = ((DoubleParameter) params[24])
                .getDouble().doubleValue();
        p.isEdgeLabelNodeRepulsionForce = ((BooleanParameter) params[25])
                .getBoolean().booleanValue();
        p.edgeLabelForceRepulsionMax = ((DoubleParameter) params[26])
                .getDouble().doubleValue();
        p.edgeLabelForceSoftOuterBorder = ((DoubleParameter) params[27])
                .getDouble().doubleValue();
        p.edgeLabelForceSoftInnerBorder = ((DoubleParameter) params[28])
                .getDouble().doubleValue();
        p.isLabelNodeEdgeRepulsion = ((BooleanParameter) params[29])
                .getBoolean().booleanValue();
        p.repulsionConstantLabelNodeEdge = ((DoubleParameter) params[30])
                .getDouble().doubleValue();
        p.isNodeLabelAdjacentEdgesRepulsion = ((BooleanParameter) params[31])
                .getBoolean().booleanValue();
        p.NodeLabelAdjacentEdgesRepulsionMaxForce = ((DoubleParameter) params[32])
                .getDouble().doubleValue();
        p.NodeLabelAdjacentEdgesRepulsionForceSoftInnerBorder = ((DoubleParameter) params[33])
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
}
