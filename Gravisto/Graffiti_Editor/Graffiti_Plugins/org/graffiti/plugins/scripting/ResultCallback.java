package org.graffiti.plugins.scripting;

/**
 * Classes implementing {@code ResultCallback} process the value or error
 * resulting from the execution of a script.
 * 
 * @author Andreas Glei&szlig;ner
 */
public interface ResultCallback {
    /**
     * Is called when the script evaluated to a value.
     */
    public void reportResult(String value);

    /**
     * Is called when the script successfully finished execution without
     * evaluating to a value.
     */
    public void reportResult();

    /**
     * Is called when an error occurred during script execution.
     */
    public void reportError(String message);
}
