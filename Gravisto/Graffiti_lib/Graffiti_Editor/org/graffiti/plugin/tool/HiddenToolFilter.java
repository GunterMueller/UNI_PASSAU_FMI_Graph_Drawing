// =============================================================================
//
//   HiddenToolFilter.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.tool;

/**
 * {@code ToolFilter} that let pass those tools that are not hidden.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see Tool#isHidden()
 */
public class HiddenToolFilter implements ToolFilter {
    /**
     * {@inheritDoc} This implementation returns {@code true} iff the tool is
     * not explicitly hidden.
     * 
     * @see Tool#isHidden()
     */
    public boolean isVisible(Tool<?> tool) {
        return !tool.isHidden();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
