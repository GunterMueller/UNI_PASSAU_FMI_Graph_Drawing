// =============================================================================
//
//   AttributeCache.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AttributeCache.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.importers.graphml.parser;

/**
 * Class <code>AttributeCache</code> is used to cache the path and type of an
 * attribute declared in a &lt;key&gt; element of graphML.
 * 
 * @author ruediger
 */
class AttributeCache {

    /** Indicates that no type has been assigned. */
    static final int NOTYPE = 0;

    /** Represents a boolean type declaration. */
    static final int BOOLEAN = 1;

    /** Represents an integer type declaration. */
    static final int INT = 2;

    /** Represents a long type declaration. */
    static final int LONG = 3;

    /** Represents a float type declaration. */
    static final int FLOAT = 4;

    /** Represents a double type declaration. */
    static final int DOUBLE = 5;

    /** Represents a string type declaration. */
    static final int STRING = 6;

    /** The cached path. */
    private String path;

    /** The cached type. */
    private int type;

    /**
     * Constructs a new <code>AttributeCache</code>.
     */
    AttributeCache() {
        this.path = "";
        this.type = NOTYPE;
    }

    /**
     * Returns the currently cached path.
     * 
     * @return the currently cached path.
     */
    String getPath() {
        return this.path;
    }

    /**
     * Returns <code>true</code> if the attribute cache is reset,
     * <code>false</code> otherwise.
     * 
     * @return <code>true</code> if the attribute cache is reset,
     *         <code>false</code> otherwise.
     */
    boolean isReset() {
        return this.path.equals("") && (this.type == NOTYPE);
    }

    /**
     * Returns the currently cached type.
     * 
     * @return the currently cached type.
     */
    int getType() {
        return type;
    }

    /**
     * Prepares the cache, i.e. sets a new path and a new type value.
     * 
     * @param path
     *            the path name to be cached.
     * @param type
     *            the type to be cached.
     */
    void prepare(String path, int type) {
        this.path = path;
        this.type = type;
    }

    /**
     * Resets the cache. After executing this method <code>isReset()</code> will
     * always return <code>true</code>.
     */
    void reset() {
        this.path = "";
        this.type = NOTYPE;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
