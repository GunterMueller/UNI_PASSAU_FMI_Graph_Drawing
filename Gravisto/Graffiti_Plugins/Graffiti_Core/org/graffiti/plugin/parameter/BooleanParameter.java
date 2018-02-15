// =============================================================================
//
//   BooleanParameter.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: BooleanParameter.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.plugin.parameter;

/**
 * Parameter that contains a <code>Boolean</code> value.
 * 
 * @version $Revision: 5767 $
 */
public class BooleanParameter extends AbstractSingleParameter<Boolean> {

    /**
     * 
     */
    private static final long serialVersionUID = 3783988704320403499L;

    /**
     * Constructs a new boolean parameter.
     * 
     * @param value
     *            the new Boolean value. May be null.
     * @param name
     *            the name of the parameter.
     * @param description
     *            the description of the parameter.
     */
    public BooleanParameter(Boolean value, String name, String description) {
        super(value, name, description);
    }

    /**
     * Returns the value of this parameter as a <code>Boolean</code>.
     * 
     * @return the value of this parameter as a <code>Boolean</code>.
     */
    public Boolean getBoolean() {
        return getValue();
    }

    /**
     * Returns <code>true</code>, if the current value is valid.
     * 
     * @return DOCUMENT ME!
     */
    public boolean isValid() {
        return getValue() != null;
    }

    /**
     * @see org.graffiti.plugin.parameter.Parameter#toXMLString()
     */
    @Override
    public String toXMLString() {
        return getStandardXML(getValue().booleanValue() ? "true" : "false");
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
