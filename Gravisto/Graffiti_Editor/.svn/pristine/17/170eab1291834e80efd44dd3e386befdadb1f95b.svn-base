// =============================================================================
//
//   Assignment.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.sampler;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class RandomAssignment {
    private String target;
    private Sampler sampler;
    private AssignmentList assignments;

    public RandomAssignment(String target, Sampler sampler) {
        this.target = target;
        this.sampler = sampler;
    }

    public RandomAssignment(String target, AssignmentList assignments) {
        this.target = target;
        this.assignments = assignments;
    }

    protected void apply(SamplingContext context) {
        if (sampler != null) {
            context.setVariable(target, sampler.sample(context));
        } else {
            context.addChild(target, assignments);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
