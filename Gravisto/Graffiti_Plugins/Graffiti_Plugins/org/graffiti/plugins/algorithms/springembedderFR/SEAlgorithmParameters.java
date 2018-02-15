// =============================================================================
//
//   AbstractForce.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SEAlgorithmParameters.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.springembedderFR;

import org.graffiti.plugin.parameter.StringSelectionParameter;

/**
 * Class for the parameters of the spring embedder of Fruchterman and Reingold.
 * 
 * @author matzder
 * @version $Revision: 5766 $ $Date: 2006-06-12 07:52:18 +0200 (Mo, 12 Jun 2006)
 *          $
 */
public class SEAlgorithmParameters {

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
    public FRGraph fRGraph;

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
    public Grid grid;

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

    /**
     * Creates a new Spring Embedder parameter object.
     * 
     */
    public SEAlgorithmParameters() {

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
    }

    private void setStandardParameters() {

        // initial variants
        isGridVariant = true;
        isCalculationWithNodeSizes = true;
        isCalculationWithNodesAsRectangles = true;
        isCalculationWangMiyamoto = false;
        isNodesGrowing = false;
        isLocalTemperature = true;
        isGravity = true;
        isGlobalTempConceptFR = true;
        isQuenchingPhase = true;
        isCalculationWithForster = true;
        isCalculationIdealLengths = false;
        isCalculationWithNodeEdgeRepulsion = false;

        // initial constants to normalize calculation (test results)
        width = 1000;
        height = 1000;

        gravityConstant = 10;
        attractionConstant = 0.0001;
        repulsionConstantNodeNode = 10.0;
        normalizingIdealLengthFactor = 0.2d;
        repulsionConstantNodeEdge = 100.0;

        // initial values for algorithm
        quenchingIterations = 50;
        simmeringIterations = 50;
        quenchingTemperatureStart = 100;
        quenchingTemperatureEnd = 10;
        simmeringTemperatureStart = 10;
        simmeringTemperatureEnd = 2;

        magneticSpringConstant = 10d;
        barycenter = new GeometricalVector();

        optimalNodeDistance = 5d;
        // idealEdgeLength = 5d;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
