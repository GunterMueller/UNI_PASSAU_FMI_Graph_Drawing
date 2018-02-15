// =============================================================================
//
//   LinkedHashMapAttribute.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: LinkedHashMapAttribute.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.attributes;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.graffiti.plugin.XMLHelper;

/**
 * DOCUMENT ME!
 * 
 * @version $Revision: 5767 $
 */
public class LinkedHashMapAttribute extends AbstractCollectionAttribute
        implements SortedCollectionAttribute {
    /**
     * Construct a new instance of a <code>LinkedHashMapAttribute</code>. The
     * internal LinkedHashMap is initialized empty.
     * 
     * @param id
     *            the id of the attribute.
     */
    public LinkedHashMapAttribute(String id) {
        super(id);
        this.attributes = new LinkedHashMap<String, Attribute>();
    }

    /**
     * Sets the collection of attributes contained within this
     * <tt>CollectionAttribute</tt> For each entry in the map, pre- and post-
     * AttributeAdded events are generated since method <code>add(Attribute
     * a)</code> is called for each attribute in the map.
     * 
     * @param attrs
     *            the Map that contains all attributes.
     */
    public void setCollection(Map<String, Attribute> attrs) {
        assert attrs != null;
        attributes = new LinkedHashMap<String, Attribute>();

        if (getAttributable() == null) {
            for (Attribute attr : attrs.values()) {
                this.add((Attribute) attr.copy(), false);
            }
        } else {
            for (Attribute attr : attrs.values()) {
                this.add((Attribute) attr.copy());
            }
        }
    }

    /**
     * Returns a cloned map (shallow copy of map: i.e.
     * <code>this.map.equals(getCollection())</code><b>but not</b>
     * <code>this.map == getCollection()</code>) between attributes' ids and
     * attributes contained in this <code>CollectionAttribute</code>.
     * 
     * @return a clone of the list of attributes in this
     *         <code>CollectionAttribute</code>.
     */
    public Map<String, Attribute> getCollection() {
        return Collections.unmodifiableMap(attributes);
    }

    /**
     * Already done in constructor for this attribute type.
     * 
     * @see org.graffiti.attributes.Attribute#setDefaultValue()
     */
    public void setDefaultValue() {
    }

    /**
     * Copies this <code>CollectionAttribute</code> and returns the copy. All
     * sub-attributes will be copied, too, i.e. a deep-copy is returned.
     * 
     * @return a copy of the <code>CollectionAttribute</code>.
     */
    public Object copy() {
        LinkedHashMapAttribute copiedAttributes = new LinkedHashMapAttribute(
                this.getId());

        for (Map.Entry<String, Attribute> entry : attributes.entrySet()) {
            Attribute copiedAttribute = (Attribute) entry.getValue().copy();
            copiedAttribute.setParent(copiedAttributes);
            copiedAttributes.attributes.put(entry.getKey(), copiedAttribute);
        }

        return copiedAttributes;
    }

    /**
     * Sets the value of the attribute by calling method
     * <code>setCollection(Map attrs)</code>. The "value" is the Collection of
     * attributes. For each entry in the map, pre- and post- AttributeAdded
     * events are generated.
     * 
     * @param o
     *            the new value of the attribute.
     * 
     * @exception IllegalArgumentException
     *                if the parameter has not the appropriate class for this
     *                attribute.
     */
    @Override
    protected void doSetValue(Object o) throws IllegalArgumentException {
        assert o != null;

        try {
            @SuppressWarnings("unchecked")
            Map<String, Attribute> attrs = (Map<String, Attribute>) o;
            setCollection(attrs);
        } catch (ClassCastException cce) {
            throw new IllegalArgumentException("Wrong argument type "
                    + "((LinkedHash)Map<String, Attribute> expected).");
        }
    }

    /**
     * @see org.graffiti.plugin.Displayable#toXMLString()
     */
    @Override
    public String toXMLString() {
        StringBuffer valString = new StringBuffer();
        valString.append("<subAttributes>" + XMLHelper.getDelimiter());

        for (Attribute attr : attributes.values()) {
            valString.append(XMLHelper.spc(6) + "<subattr>"
                    + attr.toXMLString() + "</subattr>"
                    + XMLHelper.getDelimiter());
        }

        valString.append(XMLHelper.spc(4) + "</subAttributes>"
                + XMLHelper.getDelimiter() + XMLHelper.spc(4)
                + "<sorted>true</sorted>");

        return getStandardXML(valString.toString());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
