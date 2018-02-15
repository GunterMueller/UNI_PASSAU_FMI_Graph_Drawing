// =============================================================================
//
//   LoggingUtil.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.graffiti.plugins.tools.benchmark.output.LoggingOutput;

/**
 * Logging utility for the benchmark tool.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class LoggingUtil {
    private static LinkedList<LoggingOutput> loggers;
    private static LinkedList<LogRecord> prematureOutput;

    private static Logger benchmarkLogger;

    private static final String[] CORE_LOGGERS = { "org.graffiti.graph",
            "org.graffiti.attributes" };

    /**
     * Stack holding the logging levels.
     */
    private static Stack<Map<Logger, Level>> LEVEL_STACK;

    /**
     * Sets the logging levels and saves the previous levels on a stack.
     * 
     * @param coreLogging
     *            the logging level of the core classes.
     */
    protected static void push(Level coreLogging) {
        if (LEVEL_STACK == null) {
            LEVEL_STACK = new Stack<Map<Logger, Level>>();
        }

        Map<Logger, Level> map = new HashMap<Logger, Level>();

        for (String logger : CORE_LOGGERS) {
            Logger coreLogger = Logger.getLogger(logger);
            map.put(coreLogger, coreLogger.getLevel());
            coreLogger.setLevel(coreLogging);
        }

        LEVEL_STACK.push(map);

        loggers = null;
        prematureOutput = new LinkedList<LogRecord>();
        if (benchmarkLogger == null) {
            benchmarkLogger = Logger.getLogger(Benchmark.class
                    .getCanonicalName());
            benchmarkLogger.setLevel(Level.FINEST);
            benchmarkLogger.setUseParentHandlers(false);
            benchmarkLogger.addHandler(new Handler() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public void close() throws SecurityException {
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public void flush() {
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public void publish(LogRecord record) {
                    try {
                        LoggingUtil.publish(record);
                    } catch (BenchmarkException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }

    /**
     * Restores the previous logging levels.
     */
    protected static void pop() {
        if (LEVEL_STACK == null)
            throw new IllegalStateException("Empty logging stack.");

        Map<Logger, Level> map = LEVEL_STACK.pop();

        if (LEVEL_STACK.isEmpty()) {
            LEVEL_STACK = null;
        }

        for (Map.Entry<Logger, Level> entry : map.entrySet()) {
            entry.getKey().setLevel(entry.getValue());
        }

        loggers = null;
    }

    protected static void begin() throws BenchmarkException {
        if (loggers != null) {
            for (LogRecord record : prematureOutput) {
                publish(record);
            }
        }
        prematureOutput = null;
    }

    public static void addLoggingOutput(LoggingOutput loggingOutput) {
        if (loggers == null) {
            loggers = new LinkedList<LoggingOutput>();
        }
        loggers.add(loggingOutput);
    }

    private static void publish(LogRecord record) throws BenchmarkException {
        if (loggers == null) {
            prematureOutput.add(record);
        } else {
            for (LoggingOutput output : loggers) {
                output.log(record);
            }
        }
    }

    public static Logger getLogger() {
        return benchmarkLogger;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
