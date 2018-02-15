//=============================================================================
//
//   EdgeTab.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
//$Id: EdgeTab.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.inspectors.yagi;

/**
 * Represents a tabulator in the inspector, which handles the properties of
 * edges.
 */
public class EdgeTab extends AbstractTab {

    /**
     * 
     */
    private static final long serialVersionUID = -954849355128485963L;

    /**
     * Constructs a EdgeTab and sets the title.
     */
    public EdgeTab() {
        this.title = "Edge";
        this.type = ViewTab.EDGE;
    }

    /**
     * Returns a copy of this EdgeTab.
     */
    @Override
    public Object clone() {
        return new EdgeTab();
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
