// =============================================================================
//
//   FunctionSampler.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.sampler;

import java.util.Arrays;
import java.util.List;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class FunctionSampler extends Sampler {
    private String functionId;
    private List<Sampler> arguments;

    public FunctionSampler(String functionId, List<Sampler> arguments) {
        this.functionId = functionId;
        this.arguments = arguments;
    }

    public FunctionSampler(String functionId, Sampler... arguments) {
        this(functionId, Arrays.asList(arguments));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double sample(SamplingContext context) {
        return context.call(functionId, getArguments(arguments, context));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
