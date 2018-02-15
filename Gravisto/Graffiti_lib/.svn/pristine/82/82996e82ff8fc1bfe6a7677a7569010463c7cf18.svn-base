// =============================================================================
//
//   CompositeSource.java
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

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class CompositeSource extends AbstractGraphSource {
    private List<AbstractGraphSource> sources;

    public CompositeSource() {
        sources = new LinkedList<AbstractGraphSource>();
    }

    public void addSource(AbstractGraphSource source) {
        sources.add(source);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void contribute(Collection<GraphFactory> collection,
            Random random, Assignment assignment) throws BenchmarkException {
        for (GraphSource source : sources) {
            source.contribute(collection, random.nextLong(), assignment);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void provideSourceInfo(SourceAttribute sourceAttribute) {
        sourceAttribute.setType("composite");
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
