package org.graffiti.plugins.algorithms.mnn;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.editor.MainFrame;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.connectivity.Fourconnect;
import org.graffiti.plugins.algorithms.planarity.PlanarityAlgorithm;
import org.graffiti.plugins.algorithms.planarity.TestedGraph;

/**
 * This class implements the original version of the algorithm of Miuara, Nakano
 * and Nishizeki. It also implements an extended version for triangulated an not
 * triangulated graphs.
 * 
 * @author Thomas Ortmeier
 * @version $Revision: 5766 $ $Date: 2010-05-07 19:21:40 +0200 (Fr, 07 Mai 2010)
 *          $
 */
public class MnnAlgorithm extends AbstractAlgorithm {

    // The embedded graph
    private EmbeddedGraph embeddedGraph;

    // The 4-canonical ordering of the nodes
    private CanonicalOrdering canonicalOrdering;

    // grid size parameter
    private IntegerParameter gridParameter;
    private int gridSize;

    // zoom parameter
    private BooleanParameter zoomParameter;
    private boolean zoom;

    // lazy parameter (shifts nodes only when its nesessary)
    private BooleanParameter lazyParameter;
    private boolean lazy;

    // extended version parameter (for not triangulated graphs)
    private BooleanParameter extendedParameter;
    private boolean extended;

    // the boundary of G
    private LinkedList<Node> boundary = null;

    // several "special" nodes
    private Node wl = null;
    private Node wr = null;
    private Node ws = null;
    private Node wl1 = null;
    private Node wr1 = null;

    // the maximum x-coordinate of G'
    private int ymax = 0;

    // all the nodes
    private HashMap<Node, HashSet<Node>> underSet = null;

    // the temporary boundary of the graph
    private LinkedList<Node> tmpBoundary = null;

    // height of the graph
    private int height = 0;

    // width of the graph
    private int width = 0;

    /**
     * Constructor
     */
    public MnnAlgorithm() {
        super();
    }

    /**
     * Returns the Name of the Algorithm
     */
    public String getName() {
        return "Algorithmus von Miura, Nakano, Nishizeki";
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    public Parameter<?>[] getAlgorithmParameters() {
        gridParameter = new IntegerParameter(new Integer(60), new Integer(30),
                new Integer(100), "Grid size",
                "This value is meant for the distance between two nodes.");

        lazyParameter = new BooleanParameter(true, "Lazy",
                "Lazy Version for triangulatd graphs");

        extendedParameter = new BooleanParameter(true, "Extended Version",
                "Extended Version for not triangulated graphs");

        zoomParameter = new BooleanParameter(true, "Zoom Graph",
                "Zoom Graph to fit screen.");

        return new Parameter[] { gridParameter, lazyParameter,
                extendedParameter, zoomParameter };
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(org.graffiti.plugin.parameter.Parameter[])
     */
    public void setAlgorithmParameters(Parameter<?>[] parameters) {
        gridSize = ((IntegerParameter) parameters[0]).getInteger().intValue();
        zoom = ((BooleanParameter) parameters[3]).getBoolean().booleanValue();
        lazy = ((BooleanParameter) parameters[1]).getBoolean().booleanValue();
        extended = ((BooleanParameter) parameters[2]).getBoolean()
                .booleanValue();

        // the extended version is always lazy
        if (extended) {
            lazy = true;
        }
    }

    /**
     * Checks the preconditions for the MNN algorithm. The graph is not allowed
     * to be empty and shall be fourconnected and planar.
     */
    public void check() throws PreconditionException {
        PlanarityAlgorithm planar = new PlanarityAlgorithm();
        planar.attach(graph);
        TestedGraph testedGraph = planar.getTestedGraph();

        PreconditionException errors = new PreconditionException();

        if (graph == null) {
            errors.add("The graph instance may not be null.");

        } else if (graph.getNumberOfNodes() == 0) {

            errors.add("The graph is empty.");
        }

        if (!testedGraph.isPlanar()) {
            errors.add("The graph is not planar.");
        }

        if (testedGraph.getNumberOfDoubleEdges() > 0) {
            errors.add("The graph contains double edges.");
        }

        if (testedGraph.getNumberOfLoops() > 0) {
            errors.add("The graph contains loops.");
        }

        if (graph.getNumberOfNodes() < 6) {
            errors.add("The graph is too small and not 4-connected.");
        }

        Fourconnect fc = new Fourconnect();
        fc.attach(graph);
        fc.testFourconnect();
        if (!fc.isFourconnected()) {
            errors.add("The graph is not 4-connected.");
        }

        if (graph.getNumberOfEdges() >= graph.getNumberOfNodes() * 3 - 6) {
            errors.add("There must me at least one face with four nodes.");
        }

        if (!errors.isEmpty())
            throw errors;

    }

    /**
     * Resets the algorithm
     */
    public void reset() {
        super.reset();
        embeddedGraph = null;
        canonicalOrdering = null;
    }

    /**
     * Executes the Miura, Nakano, Nishizeki algorithm.
     */
    public void execute() {

        long start = System.currentTimeMillis();

        // The mnn algorithm
        mnn();

        // Zoom the graph, that anything can be seen
        if (zoom) {
            zoomGraph();
        }

        long finish = System.currentTimeMillis();

        // Algorithm Info
        System.out.println("Algorithm-Time: "
                + ((double) (finish - start) / 1000));
        System.out.println("Width: "
                + width
                + "/"
                + Math
                        .round((float) (Math
                                .ceil(graph.getNumberOfNodes() / 2.0) - 1))
                + " Height: "
                + height
                + "/"
                + Math
                        .round((float) (Math
                                .ceil(graph.getNumberOfNodes() / 2.0))));

    }

    /**
     * The Miura, Nakano, Nishizeki algorithm
     */
    private void mnn() {

        // Calculate an embedding of the planar graph
        embeddedGraph = new EmbeddedGraph(graph);

        // triangulated Version
        if (!extended) {
            // Triangulate the embedded graph
            embeddedGraph.triangulate();

            // 1. Find a 4-canonical ordering of the graph
            canonicalOrdering = new CanonicalOrdering(embeddedGraph);
        }

        // Not triangulated Version
        else {
            // 1. Find a 4-canonical decomposition of the graph
            canonicalOrdering = new CanonicalDepomposition(embeddedGraph);
        }

        // 2. Devide the graph into 2 subgraphs to draw them independently...
        // ... the first half
        canonicalOrdering.getFirstHalf();

        // some initialisation
        for (CanonicalOrderingNode con : canonicalOrdering
                .getCanonicalOrdering()) {
            for (Node n : con.getNodes()) {
                n.setBoolean("mnn.up", true);
            }
        }

        // the "core" algorithm
        calculateCoordinates();

        // ... the second half
        canonicalOrdering.getSecondHalf();

        // some initialisation
        for (CanonicalOrderingNode con : canonicalOrdering
                .getCanonicalOrdering()) {
            for (Node n : con.getNodes()) {
                n.setBoolean("mnn.up", false);
            }
        }

        // the "core" algorithm
        calculateCoordinates();

        // 3. Draws the graph
        draw();

        // Delete the added edges if the graph first had been triangulated
        if (!extended) {
            for (Edge edge : embeddedGraph.getAddedEdges()) {
                graph.deleteEdge(edge);
            }
        }
    }

    /**
     * calculates the new boundary of G
     * 
     * @param currentCon
     *            the current canonical ordeing node
     */
    private void calculateBoundary(CanonicalOrderingNode currentCon) {

        // the smaller neighbours of the next nodes(s)
        Collection<Node> neighbours = canonicalOrdering
                .getSmallerNeighbours(currentCon);

        // set with the nodes "unter" the current node
        HashSet<Node> under = new HashSet<Node>();

        tmpBoundary = new LinkedList<Node>(); // the new boundary
        wl = null; // the left neighbour
        wr = null; // the right neighbour
        ws = null; // the smallest neighbour
        wl1 = null; // the second left neighbour
        wr1 = null; // the second right neighbour
        ymax = 0; // the maximum y-coordinate of the neighbours

        int counter = 1;
        boolean addingMode = false;
        int neighboursCounter = 0;

        // Calculate the new boundary and the "special nodes" w_s, w_l,...
        // iterate over the old boundary
        for (Node node : boundary) {

            if (neighbours.contains(node)) {
                addingMode = true;
                neighboursCounter++;
            }

            // in the addingMode the new node(s) are added to the boundary
            if (addingMode) {
                if (counter == 1) {
                    wl = node;
                    ws = node;
                    tmpBoundary.add(node);

                    // add the new node(s)
                    tmpBoundary.addAll(currentCon.getNodes());
                }

                if (counter == 2) {
                    wl1 = node;
                }

                if (node.getInteger("mnn.y") < ws.getInteger("mnn.y")
                        || node.getInteger("mnn.ordering") < ws
                                .getInteger("mnn.ordering")) {
                    ws = node;
                }

                if (neighbours.size() == neighboursCounter) {
                    tmpBoundary.add(node);
                }

                if (ymax < node.getInteger("mnn.y")) {
                    ymax = node.getInteger("mnn.y");
                }

                if (counter >= 2 && neighboursCounter != neighbours.size()) {
                    under.addAll(underSet.get(node));
                }

                wr1 = wr;
                wr = node;

                counter++;
            } else {
                tmpBoundary.add(node);
            }

            if (neighboursCounter >= neighbours.size()) {
                addingMode = false;
            }
        }

        under.addAll(currentCon.getNodes());

        // calculate the underset for the new node(s)
        for (Node n : order(currentCon.getNodes())) {
            underSet.put(n, under);
            @SuppressWarnings("unchecked")
            HashSet<Node> tmpunder = (HashSet<Node>) under.clone();
            tmpunder.remove(n);
            under = tmpunder;

        }

    }

    /**
     * returns the correct ordering of the subnodes, if a Canonical ordering
     * Node contains 2 or more Subnodes
     * 
     * @param l
     *            the subnodes
     * @return the correct ordering of the subnodes
     */
    private LinkedList<Node> order(LinkedList<Node> l) {

        LinkedList<Node> result = new LinkedList<Node>();

        for (Node n : boundary) {
            for (Node m : n.getNeighbors()) {
                if (l.contains(m)) {
                    result.addLast(m);
                    break;
                }
            }
            if (result.size() != 0) {
                break;
            }
        }

        while (result.size() < l.size()) {

            for (Node n : result.getLast().getNeighbors()) {
                if (l.contains(n) && !result.contains(n)) {
                    result.addLast(n);
                }
            }
        }

        return result;
    }

    /**
     * calculates the coordinates for all nodes
     */
    private void calculateCoordinates() {
        boundary = new LinkedList<Node>();
        underSet = new HashMap<Node, HashSet<Node>>();

        // the first node with the coordinate (0,0)
        if (canonicalOrdering.hasNext()) {
            Node current = canonicalOrdering.next().getNodes().get(0);
            current.setInteger("mnn.x", 0);
            current.setInteger("mnn.y", 0);
            boundary.add(current);
            HashSet<Node> under = new HashSet<Node>();
            under.add(current);
            underSet.put(current, under);

        }

        // save this node for the correct boundary
        Node last = null;

        // the second node with the coordinate (2,0)
        if (canonicalOrdering.hasNext()) {
            last = canonicalOrdering.next().getNodes().get(0);
            last.setInteger("mnn.x", 2);
            last.setInteger("mnn.y", 0);
            HashSet<Node> under = new HashSet<Node>();
            under.add(last);
            underSet.put(last, under);
        }

        int x_count = 0;

        // the third node with the coordinate (1,1)
        if (canonicalOrdering.hasNext()) {

            /*
             * if there are more nodes with the ordering number '3' the
             * coordinates are (1,1), ..., (x,1)
             */
            if (lazy) {
                LinkedList<Node> l = canonicalOrdering.next().getNodes();
                l = order(l);
                HashSet<Node> under = new HashSet<Node>();
                under.addAll(l);

                for (Node current : l) {
                    x_count++;
                    current.setInteger("mnn.x", x_count);
                    current.setInteger("mnn.y", 1);
                    boundary.add(current);

                    underSet.put(current, under);

                    @SuppressWarnings("unchecked")
                    HashSet<Node> tmpunder = (HashSet<Node>) under.clone();
                    tmpunder.remove(current);
                    under = tmpunder;

                }

            } else {
                x_count++;
                Node current = canonicalOrdering.next().getNodes().get(0);
                current.setInteger("mnn.x", 1);
                current.setInteger("mnn.y", 1);
                boundary.add(current);
                HashSet<Node> under = new HashSet<Node>();
                under.add(current);
                underSet.put(current, under);
            }
        }

        // add the node with coordinate (2,0) to the boundary
        if (last != null) {
            last.setInteger("mnn.x", 1 + x_count);
            boundary.add(last);
        }

        // calculate the coordinates for the remaining nodes
        while (canonicalOrdering.hasNext()) {

            // the next node(s)
            CanonicalOrderingNode currentCon = canonicalOrdering.next();
            calculateBoundary(currentCon);

            // Case 1, 3, 5
            if ((wl.getInteger("mnn.y") < wr.getInteger("mnn.y"))
                    || (wl.getInteger("mnn.y") == wr.getInteger("mnn.y")
                            && !ws.equals(wr) && !ws.equals(wl) && wl1
                            .getInteger("mnn.y") != ymax)
                    || (wl.getInteger("mnn.y") == wr.getInteger("mnn.y") && ws
                            .equals(wl))) {

                if (lazy) {
                    int x = 0;

                    int[] interval = calculateInterval(true);
                    int minX = interval[0];
                    int maxX = interval[1];
                    int y = interval[2];

                    int intervalLength = maxX - minX; // 0, platz f�r einen

                    // there is enought space for the new nodes
                    if (intervalLength >= currentCon.getNumberOfNodes() - 1) {
                        x = getCenter(minX, maxX, currentCon.getNumberOfNodes());
                    }
                    // there is not enought space for the new nodes --> shift
                    else {
                        x = minX;

                        shift(false, currentCon.getNumberOfNodes()
                                - intervalLength - 1);
                    }

                    // set the new coordinates
                    for (Node n : order(currentCon.getNodes())) {
                        n.setInteger("mnn.x", x);
                        n.setInteger("mnn.y", y);
                        x++;
                    }

                }
                // the original version
                else {

                    shift(false, 1);

                    int x = 0;
                    int y = 0;

                    if (wr1.getInteger("mnn.y") < ymax) {
                        y = ymax;
                    } else {
                        y = ymax + 1;
                    }

                    x = ws.getInteger("mnn.x") + y - ws.getInteger("mnn.y");

                    (currentCon.getNodes().get(0)).setInteger("mnn.x", x);
                    (currentCon.getNodes().get(0)).setInteger("mnn.y", y);
                }

            }
            // Case 2, 4, 6
            else if (wl.getInteger("mnn.y") > wr.getInteger("mnn.y")
                    || (wl.getInteger("mnn.y") == wr.getInteger("mnn.y")
                            && !ws.equals(wr) && !ws.equals(wl) && wl1
                            .getInteger("mnn.y") == ymax)
                    || (wl.getInteger("mnn.y") == wr.getInteger("mnn.y") && ws
                            .equals(wr))) {

                if (lazy) {
                    int x = 0;

                    int[] interval = calculateInterval(false);
                    int minX = interval[0];
                    int maxX = interval[1];
                    int y = interval[2];

                    int intervalLength = maxX - minX;

                    // enought space for the new nodes
                    if (intervalLength >= currentCon.getNumberOfNodes() - 1) {
                        x = getCenter(minX, maxX, currentCon.getNumberOfNodes());
                    }
                    // shift is necessary
                    else {
                        x = minX;
                        shift(true, currentCon.getNumberOfNodes()
                                - intervalLength - 1);

                    }

                    for (Node n : order(currentCon.getNodes())) {
                        n.setInteger("mnn.x", x);
                        n.setInteger("mnn.y", y);
                        x++;
                    }

                } else {
                    shift(true, 1);

                    int x = 0;
                    int y = 0;

                    if (wl1.getInteger("mnn.y") < ymax) {
                        y = ymax;
                    } else {
                        y = ymax + 1;
                    }

                    x = ws.getInteger("mnn.x") - (y - ws.getInteger("mnn.y"));

                    (currentCon.getNodes().get(0)).setInteger("mnn.x", x);
                    (currentCon.getNodes().get(0)).setInteger("mnn.y", y);
                }

            }

            // Set new boundary
            boundary = tmpBoundary;
        }
    }

    /**
     * Calculates the interval of possible locations of the new node(s)
     * 
     * @param case1
     *            true if case 1,3,5, else false
     * @return the interval
     */
    private int[] calculateInterval(boolean case1) {

        // calculate the y-coordinate of the new node(s) depending on the case
        int y = 0;

        if (case1) {
            if (wr1.getInteger("mnn.y") < ymax) {
                y = ymax;
            } else {
                y = ymax + 1;
            }
        } else {
            if (wl1.getInteger("mnn.y") < ymax) {
                y = ymax;
            } else {
                y = ymax + 1;
            }
        }

        // the condition with slope = 1 must be satisfied (w_l and w_r)
        int deltaY_wl = y - wl.getInteger("mnn.y");
        int minX = wl.getInteger("mnn.x") + deltaY_wl;

        int deltaY_wr = y - wr.getInteger("mnn.y");
        int maxX = wr.getInteger("mnn.x") - deltaY_wr;

        // calculate all the coordinates on the border from w_l to w_r
        ArrayList<Integer> visx = new ArrayList<Integer>();
        ArrayList<Integer> visy = new ArrayList<Integer>();

        int c = 0;
        boolean ok = false;

        for (Node bn : boundary) {

            if (bn == wl) {
                ok = true;
            }

            if (ok) {
                visx.add(c, bn.getInteger("mnn.x"));
                visy.add(c, bn.getInteger("mnn.y"));
                c++;
            }

            if (bn == wr) {
                ok = false;
            }
        }

        int cutx = Integer.MAX_VALUE;

        int cccc;

        if (case1) {
            cccc = Integer.MAX_VALUE;
        } else {
            cccc = 0;
        }

        // remove the coordinates that are not visible from all points
        for (int i = 0; i < visx.size() - 1; i++) {
            int divx = visx.get(i + 1) - visx.get(i);
            int divy = visy.get(i + 1) - visy.get(i);

            if (divy != 0) {
                cutx = divx / divy * (y - visy.get(i)) + visx.get(i);

                // slope positiv
                if (case1 && divy >= 0) {
                    if (cutx < cccc) {
                        cccc = cutx;
                    }
                } else if (!case1 && divy < 0) {
                    if (cutx > cccc) {
                        cccc = cutx;
                    }
                }
            }
        }

        // if w_l and w_r are not equal...
        if ((wr.getInteger("mnn.y") != wl.getInteger("mnn.y"))) {

            if (case1) {
                if (cccc != Integer.MAX_VALUE) {
                    cccc = Math.round((float) Math.ceil(cccc) - 1);
                }

                if (maxX > cccc) {
                    maxX = cccc;
                }
            } else {
                if (cccc != 0) {
                    cccc = Math.round((float) Math.floor(cccc) + 1);
                }

                if (minX < cccc) {
                    minX = cccc;
                }
            }
        } else {

            if (deltaY_wl == 0) {
                minX++;
            }

            if (deltaY_wr == 0) {
                maxX--;
            }
        }

        return new int[] { minX, maxX, y };

    }

    /**
     * Shift Operation
     * 
     * @param withCurrent
     *            if true, w_s is also shifted
     * @param length
     *            the shift distance
     */
    private void shift(boolean withCurrent, int length) {

        boolean shift = false;
        boolean shiftnext = false;
        HashSet<Node> shiftNodes = new HashSet<Node>();

        for (Node current : boundary) {
            if (shiftnext) {
                shift = true;
            }

            if (withCurrent) {
                if (current.equals(ws)) {
                    shift = true;
                }
            } else {
                if (current.equals(ws)) {
                    shiftnext = true;
                }
            }

            if (shift) {
                HashSet<Node> col = underSet.get(current);
                shiftNodes.addAll(col);
            }
        }

        for (Node current : shiftNodes) {
            current.setInteger("mnn.x", current.getInteger("mnn.x") + length);
        }

    }

    /**
     * returns the center position for the new Nodes
     * 
     * @param min
     *            the minimum position
     * @param max
     *            the maximum position
     * @param numOfNodes
     *            the number of nodes to be inserted
     * @return the center position
     */
    private int getCenter(int min, int max, int numOfNodes) {
        return Math.round((max - min - numOfNodes + 1) / 2) + min;
    }

    /**
     * Draws the graph
     */
    private void draw() {
        // Calculate the width of the upper and lower triangle
        int maxXup = 0;
        int maxXdown = 0;

        // get the width of the two trinagles
        for (Node node : graph.getNodes()) {
            if (node.getBoolean("mnn.up")) {
                if (node.getInteger("mnn.x") > maxXup) {
                    maxXup = node.getInteger("mnn.x");
                }
            } else {
                if (node.getInteger("mnn.x") > maxXdown) {
                    maxXdown = node.getInteger("mnn.x");
                }
            }
        }

        // Width of the Graph
        width = Math.max(maxXup, maxXdown);

        int xdiff = Math.abs(maxXup - maxXdown);

        // Center the smaller one
        if (xdiff >= 2) {
            int shift = Math.round(xdiff / 2);

            if (maxXup > maxXdown) {
                for (Node node : graph.getNodes()) {
                    if (!node.getBoolean("mnn.up")) {
                        node.setInteger("mnn.x", node.getInteger("mnn.x")
                                + shift);
                    }
                }
            } else {
                for (Node node : graph.getNodes()) {
                    if (node.getBoolean("mnn.up")) {
                        node.setInteger("mnn.x", node.getInteger("mnn.x")
                                + shift);
                    }
                }
            }
        }

        // if lazy version, try to kompact the graph vertically
        if (lazy) {

            height = 0;
            for (Node n : boundary) {
                // try to compact the graph vertically
                for (Node neighbour : n.getNeighbors()) {
                    if (n.getBoolean("mnn.up") != neighbour
                            .getBoolean("mnn.up")) {

                        int dist = Math.abs(n.getInteger("mnn.x")
                                - neighbour.getInteger("mnn.x"))
                                + n.getInteger("mnn.y")
                                + neighbour.getInteger("mnn.y") + 1;

                        if (dist > height) {
                            height = dist;

                        }
                    }
                }
            }
        } else {
            height = Math.round((float) (Math
                    .ceil(graph.getNumberOfNodes() / 2.0)));
        }

        // Draw the Graph
        for (Node node : graph.getNodes()) {
            // turn around the lower triangle
            if (!node.getBoolean("mnn.up")) {
                if (lazy) {
                    node.setInteger("mnn.y", height - node.getInteger("mnn.y"));
                } else {
                    node.setInteger("mnn.y", graph.getNumberOfNodes() / 2
                            - node.getInteger("mnn.y"));
                }
            }

            setGridCoordinate(node, node.getInteger("mnn.x"), node
                    .getInteger("mnn.y"));
        }

    }

    /**
     * Sets the Grid Coordinate of a Node
     * 
     * @param node
     *            the node
     * @param x
     *            the integer Coordinate
     * @param y
     *            the integer Coordinate
     */
    private void setGridCoordinate(Node node, int x, int y) {
        CoordinateAttribute ca = ((NodeGraphicAttribute) node.getAttributes()
                .getAttribute(NodeGraphicAttribute.GRAPHICS)).getCoordinate();

        double xCoord;
        double yCoord;

        xCoord = gridSize + x * gridSize;
        yCoord = gridSize + y * gridSize;

        ca.setCoordinate(new Point2D.Double(xCoord, yCoord));
    }

    /**
     * Zooms the graph
     */
    private void zoomGraph() {

        double biggestX = Double.NEGATIVE_INFINITY;
        double biggestY = Double.NEGATIVE_INFINITY;

        for (Node node : graph.getNodes()) {

            NodeGraphicAttribute nga = (NodeGraphicAttribute) (node
                    .getAttributes()
                    .getAttribute(NodeGraphicAttribute.GRAPHICS));

            if (nga.getCoordinate().getX() > biggestX) {
                biggestX = nga.getCoordinate().getX();
            }

            if (nga.getCoordinate().getY() > biggestY) {
                biggestY = nga.getCoordinate().getY();
            }

        }

        GraffitiSingleton gs = GraffitiSingleton.getInstance();
        MainFrame mf = gs.getMainFrame();

        double frameWidth = mf.getWidth();
        double frameHeight = mf.getHeight();
        double zoom = 1.0;

        if (biggestX > frameWidth) {
            zoom = frameWidth / biggestX;
        }

        if (biggestY > frameHeight) {
            if (frameHeight / biggestY < zoom) {
                zoom = frameHeight / biggestY;
            }
        }

        AffineTransform a = mf.getActiveEditorSession().getActiveView()
                .getZoomTransform();
        a.setToScale(zoom * 0.82, zoom * 0.82);

    }

}
