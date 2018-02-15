package org.graffiti.plugins.algorithms.mnn;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringParameter;

/**
 * This class implements a Sweepline Algorithm that compacts a graph that was
 * drawn with the algorithm of Miura, Nakano and Nishiszeki
 * 
 * @author Thomas Ortmeier
 * @version $Revision: 5766 $ $Date: 2010-05-07 19:21:40 +0200 (Fr, 07 Mai 2010)
 *          $
 */
public class SweeplineAlgorithm extends AbstractAlgorithm {

    // height of the graph
    private int height = 0;

    // width of the graph
    private int width = 0;

    private StringParameter directionParameter;
    private String direction = "";

    // "auto"-mode: shifts the nodes to left
    private BooleanParameter autoParameter;
    private boolean auto = false;

    /*
     * resets the four corner nodes to the corner of the grid after every
     * iteration
     */
    private BooleanParameter moveFourNodesParameter;
    private boolean moveFourNodes = false;

    // runs the algorithm step by step
    private BooleanParameter singleStepParameter;
    private boolean singleStep = false;

    // standard grid size
    private int gridSize = 60;

    // true, if the graph is triangulatet
    private boolean triangulated = false;

    // for "auto"-mode: true, if the graph could be compacted vertical
    private boolean compactU = true;

    // for "auto"-mode: true, if the graph could be compacted horizontal
    private boolean compactL = true;

    // The embedded graph
    private EmbeddedGraph embeddedGraph;

    /**
     * If user clicks the next iteration step.
     */
    public final static String NEXT_STEP = " >> ";

    /**
     * If user clicks complete without step view. Algorithm runs without the
     * rest steps.
     */
    public final static String COMPLETE_WITHOUT_STEP_VIEW = "complete without stepview";

    private String mode = "";

    /**
     * Constructor
     */
    public SweeplineAlgorithm() {
        super();
    }

    /**
     * Returns the Name of the Algorithm
     */
    public String getName() {
        return "Sweepline Compact";
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        directionParameter = new StringParameter("l", "Sweepline direction",
                "(l = to left; r = to right)");

        autoParameter = new BooleanParameter(true, "auto mode", "auto mode");

        moveFourNodesParameter = new BooleanParameter(true, "4 nodes mode",
                "resets the 4 corner nodes always to the 4 corners");

        singleStepParameter = new BooleanParameter(false, "single step mode",
                "single step mode");

        return new Parameter[] { directionParameter, autoParameter,
                moveFourNodesParameter, singleStepParameter };
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters
     *      (org.graffiti.plugin.parameter.Parameter[])
     */
    public void setAlgorithmParameters(Parameter<?>[] parameters) {
        direction = ((StringParameter) parameters[0]).getString();
        auto = ((BooleanParameter) parameters[1]).getBoolean().booleanValue();
        moveFourNodes = ((BooleanParameter) parameters[2]).getBoolean()
                .booleanValue();
        singleStep = ((BooleanParameter) parameters[3]).getBoolean()
                .booleanValue();
    }

    /**
     * Checks the preconditions for the sweepline algorithm.
     */
    public void check() throws PreconditionException {

        PreconditionException errors = new PreconditionException();

        if (!errors.isEmpty())
            throw errors;

    }

    /**
     * Resets the algorithm
     */
    public void reset() {
        super.reset();
    }

    /**
     * Executes the sweepline algorithm.
     */
    public void execute() {

        init();

        // Calculate an embedding of the planar graph
        embeddedGraph = new EmbeddedGraph(graph);

        if (!auto) {
            for (int i = 0; i < direction.length(); i++) {
                sweepline(direction.toCharArray()[i]);
            }
        }
        /*
         * 'auto'-compact mode: shifts the nodes to the left as long as
         * possible; then the nodes are shifted to the bottom as long as
         * possible and so on
         */
        else {
            char autodir = 'l';
            compactU = true;
            compactL = true;
            int loopCountL = 0;
            int loopCountU = 0;

            while (true) {
                sweepline(autodir);

                if (autodir == 'l') {

                    if (compactL) {
                        autodir = 'l';
                    } else {
                        autodir = 'u';
                    }

                    loopCountL++;

                    if (loopCountU > 1) {
                        compactU = true;
                    }
                    loopCountU = 0;

                } else {

                    if (compactU) {
                        autodir = 'u';
                    } else {
                        autodir = 'l';
                    }

                    loopCountU++;

                    if (loopCountL > 1) {
                        compactL = true;
                    }
                    loopCountL = 0;
                }

                if (!compactL && !compactU) {
                    break;
                }
            }
        }

        System.out.println("width: " + width + " height: " + height);
    }

    /**
     * Sets the Grid Coordinate of a Node
     * 
     * @param node
     *            the node
     */
    private void setGridCoordinate(Node node) {
        CoordinateAttribute ca = ((NodeGraphicAttribute) node.getAttributes()
                .getAttribute(NodeGraphicAttribute.GRAPHICS)).getCoordinate();

        double xCoord;
        double yCoord;

        xCoord = gridSize + node.getInteger("mnn.x") * gridSize;
        yCoord = gridSize + node.getInteger("mnn.y") * gridSize;

        ca.setCoordinate(new Point2D.Double(xCoord, yCoord));
    }

    /**
     * some initialisation
     */
    private void init() {

        // is the graph triangulated
        if (graph.getNumberOfEdges() == graph.getNumberOfNodes() * 3 - 7) {
            triangulated = true;
        }

        // calculate the current width and height of the graph
        height = 0;
        width = 0;

        for (Node n : graph.getNodes()) {
            if (n.getInteger("mnn.x") > width) {
                width = n.getInteger("mnn.x");
            }
            if (n.getInteger("mnn.y") > height) {
                height = n.getInteger("mnn.y");
            }

        }
    }

    /**
     * the sweepline algorithm
     * 
     * @param dir
     *            direction ('l': move the nodes to the left 'u': move the nodes
     *            up )
     */
    private void sweepline(char dir) {
        // execution mode - step by step or normal
        mode = "";

        // array with all nodes
        Node[][] a = new Node[width + 1][height + 1];
        for (Node n : graph.getNodes()) {
            int x = n.getInteger("mnn.x");
            int y = n.getInteger("mnn.y");
            a[x][y] = n;
            n.setInteger("mnn.oldx", n.getInteger("mnn.x"));
            n.setInteger("mnn.oldy", n.getInteger("mnn.y"));
        }

        // the four corner nodes
        Node ol = null;
        Node or = null;
        Node ul = null;
        Node ur = null;

        if (moveFourNodes) {
            for (int i = 0; i <= width; i++) {
                if (a[i][0] != null) {

                    if (ul == null) {
                        ul = a[i][0];
                    } else {
                        ur = a[i][0];
                    }
                }
            }

            for (int i = 0; i <= width; i++) {
                if (a[i][height] != null) {

                    if (ol == null) {
                        ol = a[i][height];
                    } else {
                        or = a[i][height];
                    }
                }
            }

            ul.setInteger("mnn.x", 0);
            ul.setInteger("mnn.y", 0);
            setGridCoordinate(ul);

            ur.setInteger("mnn.x", width);
            ur.setInteger("mnn.y", 0);
            setGridCoordinate(ur);

            ol.setInteger("mnn.x", 0);
            ol.setInteger("mnn.y", height);
            setGridCoordinate(ol);

            or.setInteger("mnn.x", width);
            or.setInteger("mnn.y", height);
            setGridCoordinate(or);

            // calculate the array again
            a = new Node[width + 1][height + 1];
            for (Node n : graph.getNodes()) {
                int x = n.getInteger("mnn.x");
                int y = n.getInteger("mnn.y");
                a[x][y] = n;
                n.setInteger("mnn.oldx", n.getInteger("mnn.x"));
                n.setInteger("mnn.oldy", n.getInteger("mnn.y"));
            }
        }

        // move the nodes up ('u') or left ('l')
        switch (dir) {

        // sweepline from left to right
        case 'l':

            compactL = false;

            for (int i = 1; i <= width; i++) {
                for (int j = 0; j <= height; j++) {
                    if (a[i][j] != null) {
                        Node current = a[i][j];

                        // the triangulated version
                        if (triangulated) {
                            /*
                             * calculate the intervall with the legal positions
                             * for the current node
                             */
                            int[] minMax = getMinXCoord(current);

                            // if the node can be moved...
                            if (minMax[0] < current.getInteger("mnn.x")) {
                                // ... do it
                                current.setInteger("mnn.x", minMax[0]);
                                setGridCoordinate(current);

                                // step by step mode
                                if (singleStep) {
                                    if (mode != COMPLETE_WITHOUT_STEP_VIEW) {
                                        mode = stepHandle();
                                    }
                                }
                            }
                        }
                        // the not triangulated version
                        else {

                            while (true) {
                                // move the cuurent node to the left
                                current.setInteger("mnn.x", current
                                        .getInteger("mnn.x") - 1);

                                // test if this position is legal...
                                if (isLegalPosition(current)) {
                                    // ...and set the grid coordinate
                                    setGridCoordinate(current);
                                }
                                // else move it back to it's old position
                                else {
                                    current.setInteger("mnn.x", current
                                            .getInteger("mnn.x") + 1);
                                    break;
                                }

                                // step by step mode
                                if (singleStep) {
                                    if (mode != COMPLETE_WITHOUT_STEP_VIEW) {
                                        mode = stepHandle();
                                    }
                                }
                            }
                        }
                    }
                }
            }

            break;

        // sweepline from the top to the bottom
        case 'u':

            compactU = false;

            for (int i = 1; i <= height; i++) {
                for (int j = 0; j <= width; j++) {
                    if (a[j][i] != null) {

                        Node current = a[j][i];

                        if (triangulated) {
                            int[] minMax = getMinYCoord(current);

                            if (minMax[0] < current.getInteger("mnn.y")) {

                                current.setInteger("mnn.y", minMax[0]);

                                setGridCoordinate(current);

                                if (singleStep) {
                                    if (mode != COMPLETE_WITHOUT_STEP_VIEW) {
                                        mode = stepHandle();
                                    }
                                }

                            }
                        } else {

                            while (true) {
                                current.setInteger("mnn.y", current
                                        .getInteger("mnn.y") - 1);

                                if (isLegalPosition(current)) {
                                    setGridCoordinate(current);
                                } else {
                                    current.setInteger("mnn.y", current
                                            .getInteger("mnn.y") + 1);
                                    break;
                                }

                                if (singleStep) {
                                    if (mode != COMPLETE_WITHOUT_STEP_VIEW) {
                                        mode = stepHandle();
                                    }
                                }
                            }
                        }
                    }
                }
            }

            break;

        }

        // calculate the new width an height
        height = 0;
        width = 0;

        for (Node n : graph.getNodes()) {
            int x = n.getInteger("mnn.x");
            int y = n.getInteger("mnn.y");

            if (x > width) {
                width = x;
            }

            if (y > height) {
                height = y;
            }
        }

        // move the four corner nodes to the corner of the grid
        if (moveFourNodes) {
            ul.setInteger("mnn.x", 0);
            ul.setInteger("mnn.y", 0);
            setGridCoordinate(ul);

            ur.setInteger("mnn.x", width);
            ur.setInteger("mnn.y", 0);
            setGridCoordinate(ur);

            ol.setInteger("mnn.x", 0);
            ol.setInteger("mnn.y", height);
            setGridCoordinate(ol);

            or.setInteger("mnn.x", width);
            or.setInteger("mnn.y", height);
            setGridCoordinate(or);
        }

        // check if some nodes could be moved left or up
        for (Node n : graph.getNodes()) {
            if (n.getInteger("mnn.x") != n.getInteger("mnn.oldx")) {
                compactL = true;
            }

            if (n.getInteger("mnn.y") != n.getInteger("mnn.oldy")) {
                compactU = true;
            }

        }

    }

    /**
     * calculate the interval of the legal x-positions for a node n.
     * 
     * @param n
     *            the node
     * 
     * @return the min [0] an the max [1] x-coordinate
     */
    private int[] getMinXCoord(Node n) {

        double y = n.getInteger("mnn.y");
        int x = n.getInteger("mnn.x");
        int maxSchnitt = 0;// Integer.MIN_VALUE;
        int minSchnitt = width;

        for (Edge e : getNeighbourNodes(n)) {
            double xn1 = e.getSource().getInteger("mnn.x");
            double yn1 = e.getSource().getInteger("mnn.y");

            double xn2 = e.getTarget().getInteger("mnn.x");
            double yn2 = e.getTarget().getInteger("mnn.y");

            if (yn2 - yn1 != 0) {

                double xSchnitt = ((xn2 - xn1) / (yn2 - yn1)) * (y - yn1) + xn1;

                if (xSchnitt < x
                        && (int) Math.round(Math.floor(xSchnitt)) + 1 > maxSchnitt) {
                    maxSchnitt = (int) Math.round(Math.floor(xSchnitt)) + 1;
                    // System.out.println("NEW MAX " + maxSchnitt);
                }

                if (xSchnitt > x
                        && (int) Math.round(Math.ceil(xSchnitt)) - 1 < minSchnitt) {
                    minSchnitt = (int) Math.round(Math.ceil(xSchnitt)) - 1;
                    // System.out.println("NEW MIN " + maxSchnitt);
                }
            }

        }

        int[] result = new int[2];
        result[0] = maxSchnitt;
        result[1] = minSchnitt;
        return result;
    }

    private int[] getMinYCoord(Node n) {

        int y = n.getInteger("mnn.y");
        double x = n.getInteger("mnn.x");
        int maxSchnitt = 0;
        int minSchnitt = height;

        for (Edge e : getNeighbourNodes(n)) {

            double xn1 = e.getSource().getInteger("mnn.x");
            double yn1 = e.getSource().getInteger("mnn.y");

            double xn2 = e.getTarget().getInteger("mnn.x");
            double yn2 = e.getTarget().getInteger("mnn.y");

            if (xn2 - xn1 != 0) {

                double ySchnitt = (yn2 - yn1) / (xn2 - xn1) * x + yn1
                        - (yn2 - yn1) / (xn2 - xn1) * xn1;

                if (ySchnitt < y
                        && (int) Math.round(Math.floor(ySchnitt)) + 1 > maxSchnitt) {
                    maxSchnitt = (int) Math.round(Math.floor(ySchnitt)) + 1;
                }

                if (ySchnitt > y
                        && (int) Math.round(Math.ceil(ySchnitt)) - 1 < minSchnitt) {
                    minSchnitt = (int) Math.round(Math.ceil(ySchnitt)) - 1;
                }
            }
        }

        int[] result = new int[2];
        result[0] = maxSchnitt;
        result[1] = minSchnitt;
        return result;
    }

    /**
     * returns the edges of the face the node n lies in
     * 
     * @param n
     *            the current node
     * @return all the edges of the face the node n lies in
     */
    private LinkedList<Edge> getNeighbourNodes(Node n) {

        LinkedList<Edge> result = new LinkedList<Edge>();
        List<Face> faceList = embeddedGraph.getInnerFaces(n);

        for (Face f : faceList) {
            for (Edge e : f.getEdges()) {
                if (e.getSource() != n && e.getTarget() != n) {
                    result.add(e);
                }
            }
        }

        return result;
    }

    /**
     * "liegt rechts von"-algorithm
     * 
     * @param p
     *            node p
     * @param q
     *            node q
     * @param r
     *            the node to be tested on which side it lies
     * @return a number which tells, if the node lies left, right or on the line
     *         througt p and q
     */
    private int lrv(Node p, Node q, Node r) {
        int rx = r.getInteger("mnn.x");
        int ry = r.getInteger("mnn.y");

        int px = p.getInteger("mnn.x");
        int py = p.getInteger("mnn.y");

        int qx = q.getInteger("mnn.x");
        int qy = q.getInteger("mnn.y");

        return ((ry - py) * (qx - px)) - ((rx - px) * (qy - py));
    }

    /**
     * @param e1
     *            edge 1
     * @param e2
     *            edge 2
     * @return true, if the two edges have a cut
     */
    private boolean hasCut(Edge e1, Edge e2) {
        Node e1_a = e1.getSource();
        Node e1_b = e1.getTarget();

        Node e2_a = e2.getSource();
        Node e2_b = e2.getTarget();

        int lrv_e1_a = lrv(e2_a, e2_b, e1_a);
        int lrv_e1_b = lrv(e2_a, e2_b, e1_b);

        int lrv_e2_a = lrv(e1_a, e1_b, e2_a);
        int lrv_e2_b = lrv(e1_a, e1_b, e2_b);

        if (((lrv_e1_a < 0 && lrv_e1_b > 0) || (lrv_e1_a > 0 && lrv_e1_b < 0))
                && ((lrv_e2_a < 0 && lrv_e2_b > 0) || (lrv_e2_a > 0 && lrv_e2_b < 0)))
            return true;
        else
            return false;
    }

    /**
     * @param n
     * @return true, if this is a legal position for node n
     */
    private boolean isLegalPosition(Node n) {

        if (n.getInteger("mnn.x") < 0)
            return false;

        for (Edge e1 : getNeighbourNodes(n)) {
            for (Edge e2 : n.getEdges()) {

                if (hasCut(e1, e2))
                    return false;

                if (touches(e2, e1.getSource()))
                    return false;

                if (touches(e2, e1.getTarget()))
                    return false;
            }

            if (touches(e1, n))
                return false;
        }

        return true;
    }

    /**
     * @param e
     *            an edge
     * @param n
     *            a node
     * @return true, if node n touches the edge e
     */
    private boolean touches(Edge e, Node n) {

        // if n lies on the line through e
        if (lrv(e.getSource(), e.getTarget(), n) == 0) {
            int esx = e.getSource().getInteger("mnn.x");
            int esy = e.getSource().getInteger("mnn.y");
            int etx = e.getTarget().getInteger("mnn.x");
            int ety = e.getTarget().getInteger("mnn.y");

            int minx = Math.min(esx, etx);
            int maxx = Math.max(esx, etx);
            int miny = Math.min(esy, ety);
            int maxy = Math.max(esy, ety);

            // if n lies on e...
            if (n.getInteger("mnn.x") >= minx && n.getInteger("mnn.x") <= maxx
                    && n.getInteger("mnn.y") >= miny
                    && n.getInteger("mnn.y") <= maxy) {

                // ... and is the same n
                if (e.getSource() == n || e.getTarget() == n)
                    return false;
                else
                    return true;

            } else
                return false;

        } else
            return false;
    }

    /**
     * @return the chosen button
     */
    protected String stepHandle() {

        String[] options = { NEXT_STEP, COMPLETE_WITHOUT_STEP_VIEW };
        int chosen = JOptionPane.showOptionDialog(null, "next", "Next step",
                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
                options, options[0]);

        if (chosen == 0)
            return NEXT_STEP;
        else if (chosen == 1)
            return COMPLETE_WITHOUT_STEP_VIEW;
        else
            return "";
    }
}
