// =============================================================================
//
//   DummyDecycling.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DummyDecycling.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.decycling;

import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * This class is a dummy-implementation. If the user wants to keep the graph the
 * way he created it, this implementation only checks, if the following is true:
 * <ul>
 * <li>The <tt>Attribute</tt> <tt>sugiyama.hasBeenDecycled</tt> does exist and
 * is set to <tt>true</tt>.
 * <li>The <tt>Graph</tt> as acyclic
 * </ul>
 * 
 * @author Ferdinand H&uuml;bner
 */
public class DummyDecycling extends AbstractAlgorithm implements
        DecyclingAlgorithm {
    private final String ALGORITHM_NAME = "Dummy-Decycling (Only checks if the graph is acyclic)";

    private SugiyamaData data;

    public void undo() {
    }

    @Override
    public void check() {
    }

    /**
     * Execute the algorithm: Check for cycles in the graph and for the
     * hasBeenDecycled-Bit in the graph. If the graph is acyclic, the bit is set
     * by the algorithm
     * 
     * @todo Problems with big graphs - TopoSort loops forever
     */
    public void execute() {
        // HashSet<String> messages = new HashSet<String>();
        // boolean errors = false;
        //
        // // Run topoSort to check for a cycle in the graph
        // TopoSort tSort = new TopoSort();
        // tSort.attach(this.graph);
        // tSort.setData(this.data);
        // try
        // {
        // tSort.execute();
        // try
        // {
        // graph.setBoolean(SugiyamaConstants.PATH_HASBEENDECYCLED, true);
        // }
        // catch(AttributeNotFoundException anfe)
        // {
        // graph.addBoolean(SugiyamaConstants.PATH_SUGIYAMA,
        // SugiyamaConstants.SUBPATH_HASBEENDECYCLED, true);
        // }
        // catch(Exception e)
        // {
        //                
        // }
        // }
        // catch (RuntimeException rte)
        // {
        // messages.add("The graph contains at least one cycle.");
        // errors = true;
        // }
        //
        // if (errors)
        // {
        // String message = "Cannot proceed due to the following errors:\n\n";
        // Iterator<String> iter = messages.iterator();
        // while (iter.hasNext())
        // {
        // message += "- " + iter.next() + "\n";
        // }
        // throw new RuntimeException(message);
        //
        // }

    }

    public String getName() {
        return this.ALGORITHM_NAME;
    }

    public SugiyamaData getData() {
        return this.data;
    }

    public void setData(SugiyamaData data) {
        this.data = data;
    }

    public boolean supportsBigNodes() {
        return true;
    }

    public boolean supportsConstraints() {
        return true;
    }

    public boolean supportsAlgorithmType(String algorithmType) {
        return algorithmType
                .equals(SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA)
                || algorithmType
                        .equals(SugiyamaConstants.PARAM_RADIAL_SUGIYAMA);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
