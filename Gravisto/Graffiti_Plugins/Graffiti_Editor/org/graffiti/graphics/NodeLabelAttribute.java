// =============================================================================
//
//   NodeLabelAttribute.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: NodeLabelAttribute.java 5768 2010-05-07 18:42:39Z gleissner $

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
 *          specific for nodes.
 */
public class NodeLabelAttribute extends LabelAttribute {

    /** Position of a label within this node. */
    private NodeLabelPositionAttribute position;

    /**
     * Constructor for NodeLabelAttribute.
     * 
     * @param id
     */
    public NodeLabelAttribute(String id) {
        super(id);
        this.position = new NodeLabelPositionAttribute(POSITION);
        this.add(this.position, false);
    }

    /**
     * Constructor for NodeLabelAttribute.
     * 
     * @param id
     * @param l
     *            label string
     */
    public NodeLabelAttribute(String id, String l) {
        super(id);
        this.position = new NodeLabelPositionAttribute(POSITION);
        this.label.setString(l);
        this.add(this.position, false);
    }

    /**
     * Constructor for NodeLabelAttribute.
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
    public NodeLabelAttribute(String id, String l,
            NodeLabelPositionAttribute p, String a, String f, ColorAttribute tc) {
        super(id, l, p, a, f, tc);
        this.position = new NodeLabelPositionAttribute(POSITION);
        this.add(this.position, false);
    }

    /**
     * Constructor for NodeLabelAttribute.
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
    public NodeLabelAttribute(String id, StringAttribute l,
            NodeLabelPositionAttribute p, StringAttribute a, StringAttribute f,
            ColorAttribute tc) {
        super(id, l, p, a, f, tc);
        this.position = new NodeLabelPositionAttribute(POSITION);
        this.add(this.position, false);
    }

    /**
     * Constructor for NodeLabelAttribute.
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
    public NodeLabelAttribute(String id, String l,
            NodeLabelPositionAttribute p, String a, String f, Color tc) {
        super(id, l, p, a, f, tc);
        this.position = new NodeLabelPositionAttribute(POSITION);
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
            } else if (attrId.equals(TEXTCOLOR)) {
                setTextcolor((CollectionAttribute) attrs.get(TEXTCOLOR));
            } else if (attrId.equals(FONT_SIZE)) {
                setFontSize(((IntegerAttribute) attrs.get(FONT_SIZE))
                        .getInteger());
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
    public NodeLabelPositionAttribute getPosition() {
        return this.position;
    }

    /**
     * Returns a deep copy of this object.
     * 
     * @return a deep copy of this object.
     */
    @Override
    public Object copy() {
        NodeLabelAttribute copied = new NodeLabelAttribute(this.getId());
        copied.setLabel(new String(this.getLabel()));

        // copied.label = new StringAttribute(LABEL, new
        // String(this.getLabel()));
        copied.setPosition((NodeLabelPositionAttribute) this.getPosition()
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
    // protected void doSetValue(Object v)
    // throws IllegalArgumentException {
    // if(v instanceof Map) {
    // setCollection((Map) v);
    //
    // return;
    // }
    //
    // super.doSetValue(v);
    //
    // try {
    // this.position =
    // (NodeLabelPositionAttribute) ((NodeLabelAttribute) v).getPosition()
    // .copy();
    // } catch(ClassCastException cce) {
    // throw new IllegalArgumentException("Invalid value type.");
    // }
    // }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
