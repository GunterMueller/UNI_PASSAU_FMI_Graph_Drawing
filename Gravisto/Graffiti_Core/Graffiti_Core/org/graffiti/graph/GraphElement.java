// =============================================================================
//
//   GraphElement.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphElement.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.graph;

import org.graffiti.attributes.Attributable;

/**
 * Interfaces a graph element. A graph element knows the graph it belongs to and
 * can contain attributes.
 * 
 * @version $Revision: 5767 $
 * 
 * @see Node
 * @see Edge
 */
public interface GraphElement extends Attributable {

    /**
     * Returns the Graph the GraphElement belongs to.
     * 
     * @return the Graph the GraphElement belongs to.
     */
    public Graph getGraph();

    /**
     * Removes the GraphElement from the graph.
     */
    public void remove();
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
