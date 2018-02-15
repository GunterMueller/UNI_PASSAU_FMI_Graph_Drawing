package org.graffiti.plugins.algorithms.phyloTrees.utility;

import java.util.Comparator;

import org.graffiti.graph.Edge;

/**
 * Compare two edges according to their numbering.
 * 
 * @see PhyloTreeConstants#PATH_EDGE_NUMBER
 */
public class EdgeNumberingComparator implements Comparator<Edge> {

    /**
     * Returns a value less than, equal to or larger than 0 if the number of the
     * first parameter is smaller, equal or larger than that of the second
     * parameter.
     * 
     * If either element has no numbering it is asumed to be larger than the
     * other element. If both elements have no numbering, they are assumed to be
     * equal.
     * 
     * @param e1
     *            The first Edge.
     * @param e2
     *            The second Edge.
     * @return A value less than, equal to or larger than 0 if the number of the
     *         first parameter is smaller, equal or larger than that of the
     *         second parameter.
     */
    public int compare(Edge e1, Edge e2) {
        int position1;
        int position2;

        final String numberPath = PhyloTreeConstants.PATH_EDGE_NUMBER;

        if (e1.containsAttribute(numberPath)) {
            position1 = e1.getInteger(PhyloTreeConstants.PATH_EDGE_NUMBER);
        } else {
            position1 = Integer.MAX_VALUE;
        }

        if (e2.containsAttribute(numberPath)) {
            position2 = e2.getInteger(PhyloTreeConstants.PATH_EDGE_NUMBER);
        } else {
            position2 = Integer.MAX_VALUE;
        }

        return position1 - position2;
    }
}
