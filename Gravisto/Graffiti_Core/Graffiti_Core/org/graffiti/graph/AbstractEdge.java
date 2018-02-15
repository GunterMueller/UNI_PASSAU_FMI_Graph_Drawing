// =============================================================================
//
//   AbstractEdge.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractEdge.java 5779 2010-05-10 20:31:37Z gleissner $

package org.graffiti.graph;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.event.EdgeEvent;
import org.graffiti.event.ListenerManager;

/**
 * Provides default implementations of methods on edges.
 * 
 * @version $Revision: 5779 $
 */
public abstract class AbstractEdge extends AbstractGraphElement implements Edge {
    /** The logger for the current class. */
    private static final Logger logger = Logger.getLogger(AbstractEdge.class
            .getName());
    
    static {
        logger.setLevel(Level.SEVERE);
    }

    /**
     * Constructs a new <code>AbstractEdge</code>. Also sets the graph.
     * 
     * @param graph
     *            the <code>Graph</code> the <code>AbstractGraphElement</code>
     *            belongs to.
     */
    public AbstractEdge(Graph graph) {
        super(graph);
    }

    /**
     * Constructs a new <code>AbstractEdge</code>. Sets the graph of the new
     * <code>AbstractEdge</code>.
     * 
     * @param graph
     *            the <code>Graph</code> the new <code>AbstractEdge</code>
     *            instance shall belong to.
     * @param coll
     *            the <code>CollectionAttribute</code> of the newly created
     *            <code>AbstractEdge</code> instance.
     */
    public AbstractEdge(Graph graph, CollectionAttribute coll) {
        super(graph, coll);
    }

    /**
     * Determines if an <code>Edge</code> is directed (<code>true</code>) or
     * not. Informs the ListenerManager that the direction has been modified.
     * 
     * @param directed
     *            <code>true</code>, if the <code>Edge</code> is destined to be
     *            directed, <code>false</code> otherwise.
     */
    public void setDirected(boolean directed) {
        ListenerManager listMan = getListenerManager();
        listMan.preDirectedChanged(new EdgeEvent(this));
        logger.info("The edge is set to be directed: " + directed);
        doSetDirected(directed);
        listMan.postDirectedChanged(new EdgeEvent(this));
    }

    /**
     * Sets the source of the current <code>Edge</code> to <code>source</code>.
     * <code>source</code> must be contained in the same <code>Graph</code> as
     * the current <code>Edge</code>. Informs the ListenerManager about the
     * change.
     * 
     * @param source
     *            the source to be set.
     * 
     * @exception GraphElementNotFoundException
     *                if source cannot be found in the <code>Graph</code>.
     */
    public void setSource(Node source) throws GraphElementNotFoundException {
        assert source != null;

        ListenerManager listMan = this.getListenerManager();
        listMan.preSourceNodeChanged(new EdgeEvent(this));
        doSetSource(source);
        listMan.postSourceNodeChanged(new EdgeEvent(this));
    }

    /**
     * Sets the target of the current <code>Edge</code> to target. Target must
     * be contained in the same <code>Graph</code> as the <code>Edge</code>.
     * Informs the ListenerManager about the change.
     * 
     * @param target
     *            the target to be set.
     * 
     * @exception GraphElementNotFoundException
     *                if the target cannot be found in the <code>Graph</code>.
     */
    public void setTarget(Node target) throws GraphElementNotFoundException {
        assert target != null;

        ListenerManager listMan = this.getListenerManager();
        listMan.preTargetNodeChanged(new EdgeEvent(this));
        doSetTarget(target);
        listMan.postTargetNodeChanged(new EdgeEvent(this));
    }

    /**
     * Swaps source and target of the edge. Informs the listtenerManager about
     * the change.
     */
    public void reverse() {
        logger.info("swapping source and target of the edge");

        ListenerManager listMan = getListenerManager();
        listMan.preEdgeReversed(new EdgeEvent(this));
        doReverse();
        listMan.postEdgeReversed(new EdgeEvent(this));
    }

    /**
     * Swaps source and target of the edge.
     */
    protected abstract void doReverse();

    /**
     * Determines if an <code>Edge</code> is directed (<code>true</code>) or
     * not.
     * 
     * @param directed
     *            <code>true</code>, if the <code>Edge</code> is destined to be
     *            directed, <code>false</code> otherwise.
     */
    protected abstract void doSetDirected(boolean directed);

    /**
     * Sets the source of the current <code>Edge</code> to <code>source</code>.
     * <code>source</code> must be contained in the same <code>Graph</code> as
     * the current <code>Edge</code>.
     * 
     * @param source
     *            the source to be set.
     * 
     * @exception GraphElementNotFoundException
     *                if source cannot be found in the <code>Graph</code>.
     */
    protected abstract void doSetSource(Node source)
            throws GraphElementNotFoundException;

    /**
     * Sets the target of the current <code>Edge</code> to target. Target must
     * be contained in the same <code>Graph</code> as the <code>Edge</code>.
     * 
     * @param target
     *            the target to be set.
     * 
     * @exception GraphElementNotFoundException
     *                if the target cannot be found in the <code>Graph</code>.
     */
    protected abstract void doSetTarget(Node target)
            throws GraphElementNotFoundException;
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
