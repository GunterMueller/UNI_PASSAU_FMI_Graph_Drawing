// =============================================================================
//
//   TestdataIterator.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.cyclicLeveling.test;

import java.io.File;
import java.util.Iterator;

/**
 * @author Gerg�
 * @version $Revision$ $Date$
 */
public class TestdataIterator implements Iterator<String> {

    String[] entries;
    int current;

    public TestdataIterator() {
        entries = new File("c:/graphs/").list();
        current = -1;
    }

    /*
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        if (current < (entries.length - 1))
            return true;
        else
            return false;
    }

    /*
     * @see java.util.Iterator#next()
     */
    public String next() {
        current++;
        return entries[current];

    }

    /*
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
