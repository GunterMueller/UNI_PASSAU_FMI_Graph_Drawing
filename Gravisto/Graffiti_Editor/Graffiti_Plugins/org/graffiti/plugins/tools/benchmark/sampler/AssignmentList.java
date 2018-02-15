// =============================================================================
//
//   AssignmentList.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.sampler;

import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class AssignmentList extends LinkedList<RandomAssignment> {
    /**
     * 
     */
    private static final long serialVersionUID = 640970715941971312L;

    public AssignmentList() {
    }

    public AssignmentList(RandomAssignment assignment) {
        add(assignment);
    }

    public SamplingContext createContext(
            Map<String, Double> inheritedVariables, Random random) {
        return new SamplingContext(this, inheritedVariables, random);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
