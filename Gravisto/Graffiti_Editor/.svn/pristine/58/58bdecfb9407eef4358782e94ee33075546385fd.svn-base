// =============================================================================
//
//   ExtendedDFS.java
//
//   Copyright (c) 2001-2014, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.extendedDFS;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.CompositeAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.AlgorithmResult;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.SDlayout.ObjectAttribute;
import org.graffiti.plugins.algorithms.connectivity.Connect;
import org.graffiti.selection.Selection;

/**
 * ExtendedDFS is a standard DFS, but for every edge the subtree of the child,
 * in which the edge ends, is saved.
 * 
 * This plugin is used by SDLayout. The additional map is used for a linear time
 * access to the current child.
 * 
 * @author Christina Ehrlinger
 * @version $Revision$ $Date$
 */
public class ExtendedDFS extends AbstractAlgorithm {

    /**
     * attribute for the random order of the edges
     */
    public static final String RANDOM = "The order at every node is random";

    /**
     * attribute for the order as the user inserted the edges
     */
    public static final String DRAWING = "The order corresponds to the order, in which the edges were inserted into the graph";

    /**
     * attribute for the order as the user or the used generator inserted the
     * edges
     */
    public static final String COUNTERCLOCK = "The order is counterclockwise and starts with the edge left of the incoming tree edge";

    /**
     * attribute for the tree edges
     */
    private List<Edge> treeEdges;

    /**
     * attribute for the next available DFS number
     */
    private int nextDFSnumber;

    /**
     * attribute for the list of the parameter
     */
    private LinkedList<Parameter<?>> parameterList;

    /**
     * attribute for the parameter, which node should be the start node
     */
    private SelectionParameter selParam;

    /**
     * attribute for the start node of the DFS algorithm
     */
    private Selection selection;

    /**
     * attribute for the parameter, which order of the edges the user want to
     * use
     */
    private StringSelectionParameter edgesParameter;

    /**
     * attribute for the connectivity algorithm
     */
    private Connect connectalgorithm;

    /**
     * The constructor generates an instance of ExtendedDFS with the
     * SelectionParameter for the startnode and an StringSelectionParameter for
     * the order of the edges
     */
    public ExtendedDFS() {
        parameterList = new LinkedList<Parameter<?>>();
        selParam = new SelectionParameter("Start node",
                "SD Layout will start with the only selected node.");
        String[] edgesOrder = { COUNTERCLOCK, RANDOM, DRAWING };
        edgesParameter = new StringSelectionParameter(edgesOrder,
                "Order of the edges",
                "In which order should the edges be processed for the DFS-tree?");
        parameterList.addFirst(edgesParameter);
        parameterList.addFirst(selParam);

    }

    /**
     * The method checks, if the given graph is null or emtpy. Also the given
     * graph has to be connected and one node has to be marked as start node.
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        if (graph == null) {
            errors.add("The graph instance may not be null.");
        } else if (graph.isEmpty()) {
            errors.add("The graph may not be emtpy.");
        }

        selection = selParam.getValue();
        if ((selection == null) || (selection.getNodes().size() != 1))
            errors.add("SD needs exactly one selected node for start.");

        // graph has to be connected
        connectalgorithm = new Connect();
        connectalgorithm.attach(graph);
        connectalgorithm.execute();
        AlgorithmResult resultConnect = connectalgorithm.getResult();
        if (resultConnect.getResult().get("connected").equals(false)) {
            errors.add("The Graph has to be connected.");
        }
        if (!errors.isEmpty()) {
            throw errors;
        }
    }

    /**
     * This method initializes and starts the DFS.
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    @Override
    public void execute() {
        nextDFSnumber = 1;
        treeEdges = new LinkedList<>();

        initializeDFS();
        depthFirstSearch(selection.getNodes().get(0),
                new HashMap<Node, Node>(), null);
    }

    /**
     * method initializes the nodes for the DFS, so every node is not visited
     * and creates the attribute "childAtNode"
     */
    private void initializeDFS() {
        treeEdges.clear();
        nextDFSnumber = 1;
        List<Node> nodes = graph.getNodes();
        for (Node node : nodes) {
            node.setBoolean("visited", false);
        }
        for (Edge edge : graph.getEdges()) {
            Attribute childAtNode = new ObjectAttribute("childAtNode");
            childAtNode.setValue(new HashMap<Node, Node>());
            edge.addAttribute(childAtNode, "");
        }
    }

    /**
     * method calculates the tree edges of the graph with the traversal
     * "depth first search"
     * 
     * @param currentNode
     *            : the current node of the recursive call
     * @param currentSuccessor
     *            : the map saves the current path in the graph
     * @param treeEdge
     *            : the incoming tree edge of the current node
     */
    @SuppressWarnings("unchecked")
    private void depthFirstSearch(Node currentNode,
            Map<Node, Node> currentSuccessor, Edge treeEdge) {

        // current node is visited and get labeled
        currentNode.setBoolean("visited", true);
        currentNode.setInteger("DFSnumber", nextDFSnumber);
        setLabel(currentNode, ((Integer) nextDFSnumber).toString());
        nextDFSnumber++;

        Collection<Edge> edgesColl = currentNode.getEdges();
        List<Edge> edges = new LinkedList<Edge>(edgesColl);

        // edges are sorted randomly
        if (edgesParameter.getValue().equals(COUNTERCLOCK)) {
            // edges are sorted from left to right
            ObjectAttribute rotationsystem = (ObjectAttribute) currentNode
                    .getAttribute("rotationsystem");
            edges = (List<Edge>) rotationsystem.getValue();
            if (treeEdge != null) {
                edges = calculateOrder(currentNode, treeEdge, edges);
            }

        } else if (edgesParameter.getValue().equals(RANDOM)) {
            Collections.shuffle(edges);
        }

        // the DFS algorithm
        for (Edge edge : edges) {
            Node othernode = getOtherEndNode(edge, currentNode);

            ObjectAttribute childAtNodeAttribute = (ObjectAttribute) edge
                    .getAttribute("childAtNode");
            HashMap<Node, Node> childAtNode = (HashMap<Node, Node>) childAtNodeAttribute
                    .getValue();

            if (!othernode.getBoolean("visited")) {
                currentSuccessor.put(currentNode, othernode);
                childAtNode.put(currentNode, othernode);
                treeEdges.add(edge);
                depthFirstSearch(othernode, currentSuccessor, edge);
            } else {
                if (currentNode.getInteger("DFSnumber") > othernode
                        .getInteger("DFSnumber")) {
                    Node succ = othernode;
                    while (!succ.equals(currentNode)) {
                        Node nextsucc = currentSuccessor.get(succ);
                        childAtNode.put(succ, nextsucc);
                        succ = nextsucc;
                    }
                }
            }
        }
    }

    /**
     * Get-method for the tree edges
     * 
     * @return the tree edges
     */
    public List<Edge> getTreeEdges() {
        return treeEdges;
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
     * Sets a Label to a node.
     * 
     * Taken from <code>org.graffiti.plugins.algorithms.bfs.BFS</code>
     * 
     * @param n
     *            The node to label
     * @param val
     *            The label
     */
    private void setLabel(Node n, String val) {
        LabelAttribute labelAttr = (LabelAttribute) searchForAttribute(
                n.getAttribute(""), LabelAttribute.class);
        if (labelAttr != null) {
            labelAttr.setLabel(val);
        } else {
            labelAttr = new NodeLabelAttribute("label");
            labelAttr.setLabel(val);
            n.addAttribute(labelAttr, "");
        }
    }

    /**
     * Taken from <code>org.graffiti.plugins.algorithms.bfs.BFS</code>
     */
    private Attribute searchForAttribute(Attribute attr,
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
                return null;
        }
        return null;
    }

    /**
     * method calculates the order of the edges, so that the first edge of the
     * rotation system is left to the incoming tree edge
     * 
     * @param node
     *            : the current node
     * @param treeEdge
     *            : edge, with which the rotation system should start
     */
    private List<Edge> calculateOrder(Node node, Edge treeEdge,
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
        Edge edge = tmp2.remove(0);
        tmp2.add(edge);
        return tmp2;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    @Override
    public String getName() {
        return "ExtendedDFS";
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
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        selection = ((SelectionParameter) params[0]).getSelection();
        edgesParameter = ((StringSelectionParameter) params[1]);
    }

    /**
     * This method removes all attributes from the nodes and edges, which were
     * inserted during the DFS. The attributes for the nodes are "visited" and
     * "DFSnumber" and for the edges the map "childAtNode".
     */
    public void clearAttributes() {
        for (Node node : graph.getNodes()) {
            node.removeAttribute("visited");
            node.removeAttribute("DFSnumber");
        }

        for (Edge edge : graph.getEdges()) {
            edge.removeAttribute("childAtNode");
        }
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
