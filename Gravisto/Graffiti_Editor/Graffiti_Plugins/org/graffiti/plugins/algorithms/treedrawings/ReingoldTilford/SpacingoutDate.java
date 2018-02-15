// =============================================================================
//
//   SpacingoutDate.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SpacingoutDate.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.treedrawings.ReingoldTilford;

/**
 * @author Beiqi
 * @version $Revision: 5766 $ $Date: 2006-07-03 22:10:05 +0200 (Mo, 03 Jul 2006)
 *          $
 */
public class SpacingoutDate {
    int defaultAncestor;
    double[] shiftArray = new double[1];
    double[] changeArray = new double[1];

    public SpacingoutDate() {

    }

    public SpacingoutDate(int defaultAncestor, double[] shiftArray,
            double[] changeArray) {
        this.defaultAncestor = defaultAncestor;
        this.shiftArray = shiftArray;
        this.changeArray = changeArray;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
