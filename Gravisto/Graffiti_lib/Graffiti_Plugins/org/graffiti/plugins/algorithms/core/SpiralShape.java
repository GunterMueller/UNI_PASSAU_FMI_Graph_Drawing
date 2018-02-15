package org.graffiti.plugins.algorithms.core;

import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import org.graffiti.graph.Edge;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.view.NodeShape;
import org.graffiti.plugin.view.ShapeNotFoundException;
import org.graffiti.plugins.views.defaults.LineEdgeShape;

/**
 * represents an edge as a spiral segment
 */
@SuppressWarnings("nls")
public class SpiralShape extends LineEdgeShape {

    /**
     * default constructor
     */
    public SpiralShape() {
        super();
    }

    /**
     * This method is called when one of the incident nodes of the edge has
     * changed. It takes arguments for the center of the shape, the start point,
     * and the end point. The start and end points are specified in terms of
     * angle and radius. The spiral curve is formed by varying the angle and
     * radius smoothly between the two end points. It also uses information
     * about ports and coordinates. It attaches arrows if there are any.
     * 
     * @param edgeAttr
     *            the attribute that contains all necessary information to
     *            construct a spiral
     * @param sourceShape
     *            the <code>NodeShape</code> of the source node
     * @param targetShape
     *            the <code>NodeShape</code> of the target node
     */
    @Override
    public void buildShape(EdgeGraphicAttribute edgeAttr,
            NodeShape sourceShape, NodeShape targetShape)
            throws ShapeNotFoundException {

        this.graphicsAttr = edgeAttr;

        // get the current edge
        Edge edge = (Edge) edgeAttr.getAttributable();

        // get the center of the spiral
        Params p = new Params();
        Point2D center = p.getCenter();

        // get docking coordinates - start point is the node on inner radius
        Point2D start, end;
        if (edge.getSource().getInteger(GraphicAttributeConstants.LEVEL) <= edge
                .getTarget().getInteger(GraphicAttributeConstants.LEVEL)) {
            start = getSourceDockingCoords(edgeAttr, sourceShape);
            end = getTargetDockingCoords(edgeAttr, targetShape);
        } else {
            end = getSourceDockingCoords(edgeAttr, sourceShape);
            start = getTargetDockingCoords(edgeAttr, targetShape);
        }

        // compute start/end radius and angle of the spiral
        double startRadius = start.distance(center);
        double endRadius = end.distance(center);
        double startAngle, endAngle;
        if (start.getY() - center.getY() < 0) {
            startAngle = Math
                    .acos((start.getX() - center.getX()) / startRadius);
        } else {
            startAngle = Math.acos(-(start.getX() - center.getX())
                    / startRadius)
                    + Math.PI;
        }
        if (end.getY() - center.getY() < 0) {
            endAngle = Math.acos((end.getX() - center.getX()) / endRadius);
        } else {
            endAngle = Math.acos(-(end.getX() - center.getX()) / endRadius)
                    + Math.PI;
        }

        // compute a mirror point for the center concerning the start point
        Point2D mirror = new Point2D.Double(center.getX() + startRadius * 2
                * Math.cos(startAngle), center.getY() - startRadius * 2
                * Math.sin(startAngle));

        // set new start and end docking coordinates - on the inner side for
        // outer node (end) and on the outer side for inner node (start)
        Point2D newStart, newEnd;
        if (edge.getSource().getInteger(GraphicAttributeConstants.LEVEL) <= edge
                .getTarget().getInteger(GraphicAttributeConstants.LEVEL)) {
            this.line2D.setLine(start, mirror);
            newStart = sourceShape.getIntersection(this.line2D);
            this.line2D.setLine(end, center);
            newEnd = targetShape.getIntersection(this.line2D);
        } else {
            this.line2D.setLine(end, center);
            newEnd = sourceShape.getIntersection(this.line2D);
            this.line2D.setLine(start, mirror);
            newStart = targetShape.getIntersection(this.line2D);
        }

        // if no intersection was found, just draw from/to docking
        if (newStart != null) {
            start = newStart;
        }
        if (newEnd != null) {
            end = newEnd;
        }

        // attach arrows
        start = attachSourceArrow(edgeAttr, start, end);
        end = attachTargetArrow(edgeAttr, end, start);

        // compute new start/end radius and angle of the spiral
        startRadius = start.distance(center);
        endRadius = end.distance(center);
        if (start.getY() - center.getY() < 0) {
            startAngle = Math
                    .acos((start.getX() - center.getX()) / startRadius);
        } else {
            startAngle = Math.acos(-(start.getX() - center.getX())
                    / startRadius)
                    + Math.PI;
        }
        if (end.getY() - center.getY() < 0) {
            endAngle = Math.acos((end.getX() - center.getX()) / endRadius)
                    + (-edge.getInteger(GraphicAttributeConstants.OFFSET)) * 2
                    * Math.PI;
        } else {
            endAngle = Math.acos(-(end.getX() - center.getX()) / endRadius)
                    + Math.PI
                    + (-edge.getInteger(GraphicAttributeConstants.OFFSET)) * 2
                    * Math.PI;
        }

        // if start and endangle are equal - just draw a straight line
        if (startAngle == endAngle
                && edge.getInteger(GraphicAttributeConstants.OFFSET) == 0) {
            this.line2D = new Line2D.Double(start, end);
            this.linePath = new GeneralPath(this.line2D);
        } else {

            // get the bigger of the two radii
            double outerRadius = Math.max(startRadius, endRadius);

            // figure the spiral direction: 1 if angle increases, -1 otherwise
            int angleDirection = 0;
            if (startAngle < endAngle) {
                angleDirection = 1;
            } else {
                angleDirection = -1;
            }

            // get quality of the spiral approximation
            double flatness = outerRadius / p.getQuality();
            double angle = startAngle; // current angle
            double radius = startRadius; // current radius
            boolean done = false; // are we done yet?

            // add start point to path
            this.linePath.moveTo((float) start.getX(), (float) start.getY());

            while (!done) {

                // add current line
                this.linePath.lineTo((float) (center.getX() + radius
                        * Math.cos(angle)), (float) (center.getY() - radius
                        * Math.sin(angle)));

                // update angle
                double x = flatness / radius;
                if (Double.isNaN(x) || (x > 0.1)) {
                    angle += Math.PI / 4 * angleDirection;
                } else {
                    double y = 2 * x * x - 4 * x + 1;
                    angle += Math.acos(y) * angleDirection;
                }

                // Check whether we've gone past the end of the spiral
                if ((angle - endAngle) * angleDirection > 0) {
                    done = true;
                }

                // Now that we know the new angle, we can use interpolation to
                // figure out what the corresponding radius is
                double fractionComplete = (angle - startAngle)
                        / (endAngle - startAngle);
                radius = startRadius + (endRadius - startRadius)
                        * fractionComplete;

            }

            // add a line to the end point
            this.linePath.lineTo((float) end.getX(), (float) end.getY());

        }

        this.realBounds = this.linePath.getBounds2D();
        this.realBounds = getThickBounds(this.linePath, edgeAttr);

        if (this.headArrow != null) {
            this.realBounds.add(this.headArrow.getBounds2D());
        }

        if (this.tailArrow != null) {
            this.realBounds.add(this.tailArrow.getBounds2D());
        }

        AffineTransform at = new AffineTransform();
        at.setToTranslation(-this.realBounds.getX(), -this.realBounds.getY());
        this.headArrow = at.createTransformedShape(this.headArrow);
        this.tailArrow = at.createTransformedShape(this.tailArrow);
        this.linePath = new GeneralPath(this.linePath
                .createTransformedShape(at));
    }

    /**
     * A spiral is an open curve, not a closed area, so always return false
     */
    @Override
    public boolean contains(double x, double y) {
        return false;
    }

}
