// =============================================================================
//
//   ColorAttribute.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ColorAttribute.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.graphics;

import java.awt.Color;
import java.util.Map;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.attributes.IntegerAttribute;

/**
 * Contains the color graphic attribute. (The value of opacity has no effect
 * when applied to the outline of a node since that would lead to problems with
 * overlapping filling and outline.)
 * 
 * @version $Revision: 5768 $
 */
public class ColorAttribute extends HashMapAttribute implements
        GraphicAttributeConstants {

    /** Contains value for blue color. */
    private IntegerAttribute blue;

    /** Contains value for green color. */
    private IntegerAttribute green;

    /** Contains value for opacity. */
    private IntegerAttribute opacity;

    /** Contains value for red color. */
    private IntegerAttribute red;

    /**
     * Constructs a new <code>ColorAttribute</code>.
     * 
     * @param id
     *            the id of the attribute.
     * @param c
     *            the color-value of the attribute.
     */
    public ColorAttribute(String id, Color c) {
        this(id, c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
    }

    /**
     * Constructs a new <code>ColorAttribute</code>.
     * 
     * @param id
     *            the id of the attribute.
     * @param c
     *            the color-value of the attribute.
     */
    public ColorAttribute(String id, ColorAttribute c) {
        this(id, c.getRed(), c.getGreen(), c.getBlue(), c.getOpacity());
    }

    /**
     * Constructs a new <code>ColorAttribute</code>.
     * 
     * @param id
     *            the id of the attribute.
     */
    public ColorAttribute(String id) {
        this(id, 255, 255, 255, 255);
    }

    /**
     * Constructs a new <code>ColorAttribute</code> and initialises with the
     * given values.
     * 
     * @param id
     *            the id of the attribute.
     * @param r
     *            the red-value of the attribute.
     * @param g
     *            the green-value of the attribute.
     * @param b
     *            the blue-value of the attribute.
     * @param t
     *            the opacity-value of the attribute.
     */
    public ColorAttribute(String id, int r, int g, int b, int t) {
        super(id);
        this.red = new IntegerAttribute(RED, r);
        this.green = new IntegerAttribute(GREEN, g);
        this.blue = new IntegerAttribute(BLUE, b);
        this.opacity = new IntegerAttribute(OPAC, t);
        add(this.red, false);
        add(this.green, false);
        add(this.blue, false);
        add(this.opacity, false);
    }

    /**
     * Sets the 'blue'-value.
     * 
     * @param b
     *            the 'blue'-value to be set.
     */
    public void setBlue(int b) {
        this.blue.setInteger(b);
    }

    /**
     * Returns the 'blue'-value of the encapsulated color.
     * 
     * @return the 'blue'-value of the encapsulated color.
     */
    public int getBlue() {
        return this.blue.getInteger();
    }

    /**
     * Sets the collection of attributes contained within this
     * <tt>CollectionAttribute</tt>. The color values are set, additional values
     * are simply added (that means that if there exists already a subattribute
     * with the same id, an exception will be thrown).
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
            if (attrId.equals(RED)) {
                setRed(((IntegerAttribute) attrs.get(RED)).getInteger());
            } else if (attrId.equals(GREEN)) {
                setGreen(((IntegerAttribute) attrs.get(GREEN)).getInteger());
            } else if (attrId.equals(BLUE)) {
                setBlue(((IntegerAttribute) attrs.get(BLUE)).getInteger());
            } else if (attrId.equals(OPAC)) {
                setOpacity(((IntegerAttribute) attrs.get(OPAC)).getInteger());
            } else {
                this.add((Attribute) attrs.get(attrId).copy());
            }
        }

    }

    /**
     * Comfort function to set a java.awt.Color.
     * 
     * @param c
     *            the java.awt.Color value.
     */
    public void setColor(java.awt.Color c) {
        this.red.setInteger(c.getRed());
        this.green.setInteger(c.getGreen());
        this.blue.setInteger(c.getBlue());
        this.opacity.setInteger(c.getAlpha());
    }

    /**
     * Comfort function to get a java.awt.Color.
     * 
     * @return the built java.awt.Color object.
     */
    public Color getColor() {
        return new Color(this.getRed(), this.getGreen(), this.getBlue(), this
                .getOpacity());
    }

    /**
     * Sets the 'green'-value.
     * 
     * @param g
     *            the 'green'-value to be set.
     */
    public void setGreen(int g) {
        this.green.setInteger(g);
    }

    /**
     * Returns the 'green'-value of the encapsulated color.
     * 
     * @return the 'green'-value of the encapsulated color.
     */
    public int getGreen() {
        return this.green.getInteger();
    }

    /**
     * Sets the 'opacity'-value.
     * 
     * @param t
     *            the 'opacity'-value to be set.
     */
    public void setOpacity(int t) {
        this.opacity.setInteger(t);
    }

    /**
     * Returns the 'opacity'-value of the encapsulated color.
     * 
     * @return the 'opacity'-value of the encapsulated color.
     */
    public int getOpacity() {
        return this.opacity.getInteger();
    }

    /**
     * Sets the 'red'-value.
     * 
     * @param r
     *            the 'red'-value to be set.
     */
    public void setRed(int r) {
        this.red.setInteger(r);
    }

    /**
     * Returns the 'red'-value of the encapsulated color.
     * 
     * @return the 'red'-value of the encapsulated color.
     */
    public int getRed() {
        return this.red.getInteger();
    }

    // /**
    // * Returns a java.awt.Color object with the same values.
    // *
    // * @return a java.awt.Color object with the same values.
    // */
    // public Object getValue()
    // {
    // return new Color(red.getInteger(), green.getInteger(),
    // blue.getInteger(), opacity.getInteger());
    // }

    /**
     * Returns a deep copy of this object.
     * 
     * @return A deep copy of this object.
     */
    @Override
    public Object copy() {
        ColorAttribute copied = new ColorAttribute(this.getId());
        copied.setRed(this.getRed());
        copied.setGreen(this.getGreen());
        copied.setBlue(this.getBlue());
        copied.setOpacity(this.getOpacity());

        return copied;
    }

    /*
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object attr) {
        if (!(attr instanceof ColorAttribute))
            return false;
        ColorAttribute cattr = (ColorAttribute) attr;
        return blue.equals(cattr.blue) && green.equals(cattr.green)
                && red.equals(cattr.red) && opacity.equals(cattr.opacity);
    }

    // /**
    // * Sets the value of this <code>Attribute</code> to the given value
    // without
    // * informing the <code>ListenerManager</code>.
    // *
    // * @param v the new value.
    // *
    // * @exception IllegalArgumentException if <code>v</code> is not of the
    // * apropriate type.
    // */
    // protected void doSetValue(Object v)
    // throws IllegalArgumentException
    // {
    // try
    // {
    // red.setInteger(((Color) v).getRed());
    // green.setInteger(((Color) v).getGreen());
    // blue.setInteger(((Color) v).getBlue());
    // opacity.setInteger(((Color) v).getAlpha());
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
