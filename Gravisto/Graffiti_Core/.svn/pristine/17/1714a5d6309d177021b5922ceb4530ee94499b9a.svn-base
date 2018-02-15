// =============================================================================
//
//   SugiyamaBenchmarkAdapter.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.gridsifting.benchmark;

import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graffiti.plugin.algorithm.Algorithm;
import org.graffiti.plugin.algorithm.AlgorithmResult;
import org.graffiti.plugin.algorithm.CalculatingAlgorithm;
import org.graffiti.plugin.algorithm.DefaultAlgorithmResult;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.sugiyama.Sugiyama;
import org.graffiti.plugins.algorithms.sugiyama.SugiyamaAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.SugiyamaAnimation;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;
import org.graffiti.plugins.tools.benchmark.NestingAlgorithm;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class SugiyamaBenchmarkAdapter extends Sugiyama implements
        NestingAlgorithm, CalculatingAlgorithm {
    public static final String CROSSMIN_TIME_KEY = "crossminTime";
    public static final String CROSSMIN_TIME_UNTIL_BREAK_KEY = "crossminTimeUntilBreak";
    public static final String EFFECTIVE_ROUNDS_KEY = "effectiveRounds";
    public static final String LEVEL_COUNT_KEY = "levelCount";

    private AlgorithmResult result;

    public SugiyamaBenchmarkAdapter() {
        Logger.getLogger(Sugiyama.class.getCanonicalName()).setLevel(Level.OFF);
        Logger.getLogger(SugiyamaAnimation.class.getCanonicalName()).setLevel(
                Level.OFF);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNestedAlgorithms(Map<String, Algorithm> algorithms) {
        Parameter<?>[] parameters = getParameters();
        SugiyamaData data = getSugiyamaData();
        SugiyamaAlgorithm[] algs = new SugiyamaAlgorithm[4];
        algs[0] = (SugiyamaAlgorithm) algorithms.get("decycling");
        algs[1] = (SugiyamaAlgorithm) algorithms.get("levelling");
        algs[2] = (SugiyamaAlgorithm) algorithms.get("crossmin");
        algs[3] = (SugiyamaAlgorithm) algorithms.get("layout");
        for (int i = 0; i < 4; i++) {
            if (algs[i] != null) {
                algs[i].setData(data);
            }
        }
        data.setSelectedAlgorithms(algs);
        ((BooleanParameter) (data.getAlgorithmParameters()[0])).setValue(true);
        setParameters(parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        super.execute();
        /*
         * SugiyamaAnimation animation = (SugiyamaAnimation) getAnimation();
         * 
         * animation.nextStep(); animation.nextStep();
         * 
         * result = new DefaultAlgorithmResult();
         * 
         * long startCrossMinTime = System.nanoTime();
         * 
         * while (animation.hasNextStep()) { animation.nextStep(); }
         * 
         * long endCrossMinTime = System.nanoTime();
         */

        result = new DefaultAlgorithmResult();

        // int crossings = countCrossings(animation);

        SugiyamaData data = getSugiyamaData();
//        NodeLayers levels = data.getLayers();
//        int levelCount = levels.getNumberOfLayers();
//
//        int maxLevelWidth = 0;
//
//        for (int level = 0; level < levelCount; level++) {
//            maxLevelWidth = Math.max(maxLevelWidth, levels.getLayer(level)
//                    .size());
//        }

        Integer crossingCount = (Integer) data
                .getObject(SugiyamaData.CROSSING_COUNT);

        if (crossingCount != null) {
            result.addToResult(SugiyamaData.CROSSING_COUNT, crossingCount);
        }

        Integer initialCrossingCount = (Integer) data
                .getObject(SugiyamaData.INITIAL_CROSSING_COUNT);

        if (initialCrossingCount != null) {
            result.addToResult(SugiyamaData.INITIAL_CROSSING_COUNT,
                    initialCrossingCount);
        }
        
        Integer levelCount = (Integer) data.getObject(LEVEL_COUNT_KEY);

        if (levelCount != null) {
            result.addToResult(LEVEL_COUNT_KEY, levelCount);
        }

        Integer initialLevelCount = (Integer) data
                .getObject(SugiyamaData.INITIAL_LEVEL_COUNT);

        if (initialLevelCount != null) {
            result.addToResult(SugiyamaData.INITIAL_LEVEL_COUNT,
                    initialLevelCount);
        }

        result.addToResult("maxLevelWidth", 0);

        Long crossminTime = (Long) data.getObject(CROSSMIN_TIME_KEY);

        if (crossminTime != null) {
            result.addToResult(CROSSMIN_TIME_KEY, String.format((Locale) null,
                    "%f", crossminTime / 1000000000.0));
        }

        Long crossminTimeUntilBreak = (Long) data
                .getObject(CROSSMIN_TIME_UNTIL_BREAK_KEY);

        if (crossminTimeUntilBreak != null) {
            result
                    .addToResult(CROSSMIN_TIME_UNTIL_BREAK_KEY, String.format(
                            (Locale) null, "%f",
                            crossminTimeUntilBreak / 1000000000.0));
        }

        Integer effectiveRounds = (Integer) data
                .getObject(EFFECTIVE_ROUNDS_KEY);

        if (effectiveRounds != null) {
            result.addToResult(EFFECTIVE_ROUNDS_KEY, effectiveRounds);
        }
        
        System.gc();
    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.sugiyama.Sugiyama#setAlgorithmParameters
     * (org.graffiti.plugin.parameter.Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        ((BooleanParameter) data.getAlgorithmParameters()[0]).setValue(false);
        super.setAlgorithmParameters(params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AlgorithmResult getResult() {
        AlgorithmResult res = result;
        result = null;
        return res;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
