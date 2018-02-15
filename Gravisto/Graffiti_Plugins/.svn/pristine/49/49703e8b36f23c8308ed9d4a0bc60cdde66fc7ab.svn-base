// =============================================================================
//
//   ZoomEventDispatcher.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.managers;

import java.util.LinkedList;
import java.util.List;

import org.graffiti.plugin.view.Viewport;
import org.graffiti.plugin.view.ViewportAdapter;
import org.graffiti.plugin.view.ViewportListener;
import org.graffiti.plugin.view.Zoomable;

/**
 * Dispatches the change events of viewports to registered listeners.
 * 
 * @see Viewport
 * @see ViewportListener
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class ViewportEventDispatcher implements ViewportListener {
    private List<ViewportListener> listeners;

    public ViewportEventDispatcher() {
        listeners = new LinkedList<ViewportListener>();
    }

    public void addListener(ViewportListener viewportListener) {
        listeners.add(viewportListener);
    }

    public void removeListener(ViewportListener viewportListener) {
        listeners.remove(viewportListener);
    }

    /**
     * {@inheritDoc}
     */
    public void onViewportChange(Viewport viewport) {
        for (ViewportListener listener : listeners) {
            listener.onViewportChange(viewport);
        }
    }

    public void onZoomChange(Zoomable zoomable) {
        if (zoomable instanceof Viewport) {
            onViewportChange((Viewport) zoomable);
        } else {
            onViewportChange(new ViewportAdapter(zoomable));
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
