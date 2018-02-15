// =============================================================================
//
//   ExtendedMouseDrag.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.usergestures;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import org.graffiti.graph.GraphElement;
import org.graffiti.plugin.view.interactive.MouseDragGesture;
import org.graffiti.plugin.view.interactive.MouseGesture;
import org.graffiti.plugins.modes.fast.Sector;
import org.graffiti.plugins.views.fast.MathUtil;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class ExtendedMouseDrag extends MouseDragGesture {
    private static MouseGestureFactory factory = new MouseGestureFactory() {
        @Override
        public MouseGesture createGesture(Point2D position,
                Point2D previousPosition, Point2D previousRawPosition,
                MouseEvent event, GraphElement element,
                boolean isOnShapeBorder, String bend, Sector horizontalSector,
                Sector verticalSector) {
            Point2D delta = MathUtil.subtract(position, previousPosition);
            Point2D rawDelta = MathUtil.subtract(event.getPoint(),
                    previousRawPosition);
            int buttonId = 0;
            int mask = event.getModifiersEx();
            if ((mask & InputEvent.BUTTON1_DOWN_MASK) == InputEvent.BUTTON1_DOWN_MASK) {
                buttonId = MouseEvent.BUTTON1;
            } else if ((mask & InputEvent.BUTTON3_DOWN_MASK) == InputEvent.BUTTON3_DOWN_MASK) {
                buttonId = MouseEvent.BUTTON3;
            } else if ((mask & InputEvent.BUTTON2_DOWN_MASK) == InputEvent.BUTTON2_DOWN_MASK) {
                buttonId = MouseEvent.BUTTON2;
            }
            return new ExtendedMouseDrag(position, event.getPoint(), delta,
                    rawDelta, mask, buttonId, element, isOnShapeBorder, bend,
                    horizontalSector, verticalSector);
        }
    };

    public static MouseGestureFactory getFactory() {
        return factory;
    }

    private Point2D rawPosition;
    private Point2D rawDelta;
    private GraphElement element;
    private boolean isOnShapeBorder;
    private String bend;
    private Sector horizontalSector;
    private Sector verticalSector;

    public ExtendedMouseDrag(Point2D position, Point2D rawPosition,
            Point2D delta, Point2D rawDelta, int modifierMask, int buttonId,
            GraphElement element, boolean isOnShapeBorder, String bend,
            Sector horizontalSector, Sector verticalSector) {
        super(position, delta, modifierMask, buttonId);
        this.rawPosition = rawPosition;
        this.rawDelta = rawDelta;
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

    public Point2D getRawDelta() {
        return rawDelta;
    }

    @Override
    public String toString() {
        return "ExtendedMouseDrag[position: " + getPosition().toString()
                + ", delta: " + getDelta().toString() + ", modifiers: "
                + "[...]" + ", element: "
                + (element == null ? "null" : element.toString())
                + ", isOnShapeBorder: " + isOnShapeBorder + "]";
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
