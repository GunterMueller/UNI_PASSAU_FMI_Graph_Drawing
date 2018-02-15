// =============================================================================
//
//   FormatException.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.xml;

import org.graffiti.plugins.tools.benchmark.Benchmark;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class FormatException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = -4604316578861680459L;

    public FormatException(Throwable cause) {
        super(format(cause.getLocalizedMessage()), cause);
    }

    public FormatException(String key, Object... args) {
        super(format(Benchmark.getString(key, args)));
    }

    private static String format(String message) {
        return String.format(Benchmark
                .getString("BenchmarkDescriptionFormatException.message"),
                message);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
