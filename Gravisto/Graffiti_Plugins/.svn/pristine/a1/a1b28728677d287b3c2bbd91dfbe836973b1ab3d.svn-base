// =============================================================================
//
//   LexBFSNode.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.interval;

import java.util.LinkedList;

import org.graffiti.graph.Node;

/**
 * The class represents a node of a graph with added information.
 * 
 * @author struckmeier
 */
public class LexBFSNode implements Comparable<LexBFSNode> {

    private Node Node;

    private LexBFSNode vorgaenger;

    private LexBFSNode nachfolger;

    private LexBFSSet lexBFSClass;

    private LinkedList<LexBFSNode> neighbors = new LinkedList<LexBFSNode>();

    private Boolean done = false;

    // private LexBFSNode parent = null;

    private LinkedList<LexBFSNode> rightNeighbors = new LinkedList<LexBFSNode>();

    private int number;

    private LexBFSClique clique;

    private double leftEnd;

    private double rightEnd;

    /**
     * Returns the done.
     * 
     * @return the done.
     */
    public Boolean getDone() {
        return done;
    }

    /**
     * Sets the done.
     * 
     * @param done
     *            the done to set.
     */
    public void setDone(Boolean done) {
        this.done = done;
    }

    /**
     * Returns the node.
     * 
     * @return the node.
     */
    public Node getNode() {
        return Node;
    }

    /**
     * Sets the node.
     * 
     * @param node
     *            the node to set.
     */
    public void setNode(Node node) {
        Node = node;
    }

    public void addNeighbor(LexBFSNode lex) {
        neighbors.add(lex);
    }

    public void addRightNeighbor(LexBFSNode lex) {
        rightNeighbors.add(lex);
    }

    /**
     * Returns the vorgaenger.
     * 
     * @return the vorgaenger.
     */
    public LexBFSNode getVorgaenger() {
        return vorgaenger;
    }

    /**
     * Sets the vorgaenger.
     * 
     * @param vorgaenger
     *            the vorgaenger to set.
     */
    public void setVorgaenger(LexBFSNode vorgaenger) {
        this.vorgaenger = vorgaenger;
    }

    /**
     * Returns the nachfolger.
     * 
     * @return the nachfolger.
     */
    public LexBFSNode getNachfolger() {
        return nachfolger;
    }

    /**
     * Sets the nachfolger.
     * 
     * @param nachfolger
     *            the nachfolger to set.
     */
    public void setNachfolger(LexBFSNode nachfolger) {
        this.nachfolger = nachfolger;
    }

    /**
     * Returns the lexBFSClass.
     * 
     * @return the lexBFSClass.
     */
    public LexBFSSet getLexBFSClass() {
        return lexBFSClass;
    }

    /**
     * Sets the lexBFSClass.
     * 
     * @param lexBFSClass
     *            the lexBFSClass to set.
     */
    public void setLexBFSClass(LexBFSSet lexBFSClass) {
        this.lexBFSClass = lexBFSClass;
    }

    /**
     * Returns the neighbors.
     * 
     * @return the neighbors.
     */
    public LinkedList<LexBFSNode> getNeighbors() {
        return neighbors;
    }

    /**
     * Sets the parent.
     * 
     * @param parent
     *            the parent to set.
     */
    public void setParent(LexBFSNode parent) {
        // this.parent = parent;
    }

    /**
     * Returns the parent.
     * 
     * @return the parent.
     */
    public LexBFSNode getParent() {
        if (rightNeighbors.isEmpty())
            return null;
        else
            return rightNeighbors.getFirst();
    }

    /**
     * Sets the rightNeighbors.
     * 
     * @param rightNeighbors
     *            the rightNeighbors to set.
     */
    public void setRightNeighbors(LinkedList<LexBFSNode> rightNeighbors) {
        this.rightNeighbors = rightNeighbors;
    }

    /**
     * Returns the rightNeighbors.
     * 
     * @return the rightNeighbors.
     */
    public LinkedList<LexBFSNode> getRightNeighbors() {
        return rightNeighbors;
    }

    /**
     * Sets the number.
     * 
     * @param number
     *            the number to set.
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     * Returns the number.
     * 
     * @return the number.
     */
    public int getNumber() {
        return number;
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(LexBFSNode otherNode) {
        if (this.getNumber() == (otherNode.getNumber()))
            return 0;
        else if (this.getNumber() > (otherNode.getNumber()))
            return 1;
        else
            return -1;
    }

    /**
     * Returns the leftEnd.
     * 
     * @return the leftEnd.
     */
    public double getLeftEnd() {
        return leftEnd;
    }

    /**
     * Sets the leftEnd.
     * 
     * @param leftEnd
     *            the leftEnd to set.
     */
    public void setLeftEnd(double leftEnd) {
        this.leftEnd = leftEnd;
    }

    /**
     * Returns the rightEnd.
     * 
     * @return the rightEnd.
     */
    public double getRightEnd() {
        return rightEnd;
    }

    /**
     * Sets the rightEnd.
     * 
     * @param rightEnd
     *            the rightEnd to set.
     */
    public void setRightEnd(double rightEnd) {
        this.rightEnd = rightEnd;
    }

    /**
     * Sets the clique.
     * 
     * @param clique
     *            the clique to set.
     */
    public void setClique(LexBFSClique clique) {
        this.clique = clique;
    }

    /**
     * Returns the clique.
     * 
     * @return the clique.
     */
    public LexBFSClique getClique() {
        return clique;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
