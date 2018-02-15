// =============================================================================
//
//   NodeEvent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: NodeEvent.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.event;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;

/**
 * Contains a node event. A <code>NodeEvent</code> object is passed to every
 * <code>NodeListener</code> or <code>AbstractNodeListener</code> object which
 * is registered to receive the "interesting" node events using the component's
 * <code>addNodeListener</code> method. (<code>AbstractNodeListener</code>
 * objects implement the <code>NodeListener</code> interface.) Each such
 * listener object gets a <code>NodeEvent</code> containing the node event.
 * 
 * @version $Revision: 5767 $
 * 
 * @see NodeListener
 * @see AbstractNodeListener
 */
public class NodeEvent extends AbstractEvent {
    /**
     * 
     */
    private static final long serialVersionUID = -5518693239781586203L;
    /** The edge that might have been responsible for the NodeEvent. */
    private Edge edge;

    /**
     * Constructs a graph event object with the specified source components.
     * 
     * @param node
     *            the node that originated the event.
     * @param edge
     *            the edge that originated the event.
     */
    public NodeEvent(Node node, Edge edge) {
        super(node);
        this.edge = edge;
    }

    /**
     * Returns the edge that originated this event. E.g.: the edge that has been
     * added to the incoming egdes list of the node. Might return <tt>null</tt>.
     * 
     * @return The edge that originated the event.
     */
    public Edge getEdge() {
        return edge;
    }

    /**
     * Returns the node that has been changed by this event.
     * 
     * @return The node that has been changed by this event.
     */
    public Node getNode() {
        return (Node) getSource();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
