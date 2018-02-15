// =============================================================================
//
//   Timer.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.body;

import org.graffiti.plugins.tools.benchmark.Assignment;
import org.graffiti.plugins.tools.benchmark.BenchmarkException;
import org.graffiti.plugins.tools.benchmark.Data;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class Timer implements BodyElement {
    private BodyElement nextElement;
    private String id;
    private boolean isStarting;

    public Timer(String id, boolean isStarting) {
        this.id = id;
        this.isStarting = isStarting;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Data data, Assignment assignment)
            throws BenchmarkException {
        data.setTimer(id, isStarting);
        nextElement.execute(data, assignment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNext(BodyElement nextElement) throws BenchmarkException {
        this.nextElement = nextElement;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateSeed(long seed) {
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
