// =============================================================================
//
//   PortsAttribute.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: PortsAttribute.java 5808 2010-08-26 12:45:01Z hanauer $

package org.graffiti.graphics;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.util.ConcatIterator;

/**
 * Contains ingoing, outgoing and common ports
 * 
 * @author breu
 * @version $Revision: 5808 $
 */
public class PortsAttribute extends HashMapAttribute implements
        GraphicAttributeConstants {

    /** Holds all ports. */
    private CollectionAttribute common;

    /** Holds all ingoing ports. */
    private CollectionAttribute ingoing;

    /** Holds all outgoing ports. */
    private CollectionAttribute outgoing;

    /**
     * Constructor for Ports.
     * 
     * @param id
     *            the id of the attribute.
     */
    public PortsAttribute(String id) {
        super(id);
        this.ingoing = new HashMapAttribute(IN);
        this.outgoing = new HashMapAttribute(OUT);
        this.common = new HashMapAttribute(COMMON);
        add(this.ingoing, false);
        add(this.outgoing, false);
        add(this.common, false);
    }

    /**
     * Constructor for Ports.
     * 
     * @param id
     *            the id of the attribute.
     * @param i
     *            the ingoing-value of the attribute.
     * @param o
     *            the outgoing-value of the attribute.
     * @param c
     *            the common-value of the attribute.
     */
    public PortsAttribute(String id, CollectionAttribute i,
            CollectionAttribute o, CollectionAttribute c) {
        super(id);
        this.ingoing = new HashMapAttribute(IN);
        this.ingoing.setCollection(i.getCollection());
        this.outgoing = new HashMapAttribute(OUT);
        this.outgoing.setCollection(o.getCollection());
        this.common = new HashMapAttribute(COMMON);
        this.common.setCollection(c.getCollection());
        add(this.ingoing, false);
        add(this.outgoing, false);
        add(this.common, false);
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
            if (attrId.equals(IN)) {
                setIngoing((CollectionAttribute) attrs.get(IN));
            } else if (attrId.equals(OUT)) {
                setOutgoing((CollectionAttribute) attrs.get(OUT));
            } else if (attrId.equals(COMMON)) {
                setCommon((CollectionAttribute) attrs.get(COMMON));
            } else {
                this.add((Attribute) attrs.get(attrId).copy());
            }
        }
    }

    /**
     * Sets the 'common'-value.
     * 
     * @param c
     *            the 'common'-value to be set.
     */
    public void setCommon(CollectionAttribute c) {
        this.common.setCollection(c.getCollection());

        // this.common = c;
    }

    /**
     * Returns the 'common'-value of the encapsulated ports.
     * 
     * @return the 'common'-value of the encapsulated ports.
     */
    public CollectionAttribute getCommon() {
        return this.common;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param ports
     *            DOCUMENT ME!
     */
    public void setCommonPorts(List<Port> ports) {
        Map<String, Attribute> portsMap = new HashMap<String, Attribute>();
        int i = 1;

        for (Port port : ports) {
            PortAttribute portAttr = new PortAttribute("port" + i, port
                    .getName(), port.getX(), port.getY());
            portsMap.put("port" + i++, portAttr);
        }

        this.common.setCollection(portsMap);
    }

    /**
     * Sets the 'ingoing'-value.
     * 
     * @param i
     *            the 'ingoing'-value to be set.
     */
    public void setIngoing(CollectionAttribute i) {
        this.ingoing.setCollection(i.getCollection());

        // this.ingoing = i;
    }

    /**
     * Returns the 'ingoing'-value of the encapsulated ports.
     * 
     * @return the 'ingoing'-value of the encapsulated ports.
     */
    public CollectionAttribute getIngoing() {
        return this.ingoing;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param ports
     *            DOCUMENT ME!
     */
    public void setIngoingPorts(List<Port> ports) {
        Map<String, Attribute> portsMap = new HashMap<String, Attribute>();
        int i = 1;

        for (Port port : ports) {
            PortAttribute portAttr = new PortAttribute("port" + i, port
                    .getName(), port.getX(), port.getY());
            portsMap.put("port" + i++, portAttr);
        }

        this.ingoing.setCollection(portsMap);
    }

    /**
     * Sets the 'outgoing'-value.
     * 
     * @param o
     *            the 'outgoing'-value to be set.
     */
    public void setOutgoing(CollectionAttribute o) {
        this.ingoing.setCollection(o.getCollection());

        // this.outgoing = o;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param ports
     *            DOCUMENT ME!
     */
    public void setOutgoingPorts(List<Port> ports) {
        Map<String, Attribute> portsMap = new HashMap<String, Attribute>();
        int i = 1;

        for (Port port : ports) {
            PortAttribute portAttr = new PortAttribute("port" + i, port
                    .getName(), port.getX(), port.getY());
            portsMap.put("port" + i++, portAttr);
        }

        this.outgoing.setCollection(portsMap);
    }

    /**
     * Returns the 'outgoing'-value of the encapsulated ports.
     * 
     * @return the 'outgoing'-value of the encapsulated ports.
     */
    public CollectionAttribute getOutgoing() {
        return this.outgoing;
    }

    /**
     * Look if there is a <code>PortAttribute</code> in this
     * <code>CollectionAttribute</code> called <code>name</code>. Returns
     * <code>null </code> if there is no such attribute.
     * 
     * @param name
     *            the name of the port attribute wanted.
     * @param out
     *            DOCUMENT ME!
     * 
     * @return the <code>PortAttribute</code> named <code>name</code> or
     *         <code>null</code> if no such attribute exists.
     */
    public PortAttribute getPort(String name, boolean out) {
        Map<String, Attribute> commonPortAttributesMap = this.common
                .getCollection();
        Collection<Attribute> commonPortAttributes = commonPortAttributesMap
                .values();
        Map<String, Attribute> otherPortAttributesMap;

        if (out) {
            otherPortAttributesMap = outgoing.getCollection();
        } else {
            otherPortAttributesMap = ingoing.getCollection();
        }

        Collection<Attribute> otherPortAttributes = otherPortAttributesMap
                .values();

        // warning can be ignored, since arguments are of correct type
        @SuppressWarnings("unchecked")
        Iterator<Attribute> it = new ConcatIterator<Attribute>(
                commonPortAttributes, otherPortAttributes);

        while (it.hasNext()) {
            Attribute att = it.next();
            if (att instanceof PortAttribute) {
                PortAttribute port = (PortAttribute) att;

                if (port.getName().equals(name))
                    return port;
            } else {
            }
            
        }

        return null;
    }

    /**
     * Returns a deep copy of this object.
     * 
     * @return A deep copy of this object.
     */
    @Override
    public Object copy() {
        PortsAttribute copied = new PortsAttribute(this.getId());
        copied.setIngoing((CollectionAttribute) this.getIngoing().copy());
        copied.setOutgoing((CollectionAttribute) this.getOutgoing().copy());
        copied.setCommon((CollectionAttribute) this.getCommon().copy());

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
    // throws IllegalArgumentException
    // {
    // PortsAttribute tmp;
    //
    // try
    // {
    // tmp = (PortsAttribute) v;
    // }
    // catch(ClassCastException cce)
    // {
    // throw new IllegalArgumentException("Invalid value type.");
    // }
    //
    // this.ingoing = (CollectionAttribute) tmp.getIngoing().copy();
    // this.outgoing = (CollectionAttribute) tmp.getOutgoing().copy();
    // this.common = (CollectionAttribute) tmp.getCommon().copy();
    // }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
