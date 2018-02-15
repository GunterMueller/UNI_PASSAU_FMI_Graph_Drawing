// =============================================================================
//
//   SimpleCyclicLeveling.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.levelling;

import org.graffiti.graph.Node;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;

/**
 * @author brunner
 * @version $Revision$ $Date$
 */
public class SimpleCyclicLeveling extends AbstractCyclicLevelingAlgorithm
        implements LevellingAlgorithm {
    private final int INITIAL_WIDTH = 10;

    private int width;

    public SimpleCyclicLeveling() {
        width = INITIAL_WIDTH;
    }

    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        IntegerParameter p1 = new IntegerParameter(20, "Nodes / Layer",
                "Set the max number of nodes per layer", 1, 100, 1,
                Integer.MAX_VALUE);

        this.parameters = new Parameter[] { p1 };
        return this.parameters;
    }

    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        width = ((IntegerParameter) params[0]).getInteger();
    }

    public String getName() {
        return "Simple Cyclic Leveling";
    }

    @Override
    protected void levelNodes() {
        int currentLevel = 0;
        int currentNode = 0;
        for (Node n : graph.getNodes()) {
            setNodeLevel(n, currentLevel);
            currentNode++;
            if (currentNode == width) {
                currentLevel++;
                currentNode = 0;
            }
        }
        if (currentNode > 0) {
            numberOfLevels = currentLevel + 1;
        } else {
            numberOfLevels = currentLevel;
        }
    }

    public boolean supportsAlgorithmType(String algorithmType) {
        return algorithmType.equals(SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA);
    }

    public boolean supportsBigNodes() {
        return false;
    }

    public boolean supportsConstraints() {
        return false;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
