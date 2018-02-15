// =============================================================================
//
//   TreeWidthAlgorithm.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treeWidth;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.CompositeAttribute;
import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.ProbabilityParameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.selection.Selection;
import org.graffiti.session.EditorSession;
import org.graffiti.util.Queue;

/**
 * TreeWidthAlgorithm implements an extended version of the Minimum Fill-in
 * algorithm and a Minimum Fill-in excluding one neighbor algorithm, which lays
 * out a rooted treeDecompostion. The behavior is influenced by various
 * parameters:
 * <ul>
 * <li><b>makeParam</b> :Return <tt>true</tt> here, if the graph is random,
 * <tt>false</tt>, if the graph is given.
 * <li><b>nodesParam</b> :is the number of nodes for the random graph.
 * <li><b>probabilityParam</b> :is the probability of edges for the random
 * graph.
 * <li><b>firstLabelParam</b> :is the first label for the node.
 * </ul>
 * 
 * @author wangq
 * @version $Revision$ $Date$
 */
public class TreeWidthAlgorithm extends AbstractAlgorithm {
    private int maxLabel = 1;
    /** DOCUMENT ME! */
    /**
     * logger Data
     */
    ;
    private static final Logger logger = Logger
            .getLogger(TreeWidthAlgorithm.class.getName());
    /**
     * algorithm1 minimum fill-in
     */
    private static final String algorithm1 = "minimal Fill-in " + "Heuristik";
    /**
     * algorithm2 MFEO1
     */
    private static final String algorithm2 = "MFEO1 Heuristik";
    /** make parameter */
    private BooleanParameter makeParam;
    /** the node's minimum label */
    private IntegerParameter firstLabelParam;
    /** number of nodes */
    private IntegerParameter nodesParam;
    /** probability parameter of edges */
    private ProbabilityParameter probabilityParam;
    /** selection */
    private Selection selection;
    /** matrixGraph for original graph */
    private MatrixGraph graphMap;
    /** the size of maximum label */
    private int labelSize;

    /**
     * Constructs a new instance.
     */
    public TreeWidthAlgorithm() {
        makeParam = new BooleanParameter(false, "random graph",
                "Use a random graph");

        nodesParam = new IntegerParameter(new Integer(5), new Integer(0),
                new Integer(100), "number of nodes",
                "the number of nodes to generate in the random graph");

        probabilityParam = new ProbabilityParameter(0.5,
                "Probability of edges",
                "probability of edge generation in the random graph");

        firstLabelParam = new IntegerParameter(new Integer(1), "Start value",
                "Labelling will start with this number.");
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Tree Width";
    }

    /**
     * @see org.graffiti.plugin.algorithm
     *      .Algorithm#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        selection = ((SelectionParameter) params[0]).getSelection();
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        SelectionParameter selParam = new SelectionParameter("Selection",
                "<html>The selection to work on.<p>If empty, "
                        + "the whole graph is used.</html>");
        selParam.setSelection(new Selection("_temp_"));
        String[] algParams = { algorithm1, algorithm2 };
        StringSelectionParameter algorithm = new StringSelectionParameter(
                algParams, "ALGORITHM", "select a algorithm");
        return new Parameter[] { selParam, algorithm, makeParam, nodesParam,
                probabilityParam, firstLabelParam };
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {

        PreconditionException errors = new PreconditionException();
        if (this.makeParam.getBoolean().booleanValue()) {
            if (nodesParam.getInteger().compareTo(new Integer(2)) < 0) {
                errors.add("The number of nodes may not be smaller "
                        + "than two.");
            }

            double probaTest = 2.0 / nodesParam.getInteger();

            if (probabilityParam.getProbability().compareTo(probaTest) < 0) {
                errors.add("If the Graph is connected, "
                        + "the probability may not be smaller than "
                        + probaTest);
            }

            if (probabilityParam.getProbability().compareTo(new Double(1.0)) > 0) {
                errors.add("The probability may not be greater " + "than 1.0.");
            }

            if (probabilityParam.getProbability().compareTo(new Double(0.0)) < 0) {
                errors.add("The probability may not be less than 0.0.");
            }

            if (firstLabelParam.getInteger().compareTo(new Integer(1)) < 0) {
                errors.add("The label may not be less than 1.");
            }

        } else {

            if (graph.getNumberOfNodes() == 0) {
                errors.add(" The Graph is empty");
            }

            if (graph.getNumberOfNodes() == 1) {
                errors.add(" The Graph is too small");
            }

            if (!testLabelOfNodes()) {
                errors.add(" the label of the nodes may not empty.");
            }
            if (graph.getNumberOfNodes() >= 1) {

                if (!testConnected()) {
                    errors.add("The graph is not connected.");
                }
                if (graph.isDirected() == true) {
                    errors.add("The graph may not be directed");
                }
            }

        }
        if (!errors.isEmpty())
            throw errors;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        graph = null;
        nodesParam.setValue(new Integer(5));
        probabilityParam.setValue(new Double(0.5));
        firstLabelParam.setValue(new Integer(1));
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        graph.getListenerManager().transactionStarted(this);
        if (this.makeParam.getBoolean().booleanValue()) {
            graph.clear();
            generator();
            labelNodes(graph.getNodes(), firstLabelParam.getValue().intValue());
        }

        if (this.parameters[1].getValue() == algorithm1) {
            this.graphMap = new MatrixGraph(graph);
            this.graphMap.createMatrixGraph();
            MinimumFillIn minFill = new MinimumFillIn(this.graphMap, graph);
            minFill.calculateFillin();
            labelSize = minFill.getUw();
            logger.log(Level.INFO, "\t >>>>>>>>>> Upper Bound is  "
                    + minFill.getUw() + " <<<<<<<<<<");
            newSession();
            graph.getListenerManager().transactionFinished(this);
            graph.clear();
            graph.getListenerManager().transactionStarted(this);
            /** Creates treeDecomposition */
            display(minFill.getTwNodes());
            graph.getListenerManager().transactionFinished(this);
        } else {
            this.graphMap = new MatrixGraph(this.graph);
            this.graphMap.createMatrixGraph();
            MinimumFillIn minFill1 = new MinimumFillIn(this.graphMap,
                    this.graph);
            minFill1.calculateFillin1();
            labelSize = minFill1.getUw();
            newSession();
            logger.log(Level.INFO, "\t >>>>>>>>>> lower Bound is  "
                    + minFill1.getLw() + " <<<<<<<<<<");
            logger.log(Level.INFO, "\t >>>>>>>>>> Upper Bound is  "
                    + minFill1.getUw() + " <<<<<<<<<<");
            graph.getListenerManager().transactionFinished(this);
            graph.clear();
            graph.getListenerManager().transactionStarted(this);
            /** Creates treeDecomposition */
            display(minFill1.getTwNodes());
            graph.getListenerManager().transactionFinished(this);
        }
    }

    /**
     * Uses ReingoldTilfordAlgorithm, which draws a reordner tree.
     */
    private void ReingoldTilfordAlgorithm() {
        ReingoldTilfordAlgorithm rt = new ReingoldTilfordAlgorithm();
        rt.attach(graph);
        rt.setAlgorithmParameters(rt.getAlgorithmParameters());
        try {
            rt.check();
        } catch (PreconditionException e) {
            throw new RuntimeException(e);
        }
        rt.execute();

    }

    /**
     * Creates a new Session, which creates a new window for the original graph.
     * 
     */
    private void newSession() {
        GraffitiSingleton gs = GraffitiSingleton.getInstance();
        EditorSession graphSession = gs.getMainFrame().getActiveEditorSession();
        EditorSession treeWidthSession = new EditorSession();
        Graph treewidthGraph = treeWidthSession.getGraph();
        /** copy original graph */
        treewidthGraph.addGraph(this.graph);
        this.graph.setDirected(false);
        gs.getMainFrame().showViewChooserDialog(treeWidthSession, false);
    }

    /**
     * Builds the treeDecompostion, which searches the neighbor of every
     * treeWidthNode
     * 
     * @param twNodes
     *            all treeWidthNodes
     */
    private void display(ArrayList<TreeWidthNode> twNodes) {
        graph.setDirected(true);
        for (int j = 0; j < twNodes.size(); j++) {
            TreeWidthNode x = twNodes.get(j);
            Node nodex = graph.addNode();
            numberNodes(nodex, x);
        }
        for (int j = 0; j < twNodes.size() - 1; j++) {
            TreeWidthNode x = twNodes.get(j);
            TreeWidthNode y = x.makeOneNg(twNodes);
            graph.addEdge(y.getGraphNode(), x.getGraphNode(), true);
        }
        ReingoldTilfordAlgorithm();
    }

    /**
     * Test, if the graph is completely labeled
     * 
     */
    private boolean testLabelOfNodes() {
        for (int i = 0; i < graph.getNumberOfNodes(); i++) {
            Node node = graph.getNodes().get(i);
            try {
                LabelAttribute labelAttr = (LabelAttribute) searchForAttribute(
                        node.getAttribute(""), LabelAttribute.class);
                if (maxLabel < labelAttr.getLabel().length()) {
                    maxLabel = labelAttr.getLabel().length();
                }
            } catch (NullPointerException anfe) {
                return false;
            }
        }
        return true;
    }

    /**
     * Test, if the graph is connected
     * 
     */
    private boolean testConnected() {

        boolean connected = false;
        Node sourceNode = graph.getNodes().get(0);
        Queue q = new Queue();
        /** d contains a mapping from node to an integer, the bfsnum */
        Set<Node> visited = new HashSet<Node>();
        q.addLast(sourceNode);
        visited.add(sourceNode);
        while (!q.isEmpty()) {
            Node v = (Node) q.removeFirst();
            /**
             * mark all neighbors and add all unmarked neighbors of v to the
             * queue
             */
            for (Iterator<Node> neighbours = v.getNeighborsIterator(); neighbours
                    .hasNext();) {
                Node neighbour = neighbours.next();
                if (!visited.contains(neighbour)) {
                    visited.add(neighbour);
                    q.addLast(neighbour);
                }
            }
        }

        if (graph.getNodes().size() == visited.size()) {
            connected = true;
        }
        return connected;
    }

    /**
     * Generate the nodes in the graph
     * 
     * @param nodeNumber
     *            the number of nodes that would be inserted into graph.
     */
    private void addNodes(int nodeNumber) {
        Node[] node = new Node[nodeNumber];
        HashMap<Double, Object> xCoor = new HashMap<Double, Object>();
        HashMap<Double, Object> yCoor = new HashMap<Double, Object>();
        // generate nodes and assign coordinates to them
        for (int i = 0; i < nodeNumber; ++i) {
            node[i] = graph.addNode();
            double x = getXYCoordinate(xCoor);
            double y = getXYCoordinate(yCoor);
            CoordinateAttribute ca = (CoordinateAttribute) node[i]
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COORDINATE);
            ca.setCoordinate(new Point2D.Double(x, y));
        }
    }

    /**
     * Generate the nodes coordinates
     * 
     * @param xyCoor
     */
    private double getXYCoordinate(HashMap<Double, Object> xyCoor) {
        boolean seek = true;
        double result = 0.0;
        while (seek) {
            result = Math.random();
            result = result * 1000;
            Double key = new Double(result);
            if (!xyCoor.containsKey(key)) {
                xyCoor.put(key, null);
                seek = false;
            }
        }
        return result;
    }

    /**
     * Label all nodes in the graph
     * 
     * @param auxN
     *            The Iterator of nodes in the current graph.
     * @param labelList
     *            Array of the Tree Width Nodes that stores the result
     * 
     */
    private void numberNodes(Node auxN, TreeWidthNode labelList) {
        int number = 0;
        NodeLabelAttribute labelAttr;
        NodeGraphicAttribute ngb = (NodeGraphicAttribute) auxN
                .getAttribute("graphics");
        ngb.getDimension().setWidth(20 * labelSize * maxLabel);
        String LABEL_PATH;
        if (GraphicAttributeConstants.LABEL_ATTRIBUTE_PATH.equals("")) {
            LABEL_PATH = GraphicAttributeConstants.LABEL;
        } else {
            LABEL_PATH = GraphicAttributeConstants.LABEL_ATTRIBUTE_PATH
                    + Attribute.SEPARATOR + GraphicAttributeConstants.LABEL;
        }
        String labelName = "";
        for (int i = 0; i < labelList.getNodes().size(); i++) {
            Node oriNode = ((labelList).getNodes()).get(i);
            LabelAttribute labelAttr1 = (LabelAttribute) searchForAttribute(
                    oriNode.getAttribute(""), LabelAttribute.class);
            labelName += labelAttr1.getLabel() + ",";
            labelList.setGraphNode(auxN);
        }
        labelAttr = new NodeLabelAttribute(GraphicAttributeConstants.LABEL,
                labelName);
        auxN.addAttribute(labelAttr,
                GraphicAttributeConstants.LABEL_ATTRIBUTE_PATH);
    }

    /**
     * Create a graph as an example. The parameters are specified by the user.
     * 
     */
    private void generator() {
        /** add nodes */
        this.addNodes(nodesParam.getInteger().intValue());
        double p = probabilityParam.getProbability().doubleValue();
        int edgeNr = new Integer((int) Math.round((nodesParam.getInteger()
                .intValue() * (nodesParam.getInteger().intValue() - 1))
                / 2 * p)).intValue();
        /** add edges */
        this.generatoraddEdge(edgeNr);
    }

    /**
     * Generate the edges in the sample graph
     * 
     * @param edgeNumber
     *            the number of edges to be inserted into graph.
     */
    public void generatoraddEdge(int edgeNumber) {
        List<Node> nodesList = graph.getNodes();
        List<Node> nodesListCon = new ArrayList<Node>();
        graph.addEdge(graph.getNodes().get(0), graph.getNodes().get(1), false);
        nodesListCon.add(graph.getNodes().get(0));
        nodesListCon.add(graph.getNodes().get(1));
        int i = 2;
        while (i < nodesList.size()) {
            Node n11 = nodesList.get(i);
            int pos1 = getRandomPos(nodesListCon.size());
            Node n21 = nodesList.get(pos1);
            graph.addEdge(n11, n21, false);
            i++;
            nodesListCon.add(n11);
        }
        int in = edgeNumber - nodesList.size() + 1;
        while (in > 0) {
            int pos1 = getRandomPos(nodesList.size());
            Node n1 = nodesList.get(pos1);
            int pos2 = getRandomPos(nodesList.size());
            Node n2 = nodesList.get(pos2);
            if (!n1.getAllInNeighbors().contains(n2) && !n1.equals(n2)) {
                graph.addEdge(n1, n2, false);
                in--;
            }
        }
        graph.setDirected(false);
    }

    /**
     * Returns Nodes' position
     * 
     * @param length
     * 
     */
    public int getRandomPos(int length) {
        double random = Math.random();
        Float flo = new Float((length - 1) * random);
        int pos = Math.round(flo.floatValue());
        return pos;
    }

    /**
     * Sets the nodes' labels beginning with the specified start number.
     * 
     * @param nodes
     *            The nodes to label.
     * @param startNumber
     *            The labeling starts with this number.
     */
    protected void labelNodes(Collection<Node> nodes, int startNumber) {
        int number = startNumber;
        for (Node node : nodes) {
            NodeLabelAttribute labelAttr = new NodeLabelAttribute(
                    GraphicAttributeConstants.LABEL, String.valueOf(number));
            node.addAttribute(labelAttr,
                    GraphicAttributeConstants.LABEL_ATTRIBUTE_PATH);
            number++;
        }
        String s = Integer.toString(startNumber);
        maxLabel = s.length();
    }

    /**
     * Specifies the attribute for the node
     * 
     * @param attr
     * @param attributeType
     * @return Attribute
     */
    public Attribute searchForAttribute(Attribute attr,
            Class<? extends Attribute> attributeType) {
        if (attributeType.isInstance(attr))
            return attr;
        else {
            if (attr instanceof CollectionAttribute) {
                Iterator<Attribute> it = ((CollectionAttribute) attr)
                        .getCollection().values().iterator();

                while (it.hasNext()) {
                    Attribute newAttr = searchForAttribute(it.next(),
                            attributeType);

                    if (newAttr != null)
                        return newAttr;
                }
            } else if (attr instanceof CompositeAttribute)
                // TODO: treat those correctly; some of those have not yet
                // been correctly implemented
                return null;
        }
        return null;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
