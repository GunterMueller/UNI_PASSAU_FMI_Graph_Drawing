package org.graffiti.plugins.algorithms.labeling;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;
import org.graffiti.plugins.algorithms.labeling.finitePositions.FinitePositionsAlgorithm;
import org.graffiti.plugins.algorithms.labeling.finitePositionsIndividualWeighting.FinitePositionsAlgorithmIndividualWeighting;
import org.graffiti.plugins.algorithms.labeling.labelGenerator.RandomLabelGenerator;

/**
 * @author scholz
 */
public class LabelingPlugin extends GenericPluginAdapter {
    public LabelingPlugin() {
        this.algorithms = new Algorithm[7];
        this.algorithms[0] = new FinitePositionsAlgorithm();
        this.algorithms[1] = new FinitePositionsAlgorithmIndividualWeighting();
        this.algorithms[2] = new FRSpringLabelingAlgorithmStandard();
        this.algorithms[3] = new FRSpringLabelingAlgorithmSomeParams();
        this.algorithms[4] = new FRSpringLabelingAlgorithmAllParams();
        this.algorithms[5] = new FinitePositionsAndSpringEmbedderAlgorithm();
        this.algorithms[6] = new RandomLabelGenerator();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode("Label Placement Algorithms", algorithms,
                null);
    }
}
