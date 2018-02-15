// =============================================================================
//
//   EvaluationData.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.graffiti.core.DeepCopy;
import org.graffiti.graph.Graph;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class Data implements DeepCopy {
    private Graph originalGraph;
    private Graph currentGraph;
    private Set<String> activeTimers;
    private Map<String, Long> times;
    private Map<String, Object> results;
    private String sourceIndex;

    private Data() {
    }

    public Data(Graph originalGraph, String sourceIndex) {
        this.originalGraph = originalGraph;
        currentGraph = (Graph) originalGraph.copy();
        activeTimers = new HashSet<String>();
        times = new HashMap<String, Long>();
        results = new HashMap<String, Object>();
        this.sourceIndex = sourceIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Data copy() {
        Data copy = new Data();
        copy.originalGraph = originalGraph;
        copy.currentGraph = (Graph) currentGraph.copy();
        copy.activeTimers = new HashSet<String>(activeTimers);
        copy.times = new HashMap<String, Long>(times);
        copy.results = new HashMap<String, Object>(results);
        copy.sourceIndex = sourceIndex;
        for (Map.Entry<String, Object> entry : results.entrySet()) {
            Object obj = entry.getValue();
            if (obj instanceof DeepCopy) {
                obj = ((DeepCopy) obj).copy();
            }
            copy.results.put(entry.getKey(), obj);
        }
        return copy;
    }

    public Graph getCurrentGraph() {
        return currentGraph;
    }

    public Graph getOriginalGraph() {
        return originalGraph;
    }

    public void setTimer(String id, boolean isStarting) {
        if (isStarting) {
            activeTimers.add(id);
        } else {
            activeTimers.remove(id);
        }
    }

    public Set<String> getActiveTimers() {
        return activeTimers;
    }

    public void addTime(Collection<String> timers, long deltaTime) {
        for (String id : timers) {
            Long time = times.get(id);
            if (time == null) {
                time = 0l;
            }
            times.put(id, time + deltaTime);
        }
    }

    public Map<String, Long> getTimes() {
        return times;
    }

    public String getSourceIndex() {
        return sourceIndex;
    }

    /**
     * @param resultId
     * @param result
     */
    public void addResult(String resultId, Map<String, Object> result) {
        for (Map.Entry<String, Object> entry : result.entrySet()) {
            results.put(resultId + "." + entry.getKey(), entry.getValue());
        }
    }

    public Map<String, Object> getResults() {
        return results;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
