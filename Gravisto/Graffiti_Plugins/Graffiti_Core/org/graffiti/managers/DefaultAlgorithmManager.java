// =============================================================================
//
//   DefaultAlgorithmManager.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DefaultAlgorithmManager.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.managers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.graffiti.managers.pluginmgr.PluginDescription;
import org.graffiti.plugin.GenericPlugin;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * Manages the map of available algorithms: key = algorithm class names, value =
 * algorithm
 * 
 * @version $Revision: 5767 $
 */
public class DefaultAlgorithmManager implements AlgorithmManager {

    /** The algorithms: key = algorithm class names, value = algorithm */
    private Map<String, Algorithm> algorithms;

    /**
     * Constructs a new algorithm manager.
     */
    public DefaultAlgorithmManager() {
        algorithms = new HashMap<String, Algorithm>();
    }

    /*
     * @see org.graffiti.managers.AlgorithmManager#getAlgorithms()
     */
    public List<Algorithm> getAlgorithms() {
        return new LinkedList<Algorithm>(algorithms.values());
    }

    /*
     * @see
     * org.graffiti.managers.AlgorithmManager#getClassName(org.graffiti.plugin
     * .algorithm.Algorithm)
     */
    public String getClassName(Algorithm algorithm) {
        for (Entry<String, Algorithm> entry : algorithms.entrySet())
            if (entry.getValue() == algorithm)
                return entry.getKey();

        // not found:
        return null;
    }

    /*
     * @see
     * org.graffiti.managers.AlgorithmManager#addAlgorithm(org.graffiti.plugin
     * .algorithm.Algorithm)
     */
    public void addAlgorithm(Algorithm algorithm) {
        algorithms.put(algorithm.getClass().getName(), algorithm);
    }

    /*
     * @see
     * org.graffiti.managers.pluginmgr.PluginManagerListener#pluginAdded(org
     * .graffiti.plugin.GenericPlugin,
     * org.graffiti.managers.pluginmgr.PluginDescription)
     */
    public void pluginAdded(GenericPlugin plugin, PluginDescription desc) {
        Algorithm[] algs = plugin.getAlgorithms();

        if (algs != null) {
            for (Algorithm alg : algs) {
                addAlgorithm(alg);
            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
