package org.graffiti.plugins.algorithms.phyloTrees.utility;

import java.awt.Color;

import org.graffiti.graphics.GraphicAttributeConstants;

public class PhyloTreeConstants {

    // default values

    /**
     * The default Color of an Edge in a tree.
     */
    public static final Color DEFAULT_EDGE_COLOR = Color.BLACK;

    /**
     * The default thickness of an edge in a tree.
     */
    public static final double DEFAULT_EDGE_THICKNESS = 3;

    // paths

    /**
     * The path to the weight attribute of an edge.
     */
    public static final String PATH_WEIGHT = "weight";

    /**
     * The path to the number of an edge.
     */
    public static final String PATH_EDGE_NUMBER = "number";

    // Graph positioning constants

    public static final double LEFT_PANEL_BUFFER = 20;

    public static final double RIGHT_PANEL_BUFFER = 10;

    public static final double TOP_PANEL_BUFFER = 10;

    /**
     * The y-coordinates of the upper boundary for drawing.
     */
    public static final double VERTICAL_STARTING_POSITION = 0;

    /**
     * The minimal vertical distance of two Nodes. Is being used to set the
     * distance between leaf nodes in Phylogram-like graphs.
     */
    public static final double MIN_VERTICAL_NODE_DISTANCE = 15;

    /**
     * The minimal distance of two leafs in a circular drawing.
     */
    public static final double MIN_LEAF_DISTANCE = 8;

    /**
     * Angle, that is left free between the last and the first node when doing
     * circular drawings.
     */
    public static final double CIRCULAR_SPACING = (Math.PI / 180) * 5;

    /**
     * The minimal edge weight. Must be larger than 0.
     */
    public static final double MIN_EDGE_WEIGHT = 1e-8;

    /**
     * The factor by which the minimum edge weight of a tree is calculated from
     * the average edge weight of the tree.
     * 
     * Must be larger than 0.
     */
    public static final double MIN_EDGE_WEIGHT_FACTOR = 0.2;

    /**
     * The minimal length of edges in screen-pixel
     */
    public static final double MIN_EDGE_SIZE = 5;

    // Root Node Attributes

    /**
     * The diameter of the root node.
     */
    public static final double ROOT_NODE_DIAMETER = 10d;

    /**
     * The color of the root node.
     */
    public static final Color ROOT_NODE_COLOR = Color.BLACK;

    /**
     * The Shape of the root node.
     */
    public static final String ROOT_NODE_SHAPE = GraphicAttributeConstants.CIRCLE_CLASSNAME;

    // Edge Shape Parameters

    /**
     * The ratio of the horizontal distance from source node to the bend of a
     * shape in the eurogram.
     */
    public static final double SHAPE_EUROGRAM_RATIO = 0.2;

    // Node Placements

    /**
     * Nodes are to be placed in the vertical center of it's children.
     */
    public static final String NODES_CENTERED_INTERMEDIATE = "Centered amongst its children";

    /**
     * Nodes are to be placed in the vertical center of it's subtrees.
     */
    public static final String NODES_CENTERED_SUBTREE = "Centered amongst its subtree";

    /**
     * Nodes are to be placed vertically closer to those nodes, which are
     * horizontally closer.
     */
    public static final String NODES_WEIGHTED = "Weighted";

    /**
     * String array, containing all possible Node placements.
     */
    public static final String[] NODE_PLACEMENTS = {
            NODES_CENTERED_INTERMEDIATE, NODES_CENTERED_SUBTREE, NODES_WEIGHTED };
}
