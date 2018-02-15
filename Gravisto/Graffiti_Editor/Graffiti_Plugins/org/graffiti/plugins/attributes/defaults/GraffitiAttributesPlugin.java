// =============================================================================
//
//   GraffitiAttributesPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraffitiAttributesPlugin.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.attributes.defaults;

import org.graffiti.attributes.BooleanAttribute;
import org.graffiti.attributes.ByteAttribute;
import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.attributes.FloatAttribute;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.attributes.IntegerAttribute;
import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.attributes.LongAttribute;
import org.graffiti.attributes.ShortAttribute;
import org.graffiti.attributes.StringAttribute;
import org.graffiti.plugin.GenericPluginAdapter;

/**
 * This class provides the default attribute types.
 */
public class GraffitiAttributesPlugin extends GenericPluginAdapter {

    /** The number of attributes provided by default. */
    private static final int NUMBER_OF_ATTRIBUTES = 10;

    /**
     * Constructs a new <code>GraffitiAttributesPlugin</code>.
     */
    public GraffitiAttributesPlugin() {
        super();
        this.attributes = new Class[NUMBER_OF_ATTRIBUTES];
        attributes[0] = BooleanAttribute.class;
        attributes[1] = ByteAttribute.class;
        attributes[2] = DoubleAttribute.class;
        attributes[3] = FloatAttribute.class;
        attributes[4] = HashMapAttribute.class;
        attributes[5] = IntegerAttribute.class;
        attributes[6] = LongAttribute.class;
        attributes[7] = ShortAttribute.class;
        attributes[8] = StringAttribute.class;
        attributes[9] = LinkedHashMapAttribute.class;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
