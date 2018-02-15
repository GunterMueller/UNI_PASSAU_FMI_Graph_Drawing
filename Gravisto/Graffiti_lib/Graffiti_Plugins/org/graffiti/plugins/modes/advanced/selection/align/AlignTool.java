// =============================================================================
//
//   SelectionTool.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AlignTool.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced.selection.align;

import org.graffiti.plugins.modes.advanced.FunctionAction;
import org.graffiti.plugins.modes.advanced.PositionInfo;
import org.graffiti.plugins.modes.advanced.ToolPlugin;
import org.graffiti.plugins.modes.advanced.selection.SelectionTool;

/**
 * An editing-tool for selecting and moving graph-elements. Note: A (the) region
 * is an area of the graph during the process of selecting elements (currently
 * always a rectangle).
 * 
 * @deprecated
 */
@Deprecated
public class AlignTool extends SelectionTool {
    /**
     * Creates a new SelectionTool object.
     * 
     * @param toolPlugin
     *            The given ToolPlugin
     * @param positionInfo
     *            The given PositionInfo
     */
    public AlignTool(ToolPlugin toolPlugin, PositionInfo positionInfo) {
        super(toolPlugin, positionInfo);

    }

    /**
     * Returns the FunctionAction of a given function
     * 
     * @param functionName
     *            Given name of the function
     * 
     * @return The action of the function (if not exists: then null)
     */
    @Override
    public FunctionAction getFunctionAction(String functionName) {
        FunctionAction superAction = super.getFunctionAction(functionName);

        return superAction;
    }
}
