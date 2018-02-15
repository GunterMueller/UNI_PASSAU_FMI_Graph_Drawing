package org.graffiti.plugins.algorithms.sugiyama.layout.cyclic;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements the <a href="http://de.wikipedia.org/wiki/Algorithmus_von_Tarjan_zur_Bestimmung_starker_Zusammenhangskomponenten"
 * > Algorithm of Tarjan</a> to find strongly connected components in the block
 * graph.
 */
class Tarjan {
    private int tarjanDfsNum;
    private Map<Block, Integer> tarjanIndex;
    private Map<Block, Integer> tarjanLowlink;
    private Stack<Block> tarjanStack;

    /** The logger */
    private static final Logger logger = Logger.getLogger(Tarjan.class
            .getName());

    /**
     * Implements the <a href="http://de.wikipedia.org/wiki/Algorithmus_von_Tarjan_zur_Bestimmung_starker_Zusammenhangskomponenten"
     * > Algorithm of Tarjan</a> to find strongly connected components (SCCs) in
     * the block graph.
     * <p>
     * Additionally, the adjacencies between the SCCs are found and stored
     * within each SCC.
     */
    Collection<SCC> findStronglyConnectedComponents(Set<Block> blocks) {
        Collection<SCC> sccs = new LinkedList<SCC>();

        // init the data structures of the Tarjan algorithm
        tarjanDfsNum = 0;
        tarjanIndex = new HashMap<Block, Integer>();
        tarjanLowlink = new HashMap<Block, Integer>();
        tarjanStack = new Stack<Block>();

        // for all root nodes (i.e. blocks)
        for (Block block : blocks)
            // if unvisited then visit
            if (!tarjanIndex.containsKey(block)) {
                tarjan(block, sccs);
            }

        // build the neighborship relations between the SCCs
        for (SCC scc : sccs) {
            scc.findNeighbors();
        }

        return sccs;
    }

    private void tarjan(Block tarjanNode, Collection<SCC> sccs) {
        tarjanIndex.put(tarjanNode, tarjanDfsNum);
        tarjanLowlink.put(tarjanNode, tarjanDfsNum);
        tarjanDfsNum++;
        tarjanStack.push(tarjanNode);
        for (Block neighbor : tarjanNode.getLeftNeighborBlocks()) {
            if (!tarjanIndex.containsKey(neighbor)) {
                tarjan(neighbor, sccs);
                tarjanLowlink.put(tarjanNode, Math.min(tarjanLowlink
                        .get(tarjanNode), tarjanLowlink.get(neighbor)));
            } else if (tarjanStack.contains(neighbor)) {
                tarjanLowlink.put(tarjanNode, Math.min(tarjanLowlink
                        .get(tarjanNode), tarjanIndex.get(neighbor)));
            }
        }

        // found a SCC?
        if (tarjanLowlink.get(tarjanNode).equals(tarjanIndex.get(tarjanNode))) {
            // create new SCC object
            SCC scc = new SCC();
            Block topOfStack;
            do {
                topOfStack = tarjanStack.pop();
                // add tempNode to the current SCC
                scc.blocks.add(topOfStack);
                topOfStack.parentSCC = scc;
            } while (topOfStack != tarjanNode);
            sccs.add(scc);

            if (scc.blocks.size() > 1) {
                logger.log(Level.FINE, "Found SCC (" + scc.blocks.size()
                        + " blocks)");
                // for (Block block : scc.blocks)
                // System.out.println(block);
            }
        }
    }
}
