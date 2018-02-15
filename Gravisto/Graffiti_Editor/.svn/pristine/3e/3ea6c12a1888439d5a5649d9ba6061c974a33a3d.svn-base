/*
 * ThicknessList.java
 * 
 * Copyright (c) 2001-2006 Gravisto Team, University of Passau
 * 
 * Created on Aug 22, 2005
 *
 */

package org.graffiti.plugins.algorithms.GeoThickness;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * @author ma
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class ThicknessList {

    private HashMap<LocalEdge, Boolean> crossEdge = new HashMap<LocalEdge, Boolean>();

    private Collection<LocalEdge> edgeList = new ArrayList<LocalEdge>();

    public HashMap<LocalEdge, Boolean> getKeySet() {
        return this.crossEdge;
    }

    public Collection<LocalEdge> getEdgeSet() {
        return this.edgeList;
    }

    public void setKeySet(HashMap<LocalEdge, Boolean> keySet) {
        this.crossEdge.putAll(keySet);
    }

    public void setEdgeSet(LocalEdge edge) {
        this.edgeList.add(edge);
    }

}
