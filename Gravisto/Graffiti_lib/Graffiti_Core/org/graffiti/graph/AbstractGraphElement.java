// =============================================================================
//
//   AbstractGraphElement.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractGraphElement.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.graph;

import org.graffiti.attributes.AbstractAttributable;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.event.ListenerManager;

/**
 * GraphElements are Attributables which know the graph they belong to. This
 * class provides the functionality for accessing the graph.
 * 
 * @see AbstractNode
 * @see AbstractEdge
 * @see Node
 * @see Edge
 */
public abstract class AbstractGraphElement extends AbstractAttributable
        implements GraphElement {

    /** The graph the current <code>AbstractGraphElement</code> belongs to. */
    protected Graph graph;

    /**
     * Constructs a new <code>AbstrctGraphElement</code>.
     */
    public AbstractGraphElement() {
    }

    /**
     * Constructs a new <code>AbstrctGraphElement</code>.
     * 
     * @param coll
     *            the <code>CollectionAttribute</code> of the new
     *            <code>AbstractGraphElement</code> instance.
     */
    public AbstractGraphElement(CollectionAttribute coll) {
        super(coll);
    }

    /**
     * Constructs a new <code>AbstrctGraphElement</code>. Sets the graph of the
     * current <code>AbstrctGraphElement</code>.
     * 
     * @param graph
     *            the graph the <code>AbstrctGraphElement</code> belongs to.
     * @param coll
     *            the <code>CollectionAttribute</code> of the new
     *            <code>AbstractGraphElement</code> instance.
     */
    public AbstractGraphElement(Graph graph, CollectionAttribute coll) {
        super(coll);
        this.graph = graph;
    }

    /**
     * Constructs a new <code>AbstrctGraphElement</code>. Sets the graph of the
     * current <code>AbstrctGraphElement</code>.
     * 
     * @param graph
     *            the graph the <code>AbstrctGraphElement</code> belongs to.
     */
    public AbstractGraphElement(Graph graph) {
        assert graph != null;
        this.graph = graph;
    }

    /**
     * Returns the Graph the <code>AbstractGraphElement</code> belongs to.
     * 
     * @return the Graph the GraphElement belongs to.
     */
    public Graph getGraph() {
        return this.graph;
    }

    /**
     * Remove element from graph.
     */
    public void remove() {
        this.graph = null;
    }

    /**
     * Returns the ListenerManager of the <code>GraphElement</code>.
     * 
     * @return the ListenerManager of the <code>GraphElement</code>.
     */
    public ListenerManager getListenerManager() {
        return this.getGraph().getListenerManager();
    }

    @Override
    public String toString() {
        try {
            return getString("label.label");
        } catch (Exception e1) {
            try {
                return getString("label0.label");
            } catch (Exception e2) {
                return super.toString();
            }
        }
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
