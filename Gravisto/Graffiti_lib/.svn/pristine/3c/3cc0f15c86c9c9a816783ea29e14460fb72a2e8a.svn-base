// =============================================================================
//
//   BestFitDecreaseChildOrderStrategy.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.reingoldtilford;

import java.util.LinkedList;
import java.util.Set;

/**
 * Tries to minimize the width of the tree layout by succesively adding the
 * child that respectively contributes least to an increase of the
 * {@link TreeCombinationStack}.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see ChildOrderPolicy#BEST_FIT_DECREASE
 */
public class BestFitDecreaseChildOrderStrategy implements ChildOrderStrategy {

    /**
     * Holds the data for the tree that so far yields in the least width
     * increase when beeing added to the given <code>TreeCombinationStack</code>
     * .
     * 
     * @author Andreas Glei&szlig;ner
     * @version $Revision$ $Date$
     */
    private class BestRecord {
        /**
         * See {@link #getWidth()}.
         */
        private double width;

        /**
         * See {@link #tree} .
         */
        private Tree tree;

        /**
         * See {@link #isFlip()}
         */
        private boolean flip;

        /**
         * Creates a new <code>BestRecord</code>
         * 
         * @param width
         *            See {@link #getWidth()}.
         * @param tree
         *            See {@link #getTree()}.
         * @param flip
         *            See {@link #isFlip()}.
         */
        public BestRecord(double width, Tree tree, boolean flip) {
            this.width = width;
            this.tree = tree;
            this.flip = flip;
        }

        /**
         * Returns the width of the <code>TreeCombinationStack</code> if
         * <code>tree</code> is pushed.
         * 
         * @return the width of the <code>TreeCombinationStack</code> if
         *         <code>tree</code> is pushed.
         */
        public double getWidth() {
            return width;
        }

        /**
         * Returns the tree that so far yields in the least width increase when
         * beeing added to the given <code>TreeCombinationStack</code>.
         * 
         * @return the tree that so far yields in the least width increase when
         *         beeing added to the given <code>TreeCombinationStack</code>.
         */
        public Tree getTree() {
            return tree;
        }

        /**
         * Returns if the flipped counterpart of <code>tree</code> shall be
         * pushed rather than <code>tree</code> itself.
         * 
         * @see Tree#getFlipped()
         * @return if the flipped counterpart of <code>tree</code> shall be
         *         pushed rather than <code>tree</code> itself.
         */
        public boolean isFlip() {
            return flip;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void combineChildren(Tree parent, ReingoldTilfordAlgorithm algorithm) {
        if (parent.isShallow()) {
            (new TreeCombinationStack(parent.getChildren(), algorithm))
                    .apply(parent);
        } else {
            boolean mayFlip = algorithm.isConsiderFlipping();
            LinkedList<Tree> children = parent.getChildren();
            Set<Tree> newSet = algorithm.createSet(children);
            double maxWidth = 0;
            Tree maxWidthTree = null;
            for (Tree child : children) {
                double width = child.getWidth();
                if (width > maxWidth) {
                    maxWidth = width;
                    maxWidthTree = child;
                }
            }

            TreeCombinationStack treeCombinationStack = new TreeCombinationStack(
                    maxWidthTree, algorithm);
            newSet.remove(maxWidthTree);
            BestRecord bestLeft = testInsertion(treeCombinationStack, newSet,
                    true, mayFlip);
            BestRecord bestRight = testInsertion(treeCombinationStack, newSet,
                    false, mayFlip);

            // Push the respectively best tree until there are no more trees to
            // be pushed.
            while (!newSet.isEmpty()) {
                if (bestLeft.getWidth() < bestRight.getWidth()) {
                    Tree tree = bestLeft.getTree();
                    if (bestLeft.isFlip()) {
                        treeCombinationStack.push(tree.getFlipped(), false);
                    } else {
                        treeCombinationStack.push(tree, false);
                    }
                    newSet.remove(tree);
                    if (bestRight.getTree() == tree) {
                        bestRight = testInsertion(treeCombinationStack, newSet,
                                true, mayFlip);
                    }
                    bestLeft = testInsertion(treeCombinationStack, newSet,
                            false, mayFlip);
                } else {
                    Tree tree = bestRight.getTree();
                    if (bestRight.isFlip()) {
                        treeCombinationStack.push(tree.getFlipped(), true);
                    } else {
                        treeCombinationStack.push(tree, true);
                    }
                    newSet.remove(tree);
                    if (bestLeft.getTree() == tree) {
                        bestLeft = testInsertion(treeCombinationStack, newSet,
                                false, mayFlip);
                    }
                    bestRight = testInsertion(treeCombinationStack, newSet,
                            true, mayFlip);
                }
            }
            treeCombinationStack.apply(parent);
        }
    }

    /**
     * Tests for each tree in <code>trees</code> pushing it to
     * <code>treeCombinationStack</code> and returns the candidate that let to
     * the thinnest stack.
     * 
     * @param treeCombinationStack
     *            the stack the trees are pushed onto.
     * @param trees
     *            the candidates.
     * @param right
     *            <code>true</code> if the trees shall be pushed on the right
     *            side.<br>
     *            <code>false</code> if the trees shall be pushed on the left
     *            side.<br>
     * @param mayFlip
     *            <code>true</code> iff the flipped counterparts of the
     *            candidates shall also be tested.
     * @return the data of the best candidate.
     * @see Tree#getFlipped()
     */
    private BestRecord testInsertion(TreeCombinationStack treeCombinationStack,
            Set<Tree> trees, boolean right, boolean mayFlip) {
        double minWidth = Double.MAX_VALUE;
        Tree bestTree = null;
        boolean flip = false;
        for (Tree tree : trees) {
            double width = treeCombinationStack.testInsertion(tree, right);
            if (width < minWidth) {
                minWidth = width;
                bestTree = tree;
                flip = false;
            }
            if (mayFlip) {
                Tree flippedTree = tree.getFlipped();
                width = treeCombinationStack.testInsertion(flippedTree, right);
                if (width < minWidth) {
                    minWidth = width;
                    bestTree = tree;
                    flip = true;
                }
            }
        }
        return new BestRecord(minWidth, bestTree, flip);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
