// =============================================================================
//
//   RenderedImageAttribute.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: RenderedImageAttribute.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.graphics;

import java.awt.image.BufferedImage;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.CompositeAttribute;

/**
 * Contains the rendered image graphic attribute.
 * 
 * @author breu
 * @version $Revision: 5768 $
 */
public class RenderedImageAttribute extends CompositeAttribute {

    /** Contains the value of this <code>RenderedImageAttribute</code>. */
    private BufferedImage image;

    /**
     * Constructor for RenderedImageAttribute.
     * 
     * @param id
     *            the id of the attribute.
     * @param value
     *            the value of the attribute.
     */
    public RenderedImageAttribute(String id, BufferedImage value) {
        super(id);
        this.image = value;
    }

    /**
     * Standard constructor. Creates an new image with size 0 of type int-argb.
     * 
     * @param id
     *            the id of the attribute.
     */
    public RenderedImageAttribute(String id) {
        super(id);
        image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * @see org.graffiti.attributes.CompositeAttribute#setAttribute(String,
     *      Attribute)
     */
    @Override
    public void setAttribute(String id, Attribute att)
            throws AttributeNotFoundException, IllegalArgumentException {
    }

    /**
     * @see org.graffiti.attributes.CompositeAttribute#getAttribute(String)
     */
    @Override
    public Attribute getAttribute(String id) throws AttributeNotFoundException {
        throw new RuntimeException("still to implement!");
    }

    /**
     * @see org.graffiti.attributes.CompositeAttribute#getAttributes()
     */
    @Override
    public CollectionAttribute getAttributes() {
        throw new RuntimeException("still to implement!");
    }

    /**
     * @see org.graffiti.attributes.Attribute#setDefaultValue()
     */
    public void setDefaultValue() {
        if (image == null) {
            image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        }
    }

    /**
     * Sets the 'image'-value.
     * 
     * @param i
     *            'image'-value to be set.
     */
    public void setImage(BufferedImage i) {
        this.image = i;
    }

    /**
     * Returns the 'image'-value of the encapsulated RenderedImage.
     * 
     * @return the 'image'-value of the encapsulated RenderedImage.
     */
    public BufferedImage getImage() {
        return this.image;
    }

    /**
     * Returns the value of this attribute. This attribute just returns the
     * image.
     * 
     * @return the value of this attribute.
     */
    public Object getValue() {
        return getImage();
    }

    /**
     * Copies the image to a new image with the same size.
     * 
     * @return the copied image.
     * 
     * @see org.graffiti.core.DeepCopy#copy()
     */
    public Object copy() {
        return new RenderedImageAttribute(this.id, image);
    }

    /**
     * Sets the value of this object to the given value.
     * 
     * @param o
     *            the new value of this object.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    @Override
    protected void doSetValue(Object o) {
        if (o instanceof BufferedImage) {
            image = (BufferedImage) o;
        } else
            throw new IllegalArgumentException(
                    "only instances of BufferedImage are accepted!");
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
