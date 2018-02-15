// =============================================================================
//
//   BestInsertionChildOrderStrategy.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.reingoldtilford;

import java.util.LinkedList;

/**
 * Tries to minimize the width of the tree by successively inserting the
 * children into a {@link TreeCombinationList} at the position that respectively
 * yields in the minimal width of the list.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see ChildOrderPolicy#BEST_INSERTION
 */
public class BestInsertionChildOrderStrategy implements ChildOrderStrategy {
    /**
     * For a given <code>TreeCombinationList</code> and a tree to be added to
     * it, Worker tests the insertion at all positions and holds the record for
     * the so far best found insertion.
     * 
     * @author Andreas Glei&szlig;ner
     * @version $Revision$ $Date$
     */
    private class Worker {
        /**
         * The so far best found position where {@link #tree} can be inserted.
         */
        private int bestPos;

        /**
         * The width of {@link #list} if {@link #tree} is added at the position
         * {@link #bestPos}
         */
        private double minWidth;

        /**
         * Denotes if {@link #tree} or the flipped counterpart of {@link #tree}
         * shall be inserted in {@link #list}.
         */
        private boolean flip;

        /**
         * The list into which {@link #tree} is to be inserted.
         */
        private TreeCombinationList list;

        /**
         * The tree that is to be inserted into {@link #tree}.
         */
        private Tree tree;

        /**
         * Denotes if the insertion of the flipped counterpart of {@link #tree}
         * rather than <code>tree</code> itself shall also be considered.
         * 
         * @see Tree#getFlipped()
         */
        private boolean mayFlip;

        /**
         * Creates a new <code>Worker</code>.
         * 
         * @param list
         *            See {@link #list}.
         * @param tree
         *            See {@link #tree}.
         * @param mayFlip
         *            {@link #mayFlip}.
         */
        private Worker(TreeCombinationList list, Tree tree, boolean mayFlip) {
            this.list = list;
            this.tree = tree;
            this.mayFlip = mayFlip;
            flip = false;
            bestPos = 0;
            minWidth = Double.MAX_VALUE;
        }

        /**
         * Tests the insertion of {@link #tree} into {@link #list} at the
         * current position and records if it beats the by then best insertion.
         */
        private void test() {
            double width = list.testInsertion(tree);
            if (width < minWidth) {
                minWidth = width;
                bestPos = list.getPointer();
                flip = false;
            }
            if (mayFlip) {
                width = list.testInsertion(tree.getFlipped());
                if (width < minWidth) {
                    minWidth = width;
                    bestPos = list.getPointer();
                    flip = true;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void combineChildren(Tree parent, ReingoldTilfordAlgorithm algorithm) {
        if (parent.isShallow()) {
            // All children are leaves.
            // Every permutation would yield the same width.
            (new TreeCombinationStack(parent.getChildren(), algorithm))
                    .apply(parent);
        } else {
            boolean mayFlip = algorithm.isConsiderFlipping();
            LinkedList<Tree> children = new LinkedList<Tree>(parent
                    .getChildren());
            TreeCombinationList list = new TreeCombinationList(algorithm);
            // Take repeatedly an arbitrary tree out of children and insert it
            // at the respectively best position until there is no child left
            // for insertion.
            while (!children.isEmpty()) {
                Tree tree = children.removeFirst();
                Worker worker = new Worker(list, tree, mayFlip);
                int initialPos = list.getPointer();
                worker.test();
                // Move downwards while trying the insertion at each passed
                // position.
                while (list.getPointer() > 0) {
                    list.decPointer();
                    worker.test();
                }
                // Move to the initial position.
                list.movePointer(initialPos);
                // Move upwards while trying the insertion at each passed
                // position.
                while (list.getPointer() < list.getSize()) {
                    list.incPointer();
                    worker.test();
                }
                // Move to the best position found and perform the insertion.
                list.movePointer(worker.bestPos);
                if (worker.flip) {
                    list.insert(tree.getFlipped());
                } else {
                    list.insert(tree);
                }
            }
            list.apply(parent);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
