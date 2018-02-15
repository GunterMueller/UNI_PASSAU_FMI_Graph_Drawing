// =============================================================================
//
//   BinaryFunction.java
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
public abstract class BinaryFunction extends Function {
    /**
     * {@inheritDoc}
     */
    @Override
    public final double eval(double... args) {
        if (args.length != 2)
            throw new SamplingException("error.functionArgumentCount");
        else
            return calc(args[0], args[1]);
    }

    protected abstract double calc(double first, double second);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
