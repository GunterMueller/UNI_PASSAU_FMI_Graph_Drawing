// =============================================================================
//
//   AllGraphTests.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AllGraphTests.java 5771 2010-05-07 18:46:57Z gleissner $

package tests.graffiti.graph;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * A test suite for all graffiti.graph test cases.
 * 
 * @version $Revision: 5771 $
 */
public class AllGraphTests extends TestCase {

    /**
     * Constructs a new test case.
     * 
     * @param name
     *            the name for the test case.
     */
    public AllGraphTests(String name) {
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
        TestSuite suite = new TestSuite("Graph");
        suite.addTestSuite(AdjListGraphTest.class);
        suite.addTestSuite(AdjListEdgeTest.class);
        suite.addTestSuite(AdjListNodeTest.class);
        suite.addTestSuite(OptAdjListGraphTest.class);
        suite.addTestSuite(AttributeConsumerTest.class);
        suite.addTestSuite(FastGraphTest.class);
        suite.addTestSuite(FastEdgeTest.class);
        suite.addTestSuite(FastNodeTest.class);

        // Add other test suites here
        return suite;
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
