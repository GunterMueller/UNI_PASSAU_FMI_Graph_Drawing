// =============================================================================
//
//   ConjunctiveToolFilter.java
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
 * The {@code ConjunctiveToolFilter} combines several filters by a conjunction
 * of their conditions, i.e. a tool passes the filter if all contained filters
 * return {@code true}.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class ConjunctiveToolFilter implements ToolFilter {
    /**
     * The filters.
     */
    private Set<ToolFilter> filters;

    /**
     * Constructs a {@code ConjunctiveToolFilter} containing no tool filters.
     * 
     * @see ToolFilter
     */
    public ConjunctiveToolFilter() {
        filters = new HashSet<ToolFilter>();
    }

    /**
     * Constructs a {@code ConjunctiveToolFilter} initially containing the
     * specified filters.
     * 
     * @param filters
     *            the filters this conjunctive filter initially contains.
     */
    public ConjunctiveToolFilter(ToolFilter... filters) {
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
     * {@inheritDoc} This implementation returns {@code true}, if all contained
     * filters return {@code true}, else {@code false}. The result is lazily
     * evaluated.
     */
    public boolean isVisible(Tool<?> tool) {
        for (ToolFilter filter : filters) {
            if (!filter.isVisible(tool))
                return false;
        }
        return true;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
