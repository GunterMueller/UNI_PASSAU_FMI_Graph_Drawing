// =============================================================================
//
//   Java2DImage.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.java2d;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.graffiti.plugins.views.fast.FastImage;
import org.graffiti.plugins.views.fast.java2d.label.Java2DLabel;
import org.graffiti.plugins.views.fast.java2d.label.commands.Java2DLabelCommand;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class Java2DImage extends FastImage<Java2DLabel, Java2DLabelCommand> {
    private BufferedImage image;

    protected Java2DImage(BufferedImage image) {
        this.image = image;
    }

    protected Java2DImage(Java2DImage fastImage, int targetWidth,
            int targetHeight) {
        super(fastImage);
        image = new BufferedImage(targetWidth, targetHeight, fastImage.image
                .getType());
        Graphics2D g = image.createGraphics();
        g.drawImage(fastImage.image, 0, 0, targetWidth, targetHeight, null);
        g.dispose();
    }

    @Override
    public int getHeight() {
        return image.getHeight(null);
    }

    @Override
    public Java2DImage getScaled(int targetWidth, int targetHeight) {
        if (targetWidth == getWidth() && targetHeight == getHeight())
            return this;
        else
            return new Java2DImage(this, targetWidth, targetHeight);
    }

    @Override
    public int getWidth() {
        return image.getWidth(null);
    }

    public BufferedImage getImage() {
        return image;
    }

    @Override
    public void onDispose() {
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
