// =============================================================================
//
//   AttributeUtil.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark;

import java.util.Iterator;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.plugins.tools.benchmark.source.SourceAttribute;
import org.graffiti.plugins.tools.benchmark.source.SourcesAttribute;
import org.graffiti.plugins.tools.math.Permutation;
import org.graffiti.util.VoidCallback;

/**
 * Utility class for maintaining the benchmark attribute of a graph.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class AttributeUtil {
    public static final String BENCHMARK_PATH = "benchmark";

    public static void provideBenchmarkAttribute(Graph graph) {
        BenchmarkAttribute benchmarkAttribute = null;
        if (!graph.containsAttribute(BENCHMARK_PATH)) {
            benchmarkAttribute = new BenchmarkAttribute();
            graph.addAttribute(benchmarkAttribute, "");
        } else {
            benchmarkAttribute = (BenchmarkAttribute) graph
                    .getAttribute(BENCHMARK_PATH);
        }
        if (!benchmarkAttribute.containsAttribute(SourcesAttribute.ID)) {
            benchmarkAttribute.add(new SourcesAttribute());
        }
    }

    public static void addElementAttributes(Graph graph,
            Permutation nodeTiebreaker, Permutation edgeTiebreaker) {
        int uid = 0;
        for (Iterator<Node> iter = graph.getNodesIterator(); iter.hasNext(); uid++) {
            addElementAttributes(iter.next(), uid, nodeTiebreaker);
        }

        uid = 0;
        for (Iterator<Edge> iter = graph.getEdgesIterator(); iter.hasNext(); uid++) {
            addElementAttributes(iter.next(), uid, edgeTiebreaker);
        }
    }

    private static void addElementAttributes(GraphElement element, int uid,
            Permutation tiebreaker) {
        if (element.containsAttribute(BENCHMARK_PATH)) {
            element.removeAttribute(BENCHMARK_PATH);
        }

        BenchmarkAttribute benchmarkAttribute = new BenchmarkAttribute();
        element.addAttribute(benchmarkAttribute, "");
        benchmarkAttribute.addUid(uid);
        benchmarkAttribute.addTiebreaker(tiebreaker.get(uid));
    }

    public static void addSourceAttribute(Graph graph, boolean isSource,
            VoidCallback<SourceAttribute> callback) {
        SourcesAttribute sources = (SourcesAttribute) graph
                .getAttribute(SourcesAttribute.PATH);
        String id = sources.getNextId(isSource);
        SourceAttribute source = new SourceAttribute(id);
        sources.add(source);
        callback.call(source);
    }

    public static void removeBenchmarkAttribute(Graph graph) {
        if (graph.containsAttribute(BENCHMARK_PATH)) {
            graph.removeAttribute(BENCHMARK_PATH);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
