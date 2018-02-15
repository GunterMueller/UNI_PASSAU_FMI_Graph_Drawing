package org.graffiti.plugins.algorithms.sugiyama.util;

import org.graffiti.plugins.algorithms.sugiyama.crossmin.global.CrossMinObject;

/**
 * This class implements parts of an ArrayList that stores CrossMinObjects. It
 * does not perform any range-checks and does not care for garbage collection
 */
public class LazyCrossMinObjectArrayList {
    private CrossMinObject[] elements;
    public int elementCount;

    public LazyCrossMinObjectArrayList(int size) {
        elements = new CrossMinObject[size];
        elementCount = 0;
    }

    public CrossMinObject get(int index) {
        return elements[index];
    }

    public void add(CrossMinObject element) {
        elements[elementCount++] = element;
    }

    public void clear() {
        elementCount = 0;
    }

    public void set(int index, CrossMinObject element) {
        elements[index] = element;
    }
}
