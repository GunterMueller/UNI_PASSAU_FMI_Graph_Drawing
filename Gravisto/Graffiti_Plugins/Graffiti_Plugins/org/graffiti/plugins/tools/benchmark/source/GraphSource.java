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

import org.graffiti.plugins.tools.benchmark.Assignment;
import org.graffiti.plugins.tools.benchmark.BenchmarkException;

/**
 * Classes implementing {@code GraphSource} are sources of graphs, e.g. files or
 * graph generators. Several sources may be joined by a {@link CompositeSource}.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public interface GraphSource {
    /**
     * Adds the graphs provided by this source to the specified collection.
     * 
     * @param collection
     *            the collection to which the graphs are added to.
     * @param seed
     *            a deterministic source of randomness.
     */
    public void contribute(Collection<GraphFactory> collection, long seed,
            Assignment assignment) throws BenchmarkException;

    public void addTransformation(SourceTransformation transformation);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
