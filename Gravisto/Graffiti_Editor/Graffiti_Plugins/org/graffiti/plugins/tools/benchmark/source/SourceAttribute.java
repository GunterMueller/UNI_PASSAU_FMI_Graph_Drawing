// =============================================================================
//
//   SourceAttribute.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.source;

import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.attributes.StringAttribute;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class SourceAttribute extends LinkedHashMapAttribute {
    private static final String TYPE_ID = "type";

    public SourceAttribute(String id) {
        super(id);
    }

    public void setType(String type) {
        if (containsAttribute(TYPE_ID)) {
            ((StringAttribute) getAttribute(TYPE_ID)).setString(type);
        } else {
            add(new StringAttribute(TYPE_ID, type));
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
