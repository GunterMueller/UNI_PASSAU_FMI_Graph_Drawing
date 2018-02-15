// =============================================================================
//
//   LoggingOutput.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.output;

import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import org.graffiti.plugins.tools.benchmark.BenchmarkException;
import org.graffiti.plugins.tools.benchmark.LoggingUtil;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class LoggingOutput extends BenchmarkOutput {
    private int level;
    private Formatter formatter;

    public void log(LogRecord record) throws BenchmarkException {
        try {
            if (record.getLevel().intValue() >= level) {
                new PrintStream(getOut(true), true).println(formatter
                        .format(record));
            }
        } catch (IOException e) {
            throw new BenchmarkException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postBenchmark() throws IOException, BenchmarkException {
        getOut(true).flush();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(int sourceCount) throws BenchmarkException {
        if (options != null) {
            String levelString = options.get("level");
            if (levelString != null) {
                level = Level.parse(levelString.trim()).intValue();
            }

            String formatString = options.get("format");
            if (formatString != null) {
                if (formatString.equals("raw")) {
                    formatter = new Formatter() {
                        @Override
                        public String format(LogRecord record) {
                            return record.getMessage();
                        }
                    };
                } else if (formatString.equals("simple")) {
                    formatter = new SimpleFormatter();
                } else
                    throw new BenchmarkException("error.unknownOutputOption",
                            formatString);
            }
        }
        if (formatter == null) {
            formatter = new SimpleFormatter();
        }
        LoggingUtil.addLoggingOutput(this);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
