// =============================================================================
//
//   ActionContext.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.java2d.label.commands;

import java.awt.Graphics2D;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class CommandContext {
    public CommandContext(Graphics2D graphics) {
        this.graphics = graphics;
    }

    Graphics2D graphics;
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
