// =============================================================================
//
//   AbstractAlgorithm.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractAlgorithm.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.plugin.algorithm;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.AbstractParametrizable;
import org.graffiti.plugin.algorithm.animation.Animation;

/**
 * Implements some empty versions of non-obligatory methods.
 */
public abstract class AbstractAlgorithm extends AbstractParametrizable
        implements Algorithm {

    /** The graph on which the algorithm will work. */
    protected Graph graph;

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#attach(org.graffiti.graph.Graph)
     */
    public void attach(Graph g) {
        this.graph = g;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    public void check() throws PreconditionException {
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    public void reset() {
        this.graph = null;
        this.parameters = null;
    }

    /**
     * Returns <tt>true</tt> if this algorithm supports animation; i.e.
     * <tt>getAnimation</tt> will not throw an
     * <tt>UnsupportedOperationException</tt>.
     * <p>
     * This implementation always returns <tt>false</tt>.
     * 
     * @see #getAnimation()
     * 
     * @return <tt>true</tt> if this algorithm supports animation.
     */
    public boolean supportsAnimation() {
        return false;
    }

    /**
     * Returns an animation for this algorithm if it supports animation. An
     * algorithm supports animation if <tt>supportsAnimation</tt> returns
     * <tt>true</tt>. Throws <tt>UnsupportedOperationException</tt> otherwise.
     * <p>
     * This implementation always throws <tt>UnsupportedOperationException</tt>.
     * 
     * @see #supportsAnimation()
     * 
     * @return an animation for this algorithm.
     * @throws UnsupportedOperationException
     *             if this algorithm does not support animation.
     */
    public Animation getAnimation() {
        throw new UnsupportedOperationException();
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
