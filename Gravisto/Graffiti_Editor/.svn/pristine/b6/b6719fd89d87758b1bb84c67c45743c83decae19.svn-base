// =============================================================================
//
//   UnaryFunction.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.sampler;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class UnaryFunction extends Function {
    /**
     * {@inheritDoc}
     */
    @Override
    public final double eval(double... args) {
        if (args.length != 1)
            throw new SamplingException("error.functionArgumentCount");
        else
            return calc(args[0]);
    }

    protected abstract double calc(double arg);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
