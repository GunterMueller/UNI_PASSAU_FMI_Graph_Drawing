package org.graffiti.util.heap;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Simple implementation of the <tt>Heap</tt> interface designed for debugging
 * purposes.
 * <p>
 * 
 * @author Harald
 * @version $Revision$ $Date$
 * @param <E>
 * @param <K>
 */
public class DebugHeap<E, K> extends AbstractHeap<E, K> {

    private Comparator<? super K> comparator = null;

    private Entry<E, K> first = null;

    private int size = 0;

    public DebugHeap() {

    }

    public DebugHeap(Collection<E> c) {
        addAll(c);
    }

    public DebugHeap(Comparator<? super K> c) {
        this.comparator = c;
    }

    public DebugHeap(Heap<E, K> h) {
        for (Heap.Entry<E, K> e : h.entries()) {
            add(e.getElement(), e.getKey());
        }
    }

    @Override
    public Comparator<? super K> comparator() {
        return comparator;
    }

    @Override
    public Heap.Entry<E, K> add(E element, K key) {
        first = new Entry<E, K>(first, element, key);
        size++;
        return first;
    }

    @Override
    public Collection<Heap.Entry<E, K>> entries() {
        return new AbstractCollection<Heap.Entry<E, K>>() {
            public int size() {
                return size;
            }

            public Iterator<Heap.Entry<E, K>> iterator() {
                return new Iterator<Heap.Entry<E, K>>() {
                    private Entry<E, K> prev = null;

                    private Entry<E, K> curr = null;

                    private Entry<E, K> next = first;

                    public boolean hasNext() {
                        return next != null;
                    }

                    public Entry<E, K> next() {
                        if (next == null)
                            throw new NoSuchElementException();
                        prev = curr == null ? prev : curr;
                        curr = next;
                        next = next.next;
                        return curr;
                    }

                    public void remove() {
                        if (curr == null)
                            throw new IllegalStateException();
                        if (prev == null) {
                            first = next;
                        } else {
                            prev.next = next;
                        }
                        curr = null;
                        size--;
                    }
                };
            }
        };
    }

    private static class Entry<E, K> extends AbstractHeap.Entry<E, K> {
        private E element = null;

        private K key = null;

        private Entry<E, K> next = null;

        public Entry(Entry<E, K> next, E element, K key) {
            this.element = element;
            this.key = key;
            this.next = next;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public K getKey() {
            return key;
        }

        public E getElement() {
            return element;
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
