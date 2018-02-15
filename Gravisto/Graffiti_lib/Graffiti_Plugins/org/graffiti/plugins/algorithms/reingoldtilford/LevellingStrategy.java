// =============================================================================
//
//   LevellingStrategy.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.reingoldtilford;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;

/**
 * Subclasses of <code>LevellingStrategy</code> define at which y-coordinate
 * each <code>Node</code> is placed. The y-coordinate may be calculated
 * individually for each {@link Node} or be restricted to certain values
 * (levels). The level to which each nodes belongs depends on its depth (number
 * of edges of the simple path connecting the node and the root) in the tree.
 * <p>
 * To provide a new levelling policy, create a class extending
 * <code>LevellingStrategy</code> and add a new member to the
 * <code>LevellingPolicy</code> enumeration.
 * <p>
 * Subclasses of <code>LevellingStrategy</code> must not directly query the
 * dimensions of the nodes but rather use
 * {@link ReingoldTilfordAlgorithm#getNodeDimension(Node)}.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see ReingoldTilfordAlgorithm
 * @see LevellingPolicy
 * @see Orientation
 */
public abstract class LevellingStrategy implements Cloneable {
    /**
     * A double parameter whose semantic is left to the subclasses of
     * <code>LevellingStrategy</code>. It is usually used to determine the
     * default distance between proximate levels.
     */
    protected double parameter;

    /**
     * See {@link ReingoldTilfordAlgorithm#getVerticalNodeDistance()}.
     */
    protected double verticalNodeDistance;

    /**
     * The levels.
     */
    protected ArrayList<Double> levels;

    /**
     * Sets parameters for the <code>LevellingStrategy</code>. Must only be
     * called by {@link LevellingPolicy#createLevellingStrategy(double, double)}
     * .
     * 
     * @param parameter
     *            See {@link #parameter}.
     * @param verticalNodeDistance
     *            See {@link ReingoldTilfordAlgorithm#getVerticalNodeDistance()}
     */
    void setParameter(double parameter, double verticalNodeDistance) {
        this.parameter = parameter;
        this.verticalNodeDistance = verticalNodeDistance;
    }

    /**
     * Calculates the levels. The levels can afterwards be queried by
     * {@link #getLevels()}. The default implementation of this method calls
     * {@link #recursivlyCalculateLevels(Node, int, ReingoldTilfordAlgorithm)}.
     * To define their specific behaviour, subclasses of
     * <code>LevellingStrategy</code> may either override this method or
     * override {@link #notifyNodeHeight(int, double)}, which is called by
     * <code>recursivlyCalculateLevels(Node, int, ReingoldTilfordAlgorithm)
     * </code> for each node of the tree.
     * 
     * @param root
     *            the root of the tree.
     * @param algorithm
     *            the Reingold-Tilford algorithm.
     */
    public void calculateLevels(Node root, ReingoldTilfordAlgorithm algorithm) {
        levels = new ArrayList<Double>();
        recursivlyCalculateLevels(root, 0, algorithm);
    }

    /**
     * Calls {@link #notifyNodeHeight(int, double)} for <code>node</code> and
     * then recursively executes <code>recursivlyCalculateLevels</code> for each
     * child of <code>node</code>.
     * 
     * @param node
     *            the node that is the root of the subtree for whose nodes
     *            <code>notifyNodeHeight</code> shall be called.
     * @param depth
     *            the depth of <code>node</code> in the tree.
     * @param algorithm
     *            the Reingold-Tiford algorithm.
     */
    private void recursivlyCalculateLevels(Node node, int depth,
            ReingoldTilfordAlgorithm algorithm) {
        if (levels.size() <= depth) {
            levels.add(0.0);
        }
        Point2D.Double dimension = algorithm.getNodeDimension(node);
        notifyNodeHeight(depth, dimension.getY());
        for (Edge outEdge : node.getAllOutEdges()) {
            recursivlyCalculateLevels(outEdge.getTarget(), depth + 1, algorithm);
        }
    }

    /**
     * If {@link #calculateLevels(Node, ReingoldTilfordAlgorithm)} is not
     * overridden, this method is called for each node of the tree. Subclasses
     * of <code>LevellingStrategy</code> may override this method to define
     * their specific behaviour.
     * 
     * @param depth
     *            the depth of the node.
     * @param height
     *            the height of the node.
     */
    protected void notifyNodeHeight(int depth, double height) {
    }

    /**
     * Returns the levels.
     * <p>
     * <b>Preconditions:</b><br>
     * {@link #calculateLevels(Node, ReingoldTilfordAlgorithm)} must have been
     * called.
     * 
     * @return the levels or <code>null</code> if
     *         <code>calculateLevels(Node, ReingoldTilfordAlgorithm)</code> has
     *         not been called yet.
     */
    public ArrayList<Double> getLevels() {
        return levels;
    }

    /**
     * Returns if the height of each node shall be ignored. If
     * <code>ignoreNodeHeight</code> returns <code>true</code> and the space
     * between consecutive levels is not big enough to comprise each node placed
     * at these levels, nodes or edges may intersect. If
     * <code>ignoreNodeHeight</code> returns <code>false</code>, the nodes of
     * the subtrees of oversized nodes are moved to consecutive levels until
     * there are no intersecting nodes and edges. The default implementation
     * always returns <code>false</code>. Subclasses of
     * <code>LevellingStrategy</code> may override this method to define their
     * specific behaviour.
     * 
     * @return <code>false</code>.
     */
    public boolean ignoreNodeHeight() {
        return false;
    }

    /**
     * Creates a new <code>LevellingStrategy</code>. Subclasses of
     * <code>LevellingStrategy</code> must implement this method to create an
     * instance of exactly that sublass.
     * 
     * @return a new <code>LevellingStrategy</code>.
     */
    public abstract LevellingStrategy create();
}

/**
 * This <code>LevellingStrategy</code> yields individual y-coordinates for each
 * <code>Node</code>. There are no levels. The
 * {@link LevellingStrategy#parameter} field is not used.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see LevellingPolicy#LOCAL
 */
class LocalLevelsStrategy extends LevellingStrategy {
    /**
     * {@inheritDoc}
     */
    @Override
    public void calculateLevels(Node root, ReingoldTilfordAlgorithm algorithm) {
        // Do nothing
    }

    /**
     * Creates a new <code>LocalLevelsStrategy</code>.
     */
    @Override
    public LevellingStrategy create() {
        return new LocalLevelsStrategy();
    }
}

/**
 * This <code>LevellingStrategy</code> yields levels of equal distance, which is
 * determined by parameter. The behavior is further diversified by the
 * {@link #ignoreNodeHeight} and {@link #adjustLevel} fields.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see LevellingPolicy#FIXED_PARAMETER_IGNORE
 * @see LevellingPolicy#FIXED_PARAMETER_ADJUST_INDIVIDUAL
 * @see LevellingPolicy#FIXED_PARAMETER_ADJUST_LEVEL
 */
class GloballyFixedLevelsStrategy extends LevellingStrategy {
    /**
     * If <code>ignoreNodeHeight</code> is <code>true</code>, the height of each
     * node is ignored. Nodes that do not fit in the space between proximate
     * levels may lead to intersecting nodes or edges.
     * <p>
     * If <code>ignoreNodeHeight</code> is </code>false</code>, either, if
     * {@link #adjustLevel} is <code>false</code>, the nodes of the subtrees of
     * oversized nodes are moved to consecutive levels until there are no
     * intersecting nodes and edges, or, if <code>adjustLevel</code> is
     * <code>true</code>, the affected levels are skipped by each node of the
     * tree.
     * 
     * @see #ignoreNodeHeight()
     * @see LevellingStrategy#ignoreNodeHeight()
     */
    private boolean ignoreNodeHeight;

    /**
     * See {@link #ignoreNodeHeight}.
     */
    private boolean adjustLevel;

    /**
     * {@inheritDoc}
     */
    @Override
    public void calculateLevels(Node root, ReingoldTilfordAlgorithm algorithm) {
        super.calculateLevels(root, algorithm);
        if (!adjustLevel) {
            for (int i = 0; i < levels.size(); i++) {
                levels.set(i, parameter);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void notifyNodeHeight(int level, double height) {
        if (!adjustLevel)
            return;
        double usedHeight = height + verticalNodeDistance;
        if (usedHeight > levels.get(level)) {
            levels.set(level, parameter * Math.ceil(usedHeight / parameter));
        }
    }

    /**
     * Creates a new <code>GloballyFixedLevelsStrategy</code>.
     * 
     * @param ignoreNodeHeight
     *            See {@link #ignoreNodeHeight}.
     * @param adjustLevel
     *            See {@link #ignoreNodeHeight}.
     */
    public GloballyFixedLevelsStrategy(boolean ignoreNodeHeight,
            boolean adjustLevel) {
        this.ignoreNodeHeight = ignoreNodeHeight;
        this.adjustLevel = adjustLevel;
    }

    /**
     * Returns if the height of each node shall be ignored.
     * 
     * @return the value of {@link #ignoreNodeHeight}.
     * @see LevellingStrategy#ignoreNodeHeight()
     */
    @Override
    public boolean ignoreNodeHeight() {
        return ignoreNodeHeight;
    }

    /**
     * Creates a new <code>GloballyFixedLevelsStrategy</code>. The fields
     * {@link #ignoreNodeHeight} and {@link #adjustLevel} are copied from this
     * <code>GloballyFixedLevelsStrategy</code> so that this and the new
     * <code>LevellingStrategy</code> exhibit identical behaviour.
     */
    @Override
    public LevellingStrategy create() {
        return new GloballyFixedLevelsStrategy(ignoreNodeHeight, adjustLevel);
    }

}

/**
 * This <code>LevellingStrategy</code> yields levels whose distances are
 * determined by the highest node of the respective depth.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see LevellingPolicy#HIGHEST_NODE
 */
class HighestNodeLevelsStrategy extends LevellingStrategy {
    /**
     * {@inheritDoc}
     */
    @Override
    public void calculateLevels(Node root, ReingoldTilfordAlgorithm algorithm) {
        super.calculateLevels(root, algorithm);
        for (int i = 0; i < levels.size(); i++) {
            levels.set(i, levels.get(i) + verticalNodeDistance);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void notifyNodeHeight(int level, double height) {
        if (height > levels.get(level)) {
            levels.set(level, height);
        }
    }

    /**
     * Creates a new <code>HighestNodeLevelsStrategy</code>.
     */
    @Override
    public LevellingStrategy create() {
        return new HighestNodeLevelsStrategy();
    }
}

/**
 * This <code>LevellingStrategy</code> yields levels of equal distance, which is
 * determined by the highest node of the tree.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
class GloballyHighestNodeLevelsStrategy extends LevellingStrategy {
    /**
     * <code>maxHeight</code> is used during the execution of
     * {@link #calculateLevels(Node, ReingoldTilfordAlgorithm)} to hold the
     * height of the so far highest found node in the tree.
     */
    private double maxHeight;

    /**
     * {@inheritDoc}
     */
    @Override
    public void calculateLevels(Node root, ReingoldTilfordAlgorithm algorithm) {
        maxHeight = 0.0;
        super.calculateLevels(root, algorithm);
        for (int i = 0; i < levels.size(); i++) {
            levels.set(i, maxHeight + verticalNodeDistance);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void notifyNodeHeight(int level, double height) {
        if (height > maxHeight) {
            maxHeight = height;
        }
    }

    /**
     * Creates a new <code>GloballyHighestNodeLevelsStrategy</code>.
     */
    @Override
    public LevellingStrategy create() {
        return new GloballyHighestNodeLevelsStrategy();
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
