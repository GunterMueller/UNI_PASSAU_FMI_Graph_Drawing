// =============================================================================
//
//   IntegerIntervall.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.math;

import java.math.BigInteger;
import java.util.Iterator;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class IntegerInterval implements FiniteSet<Long> {
    
    private class Iter implements Iterator<Long> {
        private long counter;
        
        public Iter() {
            counter = 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasNext() {
            return counter < size;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Long next() {
            long result = lower + counter;
            counter++;
            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    };
    
    private long lower;
    private long size;

    public IntegerInterval(long from, long to) {
        this.lower = Math.min(from, to);
        this.size = Math.max(from, to) - lower + 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long get(BigInteger number) {
        return lower + number.longValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigInteger getSize() {
        return BigInteger.valueOf(size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Long> iterator() {
        return new Iter();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
