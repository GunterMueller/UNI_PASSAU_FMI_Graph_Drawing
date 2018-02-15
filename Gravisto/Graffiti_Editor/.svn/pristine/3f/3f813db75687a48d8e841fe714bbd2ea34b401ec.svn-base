// =============================================================================
//
//   TesselationDataList.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class TesselationDataList {
    private ArrayList<TesselationData> list;
    private int vertexCount;
    private int indexCount;

    public TesselationDataList() {
        list = new ArrayList<TesselationData>();
        vertexCount = 0;
        indexCount = 0;
    }

    public TesselationDataList(TesselationData data) {
        this();
        add(data);
    }

    public void add(TesselationData data) {
        list.add(data);
        vertexCount += data.getVertexCount();
        indexCount += data.getIndexCount();
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getIndexCount() {
        return indexCount;
    }

    public Collection<TesselationData> getCollection() {
        return list;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
