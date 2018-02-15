// ==============================================================================
//
//   NetworkFlowPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: NetworkFlowPlugin.java 5766 2010-05-07 18:39:06Z gleissner $

/*
 * Created on 13.07.2004
 */

package org.graffiti.plugins.algorithms.networkFlow;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * Provides a preflow-push maximum-flow algorithm and an algorithm to delete
 * flows and capacities.
 * 
 * @author Markus K�ser
 * @version $Revision 1.0 $
 */
public class NetworkFlowPlugin extends GenericPluginAdapter {

    /**
     * Constructs the <code> NetworkFlowPlugin </code>
     */
    public NetworkFlowPlugin() {
        super();
        this.algorithms = new Algorithm[2];
        this.algorithms[0] = new RemoveNetworkDataAlgorithm();
        this.algorithms[1] = new PreflowPushAlgorithm();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(NETWORK_FLOW, new Algorithm[0],
                new PluginPathNode[] { new PluginPathNode("Preflow Push",
                        algorithms, null) });
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
