// =============================================================================
//
//   TreeCombinationList.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.reingoldtilford;

import java.util.LinkedList;

/**
 * <code>TreeCombinationList</code> is used to compose a {@link Tree} layout
 * from the layouts of its children. Trees are inserted at arbitrary positions
 * in the list.
 * <p>
 * <code>TreeCombinationList</code> consists a 'left'
 * {@link TreeCombinationStack} growing to the right and a 'right'
 * <code>TreeCombinationStack</code> growing to the left. Inserting a
 * <code>Tree</code> layout into this <code>TreeCombinationList</code> is
 * implemented by pushing it to the left stack. Moving the pointer that denotes
 * the position where new layouts shall be inserted into this list is
 * implemented by popping the <code>Tree</code> from one stack and pushing it
 * onto the other stack. {@link #apply(Tree)} is realized by shifting all trees
 * to the left stack and calling {@link TreeCombinationStack#apply(Tree)} on the
 * left stack.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class TreeCombinationList {
    /**
     * The left <code>TreeCombinationStack</code>.
     */
    private TreeCombinationStack leftStack;

    /**
     * The right <code>TreeCombinationStack</code>.
     */
    private TreeCombinationStack rightStack;

    /**
     * The count of tree layouts in this list.
     */
    private int treeCount;

    /**
     * Denotes the insertion position of new tree layouts in this list.
     * Inserting tree layouts in the leftmost position is represented by a value
     * of <code>0</code>. Inserting tree layouts at the rightmost position is
     * represented by a value of {@link #treeCount}.
     */
    private int pointer;

    /**
     * Creates a new <code>TreeCombinationList</code>.
     * 
     * @param algorithm
     *            the Reingold-Tilford algorithm.
     */
    public TreeCombinationList(ReingoldTilfordAlgorithm algorithm) {
        leftStack = new TreeCombinationStack(algorithm);
        rightStack = new TreeCombinationStack(algorithm);
        treeCount = 0;
        pointer = 0;
    }

    /**
     * Creates a new <code>TreeCombinationList</code> and inserts all tree
     * layouts of <code>trees</code> into it.
     * 
     * @param trees
     *            the tree layouts to be inserted into the new list.
     * @param algorithm
     *            the Reingold-Tilford algorithm.
     */
    public TreeCombinationList(LinkedList<Tree> trees,
            ReingoldTilfordAlgorithm algorithm) {
        leftStack = new TreeCombinationStack(trees, algorithm);
        rightStack = new TreeCombinationStack(algorithm);
        treeCount = trees.size();
        pointer = treeCount;
    }

    /**
     * Inserts <code>tree</code> at the current pointer position. The pointer
     * can be moved by {@link #incPointer()}, {@link #decPointer()} or
     * {@link #movePointer(int)}.
     * 
     * @param tree
     *            the tree to be inserted.
     * @throws LockedTreeException
     *             if <code>tree</code> is locked.
     * @see Tree#isLocked()
     */
    public void insert(Tree tree) {
        leftStack.push(tree, true);
        pointer++;
        treeCount++;
    }

    /**
     * Moves the insertion pointer one position to the right. It is realized by
     * popping a tree layout from the right stack and pushing it onto the left
     * stack.
     * 
     * @throws IndexOutOfBoundsException
     *             if the pointer is moved out to invalid positions (beneath the
     *             end of the list).
     */
    public void incPointer() {
        if (pointer == treeCount)
            throw new IndexOutOfBoundsException();
        Tree tree = rightStack.peek();
        rightStack.pop();
        leftStack.push(tree, true);
        pointer++;
    }

    /**
     * Moves the insertion pointer one position to the left. It is realized by
     * popping a tree layout from the left stack and pushing it onto the right
     * stack.
     * 
     * @throws IndexOutOfBoundsException
     *             if the pointer is moved out to invalid positions (below the
     *             start of the list).
     */
    public void decPointer() {
        if (pointer == 0)
            throw new IndexOutOfBoundsException();
        Tree tree = leftStack.peek();
        leftStack.pop();
        rightStack.push(tree, false);
        pointer--;
    }

    /**
     * Moves the insertion pointer to the passed position. It is realized by
     * calling {@link #incPointer()} or {@link #decPointer()} repeatedly until
     * the desired position is reached.
     * 
     * @param index
     *            the position the insertion pointer is moved to.
     */
    public void movePointer(int index) {
        if (index < 0 || index > treeCount)
            throw new IndexOutOfBoundsException();
        while (pointer < index) {
            incPointer();
        }
        while (pointer > index) {
            decPointer();
        }
    }

    /**
     * Returns the current pointer position.
     * 
     * @return the current pointer position.
     */
    public int getPointer() {
        return pointer;
    }

    /**
     * Returns the size of the list.
     * 
     * @return the size of the list.
     */
    public int getSize() {
        return treeCount;
    }

    /**
     * Returns the width of the combined tree layout if <code>tree<code> was
     * added to this list.
     * 
     * @param tree
     *            the tree layout whose insertion is to be tested.
     * @return the width of the combined tree layout if <code>tree</code> was
     *         added to this list.
     * @throws LockedTreeException
     *             if <code>tree</code> is locked.
     * @see Tree#isLocked()
     */
    public double testInsertion(Tree tree) {
        leftStack.push(tree, true);
        double width = leftStack.calculateCombinedWidth(rightStack);
        leftStack.pop();
        return width;
    }

    /**
     * Returns the width of the combined tree layout.
     * 
     * @return the width of the combined tree layout.
     */
    public double testWidth() {
        return leftStack.calculateCombinedWidth(rightStack);
    }

    /**
     * Applies the combined tree layout composed by this
     * <code>TreeCombinationList</code> to the tree layout of
     * <code>parent</code>.
     * 
     * @param parent
     *            the tree layout the combined tree layout composed by this
     *            <code>TreeCombinationList</code> is applied to. It must be the
     *            parent of the tree layouts inserted into this list.
     */
    public void apply(Tree parent) {
        if (pointer <= treeCount / 2) {
            movePointer(0);
            rightStack.apply(parent);
        } else {
            movePointer(treeCount);
            leftStack.apply(parent);
        }
    }

    public double bubbleDown(double currentWidth, boolean mayFlip) {
        if (pointer <= 0 || pointer >= treeCount)
            throw new IllegalStateException();
        pointer--;
        Tree leftTree = leftStack.peek();
        leftStack.pop();
        Tree rightTree = rightStack.peek();
        rightStack.pop();
        rightStack.push(leftTree, false);
        rightStack.push(rightTree, false);
        double width = testWidth();
        if (width <= currentWidth)
            return width;
        else {
            rightStack.pop();
            if (mayFlip) {
                rightStack.push(rightTree.getFlipped(), false);
                width = testWidth();
                if (width <= currentWidth)
                    return width;
                rightStack.pop();
            }

            rightStack.pop();
            rightStack.push(rightTree, false);
            rightStack.push(leftTree, false);
            return currentWidth;
        }
    }

    public double bubbleUp(double currentWidth, boolean mayFlip) {
        if (pointer <= 0 || pointer >= treeCount)
            throw new IllegalStateException();
        pointer++;
        Tree leftTree = leftStack.peek();
        leftStack.pop();
        Tree rightTree = rightStack.peek();
        rightStack.pop();
        leftStack.push(rightTree, true);
        leftStack.push(leftTree, true);
        double width = testWidth();
        if (width <= currentWidth)
            return width;
        else {
            leftStack.pop();
            if (mayFlip) {
                leftStack.push(leftTree.getFlipped(), true);
                width = testWidth();
                if (width <= currentWidth)
                    return width;
                leftStack.pop();
            }
            leftStack.pop();
            leftStack.push(leftTree, true);
            leftStack.push(rightTree, true);
            return currentWidth;
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
