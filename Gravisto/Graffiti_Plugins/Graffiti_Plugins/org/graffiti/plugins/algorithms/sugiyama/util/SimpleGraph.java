// =============================================================================
//
//   SimpleGraph.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class SimpleGraph {
    private int nodeCount;
    private int edgeCount;
    private Graph originalGraph;
    private Node[] originalNodes;
    private Edge[] originalEdges;
    private Map<GraphElement, Integer> map;
    private int[] sources;
    private int[] targets;

    private int[][] inEdges;
    private int[][] outEdges;
    private int[][] allEdges;
    private int[][] inNeighbors;
    private int[][] outNeighbors;
    private int[][] allNeighbors;

    public SimpleGraph(Graph originalGraph) {
        this.originalGraph = originalGraph;
        nodeCount = originalGraph.getNumberOfNodes();
        edgeCount = originalGraph.getNumberOfEdges();
        originalNodes = originalGraph.getNodes().toArray(new Node[nodeCount]);
        originalEdges = originalGraph.getEdges().toArray(new Edge[edgeCount]);

        map = new HashMap<GraphElement, Integer>();

        sources = new int[edgeCount];
        targets = new int[edgeCount];
        inEdges = new int[nodeCount][];
        outEdges = new int[nodeCount][];
        allEdges = new int[nodeCount][];
        inNeighbors = new int[nodeCount][];
        outNeighbors = new int[nodeCount][];
        allNeighbors = new int[nodeCount][];

        for (int i = 0; i < nodeCount; i++) {
            map.put(originalNodes[i], i);
        }

        for (int i = 0; i < edgeCount; i++) {
            Edge edge = originalEdges[i];
            map.put(edge, i);
            sources[i] = map.get(edge.getSource());
            targets[i] = map.get(edge.getTarget());
        }

        for (int i = 0; i < nodeCount; i++) {
            Node node = originalNodes[i];
            Collection<Edge> originalInEdges = node.getDirectedInEdges();
            Collection<Edge> originalOutEdges = node.getDirectedOutEdges();
            Collection<Edge> originalUndirectedEdges = node
                    .getUndirectedEdges();

            int inDegree = originalInEdges.size();
            int outDegree = originalOutEdges.size();
            int degree = originalUndirectedEdges.size() + inDegree + outDegree;

            int[] localInEdges = new int[inDegree];
            int[] localOutEdges = new int[outDegree];
            int[] localAllEdges = new int[degree];
            int[] localInNeighbors = new int[inDegree];
            int[] localOutNeighbors = new int[outDegree];
            int[] localAllNeighbors = new int[degree];

            int allIndex = 0;
            int inIndex = 0;
            int outIndex = 0;

            for (Edge edge : originalInEdges) {
                int ee = map.get(edge);
                int nn = sources[ee];
                localInEdges[inIndex] = ee;
                localInNeighbors[inIndex] = nn;
                localAllEdges[allIndex] = ee;
                localAllNeighbors[allIndex] = nn;

                inIndex++;
                allIndex++;
            }

            for (Edge edge : originalUndirectedEdges) {
                int ee = map.get(edge);
                int nn = edge.getSource() == node ? targets[ee] : sources[ee];
                localAllEdges[allIndex] = ee;
                localAllNeighbors[allIndex] = nn;

                allIndex++;
            }

            for (Edge edge : originalOutEdges) {
                int ee = map.get(edge);
                int nn = targets[ee];
                localOutEdges[outIndex] = ee;
                localOutNeighbors[outIndex] = nn;
                localAllEdges[allIndex] = ee;
                localAllNeighbors[allIndex] = nn;

                outIndex++;
                allIndex++;
            }

            inEdges[i] = localInEdges;
            outEdges[i] = localOutEdges;
            allEdges[i] = localAllEdges;
            inNeighbors[i] = localInNeighbors;
            outNeighbors[i] = localOutNeighbors;
            allNeighbors[i] = localAllNeighbors;
        }
    }

    public int getSource(int i) {
        return sources[i];
    }

    public int getTarget(int i) {
        return targets[i];
    }

    public int[] getInEdges(int i) {
        return inEdges[i];
    }

    public int[] getOutEdges(int i) {
        return outEdges[i];
    }

    public int[] getAllEdges(int i) {
        return allEdges[i];
    }

    public int[] getInNeighbors(int i) {
        return inNeighbors[i];
    }

    public int[] getOutNeighbors(int i) {
        return outNeighbors[i];
    }

    public int[] getAllNeighbors(int i) {
        return allNeighbors[i];
    }

    public Node getNode(int i) {
        return originalNodes[i];
    }

    public Edge getEdge(int i) {
        return originalEdges[i];
    }

    public Graph getGraph() {
        return originalGraph;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public int getEdgeCount() {
        return edgeCount;
    }

    public int getIndex(GraphElement element) {
        return map.get(element);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
