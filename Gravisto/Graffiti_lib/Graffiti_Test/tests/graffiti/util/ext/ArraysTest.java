package tests.graffiti.util.ext;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

public class ArraysTest {

    @Test
    public void toStringTest() {
        assertEquals("[]", Arrays.toString(new Object[] {}));
        assertEquals("[null]", Arrays.toString(new Object[] { null }));
        assertEquals("[1]", Arrays.toString(new Object[] { 1 }));
        assertEquals("[1,2]", Arrays.toString(new Object[] { 1, 2 }));
    }
}
