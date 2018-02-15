// =============================================================================
//
//   AttributeParameter.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AttributeParameter.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.plugin.parameter;

import org.graffiti.attributes.Attribute;

/**
 * This class is used for <code>Parameters</code> that satisfy the
 * <code>org.graffiti.attributes.Attribte</code> interface.
 * 
 * @version $Revision: 5767 $
 * 
 * @see Attribute
 */
public class AttributeParameter extends AbstractSingleParameter<Attribute> {

    /**
     * 
     */
    private static final long serialVersionUID = -2816476572242654715L;

    /**
     * Constructs a new attribute parameter.
     * 
     * @param name
     *            the name of the attribute.
     * @param description
     *            the description of the attribute.
     */
    public AttributeParameter(String name, String description) {
        super(name, description);
    }

    /**
     * Returns the <code>Attribute</code> the <code>AttributeParameter</code>
     * represents.
     * 
     * @return the <code>Attribute</code> the <code>AttributeParameter</code>
     *         represents.
     */
    public Attribute getAttribute() {
        return getValue();
    }

    /**
     * Returns the path to the <code>Attribute</code> the
     * <code>AttributeParameter</code> represents.
     * 
     * @return the path to the <code>Attribute</code> the
     *         <code>AttributeParameter</code> represents.
     */
    public String getPath() {
        return getValue().getPath();
    }

    /**
     * Sets the value of the <code>AttributeParameter</code>.
     * 
     * @param value
     *            the new value of the <code>AttributeParameter</code>.
     */
    @Override
    public void setValue(Attribute value)

    {
        super.setValue(value);
    }

    /**
     * @see org.graffiti.plugin.parameter.Parameter#toXMLString()
     */
    @Override
    public String toXMLString() {
        return getStandardXML(getValue().toXMLString());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
