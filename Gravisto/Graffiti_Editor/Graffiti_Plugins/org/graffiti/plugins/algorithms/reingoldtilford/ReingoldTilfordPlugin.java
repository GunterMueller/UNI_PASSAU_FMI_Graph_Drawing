// =============================================================================
//
//   ReingoldTilfordPlugin.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.reingoldtilford;

import java.util.LinkedList;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;
import org.graffiti.plugins.tools.debug.DebugUtil;

/**
 * Provides a Reingold-Tilford algorithm.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision 0$
 */
public class ReingoldTilfordPlugin extends GenericPluginAdapter {
    /**
     * Creates a new <code>ReingoldTilfordPlugin</code> object.
     */
    public ReingoldTilfordPlugin() {
        algorithms = new Algorithm[] { new ReingoldTilfordAlgorithm() };
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(TREES, algorithms, null);
    }

    public static String getDebugLabel(TreeCombinationStack stack) {
        LinkedList<Tree> trees = stack.getTrees();
        if (trees.isEmpty())
            return "";
        return getParentDebugLabel(trees.getFirst());
    }

    public static String getParentDebugLabel(Tree tree) {
        Tree parent = tree.getParent();
        if (parent == null)
            return "";
        return getDebugLabel(parent);
    }

    public static String getDebugLabel(Tree tree) {
        return DebugUtil.getDebugLabel(tree.getNode());
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
