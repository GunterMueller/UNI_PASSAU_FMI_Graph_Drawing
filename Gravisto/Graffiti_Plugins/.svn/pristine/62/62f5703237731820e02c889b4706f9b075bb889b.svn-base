// =============================================================================
//
//   TreeCombinationStack.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.reingoldtilford;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Stack;

import org.graffiti.plugins.tools.debug.DebugImage;
import org.graffiti.plugins.tools.debug.DebugSession;
import org.graffiti.plugins.tools.debug.DebugWriter;

/**
 * <code>TreeCombinationStack</code> is used to compose a {@link Tree} layout
 * from the layouts of its children. The subtrees may be pushed (
 * {@link #push(Tree, boolean)}) on the right or the left side of the
 * combination. Pushing a tree onto the stack can be reverted by {@link #pop()}
 * in a LIFO manner. A tree pushed onto the stack is locked {see @link
 * Tree#isLocked()} until it is popped from the stack.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class TreeCombinationStack {
    /**
     * Stores the information necessary to revert a
     * {@link TreeCombinationStack#push(Tree, boolean)} operation.
     * 
     * @author gleissne
     * @version $Revision$ $Date$
     */
    class Change {
        /*
         * Fields used to store the current state in order to revert the stack
         * later.
         */
        boolean right;
        private double previousFirstRelativeLeft;
        private double previousLastChildLeft;
        private double previousContourLeft;
        private double previousWidth;
        private double previousHeight;
        private BasicContourNodeList previousLeftContour;
        private BasicContourNodeList previousRightContour;
        private BasicContourNodeList.Iterator connectingElement;
        private ContourNode otherCurrentNode;
        private double otherCurrentNodeDx;
        private double otherCurrentNodeDy;
        private BasicContourNodeList.Iterator last;
        private BasicContourNodeList.Iterator otherListLast;
        private double otherListLastX;
        private double rightContourExtension;
        private GrowingDirection previousGrowingDirection;

        Change(boolean right) {
            this.right = right;
            previousFirstRelativeLeft = relativeLeft.getFirst();
            previousLastChildLeft = lastChildLeft;
            previousContourLeft = contourLeft;
            previousLeftContour = leftContour;
            previousRightContour = rightContour;
            previousWidth = width;
            previousHeight = height;
            rightContourExtension = 0.0;
            previousGrowingDirection = growingDirection;
        }

        /**
         * Undoes a {@link TreeCombinationStack#push(Tree, boolean)} operation.
         */
        void revert() {
            if (rightContourExtension != 0.0) {
                rightContour.extendRightContour(-rightContourExtension);
            }
            if (connectingElement != null) {
                connectingElement.revert(this);
            }
            if (right) {
                trees.getLast().unlock();
                relativeLeft.removeLast();
                trees.removeLast();
            } else {
                trees.getFirst().unlock();
                relativeLeft.removeFirst();
                trees.removeFirst();
            }
            relativeLeft.set(0, previousFirstRelativeLeft);
            lastChildLeft = previousLastChildLeft;
            contourLeft = previousContourLeft;
            leftContour = previousLeftContour;
            rightContour = previousRightContour;
            width = previousWidth;
            height = previousHeight;
            treeCount--;
            growingDirection = previousGrowingDirection;
        }

        /**
         * Returns <code>otherCurrentNode</code>.
         * 
         * @return <code>otherCurrentNode</code>.
         */
        ContourNode getOtherCurrentNode() {
            return otherCurrentNode;
        }

        /**
         * Sets <code>otherCurrentNode</code>.
         * 
         * @param otherCurrentNode
         */
        void setOtherCurrentNode(ContourNode otherCurrentNode) {
            this.otherCurrentNode = otherCurrentNode;
        }

        /**
         * Returns <code>otherCurrentNodeDx</code>.
         * 
         * @return <code>otherCurrentNodeDx</code>.
         */
        double getOtherCurrentNodeDx() {
            return otherCurrentNodeDx;
        }

        /**
         * Sets <code>otherCurrentNodeDx</code>.
         * 
         * @param otherCurrentNodeDx
         */
        void setOtherCurrentNodeDx(double otherCurrentNodeDx) {
            this.otherCurrentNodeDx = otherCurrentNodeDx;
        }

        /**
         * Returns <code>otherCurrentNodeDy</code>.
         * 
         * @return <code>otherCurrentNodeDy</code>.
         */
        double getOtherCurrentNodeDy() {
            return otherCurrentNodeDy;
        }

        /**
         * Sets <code>otherCurrentNodeDy</code>.
         * 
         * @param otherCurrentNodeDy
         */
        void setOtherCurrentNodeDy(double otherCurrentNodeDy) {
            this.otherCurrentNodeDy = otherCurrentNodeDy;
        }

        /**
         * Returns <code>last</code>.
         * 
         * @return <code>last</code>.
         */
        BasicContourNodeList.Iterator getLast() {
            return last;
        }

        /**
         * Sets <code>last</code>.
         * 
         * @param last
         */
        void setLast(BasicContourNodeList.Iterator last) {
            this.last = last;
        }

        /**
         * Returns <code>otherListLast</code>.
         * 
         * @return <code>otherListLast</code>.
         */
        BasicContourNodeList.Iterator getOtherListLast() {
            return otherListLast;
        }

        /**
         * Sets <code>otherListLast</code>.
         * 
         * @param otherListLast
         */
        void setOtherListLast(BasicContourNodeList.Iterator otherListLast) {
            this.otherListLast = otherListLast;
        }

        /**
         * Returns <code>otherListLastX</code>.
         * 
         * @return <code>otherListLastX</code>.
         */
        double getOtherListLastX() {
            return otherListLastX;
        }

        /**
         * Sets <code>otherListLastX</code>.
         * 
         * @param otherListLastX
         */
        void setOtherListLastX(double otherListLastX) {
            this.otherListLastX = otherListLastX;
        }

        /**
         * Sets <code>rightContourExtension</code>.
         * 
         * @param rightContourExtension
         */
        void setRightContourExtension(double rightContourExtension) {
            this.rightContourExtension = rightContourExtension;
        }

        /**
         * Sets <code>connectingElement</code>.
         * 
         * @param connectingElement
         */
        void setConnectingElement(
                BasicContourNodeList.Iterator connectingElement) {
            this.connectingElement = connectingElement;
        }

    }

    /**
     * Denotes if all tree layouts are pushed onto the same side.
     * 
     * @author Andreas Glei&szlig;ner
     * @version $Revision$ $Date$
     */
    private enum GrowingDirection {
        /**
         * The stack contains at least two tree layouts and they were pushed to
         * different sides.
         */
        UNDIRECTED,

        /**
         * The stack contains only one tree layout or all tree layouts were
         * pushed to the right side.
         */
        RIGHT,

        /**
         * The stack contains at least two tree layouts and they were pushed to
         * the left side.
         */
        LEFT
    };

    /**
     * The Reingold-Tilford algorithm.
     */
    private ReingoldTilfordAlgorithm algorithm;

    /**
     * The tree layouts contained in this stack.
     */
    private LinkedList<Tree> trees;

    /**
     * The x-coordinates of the tree layouts relative to their respective left
     * neighbour. The x-coordinate of the leftmost (i.e. first) tree layout in
     * this stack is given relative to the coordinate system of the combined
     * tree layouts.
     */
    private LinkedList<Double> relativeLeft;

    /**
     * The changes of the push operations are stored in <code>changeStack</code>
     * for the pop operations to undo them.
     */
    private Stack<Change> changeStack;

    /**
     * The count of tree layouts in this stack.
     */
    int treeCount;

    /**
     * The width of the combined tree layout.
     */
    private double width;

    /**
     * The height of the combined tree layout.
     */
    private double height;

    /**
     * See {@link GrowingDirection}
     */
    private GrowingDirection growingDirection;

    /**
     * x-coordinate of the origin of the contour lines relative in the
     * coordinate system of the combined tree layout.
     * 
     * @see #leftContour
     * @see #rightContour
     */
    private double contourLeft;

    /**
     * The left contour of the combined tree layout.
     */
    private BasicContourNodeList leftContour;

    /**
     * The right contour of the combined tree layout.
     */
    private BasicContourNodeList rightContour;

    /**
     * x-coordinate of the rightmost tree layout pushed onto this stack in the
     * coordinate system of the combined tree layout.
     */
    private double lastChildLeft;

    /**
     * Creates an empty <code>TreeCombinationStack</code>.
     * 
     * @param algorithm
     *            the Reingold-Tilford algorithm.
     */
    public TreeCombinationStack(ReingoldTilfordAlgorithm algorithm) {
        this.algorithm = algorithm;
        trees = new LinkedList<Tree>();
        relativeLeft = new LinkedList<Double>();
        treeCount = 0;
        width = 0;
        growingDirection = GrowingDirection.RIGHT;
    }

    /**
     * Creates a <code>TreeCombinationStack</code> an pushes <code>tree</code>
     * onto it.
     * 
     * @param tree
     *            the tree to be pushed onto the stack.
     * @param algorithm
     *            the Reingold-Tilford algorithm.
     * @throws LockedTreeException
     *             if <code>tree</code> is locked.
     * @see Tree#isLocked()
     */
    public TreeCombinationStack(Tree tree, ReingoldTilfordAlgorithm algorithm) {
        this.algorithm = algorithm;
        trees = new LinkedList<Tree>();
        relativeLeft = new LinkedList<Double>();
        growingDirection = GrowingDirection.RIGHT;
        addInitialNode(tree);
    }

    /**
     * Creates a new <code>TreeCombinationStack</code> an pushes the trees in
     * <code>trees</code> onto it (subsequently on the right sight).
     * 
     * @param trees
     *            the trees to be pushed onto the stack.
     * @param algorithm
     *            the Reingold-Tilford algorithm.
     * @throws LockedTreeException
     *             if on of the trees is locked.
     * @see Tree#isLocked()
     */
    public TreeCombinationStack(LinkedList<Tree> trees,
            ReingoldTilfordAlgorithm algorithm) {
        this.algorithm = algorithm;
        this.trees = new LinkedList<Tree>();
        relativeLeft = new LinkedList<Double>();
        growingDirection = GrowingDirection.RIGHT;
        for (Tree tree : trees) {
            push(tree, true);
        }
    }

    /**
     * Pushes tree on the empty stack.
     * 
     * @param tree
     *            the tree to be pushed on the stack.
     * @throws LockedTreeException
     *             if <code>tree</code> is locked.
     * @see Tree#isLocked()
     */
    private void addInitialNode(Tree tree) {
        tree.lock();
        trees.add(tree);
        relativeLeft.add(0.0);
        treeCount = 1;
        width = tree.getWidth();
        height = tree.getHeight();
        leftContour = tree.getLeftContour();
        rightContour = tree.getRightContour();
        contourLeft = tree.getNodeLeft();
        changeStack = new Stack<Change>();
        lastChildLeft = 0.0;
    }

    /**
     * Pushes <code>tree</code> onto the stack. The contour lines of the stack
     * and the tree are combined. This operation can be reverted by a call to
     * {@link #pop()}.
     * 
     * @param tree
     *            the tree to be pushed onto the stack.
     * @param right
     *            <code>true</code> if the tree is added to the right side. <br>
     *            <code>false</code> if the tree is added to the left side.
     * @throws LockedTreeException
     *             if <code>tree</code> is locked.
     * @see Tree#isLocked()
     */
    public void push(Tree tree, boolean right) {
        if (treeCount == 0) {
            addInitialNode(tree);
        } else {
            if (right) {
                pushRight(tree);
            } else {
                pushLeft(tree);
            }
        }
    }

    /**
     * Pushes <code>tree</code> onto the stack. The tree is added on the right
     * side.
     * 
     * @param tree
     *            the tree to be pushed onto the stack.
     * @throws LockedTreeException
     *             if <code>tree</code> is locked.
     * @see Tree#isLocked()
     */
    private void pushRight(Tree tree) {
        tree.lock();
        Change change = new Change(true);
        if (treeCount == 1) {
            growingDirection = GrowingDirection.RIGHT;
        } else {
            if (growingDirection == GrowingDirection.LEFT) {
                growingDirection = GrowingDirection.UNDIRECTED;
            }
        }
        trees.addLast(tree);
        treeCount++;
        double minimalVerticalDistance = algorithm.getMinimalVerticalDistance();

        ContourCombinationInfo contourCombinationInfo;
        if (minimalVerticalDistance == 0) {
            contourCombinationInfo = rightContour.calculateCombination(
                    algorithm.getMinimalHorizontalDistance() + contourLeft
                            - tree.getNodeLeft(), tree.getLeftContour());
        } else {
            contourCombinationInfo = new TranslatedContourNodeList(
                    minimalVerticalDistance, rightContour)
                    .calculateCombination(algorithm
                            .getMinimalHorizontalDistance()
                            + contourLeft - tree.getNodeLeft(), tree
                            .getLeftContour());
        }
        double shift = contourCombinationInfo.getShift();

        if (shift >= 0) {
            relativeLeft.addLast(shift - lastChildLeft);
        } else {
            relativeLeft.set(0, relativeLeft.getFirst() - shift);
            lastChildLeft -= shift;
            contourLeft -= shift;
            relativeLeft.addLast(-lastChildLeft);
            shift = 0;
        }

        int comparedHeights = contourCombinationInfo.getComparedHeights();
        if (comparedHeights == -1) {
            // RightContour of the left tree is higher.
            // leftContour is not modified.
            rightContour = tree.getRightContour();
            change.setConnectingElement(rightContour.getLast());
            rightContour.getLast().connect(
                    contourCombinationInfo.getConnectionNode(),
                    -shift + contourLeft - tree.getNodeLeft(), change);
        } else if (comparedHeights == 1) {
            // LeftContour of the right tree is higher.
            // rightContour is just extended.
            change.setConnectingElement(leftContour.getLast());
            leftContour.getLast().connect(
                    contourCombinationInfo.getConnectionNode(),
                    shift + tree.getNodeLeft() - contourLeft, change);
            rightContour = tree.getRightContour();
        } else if (comparedHeights == 0) {
            rightContour = tree.getRightContour();
        }
        lastChildLeft += relativeLeft.getLast();
        double rightContourExtension = lastChildLeft - contourLeft
                + tree.getNodeLeft();
        change.setRightContourExtension(rightContourExtension);
        rightContour.extendRightContour(rightContourExtension);
        width = Math.max(width, lastChildLeft + tree.getWidth());
        height = Math.max(height, tree.getHeight());
        changeStack.push(change);
    }

    /**
     * Pushes <code>tree</code> onto the stack. The tree is added on the left
     * side.
     * 
     * @param tree
     *            the tree to be pushed onto the stack.
     * @throws LockedTreeException
     *             if <code>tree</code> is locked.
     * @see Tree#isLocked()
     */
    private void pushLeft(Tree tree) {
        tree.lock();
        Change change = new Change(false);
        if (treeCount == 1) {
            growingDirection = GrowingDirection.LEFT;
        } else {
            if (growingDirection == GrowingDirection.RIGHT) {
                growingDirection = GrowingDirection.UNDIRECTED;
            }
        }
        trees.addFirst(tree);
        treeCount++;
        double minimalVerticalDistance = algorithm.getMinimalVerticalDistance();

        ContourCombinationInfo contourCombinationInfo;
        if (minimalVerticalDistance == 0) {
            contourCombinationInfo = tree.getRightContour()
                    .calculateCombination(
                            algorithm.getMinimalHorizontalDistance()
                                    + tree.getNodeLeft() - contourLeft,
                            leftContour);
        } else {
            contourCombinationInfo = new TranslatedContourNodeList(
                    minimalVerticalDistance, tree.getRightContour())
                    .calculateCombination(algorithm
                            .getMinimalHorizontalDistance()
                            + tree.getNodeLeft() - contourLeft, leftContour);
        }
        double shift = contourCombinationInfo.getShift();
        double previousContourLeft = contourLeft;

        if (shift >= 0) {
            relativeLeft.set(0, relativeLeft.getFirst() + shift);// /////
            lastChildLeft += shift;
            relativeLeft.addFirst(0.0);
            contourLeft = tree.getNodeLeft();
        } else {
            relativeLeft.set(0, relativeLeft.getFirst() + shift);
            relativeLeft.addFirst(-shift);
            contourLeft = tree.getNodeLeft() - shift;
        }

        int comparedHeights = contourCombinationInfo.getComparedHeights();
        if (comparedHeights == -1) {
            leftContour = tree.getLeftContour();
            change.setConnectingElement(rightContour.getLast());
            rightContour
                    .getLast()
                    .connect(
                            contourCombinationInfo.getConnectionNode(),
                            -(shift + previousContourLeft - tree.getNodeLeft()),
                            change);

        } else if (comparedHeights == 1) {
            leftContour = tree.getLeftContour();
            change.setConnectingElement(leftContour.getLast());
            leftContour.getLast().connect(
                    contourCombinationInfo.getConnectionNode(),
                    shift + previousContourLeft - tree.getNodeLeft(), change);
        } else if (comparedHeights == 0) {
            // rightContour = rtNode.getRightContour(); // prev
            leftContour = tree.getLeftContour();
        }
        double rightContourExtension = shift + previousContourLeft
                - tree.getNodeLeft();
        change.setRightContourExtension(rightContourExtension);
        rightContour.extendRightContour(rightContourExtension);
        if (shift > 0) {
            width = Math.max(width + shift, tree.getWidth());
        }
        height = Math.max(height, tree.getHeight());
        changeStack.push(change);
    }

    /**
     * Pops the last added tree from the stack. Unlocks that tree.
     * 
     * @throws NoSuchElementException
     *             if there is no tree to pop, i.e. the stack is empty.
     */
    public void pop() {
        if (treeCount > 1) {
            Change change = changeStack.pop();
            change.revert();
        } else if (treeCount == 1) {
            trees.getFirst().unlock();
            treeCount = 0;
            trees.clear();
            relativeLeft.clear();
            width = 0;
        } else
            throw new NoSuchElementException();
    }

    private void alignToGrid() {
        double space = algorithm.getGridSpacing();
        /*
         * long w = (Math.round((lastChildLeft + trees.getLast().getNodeLeft() -
         * contourLeft ) 2.0 / space ) - 1 ) / 2;
         */
        long w = Math.round((lastChildLeft + trees.getLast().getNodeLeft()
                + trees.getLast().getNodeWidth() - contourLeft)
                * 2.0 / space) - 1;
        if ((w & 3) != 0) {
            relativeLeft.set(treeCount / 2, relativeLeft.get(treeCount / 2)
                    + space);
            width += space;
            rightContour.extendRightContour(space);
        }
    }

    /**
     * Applies the combined tree layout composed by this
     * <code>TreeCombinationStack</code> to the tree layout of
     * <code>parent</code>.
     * 
     * @param parent
     *            the tree layout the combined tree layout composed by this
     *            <code>TreeCombinationStack</code> is applied to. It must be
     *            the parent of the tree layouts inserted into this list.
     */
    public void apply(Tree parent) {
        if (algorithm.isAlignToGrid()) {
            alignToGrid();
        }
        double verticalNodeDistance = parent.getVerticalNodeDistance();

        for (Tree child : trees) {
            child.dropContours();
            child.setTop(parent.getNodeHeight() + verticalNodeDistance);
        }
        parent.setLeftContour(leftContour);
        parent.setRightContour(rightContour);
        parent.setHeight(height);
        parent.setChildren(trees);
        parent.updateChildIndices();
        double nodeWidth = parent.getNodeWidth();
        if (nodeWidth > width) {
            relativeLeft.set(0, (nodeWidth - width) / 2.0);
            parent.setWidth(nodeWidth);
            parent.setNodeLeft(0.0);
            setLeftOfNodes();
        } else {
            parent.setWidth(width);
            setLeftOfNodes();

            parent.setNodeLeft(algorithm.getParentPlacement()
                    .calculateNodeLeft(parent));
        }

    }

    private void setLeftOfNodes() {
        if (treeCount > 2 && algorithm.isMirrorIsomorphicInvariant()) {

            Tree[] orderedTrees = new Tree[treeCount];
            trees.toArray(orderedTrees);
            if (growingDirection.equals(GrowingDirection.UNDIRECTED)) {
                while (treeCount > 0) {
                    pop();
                }
                for (Tree tree : orderedTrees) {
                    push(tree, true);
                }
            }
            double l = 0;
            double fV[] = new double[treeCount];
            Iterator<Double> relativeLeftIter = relativeLeft.iterator();
            for (int i = 0; i < treeCount; i++) {
                l += relativeLeftIter.next();
                fV[i] = l;
            }
            GrowingDirection direction = growingDirection;
            while (treeCount > 0) {
                pop();
            }
            if (direction.equals(GrowingDirection.RIGHT)) {
                for (int i = orderedTrees.length - 1; i >= 0; i--) {
                    push(orderedTrees[i], false);
                }
            } else {
                for (int i = 0; i < orderedTrees.length; i++) {
                    push(orderedTrees[i], true);
                }
            }
            l = 0;
            relativeLeftIter = relativeLeft.iterator();
            Iterator<Tree> treesIter = trees.iterator();
            for (int i = 0; i < treeCount; i++) {
                l += relativeLeftIter.next();
                treesIter.next().setLeft((l + fV[i]) / 2.0);
            }
        } else {
            double l = 0;
            Iterator<Double> relativeLeftIter = relativeLeft.iterator();
            Iterator<Tree> treesIter = trees.iterator();
            for (int i = 0; i < treeCount; i++) {
                l += relativeLeftIter.next();
                treesIter.next().setLeft(l);
            }
        }
    }

    /**
     * Returns the width of the combined tree layout.
     * 
     * @return the width of the combined tree layout.
     * @see #width
     */
    public double getWidth() {
        return width;
    }

    /**
     * Returns the tree layouts contained in this stack.
     * 
     * @return the tree layouts contained in this stack.
     * @see #trees
     */
    public LinkedList<Tree> getTrees() {
        return trees;
    }

    /**
     * Returns the width of the combined tree layout if <code>tree<code> was
     * added to this stack.
     * 
     * @param tree
     *            the tree layout whose insertion is to be tested.
     * @return the width of the combined tree layout if <code>tree</code> was
     *         added to this stack.
     * @throws LockedTreeException
     *             if <code>tree</code> is locked.
     * @see Tree#isLocked()
     */
    public double testInsertion(Tree tree, boolean right) {
        double val;
        push(tree, right);
        val = width;
        pop();
        return val;
    }

    /**
     * Returns the width of the combined tree layout if all tree layouts of
     * <code>rightStack</code> were added to this stack.
     * 
     * @param rightStack
     *            the other <code>TreeCombinationStack</code>.
     * @return the width of the combined tree layout if all tree layouts of
     *         <code>rightStack</code> were added to this stack.
     */
    public double calculateCombinedWidth(TreeCombinationStack rightStack) {
        if (treeCount == 0)
            return rightStack.width;
        else if (rightStack.treeCount == 0)
            return width;
        ContourCombinationInfo contourCombinationInfo = null;
        double minimalVerticalDistance = algorithm.getMinimalVerticalDistance();
        if (minimalVerticalDistance == 0) {
            contourCombinationInfo = rightContour.calculateCombination(
                    algorithm.getMinimalHorizontalDistance() + contourLeft
                            - rightStack.contourLeft, rightStack.leftContour);
        } else {
            contourCombinationInfo = new TranslatedContourNodeList(
                    minimalVerticalDistance, rightContour)
                    .calculateCombination(algorithm
                            .getMinimalHorizontalDistance()
                            + contourLeft - rightStack.contourLeft,
                            rightStack.leftContour);
        }
        double shift = contourCombinationInfo.getShift();
        if (shift > 0)
            return Math.max(width, shift + rightStack.width);
        else
            return rightStack.width;
    }

    /**
     * Returns the count of tree layouts in this stack.
     * 
     * @return the count of tree layouts in this stack.
     */
    public int getTreeCount() {
        return treeCount;
    }

    /**
     * Returns the top element of this stack.
     * 
     * @return the top element of this stack;<br>
     *         <code>null</code> if this stack is empty.
     */
    public Tree peek() {
        if (treeCount == 0)
            return null;
        else if (treeCount == 1)
            return trees.getFirst();
        else {
            if (changeStack.peek().right)
                return trees.getLast();
            else
                return trees.getFirst();
        }
    }

    /**
     * Writes information about this stack to a <code>DebugSession</code>.
     * 
     * @param session
     *            the <code>DebugSession</code> written to.
     * @param algorithm
     *            the Reingold-Tilford algorithm.
     */
    public void writeToDebugSession(DebugSession session,
            ReingoldTilfordAlgorithm algorithm) {

        DebugWriter writer = session.createTextWriter(true);
        writer.println("treeCount: " + treeCount);
        writer.println("width: " + width);
        writer.println("height: " + height);
        writer.println("contourLeft: " + contourLeft);
        writer.println("lastChildLeft: " + lastChildLeft);
        writer.close();
        DebugImage image = session.createImageWriter();
        image.setGrid(25);
        /*
         * image.setColor(Color.BLUE); image.fillOval(0, 0, 8, 3);
         * image.fillOval(0, 0, 3, 8);
         */
        image.setColor(Color.BLACK);
        double l = 0;
        Iterator<Double> relativeLeftIter = relativeLeft.iterator();
        Iterator<Tree> treesIter = trees.iterator();
        for (int i = 0; i < treeCount; i++) {
            l += relativeLeftIter.next();
            treesIter.next().writeToDebugImage(image, l, 0, false, algorithm);
        }
        image.setColor(Color.RED);
        leftContour.writeToDebugImage(image, contourLeft, 0);
        image.setColor(Color.GREEN);
        rightContour.writeToDebugImage(image, contourLeft, 0);
        image.fillOval(contourLeft + rightContour.getLast().getX() - 2,
                rightContour.getLast().getY() - 2, 5, 5);
        image.close();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
