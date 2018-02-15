//==============================================================================
//
//   VECParameter.java
//
//   Copyright (c) 2003-2004 Paul Holleis
//
//==============================================================================
// $Id: VECParameter.java 491 2004-10-11 11:57:29Z holleis $

package quoggles.parameters;

import org.graffiti.plugin.editcomponent.ValueEditComponent;
import org.graffiti.plugin.parameter.AbstractSingleParameter;

/**
 * Parameter that contains a <code>ValueEditComponent</code> as value.
 *
 * @version $Revision: 491 $
 */
public class VECParameter
    extends AbstractSingleParameter
{
    //~ Instance fields ========================================================

    /** The value of this parameter. */
    private ValueEditComponent value = null;

    //~ Constructors ===========================================================

    /**
     * Constructs a new ValueEditComponent parameter.
     *
     * @param value the new ValueEditComponent. May be null.
     * @param name the name of the parameter.
     * @param description the description of the parameter.
     */
    public VECParameter(ValueEditComponent value, String name, String description)
    {
        super(value, name, description);
        this.value = value;
    }

    /**
     * Constructs a new ValueEditComponent parameter from the value of the
     * given string.
     *
     * @param value the new ValueEditComponent. May be null.
     */
    public VECParameter(String strValue)
    {
    	super(null, "", "automatically generated");
    	
    }


    //~ Methods ================================================================

    /**
     * 
     */
    public ValueEditComponent getVEC()
    {
        return value;
    }

    /**
     * Returns <code>true</code>, if the current value is valid.
     *
     * @return DOCUMENT ME!
     */
    public boolean isValid()
    {
        if(value == null)
        {
            return false;
        }

        return true;
    }

    /**
     * 
     * @exception IllegalArgumentException thrown if <code>value</code> is not
     *            of the correct type.
     */
    public void setValue(Object value)
    {
        try
        {
            this.value = (ValueEditComponent) value;
        }
        catch(Exception e)
        {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Writes the classname of the ValueEditComponent, the classname of the
     * Displayable encapsulated by the ValueEditComponent and the value of the
     * Displayable.
     * 
     * @return XML representation of the value of the paramter
     */
    public String toXMLString() {
//        return getStandardXML(value.toString());
        String vstr = value.getClass().getName() + " " +
			value.getDisplayable().getClass().getName() + " " +
			value.getDisplayable().getValue().toString();
    	
    	return getStandardXML(vstr);
    }

}

//------------------------------------------------------------------------------
//   end of file
//------------------------------------------------------------------------------
