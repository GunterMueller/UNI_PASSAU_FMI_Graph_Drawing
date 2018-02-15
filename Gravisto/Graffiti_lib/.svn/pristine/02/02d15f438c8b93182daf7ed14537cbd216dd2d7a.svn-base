package org.graffiti.plugins.shapes.edges.circleLineSegmentationShape;

import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.LinkedList;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.view.NodeShape;
import org.graffiti.plugin.view.ShapeNotFoundException;
import org.graffiti.plugins.views.defaults.PolyLineEdgeShape;

/**
 * Edge shape consisting of a circle and a line.
 * 
 * The circle starts at the source node of the edge and ends at the point of
 * intersection of the target node of the edge and the center of the circle.
 * This point is connected to the target node via a straight line.
 * 
 * The circle's center can be set by a {@link CoordinateAttribute}.
 * 
 * @see GraphicAttributeConstants#CIRCLE_LINE_SEGMENTATION_SHAPE
 * @see GraphicAttributeConstants#CIRCLE_LINE_SEGMENTATION_CLASSNAME
 * @see GraphicAttributeConstants#CIRCLE_CENTER
 * @see GraphicAttributeConstants#CIRCLE_CENTER_PATH
 */
public class CircleLineSegmentationShape extends PolyLineEdgeShape {

    /**
     * The rotation angle used for interpolating the circle segment. Using a
     * smaller angle results in more calculations but increases the accuracy of
     * the placement of the arrow at the tail of the edge.
     */
    private static final double ROTATION_ANGLE = (Math.PI / 180) * 2;

    /**
     * The point used as the center in case no center attribute has been set or
     * the Attribute set is not of type {@link CoordinateAttribute}
     */
    private static Point2D DEFAULT_CIRCLE_CENTER = new Point2D.Float(0, 0);

    /**
     * @see org.graffiti.plugins.views.defaults.LineEdgeShape#buildShape(org.graffiti.graphics.EdgeGraphicAttribute,
     *      org.graffiti.plugin.view.NodeShape,
     *      org.graffiti.plugin.view.NodeShape)
     */
    @Override
    public void buildShape(EdgeGraphicAttribute graphics,
            NodeShape sourceShape, NodeShape targetShape)
            throws ShapeNotFoundException {
        assert graphics != null;
        assert sourceShape != null;
        assert targetShape != null;

        this.graphicsAttr = graphics;

        // calculate bend coordinates
        Edge edge = (Edge) this.graphicsAttr.getAttributable();
        Point2D centerCoords = getCenterCoordinates(edge);
        Point2D sourceCoords = getCoords(edge.getSource());
        Point2D targetCoords = getCoords(edge.getTarget());

        double distCenterTarget = targetCoords.distance(centerCoords);
        double circleRadius = sourceCoords.distance(centerCoords);

        double bendX = centerCoords.getX()
                + circleRadius
                * ((targetCoords.getX() - centerCoords.getX()) / distCenterTarget);
        double bendY = centerCoords.getY()
                + circleRadius
                * ((targetCoords.getY() - centerCoords.getY()) / distCenterTarget);

        Point2D bendCoords = new Point2D.Double(bendX, bendY);

        // create bends list
        LinkedList<Point2D> bends = new LinkedList<Point2D>();

        Point2D sourceDockingCoords = getSourceDockingCoords(graphics,
                sourceShape);
        Point2D targetDockingCoords = getTargetDockingCoords(graphics,
                targetShape);

        if (centerCoords.equals(sourceCoords)) {
            bends.add(sourceDockingCoords);
        } else {
            if (isRightOf(sourceCoords, centerCoords, targetCoords)) {
                // clockwise
                Point2D currentBend = sourceDockingCoords;
                while (isRightOf(currentBend, centerCoords, bendCoords)) {
                    bends.add(currentBend);
                    currentBend = rotateNodeAroundCenter(currentBend,
                            centerCoords, true);
                }
            } else {
                // counterclockwise
                Point2D currentBend = sourceDockingCoords;
                while (!isRightOf(currentBend, centerCoords, bendCoords)) {
                    bends.add(currentBend);
                    currentBend = rotateNodeAroundCenter(currentBend,
                            centerCoords, false);
                }
            }

            bends.add(bendCoords);
        }

        bends.add(targetDockingCoords);

        trimToActualStartAndEndPoints(graphicsAttr, sourceShape, targetShape,
                bends);

        // create new path
        this.linePath = new GeneralPath();

        if (bends.size() >= 2) {
            Point2D first = bends.getFirst();
            this.linePath.moveTo(first.getX(), first.getY());

            Arc2D arc = new Arc2D.Float();
            arc.setArcByCenter(centerCoords.getX(), centerCoords.getY(),
                    circleRadius, 0, 0, Arc2D.OPEN);

            if (isRightOf(sourceCoords, centerCoords, bendCoords)) {
                arc.setAngles(bendCoords, first);
                if (arc.getAngleExtent() <= 180) {
                    this.linePath.append(arc, false);
                }
                this.linePath.moveTo(bendCoords.getX(), bendCoords.getY());
            } else {
                arc.setAngles(first, bendCoords);
                if (arc.getAngleExtent() <= 180) {
                    this.linePath.append(arc, true);
                } else {
                    this.linePath.moveTo(bendCoords.getX(), bendCoords.getY());
                }
            }

            Point2D last = bends.getLast();
            this.linePath.lineTo(last.getX(), last.getY());
        }

        // add the arrow to the edge
        getThickBounds(this.linePath, graphics);

        if (headArrow != null) {
            this.realBounds.add(headArrow.getBounds2D());
        }

        if (tailArrow != null) {
            this.realBounds.add(tailArrow.getBounds2D());
        }
    }

    /**
     * Rotates a point around a center point. The angle is specified in the
     * constant ROTATION_ANGLE. The direction of the rotation can be set via
     * parameter.
     * 
     * @param initial
     *            The point the be rotated.
     * @param center
     *            The point around which the rotation is to be done.
     * @param counterclockwise
     *            <code>true</code> if the rotation is to be in counterclockwise
     *            direction, <code>false</code> if it is to be in clockwise
     *            direction.
     * @return The point given as the parameter initial rotated around the point
     *         given as the parameter center.
     */
    private Point2D rotateNodeAroundCenter(Point2D initial, Point2D center,
            boolean counterclockwise) {
        assert initial != null;
        assert center != null;

        double radius = initial.distance(center);

        double initialUnitVectorX = (initial.getX() - center.getX()) / radius;
        double initialUnitVectorY = (initial.getY() - center.getY()) / radius;

        // do rotation
        int sign = counterclockwise ? 1 : -1;
        double cos = Math.cos(sign * ROTATION_ANGLE);
        double sin = Math.sin(sign * ROTATION_ANGLE);

        double targetUnitVectorX = initialUnitVectorX * cos
                - initialUnitVectorY * sin;
        double targetUnitVectorY = initialUnitVectorX * sin
                + initialUnitVectorY * cos;

        // move and scale target vector
        double targetVectorX = targetUnitVectorX * radius + center.getX();
        double targetVectorY = targetUnitVectorY * radius + center.getY();

        return new Point2D.Double(targetVectorX, targetVectorY);
    }

    /**
     * Returns the center coordinates of the given edge. The edges' center
     * coordinates must be saved in a {@link CoordinateAttribute} in
     * GraphicsAttributesConstants.CIRCLE_CENTER_PATH.
     * 
     * @param edge
     *            The edge containing the information about the circle.
     * @return The coordinates of the center of the circle.
     * 
     * @see GraphicAttributeConstants#CIRCLE_CENTER
     * @see GraphicAttributeConstants#CIRCLE_CENTER_PATH
     */
    private Point2D getCenterCoordinates(Edge edge) {
        assert edge != null;

        Point2D centerCoords = DEFAULT_CIRCLE_CENTER;

        if (edge
                .containsAttribute(GraphicAttributeConstants.CIRCLE_CENTER_PATH)) {
            Attribute circleCenterAttr = edge
                    .getAttribute(GraphicAttributeConstants.CIRCLE_CENTER_PATH);

            if (circleCenterAttr instanceof CoordinateAttribute) {
                CoordinateAttribute coordAttr = (CoordinateAttribute) circleCenterAttr;
                centerCoords = new Point2D.Double(coordAttr.getX(), coordAttr
                        .getY());
            }
        }

        return centerCoords;
    }

    /**
     * Returns the coordinates of a Node.
     * 
     * @param n
     *            The Node whose coordinates are to be returned.
     * @return The coordinates of the given Node.
     */
    private Point2D getCoords(Node n) {
        assert n != null;

        CoordinateAttribute ca = (CoordinateAttribute) n
                .getAttribute(GraphicAttributeConstants.COORD_PATH);

        return ca.getCoordinate();
    }

    /**
     * Tests whether a given point lies right to a edge from source to target.
     * 
     * @param point
     *            To coordinates of a point that is to be tested for its
     *            placement.
     * @param source
     *            The source of the edge.
     * @param target
     *            The target of the edge.
     * @return <code>true</code> if point lies right to the edge from source to
     *         target, <code>false</code> otherwise.
     */
    private boolean isRightOf(Point2D point, Point2D target, Point2D source) {
        assert point != null;
        assert target != null;
        assert source != null;

        double w = (source.getY() - point.getY())
                * (target.getX() - source.getX())
                - (point.getX() - source.getX())
                * (source.getY() - target.getY());

        return w < 0;
    }
}
