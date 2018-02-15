// =============================================================================
//
//   SDlayout.java
//
//   Copyright (c) 2001-2014, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.SDlayout;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.AlgorithmResult;
import org.graffiti.plugin.algorithm.CalculatingAlgorithm;
import org.graffiti.plugin.algorithm.DefaultAlgorithmResult;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.connectivity.Connect;
import org.graffiti.plugins.algorithms.extendedDFS.ExtendedDFS;
import org.graffiti.plugins.algorithms.generators.PlanarTriconnectedGraphGenerator;
import org.graffiti.plugins.algorithms.rotation.Rotationsystem;
import org.graffiti.plugins.algorithms.treedrawings.HexaGridPentaTree.Vector;
import org.graffiti.plugins.views.defaults.PolyLineEdgeShape;
import org.graffiti.selection.Selection;

/**
 * This class implements the isSDLayout algorithm from Christopher Auer. It
 * calculates, if a graph with a given rotationsystem is planar with using a
 * splitable deque.
 * 
 * @author Christina Ehrlinger
 * @version $Revision$ $Date$
 */
public class SDlayout extends AbstractAlgorithm implements CalculatingAlgorithm {

    /**
     * String for the error in processVertex()
     */
    private static final String PROCESS_VERTEX = "An edge, which should be removed, is not at the head (tail) of the deque.";

    /**
     * String for the error, if the deque can not be splitted
     */
    private static final String SPLIT_1 = "Split of the deque was not possible.";

    /**
     * String for the error, if the deque after splitDeque() is not empty
     */
    private static final String SPLIT_2 = "After the split, the deque was not emtpy.";

    /**
     * String for the error, if the removed child deque is not empty
     */
    private static final String NOT_EMTPY = "The deque of an nested sector was not emtpy.";

    /**
     * String for the error in the nesting
     */
    private static final String NESTING = "The sectors are interlacing.";

    /**
     * String for the error, if the deque after a leaf ist not empty
     */
    private static final String LEAF_NOT_EMTPY = "The deque of a leaf was not empty.";

    /**
     * attribute for the current error
     */
    private String status;

    /**
     * attribute for the parameter, if a graph should be generated
     */
    private BooleanParameter generateParameter;

    /**
     * attribute for the parameter, if the created graph should have SD-Layout
     */
    private BooleanParameter sdLayoutParameter;

    /**
     * attribute for the parameter list
     */
    private LinkedList<Parameter<?>> parameterList;

    /**
     * attribute for the parameter, if the tree edges should be marked
     */
    private BooleanParameter markTreeEdges;

    /**
     * attribute for the parameter, if the cross edges should be marked
     */
    private BooleanParameter markCrossEdges;

    /**
     * attribute for the parameter, which node should be the start node
     */
    private SelectionParameter selParam;

    /**
     * attribute for the parameter for the number of the created nodes
     */
    private IntegerParameter numberOfNodesParam;

    /**
     * attribute for the start node of the dfs algorithm
     */
    private Selection selection;

    /**
     * attribute for the parameter for the order of the edges for DFS
     */
    private StringSelectionParameter edgesParameter;

    /**
     * attribute for the graph generator algorithm
     */
    private PlanarTriconnectedGraphGenerator graphGenerator;

    /**
     * attribute for the rotation system algorithm
     */
    private Rotationsystem rotationalgorithm;

    /**
     * attribute for the connectivity algorithm
     */
    private Connect connectalgorithm;

    /**
     * attribute for the DFS algorithm
     */
    private ExtendedDFS dfsalgorithm;

    /**
     * attribute for the nodes, sorted by the dfs number
     */
    private List<Node> sortedNodes;

    /**
     * attribute for the tree edges of the graph
     */
    private List<Edge> treeEdges;

    /**
     * attribute for the edges, which crosses
     */
    private List<Edge> crossEdges;

    /**
     * attribute for the edge at the current sector
     */
    private Map<Integer, List<Edge>> sectorEdges;

    /**
     * attribute for the duration of the main algorithm
     */
    private long duration;

    /**
     * attribute for the number of splits
     */
    private int splitCounter;

    /**
     * attribute if the graph has a SD Layout
     */
    private boolean isSDLayoutResult;

    /**
     * The constructor creates a SDLayout instance and all of the parameters
     */
    protected SDlayout() {
        duration = 0;
        parameterList = new LinkedList<Parameter<?>>();

        String[] edgesOrder = { ExtendedDFS.COUNTERCLOCK, ExtendedDFS.RANDOM,
                ExtendedDFS.DRAWING };
        edgesParameter = new StringSelectionParameter(edgesOrder,
                "Order of the edges",
                "In which order should the edges be processed for the DFS-tree?");
        markTreeEdges = new BooleanParameter(true, "Mark the tree edges",
                "Mark the tree edges, which are used by the algorithm");
        markCrossEdges = new BooleanParameter(true, "Mark the crossed edges",
                "Mark the edges, which causes the first cross of edges");
        selParam = new SelectionParameter("Start node",
                "SD Layout will start with the only selected node.");
        numberOfNodesParam = new IntegerParameter(9, "Number of nodes",
                "maximum number of the nodes, which are created (Minimum is 9)");
        generateParameter = new BooleanParameter(true, "Generate",
                "Generate a random graph");
        sdLayoutParameter = new BooleanParameter(true, "SD-Layout",
                "Generated graph has SD-Layout");

        parameterList.addFirst(selParam);
        parameterList.addFirst(edgesParameter);
        parameterList.addFirst(markTreeEdges);
        parameterList.addFirst(markCrossEdges);
        parameterList.addFirst(numberOfNodesParam);
        parameterList.addFirst(sdLayoutParameter);
        parameterList.addFirst(generateParameter);
    }

    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        if (numberOfNodesParam.getValue() < 9) {
            errors.add("The minimum number of nodes is 9!");
        }

        // The graph is inherited from AbstractAlgorithm.
        if (!generateParameter.getValue()) {
            if (graph == null) {
                errors.add("The graph instance may not be null.");

            } else if (graph.isEmpty()) {
                errors.add("The graph may not be emtpy.");
            }

            selection = selParam.getValue();
            if ((selection == null) || (selection.getNodes().size() > 1))
                errors.add("SD needs one selected node for start or a random node is chosen.");

            // graph has to be connected
            connectalgorithm = new Connect();
            connectalgorithm.attach(graph);
            connectalgorithm.execute();
            AlgorithmResult resultConnect = connectalgorithm.getResult();
            if (resultConnect.getResult().get("connected").equals(false)) {
                errors.add("The Graph has to be connected.");
            }
        }

        if (!errors.isEmpty()) {
            throw errors;
        }

        // initialize the attributes
        rotationalgorithm = new Rotationsystem();
        rotationalgorithm.attach(graph);
        graphGenerator = new PlanarTriconnectedGraphGenerator();
        dfsalgorithm = new ExtendedDFS();
        dfsalgorithm.attach(graph);
        status = "";

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        graph.getListenerManager().transactionStarted(this);

        treeEdges = new LinkedList<>();
        sortedNodes = new LinkedList<>();
        crossEdges = new LinkedList<>();
        sectorEdges = new HashMap<Integer, List<Edge>>();
        splitCounter = 0;

        // graph is generated with PlanarTriconnectedGraphGenerator
        if (generateParameter.getValue()) {
            graph.clear();
            graphGenerator.attach(graph);
            Parameter<?>[] paramGenerator = new Parameter<?>[1];
            paramGenerator[0] = numberOfNodesParam;
            graphGenerator.setAlgorithmParameters(paramGenerator);
            graphGenerator.execute();

        }

        if (generateParameter.getValue() || selection.getNodes().isEmpty()) {
            selection = new Selection();
            selection.add(graph.getNodes().get(
                    (int) (Math.random() * graph.getNumberOfNodes())));
        }

        colorEdges(Color.BLACK, graph.getEdges());
        Node startNode = selection.getNodes().get(0);

        // calculates the rotationsystem of the graph
        rotationalgorithm.execute();

        if (!sdLayoutParameter.getValue() && generateParameter.getValue()) {
            createNoSDLayout();
        }

        // selection is given to dfsalgorithm
        Parameter<?>[] dfsParam = new Parameter<?>[2];
        selParam.setValue(selection);
        dfsParam[0] = selParam;
        dfsParam[1] = edgesParameter;
        dfsalgorithm.setAlgorithmParameters(dfsParam);
        dfsalgorithm.execute();

        treeEdges = dfsalgorithm.getTreeEdges();
        if (markTreeEdges.getValue()) {
            colorEdges(Color.GREEN, treeEdges);
        }

        // sorts the node acoording to the dfs number to get a linear layout
        sortedNodes = new LinkedList<>(graph.getNodes());
        Collections.sort(sortedNodes, new Comparator<Node>() {

            @Override
            public int compare(Node o1, Node o2) {
                return o1.getInteger("DFSnumber") - o2.getInteger("DFSnumber");
            }
        });

        for (Edge edge : graph.getEdges()) {
            Attribute currentSD = new ObjectAttribute("currentSD");
            currentSD.setDefaultValue();
            edge.addAttribute(currentSD, "");
        }
        long start = System.nanoTime();
        isSDLayoutResult = isSDLayout(startNode, new LinkedList<Edge>());
        long end = System.nanoTime();
        duration = end - start;
        if (markCrossEdges.getValue()) {
            colorEdges(Color.RED, crossEdges);
        }

        clearAttributes();

        graph.getListenerManager().transactionFinished(this);

    }

    /**
     * Method implements the actual isSDLayout algorithm
     * 
     * In phase 1 the rotation system is resorted. The first edge is the edge to
     * the parent, or for the root the edge to the first child. The current
     * deque is split for the children.
     * 
     * In phase 2 is proofed, if the sectors are interlacing or disjunct.
     * 
     * In phase 3 the node is processed with the partial rotation system and
     * calculates the input deque for the child.
     * 
     * @param currentNode
     *            : the current node
     * @param deque
     *            : the cooresponding deque
     * @return true, if SDLayout, false, if not SDLayout
     */
    @SuppressWarnings("unchecked")
    private boolean isSDLayout(Node currentNode, Deque<Edge> deque) {
        // rotation system of the current node
        ObjectAttribute rotationsystem = (ObjectAttribute) currentNode
                .getAttribute("rotationsystem");
        List<Edge> rotation = (List<Edge>) rotationsystem.getValue();

        // proof if node has a incoming tree edge
        Edge incoming = calculateIncomingEdge(currentNode);
        int incomingIndex;
        if (incoming != null) {
            incomingIndex = calculateIndexOfTreeEdge(incoming, rotation);
        } else {
            incomingIndex = -1;
        }

        // proof if node has a outgoing tree edge
        List<Edge> outgoing = calculateOutgoingEdges(currentNode);
        if (!outgoing.isEmpty()) {
            calculateIndexOfTreeEdge(outgoing.get(0), rotation);
        }

        // exeption: isolated node
        if (outgoing.isEmpty() && incomingIndex == -1) {
            return true;
        }

        // current node is leaf
        if (outgoing.isEmpty()) {
            calculateHeadEdgesRootLeaf(currentNode, incoming, rotation);
            // head edges of the current node
            ObjectAttribute headEdges = (ObjectAttribute) currentNode
                    .getAttribute("headEdges");

            // tail edges of the current node
            ObjectAttribute tailEdges = (ObjectAttribute) currentNode
                    .getAttribute("tailEdges");

            deque = processVertex(currentNode,
                    (List<Edge>) headEdges.getValue(),
                    (List<Edge>) tailEdges.getValue(), deque);

            if (deque == null || !deque.isEmpty()) {
                if (status.equals("")) {
                    status = "\nAt node " + currentNode + ":\n"
                            + LEAF_NOT_EMTPY;
                }
                return false;
            } else {
                return true;
            }
        }

        /**
         * Phase 1 the rotation system is resorted and the splitted deques for
         * the children are calculated
         */
        // current node is root
        if (incomingIndex == -1) {
            int firstChildIndex = calculateIndexOfTreeEdge(outgoing.get(0),
                    rotation);
            List<Edge> tmp1 = rotation.subList(0, firstChildIndex);
            List<Edge> tmp2 = rotation
                    .subList(firstChildIndex, rotation.size());

            tmp2.addAll(tmp1);

            rotationsystem.setValue(tmp2);
            rotation = (List<Edge>) rotationsystem.getValue();
        } else {
            List<Edge> tmp1 = rotation.subList(0, incomingIndex);
            List<Edge> tmp2 = rotation.subList(incomingIndex, rotation.size());

            tmp2.addAll(tmp1);

            rotationsystem.setValue(tmp2);
            rotation = (List<Edge>) rotationsystem.getValue();
        }

        // list of the children
        List<Node> children = calculateChildren(currentNode);

        // list of the deques of each child
        Map<Node, Deque<Edge>> childrenDeque = new HashMap<Node, Deque<Edge>>();

        // split is only necessary, if the current node has more than one child
        if (children.size() == 1) {
            Node child = children.get(0);
            childrenDeque.put(child, deque);
            for (Edge edgeDeque : deque) {
                ObjectAttribute currentSDAttribute = (ObjectAttribute) edgeDeque
                        .getAttribute("currentSD");
                currentSDAttribute.setValue(child);
            }
        } else {
            splitCounter++;
            childrenDeque = splitDeque(currentNode, children, deque);
        }
        if (childrenDeque == null) {
            if (status.equals("")) {
                status = "\nAt node " + currentNode + ":\n" + SPLIT_1;
            }
            return false;
        }

        /**
         * Phase 2 proofs, if the sectors are nested or disjunct
         */
        Map<Node, List<Edge>> partialRotationsystem = new HashMap<>();
        boolean[] removed = new boolean[children.size() + 1];
        for (int i = 0; i < children.size(); i++) {
            partialRotationsystem.put(children.get(i), new LinkedList<Edge>());
            sectorEdges.put(i + 1, new LinkedList<Edge>());
            removed[i + 1] = false;
        }
        sectorEdges.put(0, new LinkedList<Edge>());

        Stack<Integer> sectors = new Stack<>();
        Map<Integer, Integer> sectorRemoved = new HashMap<>();
        sectors.add(0);

        for (Edge edge : rotation) {
            Node endNode = getOtherEndNode(edge, currentNode);
            Node currentChild = null;
            ObjectAttribute currentSDAttribute = (ObjectAttribute) edge
                    .getAttribute("currentSD");
            ObjectAttribute childAtNodeAttribute = (ObjectAttribute) edge
                    .getAttribute("childAtNode");

            if (currentSDAttribute.getValue() != null) {
                currentChild = (Node) currentSDAttribute.getValue();
            } else {
                currentChild = ((HashMap<Node, Node>) childAtNodeAttribute
                        .getValue()).get(currentNode);
            }

            if (currentChild != null) {
                partialRotationsystem.get(currentChild).add(edge);
            }

            int currentSector;
            // current sector is calculated
            if (endNode.getInteger("DFSnumber") < currentNode
                    .getInteger("DFSnumber")) {
                currentSector = 0;
            } else {
                int childIndex = children.indexOf(currentChild);
                currentSector = childIndex + 1;
            }

            if (!treeEdges.contains(edge)) {
                sectorEdges.get(currentSector).add(edge);
            }

            // stack contains sector index
            if (sectors.contains(currentSector)) {
                while (!sectors.peek().equals(currentSector)) {
                    int removedIndex = sectors.pop();
                    removed[removedIndex] = true;
                    sectorRemoved.put(removedIndex, currentSector);

                    // an addition of me for an error
                    // cross edges are the edges in the deque od the child
                    // and the edges, which are part of the jordan curve
                    if (currentSector != 0
                            && !edgesIncidentToNode(childrenDeque.get(children
                                    .get(removedIndex - 1)), currentNode)) {
                        Node subtree = children.get(removedIndex - 1);
                        for (Edge edgeDeque : childrenDeque.get(subtree)) {
                            if (removedInSubtree(edgeDeque, currentNode,
                                    subtree)) {
                                crossEdges.add(edgeDeque);
                                List<Edge> sectorList = sectorEdges
                                        .get(currentSector);
                                crossEdges.add(sectorList.get(0));
                                crossEdges
                                        .add(sectorList.get(sectorList.size() - 1));
                                findJordanCurve(currentNode,
                                        children.get(sectorRemoved
                                                .get(removedIndex) - 1),
                                        sectorList);
                            }
                        }
                        status = "\nAt node " + currentNode + ":\n" + NOT_EMTPY;
                        return false;
                    }
                }
                // sector index can be pushed onto the stack
            } else if (!removed[currentSector]) {
                sectors.push(currentSector);
            } else {
                // sectors are interlacing
                crossEdges.add(edge);

                List<Edge> sectorlist = sectorEdges.get(sectorRemoved
                        .get(currentSector));
                if (!sectorlist.isEmpty()) {
                    crossEdges.add(sectorlist.get(sectorlist.size() - 1));
                }
                status = "\nAt node " + currentNode + ":\n" + NESTING;
                return false;
            }
        }

        /**
         * Phase 3 the head and tail edges are calculated with the partial
         * rotation system and processed with the process vertex method
         */
        for (Node child : children) {

            List<Edge> partial = partialRotationsystem.get(child);

            Edge toChild = null;
            for (Edge edge : outgoing) {
                Node other = getOtherEndNode(edge, currentNode);
                if (other.equals(child)) {
                    toChild = edge;
                }
            }

            // calculate head and tail edges
            if (incomingIndex == -1) {
                calculateHeadEdgesRootLeaf(currentNode, toChild, partial);
            } else {
                partial.add(0, incoming);
                calculateHeadTailEdgesInnerVertex(currentNode, incoming,
                        toChild, partial);
            }
            // head edges of the current node
            ObjectAttribute headEdges = (ObjectAttribute) currentNode
                    .getAttribute("headEdges");

            // tail edges of the current node
            ObjectAttribute tailEdges = (ObjectAttribute) currentNode
                    .getAttribute("tailEdges");

            Deque<Edge> currentChildDeque = childrenDeque.get(child);
            childrenDeque.put(
                    child,
                    processVertex(currentNode,
                            (List<Edge>) headEdges.getValue(),
                            (List<Edge>) tailEdges.getValue(),
                            currentChildDeque));
            if (childrenDeque.get(child) == null) {
                return false;
            } else if (!isSDLayout(child, childrenDeque.get(child))) {
                return false;
            }
        }

        return true;
    }

    /**
     * The method calculates, if all edges in the given deque are incident to
     * the given node
     * 
     * @param deque
     *            : the given deque
     * @param currentNode
     *            : the given node
     * @return true, if all edges are incident to the given node, otherwise
     *         false
     */
    private boolean edgesIncidentToNode(Deque<Edge> deque, Node currentNode) {

        for (Edge edge : deque) {
            Node end1 = edge.getSource();
            Node end2 = edge.getTarget();

            if (!currentNode.equals(end1) && !currentNode.equals(end2)) {
                return false;
            }

            Node otherNode;
            if (currentNode.equals(end1)) {
                otherNode = end2;
            } else {
                otherNode = end1;
            }

            if (otherNode.getInteger("DFSnumber") > currentNode
                    .getInteger("DFSnumber")) {
                return false;
            }
        }

        return true;
    }

    /**
     * method calculates the head and tail edges of an inner vertex
     * 
     * @param node
     *            : the current node
     * @param first
     *            : the first tree edge in the current rotation system
     * @param second
     *            : the second tree edge in the current rotation system
     */
    private void calculateHeadTailEdgesInnerVertex(Node node, Edge first,
            Edge second, List<Edge> rotation) {

        List<Edge> tmp1 = new LinkedList<>();
        List<Edge> tmp2 = new LinkedList<>();
        List<Edge> tmp3 = new LinkedList<>();

        // distributes the edges into 3 lists
        // 1.list : edges until the first tree edge
        // 2. list : all edges between the first and the second tree edge
        // 3. list : the remaining edges between the second tree edge and the
        // end of the rotation system
        Edge currentTreeEdge = null;
        for (Edge edge : rotation) {
            if (edge.equals(first)) {
                currentTreeEdge = first;
            } else if (edge.equals(second)) {
                currentTreeEdge = second;
            }
            if (currentTreeEdge == null) {
                tmp1.add(edge);
            } else if (currentTreeEdge.equals(first)) {
                tmp2.add(edge);
            } else if (currentTreeEdge.equals(second)) {
                tmp3.add(edge);
            }
        }
        // tmp3 + tmp1
        tmp3.addAll(tmp1);

        // Calculates which of tmp2 and tmp3 are head and tail edges
        Node other;

        if (tmp2.get(0).getSource().equals(node)) {
            other = tmp2.get(0).getTarget();
        } else {
            other = tmp2.get(0).getSource();
        }

        // both tree edges are removed at the beginning of the lists
        tmp2.remove(0);
        tmp3.remove(0);

        Attribute headAttribute = new ObjectAttribute("headEdges");
        Attribute tailAttribute = new ObjectAttribute("tailEdges");

        // calculates which are the head and the tail edges
        if (other.getInteger("DFSnumber") > node.getInteger("DFSnumber")) {

            headAttribute.setValue(tmp3);

            Collections.reverse(tmp2);
            tailAttribute.setValue(tmp2);

        } else {
            headAttribute.setValue(tmp2);

            Collections.reverse(tmp3);
            tailAttribute.setValue(tmp3);
        }

        if (node.getAttributes().containsAttribute("headEdges")) {
            node.removeAttribute("headEdges");
        }
        if (node.getAttributes().containsAttribute("tailEdges")) {
            node.removeAttribute("tailEdges");
        }
        node.addAttribute(headAttribute, "");
        node.addAttribute(tailAttribute, "");
    }

    /**
     * method calculates the head edges of the root or a leaf
     * 
     * @param node
     *            : the current node
     * @param treeEdge
     *            : the outgoing (for root) or the incoming (for leaf) edge
     */
    private void calculateHeadEdgesRootLeaf(Node node, Edge treeEdge,
            List<Edge> rotation) {

        // list for the edges until the tree edge
        List<Edge> tmp1 = new LinkedList<>();
        // list for the edges from the tree edge until the end of the rotation
        // system
        List<Edge> tmp2 = new LinkedList<>();

        // distributes the edges into 2 lists
        Edge currentTreeEdge = null;
        for (Edge edge : rotation) {
            if (edge.equals(treeEdge)) {
                currentTreeEdge = treeEdge;
            }
            if (currentTreeEdge == null) {
                tmp1.add(edge);
            } else if (currentTreeEdge.equals(treeEdge)) {
                tmp2.add(edge);
            }
        }

        tmp2.addAll(tmp1);
        // tree edge is removed
        tmp2.remove(0);

        Attribute headAttribute = new ObjectAttribute("headEdges");
        headAttribute.setValue(tmp2);
        if (node.getAttributes().containsAttribute("headEdges")) {
            node.removeAttribute("headEdges");
        }

        Attribute tailAttribute = new ObjectAttribute("tailEdges");
        tailAttribute.setValue(new LinkedList<>());
        if (node.getAttributes().containsAttribute("tailEdges")) {
            node.removeAttribute("tailEdges");
        }

        node.addAttribute(headAttribute, "");
        node.addAttribute(tailAttribute, "");
    }

    /**
     * method calculates the incoming tree edge of a vertex
     * 
     * @param node
     *            : the current node
     * @return : the incoming tree edge or null
     */
    private Edge calculateIncomingEdge(Node node) {
        for (Edge edge : treeEdges) {
            if (edge.getSource().equals(node) || edge.getTarget().equals(node)) {
                Node otherNode;
                if (edge.getSource().equals(node)) {
                    otherNode = edge.getTarget();
                } else {
                    otherNode = edge.getSource();
                }
                if (otherNode.getInteger("DFSnumber") < node
                        .getInteger("DFSnumber")) {
                    return edge;
                }
            }
        }
        return null;
    }

    /**
     * method calculates the outgoing tree edge of a vertex
     * 
     * @param node
     *            : the current node
     * @return : the list of the outgoing tree edges or null
     */
    private List<Edge> calculateOutgoingEdges(Node node) {
        List<Edge> children = new LinkedList<>();
        for (Edge edge : treeEdges) {
            if (edge.getSource().equals(node) || edge.getTarget().equals(node)) {
                Node otherNode;
                if (edge.getSource().equals(node)) {
                    otherNode = edge.getTarget();
                } else {
                    otherNode = edge.getSource();
                }
                if (otherNode.getInteger("DFSnumber") > node
                        .getInteger("DFSnumber")) {
                    children.add(edge);
                }
            }
        }
        return children;
    }

    /**
     * method calculates the index of the given edge in the given rotation
     * system
     * 
     * @param treeEdge
     *            : index of which edge should be searched
     * @param rotation
     *            : the current rotation system
     * @return the index of the given edge, -1 if not found
     */
    private int calculateIndexOfTreeEdge(Edge treeEdge, List<Edge> rotation) {
        int index = 0;
        for (Edge edge : rotation) {
            if (edge.equals(treeEdge)) {
                return index;
            } else {
                index++;
            }
        }
        return -1;
    }

    /**
     * method calculates the children of the given node in the tree layout
     * 
     * @param currentNode
     *            : the current node
     * @return : a list of the children of the node in the tree layout
     */
    private List<Node> calculateChildren(Node currentNode) {
        List<Node> children = new ArrayList<>();
        for (Edge edge : treeEdges) {
            if (edge.getSource().equals(currentNode)
                    || edge.getTarget().equals(currentNode)) {
                Node otherNode = getOtherEndNode(edge, currentNode);
                if (otherNode.getInteger("DFSnumber") > currentNode
                        .getInteger("DFSnumber")) {
                    children.add(otherNode);
                }
            }
        }
        return children;
    }

    /**
     * method removes the created attribute, so the algorithm can be recalled at
     * the same graph
     */
    private void clearAttributes() {
        rotationalgorithm.clearAttributes();
        dfsalgorithm.clearAttributes();
        for (Node node : graph.getNodes()) {
            if (node.containsAttribute("headEdges")) {
                node.removeAttribute("headEdges");
            }
            if (node.containsAttribute("headEdges")) {
                node.removeAttribute("tailEdges");
            }
        }
        for (Edge edge : graph.getEdges()) {
            edge.removeAttribute("currentSD");
        }
    }

    /**
     * method colors the calculated treeEdges in the given color
     * 
     * @param color
     *            : the color for the edges
     */
    private void colorEdges(Color color, Collection<Edge> list) {
        for (Edge edge : list) {
            ColorAttribute ca = (ColorAttribute) edge
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.FRAMECOLOR);
            ca.setColor(color);
        }
    }

    /**
     * This method changes the rotation system of one node, so the graph has no
     * longer a SD Layout. Two edges are swapped and for the two edges a bend is
     * added for the representation of the graph.
     */
    @SuppressWarnings("unchecked")
    private void createNoSDLayout() {
        Node randomNode = graph.getNodes().get(
                (int) (Math.random() * graph.getNumberOfNodes()));

        ObjectAttribute rotationsystem = (ObjectAttribute) randomNode
                .getAttribute("rotationsystem");
        List<Edge> rotation = (List<Edge>) rotationsystem.getValue();

        Edge e1 = rotation.get(0);
        Edge e2 = rotation.get(1);

        rotation.add(0, e1);
        rotation.add(0, e2);

        CoordinateAttribute source1;
        CoordinateAttribute target1;
        CoordinateAttribute target2;

        if (e1.getSource().equals(e2.getSource())) {
            source1 = (CoordinateAttribute) e1.getSource().getAttribute(
                    GraphicAttributeConstants.COORD_PATH);
            target1 = (CoordinateAttribute) e1.getTarget().getAttribute(
                    GraphicAttributeConstants.COORD_PATH);
            target2 = (CoordinateAttribute) e2.getTarget().getAttribute(
                    GraphicAttributeConstants.COORD_PATH);
        } else if (e1.getTarget().equals(e2.getSource())) {
            source1 = (CoordinateAttribute) e1.getTarget().getAttribute(
                    GraphicAttributeConstants.COORD_PATH);
            target1 = (CoordinateAttribute) e1.getSource().getAttribute(
                    GraphicAttributeConstants.COORD_PATH);
            target2 = (CoordinateAttribute) e2.getTarget().getAttribute(
                    GraphicAttributeConstants.COORD_PATH);
        } else if (e1.getSource().equals(e2.getTarget())) {
            source1 = (CoordinateAttribute) e1.getSource().getAttribute(
                    GraphicAttributeConstants.COORD_PATH);
            target1 = (CoordinateAttribute) e1.getTarget().getAttribute(
                    GraphicAttributeConstants.COORD_PATH);
            target2 = (CoordinateAttribute) e2.getSource().getAttribute(
                    GraphicAttributeConstants.COORD_PATH);
        } else {
            source1 = (CoordinateAttribute) e1.getTarget().getAttribute(
                    GraphicAttributeConstants.COORD_PATH);
            target1 = (CoordinateAttribute) e1.getSource().getAttribute(
                    GraphicAttributeConstants.COORD_PATH);
            target2 = (CoordinateAttribute) e2.getSource().getAttribute(
                    GraphicAttributeConstants.COORD_PATH);
        }

        Vector v1 = new Vector(source1.getX() - target1.getX(), source1.getY()
                - target1.getY());
        Vector v2 = new Vector(source1.getX() - target2.getX(), source1.getY()
                - target2.getY());

        double length1 = Math.sqrt(v1.getX() * v1.getX() + v1.getY()
                * v1.getY());
        double length2 = Math.sqrt(v2.getX() * v2.getX() + v2.getY()
                * v2.getY());
        double bend = Math.min(length1, length2);
        bend = Math.max(bend / 4, 16);
        bend = Math.min(bend, 40);
        Vector newBend1 = new Vector(v1.getX() / length1 * bend, v1.getY()
                / length1 * bend);
        Vector newBend2 = new Vector(v2.getX() / length2 * bend, v2.getY()
                / length2 * bend);

        EdgeGraphicAttribute edgeGraphic1 = (EdgeGraphicAttribute) e1
                .getAttribute(GraphicAttributeConstants.GRAPHICS);
        edgeGraphic1.setShape(PolyLineEdgeShape.class.getName());
        SortedCollectionAttribute bends1 = edgeGraphic1.getBends();
        bends1.add(new CoordinateAttribute("bend0", new Point2D.Double(source1
                .getX() - newBend2.getX(), source1.getY() - newBend2.getY())));

        EdgeGraphicAttribute edgeGraphic2 = (EdgeGraphicAttribute) e2
                .getAttribute(GraphicAttributeConstants.GRAPHICS);
        edgeGraphic2.setShape(PolyLineEdgeShape.class.getName());
        SortedCollectionAttribute bends2 = edgeGraphic2.getBends();
        bends2.add(new CoordinateAttribute("bend0", new Point2D.Double(source1
                .getX() - newBend1.getX(), source1.getY() - newBend1.getY())));
    }

    /**
     * method calculates the tree edges between the two nodes, which restrict
     * the sector.
     * 
     * This is used in the additional case of error, if the deque of the removed
     * child is not empty.
     * 
     * @param currentParent
     *            : the current node of the execution
     * @param node
     *            : the child of the sector, in which the removed sector is
     *            nested
     * @param sector
     *            : the restricted first and last edge of the sector of the
     *            given node
     */
    private void findJordanCurve(Node currentParent, Node node,
            List<Edge> sector) {
        Node firstEnd = getOtherEndNode(sector.get(0), currentParent);

        Node currentNode = firstEnd;
        while (!currentNode.equals(node)) {
            Edge incoming = calculateIncomingEdge(currentNode);
            crossEdges.add(incoming);
            currentNode = getOtherEndNode(incoming, currentNode);
        }

        Node secondEnd = getOtherEndNode(sector.get(sector.size() - 1),
                currentParent);
        currentNode = node;
        while (!currentNode.equals(secondEnd)) {
            List<Edge> outgoing = calculateOutgoingEdges(currentNode);
            Node nextNode = null;
            Edge nextEdge = null;
            for (Edge edge : outgoing) {
                Node endNode = getOtherEndNode(edge, currentNode);

                if (endNode.getInteger("DFSnumber") <= secondEnd
                        .getInteger("DFSnumber")) {

                    nextNode = endNode;
                    nextEdge = edge;
                }
            }
            currentNode = nextNode;
            crossEdges.add(nextEdge);
        }

    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {

        parameters = new Parameter[parameterList.size()];
        return parameterList.toArray(parameters);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    @Override
    public String getName() {
        return "IsSDLayout";
    }

    /**
     * get-method for the result of the calculation
     * 
     * 
     * @return true, if the graph has a SD layout, otherwise false
     */
    public boolean getBooleanResult() {
        return isSDLayoutResult;
    }

    /**
     * Get-method for the attribute duration used for the measurement
     * 
     * @return duration of the isSDLayout()-method
     */
    public long getTime() {
        return duration;
    }

    /**
     * Get-method for the attribute splitCounter used for the measurement
     * 
     * @return the counter of the calls of the split()-method
     */
    public int getSplitCounter() {
        return splitCounter;
    }

    /**
     * get-method for the selection used for the measurement
     * 
     * @return the current selection
     */
    public Selection getSelection() {
        return selection;
    }

    /**
     * method calculates the other end node of an edge
     * 
     * @param edge
     *            : the given edge
     * @param node
     *            : the first end node
     * @return : the second end node
     */
    private Node getOtherEndNode(Edge edge, Node node) {
        Node endNode;
        if (edge.getSource().equals(node)) {
            endNode = edge.getTarget();
        } else {
            endNode = edge.getSource();
        }
        return endNode;
    }

    /**
     * method processes a vertex with the associated head and tail edges and
     * calculates the output deque
     * 
     * head edges are processed at the head of the deque and tail edges at the
     * tail of the deque.
     * 
     * edges from a predecessor are removed and edges to a successor are
     * inserted.
     * 
     * @param node
     *            : current node
     * @param headEdges
     *            : the associated head edges
     * @param tailEdges
     *            : the associated tail edges
     * @param deque
     *            : current deque
     */
    private Deque<Edge> processVertex(Node node, List<Edge> headEdges,
            List<Edge> tailEdges, Deque<Edge> deque) {
        for (Edge edge : headEdges) {
            Node other = getOtherEndNode(edge, node);

            // if other node is a predecessor
            if (other.getInteger("DFSnumber") < node.getInteger("DFSnumber")) {
                if (!deque.isEmpty() && deque.peekFirst().equals(edge)) {
                    deque.removeFirst();
                } else {
                    crossEdges.add(edge);
                    while (!deque.isEmpty() && !deque.peekFirst().equals(edge)) {
                        crossEdges.add(deque.removeFirst());
                    }
                    status = "\nAt node " + node + ":\n" + PROCESS_VERTEX;
                    return null;
                }
            } else {
                deque.offerFirst(edge);
            }
        }

        for (Edge edge : tailEdges) {
            Node other = getOtherEndNode(edge, node);
            // if other node is a predecessor
            if (other.getInteger("DFSnumber") < node.getInteger("DFSnumber")) {
                if (!deque.isEmpty() && deque.peekLast().equals(edge)) {
                    deque.removeLast();
                } else {
                    crossEdges.add(edge);
                    while (!deque.isEmpty() && !deque.peekLast().equals(edge)) {
                        crossEdges.add(deque.removeLast());
                    }
                    status = "\nAt node " + node + ":\n" + PROCESS_VERTEX;
                    return null;
                }
            } else {
                deque.offerLast(edge);
            }
        }
        return deque;
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        int i = 0;
        for (Parameter<?> p : parameterList) {
            @SuppressWarnings("unchecked")
            Parameter<Object> pp = (Parameter<Object>) p;
            pp.setValue(params[i].getValue());
            i++;
        }
    }

    /**
     * method splits the deque for the children of a node
     * 
     * @param currentNode
     *            : the current node
     * @param children
     *            : the children of the node
     * @param deque
     *            : the deque of the current node
     * @return : a map for the children and their corresponding deque
     */
    private Map<Node, Deque<Edge>> splitDeque(Node currentNode,
            List<Node> children, Deque<Edge> deque) {

        Edge notRemovedEdge = null;

        ObjectAttribute rotationsystem = (ObjectAttribute) currentNode
                .getAttribute("rotationsystem");
        @SuppressWarnings("unchecked")
        List<Edge> rotation = (List<Edge>) rotationsystem.getValue();

        Map<Node, Deque<Edge>> childrenDeque = new HashMap<>();
        for (Node child : children) {
            childrenDeque.put(child, new LinkedList<Edge>());
        }

        Node currentChild = children.get(0);

        for (Edge edge : rotation) {
            Node endNode = getOtherEndNode(edge, currentNode);
            // First case: the edge goes to a child
            if (children.contains(endNode)) {
                currentChild = endNode;

                while (!deque.isEmpty()
                        && removedInSubtree(deque.peekFirst(), currentNode,
                                endNode)) {

                    Edge firstEdge = deque.remove();
                    notRemovedEdge = firstEdge;

                    ObjectAttribute currentSDAttribute = (ObjectAttribute) firstEdge
                            .getAttribute("currentSD");
                    currentSDAttribute.setValue(endNode);

                    childrenDeque.get(endNode).add(firstEdge);
                }
                if (!deque.isEmpty()) {
                    notRemovedEdge = deque.peek();
                }

                // Second case: the edge is already in the deque
            } else if (deque.contains(edge)) {
                if (deque.peekFirst().equals(edge)) {
                    ObjectAttribute currentSDAttribute = (ObjectAttribute) edge
                            .getAttribute("currentSD");
                    currentSDAttribute.setValue(currentChild);
                    childrenDeque.get(currentChild).add(deque.pop());
                } else {
                    crossEdges.add(edge);
                    while (!deque.isEmpty() && !deque.peekFirst().equals(edge)) {
                        crossEdges.add(deque.remove());
                    }
                    status = "\nAt node " + currentNode + ":\n" + SPLIT_1;
                    return null;
                }
            }
        }

        if (!deque.isEmpty()) {
            crossEdges.addAll(deque);

            if (notRemovedEdge != null) {
                crossEdges.add(notRemovedEdge);
            }
            status = "\nAt node " + currentNode + ":\n" + SPLIT_2;
            return null;
        } else {
            return childrenDeque;
        }
    }

    /**
     * Method calculates, if the given edge is removed in the subtree of the
     * child
     * 
     * The edges have a pointer to the subtree t each node. This pointer is
     * created by the DFS traversal.
     * 
     * @param edge
     *            : the edge, which should be removed
     * @param currentNode
     *            : the current node
     * @param child
     *            : the child, in which subtree the edge can be removed
     * @return true, if the edge is removed in the subtree of the child, else
     *         false
     */
    private boolean removedInSubtree(Edge edge, Node currentNode, Node child) {
        ObjectAttribute childAtNodeAttribute = (ObjectAttribute) edge
                .getAttribute("childAtNode");

        @SuppressWarnings("unchecked")
        Map<Node, Node> childAtNode = (Map<Node, Node>) childAtNodeAttribute
                .getValue();

        Node subtree = childAtNode.get(currentNode);

        if (child.equals(subtree)) {
            return true;
        }
        return false;
    }

    /**
     * @see org.graffiti.plugin.algorithm.CalculatingAlgorithm#getResult()
     */
    public AlgorithmResult getResult() {
        AlgorithmResult result = new DefaultAlgorithmResult();
        String text = "The graph has " + (isSDLayoutResult ? "a " : "no ")
                + "SD layout.\n" + status;
        result.setComponentForJDialog(text);
        result.addToResult("SDLayout", isSDLayoutResult);
        return result;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
