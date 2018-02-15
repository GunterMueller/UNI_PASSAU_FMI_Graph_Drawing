package org.graffiti.plugins.algorithms.kandinsky;

import java.util.Comparator;

/**
 * This Comparator orders two NormNodes according to their relative
 * y-coordinates which were computed by the class CompactRepresentation.java.
 * Returns the ascending order.
 * 
 * @author Sonja Zur
 * @version $Revision$ $Date$
 */
public class YComparator implements Comparator<NormArc> {
    public int compare(NormArc o1, NormArc o2) {
        NormNode n1 = o1.getTo();
        NormNode n2 = o2.getTo();
        if (n1.getY() < n2.getY())
            return -1;
        else {
            if (n1.getY() > n2.getY())
                return +1;
            else
                return 0;
        }
    }
}
