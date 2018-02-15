// =============================================================================
//
//   ToolPopupMenuProvider.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.tool;

import org.graffiti.plugin.actions.GraffitiAction;
import org.graffiti.plugin.gui.ToolButton;

/**
 * Classes implementing {@code ToolPopupMenuProvider} know how to edit
 * customizable tools. When the user right-clicks on a tool button, a popup menu
 * is shown, which contains the actions provided by {@code
 * ToolPopupMenuProvider}.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see ToolButton
 */
public interface ToolPopupMenuProvider {
    /**
     * Returns an action related to the specified tool. This action will be
     * added to the context menu that is shown when the user right-clicks on the
     * tool button of the specified tool.
     * 
     * @param tool
     *            the tool for which the action to return.
     * @return an action related to the specified tool.
     */
    public GraffitiAction provideActionForTool(Tool<?> tool);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
