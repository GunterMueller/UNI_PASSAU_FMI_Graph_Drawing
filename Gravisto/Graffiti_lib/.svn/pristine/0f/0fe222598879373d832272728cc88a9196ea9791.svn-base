// =============================================================================
//
//   NodeRep.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.java2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.graffiti.graph.Node;
import org.graffiti.graphics.Dash;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugin.view.ShapeNotFoundException;
import org.graffiti.plugins.views.defaults.RectangleNodeShape;
import org.graffiti.plugins.views.fast.FastViewPlugin;

/**
 * Node representative optimized for speed.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see Node
 */
class SpeedNodeRep extends AbstractNodeRep {
    /**
     * Bounds of the node shape.
     */
    private Rectangle2D bounds;

    /**
     * Frame thickness.
     */
    private double frameThickness;

    /**
     * The dash, i.e. line pattern.
     */
    private Dash dash;

    /**
     * Stroke.
     */
    private Stroke stroke;

    /**
     * Color of the frame.
     */
    private Color frameColor;

    /**
     * Color of the fill.
     */
    private Color fillColor;

    /**
     * Background image as obtained from the attribute.
     */
    private BufferedImage image;

    /**
     * Background image with tile/adjust taken into accont.
     */
    private BufferedImage scaledImage;

    /**
     * Denotes if the image is tiled, i.e. repeated to fill the complete shape.
     */
    private boolean isTiled;

    /**
     * Denotes if the image is maximized, i.e. scaled to fill the complete shape
     * in at least one dimension.
     */
    private boolean isMaximized;

    /**
     * Denotes if the node is selected.
     */
    private boolean isSelected;

    /**
     * Denotes if the node is hovered.
     */
    private boolean isHover;

    /**
     * Constructs a node representative.
     * 
     * @param node
     *            the node to represent.
     */
    protected SpeedNodeRep(Node node) {
        super(node);
        frameThickness = GraphicAttributeConstants.DEFAULT_NODE_FRAMETHICKNESS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void buildShape(NodeGraphicAttribute attribute) {
        // TODO: Temporary FIX while defaults.RectangularNodeShape is in use.
        // Begin
        /*
         * if (shape instanceof RectangleNodeShape) { attribute =
         * (NodeGraphicAttribute) attribute.copy(); DimensionAttribute dimension
         * = attribute.getDimension(); dimension.setWidth(dimension.getWidth() -
         * frameThickness); dimension.setHeight(dimension.getHeight() -
         * frameThickness); }
         */
        // End
        try {
            shape.buildShape(attribute);
            bounds = shape.getBounds2D();
        } catch (ShapeNotFoundException e) {
        }
        recalculateImage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDraw(Graphics2D g, DrawingSet set) {
        double width = bounds.getWidth();
        double height = bounds.getHeight();
        double left = position.getX() - width / 2.0;
        double top = position.getY() - height / 2.0;
        set.addToBounds(new Rectangle2D.Double(left, top, width, height));

        g.translate(left, top);
        g.setColor(fillColor);
        // TODO: while RectangleNodeShape is in use...
        if (shape instanceof RectangleNodeShape) {
            g.fill(new Rectangle2D.Double(frameThickness / 2.0,
                    frameThickness / 2.0, width - frameThickness, height
                            - frameThickness));
        } else {
            g.fill(shape);
        }
        if (scaledImage != null) {
            g.drawImage(scaledImage, (int) Math.floor(frameThickness / 2.0),
                    (int) Math.floor(frameThickness / 2.0), null);
        }
        g.setColor(frameColor);
        g.setStroke(stroke);
        // TODO: while RectangleNodeShape is in use...
        if (shape instanceof RectangleNodeShape) {
            g.draw(new Rectangle2D.Double(frameThickness / 2.0,
                    frameThickness / 2.0, width - frameThickness, height
                            - frameThickness));
        } else {
            g.draw(shape);
        }
        Color col = null;
        if (isSelected) {
            col = FastViewPlugin.SELECTION_COLOR;
        } else if (isHover) {
            col = FastViewPlugin.HOVER_COLOR;
        }
        if (col != null) {
            int hs = FastViewPlugin.NODE_HANDLE_SIZE;
            g.translate(0.5, 0.5);
            g.setColor(col);
            g.setStroke(set.defaultStroke);
            g.fillRect(0, 0, hs, hs);
            g.drawRect(0, 0, hs, hs);
            g.fillRect((int) (width - hs - 1), 0, hs, hs);
            g.drawRect((int) (width - hs - 1), 0, hs, hs);
            g.fillRect(0, (int) (height - hs - 1), hs, hs);
            g.drawRect(0, (int) (height - hs - 1), hs, hs);
            g.fillRect((int) (width - hs - 1), (int) (height - hs - 1), hs, hs);
            g.drawRect((int) (width - hs - 1), (int) (height - hs - 1), hs, hs);
        }
        g.setTransform(set.affineTransform);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setFrameThickness(double frameThickness) {
        this.frameThickness = frameThickness;
        recalculateImage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    double getFrameThickness() {
        return frameThickness;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setDash(Dash dash) {
        this.dash = dash;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Dash getDash() {
        return dash;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setFrameColor(Color frameColor) {
        this.frameColor = frameColor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setBackgroundImage(BufferedImage image, boolean maximized,
            boolean tiled) {
        this.image = image;
        this.isMaximized = maximized;
        this.isTiled = tiled;
        recalculateImage();
    }

    /**
     * Calculates the scaled image, i.e. renders the image if necessary scaled
     * and multiple times, taking {@code isMaximized} and {@code isTiled} into
     * account.
     */
    private void recalculateImage() {
        if (image == null || bounds == null
                || (image.getWidth() == 1 && image.getHeight() == 1)) {
            scaledImage = null;
            return;
        }
        double width = bounds.getWidth() - frameThickness;
        double height = bounds.getHeight() - frameThickness;
        scaledImage = new BufferedImage((int) width, (int) height,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaledImage.createGraphics();
        g.setBackground(new Color(0, 0, 0, 0));
        g.clearRect(0, 0, (int) width, (int) height);
        g.setClip(shape);
        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();
        if (isMaximized) {
            double scale = Math.min(width / imageWidth, height / imageHeight);
            imageWidth *= scale;
            imageHeight *= scale;
        }
        if (isTiled) {
            int xc = (int) (width / imageWidth) + 1;
            int yc = (int) (height / imageHeight) + 1;
            for (int ix = 0; ix < xc; ix++) {
                for (int iy = 0; iy < yc; iy++) {
                    g.drawImage(image, (int) Math.floor(ix * imageWidth),
                            (int) Math.floor(iy * imageHeight), (int) Math
                                    .ceil(imageWidth), (int) Math
                                    .ceil(imageHeight), null);
                }
            }
        } else {
            g.drawImage(image, (int) Math.floor((width - imageWidth) / 2.0),
                    (int) Math.floor((height - imageHeight) / 2.0), (int) Math
                            .ceil(imageWidth), (int) Math.ceil(imageHeight),
                    null);
        }
        g.dispose();
    }

    /**
     * @{inheritdoc
     */
    @Override
    void setHover(boolean isHover) {
        this.isHover = isHover;
    }

    /**
     * @{inheritdoc
     */
    @Override
    void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    boolean isSelected() {
        return isSelected;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
