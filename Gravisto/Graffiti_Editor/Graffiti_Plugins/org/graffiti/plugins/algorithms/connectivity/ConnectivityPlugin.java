package org.graffiti.plugins.algorithms.connectivity;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * The plugin class for the connectivity testing algorithms
 * 
 * @author Wolfgang Brunner
 */
public class ConnectivityPlugin extends GenericPluginAdapter {

    /**
     * Constructs a new <code>PlanarityPlugin</code>
     */
    public ConnectivityPlugin() {
        this.algorithms = new Algorithm[4];
        this.algorithms[0] = new Connect();
        this.algorithms[1] = new Biconnect();
        this.algorithms[2] = new Triconnect();
        this.algorithms[3] = new FourconnectSlow();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode("Connectivity", algorithms, null);
    }

}
