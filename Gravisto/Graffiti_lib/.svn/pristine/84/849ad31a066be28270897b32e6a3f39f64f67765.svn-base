// =============================================================================
//
//   ChildOrderStrategy.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.reingoldtilford;

/**
 * Defines an interface for classes that know how to combine the tree layout of
 * siblings.
 * <p>
 * To provide a new policy for child order, create a class implementing
 * <code>ChildOrderStrategy</code><br>
 * 
 * <pre>
 * class MyChildOrderStrategy implements ChildOrderStrategy {
 *     public void combineChildren(Tree parent, ReingoldTilfordAlgorithm algorithm) {
 *         TreeCombinationStack stack = new TreeCombinationStack();
 * 
 *         // Insert children in the desired order here...
 * 
 *         stack.apply(parent);
 *     }
 * }
 * </pre>
 * 
 * and add a new member to this enumeration.<br>
 * 
 * <pre>
 * enum ChildOrderPolicy
 * {
 *      ...
 *      BUBBLE_SORT(new BubbleSortChildOrderStrategy(), "Bubble sort"),
 *      MY_POLICY(new MyChildOrderStrategy(), "My new child order"); // &lt;-- Add this line.
 *      ...
 * }
 * </pre>
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see ChildOrderPolicy
 */
public interface ChildOrderStrategy {
    /**
     * Combines the tree layouts of the children of parent.
     * <p>
     * <b>Preconditions:</b><br>
     * {@code parent.getChildren().size() > 1}
     * 
     * @param parent
     *            the <code>Tree</code> whose children are to be combined.
     * @param algorithm
     *            the Reingold-Tilford-Algorithm.
     * @see ChildOrderPolicy#combineChildren(Tree, ReingoldTilfordAlgorithm)
     */
    public void combineChildren(Tree parent, ReingoldTilfordAlgorithm algorithm);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
