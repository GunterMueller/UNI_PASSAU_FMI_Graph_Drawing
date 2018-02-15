package org.graffiti.plugins.algorithms.mst;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class provides a skeleton implementation of the <tt>Heap</tt> interface
 * to minimize the effort required to implement this interface.
 * <p>
 * 
 * @author Harald
 * @version $Revision$ $Date$
 * @param <E>
 * @param <K>
 */
public abstract class AbstractHeap<E, K> extends AbstractCollection<E>
        implements Heap<E, K> {

    protected AbstractHeap() {
    }

    @Override
    public int size() {
        return entries().size();
    }

    public abstract Comparator<? super K> comparator();

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            Iterator<Heap.Entry<E, K>> i = entries().iterator();

            public boolean hasNext() {
                return i.hasNext();
            }

            public E next() {
                return i.next().getElement();
            }

            public void remove() {
                i.remove();
            }
        };
    }

    @SuppressWarnings("unchecked")
    public E getPeek() {
        if (isEmpty())
            throw new NoSuchElementException();
        if (comparator() == null) {
            Iterator<Heap.Entry<E, K>> i = entries().iterator();
            Heap.Entry<E, K> peek = i.next();
            Comparable peekKey = (Comparable) peek.getKey();
            if (peekKey == null)
                return peek.getElement();
            while (i.hasNext()) {
                Heap.Entry<E, K> next = i.next();
                Comparable nextKey = (Comparable) next.getKey();
                if (nextKey == null)
                    return next.getElement();
                else {
                    int nextComparedToPeek = (nextKey).compareTo(peekKey);
                    if (nextComparedToPeek < 0) {
                        peek = next;
                        peekKey = nextKey;
                    }
                }
            }
            return peek.getElement();
        } else {
            Iterator<Heap.Entry<E, K>> i = entries().iterator();
            Heap.Entry<E, K> peek = i.next();
            K peekKey = peek.getKey();
            if (peekKey == null)
                return peek.getElement();
            while (i.hasNext()) {
                Heap.Entry<E, K> next = i.next();
                K nextKey = next.getKey();
                if (peekKey == null)
                    return peek.getElement();
                else if (nextKey == null)
                    return next.getElement();
                else {
                    int nextCompareToPeek = comparator().compare(nextKey,
                            peekKey);
                    if (nextCompareToPeek < 0) {
                        peek = next;
                    }
                }
            }
            return peek.getElement();
        }
    }

    @Override
    public boolean add(E element) {
        add(element, null);
        return true;
    }

    public Heap.Entry<E, K> add(E element, K key) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    public E removePeek() {
        if (isEmpty())
            throw new NoSuchElementException();
        if (comparator() == null) {
            Iterator<Heap.Entry<E, K>> i = entries().iterator();
            Heap.Entry<E, K> peek = i.next();
            Comparable peekKey = (Comparable) peek.getKey();
            if (peekKey == null) {
                i.remove();
                return peek.getElement();
            }
            while (i.hasNext()) {
                Heap.Entry<E, K> next = i.next();
                Comparable nextKey = (Comparable) next.getKey();
                if (nextKey == null) {
                    i.remove();
                    return next.getElement();
                } else {
                    int nextCompareToPeek = nextKey.compareTo(peekKey);
                    if (nextCompareToPeek < 0) {
                        peek = next;
                        peekKey = (Comparable) peek.getKey();
                    }
                }
            }
            entries().remove(peek);
            return peek.getElement();
        } else {
            Iterator<Heap.Entry<E, K>> i = entries().iterator();
            Heap.Entry<E, K> peek = i.next();
            K peekKey = peek.getKey();
            if (peekKey == null) {
                i.remove();
                return peek.getElement();
            }
            while (i.hasNext()) {
                Heap.Entry<E, K> next = i.next();
                K nextKey = next.getKey();
                if (nextKey == null) {
                    i.remove();
                    return next.getElement();
                } else {
                    int nextCompareToPeek = comparator().compare(nextKey,
                            peekKey);
                    if (nextCompareToPeek < 0) {
                        peek = next;
                        peekKey = peek.getKey();
                    }
                }
            }
            entries().remove(peek);
            return peek.getElement();
        }
    }

    public abstract Collection<Heap.Entry<E, K>> entries();

    protected abstract static class Entry<E, K> implements Heap.Entry<E, K> {

        /**
         * Sole Constructor.
         * 
         */
        protected Entry() {
        }

        public abstract void setKey(K key);

        public abstract K getKey();

        public abstract E getElement();

        @Override
        @SuppressWarnings("unchecked")
        public boolean equals(Object o) {
            if (o == null)
                return false;
            if (!(o instanceof Heap.Entry))
                return false;
            Heap.Entry e = (Heap.Entry) o;
            K key = getKey();
            E elem = getElement();
            Object eKey = e.getKey();
            Object eElem = e.getElement();
            return (key == null ? eKey == null : key.equals(eKey))
                    && (elem == null ? eElem == null : elem.equals(eElem));
        }

        @Override
        public int hashCode() {
            Object elem = getElement();
            Object key = getKey();
            return (elem == null ? 0 : elem.hashCode())
                    ^ (key == null ? 0 : key.hashCode());
        }

        @Override
        public String toString() {
            return "(" + String.valueOf(getKey()) + ","
                    + String.valueOf(getElement()) + ")";
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
