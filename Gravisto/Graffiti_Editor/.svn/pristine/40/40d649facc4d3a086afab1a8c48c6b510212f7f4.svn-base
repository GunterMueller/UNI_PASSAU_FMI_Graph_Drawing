package org.graffiti.plugins.algorithms.planarity;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * The plugin class for the planarity test
 * 
 * @author Wolfgang Brunner
 */
public class PlanarityPlugin extends GenericPluginAdapter {

    /**
     * Constructs a new <code>PlanarityPlugin</code>
     */
    public PlanarityPlugin() {
        this.algorithms = new Algorithm[2];
        this.algorithms[0] = new PlanarityAlgorithm(false);
        this.algorithms[1] = new PlanarityAlgorithm(true);
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(PLANAR, algorithms, null);
    }

}
