// =============================================================================
//
//   SourceTransformation.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.source;

import org.graffiti.attributes.LongAttribute;
import org.graffiti.attributes.StringAttribute;
import org.graffiti.graph.Graph;
import org.graffiti.plugins.tools.benchmark.Assignment;
import org.graffiti.plugins.tools.benchmark.AttributeUtil;
import org.graffiti.plugins.tools.benchmark.BenchmarkException;
import org.graffiti.plugins.tools.benchmark.BoundAlgorithm;
import org.graffiti.util.VoidCallback;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class SourceTransformation extends BoundAlgorithm {
    public SourceTransformation(String className) {
        super(className);
    }

    public void execute(Graph graph, final long seed, Assignment assignment)
            throws BenchmarkException {
        updateSeed(seed);
        execute(graph, assignment);

        AttributeUtil.addSourceAttribute(graph, false,
                new VoidCallback<SourceAttribute>() {
                    @Override
                    public void call(SourceAttribute sourceAttribute) {
                        sourceAttribute.add(new LongAttribute("seed",
                                actualSeed));
                        sourceAttribute.add(new StringAttribute("class",
                                className));
                    }
                });
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
