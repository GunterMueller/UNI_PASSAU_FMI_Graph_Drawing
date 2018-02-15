// =============================================================================
//
//   NestingAlgorithm.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark;

import java.util.Map;

import org.graffiti.plugin.algorithm.Algorithm;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public interface NestingAlgorithm extends Algorithm {
    public void setNestedAlgorithms(Map<String, Algorithm> algorithms);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
