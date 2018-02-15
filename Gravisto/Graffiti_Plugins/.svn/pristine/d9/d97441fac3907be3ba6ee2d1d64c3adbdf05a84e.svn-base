// =============================================================================
//
//   SugiyamaAdapter.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.chebyshev.benchmark;

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
import org.graffiti.plugins.algorithms.sugiyama.crossmin.BilayerCrossCounter;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;
import org.graffiti.plugins.tools.benchmark.NestingAlgorithm;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class SugiyamaBenchmarkAdapter extends Sugiyama implements
        NestingAlgorithm, CalculatingAlgorithm {
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
        SugiyamaAnimation animation = (SugiyamaAnimation) getAnimation();

        animation.nextStep();
        animation.nextStep();

        result = new DefaultAlgorithmResult();

        long startCrossMinTime = System.nanoTime();

        while (animation.hasNextStep()) {
            animation.nextStep();
        }

        long endCrossMinTime = System.nanoTime();

        int crossings = countCrossings(animation);

        result.addToResult("crossings", crossings);
        result.addToResult("time", String.format((Locale) null, "%f",
                (endCrossMinTime - startCrossMinTime) / 1000000000.0));
    }

    private int countCrossings(SugiyamaAnimation animation) {
        int crossings = 0;
        SugiyamaData data = animation.getData();
        int layerCount = data.getLayers().getNumberOfLayers();
        for (int layer = 0; layer < layerCount; layer++) {
            crossings += new BilayerCrossCounter(graph, layer, data)
                    .getNumberOfCrossings();
        }
        return crossings;
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
