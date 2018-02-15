package org.graffiti.plugins.algorithms.springembedderFR;

import java.util.LinkedList;

import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.DoubleParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.selection.Selection;

/**
 * An advanced SpringEmbedder (Fruchterman & Reingold)
 * 
 * @author matzeder
 */
public class FRSpringAlgorithmAllParams extends FRSpringAlgorithmStandard {

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

    /**
     * Parameter for the choice of the variant (specified by user)
     */
    protected static StringSelectionParameter auswahl;

    /**
     * Constructs a new instance of an Spring Embedder Algorithm, where the user
     * can specify many parameters.
     * 
     */
    public FRSpringAlgorithmAllParams() {
        super();
        nrQuenchingParam = new IntegerParameter(p.quenchingIterations,
                "number of iterations in quenching phase",
                "Sets the number of iterations during quenching phase.");

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
                "Sets the start temperature of the simmering phase.");

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
        return "Standard (parameters adjustable)";
    }

    /**
     * Returns an array with the parameters of this algorithm
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        SelectionParameter selParam = new SelectionParameter("Selection",
                "<html>The selection to work on.<p>If empty, "
                        + "the whole graph is used.</html>");
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
        variantsParam.setValue(AbstractSEForce.NO_GV_NO_NODE_SIZE);

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
                10.0d, "magnetic field strength",
                "Strength of the magnetic field");

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
                magneticSpringConstantParam };
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
    }
}
