// =============================================================================
//
//   ToolFactory.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.tool;

import org.graffiti.plugin.view.interactive.InteractiveView;
import org.graffiti.plugin.view.interactive.ViewFamily;

/**
 * Classes implementing {@code ToolFactory} know how to create tools.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public interface ToolFactory {
    /**
     * @return the id of this factory. Different instances of {@code
     *         ToolFactory} must return different ids.
     */
    public String getId();

    /**
     * Creates a new tool with the specified id for the specified view family.
     * 
     * @param <T>
     *            the superclass of all views belonging to the specified view
     *            family.
     * @param id
     *            the id of the tool to create.
     * @param viewFamily
     *            the view family that is supported by the tool to create.
     * @return a new tool with the specified id for the specified view family.
     */
    public <T extends InteractiveView<T>> Tool<T> create(String id,
            ViewFamily<T> viewFamily);

    /**
     * Returns if this factory can create tools that support the specified view
     * family.
     * 
     * @param viewFamily
     *            the view family in question.
     * @return {@code true} iff this factory can create tools that support the
     *         specified view family.
     */
    public boolean acceptsViewFamily(ViewFamily<?> viewFamily);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
