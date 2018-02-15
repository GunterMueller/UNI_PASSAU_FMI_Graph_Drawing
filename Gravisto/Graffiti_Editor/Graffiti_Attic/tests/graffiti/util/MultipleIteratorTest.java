// =============================================================================
//
//   MultipleIteratorTest.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: MultipleIteratorTest.java 5773 2010-05-07 18:50:34Z gleissner $

package tests.graffiti.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.graffiti.util.MultipleIterator;

/**
 * Contains test cases for the <code>java.util.Iterator</code> implementation
 * which allows iteration over more then one iterator.
 * 
 * @version $Revision: 5773 $
 */
public class MultipleIteratorTest extends TestCase {

    /** An auxiliary list. */
    private List<Integer> l1;

    /** Another auxiliary list. */
    private List<Integer> l2;

    /** The iterator array required for the test cases. */
    private Iterator<Integer>[] iters;

    /**
     * Constructs a new test case for the <code>MultipleIteratorTest</code>
     * class.
     * 
     * @param name
     *            the name for the test case.
     */
    public MultipleIteratorTest(String name) {
        super(name);
    }

    /**
     * Main method for running all the test cases of this test class.
     * 
     * @param args
     *            DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(MultipleIteratorTest.class);
    }

    /**
     * Tests, if more than one iterator will be correctly iterated.
     */
    public void testIteration() {
        for (Iterator<Integer> mi = new MultipleIterator<Integer>(iters[0],
                iters[1]); mi.hasNext();) {
            Object element = mi.next();
            assertTrue("iterator contains all list elements.", l1
                    .contains(element)
                    || l2.contains(element));
        }
    }

    /**
     * Tests if remove throws UnsupportedOperationException.
     */
    public void testRemove() {
        try {
            new MultipleIterator<Integer>(iters[0], iters[1]).remove();
            fail("UnsupportedOperationException not thrown.");
        } catch (UnsupportedOperationException uoe) {
            assertTrue("UnsupportedOperationException properly thrown.", true);
        }
    }

    /**
     * Constructs an array of Iterators containing Integer instances.
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void setUp() {
        int i = 0;
        l1 = new LinkedList<Integer>();

        for (int k = i; k < 5; k++) {
            l1.add(new Integer(k));
            i++;
        }

        l2 = new LinkedList<Integer>();

        for (int k = i; k < 7; k++) {
            l2.add(new Integer(k));
            i++;
        }

        iters = new Iterator[2];
        iters[0] = l1.iterator();
        iters[1] = l2.iterator();
    }

    /**
     * Tears down the test environement.
     */
    @Override
    protected void tearDown() {
        iters = null;
        l1.clear();
        l2.clear();
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
