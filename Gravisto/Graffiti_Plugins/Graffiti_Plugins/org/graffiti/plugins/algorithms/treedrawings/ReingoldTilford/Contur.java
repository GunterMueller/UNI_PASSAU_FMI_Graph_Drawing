// =============================================================================
//
//   Contur.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Contur.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.treedrawings.ReingoldTilford;

import java.util.LinkedList;

/**
 * @author Beiqi
 * @version $Revision: 5766 $ $Date: 2010-05-07 19:21:40 +0200 (Fr, 07 Mai 2010)
 *          $
 * 
 *          Create a Contur class
 */
public class Contur {
    private LinkedList<ConturElement> c;

    public Contur() {
        c = new LinkedList<ConturElement>();
    }

    public LinkedList<ConturElement> getContur() {
        return c;
    }

    public void setContur(LinkedList<ConturElement> c) {
        this.c = c;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
