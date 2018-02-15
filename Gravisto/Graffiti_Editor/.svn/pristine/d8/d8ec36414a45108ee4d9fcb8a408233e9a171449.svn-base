package quoggles.auxiliary;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Provides instances of <code>Comparator</code> that take two
 * <code>Number</code>s and compare them according to the type of each
 * <code>Comparator</code>.
 */
public class Comparators {

    /**
     * Returns an instance of <code>Comparator</code> that takes two 
     * <code>Number</code>s and compares them according to their integer 
     * representation.
     * 
     * @return
     */
    public final static Comparator getIntegerComparator() {
        return new IntComparator();
    }

    /**
     * Returns an instance of <code>Comparator</code> that takes two 
     * <code>Number</code>s and compares them according to their double 
     * representation.
     * 
     * @return
     */
    public final static Comparator getFloatingComparator() {
        return new FloatComparator();
    }

    /**
     * Returns an instance of <code>Comparator</code> that takes two 
     * <code>Number</code>s and compares them according to their double 
     * representation.
     * 
     * @return
     */
    public final static Comparator getStringComparator() {
        return new StringComparator();
    }


    private static final class IntComparator implements Comparator {

        /**
         * @see java.util.Comparator#compare(java.lang.Object,java.lang.Object)
         */
        public int compare(Object o1, Object o2) {
            Number n1 = (Number)o1;
            Number n2 = (Number)o2;
            return new Long(n1.longValue())
                .compareTo(new Long(n2.longValue()));
        }
    }

    private static final class StringComparator implements Comparator {

        /**
         * @see java.util.Comparator#compare(java.lang.Object,java.lang.Object)
         */
        public int compare(Object o1, Object o2) {
            return ((String)o1).compareTo((String)o2);
        }
    }

    private static final class FloatComparator implements Comparator {

        /**
         * @see java.util.Comparator#compare(java.lang.Object,java.lang.Object)
         */
        public int compare(Object o1, Object o2) {
            Number n1 = (Number)o1;
            Number n2 = (Number)o2;
            return new Double(n1.doubleValue())
                .compareTo(new Double(n2.doubleValue()));
        }
    }
    
    /**
     * Used to compare a table (list of lists) according to the first
     * <code>sortByNr</code> columns.
     */
    public static final class TableComparator implements Comparator {

        private int sortByNr;
        
        
        public TableComparator(int sortByNr) {
            this.sortByNr = sortByNr;
        }
        

        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object o1, Object o2) {
            List c1sub = ((List)o1).subList(0, sortByNr);
            List c2sub = ((List)o2).subList(0, sortByNr);
            
            return listCompare(c1sub, c2sub);
        }
        
        
        /**
         * Iterates through the lists (both same size). First element 
         * comparison that yields a value other than zero sets the result of
         * this comparison.
         * If all comparisons yield zero, the two lists are considered equal.
         * 
         * @param l1
         * @param l2
         * 
         * @return according to <code>Comparator</code> interface
         */
        private int listCompare(List l1, List l2) {
            Iterator it2 = l2.iterator();
            for (Iterator it = l1.iterator(); it.hasNext();) {
                Object el1 = it.next();
                Object el2 = it2.next();
                int elComp = elemCompare(el1, el2);
                if (elComp != 0) {
                    return elComp;
                }
            }
            return 0;
        }
        
        /**
         * Compares two elements according to the natural order. If an element
         * does not implement interface <code>Comparable</code>, the 
         * <code>toString()</code> method is used.
         * 
         * @param el1
         * @param el2
         * 
         * @return <code>el1.compareTo(el2)</code>
         */
        private int elemCompare(Object el1, Object el2) {
            if (!(el1 instanceof Comparable)) {
                el1 = el1.toString();
            }
            if (!(el2 instanceof Comparable)) {
                el2 = el2.toString();
            }
            
            return ((Comparable)el1).compareTo(el2);
        }
    }
}
