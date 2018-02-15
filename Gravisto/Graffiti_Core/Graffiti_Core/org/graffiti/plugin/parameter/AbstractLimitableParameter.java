// =============================================================================
//
//   AbstractLimitableParameter.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractLimitableParameter.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.plugin.parameter;

/**
 * This abstract class provides an implementation for the <code>isValid</code>
 * method, using the <code>compareTo</code> method of the
 * <code>Comparable</code> interface.
 * 
 * @version $Revision: 5767 $
 */
public abstract class AbstractLimitableParameter<T extends Comparable<T>>
        extends AbstractSingleParameter<T> implements LimitableParameter<T> {

    /**
     * 
     */
    private static final long serialVersionUID = -3074284113269731120L;

    /** The parameter's maximum. */
    protected T max;

    /** The parameter's minimum. */
    protected T min;

    /** The parameter's slider maximum. */
    protected T sliderMax;

    /** The parameter's slider minimum. */
    protected T sliderMin;

    /**
     * Constructs a new abstract limitable parameter.
     * 
     * @param value
     *            the value of the parameter.
     * @param name
     *            the name of the parameter.
     * @param description
     *            the description of the parameter.
     */
    public AbstractLimitableParameter(T value, String name, String description) {
        this(value, name, description, null, null, null, null);
    }

    /**
     * Constructs a new abstract limitable parameter.
     * 
     * @param name
     *            the name of the parameter.
     * @param description
     *            the description of the parameter.
     * @param sliderMin
     *            The parameter's slider minimum.
     * @param sliderMax
     *            The parameter's slider maximum.
     */
    public AbstractLimitableParameter(String name, String description,
            T sliderMin, T sliderMax) {
        this(null, name, description, sliderMin, sliderMax, null, null);
    }

    /**
     * Constructs a new abstract limitable parameter.
     * 
     * @param name
     *            the name of the parameter.
     * @param description
     *            the description of the parameter.
     * @param sliderMin
     *            The parameter's slider minimum.
     * @param sliderMax
     *            The parameter's slider maximum.
     * @param min
     *            The parameter's minimum.
     * @param max
     *            The parameter's maximum.
     */
    public AbstractLimitableParameter(String name, String description,
            T sliderMin, T sliderMax, T min, T max) {
        this(null, name, description, sliderMin, sliderMax, min, max);
    }

    /**
     * Constructs a new abstract limitable parameter.
     * 
     * @param value
     *            the value of the parameter.
     * @param name
     *            the name of the parameter.
     * @param description
     *            the description of the parameter.
     * @param sliderMin
     *            The parameter's slider minimum.
     * @param sliderMax
     *            The parameter's slider maximum.
     */
    public AbstractLimitableParameter(T value, String name, String description,
            T sliderMin, T sliderMax) {
        this(value, name, description, sliderMin, sliderMax, null, null);
    }

    /**
     * Constructs a new abstract limitable parameter.
     * 
     * @param value
     *            the value of the parameter.
     * @param name
     *            the name of the parameter.
     * @param description
     *            the description of the parameter.
     * @param sliderMin
     *            The parameter's slider minimum.
     * @param sliderMax
     *            The parameter's slider maximum.
     * @param min
     *            The parameter's minimum.
     * @param max
     *            The parameter's maximum.
     */
    public AbstractLimitableParameter(T value, String name, String description,
            T sliderMin, T sliderMax, T min, T max) {
        super(name, description);
        if (value != null) {
            if (min != null) {
                if (value.compareTo(min) < 0) {
                    value = min;
                }
            }
            if (max != null) {
                if (value.compareTo(max) > 0) {
                    value = max;
                }
            }
        }
        setValue(value);
        this.sliderMin = sliderMin;
        this.sliderMax = sliderMax;
        this.min = min;
        this.max = max;
    }

    /**
     * Returns the maximum of the intervall.
     * 
     * @return the maximum of the intervall.
     */
    public T getMax() {
        return max;
    }

    /**
     * Returns the minimum of the intervall.
     * 
     * @return the minimum of the intervall.
     */
    public T getMin() {
        return min;
    }

    /**
     * Returns the maximum of the intervall.
     * 
     * @return the maximum of the intervall.
     */
    public T getSliderMax() {
        return sliderMax;
    }

    /**
     * Returns the minimum of the intervall.
     * 
     * @return the minimum of the intervall.
     */
    public T getSliderMin() {
        return sliderMin;
    }

    /**
     * Returns <code>true</code> if the value is between the minimum and the
     * maximum, <code>false</code> otherwise.
     * 
     * @return <code>true</code> if the value is between the minimum and the
     *         maximum, <code>false</code> otherwise.
     */
    public boolean isValid() {
        if (getValue() == null)
            return false;

        if ((max != null) && (max.compareTo(getValue()) < 0))
            return false;

        if ((min != null) && (min.compareTo(getValue()) > 0))
            return false;

        return true;
    }

    /**
     * @see org.graffiti.plugin.parameter.Parameter#toXMLString()
     */
    @Override
    public String toXMLString() {
        return getStandardXML(getValue().toString());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
