package org.visnacom.sugiyama.test;

import java.util.ListIterator;

import org.visnacom.model.DLL;

import junit.framework.TestCase;

public class DLLTest extends TestCase {

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(DLLTest.class);
    }

    public void testIteratorset() {
        DLL list = new DLL();
        list.add(new Integer(1));
        list.add(new Integer(2));
        ListIterator it = list.listIterator();
        assertEquals(new Integer(1),it.next());
        it.set(new Integer(2));
        assertEquals(null, it.next());
        it.set(new Integer(1));
        assertEquals(new Integer(2),list.get(0));
        assertEquals(new Integer(1),list.get(1));
    }
}
