// =============================================================================
//
//   AbstractForce.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SELabelingAlgorithmParameters.java 1289 2006-06-12 05:52:18Z matzeder $

package org.graffiti.plugins.algorithms.labeling;

import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.springembedderFR.GeometricalVector;
import org.graffiti.plugins.algorithms.springembedderFR.MagneticField;

/**
 * Class for the parameters of the spring embedder of Fruchterman and Reingold.
 * 
 * @author matzder
 * @version $Revision: 1289 $ $Date: 2006-06-12 07:52:18 +0200 (Mon, 12 Jun
 *          2006) $
 */
public class SELabelingAlgorithmParameters {

    // -----------------------BEGIN BOOLEAN---------------------
    /**
     * True, wenn grid variant verwendet werden soll, sonst false
     */
    public boolean isGridVariant;

    /**
     * True, wenn mit Knotengr��en gerechnet werden soll
     */
    public boolean isCalculationWithNodeSizes;

    /**
     * If true, then nodes are treated as rectangles, else as circles
     */
    public boolean isCalculationWithNodesAsRectangles;

    /**
     * True, then calculate the distance of the nodes after Wang and Miyamoto
     * (circles)
     */
    public boolean isCalculationWangMiyamoto;

    /**
     * True, if calculation with local temperatures
     */
    public boolean isLocalTemperature;

    /**
     * True, if calculation with growing nodes
     */
    public boolean isNodesGrowing;

    /**
     * true if gravity should be used, else false
     */
    public boolean isGravity;

    /**
     * True, if global temperature is used originally of FR, false if new
     * approach is used
     */
    public boolean isGlobalTempConceptFR;

    /**
     * If true, an quenching phase is executed, else not.
     */
    public boolean isQuenchingPhase;

    /**
     * Use the calculation method of Forster if true
     */
    public boolean isCalculationWithForster;

    /**
     * The algorithm calculates the ideal lengths (node-node distance, edge
     * length) after Fruchterman and Reingold
     */
    public boolean isCalculationIdealLengths;

    /**
     * True, if calculation with node edge repulsion
     */
    public boolean isCalculationWithNodeEdgeRepulsion;

    // -------------------END BOOLEAN-----------------------------

    /**
     * A FRGraph, especially for this algorithm.
     */
    public FRGraphLabeling fRGraph;

    /**
     * Calculated constant, dependent on length of an edge longest edge
     */
    public double attractionConstant;

    /**
     * Constant to normalize the repulsion between nodes and edges
     */
    public double repulsionConstantNodeEdge;

    /**
     * Value for the number of quenching iterations
     */
    public int quenchingIterations;

    /**
     * Value for the number of simmering iterations
     */
    public int simmeringIterations;

    /**
     * defines the optimal node distance
     */
    public double optimalNodeDistance;

    // /**
    // * defines the ideal edge length, which one wants to have
    // */
    // public double idealEdgeLength;

    /**
     * Intensity of the gravity
     */
    public int gravityConstant;

    /**
     * Factor to normalize the distance between nodes and edges (found
     * experimentally)
     */
    public double normalizingIdealLengthFactor;

    /**
     * Variable of the algorithm of Fruchterman and Reingold (for grid length
     * and for calculation of optimal lengths used)
     */
    public double k;

    /**
     * Constant for the height of the frame, where the graph is drawn
     */
    public double height;

    /**
     * Constant for the width of the frame, where the graph is drawn
     */
    public double width;

    /**
     * to know in which iteration algorithm is
     */
    public int simmeringIteration;

    /**
     * Normierungskonstante f�r die absto�ende Kraft zwischen Knoten
     */
    public double repulsionConstantNodeNode;

    /**
     * The grid of the Algorithm
     */
    public org.graffiti.plugins.algorithms.labeling.Grid grid;

    /**
     * Start temperature at beginning of quenching
     */
    public double quenchingTemperatureStart;

    /**
     * end temperature at the end of quenching
     */
    public double quenchingTemperatureEnd;

    /**
     * start temperature at beginning of simmering
     */
    public double simmeringTemperatureStart;

    /**
     * end temperature at the end of simmering
     */
    public double simmeringTemperatureEnd;

    /**
     * Vector which specifies in every iteration the barycenter of the graph.
     */
    public GeometricalVector barycenter;

    /**
     * The phase of the algorithm, which is actual
     */
    public int phase;

    /**
     * The position of the root node (magnetic force)
     */
    public GeometricalVector root;

    /**
     * The selection
     */
    public StringSelectionParameter magneticFieldParameter;

    /**
     * The acting magnetic field
     */
    public MagneticField magneticField;

    /**
     * Strength of the magnetic force
     */
    public double magneticSpringConstant;

    // LABELING PARAMETERS
    /**
     * does little logging every algorithm step, such as displaying the forces
     * calculated
     */
    public boolean isBasicForceLogging;
    /** labeling forces will do extensive logging */
    public boolean isLabelingForcesVerboseMode;
    /** tracks FRNode positions on the console */
    public boolean isConsoleFRNodeTracking;
    /** shows run statistics on the console */
    public boolean isConsoleStatistics;

    /** keeps node labels at a distance to their corresponding nodes */
    public double optimalNodeDistanceToLabel;
    public double attractionConstantToLabel;
    public double repulsionConstantNodeToLabel;
    public double repulsionConstantBothLabels;

    /**
     * force between node label nodes and the emerging edges of their
     * corresponding node TODO: not done yet
     */
    public boolean isNodeLabelNodeEdgeRepulsionForce;

    /**
     * attraction force between edge label nodes and their corresponding
     * edges/nodes TODO: not done yet
     */
    public boolean isEdgeLabelNodeAttractionForce;

    /**
     * repulsion force between edge label node and corresponding edge to avoid
     * readablility issues from letter-edge overlaps
     */
    public boolean isEdgeLabelNodeRepulsionForce;
    /**
     * scaling factor for the edge label force; <br>
     * should be adjusted to the other forces' power; not to be chosen to huge,
     * as labels might jump too far
     */
    public double edgeLabelForceRepulsionMax;
    /**
     * desired distance between edge label border and corresponding edge
     * <p>
     * Note that if there are no other forces that squeeze the node towards the
     * edge (such as artificial adjacent nodes), the label will be located at
     * this distance to the edge.
     * <p>
     * must be > 0
     */
    public double edgeLabelForceSoftOuterBorder;
    /**
     * in constrast to <code>edgeLabelForceSoftOuterBorder</code>, this value
     * issues that minor violations (letters overlapping the edge) will be less
     * punished. In most cases, the label size tolerates a few pixels (about
     * two).
     * <p>
     * must be >= 0
     */
    public double edgeLabelForceSoftInnerBorder;

    /** repulsion force between label nodes and not <i>own</i> edges */
    public boolean isLabelNodeEdgeRepulsion;
    /** Constant to normalize the repulsion between label nodes and edges */
    public double repulsionConstantLabelNodeEdge;

    /**
     * circular repulsion force between node label nodes and the emerging edges
     * of the parent node
     */
    public boolean isNodeLabelAdjacentEdgesRepulsion;

    /**
     * scaling factor for the node label - emerging edges repulsion force; <br>
     * should be adjusted to the other forces' power; not to be chosen to huge,
     * as labels might jump too far
     */
    public double NodeLabelAdjacentEdgesRepulsionMaxForce;

    /**
     * force used if trying to "jump" over bad local optima.
     */
    public double NodeLabelAdjacentEdgesRepulsionJumpForce;

    /**
     * As node labels should reside closely to their parents, the distance to
     * their parents will be quite small. Thus, distances to emerging edges are
     * small. If there is no way to overcome overlaps without running into new
     * ones, the force will perform badly.
     * <p>
     * This value shrinks the labels by the given size.
     */
    public double NodeLabelAdjacentEdgesRepulsionForceSoftInnerBorder;

    /**
     * Creates a new Spring Embedder parameter object.
     * 
     */
    public SELabelingAlgorithmParameters() {

        setStandardParameters();

    }

    @Override
    public String toString() {
        String s = "";

        s += "\n GridVariant: " + isGridVariant;
        s += "\n CalculationWithNodeSizes: " + isCalculationWithNodeSizes;
        s += "\n  - withRectangles: " + isCalculationWithNodesAsRectangles;
        s += "\n  - withMiyamoto: " + isCalculationWangMiyamoto;
        s += "\n  - nodesGrowing: " + isNodesGrowing;
        s += "\n LocalTemperature: " + isLocalTemperature;
        s += "\n Gravity: " + isGravity;
        s += "\n GlobalTempFR: " + isGlobalTempConceptFR;
        s += "\n QuenchingPhase: " + isQuenchingPhase;
        s += "\n WithForster: " + isCalculationWithForster;
        s += "\n CalculationIdealLengths: " + isCalculationIdealLengths;
        s += "\n WithNodeEdgeRepulsion: " + isCalculationWithNodeEdgeRepulsion;
        s += "\n AttConst: " + attractionConstant;
        s += "\n RepConst: " + repulsionConstantNodeEdge;
        s += "\n RepConstNN: " + repulsionConstantNodeNode;
        s += "\n #Quenching: " + quenchingIterations;
        s += "\n #Simmering: " + simmeringIterations;
        s += "\n IdealNodeDist: " + optimalNodeDistance;
        // s += "\n IdealEdgeLength: " + idealEdgeLength;
        s += "\n GravityConst: " + gravityConstant;
        s += "\n NormIdealLengthFactor: " + normalizingIdealLengthFactor;
        s += "\n K: " + k;
        s += "\n QuenchingTempStart: " + quenchingTemperatureStart;
        s += "\n QuenchingTempEnd: " + quenchingTemperatureEnd;
        s += "\n SimmeringTempStart: " + simmeringTemperatureStart;
        s += "\n SimmeringTempEnd: " + simmeringTemperatureEnd;
        s += "\n MagneticField: " + magneticField;
        s += "\n MagneticSpringConst: " + magneticSpringConstant;
        return s;
        // TODO: add labeling statistics
    }

    protected void setStandardParameters() {

        // initial variants
        isGridVariant = false;
        isCalculationWithNodeSizes = true;
        isCalculationWithNodesAsRectangles = true;
        isCalculationWangMiyamoto = false;
        isNodesGrowing = false;
        isLocalTemperature = false;
        isGravity = false;
        isGlobalTempConceptFR = true;
        isQuenchingPhase = true;
        isCalculationWithForster = true;
        isCalculationIdealLengths = false;
        isCalculationWithNodeEdgeRepulsion = false;

        // labeling
        isBasicForceLogging = false;
        isConsoleFRNodeTracking = false;
        isConsoleStatistics = false;
        isLabelingForcesVerboseMode = false;
        isEdgeLabelNodeRepulsionForce = true;
        isLabelNodeEdgeRepulsion = true;
        isNodeLabelAdjacentEdgesRepulsion = true;

        // initial constants to normalize calculation (test results)
        width = 1000;
        height = 1000;

        gravityConstant = 10; // 10
        attractionConstant = 0.0001; // 0.0001
        attractionConstantToLabel = 0.0005;
        repulsionConstantNodeNode = 20.0; // 10.0
        repulsionConstantNodeToLabel = 80.0;
        repulsionConstantBothLabels = 60.0;
        normalizingIdealLengthFactor = 0.2d; // 0.2
        repulsionConstantNodeEdge = 100.0; // 100.0
        // labeling
        edgeLabelForceRepulsionMax = 10d;
        edgeLabelForceSoftOuterBorder = 6d;
        edgeLabelForceSoftInnerBorder = 4d;
        repulsionConstantLabelNodeEdge = 0.00001d;
        NodeLabelAdjacentEdgesRepulsionMaxForce = .5d;
        NodeLabelAdjacentEdgesRepulsionForceSoftInnerBorder = 2d;
        NodeLabelAdjacentEdgesRepulsionJumpForce = 1000d;

        // initial values for algorithm
        quenchingIterations = 10;
        simmeringIterations = 40;
        quenchingTemperatureStart = 20;
        quenchingTemperatureEnd = 5;
        simmeringTemperatureStart = 5;
        simmeringTemperatureEnd = 1;

        magneticSpringConstant = 0.01d;
        barycenter = new GeometricalVector();

        optimalNodeDistanceToLabel = .2d;
        optimalNodeDistance = 3d; // 5
        // idealEdgeLength = 5d;

        // // ORIGINAL PARAMETERS
        // isGridVariant = false;
        // isCalculationWithNodeSizes = true;
        // isCalculationWithNodesAsRectangles = true;
        // isCalculationWangMiyamoto = false;
        // isNodesGrowing = false;
        // isLocalTemperature = true;
        // isGravity = true;
        // isGlobalTempConceptFR = true;
        // isQuenchingPhase = true;
        // isCalculationWithForster = true;
        // isCalculationIdealLengths = false;
        // isCalculationWithNodeEdgeRepulsion = false;
        // // initial constants to normalize calculation (test results)
        // width = 1000;
        // height = 1000;
        //
        // gravityConstant = 10;
        // attractionConstant = 0.0001;
        // repulsionConstantNodeNode = 10.0;
        // normalizingIdealLengthFactor = 0.2d;
        // repulsionConstantNodeEdge = 100.0;
        //
        // // initial values for algorithm
        // quenchingIterations = 50;
        // simmeringIterations = 50;
        // quenchingTemperatureStart = 100;
        // quenchingTemperatureEnd = 10;
        // simmeringTemperatureStart = 10;
        // simmeringTemperatureEnd = 2;
        //
        // magneticSpringConstant = 10d;
        // barycenter = new GeometricalVector();
        //
        // optimalNodeDistance = 5d;
        // // idealEdgeLength = 5d;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
