// =============================================================================
//
//   Algorithm.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Algorithm.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.plugin.algorithm;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.Parametrizable;
import org.graffiti.plugin.algorithm.animation.Animation;

/**
 * An <code>Algorithm</code> has to provide a function that returns an array of
 * <code>Parameters</code> it needs, in order to give user interfaces the
 * possibility to provide the <code>Algorithm</code> with apropriate parameters.
 * 
 * @version $Revision: 5767 $
 */
public interface Algorithm extends Parametrizable {

    /**
     * Attaches the given graph to this algorithm.
     * 
     * @param g
     *            the graph to attach.
     */
    public void attach(Graph g);

    /**
     * Checks whether all preconditions of the current graph are satisfied.
     * 
     * @throws PreconditionException
     *             if the preconditions of the current graph are not satisfied.
     */
    public void check() throws PreconditionException;

    /**
     * Executes the whole algorithm.
     */
    public void execute();

    /**
     * Resets the internal state of the algorithm.
     */
    public void reset();

    /**
     * Returns <tt>true</tt> if this algorithm supports animation; i.e.
     * <tt>getAnimation</tt> will not throw an
     * <tt>UnsupportedOperationException</tt>.
     * 
     * @see #getAnimation()
     * 
     * @return <tt>true</tt> if this algorithm supports animation.
     */
    public boolean supportsAnimation();

    /**
     * Returns an animation for this algorithm if it supports animation. An
     * algorithm supports animation if <tt>supportsAnimation</tt> returns
     * <tt>true</tt>. Throws <tt>UnsupportedOperationException</tt> otherwise.
     * 
     * @see #supportsAnimation()
     * 
     * @return an animation for this algorithm.
     * @throws UnsupportedOperationException
     *             if this algorithm does not support animation.
     */
    public Animation getAnimation();

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
