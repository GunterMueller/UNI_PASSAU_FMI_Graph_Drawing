// =============================================================================
//
//   ImageAttribute.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ImageAttribute.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.graphics;

import java.awt.image.BufferedImage;
import java.util.Map;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.BooleanAttribute;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.attributes.IntegerAttribute;
import org.graffiti.attributes.StringAttribute;

/**
 * Contains the graphic attribute image.
 * 
 * @author breu
 * @version $Revision: 5768 $
 */
public class ImageAttribute extends HashMapAttribute implements
        GraphicAttributeConstants {

    /** Contains the image. */
    private RenderedImageAttribute image;

    /**
     * Indicates whether the image has to maximize to the size of surrounding
     * rectangle
     */
    private BooleanAttribute maximize;

    /**
     * Indicates whether the image has to be tiled if it is smaller than the
     * surrounding rectangle
     */
    private BooleanAttribute tiled;

    /**
     * A reference to the image that is not specified by <code>java.awt.Image
     * </code>
     */
    private StringAttribute reference;

    /**
     * Constructor for Image.
     * 
     * @param id
     *            the id of the attribute.
     */
    public ImageAttribute(String id) {
        this(id, false, false, new RenderedImageAttribute(IMAGE), "");
    }

    /**
     * Constructor for Image.
     * 
     * @param id
     *            the id of the attribute.
     * @param t
     *            the tiled-value of the attribute.
     * @param m
     *            the maximize-value of the attribute.
     * @param i
     *            the image-value of the attribute.
     * @param r
     *            the reference-value of the attribute.
     */
    public ImageAttribute(String id, boolean t, boolean m,
            RenderedImageAttribute i, String r) {
        this(id, t, m, i.getImage(), r);
    }

    /**
     * Constructor for Image.
     * 
     * @param id
     *            the id of the attribute.
     * @param t
     *            the tiled-value of the attribute.
     * @param m
     *            the maximize-value of the attribute.
     * @param i
     *            the image-value of the attribute.
     * @param r
     *            the reference-value of the attribute.
     */
    public ImageAttribute(String id, BooleanAttribute t, BooleanAttribute m,
            RenderedImageAttribute i, StringAttribute r) {
        this(id, t.getBoolean(), m.getBoolean(), i.getImage(), r.getString());
    }

    /**
     * Constructor for Image.
     * 
     * @param id
     *            the id of the attribute.
     * @param t
     *            the tiled-value of the attribute.
     * @param m
     *            the maximize-value of the attribute.
     * @param i
     *            the java.awt.image-value of the attribute.
     * @param r
     *            the reference-value of the attribute.
     */
    public ImageAttribute(String id, boolean t, boolean m, BufferedImage i,
            String r) {
        super(id);
        this.tiled = new BooleanAttribute(TILED, t);
        this.maximize = new BooleanAttribute(MAXIMIZE, m);

        this.image = new RenderedImageAttribute(IMAGE, i);
        this.reference = new StringAttribute(REF, r);
        add(this.tiled, false);
        add(this.maximize, false);
        add(this.image, false);
        add(this.reference, false);
    }

    /**
     * Sets the collection of attributes contained within this
     * <tt>CollectionAttribute</tt>
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
            if (attrId.equals(TILED)) {
                setTiled(((BooleanAttribute) attrs.get(TILED)).getBoolean());
            } else if (attrId.equals(MAXIMIZE)) {
                setMaximize(((BooleanAttribute) attrs.get(MAXIMIZE))
                        .getBoolean());
            } else if (attrId.equals(IMAGE)) {
                setImage((RenderedImageAttribute) attrs.get(IMAGE));
            } else if (attrId.equals(REF)) {
                setReference(((StringAttribute) attrs.get(REF)).getString());
            } else {
                this.add((Attribute) attrs.get(attrId).copy());
            }
        }
    }

    /**
     * Sets the 'image'-value.
     * 
     * @param i
     *            the 'image'-value to be set.
     */
    public void setImage(RenderedImageAttribute i) {
        this.image.setImage(i.getImage());
    }

    /**
     * Returns the 'image'-value of the encapsulated image.
     * 
     * @return the 'image'-value of the encapsulated image.
     */
    public RenderedImageAttribute getImage() {
        return this.image;
    }

    /**
     * Sets the 'maximize'-value.
     * 
     * @param m
     *            the 'maximize'-value to be set.
     */
    public void setMaximize(boolean m) {
        this.maximize.setBoolean(m);
    }

    /**
     * Returns the 'maximize'-value of the encapsulated image.
     * 
     * @return the 'maximize'-value of the encapsulated image.
     */
    public boolean getMaximize() {
        return this.maximize.getBoolean();
    }

    /**
     * Sets the 'reference'-value.
     * 
     * @param r
     *            the 'reference'-value to be set.
     */
    public void setReference(String r) {
        this.reference.setString(r);
    }

    /**
     * Returns the 'reference'-value of the encapsulated image.
     * 
     * @return the 'reference'-value of the encapsulated image.
     */
    public String getReference() {
        return this.reference.getString();
    }

    /**
     * Sets the 'tiled'-value.
     * 
     * @param t
     *            the 'tiled'-value to be set.
     */
    public void setTiled(boolean t) {
        this.tiled.setBoolean(t);
    }

    /**
     * Returns the 'tiled'-value of the encapsulated image.
     * 
     * @return the 'tiled'-value of the encapsulated image.
     */
    public boolean getTiled() {
        return this.tiled.getBoolean();
    }

    /**
     * Returns a deep copy of this object.
     * 
     * @return A deep copy of this object.
     */
    @Override
    public Object copy() {
        ImageAttribute copied = new ImageAttribute(this.getId());
        copied.setTiled(this.getTiled());
        copied.setMaximize(this.getMaximize());
        copied.setReference(this.getReference());
        copied.setImage((RenderedImageAttribute) this.getImage().copy());
        return copied;
    }

    /**
     * Sets the value of this <code>Attribute</code> to the given value without
     * informing the <code>ListenerManager</code>.
     * 
     * @param v
     *            the new value.
     * 
     * @exception IllegalArgumentException
     *                if <code>v</code> is not of the apropriate type.
     */
    @Override
    protected void doSetValue(Object v) throws IllegalArgumentException {
        ImageAttribute value;
        // try
        if (v instanceof ImageAttribute) {
            value = (ImageAttribute) v;
            setTiled(value.getTiled());
            setMaximize(value.getMaximize());
            setImage(value.getImage());
            setReference(value.getReference());

        }
        // catch (ClassCastException cce)
        else {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Attribute> map = (Map<String, Attribute>) v;
                Attribute tiledAttr = map.get(TILED);
                if (tiledAttr instanceof IntegerAttribute) {
                    tiledAttr = new BooleanAttribute(
                            tiledAttr.getId(),
                            ((IntegerAttribute) tiledAttr).getInteger() == 0 ? false
                                    : true);
                }
                Attribute maxAttr = map.get(MAXIMIZE);
                if (maxAttr instanceof IntegerAttribute) {
                    maxAttr = new BooleanAttribute(
                            maxAttr.getId(),
                            ((IntegerAttribute) maxAttr).getInteger() == 0 ? false
                                    : true);
                }

                setTiled(((BooleanAttribute) tiledAttr).getBoolean());
                setMaximize(((BooleanAttribute) maxAttr).getBoolean());
                setImage((RenderedImageAttribute) map.get(IMAGE));
                setReference(((StringAttribute) map.get(REF)).getString());
            } catch (ClassCastException cce2) {
                throw new IllegalArgumentException("Invalid value type.");
            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
