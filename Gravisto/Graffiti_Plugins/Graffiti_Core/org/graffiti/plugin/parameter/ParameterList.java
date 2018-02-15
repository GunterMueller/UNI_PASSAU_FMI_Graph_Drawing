// =============================================================================
//
//   ParameterList.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ParameterList.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.plugin.parameter;

/**
 * A <code>ParameterList</code> can be used to create enumeration type
 * parameters, by grouping them into a list. The list can contain any number of
 * <code>SingleParameters</code>.
 * 
 * @version $Revision: 5767 $
 * 
 * @see SingleParameter
 */
public interface ParameterList<T> extends Parameter<T> {

    /**
     * DOCUMENT ME!
     * 
     * @link aggregation
     */

    /* #SingleParameter lnkSingleParameter; */

    /**
     * Adds a <code>SingleParameter</code> to the list.
     * 
     * @param sp
     *            the <code>SingleParameter</code> to add to the list.
     */
    void addParameter(SingleParameter<T> sp);

    /**
     * Removes a <code>SingleParameter</code> from the list.
     * 
     * @param sp
     *            the <code>SingleParameter</code> to remove from the list.
     */
    void removeParameter(SingleParameter<T> sp);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
