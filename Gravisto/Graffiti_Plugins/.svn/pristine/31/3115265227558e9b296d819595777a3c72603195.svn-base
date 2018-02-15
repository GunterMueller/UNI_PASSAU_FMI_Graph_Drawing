/*
 * Created on Sep 2, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

package org.graffiti.plugins.algorithms.fpp;

/**
 * @author Le Pham Hai Dang
 */

import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;

/** A "Face" obj contains a list of Nodes and Edges */
public class Face {

    // ~ Instance fields
    // ========================================================

    private int outv = 0;

    private int oute = 0;

    private LinkedList<Node> listNode;

    private LinkedList<Edge> listEdge;

    private int index;

    private boolean isSepf;

    // ~ Constructors
    // ================================================================
    /**
     * 
     * Create a face, which contains the nodes of the listNode and the edges of
     * the listEdge
     * 
     * @param listNode
     *            <code>LinkedList</code>
     * @param listEdge
     *            <code>LinkedList</code>
     */
    public Face(LinkedList<Node> listNode, LinkedList<Edge> listEdge) {
        this.listNode = listNode;
        this.listEdge = listEdge;
        this.isSepf = false;
    }

    /** Create a new face without nodes and edges */
    public Face() {
        listNode = new LinkedList<Node>();
        listEdge = new LinkedList<Edge>();
        this.isSepf = false;
    }

    // ~ Methods
    // ================================================================
    /** @return true if the face is a separationface otherwise false */
    protected boolean getIsSepf() {
        return isSepf;
    }

    /**
     * @param isSepf
     *            define if the face is a separationface or not
     */
    protected void setIsSepf(boolean isSepf) {
        this.isSepf = isSepf;
    }

    /** @return the listNode <code>LinkedList</code> */
    public LinkedList<Node> getNodelist() {
        return listNode;
    }

    /** @return the edgeNode <code>LinkedList</code> */
    public LinkedList<Edge> getEdgelist() {
        return listEdge;
    }

    /** @return the number of nodes <code>int</code> */
    protected int nodeSize() {
        return listNode.size();
    }

    /**
     * Set the number <code>int</code> of nodes, which are part of the outerface
     */
    protected void setOutv(int vertex) {
        outv = vertex;
    }

    /**
     * @return the number <code>int</code> of nodes, which are part of the
     *         outerface
     */
    protected int getOutv() {
        return outv;
    }

    /** Increment the number of outV */
    protected void incrementOutv() {
        outv++;
    }

    /**
     * Set the number <code>int</code> of edges, which are part of the outerface
     */
    protected void setOute(int edge) {
        oute = edge;
    }

    /**
     * @return the number <code>int</code> of edges, which are part of the
     *         outerface
     */
    protected int getOute() {
        return oute;
    }

    /** Increment the number of outE */
    protected void incrementOute() {
        oute++;
    }

    /** Decrement the number of outE */
    protected void decrementOute() {
        oute--;
    }

    /**
     * Set the index <code>int</code> of this face
     */
    protected void setIndex(int index) {
        this.index = index;
    }

    /** @return the index <code>int</code> of this face */
    protected int getIndex() {
        return index;
    }
}
