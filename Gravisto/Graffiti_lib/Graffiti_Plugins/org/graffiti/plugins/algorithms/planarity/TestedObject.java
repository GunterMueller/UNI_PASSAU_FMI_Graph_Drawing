package org.graffiti.plugins.algorithms.planarity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.graph.Node;

/**
 * The main class to store the calculated embeddings and the found Kuratowski
 * subgraphs.
 * 
 * @see TestedGraph
 * @see TestedComponent
 * @see TestedBicomp
 * 
 * @author Wolfgang Brunner
 */
public class TestedObject {

    /**
     * Stores whether the object is planar
     */
    protected boolean planar;

    /**
     * The list of <code>org.graffiti.graph.Node</code> objects in the object
     */
    protected List<Node> nodes;

    /**
     * The number of nodes
     */
    protected int numberOfNodes;

    /**
     * The mapping between <code>org.graffiti.graph.Node</code> and
     * <code>RealNode</code> objects
     */
    protected static HashMap<Node, RealNode> map;

    /**
     * The number of double edges in the object
     */
    protected int doubleEdges;

    /**
     * Constructs a new <code>TestedObject</code>
     * 
     * @param map
     *            The mapping between <code>org.graffiti.graph.Node</code> and
     *            <code>RealNode</code> objects
     */
    public TestedObject(HashMap<Node, RealNode> map) {
        TestedObject.map = map;
    }

    /**
     * Gives the result of the planarity test
     * 
     * @return <code>true</code> if the object is planar
     */
    public boolean isPlanar() {
        return planar;
    }

    /**
     * Gives the list of the nodes in the object
     * 
     * @return A list of <code>org.graffiti.graph.Node</code> objects
     */
    public List<Node> getNodes() {
        return nodes;
    }

    /**
     * Gives the number of nodes in the object
     * 
     * @return The number of nodes
     */
    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    /**
     * Gives the number of double edges in the object
     * 
     * @return The number of double edges
     */
    public int getNumberOfDoubleEdges() {
        return doubleEdges;
    }

    /**
     * Gives the targets of double edges starting from the given node
     * 
     * @param node
     *            The starting node
     * 
     * @return A list of <code>org.graffiti.graph.Node</code> objects
     */
    public List<Node> getDoubleEdgeTargets(Node node) {
        RealNode rNode = map.get(node);
        return rNode.doubleEdgeTargets;
    }

    /**
     * Gives the number of loops on the given node
     * 
     * @param node
     *            The node to count the loops on
     * 
     * @return The number of loops
     */
    public int getNumberOfLoops(Node node) {
        RealNode rNode = map.get(node);
        return rNode.loops;
    }

    /**
     * Converts a list of <code>ArbitraryNode</code> objects to a list of
     * <code>org.graffiti.graph.Node</code> objects and adds it to another list
     * 
     * @param list
     *            The list of <code>org.graffiti.graph.Node</code> objects
     * @param toAdd
     *            The list of <code>ArbitraryNode</code> objects
     * 
     * @return The merged list of <code>org.graffiti.graph.Node</code> objects
     */
    protected List<Node> addAll(List<Node> list, List<ArbitraryNode> toAdd) {
        for (Iterator<ArbitraryNode> i = toAdd.iterator(); i.hasNext();) {
            ArbitraryNode aNode = i.next();
            list.add(aNode.getRealNode().originalNode);
        }
        return list;
    }

    /**
     * Gives the complete adjacency list of the given node
     * 
     * @param node
     *            The node
     * @return The adjacency list of the node
     */
    public List<Node> getAdjacencyList(Node node) {
        RealNode rNode = map.get(node);
        List<Node> result = new LinkedList<Node>();
        addAll(result, rNode.adjacencyList);
        List<RealNode> childs = rNode.separatedDFSChildList.getList();
        for (Iterator<RealNode> i = childs.iterator(); i.hasNext();) {
            RealNode child = i.next();
            addAll(result, child.virtualParent.adjacencyList);
        }
        return result;
    }

    /**
     * Gives a textual representation of the node
     * 
     * @param node
     *            The node
     * 
     * @return The name of the node
     */
    public String toString(Node node) {
        return map.get(node).toString();
    }

    /**
     * Returns a textual representation of list containing
     * <code>org.graffiti.graph.Node</code> objects
     * 
     * @param nodes
     *            The list
     * @return The <code>String</code> representing the list
     */
    protected static String toStringNodeList(List<Node> nodes) {
        String result = "[";
        for (Iterator<Node> i = nodes.iterator(); i.hasNext();) {
            Node node = i.next();
            RealNode rNode = map.get(node);
            result += rNode;
            if (i.hasNext()) {
                result += ", ";
            }
        }
        result += "]";
        return result;
    }

    /**
     * Indents the given <code>String</code> by <code>spaces</codes> spaces
     * 
     * @param input
     *            The <code>String</code> to indent
     * @param spaces
     *            The amount of spaces to add
     * @return The indented <code>String</code>
     */
    public static String indent(String input, int spaces) {
        String result = "";
        String space = "";
        for (int i = 0; i < spaces; i++) {
            space += " ";
        }
        result += space;
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            result += ch;
            if ((ch == '\n') && (i != input.length() - 1)) {
                result += space;
            }
        }
        return result;
    }

}
