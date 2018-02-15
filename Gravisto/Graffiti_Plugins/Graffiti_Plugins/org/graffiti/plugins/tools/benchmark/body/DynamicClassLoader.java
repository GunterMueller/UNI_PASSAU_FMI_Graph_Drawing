// =============================================================================
//
//   ClassLoader.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.body;

import org.apache.bcel.classfile.JavaClass;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class DynamicClassLoader extends ClassLoader {
    public Class<?> createClass(JavaClass javaClass) {
        byte[] bytes = javaClass.getBytes();
        return defineClass(javaClass.getClassName(), bytes, 0, bytes.length);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
