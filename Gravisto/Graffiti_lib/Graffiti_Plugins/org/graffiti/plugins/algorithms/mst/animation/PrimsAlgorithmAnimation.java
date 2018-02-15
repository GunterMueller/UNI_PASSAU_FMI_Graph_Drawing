package org.graffiti.plugins.algorithms.mst.animation;

import java.util.List;

import org.graffiti.plugin.algorithm.animation.AbstractAnimation;
import org.graffiti.plugin.algorithm.animation.Animation;
import org.graffiti.plugin.algorithm.animation.BidirectionalAnimation;
import org.graffiti.plugin.algorithm.animation.ForwardAnimation;
import org.graffiti.plugins.algorithms.mst.adapters.GraphAdapter;
import org.graffiti.plugins.algorithms.mst.adapters.HeapAdapter;
import org.graffiti.plugins.algorithms.mst.adapters.NodeAdapter;

/**
 * Animated version of Prim's algorithm; supports steps to previous states. This
 * implementation uses instances of <tt>ForwardAnimation</tt> and
 * <tt>BidirectionalAnimation</tt> to implement its iteration logic. Both use
 * <tt>PrimsAlgorithmStep</tt> as a first step. Depending on which instance is
 * used, this animation acts as either a forward or bidirectional animation. You
 * select an instance by passing the appropriate value of
 * <tt>supportsPreviousSteps</tt> to this animation's constructor. This instance
 * acts as a delegate for all public methods of this animation except
 * <tt>isReady</tt>. Some of them such as <tt>supportsPreviousStep</tt> delegate
 * directly to the enclosed instance, others such as <tt>nextStep</tt> augment
 * the delegates functionality with additional processing. Consult the
 * specification of these methods for further information.
 * <p>
 * Both delegates use instances of <tt>PrimsAlgorithmStep</tt>.
 * 
 * 
 * @see org.graffiti.plugin.algorithm.animation.ForwardAnimation
 * @see org.graffiti.plugin.algorithm.animation.BidirectionalAnimation
 * @see org.graffiti.plugins.algorithms.mst.animation.PrimsAlgorithmStep
 * @see #PrimsAlgorithmAnimation(GraphAdapter, HeapAdapter, boolean)
 * 
 * @author Harald Frankenberger
 * @version $Revision$ $Date$
 */
public class PrimsAlgorithmAnimation extends AbstractAnimation {

    /**
     * Stores all instances created of PrimsAlgorithmAnimation in order to
     * guarentee correct processing during modifications of the underlying
     * graph.
     */
    private static List<PrimsAlgorithmAnimation> instances = new java.util.ArrayList<PrimsAlgorithmAnimation>(
            2);

    /**
     * The delegate used to implement the iteration logic of this animation.
     */
    private Animation delegate = null;

    /**
     * The graph underlying this animation.
     */
    private GraphAdapter graph = null;

    /**
     * The heap used by Prim's algorithm.
     */
    private HeapAdapter heap = null;

    private boolean isCleared = true;

    /**
     * Initializes this animation with the given <tt>GraphProxy</tt> and
     * <tt>HeapProxy</tt>. If <tt>supportsPreviousSteps</tt> is true, this
     * animation will support steps to previous states of Prim's algorithm.
     * <p>
     * Depending on the value of <tt>supportsPreviousSteps</tt> this animation
     * is initialized either with an instance of <tt>ForwardAnimation</tt> or
     * <tt>BidirectionalAnimation</tt>. This instance acts as a delegate for
     * calls to all public methods except <tt>isReady</tt>.
     * 
     * @param g
     * @param h
     * @param supportsPreviousSteps
     */
    public PrimsAlgorithmAnimation(GraphAdapter g, HeapAdapter h,
            boolean supportsPreviousSteps) {
        for (Animation a : instances) {
            a.clear();
        }
        if (supportsPreviousSteps) {
            delegate = new BidirectionalAnimation(new PrimsAlgorithmStep(h,
                    false));
        } else {
            delegate = new ForwardAnimation(new PrimsAlgorithmStep(h, true));
        }
        graph = g;
        heap = h;
        isCleared = true;
        instances.add(this);
    }

    /**
     * Returns <tt>true</tt> if the preconditions of the algorithm underlying
     * this animation are satisfied.
     * <p>
     * This implementation returns <tt>true</tt> if the underlying graph of this
     * animation is undirected and connected.
     * 
     * @return <tt>true</tt> if the preconditions of the algoritm underlying
     *         this animation are satisfied.
     */
    @Override
    public boolean isReady() {
        return graph.isUndirected() && graph.isConnected();
    }

    /**
     * Throws <tt>IllegalStateException</tt> if this animation is not ready.
     * 
     */
    private void checkIsReady() {
        if (!isReady())
            throw new IllegalStateException();
    }

    /**
     * Returns <tt>true</tt> if this animation has a next step.
     * <p>
     * This implementation first checks whether this animation is ready. If not
     * it throws <tt>IllegalStateException</tt>. Otherwise it checks whether
     * this animation is cleared. If so, it returns <tt>true</tt> if the
     * underlying graph is not empty. Otherwise it delegates to
     * <tt>hasNextStep</tt> of its enclosed <tt>Animation</tt> instance.
     * 
     * @return <tt>true</tt> if this animation has a next step.
     */
    @Override
    public boolean hasNextStep() {
        checkIsReady();
        if (isCleared)
            return !graph.isEmpty();
        else
            return delegate.hasNextStep();
    }

    /**
     * Returns <tt>true</tt> if this animation supports steps to previous states
     * of the underlying algorithm.
     * <p>
     * This implementation delegates to <tt>supportsPreviousStep</tt> of its
     * enclosed <tt>Animation</tt> instance.
     * 
     * @return <tt>true</tt> if this animation supports steps to previous states
     *         of the underlying algorithm.
     */
    @Override
    public boolean supportsPreviousStep() {
        return delegate.supportsPreviousStep();
    }

    /**
     * Returns <tt>true</tt> if this animation has a previous step.
     * <p>
     * This implementation first checks whether this animation is ready. If not
     * it throws <tt>IllegalStateException</tt>. Otherwise it delegates to
     * <tt>hasPreviousStep</tt> of its enclosed <tt>Animation</tt> instance.
     * 
     * @return <tt>true</tt> if this animation has a previous step.
     * @throws IllegalStateException
     *             if this animation is not ready.
     */
    @Override
    public boolean hasPreviousStep() {
        checkIsReady();
        return delegate.hasPreviousStep();
    }

    /**
     * Computes the next state of the underlying algorithm; i.e. moves the
     * animation one step forward.
     * <p>
     * This implementation first checks whether this animation is ready. If not
     * it throws <tt>IllegalStateException</tt>. Otherwise if proceeds as
     * follows:
     * <ol>
     * <li>if this animation is newly created or cleared, it initializes this
     * animation's data; i.e. adding attributes to the graph, nodes and edges,
     * adding nodes to the heap, etc.
     * <li>the delegate's <tt>nextStep</tt> method is called.
     * <li>if this animation does not have another next step, this animations
     * data are cleaned up; i.e. basically retaining only those attributes, that
     * are of interest for further processing of the graph.
     * </ol>
     * 
     * @see #hasNextStep()
     * @see #isReady()
     * 
     * @throws IllegalStateException
     *             if this animation does not have a next step.
     * @throws IllegalStateException
     *             if this animation is not ready.
     */
    @Override
    public void performNextStep() {
        checkIsReady();
        if (isCleared) {
            init();
            isCleared = false;
        }
        delegate.nextStep();
        if (!hasNextStep()) {
            graph.clean();
        }
    }

    /**
     * Computes the previous state of the underlying algorithm; i.e. moves this
     * animation one step backwards.
     * <p>
     * This implementation first checks whether this animation is ready. If not
     * it throws <tt>IllegalStateException</tt>. Otherwise it delegates to
     * <tt>previousStep</tt> of the enclosed <tt>Animation</tt> instance.
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
    @Override
    public void performPreviousStep() {
        checkIsReady();
        delegate.previousStep();
        if (!hasPreviousStep()) {
            clear();
        }
    }

    /**
     * This animation supports clear; returns <tt>true</tt>.
     * 
     * @return <tt>true</tt>.
     */
    @Override
    public boolean supportsClear() {
        return true;
    }

    /**
     * Returns <tt>true</tt> if this animation is cleared.
     * 
     * @return <tt>true</tt> if this animation is cleared.
     */
    @Override
    public boolean isCleared() {
        return isCleared;
    }

    /**
     * Resets this animation to its initial state.
     * <p>
     * This implementation first calls <tt>clear</tt> on this animation's
     * delegate. Then this animations <tt>GraphProxy</tt> and <tt>HeapProxy</tt>
     * instance are cleared.
     * 
     * @see org.graffiti.plugins.algorithms.mst.adapters.GraphAdapter#clear()
     * @see org.graffiti.plugins.algorithms.mst.adapters.HeapAdapter#clear()
     * 
     */
    @Override
    public void performClear() {
        delegate.clear();
        graph.clear();
        heap.clear();
        isCleared = true;
    }

    /**
     * Initializes this animation's data. If this animation's
     * <tt>GraphProxy</tt> is not empty it first calls its <tt>init(Heap)</tt>
     * method.
     * <p>
     * Then it processes the graph's start node and its adjacent nodes as
     * specified by Prim's algorithm. This is necessary as Prim's algorithm
     * selects nodes to grow the minimum spanning tree, but our animation should
     * begin with the first edge to be selected. So we preselect the first node
     * and select edges in subsequent steps.
     */
    private void init() {
        if (!graph.isEmpty()) {
            graph.init(heap);
            graph.startNode().setKey(0f);
            NodeAdapter n = heap.removePeek();
            n.select();
            for (NodeAdapter m : n.adjacentNodes()) {
                float weight = n.edgeTo(m).getWeight();
                if (!m.isSelected() && weight < m.getKey()) {
                    m.setKey(weight);
                    m.setParent(n);
                }
            }
        }
    }

    /**
     * Returns "Prim's Algorithm".
     */
    @Override
    public String getName() {
        return "Prim's Algorithm";
    }

    /**
     * Enables the graph modification policies of all instances of
     * <tt>PrimsAlgorithmAnimation</tt>.
     */
    @Override
    public void enableGraphModificationPolicy() {
        for (PrimsAlgorithmAnimation a : instances) {
            a.getGraphModificationPolicy().enable();
        }
    }

    /**
     * Disables the graph modification policies of all instances of
     * <tt>PrimsAlgorithmAnimation</tt>.
     */
    @Override
    public void disableGraphModificationPolicy() {
        for (PrimsAlgorithmAnimation a : instances) {
            a.getGraphModificationPolicy().disable();
        }
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
