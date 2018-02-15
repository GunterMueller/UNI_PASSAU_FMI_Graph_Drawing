// =============================================================================
//
//   FunctionComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: FunctionComponent.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced;

import java.awt.Point;

/**
 * A (GUI-)component which supports the concept of functions. FunctionComponents
 * are organized in a hierarchical structure, like civquest.quadmap.fieldview
 */
public interface FunctionComponent {

    /**
     * Returns a reference to a NEW Action-object assigned to the given
     * function-name, if this FunctionComponent knows about such a function
     * itself, null otherwise.
     * 
     * <p>
     * Note that subsequent calls to this function must return different
     * Action-objects!!! NOTE: If only a sub-function-component knows that
     * function, returning something != null is not required. However, in some
     * cases (field-views in CivQuest), a value != null will be returned in that
     * case.
     * </p>
     * 
     * @param functionName
     *            name of the function
     * 
     * @return reference to an appropriate Action-object as described, null in
     *         certain cases (see above)
     */
    public FunctionAction getFunctionAction(String functionName);

    /**
     * Returns an object with some position-information, including the last
     * mouse-position. Used for feeding FunctionActionEvents with
     * position-information.
     * 
     * @return object with position-information as described
     */
    public PositionInfo getPositionInfo();

    /**
     * Returns the direct sub-FunctionComponent with the given name (relative to
     * this FunctionComponent), if it exists, null otherwise. NOTE: The given
     * String may ONLY contain the subcomponent-prefix, not the whole
     * function-name ("civquest" vs. "civquest.quit").
     * 
     * @param name
     *            name of the sub-function-component to be returned
     * 
     * @return searched FunctionComponent if it exists, null otherwise
     */
    public FunctionComponent getSubComponent(String name);

    /**
     * Is called by the FunctionManager each time after an input-event was
     * processed. Processing means that all functions assigned to the event are
     * executed.
     * 
     * @param position
     *            DOCUMENT ME!
     */
    public void afterEvent(Point position);

    /**
     * Is called by the FunctionManager each time before an input-event is
     * processed. Processing means, that all functions assigned to the event are
     * executed.
     * 
     * @param position
     *            DOCUMENT ME!
     */
    public void beforeEvent(Point position);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
