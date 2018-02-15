package org.graffiti.plugins.algorithms.mnn;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;
import org.graffiti.plugins.algorithms.generators.RandomFourConnectedPlanarGraphGenerator;

/**
 * Provides the algorithm of Miura, Nakano, Nishizeki.
 * 
 * @author Thomas Ormteier
 * @version $Revision: 5766 $
 */
public class MnnPlugin extends GenericPluginAdapter {

    /**
     * Creates a new MnnPlugin Object.
     */
    public MnnPlugin() {
        this.algorithms = new Algorithm[3];
        this.algorithms[0] = new RandomFourConnectedPlanarGraphGenerator();
        this.algorithms[1] = new SweeplineAlgorithm();
        this.algorithms[2] = new MnnAlgorithm();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(PLANAR, new Algorithm[0],
                new PluginPathNode[] { new PluginPathNode(
                        "Miura, Kakano, Nishizeki", algorithms, null) });
    }
}
