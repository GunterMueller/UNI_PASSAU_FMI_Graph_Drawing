// =============================================================================
//
//   UniversalSiftingAlgorithm.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.gridsifting;

import org.graffiti.core.Bundle;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.algorithm.animation.Animation;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.sugiyama.levelling.LevellingAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class UniversalSiftingAlgorithm extends AbstractAlgorithm implements
        LevellingAlgorithm {
    private static final String NAME = UniversalSiftingAlgorithm
            .getString("universalsifting.levelling.name");

    private static Bundle BUNDLE;

    /**
     * The data of the sugiyama framework this algorithm operates on.
     */
    protected SugiyamaData data;

    /**
     * The parameters of this algorithm.
     */
    protected AlgorithmParameters parameters;

    public UniversalSiftingAlgorithm() {
        parameters = new AlgorithmParameters();
    }

    public static String format(String key, Object... args) {
        return String.format(getString(key), args);
    }

    public static String getString(String key) {
        if (BUNDLE == null) {
            BUNDLE = Bundle.getBundle(UniversalSiftingAlgorithm.class);
        }
        return BUNDLE.getString(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void check() throws PreconditionException {
        // TODO
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        graph.getListenerManager().transactionStarted(this);

        UniversalSiftingAnimation animation = new UniversalSiftingAnimation(
                data, parameters, false);

        // System.out.println("Starting US...");
        // long startTime = System.nanoTime();

        while (animation.hasNextStep()) {
            animation.nextStep();
        }

        // long stopTime = System.nanoTime();
        // System.out.println("Time: " + ((stopTime - startTime) / 1000000000.0)
        // + "s");

        graph.getListenerManager().transactionFinished(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Animation getAnimation() {
        return new UniversalSiftingAnimation(data, parameters, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return NAME;
    }

    protected AlgorithmParameters getUniversalSiftingParameters() {
        return parameters;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    public SugiyamaData getData() {
        return data;
    }

    /**
     * {@inheritDoc}
     */
    public void setData(SugiyamaData data) {
        this.data = data;
    }

    /**
     * {@inheritDoc} This implementation only supports the horizontal sugiyama.
     */
    public boolean supportsAlgorithmType(String algorithmType) {
        return algorithmType
                .equals(SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA);
    }

    /**
     * {@inheritDoc} This implementation returns {@code false}.
     */
    public boolean supportsBigNodes() {
        return false;
    }

    /**
     * {@inheritDoc} This implementation returns {@code false}.
     */
    public boolean supportsConstraints() {
        return false;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
