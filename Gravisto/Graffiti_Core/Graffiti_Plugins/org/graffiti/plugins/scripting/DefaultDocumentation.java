package org.graffiti.plugins.scripting;

import org.graffiti.plugins.scripting.reflect.DocumentedDelegate;

/**
 * Uninstantiable placeholder class used as a parameter value in the {@code
 * DocumentedDelegate} annotation.
 * 
 * @author Andreas Glei&szlig;ner
 * @see DocumentedDelegate
 */
public class DefaultDocumentation {
    /**
     * Prevents instantiation.
     */
    private DefaultDocumentation() {
    }
}
