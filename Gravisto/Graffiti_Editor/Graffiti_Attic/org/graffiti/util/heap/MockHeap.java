// =============================================================================
//
//   MockHeap.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.util.heap;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

/**
 * @author Harald
 * @version $Revision$ $Date$
 */
public class MockHeap extends AbstractHeap<Integer, Integer> {

    /*
     * @see org.graffiti.util.heap.AbstractHeap#comparator()
     */
    @Override
    public Comparator<? super Integer> comparator() {
        return null;
    }

    /*
     * @see org.graffiti.util.heap.AbstractHeap#entries()
     */
    @Override
    public Collection<org.graffiti.plugins.algorithms.mst.Heap.Entry<Integer, Integer>> entries() {
        final Entry[] entries = new Entry[] { new Entry(0), new Entry(9) };
        return new java.util.AbstractCollection<Heap.Entry<Integer, Integer>>() {
            public int size() {
                return entries.length;
            }

            public Iterator<Heap.Entry<Integer, Integer>> iterator() {
                return new Iterator<Heap.Entry<Integer, Integer>>() {
                    int next = -1;

                    public boolean hasNext() {
                        return next + 1 < entries.length;
                    }

                    public Heap.Entry<Integer, Integer> next() {
                        if (next + 1 >= entries.length)
                            throw new java.util.NoSuchElementException();
                        next++;
                        return entries[next];
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    private class Entry extends AbstractHeap.Entry<Integer, Integer> {

        private int elem = -1;

        Entry(int i) {
            elem = i;
        }

        /*
         * @see org.graffiti.util.heap.AbstractHeap.Entry#getElement()
         */
        @Override
        public Integer getElement() {
            return elem;
        }

        /*
         * @see org.graffiti.util.heap.AbstractHeap.Entry#getKey()
         */
        @Override
        public Integer getKey() {
            return elem;
        }

        /*
         * @see
         * org.graffiti.util.heap.AbstractHeap.Entry#setKey(java.lang.Object)
         */
        @Override
        public void setKey(Integer key) {
            elem = key;
        }

    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
