// =============================================================================
//
//   ClassNames.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.util.ext;

/**
 * Extension methods for {@link String}s that represent names of {@link Class}
 * es.
 * 
 * @author Harald Frankenberger
 */
public class ClassName {

    private static Map<String, Class<?>> cache = new HashMap<String, Class<?>>();

    private ClassName() {
    }

    /**
     * Returns the class for this class name, or <code>null</code> if this
     * string is not the binary name of a class.
     * 
     * @param this_
     *            this class name
     * @return the class for this class name
     */
    public static Class<?> toClass(String this_) {
        Class<?> elementClass = cache.get(this_);
        if (elementClass != null)
            return elementClass;
        try {
            Class<?> classForName = Class.forName(this_);
            cache.put(this_, classForName);
            return classForName;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static Class<?> findInPackage(String this_, String package_) {
        String binaryClassName = Arrays.concat(this_, ".", package_);
        return toClass(binaryClassName);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
