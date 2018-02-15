// =============================================================================
//
//   OpenGLNodeChangeListener.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Map;

import org.graffiti.graph.Node;
import org.graffiti.graphics.Dash;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugin.view.NodeShape;
import org.graffiti.plugins.views.fast.NodeChangeListener;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class OpenGLNodeChangeListener extends NodeChangeListener<OpenGLEngine> {
    private Map<Node, AbstractNodeRep> nodes;
    private ChangeProcessor changeProcessor;

    protected OpenGLNodeChangeListener(OpenGLEngine engine,
            Map<Node, AbstractNodeRep> nodes, ChangeProcessor changeProcessor) {
        super(engine);
        this.nodes = nodes;
        this.changeProcessor = changeProcessor;
    }

    @Override
    public void onChangeDash(Node node, Dash dash) {
        changeProcessor.onTesselation(nodes.get(node));
    }

    @Override
    public void onChangeDepth(Node node, double depth) {
        changeProcessor.onDepth(nodes.get(node), depth);
    }

    @Override
    public void onChangeFillColor(Node node, Color color) {
        changeProcessor.onColor(nodes.get(node));
    }

    @Override
    public void onChangeFrameColor(Node node, Color color) {
        changeProcessor.onColor(nodes.get(node));
    }

    @Override
    public void onChangeFrameThickness(Node node, double frameThickness) {
        changeProcessor.onTesselation(nodes.get(node));
    }

    @Override
    public void onChangeHover(Node node, boolean isHover) {
        changeProcessor.onHover(nodes.get(node), isHover);
    }

    @Override
    public void onChangePosition(Node node, Point2D position) {
        changeProcessor.onPosition(nodes.get(node));
    }

    @Override
    public void onChangeSelection(Node node, boolean isSelected) {
        AbstractNodeRep nodeRep = nodes.get(node);
        // TODO: Temporary fix.
        // onChangeSelection() should only occur after onAdd() notification.
        if (nodeRep == null)
            return;
        changeProcessor.onSelection(nodeRep, isSelected);
    }

    @Override
    public void onChangeShape(Node node, NodeGraphicAttribute attribute) {
        changeProcessor.onShape(nodes.get(node));
    }

    @Override
    public void onChangeSize(Node node, Point2D size) {
        // Nothing to do.
    }

    @Override
    public void onSetShape(Node node, NodeShape shape) {
        // Nothing to do.
    }

    @Override
    public void onChangeBackgroundImage(Node node, BufferedImage image,
            boolean isMaximized, boolean isTiled) {
        // TODO: background image => TextureManager
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
