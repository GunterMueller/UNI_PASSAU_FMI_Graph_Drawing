// =============================================================================
//
//   DisjunctiveToolFilter.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.tool;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * The {@code DisjunctiveToolFilter} combines several filters by a disjunction
 * of their conditions, i.e. a tool passes the filter if any of the contained
 * filters returns {@code true}.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class DisjunctiveToolFilter implements ToolFilter {
    /**
     * The filters.
     */
    private Set<ToolFilter> filters;

    /**
     * Constructs a {@code DisjunctiveToolFilter} containing no tool filters.
     * 
     * @see ToolFilter
     */
    public DisjunctiveToolFilter() {
        filters = new HashSet<ToolFilter>();
    }

    /**
     * Constructs a {@code DisjunctiveToolFilter} initially containing the
     * specified filters.
     * 
     * @param filters
     *            the filters this disjunctive filter initially contains.
     */
    public DisjunctiveToolFilter(ToolFilter... filters) {
        this.filters = new HashSet<ToolFilter>(Arrays.asList(filters));
    }

    /**
     * Adds the specified tool filter.
     * 
     * @param filter
     *            the filter to add.
     */
    public void addFilter(ToolFilter filter) {
        filters.add(filter);
    }

    /**
     * Removes the specified tool filter.
     * 
     * @param filter
     *            the filter to remove.
     */
    public void removeFilter(ToolFilter filter) {
        filters.remove(filter);
    }

    /**
     * {@inheritDoc} This implementation returns {@code true}, if any of the
     * contained filters return {@code true}, else {@code false}. The result is
     * lazily evaluated.
     */
    public boolean isVisible(Tool<?> tool) {
        for (ToolFilter filter : filters) {
            if (filter.isVisible(tool))
                return true;
        }
        return false;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
