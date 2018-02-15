package org.graffiti.plugins.algorithms.kandinsky;

import org.graffiti.graph.Edge;

/**
 * Creates a device for an edge, which contains two device arcs and a third one,
 * if there is a bend constraint. The total capacity of a device is 1. If the
 * sum of the capacities of the arcs of a device exceeds 1, the device is
 * oversaturated.
 */
public class Device {

    /** The first of the two/three arcs of the Kandinsky device. */
    private MCMFArc one;

    /** The second of the two/three arcs of the Kandinsky device. */
    private MCMFArc two;

    /** The third of the three arcs of the Kandinsky device. */
    private MCMFArc three;

    /** The capacity of the device. */
    private int cap;

    /** The edge of the device. */
    private Edge edge;

    /** True, if it is the direction of the edge. */
    private boolean direction;

    /** True, if the device has a third arc. */
    private boolean thirdArc;

    /**
     * Creates a device for an edge.
     * 
     * @param edge
     *            The edge for which the device is constructed.
     * @param direction
     *            True, if the device is in the direction of the edge.
     */
    public Device(Edge edge, boolean direction) {
        this.one = null;
        this.two = null;
        this.three = null;
        this.cap = 0;
        this.edge = edge;
        this.direction = direction;
        thirdArc = false;
    }

    /**
     * Returns the first <code>MCMFArc</code> of the device.
     * 
     * @return the the first <code>MCMFArc</code>.
     */
    protected MCMFArc getOne() {
        return one;
    }

    /**
     * Sets the first <code>MCMFArc</code> of the device.
     * 
     * @param one
     *            the first <code>MCMFArc</code> to set.
     */
    protected void setOne(MCMFArc one) {
        this.one = one;
    }

    /**
     * Returns the second <code>MCMFArc</code> of the device.
     * 
     * @return the second <code>MCMFArc</code>.
     */
    protected MCMFArc getTwo() {
        return two;
    }

    /**
     * Sets the second <code>MCMFArc</code> of the device.
     * 
     * @param two
     *            the second <code>MCMFArc</code> to set.
     */
    protected void setTwo(MCMFArc two) {
        this.two = two;
    }

    /**
     * Returns the third <code>MCMFArc</code> of the device.
     * 
     * @return the third <code>MCMFArc</code>.
     */
    protected MCMFArc getThree() {
        return three;
    }

    /**
     * Sets the third <code>MCMFArc</code> of the device.
     * 
     * @param three
     *            the third <code>MCMFArc</code> to set.
     */
    protected void setThree(MCMFArc three) {
        this.three = three;
        thirdArc = true;
    }

    /**
     * Returns the capacity of the device.
     * 
     * @return the capacity.
     */
    protected int getCap() {
        return cap;
    }

    /**
     * Sets the capacity of the device.
     * 
     * @param cap
     *            the capacity to set.
     */
    protected void setCap(int cap) {
        this.cap = cap;
    }

    /**
     * Returns true if the device is empty.
     * 
     * @return true if the device is empty.
     */
    protected boolean isEmpty() {
        // if ((one.getFlow() != 0) || (two.getFlow() != 0))
        if ((one.getFlow() != 0) || (two.getFlow() != 0)
                || ((three != null) && (three.getFlow() != 0)))
            return false;
        return true;
    }

    /**
     * Returns the edge for which the device is constructed.
     * 
     * @return the <code>Edge</code>.
     */
    protected Edge getEdge() {
        return edge;
    }

    /**
     * Returns the direction of the edge.
     * 
     * @return true, if the device is in direction of the edge.
     */
    protected boolean getDirection() {
        return direction;
    }

    /**
     * Returns true, if the device has a third arc.
     * 
     * @return true, if the device has a third arc.
     */
    protected boolean hasThirdArc() {
        return thirdArc;
    }

    /**
     * Returns true, if the device is over-saturated.
     * 
     * @return true, if the device is over-saturated.
     */
    protected boolean isOversaturated() {
        int completeFlow = one.getFlow() + two.getFlow();
        if (three != null) {
            completeFlow += three.getFlow();
        }
        if (completeFlow > cap)
            return true;
        return false;
    }
}
