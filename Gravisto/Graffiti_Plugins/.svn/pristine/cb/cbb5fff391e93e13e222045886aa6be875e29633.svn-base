// =============================================================================
//
//   PrimsAlgorithmAnimation.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.algorithm.animation;

import java.util.List;

/**
 * An animation that supports both steps forward and steps to previous states.
 * This implementation uses delegation to instances of interface <tt>Step</tt>
 * to implement the methods <tt>hasNextStep</tt>, <tt>nextStep</tt> and
 * <tt>previousStep</tt>: <tt>hasNextStep</tt> delegates to <tt>hasNext</tt> and
 * <tt>previousStep</tt> delegates to <tt>undo</tt> in interface <tt>Step</tt>.
 * <p>
 * The delegation pattern for <tt>nextStep</tt> depends on this animation's
 * current state. Let <tt>a</tt> be a newly created bidirectional animation and
 * <tt>n</tt> be its next step. A call to <tt>nextStep</tt> has the following
 * effects:
 * <ul>
 * <li>Execute <tt>n.next()</tt>
 * <li>Make <tt>n</tt> the previous step of this animation
 * </ul>
 * To achieve the second task <tt>n</tt> is added to a list of performed steps.
 * This course of action is taken as long as <tt>nextStep</tt> is called.
 * <p>
 * Let <tt>p</tt> be the step that was last performed by this animation. Now
 * suppose a client calls <tt>previousStep</tt>. This call has the following
 * effects:
 * <ul>
 * <li>Call <tt>p.undo</tt>.
 * <li>Make <tt>p</tt> the next step of this animation.
 * </ul>
 * To achieve the second task it's sufficient to remember <tt>p</tt>'s index in
 * the list of performed steps and to ensure that the next call to
 * <tt>nextStep</tt> will call <tt>redo</tt> on <tt>p</tt> instead of executing
 * <tt>p.next()</tt> and adding it to the list of steps performed. This course
 * of action is taken every time <tt>previousStep</tt> called.
 * <p>
 * Let <tt>i</tt> be the index of this animation's next step <tt>n</tt>. Suppose
 * a client calls <tt>nextStep</tt>. This call has the following effects:
 * <ul>
 * <li>Call <tt>n.redo()</tt>.
 * <li>Make <tt>n</tt> the previous step of this animation.
 * </ul>
 * To achieve the second task it's sufficient to increment <tt>i</tt> by one.
 * This course of action is taken as long as there are steps that can be redone.
 * 
 * @see Step
 * @see Step#hasNext()
 * @see Step#next()
 * @see Step#undo()
 * @see Step#redo()
 * 
 * @author Harald
 * @version $Revision$ $Date$
 */
public class BidirectionalAnimation extends AbstractAnimation {
    /**
     * The first step of this animation. As long as this animation's first step
     * is not specified, any attempt to call <tt>hasNextStep</tt> or
     * <tt>nextStep</tt> will throw <tt>IllegalStateException</tt>.
     */
    private Step first = null;

    /**
     * The next step of this animation. The next call to <tt>nextStep</tt> will
     * call this step's <tt>next</tt> method and add it to the list of performed
     * steps.
     */
    private Step next = null;

    /**
     * The list of performed steps.
     */
    private List<Step> steps = new java.util.ArrayList<Step>();

    /**
     * The index of the next step that has already been performed and is ready
     * to be redone. Incremented each time <tt>nextStep</tt> is called.
     * Decremented each time <tt>previousStep</tt> is called.
     */
    private int indexOfNextStep = 0;

    /**
     * Default constructor; does nothing. Note that as long as this animation's
     * first step is not properly initialized (i.e. assigned a non-null value)
     * every attempt to call <tt>hasNextStep</tt> or <tt>nextStep</tt> will
     * throw <tt>IllegalStateException</tt>. You can initalize this animation's
     * first step anytime this animation is in a cleared state, i.e. after
     * initialization or after a call to <tt>clear</tt> as long as
     * <tt>nextStep</tt> hasn't been called.
     * 
     * @see #setFirstStep(Step)
     */
    public BidirectionalAnimation() {
    }

    /**
     * Initializes this animation with the specified initial step. Note that as
     * long as this animation's first step is not properly initialized (i.e.
     * assigned a non-null value) every attempt to call <tt>hasNextStep</tt> or
     * <tt>nextStep</tt> will throw <tt>IllegalStateException</tt>. You can
     * initalize this animation's first step anytime this animation is in a
     * cleared state, i.e. after initialization or after a call to
     * <tt>clear</tt> as long as <tt>nextStep</tt> hasn't been called.
     * 
     * @see #setFirstStep(Step)
     * 
     * @param first
     *            the first step of this animation
     */
    public BidirectionalAnimation(Step first) {
        this.first = first;
    }

    /**
     * Returns <tt>true</tt> if the preconditions of the algorithm underlying
     * this animation are satisfied.
     * <p>
     * This implementation always returns <tt>true</tt>.
     * 
     * @return <tt>true</tt> if the preconditions of the algoritm underlying
     *         this animation are satisfied.
     */
    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public boolean supportsClear() {
        return true;
    }

    @Override
    public boolean isCleared() {
        return indexOfNextStep == 0;
    }

    /**
     * Returns true if this animation has a next step.
     * <p>
     * This implementation returns true if there are steps to redo. Otherwise it
     * delegates to <tt>hasNext</tt> of this animation's next step.
     * 
     * @return true if this animation has a next step.
     */
    @Override
    public boolean hasNextStep() {
        if (indexOfNextStep < steps.size())
            return true;
        else {
            assert indexOfNextStep == steps.size();
            if (next != null)
                return next.hasNext();
            else {
                checkFirstIsNotNull();
                return first.hasNext();
            }
        }
    }

    /**
     * Returns <tt>true</tt> if this animation supports steps to previous states
     * of the underlying algorithm.
     * <p>
     * This implementation always returns <tt>true</tt>.
     * 
     * @return <tt>true</tt> if this animation supports steps to previous states
     *         of the underlying algorithm.
     */
    @Override
    public boolean supportsPreviousStep() {
        return true;
    }

    /**
     * Computes the next state of the underlying algorithm; i.e. moves the
     * animation one step forward.
     * <p>
     * Uses <tt>Step.next</tt> to compute the next state of this animation if
     * there are no steps that can be redone; uses <tt>Step.redo</tt> otherwise.
     * 
     * @see Step#next()
     * @see Step#redo()
     */
    @Override
    public void performNextStep() {
        if (indexOfNextStep == steps.size()) {
            doNextStep();
        } else {
            redoNextStep();
        }
    }

    /**
     * Redoes the next step in the list of already performed steps.
     */
    private void redoNextStep() {
        steps.get(indexOfNextStep).redo();
        indexOfNextStep++;
    }

    /**
     * Performs the next step of this animation.
     */
    private void doNextStep() {
        if (next == null) {
            checkFirstIsNotNull();
            steps.add(first);
            next = first.next();
            indexOfNextStep++;
        } else {
            steps.add(next);
            next = next.next();
            indexOfNextStep++;
        }
    }

    /**
     * Returns true if this animation has a previous step.
     * 
     * @return true if this animation has a previous step.
     */
    @Override
    public boolean hasPreviousStep() {
        return indexOfNextStep != 0;
    }

    /**
     * Computes the previous state of the underlying algorithm; i.e. moves this
     * animation one step backwards.
     * 
     * @throws IllegalStateException
     *             if this animation does not have a previous step
     */
    @Override
    public void performPreviousStep() {
        if (indexOfNextStep == 0)
            throw new IllegalStateException();
        steps.get(indexOfNextStep - 1).undo();
        indexOfNextStep--;
    }

    /**
     * Resets this animation to its initial state.
     */
    @Override
    public void performClear() {
        steps.clear();
        indexOfNextStep = 0;
        next = null;
    }

    /**
     * Set the first step of this animation to the specified value.
     * 
     * @param s
     *            the first step of this animation.
     * @throws IllegalStateException
     *             if this animation is not in a cleared state, i.e. newly
     *             created or cleared by a call to <tt>clear</tt> and
     *             <tt>nextStep</tt> has not yet been called.
     */
    public void setFirstStep(Step s) {
        checkIsCleared();
        first = s;
    }

    /**
     * Checks whether this animation is in a cleared state, i.e. newly created
     * or cleared by a call to <tt>clear</tt>. Throws
     * <tt>IllegalStateException</tt> otherwise.
     */
    private void checkIsCleared() {
        if (next != null)
            throw new IllegalStateException();
    }

    /**
     * Checks whether the first step of this animation has been properly
     * initialized. Throws <tt>IllegalStateException</tt> otherwise.
     */
    private void checkFirstIsNotNull() {
        if (first == null)
            throw new IllegalStateException();
    }

}
