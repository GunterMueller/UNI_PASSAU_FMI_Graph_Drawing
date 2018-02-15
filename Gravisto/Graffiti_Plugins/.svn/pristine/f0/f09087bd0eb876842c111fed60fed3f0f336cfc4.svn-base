// =============================================================================
//
//   SamplerContext.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.sampler;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class SamplingContext {
    private Map<String, Double> variables;
    private Map<String, AssignmentList> children;
    private Random random;

    protected SamplingContext(AssignmentList assignments,
            Map<String, Double> inheritedVariables, Random random) {
        this.random = random;
        variables = new HashMap<String, Double>(inheritedVariables);
        children = new HashMap<String, AssignmentList>();
        for (RandomAssignment assignment : assignments) {
            assignment.apply(this);
        }
    }

    public double call(String id, double[] arguments) {
        Function function = Function.get(id);
        if (function != null)
            return function.eval(arguments);
        DistributionFactory factory = DistributionFactory.get(id);
        if (factory != null)
            return factory.createDistribution(arguments).sample(random)
                    .doubleValue();
        throw new SamplingException("error.noFunctionOrDistribution");
    }

    public Double getVariable(String variableName) {
        Double value = variables.get(variableName);
        if (value == null)
            throw new SamplingException("error.unassignedVariable",
                    variableName);
        else
            return value;
    }

    public void setVariable(String variableName, double value) {
        variables.put(variableName, value);
    }

    public void addChild(String id, AssignmentList assignments) {
        children.put(id, assignments);
    }

    public Random getRandom() {
        return random;
    }

    public SamplingContext createContext(String id) {
        return children.get(id).createContext(variables, random);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
