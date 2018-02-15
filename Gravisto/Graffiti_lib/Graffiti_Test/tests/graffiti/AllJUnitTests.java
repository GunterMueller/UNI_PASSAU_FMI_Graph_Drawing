// =============================================================================
//
//   AllJUnitTests.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AllJUnitTests.java 5775 2010-05-07 18:55:47Z gleissner $

package tests.graffiti;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * A test suite for all graffiti test cases.
 * 
 * @version $Revision: 5775 $
 */
public class AllJUnitTests {

    /**
     * Creates a new AllJUnitTests object.
     * 
     * @param name
     *            DOCUMENT ME!
     */
    public AllJUnitTests(String name) {
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
        TestSuite suite = new TestSuite("All Tests");
        suite.addTest(tests.graffiti.attributes.AllAttributesTests.suite());
        suite.addTest(tests.graffiti.event.AllEventTests.suite());
        suite.addTest(tests.graffiti.graph.AllGraphTests.suite());
        suite.addTest(tests.graffiti.io.AllIOTests.suite());

        // Add other test suites here
        return suite;
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
