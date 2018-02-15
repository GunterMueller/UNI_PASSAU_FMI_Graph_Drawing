// =============================================================================
//
//   GmlAttributeValue.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GmlAttributeValue.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.gmlWriter.gmlAttribute;

/**
 * This interface defines how to access the attribute value it contains for
 * writing it to a file.
 * 
 * @author ruediger
 */
public interface GmlAttributeValue {

    /**
     * Returns a String representation of the attribute value.
     * 
     * @return a String representation of the attribute value.
     */
    String getString();
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
