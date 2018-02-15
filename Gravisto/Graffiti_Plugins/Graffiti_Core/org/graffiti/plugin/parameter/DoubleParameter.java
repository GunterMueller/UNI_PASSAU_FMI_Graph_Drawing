// =============================================================================
//
//   DoubleParameter.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DoubleParameter.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.plugin.parameter;

/**
 * Represents a double parameter.
 * 
 * @version $Revision: 5767 $
 */
public class DoubleParameter extends AbstractLimitableParameter<Double> {

    /**
     * 
     */
    private static final long serialVersionUID = 4305763389008064301L;

    /**
     * Constructs a new double parameter.
     * 
     * @param name
     *            the name of the parameter.
     * @param description
     *            the description of the parameter.
     */
    public DoubleParameter(String name, String description) {
        super(null, name, description);
    }

    /**
     * Constructs a new double parameter.
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
    public DoubleParameter(String name, String description, Double sliderMin,
            Double sliderMax) {
        super(name, description, sliderMin, sliderMax);
    }

    /**
     * Constructs a new double parameter.
     * 
     * @param val
     *            the value of the parameter
     * @param name
     *            the name of the parameter.
     * @param description
     *            the description of the parameter.
     */
    public DoubleParameter(Double val, String name, String description) {
        super(val, name, description);
    }

    /**
     * Constructs a new double parameter.
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
     */
    public DoubleParameter(Double val, String name, String description,
            Double sliderMin, Double sliderMax) {
        super(val, name, description, sliderMin, sliderMax);
    }

    /**
     * Constructs a new double parameter.
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
    public DoubleParameter(Double val, String name, String description,
            Double sliderMin, Double sliderMax, Double min, Double max) {
        super(val, name, description, sliderMin, sliderMax, min, max);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param val
     *            DOCUMENT ME!
     */
    public void setDouble(Double val) {
        setValue(val);
    }

    /**
     * Returns the value of this parameter as a <code>Double</code>.
     * 
     * @return the value of this parameter as a <code>Double</code>.
     */
    public Double getDouble() {
        return getValue();
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
