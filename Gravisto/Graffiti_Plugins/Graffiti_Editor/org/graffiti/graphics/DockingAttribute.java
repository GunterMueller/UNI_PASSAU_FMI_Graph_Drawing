// =============================================================================
//
//   DockingAttribute.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DockingAttribute.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.graphics;

import java.util.Map;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.attributes.StringAttribute;

/**
 * Contains the graphic attribute docking for edges. It contains the names of
 * the ports the edge should dock to in the source/target node. Default is the
 * empty string - the <code>EdgeComponent</code> knows how to interpret this
 * correctly.
 * 
 * @author breu
 * @version $Revision: 5768 $
 */
public class DockingAttribute extends HashMapAttribute implements
        GraphicAttributeConstants {

    /** Defines a port at source node */
    private StringAttribute source;

    /** Defines a port at target node */
    private StringAttribute target;

    /**
     * Constructor for Docking.
     * 
     * @param id
     *            the id of the attriubte.
     */
    public DockingAttribute(String id) {
        this(id, "", "");
    }

    /**
     * Constructor for Docking.
     * 
     * @param id
     *            the id of the attribute.
     * @param source
     *            the source-value of the attribute.
     * @param target
     *            the target-value of the attribute.
     */
    public DockingAttribute(String id, String source, String target) {
        super(id);
        this.source = new StringAttribute(SOURCE, source);
        this.target = new StringAttribute(TARGET, target);
        add(this.source, false);
        add(this.target, false);
    }

    /**
     * Sets the collection of attributes contained within this
     * <tt>CollectionAttribute</tt>. The docking values are set, additional
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
            if (attrId.equals(SOURCE)) {
                setSource(((StringAttribute) attrs.get(SOURCE)).getString());
            } else if (attrId.equals(TARGET)) {
                setTarget(((StringAttribute) attrs.get(TARGET)).getString());
            } else {
                this.add((Attribute) attrs.get(attrId).copy());
            }
        }

    }

    /**
     * Sets the 'source'-value.
     * 
     * @param s
     *            the 'source'-value to be set.
     */
    public void setSource(String s) {
        this.source.setString(s);
    }

    /**
     * Returns the 'source'-value of the encapsulated docking.
     * 
     * @return the 'source'-value of the encapsulated docking.
     */
    public String getSource() {
        return this.source.getString();
    }

    /**
     * Sets the 'target'-value.
     * 
     * @param t
     *            the 'target'-value to be set.
     */
    public void setTarget(String t) {
        this.target.setString(t);
    }

    /**
     * Returns the 'target'-value of the encapsulated docking.
     * 
     * @return the 'target'-value of the encapsulated docking.
     */
    public String getTarget() {
        return this.target.getString();
    }

    /**
     * Returns a deep copy of this object.
     * 
     * @return A deep copy of this object.
     */
    @Override
    public Object copy() {
        DockingAttribute copied = new DockingAttribute(this.getId());
        copied.setSource(this.getSource());
        copied.setTarget(this.getTarget());

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
    // * <code>DockingAttribute</code>.
    // */
    // protected void doSetValue(Object v)
    // throws IllegalArgumentException
    // {
    // try
    // {
    // this.source.setString(((DockingAttribute) v).getSource());
    // this.target.setString(((DockingAttribute) v).getTarget());
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
