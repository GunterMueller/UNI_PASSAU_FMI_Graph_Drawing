package org.graffiti.util.ext;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Extension methods for {@link Exception}s.
 * 
 * @author Harald Frankenberger
 */
public class Throwables {

    private Throwables() {
    }

    /**
     * Returns this throwable as a {@link RuntimeException}.
     * 
     * @param this_
     *            this throwable
     * @return this throwable as a {@link RuntimeException}
     */
    public static RuntimeException asRuntimeException(Throwable this_) {
        final Throwable _this = this_;
        return new RuntimeException() {
            /**
             * 
             */
            private static final long serialVersionUID = -6194335184168899878L;

            @Override
            public boolean equals(Object obj) {
                return _this.equals(obj);
            }

            @Override
            public Throwable getCause() {
                return _this;
            }

            @Override
            public String getLocalizedMessage() {
                return _this.getLocalizedMessage();
            }

            @Override
            public String getMessage() {
                return _this.getMessage();
            }

            @Override
            public StackTraceElement[] getStackTrace() {
                return _this.getStackTrace();
            }

            @Override
            public int hashCode() {
                return _this.hashCode();
            }

            @Override
            public synchronized Throwable initCause(Throwable cause) {
                return _this.initCause(cause);
            }

            @Override
            public void printStackTrace() {
                _this.printStackTrace();
            }

            @Override
            public void printStackTrace(PrintStream s) {
                _this.printStackTrace(s);
            }

            @Override
            public void printStackTrace(PrintWriter s) {
                _this.printStackTrace(s);
            }

            @Override
            public void setStackTrace(StackTraceElement[] stackTrace) {
                _this.setStackTrace(stackTrace);
            }

            @Override
            public String toString() {
                return _this.toString();
            }
        };
    }

}
