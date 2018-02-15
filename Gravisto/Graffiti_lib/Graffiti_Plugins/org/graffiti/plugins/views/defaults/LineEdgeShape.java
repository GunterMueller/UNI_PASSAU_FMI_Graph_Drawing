// =============================================================================
//
//   LineEdgeShape.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: LineEdgeShape.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.views.defaults;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.DockingAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.graphics.PortAttribute;
import org.graffiti.graphics.PortsAttribute;
import org.graffiti.plugin.view.ArrowShape;
import org.graffiti.plugin.view.EdgeShape;
import org.graffiti.plugin.view.NodeShape;
import org.graffiti.plugin.view.ShapeNotFoundException;
import org.graffiti.util.InstanceCreationException;
import org.graffiti.util.InstanceLoader;

/**
 * Class representing an edge as one straight line.
 * 
 * @version $Revision: 5766 $
 */
public abstract class LineEdgeShape implements EdgeShape {

    /** The graphicsAttribute of the edge this shape represents. */
    protected EdgeGraphicAttribute graphicsAttr;

    /**
     * The <code>Line</code> that is represented by this <code>EdgeShape</code>
     */
    protected GeneralPath linePath;

    private boolean trimArrows = true;

    /**
     * The <code>Line2D</code> that might represent this
     * <code>LineEdgeShape</code> or used for intersection purposes.
     */
    protected Line2D line2D;

    // /**
    // * Largest distance between mouseclick and line so that line still gets
    // * selected.
    // */
    // protected final double LINE_TOLERANCE = 2d;

    /**
     * The real bounding box with coordinates relative to the view of this
     * shape.
     */
    protected Rectangle2D realBounds;

    /** The shape of the arrow on the source side. */
    protected Shape headArrow;

    /** The shape of the arrow on the source side. */
    protected Shape tailArrow;

    /**
     * The constructor creates a line using default values. The shapes of the
     * source and target nodes are given to enable the edge to paint itself
     * correctly between those nodes. The line is painted between the centers of
     * the source and target shape. No arrows are painted.
     */
    public LineEdgeShape() {
        this.linePath = new GeneralPath();
        this.realBounds = new Rectangle2D.Double(0d, 0d, 0d, 0d);
        this.line2D = new Line2D.Double();
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public Rectangle getBounds() {
        return this.linePath.getBounds();
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public Rectangle2D getBounds2D() {
        return this.linePath.getBounds2D();
    }

    /**
     * Returns the arrow at the target side.
     * 
     * @return the arrow at the target side.
     */
    public Shape getHeadArrow() {
        return this.headArrow;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param t
     *            DOCUMENT ME!
     * @param d
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public PathIterator getPathIterator(AffineTransform t, double d) {
        return this.linePath.getPathIterator(t, d);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param t
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public PathIterator getPathIterator(AffineTransform t) {
        return this.linePath.getPathIterator(t);
    }

    /**
     * Returns the correct bounding box with coordinates relative to the view.
     * 
     * @return the correct bounding box with coordinates relative to the view.
     */
    public Rectangle2D getRealBounds2D() {
        return realBounds;
    }

    /**
     * Called when one of the nodes belonging to this edge has changed.
     * 
     * @param graphics
     *            the attribute that has changed
     * @param source
     *            the <code>NodeShape</code> of the source node
     * @param target
     *            the <code>NodeShape</code> of the target node
     */
    public abstract void buildShape(EdgeGraphicAttribute graphics,
            NodeShape source, NodeShape target) throws ShapeNotFoundException;

    /**
     * Returns the arrow at the source side.
     * 
     * @return the arrow at the source side.
     */
    public Shape getTailArrow() {
        return this.tailArrow;
    }

    /**
     * Checks whether or not a rectangle lies entirely within this shape.
     * 
     * @param x
     *            the x-coordinate of the point to check.
     * @param y
     *            the y-coordinate of the point to check.
     * @param w
     *            width
     * @param h
     *            height
     * 
     * @return true if the point lies within this shape.
     * 
     * @throws RuntimeException
     *             DOCUMENT ME!
     */
    public boolean contains(double x, double y, double w, double h) {
        throw new RuntimeException();
    }

    /**
     * Decides whether or not a point lies within this shape.
     * 
     * @param x
     *            the x-coordinate of the point to check.
     * @param y
     *            the y-coordinate of the point to check.
     * 
     * @return true if the point lies within this shape.
     */
    public abstract boolean contains(double x, double y);

    /**
     * DOCUMENT ME!
     * 
     * @param p
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public boolean contains(Point2D p) {
        return this.contains(p.getX(), p.getY());
    }

    /**
     * DOCUMENT ME!
     * 
     * @param r
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public boolean contains(Rectangle2D r) {
        return this.contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    /**
     * DOCUMENT ME!
     * 
     * @param x
     *            DOCUMENT ME!
     * @param y
     *            DOCUMENT ME!
     * @param w
     *            DOCUMENT ME!
     * @param h
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public boolean intersects(double x, double y, double w, double h) {
        return this.linePath.intersects(x, y, w, h);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param r
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public boolean intersects(Rectangle2D r) {
        return this.linePath.intersects(r);
    }

    /**
     * Checks whether or not a point is said to be locateds on a line. It uses
     * the field <code>LINE_TOLERANCE</code> as a certain tolerance, i.e. it
     * really checks whether or not the point lies inside a rectangle around the
     * line.
     * 
     * @param line
     * @param x
     * @param y
     * 
     * @return DOCUMENT ME!
     */
    public boolean lineContains(Line2D line, double x, double y) {
        double maxDist = CLICK_TOLERANCE;
        double lineBreadth = graphicsAttr.getThickness()
                + graphicsAttr.getFrameThickness();

        if (lineBreadth > maxDist) {
            maxDist = lineBreadth / 2d;
        }

        double dist = line.ptSegDist(x, y);

        // if (dist == Double.NaN) System.out.println(dist + " "+ x + " " + y);
        // System.out.println("dist = "+ dist);
        if (dist < maxDist)
            return true;
        else
            return false;
    }

    /**
     * Returns the coordinates of the port the edge belongs to. It returns
     * default values if no port could be found.
     * 
     * @param edgeAttr
     *            the graphic attribute of the edge
     * @param sourceShape
     *            the shape of the source node
     * 
     * @return a <code>Point2D</code> representing the coordinates of the port
     *         the edge wants to dock to or those of the default port when there
     *         was no or wrong port information int the edge.
     */
    protected Point2D getSourceDockingCoords(EdgeGraphicAttribute edgeAttr,
            NodeShape sourceShape) {
        DockingAttribute docking = edgeAttr.getDocking();
        String sourcePortName = docking.getSource();

        Edge edge = (Edge) edgeAttr.getAttributable();
        Node sourceNode = edge.getSource();

        return getDockingCoords(sourcePortName, sourceShape, sourceNode, true);
    }

    /**
     * Returns the coordinates of the port the edge belongs to. It returns
     * default values if no port could be found.
     * 
     * @param edgeAttr
     *            the graphic attribute of the edge
     * @param targetShape
     *            the shape of the target node
     * 
     * @return a <code>Point2D</code> representing the coordinates of the port
     *         the edge wants to dock to or those of the default port when there
     *         was no or wrong port information int the edge.
     */
    protected Point2D getTargetDockingCoords(EdgeGraphicAttribute edgeAttr,
            NodeShape targetShape) {
        DockingAttribute docking = edgeAttr.getDocking();
        String targetPortName = docking.getTarget();

        Edge edge = (Edge) edgeAttr.getAttributable();
        Node targetNode = edge.getTarget();

        return getDockingCoords(targetPortName, targetShape, targetNode, false);
    }

    /**
     * Set and get bounds taking line width into account.
     * 
     * @param path
     *            DOCUMENT ME!
     * @param edgeAttr
     *            DOCUMENT ME!
     * 
     * @return bounding rectangle including line width.
     */
    protected Rectangle2D getThickBounds(GeneralPath path,
            EdgeGraphicAttribute edgeAttr) {
        double thickness = edgeAttr.getThickness()
                + edgeAttr.getFrameThickness();

        if (thickness > 1) {
            AffineTransform at = new AffineTransform();
            // at.setToTranslation(thickness + 6, thickness + 6);
            Shape shape1 = path.createTransformedShape(at);
            // at.setToTranslation(-thickness, -thickness);

            // Shape shape2 = path.createTransformedShape(at);

            Rectangle2D rect = shape1.getBounds2D();
            this.realBounds = new Rectangle2D.Double(rect.getX() - thickness
                    - 6, rect.getY() - thickness - 6, rect.getWidth() + 2
                    * thickness + 12, rect.getHeight() + 2 * thickness + 12);
            // this.realBounds.add(shape1.getBounds2D());
            // this.realBounds.add(shape2.getBounds2D());

        }

        return this.realBounds;
    }

    /**
     * Set and get bounds taking line width into account.
     * 
     * @param line
     *            DOCUMENT ME!
     * @param edgeAttr
     *            DOCUMENT ME!
     * 
     * @return bounding rectangle including line width.
     */
    protected Rectangle2D getThickBounds(Line2D line,
            EdgeGraphicAttribute edgeAttr) {
        this.realBounds = line.getBounds2D();

        double thickness = edgeAttr.getThickness()
                + edgeAttr.getFrameThickness();

        if (thickness > 1) {
            AffineTransform at = new AffineTransform();
            at.setToTranslation(thickness / 2d, thickness / 2d);

            Point2D s1 = at.transform(line2D.getP1(), null);
            Point2D e1 = at.transform(line2D.getP2(), null);
            at.setToTranslation(-thickness / 2d, -thickness / 2d);

            Point2D s2 = at.transform(line2D.getP1(), null);
            Point2D e2 = at.transform(line2D.getP2(), null);

            Rectangle2D addRect = new Rectangle2D.Double();
            addRect.setFrameFromDiagonal(s1.getX(), s1.getY(), e1.getX(), e1
                    .getY());
            this.realBounds.add(addRect);
            addRect.setFrameFromDiagonal(s2.getX(), s2.getY(), e2.getX(), e2
                    .getY());
            this.realBounds.add(addRect);
        }

        return this.realBounds;
    }

    /**
     * Creates an arrow from the classname found in the graphics attribute (if
     * there is one specified) and affixes it. Uses <code>getArrowTail</code>
     * The arrow is attached at the <code>target</code> point; it points in the
     * direction from <code>other</code> to <code>target</code>. The arrow shape
     * is saved in the according member variable. Since the edge should (if
     * there are arrow(s)) not any longer be attached to its intersection point
     * with the node but rather to the arrow anchor, this method returns the new
     * (or old if no arrow(s)) point where the edge should be attached at the
     * node.
     * 
     * @param edgeAttr
     *            the graphics attribute.
     * @param target
     *            the point where to attach the arrow.
     * @param other
     *            indicates the direction for the arrow.
     * 
     * @return (new) attachment point for the edge.
     * 
     * @throws ShapeNotFoundException
     *             DOCUMENT ME!
     */
    protected Point2D attachSourceArrow(EdgeGraphicAttribute edgeAttr,
            Point2D target, Point2D other) throws ShapeNotFoundException {
        Point2D newTarget = target;
        ArrowShape tailShape = null;

        String shapeClass = edgeAttr.getArrowtail();

        if (!shapeClass.equals("")) {
            try {
                tailShape = (ArrowShape) InstanceLoader
                        .createInstance(shapeClass);
            } catch (InstanceCreationException ie) {
                throw new ShapeNotFoundException(ie.toString());
            }

            // glue arrow on the line at the correct spot and rotation
            this.tailArrow = tailShape.affix(target, other, edgeAttr
                    .getThickness()
                    + edgeAttr.getFrameThickness());
            newTarget = tailShape.getAnchor();
        } else {
            tailArrow = null;
        }

        return newTarget;
    }

    /**
     * Creates an arrow from the classname found in the graphics attribute (if
     * there is one specified) and affixes it. Uses <code>getArrowHead</code>
     * The arrow is attached at the <code>target</code> point; it points in the
     * direction from <code>other</code> to <code>target</code>. The arrow shape
     * is saved in the according member variable. Since the edge should (if
     * there are arrow(s)) not any longer be attached to its intersection point
     * with the node but rather to the arrow anchor, this method returns the new
     * (or old if no arrow(s)) point where the edge should be attached at the
     * node.
     * 
     * @param edgeAttr
     *            the graphics attribute.
     * @param target
     *            the point where to attach the arrow.
     * @param other
     *            indicates the direction for the arrow.
     * 
     * @return (new) attachment point for the edge.
     * 
     * @throws ShapeNotFoundException
     *             DOCUMENT ME!
     */
    protected Point2D attachTargetArrow(EdgeGraphicAttribute edgeAttr,
            Point2D target, Point2D other) throws ShapeNotFoundException {
        Point2D newTarget = target;
        ArrowShape headShape = null;

        String shapeClass = edgeAttr.getArrowhead();

        if (!shapeClass.equals("")) {
            try {
                headShape = (ArrowShape) InstanceLoader
                        .createInstance(shapeClass);
            } catch (InstanceCreationException ie) {
                throw new ShapeNotFoundException(ie.toString());
            }

            // glue arrow on the line at the correct spot and rotation
            this.headArrow = headShape.affix(target, other, edgeAttr
                    .getThickness()
                    + edgeAttr.getFrameThickness());
            newTarget = headShape.getAnchor();
        } else {
            headArrow = null;
        }

        return newTarget;
    }

    /**
     * Returns the coordinates of the port named <code>portName</code>.
     * 
     * @param portName
     *            the name of the port the edge wants to dock to.
     * @param shape
     *            the shape of the node the edge wants to dock to. Needed to
     *            calculate the absolute coordinates of the ports.
     * @param node
     *            the node the edge wants to dock to. Needed to get to its port
     *            attributes.
     * @param out
     *            <code>true</code> if only common and outgoing ports should be
     *            searched, <code>false</code> if only common and ingoing ports
     *            should be searched.
     * 
     * @return a <code>Point2D</code> representing the coordinates of the port
     *         the edge wants to dock to or those of the default port when there
     *         was no or wrong port information int the edge.
     */
    private Point2D getDockingCoords(String portName, NodeShape shape,
            Node node, boolean out) {
        Rectangle2D sRect = shape.getRealBounds2D();
        Point2D point = new Point2D.Double();

        NodeGraphicAttribute nodeAttr = (NodeGraphicAttribute) node
                .getAttribute(GraphicAttributeConstants.GRAPHICS);

        if (portName.equals("")) {
            point = calculateDefaultDocking(nodeAttr, shape);
        } else {
            // boolean found = false;
            PortsAttribute ports = nodeAttr.getPorts();
            PortAttribute port = ports.getPort(portName, out);

            if (port == null) {
                // specified port not found
                point = calculateDefaultDocking(nodeAttr, shape);
            } else {
                CoordinateAttribute coords = port.getCoordinate();
                point.setLocation(sRect.getCenterX()
                        + ((coords.getX() * sRect.getWidth()) / 2d), sRect
                        .getCenterY()
                        + ((coords.getY() * sRect.getHeight()) / 2d));
            }
        }

        return point;
    }

    // /**
    // * Returns the <code>CoordinateAttribute</code> that represents the bend
    // * that is near the coordinates <code>x</code>, <code>y</code>.
    // * It returns null if no bend is near.
    // *
    // * @param x x coordinate relative to the coordinates of this shape.
    // * @param y y coordinate relative to the coordinates of this shape.
    // *
    // * @return the <code>CoordinateAttribute</code> of the bend hit or null if
    // * no bend was hit.
    // */
    // public CoordinateAttribute bendHit(double x, double y) {
    // return null;
    // }

    /**
     * Returns the coordinates of the default port of a node. (standard
     * implementation is the center of the node).
     * 
     * @param nodeAttr
     *            the graphics attribute of the node. Needed to get the (center)
     *            coordinates of the node.
     * @param shape
     *            the shape of the node. May be needed to to more sophisticated
     *            calculation of default ports.
     * 
     * @return absolute coordinates of default port.
     */
    private Point2D calculateDefaultDocking(NodeGraphicAttribute nodeAttr,
            NodeShape shape) {
        Point2D point = new Point2D.Double();
        CoordinateAttribute coord = nodeAttr.getCoordinate();
        point.setLocation(coord.getX(), coord.getY());

        return point;
    }

    protected Point2D calculateActualStartPoint(EdgeGraphicAttribute edgeAttr,
            NodeShape sourceShape, Point2D start, Point2D end)
            throws ShapeNotFoundException {
        Point2D newStart = sourceShape.getIntersection(new Line2D.Double(start,
                end));
        // if no intersection was found, just draw from / to docking
        if (newStart != null) {
            start = newStart;
        }
        return attachSourceArrow(edgeAttr, start, end);
    }

    protected Point2D calculateActualEndPoint(EdgeGraphicAttribute edgeAttr,
            NodeShape targetShape, Point2D start, Point2D end)
            throws ShapeNotFoundException {

        Point2D newStart = targetShape.getIntersection(new Line2D.Double(start,
                end));
        // if no intersection was found, just draw from / to docking
        if (newStart != null) {
            start = newStart;
        }
        return attachTargetArrow(edgeAttr, start, end);

    }

    /**
     * Trims the given list of bends to the bends outside of of both end nodes.
     * The parameter <code>bendsList</code> has to contain all bends of the
     * edge. The first and last <code>Point2D</code>s in the list are the
     * docking at the source and target node, respectively. This list will be
     * modified. After the call of the method the list contains all points which
     * lie outside of both end nodes. The first point is the attachment point of
     * the source arrow or the docking point when there is no source arrow. The
     * last point is the attachment point of the target arrow or the docking
     * point when there is no target arrow. Note that the list can be empty
     * after the call of the method. This happens when the end nodes cover the
     * edge completely.
     * 
     * @param edgeAttr
     *            the graphics attribute of the edge.
     * @param sourceShape
     *            the shape of the source node.
     * @param targetShape
     *            the shape of the target node.
     * @param bendsList
     *            All bends of the edge.
     * 
     * @return <code>true</code> when a odd number of bends at the start node
     *         has been removed. Only used by <code>SmoothLineEdgeShape</code>.
     */
    protected boolean trimToActualStartAndEndPoints(
            EdgeGraphicAttribute edgeAttr, NodeShape sourceShape,
            NodeShape targetShape, LinkedList<Point2D> bendsList)
            throws ShapeNotFoundException {

        boolean odd = false;
        try {
            Node source = ((Edge) edgeAttr.getAttributable()).getSource();
            Node target = ((Edge) edgeAttr.getAttributable()).getTarget();

            CoordinateAttribute sca = (CoordinateAttribute) source
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COORDINATE);

            CoordinateAttribute tca = (CoordinateAttribute) target
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COORDINATE);

            Point2D sourcePoint = new Point2D.Double(sca.getX(), sca.getY());
            Point2D targetPoint = new Point2D.Double(tca.getX(), tca.getY());

            if (!sourcePoint.equals(bendsList.getFirst())) {
                bendsList.addFirst(sourcePoint);
            }
            if (!targetPoint.equals(bendsList.getLast())) {
                bendsList.addLast(targetPoint);
            }

            Point2D newStart = bendsList.getFirst();
            Point2D newEnd = bendsList.getLast();
            if (sourceShape.getBounds().getWidth() != 0
                    || sourceShape.getBounds().getHeight() != 0) {
                while ((newStart = sourceShape
                        .getIntersection(new Line2D.Double(
                                bendsList.getFirst(), bendsList.get(1)))) == null) {
                    bendsList.removeFirst();
                    odd = !odd;
                }
                bendsList.removeFirst();
                if (!newStart.equals(bendsList.getFirst())) {
                    bendsList.addFirst(newStart);
                } else {
                    odd = !odd;
                }
            }
            if (targetShape.getBounds().getWidth() != 0
                    || targetShape.getBounds().getHeight() != 0) {
                while ((newEnd = targetShape.getIntersection(new Line2D.Double(
                        bendsList.getLast(), bendsList
                                .get(bendsList.size() - 2)))) == null) {
                    bendsList.removeLast();
                }
                bendsList.removeLast();
                if (!newEnd.equals(bendsList.getLast())) {
                    bendsList.addLast(newEnd);
                }
            }
            if (trimArrows) {
                newStart = attachSourceArrow(edgeAttr, newStart, bendsList
                        .get(1));
                newEnd = attachTargetArrow(edgeAttr, newEnd, bendsList
                        .get(bendsList.size() - 2));
            }
            bendsList.removeFirst();
            bendsList.addFirst(newStart);
            bendsList.removeLast();
            bendsList.addLast(newEnd);
            return odd;
        } catch (IndexOutOfBoundsException e) {
            while (!bendsList.isEmpty()) {
                bendsList.removeFirst();
            }
            return false;
        }
    }

    public void setTrimArrows(boolean trimArrows) {
        this.trimArrows = trimArrows;
    }

    public GeneralPath getLinePath() {
        return linePath;

    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
