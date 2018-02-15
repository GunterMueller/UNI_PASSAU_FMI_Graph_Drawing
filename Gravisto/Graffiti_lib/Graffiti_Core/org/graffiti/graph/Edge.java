// =============================================================================
//
//   Edge.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Edge.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.graph;

/**
 * Provides an interfaces for a <code>Graph</code><code>Edge</code>. An
 * <code>Edge</code> consists of source and target, directed edges point from
 * source to target. Source, target and <code>Edge</code> must belong to the
 * same <code>Graph</code>. Otherwise the copy-methods of the interface
 * <code>org.grafitti.graph.Graph</code> have to be used.
 * 
 * @version $Revision: 5767 $
 * 
 * @see GraphElement
 * @see Node
 * @see Graph
 */
public interface Edge extends GraphElement {

    /** Indicates that an <code>Edge</code> is directed. */
    public static final boolean DIRECTED = true;

    /** Indicates that an <code>Edge</code> is undirected. */
    public static final boolean UNDIRECTED = false;

    /**
     * Determines if an <code>Edge</code> is directed (<code>true</code>) or
     * not.
     * 
     * @param directed
     *            <code>true</code>, if the <code>Edge</code> is destined to be
     *            directed, <code>false</code> otherwise.
     */
    public void setDirected(boolean directed);

    /**
     * Returns <code>true</code>, if the <code>Edge</code> is directed,
     * <code>false</code> otherwise.
     * 
     * @return <code>true</code>, if the <code>Edge</code> is directed,
     *         <code>false</code> otherwise.
     */
    public boolean isDirected();

    /**
     * Sets the source of the current <code>Edge</code> to source.
     * <code>source</code> must be contained in the same <code>Graph</code> as
     * the current <code>Edge</code>. Informs the ListenerManager about the
     * change.
     * 
     * @param source
     *            the source to be set.
     * 
     * @exception GraphElementNotFoundException
     *                if source cannot be found in the <code>Graph</code>.
     * @exception IllegalArgumentException
     *                if source belongs to a different <code>Graph</code>.
     */
    public void setSource(Node source) throws GraphElementNotFoundException,
            IllegalArgumentException;

    /**
     * Returns the source of the current <code>Edge</code>.
     * 
     * @return the source of the current <code>Edge</code>.
     */
    public Node getSource();

    /**
     * Sets the target of the current <code>Edge</code> to target.
     * <code>target</code> must be contained in the same <code>Graph</code> as
     * the <code>Edge</code>. Informs the ListenerManager about the change.
     * 
     * @param target
     *            the target to be set.
     * 
     * @exception GraphElementNotFoundException
     *                if the target cannot be found in the <code>Graph</code>.
     * @exception IllegalArgumentException
     *                if target belongs to a different <code>Graph</code>.
     */
    public void setTarget(Node target) throws GraphElementNotFoundException,
            IllegalArgumentException;

    /**
     * Returns the target of the current <code>Edge</code>.
     * 
     * @return the target of the current <code>Edge</code>.
     */
    public Node getTarget();

    /**
     * Changes source and target of the <code>Edge</code>, that means if the
     * <code>Edge</code> is directed the direction will be changed.
     */
    public void reverse();
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
