// =============================================================================
//
//   Heapsort.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Heapsort.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.algorithms.confluentDrawing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Heapsort class
 * 
 * @author Xiaolei Zhang
 * @version $Revision: 5772 $ $Date: 2007-07-18 16:12:43 +0200 (Mi, 18 Jul 2007)
 *          $
 */
public class Heapsort {

    /**
     * Creates a new Heapsort object.
     */
    public Heapsort() {
    }

    /**
     * heap sort with ascending order.
     * 
     * @param toSort
     *            the collection to sort
     * @param ordering
     *            the sort order
     * 
     * @return the sorted collection
     */
    protected static ArrayList heapsort(Collection toSort, Comparator ordering) {
        ArrayList target = new ArrayList();

        if (toSort.isEmpty())
            return target;

        // in the 0 position add the null so that the collection index begins
        // from 1
        if (((DegreeComp) ordering).getCompFlag() == ((DegreeComp) ordering)
                .getASC()) {
            target.add(null);
        }

        // target.ensureCapacity(2);
        // less than two items to sort?
        if (toSort.size() < 2) {
            Iterator itr = toSort.iterator();

            while (itr.hasNext()) {
                target.add(itr.next());
            }

            return target;
        }

        // copy all list elements into an array
        Object[] objArray = toSort.toArray();
        int oaSize = objArray.length;

        // modify this array to match the heap conventions (heapify)
        for (int parent = (oaSize - 1) / 2; parent >= 0; parent--) {
            heapify(objArray, ordering, parent, oaSize);
        }

        // toSort.clear();
        while (oaSize > 0) {
            if (((DegreeComp) ordering).getCompFlag() == ((DegreeComp) ordering)
                    .getASC()) {
                target.add(1, objArray[0]);
            } else {
                target.add(objArray[0]);
            }

            objArray[0] = objArray[--oaSize];
            heapify(objArray, ordering, 0, oaSize);
        }

        // target.add(0, null);
        return target;
    }

    /**
     * heapsort methode with ascending order.
     * 
     * @param objArray
     *            the collection to sort
     * @param ordering
     *            with it sort the collection
     * @param parent
     *            parent element
     * @param oaSize
     *            heap size
     */
    private static void heapify(Object[] objArray, Comparator ordering,
            int parent, int oaSize) {
        int largest = parent;
        int leftChild;
        int rightChild;
        Object aux;

        do {
            parent = largest;
            leftChild = (parent * 2) + 1;
            rightChild = (parent * 2) + 2;

            // find largest of parent and both childs
            if ((leftChild < oaSize)
                    && (ordering.compare(objArray[leftChild], objArray[parent]) > 0)) {
                largest = leftChild;
            } else {
                largest = parent;
            }

            if ((rightChild < oaSize)
                    && (ordering.compare(objArray[rightChild],
                            objArray[largest]) > 0)) {
                largest = rightChild;
            }

            // if parent is not the largest element,
            // make it the largest one
            if (parent != largest) {
                aux = objArray[parent];
                objArray[parent] = objArray[largest];
                objArray[largest] = aux;
            }
        } while (largest != parent);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
