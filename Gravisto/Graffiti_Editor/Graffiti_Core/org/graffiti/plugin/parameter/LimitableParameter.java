// =============================================================================
//
//   LimitableParameter.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: LimitableParameter.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.plugin.parameter;

/**
 * The value of a <code>LimitableParameter</code> can be limited by giving
 * maximum and minimum values.
 * 
 * @version $Revision: 5767 $
 */
public interface LimitableParameter<T extends Comparable<T>> extends
        SingleParameter<T> {

    /**
     * Returns the maximum value for this <code>LimitableParameter</code>.
     * 
     * @return the maximum value for this <code>LimitableParameter</code>.
     */
    public T getMax();

    /**
     * Returns the minimum value for this <code>LimitableParameter</code>.
     * 
     * @return the minimum value for this <code>LimitableParameter</code>.
     */
    public T getMin();

    /**
     * Returns the maximum value of the slider of this
     * <code>LimitableParameter</code>.
     * 
     * @return the maximum value of the slider of this
     *         <code>LimitableParameter</code>.
     */
    public T getSliderMax();

    /**
     * Returns the minimum value of the slider for this
     * <code>LimitableParameter</code>.
     * 
     * @return the minimum value of the slider for this
     *         <code>LimitableParameter</code>.
     */
    public T getSliderMin();

    /**
     * Returns <code>true</code> if the value is between the minimum and the
     * maximum, <code>false</code> otherwise.
     * 
     * @return <code>true</code> if the value is between the minimum and the
     *         maximum, <code>false</code> otherwise.
     */
    public boolean isValid();
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
