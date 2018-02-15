// =============================================================================
//
//   IterationParser.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.chebyshev.iterations;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.graffiti.plugins.algorithms.chebyshev.AuxGraph;
import org.graffiti.plugins.algorithms.chebyshev.Step;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class IterationParser {
    private static Map<String, IterationGenerator> map;
    private static AuxGraph graph;

    private static void createMap() {
        map = new HashMap<String, IterationGenerator>();
        map.put("down", new DownGenerator());
        map.put("up", new UpGenerator());
        map.put("top.down", new TopDownGenerator());
        map.put("top.up", new TopUpGenerator());
        map.put("belowTop.down", new BelowTopDownGenerator());
    }

    public static LinkedList<Step> parse(String string, AuxGraph graph) {
        try {
            if (map == null) {
                createMap();
            }
            IterationParser.graph = graph;
            StepList result = (StepList) new Parser(new Scanner(string))
                    .parse().value;
            graph = null;
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    protected static StepList create(String id) {
        IterationGenerator generator = map.get(id);
        if (generator == null)
            throw new RuntimeException();
        if (graph == null)
            // Parser is not interested in the result, just checks syntax.
            return new StepList();
        else
            return generator.generate(graph);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
