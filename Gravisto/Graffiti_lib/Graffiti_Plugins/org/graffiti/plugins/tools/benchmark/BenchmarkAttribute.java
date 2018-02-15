// =============================================================================
//
//   BenchmarkAttribute.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.IntegerAttribute;
import org.graffiti.attributes.LinkedHashMapAttribute;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class BenchmarkAttribute extends LinkedHashMapAttribute {
    public static final String ID = "benchmark";
    public static final String PATH = ID;
    public static final String UID_ID = "uid";
    public static final String UID_PATH = AttributeUtil.BENCHMARK_PATH
            + Attribute.SEPARATOR + UID_ID;
    public static final String TIEBREAKER_ID = "tiebreaker";
    public static final String TIEBREAKER_PATH = AttributeUtil.BENCHMARK_PATH
            + Attribute.SEPARATOR + TIEBREAKER_ID;

    public BenchmarkAttribute() {
        super(ID);
    }

    public void addUid(int uid) {
        add(new IntegerAttribute(UID_ID, uid));
    }

    public void addTiebreaker(int tiebreaker) {
        add(new IntegerAttribute(TIEBREAKER_ID, tiebreaker));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
