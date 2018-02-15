// =============================================================================
//
//   AlgorithmParameters.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.chebyshev;

import java.util.LinkedList;
import java.util.Random;

import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringParameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.chebyshev.cores.AlgorithmCore;
import org.graffiti.plugins.algorithms.chebyshev.cores.BaryCenter;
import org.graffiti.plugins.algorithms.chebyshev.cores.FastBaryCenter;
import org.graffiti.plugins.algorithms.chebyshev.cores.IdleCore;
import org.graffiti.plugins.algorithms.chebyshev.cores.MCMCore;
import org.graffiti.plugins.algorithms.chebyshev.iterations.IterationParser;
import org.graffiti.plugins.algorithms.chebyshev.localimprovers.LocalImprover;
import org.graffiti.plugins.algorithms.chebyshev.localimprovers.NoImprover;
import org.graffiti.plugins.algorithms.chebyshev.selectors.BranchSelector;
import org.graffiti.plugins.algorithms.chebyshev.selectors.LeftSpaceSelector;
import org.graffiti.plugins.algorithms.chebyshev.selectors.NoBranchSelector;
import org.graffiti.plugins.algorithms.chebyshev.selectors.RandomSelector;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class AlgorithmParameters {
    private static final String DEFAULT_ITERATION_PATTERN = "(up down) * 5";
    // = "down up (top.down top.up) * 15 belowTop.down";

    private static final Class<?>[] CORES = { MCMCore.class, IdleCore.class,
            BaryCenter.class, FastBaryCenter.class };

    private static final Class<?>[] LOCAL_IMPROVERS = {
    // TODO:
    NoImprover.class };

    private static final Class<?>[] BRANCH_SELECTORS = {
            LeftSpaceSelector.class, RandomSelector.class,
            NoBranchSelector.class };

    private String iterationPattern;

    private boolean isNormalizing;

    private long seed;

    private Random random;

    private AlgorithmFactory<AlgorithmCore> coreFactory;

    private AlgorithmFactory<LocalImprover> localImproverFactory;

    private AlgorithmFactory<BranchSelector> branchSelectorFactory;

    private boolean isDebug;

    private static String getString(String key) {
        return MCMCrossMinAlgorithm.getString(key);
    }

    public AlgorithmCore createCore(AuxGraph graph) {
        return coreFactory.create(graph, this);
    }

    private <U extends AbstractSubAlgorithm> AlgorithmFactory<U> createFactory(
            Class<?>[] classes, Class<U> algorithmClass,
            Parameter<?> parameter, Class<?>... parameterTypes) {
        int index = ((StringSelectionParameter) parameter).getSelectedIndex();
        try {
            return new AlgorithmFactory<U>(classes[index].asSubclass(
                    algorithmClass).getConstructor(parameterTypes));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public LocalImprover createLocalImprover(AuxGraph graph) {
        return localImproverFactory.create(graph, this);
    }

    public BranchSelector createBranchSelector(AuxGraph graph) {
        return branchSelectorFactory.create(graph, this);
    }

    private String[] createNames(Class<?>[] classes) {
        int count = classes.length;
        String[] names = new String[count];
        for (int i = 0; i < count; i++) {
            String name = classes[i].getCanonicalName();
            name = MCMCrossMinAlgorithm.getString(name);
            names[i] = name;
        }
        return names;
    }

    /**
     * Creates the algorithm steps for the specified graph or checks if the
     * iteration pattern is well-formed.
     * 
     * @param graph
     *            the graph. May be {@code null}.
     * @return the steps of the algorithm. Is {@code null} if the iteration
     *         pattern is not well-formed.
     */
    public LinkedList<Step> createSteps(AuxGraph graph) {
        return IterationParser.parse(iterationPattern, graph);
    }

    public boolean isDebug() {
        return isDebug;
    }

    protected Parameter<?>[] getAlgorithmParameters() {
        return new Parameter<?>[] {
                new StringParameter(DEFAULT_ITERATION_PATTERN,
                        getString("params.iterationPattern.name"),
                        getString("params.iterationPattern.desc")),
                new StringParameter(String.valueOf((new Random().nextLong())),
                        getString("params.random.name"),
                        getString("params.random.desc")),
                new StringSelectionParameter(createNames(CORES),
                        getString("params.core.name"),
                        getString("params.core.desc")),
                new StringSelectionParameter(createNames(LOCAL_IMPROVERS),
                        getString("params.localImprover.name"),
                        getString("params.localImprover.desc")),
                new StringSelectionParameter(createNames(BRANCH_SELECTORS),
                        getString("params.branchSelector.name"),
                        getString("params.branchSelector.desc")),
                new BooleanParameter(false, getString("params.debug.name"),
                        getString("params.debug.desc")),
                new BooleanParameter(false, getString("params.normalize.name"),
                        getString("params.normalize.desc")) };
    }

    public Random getRandom() {
        return random;
    }

    protected void setAlgorithmParameters(Parameter<?>[] params) {
        iterationPattern = ((StringParameter) params[0]).getString();
        String seedString = ((StringParameter) params[1]).getString();
        try {
            seed = Long.parseLong(seedString);
        } catch (NumberFormatException e) {
            seed = new Random().nextLong();
        }
        random = new Random(seed);
        coreFactory = createFactory(CORES, AlgorithmCore.class, params[2]);
        localImproverFactory = createFactory(LOCAL_IMPROVERS,
                LocalImprover.class, params[3]);
        branchSelectorFactory = createFactory(BRANCH_SELECTORS,
                BranchSelector.class, params[4]);
        isDebug = ((BooleanParameter) params[5]).getBoolean();
        isNormalizing = ((BooleanParameter) params[6]).getBoolean();
    }

    public boolean isNormalizing() {
        return isNormalizing;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
