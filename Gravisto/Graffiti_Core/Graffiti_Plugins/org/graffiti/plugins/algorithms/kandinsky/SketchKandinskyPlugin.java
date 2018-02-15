package org.graffiti.plugins.algorithms.kandinsky;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * The plugin class for the Kandinsky algorithm and the sketch extension.
 * 
 * @author Sonja Zur
 */
public class SketchKandinskyPlugin extends GenericPluginAdapter {

    /**
     * Constructs a new <code>KandinskyPlugin</code>.
     */
    public SketchKandinskyPlugin() {
        this.algorithms = new Algorithm[2];
        this.algorithms[0] = new SketchAlgorithm();
        this.algorithms[1] = new KandinskyAlgorithm();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(PLANAR, new Algorithm[0],
                new PluginPathNode[] { new PluginPathNode(ORTHOGONAL,
                        algorithms, null) });
    }
}
