// =============================================================================
//
//   LevelNormalizer.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.gridsifting;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
class LevelNormalizer {
    private BlockGraph graph;
    private NodeBlock[] nodeBlocks;
    private boolean[] hit;
    private int[] phiToNormalized;

    public LevelNormalizer(BlockGraph graph) {
        this.graph = graph;
        nodeBlocks = graph.nodeBlocks;
        int maxLevelCount = 2 * nodeBlocks.length + 10;
        hit = new boolean[2 * maxLevelCount];
        phiToNormalized = new int[maxLevelCount];
    }

    public void normalizeLevels() {
        for (NodeBlock nodeBlock : nodeBlocks) {
            int phi = nodeBlock.phi;
            hit[phi] = true;
        }

        int nextIndex = 2;

        int levelCount = graph.levelCount;

        for (int phi = 1; phi <= levelCount; phi++) {
            if (hit[phi]) {
                phiToNormalized[phi] = nextIndex;
                nextIndex += 2;
                hit[phi] = false;
            }
        }

        for (NodeBlock nodeBlock : nodeBlocks) {
            nodeBlock.phi = phiToNormalized[nodeBlock.phi];
        }

        graph.levelCount = nextIndex - 1;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
