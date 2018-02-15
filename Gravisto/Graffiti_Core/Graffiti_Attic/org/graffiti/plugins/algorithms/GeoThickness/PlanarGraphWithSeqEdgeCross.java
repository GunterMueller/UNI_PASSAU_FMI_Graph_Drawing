// =============================================================================
//
//   PlanarGraphWithSeqEdgeCross.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//   Created on Jul 7, 2005
// =============================================================================

package org.graffiti.plugins.algorithms.GeoThickness;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author ma
 * 
 *         a heuristic is based on greddy algorithm to search maximum planar
 *         subgraph.
 * 
 *         all edges are sorted with the number its crossing. if an edge with
 *         existing edges a crossing has, it is removes. the procedure is always
 *         accomplished, to the graph has no more edge. a maximum Subgraph are
 *         found
 */
public class PlanarGraphWithSeqEdgeCross extends PlanarGraphSeek<LocalEdge> {

    /** number of the method */
    private int nrofmethod;

    /**
     * Constructor
     */
    public PlanarGraphWithSeqEdgeCross(HashMap<Integer, LocalEdge> edgeList,
            int number) {
        super(edgeList);
        this.nrofmethod = number;
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

        HashMap<LocalEdge, Boolean> crossEdgeHash = new HashMap<LocalEdge, Boolean>();

        // TODO Auto-generated method stub
        Collection<LocalEdge> result = new ArrayList<LocalEdge>();

        HeapList heapedges = null;

        boolean hasCross = false;

        if (nrofmethod == 2) {
            heapedges = new HeapList(2, 3);
        } else {
            heapedges = new HeapList(1, 3);
        }

        heapedges.setCompareKey(this.edgeAttribute);

        Iterator<Integer> iterator = this.edgeList.keySet().iterator();

        while (iterator.hasNext()) {
            heapedges.setElement(this.edgeList.get(iterator.next()));
        }

        while (!heapedges.isEmpty()) {
            hasCross = false;

            LocalEdge newEdge = (LocalEdge) heapedges.getElement();

            if (crossEdgeHash.containsKey(newEdge)) {
                hasCross = true;
            } else {
                try {
                    crossEdgeHash.putAll(this.edgeAttribute.get(newEdge));
                } catch (Exception e) {
                }
            }

            if (!hasCross) {
                result.add(newEdge);
                this.edgeList.remove(newEdge.getID());
            }
        }
        return result;
    }
}
