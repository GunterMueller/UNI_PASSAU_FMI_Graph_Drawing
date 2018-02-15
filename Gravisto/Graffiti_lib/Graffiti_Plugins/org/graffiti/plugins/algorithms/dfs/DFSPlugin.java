package org.graffiti.plugins.algorithms.dfs;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * A simple DFS plugin.
 * 
 */
public class DFSPlugin extends GenericPluginAdapter {
    /**
     * Creates a new DFSPlugin object.
     */
    public DFSPlugin() {
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new DFS(new DFSHeightLabeler());
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(BETA, algorithms, null);
    }

}
