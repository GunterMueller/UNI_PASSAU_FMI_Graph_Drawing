// =============================================================================
//
//   ProbabilityParameter.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ProbabilityParameter.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.plugin.parameter;

/**
 * Represents a probability parameter. This parameter's value is between 0.0 and
 * 1.0.
 */
public class ProbabilityParameter extends AbstractLimitableParameter<Double> {

    /**
     * 
     */
    private static final long serialVersionUID = 9217142625135795719L;

    /**
     * Creates a ProbabilityParameter.
     * 
     * @param name
     *            The parameter's name.
     * @param description
     *            The parameter's description.
     */
    public ProbabilityParameter(String name, String description) {
        super(name, description, new Double(0.0), new Double(1.0), new Double(
                0.0), new Double(1.0));
    }

    /**
     * Creates a ProbabilityParameter.
     * 
     * @param val
     *            The parameter's value.
     * @param name
     *            The parameter's name.
     * @param description
     *            The parameter's description.
     */
    public ProbabilityParameter(double val, String name, String description) {
        super(val, name, description, new Double(0.0), new Double(1.0),
                new Double(0.0), new Double(1.0));
    }

    /**
     * Sets the value of this parameter as a <code>Double</code>.
     * 
     * @param val
     *            The value to set in this parameter.
     */
    public void setProbability(Double val) {
        setValue(val);
    }

    /**
     * Returns the value of this parameter as a <code>Double</code>.
     * 
     * @return the value of this parameter as a <code>Double</code>.
     */
    public Double getProbability() {
        return getValue();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
