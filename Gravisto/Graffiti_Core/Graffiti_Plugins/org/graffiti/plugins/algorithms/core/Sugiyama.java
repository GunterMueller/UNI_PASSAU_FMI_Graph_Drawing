package org.graffiti.plugins.algorithms.core;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;

/**
 * draws the graph with a radial layout for a given layering of the nodes
 * 
 * @author Matthias H�llm�ller
 * 
 */
public class Sugiyama {

    /**
     * the graph
     */
    private Graph graph;

    /**
     * the leveling
     */
    private HashMap<Node, Integer> level;

    /**
     * the order of the nodes
     */
    private LinkedList<Node>[] order;

    /**
     * the maximum level
     */
    private Integer maxLevel;

    /**
     * store all the dummy nodes
     */
    private HashSet<Node> dummies = new HashSet<Node>();

    /**
     * the constructor
     * 
     * @param graph
     *            the graph
     * @param level
     *            the leveling
     */
    public Sugiyama(Graph graph, HashMap<Node, Integer> level) {
        this.graph = graph;
        this.level = level;
    }

    /**
     * draws the graph with a radial layout for the given layering of the nodes
     */
    @SuppressWarnings("unchecked")
    public void execute() {

        // create an initial order
        this.order = createOrder();

        // set the color of the nodes according to their level number
        setColor();

        // make the graph proper - insert dummies
        insertDummies();

        // computes the order with minimum crossings and according offsets
        CrossingReduction cR = new CrossingReduction(this.graph, this.order);
        HashMap<Edge, Integer> offset = cR.minimizeCrossings();

        // get the horizontal coordinates for the current order
        CoordinateAssignment ca = new CoordinateAssignment(this.graph,
                this.order, offset, this.dummies);
        HashMap<Node, Integer> xCoordinates = ca.getCoordinates();

        // draws the graph radial according to the calculated coordinates and
        // offsets
        Drawing d = new Drawing(this.graph, this.order);
        d.drawRadial(xCoordinates, offset);

        // reset the parameters
        Params p = new Params();
        p.setDefault();
    }

    /**
     * get the maximum level number
     * 
     * @return maximum level number
     */
    private int getMaxLevel() {
        int max = 0;
        for (Integer i : this.level.values()) {
            if (i > max) {
                max = i;
            }
        }
        return max;
    }

    /**
     * make the graph proper - insert dummy nodes and according edges for edges
     * spanning more than one level
     */
    private void insertDummies() {

        // get all edges
        Collection<Edge> edges = this.graph.getEdges();
        LinkedList<Edge> edgesTemp = new LinkedList<Edge>();
        edgesTemp.addAll(edges);

        for (Edge e : edgesTemp) {

            // get source and target node
            Node source = e.getSource();
            Node target = e.getTarget();

            // get source and target level
            int sourceLevel = this.level.get(source);
            int targetLevel = this.level.get(target);

            // if the edge spans more than one level
            if (Math.abs(sourceLevel - targetLevel) > 1) {

                // define start and endlevel (from smaller to higher level)
                int start = targetLevel;
                int end = sourceLevel;
                Node currentNode = target;
                Node endNode = source;
                if (sourceLevel < targetLevel) {
                    start = sourceLevel;
                    end = targetLevel;
                    currentNode = source;
                    endNode = target;
                }

                // insert dummy nodes with according level number and new edges
                for (int i = start + 1; i < end; i++) {

                    // add new node
                    Node n = this.graph.addNode();

                    // set dimension of dummy nodes to 0
                    NodeGraphicAttribute nga = ((NodeGraphicAttribute) n
                            .getAttributes().getAttribute(
                                    GraphicAttributeConstants.GRAPHICS));
                    DimensionAttribute dim = nga.getDimension();
                    dim.setHeight(3.0);
                    dim.setWidth(3.0);

                    // add the node to the current level
                    this.order[i].add(n);

                    // set level number to the node
                    this.level.put(n, i);

                    // add node to dummy list
                    this.dummies.add(n);

                    // add new edge
                    this.graph.addEdge(currentNode, n, false);
                    currentNode = n;

                }
                // add final edge
                this.graph.addEdge(currentNode, endNode, false);

                // delete old edge
                this.graph.deleteEdge(e);
            }
        }
    }

    /**
     * sets the color of the nodes according to their level number
     */
    private void setColor() {

        // define colors - 10 are enough because the number of levels is
        // constrained to 10
        Color[] colors = new Color[10];
        colors[0] = new Color(255, 0, 0);
        colors[1] = new Color(255, 100, 0);
        colors[2] = new Color(255, 200, 0);
        colors[3] = new Color(255, 255, 0);
        colors[4] = new Color(150, 255, 0);
        colors[5] = new Color(0, 255, 0);
        colors[6] = new Color(0, 255, 150);
        colors[7] = new Color(0, 255, 255);
        colors[8] = new Color(0, 120, 255);
        colors[9] = new Color(0, 0, 255);

        for (int i = 0; i < this.order.length; ++i) {
            for (Node n : this.order[i]) {

                // get the color attribute
                ColorAttribute ca = (ColorAttribute) n
                        .getAttribute(GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.FILLCOLOR);

                // set the color according to the level direction
                Params p = new Params();
                if (p.getDirection() || !p.getCore()) {
                    ca.setColor(colors[i * colors.length / this.order.length]);
                } else {
                    ca.setColor(colors[(colors.length - 1) - i * colors.length
                            / this.order.length]);
                }
            }
        }
    }

    /**
     * creates a array of LinkedLists - containing nodes for each level
     */
    @SuppressWarnings("unchecked")
    private LinkedList[] createOrder() {

        this.maxLevel = getMaxLevel() + 1;

        // array of LinkedLists containing nodes - one for each level
        LinkedList<Node>[] orderTemp = new LinkedList[this.maxLevel + 1];

        // initialize LinkedLists
        for (int i = 0; i <= this.maxLevel; i++) {
            orderTemp[i] = new LinkedList<Node>();
        }

        // add the nodes to the lists according to their level-number
        for (Node n : this.graph.getNodes()) {

            // direction? - highest level in center or on outer circle
            Params p = new Params();
            if (p.getDirection()) {
                orderTemp[this.maxLevel - this.level.get(n)].add(n);
            } else {
                orderTemp[this.level.get(n)].add(n);
            }
        }

        // count levels with elements
        int countLevels = 0;
        for (int i = 0; i < orderTemp.length; i++) {
            if (!orderTemp[i].isEmpty()) {
                countLevels++;
            }
        }

        LinkedList<Node>[] order = new LinkedList[countLevels];

        // remove empty levels
        countLevels = 0;
        for (int i = 0; i < orderTemp.length; i++) {
            if (!orderTemp[i].isEmpty()) {
                order[countLevels] = orderTemp[i];
                countLevels++;
            }
        }

        // set the level number according to the order
        for (int i = 0; i < order.length; i++) {
            for (Node n : order[i]) {
                this.level.put(n, i);
            }
        }

        return order;
    }

    /**
     * just set the color of the nodes
     */
    @SuppressWarnings("unchecked")
    public void justColor() {

        // create an initial order
        this.order = createOrder();

        // set the color of the nodes according to their level number
        setColor();
    }

}
