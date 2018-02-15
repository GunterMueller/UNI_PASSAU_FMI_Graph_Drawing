// =============================================================================
//
//   SugiyamaConstants.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SugiyamaConstants.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.util;

import org.graffiti.graphics.GraphicAttributeConstants;

/**
 * This class stores constants needed in the sugiyama-algorithm
 * 
 * @author Ferdinand H&uuml;bner
 */
public interface SugiyamaConstants {

    // -----------------------------------------------------------------------//
    // Error-Messages //
    // -----------------------------------------------------------------------//

    /** Error-message if the attached graph is null */
    public static final String ERROR_GRAPH_IS_NULL = "The attached graph"
            + " may not be null!";

    /** Error-message if the graph does not contain at least one node */
    public static final String ERROR_GRAPH_IS_EMPTY = "The graph is"
            + " empty!";

    /** Error-message in case that the graph is not directed */
    public static final String ERROR_GRAPH_UNDIRECTED = "The attached"
            + " graph is NOT directed!";

    /** Error-messagein case that the preconditions have not been checked */
    public static final String ERROR_PREC_NOT_CHECKED = "Preconditions"
            + " have not been checked. Call check() first.";

    /** Error-message in case that an infinte loop has been detected */
    public static final String ERROR_INFINITE_LOOP = "Infinite loop"
            + " detected. Have to abort.";

    // -----------------------------------------------------------------------//
    // Attribute-Paths //
    // -----------------------------------------------------------------------//

    /** Path in the attribute-tree to access the sugiyama-indegree */
    public static final String PATH_INDEGREE = "sugiyama.inDegree";

    /** Path to the x-position of a node */
    public static final String PATH_XPOS = "sugiyama.xpos";

    /**
     * Path to a string attached to a node, that is needed to compare two nodes
     * lexicographically
     */
    public static final String PATH_LEX = "sugiyama.lex";

    /** Path to the barycenter-value of a node */
    public static final String PATH_BARYCENTER = "sugiyama.barycenter";

    /** Path to the level-attribute needed by the brandes/koepf-algorithm */
    public static final String PATH_BK_LEVEL = "graphics.level";

    /** Path to the order-attribute needed by the brandes/koepf-algorithm */
    public static final String PATH_BK_ORDER = "graphics.order";

    /** Path to the dummy-attribute needed by the brandes/koepf-algorithm */
    public static final String PATH_BK_DUMMY = "graphics.dummy";

    /** Path to a nodes x-coordinates in the graph */
    public static final String PATH_X_COORD = GraphicAttributeConstants.COORDX_PATH;

    /** Path to a nodes y-coordinates in the graph */
    public static final String PATH_Y_COORD = GraphicAttributeConstants.COORDY_PATH;

    /** Path to the sugiyama-bends-attribute on an edge (obsolete) */
    public static final String PATH_SUGIYAMA_BENDS = "sugiyama.bends";

    /** Path to the dfs-number of a node */
    public static final String PATH_DFSNUM = "sugiyama.dfsNum";

    /** Path to the dfs-completion-number of a node */
    public static final String PATH_COMPNUM = "sugiyama.compNum";

    /**
     * Path to the distance - needed for topoSort and computation of longest
     * path in the graph
     */
    public static final String PATH_DISTANCE = "sugiyama.distance";

    /** Attribute needed for topoSort */
    public static final String PATH_TOPO = "sugiyama.topo";

    /** Path to the bfs-number of a node */
    public static final String PATH_BFSNUM = "sugiyama.bfsNum";

    /** Path to the sugiyama-attribute-tree */
    public static final String PATH_SUGIYAMA = "sugiyama";

    /** sub-path of the sugiyama-xpos-attribute */
    public static final String SUBPATH_XPOS = "xpos";

    /** sub-path of the sugiyama-lex-attribute */
    public static final String SUBPATH_LEX = "lex";

    /** path to the old x-position of a node */
    public static final String PATH_OLDPOS = "sugiyama.oldPos";

    /** sub-path to the old x-position of a node */
    public static final String SUBPATH_OLDPOS = "oldPos";

    /** sub-path to the barycenter-value of a node */
    public static final String SUBPATH_BARYCENER = "barycenter";

    /** path to the hasBeenDecycled-bit in the graph */
    public static final String PATH_HASBEENDECYCLED = "sugiyama.hasBeenDecycled";

    /** sub-path to the hasBeenDecycled-bit in the sugiyama-attribute-tree */
    public static final String SUBPATH_HASBEENDECYCLED = "hasBeenDecycled";

    /** sub-path to the bfs-number of a node */
    public static final String SUBPATH_BFSNUM = "bfsNum";

    /** path to the reversed-attribute of an edge */
    public static final String PATH_REVERSED = "sugiyama.reversed";

    /** path to the marked-attribute of an edge (used by BK) */
    public static final String PATH_BK_MARKED = "sugiyama.marked";

    /** sub-path to the reversed-attribute of an edge */
    public static final String SUBPATH_REVERSED = "reversed";

    /** path to the dist-attribute of a node - used in dfsdecycling */
    public static final String PATH_DIST = "sugiyama.dist";

    /** sub-path to the dist-attribute of a node */
    public static final String SUBPATH_DIST = "dist";

    /** sub-path to the attribute dfsNum */
    public static final String SUBPATH_DFSNUM = "dfsNum";

    /** sub-path to the completion-number in dfs */
    public static final String SUBPATH_COMPNUM = "compNum";

    /** path to the attribute topoPreds (toposort) */
    public static final String PATH_TOPOPREDS = "sugiyama.topoPreds";

    /** subpath to the attribute topo (for toposort) */
    public static final String SUBPATH_TOPO = "topo";

    /** sub-path to the attribute topoPreds */
    public static final String SUBPATH_TOPOPREDS = "topoPreds";

    /** sub-path to the attribute level - which level is the node on */
    public static final String SUBPATH_LEVEL = "level";

    /** path to the level-attribute */
    public static final String PATH_LEVEL = "sugiyama.level";

    /** path to the sugiyama-label of the node */
    public static final String PATH_LABEL = "sugiyama.identifier";

    /** sub-path to the sugiyama-label of the node */
    public static final String SUBPATH_LABEL = "identifier";

    /** path to the constraints-hashmap */
    public static final String PATH_CONSTRAINTS = "sugiyama.constraints";

    /** sub-path to the constraints-hashmap */
    public static final String SUBPATH_CONSTRAINTS = "constraints";

    /** path to the attribute that marks a node as a dummy-node */
    public static final String PATH_DUMMY = "sugiyama.isDummyNode";

    /** subpath to the attribute that marks a node as a dummy-node */
    public static final String SUBPATH_DUMMY = "isDummyNode";

    /** path to the sugiyama coordinate-attribute */
    public static final String PATH_COORDINATE = "sugiyama.coordinate";

    /** subpath to the sugiyama coordinate-attribute */
    public static final String SUBPATH_COORDINATE = "coordinate";

    /** Path to the CoffmanGraham number attribute of a node */
    public static final String PATH_CGNUM = "sugiyama.cgNum";

    /** Sub-path to the CoffmanGraham number attribute of a node */
    public static final String SUBPATH_CGNUM = "cgNum";

    /** Path to the in-degree value attribute of a node in CoffmanGraham */
    public static final String PATH_CGINDEGREE = "sugiyama.cgIndegree";

    /** Sub-path to the in-degree value attribute of a node in CoffmanGraham */
    public static final String SUBPATH_CGINDEGREE = "cgIndegree";

    /** Path to the placed attribute of a node in CoffmanGraham */
    public static final String PATH_CGPLACED = "sugiyama.placed";

    /** Sub-path to the placed attribute of a node in CoffmanGraham */
    public static final String SUBPATH_CGPLACED = "placed";

    /** Path to the layer value attribute of a node in CoffmanGraham */
    public static final String PATH_CGLAYER = "sugiyama.cgLayer";

    /** Sub-path to the layer value attribute of a node in CoffmanGraham */
    public static final String SUBPATH_CGLAYER = "cgLayer";

    /** Path to the SugiyamaNode attribute of a node in IncrementalSugiyama */
    public static final String PATH_INC_NODE = "sugiyama.incNode";

    /** Sub-path to the SugiyamaNode attribute of a node in IncrementalSugiyama */
    public static final String SUBPATH_INC_NODE = "incNode";

    /** Path to the SugiyamaEdge attribute of an edge in IncrementalSugiyama */
    public static final String PATH_INC_EDGE = "sugiyama.incEdge";

    /** Sub-path to the SugiyamaEdge attribute of an edge in IncrementalSugiyama */
    public static final String SUBPATH_INC_EDGE = "incEdge";

    // ------------------------------------------------------------------------//
    // Default-Settings //
    // ------------------------------------------------------------------------//

    /** The default decycling-algorithm (complete binary-name) */
    public final String DEFAULT_DECYCLING_ALGORITHM = "org.graffiti.plugins."
            + "algorithms.sugiyama.decycling.DFSDecycling";

    /** The default levelling-algorithm (complete binary-name) */
    public final String DEFAULT_LEVELLING_ALGORITHM = "org.graffiti.plugins."
            + "algorithms.sugiyama.levelling.LongestPath";

    /** The default crossmin-algorithm (complete binary-name) */
    public final String DEFAULT_CROSSMIN_ALGORITHM = "org.graffiti.plugins."
            + "algorithms.sugiyama.crossmin.BaryCenter";

    /** The default layout-algorithm (complete binary-name) */
    public final String DEFAULT_LAYOUT_ALGORITHM = "org.graffiti.plugins."
            + "algorithms.sugiyama.layout.BrandesKoepfWrapper";

    // ------------------------------------------------------------------------//
    // Keys in the preferences for sugiyama //
    // ------------------------------------------------------------------------//

    /** key to the number of available decycling-algorithms */
    public final String KEY_NUM_DECYCLING_ALGORITHMS = "numDecyclingAlgorithms";

    /** key to the number of available levelling-algorithms */
    public final String KEY_NUM_LEVELLING_ALGORITHMS = "numLevellingAlgorithms";

    /** key to the number of available crossmin-algorithms */
    public final String KEY_NUM_CROSSMIN_ALGORITHMS = "numCrossMinAlgorithms";

    /** key to the number of available layout-algorithms */
    public final String KEY_NUM_LAYOUT_ALGORITHMS = "numLayoutAlgorithms";

    /** key-prefix of a decycling-algorith */
    public final String KEY_PREFIX_DECYCLING = "decyclingAlgorithm";

    /** key-prefix of a levelling-algorithm */
    public final String KEY_PREFIX_LEVELLING = "levellingAlgorithm";

    /** key-prefix of a crossmin-algorithm */
    public final String KEY_PREFIX_CROSSMIN = "crossMinAlgorithm";

    /** key-prefix of a layout-algorithm */
    public final String KEY_PREFIX_LAYOUT = "layoutAlgorithm";

    /** key to the last selected decycling-algorithm */
    public final String KEY_SELECTED_DECYCLING = "selectedDecyclingAlgorithm";

    /** key to the last selected levelling-algorithm */
    public final String KEY_SELECTED_LEVELLING = "selectedLevellingAlgorithm";

    /** key to the last selected crossmin-algorithm */
    public final String KEY_SELECTED_CROSSMIN = "selectedCrossMinAlgorithm";

    /** key to the last selected layout-algorithm */
    public final String KEY_SELECTED_LAYOUT = "selectedLayoutAlgorithm";

    public final String KEY_FRAMEWORK_ANIMATED = "frameworkAnimated";

    public final String KEY_FRAMEWORK_DRAWING = "frameworkDrawing";

    public final String KEY_FRAMEWORK_BIG_NODE_POLICY = "frameworkBigNodePolicy";

    public final String KEY_FRAMEWORK_CONSTRAINT_POLICY = "frameworkConstraintPolicy";

    public final String KEY_FRAMEWORK_GRID_TYPE = "gridType";

    // ------------------------------------------------------------------------//
    // Misc constants //
    // ------------------------------------------------------------------------//

    /** Prefix of the unique identifier for a node */
    public final String PREFIX_SUGIYAMA_NODE_LABEL = "sugiyamaNode";

    /** Prefix of a constraint */
    public final String PREFIX_CONSTRAINT = "sugiyamaConstraint";

    public final int DEFAULT_ANIMATION_POLICY = 0;

    /** Policies on how to handle big nodes */
    public final int BIG_NODES_HANDLE = 0;

    public final int BIG_NODES_SHRINK = 1;

    public final int BIG_NODES_IGNORE = 2;

    public final int DEFAULT_BIG_NODE_POLICY = 2;

    /** Policies on how to handle constraints */
    public final int CONSTRAINTS_HANDLE = 0;

    public final int CONSTRAINTS_IGNORE = 1;

    public final int DEFAULT_CONSTRAINT_POLICY = 1;

    /** Default width of a node */
    public final int DEFAULT_NODE_WIDTH = GraphicAttributeConstants.DEFAULT_NODE_SIZE.width;

    /** Default height of a node */
    public final int DEFAULT_NODE_HEIGHT = GraphicAttributeConstants.DEFAULT_NODE_SIZE.height;

    // ------------------------------------------------------------------------//
    // Constraint-identifiers //
    // ------------------------------------------------------------------------//

    public final String CONSTRAINT_HORIZONTAL_TWO_NODES = "HORIZONTAL_TWO_NODES_";

    public final String CONSTRAINT_VERTICAL_TWO_NODES = "VERTICAL_TWO_NODES_";

    public final String CONSTRAINT_ABOVE = "ABOVE_";

    public final String CONSTRAINT_BELOW = "BELOW_";

    public final String CONSTRAINT_EQUAL_Y = "EQUAL_Y_";

    public final String CONSTRAINT_NONEQUAL_Y = "NONEQUAL_Y";

    public final String CONSTRAINT_LEFT_OF = "LEFT_OF_";

    public final String CONSTRAINT_RIGHT_OF = "RIGHT_OF_";

    public final String CONSTRAINT_EQUAL_X = "EQUAL_X_";

    public final String CONSTRAINT_NONEQUAL_X = "NONEQUAL_X";

    public final String CONSTRAINT_MANDATORY = "MANDATORY_";

    // ------------------------------------------------------------------------//
    // String-parameters //
    // ------------------------------------------------------------------------//

    public final String PARAM_HORIZONTAL_SUGIYAMA = "horizontal";

    public final String PARAM_RADIAL_SUGIYAMA = "radial";

    public final String PARAM_CYCLIC_SUGIYAMA = "cyclic";

    public final String PARAM_BIG_NODE_SHRINK = "Shrink big nodes to default size";

    public final String PARAM_BIG_NODE_IGNORE = "Ignore big nodes";

    public final String PARAM_BIG_NODE_HANDLE = "Process big nodes";

    public final String PARAM_CONSTRAINTS_IGNORE = "Ignore constraints";

    public final String PARAM_CONSTRAINTS_HANDLE = "Satisfy constraints";

    public final String PARAM_GRID_NONE = "none";

    public final String PARAM_GRID_ORTHOGONAL = "orthogonal grid";

    public final String PARAM_GRID_RADIAL = "radial grid";

    public final String GRID_CLASSNAME_ORTHOGONAL = "org.graffiti.plugins.grids.OrthogonalGrid";

    public final String GRID_CLASSNAME_RADIAL = "org.graffiti.plugins.grids.RadialGrid";

    public final String GRID_CLASSNAME_NONE = "org.graffiti.plugin.view.NoGrid";

    // ------------------------------------------------------------------------//
    // Edge locating priority list strings for SCCDecycling
    // ------------------------------------------------------------------------//

    /**
     * PrioityList1: (MIN Out-Degree Sources, MIN In-Degree Targets, MAX
     * In-Degree Sources, MAX Out-Degree Targets)
     */
    public static final String PRIORITY1 = "1: MIN Out-Degree Sources, "
            + "MIN In-Degree Targets, MAX In-Degree Sources, "
            + "MAX Out-Degree Targets";

    /**
     * PriorityList2: (MIN Out-Degree Sources, MIN In-Degree Targets, MAX
     * Out-Degree Targets, MAX In-Degree Sources)
     */
    public static final String PRIORITY2 = "2: MIN Out-Degree Sources, "
            + "MIN In-Degree Targets, MAX Out-Degree Targets, "
            + "MAX In-Degree Sources";

    /**
     * PriorityList3: (MIN In-Degree Targets, MIN Out-Degree Sources, MAX
     * In-Degree Sources, MAX Out-Degree Targets)
     */
    public static final String PRIORITY3 = "3: MIN In-Degree Targets, "
            + "MIN Out-Degree Sources, MAX In-Degree Sources, "
            + "MAX Out-Degree Targets";

    /**
     * PriorityList4: (MIN In-Degree Targets, MIN Out-Degree Sources, MAX
     * Out-Degree Targets, MAX In-Degree Sources)
     */
    public static final String PRIORITY4 = "4: MIN In-Degree Targets, "
            + "MIN Out-Degree Sources, MAX Out-Degree Targets, "
            + "MAX In-Degree Sources";

    /** PriorityList5: Random edge */
    public static final String PRIORITY5 = "5: Random edge";

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
