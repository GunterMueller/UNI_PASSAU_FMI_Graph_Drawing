// =============================================================================
//
//   DebugElement.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.debug;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
interface DebugElement {
    public void writeToDocument(Document document) throws DocumentException;
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
