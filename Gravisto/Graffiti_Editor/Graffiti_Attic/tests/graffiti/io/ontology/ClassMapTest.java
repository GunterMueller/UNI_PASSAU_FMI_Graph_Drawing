package tests.graffiti.io.ontology;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ClassMapTest {

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void addUnconfirmedNull() {
        new ClassBase().addDeclaredClass(null);
    }

    @Test(expected = java.util.NoSuchElementException.class)
    public void classForIdThatDoesNotExist() {
        new ClassBase().classForId("Class");
    }

    @Test(expected = java.util.NoSuchElementException.class)
    public void unconfirmedClassForId() {
        ClassBase classMap = new ClassBase();
        String unconfirmedClass = "Class";
        classMap.addDeclaredClass(unconfirmedClass);
        classMap.classForId(unconfirmedClass);
    }

    @Test
    public void addClass() {
        ClassBase classMap = new ClassBase();
        String classId = "Class";
        String label = "label";

        classMap.addDeclaredClass(classId);
        classMap.addClass(classId, label);
        String classLabel = classMap.classForId(classId).label();
        assertEquals(label, classLabel);
    }
}
