// =============================================================================
//
//   AbstractHeapTest.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.util.heap;

import java.util.Iterator;

import junit.framework.TestCase;

/**
 * @author Harald
 * @version $Revision$ $Date$
 */
public class AbstractHeapTest extends TestCase {

    MockHeap heap = null;

    /*
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        heap = new MockHeap();
    }

    /*
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        heap = null;
    }

    /**
     * Test method for
     * {@link org.graffiti.plugins.algorithms.mst.AbstractHeap#size()}.
     */
    public final void testSize() {
        assertEquals(heap.entries().size(), heap.size());
    }

    /**
     * Test method for
     * {@link org.graffiti.plugins.algorithms.mst.AbstractHeap#AbstractHeap()}.
     */
    public final void testAbstractHeap() {

    }

    /**
     * Test method for
     * {@link org.graffiti.plugins.algorithms.mst.AbstractHeap#comparator()}.
     */
    public final void testComparator() {
    }

    /**
     * Test method for
     * {@link org.graffiti.plugins.algorithms.mst.AbstractHeap#iterator()}.
     */
    @SuppressWarnings("unchecked")
    public final void testIterator() {
        Iterator it = heap.iterator();
        for (int i = 0; i < heap.size(); i++) {
            it.next();
        }
        assertFalse(it.hasNext());
    }

    public final void testIteratorNext() {
        try {
            Iterator<Integer> i = heap.iterator();
            for (; i.hasNext(); i.next()) {
            }
            i.next();
        } catch (java.util.NoSuchElementException e) {
            return;
        }
        fail();
    }

    /**
     * Test method for
     * {@link org.graffiti.plugins.algorithms.mst.AbstractHeap#getPeek()}.
     */
    public final void testGetPeek() {
        assertEquals(java.util.Collections.min(heap), heap.getPeek());
    }

    /**
     * Test method for
     * {@link org.graffiti.plugins.algorithms.mst.AbstractHeap#entries()}.
     */
    public final void testEntries() {
        for (Heap.Entry<Integer, Integer> e : heap.entries()) {
            assertTrue(heap.contains(e.getElement()));
        }
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
