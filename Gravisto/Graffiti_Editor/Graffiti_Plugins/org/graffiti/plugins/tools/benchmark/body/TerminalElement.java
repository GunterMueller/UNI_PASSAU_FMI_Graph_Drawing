// =============================================================================
//
//   TerminalElement.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.body;

import java.util.List;

import org.graffiti.plugins.tools.benchmark.Assignment;
import org.graffiti.plugins.tools.benchmark.BenchmarkException;
import org.graffiti.plugins.tools.benchmark.Data;
import org.graffiti.plugins.tools.benchmark.output.BenchmarkOutput;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class TerminalElement implements BodyElement {
    private List<BenchmarkOutput> outputs;

    public TerminalElement(List<BenchmarkOutput> outputs) {
        this.outputs = outputs;
    }

    /**
     * {@inheritDoc} This implementation throws an
     * {@link UnsupportedOperationException} as the terminal element does not
     * have a successor.
     */
    @Override
    public void setNext(BodyElement nextElement) throws BenchmarkException {
        throw new UnsupportedOperationException("Not implemented.");
    }

    /**
     * {@inheritDoc} This implementation throws an
     * {@link UnsupportedOperationException} as the terminal element is not
     * assigned a seed.
     */
    @Override
    public void updateSeed(long seed) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Data data, Assignment assignment)
            throws BenchmarkException {
        for (BenchmarkOutput output : outputs) {
            output.postConfig(assignment, data);
        }

        assignment.incConfigurationIndex();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
