package org.graffiti.plugins.tools.benchmark.math;

/**
 * Subclasses of {@code BinarySearch} represent algorithms based on a binary
 * search. The algorithm specific code is implemented by overriding
 * {@link #test(int)}. The binary search is performed by {@link #search()}.
 * There must be a constant x so that the condition holds for each y >= x und
 * does not hold for each y < x. If the test whether or not the condition holds
 * for a specific integer runs in O(1), the binary search finds the constant in
 * O(log n).
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class BinarySearch {
    protected abstract boolean test(int value);

    public final int search() {
        if (test(1)) {
            if (test(0))
                return 0;
            else
                return 1;
        }

        int value = 3;
        int mask = 1;

        while (!test(value)) {
            value <<= 1;
            value++;
            mask <<= 1;
        }

        return search(value, mask);
    }

    private final int search(int value, int mask) {
        while (mask > 0) {
            value &= ~mask;

            boolean success = test(value);

            if (!success) {
                value |= mask;
            }

            mask >>= 1;
        }

        return value;
    }
}
