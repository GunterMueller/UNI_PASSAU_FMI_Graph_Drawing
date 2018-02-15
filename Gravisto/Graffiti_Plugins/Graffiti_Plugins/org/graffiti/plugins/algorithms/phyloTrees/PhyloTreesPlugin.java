package org.graffiti.plugins.algorithms.phyloTrees;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * Plugin for drawing phylogenetic trees.
 */
public class PhyloTreesPlugin extends GenericPluginAdapter {

    /**
     * Creates a new PhyloTreesPlugin object.
     */
    public PhyloTreesPlugin() {
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new PhyloTreeAlgorithm();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(TREES, algorithms, null);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
