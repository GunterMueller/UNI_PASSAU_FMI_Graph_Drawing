package org.graffiti.plugins.algorithms.sugiyama.layout.cyclic;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

class TopologicalSort {
    /**
     * Performs a topological sort. The resulting array starts with the sources
     * of the graph.
     */
    static void topoSort(Collection<TopoSortable> tss, TopoSortable[] result) {
        if (tss.size() != result.length)
            throw new IllegalArgumentException("tss.size() != result.length");

        LinkedList<TopoSortable> sources = new LinkedList<TopoSortable>();
        Map<TopoSortable, Integer> inDegree = new HashMap<TopoSortable, Integer>(
                tss.size());
        int resultIndex = 0;

        // set each TopoSortable's link count
        for (TopoSortable ts : tss) {
            inDegree.put(ts, 0);
        }
        for (TopoSortable ts : tss) {
            for (TopoSortable succ : ts.getTopoSortSuccessors()) {
                if (!inDegree.containsKey(succ))
                    throw new IllegalStateException(
                            "This shouldn't happen - Bug!");
                inDegree.put(succ, inDegree.get(succ) + 1);
            }
        }

        // find the first sources
        for (TopoSortable ts : tss)
            if (inDegree.get(ts) == 0) {
                sources.addLast(ts);
            }

        // remove the sources from the graph and watch for new sources
        while (!sources.isEmpty()) {
            for (TopoSortable successor : sources.getFirst()
                    .getTopoSortSuccessors()) {
                inDegree.put(successor, inDegree.get(successor) - 1);
                if (inDegree.get(successor) == 0) {
                    sources.addLast(successor);
                }
            }
            result[resultIndex] = sources.removeFirst();
            resultIndex++;
        }

        if (resultIndex != tss.size())
            throw new IllegalArgumentException(
                    "The supplied graph could not be topo-sorted! (Cycles?)");
    }
}
