// =============================================================================
//
//   AbstractNodeRep.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.graffiti.graph.Node;
import org.graffiti.plugin.view.NodeShape;
import org.graffiti.plugins.views.fast.AttributeUtil;
import org.graffiti.plugins.views.fast.FastViewPlugin;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class AbstractNodeRep extends AbstractOpenGLRep {
    protected Node node;
    protected NodeShape shape;

    /**
     * The position of this node representant. Do not set position other than by
     * {@link #setPosition(Point2D)}
     */
    protected Point2D position;
    protected Rectangle2D bounds;

    protected AbstractNodeRep(Node node) {
        super();
        this.node = node;
        position = new Point2D.Double();
    }

    protected Node getNode() {
        return node;
    }

    public NodeShape getShape() {
        if (shape == null) {
            shape = AttributeUtil.getShape(node);
            recalculateBounds();
        }
        return shape;
    }

    public Point2D getPosition() {
        return position;
    }

    public Rectangle2D getBounds() {
        return bounds;
    }

    protected void setPosition(Point2D position) {
        this.position = position;
        recalculateBounds();
    }

    private void recalculateBounds() {
        if (shape == null)
            return;
        Rectangle2D b = shape.getBounds2D();
        double width = b.getWidth();
        double height = b.getHeight();
        bounds = new Rectangle2D.Double(position.getX() - width * 0.5, position
                .getY()
                - height * 0.5, width, height);
    }

    @Override
    protected void retrieveShape(Map<Node, AbstractNodeRep> nodes) {
        shape = AttributeUtil.getShape(node);
    }

    @Override
    protected void setAsHovered() {
        setSelectionDepth(depth);
        setSelectionColor(FastViewPlugin.HOVER_COLOR);
    }

    @Override
    protected void setAsSelected() {
        setSelectionDepth(depth);
        setSelectionColor(FastViewPlugin.SELECTION_COLOR);
    }

    @Override
    protected void setAsUnselected() {
        setSelectionDepth(2.0);
    }

    @Override
    protected void tesselate(OpenGLEngine engine) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    protected abstract void updateColor();

    @Override
    protected abstract void updateElementDepth();

    @Override
    protected abstract void updatePosition();

    protected abstract void setSelectionDepth(double depth);

    protected abstract void setSelectionColor(Color color);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
