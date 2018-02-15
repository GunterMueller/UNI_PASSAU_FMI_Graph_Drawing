// =============================================================================
//
//   SpearmanFootrule.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.math;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class SpearmanFootrule implements PermutationMetric {

    private static SpearmanFootrule singleton;
    
    public static SpearmanFootrule get() {
        if (singleton == null) {
            singleton = new SpearmanFootrule();
        }
        
        return singleton;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public long getDistance(Permutation p1, Permutation p2) {
        int len = p1.getLength();
        if (len != p2.getLength()) throw new IllegalArgumentException();
        
        Permutation pi1 = p1.inverse();
        Permutation pi2 = p2.inverse();
        
        long result = 0;
        
        for (int i = 0; i < len; i++) {
            result += Math.abs(pi1.get(i) - pi2.get(i));
        }
        
        return result;
    }

}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
