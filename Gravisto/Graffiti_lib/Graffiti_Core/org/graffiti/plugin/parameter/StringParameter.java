// =============================================================================
//
//   StringParameter.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: StringParameter.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.plugin.parameter;

/**
 * Parameter that contains an <code>Integer</code> value.
 * 
 * @version $Revision: 5767 $
 */
public class StringParameter extends AbstractSingleParameter<String> {

    /**
     * 
     */
    private static final long serialVersionUID = -5925715130402487280L;

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
    public StringParameter(String value, String name, String description) {
        super(value, name, description);
    }

    /**
     * Returns the value of this parameter as an <code>String</code>.
     * 
     * @return the value of this parameter as an <code>String</code>.
     */
    public String getString() {
        return getValue();
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
