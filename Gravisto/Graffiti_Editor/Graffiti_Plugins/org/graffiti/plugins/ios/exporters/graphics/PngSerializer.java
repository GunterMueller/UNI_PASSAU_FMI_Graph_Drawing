//==============================================================================
//
//   EpsSerializer.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//==============================================================================
// $Id: PngSerializer.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.exporters.graphics;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;

/**
 * Saves the graph as a png graphic.
 * 
 * @version $Revision: 5766 $
 */
public class PngSerializer extends GraphicsSerializer {

    int zoomFactor = 100;

    /*
     * @see org.graffiti.plugin.io.Serializer#getExtensions()
     */
    public String[] getExtensions() {
        return new String[] { ".png" };
    }

    /*
     * @see GraphicsSerializer#getGraphicsContainer(OutputStream, Rectangle)
     */
    @Override
    protected GraphicsContainer getGraphicsContainer(OutputStream stream,
            Rectangle rect) throws IOException {
        return new PngGraphicsContainer(stream, rect);
    }

    /**
     * Implementation of GraphicsContainer using a BufferedImage.
     */
    public class PngGraphicsContainer implements GraphicsContainer {
        /** Image used to draw the png file. */
        BufferedImage image;

        /** Graphics object to use. */
        Graphics2D graphics;

        /** Remember output stream to use. */
        OutputStream output;

        /**
         * Creates a new PngGraphicsContainer object.
         * 
         * @param stream
         *            OutputStream to use for the file.
         * @param rect
         *            Rectangular describing the area.
         * 
         * @throws IOException
         *             if an error occurs accessing the stream.
         */
        public PngGraphicsContainer(OutputStream stream, Rectangle rect)
                throws IOException {
            output = stream;

            image = new BufferedImage(
                    (int) (rect.getWidth() * zoomFactor) / 100, (int) (rect
                            .getHeight() * zoomFactor) / 100,
                    BufferedImage.TYPE_INT_ARGB);
            graphics = image.createGraphics();

            AffineTransform zoom = new AffineTransform(zoomFactor / 100.0, 0,
                    0, zoomFactor / 100.0,
                    -(rect.getMinX() * zoomFactor) / 100,
                    -(rect.getMinY() * zoomFactor) / 100);

            graphics.transform(zoom);
        }

        /*
         * @see GraphicsContainer#getGraphics()
         */
        public Graphics getGraphics() {
            return graphics;
        }

        /*
         * @see GraphicsContainer#saveGraphics()
         */
        public void saveGraphics() throws IOException {
            ImageIO.write(image, "png", output);
            output.close();
        }
    }

    public String getName() {
        return "PNG Exporter";
    }

    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        IntegerParameter zoom = new IntegerParameter(100, "zoom factor",
                "Which zoom factor (in percent) should be used?", 1, 200, 1,
                Integer.MAX_VALUE);
        return new Parameter[] { zoom };
    }

    @Override
    protected void setAlgorithmParameters(Parameter<?>[] params) {
        zoomFactor = ((IntegerParameter) params[0]).getValue();
    }

}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
