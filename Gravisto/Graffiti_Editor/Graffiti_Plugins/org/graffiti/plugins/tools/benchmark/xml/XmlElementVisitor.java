// =============================================================================
//
//   NodeVisitor.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.xml;

import org.w3c.dom.Element;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
interface XmlElementVisitor {
    public void visit(Element node) throws FormatException;
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
