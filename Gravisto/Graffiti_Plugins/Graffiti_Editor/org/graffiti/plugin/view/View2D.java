// =============================================================================
//
//   View2D.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: View2D.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.view;

import java.awt.Graphics2D;

/**
 * A 2D view as a graphical representation for a graph.
 */
public interface View2D extends View {
    public Viewport getViewport();

    public void zoomToFitAfterRedraw();

    public void print(Graphics2D g, int width, int height);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
