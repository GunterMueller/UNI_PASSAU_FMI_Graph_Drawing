// =============================================================================
//
//   ViewportDummy.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view;

import java.awt.geom.AffineTransform;

/**
 * Wraps a {@link Zoomable} as a {@link Viewport}.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class ViewportAdapter extends Viewport {
    private Zoomable zoomable;

    public ViewportAdapter(Zoomable zoomable) {
        this.zoomable = zoomable;
    }

    public AffineTransform getZoomTransform() {
        return zoomable.getZoomTransform();
    }

    public void setZoom(double factor) {
        zoomable.setZoom(factor);
    }

    @Override
    public double getZoom() {
        return zoomable.getZoom();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
