// =============================================================================
//
//   DeterministicGraphGeneratorAlgorithm.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.generators;

import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringParameter;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class DeterministicGraphGeneratorAlgorithm extends AbstractAlgorithm {
    private static final int PARAMETER_COUNT = 8;

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Parameter<?>[] getAlgorithmParameters() {
        Parameter<?>[] parameters = new Parameter<?>[1 + 2 * PARAMETER_COUNT];
        parameters[0] = new StringParameter("default", "seed", "");
        return parameters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setAlgorithmParameters(Parameter<?>[] params) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Deterministic Graph Generator";
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
