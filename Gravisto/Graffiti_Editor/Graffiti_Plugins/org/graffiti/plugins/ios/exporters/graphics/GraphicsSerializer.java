//==============================================================================
//
//   GraphicsSerializer.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//==============================================================================
// $Id: GraphicsSerializer.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.exporters.graphics;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.SwingUtilities;

import org.graffiti.graph.Graph;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.grid.GridAttribute;
import org.graffiti.plugin.io.AbstractOutputSerializer;
import org.graffiti.plugin.view.Grid;
import org.graffiti.plugin.view.NoGrid;
import org.graffiti.plugins.views.fast.java2d.Java2DFastView;

/**
 * Saves the graph as an image file.
 * 
 * @version $Revision: 5766 $
 */
public abstract class GraphicsSerializer extends AbstractOutputSerializer {

    private final static int GRID_BORDER = 25;

    /*
     * @see org.graffiti.plugin.io.OutputSerializer#write(java.io.OutputStream,
     * org.graffiti.graph.Graph)
     */
    public void write(OutputStream stream, final Graph g) throws IOException {
        // Create a temporary view to draw the graph.
        Java2DFastView view = new Java2DFastView();
        view.setGraph(g);

        // Draw the graph into a dummy 1x1 image so the viewport of the view
        // knows the bounds of the graph.
        Graphics2D graphics = (new BufferedImage(1, 1,
                BufferedImage.TYPE_INT_RGB)).createGraphics();
        view.print(graphics, 1, 1);
        graphics.dispose();

        // Draw the graph using the Graphics2D object obtained for the output
        // stream.
        Rectangle2D rect = view.getViewport().getLogicalElementsBounds();
        try {
            Grid grid = ((GridAttribute) g
                    .getAttribute(GraphicAttributeConstants.GRID_PATH))
                    .getGrid();
            if ((grid != null) && !(grid instanceof NoGrid)) {
                rect.add(rect.getMinX() - GRID_BORDER, rect.getMinY()
                        - GRID_BORDER);
                rect.add(rect.getMaxX() + GRID_BORDER, rect.getMaxY()
                        + GRID_BORDER);
            }
        } catch (Exception e) {
            // no grid
        }

        GraphicsContainer gc = getGraphicsContainer(stream, rect.getBounds());
        graphics = (Graphics2D) gc.getGraphics();
        graphics.setBackground(view.getBackgroundColor());
        // Without the following command, the background is transparent.
        // graphics.clearRect((int)rect.getX(), (int)rect.getY(), (int)Math
        // .ceil(rect.getWidth()) + 1, (int)Math.ceil(rect.getHeight()) + 1);
        view.print(graphics, (int) rect.getWidth(), (int) rect.getHeight());
        gc.saveGraphics();

        // ?
        if (g.isModified()) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    g.setModified(true);
                }
            });
        }
    }

    /**
     * Get the graphics container used by this graphics serializer.
     * 
     * @param stream
     *            Outputstream to write graphics to.
     * @param rect
     *            Rectangle describing the area of the graphics.
     * 
     * @return GraphicsContainer to use.
     * 
     * @throws IOException
     *             if an error occurs while accessing the stream.
     */
    protected abstract GraphicsContainer getGraphicsContainer(
            OutputStream stream, Rectangle rect) throws IOException;

    /**
     * Container for graphics object used to write into a file.
     */
    public interface GraphicsContainer {
        /**
         * Get graphics object to write components on.
         * 
         * @return Graphics object to use.
         */
        public Graphics getGraphics();

        /**
         * Save graphics into file.
         * 
         * @throws IOException
         *             if graphics could not be saved.
         */
        public void saveGraphics() throws IOException;
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
