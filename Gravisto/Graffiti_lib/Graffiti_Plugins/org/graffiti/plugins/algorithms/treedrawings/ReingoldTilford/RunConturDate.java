// =============================================================================
//
//   RunConturDate.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: RunConturDate.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.treedrawings.ReingoldTilford;

/**
 * @author Beiqi
 * @version $Revision: 5766 $ $Date: 2006-07-03 22:10:05 +0200 (Mo, 03 Jul 2006)
 *          $
 */
public class RunConturDate {
    int sepBegin;
    int minSep;
    int i;
    int j;
    int lShiftSum;
    int rShiftSum;

    public RunConturDate() {

    }

    public RunConturDate(int sepBegin, int minSep, int i, int j, int lShiftSum,
            int rShiftSum) {
        this.sepBegin = sepBegin;
        this.minSep = minSep;
        this.i = i;
        this.j = j;
        this.lShiftSum = lShiftSum;
        this.rShiftSum = rShiftSum;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
