package org.graffiti.plugins.algorithms.fiftyconnected;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * @author Szarowski Szymon
 */
public class FiftyPlugin extends GenericPluginAdapter {
    public FiftyPlugin() {
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new FiftyAlgorithm();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(DELETE, algorithms, null);
    }

}
