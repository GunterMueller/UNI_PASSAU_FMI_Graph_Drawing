// =============================================================================
//
//   SamplerList.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.sampler;

import java.util.LinkedList;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class SamplerList extends LinkedList<Sampler> {
    /**
     * 
     */
    private static final long serialVersionUID = -3046744361744310152L;

    public SamplerList(Sampler sampler) {
        add(sampler);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
