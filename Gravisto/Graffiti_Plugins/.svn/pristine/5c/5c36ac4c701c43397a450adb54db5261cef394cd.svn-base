// =============================================================================
//
//   GeneratorSource.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.source;

import java.util.Collection;
import java.util.Random;

import org.graffiti.graph.Graph;
import org.graffiti.plugins.tools.benchmark.Assignment;
import org.graffiti.plugins.tools.benchmark.BenchmarkException;
import org.graffiti.plugins.tools.benchmark.generators.DeterministicGraphGenerator;
import org.graffiti.plugins.tools.benchmark.sampler.AssignmentList;
import org.graffiti.plugins.tools.benchmark.sampler.SamplingException;
import org.graffiti.plugins.tools.benchmark.xml.FormatException;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class GeneratorSource extends AbstractGraphSource {
    private int quantity;
    private DeterministicGraphGenerator generator;

    public GeneratorSource(String className, int quantity,
            AssignmentList assignments) throws FormatException {
        this.quantity = quantity;

        try {
            generator = Class.forName(className).asSubclass(
                    DeterministicGraphGenerator.class).newInstance();
            generator.setAssignments(assignments);
        } catch (Exception e) {
            throw new FormatException("error.generatorClass", className);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void contribute(Collection<GraphFactory> collection,
            final Random random, final Assignment assignment)
            throws BenchmarkException {
        for (int i = 0; i < quantity; i++) {
            collection.add(new GraphFactory() {

                @Override
                protected Graph makeGraph() throws BenchmarkException {
                    try {
                        return generator.generate(random);
                    } catch (SamplingException e) {
                        throw new BenchmarkException(e);
                    }
                }

                @Override
                protected Assignment getAssignment() {
                    return assignment;
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void provideSourceInfo(SourceAttribute sourceAttribute) {
        //
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
