package org.graffiti.plugins.algorithms.mnn;

import java.util.LinkedList;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;

/**
 * This class is a collection of nodes with the same canonical ordering number
 * 
 * @author Thomas
 * @version $Revision: 5766 $ $Date: 2006-08-10 11:02:23 +0200 (Do, 10 Aug 2006)
 *          $
 */
public class CanonicalOrderingNode {
    // path to the node-label
    private static final String PATH = GraphicAttributeConstants.LABEL
            + Attribute.SEPARATOR + GraphicAttributeConstants.LABEL;

    // the nodes of the canonical ordering node
    private LinkedList<Node> nodes;

    /**
     * Constructor
     * 
     * @param node
     *            one node
     */
    public CanonicalOrderingNode(Node node) {
        nodes = new LinkedList<Node>();
        nodes.add(node);
    }

    /**
     * Constructor
     * 
     * @param nodes
     *            a list of nodes with the same ordering number
     */
    public CanonicalOrderingNode(LinkedList<Node> nodes) {
        this.nodes = nodes;
    }

    /**
     * set the ordering number
     * 
     * @param num
     *            the number
     */
    public void setNumber(int num) {
        for (Node n : nodes) {
            n.setInteger("mnn.ordering", num);
        }
    }

    /**
     * get the ordering number
     * 
     * @return the number
     */
    public int getNumber() {
        return this.getNodes().get(0).getInteger("mnn.ordering");
    }

    /**
     * returns the nodes
     * 
     * @return the nodes
     */
    public LinkedList<Node> getNodes() {
        return nodes;
    }

    /**
     * returns the nuber of noces in this Canonical ordering node
     * 
     * @return the number of nodes
     */
    public int getNumberOfNodes() {
        return nodes.size();
    }

    /**
     * toString()
     */
    @Override
    public String toString() {

        String s = "[";

        for (Node n : nodes) {
            s += n.getString(PATH) + " ";
        }
        s += "]";
        return s;
    }
}
