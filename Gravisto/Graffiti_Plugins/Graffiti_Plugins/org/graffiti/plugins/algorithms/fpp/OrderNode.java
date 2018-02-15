/*
 * Created on Aug 29, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

package org.graffiti.plugins.algorithms.fpp;

/**
 * @author Le Pham Hai Dang
 */

import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.graph.Node;

/** OrderNode manage the different between handle and a normal node */
public class OrderNode {

    // ~ Instance fields
    // ========================================================
    private boolean handle;

    private LinkedList<Node> handleList, interiorNeighbours;

    private Node node;

    private int number;

    private Node rightvertex;

    private Node leftvertex;

    // ~ Constructors
    // ================================================================
    /**
     * OrderNode contains exact one node
     * 
     * @param node
     *            <code>Node</code>
     * @param number
     *            <code>int</code>
     */
    OrderNode(Node node, int number) {
        this.node = node;
        this.number = number;
        this.handle = false;
        this.interiorNeighbours = new LinkedList<Node>();
    }

    /**
     * OrderNode contains a handle
     * 
     * @param list
     *            <code>LinkedList</code>
     * @param number
     *            <code>int</code>
     */
    OrderNode(LinkedList<Node> list, int number) {
        this.handleList = list;
        this.number = number;
        this.handle = true;
        this.interiorNeighbours = new LinkedList<Node>();
    }

    // ~ Methods
    // ================================================================

    /**
     * Add the Node current to the interior neighbours
     * 
     * @param current
     *            <code>Node</code>
     */
    protected void addInteriorNeighbours(Node current) {
        interiorNeighbours.add(current);
    }

    /**
     * 
     * @return all interior neighbours <code>LinkedList</code> of the OrderNode.
     *         (The order is from left to right of the next lmc OrderNode)
     */
    protected LinkedList<Node> getInteriorNeighbours() {
        return interiorNeighbours;
    }

    /** Set the rightvertex <code>Node</code> of the OrderNode */
    protected void setRightvertex(Node rightvertex) {
        this.rightvertex = rightvertex;
    }

    /** Set the leftvertex <code>Node</code> of the OrderNode */
    protected void setLeftvertex(Node leftvertex) {
        this.leftvertex = leftvertex;
    }

    /**
     * Distinction between handle and exact one node
     * 
     * @return true <code>Boolean</code>, if the OrderNode contains a handle
     *         <code>LinkedList</code>, otherwise <code>false</code>.
     */
    public boolean getHandle() {
        return handle;
    }

    /** @return exact one Node */
    public Node getOrderNode() {
        return node;
    }

    /** @return the handle */
    public LinkedList<Node> getOrderList() {
        return handleList;
    }

    /** @return the handleListIterator */
    public Iterator<Node> getOrderIterator() {
        return handleList.iterator();
    }

    /** @return the number <code>int</code> of the OrderNode */
    public int getNumber() {
        return number;
    }

    /** @return rightvertex <code>Node</code> of the OrderNode */
    public Node getRightvertex() {
        return rightvertex;
    }

    /** @return leftvertex <code>Node</code> of the OrderNode */
    public Node getLeftvertex() {
        return leftvertex;
    }
}
