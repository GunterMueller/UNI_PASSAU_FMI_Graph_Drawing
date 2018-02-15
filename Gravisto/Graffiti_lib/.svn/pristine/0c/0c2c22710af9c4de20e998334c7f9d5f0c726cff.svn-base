/*
 * Created on 23.08.2004
 */

package org.graffiti.plugins.algorithms.betweenness;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * This plugin contains an algorithm for computing the betweenness centrality of
 * the nodes and edges of a graph.
 * 
 * @author Markus K�ser
 * @version $Revision 1.0 $
 */
public class BetweennessPlugin extends GenericPluginAdapter {
    /**
     * Generates a new <code>BetweennessPlugin</code>
     */
    public BetweennessPlugin() {
        super();
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new BetweennessAlgorithm();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(BETA, algorithms, null);
    }

}
