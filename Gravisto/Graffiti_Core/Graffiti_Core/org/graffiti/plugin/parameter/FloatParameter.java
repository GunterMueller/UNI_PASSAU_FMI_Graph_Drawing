// =============================================================================
//
//   FloatParameter.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: FloatParameter.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.plugin.parameter;

/**
 * Parameter that contains a float value.
 * 
 * @version $Revision: 5767 $
 */
public class FloatParameter extends AbstractLimitableParameter<Float> {

    /**
     * 
     */
    private static final long serialVersionUID = -2739898919259872439L;

    /**
     * Constructs a new float parameter.
     * 
     * @param name
     *            the name of the parameter.
     * @param description
     *            the description of the parameter.
     */
    public FloatParameter(String name, String description) {
        super(null, name, description);
    }

    public FloatParameter(Float value, String name, String description) {
        super(value, name, description);
    }

    /**
     * Constructs a new float parameter.
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
    public FloatParameter(Float val, String name, String description,
            Float sliderMin, Float sliderMax, Float min, Float max) {
        super(val, name, description, sliderMin, sliderMax, min, max);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
