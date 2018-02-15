// =============================================================================
//
//   RoutingStyles.java
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
 * The layout of the edges. Each layout consists of a name and a
 * {@link EdgeLayoutStrategy} that does the actual calculations.
 * <p>
 * To provide a new edge layout, create a class implementing
 * <code>EdgeLayoutStrategy</code>
 * 
 * <pre>
 * public class MyEdgeLayoutStrategy implements EdgeLayoutStrategy {
 *     public void calculateContours(Tree tree, ReingoldTilfordAlgorithm algorithm)
 *     {
 *         BasicContourNodeList oldLeftContour = tree.getLeftContour();
 *         BasicContourNodeList oldRightContour = tree.getRightContour();
 *         BasicContourNodeList newLeftContour = new BasicContourNodeList();
 *         BasicContourNodeList newRightContour = new BasicContourNodeList();
 *         
 *         // Build the contour lines enclosing the root node...
 *         newLeftContour.addNode(...);
 *         ...
 *         
 *         // Include the contour lines of the combined subtrees.
 *         newLeftContour.getLast().connectToLeftContour(oldLeftContour, ...);
 *         newRightContour.getLast().connectToRightContour(oldRightContour, ...);
 *         
 *         // Set the new contours.
 *         tree.setLeftContour(newLeftContour);
 *         tree.setRightContour(newRightContour);
 *     }
 * 
 *     public void layEdges(Tree tree, double xOrigin, double yOrigin, ReingoldTilfordAlgorithm algorithm)
 *     {
 *         Node node = tree.getNode();
 *         for (Tree child : children)
 *         {
 *             Edge edge = child.getEdge();
 *             
 *             // Add ports and bends.
 *             ...
 *         }
 *     }
 * }
 * </pre>
 * 
 * and add a new member to this enumeration.<br>
 * 
 * <pre>
 * enum EdgeLayout
 * {
 *     ...
 *     BOTTOM_TO_TOP(new CenterToCenterEdgeLayoutStrategy(true, true), "Bottom to top"),
 *     MY_EDGE_LAYOUT(new MyEdgeLayoutStrategy(), "My edge layout"); //&lt;-- Add this line
 *     ...
 * }
 * </pre>
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see EdgeLayoutStrategy
 * @see Tree
 */
enum EdgeLayout {
    /**
     * Edge leaves parent at the center of the bottom side and enters the child
     * at the center at the top side.
     * <p>
     * <center> <img
     * src="doc-files/BottomCenterToTopCenterEdgeLayoutStrategy-1.png">
     * </center>
     * 
     * @see BottomCenterToTopCenterEdgeLayoutStrategy
     */
    BOTTOM_CENTER_TO_TOP_CENTER(BottomCenterToTopCenterEdgeLayoutStrategy
            .getSingleton(), "Bottom center to top center"),

    /**
     * Edges resemble a bus system.
     * <p>
     * <center><img src="doc-files/BusEdgeLayoutStrategy-1.png"></center>
     */
    BUS(new BusEdgeLayoutStrategy(), "Bus layout"),

    /**
     * Straight line between parent and child; may intersect the siblings of the
     * child.
     * <p>
     * <center> <img src="doc-files/CenterToCenterEdgeLayoutStrategy-1.png">
     * </center>
     */
    CENTER_TO_CENTER(new CenterToCenterEdgeLayoutStrategy(false, false),
            "Center to center (edges may intersect siblings)"),

    /**
     * Straight line between parent and child; must enter the child at its top
     * side.
     * <p>
     * <center> <img src="doc-files/CenterToCenterEdgeLayoutStrategy-2.png">
     * </center>
     */
    CENTER_TO_TOP(new CenterToCenterEdgeLayoutStrategy(true, false),
            "Center to top"),

    /**
     * Straight line between parent and child; must leave the parent at its
     * bottom side and enter the child at its top side.
     * <p>
     * <center> <img src="doc-files/CenterToCenterEdgeLayoutStrategy-3.png">
     * </center>
     */
    BOTTOM_TO_TOP(new CenterToCenterEdgeLayoutStrategy(true, true),
            "Bottom to top");

    /**
     * The names of the edge layouts.
     * 
     * @see #getNames()
     */
    private static ArrayList<String> names;

    /**
     * The <code>EdgeLayoutStrategy</code> used by this enumeration member.
     */
    private EdgeLayoutStrategy strategy;

    /**
     * Returns the names of the edge layouts. To get the name of a specific edge
     * layout such as <code>BOTTOM_TO_TOP</code>, you can write<br> {@code
     * EdgeLayout.getNames().get( EdgeLayout.BOTTOM_TO_TOP.ordinal())}
     * 
     * @return the names of the policies.
     * @see #names
     */
    public static List<String> getNames() {
        return Collections.unmodifiableList(names);
    }

    /**
     * Creates a new <code>EdgeLayout</code> enumeration member.
     * 
     * @param strategy
     *            the strategy the new edge layout employs.
     * @param name
     *            the name of the new edge layout.
     */
    private EdgeLayout(EdgeLayoutStrategy strategy, String name) {
        this.strategy = strategy;
        addName(name);
    }

    /**
     * Is called by the constructor of this edge layout to add its name to the
     * <code>names</code> list.
     * 
     * @param name
     *            the name of the new edge layout.
     * @see #getNames()
     */
    private void addName(String name) {
        if (names == null) {
            names = new ArrayList<String>();
        }
        names.add(name);
    }

    /**
     * Creates and sets the contours of <code>tree</code> using the strategy of
     * this enumeration member. <br>
     * <b>Preconditions:</b><br>
     * <code>tree</code> must contain at least 1 child.<br>
     * The children of <code>tree</code> have been combined.
     * 
     * @param tree
     *            the tree whose contours are set.
     * @param algorithm
     *            the Reingold-Tilford algorithm.
     * @see EdgeLayoutStrategy#calculateContours(Tree, ReingoldTilfordAlgorithm)
     */
    public void calculateContours(Tree tree, ReingoldTilfordAlgorithm algorithm) {
        strategy.calculateContours(tree, algorithm);
    }

    /**
     * Sets the ports and bends for the edges connecting the root node of
     * <code>tree</code> with its subtrees using the strategy of this
     * enumeration member.
     * 
     * @param tree
     *            the tree layout whose edges are layed out.
     * @param xOrigin
     *            the absolute x-coordinate of <code>tree</code> within the
     *            coordinate system of Gravisto.
     * @param yOrigin
     *            the absolute x-coordinate of <code>tree</code> within the
     *            coordinate system of Gravisto.
     * @param algorithm
     *            the Reingold-Tilford algorithm.
     * @see EdgeLayoutStrategy#layEdges(Tree, double, double,
     *      ReingoldTilfordAlgorithm)
     */
    public void layEdges(Tree tree, double xOrigin, double yOrigin,
            ReingoldTilfordAlgorithm algorithm) {
        strategy.layEdges(tree, xOrigin, yOrigin, algorithm);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
