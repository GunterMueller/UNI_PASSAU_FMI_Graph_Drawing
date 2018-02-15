package org.graffiti.plugins.algorithms.lapvis;

/**
 * @author Le Pham Hai Dang
 */

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

public class LAPVisualizationPlugin extends GenericPluginAdapter {

    public LAPVisualizationPlugin() {
        this.algorithms = new Algorithm[] { new LAPVisualizationAlgorithm() };
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(ARBITRARY, algorithms, null);
    }

}
