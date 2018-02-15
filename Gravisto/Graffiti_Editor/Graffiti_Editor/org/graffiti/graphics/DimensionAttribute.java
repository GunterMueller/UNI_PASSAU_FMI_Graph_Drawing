// =============================================================================
//
//   DimensionAttribute.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DimensionAttribute.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.graphics;

import java.awt.Dimension;
import java.util.Map;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.attributes.HashMapAttribute;

/**
 * Contains the graphic attribute dimension, that represents the minimal
 * surrounding rectangle
 * 
 * @author breu
 * @version $Revision: 5768 $
 */
public class DimensionAttribute extends HashMapAttribute implements
        GraphicAttributeConstants {

    /** Contains height of minimal surrounding rectangle for a node */
    private DoubleAttribute height;

    /** Contains width of minimal surrounding rectangle for a node */
    private DoubleAttribute width;

    /**
     * Constructor for Dimension.
     * 
     * @param id
     *            the id of the attribute.
     */
    public DimensionAttribute(String id) {
        this(id, 0, 0);
    }

    /**
     * Constructor for Dimension.
     * 
     * @param id
     *            the id of the attribute.
     * @param d
     *            the dimension-value of the attribute.
     */
    public DimensionAttribute(String id, java.awt.Dimension d) {
        this(id, d.getWidth(), d.getHeight());
    }

    /**
     * Constructor for Dimension.
     * 
     * @param id
     *            the id of the attribute.
     * @param h
     *            the height-value of the attribute.
     * @param w
     *            the width-value of the attribute.
     */
    public DimensionAttribute(String id, double h, double w) {
        super(id);
        height = new DoubleAttribute(HEIGHT, h);
        width = new DoubleAttribute(WIDTH, w);
        add(this.height, false);
        add(this.width, false);
    }

    /**
     * Constructor for Dimension.
     * 
     * @param id
     *            the id of the attribute.
     * @param h
     *            the height-value of the attribute.
     * @param w
     *            the width-value of the attribute.
     */
    public DimensionAttribute(String id, DoubleAttribute h, DoubleAttribute w) {
        this(id, h.getDouble(), w.getDouble());
    }

    /**
     * Sets the collection of attributes contained within this
     * <tt>CollectionAttribute</tt>. The dimension values are set, additional
     * values are simply added (that means that if there exists already a
     * subattribute with the same id, an exception will be thrown).
     * 
     * @param attrs
     *            the map that contains all attributes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    @Override
    public void setCollection(Map<String, Attribute> attrs) {

        for (String attrId : attrs.keySet()) {
            if (attrId.equals(WIDTH)) {
                setWidth(((DoubleAttribute) attrs.get(WIDTH)).getDouble());
            } else if (attrId.equals(HEIGHT)) {
                setHeight(((DoubleAttribute) attrs.get(HEIGHT)).getDouble());
            } else {
                this.add((Attribute) attrs.get(attrId).copy());
            }
        }

    }

    /**
     * Sets the <code>Dimension</code> of the attribute.
     * 
     * @param d
     *            the dimension-value of the attribute.
     */
    public void setDimension(Dimension d) {
        this.height.setDouble(d.getHeight());
        this.width.setDouble(d.getWidth());
    }

    /**
     * Sets the <code>Dimension</code> of the attribute.
     * 
     * @param w
     *            the dimension-value of the attribute.
     * @param h
     *            DOCUMENT ME!
     */
    public void setDimension(double w, double h) {
        this.height.setDouble(h);
        this.width.setDouble(w);
    }

    /**
     * Returns the <code>Dimension</code> of the attribute.
     * 
     * @return the <code>Dimension</code> of the attribute.
     */
    public Dimension getDimension() {
        return new Dimension((int) this.width.getDouble(), (int) this.height
                .getDouble());
    }

    /**
     * Sets the 'height'-value.
     * 
     * @param h
     *            the 'height'-value to be set.
     */
    public void setHeight(double h) {
        this.height.setDouble(h);
    }

    /**
     * Returns the 'height'-value of the encapsulated dimension.
     * 
     * @return the 'height'-value of the encapsulated dimension.
     */
    public double getHeight() {
        return this.height.getDouble();
    }

    /**
     * Sets the 'width'-value.
     * 
     * @param w
     *            the 'width'-value to be set.
     */
    public void setWidth(double w) {
        this.width.setDouble(w);
    }

    /**
     * Returns the 'width'-value of the encapsulated dimension.
     * 
     * @return the 'width'-value of the encapsulated dimension.
     */
    public double getWidth() {
        return this.width.getDouble();
    }

    /**
     * Returns a deep copy of this object.
     * 
     * @return A deep copy of this object.
     */
    @Override
    public Object copy() {
        DimensionAttribute copied = new DimensionAttribute(this.getId());
        copied.setHeight(this.getHeight());
        copied.setWidth(this.getWidth());

        return copied;
    }

    // /**
    // * Sets the value of this <code>Attribute</code> to the given value
    // without
    // * informing the <code>ListenerManager</code>.
    // *
    // * @param v the new value.
    // *
    // * @exception IllegalArgumentException if <code>v</code> is not of type
    // * <code>java.awt.Dimension</code>.
    // */
    // protected void doSetValue(Object v)
    // throws IllegalArgumentException
    // {
    // try
    // {
    // width.setDouble(((Dimension) v).getWidth());
    // height.setDouble(((Dimension) v).getHeight());
    // }
    // catch(ClassCastException cce)
    // {
    // throw new IllegalArgumentException("Invalid value type.");
    // }
    // }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
