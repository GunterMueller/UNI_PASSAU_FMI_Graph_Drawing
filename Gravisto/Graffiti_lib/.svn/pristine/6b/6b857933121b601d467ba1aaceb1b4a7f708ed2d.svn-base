// =============================================================================
//
//   PlanarGraphWithXCoordinate.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//   Created on Jul 7, 2005
// =============================================================================

package org.graffiti.plugins.algorithms.GeoThickness;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * @author ma
 * 
 *         A change of the intersection test problem of swap-line algorithm
 */
public class PlanarGraphWithXCoordinate extends PlanarGraphSeek<LocalEdge> {

    public PlanarGraphWithXCoordinate(HashMap<Integer, LocalEdge> edgeList) {
        super(edgeList);
    }

    /*
     * 
     * 
     * @see
     * org.graffiti.plugins.algorithms.GeoThickness.PlanarGraphSeek#getPlanarGraph
     * ()
     */
    @Override
    public Collection<LocalEdge> getPlanarGraph() {
        // TODO Auto-generated method stub
        // return the planar graph
        Collection<LocalEdge> result = new ArrayList<LocalEdge>();

        // two-three-tree of edge
        this.edgeTree = new TwoThreeTree();

        LocalEdge[] edgeLeftAndRight = null;

        LocalEdge edgeLeft = null;
        LocalEdge edgeRight = null;

        HashMap<Integer, Object> deletedEdge = new HashMap<Integer, Object>();

        while (!this.nodeList.isEmpty()) {
            // for all node in the nodeList
            LocalNode node = (LocalNode) this.nodeList.getElement();

            boolean isLeftNode = node.isLeftNode();

            LocalEdge edge;

            if (isLeftNode) {
                edge = node.getTopEdge();
            } else {
                edge = node.getBottomEdge();
            }

            // System.out.println("id of edge: " + edge.getID());

            if (isLeftNode) {
                // current node is left node of an edge
                // System.out.println("insert edge: " + newString(edge));

                edgeLeftAndRight = this.edgeTree.insert(new EdgeVertex(edge));

                // System.out.println("edge tree: " + this.edgeTree.toString());

                if (geoAlgorithms.crossEdge(edge, edgeLeftAndRight[0])
                        || geoAlgorithms.crossEdge(edge, edgeLeftAndRight[1])) {
                    this.edgeTree.delete(edge);
                    deletedEdge.put(edge.getID(), null);
                }
            } else if (!deletedEdge.containsKey(edge.getID())) {
                // current node is right node of an edge

                // System.out.println("delete edge: " + newString(edge));
                edgeLeftAndRight = this.edgeTree.delete(edge);
                // System.out.println("edge tree: " + this.edgeTree.toString());
                deletedEdge.put(edge.getID(), null);
                result.add(edge);
                this.edgeList.remove(edge.getID());

                edgeLeft = edgeLeftAndRight[0];
                edgeRight = edgeLeftAndRight[1];

                while (geoAlgorithms.crossEdge(edgeLeft, edgeRight)) {
                    if (isBefore(edgeLeft, edgeRight)) {
                        this.edgeTree.delete(edgeRight);
                        deletedEdge.put(edgeRight.getID(), null);
                        edgeRight = this.edgeTree.searchLeftAndRight(edgeLeft)[1];
                    } else {
                        this.edgeTree.delete(edgeLeft);
                        deletedEdge.put(edgeLeft.getID(), null);
                        edgeLeft = this.edgeTree.searchLeftAndRight(edgeRight)[0];
                    }
                }
            }
        }

        return result;
    }

    /**
     * the left node of edge1 has the smaller x coordinate as the left node of
     * edge2
     * 
     * @param edge1
     *            edge
     * @param edge2
     * @return true edge1 stands before the edge2
     */
    private boolean isBefore(LocalEdge edge1, LocalEdge edge2) {
        return edge1.getLeftX() < edge2.getLeftX();
    }
}
