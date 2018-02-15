// =============================================================================
//
//   TreeVisitor.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.reingoldtilford;

/**
 * Classes implementing <code>TreeVisitor</code> perform some task on a
 * {@link Tree} layout. <code>T</code> denotes the return type of the
 * {@link #visit(Tree)} method.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public interface TreeVisitor<T> {
    /**
     * Performs some task on <code>tree</code>.
     * 
     * @param tree
     *            the tree layout the task is performed on.
     * @return the result of the task.
     */
    public T visit(Tree tree);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
