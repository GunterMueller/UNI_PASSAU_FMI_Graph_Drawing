// =============================================================================
//
//   RoutingStrategy.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.reingoldtilford;

/**
 * Defines an interface for classes that know how to lay out edges connecting a
 * node with its subtrees.
 * <p>
 * To provide a new edge layout, create a class implementing
 * <code>EdgeLayoutStrategy</code>.
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
 * and add a new member to the <code>EdgeLayout</code> enumeration.<br>
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
 * @see EdgeLayout
 * @see Tree
 */
public interface EdgeLayoutStrategy {

    /**
     * Creates and sets the contours of <code>tree</code>. <br>
     * <b>Preconditions:</b><br>
     * <code>tree</code> must contain at least 1 child.<br>
     * The children of <code>tree</code> have been combined.
     * 
     * @param tree
     *            the tree whose contours are set.
     * @param algorithm
     *            the Reingold-Tilford algorithm.
     * @see EdgeLayout#calculateContours(Tree, ReingoldTilfordAlgorithm)
     */
    public void calculateContours(Tree tree, ReingoldTilfordAlgorithm algorithm);

    /**
     * Sets the ports and bends for the edges connecting the root node of
     * <code>tree</code> with its subtrees. Implementing methods must not
     * directly query or set coordinates but rather use the methods
     * {@link ReingoldTilfordAlgorithm#createPort(String, double, double)} and
     * {@link ReingoldTilfordAlgorithm#createCoordinateAttribute(String, double, double)}
     * .
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
     * @see Orientation
     * @see EdgeLayout#layEdges(Tree, double, double, ReingoldTilfordAlgorithm)
     */
    public void layEdges(Tree tree, double xOrigin, double yOrigin,
            ReingoldTilfordAlgorithm algorithm);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
