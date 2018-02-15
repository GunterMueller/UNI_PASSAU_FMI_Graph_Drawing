// =============================================================================
//
//   SamplingException.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.sampler;

import org.graffiti.plugins.tools.benchmark.Benchmark;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class SamplingException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = -8552783636548676362L;

    public SamplingException(Throwable cause) {
        super(format(cause.getLocalizedMessage()), cause);
    }

    public SamplingException(String key, Object... args) {
        super(format(Benchmark.getString(key, args)));
    }

    private static String format(String message) {
        return String.format(Benchmark.getString("SamplingException.message"),
                message);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
