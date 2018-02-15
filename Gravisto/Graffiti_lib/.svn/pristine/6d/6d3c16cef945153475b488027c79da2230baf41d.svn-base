package org.graffiti.plugins.algorithms.fpp;

/**
 * @author Le Pham Hai Dang
 */

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

public class FPPPlugin extends GenericPluginAdapter {

    public FPPPlugin() {
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new FPP();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(PLANAR, algorithms, null);
    }

}
