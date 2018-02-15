// =============================================================================
//
//   DummyToolFilter.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.tool;

/**
 * {@code ToolFilter}, which filters out all tool dummies and deleted tools.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see ToolDummy
 */
public class DummyToolFilter implements ToolFilter {
    /**
     * {@inheritDoc}. This implementation returns {@code false} for all tool
     * dummies and delted tool.
     * 
     * @see ToolDummy
     */
    public boolean isVisible(Tool<?> tool) {
        return !tool.isDummy() && !tool.isDeleted();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
