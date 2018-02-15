// =============================================================================
//
//   PositionAttribute.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: PositionAttribute.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.graphics;

import java.util.Map;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.LinkedHashMapAttribute;

/**
 * Contains properties of the attribute position for a label
 * 
 * @version $Revision: 5768 $
 */
public abstract class PositionAttribute extends LinkedHashMapAttribute
        implements GraphicAttributeConstants {

    /**
     * Constructor for NodeLabelPositionAttribute.
     * 
     * @param id
     */
    public PositionAttribute(String id) {
        super(id);
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
        if (!attrs.keySet().isEmpty())
            throw new IllegalArgumentException("Invalid map.");
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
