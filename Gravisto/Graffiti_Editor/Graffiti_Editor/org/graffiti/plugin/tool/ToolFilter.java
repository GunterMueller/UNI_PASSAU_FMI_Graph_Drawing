// =============================================================================
//
//   ToolFilter.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.tool;

/**
 * Classes implementing {@code ToolFilter} control the set of currently visible
 * tools. Tool filters are added to the registry by
 * {@link ToolRegistry#addToolFilter(ToolFilter)}.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see ToolRegistry
 * @see Tool
 */
public interface ToolFilter {
    /**
     * Returns if the specified tool shall be visible.
     * 
     * @param tool
     *            the tool in question.
     * @return {@code true} iff the specified tool shall be visible.
     */
    public boolean isVisible(Tool<?> tool);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
