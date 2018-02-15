// =============================================================================
//
//   TransactionEventTest.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: TransactionEventTest.java 5771 2010-05-07 18:46:57Z gleissner $

package tests.graffiti.event;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.graffiti.event.TransactionEvent;
import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Graph;

/**
 * Tests class TransactionEvent.
 * 
 * @version $Revision: 5771 $
 */
public class TransactionEventTest extends TestCase {

    /** DOCUMENT ME! */
    TestGraphListener graphListener;

    /**
     * Constructs a test case with the given name.
     * 
     * @param name
     *            The name of the test case.
     */
    public TransactionEventTest(String name) {
        super(name);
    }

    /**
     * Main method for running all the test cases of this test class.
     * 
     * @param args
     *            DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TransactionEventTest.class);
    }

    /**
     * Tests the transaction event constructor.
     */
    public void testTransactionEventConstructor() {
        Graph graph = new AdjListGraph();
        TransactionEvent event = new TransactionEvent(graph);

        assertEquals("Failed to create a correct TransactionEvent."
                + "The source reference is not the same", graph, event
                .getSource());
    }

    /**
     * Tests the transcaction event constructor with the set of changed objects.
     */
    public void testTransactionEventConstructorWithSet() {
        Graph graph = new AdjListGraph();
        Set<Object> changedObjects = new HashSet<Object>();
        TransactionEvent event = new TransactionEvent(graph, changedObjects);

        assertEquals("Failed to create a correct TransactionEvent. "
                + "The source reference is not the same", graph, event
                .getSource());
        assertEquals("Failed to create a correct TransactionEvent. "
                + "The reference to the set of changed objects "
                + "is not the same", changedObjects, event.getChangedObjects());
    }

    /**
     * Sets up the text fixture. Called before every test case method.
     */
    @Override
    protected void setUp() {
        graphListener = new TestGraphListener();
    }

    /**
     * Tears down the text fixture. Called after all test case methods hava run.
     */
    @Override
    protected void tearDown() {
    }

    // It is first a beginning of TransactionEventTest. Further tests will
    // follow
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
