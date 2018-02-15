// =============================================================================
//
//   GraphFactory.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.source;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.graffiti.graph.Graph;
import org.graffiti.plugins.tools.benchmark.Assignment;
import org.graffiti.plugins.tools.benchmark.AttributeUtil;
import org.graffiti.plugins.tools.benchmark.BenchmarkException;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class GraphFactory {
    protected abstract Graph makeGraph() throws BenchmarkException;

    protected abstract Assignment getAssignment();

    private List<SourceTransformation> transformations;
    private LinkedList<Long> seeds;

    protected GraphFactory() {
        transformations = new LinkedList<SourceTransformation>();
        seeds = new LinkedList<Long>();
    }

    public final Graph createGraph() throws BenchmarkException {
        Graph graph = makeGraph();
        Assignment assignment = getAssignment();

        AttributeUtil.provideBenchmarkAttribute(graph);
        /*
         * AttributeUtil.addSourceAttribute( graph, true, new
         * VoidCallback<SourceAttribute>() {
         * 
         * @Override public void call(SourceAttribute sourceAttribute) {
         * sourceAttribute.add(new LongAttribute("seed", actualSeed));
         * provideSourceInfo(sourceAttribute); } });
         */

        for (SourceTransformation transformation : transformations) {
            transformation.execute(graph, seeds.removeFirst(), assignment);
        }

        return graph;
    }

    public void addTransformations(List<SourceTransformation> transformations,
            Random random) {
        this.transformations.addAll(transformations);

        for (@SuppressWarnings("unused")
        SourceTransformation transformation : transformations) {
            seeds.add(random.nextLong());
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
