// =============================================================================
//
//   GraphElementsEdit.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphElementsEdit.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.undo;

import java.util.Map;

import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;

/**
 * <code>GraphElementsEdit</code> is abstract class for building edits belong to
 * the operations on graph elements like adding or removing.
 * 
 * @author $Author: gleissner $
 * @version $Revision: 5767 $ $Date: 2006-01-03 09:42:05 +0100 (Di, 03 Jan 2006)
 *          $
 */
public abstract class GraphElementsEdit extends GraffitiAbstractUndoableEdit {

    /**
     * 
     */
    private static final long serialVersionUID = -1661975677436116625L;
    /** Necessary graph reference */
    protected Graph graph;

    /**
     * Create a nes <code>GraphElementsEdit</code>.
     * 
     * @param graph
     *            a graph reference
     * @param geMap
     *            reference to the map supports the undo operations.
     */
    public GraphElementsEdit(Graph graph, Map<GraphElement, GraphElement> geMap) {
        super(geMap);
        this.graph = graph;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
