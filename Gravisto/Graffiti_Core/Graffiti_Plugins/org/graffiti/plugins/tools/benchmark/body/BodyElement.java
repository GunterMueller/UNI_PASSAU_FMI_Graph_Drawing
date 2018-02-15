// =============================================================================
//
//   BodyElement.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.body;

import org.graffiti.plugins.tools.benchmark.Assignment;
import org.graffiti.plugins.tools.benchmark.BenchmarkException;
import org.graffiti.plugins.tools.benchmark.Data;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public interface BodyElement {
    public void setNext(BodyElement nextElement) throws BenchmarkException;

    public void updateSeed(long seed);

    public void execute(Data data, Assignment assignment)
            throws BenchmarkException;
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
