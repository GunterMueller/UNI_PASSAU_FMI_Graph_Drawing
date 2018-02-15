// =============================================================================
//
//   InverseToolFilter.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.tool;

/**
 * {@code ToolFilter}, which lets pass the complement of tools admitted by
 * another tool filter.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class NegatingToolFilter implements ToolFilter {
    /**
     * The tool filter whose responses are negated.
     */
    private ToolFilter filter;

    /**
     * Constructs a {@code ToolFilter}, which negates the responses of the
     * specified tool filter.
     * 
     * @param filter
     *            the {@link ToolFilter}, whose responses are to be negated by
     *            this tool filter.
     */
    public NegatingToolFilter(ToolFilter filter) {
        this.filter = filter;
    }

    /**
     * {@inheritDoc} This implementation delegates the question to the contained
     * tool filter and returns the negation of its response.
     */
    public boolean isVisible(Tool<?> tool) {
        return !filter.isVisible(tool);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
