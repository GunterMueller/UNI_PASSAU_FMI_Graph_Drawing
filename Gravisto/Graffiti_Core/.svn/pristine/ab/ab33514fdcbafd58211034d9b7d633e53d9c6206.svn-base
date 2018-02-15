// =============================================================================
//
//   ViewFamilyToolFilter.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.tool;

import org.graffiti.plugin.view.interactive.ViewFamily;

/**
 * {@code ToolFilter} that allows for those tools that belong to the currently
 * active tool family.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see Tool#getViewFamily()
 * @see ViewFamily
 */
public class ViewFamilyToolFilter implements ToolFilter {
    // The view family.
    private ViewFamily<?> viewFamily;

    /**
     * Sets the currently active view family.
     * 
     * @param viewFamily
     *            the view family that is currently active.
     */
    public void setCurrentViewFamily(ViewFamily<?> viewFamily) {
        this.viewFamily = viewFamily;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isVisible(Tool<?> tool) {
        return tool.getViewFamily().equals(viewFamily);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
