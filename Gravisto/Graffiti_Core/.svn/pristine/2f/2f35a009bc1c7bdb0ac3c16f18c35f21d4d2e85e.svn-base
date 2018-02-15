/*
 * PlanarGraphWithLangOfEdge.java
 * 
 * Copyright (c) 2001-2006 Gravisto Team, University of Passau
 * 
 * Created on Nov 5, 2005
 *
 */

package org.graffiti.plugins.algorithms.GeoThickness;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author ma
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class PlanarGraphWithLangOfEdge extends PlanarGraphSeek<LocalEdge> {
    /**
     * Constructor
     */
    public PlanarGraphWithLangOfEdge(HashMap<Integer, LocalEdge> edgeList,
            int number) {
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

        HashMap<LocalEdge, Boolean> crossEdgeHash = new HashMap<LocalEdge, Boolean>();

        // TODO Auto-generated method stub
        Collection<LocalEdge> result = new ArrayList<LocalEdge>();

        HeapList heapedges = null;

        boolean hasCross = false;

        heapedges = new HeapList(1, 4);

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
