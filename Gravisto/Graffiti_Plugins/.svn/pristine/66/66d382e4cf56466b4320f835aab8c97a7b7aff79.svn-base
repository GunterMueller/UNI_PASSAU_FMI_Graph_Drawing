package org.graffiti.plugins.algorithms.triangulation;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * This class represents the plugin which contains algorithms to triangulate a
 * graph.
 * 
 * @author hofmeier
 */
public class TriangulationPlugin extends GenericPluginAdapter {
    /**
     * Creates a new instance of the class.
     */
    public TriangulationPlugin() {
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new Triangulation();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(PLANAR, algorithms, null);
    }

}
