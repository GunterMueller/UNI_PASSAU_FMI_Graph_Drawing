// =============================================================================
//
//   MCMCrossMinAlgorithm.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.chebyshev;

import org.graffiti.core.Bundle;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.algorithm.animation.Animation;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.sugiyama.crossmin.CrossMinAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.gridsifting.benchmark.SugiyamaBenchmarkAdapter;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class MCMCrossMinAlgorithm extends MCMAbstractAlgorithm implements
        CrossMinAlgorithm {
    private static final String NAME = MCMCrossMinAlgorithm
            .getString("crossmin.name");

    private static Bundle BUNDLE;

    /**
     * The parameters of this algorithm.
     */
    protected AlgorithmParameters parameters;

    public MCMCrossMinAlgorithm() {
        parameters = new AlgorithmParameters();
    }

    public static String format(String key, Object... args) {
        return String.format(getString(key), args);
    }

    public static String getString(String key) {
        if (BUNDLE == null) {
            BUNDLE = Bundle.getBundle(MCMCrossMinAlgorithm.class);
        }
        return BUNDLE.getString(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void check() throws PreconditionException {
        if (parameters.createSteps(null) == null)
            throw new PreconditionException(
                    getString("exception.precondition.iterationPattern"));
    }

    @Override
    public void execute() {
        MCMCrossMinAnimation animation = new MCMCrossMinAnimation(data,
                parameters, false);
        long startTime = System.nanoTime();
        while (animation.hasNextStep()) {
            animation.nextStep();
        }
        long stopTime = System.nanoTime();
        data.putObject(SugiyamaBenchmarkAdapter.CROSSMIN_TIME_KEY, stopTime
                - startTime);
    }

    @Override
    public Animation getAnimation() {
        return new MCMCrossMinAnimation(data, parameters, true);
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return NAME;
    }

    protected AlgorithmParameters getMMSParameters() {
        return parameters;
    }

    @Override
    public boolean supportsAnimation() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Parameter<?>[] getAlgorithmParameters() {
        return parameters.getAlgorithmParameters();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setAlgorithmParameters(Parameter<?>[] params) {
        parameters.setAlgorithmParameters(params);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
