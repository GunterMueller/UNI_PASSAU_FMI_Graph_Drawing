package org.graffiti.plugins.algorithms.cyclicLeveling.spiralshape;

import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.LinkedList;

import org.graffiti.attributes.Attribute;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.plugin.view.NodeShape;
import org.graffiti.plugin.view.ShapeNotFoundException;
import org.graffiti.plugins.views.defaults.PolyLineEdgeShape;

/**
 * This Shape implementation represents a spiral curve
 * 
 * @author Raymund F�l�p
 */
public class SpiralShape extends PolyLineEdgeShape {

    public SpiralShape() {
        super();
    }

    /**
     * @see org.graffiti.plugins.views.defaults.LineEdgeShape#buildShape(org.graffiti.graphics.EdgeGraphicAttribute,
     *      org.graffiti.plugin.view.NodeShape,
     *      org.graffiti.plugin.view.NodeShape)
     */
    @Override
    public void buildShape(EdgeGraphicAttribute graphics, NodeShape source,
            NodeShape target) throws ShapeNotFoundException {
        // this assignment is necessary, but i don't know why
        this.graphicsAttr = graphics;

        // the complete path is collected in the variable pointsList
        LinkedList<Point2D> pointsList = new LinkedList<Point2D>();

        // the first point is at the source node
        pointsList.addFirst(getSourceDockingCoords(graphics, source));

        // iterate over the edge's bends and add a spiral segment for each bend
        for (Attribute a : graphics.getBends().getCollection().values()) {
            addSpiralSegment(pointsList, ((CoordinateAttribute) a)
                    .getCoordinate());
        }

        // go from the last bend to the target node
        addSpiralSegment(pointsList, getTargetDockingCoords(graphics, target));

        trimToActualStartAndEndPoints(graphicsAttr, source, target, pointsList);

        // store the bendsList in the linePath member
        this.linePath = new GeneralPath();
        for (Point2D p : pointsList)
            if (p == pointsList.getFirst()) {
                this.linePath.moveTo(p.getX(), p.getY());
            } else {
                this.linePath.lineTo(p.getX(), p.getY());
            }

        /* add the arrow to the edge */
        getThickBounds(this.linePath, graphics);

        if (headArrow != null) {
            this.realBounds.add(headArrow.getBounds2D());
        }

        if (tailArrow != null) {
            this.realBounds.add(tailArrow.getBounds2D());
        }
    }

    /**
     * Appends a spiral segment to the pointsList that starts at the last point
     * of bendsList and ends at endPoint.
     */
    private void addSpiralSegment(LinkedList<Point2D> pointsList,
            Point2D endPoint) {
        Point2D startPoint = pointsList.getLast();

        double radius = startPoint.distance(new Point2D.Float());
        double endRadius = endPoint.distance(new Point2D.Float());

        double angle = computeAngle(startPoint);
        double endAngle = computeAngle(endPoint);
        if (endAngle >= angle) {
            angle += 2 * PI;
        }

        double angleStep = -2 * (PI / 180); // step width: 2 degrees
        double radiusStep = (endAngle - angle) / angleStep;
        radiusStep = (endRadius - radius) / radiusStep;

        while (angle + angleStep > endAngle) {
            angle += angleStep;
            radius += radiusStep;
            pointsList.add(new Point2D.Double(cos(angle) * radius, sin(angle)
                    * radius));
        }

        pointsList.add(new Point2D.Double(cos(endAngle) * endRadius,
                sin(endAngle) * endRadius));
    }

    /**
     * This method computes the angle between the vectors (center, p) and the
     * horizontal line trough center.
     */
    private double computeAngle(Point2D p) {
        return atan2(p.getY(), p.getX()) + 2 * PI;
    }
}