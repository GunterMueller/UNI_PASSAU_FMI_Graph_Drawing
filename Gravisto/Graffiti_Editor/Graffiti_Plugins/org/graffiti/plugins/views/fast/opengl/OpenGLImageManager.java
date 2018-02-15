// =============================================================================
//
//   OpenGLImageManager.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl;

import java.awt.image.BufferedImage;

import org.graffiti.plugins.views.fast.ImageManager;
import org.graffiti.plugins.views.fast.opengl.label.OpenGLLabel;
import org.graffiti.plugins.views.fast.opengl.label.OpenGLLabelManager;
import org.graffiti.plugins.views.fast.opengl.label.commands.OpenGLLabelCommand;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class OpenGLImageManager extends
        ImageManager<OpenGLLabel, OpenGLLabelCommand> {
    protected OpenGLImageManager(OpenGLLabelManager labelManager) {
        super(labelManager);
    }

    @Override
    protected OpenGLImage createImage(BufferedImage image) {
        return new OpenGLImage(image);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
