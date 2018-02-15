// =============================================================================
//
//   TreeWidthNode.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treeWidth;

import java.util.ArrayList;
import java.util.Iterator;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * Constructs the treeWidthNode, which the treeWidth decomposition built.
 * 
 * @author wangq
 * @version $Revision$ $Date$
 */
public class TreeWidthNode {
    /** size of the cliqueNode in a treeWidthNode */
    private int cliqueNodeSize;
    /** the main node from a graphNode */
    private SuperNode mainNode;
    /** the iterator of neighbors of main node */
    private Iterator<SuperNode> ngNodeItr;
    /** all of graph nodes in a treeWidthNode */
    private ArrayList<Node> nodes;
    /** all of cliqueNode in a treeWidthNode */
    private ArrayList<Node> cliqueNodes = new ArrayList<Node>();
    /** all of NoCliqueNode in a treeWidthNode */
    private ArrayList<Node> noCliqueNodes = new ArrayList<Node>();
    /** the matrixGraph */
    private MatrixGraph matrixGraph;
    /** the original graph */
    private Graph graph;
    /** the graphNode in original graph */
    private Node graphNode;

    /**
     * Constructs a new instance.
     */
    public TreeWidthNode() {
    }

    /**
     * Constructs a new instance.
     * 
     * @param hsm
     *            is matrixGraph. graph is the original graph.
     */
    public TreeWidthNode(MatrixGraph hsm, Graph graph) {
        this.graph = graph;
        this.matrixGraph = hsm;
        this.nodes = new ArrayList<Node>();
    }

    /**
     * Returns the size.
     * 
     * @return the size.
     */
    public int getSize() {
        return this.nodes.size();
    }

    /**
     * Returns the nodes.
     * 
     * @return the nodes.
     */
    public ArrayList<Node> getNodes() {
        return this.nodes;
    }

    /**
     * Returns the mainNode.
     * 
     * @return the mainNode.
     */
    public SuperNode getMainNode() {
        return mainNode;
    }

    /**
     * Sets the mainNode.
     * 
     * @param mainNode
     *            the mainNode to set.
     */
    public void setMainNode(SuperNode mainNode) {

        this.mainNode = mainNode;
        this.nodes.add(this.mainNode.getNode());
        this.ngNodeItr = mainNode.getNeighbors().iterator();
        while (this.ngNodeItr != null && this.ngNodeItr.hasNext()) {
            SuperNode n = this.ngNodeItr.next();
            if (!n.isFinish()) {
                Node ngh = n.getNode();
                this.nodes.add(ngh);
                cliqueNodeSize++;
            }
        }

    }

    /**
     * Returns the graphNode.
     * 
     * @return the graphNode.
     */
    public Node getGraphNode() {
        return graphNode;
    }

    /**
     * Sets the graphNode.
     * 
     * @param graphNode
     *            the graphNode to set.
     */
    public void setGraphNode(Node graphNode) {
        this.graphNode = graphNode;
    }

    /**
     * Returns the cliqueNodes.
     * 
     * @return the cliqueNodes.
     */
    public ArrayList<Node> getCliqueNodes() {
        return cliqueNodes;
    }

    /**
     * Sets the cliqueNodes.
     * 
     * @param cliqueNodes
     *            the cliqueNodes to set.
     */
    public void setCliqueNodes(ArrayList<Node> cliqueNodes) {
        this.cliqueNodes = cliqueNodes;
    }

    /**
     * Returns the noCliqueNodes.
     * 
     * @return the noCliqueNodes.
     */

    public ArrayList<Node> getNoCliqueNodes() {
        ArrayList<Node> allnode = new ArrayList<Node>();
        allnode.addAll(nodes);
        allnode.removeAll(getCliqueNodes());
        return allnode;
    }

    /**
     * Sets the noCliqueNodes.
     * 
     * @param noCliqueNodes
     *            the noCliqueNodes to set.
     */
    public void setNoCliqueNodes(ArrayList<Node> noCliqueNodes) {
        this.noCliqueNodes = noCliqueNodes;
    }

    /**
     * return the neighbor
     * 
     * @param nodeArray
     *            the all treeWidth nodes.
     */
    public TreeWidthNode makeOneNg(ArrayList<TreeWidthNode> nodeArray) {
        TreeWidthNode ng = new TreeWidthNode();
        int t = 0;
        Iterator<TreeWidthNode> twnItr1 = nodeArray.iterator();
        while (twnItr1.hasNext()) {
            TreeWidthNode twn = twnItr1.next();
            if (twn.getNodes().containsAll(getNoCliqueNodes())
                    && twn.getNodes() != getNodes()) {
                if (twn.getNoCliqueNodes().size() == 0) {
                    ng = twn;
                    break;
                } else {
                    if (twn.getNodes().size() > t) {
                        t = twn.getNodes().size();
                        ng = twn;
                    }
                    t = twn.getNodes().size();
                    ng = twn;
                }
            }
        }
        return ng;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
