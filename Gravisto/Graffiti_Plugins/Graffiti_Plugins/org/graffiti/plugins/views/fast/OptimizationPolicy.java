// =============================================================================
//
//   OptimizationPolicy.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast;

import java.util.ArrayList;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public enum OptimizationPolicy {
    QUALITY_SPEED("High quality at high speed"), QUALITY_SPACE(
            "High quality with low memory demands"), DRAFT("Draft quality");

    private static ArrayList<String> names;

    private OptimizationPolicy(String name) {
        addName(name);
    }

    public String getName() {
        return names.get(ordinal());
    }

    public static ArrayList<String> getNames() {
        return names;
    }

    private void addName(String name) {
        if (names == null) {
            names = new ArrayList<String>();
        }
        names.add(name);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
