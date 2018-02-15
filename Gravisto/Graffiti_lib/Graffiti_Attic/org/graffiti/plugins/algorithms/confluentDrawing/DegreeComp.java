// =============================================================================
//
//   DegreeComp.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DegreeComp.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.algorithms.confluentDrawing;

import java.util.Comparator;

import org.graffiti.graph.Node;

/**
 * DOCUMENT ME!
 * 
 * @author $Author: gleissner $
 * @version $Revision: 5772 $ $Date: 2007-07-18 16:12:43 +0200 (Mi, 18 Jul 2007)
 *          $
 */
public class DegreeComp implements Comparator {

    /** DOCUMENT ME! */
    public static final char ASC = 'a';

    /** DOCUMENT ME! */
    public static final char DES = 'd';

    /** DOCUMENT ME! */
    public static final char BICLIQUES = 'b';

    public static final char CLIQUES = 'c';

    public static final char INT = 'i';

    /**
     * if <code>true</code>, it flags ascending sort. if <code>false</code>, it
     * flags descending sort.
     */
    private char compFlag;

    /**
     * Creates a new DegreeComp object.
     * 
     * @param flag
     *            the flag of ascending or descending sort
     */
    public DegreeComp(char flag) {
        this.compFlag = flag;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return Returns the aSC.
     */
    public char getASC() {
        return ASC;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return Returns the compFlag.
     */
    public char getCompFlag() {
        return this.compFlag;
    }

    /*
     * 
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object o1, Object o2) {
        boolean less = false;

        switch (this.compFlag) {
        case 'a': {
            less = ((Node) o1).getInDegree() < ((Node) o2).getInDegree();

            break;
        }

        case 'd': {
            less = ((Node) o1).getOutDegree() > ((Node) o2).getOutDegree();

            break;
        }

        case 'b': {
            less = ((Biclique) o1).size() > ((Biclique) o2).size();

            break;
        }

        case 'c': {
            less = ((Clique) o1).size() > ((Clique) o2).size();

            break;
        }

        case 'i': {
            less = ((MyNode) o1).getNumber() > ((MyNode) o2).getNumber();
            break;
        }
        }

        if (less)
            return 1;
        else
            return -1;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
