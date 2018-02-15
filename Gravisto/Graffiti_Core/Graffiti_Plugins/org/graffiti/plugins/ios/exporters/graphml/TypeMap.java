// =============================================================================
//
//   TypeMap.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: TypeMap.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.exporters.graphml;

import java.util.HashMap;

/**
 * This class provides a mapping from Gravisto attributes types to graphML
 * attribute types.
 * 
 * @author ruediger
 */
class TypeMap {

    /** Maps Gravisto attribute types to graphML attribute types. */
    private HashMap<String, String> map;

    /**
     * Constructs a new <code>TypeMap</code>.
     */
    TypeMap() {
        this.map = new HashMap<String, String>();

        // add the straight forward mapping for the base attributes
        this.map.put("org.graffiti.attributes.BooleanAttribute", "boolean");
        this.map.put("org.graffiti.attributes.IntegerAttribute", "int");
        this.map.put("org.graffiti.attributes.LongAttribute", "long");
        this.map.put("org.graffiti.attributes.FloatAttribute", "float");
        this.map.put("org.graffiti.attributes.DoubleAttribute", "double");
        this.map.put("org.graffiti.attributes.StringAttribute", "string");

        // advanced mappings
        this.map.put("org.graffiti.attributes.ShortAttribute", "int");
        this.map.put("org.graffiti.attributes.ByteAttribute", "int");
        this.map.put("org.graffiti.attributes.NodeShapeAttribute", "string");
        this.map.put("org.graffiti.attributes.EdgeShapeAttribute", "string");
        this.map.put("org.graffiti.graphics.RenderedImageAttribute", "string");
        this.map.put("org.graffiti.graphics.LineModeAttribute", "string");
        this.map
                .put(
                        "org.graffiti.graphics.GridAttribute$GridClassAttribute$GridClassStringAttribute",
                        "string");
    }

    /**
     * Returns the graphML attribute type for a given Gravisto attribute type,
     * <code>null</code> if there is no such type in the map.
     * 
     * @param gravistoType
     *            the Gravisto attribute type.
     * 
     * @return the graphML attribute type.
     */
    String getGraphMLType(String gravistoType) {
        String graphMLType = this.map.get(gravistoType);

        return graphMLType;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
