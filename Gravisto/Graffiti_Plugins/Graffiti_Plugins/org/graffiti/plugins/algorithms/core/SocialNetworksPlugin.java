package org.graffiti.plugins.algorithms.core;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * Provides algorithms to analyse the graph with following methods of social
 * network analysis: core-degree, closeness-centrality and
 * betweenness-centrality. For each method an according leveling is computed and
 * the graph is drawn radial by an extension of the sugiyama framework
 * 
 * @author Matthias H�llm�ller
 */
public class SocialNetworksPlugin extends GenericPluginAdapter {

    /**
     * creates a new SocialNetworksPlugin object
     */
    public SocialNetworksPlugin() {
        this.algorithms = new Algorithm[3];
        this.algorithms[0] = new CoreAlgorithm();
        this.algorithms[1] = new ClosenessAlgorithm();
        this.algorithms[2] = new BetweennessAlgorithm();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(BETA, new Algorithm[0],
                new PluginPathNode[] { new PluginPathNode("Social Networks",
                        algorithms, null) });
    }

}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
