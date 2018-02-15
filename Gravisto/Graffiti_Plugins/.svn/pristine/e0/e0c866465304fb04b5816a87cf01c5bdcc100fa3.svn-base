// =============================================================================
//
//   ObjectReferenceComparator.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.util;

import java.lang.ref.WeakReference;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * The {@code ObjectReferenceComparator} guarantees to impose a total ordering
 * for every type {@code T}. The objects are primarily ordered by their hash
 * code. If for some objects {@code a} and {@code b}, {@code a.hashCode() ==
 * b.hashCode()}, but {@code a != b}, these objects are held in a
 * {@link LinkedList} and ordered by their position in that list. Different
 * instances of {@link ObjectReferenceComparator} may impose different total
 * orderings. The objects are only weakly referenced from the list, so that
 * having been compared by this comparator does not prevent them from being
 * finalized by the garbage collector.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see WeakReference
 */
public class ObjectReferenceComparator<T> implements Comparator<T> {
    /**
     * {@code WeakReference}, which removes itself from the {@code
     * duplicatesMap} if the referenced object is finalized.
     * 
     * @author Andreas Glei&szlig;ner
     * @version $Revision$ $Date$
     */
    private class ListedWeakReference extends WeakReference<T> {
        public ListedWeakReference(T t) {
            super(t);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void clear() {
            vanish();
            super.clear();
        }

        /**
         * Removes this reference from the list and potentially the list from
         * the duplicates map.
         */
        private void vanish() {
            T t = get();
            if (t == null)
                return;
            int hashCode = t.hashCode();
            duplicatesMap.get(hashCode);
            LinkedList<ListedWeakReference> list = duplicatesMap.get(hashCode);
            if (list != null) {
                Iterator<ListedWeakReference> iter = list.iterator();
                while (iter.hasNext()) {
                    ListedWeakReference lwr = iter.next();
                    T u = lwr.get();
                    if (u == t) {
                        iter.remove();
                        break;
                    }
                }
                if (list.isEmpty()) {
                    duplicatesMap.remove(hashCode);
                    if (duplicatesMap.isEmpty()) {
                        duplicatesMap = null;
                    }
                }
            }
        }
    };

    private HashMap<Integer, LinkedList<ListedWeakReference>> duplicatesMap;

    /**
     * Compares its two arguments by their hashcode or, if both arguments share
     * the same hashcode, by their position in a {@link LinkedList} maintained
     * by this comparator.
     * 
     * @param arg0
     *            the first object to be compared.
     * @param arg1
     *            the second object to be compared.
     */
    public int compare(T arg0, T arg1) {
        int h0 = arg0.hashCode();
        int h1 = arg1.hashCode();
        if (h0 < h1)
            return -1;
        if (h0 > h1)
            return 1;
        if (arg0 == arg1)
            return 0;
        // Different objects with same hashcode.
        if (duplicatesMap == null) {
            duplicatesMap = new HashMap<Integer, LinkedList<ListedWeakReference>>();
        }
        LinkedList<ListedWeakReference> list = duplicatesMap.get(h0);
        if (list == null) {
            list = new LinkedList<ListedWeakReference>();
            duplicatesMap.put(h0, list);
        } else {
            for (ListedWeakReference lwr : list) {
                T t = lwr.get();
                if (t == null) {
                    continue;
                }
                if (t == arg0)
                    return -1;
                if (t == arg1)
                    return 1;
            }
        }
        list.addLast(new ListedWeakReference(arg0));
        list.addLast(new ListedWeakReference(arg1));
        return -1;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
