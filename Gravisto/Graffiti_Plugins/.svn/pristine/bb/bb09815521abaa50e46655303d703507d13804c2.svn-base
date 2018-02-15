// =============================================================================
//
//   EdgeAdapterStub.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package tests.graffiti.plugins.algorithms.mst;

import org.graffiti.plugins.algorithms.mst.adapters.EdgeAdapter;

/**
 * @author Harald
 * @version $Revision$ $Date$
 */
public class EdgeAdapterStub extends EdgeAdapter {
    private float weight = 1f;
    private boolean isSelected = false;
    private boolean isColored = false;

    public EdgeAdapterStub() {
        this(false);
    }

    public EdgeAdapterStub(boolean isColored) {
        this.isColored = isColored;
    }

    @Override
    public float getWeight() {
        return weight;
    }

    @Override
    public void select() {
        isSelected = true;
    }

    @Override
    public void unselect() {
        isSelected = false;
    }

    @Override
    public void init() {

    }

    @Override
    public void clean() {

    }

    @Override
    public void clear() {

    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public boolean isColored() {
        return isColored;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
