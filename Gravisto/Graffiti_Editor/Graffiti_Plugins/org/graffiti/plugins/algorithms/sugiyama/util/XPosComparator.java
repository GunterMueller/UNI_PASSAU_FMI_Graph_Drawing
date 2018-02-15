// =============================================================================
//
//   XPosComparator.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: XPosComparator.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.util;

import java.util.Comparator;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Node;

/**
 * Utility-Class to compare nodes according to their sugiyama.xpos-attribute
 * 
 * @author Ferdinand H�bner
 */
public class XPosComparator implements Comparator<Node> {
    public int compare(Node a, Node b) {
        double xpos_a, xpos_b;
        try {
            xpos_a = a.getDouble(SugiyamaConstants.PATH_XPOS);
        } catch (AttributeNotFoundException anfe) {
            xpos_a = Double.MAX_VALUE;
        }
        try {
            xpos_b = b.getDouble(SugiyamaConstants.PATH_XPOS);
        } catch (AttributeNotFoundException anfe) {
            xpos_b = Double.MAX_VALUE;
        }
        return (new Double(xpos_a).compareTo(new Double(xpos_b)));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
