package org.graffiti.plugins.tools.math;


/**
 * @author Andreas Glei&szlig;ner
 */
public class KendallTau implements PermutationMetric {

    private static KendallTau singleton;
    
    public static KendallTau get() {
        if (singleton == null) {
            singleton = new KendallTau();
        }
        
        return singleton;
    }
    
    @Override
    public long getDistance(Permutation p1, Permutation p2) {
        int len = p1.getLength();
        if (len != p2.getLength()) throw new IllegalArgumentException();
        
        Permutation pi1 = p1.inverse();
        Permutation pi2 = p2.inverse();
        
        long result = 0;
        
        for (int j = 0; j < len; j++) {
            for (int i = 0; i < j; i++) {
                if ((pi1.get(i) - pi1.get(j)) * (pi2.get(i) - pi2.get(j)) < 0) {
                    result++;
                }
            }
        }
        
        return result;
    }
}
