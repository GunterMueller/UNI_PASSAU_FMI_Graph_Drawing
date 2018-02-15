// =============================================================================
//
//   IncrementalSugiyamaData.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SugiyamaData.java 2145 2007-11-19 15:11:55Z matzeder $

package org.graffiti.plugins.algorithms.sugiyama.util;

import java.util.HashMap;
import java.util.HashSet;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.sugiyama.SugiyamaAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.constraints.SugiyamaConstraint;
import org.graffiti.plugins.algorithms.sugiyama.incremental.IncrementalSugiyama;

/**
 * This class extends <tt>SugiyamaData</tt> to support the incremental sugiyama
 * algorithm.
 */
public class IncrementalSugiyamaData extends SugiyamaData {

    /* is this data object for the incremental sugiyama */
    private boolean incremental = true;

    /* minimal distance in x direction of two nodes */
    private int minimal_offset_x = 0;

    /* minimal distance in y direction of two nodes */
    private int minimal_offset_y = 0;

    /**
     * Default constructor - initialize the underlying data-structures
     */
    public IncrementalSugiyamaData() {
        super(true);
        algorithmType = SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA;
        reversedEdges = new HashSet<Edge>();
        deletedEdges = new HashSet<Edge>();
        dummyNodes = new HashSet<Node>();
        layers = new NodeLayers();
        startNode = null;
        algorithmBinaryNames = null;
        algorithms = new SugiyamaAlgorithm[4];
        lastSelectedAlgorithms = new String[4];
        alternateLex = false;
        animated = false;
        constraints = new HashSet<SugiyamaConstraint>();
        nodeMap = new HashMap<String, Node>();
        algorithmMap = new HashMap<String, SugiyamaAlgorithm>();
        buildFrameworkParameters();
        objects = new HashMap<String, Object>();
        bigNodes = new HashSet<BigNode>();
    }

    public void setFrameworkParameters(Parameter<?>[] params) {
        super.setAlgorithmParameters(params);

        minimal_offset_x = ((IntegerParameter) params[4]).getInteger();
        minimal_offset_y = ((IntegerParameter) params[5]).getInteger();

    }

    private void buildFrameworkParameters() {
        BooleanParameter animate = new BooleanParameter(false, "Animate",
                "Turn on support for animations");
        String[] drawing = new String[] {
                SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA,
                SugiyamaConstants.PARAM_RADIAL_SUGIYAMA,
                SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA };
        StringSelectionParameter drawingMethod = new StringSelectionParameter(
                drawing, "Drawing-method", "Please select the drawing-method");
        String[] bNodes = new String[] {
                SugiyamaConstants.PARAM_BIG_NODE_HANDLE,
                SugiyamaConstants.PARAM_BIG_NODE_SHRINK,
                SugiyamaConstants.PARAM_BIG_NODE_IGNORE };
        StringSelectionParameter bigNodes = new StringSelectionParameter(
                bNodes, "Big nodes", "Choose how to handle big nodes");
        bigNodes.setSelectedValue(2);
        String[] constraintHandling = new String[] {
                SugiyamaConstants.PARAM_CONSTRAINTS_HANDLE,
                SugiyamaConstants.PARAM_CONSTRAINTS_IGNORE };
        StringSelectionParameter constraints = new StringSelectionParameter(
                constraintHandling, "Constraints",
                "Choose how to handle constraints");

        IntegerParameter x = new IntegerParameter(IncrementalSugiyama
                .getMinimal_offset_x(), 1, 999, "Minimal x-offset",
                "Set the minimal x-offset here");
        IntegerParameter y = new IntegerParameter(IncrementalSugiyama
                .getMinimal_offset_y(), 1, 999, "Minimal y-offset",
                "Set the minimal y-offset here");

        constraints.setSelectedValue(1);
        this.parameters = new Parameter[] { animate, drawingMethod, bigNodes,
                constraints, x, y };

        PreferencesUtil.loadFrameworkParameters(this);
    }

    /**
     * Returns a copy of this SugiyamaData-object
     * 
     * @return A copy of this SugiyamaData-object
     */
    @Override
    public IncrementalSugiyamaData copy() {
        IncrementalSugiyamaData copy = new IncrementalSugiyamaData();
        super.copy(copy);

        copy.incremental = this.incremental;
        copy.minimal_offset_x = this.minimal_offset_x;
        copy.minimal_offset_y = this.minimal_offset_y;
        return copy;
    }

    /**
     * Sets the incremental.
     * 
     * @param incremental
     *            the incremental to set.
     */
    public void setIncremental(boolean incremental) {
        this.incremental = incremental;
    }

    /**
     * Returns the minimal_offset_x.
     * 
     * @return the minimal_offset_x.
     */
    public int getMinimal_offset_x() {
        return minimal_offset_x;
    }

    /**
     * Returns the minimal_offset_y.
     * 
     * @return the minimal_offset_y.
     */
    public int getMinimal_offset_y() {
        return minimal_offset_y;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
