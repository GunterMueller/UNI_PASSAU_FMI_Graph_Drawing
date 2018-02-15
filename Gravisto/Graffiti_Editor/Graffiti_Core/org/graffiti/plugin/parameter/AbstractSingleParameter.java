// =============================================================================
//
//   AbstractSingleParameter.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractSingleParameter.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.plugin.parameter;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.graffiti.plugin.XMLHelper;

/**
 * Implements functions that are common in all SingleParameters.
 * 
 * @version $Revision: 5767 $
 */
public abstract class AbstractSingleParameter<T> implements SingleParameter<T> {

    /**
     * 
     */
    private static final long serialVersionUID = 4494761103667932516L;

    /** The image representing the parameter. */
    private BufferedImage image = null;

    /** The value of this parameter. */
    private T value;

    /** The description of the parameter. */
    private String description;

    /** The name of the parameter. */
    private String name;

    /** The parent for the dependency */
    private Parameter<?> dependencyParent;

    /** The value of the dependency */
    private Object dependencyValue;

    /** Stores whether the parameter is currently visible */
    private boolean visible;

    /**
     * Constructs a new abstract single parameter class.
     * 
     * @param val
     *            DOCUMENT ME!
     * @param name
     *            the name of the parameter.
     * @param description
     *            the description of the parameter.
     */
    public AbstractSingleParameter(T val, String name, String description) {
        this.name = name;
        this.description = description;
        value = val;
        dependencyParent = null;
        dependencyValue = null;
    }

    /**
     * Constructs a new abstract single parameter class.
     * 
     * @param name
     *            the name of the parameter.
     * @param description
     *            the description of the parameter.
     */
    public AbstractSingleParameter(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Sets the description.
     * 
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the description of the parameter.
     * 
     * @return the description of the parameter.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the image representing the parameter.
     * 
     * @return the image representing the parameter.
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Returns the name of the parameter.
     * 
     * @return the name of the parameter.
     */
    public String getName() {
        return name;
    }

    /**
     * @see org.graffiti.plugin.Displayable#setValue(java.lang.Object)
     */
    public void setValue(T val) throws IllegalArgumentException {
        value = val;
    }

    /**
     * @see org.graffiti.plugin.Displayable#getValue()
     */
    public T getValue() {
        return value;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (getValue() == null)
            return "Parameter (" + name + "), value: null";
        else
            return "Parameter (" + name + "), value: " + getValue().toString();
    }

    /**
     * @see org.graffiti.plugin.parameter.Parameter#toXMLString()
     */
    public String toXMLString() {
        String valStr = (value == null) ? "null" : value.toString();

        return "<parameter classname=\\\"" + getClass().getName() + "\\\">"
                + XMLHelper.getDelimiter() + XMLHelper.spc(2)
                + "<value><![CDATA[" + valStr + "]]>"
                + XMLHelper.getDelimiter() + XMLHelper.spc(2) + "</value>"
                + XMLHelper.getDelimiter() + "</parameter>";
    }

    /**
     * Embeds the given String into an XML String. It includes the classname of
     * the parameter and a "value" element that gets the given String
     * <code>valueString</code> as content.
     * 
     * @param valueString
     * 
     * @return DOCUMENT ME!
     */
    protected String getStandardXML(String valueString) {
        return "<parameter classname=\\\"" + getClass().getName()
                + "\\\" name=\\\"" + getName() + "\\\" description=\\\""
                + getDescription() + "\\\">" + XMLHelper.getDelimiter()
                + XMLHelper.spc(2) + "<value>" + XMLHelper.getDelimiter()
                + XMLHelper.spc(4) + valueString + XMLHelper.getDelimiter()
                + XMLHelper.spc(2) + "</value>" + XMLHelper.getDelimiter()
                + "</parameter>";
    }

    @SuppressWarnings("unchecked")
    public Parameter<T> copy() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos
                    .toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object deepCopy = ois.readObject();
            return (Parameter<T>) deepCopy;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean canCopy() {
        return true;
    }

    @SuppressWarnings("unchecked")
    public void setObjectValue(Object value) {
        setValue((T) value);
    }

    public void setDependency(Parameter<?> parent, Object value) {
        dependencyParent = parent;
        dependencyValue = value;
    }

    public Parameter<?> getDependencyParent() {
        return dependencyParent;
    }

    public Object getDependencyValue() {
        return dependencyValue;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
