// =============================================================================
//
//   ReingoldTilfordAlgorithm.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.reingoldtilford;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.Port;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.DoubleParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.treedrawings.GraphChecker;
import org.graffiti.selection.Selection;

/**
 * ReingoldTilfordAlgorithm implements an extended version of the
 * Reingold-Tilford algorithm, which hierarchically lays out a rooted tree of
 * unbounded degree and with arbitrary node sizes.
 * <p>
 * Throughout this package, the terms 'width' and 'height' of (sub)trees refer
 * to dimensions of the tree layout rather than graph-theoretic properties.
 * <p>
 * The behavior is influenced by various parameters:
 * <ul>
 * <li>The minimal distance between subtrees (see
 * {@link #minimalHorizontalDistance}).
 * <li>The distance between a node and its children (see
 * {@link #verticalNodeDistance}).
 * <li>The layout of the edges (see {@link #edgeLayout}).
 * <li>The order of children (see {@link #childOrderPolicy}) which may be used
 * to optimize the width of the subtrees.
 * <li>The position where a parent node is placed above its children (see
 * {@link #parentPlacement}).
 * <li>The nodes may be placed in a grid or at arbitrary <code>double</code>
 * coordinates (see {@link #alignToGrid}).
 * <li>The orientation of the tree layout (see {@link #orientationStrategy}).
 * <li>The levelling (see {@link #levellingStrategy}).
 * </ul>
 * <p>
 * In all classes except <code>ReingoldTilfordAlgorithm</code> and
 * <code>Orientation</code>, the tree is considered as growing from top to
 * bottom, i.e. the root has the y-coordinate 0 and children have greater
 * y-coordinates than their parents. In order to achieve different orientations,
 * the coordinates and dimensions of the nodes, the edges and the ports are
 * transformed by the respective descendants of {@link Orientation}. Therefore
 * <code>Tree</code> and descendants of <code>ChildOrderStrategy</code>,
 * <code>EdgeLayoutStrategy</code> and <code>Levelling</code> must not directly
 * query or set coordinates or dimensions but rather use the methods
 * {@link ReingoldTilfordAlgorithm#getNodePosition(Node)},
 * {@link ReingoldTilfordAlgorithm#getNodeDimension(Node)},
 * {@link ReingoldTilfordAlgorithm#setNodePosition(Node, double, double)},
 * {@link ReingoldTilfordAlgorithm#createPort(String, double, double)} and
 * {@link ReingoldTilfordAlgorithm#createCoordinateAttribute(String, double, double)}.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see ReingoldTilfordPlugin
 */
public class ReingoldTilfordAlgorithm extends AbstractAlgorithm {
    /**
     * By this enumeration, each parameter is assigned an unique index.
     */
    private enum ParameterIndices {
        SELECTION, EDGE_LAYOUT, CHILD_ORDER, OPTIMIZE_UP_TO_DEGREE, CONSIDER_FLIPPING, STABLE_OPTIMIZATION, PARENT_PLACEMENT, MINIMAL_HORIZONTAL_DISTANCE, MINIMAL_VERTICAL_DISTANCE, VERTICAL_NODE_DISTANCE, ALIGN_TO_GRID, GRID_SPACING, ORIENTATION, LEVELLING, LEVELLING_PARAMETER, MIRROR_ISOMORPHIC_INVARIANCE
    };

    /**
     * Default value of {@link #minimalHorizontalDistance}.
     */
    private static final double DEFAULT_MINIMAL_HORIZONTAL_DISTANCE = 25.0;

    /**
     * Default value of {@link #minimalVerticalDistance}.
     */
    private static final double DEFAULT_MINIMAL_VERTICAL_DISTANCE = 0.0;

    /**
     * Default value of {@link #verticalNodeDistance}.
     */
    private static final double DEFAULT_VERTICAL_NODE_DISTANCE = 25.0;

    /**
     * Default value of {@link #alignToGrid}.
     */
    private static final boolean DEFAULT_ALIGN_TO_GRID = false;

    /**
     * Default value of {@link #gridSpacing}.
     */
    private static final double DEFAULT_GRID_SPACING = 50.0;

    /**
     * Default value of {@link #optimizeUpToDegree}.
     */
    private static final int DEFAULT_OPTIMIZE_UP_TO_DEGREE = 0;

    /**
     * Default value of {@link #considersFlipping}.
     */
    private static final boolean DEFAULT_CONSIDER_FLIPPING = false;

    /**
     * Default value of {@link #doesStableOptimization}.
     */
    private static final boolean DEFAULT_STABLE_OPTIMIZATION = false;

    /**
     * Default value of {@link #levellingParameter}.
     */
    private static final double DEFAULT_LEVELLING_PARAMETER = 50.0;

    /**
     * Default value of (@link #isMirrorIsomorphicInvariant}.
     */
    private static final boolean DEFAULT_MIRROR_ISOMORPHIC_INVARIANCE = false;

    /**
     * See {@link #selection}.
     */
    private SelectionParameter selectionParameter;

    /**
     * See {@link #edgeLayout}.
     */
    private StringSelectionParameter edgeLayoutParameter;

    /**
     * See {@link #childOrderPolicy}.
     */
    private StringSelectionParameter childOrderPolicyParameter;

    /**
     * See {@link #optimizeUpToDegree}.
     */
    private IntegerParameter optimizeUpToDegreeParameter;

    /**
     * See {@link #considersFlipping}.
     */
    private BooleanParameter considerFlippingParameter;

    /**
     * See {@link #doesStableOptimization}.
     */
    private BooleanParameter stableOptimizationParameter;

    /**
     * See {@link #parentPlacement}.
     */
    private StringSelectionParameter parentPlacementParameter;

    /**
     * See {@link #minimalHorizontalDistance}.
     */
    private DoubleParameter minimalHorizontalDistanceParameter;

    /**
     * See {@link #minimalVerticalDistance}.
     */
    private DoubleParameter minimalVerticalDistanceParameter;

    /**
     * See {@link #verticalNodeDistance}.
     */
    private DoubleParameter verticalNodeDistanceParameter;

    /**
     * See {@link #alignToGrid}.
     */
    private BooleanParameter alignToGridParameter;

    /**
     * See {@link #gridSpacing}.
     */
    private DoubleParameter gridSpacingParameter;

    /**
     * See {@link #orientationStrategy}.
     */
    private StringSelectionParameter orientationParameter;

    /**
     * See {@link #levellingStrategy}.
     */
    private StringSelectionParameter levellingParameter;

    /**
     * See {@link #levellingStrategy}.
     */
    private DoubleParameter levellingParameterParameter;

    /**
     * See {@link #isMirrorIsomorphicInvariant()}.
     */
    private BooleanParameter mirrorIsomorphicInvarianceParameter;

    /**
     * The currently selected nodes. This parameter is not used at the moment.
     * The root of the tree is instead determined by
     * {@link GraphChecker#checkTree(org.graffiti.graph.Graph, int)}.
     */
    private Selection selection;

    /**
     * The minimal horizontal distance between subtrees. From each point in a
     * given subtree, no sibling subtree must intersect the point's surrounding
     * rectangular area whoose sidelengths are 2
     * <code>minimalHorizontalDistance</code> and 2
     * <code>minimalHorizontalDistance</code>.
     * <p>
     * <center><img src="doc-files/ReingoldTilfordAlgorithm-1.png"></center>
     * <p>
     * <code>minimalHorizontalDistance</code> and
     * <code>minimalVerticalDistance</code> do not affect the distance of a
     * subtree to its ancestor nodes (use {@link #verticalNodeDistance} instead)
     * and the edges which connect its sibling subtrees to its parent. <center>
     * <img src="doc-files/ReingoldTilfordAlgorithm-2.png" align="center">
     * </center>
     */
    private double minimalHorizontalDistance;

    /**
     * The minimal vertical distance between subtrees.
     * 
     * @see #minimalHorizontalDistance
     */
    private double minimalVerticalDistance;

    /**
     * The vertical distance from the top side of a subtree to the bottom side
     * of its parent. See {@link #minimalHorizontalDistance} for an
     * illustration.
     */
    private double verticalNodeDistance;

    /**
     * The layout of the edges.
     * 
     * @see #getEdgeLayout()
     */
    private EdgeLayout edgeLayout;

    /**
     * The order of the children. The order affects the width of the subtrees so
     * some descendands of <code>ChildOrderPolicy</code> are used to minimize
     * it. For each node, when {@code optimizeUpToDegree > 0} and the count of
     * children {@code > optimizeUpToDegree},
     * {@link AllPermutationsChildOrderStrategy} is used rather than {@code
     * #childOrderPolicy}
     */
    private ChildOrderPolicy childOrderPolicy;

    /**
     * See {@link #childOrderPolicy}.
     */
    private int optimizeUpToDegree;

    /**
     * Determines if instances of {@link ChildOrderPolicy} may flip complete
     * subtrees in addition to reordering them.
     */
    private boolean considersFlipping;

    /**
     * Determines if the repeated execution of this algorithm shall always yield
     * the same graph layout. Different results in consecutive executions are
     * sometimes intended as some instances of {@link ChildOrderPolicy} try to
     * heuristically optimize the width of the tree. Randomness is then
     * introduced by the accidental iteration order of {@link HashSet}.
     * 
     * @see #createSet()
     * @see #createSet(Collection)
     */
    private boolean doesStableOptimization;

    /**
     * Placement of a parent in respect of its children. If {@link #alignToGrid}
     * , <code>parentPlacement</code> is set to
     * {@link ParentPlacement#CENTER_ABOVE_OUTSIDE_CHILDREN} and
     * {@link #levellingStrategy} is created by
     * {@link LevellingPolicy#FIXED_PARAMETER_ADJUST_INDIVIDUAL}.
     * 
     * @see #getParentPlacement()
     */
    private ParentPlacement parentPlacement;

    /**
     * Determines if the nodes shall be placed in a grid (with a spacing of
     * <code>gridSpacing</code>) or at arbitrary <code>double</code>
     * coordinates. If <code>true</code>, <code>parentPlacement</code> is set to
     * {@link ParentPlacement#CENTER_ABOVE_OUTSIDE_CHILDREN} and
     * {@link #levellingStrategy} is created by
     * {@link LevellingPolicy#FIXED_PARAMETER_ADJUST_INDIVIDUAL}.
     */
    private boolean alignToGrid;

    /**
     * See {@link #alignToGrid}.
     */
    private double gridSpacing;

    /**
     * The orientation of the tree layout.
     */
    private OrientationStrategy orientationStrategy;

    /**
     * The levelling.
     * 
     * @see LevellingPolicy
     * @see #getLevels()
     * @see #ignoreNodeHeightAtLevelling()
     */
    private LevellingStrategy levellingStrategy;

    /**
     * See {@link #isMirrorIsomorphicInvariant()}.
     */
    private boolean isMirrorIsomorphicInvariant;

    /**
     * The root of the tree.
     */
    private Node root;

    /**
     * The layout of the root.
     */
    private Tree treeRoot;

    /**
     * Constructs a ReingoldTilfordAlgorithm object.
     */
    public ReingoldTilfordAlgorithm() {
        selectionParameter = new SelectionParameter("Selection",
                "Root of the drawn tree.");
        edgeLayoutParameter = new StringSelectionParameter(EdgeLayout
                .getNames().toArray(new String[0]), "Edge layout", "");
        childOrderPolicyParameter = new StringSelectionParameter(
                ChildOrderPolicy.getNames().toArray(new String[0]),
                "Child order policy", "");
        optimizeUpToDegreeParameter = new IntegerParameter(
                DEFAULT_OPTIMIZE_UP_TO_DEGREE,
                "Always optimize width up to this degree.", "");
        considerFlippingParameter = new BooleanParameter(
                DEFAULT_CONSIDER_FLIPPING, "Flipping",
                "Consider flipping subtrees when optimizing.");
        stableOptimizationParameter = new BooleanParameter(
                DEFAULT_STABLE_OPTIMIZATION, "Stable Optimization",
                "Optimization yields the same result when applied repeatedly.");
        parentPlacementParameter = new StringSelectionParameter(ParentPlacement
                .getNames().toArray(new String[0]), "Parent placement", "");
        minimalHorizontalDistanceParameter = new DoubleParameter(
                DEFAULT_MINIMAL_HORIZONTAL_DISTANCE,
                "Minimal horizontal distance", "", 0.0, 1000.0);
        minimalVerticalDistanceParameter = new DoubleParameter(
                DEFAULT_MINIMAL_VERTICAL_DISTANCE, "Minimal vertical distance",
                "", 0.0, 1000.0);
        verticalNodeDistanceParameter = new DoubleParameter(
                DEFAULT_VERTICAL_NODE_DISTANCE, "Vertical node distance", "",
                0.0, 1000.0);
        alignToGridParameter = new BooleanParameter(DEFAULT_ALIGN_TO_GRID,
                "Align to grid", "Place all nodes at grid points."
                        + String.format("%20c", ' '));
        gridSpacingParameter = new DoubleParameter(DEFAULT_GRID_SPACING,
                "Grid spacing", "", Double.MIN_VALUE, 1000.0);
        orientationParameter = new StringSelectionParameter(Orientation
                .getNames().toArray(new String[0]), "Orientation", "");
        orientationStrategy = null;
        levellingParameter = new StringSelectionParameter(LevellingPolicy
                .getNames().toArray(new String[0]), "Levelling", "");
        levellingParameterParameter = new DoubleParameter(
                DEFAULT_LEVELLING_PARAMETER, "Levelling parameter", "", 0.0,
                1000.0);
        levellingStrategy = null;
        mirrorIsomorphicInvarianceParameter = new BooleanParameter(
                DEFAULT_MIRROR_ISOMORPHIC_INVARIANCE, "Mirror-isomorphism",
                "Draw mirror-isomorphic subtrees equally.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        parameters = new Parameter[ParameterIndices.values().length];
        parameters[ParameterIndices.SELECTION.ordinal()] = selectionParameter;
        parameters[ParameterIndices.EDGE_LAYOUT.ordinal()] = edgeLayoutParameter;
        parameters[ParameterIndices.CHILD_ORDER.ordinal()] = childOrderPolicyParameter;
        parameters[ParameterIndices.OPTIMIZE_UP_TO_DEGREE.ordinal()] = optimizeUpToDegreeParameter;
        parameters[ParameterIndices.CONSIDER_FLIPPING.ordinal()] = considerFlippingParameter;
        parameters[ParameterIndices.STABLE_OPTIMIZATION.ordinal()] = stableOptimizationParameter;
        parameters[ParameterIndices.PARENT_PLACEMENT.ordinal()] = parentPlacementParameter;
        parameters[ParameterIndices.MINIMAL_HORIZONTAL_DISTANCE.ordinal()] = minimalHorizontalDistanceParameter;
        parameters[ParameterIndices.MINIMAL_VERTICAL_DISTANCE.ordinal()] = minimalVerticalDistanceParameter;
        parameters[ParameterIndices.VERTICAL_NODE_DISTANCE.ordinal()] = verticalNodeDistanceParameter;
        parameters[ParameterIndices.ALIGN_TO_GRID.ordinal()] = alignToGridParameter;
        parameters[ParameterIndices.GRID_SPACING.ordinal()] = gridSpacingParameter;
        parameters[ParameterIndices.ORIENTATION.ordinal()] = orientationParameter;
        parameters[ParameterIndices.LEVELLING.ordinal()] = levellingParameter;
        parameters[ParameterIndices.LEVELLING_PARAMETER.ordinal()] = levellingParameterParameter;
        parameters[ParameterIndices.MIRROR_ISOMORPHIC_INVARIANCE.ordinal()] = mirrorIsomorphicInvarianceParameter;
        return super.getAlgorithmParameters();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        super.setAlgorithmParameters(params);
        selection = ((SelectionParameter) params[ParameterIndices.SELECTION
                .ordinal()]).getSelection();
        edgeLayout = EdgeLayout.values()[((StringSelectionParameter) params[ParameterIndices.EDGE_LAYOUT
                .ordinal()]).getSelectedIndex()];
        childOrderPolicy = ChildOrderPolicy.values()[((StringSelectionParameter) params[ParameterIndices.CHILD_ORDER
                .ordinal()]).getSelectedIndex()];
        optimizeUpToDegree = ((IntegerParameter) params[ParameterIndices.OPTIMIZE_UP_TO_DEGREE
                .ordinal()]).getInteger();
        considersFlipping = ((BooleanParameter) params[ParameterIndices.CONSIDER_FLIPPING
                .ordinal()]).getBoolean();
        doesStableOptimization = ((BooleanParameter) params[ParameterIndices.STABLE_OPTIMIZATION
                .ordinal()]).getBoolean();
        parentPlacement = ParentPlacement.values()[((StringSelectionParameter) params[ParameterIndices.PARENT_PLACEMENT
                .ordinal()]).getSelectedIndex()];
        minimalHorizontalDistance = ((DoubleParameter) params[ParameterIndices.MINIMAL_HORIZONTAL_DISTANCE
                .ordinal()]).getDouble();
        minimalVerticalDistance = ((DoubleParameter) params[ParameterIndices.MINIMAL_VERTICAL_DISTANCE
                .ordinal()]).getDouble();
        verticalNodeDistance = ((DoubleParameter) params[ParameterIndices.VERTICAL_NODE_DISTANCE
                .ordinal()]).getDouble();
        alignToGrid = ((BooleanParameter) params[ParameterIndices.ALIGN_TO_GRID
                .ordinal()]).getBoolean();
        gridSpacing = ((DoubleParameter) params[ParameterIndices.GRID_SPACING
                .ordinal()]).getDouble();
        orientationStrategy = Orientation.values()[((StringSelectionParameter) params[ParameterIndices.ORIENTATION
                .ordinal()]).getSelectedIndex()].createOrientationStrategy();
        levellingStrategy = LevellingPolicy.values()[((StringSelectionParameter) params[ParameterIndices.LEVELLING
                .ordinal()]).getSelectedIndex()].createLevellingStrategy(
                ((DoubleParameter) params[ParameterIndices.LEVELLING_PARAMETER
                        .ordinal()]).getDouble(), verticalNodeDistance);
        isMirrorIsomorphicInvariant = ((BooleanParameter) params[ParameterIndices.MIRROR_ISOMORPHIC_INVARIANCE
                .ordinal()]).getBoolean();

        if (alignToGrid) {
            minimalVerticalDistance = 0.0;
            verticalNodeDistance = gridSpacing / 2.0;
            minimalHorizontalDistance = gridSpacing / 2.0;
            parentPlacement = ParentPlacement.CENTER_ABOVE_OUTSIDE_CHILDREN;
            levellingStrategy = LevellingPolicy.FIXED_PARAMETER_ADJUST_INDIVIDUAL
                    .createLevellingStrategy(gridSpacing, gridSpacing / 2.0);
            isMirrorIsomorphicInvariant = false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors;
        try {
            root = GraphChecker.checkTree(graph, Integer.MAX_VALUE);
            errors = new PreconditionException();
        } catch (PreconditionException p) {
            if (selection != null) {
                selection.clear();
                Iterator<PreconditionException.Entry> iter = p.iterator();
                while (iter.hasNext()) {
                    Selection newSelection = (Selection) iter.next().source;
                    if (newSelection != null) {
                        selection.addSelection(newSelection);
                    }
                }
            }
            errors = p;
        }
        if (alignToGrid && gridSpacing <= minimalHorizontalDistance) {
            errors
                    .add("The minimal horizontal distance must not exceed grid spacing.");
        }
        if (minimalHorizontalDistance < 0) {
            errors.add("The minimal horizontal distance must not be negative.");
        }
        if (minimalVerticalDistance < 0) {
            errors.add("The minimal vertical distance must not be negative.");
        }
        if (verticalNodeDistance < 0) {
            errors.add("The vertical node distance must not be negative.");

        }
        if (alignToGrid && gridSpacing <= 0) {
            errors.add("Grid spacing must be positive.");
        }
        if (optimizeUpToDegree < 0) {
            errors.add("The degree up to which all permutations are tested "
                    + "must not be negative");
        }
        if (levellingStrategy.parameter <= 0) {
            errors.add("The levelling parameter must be positive.");
        }
        if (!errors.isEmpty())
            throw errors;
    }

    /**
     * Executes the algorithm.
     * <p>
     * At first {@link #levellingStrategy} inspects the tree to possibly
     * calculate the levels where the nodes will be placed at.
     * <p>
     * The actual job is done by constructing a {@link Tree} for the root, which
     * recursively constructs instances of <code>Tree</code> for its children.
     * In doing so, the tree layout is created in a bottom-up manner.
     * <p>
     * Finally the layout is applied to the graph by a call to
     * {@link Tree#draw(ReingoldTilfordAlgorithm, double, double)}.
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        assert (root != null);

        // Uncomment for debug purposes.
        // DebugSession.create("");

        graph.getListenerManager().transactionStarted(this);
        levellingStrategy.calculateLevels(root, this);

        // Uncomment for debug purposes.
        /*
         * TreeVisitor<Boolean> debugCondition = new TreeVisitor<Boolean>() {
         * public Boolean visit(Tree tree) { return true; }
         * 
         * }; Tree tree = new Tree(root, null, null, 0, this, debugCondition);
         */

        Tree tree = new Tree(root, null, null, 0, this, null);
        orientationStrategy.setWidth(tree.getWidth());
        orientationStrategy.setHeight(tree.getHeight());
        tree.draw(this, 0.0, 0.0);
        graph.getListenerManager().transactionFinished(this);
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "Reingold-Tilford";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        super.reset();
        root = null;
    }

    /**
     * Returns the edge layout.
     * 
     * @return {@link #edgeLayout}.
     */
    EdgeLayout getEdgeLayout() {
        return edgeLayout;
    }

    /**
     * Returns the child order policy.
     * 
     * @return {@link #childOrderPolicy}.
     */
    ChildOrderPolicy getChildOrderPolicy() {
        return childOrderPolicy;
    }

    /**
     * Returns the count of the children of a node up to that
     * {@link AllPermutationsChildOrderStrategy} is used for the ordering
     * instead of {@link #childOrderPolicy}, or 0, if
     * <code>childOrderPolicy</code> shall be always used.
     * 
     * @return {@link #optimizeUpToDegree}.
     */
    int getOptimizeUpToDegree() {
        return optimizeUpToDegree;
    }

    /**
     * Returns <code>true</code>, if instances of {@link ChildOrderPolicy} may
     * flip complete subtrees in addition to reordering them.
     * 
     * @return {@link #considersFlipping}.
     */
    public boolean isConsiderFlipping() {
        return considersFlipping;
    }

    /**
     * Returns where a parent is placed above its children.
     * 
     * @return {@link #parentPlacement}.
     */
    ParentPlacement getParentPlacement() {
        return parentPlacement;
    }

    /**
     * Returns the minimal horizontal distance between a subtree and its
     * siblings.
     * 
     * @return {@link #minimalHorizontalDistance}.
     */
    double getMinimalHorizontalDistance() {
        return minimalHorizontalDistance;
    }

    /**
     * Returns the minimal vertical distance between a subtree and its siblings.
     * 
     * @return {@link #minimalVerticalDistance}
     * @see #minimalHorizontalDistance
     */
    double getMinimalVerticalDistance() {
        return minimalVerticalDistance;
    }

    /**
     * Returns the vertical distance from the top side of a subtree to the
     * bottom side of its parent. See {@link #minimalHorizontalDistance} for an
     * illustration.
     * 
     * @return {@link #verticalNodeDistance}
     */
    double getVerticalNodeDistance() {
        return verticalNodeDistance;
    }

    /**
     * Returns <code>true</code> if the nodes shall be placed at grid points.
     * 
     * @return {@link #alignToGrid}
     */
    boolean isAlignToGrid() {
        return alignToGrid;
    }

    /**
     * Returns the spacing of the grid if the nodes shall be placed at grid
     * points.
     * 
     * @return {@link #gridSpacing}
     */
    double getGridSpacing() {
        return gridSpacing;
    }

    /**
     * Returns the size of <code>node.
     * <code>Tree</code> and descendants of <code>ChildOrderStrategy</code>,
     * <code>EdgeLayoutStrategy</code> and <code>Levelling</code> must not
     * directly query the dimension of a node but rather use this method. See
     * {@link Orientation} for further explanation.
     * 
     * @param node
     *            the node whoose size is to be returned.
     * @return the size of <code>node</code>.
     */
    public Point2D.Double getNodeDimension(Node node) {
        DimensionAttribute dimension = (DimensionAttribute) node
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.DIMENSION);
        Point2D.Double nodeSize = orientationStrategy
                .transformDimension(new Point2D.Double(dimension.getWidth(),
                        dimension.getHeight()));
        if (alignToGrid) {
            double unit = gridSpacing - minimalHorizontalDistance;
            int gridWidth = (int) Math.ceil((nodeSize.getX() - unit)
                    / gridSpacing);
            if ((gridWidth & 1) != 0) {
                gridWidth++;
            }
            return new Point2D.Double(gridSpacing * gridWidth + unit, nodeSize
                    .getY());
        }
        return nodeSize;
    }

    /**
     * Return the position of <code>node</code>. <code>Tree</code> and
     * descendants of <code>ChildOrderStrategy</code>,
     * <code>EdgeLayoutStrategy</code> and <code>Levelling</code> must not
     * directly query the position of a node but rather use this method. See
     * {@link Orientation} for further explanation.
     * 
     * @param node
     *            the node whoose position is to be returned.
     * @return the position of <code>node</code>.
     */
    public Point2D.Double getNodePosition(Node node) {
        CoordinateAttribute coordinate = (CoordinateAttribute) node
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.COORDINATE);
        return orientationStrategy
                .transformReadingNodePosition(new Point2D.Double(coordinate
                        .getX(), coordinate.getY()));
    }

    /**
     * Return the position of <code>node</code>. <code>Tree</code> and
     * descendants of <code>ChildOrderStrategy</code>,
     * <code>EdgeLayoutStrategy</code> and <code>Levelling</code> must not
     * directly set the position of a node but rather use this method. See
     * {@link Orientation} for further explanation.
     * 
     * @param node
     *            the node whoose position is to be set.
     * @param x
     *            the new x-coordinate of <code>node</code>.
     * @param y
     *            the new y-coordinate of <code>node</code>.
     */
    void setNodePosition(Node node, double x, double y) {
        CoordinateAttribute coordinate = (CoordinateAttribute) node
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.COORDINATE);
        Point2D.Double position = orientationStrategy
                .transformWritingNodePosition(new Point2D.Double(x, y));
        coordinate.setX(position.getX());
        coordinate.setY(position.getY());
    }

    /**
     * Creates a <code>Port</code> at the specified coordinates.
     * <code>Tree</code> and descendants of <code>ChildOrderStrategy</code>,
     * <code>EdgeLayoutStrategy</code> and <code>Levelling</code> must not
     * directly create ports, but rather use this method. See
     * {@link Orientation} for further explanation.
     * 
     * @param name
     *            the name of the new port.
     * @param x
     *            the x-coordinate of the new port.
     * @param y
     *            the y-coordinate of the new port.
     * @return the created port.
     */
    public Port createPort(String name, double x, double y) {
        Point2D.Double position = orientationStrategy
                .transformPortPosition(new Point2D.Double(x, y));
        return new Port(name, position.getX(), position.getY());
    }

    /**
     * Creates a <code>CoordinateAttribute</code> at the specified coordinates.
     * <code>Tree</code> and descendants of <code>ChildOrderStrategy</code>,
     * <code>EdgeLayoutStrategy</code> and <code>Levelling</code> must not
     * directly create coordinate attributes, but rather use this method. See
     * {@link Orientation} for further explanation.
     * 
     * @param id
     *            the id of the new coordinate attribute.
     * @param x
     *            the x-coordinate of the new coordinate attribute.
     * @param y
     *            the y-coordinate of the new coordinate attribute.
     * @return the created coordinate attribute.
     */
    public CoordinateAttribute createCoordinateAttribute(String id, double x,
            double y) {
        Point2D.Double position = orientationStrategy
                .transformWritingNodePosition(new Point2D.Double(x, y));
        return new CoordinateAttribute(id, position.getX(), position.getY());
    }

    /**
     * Returns the levels.
     * 
     * @return the vertical distances between the top side of each node and the
     *         top side of its children for the respective level. If the nodes
     *         shall not be aligned to levels, the returned
     *         <code>ArrayList</code> is empty.
     * @see LevellingPolicy
     */
    ArrayList<Double> getLevels() {
        return levellingStrategy.getLevels();
    }

    /**
     * Returns <code>true</code> if the height of the nodes shall be ignored
     * when calculating the levels.
     * 
     * @return <code>true</code>, if the height of the nodes shall be ignored
     *         when calculating the levels. This may lead to intersecting nodes.
     *         <code>false</code>, if the children shall be aligned to different
     *         levels when they would otherwise intersect with their parent.
     */
    boolean ignoreNodeHeightAtLevelling() {
        return levellingStrategy.ignoreNodeHeight();
    }

    /**
     * Returns if a flipped (mirrored) layout of a tree shall equal the layout
     * of the respective flipped tree.
     * 
     * @return <code>true</code> if a flipped (mirrored) layout of a tree shall
     *         equal the layout of the respective flipped tree.
     */
    boolean isMirrorIsomorphicInvariant() {
        return isMirrorIsomorphicInvariant;
    }

    /**
     * Creates an empty {@code Set<Tree>}.
     * 
     * @return a new {@code Set<Tree>}. If <code>doesStableOptimization</code>
     *         is <code>false</code>, a {@link HashSet} is created so that the
     *         iteration order of the returned set is random. Otherwise, a
     *         {@link LinkedHashSet} with an iteration order depending on the
     *         insertion order is created. See {@link #doesStableOptimization}
     *         for further explanation.
     */
    public Set<Tree> createSet() {

        if (doesStableOptimization)
            return new LinkedHashSet<Tree>();
        else
            return new HashSet<Tree>();
    }

    /**
     * Creates an {@code Set<Tree>} containing the elements of
     * <code>collection</code>.
     * 
     * @param collection
     *            the elements of this collection are inserted into the new set.
     * @return a new {@code Set<Tree>}. If <code>doesStableOptimization</code>
     *         is <code>false</code>, a {@link HashSet} is created so that the
     *         iteration order of the returned set is random. Otherwise, a
     *         {@link LinkedHashSet} with an iteration order depending on the
     *         insertion order is created. The set does initially contain the
     *         elements of <code>collection</code>. See
     *         {@link #doesStableOptimization} for further explanation.
     */
    public Set<Tree> createSet(Collection<Tree> collection) {
        if (doesStableOptimization)
            return new LinkedHashSet<Tree>(collection);
        else
            return new HashSet<Tree>(collection);
    }

    /**
     * Sets the tree layout of the whole tree. Must only be called by the
     * {@link Tree#Tree(Node, org.graffiti.graph.Edge, Tree, int, ReingoldTilfordAlgorithm, TreeVisitor)}
     * initialized with the root node.
     * 
     * @param treeRoot
     *            the tree layout of the whole tree.
     */
    void setTreeRoot(Tree treeRoot) {
        this.treeRoot = treeRoot;
    }

    /**
     * Returns the first tree layout for which <code>visitor</code> returns
     * <code>true</code>. The tree is searched in a depth-first manner.
     * 
     * @param visitor
     *            is called with the layout of each subtree until it returns
     *            <code>true</code>. It may return <code>false</code> for every
     *            tree layout.
     * @return the first tree for which <code>visitor</code> returns
     *         <code>true</code>, or <code>null</code> if <code>visitor</code>
     *         returns <code>false</code> for the layout of each subtree.
     */
    public Tree find(TreeVisitor<Boolean> visitor) {
        return treeRoot.find(visitor);
    }

    public void setRoot(Node root) {
        this.root = root;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
