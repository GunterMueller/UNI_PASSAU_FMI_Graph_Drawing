// =============================================================================
//
//   KeyData.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: KeyData.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.exporters.graphml;

import org.graffiti.util.Pair;

/**
 * Class {@code KeyData} is a utility class for creating (Integer, Class<?>)
 * tuples.
 * 
 * @author ruediger
 */
class KeyData extends Pair<Integer, Class<?>> {
    /**
     * Creates a new KeyData object.
     * 
     * @param id
     *            the id of the tuple.
     * @param type
     *            the type of the tuple.
     */
    KeyData(Integer id, Class<?> type) {
        super(id, type);
    }

    /**
     * Returns the id of the tuple.
     * 
     * @return the id of the tuple.
     */
    int getId() {
        return getFirst();
    }

    /**
     * Returns the type of the tuple.
     * 
     * @return the type of the tuple.
     */
    Class<?> getType() {
        return getSecond();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
