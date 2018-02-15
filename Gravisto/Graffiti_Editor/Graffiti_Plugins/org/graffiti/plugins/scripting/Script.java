package org.graffiti.plugins.scripting;

/**
 * Classes implementing {@code Script} represent an executable script.
 * 
 * @author Andreas Glei&szlig;ner
 */
public interface Script {
    /**
     * Executes this script. The results or errors are reported to the specified
     * callback.
     * 
     * @param callback
     *            callback that processes the result of the execution.
     */
    public void execute(ResultCallback callback);
}
