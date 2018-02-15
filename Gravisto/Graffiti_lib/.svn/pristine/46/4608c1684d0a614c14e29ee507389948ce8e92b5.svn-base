// =============================================================================
//
//   NodeChangeListener.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import org.graffiti.graph.Node;
import org.graffiti.graphics.Dash;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugin.view.NodeShape;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class NodeChangeListener<T extends GraphicsEngine<?, ?>> {
    /**
     * The graphics engine.
     */
    protected T engine;

    protected NodeChangeListener(T engine) {
        this.engine = engine;
    }

    public abstract void onChangePosition(Node node, Point2D position);

    public abstract void onChangeDepth(Node node, double depth);

    public abstract void onChangeSize(Node node, Point2D size);

    public abstract void onSetShape(Node node, NodeShape shape);

    public abstract void onChangeShape(Node node, NodeGraphicAttribute attribute);

    public abstract void onChangeFrameThickness(Node node, double frameThickness);

    public abstract void onChangeDash(Node node, Dash dash);

    public abstract void onChangeFrameColor(Node node, Color color);

    public abstract void onChangeFillColor(Node node, Color color);

    public abstract void onChangeSelection(Node node, boolean isSelected);

    public abstract void onChangeHover(Node node, boolean isHover);

    public abstract void onChangeBackgroundImage(Node node,
            BufferedImage image, boolean isMaximized, boolean isTiled);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
