// =============================================================================
//
//   InnerNumberedTreeNode.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.util;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * This class is only meant to be used as an inner node of a
 * <tt>NumberedTree</tt>. It therefore should not be used in any other way.
 * 
 * @author Christian Brunnermeier
 * @version $Revision$ $Date$
 */
class InnerNumberedTreeNode extends NumberedTreeNode {
    // number of all leaves contained in the subtree of this node
    private int totalNumberOfLeaves = 0;

    // indicates whether the children of this node are leaves or inner nodes
    private boolean containsLeaves = false;

    /*
     * A LinkedList of child nodes of this node. Except for the root node every
     * inner node has to have 2 or 3 children after the function repairTree() of
     * NumberedTree is called.
     */
    private LinkedList<NumberedTreeNode> children;

    /**
     * Constructs an <tt>InnerNumberedTreeNode</tt>-object.
     * 
     * @param childrenAreLeaves
     *            Indicates if the children of this node are inner nodes or
     *            leaves.
     * @param fatherNode
     *            the father of this node
     * @param tree
     *            the <tt>NumberedTree</tt> this node belongs to
     */
    @SuppressWarnings("unchecked")
    InnerNumberedTreeNode(boolean childrenAreLeaves,
            InnerNumberedTreeNode fatherNode, NumberedTree tree) {
        super(tree);
        containsLeaves = childrenAreLeaves;
        father = fatherNode;
        children = new LinkedList<NumberedTreeNode>();
    }

    /**
     * Adds a node at the given position.<br>
     * If the position isn't at the end all nodes at or after this position will
     * be shifted up by one before adding the new node.
     * 
     * @param node
     *            node to insert
     * @param position
     *            position to insert the new node
     */
    void addNode(NumberedTreeNode node, int position) {
        // O(log(n))
        assert position >= 0;
        assert node != null;

        if (!containsLeaves) {
            // The node cannot be inserted directly but has to be passed to the
            // correct child to be inserted there.

            if (getInnerNode(0).totalNumberOfLeaves > position) {
                getInnerNode(0).addNode(node, position);
            } else {
                position -= getInnerNode(0).totalNumberOfLeaves;

                if (children.size() < 3
                        || getInnerNode(1).totalNumberOfLeaves > position) {
                    getInnerNode(1).addNode(node, position);
                } else {
                    position -= getInnerNode(1).totalNumberOfLeaves;
                    getInnerNode(2).addNode(node, position);
                }
            }
        } else {
            // Now the position has to be between 0 and children.size
            assert position <= children.size();

            children.add(position, node);
            NumberedTreeNode prev = null;
            NumberedTreeNode succ = null;

            if (position + 1 < children.size()) {
                /*
                 * There has been a node on the new nodes position which has
                 * been shifted to the right.
                 */
                succ = children.get(position + 1);
                prev = succ.getPrevNode();
            } else {
                /*
                 * The new node has been added at the end of the LinkedList (and
                 * therefore at the end of the NumberTree), so there is no
                 * successor but at least one node preceding the new one (if it
                 * wasn't the first node added to the NumberTree).
                 */
                if (children.size() > 1) { // it was not the first node
                    prev = children.get(position - 1);
                    succ = prev.getNextNode();
                }
            }

            node.setPrevNode(prev);
            node.setNextNode(succ);
            node.setFather(this);
            if (prev != null) {
                prev.setNextNode(node);
            }
            if (succ != null) {
                succ.setPrevNode(node);
            }
            updateTotalChildrenNumber(1);

            if (children.size() > 3) {
                tree.repairTree(this);
            }
        }
    }

    /**
     * Returns the node at the given position.
     * 
     * @param position
     *            Position of the node
     * @return the node at the given position
     */
    NumberedTreeNode getNode(int position) {
        // O(log(n))
        assert position >= 0;
        assert position < totalNumberOfLeaves;

        // The searched node is a child
        if (containsLeaves)
            return children.get(position);

        // The first child contains the position
        int nodeNumber = 0;
        InnerNumberedTreeNode node = getInnerNode(nodeNumber);

        while (node.totalNumberOfLeaves <= position) {
            position -= node.totalNumberOfLeaves;
            nodeNumber++;
            node = getInnerNode(nodeNumber);
        }
        return node.getNode(position);
    }

    /**
     * Removes the node at the given position
     * 
     * @param position
     *            Position of the node to be removed
     */
    void removeNode(int position) {
        // O(log(n)
        assert position >= 0;
        assert position < totalNumberOfLeaves;

        if (!containsLeaves) {
            int nodeNumber = 0;
            InnerNumberedTreeNode node = getInnerNode(nodeNumber);

            while (node.totalNumberOfLeaves <= position) {
                position -= node.totalNumberOfLeaves;
                nodeNumber++;
                node = getInnerNode(nodeNumber);
            }
            node.removeNode(position);
        } else {
            NumberedTreeNode toDelete = getNode(position);
            removeLeave(toDelete);
        }
    }

    /*
     * Removes the given child which has to be a leaf and calls
     * NumberedTree.repairTree if necessary.
     */
    void removeLeave(NumberedTreeNode child) {
        // O(log(n))

        assert containsLeaves;
        NumberedTreeNode prev = child.getPrevNode();
        NumberedTreeNode succ = child.getNextNode();
        if (succ != null) {
            succ.setPrevNode(prev);
        }
        if (prev != null) {
            prev.setNextNode(succ);
        }

        children.remove(child);
        updateTotalChildrenNumber(-1);

        if (children.size() < 2) {
            tree.repairTree(this);
        }
    }

    /*
     * Updates recursively the total number of children of this node and all of
     * it's predecessors.
     */
    private void updateTotalChildrenNumber(int number) {
        // O(log(n)
        totalNumberOfLeaves += number;
        if (father != null) {
            father.updateTotalChildrenNumber(number);
        }
    }

    /**
     * Removes and returns the first child node.
     * 
     * @return the first child of this node
     */
    NumberedTreeNode removeFirst() {
        // O(log n)
        if (containsLeaves) {
            updateTotalChildrenNumber(-1);
        } else {
            InnerNumberedTreeNode node = (InnerNumberedTreeNode) children
                    .getFirst();
            updateTotalChildrenNumber(-node.totalNumberOfLeaves);
            node.setFather(null);
        }
        return children.removeFirst();
    }

    /**
     * Adds the given node as the first child.
     * 
     * @param node
     *            node to add
     */
    void addFirst(NumberedTreeNode node) {
        // O(log n)
        if (containsLeaves) {
            node.setFather(this);
            updateTotalChildrenNumber(1);
        } else {
            assert node instanceof InnerNumberedTreeNode;

            updateTotalChildrenNumber(((InnerNumberedTreeNode) node).totalNumberOfLeaves);
            ((InnerNumberedTreeNode) node).setFather(this);
        }
        children.addFirst(node);
    }

    /**
     * Removes and returns the last child node
     * 
     * @return the last child of this node
     */
    NumberedTreeNode removeLast() {
        // O(log n)
        if (containsLeaves) {
            updateTotalChildrenNumber(-1);
        } else {
            InnerNumberedTreeNode node = (InnerNumberedTreeNode) children
                    .getLast();
            updateTotalChildrenNumber(-node.totalNumberOfLeaves);
            node.setFather(null);
        }
        return children.removeLast();
    }

    /**
     * Adds the given node as the last child.
     * 
     * @param node
     *            node to add
     */
    void addLast(NumberedTreeNode node) {
        // O(log n)
        if (containsLeaves) {
            node.setFather(this);
            updateTotalChildrenNumber(1);
        } else {
            assert node instanceof InnerNumberedTreeNode;
            updateTotalChildrenNumber(((InnerNumberedTreeNode) node).totalNumberOfLeaves);
            ((InnerNumberedTreeNode) node).setFather(this);
        }
        children.addLast(node);
    }

    /**
     * Removes and returns the child node at the given position.
     * 
     * @param position
     *            position of the node to remove
     * @return child node at the given position
     */
    NumberedTreeNode removeAt(int position) {
        // O(log n)
        assert position >= 0;
        assert position < children.size();

        if (containsLeaves) {
            updateTotalChildrenNumber(-1);
        } else {
            InnerNumberedTreeNode node = (InnerNumberedTreeNode) children
                    .get(position);
            updateTotalChildrenNumber(-node.totalNumberOfLeaves);
            node.setFather(null);
        }

        return children.remove(position);
    }

    /**
     * Adds a node at the given position
     * 
     * @param position
     *            position to add the node
     * @param node
     *            node to be added
     */
    void addAt(int position, NumberedTreeNode node) {
        // O(log n)
        assert position >= 0;
        assert position <= children.size();

        if (containsLeaves) {
            node.setFather(this);
            updateTotalChildrenNumber(1);
        } else {
            assert node instanceof InnerNumberedTreeNode;
            updateTotalChildrenNumber(((InnerNumberedTreeNode) node).totalNumberOfLeaves);
            ((InnerNumberedTreeNode) node).setFather(this);
        }
        children.add(position, node);
    }

    /**
     * Returns the number of the node.
     * 
     * @return node number
     */
    int getNumberOfChild(NumberedTreeNode child) {
        // O(1) - O(log n)
        HashMap<NumberedTreeNode, Integer> numbers = tree.getNumbers();
        Integer storedNumber = numbers.get(this);

        if (storedNumber == null) {
            // number is out-of-date and has to be reevaluated.
            if (father == null) {
                // node is root
                storedNumber = 0;
            } else {
                storedNumber = father.getNumberOfChild(this);
            }
            numbers.put(this, storedNumber);
        }

        int childNumber = 0;
        while (childNumber < children.size()) {
            NumberedTreeNode nextChild = children.get(childNumber);
            if (nextChild == child) {
                break;
            }

            if (containsLeaves) {
                storedNumber++;
            } else {
                storedNumber += ((InnerNumberedTreeNode) nextChild).totalNumberOfLeaves;
            }
            childNumber++;
        }

        return storedNumber;
    }

    /**
     * A String-representation of this node containing the total number of
     * children as well as the children's String-representations.
     */
    @Override
    public String toString() {
        return "(" + totalNumberOfLeaves + ")" + children.toString();
    }

    /*  ******** GETTER AND SETTER ***************** */

    InnerNumberedTreeNode getInnerNode(int position) {
        return (InnerNumberedTreeNode) children.get(position);
    }

    int size() {
        return children.size();
    }

    boolean containsLeeves() {
        return containsLeaves;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
