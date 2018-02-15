// =============================================================================
//
//   DegreeComp.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treeWidth;

import java.util.Comparator;

/**
 * An Implementation for the different comparison.
 * 
 * @author $Author: wangq $
 * @version $Revision: 1000
 */

public class DegreeComp implements Comparator<Object> {

    /** DOCUMENT ME! */
    public static final char DEG = 'a';

    public static final char LACK = 'i';

    public static final char LACKNEIGH = 'b';

    public static final char NEIGHSIZE = 's';

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
     * @return Returns the DEG.
     */
    public char getDEG() {
        return DEG;
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
            less = ((SuperNode) o1).getDegree() < ((SuperNode) o2).getDegree();

            break;
        }

        case 'i': {
            less = ((SuperNode) o1).getLackOfEdgeSim() < ((SuperNode) o2)
                    .getLackOfEdgeSim();

            break;
        }

        case 'b': {
            less = ((SuperNode) o1).getLackOfEdgeFaSim() < ((SuperNode) o2)
                    .getLackOfEdgeFaSim();

            break;
        }
        case 's': {
            less = ((SuperNode) o1).getNodeSizeInClique() < ((SuperNode) o2)
                    .getNodeSizeInClique();

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
