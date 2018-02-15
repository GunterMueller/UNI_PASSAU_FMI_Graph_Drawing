// =============================================================================
//
//   Zoomable.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Zoomable.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.view;

import java.awt.geom.AffineTransform;

import org.graffiti.editor.MainFrame;
import org.graffiti.managers.ViewportEventDispatcher;

/**
 * {@link View}s implementing {@code} allow zooming.
 * 
 * @see Viewport
 * @author Andreas Glei&szlig;ner
 */
public interface Zoomable {
    /**
     * Returns the {@code AffineTransform} representing the current zoom.
     * 
     * @return the {@code AffineTransform} representing the current zoom.
     */
    public abstract AffineTransform getZoomTransform();

    /**
     * Returns the zoom factor.
     * 
     * @return the zoom factor. Smaller values yield in the display of a greater
     *         area with fewer details ("a view from greater distance"). 1.0 is
     *         the default value. Must be a finite value greater than 0.0.
     */
    public double getZoom();

    /**
     * Sets the scale factor. Should call
     * {@link ViewportEventDispatcher#onViewportChange(Viewport)} on the {@code
     * ZoomEventDispatcher} returned by
     * {@link MainFrame#getViewportEventDispatcher()}. Do not call {@code
     * setZoom} from within an implementation of
     * {@link ViewportListener#onViewportChange(Viewport)}, as it may cause a
     * deadlock. An actual change of the zoom is not guaranteed. The {@code
     * Zoomable} may arbitrarily restrict the values.
     * 
     * @param factor
     *            the zoom factor to set. Smaller values yield in the display of
     *            a greater area with fewer details ("a view from greater
     *            distance"). 1.0 is the default value. Must be a finite value
     *            greater than 0.0.
     */
    public void setZoom(double factor);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
