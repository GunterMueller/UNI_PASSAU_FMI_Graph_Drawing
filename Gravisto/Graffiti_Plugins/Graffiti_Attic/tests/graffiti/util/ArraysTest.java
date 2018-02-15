package tests.graffiti.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class ArraysTest {

    @Test
    public void equals() {
        assertFalse(Arrays.equals(new Object[] {}, null));
        assertFalse(Arrays.equals(new Object[] {}, new Object()));
        assertTrue(Arrays.equals(new Object[] {}, new Object[] {}));
        assertFalse(Arrays.equals(new Object[] { 1 }, new Object[] { 1, 2 }));
        assertTrue(Arrays.equals(new Object[] { 1, 2, 3 }, new Object[] { 1, 2,
                3 }));
    }

    @Test(expected = java.lang.NullPointerException.class)
    public void nullEquals() {
        Arrays.equals(null, new Object());
    }
}
