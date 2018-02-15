// =============================================================================
//
//   SocialBrandesKoepf.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SocialBrandesKoepf.java 2276 2008-03-11 16:45:02Z brunner $

package org.graffiti.plugins.algorithms.sugiyama.layout.cyclic;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.graffiti.plugins.algorithms.sugiyama.layout.cyclic.Toolkit.collectLayers;
import static org.graffiti.plugins.algorithms.sugiyama.layout.cyclic.Toolkit.flipHorizontal;
import static org.graffiti.plugins.algorithms.sugiyama.layout.cyclic.Toolkit.flipVertical;
import static org.graffiti.plugins.algorithms.sugiyama.layout.cyclic.Toolkit.flipX;
import static org.graffiti.plugins.algorithms.sugiyama.layout.cyclic.Toolkit.level;
import static org.graffiti.plugins.algorithms.sugiyama.layout.cyclic.Toolkit.markType1Conflicts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.sugiyama.layout.LayoutAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * This is an enhanced implementation of the Brandes/Koepf-Algorithm for the
 * Sugiyama-Framework:<br>
 * In contrast to the original algorithm this one also supports cyclic graphs.
 * <p>
 * The original algorithm is described in the following publication:<br>
 * U. Brandes, B. K&ouml;pf, Fast and Simple Horizontal Coordinate Assignment.
 * In: P. Mutzel, M. J&uuml;ger, and S. Leipert (Eds.): GD 2001, LNCS 2265, pp.
 * 31-44, 2002, Springer 2002.
 * <p>
 * For a comprehensive description of this algorithm see the diploma thesis of
 * the author.
 * 
 * @author Raymund F&uuml;l&ouml;p
 */
public class CyclicBrandesKoepf extends AbstractAlgorithm implements
        LayoutAlgorithm {
    /** The name of the algorithm */
    private final String ALGORITHM_NAME = "Brandes/Koepf for cyclic graphs";
    /** The <code>SugiyamaData</code>-Bean */
    private SugiyamaData data;

    /** Distance from the center (0/0) to the innermost node */
    private int RADIUS_OFFSET = 50;
    /** Minimal distance between two nodes on a layer */
    private int NODE_DELTA = 50;
    /**
     * specifies whether blocks that have more nodes than layers should be
     * split.
     */
    boolean SPLIT_LONG_BLOCKS = true;

    /**
     * should the sugiyama attributes be removed from the graph?
     */
    boolean REMOVE_ATTRIBUTES = false;

    private int DIRECTION = ALL_DIRECTIONS;

    private boolean balanced = false;

    /**
     * constants for direction
     */
    private static final int DOWN = 1;
    private static final int RIGHT = 2;
    private static final int LEFT_UP = 0 + 0;
    private static final int LEFT_DOWN = 0 + DOWN;
    private static final int RIGHT_UP = RIGHT + 0;
    private static final int RIGHT_DOWN = RIGHT + DOWN;
    private static final int ALL_DIRECTIONS = 4;

    /** The logger */
    private static final Logger logger = Logger
            .getLogger(CyclicBrandesKoepf.class.getName());

    public boolean supportsArbitraryXPos() {
        return false;
    }

    /**
     * Performs the calculations for one of the four directions.
     */
    private Map<Node, Double> doOneDirection(int direction, Node[][] layers) {
        logger.log(Level.INFO, "****** direction = " + direction + " ******");

        // debug
        // correlateLayersWithXpos(layers);

        if ((direction & RIGHT) == RIGHT) {
            flipHorizontal(layers);
        }
        if ((direction & DOWN) == DOWN) {
            flipVertical(layers);
        }

        // vertical alignment
        Set<Block> blocks = new VerticalAlignment().execute(data, layers,
                SPLIT_LONG_BLOCKS, balanced);

        // debug
        // if (direction == DIRECTION) paintBlocks(blocks);

        // horizontal compaction
        HorizontalCompaction.execute(blocks, layers);
        Map<Node, Double> x = collectCoordinates(blocks);

        // debug
        // correlateLayersWithXpos(layers);
        // correlateLayersWithX(layers, x);

        // debug
        // if (DIRECTION == ALL_DIRECTIONS) unPaintBlocks();

        if ((direction & DOWN) == DOWN) {
            flipVertical(layers);
        }
        if ((direction & RIGHT) == RIGHT) {
            flipHorizontal(layers);
            flipX(x);
        }

        // correlateLayersWithXpos(layers);
        // correlateLayersWithX(layers, x);

        return x;
    }

    // private void correlateLayersWithX(Node[][] layers, Map<Node, Double> x)
    // {
    // for (Node[] layer : layers)
    // for (int i = 1; i < layer.length; i++)
    // {
    // Node left = layer[i-1];
    // Node right = layer[i];
    // if (x.get(left) + 0.999 > x.get(right))
    // {
    // logger.log(Level.INFO, "Nodes too close (" + x.get(left) +
    // " <-> " + x.get(right) + "):");
    // logger.log(Level.INFO, getNodeLabel(left) + " <-> ");
    // logger.log(Level.INFO, getNodeLabel(right));
    // }
    // }
    // }
    //
    // private void correlateLayersWithXpos(Node[][] layers)
    // {
    // for (Node[] layer : layers)
    // for (int i = 1; i < layer.length; i++)
    // {
    // Node left = layer[i-1];
    // Node right = layer[i];
    // if (xpos(left) + 1 != xpos(right))
    // {
    // logger.log(Level.INFO, "index in \"layers\" (" + (i-1) + " <-> "
    // + i + ") doesn't match xpos (" + xpos(left) +
    // " <-> " + xpos(right) + "):");
    // logger.log(Level.INFO, getNodeLabel(left) + " <-> ");
    // logger.log(Level.INFO, getNodeLabel(right));
    // }
    // }
    // }

    /**
     * This method executes the algorithm - Initialize the data-structures, call
     * the Brandes/Koepf-Implementation and update the coordinates of the nodes
     * according to the relative coordinates returned from the
     * Brandes/Koepf-Implemenatation and user-defined offsets
     */
    public void execute() {
        graph.getListenerManager().transactionStarted(this);

        // initialize some data
        DebugToolkit.reset();
        Node[][] layers = collectLayers(data);

        // type 1 conflicts are valid for all 4 directions:
        markType1Conflicts(layers);

        // debug
        // paintMarkedEdges(graph);

        @SuppressWarnings("unchecked")
        Map<Node, Double>[] xs = new Map[4];

        // perform the layouting for all 4 directions
        xs[LEFT_UP] = doOneDirection(LEFT_UP, layers);
        xs[LEFT_DOWN] = doOneDirection(LEFT_DOWN, layers);
        xs[RIGHT_UP] = doOneDirection(RIGHT_UP, layers);
        xs[RIGHT_DOWN] = doOneDirection(RIGHT_DOWN, layers);

        // debug: undo debug markings
        // if (DIRECTION == ALL_DIRECTIONS) unPaintMarkedEdges();

        // merge the 4 layouts
        Map<Node, Double> x = balance(xs);

        // debug
        for (Node[] layer : layers) {
            for (int i = 1; i < layer.length; i++) {
                Node left = layer[i - 1];
                Node right = layer[i];
                if (x.get(left) + 0.999 > x.get(right)) {
                    logger.log(Level.FINE, "Nodes too close (" + x.get(left)
                            + " <-> " + x.get(right) + "):");
                    logger.log(Level.FINE, DebugToolkit.getNodeLabel(left)
                            + " <-> ");
                    logger.log(Level.FINE, DebugToolkit.getNodeLabel(right));
                    logger.log(Level.FINE, xs[0].get(left) + ", ");
                    logger.log(Level.FINE, xs[1].get(left) + ", ");
                    logger.log(Level.FINE, xs[2].get(left) + ", ");
                    logger.log(Level.FINE, xs[3].get(left) + " <-> ");
                    logger.log(Level.FINE, xs[0].get(right) + ", ");
                    logger.log(Level.FINE, xs[1].get(right) + ", ");
                    logger.log(Level.FINE, xs[2].get(right) + ", ");
                    logger.log(Level.FINE, xs[3].get(right) + "");
                }
            }
        }

        // transfer the layout to the real graph coordinates
        updateGraph(x);

        // remove all sugiyama attributes from the graph
        if (REMOVE_ATTRIBUTES) {
            for (Node node : graph.getNodes()) {
                node.removeAttribute("sugiyama");
            }
            for (Edge edge : graph.getEdges()) {
                edge.removeAttribute("sugiyama");
            }
        }

        graph.getListenerManager().transactionFinished(this);
    }

    /**
     * Collects the x coordinates stored in the blocks and fills a map which
     * assigns an x coordinate to each single node.
     */
    private Map<Node, Double> collectCoordinates(Set<Block> blocks) {
        Map<Node, Double> result = new HashMap<Node, Double>(graph
                .getNumberOfNodes());

        for (Block block : blocks) {
            for (int i = 0; i < block.nodes.length; i++) {
                result.put(block.nodes[i], block.x + block.inclination * i);
            }
        }

        return result;
    }

    /**
     * Aligns the assignments to the one with smallest width and computes the
     * average median.
     */
    private Map<Node, Double> balance(Map<Node, Double>[] xs) {

        logger.log(Level.INFO, "****** balance ******");

        // The x-coordinates for each node
        Map<Node, Double> x = new HashMap<Node, Double>();

        double minWidth = Double.MAX_VALUE;
        int smallestWidthLayout = 0;
        double[] min = new double[4];
        double[] max = new double[4];

        // get the layout with smallest width and set minimum and maximum value
        // for each direction
        for (int dir = 0; dir <= 3; ++dir) {
            min[dir] = Integer.MAX_VALUE;
            max[dir] = Integer.MIN_VALUE;
            for (double xValue : xs[dir].values()) {
                if (xValue < min[dir]) {
                    min[dir] = xValue;
                }
                if (xValue > max[dir]) {
                    max[dir] = xValue;
                }
            }
            double width = max[dir] - min[dir];
            if (width < minWidth) {
                minWidth = width;
                smallestWidthLayout = dir;
            }
        }

        // align the layouts to the one with smallest width
        for (int dir = 0; dir <= 3; ++dir) {

            if (dir == smallestWidthLayout) {
                continue;
            }

            double diff;
            if (dir == LEFT_UP || dir == LEFT_DOWN) {
                // align the left to right layouts to the left border of the
                // smallest layout
                diff = min[smallestWidthLayout] - min[dir];
            } else {
                // align the right to left layouts to the right border of
                // the smallest layout
                diff = max[smallestWidthLayout] - max[dir];
            }

            if (diff == 0) {
                continue;
            }

            for (Node n : xs[dir].keySet()) {
                xs[dir].put(n, xs[dir].get(n) + diff);
            }
        }

        // get the average median of each coordinate
        for (Node n : this.graph.getNodes()) {
            double[] values = new double[4];
            for (int dir = 0; dir < 4; dir++) {
                values[dir] = xs[dir].get(n);
            }
            Arrays.sort(values); // values.length == 4 -> no runtime problem
            // double finalCoordinate = (values[1] + values[2]) / 2.0;
            double finalCoordinate = (values[0] + values[1] + values[2] + values[3]) / 4.0;
            if (DIRECTION == ALL_DIRECTIONS) {
                x.put(n, finalCoordinate);
            } else {
                x.put(n, xs[DIRECTION].get(n));
            }
        }

        // move all coordinates to the left (so that leftmost x == 0)
        double minValue = Double.MAX_VALUE;
        for (double c : x.values())
            if (c < minValue) {
                minValue = c;
            }
        if (minValue != 0) {
            for (Node n : x.keySet()) {
                x.put(n, x.get(n) - minValue);
            }
        }

        return x;
    }

    /**
     * This method reads the fields <code>x</code> and <code>level</code>,
     * calculates each node's coordinates and stores those coordinates in each
     * node's "sugiyama.coordinate" attribute.
     * <p>
     * Horizontal and cyclic layout are distinguished.
     */
    private void updateGraph(Map<Node, Double> x) {
        if (data.getAlgorithmType().equals(
                SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA)) {
            updateGraphCyclic(x);
        }
        if (data.getAlgorithmType().equals(
                SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA)) {
            updateGraphHorizontal(x);
        }
    }

    /**
     * This method creates regular (horizontal) coordinates.
     */
    private void updateGraphHorizontal(Map<Node, Double> x) {
        // for each layer
        for (int i = 0; i < data.getLayers().getNumberOfLayers(); i++) {
            ArrayList<Node> layer = data.getLayers().getLayer(i);

            // for each node of current layer
            for (int j = 0; j < layer.size(); j++) {
                Node node = layer.get(j);
                if (node.containsAttribute(SugiyamaConstants.PATH_COORDINATE)) {
                    node.removeAttribute(SugiyamaConstants.PATH_COORDINATE);
                }
                CoordinateAttribute ca = new CoordinateAttribute("coordinate",
                        x.get(node) * NODE_DELTA, level(node) * RADIUS_OFFSET);
                node.addAttribute(ca, SugiyamaConstants.PATH_SUGIYAMA);
                // ca = new CoordinateAttribute(
                // "coordinate",
                // x.get(node) * NODE_DELTA,
                // level(node) * RADIUS_OFFSET);
                // node.removeAttribute("graphics.coordinate");
                // node.addAttribute(ca, "graphics");
            }
        }
    }

    /**
     * This method creates cyclic coordinates.
     */
    private void updateGraphCyclic(Map<Node, Double> x) {

        double angleBetweenLayers = -(2 * PI)
                / data.getLayers().getNumberOfLayers();

        // for each layer
        for (int i = 0; i < data.getLayers().getNumberOfLayers(); i++) {
            ArrayList<Node> layer = data.getLayers().getLayer(i);

            // for each node of current layer
            for (int j = 0; j < layer.size(); j++) {
                Node node = layer.get(j);
                if (x.containsKey(node)) {
                    double radius = RADIUS_OFFSET + (x.get(node) * NODE_DELTA);
                    if (node
                            .containsAttribute(SugiyamaConstants.PATH_COORDINATE)) {
                        node.removeAttribute(SugiyamaConstants.PATH_COORDINATE);
                    }
                    CoordinateAttribute ca = new CoordinateAttribute(
                            "coordinate", cos(i * angleBetweenLayers) * radius,
                            sin(i * angleBetweenLayers) * radius);
                    node.addAttribute(ca, SugiyamaConstants.PATH_SUGIYAMA);
                }
            }
        }
    }

    @Override
    public void reset() {
        // we don't do much here as each algorithm initializes its own data
        // structures
        super.reset();
    }

    /**
     * Return the supported parameters of this algorithm. The user can define
     * the minimal distance to the center and the spacing between adjacent
     * nodes.
     * 
     * @return Returns a <code>Parameter[]</code> of supported parameters
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        IntegerParameter p1 = new IntegerParameter(RADIUS_OFFSET, 1, 1000,
                "Distance to center",
                "Set the minimal distance of a node to the center.");
        IntegerParameter p2 = new IntegerParameter(NODE_DELTA, 1, 1000,
                "Node spacing",
                "Set the minimal distance between two nodes on the same level.");
        BooleanParameter p3 = new BooleanParameter(SPLIT_LONG_BLOCKS,
                "Split long blocks", "Split blocks that contain more nodes"
                        + " than there are levels.");
        /*
         * IntegerParameter p4 = new IntegerParameter(DIRECTION, 0,
         * ALL_DIRECTIONS, "Direction",
         * "Use only direction 0,1,2 or 3 (4 -> all 4 directions).");
         */
        String[] align = { "Right top run", "Right bottom run", "Left top run",
                "Left bottom run", "All 4 runs",
                "One balanced run, compaction right first",
                "One balanced run, compaction left first" };
        StringSelectionParameter p4 = new StringSelectionParameter(align,
                "Alignment", "Choose the vertical alignment run");
        p4.setSelectedValue(4);
        BooleanParameter p5 = new BooleanParameter(REMOVE_ATTRIBUTES,
                "Clean up",
                "Remove the attributes \"sugiyama.*\" when finished.");

        this.parameters = new Parameter[] { p1, p2, p3, p4, p5 };
        return this.parameters;
    }

    /**
     * Apply the configured parameters to the algorithm
     * 
     * @param params
     *            Parameters of the algorithm
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        RADIUS_OFFSET = ((IntegerParameter) params[0]).getValue();
        NODE_DELTA = ((IntegerParameter) params[1]).getValue();
        SPLIT_LONG_BLOCKS = ((BooleanParameter) params[2]).getValue();
        DIRECTION = ((StringSelectionParameter) params[3]).getSelectedIndex();
        if (DIRECTION > 4) {
            DIRECTION -= 5;
            balanced = true;
        } else {
            balanced = false;
        }
        REMOVE_ATTRIBUTES = ((BooleanParameter) params[4]).getValue();
        data.setCyclicLayoutRadiusOffset(RADIUS_OFFSET);
        data.setCyclicLayoutRadiusDelta(NODE_DELTA);
    }

    public SugiyamaData getData() {
        return this.data;
    }

    public void setData(SugiyamaData theData) {
        this.data = theData;
    }

    public String getName() {
        return this.ALGORITHM_NAME;
    }

    public boolean supportsBigNodes() {
        return false;
    }

    public boolean supportsConstraints() {
        return false;
    }

    public boolean supportsAlgorithmType(String algorithmType) {
        return algorithmType.equals(SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA)
                || algorithmType
                        .equals(SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
