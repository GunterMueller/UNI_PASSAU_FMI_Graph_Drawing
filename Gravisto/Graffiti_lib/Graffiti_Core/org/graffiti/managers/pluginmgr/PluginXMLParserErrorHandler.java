// =============================================================================
//
//   PluginXMLParserErrorHandler.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: PluginXMLParserErrorHandler.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.managers.pluginmgr;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * An error handler class for the plugin xml parser.
 * 
 * @version $Revision: 5767 $
 */
public class PluginXMLParserErrorHandler implements ErrorHandler {

    /**
     * Error Event Handler.
     * 
     * @param e
     *            The SAXException, which was thrown by the parser.
     * 
     * @exception SAXException
     *                if this method is called by the parser.
     */
    public void error(SAXParseException e) throws SAXException {
        throw e;
    }

    /**
     * Fatal Error Event Handler.
     * 
     * @param e
     *            The SAXException, which was thrown by the parser.
     * 
     * @exception SAXException
     *                if this method is called by the parser.
     */
    public void fatalError(SAXParseException e) throws SAXException {
        throw e;
    }

    /**
     * Warning Event Handler.
     * 
     * @param e
     *            The SAXException, which was thrown by the parser.
     * 
     * @exception SAXException
     *                if this method is called by the parser.
     */
    public void warning(SAXParseException e) throws SAXException {
        throw e;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
