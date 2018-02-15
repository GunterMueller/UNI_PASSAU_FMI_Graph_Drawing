// =============================================================================
//
//   Leveling.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.reingoldtilford;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.graffiti.graph.Node;

/**
 * <code>LevellingPolicy</code> determines the y-coordinate at which each
 * <code>Node</code> is placed. The y-coordinate may be calculated individually
 * for each {@link Node} or be restricted to certain values (levels). The level
 * to which each nodes belongs depends on its depth (number of edges of the
 * simple path connecting the node and the root) in the tree. Each levelling
 * policy consists of a name and a {@link LevellingStrategy} that does the
 * actual calculations.
 * <p>
 * To provide a new levelling policy, create a class extending
 * <code>LevellingStrategy</code> and add a new member to this enumeration.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see ReingoldTilfordAlgorithm
 */
public enum LevellingPolicy {
    /**
     * Individual y-coordinates for each <code>Node</code>. There are no levels.
     * 
     * @see LocalLevelsStrategy
     */
    LOCAL(new LocalLevelsStrategy(), "Individual levels"),

    /**
     * All levels are of equal distance, which is determined by parameter; nodes
     * at the same depth are placed at the same level, even if a node does not
     * fit into the space between the levels. If the node is to big, its
     * children and its outgoing edges may intersect the node.
     * 
     * @see GloballyFixedLevelsStrategy
     */
    FIXED_PARAMETER_IGNORE(new GloballyFixedLevelsStrategy(true, false),
            "By parameter; ignore node height"),

    /**
     * All levels are of equal distance, which is determined by parameter; if a
     * node does not fit in the space between two levels, all nodes of the
     * subtree of that node are moved to consecutive levels until there are no
     * intersecting nodes or edges.
     * 
     * @see GloballyFixedLevelsStrategy
     */
    FIXED_PARAMETER_ADJUST_INDIVIDUAL(new GloballyFixedLevelsStrategy(false,
            false), "By parameter; move children if necessary"),

    /**
     * All levels are of equal distance, which is determined by parameter; if a
     * node does not fit in the space betweeen two levels, the affected levels
     * are skipped by all nodes in the tree so that no nodes or edges intersect
     * but nodes of the same depth are still placed at the same level.
     * 
     * @see GloballyFixedLevelsStrategy
     */
    FIXED_PARAMETER_ADJUST_LEVEL(new GloballyFixedLevelsStrategy(false, true),
            "By parameter; skip levels if necessary"),

    /**
     * The distance between each level is determined by the highest node of the
     * respective depth.
     * 
     * @see HighestNodeLevelsStrategy
     */
    HIGHEST_NODE(new HighestNodeLevelsStrategy(), "Highest node per level"),

    /**
     * All levels are of equal distance, which is determined by the highest node
     * of the tree.
     * 
     * @see GloballyHighestNodeLevelsStrategy
     */
    GLOBALLY_HIGHEST_NODE(new GloballyHighestNodeLevelsStrategy(),
            "Highest node in tree");

    /**
     * The names of the policies.
     * 
     * @see #getNames()
     */
    private static ArrayList<String> names;

    /**
     * The <code>LevellingStrategy</code> used by this enumeration member.
     */
    private LevellingStrategy strategy;

    /**
     * Returns the names of the policies. To get the name of a specific policy,
     * such as <code>LOCAL</code>, you can write<br> {@code
     * LevellingPolicy.getNames().get( LevellingPolicy.LOCAL.ordinal())}
     * 
     * @return the names of the policies.
     * @see #names
     */
    public static List<String> getNames() {
        return Collections.unmodifiableList(names);
    }

    /**
     * Creates a new instance of the subclass of <code>LevellingStrategy</code>
     * employed by this enumeration member. The object is initialized using
     * {@link LevellingStrategy#setParameter(double, double)} by the parameters
     * <code>parameter</code> and <code>verticalNodeDistance</code>.
     * 
     * @param parameter
     *            the semantic of <code>parameter</code> is left to the specific
     *            subclass of <code>LevellingStrategy</code>. It is usually used
     *            to determine the default distance between proximate levels.
     * @param verticalNodeDistance
     *            See {@link ReingoldTilfordAlgorithm#getVerticalNodeDistance()}
     * @return a new instance of the subclass of <code>LevellingStrategy</code>
     *         employed by this enumeration member.
     * @see LevellingStrategy#parameter
     */
    public LevellingStrategy createLevellingStrategy(double parameter,
            double verticalNodeDistance) {
        LevellingStrategy newStrategy = strategy.create();
        newStrategy.setParameter(parameter, verticalNodeDistance);
        return newStrategy;
    }

    /**
     * Creates a new <code>LevellingPolicy</code> enumeration member.
     * 
     * @param strategy
     *            the strategy the new policy employs.
     * @param name
     *            the name of the new policy.
     */
    private LevellingPolicy(LevellingStrategy strategy, String name) {
        addName(name);
        this.strategy = strategy;
    }

    /**
     * Is called by the constructor of this policy to add its name to the
     * <code>names</code> list.
     * 
     * @param name
     *            the name of the new policy.
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
