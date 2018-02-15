// =============================================================================
//
//   TopologicalComparator.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: TopologicalComparator.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.decycling;

import java.util.Comparator;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;

/**
 * This class implements a Comparator to compare two <code>Nodes</code>
 * according to their topoligical numbering
 * 
 * @author Ferdinand H�bner
 * 
 */
public class TopologicalComparator implements Comparator<Node> {

    public int compare(Node a, Node b) {

        int topoA;
        int topoB;

        try {
            topoA = a.getInteger(SugiyamaConstants.PATH_TOPO);
            topoB = b.getInteger(SugiyamaConstants.PATH_TOPO);
        } catch (AttributeNotFoundException anfe) {
            return 0;
        }
        if (topoA < topoB)
            return -1;
        else if (topoA > topoB)
            return 1;
        else
            return 0;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
