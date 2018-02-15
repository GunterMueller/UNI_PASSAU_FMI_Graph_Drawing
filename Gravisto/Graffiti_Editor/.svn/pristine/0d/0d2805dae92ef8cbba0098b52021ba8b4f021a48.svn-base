// =============================================================================
//
//   RecognizeInterval.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.interval;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.algorithm.PreconditionException;

/**
 * This class is used to manage the recognition of an interval graph.
 * 
 * @author struckmeier
 */
public class RecognizeInterval {

    private static final Logger logger = Logger
            .getLogger(RecognizeInterval.class.getName());

    /**
     * This method is used to call the different party of the recognition
     * algorithm.
     * 
     * @param g
     * @return a clique-chain of the graph if it is an interval graph.
     * @throws PreconditionException
     */
    public IntervalSets<CliqueSet> recognize(Graph g)
            throws PreconditionException {
        logger.log(Level.FINER, "called recognize ...");
        LexBFS lex = new LexBFS();
        LexBFSNode[] order = lex.getOrder(g);
        ComputeCliques comp = new ComputeCliques();
        CliqueObject cliques = comp.computeCliqueTree(order);
        CheckInterval check = new CheckInterval();
        IntervalSets<CliqueSet> cliqueSequence = check
                .createCliqueSequence(cliques);
        cliqueSequence.setNumberOfNodes(g.getNumberOfNodes());

        return cliqueSequence;

    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
