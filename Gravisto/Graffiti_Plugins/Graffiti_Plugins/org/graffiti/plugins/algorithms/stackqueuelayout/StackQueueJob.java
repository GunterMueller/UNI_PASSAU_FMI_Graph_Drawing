// =============================================================================
//
//   StackQueueJob.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.stackqueuelayout;

import java.util.List;

import javax.swing.SwingUtilities;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class StackQueueJob {
    private StackQueueSat sat;
    private StackQueueButton button;

    public StackQueueJob(Graph graph, List<Node> nodes, List<Edge> edges,
            int stackCount, int queueCount, StackQueueButton button) {
        sat = new StackQueueSat(graph, nodes, edges, stackCount, queueCount);
        this.button = button;
    }

    public void execute() throws InterruptedException {
        final boolean isFeasible = sat.execute();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                button.set(true, isFeasible ? sat : null);
            }
        });
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
