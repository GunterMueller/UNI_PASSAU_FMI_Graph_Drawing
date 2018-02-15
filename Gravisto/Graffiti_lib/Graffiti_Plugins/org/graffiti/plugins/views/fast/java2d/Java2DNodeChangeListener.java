// =============================================================================
//
//   Java2DNodeChangeListener.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.java2d;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.SortedSet;

import org.graffiti.graph.Node;
import org.graffiti.graphics.Dash;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugin.view.NodeShape;
import org.graffiti.plugins.views.fast.NodeChangeListener;

/**
 * {@code NodeChangeListener} for the Java2D graphics engine.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class Java2DNodeChangeListener extends NodeChangeListener<Java2DEngine> {
    /**
     * A map from nodes to their representatives.
     */
    private Map<Node, AbstractNodeRep> nodes;

    /**
     * Node representatives sorted by their depth value.
     */
    private SortedSet<AbstractNodeRep> sortedNodes;

    /**
     * Constructs a {@code Java2DNodeChangeListener}.
     * 
     * @param engine
     *            the graphics engine.
     * @param nodes
     *            a map from nodes to their representatives.
     * @param sortedNodes
     *            node representatives sorted by their depth value.
     */
    protected Java2DNodeChangeListener(Java2DEngine engine,
            Map<Node, AbstractNodeRep> nodes,
            SortedSet<AbstractNodeRep> sortedNodes) {
        super(engine);
        this.nodes = nodes;
        this.sortedNodes = sortedNodes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onChangeDash(Node node, Dash dash) {
        AbstractNodeRep nodeRep = nodes.get(node);
        nodeRep.setDash(dash);
        nodeRep.setStroke(engine.acquireStroke(nodeRep.getFrameThickness(),
                dash));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onChangeDepth(Node node, double depth) {
        AbstractNodeRep nodeRep = nodes.get(node);
        if (depth != nodeRep.getDepth()) {
            sortedNodes.remove(nodeRep);
            nodeRep.setDepth(depth);
            sortedNodes.add(nodeRep);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onChangeFillColor(Node node, Color color) {
        nodes.get(node).setFillColor(color);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onChangeFrameColor(Node node, Color color) {
        nodes.get(node).setFrameColor(color);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onChangeFrameThickness(Node node, double frameThickness) {
        AbstractNodeRep nodeRep = nodes.get(node);
        nodeRep.setFrameThickness(frameThickness);
        Dash dash = nodeRep.getDash();
        if (dash != null) {
            nodeRep.setStroke(engine.acquireStroke(frameThickness, dash));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onChangePosition(Node node, Point2D position) {
        nodes.get(node).setPosition(position);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onChangeSelection(Node node, boolean isSelected) {
        AbstractNodeRep nodeRep = nodes.get(node);
        if (nodeRep == null)
            return;
        nodeRep.setSelected(isSelected);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onChangeHover(Node node, boolean isHover) {
        AbstractNodeRep nodeRep = nodes.get(node);
        if (nodeRep == null)
            return;
        nodeRep.setHover(isHover);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onChangeShape(Node node, NodeGraphicAttribute attribute) {
        AbstractNodeRep nodeRep = nodes.get(node);
        if (nodeRep == null)
            return;
        nodeRep.buildShape(attribute);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onChangeSize(Node node, Point2D size) {
        // Nothing to do. Size will be adapted on onChangeNodeShape.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSetShape(Node node, NodeShape shape) {
        AbstractNodeRep nodeRep = nodes.get(node);
        if (nodeRep == null)
            return;
        nodeRep.setShape(shape);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onChangeBackgroundImage(Node node, BufferedImage image,
            boolean isMaximized, boolean isTiled) {
        AbstractNodeRep nodeRep = nodes.get(node);
        if (nodeRep == null)
            return;
        nodeRep.setBackgroundImage(image, isMaximized, isTiled);
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
