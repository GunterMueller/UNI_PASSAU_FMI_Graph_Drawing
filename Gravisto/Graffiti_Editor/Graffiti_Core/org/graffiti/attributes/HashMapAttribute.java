// =============================================================================
//
//   HashMapAttribute.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: HashMapAttribute.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.attributes;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps a given id to an attribute. (e.g. if this Attribute contains 2 other
 * attributes, one StringAttribute with id &quot;Name&quot; and another
 * CollectionAttribute with id &quot;Color&quot; which contains three
 * IntegerAttributes ('red', 'green' and 'blue'). The map then contains only the
 * two key-value pairs 'Name'-StringAttribute and 'Color'-CollectionAttribute.
 * The subattributes 'red', 'green' and 'blue' are not mapped in this Attribute!
 * 
 * @version $Revision: 5767 $
 * 
 * @see CollectionAttribute
 * @see CompositeAttribute
 */
public class HashMapAttribute extends AbstractCollectionAttribute implements
        CollectionAttribute {
    /**
     * Construct a new instance of a <code>HashMapAttribute</code>. The internal
     * HashMap is initialized empty.
     * 
     * @param id
     *            the id of the attribute.
     */
    public HashMapAttribute(String id) {
        super(id);
        this.attributes = new HashMap<String, Attribute>();
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
        attributes = new HashMap<String, Attribute>();

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
     * attributes contained in this attribute.
     * 
     * @return a clone of the list of attributes in this attribute.
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
        HashMapAttribute copiedAttributes = new HashMapAttribute(this.getId());

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
                    + "((Hash)Map<String, Attribute> expected).");
        }

    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
