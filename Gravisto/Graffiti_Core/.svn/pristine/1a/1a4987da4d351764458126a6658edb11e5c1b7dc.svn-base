// =============================================================================
//
//   DefaultModeFilter.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.tool;

import java.util.HashSet;
import java.util.Set;

/**
 * {@code ToolFilter}, which lets pass all tools that have their default mode
 * flag set or all tools if the default mode is not active. The default mode is
 * considered to be active if there is no veto against it.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see Tool#isDefaultMode()
 * @see ToolRegistry
 */
public class DefaultModeFilter implements ToolFilter {
    /**
     * The set of vetos.
     */
    private Set<Object> vetos;

    /**
     * Denotes if the default mode is active.
     */
    private boolean isDefaultMode;

    /**
     * Creates a {@code DefaultModeFilter}.
     */
    public DefaultModeFilter() {
        vetos = new HashSet<Object>();
        isDefaultMode = true;
    }

    /**
     * Adds the specified veto against the default mode. The default mode is
     * considered inactive as long as there is at least one veto against it.
     * 
     * @param veto
     *            the veto to add against the default mode.
     * @see #removeVeto(Object)
     */
    public void addVeto(Object veto) {
        vetos.add(veto);
        isDefaultMode = vetos.isEmpty();
    }

    /**
     * Removes the specified veto against the default mode.
     * 
     * @param veto
     *            the veto to remove.
     * @see #addVeto(Object)
     */
    public void removeVeto(Object veto) {
        vetos.remove(veto);
        isDefaultMode = vetos.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isVisible(Tool<?> tool) {
        return !isDefaultMode || tool.isDefaultMode;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
