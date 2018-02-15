package org.graffiti.plugins.tools.math;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class Permutation {
    /**
     * The set of permutations of a specific length.
     */
    public static class Set implements FiniteSet<Permutation> {
        
        private class Iter implements Iterator<Permutation> {

            private BigInteger nextIndex;
            
            public Iter() {
                nextIndex = BigInteger.valueOf(0);
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public boolean hasNext() {
                return nextIndex.compareTo(size) < 0;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public Permutation next() {
                Permutation result = get(nextIndex);
                nextIndex = nextIndex.add(BigInteger.valueOf(1));
                return result;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not yet implemented.");
            }
            
        }
        
        private int length;
        private BigInteger size;

        public Set(int length) {
            this.length = length;
            size = faculty(length);
        }

        public Permutation get(BigInteger number) {
            return new Permutation(length, number);
        }

        public Permutation get(long number) {
            return get(BigInteger.valueOf(number));
        }

        public BigInteger getSize() {
            return size;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Iterator<Permutation> iterator() {
            return new Iter();
        }
    }

    private int[] array;

    public Permutation(int length) {
        array = new int[length];
        for (int i = 0; i < length; i++) {
            array[i] = i;
        }
    }

    public Permutation(int length, long number) {
        this(length, BigInteger.valueOf(number));
    }

    // 0 <= number < fac(length)
    public Permutation(int length, BigInteger number) {
        this(length);
        reorder(number);
    }

    public Permutation(boolean check, int... array) {
        this.array = array;
        if (check) {
            int length = array.length;
            boolean[] flag = new boolean[length];
            for (int i = 0; i < length; i++) {
                int v = array[i];
                if (v < 0 || v >= length)
                    throw new IllegalArgumentException();
                flag[v] = true;
            }
            for (int i = 0; i < length; i++) {
                if (!flag[i])
                    throw new IllegalArgumentException();
            }
        }
    }

    public Permutation concatenate(Permutation other) {
        int length = array.length;
        int[] otherArray = other.array;
        if (otherArray.length != length)
            throw new IllegalArgumentException();
        int[] result = new int[length];
        for (int i = 0; i < length; i++) {
            result[i] = array[otherArray[i]];
        }
        return new Permutation(false, result);
    }

    public static BigInteger faculty(int i) {
        if (i <= 1)
            return BigInteger.valueOf(1);
        else
            return BigInteger.valueOf(i).multiply(faculty(i - 1));
    }

    public int get(int i) {
        return array[i];
    }

    public int getLength() {
        return array.length;
    }

    public Permutation inverse() {
        int length = array.length;
        int[] result = new int[length];
        for (int i = 0; i < length; i++) {
            result[array[i]] = i;
        }
        return new Permutation(false, result);
    }

    private void reorder(BigInteger number) {
        int length = array.length;
        for (int i = 1; i < length; i++) {
            number = number.divide(BigInteger.valueOf(i));
            int pos = number.mod(BigInteger.valueOf(i + 1)).intValue();
            int v = array[pos];
            array[pos] = array[i];
            array[i] = v;
        }
    }

    private void reorder(Random random) {
        int length = array.length;
        for (int i = 1; i < length; i++) {
            int pos = random.nextInt(i + 1);
            int v = array[pos];
            array[pos] = array[i];
            array[i] = v;
        }
    }

    public void shuffle(Random random) {
        reorder(random);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[");
        int length = array.length;
        if (length > 0) {
            builder.append(array[0]);
            for (int i = 1; i < length; i++) {
                builder.append(" ").append(array[i]);
            }
        }
        return builder.append("]").toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Permutation))
            return false;
        return Arrays.equals(array, ((Permutation) obj).array);
    }

    @Override
    public int hashCode() {
        return array.hashCode();
    }
}
