// =============================================================================
//
//   ChildOrderPolicy.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.reingoldtilford;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The order in which the subtrees of a node a drawn from left to right. The
 * order affects the width of the tree so some policies are used to minimize the
 * width. Each policy consists of a name and a {@link ChildOrderStrategy} that
 * does the actual calculations.
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
 * @see ChildOrderStrategy
 */
enum ChildOrderPolicy {
    /**
     * The order of the subtrees is conserved as it has previously appeared.
     * 
     * @see VisualChildOrderStrategy
     */
    VISUAL_ORDER(new VisualChildOrderStrategy(), "Visual order"),

    /**
     * The order of the subtrees is obtained from the position of their root
     * nodes in the adjacency list of the parent node.
     * 
     * @see IdleChildOrderStrategy
     */
    ADJACENCY_LIST(new IdleChildOrderStrategy(), "Adjacency list"),

    /**
     * Heuristic using {@link BestFitDecreaseChildOrderStrategy} for minimizing
     * the width of the tree.
     */
    BEST_FIT_DECREASE(new BestFitDecreaseChildOrderStrategy(),
            "Best-fit decrease"),

    /**
     * Heuristic using {@link BestInsertionChildOrderStrategy} for minimizing
     * the width of the tree.
     */
    BEST_INSERTION(new BestInsertionChildOrderStrategy(), "Best insertion"),

    /**
     * Heuristic using {@link AllPermutationsChildOrderStrategy} for minimizing
     * the width of the tree.
     */
    ALL_PERMUTATIONS(AllPermutationsChildOrderStrategy.getSingleton(),
            "Try all permutations"),

    /**
     * Heuristic using {@link BubbleSortChildOrderStrategy} for minimizing the
     * width of the tree.
     */
    BUBBLE_SORT(new BubbleSortChildOrderStrategy(), "Bubble sort");

    /**
     * The names of the policies.
     * 
     * @see #getNames()
     */
    private static ArrayList<String> names;

    /**
     * The <code>ChildOrderStrategy</code> used by this enumeration member.
     */
    private ChildOrderStrategy strategy;

    /**
     * Returns the names of the policies. To get the name of a specific policy,
     * such as <code>VISUAL_ORDER</code>, you can write<br> {@code
     * ChildOrderPolicy.getNames().get(
     * ChildOrderPolicy.VISUAL_ORDER.ordinal())}
     * 
     * @return the names of the policies.
     * @see #names
     */
    public static List<String> getNames() {
        return Collections.unmodifiableList(names);
    }

    /**
     * Combines the tree layouts of the children of parent using the strategy of
     * this enumeration member.
     * <p>
     * <b>Preconditions:</b><br>
     * {@code tree.getChildren().size() > 1}
     * 
     * @param tree
     *            the <code>Tree</code> whose children are to be combined.
     * @param algorithm
     *            the Reingold-Tilford-Algorithm.
     * @see ChildOrderStrategy#combineChildren(Tree, ReingoldTilfordAlgorithm)
     */
    public void combineChildren(Tree tree, ReingoldTilfordAlgorithm algorithm) {
        strategy.combineChildren(tree, algorithm);
    }

    /**
     * Creates a new <code>ChildOrderPolicy</code> enumeration member.
     * 
     * @param strategy
     *            the strategy the new policy employs.
     * @param name
     *            the name of the new policy.
     */
    private ChildOrderPolicy(ChildOrderStrategy strategy, String name) {
        addName(name);
        this.strategy = strategy;
    }

    /**
     * Is called by the constructor of this policy to add its name to the
     * <code>names</code> list.
     * 
     * @param name
     *            the name of the new policy.
     * @see #getNames()
     */
    private void addName(String name) {
        if (names == null) {
            names = new ArrayList<String>();
        }
        names.add(name);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
