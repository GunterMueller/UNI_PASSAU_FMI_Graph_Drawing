// =============================================================================
//
//   ChildrenComparator.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ChildrenComparator.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.treedrawings.ReingoldTilford;

import java.util.Comparator;

/**
 * @author Beiqi
 * @version $Revision: 5766 $ $Date: 2010-05-07 19:21:40 +0200 (Fr, 07 Mai 2010)
 *          $
 */
public class ChildrenComparator implements Comparator<Object> {
    int number;
    RTNode node1, node2;

    public int compare(Object obj1, Object obj2) {
        RTNode node1 = (RTNode) obj1;
        RTNode node2 = (RTNode) obj2;

        ConturElement cf1 = node1.getLeftContur().getContur().getFirst();
        ConturElement cl1 = node1.getLeftContur().getContur().getLast();
        ConturElement cf2 = node2.getLeftContur().getContur().getFirst();
        ConturElement cl2 = node2.getLeftContur().getContur().getLast();

        number = new Double(cl1.getY2() - cf1.getY1()).compareTo(new Double(cl2
                .getY2()
                - cf2.getY1()));

        if (number == 0) {
            number = new Double(node1.getMaxXP().xValue
                    - node1.getMinXP().xValue).compareTo(new Double(node2
                    .getMaxXP().xValue
                    - node2.getMinXP().xValue));
        }

        return number;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
