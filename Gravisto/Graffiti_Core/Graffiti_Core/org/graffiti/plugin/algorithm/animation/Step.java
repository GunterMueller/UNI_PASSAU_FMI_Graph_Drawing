// =============================================================================
//
//   Step.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.algorithm.animation;

/**
 * One step of an animation. This interface is used by step driven
 * implementations of the <tt>Animation</tt> interface such as
 * <tt>ForwardAnimation</tt> and <tt>BidirectionalAnimation</tt>.
 * <p>
 * Steps that can be used only in forward animations are supposed to throw
 * <tt>UnsupportedOperationException</tt> on any attempt to call <tt>undo</tt>
 * or <tt>redo</tt>.
 * <p>
 * Bidirectional animations must be able to undo and redo the effects of the
 * last step performed. Therefore steps used in bidirectional animations must
 * implement <tt>undo</tt> and <tt>redo</tt> to process such requests.
 * <p>
 * After <tt>next</tt> has been called, <tt>undo</tt> and <tt>redo</tt> have to
 * be called strictly alternating; i.e. the only valid sequence of calls to undo
 * and redo on a given step <tt>s</tt> is:
 * 
 * <pre>
 * <tt>
 *  s.next()
 *  s.undo()
 *  s.redo()
 *  s.undo()
 *  s.redo()
 * </tt>
 *     etc.
 * </pre>
 * 
 * Note that this places no restrictions on calls to <tt>next</tt>.
 * 
 * @see ForwardAnimation
 * @see BidirectionalAnimation
 * 
 * @author Harald Frankenberger
 * @version $Revision$ $Date$
 */
public interface Step {

    /**
     * Returns <tt>true</tt> if this step has a successor.
     * 
     * @return <tt>true</tt> if this step has a successor.
     */
    boolean hasNext();

    /**
     * Performs this step and returns its successor.
     * 
     * @return the successor of this step.
     * @throws IllegalStateException
     *             if this step has no successor.
     */
    Step next();

    /**
     * Redoes the effects of this step after it has been undone.
     * 
     * @throws IllegalStateException
     *             if this step has not been undone.
     * @throws UnsupportedOperationException
     *             if this step is a singleton step.
     */
    void redo();

    /**
     * Undoes the effects of this step after it has been performed or redone.
     * 
     * @throws IllegalStateException
     *             if this step has not been performed or redone yet.
     * @throws UnsupportedOperationException
     *             if this step is a singleton step.
     */
    void undo();
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
