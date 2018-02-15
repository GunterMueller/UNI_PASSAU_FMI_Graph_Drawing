package org.graffiti.plugins.algorithms.tutte;

/**
 * @author hanauer
 */

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

public class TutteDrawingPlugin extends GenericPluginAdapter {

    public TutteDrawingPlugin() {
        this.algorithms = new Algorithm[] { new IdentifyOuterFaceAlgorithm(), new TutteAlgorithm() };
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(PLANAR, algorithms, null);
    }

}
