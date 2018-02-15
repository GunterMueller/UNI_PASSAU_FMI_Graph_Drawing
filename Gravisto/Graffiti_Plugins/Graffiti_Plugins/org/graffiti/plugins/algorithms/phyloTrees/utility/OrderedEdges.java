package org.graffiti.plugins.algorithms.phyloTrees.utility;

import java.util.Arrays;
import java.util.Iterator;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;

/**
 * This class provides ordering of the outgoing edges of a directed graph.
 * 
 * @author Peter Häring
 * @see PhyloTreeConstants#PATH_EDGE_NUMBER
 */
public class OrderedEdges {

    /**
     * Returns the edges of a Node, ordered according to their numbering.
     * 
     * If every Edge is numbered and the numbering is complete, i.e. every Edge
     * has an unique integer number in 1..n, where n is the number of children
     * of the Node, then this algorithm sorts in O(n). Otherwise the attributes
     * are set using updateEdgeNumbering and the algorithm runs in O(n * log n).
     * 
     * @param node
     *            The node whose outgoing edges are to be sorted and returned.
     * @return Array with outgoing edges of node, sorted according to their
     *         numbering.
     */
    public static Edge[] getOrderedEdges(Node node) {
        Edge[] sortedEdges = new Edge[node.getOutDegree()];

        Iterator<Edge> it = node.getAllOutEdges().iterator();
        boolean sortingFailure = false;

        while (it.hasNext() && !sortingFailure) {
            Edge edge = it.next();

            if (edge.containsAttribute(PhyloTreeConstants.PATH_EDGE_NUMBER)) {
                int number = edge
                        .getInteger(PhyloTreeConstants.PATH_EDGE_NUMBER);

                // test for possible illegal conditions
                if (number <= 0 || number > sortedEdges.length
                        || sortedEdges[number - 1] != null) {
                    sortingFailure = true;
                } else {
                    sortedEdges[number - 1] = edge;
                }
            } else {
                sortingFailure = true;
            }
        }

        if (sortingFailure) {
            // solve problem of inconsistent numbering
            updateEdgeNumbering(node);
            // run this algorithm again
            return getOrderedEdges(node);
        }

        return sortedEdges;
    }

    /**
     * Sets the numbering of the adjacent outgoing edges of the node given as a
     * parameter in a manner consistent with the set numbering. Edges will be
     * numbered from 1..n, where n is the number of outgoing edges.
     * 
     * This algorithm performs in O(n * log n).
     * 
     * @param node
     *            The nodes whose outgoing edges are to be set.
     */
    public static void updateEdgeNumbering(Node node) {
        // get edges
        Edge[] edgeArray = new Edge[node.getOutDegree()];
        int counter = 0;
        for (Edge e : node.getAllOutEdges()) {
            edgeArray[counter] = e;
            ++counter;
        }

        // sort edges
        Arrays.sort(edgeArray, new EdgeNumberingComparator());

        // set new numbering
        for (int i = 0; i < edgeArray.length; ++i) {
            PhyloTreeUtil.setEdgeNumber(edgeArray[i], i + 1);
        }
    }
}
