// =============================================================================
//
//   Orientation.java
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
 * The orientation of a tree layout.
 * <p>
 * In all classes except <code>ReingoldTilfordAlgorithm</code> and
 * <code>Orientation</code>, the tree is considered as growing from top to
 * bottom, i.e. the root has the y-coordinate 0 and children have greater
 * y-coordinates than their parents. In order to achieve different orientations,
 * the coordinates and dimensions of the nodes, the edges and the ports are
 * transformed by the respective descendants of <code>Orientation</code>.
 * Therefore <code>Tree</code> and descendants of
 * <code>ChildOrderStrategy</code>, <code>EdgeLayoutStrategy</code> and
 * <code>Levelling</code> must not directly query or set coordinates or
 * dimensions but rather use the methods
 * {@link ReingoldTilfordAlgorithm#getNodePosition(Node)},
 * {@link ReingoldTilfordAlgorithm#getNodeDimension(Node)},
 * {@link ReingoldTilfordAlgorithm#setNodePosition(Node, double, double)},
 * {@link ReingoldTilfordAlgorithm#createPort(String, double, double)} and
 * {@link ReingoldTilfordAlgorithm#createCoordinateAttribute(String, double, double)}.
 * <p>
 * Each orientation consists of a name and an {@link OrientationStrategy} that
 * does the actual calculations.
 * <p>
 * To provide a new orientation, create a class extending
 * <code>OrientationStrategy</code> and add a new member to this enumeration.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see OrientationStrategy
 */
public enum Orientation {
    /**
     * The root is at the top and the tree grows downwards.
     * 
     * @see TopToBottomOrientation
     */
    TOP_TO_BOTTOM(new TopToBottomOrientation(), "Top to bottom"),

    /**
     * The root is at the left and the tree grows rightwards.
     * 
     * @see LeftToRightOrientation
     */
    LEFT_TO_RIGHT(new LeftToRightOrientation(), "Left to right"),

    /**
     * The root is at the bottom and the tree grows upwards.
     * 
     * @see BottomToTopOrientation
     */
    BOTTOM_TO_TOP(new BottomToTopOrientation(), "Bottom to top"),

    /**
     * The root is at the right and the tree grows leftwards.
     * 
     * @see RightToLeftOrientation
     */
    RIGHT_TO_LEFT(new RightToLeftOrientation(), "Right to left");

    /**
     * The names of the orientations.
     * 
     * @see #getNames()
     */
    private static ArrayList<String> names;

    /**
     * The <code>OrientationStrategy</code> used by this enumeration member.
     */
    private OrientationStrategy strategy;

    /**
     * Returns the names of the orientations. To get the name of a specific
     * orientation, such as <code>TOP_TO_BOTTOM</code>, you can write<br> {@code
     * Orientation.getNames().get( Orientation.TOP_TO_BOTTOM.ordinal())}
     * 
     * @return the names of the orientations.
     * @see #names
     */
    public static List<String> getNames() {
        return Collections.unmodifiableList(names);
    }

    /**
     * Creates a new instance of the subclass of
     * <code>OrientationStrategy</code> employed by this enumeration member.
     * 
     * @return a new instance of the subclass of
     *         <code>OrientationStrategy</code> employed by this enumeration
     *         member.
     */
    public OrientationStrategy createOrientationStrategy() {
        OrientationStrategy newStrategy = null;
        try {
            newStrategy = strategy.getClass().newInstance();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return newStrategy;
    }

    /**
     * Creates a new <code>Orientation</code> enumeration member.
     * 
     * @param strategy
     *            the strategy the new orientation employs.
     * @param name
     *            the name of the new orientation.
     */
    private Orientation(OrientationStrategy strategy, String name) {
        addName(name);
        this.strategy = strategy;
    }

    /**
     * Is called by the constructor of this orientation to add its name to the
     * <code>names</code> list.
     * 
     * @param name
     *            the name of the new orientation.
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
