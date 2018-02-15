/*
 * Created on 12.09.2005
 *
 */

package org.graffiti.plugins.algorithms.treedrawings;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;
import org.graffiti.plugins.algorithms.treedrawings.DAGSplitter.DAGSplitter;
import org.graffiti.plugins.algorithms.treedrawings.RootChanger.RootChanger;
import org.graffiti.plugins.algorithms.treedrawings.TreeKNaryMaker.HelperNodeStripper;
import org.graffiti.plugins.algorithms.treedrawings.TreeKNaryMaker.TreeKNaryMaker;
import org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation.LayoutRefresher;
import org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation.hv.HVLayout;
import org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation.sorter.SubtreeSorter;
import org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation.tipover.TipoverLayout;
import org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation.tipover_mod.TipoverLayoutMod;
import org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation.tipover_mod2.TipoverLayoutMod2;

/**
 * @author Beiqi, Keilhauer
 * 
 */
public class TreeDrawingPlugin extends GenericPluginAdapter {

    /**
     * 
     */
    public TreeDrawingPlugin() {
        this.algorithms = new Algorithm[] { new HVLayout(),
                new TipoverLayout(), new TipoverLayoutMod(),
                new TipoverLayoutMod2(), new LayoutRefresher(),
                new TreeKNaryMaker(), new HelperNodeStripper(),
                new RootChanger(), new DAGSplitter(), new SubtreeSorter() };

    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(TREES, new Algorithm[0],
                new PluginPathNode[] { new PluginPathNode(
                        "hv and Tipover Drawings", algorithms, null) });
    }

}
