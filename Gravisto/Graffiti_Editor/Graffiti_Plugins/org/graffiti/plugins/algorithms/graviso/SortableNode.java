/**
 * 
 */
package org.graffiti.plugins.algorithms.graviso;

/**
 * Implements the comparable interface as a wrapper for nodes to be sorted in
 * refinement
 * 
 * @author lenhardt
 * @version $Revision: 1002 $
 */
public interface SortableNode extends Comparable<SortableNode>, Cloneable {

    public int compareTo(SortableNode o);

    public boolean equals(SortableNode o);

    /**
     * Returns the number of the node to be sorted.
     * 
     * @return the node number
     */
    public int getNodeNumber();

    /**
     * Returns the degree vector of the node to be sorted.
     * 
     * @return the degree vector
     */
    public int[] getDegreeVector();

    public SortableNode clone();

}
