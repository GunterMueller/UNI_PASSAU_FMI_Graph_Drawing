// =============================================================================
//
//   PlanarGraphWithEdgeCross.java
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
import java.util.Random;

/**
 * @author ma
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class PlanarGraphWithEdgeCross extends PlanarGraphSeek<ThicknessList> {

    /** ArrayList contains all Partition of edges */
    ArrayList<ThicknessList> thicknessOfGraph = new ArrayList<ThicknessList>();

    /** Constractor */
    public PlanarGraphWithEdgeCross(HashMap<Integer, LocalEdge> edgeList) {
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
    public Collection<ThicknessList> getPlanarGraph() {
        // TODO Auto-generated method stub
        ArrayList<LocalEdge> edgeSet = new ArrayList<LocalEdge>();
        setEdgeSet(edgeSet);

        while (!edgeSet.isEmpty()) {
            Random random = new Random();
            int nextInt = random.nextInt(edgeSet.size());
            LocalEdge edge = edgeSet.remove(nextInt);
            if (thicknessOfGraph.isEmpty()) {
                ThicknessList thicknessList = new ThicknessList();
                try {
                    thicknessList.setKeySet(this.edgeAttribute.get(edge));
                } catch (Exception e) {
                }
                thicknessList.setEdgeSet(edge);
                thicknessOfGraph.add(thicknessList);
            } else {
                calcuslationThickness(edge);
            }
        }
        return thicknessOfGraph;
    }

    /** calcuslation of then thickness for the Graph */
    private void calcuslationThickness(LocalEdge edge) {
        boolean isInsert = false;

        for (int i = 0; i < this.thicknessOfGraph.size(); i++) {

            ThicknessList thicknessList = thicknessOfGraph.get(i);
            HashMap<LocalEdge, Boolean> hashMap = thicknessList.getKeySet();
            if (!hashMap.containsKey(edge)) {
                thicknessList.setEdgeSet(edge);
                try {
                    thicknessList.setKeySet(this.edgeAttribute.get(edge));
                } catch (Exception e) {
                }
                isInsert = true;
                break;
            }
        }

        if (!isInsert) {
            ThicknessList thicknessList = new ThicknessList();
            try {
                thicknessList.setKeySet(this.edgeAttribute.get(edge));
            } catch (Exception e) {
            }
            thicknessList.setEdgeSet(edge);
            thicknessOfGraph.add(thicknessList);
        }
    }

    /** set the set of edges */
    private void setEdgeSet(ArrayList<LocalEdge> edgeSet) {
        Iterator<Integer> edgeIt = this.edgeList.keySet().iterator();
        while (edgeIt.hasNext()) {
            edgeSet.add(this.edgeList.get(edgeIt.next()));
        }
    }
}
