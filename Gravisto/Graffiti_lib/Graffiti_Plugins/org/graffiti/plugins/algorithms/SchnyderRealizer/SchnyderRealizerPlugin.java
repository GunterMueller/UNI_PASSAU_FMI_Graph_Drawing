package org.graffiti.plugins.algorithms.SchnyderRealizer;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * This class represents the plugin which contains algorithms to calculate
 * Schnyder`s realizers and draw the graph according to these.
 * 
 * @author hofmeier
 */
public class SchnyderRealizerPlugin extends GenericPluginAdapter {
    /**
     * Creates a new instance of the class.
     */
    public SchnyderRealizerPlugin() {
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new SchnyderRealizerAdministration();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(PLANAR, algorithms, null);
    }

}
