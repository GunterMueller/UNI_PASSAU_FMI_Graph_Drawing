/**
 * 
 */
package org.graffiti.plugins.algorithms.graviso;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * @author lenhardt
 * 
 */
public class GravisoPlugin extends GenericPluginAdapter {

    /**
	 * 
	 */
    public GravisoPlugin() {
        this.algorithms = new Algorithm[2];
        this.algorithms[0] = new RefinementAlgorithmSimple();
        this.algorithms[1] = new RefinementAlgorithmLabelsEqual();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(ISOMORPHISM, algorithms, null);
    }

}
