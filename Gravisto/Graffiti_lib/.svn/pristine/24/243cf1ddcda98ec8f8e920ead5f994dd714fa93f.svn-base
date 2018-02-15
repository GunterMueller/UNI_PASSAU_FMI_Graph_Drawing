// =============================================================================
//
//   InitialLPLevelling.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.gridsifting.initiallevelling;

import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.sugiyama.levelling.LPLevelling;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class InitialLPLevelling extends InitialProxyLevelling<LPLevelling> {
    public InitialLPLevelling() {
        super(new LPLevelling());
    }

    @Override
    protected void setParameters() {
        Parameter<?>[] parameters = algorithm.getParameters();
        ((IntegerParameter) (parameters[0])).setValue(initialMaxLevelWidth);
        algorithm.setParameters(parameters);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
