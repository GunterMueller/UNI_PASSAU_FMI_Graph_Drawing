// =============================================================================
//
//   Mode.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Mode.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.deprecated;

import java.util.List;

import org.graffiti.plugin.mode.GraphConstraint;

/**
 * The interface <code>Mode</code> describes a general mode in the editor. A
 * mode contains a list of <code>GraphConstraint</code>s and a list of tools.
 * 
 * @deprecated
 */
@Deprecated
public interface Mode {

    /**
     * Returns an array containing the <code>GraphConstraint</code>s of the
     * current <code>Mode</code>.
     * 
     * @return an array containing the <code>GraphConstraint</code>s of the
     *         current <code>Mode</code>.
     */
    public GraphConstraint[] getConstraints();

    /**
     * Returns the name of this mode. Tools can be added to a mode by adding
     * their ToolButtons to the toolbar with the same name as this mode.
     * 
     * @return the name of this mode.
     */
    public String getId();

    /**
     * Returns an array containing all the tools belonging <code>Mode</code>.
     * 
     * @return an array containing all the tools belonging <code>Mode</code>.
     */
    public List<Tool> getTools();

    // do they really make sense in our definition of mode?
    // public void addTool(Tool t);
    // public void removeTool();

    /**
     * Adds the given tool to the mode.
     * 
     * @param t
     *            the tool to add.
     */
    public void addTool(Tool t);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
