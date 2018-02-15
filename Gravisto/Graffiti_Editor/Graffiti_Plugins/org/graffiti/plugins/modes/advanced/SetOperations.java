// =============================================================================
//
//   SetOperations.java
//
//   Copyright (c) 2004 Graffiti Team, Uni Passau
//
// =============================================================================
// $Id: SetOperations.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Helper-classes implementing some Set-based operations. The methods return
 * HashSets or ArrayLists.
 */
public class SetOperations {

    /**
     * Returns a new Set containing all elements present in coll1, but not in
     * coll2 and nothing else.
     * 
     * @param coll1
     *            any Collection
     * @param coll2
     *            any Collection
     * @return a new Set containing all elements present in coll1, but not in
     *         coll2, and nothing else
     */
    public static <T> Set<T> minus(Collection<T> coll1, Collection<T> coll2) {
        Set<T> retValue = new HashSet<T>(coll1);
        retValue.removeAll(coll2);
        return retValue;
    }
}
