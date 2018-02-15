package org.graffiti.plugin.algorithm.animation;

/**
 * An animation that supports only forward steps. A forward animation implements
 * the methods <tt>hasNextStep</tt> and <tt>nextStep</tt> using the state
 * pattern with instances of interface <tt>Step</tt> being the states. A forward
 * animation starts at a first step and delegates calls to <tt>hasNextStep</tt>
 * and <tt>nextStep</tt> to a <tt>Step</tt> instance that changes upon every
 * call to <tt>nextStep</tt>.
 * 
 * @see Step
 * 
 * @author Harald Frankenberger
 * @version $Revision$ $Date$
 */
public class ForwardAnimation extends AbstractAnimation {
    /**
     * The first step of this animation. As long as this animation's first step
     * is not specified, any attempt to call <tt>hasNextStep</tt> or
     * <tt>nextStep</tt> will throw <tt>IllegalStateException</tt>.
     */
    private Step first = null;

    /**
     * The next step of this animation.
     */
    private Step next = null;

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
    public ForwardAnimation() {
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
    public ForwardAnimation(Step first) {
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
        return next == null;
    }

    /**
     * Resets this animation to its initial state. After the call to
     * <tt>clear</tt> finishes, the delegate for calls to <tt>hasNextStep</tt>
     * and <tt>nextStep</tt> will be this animation's first step.
     */
    @Override
    public void performClear() {
        next = null;
    }

    /**
     * Returns <tt>true</tt> if this animation has a next step.
     * 
     * @return <tt>true</tt> if this animation has a next step.
     */
    @Override
    public boolean hasNextStep() {
        if (next == null) {
            checkFirstIsNotNull();
            return first.hasNext();
        } else
            return next.hasNext();
    }

    /**
     * Computes the next state of the underlying algorithm; i.e. moves the
     * animation one step forward.
     */
    @Override
    public void performNextStep() {
        if (next == null) {
            checkFirstIsNotNull();
            next = first.next();
        } else {
            next = next.next();
        }
    }

    /**
     * Sets the first step of this animation to the specified value.
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
     * Checks whether the first step of this animation has been properly
     * initialized; throws <tt>IllegalStateException</tt> otherwise.
     */
    private void checkFirstIsNotNull() {
        if (first == null)
            throw new IllegalStateException();
    }

    /**
     * Checks whether this animation is in a cleared state, i.e. newly created
     * or cleared by a call to <tt>clear</tt>; throws
     * <tt>IllegalStateException</tt> otherwise.
     */
    private void checkIsCleared() {
        if (next != null)
            throw new IllegalStateException();
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
