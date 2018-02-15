// =============================================================================
//
//   AllUtilTests.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AllUtilTests.java 5773 2010-05-07 18:50:34Z gleissner $

package tests.graffiti.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * A test suite for all graffiti.util test cases.
 * 
 * @version $Revision: 5773 $
 */
public class AllUtilTests extends TestCase {

    /**
     * Constructs a new test case.
     * 
     * @param name
     *            the name for the test case.
     */
    public AllUtilTests(String name) {
        super(name);
    }

    /**
     * Runs the test case.
     * 
     * @param args
     *            DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Assembles and returns a test suite for all test cases within all test
     * classes.
     * 
     * @return A test suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("Util");
        suite.addTestSuite(MultiLinkedListTest.class);
        suite.addTestSuite(MultipleIteratorTest.class);
        suite.addTestSuite(ConcatIteratorTest.class);

        // Add other test suites here
        return suite;
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
