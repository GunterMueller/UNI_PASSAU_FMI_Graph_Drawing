// =============================================================================
//
//   AllPluginManagerTests.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AllPluginManagerTests.java 5773 2010-05-07 18:50:34Z gleissner $

package tests.graffiti.pluginmgr;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * A test suite for all plugin manager test cases.
 * 
 * @version $Revision: 5773 $
 */
public class AllPluginManagerTests extends TestCase {

    /**
     * Constructs a new test case.
     * 
     * @param name
     *            the name for the test case.
     */
    public AllPluginManagerTests(String name) {
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
        TestSuite suite = new TestSuite();
        suite.addTestSuite(PluginManagerTest.class);

        // Add other test suites here
        return suite;
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
