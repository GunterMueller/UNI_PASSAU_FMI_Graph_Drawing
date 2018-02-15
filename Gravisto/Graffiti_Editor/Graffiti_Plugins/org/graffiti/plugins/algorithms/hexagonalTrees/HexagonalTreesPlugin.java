package org.graffiti.plugins.algorithms.hexagonalTrees;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * @author Unfried
 * 
 */
public class HexagonalTreesPlugin extends GenericPluginAdapter {

    /**
     * 
     */
    public HexagonalTreesPlugin() {
        this.algorithms = new Algorithm[7];
        this.algorithms[0] = new TernaryTreeInHexa2();
        this.algorithms[1] = new PentaTreeInHexa2();
        this.algorithms[2] = new FournaryTreesInHexa2();
        this.algorithms[3] = new TernaryTreesInHexa2LeafsOut();
        this.algorithms[4] = new Hexa2ToHexa();
        this.algorithms[5] = new HexaToHexa2();
        this.algorithms[6] = new UnorderedTernaryTrees();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(TREES, new Algorithm[0],
                new PluginPathNode[] { new PluginPathNode(
                        "Drawings on the Hexagonal Grid 2", algorithms, null) });
    }

}
