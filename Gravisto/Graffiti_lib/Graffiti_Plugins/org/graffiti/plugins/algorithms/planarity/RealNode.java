package org.graffiti.plugins.algorithms.planarity;

import java.util.LinkedList;
import java.util.List;

import org.graffiti.graph.Node;

/**
 * A <code>RealNode</code> object represents a node of the graph and corresponds
 * to a <code>org.graffiti.graph.Node</code>.
 * 
 * @author Wolfgang Brunner
 */
public class RealNode extends ArbitraryNode {

    /**
     * The <code>org.graffiti.graph.Node</code> which corresponds to this node
     */
    public Node originalNode;

    /**
     * The depth first search index of this node in this connected component.
     * The complete number of this node is <code>DFI</code>+
     * <code>DFSStartNumber</code>.
     */
    public int DFI;

    /**
     * The parent of this node in the depth first search tree
     */
    public RealNode DFSParent;

    /**
     * The <code>VirtualNode</code> this node is directly connected to
     */
    public VirtualNode virtualParent;

    /**
     * The least ancestor of this node
     */
    public int leastAncestor;

    /**
     * The low point of this node
     */
    public int lowPoint;

    /**
     * If <code>backedgeTarget</code> is set to the current node, a back edge
     * has to be embedded
     */
    public RealNode backedgeTarget;

    /**
     * A list of pertinent <code>VirtualNode</code> objects belonging to this
     * node
     */
    public LinkedList<VirtualNode> pertinentRoots;

    /**
     * A list of depth first search childs which are still in another bicomp
     */
    public DFSChildList separatedDFSChildList;

    /**
     * The left neighbour of this node in the <code>DFSChildList</code>
     */
    public RealNode leftDFSNeighbour;

    /**
     * The right neighbour of this node in the <code>DFSChildList</code>
     */
    public RealNode rightDFSNeighbour;

    /**
     * The complete adjacency list of this node
     */
    public List<RealNode> completeAdjacencyList;

    /**
     * The number of loops on this node
     */
    public int loops;

    /**
     * For each double edge starting from this node the target is stored in this
     * list
     */
    public List<Node> doubleEdgeTargets;

    /**
     * Constructs a new <code>RealNode</code>
     * 
     * @param originalNode
     *            The <code>org.graffiti.graph.Node</code> corresponding to this
     *            node
     * @param DFSStartNumber
     *            The lowest depth first search index in this connected
     *            component
     */
    public RealNode(Node originalNode, int DFSStartNumber) {
        this.originalNode = originalNode;
        DFI = -1;
        DFSParent = null;
        virtualParent = null;
        leastAncestor = -1;
        lowPoint = -1;
        visited = null;
        backedgeTarget = null;
        degree = 1;
        doubleEdgeTargets = new LinkedList<Node>();
        pertinentRoots = new LinkedList<VirtualNode>();
        separatedDFSChildList = new DFSChildList();
        leftDFSNeighbour = null;
        rightDFSNeighbour = null;
        edgeToChild = null;
        this.DFSStartNumber = DFSStartNumber;
        adjacencyList = new LinkedList<ArbitraryNode>();
        bicomp = null;
        completeAdjacencyList = new LinkedList<RealNode>();
        loops = 0;
        quadrant = NOT_ON_BORDER;
    }

    /**
     * Returns a textual representation of this node
     * 
     * @return The name of the node
     */
    @Override
    public String toString() {
        return "Node " + (DFI + DFSStartNumber);
    }

    /**
     * Returns the <code>RealNode</code> corresponding to this node. As this
     * object is a <code>RealNode</code> <code>this</code> is returned.
     * 
     * @return <code>this</code>
     * 
     * @see VirtualNode#getRealNode
     */
    @Override
    public RealNode getRealNode() {
        return this;
    }
}
