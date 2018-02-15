/*
 * GraphCheckPlugin.java
 * 
 * Copyright (c) 2001-2006 Gravisto Team, University of Passau
 * 
 * Created on Jul 18, 2005
 *
 */

package org.graffiti.plugins.algorithms.graphcheck;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * @author ma
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class GraphCheckPlugin extends GenericPluginAdapter {
    public GraphCheckPlugin() {
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new GraphCheck();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(DELETE, algorithms, null);
    }

}
