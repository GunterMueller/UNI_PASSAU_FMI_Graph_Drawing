// =============================================================================
//
//   ExtendedMouseMove.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.usergestures;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import org.graffiti.graph.GraphElement;
import org.graffiti.plugin.view.interactive.MouseGesture;
import org.graffiti.plugin.view.interactive.MouseMoveGesture;
import org.graffiti.plugins.modes.fast.Sector;
import org.graffiti.plugins.views.fast.MathUtil;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class ExtendedMouseMove extends MouseMoveGesture {
    private static MouseGestureFactory factory = new MouseGestureFactory() {
        @Override
        public MouseGesture createGesture(Point2D position,
                Point2D previousPosition, Point2D previousRawPosition,
                MouseEvent event, GraphElement element,
                boolean isOnShapeBorder, String bend, Sector horizontalSector,
                Sector verticalSector) {
            Point2D delta = MathUtil.subtract(position, previousPosition);
            return new ExtendedMouseMove(position, event.getPoint(), delta,
                    event.getModifiersEx(), element, isOnShapeBorder, bend,
                    horizontalSector, verticalSector);
        }
    };

    public static MouseGestureFactory getFactory() {
        return factory;
    }

    private Point2D rawPosition;
    private GraphElement element;
    private boolean isOnShapeBorder;
    private String bend;
    private Sector horizontalSector;
    private Sector verticalSector;

    public ExtendedMouseMove(Point2D position, Point2D rawPosition,
            Point2D delta, int modifierMask, GraphElement element,
            boolean isOnShapeBorder, String bend, Sector horizontalSector,
            Sector verticalSector) {
        super(position, delta, modifierMask);
        this.rawPosition = rawPosition;
        this.element = element;
        this.isOnShapeBorder = isOnShapeBorder;
        this.bend = bend;
        this.horizontalSector = horizontalSector;
        this.verticalSector = verticalSector;
    }

    public Point2D getRawPosition() {
        return rawPosition;
    }

    public GraphElement getElement() {
        return element;
    }

    public boolean isOnShapeBorder() {
        return isOnShapeBorder;
    }

    public String getBend() {
        return bend;
    }

    public Sector getHorizontalSector() {
        return horizontalSector;
    }

    public Sector getVerticalSector() {
        return verticalSector;
    }

    @Override
    public String toString() {
        return "ExtendedMouseMove[position: " + getPosition().toString()
                + ", delta: " + getDelta().toString() + ", element: "
                + (element == null ? "null" : element.toString())
                + ", isOnShapeBorder: " + isOnShapeBorder + "]";
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
