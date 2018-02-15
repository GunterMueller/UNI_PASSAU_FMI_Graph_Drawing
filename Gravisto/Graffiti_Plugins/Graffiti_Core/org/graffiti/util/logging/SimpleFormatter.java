// =============================================================================
//
//   SimpleFormatter.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SimpleFormatter.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.util.logging;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Provides a brief summary of the LogRecord in a human readable format.
 * 
 * @version $Revision: 5767 $
 */
public class SimpleFormatter extends Formatter {

    /**
     * Returns the message of the given <code>LogRecord</code>
     * 
     * @param record
     *            DOCUMENT ME!
     * 
     * @return a human readable string of the log record's message.
     */
    @Override
    public String format(LogRecord record) {
        return record.getMessage() + "\n";
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
