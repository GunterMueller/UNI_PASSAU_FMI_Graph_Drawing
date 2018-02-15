package org.graffiti.plugins.algorithms.sugiyama.util;

/**
 * This class implements parts of an ArrayList that stores primitive ints. It
 * does not perform any range-checks or care for garbage collection.
 */
public class LazyIntArrayList {

    private int[] elements;
    public int elementCount;

    public LazyIntArrayList(int size) {
        elements = new int[size];
        elementCount = 0;
    }

    public int get(int index) {
        return elements[index];
    }

    public void add(int element) {
        elements[elementCount++] = element;
    }

    public void clear() {
        elementCount = 0;
    }

    public void set(int index, int element) {
        elements[index] = element;
    }

}
