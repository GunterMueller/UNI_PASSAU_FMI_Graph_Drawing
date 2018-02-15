// =============================================================================
//
//   PermutationTopologicalSort.java
//
//   Copyright (c) 2001-2011, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.permutationgraph;

import java.util.ArrayList;
import java.util.List;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * @author Emanuel Berndl
 * @version $Revision$ $Date$
 */
public class PermutationTopologicalSort {

    /**
     * Topologically sorts the nodes of the given graph.
     * 
     * @param graph
     *            The graph that is to be sorted.
     * @return A list of the nodes of the graph in a topological sort or null, if the graph cannot be topologically sorted.
     */
    public static List<Node> topSort(Graph graph) {
        if (graph != null) {
            List<Node> topologicalSort = new ArrayList<Node>();

            int i = 0;
            List<Node> sources = new ArrayList<Node>();
            int[] inDegree = new int[graph.getNodes().size()];

            // Initialize sources and inDegree
            for (int j = 0; j < graph.getNodes().size(); j++) {
                inDegree[j] = graph.getNodes().get(j).getInDegree();
                if (inDegree[j] == 0) {
                    sources.add(graph.getNodes().get(j));
                }
            }

            while (!sources.isEmpty()) {
                Node source = sources.get(0);
                sources.remove(source);
                i++;
                topologicalSort.add(source);

                for (int j = 0; j < graph.getNodes().size(); j++) {
                    if (source.getNeighbors().contains(graph.getNodes().get(j))) {
                        inDegree[j]--;

                        if (inDegree[j] == 0) {
                            sources.add(graph.getNodes().get(j));
                        }
                    }
                }
            }
            
            if(i < graph.getNodes().size()) {
                // Graph is not sorted completely, this got a cycle and cannot be topologically sorted
                return null;
            } else {
                return topologicalSort;                
            }
            
        } else {
            return null;
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
