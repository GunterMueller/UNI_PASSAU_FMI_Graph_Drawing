package org.graffiti.plugin.algorithm.animation;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * An animated version of an algorithm. Basically an animation iterates over the
 * sequence of steps as performed by an algorithm.
 * <p>
 * An animation can support iteration forward and backward by means of the
 * methods <tt>nextStep</tt>, <tt>previousStep</tt> and <tt>clear</tt>. As long
 * as the preconditions of the algorithm underlying this animation are not
 * satisfied, <tt>isReady</tt> will return <tt>false</tt> and any attempt to
 * call <tt>hasNextStep</tt>, <tt>hasPreviousStep</tt>, <tt>nextStep</tt> or
 * <tt>previousStep</tt> will throw <tt>IllegalStateException</tt>.
 * <p>
 * Animations can react differently on the event of the underlying graph being
 * modified. This behaviour is specified by the animation's
 * <tt>GraphModificationPolicy</tt>.
 * 
 * @author Harald Frankenberger
 * @version $Revision$ $Date$
 * @see Animation.GraphModificationPolicy
 */
public interface Animation {

    static final Animation EMPTY_ANIMATION = new AbstractAnimation() {
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
            return true;
        }

        @Override
        public void clear() {
        }

        @Override
        public boolean supportsPreviousStep() {
            return true;
        }

        @Override
        public boolean hasPreviousStep() {
            return false;
        }

        @Override
        public void previousStep() {
            throw new IllegalStateException();
        }

        @Override
        public boolean hasNextStep() {
            return false;
        }

        @Override
        public void nextStep() {
            throw new IllegalStateException();
        }

        @Override
        public String getName() {
            return "Empty animation";
        }
    };

    /**
     * Returns <tt>true</tt> if the preconditions of the algorithm underlying
     * this animation are satisfied.
     * 
     * @return <tt>true</tt> if the preconditions of the algoritm underlying
     *         this animation are satisfied.
     */
    public boolean isReady();

    /**
     * Returns <tt>true</tt> if this animation supports clearing; i.e. a call to
     * <tt>clear</tt> or <tt>isCleared</tt> will not throw
     * <tt>UnsupportedOperationException</tt>.
     * 
     * @see #clear()
     * @see #isCleared()
     * @return <tt>true</tt> if this animation supports clearing.
     */
    public boolean supportsClear();

    /**
     * Returns <tt>true</tt> if this animation is cleared; i.e. it is newly
     * created or <tt>clear</tt> has been called and no change of state has
     * occured yet.
     * 
     * @see #supportsClear()
     * @see #clear()
     * @return <tt>true</tt> if this animation is cleared.
     * @throws UnsupportedOperationException
     *             if this animation does not support <tt>clear<tt>.
     */
    public boolean isCleared();

    /**
     * Resets this animation to its initial state. A call to <tt>clear</tt> is
     * the only means of resetting an animation if steps to previous states are
     * not supported.
     * <p>
     * On completion of a call to <tt>clear</tt>, an animation's state is
     * equivalent to its state immediately after constructor invocation. Note
     * that this implies, that all modifications to the animation's data are
     * undone, too; i.e. data structures are cleared, changes to the underlying
     * graph are undone, etc.
     */
    public void clear();

    /**
     * Returns true if this animation has a next step.
     * 
     * @return true if this animation has a next step.
     */
    public boolean hasNextStep();

    /**
     * Returns true if this animation has a previous step. Throws
     * <tt>UnsupportedOperationException</tt> if this animation does not support
     * steps to previous states.
     * 
     * @see #previousStep()
     * @see #supportsPreviousStep()
     * 
     * @return true if this animation has a previous step.
     * @throws UnsupportedOperationException
     *             if this animation does not support steps to previous states.
     * 
     */
    public boolean hasPreviousStep();

    /**
     * Computes the next state of the underlying algorithm; i.e. moves the
     * animation one step forward.
     * 
     * @see #hasNextStep()
     * @see #isReady()
     * 
     * @throws IllegalStateException
     *             if this animation does not have a next step.
     * @throws IllegalStateException
     *             if this animation is not ready.
     */
    public void nextStep();

    /**
     * Returns <tt>true</tt> if this animation supports steps to previous states
     * of the underlying algorithm.
     * 
     * @return <tt>true</tt> if this animation supports steps to previous states
     *         of the underlying algorithm.
     */
    public boolean supportsPreviousStep();

    /**
     * Computes the previous state of the underlying algorithm; i.e. moves this
     * animation one step backwards.
     * 
     * @see #supportsPreviousStep()
     * @see #hasPreviousStep()
     * @see #isReady()
     * 
     * @throws IllegalStateException
     *             if this animation does not have a previous step or does not
     *             support steps to previous states at all.
     * @throws IllegalStateException
     *             if this animation is not ready.
     * @throws UnsupportedOperationException
     *             if this animation does not support steps to previous states.
     */
    public void previousStep();

    /**
     * Returns the name of this animation.
     * 
     * @return the name of this animation.
     */
    public String getName();

    /**
     * Returns the graph modification policy of this animation.
     * 
     * @return the graph modification policy of this animation.
     * 
     * @see Animation.GraphModificationPolicy
     */
    public GraphModificationPolicy getGraphModificationPolicy();

    /**
     * Specifies the behaviour of an animation on the event of its underlying
     * graph being modified. Through this interface animations expose the
     * possibility to signal the addition, modification and removal of nodes and
     * edges as well as the underlying graph having been modified or cleared.
     * <p>
     * The general contract of <tt>GraphModificationPolicy</tt> is to signal
     * only one and the most specific event; i.e. the event of a node having
     * been added will trigger <tt>nodeAdded</tt> but <i>not</i>
     * <tt>graphModified</tt> or both.
     * 
     * @author Harald Frankenberger
     * @version $Revision$ $Date$
     */
    interface GraphModificationPolicy {

        /**
         * Signals the event that the specified node has been added.
         * 
         * @param n
         *            the node that has been added.
         */
        void nodeAdded(Node n);

        /**
         * Signals the event that the specified node has been modified.
         * <p>
         * Modifications in this context encompass e.g. the addition or removal
         * of an attribute, or the modification of an attribute's value.
         * 
         * @param n
         *            the node that has been modified.
         */
        void nodeModified(Node n);

        /**
         * Signals the event that the specified node has been removed.
         * <p>
         * Note that the specified node has already been removed; i.e. its
         * attributes could have been already removed or it could have been
         * modified in another way.
         * 
         * @param n
         *            the node that has been removed.
         */
        void nodeRemoved(Node n);

        /**
         * Signals the event that the specified edge has been added.
         * 
         * @param e
         *            the edge that has been added.
         */
        void edgeAdded(Edge e);

        /**
         * Signals the event that the specified edge has been modified.
         * <p>
         * Modifications in this context encompass e.g. the addition or removal
         * of an attribute, or the modification of an attribute's value.
         * 
         * @param e
         *            the edge that has been modified.
         */
        void edgeModified(Edge e);

        /**
         * Signals the event that the specified edge has been removed.
         * <p>
         * Note that the specified edge has already been removed; i.e. its
         * attributes could have been already removed or it could have been
         * modified in another way.
         * 
         * 
         * @param e
         *            the edge that has been removed.
         */
        void edgeRemoved(Edge e);

        /**
         * Signals the event that the specified graph has been modified.
         * <p>
         * Modifications in this context encompass e.g. the addition or removal
         * of an attribute, or the modification of an attribute's value.
         * 
         * @param g
         *            the graph that has been modified.
         */
        void graphModified(Graph g);

        /**
         * Signals the event that the specified graph has been cleared.
         * <p>
         * Note that the specified graph has already been cleared; i.e. its
         * attributes could have been already removed or it could have been
         * modified in another way.
         * 
         * @param g
         *            the graph that has been cleared.
         */
        void graphCleared(Graph g);

        /**
         * Enables this graph modification policy; i.e. its notification
         * mechanism.
         */
        void enable();

        /**
         * Disables this graph modification policy; i.e. its notification
         * mechanism.
         */
        void disable();

    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
