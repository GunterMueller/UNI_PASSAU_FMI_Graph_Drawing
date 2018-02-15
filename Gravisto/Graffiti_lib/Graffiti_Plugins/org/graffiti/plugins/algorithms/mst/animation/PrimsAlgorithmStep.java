package org.graffiti.plugins.algorithms.mst.animation;

import org.graffiti.plugin.algorithm.animation.Step;
import org.graffiti.plugins.algorithms.mst.adapters.EdgeAdapter;
import org.graffiti.plugins.algorithms.mst.adapters.HeapAdapter;
import org.graffiti.plugins.algorithms.mst.adapters.NodeAdapter;

/**
 * One step of Prim's algorithm; selects the next edge of a minimum spanning
 * tree.
 * 
 * @author Harald
 * @version $Revision$ $Date$
 */
public class PrimsAlgorithmStep implements Step {

    /**
     * The heap used by this step of Prim's algorithm.
     */
    private HeapAdapter heap = null;

    /**
     * The last edge of the minimum spanning tree that was selected by this step
     * of Prim's algorithm.
     */
    private EdgeAdapter edge = null;

    /**
     * <tt>true</tt> if this is a singleton step; i.e. <tt>next</tt> will return
     * this instance instead of creating a new <tt>PrimsAlgorithmStep</tt>
     * instance.
     */
    private boolean isSingleton = false;

    /**
     * Creates a new step for Prim's algorithm with the specified heap proxy. If
     * <tt>isSingleton</tt> is <tt>true</tt> this instance will return itself
     * upon a call to <tt>next</tt> instead of creating a new instance of
     * <tt>PrimsAlgorithmStep</tt>.
     * 
     * @see org.graffiti.plugins.algorithms.mst.adapters.HeapAdapter
     * 
     * @param h
     *            the <tt>HeapProxy</tt> to use by this step of Prim's
     *            algorithm.
     * @param isSingleton
     *            <tt>true</tt> if this instance is a singleton step.
     */
    public PrimsAlgorithmStep(HeapAdapter h, boolean isSingleton) {
        heap = h;
        this.isSingleton = isSingleton;
    }

    /**
     * Returns <tt>true</tt> if this step has a successor.
     * <p>
     * This implementation returns <tt>true</tt> if the heap it uses is not
     * empty.
     * 
     * @return <tt>true</tt> if this step has a successor.
     */
    public boolean hasNext() {
        return !heap.isEmpty();
    }

    /**
     * Performs this step and returns its successor.
     * <p>
     * This implementation first checks whether this step has a successor. If
     * not it throws <tt>IllegalStateException</tt>. Otherwise it processes the
     * peek node in this step's heap as specified by Prim's algorithm and
     * selects the edge from the peek node to its parent. Finally if this step
     * is a singleton step, it returns itself; otherwise it creates a new
     * instance of <tt>PrimsAlgorithmStep</tt> with identical parameters and
     * returns it.
     * 
     * @see org.graffiti.plugins.algorithms.mst.adapters.HeapAdapter#removePeek()
     * @see org.graffiti.plugins.algorithms.mst.adapters.NodeAdapter
     * @see org.graffiti.plugins.algorithms.mst.adapters.NodeAdapter#edgeTo(NodeAdapter)
     * @see org.graffiti.plugins.algorithms.mst.adapters.NodeAdapter#getParent()
     * @see org.graffiti.plugins.algorithms.mst.adapters.EdgeAdapter#select()
     * 
     * @return the successor of this step.
     * @throws IllegalStateException
     *             if this step has no successor.
     */
    public Step next() {
        if (!hasNext())
            throw new IllegalStateException();
        NodeAdapter n = heap.removePeek();
        n.select();
        edge = n.edgeTo(n.getParent());
        edge.select();
        for (NodeAdapter m : n.adjacentNodes()) {
            float weight = n.edgeTo(m).getWeight();
            if (!m.isSelected() && weight < m.getKey()) {
                m.setKey(weight);
                m.setParent(n);
            }
        }
        return isSingleton ? this : new PrimsAlgorithmStep(heap, isSingleton);
    }

    /**
     * Redoes the effects of this step after it has been undone.
     * <p>
     * This implementation first checks whether this step is a singleton step or
     * the last edge
     * 
     * @throws IllegalStateException
     *             if this step has not been undone.
     * @throws UnsupportedOperationException
     *             if this step is a singleton step.
     */
    public void redo() {
        edge.select();
    }

    /**
     * Undoes the effects of this step after it has been performed or redone.
     * 
     * @throws IllegalStateException
     *             if this step has not been performed or redone yet.
     * @throws UnsupportedOperationException
     *             if this step is a singleton step.
     */
    public void undo() {
        edge.unselect();
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
