// =============================================================================
//
//   IdleChildOrderStrategy.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.reingoldtilford;

/**
 * <code>ChildOrderStrategy</code> which orders the subtrees by the position of
 * their root nodes in the adjacency list of the parent's root node.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see ChildOrderPolicy#ADJACENCY_LIST
 */
class IdleChildOrderStrategy implements ChildOrderStrategy {

    /**
     * {@inheritDoc}
     */
    public void combineChildren(Tree tree, ReingoldTilfordAlgorithm algorithm) {
        (new TreeCombinationStack(tree.getChildren(), algorithm)).apply(tree);
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
