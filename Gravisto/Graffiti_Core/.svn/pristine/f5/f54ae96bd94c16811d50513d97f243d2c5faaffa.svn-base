// =============================================================================
//
//   Viewport.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.graffiti.editor.MainFrame;
import org.graffiti.managers.ViewportEventDispatcher;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class Viewport implements Zoomable {
    /**
     * {@inheritDoc}
     */
    public double getZoom() {
        AffineTransform transform = getZoomTransform();
        return (transform.getScaleX() + transform.getScaleY()) * 0.5;
    }

    /**
     * Returns the rotation in radians.
     * 
     * @return the rotation angle in radians. 0.0 is the default value.
     */
    public double getRotation() {
        return 0.0;
    }

    /**
     * Returns the translation.
     * 
     * @return the translation. (0.0, 0.0) is the default value.
     */
    public Point2D getTranslation() {
        return new Point2D.Double();
    }

    /**
     * Returns a rectangle enclosing all graph elements.
     * 
     * @return a rectangle enclosing all graph elements in logical (attribute
     *         system) coordinates.
     */
    public Rectangle2D getLogicalElementsBounds() {
        return new Rectangle2D.Double(-0.5, -0.5, 1.0, 1.0);
    }

    /**
     * Returns the bounds of the display component.
     * 
     * @return the bounds of the display component in display coordinates.
     */
    public Rectangle2D getDisplayBounds() {
        return new Rectangle2D.Double(-0.5, -0.5, 1.0, 1.0);
    }

    /**
     * Returns the bounds of the display component.
     * 
     * @return the bounds of the display component in logical (attribute system)
     *         coordinates.
     */
    public Rectangle2D getlogicalDisplayBounds() {
        return new Rectangle2D.Double(-0.5, -0.5, 1.0, 1.0);
    }

    /**
     * Sets the rotation in radians. Should call
     * {@link ViewportEventDispatcher#onViewportChange(Viewport)} on the {@code
     * ViewportEventDispatcher} returned by
     * {@link MainFrame#getViewportEventDispatcher()}. Do not call {@code
     * setRotation} from within an implementation of
     * {@link ViewportListener#onViewportChange(Viewport)}, as it may cause a
     * deadlock. An actual change of the rotation is not guaranteed. The {@code
     * Viewport} may arbitrarily restrict the values.
     * 
     * @param angle
     *            the rotation angle in radians. 0.0 is the default value.
     */
    public void setRotation(double angle) {
        // Default implementation ignores setting.
    }

    /**
     * Sets the translation. Should call
     * {@link ViewportEventDispatcher#onViewportChange(Viewport)} on the {@code
     * ViewportEventDispatcher} returned by
     * {@link MainFrame#getViewportEventDispatcher()}. Do not call {@code
     * setRotation} from within an implementation of
     * {@link ViewportListener#onViewportChange(Viewport)}, as it may cause a
     * deadlock. An actual change of the translation is not guaranteed. The
     * {@code Viewport} may arbitrarily restrict the values.
     * 
     * @param translation
     *            the translation to set. (0.0, 0.0) is the default value.
     */
    public void setTranslation(Point2D translation) {
        // Default implementation ignores setting.
    }

    /**
     * Simultanously sets zoom, rotation and translation. This is useful as the
     * viewport else may modify the translation due to a call to setZoom etc.,
     * which makes simultanously forcing all three to specific values tricky.
     * 
     * @param zoomFactor
     *            the zoom factor. See {@link Zoomable#setZoom(double)}.
     * @param angle
     *            the rotation angle. See {@link #setRotation(double)}.
     * @param translation
     *            the translation. See {@link #setTranslation(Point2D)}.
     */
    public void setSimultanously(double zoomFactor, double angle,
            Point2D translation) {
        // Default implemtation ignores rotation and translation.
        setZoom(zoomFactor);
    }

    public Point2D transform(Point2D point) {
        return point;
    }

    public Point2D inverseTransform(Point2D point) {
        return point;
    }

    public AffineTransform getZoomRotationTransform() {
        return getZoomTransform();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
