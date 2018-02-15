// =============================================================================
//
//   ParentPlacement.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.reingoldtilford;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.graffiti.graph.Node;

/**
 * The horizontal position where a parent node is placed above its children.
 * Each parent placement consists of a name and a
 * {@link ParentPlacementStrategy} that does the actual calculations.
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
 * and add a new member to this enumeration.
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
 * @see ParentPlacementStrategy
 * @see Tree
 * @see Node
 */
enum ParentPlacement {
    /**
     * Places the parent horizontally at the mid of its outer children.
     * <p>
     * <center> <img src="doc-files/ParentPlacementStrategy-1.png"> </center>
     * 
     * @see CenterParentAboveOutsideChildren
     */
    CENTER_ABOVE_OUTSIDE_CHILDREN(new CenterParentAboveOutsideChildren(),
            "Center above outside children"),

    /**
     * Places the parent horizontally at the mean position of its children.
     * <p>
     * <center> <img src="doc-files/ParentPlacementStrategy-2.png"> </center>
     * 
     * @see PlaceParentAtMeanPosition
     */
    PLACE_AT_MEAN_POSITION(new PlaceParentAtMeanPosition(),
            "Place at mean position"),

    /**
     * Places the parent horizontally at the center of its complete subtree.
     * <p>
     * <center> <img src="doc-files/ParentPlacementStrategy-3.png"> </center>
     * 
     * @see CenterParentAboveSubtree
     */
    CENTER_ABOVE_SUBTREE(new CenterParentAboveSubtree(),
            "Center above the complete width of its subtree");

    /**
     * The names of the parent placements.
     * 
     * @see #getNames()
     */
    private static ArrayList<String> names;

    /**
     * The <code>ParentPlacementStrategy</code> used by this enumeration member.
     */
    private ParentPlacementStrategy strategy;

    /**
     * Returns the names of the parent placements. To get the name of a specific
     * parent placement such as <code>CENTER_ABOVE_SUBTREE</code>, you can write <br>
     * {@code ParentPlacement.getNames().get(
     * ParentPlacement.CENTER_ABOVE_SUBTREE.ordinal())}
     * 
     * @return the names of the parent placements.
     * @see #names
     */
    public static List<String> getNames() {
        return Collections.unmodifiableList(names);
    }

    /**
     * Calculate the x-coordinate of the root node of <code>tree</code> in the
     * coordinate system of <code>tree</code> using the strategy employed by
     * this enumeration member.
     * 
     * @param tree
     *            the subtree for whose root node the x-coordinate is to be
     *            calculated.
     * @return the x-coordinate of the root node of <code>tree</code> in the
     *         coordinate system of <code>tree</code>.
     */
    public double calculateNodeLeft(Tree tree) {
        return strategy.calculateNodeLeft(tree);
    }

    /**
     * Creates a new <code>ParentPlacement</code> enumeration member.
     * 
     * @param strategy
     *            the strategy the new parent plaxement employs.
     * @param name
     *            the name of the new parent placement.
     */
    private ParentPlacement(ParentPlacementStrategy strategy, String name) {
        addName(name);
        this.strategy = strategy;
    }

    /**
     * Is called by the constructor of this parent placement to add its name to
     * the <code>names</code> list.
     * 
     * @param name
     *            the name of the new parent placement.
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
