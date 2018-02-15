package org.graffiti.plugins.algorithms.planarity;

import java.util.LinkedList;
import java.util.List;

/**
 * The list stores the DFS childs for each node.
 * 
 * @author Wolfgang Brunner
 */
public class DFSChildList {

    /**
     * The first node of the list
     */
    private RealNode first;

    /**
     * The last node of the list
     */
    private RealNode last;

    /**
     * The size of the list
     */
    private int size;

    /**
     * Constructs a empty list.
     */
    public DFSChildList() {
        first = null;
        last = null;
        size = 0;
    }

    /**
     * Returns the list's size.
     * 
     * @return The size of the list
     */
    public int size() {
        return size;
    }

    /**
     * Tests whether the list is empty
     * 
     * @return <code>true</code> if the list is empty
     */
    public boolean isEmpty() {
        return (size == 0);
    }

    /**
     * Adds a <code>RealNode</code> to the list.
     * 
     * @param node
     *            The node to add
     */
    public void add(RealNode node) {
        if (size == 0) {
            first = node;
            last = node;
            node.leftDFSNeighbour = null;
            node.rightDFSNeighbour = null;
        } else {
            last.rightDFSNeighbour = node;
            node.leftDFSNeighbour = last;
            node.rightDFSNeighbour = null;
            last = node;
        }
        size++;
    }

    /**
     * Removes a <code>RealNode</code> from the list.
     * 
     * @param node
     *            The node to remove
     */
    public void remove(RealNode node) {
        if ((node == first) && (node == last)) {
            first = null;
            last = null;
        } else if (node == first) {
            first = first.rightDFSNeighbour;
            if (first != null) {
                first.leftDFSNeighbour = null;
            }
        } else if (node == last) {
            last = last.leftDFSNeighbour;
            if (last != null) {
                last.rightDFSNeighbour = null;
            }
        } else {
            node.leftDFSNeighbour.rightDFSNeighbour = node.rightDFSNeighbour;
            node.rightDFSNeighbour.leftDFSNeighbour = node.leftDFSNeighbour;
        }
        size--;

    }

    /**
     * Returns the first <code>RealNode</code> of the list.
     * 
     * @return The first <code>RealNode</code>
     */
    public RealNode getFirst() {
        return first;
    }

    /**
     * Returns a <code>java.util.List</code> containing the elements of this
     * list
     * 
     * @return A <code>java.util.List</code> containing the elements of this
     *         list
     */
    public List<RealNode> getList() {
        List<RealNode> result = new LinkedList<RealNode>();
        for (RealNode pNode = first; pNode != null; pNode = pNode.rightDFSNeighbour) {
            result.add(pNode);
        }
        return result;
    }
}
