// =============================================================================
//
//   PermutationGraphChangings.java
//
//   Copyright (c) 2001-2011, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.permutationgraph;

import java.util.Collection;
import java.util.List;

import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * Class contains methods to either calculate the complement or the inverse of a
 * given graph.
 * 
 * @author Emanuel Berndl
 * @version $Revision$ $Date$
 */
public class PermutationGraphChangings {

    /**
     * Method inverts the given graph. It therefore changes the directions for
     * every directed edge of the graph.
     * 
     * @param graph
     *            Graph that is to be inverted.
     * @return The inverted graph.
     */
    public static Graph invertGraph(Graph graph) {
        if (!graph.isDirected()) {
            return null;
        } else {
            Graph invertedGraph = graph;

            for (Edge edge : invertedGraph.getEdges()) {
                Node source = edge.getSource();
                edge.setSource(edge.getTarget());
                edge.setTarget(source);
            }

            return invertedGraph;
        }
    }

    /**
     * Method complements given graph.
     * 
     * @param graph
     *            The graph that is to be complemented.
     * @return The complementgraph to the given graph.
     */
    public static Graph complementGraph(Graph graph) {
        Graph complementGraph = new AdjListGraph();
        List<Node> graphNodes = graph.getNodes();

        // Build the adjacency matrix for the graph
        boolean[][] matrix = new boolean[graphNodes.size()][graphNodes.size()];

        // Fill the matrix
        for (int i = 0; i < matrix.length; i++) {
            for (int j = i + 1; j < matrix.length; j++) {
                if (!graph.getEdges(graphNodes.get(i), graphNodes.get(j))
                        .isEmpty()) {
                    matrix[i][j] = true;
                }
            }
        }

        // Set up the nodes for the new graph
        Node[] complementNodes = new Node[graphNodes.size()];
        for (int i = 0; i < graphNodes.size(); i++) {
            complementNodes[i] = complementGraph.addNode();
        }

        // Set the edges for the complement
        for (int i = 0; i < matrix.length; i++) {
            for (int j = i + 1; j < matrix.length; j++) {
                if (!matrix[i][j]) {
                    complementGraph.addEdge(complementNodes[i],
                            complementNodes[j], false);
                }
            }
        }

        return complementGraph;
    }
    
    
    /**
     * Method combines the two graphs. The two graphs have to be on the same node set. It then adds both edge sets and returns the new graph.
     * 
     * @param graph1    The first graph for the combination.
     * @param graph2    The second graph for the combination.
     * @return  The combined graph of the two given graphs.
     */
    public static Graph combineGraphs (Graph graph1, Graph graph2) {
        if(graph1 == null || graph2 == null || (!graph1.isDirected()) || (!graph2.isDirected()) || graph1.getNodes().size() != graph2.getNodes().size()) {
            return null;
            //TODO exception
        } else {
            Graph combination = new AdjListGraph();
            Node[] combinationNodes = new Node[graph1.getNodes().size()];
            
            // Build up the adjacency matrix
            boolean[][] matrix = new boolean[combinationNodes.length][combinationNodes.length];
            
            // Fill the matrix
            for(int i = 0; i < matrix.length; i++) {
                for(int j = 0; j < matrix.length; j++) {
                   Collection<Edge> edges = graph1.getEdges(graph1.getNodes().get(i), graph1.getNodes().get(j));
                   if(!edges.isEmpty()) {
                       for(Edge edge : edges) {
                           if(edge.getTarget().equals(graph1.getNodes().get(j))) {
                               matrix[i][j] = true;
                           }
                       }
                   }
                   
                   Collection<Edge> edges2 = graph2.getEdges(graph2.getNodes().get(i), graph2.getNodes().get(j));
                   if(!edges2.isEmpty()) {
                       for(Edge edge : edges2) {
                           if(edge.getTarget().equals(graph2.getNodes().get(j))) {
                               matrix[i][j] = true;
                           }
                       }
                   }
                }
            }
            
            // Build the nodes for the new graph
            for(int i = 0; i < graph1.getNodes().size(); i++) {
                combinationNodes[i] = combination.addNode();
            }
            
            // Set the edges for the combined graph
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix.length; j++) {
                    if (matrix[i][j]) {
                        combination.addEdge(combinationNodes[i], combinationNodes[j], true);
                    }
                }
            }
            
            return combination;            
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
