package org.graffiti.plugins.algorithms.mst;

import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.algorithm.animation.Animation;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.FloatParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.mst.adapters.EdgeAdapterFactory;
import org.graffiti.plugins.algorithms.mst.adapters.GraphAdapter;
import org.graffiti.plugins.algorithms.mst.adapters.HeapAdapter;
import org.graffiti.plugins.algorithms.mst.adapters.NodeAdapter;
import org.graffiti.plugins.algorithms.mst.adapters.NodeAdapterFactory;
import org.graffiti.plugins.algorithms.mst.animation.PrimsAlgorithmAnimation;

/**
 * An implementation of Prims Algorithm for computing minimum spanning trees in
 * undirected graphs; this class supports animations.
 * 
 * @author Harald
 * @version $Revision$ $Date$
 */
public class PrimsAlgorithm extends AbstractAlgorithm {
    /**
     * Sole constructor; does nothing.
     * 
     */
    public PrimsAlgorithm() {
    }

    /**
     * Returns the name of this algorithm.
     * <p>
     * This implementation returns "Prim's Algorithm".
     * 
     * @return the name of this algorithm.
     */
    public String getName() {
        return "Prim's Algorithm";
    }

    /**
     * Returns a list of <code>Parameter</code> that are set for this algorithm.
     * <p>
     * This implementation returns a list of four parameters:
     * <ol>
     * <li>a <tt>BooleanParameter</tt> that is <tt>true</tt> if this algorithm
     * uses its animation.
     * <li>a <tt>BooleanParameter</tt> that ist <tt>true</tt> if this
     * algorithm's animation supports steps to previous states.
     * <li>a <tt>BooleanParameter</tt> that is <tt>true</tt> if this algorithm's
     * animation uses colored edges to show its result.
     * <li>a <tt>FloatParameter</tt> that specifies the default weight for edges
     * that are not labelled by a string that parses as a float value.
     * </ol>
     * 
     * @see org.graffiti.plugin.parameter.BooleanParameter
     * @see org.graffiti.plugin.parameter.FloatParameter
     * 
     * @return a collection of <code>Parameter</code> that are needed by the
     *         <code>Algorithm</code>.
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        if (parameters == null) {
            parameters = new Parameter[] {
                    new BooleanParameter(
                            true,
                            "Use animation:",
                            "Unselect this if you don't want to use the animated version of Prims algorithm."),
                    new BooleanParameter(
                            false,
                            "Enable steps to previous states: ",
                            "Select this if you want to be able to jump to previous steps of the animation."),
                    new BooleanParameter(true, "Show tree edges:",
                            "Unselect this if tree edges should not be coloured."),
                    new FloatParameter(1f, "Default weight:",
                            "Default weight for unlabelled edges.") };
        }
        return parameters;
    }

    /**
     * Sets the parameters for this algorithm. Must have the same types and
     * order as the array returned by <code>getParameter</code>.
     * <p>
     * See the specification of <tt>getParameters</tt> for further information
     * on the parameters of this algorithm.
     * 
     * @see #getAlgorithmParameters()
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] p) {
        this.parameters = p;
    }

    /**
     * Specifies whether this algorithm uses its animation (instead of running
     * just once).
     * <p>
     * This implementation sets the first parameter of the array returned by
     * <tt>getParameters</tt>
     * 
     * @see #getAlgorithmParameters()
     * 
     * @param b
     *            <tt>true</tt> if this algorithm uses its animation;
     *            <tt>false</tt> if it runs only once.
     */
    public void setUsesAnimation(boolean b) {
        ((BooleanParameter) getAlgorithmParameters()[0]).setValue(b);
    }

    /**
     * Returns <tt>true</tt> if this algorithm uses its animation.
     * <p>
     * This implementation returns the value of the first parameter of the array
     * returned by <tt>getParameters</tt>
     * 
     * @see #getAlgorithmParameters()
     * 
     * @return <tt>true</tt> if this algorithm uses its animation.
     */
    public boolean usesAnimation() {
        return ((BooleanParameter) getAlgorithmParameters()[0]).getBoolean();
    }

    /**
     * Specifies whether the animation of this algorithm supports steps to
     * previous states of this algorithm.
     * <p>
     * This implementation sets the second parameter of the array returned by
     * <tt>getParameters</tt>
     * 
     * @see #getAlgorithmParameters()
     * 
     * @param b
     *            <tt>true</tt> if this algorithm's animation supports steps to
     *            previous states, <tt>false</tt> otherwise.
     */
    public void setStepsToPreviousStatesEnabled(boolean b) {
        ((BooleanParameter) getAlgorithmParameters()[1]).setValue(b);
    }

    /**
     * Returns <tt>true</tt> if this algorithm's animation supports steps to
     * previous states of this algorithm.
     * <p>
     * This implementation returns the value of the second parameter of the
     * array returned by <tt>getParameters</tt>.
     * 
     * @see #getAlgorithmParameters()
     * 
     * @return <tt>true</tt> if this algorithm's animation supports steps to
     *         previous states of this algorithm; <tt>false</tt> otherwise.
     */
    public boolean stepsToPreviousStatesEnabled() {
        return ((BooleanParameter) getAlgorithmParameters()[1]).getBoolean();
    }

    /**
     * Returns <tt>true</tt> if this algorithm's animation uses colored edges to
     * visualize its result.
     * <p>
     * This implementation returns the value of the third parameter of the array
     * returned by <tt>getParameters</tt>.
     * 
     * @see #getAlgorithmParameters()
     * 
     * @return <tt>true</tt> if this algorithm's animation uses colored edges to
     *         visualize its result.
     */
    public boolean showTreeEdges() {
        return (Boolean) getAlgorithmParameters()[2].getValue();
    }

    /**
     * Specifies whether this algorithm's animation uses colored edges to
     * visualize its result.
     * <p>
     * This implementation sets the third parameter of the array returned by
     * <tt>getParameters</tt>.
     * 
     * @see #getAlgorithmParameters()
     * 
     * @param b
     *            <tt>true</tt> if this algorithm's animation uses colored edges
     *            to visualize its result; <tt>false</tt> otherwise.
     */
    public void setShowTreeEdges(boolean b) {
        ((BooleanParameter) getAlgorithmParameters()[2]).setValue(b);
    }

    /**
     * Returns the default weight for unlabelled edges.
     * <p>
     * This implementation returns the value of the fourth parameter of the
     * array returned by <tt>getParameters</tt>.
     * 
     * @see #getAlgorithmParameters()
     * 
     * @return the default weight for unlabelled edges.
     */
    public float getDefaultWeight() {
        return ((FloatParameter) getAlgorithmParameters()[3]).getValue();
    }

    /**
     * Sets the default weight for unlabelled edges.
     * <p>
     * This implementation sets the fourth parameter of the array returned by
     * <tt>getParameters</tt>
     * 
     * @see #getAlgorithmParameters()
     * 
     * @param value
     *            the default weight for unlabelled edges.
     */
    public void setDefaultWeight(float value) {
        ((FloatParameter) getAlgorithmParameters()[3]).setValue(value);
    }

    /**
     * Checks whether all preconditions of the current graph are satisfied.
     * <p>
     * This implementation checks whether this algorithm's graph is undirected
     * and connected. If not it throws <tt>PreconditionException</tt>.
     * 
     * @throws PreconditionException
     *             if this algorithm's graph is directed or not connected.
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException e = new PreconditionException();
        GraphAdapter graph = new GraphAdapter(super.graph);
        if (!graph.isUndirected()) {
            e.add("This graph contains at least one directed edge. "
                    + "Prim's algorithm works only on undirected graphs.");
        }
        if (!graph.isConnected()) {
            e.add("This graph is not connected. "
                    + "Prim's Algorithm works only on connected graphs.");
        }
        if (graph.isMultiGraph()) {
            e.add("This graph has multiple edges. "
                    + "Prims's Algorithm works only on "
                    + "graphs that permit exaclty one edge "
                    + "between two nodes.");
        }
        if (!e.isEmpty())
            throw e;
    }

    /**
     * Executes the whole algorithm.
     * <p>
     * This implementation simply returns if this algorithm uses its animation.
     * Otherwise it executes Prim's algorithm for computing the minimum spanning
     * tree in an undirected connected graph.
     */
    public void execute() {
        if (usesAnimation())
            return;
        graph.getListenerManager().transactionStarted(this);
        EdgeAdapterFactory ef = new EdgeAdapterFactory(getDefaultWeight(),
                showTreeEdges());
        NodeAdapterFactory nf = new NodeAdapterFactory(ef);
        GraphAdapter graph = new GraphAdapter(super.graph, ef, nf);
        HeapAdapter heap = new HeapAdapter(new ArrayHeap<Node, Float>());
        graph.clear();
        heap.clear();
        graph.init(heap);
        graph.startNode().setKey(0f);
        while (!heap.isEmpty()) {
            NodeAdapter n = heap.removePeek();
            n.select();
            for (NodeAdapter m : n.adjacentNodes()) {
                float weight = n.edgeTo(m).getWeight();
                if (!m.isSelected() && weight < m.getKey()) {
                    m.setKey(weight);
                    m.setParent(n);
                }
            }
            if (!n.isRoot()) {
                n.edgeTo(n.getParent()).select();
            }
        }
        graph.clean();
        this.graph.getListenerManager().transactionFinished(this);
    }

    /**
     * Returns <tt>true</tt> if this algorithm supports animation.
     * <p>
     * This implementation delegates to <tt>usesAnimation</tt>.
     * 
     * @see #getAnimation()
     * @see #usesAnimation()
     * 
     * @return <tt>true</tt> if this algorithm supports animation.
     */
    @Override
    public boolean supportsAnimation() {
        return usesAnimation();
    }

    /**
     * Returns an animation for this algorithm if it supports animation. An
     * algorithm supports animation if <tt>supportsAnimation</tt> returns
     * <tt>true</tt>. Throws <tt>UnsupportedOperationException</tt> otherwise.
     * <p>
     * This implementation first checks whether this algorithm supports
     * animation. If it doesn't it throws <tt>UnsupportedOperationException</tt>
     * . If it does, it creates a new instance of
     * <tt>PrimsAlgorithmAnimation</tt>. If
     * <tt>stepsToPreviousStatesEnabled</tt> returns <tt>true</tt> the returned
     * animation will support steps to previous states of this algorithm.
     * 
     * @see #supportsAnimation()
     * @see org.graffiti.plugins.algorithms.mst.animation.PrimsAlgorithmAnimation
     * @see #stepsToPreviousStatesEnabled()
     * 
     * @return an animation for this algorithm.
     * @throws UnsupportedOperationException
     *             if this algorithm does not support animation.
     */
    @Override
    public Animation getAnimation() {
        if (!supportsAnimation())
            throw new UnsupportedOperationException();
        EdgeAdapterFactory ef = new EdgeAdapterFactory(getDefaultWeight(),
                showTreeEdges());
        NodeAdapterFactory nf = new NodeAdapterFactory(ef);
        GraphAdapter graph = new GraphAdapter(super.graph, ef, nf);
        HeapAdapter heap = new HeapAdapter(new ArrayHeap<Node, Float>());
        return new PrimsAlgorithmAnimation(graph, heap,
                stepsToPreviousStatesEnabled());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
