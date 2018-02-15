package org.graffiti.plugins.algorithms.mst;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

public class ArrayHeap<E, K> extends AbstractHeap<E, K> {
    private Comparator<? super K> comparator = null;

    private ArrayList<Entry> heap = null;

    public ArrayHeap() {
        heap = new ArrayList<Entry>();
    }

    @Override
    public Comparator<? super K> comparator() {
        return comparator;
    }

    @Override
    public void clear() {
        heap.clear();
    }

    @Override
    public Heap.Entry<E, K> add(E element, K key) {
        int index = heap.size();
        Entry newEntry = new Entry(element, key, index);
        heap.add(index, newEntry);
        restoreHeapProperty(index);
        return newEntry;
    }

    @Override
    public E getPeek() {
        if (heap.isEmpty())
            throw new java.util.NoSuchElementException();
        return getPeekEntry().getElement();
    }

    @Override
    public E removePeek() {
        if (heap.isEmpty())
            throw new java.util.NoSuchElementException();
        if (heap.size() == 1)
            return heap.remove(0).getElement();
        int peekIndex = 0;
        int lastIndex = heap.size() - 1;
        Entry peek = heap.get(peekIndex);
        swap(peekIndex, lastIndex);
        heap.remove(lastIndex);
        restoreHeapProperty(0);
        return peek.getElement();
    }

    @Override
    public Collection<Heap.Entry<E, K>> entries() {
        return new java.util.AbstractCollection<Heap.Entry<E, K>>() {
            @Override
            public int size() {
                return heap.size();
            }

            @Override
            public Iterator<Heap.Entry<E, K>> iterator() {
                return new Iterator<Heap.Entry<E, K>>() {
                    int last = -1;

                    boolean removed = true;

                    public boolean hasNext() {
                        return last + 1 < heap.size();
                    }

                    public Heap.Entry<E, K> next() {
                        if (last + 1 >= heap.size())
                            throw new java.util.NoSuchElementException();
                        removed = false;
                        last++;
                        return heap.get(last);
                    }

                    public void remove() {
                        if (removed)
                            throw new IllegalStateException();
                        if (last != heap.size() - 1) {
                            heap.set(last, heap.remove(heap.size() - 1));
                            ArrayHeap.this.restoreHeapProperty(last);
                        } else {
                            heap.remove(last);
                        }
                        last--;
                        removed = true;
                    }
                };
            }
        };
    }

    private void restoreHeapProperty(int element) {
        int parent = parent(element);
        if (parent != -1 && compare(element, parent) < 0) {
            moveUp(element);
        } else {
            moveDown(element);
        }
    }

    private int parent(int element) {
        // TODO: Use a shift here.
        int parentIndex = (element + 1) / 2 - 1;
        return parentIndex != -1 ? parentIndex : -1;
    }

    private int left(int element) {
        int leftIndex = 2 * element + 1;
        return leftIndex < heap.size() ? leftIndex : -1;
    }

    private int right(int element) {
        int rightIndex = 2 * (element + 1);
        return rightIndex < heap.size() ? rightIndex : -1;
    }

    private void moveUp(int element) {
        int next = element;
        int parent = parent(next);
        while (parent != -1 && compare(next, parent) < 0) {
            swap(next, parent);
            next = parent;
            parent = parent(next);
        }
    }

    private void moveDown(int element) {
        int next = element;
        while (true) {
            int min = next;
            int left = left(next);
            int right = right(next);
            if (left != -1 && compare(left, min) < 0) {
                min = left;
            }
            if (right != -1 && compare(right, min) < 0) {
                min = right;
            }
            if (min == next)
                return;
            swap(min, next);
            next = min;
        }
    }

    private void swap(int e1Index, int e2Index) {
        if (e1Index == e2Index)
            return;
        Entry e1 = heap.get(e1Index);
        Entry e2 = heap.get(e2Index);
        e1.index = e2Index;
        e2.index = e1Index;
        heap.set(e1Index, e2);
        heap.set(e2Index, e1);
    }

    @SuppressWarnings("unchecked")
    private int compare(int e1Index, int e2Index) {
        K key1 = heap.get(e1Index).getKey();
        K key2 = heap.get(e2Index).getKey();
        if (key1 == null && key2 == null)
            return 0;
        else if (key1 == null)
            return -1;
        else if (key2 == null)
            return 1;
        else {
            if (comparator == null)
                return ((Comparable<K>) key1).compareTo(key2);
            else
                return comparator.compare(key1, key2);
        }
    }

    private Entry getPeekEntry() {
        return heap.get(0);
    }

    private class Entry extends AbstractHeap.Entry<E, K> {
        private E element = null;

        private K key = null;

        private int index = -1;

        public Entry(E element, K key, int index) {
            this.element = element;
            this.key = key;
            this.index = index;
        }

        @Override
        public E getElement() {
            return element;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public void setKey(K key) {
            this.key = key;
            ArrayHeap.this.restoreHeapProperty(index);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
