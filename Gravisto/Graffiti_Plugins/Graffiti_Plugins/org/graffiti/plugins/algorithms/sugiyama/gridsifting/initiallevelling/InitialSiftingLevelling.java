// =============================================================================
//
//   InitialSiftingLevelling.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.gridsifting.initiallevelling;

import org.graffiti.plugins.algorithms.sugiyama.levelling.SiftingLevelling;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class InitialSiftingLevelling extends
        InitialProxyLevelling<SiftingLevelling> {
    public InitialSiftingLevelling() {
        super(new SiftingLevelling());
    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.sugiyama.universalsifting.initiallevelling
     * .InitialProxyLevelling#setParameters()
     */
    @Override
    protected void setParameters() {
        /*
         * Parameter<?>[] parameters = algorithm.getParameters();
         * ((IntegerParameter) (parameters[0])).setValue(initialMaxLevelWidth);
         * algorithm.setParameters(parameters);
         */
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
