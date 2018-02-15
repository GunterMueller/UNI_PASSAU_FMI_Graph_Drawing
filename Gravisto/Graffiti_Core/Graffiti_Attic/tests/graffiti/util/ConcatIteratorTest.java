// =============================================================================
//
//   ConcatIteratorTest.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ConcatIteratorTest.java 5773 2010-05-07 18:50:34Z gleissner $

package tests.graffiti.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.graffiti.util.ConcatIterator;

/**
 * @author forster
 * @version $Revision: 5773 $ $Date: 2006-07-25 10:18:35 +0200 (Di, 25 Jul 2006)
 *          $
 */
public class ConcatIteratorTest extends TestCase {
    public ConcatIteratorTest(String name) {
        super(name);
    }

    /*
     * Test iteration if neither first nor last iterator are empty.
     */
    public void testGeneral() {
        List<Integer> l1 = Arrays.asList(1, 2, 3, 4);
        List<Integer> l2 = Arrays.asList(5);
        List<Integer> l3 = new LinkedList<Integer>();
        List<Integer> l4 = Arrays.asList(6, 7, 8);

        // warning can be ignored, since arguments are of correct type
        @SuppressWarnings("unchecked")
        ConcatIterator<Integer> it = new ConcatIterator<Integer>(l1.iterator(),
                l2.iterator(), l3.iterator(), l4.iterator());

        assertTrue(it.hasNext());
        assertTrue(it.next() == 1);
        assertTrue(it.hasNext());
        assertTrue(it.next() == 2);
        assertTrue(it.hasNext());
        assertTrue(it.next() == 3);
        assertTrue(it.hasNext());
        assertTrue(it.next() == 4);
        assertTrue(it.hasNext());
        assertTrue(it.next() == 5);
        assertTrue(it.hasNext());
        assertTrue(it.next() == 6);
        assertTrue(it.hasNext());
        assertTrue(it.next() == 7);
        assertTrue(it.hasNext());
        assertTrue(it.next() == 8);
        assertTrue(!it.hasNext());
    }

    /*
     * Test iteration if last iterator is empty.
     */
    public void testLastEmpty() {
        List<Integer> l1 = Arrays.asList(1, 2, 3, 4);
        List<Integer> l2 = Arrays.asList(5);
        List<Integer> l3 = new LinkedList<Integer>();
        List<Integer> l4 = Arrays.asList(6, 7, 8);
        List<Integer> l5 = new LinkedList<Integer>();

        // warning can be ignored, since arguments are of correct type
        @SuppressWarnings("unchecked")
        ConcatIterator<Integer> it = new ConcatIterator<Integer>(l1.iterator(),
                l2.iterator(), l3.iterator(), l4.iterator(), l5.iterator());

        assertTrue(it.hasNext());
        assertTrue(it.next() == 1);
        assertTrue(it.hasNext());
        assertTrue(it.next() == 2);
        assertTrue(it.hasNext());
        assertTrue(it.next() == 3);
        assertTrue(it.hasNext());
        assertTrue(it.next() == 4);
        assertTrue(it.hasNext());
        assertTrue(it.next() == 5);
        assertTrue(it.hasNext());
        assertTrue(it.next() == 6);
        assertTrue(it.hasNext());
        assertTrue(it.next() == 7);
        assertTrue(it.hasNext());
        assertTrue(it.next() == 8);
        assertTrue(!it.hasNext());
    }

    /*
     * Test iteration if first iterator is empty.
     */
    public void testFirstEmpty() {
        List<Integer> l1 = new LinkedList<Integer>();
        List<Integer> l2 = Arrays.asList(1, 2, 3, 4);
        List<Integer> l3 = Arrays.asList(5);
        List<Integer> l4 = new LinkedList<Integer>();
        List<Integer> l5 = Arrays.asList(6, 7, 8);

        // warning can be ignored, since arguments are of correct type
        @SuppressWarnings("unchecked")
        ConcatIterator<Integer> it = new ConcatIterator<Integer>(l1.iterator(),
                l2.iterator(), l3.iterator(), l4.iterator(), l5.iterator());

        assertTrue(it.hasNext());
        assertTrue(it.next() == 1);
        assertTrue(it.hasNext());
        assertTrue(it.next() == 2);
        assertTrue(it.hasNext());
        assertTrue(it.next() == 3);
        assertTrue(it.hasNext());
        assertTrue(it.next() == 4);
        assertTrue(it.hasNext());
        assertTrue(it.next() == 5);
        assertTrue(it.hasNext());
        assertTrue(it.next() == 6);
        assertTrue(it.hasNext());
        assertTrue(it.next() == 7);
        assertTrue(it.hasNext());
        assertTrue(it.next() == 8);
        assertTrue(!it.hasNext());
    }

    /*
     * Test iteration if first iterator is empty.
     */
    public void zeroIterators() {
        ConcatIterator<Integer> it = new ConcatIterator<Integer>();

        assertTrue(!it.hasNext());
    }

    /*
     * Test method for 'org.graffiti.util.ConcatIterator.remove()'
     */
    public void testRemove() {
        List<Integer> l1 = new LinkedList<Integer>();
        List<Integer> l2 = new LinkedList<Integer>(Arrays.asList(1, 2, 3, 4));
        List<Integer> l3 = Arrays.asList(5);
        List<Integer> l4 = new LinkedList<Integer>();
        List<Integer> l5 = Arrays.asList(6, 7, 8);

        // warning can be ignored, since arguments are of correct type
        @SuppressWarnings("unchecked")
        ConcatIterator<Integer> it = new ConcatIterator<Integer>(l1.iterator(),
                l2.iterator(), l3.iterator(), l4.iterator(), l5.iterator());

        assertTrue(it.hasNext());
        assertTrue(it.next() == 1);
        assertTrue(it.hasNext());
        assertTrue(it.next() == 2);

        assertTrue(l2.contains(2));
        it.remove();
        assertTrue(!l2.contains(2));
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(ConcatIteratorTest.class);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
