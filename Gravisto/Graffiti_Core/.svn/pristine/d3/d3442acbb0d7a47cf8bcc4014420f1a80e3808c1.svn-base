// =============================================================================
//
//   InternalTimerMap.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.body;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.graffiti.plugins.tools.benchmark.xml.FormatException;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class InternalTimerMap {
    private Set<String>[] timers;

    public void check() throws FormatException {
        if (timers != null) {
            Set<String> beforeStarting = get(true, true);
            Set<String> beforeEnding = get(true, false);
            Set<String> afterStarting = get(false, true);
            Set<String> afterEnding = get(false, false);
            if (!beforeStarting.equals(afterEnding)
                    || !beforeEnding.equals(afterStarting))
                throw new FormatException("error.timerNesting");
            afterStarting.retainAll(beforeStarting);
            if (!afterStarting.isEmpty())
                throw new FormatException("error.timerNesting");
            timers[2] = null;
            timers[3] = null;
        }
    }

    public void set(String id, boolean isBefore, boolean isStarting) {
        if (timers == null) {
            @SuppressWarnings("unchecked")
            Set<String>[] ts = (Set<String>[]) new Set<?>[4];
            timers = ts;
        }
        int index = index(isBefore, isStarting);
        if (timers[index] == null) {
            timers[index] = new HashSet<String>();
        }
        timers[index].add(id);
    }

    public Set<String> get(boolean isBefore, boolean isStarting) {
        if (timers == null)
            return Collections.emptySet();
        Set<String> result = timers[index(isBefore, isStarting)];
        return result == null ? Collections.<String> emptySet() : result;
    }

    private int index(boolean isBefore, boolean isStarting) {
        return (isBefore ? 0 : 2) | (isStarting ? 0 : 1);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
