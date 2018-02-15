// =============================================================================
//
//   PermutationCalc.java
//
//   Copyright (c) 2001-2011, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.permutationgraph;

import java.util.List;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * @author Emanuel Berndl
 * @version $Revision$ $Date$
 */
public class PermutationCalc {

    /**
     * Calculates the permutation for the given graph.
     * 
     * @param graph
     *            The graph of the topological sort.
     * @param inverse   The graph of the inverted topological sort.
     * @param graphTopSort
     *            The topological sort of the graph combined with its
     *            complement's topological sort.
     * @param invertedTopSort
     *            The inverse topological sort of the graph combined with its
     *            complement's topological sort.
     * @return The permutation for the given graph.
     */
    public static int[] calculatePermutation(Graph graph, Graph inverse,
            List<Node> graphTopSort, List<Node> invertedTopSort) {
        if(graphTopSort != null && invertedTopSort != null) {
            
            int[] permutation = new int[graph.getNodes().size()];
            
            int[] order = new int[graph.getNodes().size()];
            int[] inverseOrder = new int[graph.getNodes().size()];
            
            // Initialize both arrays
            for (int i = 0; i < graphTopSort.size(); i++) {
                for (int j = 0; j < graph.getNodes().size(); j++) {
                    if (graphTopSort.get(i).equals(graph.getNodes().get(j))) {
                        order[i] = j;
                        break;
                    }
                }
            }
            
            for (int i = 0; i < invertedTopSort.size(); i++) {
                for (int j = 0; j < inverse.getNodes().size(); j++) {
                    if (invertedTopSort.get(i).equals(inverse.getNodes().get(j))) {
                        inverseOrder[i] = j;
                        break;
                    }
                }
            }
            
            int[] arrangedOrder = calculateOrder(order);
            int[] arrangedInverseOrder = calculateOrder(inverseOrder);
            
            for (int i = 0; i < permutation.length; i++) {
                permutation[arrangedInverseOrder[i]] = arrangedOrder[i];
            }
            
            return permutation;
        } else {
            return null;
        }
    }
    
    private static int[] calculateOrder (int[] order) {
        int[] arrangedOrder = new int[order.length];
        
        for(int i = 0; i < order.length; i++) {
            arrangedOrder[order[i]] = i; 
        }
        
        return arrangedOrder;
    }
    
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
