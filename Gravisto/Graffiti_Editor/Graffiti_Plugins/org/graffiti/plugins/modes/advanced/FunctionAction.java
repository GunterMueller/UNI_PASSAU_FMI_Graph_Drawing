// =============================================================================
//
//   FunctionAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: FunctionAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced;

import java.util.Map;
import java.util.Set;

import javax.swing.Action;

/**
 * Implementation of Action used within the function-concept.
 */
public interface FunctionAction extends Action {

    /**
     * Returns a Map with valid parameters for the function. Keys:
     * Parameter-names (Strings). Values: Sets of parameter-values (Objects).
     * 
     * @return Map with valid parameters
     */
    public Map<String, Set<Object>> getValidParameters();

    /**
     * Specialised version of the usual actionPerformed-method known from the
     * Action-hierarchy. Used within the function-concept. Each implementor
     * should override it, _not_ the standard-actionPerformed-method.
     * 
     * @param e
     *            any FunctionActionEvent
     */
    public void actionPerformed(FunctionActionEvent e);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
