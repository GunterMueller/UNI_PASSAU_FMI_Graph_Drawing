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

public class SweeplineKompact extends AbstractAlgorithm {

    // Only for debugging
    // private static final String PATH = GraphElementGraphicAttribute.LABEL
    // + Attribute.SEPARATOR + GraphElementGraphicAttribute.LABEL;

    private int height = 0;

    private int width = 0;

    private StringParameter directionParameter;

    private BooleanParameter autoParameter;

    private boolean auto = false;

    private BooleanParameter moveFourNodesParameter;

    private boolean moveFourNodes = false;

    private BooleanParameter singleStepParameter;

    private boolean singleStep = false;

    private String direction = "";

    private int gridSize = 60;

    private boolean triangulated = false;

    private boolean compactU = true;
    private boolean compactL = true;

    // The embedded graph
    private EmbeddedGraph embeddedGraph;

    /**
     * Constructor
     */
    public SweeplineKompact() {
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
    public Parameter<?>[] getAlgorithmParameters() {
        directionParameter = new StringParameter("l", "Sweepline direction",
                "(l = to left; r = to right)");

        autoParameter = new BooleanParameter(true, "auto mode", "auto mode");

        moveFourNodesParameter = new BooleanParameter(true, "4 nodes",
                "4 nodes");

        singleStepParameter = new BooleanParameter(false, "single step mode",
                "single step mode");

        return new Parameter[] { directionParameter, autoParameter,
                moveFourNodesParameter, singleStepParameter };
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(org.graffiti.plugin.parameter.Parameter[])
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
     * Checks the preconditions for the MNN algorithm. The graph is not allowed
     * to be empty and shall be fourconnected and planar.
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
        // System.out.println("Width: " + width + "Height: " + height);

        // Calculate an embedding of the planar graph
        embeddedGraph = new EmbeddedGraph(graph);

        if (!auto) {
            for (int i = 0; i < direction.length(); i++) {
                System.out.println("DIRECTION " + direction.toCharArray()[i]);

                sweepline(direction.toCharArray()[i]);

                // Draw the Graph
                for (Node node : graph.getNodes()) {
                    setGridCoordinate(node);
                }

                System.out.println("Width: " + width + "Height: " + height);
            }
        } else {
            char autodir = 'l';
            compactU = true;
            compactL = true;
            int loopCountL = 0;
            int loopCountU = 0;

            while (true) {
                sweepline(autodir);

                System.out.println("DIRECTION " + autodir);
                System.out.println("CL " + compactL + " CU " + compactU);

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
            System.out.println("Width: " + width + " Height: " + height);
        }

    }

    /**
     * Sets the Grid Coordinate of a Node
     * 
     * @param node
     *            the node
     */
    private void setGridCoordinate(Node node) // , int x, int y)
    {
        CoordinateAttribute ca = ((NodeGraphicAttribute) node.getAttributes()
                .getAttribute(NodeGraphicAttribute.GRAPHICS)).getCoordinate();

        double xCoord;
        double yCoord;

        xCoord = gridSize + node.getInteger("mnn.x") * gridSize;
        yCoord = gridSize + node.getInteger("mnn.y") * gridSize;

        ca.setCoordinate(new Point2D.Double(xCoord, yCoord));
    }

    private void init() {
        if (graph.getNumberOfEdges() == graph.getNumberOfNodes() * 3 - 7) {
            triangulated = true;
        }

        height = 0;
        width = 0;

        for (Node n : graph.getNodes()) {

            System.out.println("x " + n.getInteger("mnn.x"));
            System.out.println("y " + n.getInteger("mnn.y"));

            if (n.getInteger("mnn.x") > width) {
                System.out.println("new max x" + n.getInteger("mnn.x"));
                width = n.getInteger("mnn.x");
            }
            if (n.getInteger("mnn.y") > height) {
                System.out.println("new max y" + n.getInteger("mnn.y"));
                height = n.getInteger("mnn.y");
            }

        }
    }

    private void sweepline(char dir) {
        mode = "";
        Node[][] a = new Node[width + 1][height + 1];

        for (Node n : graph.getNodes()) {
            int x = n.getInteger("mnn.x");
            int y = n.getInteger("mnn.y");
            a[x][y] = n;
            n.setInteger("mnn.oldx", n.getInteger("mnn.x"));
            n.setInteger("mnn.oldy", n.getInteger("mnn.y"));
        }

        // for (int i = 0; i <= height; i++)
        // {
        // for (int j = 0; j <= width; j++)
        // {
        // if (a[j][i] != null)
        // {
        // System.out.print("[" + a[j][i].getString(PATH) + "]");
        // }
        // else
        // {
        // System.out.print("[ ]");
        // }
        // }
        // System.out.println();
        // }

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

            a = new Node[width + 1][height + 1];
            for (Node n : graph.getNodes()) {
                int x = n.getInteger("mnn.x");
                int y = n.getInteger("mnn.y");
                a[x][y] = n;
                n.setInteger("mnn.oldx", n.getInteger("mnn.x"));
                n.setInteger("mnn.oldy", n.getInteger("mnn.y"));
            }
        }

        switch (dir) {

        // sweepline from left to right
        case 'l':

            compactL = false;

            for (int i = 1; i <= width; i++) {
                for (int j = 0; j <= height; j++) {
                    if (a[i][j] != null) {
                        Node current = a[i][j];

                        if (triangulated) {

                            int[] minMax = getMinXCoord(current);

                            if (minMax[0] < current.getInteger("mnn.x")) {

                                current.setInteger("mnn.x", minMax[0]);

                                setGridCoordinate(current);

                                if (singleStep) {
                                    if (mode != COMPLETE_WITHOUT_STEP_VIEW) {
                                        mode = stepHandle();
                                    }
                                }
                                // System.out.println("Move: "
                                // + current.getString(PATH) + " ("
                                // + current.getInteger("mnn.x") + ","
                                // + current.getInteger("mnn.y") + ") ");
                            }
                        } else {
                            // System.out.println("--> NEXT NODE: "
                            // + current.getString(PATH) + " ("
                            // + current.getInteger("mnn.x") + ","
                            // + current.getInteger("mnn.y") + ") ");

                            while (true) {
                                current.setInteger("mnn.x", current
                                        .getInteger("mnn.x") - 1);

                                if (isLegalPosition(current)) {
                                    // System.out.println("Move: "
                                    // + current.getString(PATH) + " ("
                                    // + current.getInteger("mnn.x") + ","
                                    // + current.getInteger("mnn.y")
                                    // + ") ");

                                    setGridCoordinate(current);
                                } else {
                                    // System.out.println("ILLEGAL");
                                    current.setInteger("mnn.x", current
                                            .getInteger("mnn.x") + 1);
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

                                // System.out.println("Move: "
                                // + current.getString(PATH) + " ("
                                // + current.getInteger("mnn.x") + ","
                                // + current.getInteger("mnn.y") + ") ");
                            }
                        } else {
                            // System.out.println("--> NEXT NODE: "
                            // + current.getString(PATH) + " ("
                            // + current.getInteger("mnn.x") + ","
                            // + current.getInteger("mnn.y") + ") ");

                            while (true) {

                                current.setInteger("mnn.y", current
                                        .getInteger("mnn.y") - 1);

                                if (isLegalPosition(current)) {
                                    // System.out.println("Move: "
                                    // + current.getString(PATH) + " ("
                                    // + current.getInteger("mnn.x") + ","
                                    // + current.getInteger("mnn.y")
                                    // + ") ");

                                    setGridCoordinate(current);
                                } else {
                                    // System.out.println("ILLEGAL");
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

        if (moveFourNodes) {
            // System.out.println("ul " + ul.getString(PATH) + " ("
            // + ul.getInteger("mnn.x") + "," + ul.getInteger("mnn.y") + ") ");
            //
            // System.out.println("ur " + ur.getString(PATH) + " ("
            // + ur.getInteger("mnn.x") + "," + ur.getInteger("mnn.y") + ") ");
            //
            // System.out.println("ol " + ol.getString(PATH) + " ("
            // + ol.getInteger("mnn.x") + "," + ol.getInteger("mnn.y") + ") ");
            //
            // System.out.println("or " + or.getString(PATH) + " ("
            // + or.getInteger("mnn.x") + "," + or.getInteger("mnn.y") + ") ");

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

        for (Node n : graph.getNodes()) {
            if (n.getInteger("mnn.x") != n.getInteger("mnn.oldx")) {
                compactL = true;
            }

            if (n.getInteger("mnn.y") != n.getInteger("mnn.oldy")) {
                compactU = true;
            }

        }

    }

    private int[] getMinXCoord(Node n) {

        double y = n.getInteger("mnn.y");
        int x = n.getInteger("mnn.x");
        int maxSchnitt = 0;// Integer.MIN_VALUE;
        int minSchnitt = width;

        // System.out.println("--> " + n.getString(PATH) + "("
        // + n.getInteger("mnn.x") + "," + n.getInteger("mnn.y") + ")");

        for (Edge e : getNeighbourNodes(n)) {
            // System.out.println("(" + e.getSource().getString(PATH) + ","
            // + e.getTarget().getString(PATH) + ")");

            double xn1 = e.getSource().getInteger("mnn.x");
            double yn1 = e.getSource().getInteger("mnn.y");

            double xn2 = e.getTarget().getInteger("mnn.x");
            double yn2 = e.getTarget().getInteger("mnn.y");

            if (yn2 - yn1 != 0) {

                double xSchnitt = ((xn2 - xn1) / (yn2 - yn1)) * (y - yn1) + xn1;

                // System.out.println("aaa " + e.getSource().getString(PATH)
                // + " (" + e.getSource().getInteger("mnn.x") + ","
                // + e.getSource().getInteger("mnn.y") + ") " + " "
                // + e.getTarget().getString(PATH) + " ("
                // + e.getTarget().getInteger("mnn.x") + ","
                // + e.getTarget().getInteger("mnn.y") + ") " + " Schnitt: "
                // + xSchnitt);

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

        for (Edge e : addedEdges) {
            graph.deleteEdge(e);
        }
        addedEdges.clear();

        int[] result = new int[2];
        result[0] = maxSchnitt;
        result[1] = minSchnitt;
        // System.out.println("max: " + maxSchnitt + " min: " + minSchnitt);
        return result;
    }

    private int[] getMinYCoord(Node n) {

        int y = n.getInteger("mnn.y");
        double x = n.getInteger("mnn.x");
        int maxSchnitt = 0;
        int minSchnitt = height;

        // System.out.println("--> " + n.getString(PATH) + "("
        // + n.getInteger("mnn.x") + "," + n.getInteger("mnn.y") + ")");

        for (Edge e : getNeighbourNodes(n)) {
            // System.out.println("(" + e.getSource().getString(PATH) + ","
            // + e.getTarget().getString(PATH) + ")");

            double xn1 = e.getSource().getInteger("mnn.x");
            double yn1 = e.getSource().getInteger("mnn.y");

            double xn2 = e.getTarget().getInteger("mnn.x");
            double yn2 = e.getTarget().getInteger("mnn.y");

            if (xn2 - xn1 != 0) {

                double ySchnitt = (yn2 - yn1) / (xn2 - xn1) * x + yn1
                        - (yn2 - yn1) / (xn2 - xn1) * xn1;
                //
                // System.out.println("aaa " + neighbour.getString(PATH)
                // + " (" + neighbour.getInteger("mnn.x") + ","
                // + neighbour.getInteger("mnn.y") + ") " + " "
                // + n2.getString(PATH) + " ("
                // + n2.getInteger("mnn.x") + ","
                // + n2.getInteger("mnn.y") + ") " + " Schnitt: "
                // + ySchnitt);

                if (ySchnitt < y
                        && (int) Math.round(Math.floor(ySchnitt)) + 1 > maxSchnitt) {
                    maxSchnitt = (int) Math.round(Math.floor(ySchnitt)) + 1;
                    // System.out.println("NEW MAX " + maxSchnitt);
                }

                if (ySchnitt > y
                        && (int) Math.round(Math.ceil(ySchnitt)) - 1 < minSchnitt) {
                    minSchnitt = (int) Math.round(Math.ceil(ySchnitt)) - 1;
                    // System.out.println("NEW MIN " + maxSchnitt);
                }
            }

        }

        for (Edge e : addedEdges) {
            graph.deleteEdge(e);
        }

        addedEdges.clear();

        int[] result = new int[2];
        result[0] = maxSchnitt;
        result[1] = minSchnitt;
        // System.out.println("max: " + maxSchnitt + " min: " + minSchnitt);
        return result;
    }

    private LinkedList<Edge> addedEdges = new LinkedList<Edge>();

    /**
     * 
     * @param n
     * @return TODO
     */
    private LinkedList<Edge> getNeighbourNodes(Node n) {

        LinkedList<Edge> result = new LinkedList<Edge>();
        List<Face> faceList = embeddedGraph.getInnerFaces(n);

        for (Face f : faceList) {
            Node n1 = null;
            // Node n2 = null;
            for (Edge e : f.getEdges()) {

                if (e.getSource() != n && e.getTarget() != n) {
                    result.add(e);
                } else {

                    if (n1 == null) {
                        if (e.getSource() == n) {
                            n1 = e.getTarget();
                        } else {
                            n1 = e.getSource();
                        }

                    } else {
                        if (e.getSource() == n) {
                            // n2 = e.getTarget();
                        } else {
                            // n2 = e.getSource();
                        }
                    }
                }

            }
            // Edge ed = graph.addEdge(n1, n2, false);
            // System.out.println("#(" + ed.getSource().getString(PATH) + ","
            // + ed.getTarget().getString(PATH) + ")");
            // addedEdges.add(ed);
            // result.add(ed);
        }

        return result;
    }

    private int lrv(Node p, Node q, Node r) {
        int rx = r.getInteger("mnn.x");
        int ry = r.getInteger("mnn.y");

        int px = p.getInteger("mnn.x");
        int py = p.getInteger("mnn.y");

        int qx = q.getInteger("mnn.x");
        int qy = q.getInteger("mnn.y");

        return ((ry - py) * (qx - px)) - ((rx - px) * (qy - py));
    }

    // private boolean hasCut(Edge e1, Edge e2)
    // {
    // Node e1_a = e1.getSource();
    // Node e1_b = e1.getTarget();
    //
    // Node e2_a = e2.getSource();
    // Node e2_b = e2.getTarget();
    //
    // int lrv_e1_a = lrv(e2_a, e2_b, e1_a);
    // int lrv_e1_b = lrv(e2_a, e2_b, e1_b);
    //
    // int lrv_e2_a = lrv(e1_a, e1_b, e2_a);
    // int lrv_e2_b = lrv(e1_a, e1_b, e2_b);
    //
    // if (((lrv_e1_a <= 0 && lrv_e1_b >= 0) || (lrv_e1_a >= 0 && lrv_e1_b <=
    // 0))
    // && ((lrv_e2_a <= 0 && lrv_e2_b >= 0) || (lrv_e2_a >= 0 && lrv_e2_b <=
    // 0)))
    // {
    //
    // if (e1_a != e2_a && e1_a != e2_b && e1_b != e2_a && e1_b != e2_b)
    // {
    // if (lrv_e1_a == 0 && lrv_e1_b == 0 && lrv_e2_a == 0
    // && lrv_e2_b == 0)
    // {
    // return false;
    // }
    // return true;
    // }
    // else
    // {
    // return false;
    // }
    // }
    // else
    // {
    // return false;
    // }
    // }

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

    private boolean isLegalPosition(Node n) {

        if (n.getInteger("mnn.x") < 0)
            return false;

        for (Edge e1 : getNeighbourNodes(n)) {
            for (Edge e2 : n.getEdges()) {

                if (hasCut(e1, e2))
                    // System.out.println("SCHNITT!");
                    return false;

                if (touches(e2, e1.getSource()))
                    return false;

                if (touches(e2, e1.getTarget()))
                    return false;
            }

            if (touches(e1, n))
                return false;

            // System.out.println("NODE TEST "
            // + lrv(e1.getSource(), e1.getTarget(), n) + " "
            // + e1.getSource().getString(PATH) + " ("
            // + e1.getSource().getInteger("mnn.x") + ","
            // + e1.getSource().getInteger("mnn.y") + ")" + " , "
            // + e1.getTarget().getString(PATH) + " ("
            // + e1.getTarget().getInteger("mnn.x") + ","
            // + e1.getTarget().getInteger("mnn.y") + ")");

        }

        for (Edge e : addedEdges) {
            graph.deleteEdge(e);
        }

        addedEdges.clear();
        return true;
    }

    private boolean touches(Edge e, Node n) {

        if (lrv(e.getSource(), e.getTarget(), n) == 0) {

            int esx = e.getSource().getInteger("mnn.x");
            int esy = e.getSource().getInteger("mnn.y");
            int etx = e.getTarget().getInteger("mnn.x");
            int ety = e.getTarget().getInteger("mnn.y");

            int minx = Math.min(esx, etx);
            int maxx = Math.max(esx, etx);
            int miny = Math.min(esy, ety);
            int maxy = Math.max(esy, ety);

            if (n.getInteger("mnn.x") >= minx && n.getInteger("mnn.x") <= maxx
                    && n.getInteger("mnn.y") >= miny
                    && n.getInteger("mnn.y") <= maxy) {

                // BEACHTE nicht der gleiche punkt
                if (e.getSource() == n || e.getTarget() == n)
                    return false;

                return true;

            } else
                return false;

        } else
            return false;
    }

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

    /**
     * If, user clicks the next iteration step.
     */
    public final static String NEXT_STEP = " >> ";

    /**
     * If, user clicks complete without step view. Algorithm runs without the
     * rest steps.
     */
    public final static String COMPLETE_WITHOUT_STEP_VIEW = "complete without stepview";

    private String mode = "";

}
