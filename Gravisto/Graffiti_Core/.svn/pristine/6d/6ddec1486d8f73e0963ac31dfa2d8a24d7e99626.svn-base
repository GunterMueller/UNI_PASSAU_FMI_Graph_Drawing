// =============================================================================
//
//   GraphSource.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.source;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.graffiti.plugins.tools.benchmark.Assignment;
import org.graffiti.plugins.tools.benchmark.BenchmarkException;
import org.graffiti.plugins.tools.benchmark.Seedable;

/**
 * Abstract base class of common graph sources.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class AbstractGraphSource implements GraphSource, Seedable {
    private Long fixedSeed;
    private List<SourceTransformation> transformations;

    protected AbstractGraphSource() {
        transformations = new LinkedList<SourceTransformation>();
    }

    public void setFixedSeed(long fixedSeed) {
        this.fixedSeed = fixedSeed;
    }

    /**
     * {@inheritDoc}
     */
    public void contribute(Collection<GraphFactory> collection, long seed,
            Assignment assignment) throws BenchmarkException {
        final long actualSeed = fixedSeed == null ? seed : fixedSeed;
        Random random = new Random(actualSeed);
        List<GraphFactory> list = new LinkedList<GraphFactory>();
        contribute(list, random, assignment);
        for (GraphFactory graphFactory : list) {
            /*
             * AttributeUtil.provideBenchmarkAttribute(graph);
             * AttributeUtil.addSourceAttribute( graph, true, new
             * VoidCallback<SourceAttribute>() {
             * 
             * @Override public void call(SourceAttribute sourceAttribute) {
             * sourceAttribute.add(new LongAttribute("seed", actualSeed));
             * provideSourceInfo(sourceAttribute); } });
             */

            graphFactory.addTransformations(transformations, random);

            /*
             * for (SourceTransformation transformation : transformations) {
             * transformation.execute(graph, random.nextLong(), assignment); }
             */

            collection.add(graphFactory);
        }
    }

    /**
     * Adds the graphs provided by this source to the specified collection.
     * 
     * @param collection
     *            the collection to which the graphs are added to.
     * @param random
     *            a deterministic source of randomness.
     */
    protected abstract void contribute(Collection<GraphFactory> collection,
            Random random, Assignment assignment) throws BenchmarkException;

    protected abstract void provideSourceInfo(SourceAttribute sourceAttribute);

    /**
     * {@inheritDoc}
     */
    @Override
    public void addTransformation(SourceTransformation transformation) {
        transformations.add(transformation);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
