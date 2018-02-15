package org.graffiti.plugins.scripting;

import org.graffiti.plugins.scripting.delegates.MenuDelegate;
import org.graffiti.plugins.scripting.delegates.ProgramDelegate;

/**
 * Scope providing the program delegate.
 * 
 * @author Andreas Glei&szlig;ner
 * @see ProgramDelegate
 */
public class ProgramScope extends Scope {
    /**
     * Constructs a program scope with the specified parent.
     * 
     * @param basicScope
     *            the parent of the scope to construct.
     */
    ProgramScope(BasicScope basicScope) {
        super(basicScope);

        addDelegateClass(MenuDelegate.class);

        putConst("program", new ProgramDelegate(this));
    }
}
