// =============================================================================
//
//   FastViewAction.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.actions;

import org.graffiti.plugin.view.interactive.ToolAction;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.plugins.views.fast.FastViewPlugin;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class FastViewAction extends ToolAction<FastView> {
    protected FastViewAction() {
        setName(FastViewPlugin.getString("actions." + id + ".name"));
        setDescription(FastViewPlugin.getString("actions." + id + ".desc"));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
