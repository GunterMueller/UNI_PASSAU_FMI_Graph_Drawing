// =============================================================================
//
//   MouseGestureFactory.java
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
import org.graffiti.plugins.modes.fast.Sector;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class MouseGestureFactory {
    public abstract MouseGesture createGesture(Point2D position,
            Point2D previousPosition, Point2D previousRawPosition,
            MouseEvent event, GraphElement element, boolean isOnShapeBorder,
            String bend, Sector horizontalSector, Sector verticalSector);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
