// =============================================================================
//
//   AlgorithmExamplePlugin.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.guideexample;

import org.graffiti.core.Bundle;
import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * @author Kathrin Hanauer
 * @version $Revision$ $Date$
 */
public class GuideExampleAlgorithmPlugin extends GenericPluginAdapter {
    /** Resource bundle for the plugin. */
    private Bundle bundle = Bundle.getBundle(getClass());

    /**
     * Creates a new algorithm plugin.
     */
    public GuideExampleAlgorithmPlugin() {
        this.algorithms = new Algorithm[] { new GuideExampleAlgorithm(),
                new DemoAlgorithm() };
    }

    /*
     * @see org.graffiti.plugin.GenericPluginAdapter#getPathInformation()
     */
    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(bundle.getString("path.guide"), null,
                new PluginPathNode[] {
                        new PluginPathNode(bundle
                                .getString("path.guide.examples"),
                                new Algorithm[] { algorithms[0] }, null),
                        new PluginPathNode(
                                bundle.getString("path.guide.demos"),
                                new Algorithm[] { algorithms[1] }, null) });
    }

    @Override
    public String getName() {
        return bundle.getString("name");
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
