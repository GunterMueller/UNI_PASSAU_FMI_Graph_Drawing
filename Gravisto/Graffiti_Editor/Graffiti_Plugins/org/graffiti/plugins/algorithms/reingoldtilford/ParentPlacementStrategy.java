// =============================================================================
//
//   ParentPlacementStrategy.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.reingoldtilford;

import java.util.LinkedList;

import org.graffiti.graph.Node;

/**
 * Defines an interface for classes that know how to calculate the horizontal
 * placement of {@link Node}s above their children.
 * <p>
 * To provide a new parent placement, create a class implementing
 * <code>ParentPlacementStrategy</code>
 * 
 * <pre>
 * public class MyParentPlacementStrategy implements ParentPlacementStrategy {
 *     public double calculateNodeLeft(Tree tree)
 *     {
 *         ...
 *         return ...;
 *     }
 * }
 * </pre>
 * 
 * and add a new member to the {@link ParentPlacement} enumeration.
 * 
 * <pre>
 * enum ParentPlacement
 * {
 *     ...
 *     CENTER_ABOVE_SUBTREE(new CenterParentAboveSubtree(), "Center above the complete width of its subtree"),
 *     MY_PARENT_PLACEMENT(new MyParentPlacementStrategy(), "My parent placement"); //&lt;-- Add this line.
 *     ...
 * }
 * </pre>
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public interface ParentPlacementStrategy {
    /**
     * Calculate the x-coordinate of the root node of <code>tree</code> in the
     * coordinate system of <code>tree</code>.
     * 
     * @param tree
     *            the subtree for whose root node the x-coordinate is to be
     *            calculated.
     * @return the x-coordinate of the root node of <code>tree</code> in the
     *         coordinate system of <code>tree</code>.
     */
    public double calculateNodeLeft(Tree tree);
}

/**
 * A <code>ParentPlacementStrategy</code> placing the parent horizontally at the
 * mid of its outer children.
 * <p>
 * <center> <img src="doc-files/ParentPlacementStrategy-1.png"> </center>
 * 
 * @see ParentPlacement#CENTER_ABOVE_OUTSIDE_CHILDREN
 */
class CenterParentAboveOutsideChildren implements ParentPlacementStrategy {

    /**
     * {@inheritDoc}
     */
    public double calculateNodeLeft(Tree tree) {
        LinkedList<Tree> children = tree.getChildren();
        Tree leftMostChild = children.getFirst();
        Tree rightMostChild = children.getLast();
        return leftMostChild.getLeft()
                + leftMostChild.getNodeLeft()
                + (rightMostChild.getLeft() + rightMostChild.getNodeLeft()
                        + rightMostChild.getNodeWidth()
                        - leftMostChild.getNodeLeft() - leftMostChild.getLeft() - tree
                        .getNodeWidth()) / 2.0;
    }
}

/**
 * A <code>ParentPlacementStrategy</code> placing the parent horizontally at the
 * mean position of its children.
 * <p>
 * <center> <img src="doc-files/ParentPlacementStrategy-2.png"> </center>
 * 
 * @see ParentPlacement#PLACE_AT_MEAN_POSITION
 */
class PlaceParentAtMeanPosition implements ParentPlacementStrategy {

    /**
     * {@inheritDoc}
     */
    public double calculateNodeLeft(Tree tree) {
        LinkedList<Tree> children = tree.getChildren();
        double meanValue = 0.0;
        for (Tree child : children) {
            meanValue += child.getLeft() + child.getNodeLeft()
                    + child.getNodeWidth() / 2.0;
        }
        return meanValue / children.size() - tree.getNodeWidth() / 2.0;
    }
}

/**
 * A <code>ParentPlacementStrategy</code> placing the parent horizontally at the
 * center of its complete subtree.
 * <p>
 * <center> <img src="doc-files/ParentPlacementStrategy-3.png"> </center>
 * 
 * @see ParentPlacement#CENTER_ABOVE_SUBTREE
 */
class CenterParentAboveSubtree implements ParentPlacementStrategy {

    /**
     * {@inheritDoc}
     */
    public double calculateNodeLeft(Tree tree) {
        return (tree.getWidth() - tree.getNodeWidth()) / 2.0;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
