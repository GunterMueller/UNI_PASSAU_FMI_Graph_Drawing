/*
 * Created on 11.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.graffiti.plugins.algorithms.treedrawings.ReingoldTilford;

import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.DockingAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.graphics.Port;
import org.graffiti.graphics.PortsAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.algorithm.PreconditionException.Entry;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.treedrawings.GraphChecker;
import org.graffiti.plugins.views.defaults.PolyLineEdgeShape;
import org.graffiti.plugins.views.defaults.StraightLineEdgeShape;
import org.graffiti.selection.Selection;

/**
 * @author Beiqi
 * 
 *         Create a tree drawing with Reingold-Tilford Algorithm.
 */
public class ReingoldTilfordAlgorithm extends AbstractAlgorithm {

    /** The root of the tree. */
    private Node root = null;

    /** Selection */
    private Selection selection;

    /** global/individual node dimension */
    private StringSelectionParameter nodeDimensionParam;
    /** width of the global nodes */
    private IntegerParameter nodeWidthParam;
    /** height of the global nodes */
    private IntegerParameter nodeHeightParam;
    /** node-node distance */
    private IntegerParameter nodeNodeDistanceParam;
    /** global/local level */
    private StringSelectionParameter levelParam;
    /** level distance */
    private IntegerParameter levelDistanceParam;
    /** edge layout */
    private StringSelectionParameter edgeLayoutParam;
    /** port */
    private StringSelectionParameter portParam;
    /** node-edge distance */
    private IntegerParameter nodeEdgeDistanceParam;
    /** heuristic choice **/
    private StringSelectionParameter heuristicParam;

    /** TESTING VARIABLES - THESE CAN BE REMOVED LATED */
    private double widthOfGraph = 0;
    private long timeStart = 0;
    private long timeEnd = 0;

    /**
     * Constructs a new instance.
     */
    public ReingoldTilfordAlgorithm() {
        String[] nodeDimensionOptions = { "INDIVIDUAL", "DEFAULT" };
        nodeDimensionParam = new StringSelectionParameter(nodeDimensionOptions,
                "Node Dimension Mode:",
                "Select the mode of the node dimension.");

        nodeWidthParam = new IntegerParameter(
                new Integer(25),
                new Integer(0),
                new Integer(100),
                "Default Width:",
                "<html>Width of the node,"
                        + "<p>work only in default mode of Node Dimension.</p></html>");

        nodeHeightParam = new IntegerParameter(
                new Integer(25),
                new Integer(0),
                new Integer(100),
                "Default Height:",
                "<html>Height of the node,"
                        + "<p>work only in default mode of Node Dimension.</p></html>");

        nodeNodeDistanceParam = new IntegerParameter(new Integer(25),
                new Integer(0), new Integer(100), "Node-Node-Distance:",
                "Minimal distance between two neighboured nodes.");

        nodeEdgeDistanceParam = new IntegerParameter(new Integer(3),
                new Integer(0), new Integer(10), "Node-Edge-Distance:",
                "Minimal distance between node and edge.");

        String[] levelOptions = { "GLOBAL", "LOCAL" };
        levelParam = new StringSelectionParameter(levelOptions, "Level Mode:",
                "Select the mode of the level for this graph.");

        levelDistanceParam = new IntegerParameter(
                new Integer(50),
                new Integer(0),
                new Integer(200),
                "Level Distance:",
                "<html><p>GLOBAL: The distance between two above lines of"
                        + "nodes from two hierarachy levels.</p>"
                        + "<p>LOCAL: The distance between below and above line of "
                        + "nodes from two hierarchy levels.</p></html>");

        String[] edgeLayouts = { "STRAIGHT_LINE", "BUSLAYOUT" };
        edgeLayoutParam = new StringSelectionParameter(edgeLayouts,
                "Edge Layout:", "Select the edge layout.");

        String[] ports = { "CENTER", "OUTGOING; INGOING" };
        portParam = new StringSelectionParameter(ports, "Port:",
                "<html><p>CENTER(source & target): center</p>"
                        + "<p>OUTGOING(source): below_middle</p>"
                        + "<p>INGOING(target): above_middle</p>" + "</html>");
        // portParam.setSelectedValue(1);

        String[] heuristics = { "No", "Heuristic 1", "Heuristic 2" };
        heuristicParam = new StringSelectionParameter(heuristics,
                "Heuristics:", "<html><p>Choose heuristic methode:</p>"
                        + "<p>Heuristic 1 - swap children</p>"
                        + "<p>Heuristic 2 - sort children</p></html>");
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "ReingoldTilfordAlgorithm";
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {

        SelectionParameter seleParam = new SelectionParameter("Root:",
                "Root of this tree.");
        return new Parameter[] { seleParam, nodeDimensionParam, nodeWidthParam,
                nodeHeightParam, nodeNodeDistanceParam, nodeEdgeDistanceParam,
                levelParam, levelDistanceParam, edgeLayoutParam, portParam,
                heuristicParam };

    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {

        this.parameters = params;
        this.selection = ((SelectionParameter) params[0]).getSelection();

    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        Node root = null;
        try {
            root = GraphChecker.checkTree(this.graph, Integer.MAX_VALUE);
        } catch (PreconditionException p) {
            this.selection.clear();

            Iterator<Entry> itr = p.iterator();
            while (itr.hasNext()) {
                Selection selection = (Selection) itr.next().source;
                if (selection != null) {
                    this.selection.addSelection(selection);
                }
            }
            throw p;
        }

        this.root = root;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute() The given graph
     *      must have at least one node.
     */
    public void execute() {

        if (selection != null) {
            selection.clear();
        }
        if (root == null)
            throw new RuntimeException("Must call method \"check\" before "
                    + " calling \"execute\".");

        if ((graph.getNumberOfNodes() > 0)) {

            // set ports for every node
            portParamHandler(portParam.getSelectedIndex());
            // set node dimention by default mode
            nodeDefaultDimensionHandler(nodeDimensionParam.getSelectedValue());

            RTNode rootRTNode = rootHandler(root);

            FirstWalk fw = new FirstWalk(nodeNodeDistanceParam.getInteger(),
                    nodeEdgeDistanceParam.getInteger(), levelParam
                            .getSelectedIndex(), levelDistanceParam
                            .getInteger(), edgeLayoutParam.getSelectedIndex(),
                    portParam.getSelectedIndex(), heuristicParam
                            .getSelectedIndex());

            timeStart = System.currentTimeMillis();
            fw.firstWalk(rootRTNode, 0, 0);

            widthOfGraph = rootRTNode.getMaxXP().xValue
                    - rootRTNode.getMinXP().xValue;

            SecondWalk sw = new SecondWalk();
            sw.secondWalk(rootRTNode, -rootRTNode.getMinXP().xValue);

            timeEnd = System.currentTimeMillis();

            System.out.println("runTime: " + (timeEnd - timeStart)
                    + " millis\twidthOfGraph: " + widthOfGraph + "\n");
            // set edges for the graph
            edgeLayoutParamHandler(edgeLayoutParam.getSelectedIndex());

        }
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {

        // super.reset();
        graph = null;
        selection = null;

        // selection.clear();
        nodeDimensionParam.setSelectedValue(0);
        // nodeWidthParam.setValue(new Integer(25));
        // nodeHeightParam.setValue(new Integer(25));
        // levelParam.setSelectedValue(0);
        // levelDistanceParam.setValue(new Integer(50));
        // levelDistance = 0;
        // portParam.setSelectedValue(0);

    }

    private void nodeDefaultDimensionHandler(String type) {

        if (type == "DEFAULT") {
            DimensionAttribute da;
            String att = GraphicAttributeConstants.GRAPHICS
                    + Attribute.SEPARATOR + GraphicAttributeConstants.DIMENSION;
            double width = nodeWidthParam.getInteger().doubleValue();
            double height = nodeHeightParam.getInteger().doubleValue();
            Iterator<Node> nodeIt = graph.getNodesIterator();
            while (nodeIt.hasNext()) {
                Node n = nodeIt.next();
                da = (DimensionAttribute) n.getAttribute(att);
                da.setDimension(width, height);
            }
        }

    }

    private void portParamHandler(int index) {
        // set port value to every node and their docking.
        if (index == 1) {
            Iterator<Node> nodesIt = graph.getNodesIterator();
            while (nodesIt.hasNext()) {
                Node node = nodesIt.next();
                PortsAttribute portsAttr = (PortsAttribute) node
                        .getAttribute("graphics.ports");
                LinkedList<Port> ports = new LinkedList<Port>();
                ports.add(new Port("middle_above", 0, -1));
                ports.add(new Port("middle_below", 0, 1));
                portsAttr.setCommonPorts(ports);
            }

            Iterator<Edge> edgesIt = graph.getEdgesIterator();
            while (edgesIt.hasNext()) {
                Edge edge = edgesIt.next();
                DockingAttribute docking = (DockingAttribute) edge
                        .getAttribute("graphics.docking");
                docking.setSource("middle_below");
                docking.setTarget("middle_above");
            }
        } else {
            Iterator<Edge> edgesIt = graph.getEdgesIterator();
            while (edgesIt.hasNext()) {
                Edge edge = edgesIt.next();
                DockingAttribute docking = (DockingAttribute) edge
                        .getAttribute("graphics.docking");
                docking.setSource("");
                docking.setTarget("");
            }
        }
    }

    private RTNode rootHandler(Node root) {
        RTNode rootRTNode = new RTNode(root);

        if (rootRTNode.getNumberOfChildren() >= 1) {
            for (int i = 0; i < rootRTNode.getNumberOfChildren(); i++) {
                RTNode rtNode = new RTNode((Node) rootRTNode.getChildren().get(
                        i));
                rootRTNode.getChildren().set(i, rtNode);
                nodeHandler(rtNode);
            }
        }

        return rootRTNode;
    }

    private void nodeHandler(RTNode rtNode) {
        if (rtNode == null)
            return;
        else if (rtNode.getNumberOfChildren() >= 1) {
            for (int j = 0; j < rtNode.getChildren().size(); j++) {
                RTNode n = new RTNode((Node) rtNode.getChildren().get(j));
                rtNode.getChildren().set(j, n);
                nodeHandler(n);
            }
        }
    }

    private void edgeLayoutParamHandler(int index) {

        NodeGraphicAttribute na1, na2;
        CoordinateAttribute ca1, ca2;
        DimensionAttribute da1, da2;
        Iterator<Edge> eIt = graph.getEdgesIterator();
        while (eIt.hasNext()) {
            Edge edge = eIt.next();
            EdgeGraphicAttribute edgeAttr = (EdgeGraphicAttribute) edge
                    .getAttribute("graphics");
            SortedCollectionAttribute bends = new LinkedHashMapAttribute(
                    "bends");
            if (index == 1) {
                na1 = (NodeGraphicAttribute) edge.getSource().getAttribute(
                        "graphics");
                na2 = (NodeGraphicAttribute) edge.getTarget().getAttribute(
                        "graphics");
                ca1 = na1.getCoordinate();
                ca2 = na2.getCoordinate();
                da1 = na1.getDimension();
                da2 = na2.getDimension();
                double adj = ((ca2.getY() - da2.getHeight() / 2) - (ca1.getY() + da1
                        .getHeight() / 2)) / 2;

                bends.add(new CoordinateAttribute("bend0", new Point2D.Double(
                        ca1.getX(), ca1.getY() + da1.getHeight() / 2 + adj)));
                bends.add(new CoordinateAttribute("bend1", new Point2D.Double(
                        ca2.getX(), ca2.getY() - da2.getHeight() / 2 - adj)));

                edgeAttr.setBends(bends);
                edgeAttr.setShape(PolyLineEdgeShape.class.getName());
            } else {
                edgeAttr.setBends(bends);
                edgeAttr.setShape(StraightLineEdgeShape.class.getName());
            }
        }
    }

    public StringSelectionParameter getHeuristicParam() {
        return heuristicParam;
    }

    public StringSelectionParameter getPortParam() {
        return portParam;
    }

    public double getWidthOfGraph() {
        return widthOfGraph;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
