// =============================================================================
//
//   AllAttributesTests.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AllAttributesTests.java 5771 2010-05-07 18:46:57Z gleissner $

package tests.graffiti.attributes;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * A test suite for all graffiti.attributes test cases.
 * 
 * @version $Revision: 5771 $
 */
public class AllAttributesTests extends TestCase {

    /**
     * Constructs a new test case.
     * 
     * @param name
     *            the name for the test case.
     */
    public AllAttributesTests(String name) {
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
        TestSuite suite = new TestSuite("Attributes");
        suite
                .addTestSuite(tests.graffiti.attributes.IntegerAttributeTest.class);
        suite
                .addTestSuite(tests.graffiti.attributes.AbstractAttributableTest.class);
        suite
                .addTestSuite(tests.graffiti.attributes.AbstractAttributeTest.class);
        suite
                .addTestSuite(tests.graffiti.attributes.AttributeTypesManagerTest.class);
        suite
                .addTestSuite(tests.graffiti.attributes.BooleanAttributeTest.class);
        suite.addTestSuite(tests.graffiti.attributes.DoubleAttributeTest.class);
        suite.addTestSuite(tests.graffiti.attributes.FloatAttributeTest.class);
        suite.addTestSuite(tests.graffiti.attributes.StringAttributeTest.class);
        suite.addTestSuite(tests.graffiti.attributes.ByteAttributeTest.class);
        suite.addTestSuite(tests.graffiti.attributes.LongAttributeTest.class);
        suite.addTestSuite(tests.graffiti.attributes.ShortAttributeTest.class);
        suite
                .addTestSuite(tests.graffiti.attributes.HashMapAttributeTest.class);

        // Add other test suites here
        return suite;
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
