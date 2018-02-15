// =============================================================================
//
//   SeedableAlgorithm.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark;

import org.graffiti.plugin.algorithm.Algorithm;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringParameter;

/**
 * Classes implementing {@code SeedableAlgorithm} provide a seed parameter for
 * the pseudorandom number generator.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public interface SeedableAlgorithm {
    /**
     * Returns which of the parameters specifies the seed.
     * 
     * @param parameters
     *            array of parameters that has been returned by
     *            {@link Algorithm#getParameters()}.
     * @return the parameter specifying the seed. It must be an element of the
     *         parameters array.
     */
    public StringParameter getSeedParameteer(Parameter<?>[] parameters);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
