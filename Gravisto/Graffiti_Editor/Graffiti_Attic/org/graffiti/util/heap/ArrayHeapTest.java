// =============================================================================
//
//   ArrayHeapTest.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.util.heap;

import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

/**
 * @author Harald
 * @version $Revision$ $Date$
 */
public class ArrayHeapTest extends TestCase {
    private ArrayHeap<Integer, Integer> heap = null;

    /*
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        heap = new ArrayHeap<Integer, Integer>();
    }

    /*
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        heap = null;
    }

    public void testAddNotSupportedForCollectionOfEntries() {
        Collection<Heap.Entry<Integer, Integer>> entries = heap.entries();
        try {
            entries.add(null);
        } catch (UnsupportedOperationException expected) {
            return;
        }
        fail();
    }

    public void testEntriesIteratorRemove() {
        heap.add(0, 0);
        heap.add(1, 1);
        Collection<Heap.Entry<Integer, Integer>> entries = heap.entries();
        for (Iterator<Heap.Entry<Integer, Integer>> i = entries.iterator(); i
                .hasNext();)
            if (i.next().getKey().equals(new Integer(1))) {
                i.remove();
            }
        assertFalse(heap.contains(new Integer(1)));
    }

    public void testIteratorRemove() {
        Integer min = 20000;
        Integer min2 = 30000;
        heap.add(5, 5);
        heap.add(6, 6);
        heap.add(44, 44);
        heap.add(min);
        heap.add(min2);
        for (Iterator<Integer> i = heap.iterator(); i.hasNext();) {
            if (i.next().equals(min)) {
                i.remove();
            }
        }
        assertFalse(heap.contains(min));
        assertEquals(heap.entries().toString(), min2, heap.removePeek());
    }

    public void testRemove() {
        Integer min = 20000;
        Integer min2 = 30000;
        heap.add(5, 5);
        heap.add(6, 6);
        heap.add(90000, 0);
        heap.add(min);
        heap.add(min2);
        assertEquals(min, heap.getPeek());
        heap.remove(min);
        assertEquals(heap.entries().toString(), min2, heap.getPeek());
    }

    public void testEntrySetKey() {
        Heap.Entry<Integer, Integer> max = heap.add(Integer.MAX_VALUE,
                Integer.MAX_VALUE);
        heap.add(1, 1);
        heap.add(1, 1);
        heap.add(2, 2);
        assertEquals(new Integer(1), heap.getPeek());
        max.setKey(Integer.MIN_VALUE);
        assertEquals(heap.toString(), max.getElement(), heap.getPeek());
    }

    public void testGetPeek() {
        Integer min = new Integer(1000);
        heap.add(min, Integer.MIN_VALUE);
        heap.add(9, 9);
        heap.add(3, 3);
        heap.add(0, 0);
        assertEquals(min, heap.getPeek());
    }

    public final void testRemovePeek() {
        Integer min = 4000;
        Integer min2 = 60000;
        heap.add(0, 0);
        heap.add(4, 4);
        heap.add(-1, -1);
        heap.add(Integer.MIN_VALUE, Integer.MIN_VALUE);
        heap.add(min);
        heap.add(min2);
        assertEquals(min, heap.removePeek());
        assertEquals(heap.entries().toString(), min2, heap.getPeek());
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------

