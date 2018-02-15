package org.graffiti.plugins.algorithms.strongconnectivity;

/**
 * @author Kathrin Hanauer
 */

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

public class SCCPlugin extends GenericPluginAdapter {

    public SCCPlugin() {
        this.algorithms = new Algorithm[] { new SCCAlgorithm() };
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(DIRECTED, algorithms, null);
    }

}
