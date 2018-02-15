package org.graffiti.plugins.algorithms.mnn;

import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;

/**
 * This class represents a face of a planar graph
 * 
 * @author Thomas
 * @version $Revision: 5766 $ $Date: 2010-05-07 19:21:40 +0200 (Fr, 07 Mai 2010)
 *          $
 */
public class Face {

    // only for debugging - the path to the label of the node
    private static String separator = Attribute.SEPARATOR;
    private static String label = GraphicAttributeConstants.LABEL;
    private static final String PATH = label + separator + label;

    // the nodes of the face
    private LinkedList<Node> nodes;

    // the edges of the face
    private LinkedList<Edge> edges;

    // the face number
    private int number;

    /**
     * Constructor
     */
    public Face() {
        nodes = new LinkedList<Node>();
    }

    /**
     * Constructor
     * 
     * @param listNode
     *            the nodes of the face
     */
    public Face(LinkedList<Node> listNode) {
        this.nodes = listNode;
    }

    /**
     * Constructor
     * 
     * @param listNode
     *            the nodes of the face
     * @param listEdge
     *            the edges of the face
     */
    public Face(LinkedList<Node> listNode, LinkedList<Edge> listEdge) {
        this.nodes = listNode;
        this.edges = listEdge;
    }

    /**
     * returns the edges of this face
     * 
     * @return the edges
     */
    public LinkedList<Edge> getEdges() {
        return edges;
    }

    /**
     * returns the nodes of this face
     * 
     * @return the nodes
     */
    public LinkedList<Node> getNodelist() {
        return nodes;
    }

    /**
     * returns the number of nodes of this face
     * 
     * @return the number of nodes
     */
    public int getNumberOfNodes() {
        return nodes.size();
    }

    /**
     * set a face-number for debugging
     * 
     * @param num
     */
    public void setNumber(int num) {
        number = num;
    }

    /**
     * returns the face-number
     * 
     * @return the face number
     */
    public int getNumber() {
        return number;
    }

    /**
     * returns true, if the face contains the given node
     * 
     * @param node
     *            the node
     * @return true, if the face contains this node, false otherwise
     */
    public boolean contains(Node node) {
        return nodes.contains(node);
    }

    /**
     * returns true, if the given face is equal to this face
     */
    @Override
    public boolean equals(Object obj) {

        Face face = (Face) obj;
        LinkedList<Node> nodelist = face.getNodelist();

        Iterator<Node> it1 = nodes.iterator();
        Iterator<Node> it2 = nodelist.iterator();

        while (it1.hasNext()) {

            Node node = it1.next();
            if (!nodelist.contains(node))
                return false;

        }

        while (it2.hasNext()) {

            Node node = it2.next();
            if (!nodes.contains(node))
                return false;

        }

        return true;
    }

    /**
     * toString()
     */
    @Override
    public String toString() {

        Iterator<Node> i = nodes.iterator();
        String string = "Face " + number + ": [";

        while (i.hasNext()) {
            Node current = i.next();
            string += " Node " + current.getString(PATH) + " ";
        }

        string += "]";
        return string;
    }
}
