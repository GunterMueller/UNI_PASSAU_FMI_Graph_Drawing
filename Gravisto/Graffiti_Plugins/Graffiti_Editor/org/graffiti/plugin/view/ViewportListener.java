// =============================================================================
//
//   ZoomListener.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ViewportListener.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.view;

/**
 * DOCUMENT ME!
 * 
 * @author Andreas Glei&szlig;ner
 */
public interface ViewportListener {
    /**
     * Invoked when a {@code Viewport} has changed its zoom, rotation or pan.
     * From within this method, calls to {@link Zoomable#setZoom(double)},
     * {@link Viewport#setRotation(double)} and
     * {@link Viewport#setTranslation(java.awt.geom.Point2D)} are discouraged as
     * it may cause deadlocks.
     * 
     * @param viewport
     *            the {@code Viewport} that has changed its zoom, rotation or
     *            pan.
     */
    public void onViewportChange(Viewport viewport);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
