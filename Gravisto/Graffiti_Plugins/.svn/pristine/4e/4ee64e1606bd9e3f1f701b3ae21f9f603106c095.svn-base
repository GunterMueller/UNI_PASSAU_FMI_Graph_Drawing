// =============================================================================
//
//   AtomFinder.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation;

import java.util.List;

import org.graffiti.graph.Node;

/**
 * This is the interface all AtomFinder should implement.
 * 
 * @author Andreas
 * @version $Revision$ $Date$
 */
public interface AtomFinder {
    /**
     * This materialization of an AtomFinder should return all the atoms of the
     * subtree of the given root and returns them in width-decreasing and -
     * because they are atoms - also in height-increasing order.
     * 
     * @param root
     *            the root of the subtree for which we are trying to find the
     *            drawings that are atoms.
     * @return list of atoms in width-decreasing order
     */

    public List<LayoutComposition> findAtoms(Node root);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
