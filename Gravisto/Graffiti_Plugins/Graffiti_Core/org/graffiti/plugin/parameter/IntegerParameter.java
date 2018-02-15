// =============================================================================
//
//   IntegerParameter.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: IntegerParameter.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.plugin.parameter;

/**
 * Parameter that contains an <code>Integer</code> value.
 * 
 * @version $Revision: 5767 $
 */
public class IntegerParameter extends AbstractLimitableParameter<Integer> {

    /**
     * 
     */
    private static final long serialVersionUID = -5064084102337398196L;

    /**
     * Constructs a new integer parameter.
     * 
     * @param value
     *            the new integer value. May be null.
     * @param min
     *            the minimum value.
     * @param max
     *            the maximum value.
     * @param name
     *            the name of the parameter.
     * @param description
     *            the description of the parameter.
     */
    public IntegerParameter(Integer value, Integer min, Integer max,
            String name, String description) {
        super(value, name, description, min, max);
    }

    /**
     * Constructs a new integer parameter.
     * 
     * @param value
     *            the new integer value. May be null.
     * @param name
     *            the name of the parameter.
     * @param description
     *            the description of the parameter.
     */
    public IntegerParameter(Integer value, String name, String description) {
        super(value, name, description);
    }

    /**
     * Constructs a new integer parameter.
     * 
     * @param val
     *            the value of the parameter
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
    public IntegerParameter(Integer val, String name, String description,
            Integer sliderMin, Integer sliderMax, Integer min, Integer max) {
        super(val, name, description, sliderMin, sliderMax, min, max);
    }

    /**
     * Returns the value of this parameter as an <code>Integer</code>.
     * 
     * @return the value of this parameter as an <code>Integer</code>.
     */
    public Integer getInteger() {
        return getValue();
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
