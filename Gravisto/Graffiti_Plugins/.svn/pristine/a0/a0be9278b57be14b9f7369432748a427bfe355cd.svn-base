// =============================================================================
//
//   Heapsort.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treeWidth;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

/**
 * HeapSort class
 * 
 * @author wangq
 * @version $Revision: 1000
 */
public class Heapsort {

    /**
     * Creates a new HeapSort object.
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
    protected static ArrayList<Object> heapsort(ArrayList<Object> toSort,
            Comparator<Object> ordering) {
        ArrayList<Object> target = new ArrayList<Object>();

        if (toSort.isEmpty())
            return target;

        if (toSort.size() < 2) {
            Iterator<Object> itr = toSort.iterator();

            while (itr.hasNext()) {
                target.add(itr.next());
            }

            return target;
        }

        /** copy all list elements into an array */

        Object[] objArray = toSort.toArray();
        int oaSize = objArray.length;

        /** modify this array to match the heap conventions (heapify) */
        for (int parent = (oaSize - 1) / 2; parent >= 0; parent--) {
            heapify(objArray, ordering, parent, oaSize);
        }

        while (oaSize > 0) {
            target.add(objArray[0]);

            objArray[0] = objArray[--oaSize];
            heapify(objArray, ordering, 0, oaSize);
        }

        return target;
    }

    /**
     * heapSort method with ascending order.
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
    private static void heapify(Object[] objArray, Comparator<Object> ordering,
            int parent, int oaSize) {
        int largest = parent;
        int leftChild;
        int rightChild;
        Object aux;
        do {
            parent = largest;
            leftChild = (parent * 2) + 1;
            rightChild = (parent * 2) + 2;

            /** find largest of parent and both children */
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

            /**
             * if parent is not the largest element, make it the largest one
             */
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
