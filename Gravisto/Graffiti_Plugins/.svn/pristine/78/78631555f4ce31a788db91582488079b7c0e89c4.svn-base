// =============================================================================
//
//   Java2DImageManager.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.java2d;

import java.awt.image.BufferedImage;

import org.graffiti.plugins.views.fast.ImageManager;
import org.graffiti.plugins.views.fast.java2d.label.Java2DLabel;
import org.graffiti.plugins.views.fast.java2d.label.Java2DLabelManager;
import org.graffiti.plugins.views.fast.java2d.label.commands.Java2DLabelCommand;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class Java2DImageManager extends
        ImageManager<Java2DLabel, Java2DLabelCommand> {
    protected Java2DImageManager(Java2DLabelManager labelMananger) {
        super(labelMananger);
    }

    @Override
    protected Java2DImage createImage(BufferedImage image) {
        return new Java2DImage(image);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
