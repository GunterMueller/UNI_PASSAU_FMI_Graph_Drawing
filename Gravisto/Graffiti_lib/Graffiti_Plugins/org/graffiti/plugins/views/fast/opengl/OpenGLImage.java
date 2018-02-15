// =============================================================================
//
//   OpenGLImage.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.graffiti.plugins.views.fast.FastImage;
import org.graffiti.plugins.views.fast.opengl.label.OpenGLLabel;
import org.graffiti.plugins.views.fast.opengl.label.commands.OpenGLLabelCommand;

import com.sun.opengl.util.texture.Texture;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class OpenGLImage extends FastImage<OpenGLLabel, OpenGLLabelCommand> {
    // private static final boolean CREATE_MIPMAPS = false;
    private BufferedImage image;
    private Texture texture;

    protected OpenGLImage(BufferedImage image) {
        this.image = image;
    }

    protected OpenGLImage(OpenGLImage fastImage, int targetWidth,
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
    public OpenGLImage getScaled(int targetWidth, int targetHeight) {
        if (targetWidth == getWidth() && targetHeight == getHeight())
            return this;
        else
            return new OpenGLImage(this, targetWidth, targetHeight);
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
        if (texture != null) {
            texture.dispose();
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
