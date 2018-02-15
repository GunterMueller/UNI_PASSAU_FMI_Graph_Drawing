// =============================================================================
//
//   Parametrizable.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin;

import org.graffiti.plugin.parameter.Parameter;

/**
 * @author brunner
 * @version $Revision$ $Date$
 */
public interface Parametrizable {

    /**
     * Returns the name (id) of this <code>Parametrizable</code>.
     * 
     * @return DOCUMENT ME!
     */
    public String getName();

    /**
     * Sets the parameters for this <code>Parametrizable</code>. Must have the
     * same types and order as the array returned by <code>getParameter</code>.
     */
    public void setParameters(Parameter<?>[] params);

    /**
     * Returns a list of <code>Parameter</code> that are set for this
     * <code>Parametrizable</code>.
     * 
     * @return a collection of <code>Parameter</code> that are needed by the
     *         <code>Parametrizable</code>.
     */
    public Parameter<?>[] getParameters();

    /**
     * Returns the default parameters for this <code>Parametrizable</code>.
     * 
     * @return the parametrizable's default parameters
     */
    public Parameter<?>[] getDefaultParameters();

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
