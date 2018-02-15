// =============================================================================
//
//   AbstractGmlAttributeValue.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractGmlAttributeValue.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.gmlWriter.gmlAttribute;

import java.io.OutputStream;

/**
 * This class provides a rudimentary implementation of the interface
 * <code>GmlAttributeValue</code>.
 * 
 * @author ruediger
 */
public abstract class AbstractGmlAttributeValue implements GmlAttributeValue {

    /** The <code>OutputStream</code> to which to write. */
    protected OutputStream os;

    /**
     * Creates a new AbstractGmlAttributeValue object.
     * 
     * @param os
     *            the <code>OutputStream</code> to which to write.
     */
    public AbstractGmlAttributeValue(OutputStream os) {
        this.os = os;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
