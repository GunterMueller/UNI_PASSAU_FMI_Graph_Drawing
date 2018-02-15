// =============================================================================
//
//   MinMaxXPosition.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: MinMaxXPosition.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.treedrawings.ReingoldTilford;

/**
 * @author Beiqi
 * @version $Revision: 5766 $ $Date: 2006-07-03 22:10:05 +0200 (Mo, 03 Jul 2006)
 *          $
 */
public class MinMaxXPosition {
    // ConturElement position = new ConturElement();
    double xValue;
    double yValue;

    public MinMaxXPosition() {

    }

    public MinMaxXPosition(double xValue, double yValue) {
        this.xValue = xValue;
        this.yValue = yValue;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
