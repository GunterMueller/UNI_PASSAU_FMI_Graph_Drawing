//==============================================================================
//
//   BKLayoutAlgorithmPlugin.java
//
//   Copyright (c) 2001-2003 Graffiti Team, Uni Passau
//
//==============================================================================
// $Id: BKLayoutAlgorithmPlugin.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.brandeskoepf;

import org.graffiti.plugin.EditorPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;
import org.graffiti.plugin.gui.GraffitiComponent;

/**
 * This is a the plugin class for the brandes/koepf algorithm
 * 
 * @author Florian Fischer
 */
public class BKLayoutAlgorithmPlugin extends EditorPluginAdapter {
    // ~ Constructors
    // ===========================================================

    /**
     * Creates a new AlgorithmBrandesKoepfPlugin object.
     */
    public BKLayoutAlgorithmPlugin() {
        // One plugin can contain more that one algorithm. Therefore the array
        // algorithms is filled with one ore more algorithm objects.
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new BKLayoutAlgorithm();

        // menu
        this.guiComponents = new GraffitiComponent[2];

        this.guiComponents[0] = new BKMenu();
        this.guiComponents[1] = new BKItem();

        // toolbar example
        // this.guiComponents[2] = new BKToolbar();
        // this.guiComponents[3] = new BKButton();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(BETA, algorithms, null);
    }

}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
