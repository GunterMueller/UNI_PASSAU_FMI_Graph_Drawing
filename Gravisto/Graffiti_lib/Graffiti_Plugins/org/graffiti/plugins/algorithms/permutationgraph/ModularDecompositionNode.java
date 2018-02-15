// =============================================================================
//
//   ModularDecompositionNode.java
//
//   Copyright (c) 2001-2011, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.permutationgraph;

import java.util.ArrayList;
import java.util.List;

import org.graffiti.graph.Node;

/**
 * Node is used in the modular decomposition tree for a given graph. These nodes
 * will be the essential part of the permutation graph algorithm, because the
 * modules of the given graph can be read out of these nodes.
 * 
 * @author Emanuel Berndl
 * @version $Revision$ $Date$
 */
public class ModularDecompositionNode implements Cloneable {

    private List<Node> nodes;
    private ModularDecompositionNode parent;
    private ArrayList<ModularDecompositionNode> children;
    private Type type;
    private List<Node> activeEdgeNodes;
    
    public static enum Type {
        PRIME, PARALLEL, SERIES
    }

    /**
     * Constructor for a ModularDecompositionNode with a parent.
     * 
     * @param nodes
     *            Set of nodes which the node will correspond to as a module.
     * @param parent
     *            The parent of this ModularDecompositionNode. If this is null,
     *            the node has no parent and is consecutively a root.
     * @throws ModularDecompositionNodeException 
     */
    public ModularDecompositionNode(List<Node> nodes,
            ModularDecompositionNode parent) throws ModularDecompositionNodeException {
        if(nodes.isEmpty()) {
            throw new ModularDecompositionNodeException("Cannot create a ModularDecompositionNode on an empty node set.");
        }
        this.setNodes(nodes);
        this.setParent(parent);
        this.children = new ArrayList<ModularDecompositionNode>();
        this.setType(null);
    }

    /**
     * This method is used to construct a single node for the modular
     * decomposition tree, which contains only one node and thus is a singleton
     * module.
     * 
     * @param node
     *            The underlying node for the ModularDecompositionNode.
     * @param parent
     *            The parent for the ModularDecompositionNode. If this is null,
     *            the node has no parent and is the root of a tree itself.
     */
    public ModularDecompositionNode(Node node, ModularDecompositionNode parent) {
        List<Node> nodes = new ArrayList<Node>();
        nodes.add(node);
        this.setNodes(nodes);
        this.setParent(parent);
        this.children = new ArrayList<ModularDecompositionNode>();
        this.setType(null);
    }
    
    /**
     * {@inheritDoc}
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * Sets the nodes.
     * 
     * @param nodes
     *            the nodes to set.
     */
    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    /**
     * Returns the nodes.
     * 
     * @return the nodes.
     */
    public List<Node> getNodes() {
        return nodes;
    }

    /**
     * Sets the parent.
     * 
     * @param parent
     *            the parent to set.
     */
    public void setParent(ModularDecompositionNode parent) {
        this.parent = parent;
    }

    /**
     * Returns the parent.
     * 
     * @return the parent.
     */
    public ModularDecompositionNode getParent() {
        return parent;
    }

    /**
     * Returns the children.
     * 
     * @return the children.
     */
    public ArrayList<ModularDecompositionNode> getChildren() {
        return children;
    }

    /**
     * Method to add a child to this ModularDecompositionNode.
     * 
     * @param nodeToAdd
     *            Node that is to be added as a child.
     */
    public void addChild(ModularDecompositionNode nodeToAdd) {
        if (nodeToAdd != null) {
            this.getChildren().add(nodeToAdd);
            nodeToAdd.setParent(this);
        }
    }
    
    /**
     * Method to remove a given node from the children of this node.
     * 
     * @param nodeToRemove      Node that is to be removed.
     */
    public void removeChild(ModularDecompositionNode nodeToRemove) {
        nodeToRemove.setParent(null);
        
        int nodeNumber = -1;
        
        for(int i = 0; i < getChildren().size(); i++) {
            if(getChildren().get(i).equals(nodeToRemove)) {
                nodeNumber = i;
                break;
            }
        }
        
        if(nodeNumber > -1) {            
            getChildren().remove(nodeNumber);
        }
    }

    /**
     * Sets the type.
     * 
     * @param type
     *            the type to set.
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Returns the type.
     * 
     * @return the type.
     */
    public Type getType() {
        return type;
    }

    /**
     * Method is used to check whether a certain ModularDecompositionNode is
     * this. It therefore checks, if both nodes have the same underlying set of
     * nodes and the same children. {@inheritDoc}
     */
    public boolean equals(Object o) {
        if (!(o instanceof ModularDecompositionNode)) {
            return false;
        }
        ModularDecompositionNode otherNode = (ModularDecompositionNode) o;
        
        // check whether the nodes associated with this and the other node are equal
        if(!nodeSetsEqual(this.getNodes(), otherNode.getNodes())) {
            return false;
        }
        
        if(this.getChildren().size() != otherNode.getChildren().size()) {
            // different number of children - cannot be equal
            return false;
        }
        
        if(this.getType() != otherNode.getType()) {
            return false;
        }
        
        for(ModularDecompositionNode thisChildNode : this.getChildren()) {
            for(ModularDecompositionNode otherChildNode : otherNode.getChildren()) {
                if(nodeSetsEqual(thisChildNode.getNodes(), otherChildNode.getNodes())) {
                    // found two child-nodes with equal graph nodes
                    // compare them recursively
                    if(!thisChildNode.equals(otherChildNode)) {
                        return false;
                    } else {
                        // children do match, stop this iteration and continue with the next
                        // candidate of this node's children
                        break;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Method checks whether two given sets of nodes are equal. Therefore it
     * checks if they got the same size, and the same elements.
     * 
     * @param setOne    The first set to check.
     * @param setTwo    The second set to check.
     * @return  True, if both sets are of equal size and contain the same elements.
     */
    private static boolean nodeSetsEqual(List<Node> setOne, List<Node> setTwo) {
        boolean sameSets = true;
        boolean foundLocally = false; // decide whether we found a matching
                                      // element within one iteration

        if (setOne.size() != setTwo.size()) {
            // lists have different size and so cannot be equal
            return false;
        }

        for (Node nodeOfOne : setOne) {
            foundLocally = false;
            for (Node nodeOfTwo : setTwo) {
                if (nodeOfOne.equals(nodeOfTwo)) {
                    // found matching node
                    foundLocally = true;
                    break;
                }
            }
            // did not find a matching node
            if (!foundLocally) {
                sameSets = false;
                break;
            }
        }

        return sameSets;
    }

    /**
     * Sets the activeEdgeNodes.
     *
     * @param activeEdges the activeEdgeNodes to set.
     */
    public void setActiveEdgeNodes(List<Node> activeEdges) {
        this.activeEdgeNodes = activeEdges;
    }

    /**
     * Returns the activeEdgeNodes.
     *
     * @return the activeEdgeNodes.
     */
    public List<Node> getActiveEdgeNodes() {
        return activeEdgeNodes;
    }
    
    /**
     * Checks whether this node is a leaf.
     * 
     * @return  True, if the node is a leaf.
     */
    public boolean isLeaf () {
        return(getChildren().isEmpty());
    }

//    public String toString() {
//        String output = "<";
//        
//        output += "Nodes: ";
//        for(Node node : this.nodes) {
////            output += node.getString("knut");
//        }
//        
//        output += ">";
//        
//        return output;
//    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
