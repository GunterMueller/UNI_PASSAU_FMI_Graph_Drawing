/*
 * Created on 17.05.2004
 */

package org.graffiti.plugins.algorithms.clustering;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * Provides algorithms for the terminal seperation problem and the problem of
 * multiway cut. For both clustering Problems there are two algorithms provided,
 * one using the isolating cut method and one using gomory-hu-trees. In this
 * plugin both methods are applied for both problems.
 * 
 * @author Markus K�ser
 * @version $Revision 1.0 $
 */
public class ClusteringPlugin extends GenericPluginAdapter {

    /**
     * Constructs the <code>ClusteringPlugin</code>
     */
    public ClusteringPlugin() {
        super();
        this.algorithms = new Algorithm[4];
        this.algorithms[0] = new IsolatingCutTerminalSeparationAlgorithm();
        this.algorithms[1] = new IsolatingCutClusteringAlgorithm();
        this.algorithms[2] = new GomoryHuTreeTerminalSeparationAlgorithm();
        this.algorithms[3] = new GomoryHuTreeClusteringAlgorithm();
        // this.algorithms[4] = new TestAlgorithm();

    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(DELETE, new Algorithm[0],
                new PluginPathNode[] { new PluginPathNode("Clustering",
                        algorithms, null) });
    }

}
