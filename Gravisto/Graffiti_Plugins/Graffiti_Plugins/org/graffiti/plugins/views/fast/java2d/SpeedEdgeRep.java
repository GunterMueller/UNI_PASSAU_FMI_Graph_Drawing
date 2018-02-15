// =============================================================================
//
//   SpeedEdgeRep.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.java2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.graffiti.graph.Edge;
import org.graffiti.graphics.Dash;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.view.NodeShape;
import org.graffiti.plugin.view.ShapeNotFoundException;
import org.graffiti.plugins.views.fast.FastViewPlugin;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class SpeedEdgeRep extends AbstractEdgeRep {
    // private Rectangle2D bounds;
    private double frameThickness;
    private double thickness;
    private Stroke lineStroke;
    private Stroke arrowStroke;
    private Dash dash;
    private Color fillColor;
    private Color frameColor;
    private boolean isSelected;
    private boolean isHover;

    SpeedEdgeRep(Edge edge) {
        super(edge);
        frameThickness = GraphicAttributeConstants.DEFAULT_EDGE_FRAMETHICKNESS;
        thickness = GraphicAttributeConstants.DEFAULT_EDGE_THICKNESS;
        isSelected = false;
    }

    /**
     * @{inheritdoc
     */
    @Override
    void buildShape(EdgeGraphicAttribute attribute, NodeShape sourceShape,
            NodeShape targetShape) {
        try {
            shape.buildShape(attribute, sourceShape, targetShape);
            // bounds = shape.getBounds2D();
        } catch (ShapeNotFoundException e) {
        }
    }

    /**
     * @{inheritdoc
     */
    @Override
    protected void onDraw(Graphics2D g, DrawingSet set) {
        Shape tailArrow = shape.getTailArrow();
        Shape headArrow = shape.getHeadArrow();
        g.setColor(fillColor);
        if (tailArrow != null) {
            g.fill(tailArrow);
        }
        if (headArrow != null) {
            g.fill(headArrow);
        }
        g.setStroke(lineStroke);
        g.setColor(frameColor);
        g.draw(shape);
        g.setStroke(arrowStroke);
        if (tailArrow != null) {
            g.draw(tailArrow);
        }
        if (headArrow != null) {
            g.draw(headArrow);
        }
        set.addToBounds(shape.getBounds2D());

        Color col = null;
        if (isSelected) {
            col = FastViewPlugin.SELECTION_COLOR;
        } else if (isHover) {
            col = FastViewPlugin.HOVER_COLOR;
        }
        if (col != null) {
            final int EBS = FastViewPlugin.EDGE_BEND_SIZE;
            final int ECS = FastViewPlugin.EDGE_CONTROLPOINT_SIZE;
            g.setColor(col);
            double coords[] = new double[6];
            for (PathIterator iter = shape.getPathIterator(null); !iter
                    .isDone(); iter.next()) {
                int seg = iter.currentSegment(coords);
                switch (seg) {
                case PathIterator.SEG_CUBICTO:
                    coords[2] = coords[4];
                    coords[3] = coords[5];
                    // Fallthrough
                case PathIterator.SEG_QUADTO:
                    if (isSelected) {
                        g.setColor(FastViewPlugin.CONTROL_POINT_COLOR);
                        g.fill(new Ellipse2D.Double(coords[0] - 1,
                                coords[1] - 1, ECS, ECS));
                        g.setColor(col);
                    }
                    coords[0] = coords[2];
                    coords[1] = coords[3];
                    // Fallthrough
                case PathIterator.SEG_MOVETO:
                    // Fallthrough
                case PathIterator.SEG_LINETO:
                    g.fill(new Rectangle2D.Double(coords[0] - 3, coords[1] - 3,
                            EBS, EBS));
                    break;
                }
            }
        }
    }

    /**
     * @{inheritdoc
     */
    @Override
    double getFrameThickness() {
        return frameThickness;
    }

    /**
     * @{inheritdoc
     */
    @Override
    double getThickness() {
        return thickness;
    }

    /**
     * @{inheritdoc
     */
    @Override
    void setArrowStroke(Stroke arrowStroke) {
        this.arrowStroke = arrowStroke;
    }

    /**
     * @{inheritdoc
     */
    @Override
    void setBackgroundImage(BufferedImage image, boolean maximized,
            boolean tiled) {
        // TODO: does a background image on an edge make sense?
    }

    /**
     * @{inheritdoc
     */
    @Override
    void setDash(Dash dash) {
        this.dash = dash;
    }

    /**
     * @{inheritdoc
     */
    @Override
    Dash getDash() {
        return dash;
    }

    /**
     * @{inheritdoc
     */
    @Override
    void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    /**
     * @{inheritdoc
     */
    @Override
    void setFrameColor(Color frameColor) {
        this.frameColor = frameColor;
    }

    /**
     * @{inheritdoc
     */
    @Override
    void setFrameThickness(double frameThickness) {
        this.frameThickness = frameThickness;
    }

    /**
     * @{inheritdoc
     */
    @Override
    void setLineStroke(Stroke lineStroke) {
        this.lineStroke = lineStroke;
    }

    /**
     * @{inheritdoc
     */
    @Override
    void setThickness(double thickness) {
        this.thickness = thickness;
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

    /**
     * @{inheritdoc
     */
    @Override
    void setHover(boolean isHover) {
        this.isHover = isHover;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
