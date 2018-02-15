// =============================================================================
//
//   SourcesAttribute.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.source;

import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.plugins.tools.benchmark.AttributeUtil;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class SourcesAttribute extends LinkedHashMapAttribute {
    public static final String ID = "sources";
    public static final String PATH = AttributeUtil.BENCHMARK_PATH + "." + ID;

    public SourcesAttribute() {
        super(ID);
    }

    public String getNextId(boolean isSource) {
        return (isSource ? "source" : "transformation") + attributes.size();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
