// =============================================================================
//
//   BaryCenterNodeComparator.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: BaryCenterNodeComparator.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.crossmin;

import java.util.Comparator;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * This class implements a comparator to compare nodes n1 and n2.
 * <ul>
 * <li>n1 < n2, if
 * <ul>
 * <li>n1.barycenter < n2.barycenter
 * <li>if n1.barycenter == n2.barycenter, then n1.lex < n2.lex
 * </ul>
 * <li>n1 == n2, if
 * <ul>
 * <li>n1.barycenter = n2.barycenter
 * <li>n1.lex = n2.lex
 * </ul>
 * <li>n1 > n2, if
 * <ul>
 * <li>n1.barycenter > n2.barycenter
 * <li>if n1.barycenter == n2.barycenter, then n1.lex > n2.lex
 * </ul>
 * </ul>
 * n1.lex and n2.lex have to be stored as a String-Attribute on the node. As the
 * comparator cannot decide whether e.g. the barycenter-algorithm is currently
 * in its top-down or bottom-up iteration!
 */
public class BaryCenterNodeComparator implements Comparator<Node> {

    private float n1_barycenter;

    private float n2_barycenter;

    private String n1_lex;

    private String n2_lex;

    private SugiyamaData data;

    public BaryCenterNodeComparator(SugiyamaData d) {
        this.data = d;
    }

    /**
     * Compares two nodes
     */
    public int compare(Node n1, Node n2) {
        int lex1_size = 0;
        int lex2_size = 0;

        // Try to access the barycenter written on the node
        try {
            n1_barycenter = n1.getFloat(SugiyamaConstants.PATH_BARYCENTER);
        } catch (AttributeNotFoundException anfe) {
            n1_barycenter = Float.MAX_VALUE;
        }
        // Try to access the barycenter written on the node
        try {
            n2_barycenter = n2.getFloat(SugiyamaConstants.PATH_BARYCENTER);
        } catch (AttributeNotFoundException anfe) {
            n2_barycenter = Float.MAX_VALUE;
        }
        if (n1_barycenter < n2_barycenter)
            return -1;
        else if (n1_barycenter > n2_barycenter)
            return 1;
        else {
            // Try to access n.lex
            try {
                n1_lex = n1.getString(SugiyamaConstants.PATH_LEX);
                n2_lex = n2.getString(SugiyamaConstants.PATH_LEX);
                // if n.lex doesn't exist, we cannot compare the nodes, so we
                // just
                // guess that they are equal
            } catch (AttributeNotFoundException anfe) {
                n1_lex = null;
                n2_lex = null;
            }
            if (n1_lex == null || n2_lex == null)
                return 0;
            else {
                if (data.getAlternateLex()) {
                    for (int i = 0; i < n1_lex.length(); i++) {
                        if (n1_lex.charAt(i) == ',') {
                            lex1_size++;
                        }
                    }
                    for (int i = 0; i < n2_lex.length(); i++) {
                        if (n2_lex.charAt(i) == ',') {
                            lex2_size++;
                        }
                    }
                    if (lex1_size < lex2_size)
                        return -1;
                    else if (lex1_size > lex2_size)
                        return 1;
                    else
                        return n1_lex.compareTo(n2_lex);
                } else
                    return n1_lex.compareTo(n2_lex);
            }
        }
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
