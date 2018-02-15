// =============================================================================
//
//   DrawingSet.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.java2d;

import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.graffiti.plugins.views.fast.OptimizationPolicy;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class DrawingSet {
    public DrawingSet() {
        marker = new Rectangle2D.Double(1, 1, 6, 6);
    }

    public AffineTransform affineTransform;
    public Shape clip;
    private double minX;
    private double minY;
    private double maxX;
    private double maxY;
    public OptimizationPolicy optimizationPolicy;
    public Rectangle2D marker;
    public Stroke defaultStroke;

    public void prepare(AffineTransform affineTransform, Shape clip,
            OptimizationPolicy optimizationPolicy) {
        this.affineTransform = affineTransform;
        this.clip = clip;
        this.optimizationPolicy = optimizationPolicy;
        minX = Double.POSITIVE_INFINITY;
        minY = Double.POSITIVE_INFINITY;
        maxX = Double.NEGATIVE_INFINITY;
        maxY = Double.NEGATIVE_INFINITY;
    }

    private boolean isValid(Rectangle2D rectangle) {
        if (Double.isNaN(rectangle.getX()) || Double.isNaN(rectangle.getY())
                || Double.isNaN(rectangle.getWidth())
                || Double.isNaN(rectangle.getHeight()))
            return false;
        return true;
    }

    public void addToBounds(Rectangle2D rectangle) {
        if (isValid(rectangle)) {
            minX = Math.min(minX, rectangle.getMinX());
            minY = Math.min(minY, rectangle.getMinY());
            maxX = Math.max(maxX, rectangle.getMaxX());
            maxY = Math.max(maxY, rectangle.getMaxY());
        }
    }

    public void addToBounds(Point2D point) {
        minX = Math.min(minX, point.getX());
        minY = Math.min(minY, point.getY());
        maxX = Math.max(maxX, point.getX());
        maxY = Math.max(maxY, point.getY());
    }

    public void addToBoundsX(double x) {
        minX = Math.min(minX, x);
        maxX = Math.max(maxX, x);
    }

    public void addToBoundsY(double y) {
        minY = Math.min(minY, y);
        maxY = Math.max(maxY, y);
    }

    public Rectangle2D getBounds() {
        if (minX == Double.POSITIVE_INFINITY)
            return new Rectangle2D.Double();
        else
            return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
