// =============================================================================
//
//   SimpleConnectivity.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.stackqueuelayout;

import org.graffiti.graph.Graph;
import org.graffiti.plugins.algorithms.sugiyama.util.SimpleGraph;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class SimpleConnectivity {
    private SimpleGraph simpleGraph;
    private boolean[] visited;
    private int foundCount;
    
    private SimpleConnectivity(Graph graph) {
        simpleGraph = new SimpleGraph(graph);
        visited = new boolean[simpleGraph.getNodeCount()];
    }
    
    private boolean isConnected() {
        foundCount = 0;
        dfs(0);
        return foundCount == simpleGraph.getNodeCount();
    }
    
    private void dfs(int node) {
        if (!visited[node]) {
            visited[node] = true;
            foundCount++;
            for (int n : simpleGraph.getAllNeighbors(node)) {
                dfs(n);
            }
        }
    }
    
    public static boolean isConnected(Graph graph) {
        return new SimpleConnectivity(graph).isConnected(); 
    }
}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
