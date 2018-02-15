// =============================================================================
//
//   TriangulationAlgorithm.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.triangulation;

import java.util.LinkedList;

import org.graffiti.graph.Edge;

/**
 * Interface for all triangulation algorithms.
 * 
 * @author hofmeier
 * @version $Revision$ $Date$
 */
public interface TriangulationAlgorithm {
    /**
     * Performs the triangulation
     * 
     * @return the edges added during the triangulation
     */
    public LinkedList<Edge> triangulate();
}
