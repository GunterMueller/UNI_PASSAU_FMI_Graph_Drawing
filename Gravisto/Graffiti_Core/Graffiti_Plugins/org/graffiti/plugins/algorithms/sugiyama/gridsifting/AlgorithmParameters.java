// =============================================================================
//
//   AlgorithmParameters.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.gridsifting;

import java.util.Random;

import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringParameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.sugiyama.gridsifting.initiallevelling.InitialCoffmanGrahamLevelling;
import org.graffiti.plugins.algorithms.sugiyama.gridsifting.initiallevelling.InitialLPLevelling;
import org.graffiti.plugins.algorithms.sugiyama.gridsifting.initiallevelling.InitialLevelling;
import org.graffiti.plugins.algorithms.sugiyama.gridsifting.initiallevelling.InitialLongestPathLevelling;
import org.graffiti.plugins.algorithms.sugiyama.gridsifting.initiallevelling.InitialSiftingLevelling;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class AlgorithmParameters {
    private static final Class<?>[] TERMINAL_CONDITIONS = {
            NoImprovementCondition.class, FixedRoundsCondition.class, };

    private static final Class<?>[] LEVELLINGS = {
            InitialCoffmanGrahamLevelling.class,
            InitialLongestPathLevelling.class, InitialSiftingLevelling.class,
            InitialLPLevelling.class };

    private static final int INITIAL_ROUND_COUNT = 10;

    private static final int INITIAL_LEVEL_RADIUS = 1000000000;

    private long seed;

    private Random random;

    private boolean isDebug;

    private AlgorithmFactory<TerminalCondition> terminalConditionFactory;

    private int roundCount;

    private AlgorithmFactory<InitialLevelling> initialLevellingFactory;

    private int initialMaxLevelWidth;

    private int levelRadius;
    
    private boolean isSkippingEvenLevels;
    
    private boolean isSkippingNeighborLevels;
    
    private boolean isPreferring;
    
    private static String getString(String key) {
        return UniversalSiftingAlgorithm.getString(key);
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

    private String[] createNames(Class<?>[] classes) {
        int count = classes.length;
        String[] names = new String[count];
        for (int i = 0; i < count; i++) {
            String name = classes[i].getCanonicalName();
            name = UniversalSiftingAlgorithm.getString(name);
            names[i] = name;
        }
        return names;
    }

    public Random getRandom() {
        return random;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public TerminalCondition createTerminalCondition(BlockGraph graph,
            SugiyamaData sugiyamaData) {
        return terminalConditionFactory.create(graph, this, sugiyamaData);
    }

    public int getRoundCount() {
        return roundCount;
    }
    
    public boolean isSkippingEvenLevels() {
        return isSkippingEvenLevels;
    }
    
    public boolean isSkippingNeighborLevels() {
        return isSkippingNeighborLevels;
    }
    
    public boolean isPreferring() {
        return isPreferring;
    }

    public InitialLevelling createInitialLevelling(BlockGraph graph,
            SugiyamaData sugiyamaData) {
        InitialLevelling initialLevelling = initialLevellingFactory.create(
                graph, this, sugiyamaData);

        initialLevelling.setInitialMaxLevelWidth(initialMaxLevelWidth);

        return initialLevelling;
    }

    public int getLevelRadius() {
        return levelRadius;
    }

    protected Parameter<?>[] getAlgorithmParameters() {
        Parameter<?> initialLevellingParam = null;
        Parameter<?> initialMaxLevelWidthParam = null;

        Parameter<?>[] result = new Parameter<?>[] {
                new StringParameter(String.valueOf((new Random().nextLong())),
                        getString("params.random.name"),
                        getString("params.random.desc")),
                new BooleanParameter(false, getString("params.debug.name"),
                        getString("params.debug.desc")),
                new StringSelectionParameter(createNames(TERMINAL_CONDITIONS),
                        getString("params.terminalCondition.name"),
                        getString("params.terminalCondition.desc")),
                new IntegerParameter(INITIAL_ROUND_COUNT,
                        getString("params.roundCount.name"),
                        getString("params.roundCount.desc")),
                initialLevellingParam = new StringSelectionParameter(
                        createNames(LEVELLINGS),
                        getString("params.initialLevelling.name"),
                        getString("params.initialLevelling.desc")),
                initialMaxLevelWidthParam = new IntegerParameter(1,
                        getString("params.initialMaxLevelWidth.name"),
                        getString("params.initialMaxLevelWidth.desc")),
                new IntegerParameter(INITIAL_LEVEL_RADIUS,
                        getString("params.levelRadius.name"),
                        getString("params.levelRadius.desc")), };

        initialMaxLevelWidthParam.setDependency(initialLevellingParam,
                "CoffmanGraham");

        return result;
    }

    protected void setAlgorithmParameters(Parameter<?>[] params) {
        String seedString = ((StringParameter) params[0]).getString();

        try {
            seed = Long.parseLong(seedString);
        } catch (NumberFormatException e) {
            seed = new Random().nextLong();
        }

        random = new Random(seed);

        int index = 1;

        isDebug = ((BooleanParameter) params[index++]).getBoolean();

        terminalConditionFactory = createFactory(TERMINAL_CONDITIONS,
                TerminalCondition.class, params[index++]);

        roundCount = ((IntegerParameter) params[index++]).getInteger();

        initialLevellingFactory = createFactory(LEVELLINGS,
                InitialLevelling.class, params[index++]);

        initialMaxLevelWidth = ((IntegerParameter) params[index++])
                .getInteger();

        levelRadius = ((IntegerParameter) params[index++]).getInteger();
        
        isSkippingEvenLevels = levelRadius == 21;
        isSkippingNeighborLevels = levelRadius == 21;
        
        isPreferring = true;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
