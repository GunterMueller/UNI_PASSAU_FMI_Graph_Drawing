// =============================================================================
//
//   TimerHandle.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.body;

import java.util.Set;

import org.graffiti.plugins.tools.benchmark.Benchmark;
import org.graffiti.plugins.tools.benchmark.Data;
import org.graffiti.plugins.tools.benchmark.LoggingUtil;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class TimerHandle {
    private Long startTime;
    private Set<String> activeTimers;
    private Set<String> surpressedTimers;
    private Data data;

    public TimerHandle(Set<String> activeTimers, Set<String> surpressedTimers,
            Data data) {
        this.activeTimers = activeTimers;
        this.surpressedTimers = surpressedTimers;
        this.data = data;
    }

    public void beforeExecute() {
        if (startTime != null) {
            LoggingUtil.getLogger().warning(
                    Benchmark.getString("warning.recursive"));
        }
        startTime = System.nanoTime();
    }

    public void afterExecute() {
        if (startTime == null)
            return;
        long currentTime = System.nanoTime();
        long deltaTime = currentTime - startTime;
        data.addTime(activeTimers, deltaTime);
        data.addTime(surpressedTimers, -deltaTime);
    }

    protected Set<String> getActiveTimers() {
        return activeTimers;
    }

    protected Set<String> getSurpressedTimers() {
        return surpressedTimers;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
