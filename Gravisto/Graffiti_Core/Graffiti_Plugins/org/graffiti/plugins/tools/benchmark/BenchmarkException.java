// =============================================================================
//
//   BenchmarkException.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class BenchmarkException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 3706654039951949369L;

    public BenchmarkException(Throwable cause) {
        super(format(cause.getLocalizedMessage()), cause);
    }

    public BenchmarkException(String key, Object... args) {
        super(format(Benchmark.getString(key, args)));
    }

    private static String format(String message) {
        return String.format(Benchmark.getString("BenchmarkException.message"),
                message);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
