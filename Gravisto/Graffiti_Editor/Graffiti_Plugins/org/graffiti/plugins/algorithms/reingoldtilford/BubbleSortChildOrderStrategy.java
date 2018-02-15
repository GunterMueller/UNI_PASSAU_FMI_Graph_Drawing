// =============================================================================
//
//   BubbleSortChildOrderStrategy.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.reingoldtilford;

/**
 * Tries to minimize the width of the tree by inserting all children into a
 * {@link TreeCombinationList} and then reducing the width by repeatedly swaping
 * trees in the list.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see ChildOrderPolicy#BUBBLE_SORT
 */
public class BubbleSortChildOrderStrategy implements ChildOrderStrategy {

    /**
     * {@inheritDoc}
     */
    public void combineChildren(Tree parent, ReingoldTilfordAlgorithm algorithm) {
        boolean mayFlip = algorithm.isConsiderFlipping();
        // Insert all children into the list.
        TreeCombinationList list = new TreeCombinationList(
                parent.getChildren(), algorithm);
        double width = list.testWidth();
        double oldWidth = Double.MAX_VALUE;
        // Try to swap subtrees until the width did not decrease any more.
        while (width < oldWidth) {
            oldWidth = width;
            // Swaps at pointer positions that alternate between the right and
            // left side.
            list.decPointer();
            do {
                width = list.bubbleDown(width, mayFlip);
            } while (list.getPointer() > 0);
            list.incPointer();
            do {
                width = list.bubbleUp(width, mayFlip);
            } while (list.getPointer() < list.getSize());
        }
        list.apply(parent);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
