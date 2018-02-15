// =============================================================================
//
//   GraphElementsDeletedEdit.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.undo;

import java.util.Collection;
import java.util.Map;

import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.undo.GraphElementsDeletionEdit;

/**
 * @author Kathrin Hanauer
 * @version $Revision$ $Date$
 */
public class GraphElementsDeletedEdit extends GraphElementsDeletionEdit {

    /**
     * 
     */
    private static final long serialVersionUID = 3341280284365871943L;

    /**
     * @param graphElements
     * @param graph
     * @param geMap
     */
    public GraphElementsDeletedEdit(Collection<GraphElement> graphElements,
            Graph graph, Map<GraphElement, GraphElement> geMap) {
        super(graphElements, graph, geMap);

        this.executed = true;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
