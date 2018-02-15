// =============================================================================
//
//   VisualChildOrderStrategy.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.reingoldtilford;

import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

/**
 * <code>ChildOrderStrategy</code> which conserves the order of the subtrees as
 * it has previously appeared in the editor.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see ChildOrderPolicy#VISUAL_ORDER
 */
class VisualChildOrderStrategy implements ChildOrderStrategy {
    /**
     * <code>ChildrenComparator</code> is used to sort the children by their
     * (previously calculated) position relative to their parent in polar
     * coordinates.
     * 
     * @author Andreas Glei&szlig;ner
     * @version $Revision$ $Date$
     */
    private class ChildrenComparator implements Comparator<Tree> {
        /**
         * The angles of the children relative to their parent in radians.
         */
        private double angles[];

        /**
         * Creates a new <code>ChildrenComparator</code>.
         * 
         * @param angles
         *            the angles of the children relative to their parent in
         *            radians. If the parent is the root, the up direction is
         *            assigned the angle 0. Else the parent's parent is
         *            considered to have an angle of 0.
         */
        private ChildrenComparator(double angles[]) {
            this.angles = angles;
        }

        /**
         * {@inheritDoc}
         */
        public int compare(Tree tree1, Tree tree2) {
            int i1 = tree1.getChildIndex();
            int i2 = tree2.getChildIndex();
            if (angles[i1] < angles[i2])
                return -1;
            else if (angles[i1] > angles[i2])
                return 1;
            else {
                int node1Index = tree1.getChildIndex();
                int node2Index = tree2.getChildIndex();
                if (node1Index < node2Index)
                    return -1;
                else if (node1Index > node2Index)
                    return 1;
                else
                    return 0;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void combineChildren(Tree node, ReingoldTilfordAlgorithm algorithm) {
        LinkedList<Tree> children = node.getChildren();
        if (children.isEmpty())
            return;
        Tree parent = node.getParent();
        double parentsAngle = parent == null ? Math.PI / 2.0 : calculateAngle(
                node, parent, 0.0, algorithm);
        double[] angles = new double[children.size()];
        Iterator<Tree> iter = children.iterator();
        for (int i = 0; i < children.size(); i++) {
            Tree child = iter.next();
            angles[i] = calculateAngle(node, child, parentsAngle, algorithm);
        }
        TreeSet<Tree> treeSet = new TreeSet<Tree>(
                new ChildrenComparator(angles));
        treeSet.addAll(node.getChildren());
        iter = treeSet.iterator();
        TreeCombinationStack combinationStack = new TreeCombinationStack(iter
                .next(), algorithm);
        while (iter.hasNext()) {
            combinationStack.push(iter.next(), true);
        }

        combinationStack.apply(node);
    }

    /**
     * Returns the angle of the polar coordinate of <code>node</code> relative
     * to <code>center</code>. The direction specified by <code>nullAngle</code>
     * is assigned an angle of 0.
     * 
     * @param center
     *            the center of the polar coordinates.
     * @param node
     *            the tree whoose root's polar position is returned.
     * @param nullAngle
     *            specifies the direction that will be assigned an angle of 0.
     *            <code>nullAngle</code> is given in radians where the right
     *            direction is considered to have an angle of 0.
     * @param algorithm
     *            the Reingold-Tilford algorithm.
     * @return the angle in radians of the polar coordinates of the root node of
     *         the <code>node</code> tree layout.
     */
    private double calculateAngle(Tree center, Tree node, double nullAngle,
            ReingoldTilfordAlgorithm algorithm) {
        Point2D.Double position = algorithm.getNodePosition(center.getNode());
        double x1 = position.getX();
        double y1 = position.getY();
        position = algorithm.getNodePosition(node.getNode());

        double x2 = position.getX();
        double y2 = position.getY();
        if (x1 == x2 && y1 == y2)
            return 0;
        double angle = Math.atan2(y1 - y2, x2 - x1);
        if (angle < 0) {
            angle += 2.0 * Math.PI;
        }
        angle -= nullAngle;
        if (angle < 0) {
            angle += 2.0 * Math.PI;
        }
        return angle;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
