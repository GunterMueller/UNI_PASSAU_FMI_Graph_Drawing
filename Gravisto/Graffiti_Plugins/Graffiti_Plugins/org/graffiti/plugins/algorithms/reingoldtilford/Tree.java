// =============================================================================
//
//   Tree.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.reingoldtilford;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.NodeShapeAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugins.tools.debug.DebugImage;
import org.graffiti.plugins.tools.debug.DebugSession;
import org.graffiti.plugins.tools.debug.DebugUtil;
import org.graffiti.plugins.tools.debug.DebugWriter;
import org.graffiti.plugins.tools.debug.DebugImage.Alignment;

/**
 * Represents a tree layout of a subtree.
 * <p>
 * <code>ReingoldTilfordAlgorithm</code> constructs a tree layout for the
 * complete tree by passing the root {@link Node} to the constructor
 * {@link Tree#Tree(Node, Edge, Tree, int, ReingoldTilfordAlgorithm, TreeVisitor)}
 * <p>
 * The constructor {@link Tree#Tree(Tree)} is used by <code>Tree</code>
 * internally to create a mirrored version of an existing <code>Tree</code>.
 * <p>
 * Some fields of <code>Tree</code> are illustrated in the following figure:
 * <center><img src="doc-files/Tree-1.png"></center>
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class Tree {
    private static final String ELLIPTIC_SHAPE = "org.graffiti.plugins.views.defaults.EllipseNodeShape";
    private static final String CIRCULAR_SHAPE = "org.graffiti.plugins.views.defaults.CircleNodeShape";

    /**
     * The node which is the root of the subtree handled by this
     * <code>Tree</code>.
     * 
     * @see #getNode()
     */
    private Node node;

    /**
     * The x-coordinate of {@link #node} within the coordinate system of this
     * tree layout.
     * 
     * @see #getNodeLeft()
     */
    private double nodeLeft;

    /**
     * The width of {@link #node}.
     * 
     * @see #getNodeWidth()
     */
    private double nodeWidth;

    /**
     * The height of {@link #node}.
     * 
     * @see #getNodeHeight()
     */
    private double nodeHeight;

    /**
     * The edge which connects the {@link #node} of this to its parent.
     * 
     * @see #getEdge()
     */
    private Edge edge;

    /**
     * <code>true</code> if {@link #node} has an elliptic shape.
     * 
     * @see #hasRoundShape()
     */
    private boolean hasRoundShape;

    /**
     * The tree layouts of the children of <code>node</code>.
     * 
     * @see #getChildren()
     */
    private LinkedList<Tree> children;

    /**
     * The tree layout of the parent of <code>node</code>. This tree is a
     * subtree of <code>parent</code>. When <code>node</code> is the root,
     * <code>parent<code> is <code>null</code>.
     * 
     * @see #getParent()
     */
    private Tree parent;

    /**
     * The left contour.
     * 
     * @see #getLeftContour()
     * @see #setRightContour(BasicContourNodeList)
     */
    private BasicContourNodeList leftContour;

    /**
     * The right contour.
     * 
     * @see #getRightContour()
     * @see #setRightContour(BasicContourNodeList)
     */
    private BasicContourNodeList rightContour;

    /**
     * The width of this tree layout.
     * 
     * @see #getWidth()
     */
    private double width;

    /**
     * The height of this tree layout.
     * 
     * @see #getHeight()
     */
    private double height;

    /**
     * x-coordinate of this tree layout relative to <code>parent</code>.
     * 
     * @see #getLeft()
     * @see #setLeft(double)
     */
    private double left;

    /**
     * x-coordinate of this tree layout relative to <code>parent</code>.
     * 
     * @see #setTop(double)
     */
    private double top;

    /**
     * Index of this Tree in the children ArrayList of its parent.
     * 
     * @see #getChildIndex()
     * @see #setChildIndex(int)
     * @see #updateChildIndices()
     */
    private int childIndex;

    /**
     * Denotes if all children are leaves or there are no children.
     * 
     * @see #isShallow()
     */
    private boolean isShallow;

    /**
     * Points to a <code>Tree</code> which equals this Tree except for mirroring
     * along a vertical axis.
     * 
     * @see #getFlipped()
     */
    private Tree flippedCounterpart;

    /**
     * Distance of node to the children of this Tree
     */
    private double verticalNodeDistance;

    /**
     * Denotes if this tree layout or its flipped counterpart is currently
     * contained in a {@link TreeCombinationStack} or
     * {@link TreeCombinationList}.
     * 
     * @see #isLocked()
     * @see #lock()
     * @see LockedTreeException
     */
    private boolean isLocked;

    /**
     * Creates a new tree layout for the subtree whoose root is
     * <code>node</code>.
     * <p>
     * At first it constructs the layouts of its subtrees by a recursive call of
     * {@code Tree#RTNode(Node, Edge, Tree, int, ReingoldTilfordAlgorithm)} for
     * each of its children. Then, the children are combined by a
     * {@link ChildOrderPolicy} obtained from
     * {@link ReingoldTilfordAlgorithm#getChildOrderPolicy()}. Finally the edges
     * which connect the children to <code>node</code> are layout out by the
     * {@link EdgeLayout} obtained from
     * {@link ReingoldTilfordAlgorithm#getEdgeLayout()}.
     * 
     * @param node
     *            node of the graph whoose subtree is layed out by this
     *            <code>Tree</code>.
     * @param edge
     *            edge which connects the subtree of <code>node</code> to its
     *            parent.
     * @param parent
     *            the <code>Tree</code> that represents the parent of node.
     * @param level
     *            the distance (count of edges) to the root.
     * @param algorithm
     *            the ReingoldTilfordAlgorithm that created the root
     *            <code>Tree</code>.
     */
    Tree(Node node, Edge edge, Tree parent, int level,
            ReingoldTilfordAlgorithm algorithm,
            TreeVisitor<Boolean> debugCondition) {
        if (level == 0) {
            algorithm.setTreeRoot(this);
        }
        isLocked = false;
        this.node = node;
        this.edge = edge;
        retrieveNodeProperties(algorithm);
        ArrayList<Double> levels = algorithm.getLevels();
        if (levels == null) {
            verticalNodeDistance = algorithm.getVerticalNodeDistance();
        } else {
            verticalNodeDistance = levels.get(level) - nodeHeight;
            if (verticalNodeDistance < algorithm.getVerticalNodeDistance()
                    && !algorithm.ignoreNodeHeightAtLevelling()) {
                double usedHeight = nodeHeight
                        + algorithm.getVerticalNodeDistance();
                double param = levels.get(level);
                verticalNodeDistance = param * Math.ceil(usedHeight / param)
                        - nodeHeight;
            }
        }
        childIndex = -1;
        this.parent = parent;

        isShallow = true;
        children = new LinkedList<Tree>();
        for (Edge outEdge : node.getAllOutEdges()) {
            Node child = outEdge.getTarget();
            Tree tree = new Tree(child, outEdge, this, level + 1, algorithm,
                    debugCondition);
            isShallow &= tree.children.size() == 0;
            children.add(tree);

        }
        if (!children.isEmpty()) {
            updateChildIndices();
            int childCount = children.size();
            if (childCount == 1) {
                (new TreeCombinationStack(children.getFirst(), algorithm))
                        .apply(this);
            } else if (childCount <= algorithm.getOptimizeUpToDegree()) {
                AllPermutationsChildOrderStrategy.getSingleton()
                        .combineChildren(this, algorithm);
            } else {
                algorithm.getChildOrderPolicy()
                        .combineChildren(this, algorithm);
            }

            height += verticalNodeDistance + nodeHeight;

            if (algorithm.isAlignToGrid()) {
                BottomCenterToTopCenterEdgeLayoutStrategy.getSingleton()
                        .calculateContours(this, algorithm);
            } else {
                algorithm.getEdgeLayout().calculateContours(this, algorithm);
            }
        } else {
            leftContour = new BasicContourNodeList();
            leftContour.addNode(0.0, nodeHeight);
            rightContour = new BasicContourNodeList();
            rightContour.addNode(nodeWidth, 0.0);
            rightContour.addNode(0.0, nodeHeight);
            width = nodeWidth;
            height = nodeHeight;
            nodeLeft = 0.0;
        }
        if (debugCondition != null && debugCondition.visit(this)) {
            DebugSession.get("").addHeader("tree completion", 2);
            writeToDebugSession(DebugSession.get(""), algorithm);
        }
    }

    /**
     * Creates a <code>Tree</code> that is the mirrored counterpart of base.
     * 
     * @param base
     *            the <code>Tree</code> that is mirrored.
     */
    private Tree(Tree base) {
        node = base.node;
        isLocked = base.isLocked;
        nodeWidth = base.nodeWidth;
        nodeHeight = base.nodeHeight;
        edge = base.edge;
        hasRoundShape = base.hasRoundShape;
        parent = base.parent;
        width = base.width;
        height = base.height;
        childIndex = base.childIndex;
        isShallow = base.isShallow;
        flippedCounterpart = base;
        verticalNodeDistance = base.verticalNodeDistance;
        nodeLeft = width - nodeWidth - base.nodeLeft;
        if (base.leftContour != null) {
            leftContour = base.rightContour
                    .getLeftContourOfFlippedRightContour(nodeWidth);
            rightContour = base.leftContour
                    .getRightContourOfFlippedLeftContour(nodeWidth);
        }
        children = new LinkedList<Tree>();
        ListIterator<Tree> iterator = base.children.listIterator(base.children
                .size());
        while (iterator.hasPrevious()) {
            Tree child = iterator.previous();
            Tree flippedChild = child.getFlipped();
            flippedChild.top = child.top;
            flippedChild.left = width - child.width - child.left;
            children.add(flippedChild);
        }
        updateChildIndices();
    }

    /**
     * Returns if <code>node</code> is the root.
     * 
     * @return <code>true</code> if <code>node</code> is the root.
     */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * Returns the children of this node. The only allowed modifications to the
     * returned ArrayList are flipping and reordering its elements. Then
     * updateChildIndices() must be called.
     * 
     * @return the children of this node.
     */
    public LinkedList<Tree> getChildren() {
        return children;
    }

    /**
     * Sets the children of this node.
     * 
     * @param children
     *            the children.
     */
    void setChildren(LinkedList<Tree> children) {
        this.children = children;
    }

    /**
     * Returns the node that is the root of the tree this layout represents.
     * 
     * @return the node that is the root of the tree this layout represents.
     * @see #node
     */
    public Node getNode() {
        return node;
    }

    /**
     * Returns the edge that connects <code>node</code> with its parent.
     * 
     * @return the edge that connects {@link #node} with its parent.
     */
    public Edge getEdge() {
        return edge;
    }

    /**
     * Sets the {@link #childIndex} field of each child according to its
     * position in the <code>LinkedList</code> {@link #children}.
     * 
     * @see #setChildIndex(int)
     */
    public void updateChildIndices() {
        Iterator<Tree> iter = children.iterator();
        for (int i = 0; i < children.size(); i++) {
            iter.next().setChildIndex(i);
        }
    }

    /**
     * Sets the position of this <code>Tree</code> in the
     * <code>LinkedList</code> {@link #children} of the parent of this tree
     * layout.
     * 
     * @param index
     *            position of this in the <code>LinkedList</code>.
     * @see #childIndex
     * @see #getChildIndex()
     */
    private void setChildIndex(int index) {
        childIndex = index;
    }

    /**
     * Returns the position of this <code>Tree</code> in the
     * <code>LinkedList</code> {@link #children} of the parent of this tree
     * layout.
     * 
     * @return position of this in the <code>LinkedList</code> or
     *         <code>-1</code> if the index has not been set yet.
     * @see #childIndex
     * @see #setChildIndex(int)
     */
    public int getChildIndex() {
        return childIndex;
    }

    /**
     * Returns the parent of this tree layout.
     * 
     * @return the parent of this tree layout or null if <code>node</code> is
     *         the root.
     * @see #parent
     */
    public Tree getParent() {
        return parent;
    }

    /**
     * Fetches the dimensions and shape for {@link #node}.
     * 
     * @param algorithm
     *            the Reingold-Tilford algorithm.
     * @see Orientation
     */
    private void retrieveNodeProperties(ReingoldTilfordAlgorithm algorithm) {
        Point2D.Double dimension = algorithm.getNodeDimension(node);
        nodeWidth = dimension.getX();
        nodeHeight = dimension.getY();
        NodeShapeAttribute nodeShape = (NodeShapeAttribute) node
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR + GraphicAttributeConstants.SHAPE);
        String shapeName = nodeShape.getValue().toString();
        hasRoundShape = shapeName.equals(CIRCULAR_SHAPE)
                || shapeName.equals(ELLIPTIC_SHAPE);
    }

    /**
     * Sets the left and right contour of this and its flipped counterpart to
     * <code>null</code>. If the tree or its flipped counterpart previously has
     * been locked, the lock is released.
     * 
     * @see #leftContour
     * @see #rightContour
     * @see #flippedCounterpart
     * @see #isLocked
     */
    void dropContours() {
        leftContour = null;
        rightContour = null;
        isLocked = false;
        if (flippedCounterpart != null) {
            flippedCounterpart.leftContour = null;
            flippedCounterpart.rightContour = null;
            flippedCounterpart.isLocked = false;
        }
    }

    /**
     * Returns the left contour. Must only be called by methods implementing
     * {@link EdgeLayoutStrategy#calculateContours(Tree, ReingoldTilfordAlgorithm) }
     * .
     * 
     * @return the left contour.
     * @see #leftContour
     * @see #setLeftContour(BasicContourNodeList)
     */
    public BasicContourNodeList getLeftContour() {
        return leftContour;
    }

    /**
     * Returns the right contour. Must only be called by methods implementing
     * {@link EdgeLayoutStrategy#calculateContours(Tree, ReingoldTilfordAlgorithm) }
     * .
     * 
     * @return the right contour.
     * @see #rightContour
     * @see #setRightContour(BasicContourNodeList)
     */
    public BasicContourNodeList getRightContour() {
        return rightContour;
    }

    /**
     * Sets the left contour. Must only be called by methods implementing
     * {@link EdgeLayoutStrategy#calculateContours(Tree, ReingoldTilfordAlgorithm) }
     * .
     * 
     * @param leftContour
     *            the new left contour.
     * @see #leftContour
     * @see #getLeftContour()
     */
    public void setLeftContour(BasicContourNodeList leftContour) {
        this.leftContour = leftContour;
    }

    /**
     * Sets the right contour. Must only be called by methods implementing
     * {@link EdgeLayoutStrategy#calculateContours(Tree, ReingoldTilfordAlgorithm) }
     * .
     * 
     * @param rightContour
     *            the new right contour.
     * @see #rightContour
     * @see #getRightContour()
     */
    public void setRightContour(BasicContourNodeList rightContour) {
        this.rightContour = rightContour;
    }

    /**
     * Returns the width of <code>node</code>.
     * 
     * @return the width of {@link #node}.
     * @see #nodeWidth
     */
    public double getNodeWidth() {
        return nodeWidth;
    }

    /**
     * Returns the height of <code>node</code>.
     * 
     * @return the height of {@link #node}.
     */
    public double getNodeHeight() {
        return nodeHeight;
    }

    /**
     * Sets the x-coordinate of <code>node</code> within the coordinate system
     * of this tree layout.
     * 
     * @param nodeLeft
     *            the x-coordinate of {@link #node} within the coordinate system
     *            of this tree layout.
     */
    void setNodeLeft(double nodeLeft) {
        this.nodeLeft = nodeLeft;
    }

    /**
     * Returns the x-coordinate of <code>node</code> within the coordinate
     * system of this tree layout.
     * 
     * @return the x-coordinate of {@link #node} within the coordinate system of
     *         this tree layout.
     */
    public double getNodeLeft() {
        return nodeLeft;
    }

    /**
     * Sets the x-coordinate of this tree layout relative to <code>parent</code>
     * .
     * 
     * @param left
     *            x-coordinate of this tree layout relative to {@link #parent}.
     * @see #left
     * @see #getLeft()
     */
    void setLeft(double left) {
        this.left = left;
    }

    /**
     * Returns the x-coordinate of this tree layout relative to
     * <code>parent</code>.
     * 
     * @return x-coordinate of this tree layout relative to {@link #parent}.
     * @see #left
     * @see #setLeft(double)
     */
    public double getLeft() {
        return left;
    }

    /**
     * Sets the y-coordinate of this tree layout relative to <code>parent</code>
     * .
     * 
     * @param top
     *            y-coordinate of this tree layout relative to {@link #parent}.
     * @see #top
     */
    void setTop(double top) {
        this.top = top;
    }

    /**
     * Sets the width of this tree layout. Must only be called by
     * {@link TreeCombinationStack}.
     * 
     * @param width
     *            the width of this tree layout.
     * @see #width
     * @see #getWidth()
     */
    void setWidth(double width) {
        this.width = width;
    }

    /**
     * Returns the width of this tree layout.
     * 
     * @return the width of this tree layout.
     * @see #width
     * @see #setWidth(double)
     */
    public double getWidth() {
        return width;
    }

    /**
     * Sets the height of this tree layout. Must only be called by
     * {@link TreeCombinationStack}
     * 
     * @param height
     *            the height of this tree layout.
     * @see #height
     * @see #getHeight()
     */
    void setHeight(double height) {
        this.height = height;
    }

    /**
     * Returns the height of this tree layout.
     * 
     * @return the heught of this tree layout.
     * @see #height
     * @see #setHeight(double)
     */
    public double getHeight() {
        return height;
    }

    public void draw(ReingoldTilfordAlgorithm algorithm, double xOrigin,
            double yOrigin) {
        for (Tree child : children) {
            child.draw(algorithm, xOrigin + child.left, yOrigin + child.top);
        }
        algorithm.setNodePosition(node, xOrigin + nodeLeft + nodeWidth / 2.0,
                yOrigin + nodeHeight / 2.0);
        algorithm.getEdgeLayout().layEdges(this, xOrigin, yOrigin, algorithm);
    }

    public boolean hasRoundShape() {
        return hasRoundShape;
    }

    /**
     * @return <code>true</code> if all children are leaves or there are no
     *         children;<br>
     *         <code>false</code> otherwise.
     */
    public boolean isShallow() {
        return isShallow;
    }

    /**
     * Returns the flipped counterpart of this tree.
     * 
     * @return the flipped counterpart of this tree.
     * @throws LockedTreeException
     *             if <code>tree</code> is locked.
     * @see Tree#isLocked()
     */
    public Tree getFlipped() {
        if (isLocked)
            throw new LockedTreeException();
        if (flippedCounterpart == null) {
            flippedCounterpart = new Tree(this);
        }
        return flippedCounterpart;
    }

    public double getVerticalNodeDistance() {
        return verticalNodeDistance;
    }

    public Tree find(TreeVisitor<Boolean> visitor) {
        if (visitor.visit(this))
            return this;
        for (Tree child : children) {
            Tree tree = child.find(visitor);
            if (tree != null)
                return tree;
        }
        return null;
    }

    public void writeToDebugSession(DebugSession session,
            ReingoldTilfordAlgorithm algorithm) {
        DebugWriter writer = session.createTextWriter(true);
        writer.println("width: " + width);
        writer.println("height: " + height);
        writer.println("nodeLeft: " + nodeLeft);
        writer.println("nodeWidth: " + nodeWidth);
        writer.println("nodeHeight: " + nodeHeight);
        writer.close();
        DebugImage image = session.createImageWriter();
        image.setGrid(25);
        writeToDebugImage(image, 0, 0, true, algorithm);
        image.close();
    }

    public void writeToDebugImage(DebugImage image, double xOrigin,
            double yOrigin, boolean showContour,
            ReingoldTilfordAlgorithm algorithm) {
        image.drawRect(xOrigin + nodeLeft, yOrigin, nodeWidth, nodeHeight);
        String label = DebugUtil.getDebugLabel(node);
        if (!label.equals("")) {
            image.drawText(label, xOrigin + nodeLeft + nodeWidth / 2.0, yOrigin
                    + nodeHeight - 2.0, Alignment.CENTER);
        }
        for (Tree child : children) {
            image.drawLine(xOrigin + nodeLeft + nodeWidth / 2.0, yOrigin
                    + nodeHeight, xOrigin + child.left + child.nodeLeft
                    + child.nodeWidth / 2.0, yOrigin + child.top);
            child.writeToDebugImage(image, xOrigin + child.left, yOrigin
                    + child.top, false, algorithm);
        }
        if (showContour && leftContour != null) {
            image.setColor(Color.RED);
            leftContour.writeToDebugImage(image, xOrigin + nodeLeft, yOrigin);
            image.fillOval(xOrigin + nodeLeft + leftContour.getLast().getX()
                    - 2, yOrigin + leftContour.getLast().getY() - 2, 5, 5);
        }
        if (showContour && rightContour != null) {
            image.setColor(Color.GREEN);
            rightContour.writeToDebugImage(image, xOrigin + nodeLeft, yOrigin);
            image.fillOval(xOrigin + nodeLeft + rightContour.getLast().getX()
                    - 2, yOrigin + rightContour.getLast().getY() - 2, 5, 5);
            double minimalVerticalDistance = algorithm
                    .getMinimalVerticalDistance();
            if (minimalVerticalDistance > 0) {
                image.setColor(Color.CYAN);
                ContourNodeList tl = new TranslatedContourNodeList(algorithm
                        .getMinimalVerticalDistance(), rightContour);
                tl.writeToDebugImage(image, xOrigin + nodeLeft, yOrigin);
            }
        }
        image.setColor(Color.BLACK);
    }

    /**
     * Tries to lock the tree. Must only be called by
     * {@link TreeCombinationStack}. See {@link #isLocked}.
     * 
     * @throws LockedTreeException
     *             when the tree is currently locked.
     */
    void lock() {
        if (isLocked)
            throw new LockedTreeException();
        isLocked = true;
        if (flippedCounterpart != null) {
            flippedCounterpart.isLocked = true;
        }
    }

    /**
     * Unlocks the tree. Must only be called by {@link TreeCombinationStack}.
     */
    void unlock() {
        isLocked = false;
        if (flippedCounterpart != null) {
            flippedCounterpart.isLocked = false;
        }
    }

    /**
     * Returns if this tree layout or its flipped counterpart is currently
     * contained in a {@link TreeCombinationStack} or
     * {@link TreeCombinationList}.
     * 
     * @return if this tree layout or its flipped counterpart is currently
     *         contained in a <code>TreeCombinationStack</code> or
     *         <code>TreeCombinationList</code>.
     */
    boolean isLocked() {
        return isLocked;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
