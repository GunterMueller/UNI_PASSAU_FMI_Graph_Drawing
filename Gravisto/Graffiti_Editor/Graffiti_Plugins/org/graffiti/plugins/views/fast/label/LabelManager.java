// =============================================================================
//
//   LabelFactory.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.label;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeLabelAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugins.views.fast.GraphicsEngine;
import org.graffiti.plugins.views.fast.ImageManager;
import org.graffiti.util.Pair;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class LabelManager<L extends Label<L, LC>, LC extends LabelCommand> {
    protected Set<L> pendingLabelPositionChanges;
    protected Set<L> pendingLabelFormatChanges;
    protected GraphicsEngine<L, LC> engine;
    private CommandListFactory<L, LC> commandListFactory;

    protected LabelManager(GraphicsEngine<L, LC> engine,
            CommandFactory<L, LC> commandFactory) {
        this.engine = engine;
        commandListFactory = new CommandListFactory<L, LC>(commandFactory,
                engine.getFontManager());
        pendingLabelFormatChanges = new HashSet<L>();
        pendingLabelPositionChanges = new HashSet<L>();
    }

    public final void setImageManager(ImageManager<L, LC> imageManager) {
        commandListFactory.setImageManager(imageManager);
    }

    public abstract L createLabel(Node node, NodeLabelAttribute attribute);

    public abstract L createLabel(Edge edge, EdgeLabelAttribute attribute);

    public abstract void onDeleteLabel(Node node, NodeLabelAttribute attribute);

    public abstract void onDeleteLabel(Edge edge, EdgeLabelAttribute attribute);

    protected abstract L getLabel(Node node, NodeLabelAttribute attribute);

    protected abstract L getLabel(Edge edge, EdgeLabelAttribute attribute);

    protected abstract void onAddLabel(Node node, L label);

    protected abstract void onAddLabel(Edge edge, L label);

    public final void changeFormat(L label) {
        pendingLabelFormatChanges.add(label);
    }

    public final void changePosition(L label) {
        pendingLabelPositionChanges.add(label);
    }

    public final L acquireLabel(Node node, NodeLabelAttribute attribute) {
        L label = getLabel(node, attribute);
        if (label == null) {
            label = createLabel(node, attribute);
            onAddLabel(node, label);
        }
        return label;
    }

    public final L acquireLabel(Edge edge, EdgeLabelAttribute attribute) {
        L label = getLabel(edge, attribute);
        if (label == null) {
            label = createLabel(edge, attribute);
            onAddLabel(edge, label);
        }
        return label;
    }

    public final void processChanges() {
        for (L label : pendingLabelFormatChanges) {
            GraphElement element = label.getGraphElement();
            LabelAttribute attribute = label.getAttribute();
            Pair<LinkedList<LC>, Point2D> result = null;
            if (element instanceof Node) {
                result = createCommandList((Node) element,
                        (NodeLabelAttribute) attribute, label);
            } else if (element instanceof Edge) {
                result = createCommandList((Edge) element,
                        (EdgeLabelAttribute) attribute, label);
            }
            if (label.setSize(result.getSecond())) {
                pendingLabelPositionChanges.add(label);
            }
            onChangeFormat(element, attribute, label, result.getFirst());

        }
        for (L label : pendingLabelPositionChanges) {
            GraphElement element = label.getGraphElement();
            LabelAttribute attribute = label.getAttribute();
            if (element instanceof Node) {
                label.setPosition((NodeLabelAttribute) attribute);
            } else if (element instanceof Edge) {
                label.setPosition((EdgeLabelAttribute) attribute, engine
                        .getShape((Edge) element));
            }
            onChangePosition(element, attribute, label);
        }
        pendingLabelFormatChanges.clear();
        pendingLabelPositionChanges.clear();
    }

    private Pair<LinkedList<LC>, Point2D> createCommandList(Node node,
            NodeLabelAttribute attribute, L label) {
        double maxWidth = attribute.getMaxWidth();
        Shape masterClip = null;
        if (maxWidth == -1.0) {
            maxWidth = ((DoubleAttribute) node
                    .getAttribute(GraphicAttributeConstants.DIMW_PATH))
                    .getDouble();
            masterClip = engine.getShape(node);
        }
        return createCommandList(attribute.getLabel(), maxWidth, masterClip,
                attribute, label);
    }

    private Pair<LinkedList<LC>, Point2D> createCommandList(Edge edge,
            EdgeLabelAttribute attribute, L label) {
        return createCommandList(attribute.getLabel(), attribute.getMaxWidth(),
                null, attribute, label);
    }

    private Pair<LinkedList<LC>, Point2D> createCommandList(String text,
            double maxWidth, Shape masterClip, LabelAttribute attribute, L label) {
        return commandListFactory.createCommandList(text, maxWidth, attribute
                .getFont(), attribute.getFontSize(), attribute.getTextcolor()
                .getColor(), masterClip, label);
    }

    protected abstract void onChangeFormat(GraphElement element,
            LabelAttribute attribute, L label, LinkedList<LC> commands);

    protected abstract void onChangePosition(GraphElement element,
            LabelAttribute attribute, L label);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
