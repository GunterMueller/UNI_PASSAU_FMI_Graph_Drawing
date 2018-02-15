// =============================================================================
//
//   AttributeCreator.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AttributeCreator.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.importers.graphml.parser;

import java.util.Hashtable;
import java.util.Map;

import org.graffiti.attributes.CollectionAttribute;

/**
 * Class <code>AttributeCreator</code> is used for creating attributes. It
 * provides functionality for setting default values to an attribute and it can
 * be queried the type and the path of an attribute of which the id from the
 * declaration is known. There can be more than one attribute type for a given
 * attribute path. Therefore the type and the path are queried by the id which
 * is unique.
 * 
 * @author ruediger
 */
abstract class AttributeCreator {

    /**
     * A <code>Map</code> mapping paths to <code>Attributes</code> set to a
     * declared default value (using the &lt;default&gt;-element of graphML).
     */
    private Map<String, Object> defAttrMap;

    /**
     * Maps the id of a graphML key declaration to the corresponding attribute
     * path and type.
     */
    private Map<String, KeyDeclaration> keyMap;

    /**
     * Temporarily continues the path of an attribute that has a default value
     * declaration.
     */
    private String defPath;

    /**
     * Temporarily continues the type of the default value that was declared for
     * an attribute.
     */
    private String defType;

    /**
     * Constructs a new <code>AttributeCreator</code>.
     */
    AttributeCreator() {
        this.keyMap = new Hashtable<String, KeyDeclaration>();
        this.defAttrMap = new Hashtable<String, Object>();
    }

    /**
     * Sets the path and the type of an attribute to which a default value shall
     * be assigned.
     * 
     * @param name
     *            the path of the attribute.
     * @param type
     *            the type of the default attribute value.
     */
    void setDefault(String name, String type) {
        this.defPath = name;
        this.defType = type;
    }

    /**
     * Returns a <code>CollectionAttribute</code> containing a set of default
     * attributes with assigned default values.
     * 
     * @return <code>CollectionAttribute</code> containing a set of default
     *         attributes with assigned default values.
     */
    CollectionAttribute getDefaultAttributes() {
        return createDefaultAttribute();
    }

    /**
     * Returns a <code>Map</code> mapping attribute paths to <code>Object</code>
     * s containing the declared default values of the corresponding attributes.
     * The <code>Object</code>s can - according to the graphML specification be
     * either of Type <code>Boolean</code>, <code>Integer</code>,
     * <code>Long</code>, <code>Float</code>, <code>Double</code> or
     * <code>String</code>.
     * 
     * @return a <code>Map</code> mapping attribute paths to Objects containing
     *         the declared default values of the corresponding attributes.
     */
    Map<String, Object> getDefaults() {
        return this.defAttrMap;
    }

    /**
     * Creates and returns a <code>CollectionAttribute</code> that will be
     * assigned by default.
     * 
     * @return a <code>CollectionAttribute</code> that will be assigned by
     *         default.
     */
    abstract CollectionAttribute createDefaultAttribute();

    /**
     * Returns the name of the attribute that has been declared with the given
     * id.
     * 
     * @param id
     *            the id the attribute has been declared with.
     * 
     * @return the name of the attribute.
     */
    String getName(String id) {
        KeyDeclaration kd = this.keyMap.get(id);

        return kd.getPath();
    }

    /**
     * Returns the type of the attribute that has been declared with the given
     * id.
     * 
     * @param id
     *            the id the attribute has been declared with.
     * 
     * @return the type of the attribute.
     */
    String getType(String id) {
        KeyDeclaration kd = this.keyMap.get(id);

        return kd.getType();
    }

    /**
     * Adds the default value for an attribute whose type and path must have
     * been set using the method <code>setDefault</code> before.
     * 
     * @param value
     *            the value of the attribute as declared in graphML.
     */
    void addDefaultValue(String value) {
        assert this.defType != null : "type for default attribute is null.";

        Object val;

        if (this.defType.equals("boolean")) {
            val = Boolean.valueOf(value);
        } else if (this.defType.equals("int")) {
            val = Integer.valueOf(value);
        } else if (this.defType.equals("long")) {
            val = Long.valueOf(value);
        } else if (this.defType.equals("float")) {
            val = Float.valueOf(value);
        } else if (this.defType.equals("double")) {
            val = Double.valueOf(value);
        } else {
            val = value;
        }

        this.defAttrMap.put(this.defPath, val);
    }

    /**
     * Adds the given key declaration to the key mapping.
     * 
     * @param id
     *            the id of the corresponding graphML &lt;key&gt: declaration.
     * @param name
     *            the attribute name from the corresponding graphML &lt;key&gt:
     *            declaration.
     * @param type
     *            the type from the corresponding graphML &lt;key&gt:
     *            declaration.
     */
    void addKeyDeclaration(String id, String name, String type) {
        assert id != null : "the id of the <key> element is null.";
        assert name != null : "the name of the <key> element is null.";
        this.keyMap.put(id, new KeyDeclaration(name, type));
    }

    /**
     * Class <code>KeyDeclaration</code> groups an attribute path and an
     * attribute type into one object.
     */
    private class KeyDeclaration {
        /** The attribute path. */
        private String path;

        /** The attribute type. */
        private String type;

        /**
         * Constructs a new <code>KeyDeclaration</code>.
         * 
         * @param path
         *            the path of the attribute.
         * @param type
         *            the type of the attribute.
         */
        KeyDeclaration(String path, String type) {
            assert path != null : "attribute path is null.";
            assert type != null : "attribute type is null.";
            this.path = path;
            this.type = type;
        }

        /**
         * Returns the path of this <code>KeyDeclaration</code>.
         * 
         * @return the path of this <code>KeyDeclaration</code>.
         */
        String getPath() {
            return this.path;
        }

        /**
         * Returns the type of this <code>KeyDeclaration</code>.
         * 
         * @return the type of this <code>KeyDeclaration</code>.
         */
        String getType() {
            return this.type;
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
