package org.graffiti.plugins.algorithms.eades;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

public class EadesPlugin extends GenericPluginAdapter {
    public EadesPlugin() {
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new AlgorithmEades();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(DELETE, algorithms, null);
    }

}
