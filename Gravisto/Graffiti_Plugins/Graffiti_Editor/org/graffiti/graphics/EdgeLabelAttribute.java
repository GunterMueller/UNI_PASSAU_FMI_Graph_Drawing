// =============================================================================
//
//   EdgeLabelAttribute.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: EdgeLabelAttribute.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.graphics;

import java.awt.Color;
import java.util.Map;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.attributes.IntegerAttribute;
import org.graffiti.attributes.StringAttribute;

/**
 * DOCUMENT ME!
 * 
 * @author holleis
 * @version $Revision: 5768 $ Extends LabelAttribute by a PositionAttribute
 *          specific for edges.
 */
public class EdgeLabelAttribute extends LabelAttribute {

    /** Position of a label within this node. */
    private EdgeLabelPositionAttribute position;

    /**
     * Constructor for EdgeLabelAttribute.
     * 
     * @param id
     */
    public EdgeLabelAttribute(String id) {
        super(id);
        this.position = new EdgeLabelPositionAttribute(POSITION);
        this.add(this.position, false);
    }

    /**
     * Constructor for EdgeLabelAttribute.
     * 
     * @param id
     * @param l
     *            DOCUMENT ME!
     */
    public EdgeLabelAttribute(String id, String l) {
        super(id);
        this.position = new EdgeLabelPositionAttribute(POSITION);
        this.label.setString(l);
        this.add(this.position, false);
    }

    /**
     * Constructor for EdgeLabelAttribute.
     * 
     * @param id
     *            DOCUMENT ME!
     * @param l
     *            DOCUMENT ME!
     * @param p
     *            DOCUMENT ME!
     * @param a
     *            DOCUMENT ME!
     * @param f
     *            DOCUMENT ME!
     * @param tc
     *            DOCUMENT ME!
     */
    public EdgeLabelAttribute(String id, String l,
            EdgeLabelPositionAttribute p, String a, String f, ColorAttribute tc) {
        super(id, l, p, a, f, tc);
        this.position = new EdgeLabelPositionAttribute(POSITION);
        this.add(this.position, false);
    }

    /**
     * Constructor for EdgeLabelAttribute.
     * 
     * @param id
     *            DOCUMENT ME!
     * @param l
     *            DOCUMENT ME!
     * @param p
     *            DOCUMENT ME!
     * @param a
     *            DOCUMENT ME!
     * @param f
     *            DOCUMENT ME!
     * @param tc
     *            DOCUMENT ME!
     */
    public EdgeLabelAttribute(String id, StringAttribute l,
            EdgeLabelPositionAttribute p, StringAttribute a, StringAttribute f,
            ColorAttribute tc) {
        super(id, l, p, a, f, tc);
        this.position = new EdgeLabelPositionAttribute(POSITION);
        this.add(this.position, false);
    }

    /**
     * Constructor for EdgeLabelAttribute.
     * 
     * @param id
     *            DOCUMENT ME!
     * @param l
     *            DOCUMENT ME!
     * @param p
     *            DOCUMENT ME!
     * @param a
     *            DOCUMENT ME!
     * @param f
     *            DOCUMENT ME!
     * @param tc
     *            DOCUMENT ME!
     */
    public EdgeLabelAttribute(String id, String l,
            EdgeLabelPositionAttribute p, String a, String f, Color tc) {
        super(id, l, p, a, f, tc);
        this.position = new EdgeLabelPositionAttribute(POSITION);
        this.add(this.position, false);
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
            if (attrId.equals(LABEL)) {
                setLabel(((StringAttribute) attrs.get(LABEL)).getString());
            } else if (attrId.equals(POSITION)) {
                setPosition((CollectionAttribute) attrs.get(POSITION));
            } else if (attrId.equals(FONT)) {
                setFont(((StringAttribute) attrs.get(FONT)).getString());
            } else if (attrId.equals(FONT_SIZE)) {
                setFontSize(((IntegerAttribute) attrs.get(FONT_SIZE))
                        .getInteger());
            } else if (attrId.equals(TEXTCOLOR)) {
                setTextcolor((CollectionAttribute) attrs.get(TEXTCOLOR));
            } else if (attrId.equals(MAX_WIDTH)) {
                setMaxWidth(((DoubleAttribute) attrs.get(MAX_WIDTH))
                        .getDouble());

            } else {
                this.add((Attribute) attrs.get(attrId).copy());
            }
        }
    }

    /**
     * Sets the 'position'-value.
     * 
     * @param p
     *            the 'position'-value to be set.
     */
    public void setPosition(CollectionAttribute p) {
        position.setCollection(p.getCollection());
    }

    /**
     * Returns the NodeLabelPositionAttribute specifying the position of the
     * encapsulated label.
     * 
     * @return the NodeLabelPositionAttribute specifying the position of the
     *         encapsulated label.
     */
    public EdgeLabelPositionAttribute getPosition() {
        return this.position;
    }

    /**
     * Returns a deep copy of this object.
     * 
     * @return a deep copy of this object.
     */
    @Override
    public Object copy() {
        EdgeLabelAttribute copied = new EdgeLabelAttribute(this.getId());
        copied.setLabel(new String(this.getLabel()));
        copied.setPosition((EdgeLabelPositionAttribute) this.getPosition()
                .copy());
        copied.setFont(new String(this.getFont()));
        copied.setFontSize(this.getFontSize());
        copied.setTextcolor((ColorAttribute) this.getTextcolor().copy());

        return copied;
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
    // protected void doSetValue(Object v) throws IllegalArgumentException
    // {
    // super.doSetValue(v);
    //
    // try
    // {
    // this.position = (EdgeLabelPositionAttribute) ((EdgeLabelAttribute)
    // v).getPosition()
    // .copy();
    // }
    // catch (ClassCastException cce)
    // {
    // throw new IllegalArgumentException("Invalid value type.");
    // }
    // }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
