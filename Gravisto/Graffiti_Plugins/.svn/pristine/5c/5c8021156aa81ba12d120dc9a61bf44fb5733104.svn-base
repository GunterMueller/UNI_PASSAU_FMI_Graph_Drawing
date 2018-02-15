package org.graffiti.plugin.algorithm.animation;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * Partial implementation of a forward animation (i.e. an animation that does
 * not support steps to previous states). All a client has to do to implement a
 * forward animation is to extend this class and provide implementations for the
 * <tt>performClear</tt>, <tt>isReady</tt>, <tt>hasNextStep</tt> and
 * <tt>performNextStep</tt> methods. The resulting animation will provide only
 * steps forward and will clear itself when modifications of the underlying
 * graph occur.
 * <p>
 * As the implementation of <tt>GraphModificationPolicy</tt> given here relies
 * on the implementation of <tt>clear</tt>, clients that do not support
 * <tt>clear</tt> <i>must</i> implement another graph modification policy.
 * Otherwise any attempt to modify a graph while an animation is running will
 * result in an <tt>UnsupportedOperationException</tt> being thrown.
 * <p>
 * As any reasonable algorithm will modifiy attributes of the underlying graph,
 * an animation's graph modification policy can be temporarily disabled. This is
 * done by calling the respective methods
 * <tt>disableGraphModificationPolicy</tt> and
 * <tt>enableGraphModificationPolicy</tt>. Actually the implementations of
 * <tt>clear</tt>, <tt>nextStep</tt> and <tt>previousStep</tt> given by this
 * class are template methods. The methods doing the actual work are those
 * prefixed with "perform". The template methods disable notifications of graph
 * modifications before any call to the actual worker methods and re-enable them
 * afterwards. So the worker methods are the natural extension points of this
 * class for subclassing clients.
 * <p>
 * The default implementations of the worker methods all throw
 * <tt>UnsupportedOperationException</tt>.
 * <p>
 * A final word of caution: Make sure two and more instances of the same
 * animation can operate on one graph simultaneously. Suppose your
 * implementation of <tt>nextStep</tt> has the side effect of removing all
 * unnecessary attributes after the last step has been completed. Without
 * further precautions this will violate the principle from above. Suppose one
 * instance of your animation is in the middle of processing when the other
 * instance finished. The first instance will rely on all attributes being
 * present. As the second instance removed some attributes a runtime exception
 * (or worse a weird bug) will occur.
 * <p>
 * A possible solution to this problem is to track all created instances in a
 * list. Make sure their graph modification policies are all enabled and
 * disabled at the same time and clear them as soon as another instance is
 * created.
 * 
 * @author Harald Frankenberger
 * @version $Revision$ $Date$
 */
public abstract class AbstractAnimation implements Animation {

    /**
     * The graph modification policy of this animation.
     */
    protected GraphModificationPolicy graphModificationPolicy = null;

    /**
     * Sole constructor for subclass invocation.
     * 
     */
    protected AbstractAnimation() {
    }

    /**
     * Returns <tt>true</tt> if the preconditions of the underlying algorithm
     * are satisfied.
     * 
     * @return <tt>true</tt> if the preconditions of the underlying algorithm
     *         are satisfied.
     */
    public abstract boolean isReady();

    /**
     * Returns <tt>true</tt> if this animation supports clearing; i.e. a call to
     * <tt>clear</tt> or <tt>isCleared</tt> will not throw
     * <tt>UnsupportedOperationException</tt>.
     * 
     * @see #clear()
     * @see #isCleared()
     * @return <tt>true</tt> if this animation supports clearing.
     */
    public boolean supportsClear() {
        return false;
    }

    /**
     * Returns <tt>true</tt> if this animation is cleared; i.e. it is newly
     * created or <tt>clear</tt> has been called and no change of state has
     * occured yet.
     * 
     * @see #supportsClear()
     * @see #clear()
     * @return <tt>true</tt> if this animation is cleared.
     * @throws UnsupportedOperationException
     *             if this animation does not support clear.
     */
    public boolean isCleared() {
        throw new UnsupportedOperationException();
    }

    /**
     * Resets this animation to its initial state. A call to <tt>clear</tt> is
     * the only means of resetting this animation if steps to previous states
     * are not supported.
     * <p>
     * On completion of a call to <tt>clear</tt>, this animation's state is
     * equivalent to its state immediately after constructor invocation. Note
     * that this implies, that all modifications to this animation's data are
     * undone, too; i.e. data structures are cleared, changes to the underlying
     * graph are undone, etc.
     */
    public void clear() {
        disableGraphModificationPolicy();
        performClear();
        enableGraphModificationPolicy();
    }

    /**
     * Worker method called by the template implementation of <tt>clear</tt>.
     * 
     * @see #clear()
     * 
     */
    public void performClear() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns true if this animation has a next step.
     * 
     * @return true if this animation has a next step.
     */
    public abstract boolean hasNextStep();

    /**
     * Returns <tt>true</tt> if this animation supports steps to previous states
     * of the underlying algorithm.
     * <p>
     * This implementation always returns <tt>false</tt>.
     * 
     * @return <tt>true</tt> if this animation supports steps to previous states
     *         of the underlying algorithm.
     */
    public boolean supportsPreviousStep() {
        return false;
    }

    /**
     * Returns true if this animation has a previous step. Throws
     * <tt>UnsupportedOperationException</tt> if this animation does not support
     * steps to previous states.
     * <p>
     * This implementation always throws <tt>UnsupportedOperationException</tt>.
     * 
     * @return true if this animation has a previous step.
     * @throws UnsupportedOperationException
     *             if this animation does not support steps to previous states.
     */
    public boolean hasPreviousStep() {
        throw new UnsupportedOperationException();
    }

    /**
     * Computes the next state of the underlying algorithm; i.e. moves the
     * animation one step forward.
     * 
     * @throws IllegalStateException
     *             if this animation does not have a next step.
     * @throws IllegalStateException
     *             if this animation is not ready.
     */
    public void nextStep() {
        disableGraphModificationPolicy();
        performNextStep();
        enableGraphModificationPolicy();
    }

    /**
     * Worker method called by the template implementation of <tt>nextStep</tt>.
     * 
     * @see #nextStep()
     * 
     */
    protected void performNextStep() {
        throw new UnsupportedOperationException();
    }

    /**
     * Computes the previous state of the underlying algorithm; i.e. moves this
     * animation one step backwards.
     * <p>
     * This implementation always throws <tt>UnsupportedOperationException</tt>.
     * 
     * @throws IllegalStateException
     *             if this animation does not have a previous step or does not
     *             support steps to previous states at all.
     * @throws IllegalStateException
     *             if this animation is not ready.
     * @throws UnsupportedOperationException
     *             if this animation does not support steps to previous states.
     */
    public void previousStep() {
        disableGraphModificationPolicy();
        performPreviousStep();
        enableGraphModificationPolicy();
    }

    /**
     * Worker method called by the template implementation of
     * <tt>previousStep</tt>.
     * 
     * @see #previousStep()
     * 
     */
    protected void performPreviousStep() {
        throw new UnsupportedOperationException();
    }

    /**
     * Enables the graph modification policy of this animation.
     * 
     * @see #graphModificationPolicy
     */
    protected void enableGraphModificationPolicy() {
        if (this.graphModificationPolicy != null) {
            graphModificationPolicy.enable();
        }
    }

    /**
     * Disables the graph modification policy of this animation.
     * 
     * @see #graphModificationPolicy
     */
    protected void disableGraphModificationPolicy() {
        if (graphModificationPolicy != null) {
            graphModificationPolicy.disable();
        }
    }

    /**
     * Returns the name of this animation.
     * <p>
     * This implementation returns <tt>getClass().getSimpleName()</tt>.
     * 
     * @return the name of this animation.
     */
    public String getName() {
        return getClass().getSimpleName();
    }

    /**
     * Returns the graph modification policy of this animation.
     * <p>
     * The returned graph modification policy will clear this animation whenever
     * the underlying graph is modified.
     * 
     * @return the graph modification policy of this animation.
     */
    public GraphModificationPolicy getGraphModificationPolicy() {
        if (this.graphModificationPolicy == null) {
            graphModificationPolicy = new GraphModificationPolicy() {
                private boolean isEnabled = false;

                public void enable() {
                    isEnabled = true;
                }

                public void disable() {
                    isEnabled = false;
                }

                public void edgeAdded(Edge e) {
                    if (isEnabled) {
                        clear();
                    }
                }

                public void edgeModified(Edge e) {
                    if (isEnabled) {
                        clear();
                    }
                }

                public void edgeRemoved(Edge e) {
                    if (isEnabled) {
                        clear();
                    }
                }

                public void nodeAdded(Node n) {
                    if (isEnabled) {
                        clear();
                    }
                }

                public void nodeModified(Node n) {
                    if (isEnabled) {
                        clear();
                    }
                }

                public void nodeRemoved(Node n) {
                    if (isEnabled) {
                        clear();
                    }
                }

                public void graphModified(Graph g) {
                    if (isEnabled) {
                        clear();
                    }
                }

                public void graphCleared(Graph g) {
                    if (isEnabled) {
                        clear();
                    }
                }
            };
        }
        return graphModificationPolicy;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
