// =============================================================================
//
//   OpenGLGraphElementFinder.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.view.EdgeShape;
import org.graffiti.plugin.view.NodeShape;
import org.graffiti.plugin.view.interactive.GraphElementFinder;
import org.graffiti.plugins.views.fast.AbstractRepDepthComparator;
import org.graffiti.plugins.views.fast.FastViewPlugin;
import org.graffiti.plugins.views.fast.opengl.label.OpenGLLabel;
import org.graffiti.plugins.views.fast.opengl.label.commands.OpenGLLabelCommand;

//TODO: Overwrite deferred statements using OpenGL picking method
/**
 * {@code GraphElementFinder} for the OpenGL graphics engine.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see OpenGLEngine
 */
public class OpenGLGraphElementFinder extends GraphElementFinder {
    private Map<Node, AbstractNodeRep> nodes;
    private Map<Edge, AbstractEdgeRep> edges;
    private AbstractRepDepthComparator<OpenGLLabel, OpenGLLabelCommand> abstractRepDepthComparator;
    private OpenGLEngine engine;
    private BasicStroke stroke;

    protected OpenGLGraphElementFinder(Map<Node, AbstractNodeRep> nodes,
            Map<Edge, AbstractEdgeRep> edges, OpenGLEngine engine) {
        this.nodes = nodes;
        this.edges = edges;
        abstractRepDepthComparator = new AbstractRepDepthComparator<OpenGLLabel, OpenGLLabelCommand>();
        this.engine = engine;
        stroke = new BasicStroke(1.0f);
    }

    private boolean isIntersecting(EdgeShape shape, Rectangle2D rectangle) {
        if (shape == null)
            return false;
        Rectangle2D bounds = shape.getBounds2D();
        if (bounds.getWidth() != 0 && bounds.getHeight() != 0
                && !shape.getBounds2D().intersects(rectangle))
            return false;
        return stroke.createStrokedShape(shape).intersects(rectangle);
    }

    @Override
    protected void addIntersectingEdges(Rectangle2D rectangle,
            Set<? super Edge> set) {
        for (Map.Entry<Edge, AbstractEdgeRep> entry : edges.entrySet()) {
            if (isIntersecting(entry.getValue().getShape(engine), rectangle)) {
                set.add(entry.getKey());
            }
        }
    }

    @Override
    protected void addIntersectingNodes(Rectangle2D rectangle,
            Set<? super Node> set) {
        for (Map.Entry<Node, AbstractNodeRep> entry : nodes.entrySet()) {
            AbstractNodeRep nodeRep = entry.getValue();
            NodeShape shape = nodeRep.getShape();
            if (shape == null) {
                continue;
            }
            Rectangle2D nodeSize = shape.getBounds2D();
            Point2D position = nodeRep.getPosition();
            Rectangle2D translatedRectangle = new Rectangle2D.Double(rectangle
                    .getX()
                    - position.getX() + nodeSize.getWidth() / 2.0, rectangle
                    .getY()
                    - position.getY() + nodeSize.getHeight() / 2.0, rectangle
                    .getWidth(), rectangle.getHeight());
            if (shape.getBounds2D().intersects(translatedRectangle)
                    && shape.intersects(translatedRectangle)) {
                set.add(entry.getKey());
            }
        }
    }

    @Override
    public Edge getEdgeAt(Point2D position, double tolerance) {
        Rectangle2D rectangle = new Rectangle2D.Double(position.getX()
                - tolerance, position.getY() - tolerance, 2 * tolerance,
                2 * tolerance);
        SortedSet<AbstractEdgeRep> set = new TreeSet<AbstractEdgeRep>(
                abstractRepDepthComparator);
        for (AbstractEdgeRep edgeRep : edges.values()) {
            if (edgeRep.isSelected()) {
                if (getBend(position, edgeRep.getEdge(), tolerance).length() != 0) {
                    set.add(edgeRep);
                    continue;
                }
            }
            if (isIntersecting(edgeRep.getShape(engine), rectangle)) {
                set.add(edgeRep);
            }
        }
        Edge edge = set.isEmpty() ? null : set.last().getEdge();
        return edge;
    }

    @Override
    public Node getNodeAt(Point2D position) {
        Point2D translatedPosition = new Point2D.Double();
        SortedSet<AbstractNodeRep> set = new TreeSet<AbstractNodeRep>(
                abstractRepDepthComparator);
        for (AbstractNodeRep nodeRep : nodes.values()) {
            Point2D nodePos = nodeRep.getPosition();
            NodeShape shape = nodeRep.getShape();
            Rectangle2D rect = shape.getBounds2D();
            Point2D nodeSize = new Point2D.Double(rect.getWidth(), rect
                    .getHeight());
            translatedPosition.setLocation(position.getX() - nodePos.getX()
                    + nodeSize.getX() / 2.0, position.getY() - nodePos.getY()
                    + nodeSize.getY() / 2.0);
            if (shape != null
                    && shape.getBounds2D().contains(translatedPosition)
                    && ((nodeRep.isSelected() && isOnNodeHandle(
                            translatedPosition, nodeSize)) || shape
                            .contains(translatedPosition))) {
                set.add(nodeRep);
            }
        }
        Node node = set.isEmpty() ? null : set.last().getNode();
        return node;
    }

    @Override
    public boolean isOnShapeBorder(Point2D position, Node node, double tolerance) {
        AbstractNodeRep nodeRep = nodes.get(node);
        if (nodeRep == null)
            return false;
        NodeShape shape = nodeRep.getShape();
        if (shape == null)
            return false;
        Point2D nodePos = nodeRep.getPosition();
        Rectangle2D rect = shape.getBounds2D();
        Point2D nodeSize = new Point2D.Double(rect.getWidth(), rect.getHeight());
        Point2D translatedPosition = new Point2D.Double(position.getX()
                - nodePos.getX() + nodeSize.getX() / 2.0, position.getY()
                - nodePos.getY() + nodeSize.getY() / 2.0);
        if (nodeRep.isSelected()
                && isOnNodeHandle(translatedPosition, nodeSize))
            return true;
        BasicStroke stroke = new BasicStroke((float) tolerance);
        Shape strokedShape = stroke.createStrokedShape(shape);
        return strokedShape.getBounds2D().contains(translatedPosition)
                && strokedShape.contains(translatedPosition);
    }

    private boolean isOnNodeHandle(Point2D translatedPosition, Point2D nodeSize) {
        int hs = FastViewPlugin.NODE_HANDLE_SIZE;
        return ((0 <= translatedPosition.getX() && translatedPosition.getX() <= hs) || (nodeSize
                .getX()
                - hs <= translatedPosition.getX() && translatedPosition.getX() <= nodeSize
                .getX()))
                && ((0 <= translatedPosition.getY() && translatedPosition
                        .getY() <= hs) || (nodeSize.getY() - hs <= translatedPosition
                        .getY() && translatedPosition.getY() <= nodeSize.getY()));
    }

    @Override
    public String getBend(Point2D position, Edge edge, double tolerance) {
        tolerance += FastViewPlugin.EDGE_BEND_SIZE / 2.0;
        Rectangle2D rectangle = new Rectangle2D.Double(position.getX()
                - tolerance, position.getY() - tolerance, 2 * tolerance,
                2 * tolerance);
        SortedCollectionAttribute ca = (SortedCollectionAttribute) edge
                .getAttribute(GraphicAttributeConstants.BENDS_PATH);
        for (Map.Entry<String, Attribute> entry : ca.getCollection().entrySet()) {
            Attribute attribute = entry.getValue();
            if (attribute instanceof CoordinateAttribute) {
                if (rectangle.contains(((CoordinateAttribute) attribute)
                        .getCoordinate()))
                    return entry.getKey();
            }
        }
        return "";
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
