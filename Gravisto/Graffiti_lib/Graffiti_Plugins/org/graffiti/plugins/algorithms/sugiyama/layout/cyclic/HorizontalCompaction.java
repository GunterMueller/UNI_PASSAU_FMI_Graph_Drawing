package org.graffiti.plugins.algorithms.sugiyama.layout.cyclic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.graffiti.graph.Node;

class HorizontalCompaction {
    /**
     * Performs the horizontal compaction phase on the block graph. It is
     * composed of several sub-phases:<br>
     * <ul>
     * <li>search for strongly connected components (SCCs) in the block graph
     * <li>layouting every SCC by itself
     * <li>perform a topological sort on the SCCs to determine a sequence for
     * their compaction
     * <li>perform the actual compaction of the SCCs
     * </ul>
     */
    static void execute(Set<Block> blocks, Node[][] layers) {
        // find SCCs
        Collection<SCC> sccs = new Tarjan()
                .findStronglyConnectedComponents(blocks);

        // determine each SCCs inclination
        for (SCC scc : sccs) {
            scc.calculateInclination();
        }

        // due to the new inclinations the distances have changed
        Block.findBlockDistances(blocks, layers);

        // with the correct inclination each SCC can be compactified
        for (SCC scc : sccs) {
            scc.compactify();
        }

        // rebuild all edges of the blockgraph
        // (because some have been removed during SCC layout)
        Block.findNeighbors(blocks, layers);
        Block.findBlockDistances(blocks, layers);

        // compact the layouted SCCs (-> global layout)
        SCC[] topoSortedSCCs = new SCC[sccs.size()];
        TopologicalSort.topoSort(new ArrayList<TopoSortable>(sccs),
                topoSortedSCCs);
        compaction(topoSortedSCCs);
    }

    /**
     * Compactify by moving everything as far as possible to the left and then
     * as far as possible to the right.
     */
    private static void compaction(SCC[] topoSortedSCCs) {
        // from left to right: move blocks to the left
        for (int index = 0; index < topoSortedSCCs.length; index++) {
            topoSortedSCCs[index].moveLeft();
        }

        // from right to left: move blocks to the right
        for (int index = topoSortedSCCs.length - 1; index >= 0; index--) {
            topoSortedSCCs[index].moveRight();
        }
    }
}
