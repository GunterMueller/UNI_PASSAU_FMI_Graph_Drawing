package org.graffiti.plugins.algorithms.fas;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

public class FASPlugin extends GenericPluginAdapter {
    /**
     * Creates a new instance of the class.
     */
    public FASPlugin() {
        this.algorithms = new Algorithm[4];
        this.algorithms[0] = new FASRelatedAlgorithms();
        this.algorithms[1] = new FASHeuristic();
        this.algorithms[2] = new FindElementaryCircuits2();
        this.algorithms[3] = new CircuitCounter();

    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode("Feedback Arc Set", algorithms, null);
    }

}
