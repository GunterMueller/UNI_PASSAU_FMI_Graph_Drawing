//=============================================================================
//
//   NodeTab.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
//$Id: NodeTab.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.inspectors.yagi;

/**
 * Represents a tabulator in the inspector, which handles the properties of
 * nodes.
 */
public class NodeTab extends AbstractTab {

    /**
     * 
     */
    private static final long serialVersionUID = -6488474637389180940L;

    /**
     * Constructs a NodeTab and sets the title.
     */
    public NodeTab() {
        this.title = "Node";
        this.type = ViewTab.NODE;
    }

    /**
     * Returns a copy of this NodeTab.
     */
    @Override
    public Object clone() {
        return new NodeTab();
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
