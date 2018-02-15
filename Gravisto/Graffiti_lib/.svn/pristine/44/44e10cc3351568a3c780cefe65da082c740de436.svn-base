// =============================================================================
//
//   AllPermutationsChildOrderStrategy.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.reingoldtilford;

import java.util.LinkedList;
import java.util.Set;

/**
 * Tries all possible permutations of children to minimize the width of the tree
 * layout.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see ChildOrderPolicy#ALL_PERMUTATIONS
 */
public class AllPermutationsChildOrderStrategy implements ChildOrderStrategy {
    /**
     * Does the actual job.
     * 
     * @author Andreas Glei&szlig;ner
     * @version $Revision$ $Date$
     */
    private class Permutator {
        /**
         * The Reingold-Tilford algorithm.
         */
        private ReingoldTilfordAlgorithm algorithm;

        /**
         * The best permutation found so far.
         */
        private LinkedList<Tree> bestPermutation;

        /**
         * The width of the so far best permutation.
         */
        private double minWidth;

        /**
         * The <code>TreeCombinationStack</code> on that all permutations are
         * generated.
         */
        private TreeCombinationStack treeCombinationStack;

        /**
         * Creates a new <code>Permutator</code>
         * 
         * @param children
         *            the children that are to be combined.
         * @param algorithm
         *            the Reingold-Tilford algorithm.
         */
        private Permutator(LinkedList<Tree> children,
                ReingoldTilfordAlgorithm algorithm) {
            this.algorithm = algorithm;
            minWidth = Double.MAX_VALUE;
            Set<Tree> newSet = algorithm.createSet(children);
            for (Tree tree : children) {
                treeCombinationStack = new TreeCombinationStack(tree, algorithm);
                newSet.remove(tree);
                tryAllPermutations(newSet);
                newSet.add(tree);
                treeCombinationStack.pop();
            }
        }

        /**
         * Tries all permutations of the remaining <code>trees</code> to push on
         * the {@link #treeCombinationStack} while the current content of the
         * stack is considered as fixed.
         * 
         * @param trees
         *            the trees to be pushed on stack in all possible
         *            permutations.
         */
        private void tryAllPermutations(Set<Tree> trees) {
            int size = trees.size();
            Set<Tree> newSet = algorithm.createSet(trees);
            for (Tree tree : trees) {
                treeCombinationStack.push(tree, true);
                testPermutation(size, newSet, tree);
                treeCombinationStack.pop();
                if (algorithm.isConsiderFlipping()) {
                    Tree flippedTree = tree.getFlipped();
                    treeCombinationStack.push(flippedTree, true);
                    testPermutation(size, newSet, tree);
                    treeCombinationStack.pop();
                }

            }
        }

        /**
         * Adds <code>tree</code> on the stack and tries all permutations of the
         * trees in <code>newSet</code> besides <code>tree</code> to push on the
         * stack while the current content of the stack is considered as fixed.
         * 
         * @param size
         *            the size of <code>newSet</code>
         * @param newSet
         *            the trees that are (without <code>tree</code>) to be
         *            pushed on stack in all possible permutations. Contains
         *            <code>tree</code>.
         * @param tree
         *            the tree to be pushed on the stack before trying to add
         *            the rest of <code>newSet</code>.
         */
        private void testPermutation(int size, Set<Tree> newSet, Tree tree) {
            if (size == 1) {
                double width = treeCombinationStack.getWidth();
                if (width < minWidth) {
                    minWidth = width;
                    bestPermutation = new LinkedList<Tree>(treeCombinationStack
                            .getTrees());
                }
            } else {
                newSet.remove(tree);
                tryAllPermutations(newSet);
                newSet.add(tree);
            }
        }

        /**
         * Returns a new TreeCombinationStack which contains all children pushed
         * in optimal order.
         * 
         * @return a new TreeCombinationStack which contains all children pushed
         *         in optimal order.
         */
        private TreeCombinationStack getBestPermutation() {
            return new TreeCombinationStack(bestPermutation, algorithm);
        }
    }

    /**
     * Singleton for <code>AllPermutationsChildOrderStrategy</code>.
     */
    private static AllPermutationsChildOrderStrategy singleton = new AllPermutationsChildOrderStrategy();

    /**
     * Returns the single AllPermutationsChildOrderStrategy object.
     * 
     * @return the single AllPermutationsChildOrderStrategy object.
     */
    public static AllPermutationsChildOrderStrategy getSingleton() {
        return singleton;
    }

    /**
     * {@inheritDoc}
     */
    public void combineChildren(Tree tree, ReingoldTilfordAlgorithm algorithm) {
        if (tree.isShallow()) {
            // All children are leaves.
            // Every permutation would yield the same width.
            (new TreeCombinationStack(tree.getChildren(), algorithm))
                    .apply(tree);
        } else {
            (new Permutator(tree.getChildren(), algorithm))
                    .getBestPermutation().apply(tree);
        }
    }

    /**
     * Prevents illegal instanciations.
     * 
     * @see #getSingleton()
     */
    private AllPermutationsChildOrderStrategy() {
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
