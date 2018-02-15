// =============================================================================
//
//   AttributeTypesManagerTest.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AttributeTypesManagerTest.java 5771 2010-05-07 18:46:57Z gleissner $

package tests.graffiti.attributes;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.graffiti.attributes.AttributeTypesManager;
import org.graffiti.attributes.BooleanAttribute;
import org.graffiti.attributes.ByteAttribute;
import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.attributes.FloatAttribute;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.attributes.IntegerAttribute;
import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.attributes.LongAttribute;
import org.graffiti.attributes.ShortAttribute;
import org.graffiti.attributes.StringAttribute;

/**
 * DOCUMENT ME!
 * 
 * @author $Author: gleissner $
 * @version $Revision: 5771 $ $Date: 2006-01-10 12:25:10 +0100 (Di, 10 Jan 2006)
 *          $
 */
public class AttributeTypesManagerTest extends TestCase {

    /**
     * Constructs a test case with the given name.
     * 
     * @param name
     *            The name of the test case.
     */
    public AttributeTypesManagerTest(String name) {
        super(name);
    }

    /**
     * Main method for running all the test cases of this test class.
     * 
     * @param args
     *            DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(AttributeTypesManagerTest.class);
    }

    /**
     * Tests the <code>addAttributeType</code> and
     * <code>getAttributeInstance</code> methods.
     */
    public void testAddGetInstanceMethods() {
        AttributeTypesManager atm = new AttributeTypesManager();
        atm.addAttributeType(BooleanAttribute.class);
        atm.addAttributeType(ByteAttribute.class);
        atm.addAttributeType(DoubleAttribute.class);
        atm.addAttributeType(FloatAttribute.class);
        atm.addAttributeType(HashMapAttribute.class);
        atm.addAttributeType(IntegerAttribute.class);
        atm.addAttributeType(LongAttribute.class);
        atm.addAttributeType(ShortAttribute.class);
        atm.addAttributeType(StringAttribute.class);
        atm.addAttributeType(LinkedHashMapAttribute.class);
        standardTest(atm);

        atm.addAttributeType(HashMapAttribute.class);

        HashMapAttribute hma = (HashMapAttribute) atm.getAttributeInstance(
                "org.graffiti.attributes.HashMapAttribute", "hma");

        if (hma == null) {
            fail("HashMapAttribute not registered");
        }

        try {
            atm.addAttributeType(java.util.LinkedList.class);
            fail("An illegalArgumentException was expected since LinkedList"
                    + " does not implement org.graffiti.Attribute");
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * Tests the constructor of AttributeTypesManager, which should register all
     * basic attributes.
     */
    public void testConstructor() {
        AttributeTypesManager atm = new AttributeTypesManager();
        atm
                .pluginAdded(
                        new org.graffiti.plugins.attributes.defaults.GraffitiAttributesPlugin(),
                        null);
        standardTest(atm);
    }

    /**
     * Tests the <code>getAttributeTypes</code> and
     * <code>setAttributeTypes</code> methods.
     */
    public void testGetSetMethods() {
        AttributeTypesManager atm = new AttributeTypesManager();

        Map<String, Class<?>> attrTypes = new HashMap<String, Class<?>>();
        attrTypes.put("org.graffiti.attributes.IntegerAttribute",
                IntegerAttribute.class);
        attrTypes.put("org.graffiti.attributes.StringAttribute",
                StringAttribute.class);
        atm.setAttributeTypes(attrTypes);

        attrTypes = atm.getAttributeTypes();

        if (attrTypes.get("org.graffiti.attributes.IntegerAttribute") == null) {
            fail("Pair org.graffiti.attributes.IntegerAttribute - "
                    + "IntegerAttribute.class has been set but "
                    + "seems to have disappeared after set / get");
        }

        if (attrTypes.get("org.graffiti.attributes.StringAttribute") == null) {
            fail("Pair org.graffiti.attributes.StringAttribute - "
                    + "StringAttribute.class has been set but "
                    + "seems to have disappeared after set / get");
        }

        int count = 0;

        for (@SuppressWarnings("unused")
        String string : attrTypes.keySet()) {
            count++;
        }

        assertEquals("Did not register correct number of attribute types", 2,
                count);
    }

    /**
     * Tests that the basic attributes are registered correctly (and only
     * those). Used by methods <code>testConstructor()</code> and
     * <code>testAddGetInstanceMethods</code>.
     * 
     * @param atm
     *            DOCUMENT ME!
     */
    private void standardTest(AttributeTypesManager atm) {
        Map<String, Class<?>> attrTypes = atm.getAttributeTypes();
        IntegerAttribute ia = null;
        FloatAttribute fa = null;
        DoubleAttribute da = null;
        StringAttribute sa = null;
        BooleanAttribute ba = null;
        ShortAttribute sha = null;
        LongAttribute la = null;
        ByteAttribute bya = null;
        HashMapAttribute hma = null;
        LinkedHashMapAttribute lhma = null;

        if (attrTypes == null) {
            fail("map containing attribute type " + "mapping is null");
        }

        ia = (IntegerAttribute) atm.getAttributeInstance(
                "org.graffiti.attributes.IntegerAttribute", "ia");
        fa = (FloatAttribute) atm.getAttributeInstance(
                "org.graffiti.attributes.FloatAttribute", "fa");
        da = (DoubleAttribute) atm.getAttributeInstance(
                "org.graffiti.attributes.DoubleAttribute", "da");
        sa = (StringAttribute) atm.getAttributeInstance(
                "org.graffiti.attributes.StringAttribute", "sa");
        ba = (BooleanAttribute) atm.getAttributeInstance(
                "org.graffiti.attributes.BooleanAttribute", "ba");
        sha = (ShortAttribute) atm.getAttributeInstance(
                "org.graffiti.attributes.ShortAttribute", "sha");
        la = (LongAttribute) atm.getAttributeInstance(
                "org.graffiti.attributes.LongAttribute", "la");
        bya = (ByteAttribute) atm.getAttributeInstance(
                "org.graffiti.attributes.ByteAttribute", "bya");
        hma = (HashMapAttribute) atm.getAttributeInstance(
                "org.graffiti.attributes.HashMapAttribute", "hma");
        lhma = (LinkedHashMapAttribute) atm.getAttributeInstance(
                "org.graffiti.attributes.LinkedHashMapAttribute", "lhma");

        if (ia == null) {
            fail("IntegerAttribute not registered");
        }

        if (fa == null) {
            fail("FloatAttribute not registered");
        }

        if (da == null) {
            fail("DoubleAttribute not registered");
        }

        if (sa == null) {
            fail("StringAttribute not registered");
        }

        if (ba == null) {
            fail("BooleanAttribute not registered");
        }

        if (sha == null) {
            fail("ShortAttribute not registered");
        }

        if (la == null) {
            fail("LongAttribute not registered");
        }

        if (bya == null) {
            fail("ByteAttribute not registered");
        }

        if (hma == null) {
            fail("HashMapAttribute not registered");
        }

        if (lhma == null) {
            fail("LinkedHashMapAttribute not registered");
        }

        int count = 0;

        for (@SuppressWarnings("unused")
        String string : attrTypes.keySet()) {
            count++;
        }

        assertEquals("Did not register correct number of attribute types", 10,
                count);
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
