// =============================================================================
//
//   EdgeEvent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: EdgeEvent.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.event;

import org.graffiti.graph.Edge;

/**
 * Contains an edge event. An <code>EdgeEvent</code> object is passed to every
 * <code>EdgeListener</code> or <code>AbstractEdgeListener</code> object which
 * is registered to receive the "interesting" edge events using the component's
 * <code>addEdgeListener</code> method. (<code>AbstractEdgeListener</code>
 * objects implement the <code>EdgeListener</code> interface.) Each such
 * listener object gets an <code>EdgeEvent</code> containing the edge event.
 * 
 * @version $Revision: 5767 $
 * 
 * @see EdgeListener
 * @see AbstractEdgeListener
 */
public class EdgeEvent extends AbstractEvent {
    /**
     * 
     */
    private static final long serialVersionUID = -6168680374664962917L;

    /**
     * Constructs an edge event object with the specified source component.
     * 
     * @param edge
     *            the edge that originated the event.
     */
    public EdgeEvent(Edge edge) {
        super(edge);
    }

    /**
     * Returns the originator of the event.
     * 
     * @return the edge that has been changed by this event.
     */
    public Edge getEdge() {
        return (Edge) getSource();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
