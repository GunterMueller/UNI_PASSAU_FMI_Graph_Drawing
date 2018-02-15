// =============================================================================
//
//   CombinationList.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.stackqueuelayout;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class CombinationList {
    private int k;
    private int n;
    private int[] array;
    
    public CombinationList(int k, int n) {
        this.k = k;
        this.n = n;
        array = new int[k];
    }
    
    public void enumerate() {
        enumerate(0, 0);
    }
    
    private void enumerate(int index, int lowVal) {
        if (index >= k) {
            visit(transform());
        } else {
            for (int i = lowVal; i < n; i++) {
                array[index] = i;
                enumerate(index + 1, i + 1);
            }
        }
    }
    
    private boolean[] transform() {
        boolean[] result = new boolean[n];
        
        for (int i = 0; i < k; i++) {
            result[array[i]] = true;
        }
        
        return result;
    }
    
    protected abstract void visit(boolean[] array);
}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
