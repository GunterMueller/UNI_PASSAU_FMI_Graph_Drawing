package org.graffiti.plugins.algorithms.core;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;

/**
 * Draws the graph radial according to given coordinates and offsets of the
 * edges. Intra-level edges are drawn as circle segment and inter-level edges
 * are drawn as spiral segment.
 * 
 * @author Matthias H�llm�ller
 * 
 */
@SuppressWarnings("nls")
public class Drawing {

    /**
     * the graph
     */
    private Graph graph;

    /**
     * stores the level of each node
     */
    private HashMap<Node, Integer> leveling = new HashMap<Node, Integer>();

    /**
     * stores the order of each level
     */
    private LinkedList<Node>[] order;

    /**
     * the left border of the drawing
     */
    private static final double LEFT_BORDER = 80;

    /**
     * the upper border of the drawing
     */
    private static final double UPPER_BORDER = 80;

    /**
     * the minimum node distance
     */
    private static int MIN_NODE_DIST = 80;

    /**
     * the minimum radius
     */
    private static double MIN_RADIUS = 1;

    /**
     * the minimum level distance
     */
    private static double LEVEL_DIST = 1;

    /**
     * the constructor
     * 
     * @param graph
     *            the graph
     * @param order
     *            order of the nodes
     */
    public Drawing(Graph graph, LinkedList<Node>[] order) {
        this.graph = graph;
        this.order = order;
    }

    /**
     * Draws the graph radial according to given coordinates of the nodes and
     * offsets of the edges. Intra-level edges are drawn as circle segment and
     * inter-level edges are drawn as spiral segment.
     * 
     * @param coordinates
     *            the x-coordinates
     * @param offset
     *            the offsets of the edges
     */
    public void drawRadial(HashMap<Node, Integer> coordinates,
            HashMap<Edge, Integer> offset) {

        // get parameters
        Params p = new Params();
        MIN_RADIUS = p.getMinRadius();
        LEVEL_DIST = p.getLevelDist();

        // store offset to each edge
        for (Edge e : offset.keySet()) {
            e.setInteger(GraphicAttributeConstants.OFFSET, offset.get(e));
        }

        // initialize HashMap to store level numbers
        for (int i = 0; i < this.order.length; i++) {
            for (Node n : this.order[i]) {
                this.leveling.put(n, i);
                n.setInteger(GraphicAttributeConstants.LEVEL, i);
            }
        }

        HashMap<Node, Double> radialX = new HashMap<Node, Double>();
        HashMap<Node, Double> radialY = new HashMap<Node, Double>();

        // get the largest horizontal distance between two vertices on the same
        // level i
        double z = 0; // largest horizontal distance on level i plus delta
        for (int i = 1; i <= this.order.length; ++i) {
            int min = Integer.MAX_VALUE;
            int max = 0;
            for (int j = 0; j < this.order[i - 1].size(); ++j) {
                Node n = this.order[i - 1].get(j);
                if (coordinates.get(n) > max) {
                    max = coordinates.get(n);
                }
                if (coordinates.get(n) < min) {
                    min = coordinates.get(n);
                }
            }
            if (max - min >= z) {
                z = max - min + 1;
            }
        }

        // just one node in first level - start with second
        int start = 0;
        if (this.order[0].size() == 1) {
            start = 1;
        }

        // compute x and y coordinates for a radial drawing and store them in
        // radialX and radialY
        double radius = MIN_RADIUS;
        for (int i = start; i < this.order.length; i++) {

            // all nodes on a higher level than maximum level are drawn on the
            // outer circle, which has double distance to next level for
            // visualization effect
            // if (i >= p.getMaxLevel() - 1) {
            // radius = p.getMaxLevel();
            // }

            // compute coordinates
            for (int j = 0; j < this.order[i].size(); j++) {
                Node n = this.order[i].get(j);
                radialX.put(n, radius
                        * Math.cos(2 * Math.PI / z * coordinates.get(n)));
                radialY.put(n, radius
                        * Math.sin(2 * Math.PI / z * coordinates.get(n)));
            }

            // increase radius after each level
            radius += LEVEL_DIST;
        }

        // just one node on first level - put it in the center
        if (this.order[0].size() == 1) {
            Node n = this.order[0].get(0);
            radialX.put(n, 0.0);
            radialY.put(n, 0.0);
        }

        // compute center coordinates and store center point
        double centerX = (0 - getMinValue(radialX)) * MIN_NODE_DIST
                + LEFT_BORDER; // min_node_dist
        double centerY = (0 - getMinValue(radialY)) * MIN_NODE_DIST
                + UPPER_BORDER;
        p.setCenter(new Point2D.Double(centerX, centerY));

        // // add new node in the center
        // Node c = this.graph.addNode();
        //
        // // set label to center node
        // LabelAttribute labelAttr = new NodeLabelAttribute("label");
        // labelAttr.setLabel("center");
        // c.addAttribute(labelAttr, "");
        // // set dimension of center node to 0
        // NodeGraphicAttribute nga = ((NodeGraphicAttribute) c.getAttributes()
        // .getAttribute(GraphicAttributeConstants.GRAPHICS));
        // DimensionAttribute dim = nga.getDimension();
        // dim.setHeight(5.0);
        // dim.setWidth(5.0);

        // align coordinates to the left upper corner
        alignCoordinates(radialX, radialY);

        // set the x and y coordinates
        for (Node n : coordinates.keySet()) {
            CoordinateAttribute ca = ((NodeGraphicAttribute) n.getAttributes()
                    .getAttribute(GraphicAttributeConstants.GRAPHICS))
                    .getCoordinate();
            double x = LEFT_BORDER + radialX.get(n) * MIN_NODE_DIST;
            double y = UPPER_BORDER + radialY.get(n) * MIN_NODE_DIST;
            ca.setCoordinate(new Point2D.Double(x, y));
        }

        // draw edges
        for (Edge e : this.graph.getEdges()) {

            // get incident nodes
            Node u = e.getSource();
            Node v = e.getTarget();

            // just one node on inner level - draw its edges as straight lines
            if ((this.order[0].contains(u) || this.order[0].contains(v))
                    && this.order[0].size() == 1) {
                e.changeString(GraphicAttributeConstants.SHAPE_PATH,
                        GraphicAttributeConstants.STRAIGHTLINE_CLASSNAME);

                // draw interlevel-edges as spiral segments - set start and end
                // angle for each edge
            } else if (this.leveling.get(u) != this.leveling.get(v)) {
                e.setDouble(GraphicAttributeConstants.START_ANGLE, Math.PI
                        / this.order[this.leveling.get(u)].size()
                        * (this.order[this.leveling.get(u)].indexOf(u) + 1));
                e.setDouble(GraphicAttributeConstants.END_ANGLE, Math.PI
                        / this.order[this.leveling.get(v)].size()
                        * (this.order[this.leveling.get(v)].indexOf(v) + 1));
                e.changeString(GraphicAttributeConstants.SHAPE_PATH,
                        GraphicAttributeConstants.SPIRAL_CLASSNAME);

                // draw intralevel-edges as quad curve
                // exception: on the inner circle draw them as straight lines
            } else {
                if (this.order[0].contains(u)) {
                    e.changeString(GraphicAttributeConstants.SHAPE_PATH,
                            GraphicAttributeConstants.STRAIGHTLINE_CLASSNAME);
                } else {
                    e.changeString(GraphicAttributeConstants.SHAPE_PATH,
                            GraphicAttributeConstants.INTRA_LEVEL_CLASSNAME);
                }
            }
        }
    }

    /**
     * draws the graph according to levels and given order (for testing)
     */
    public void drawHorizontal(LinkedList<Node>[] order,
            HashMap<Node, Integer> coordinates) {

        for (int y = 0; y < order.length; y++) {

            for (int i = 0; i < order[y].size(); i++) {
                Node n = order[y].get(i);
                int x = coordinates.get(n);
                setGridCoordinate(n, x, y, true);
            }
        }
    }

    /**
     * sets the grid coordinate of a node
     */
    private void setGridCoordinate(Node node, int x, int y, boolean up) {

        CoordinateAttribute ca = ((NodeGraphicAttribute) node.getAttributes()
                .getAttribute(GraphicAttributeConstants.GRAPHICS))
                .getCoordinate();

        double xCoord;
        double yCoord;

        if (up) {
            xCoord = MIN_NODE_DIST + x * MIN_NODE_DIST;
            yCoord = MIN_NODE_DIST + y * MIN_NODE_DIST;
        } else {
            xCoord = MIN_NODE_DIST + x * MIN_NODE_DIST;
            yCoord = MIN_NODE_DIST + MIN_NODE_DIST
                    * this.graph.getNumberOfNodes() / 2 - y * MIN_NODE_DIST;
        }

        ca.setCoordinate(new Point2D.Double(xCoord, yCoord));
    }

    /**
     * aligns the coordinates to the left upper corner
     * 
     * @param x
     *            x-coordinates
     * @param y
     *            y-coordinates
     */
    private void alignCoordinates(HashMap<Node, Double> x,
            HashMap<Node, Double> y) {

        double minX = getMinValue(x);
        double minY = getMinValue(y);
        for (Node n : x.keySet()) {
            x.put(n, x.get(n) - minX);
            y.put(n, y.get(n) - minY);

        }

    }

    /**
     * computes the minimum value
     * 
     * @return minimum value
     */
    private double getMinValue(HashMap<Node, Double> map) {

        double min = Double.MAX_VALUE;
        for (Double d : map.values()) {
            if (d < min) {
                min = d;
            }
        }
        return min;
    }

}
