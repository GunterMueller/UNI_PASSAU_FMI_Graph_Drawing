package org.graffiti.plugins.algorithms.circulardrawing;

import org.graffiti.plugin.EditorPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

public class CircularPlugin extends EditorPluginAdapter {
    public CircularPlugin() {
        this.algorithms = new Algorithm[3];
        this.algorithms[0] = new Circular();
        this.algorithms[1] = new CircularPostprocessing();
        this.algorithms[2] = new DFSCircular();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(DELETE, new Algorithm[0],
                new PluginPathNode[] { new PluginPathNode("Circular Drawings",
                        algorithms, null) });
    }

}
