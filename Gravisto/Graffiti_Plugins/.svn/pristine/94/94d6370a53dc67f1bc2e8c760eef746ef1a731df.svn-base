// =============================================================================
//
//   SetClipCommand.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl.label.commands;

import java.awt.Shape;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class SetClipCommand extends OpenGLLabelCommand {
    private Shape clip;

    public SetClipCommand(Shape clip) {
        this.clip = clip;
    }

    @Override
    public void execute(FirstPhaseContext context) {
        context.setClip(clip);
    }

    @Override
    public void execute(SecondPhaseContext context) {
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
