// =============================================================================
//
//   BoundAlgorithm.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.algorithm.Algorithm;
import org.graffiti.plugin.algorithm.AlgorithmResult;
import org.graffiti.plugin.algorithm.CalculatingAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringParameter;
import org.graffiti.plugins.tools.benchmark.body.BodyAlgorithm;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class BoundAlgorithm implements Seedable {
    protected Long fixedSeed;
    protected long actualSeed;
    protected String className;
    protected List<BoundParameter> boundParameters;
    protected Map<String, BodyAlgorithm> boundAlgorithmParameters;

    public BoundAlgorithm(String className) {
        this.className = className;
        boundParameters = new LinkedList<BoundParameter>();
        boundAlgorithmParameters = new HashMap<String, BodyAlgorithm>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setFixedSeed(long fixedSeed) {
        this.fixedSeed = fixedSeed;
    }

    public Algorithm create(Graph graph, Assignment assignment)
            throws BenchmarkException {
        try {
            Random random = new Random(actualSeed);
            Random paramRandom = new Random(random.nextLong());
            random.nextLong(); // sub algorithms

            Class<?> clazz = Class.forName(assignment.subst(className));
            if (!Algorithm.class.isAssignableFrom(clazz))
                throw new BenchmarkException("error.implementsAlgorithm", clazz
                        .getName());
            Algorithm algorithm = create(clazz);
            Parameter<?>[] parameters = algorithm.getParameters();

            if (algorithm instanceof SeedableAlgorithm) {
                StringParameter seedParameter = ((SeedableAlgorithm) algorithm)
                        .getSeedParameteer(parameters);
                seedParameter.setValue(String.valueOf(random.nextLong()));
            }

            for (BoundParameter boundParameter : boundParameters) {
                boundParameter.apply(parameters, assignment, paramRandom
                        .nextLong());
            }

            algorithm.setParameters(parameters);

            if (algorithm instanceof NestingAlgorithm) {
                Map<String, Algorithm> algorithms = new HashMap<String, Algorithm>();
                for (Map.Entry<String, BodyAlgorithm> entry : boundAlgorithmParameters
                        .entrySet()) {
                    algorithms.put(entry.getKey(), entry.getValue().create(
                            graph, assignment));
                }
                ((NestingAlgorithm) algorithm).setNestedAlgorithms(algorithms);
            } else if (!boundAlgorithmParameters.isEmpty())
                throw new BenchmarkException("error.nestedAlgorithms",
                        algorithm.getName());

            algorithm.attach(graph);
            return algorithm;
        } catch (ClassNotFoundException e) {
            throw new BenchmarkException(e);
        }
    }

    public AlgorithmResult execute(Graph graph, Assignment assignment)
            throws BenchmarkException {
        try {
            Algorithm algorithm = create(graph, assignment);
            algorithm.check();
            algorithm.execute();
            if (algorithm instanceof CalculatingAlgorithm)
                return ((CalculatingAlgorithm) algorithm).getResult();
            else
                return null;
        } catch (PreconditionException e) {
            throw new BenchmarkException(e);
        }
    }

    public void addParameter(BoundParameter parameter) {
        boundParameters.add(parameter);
    }

    public void addAlgorithmParameter(String name, BodyAlgorithm algorithm) {
        boundAlgorithmParameters.put(name, algorithm);
    }

    protected String findUnboundVariable(Assignment assignment)
            throws BenchmarkException {
        if (assignment.isUnassigned(className))
            return className;

        for (BoundParameter parameter : boundParameters) {
            String v = parameter.findUnboundVariable(assignment);
            if (v != null)
                return v;
        }

        for (BodyAlgorithm subAlgorithm : boundAlgorithmParameters.values()) {
            String v = subAlgorithm.findUnboundVariable(assignment);
            if (v != null)
                return v;
        }
        return null;
    }

    public void updateSeed(long seed) {
        actualSeed = fixedSeed == null ? seed : fixedSeed;
        Random random = new Random(actualSeed);
        random.nextLong(); // for boundParameters
        Random rand = new Random(random.nextLong());
        for (BoundAlgorithm subAlgorithm : boundAlgorithmParameters.values()) {
            subAlgorithm.updateSeed(rand.nextLong());
        }
    }

    protected Algorithm create(Class<?> clazz) throws BenchmarkException {
        try {
            Class<? extends Algorithm> algorithmClass = clazz
                    .asSubclass(Algorithm.class);
            return algorithmClass.newInstance();
        } catch (InstantiationException e) {
            throw new BenchmarkException(e);
        } catch (IllegalAccessException e) {
            throw new BenchmarkException(e);
        }
    }

    protected void prepareOutermost() {
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
