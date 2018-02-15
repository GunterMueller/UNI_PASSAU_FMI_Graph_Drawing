/*
 * Created on 12.09.2005
 *
 */

package org.graffiti.plugins.algorithms.treeGridDrawings;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * @author Unfried
 * 
 */
public class TreeGridDrawingPlugin extends GenericPluginAdapter {

    /**
     * 
     */
    public TreeGridDrawingPlugin() {
        this.algorithms = new Algorithm[] { new PentaTreeGridAlignment(),
                new YLayout(), new XLayout(), new HDLayoutOn(), new HDLayout() };

    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(TREES, new Algorithm[0],
                new PluginPathNode[] { new PluginPathNode(
                        "Drawings on the Hexagonal Grid", algorithms, null) });
    }

}
