// =============================================================================
//
//   FastModeAction.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.commonactions;

import org.graffiti.core.Bundle;
import org.graffiti.plugin.tool.ToolRegistry;
import org.graffiti.plugin.view.interactive.InteractiveView;
import org.graffiti.plugin.view.interactive.ToolAction;
import org.graffiti.plugin.view.interactive.ViewFamily;

/**
 * Common action, which is compatible with every view family.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see ToolRegistry#addCommonAction(ToolAction)
 * @see ViewFamily
 */
public abstract class CommonAction extends ToolAction<InteractiveView<?>> {
    /**
     * Bundle containing the name and description of common actions.
     */
    private static Bundle BUNDLE = Bundle.getBundle(CommonAction.class);

    /**
     * Returns the string for the given key contained in the bundle.
     * 
     * @param key
     *            the key of the string to return.
     * @return the string for the given key contained in the bundle.
     */
    private static String getString(String key) {
        return BUNDLE.getString(key);
    }

    /**
     * Constructs a new common action. The name and description is automatically
     * read from the bundle using the id of the action to construct.
     */
    CommonAction() {
        setName(getString(id + ".name"));
        setDescription(getString(id + ".desc"));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
