/**
 * This class represents the plugin which contains algorithms to calculate
 * upward and draw the graph according to these.
 * 
 * @author Jin
 */

package org.graffiti.plugins.algorithms.upward;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

public class UpwardPlugin extends GenericPluginAdapter {
    /**
     * Creates a new instance of the class.
     */
    public UpwardPlugin() {
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new UpwardAdministration();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(DELETE, algorithms, null);
    }

}