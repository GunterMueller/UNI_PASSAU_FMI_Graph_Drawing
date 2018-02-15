// =============================================================================
//
//   OpenGLLabelCommand.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl.label.commands;

import org.graffiti.plugins.views.fast.label.LabelCommand;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class OpenGLLabelCommand extends LabelCommand {
    public abstract void execute(FirstPhaseContext context);

    public abstract void execute(SecondPhaseContext context);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
