package org.graffiti.plugins.scripting.js;

/**
 * The {@code ScriptingTimeoutError} is thrown when the the script execution
 * time exceeds a certain timeout period. It is thrown in order to terminate the
 * script.
 * 
 * @author Andreas Glei&szlig;ner
 * @see RhinoContextFactory
 */
public class ScriptingTimeoutError extends Error {
    /**
     * 
     */
    private static final long serialVersionUID = 381291049361423163L;
    /**
     * The elapsed execution time.
     */
    private double elapsedTime;

    /**
     * Constructs a scripting timeout error.
     * 
     * @param elapsedTime
     *            the execution time of the script in seconds.
     */
    public ScriptingTimeoutError(double elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    /**
     * Returns the execution time of the script.
     * 
     * @return the execution time in of the script in seconds.
     */
    public double getElapsedTime() {
        return elapsedTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return JavaScriptPlugin.format("error.timeout", elapsedTime);
    }
}
