// =============================================================================
//
//   CharFilter.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: CharFilter.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.importers.graphml.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * This filter collects multiple calls of the <code>characters()</code> events
 * and merges them into one call. The character data from these events is stored
 * in a buffer and a new character data event is fired towards the parent filter
 * once a different event comes from the parser.
 * 
 * @author ruediger
 */
class CharFilter extends XMLFilterImpl {

    /** The buffer containing the content of the collected characters events. */
    private StringBuffer buffer;

    /**
     * Constructs a new <code>CharFilter</code>.
     * 
     * @param parent
     *            the parent <code>XMLReader</code>.
     */
    public CharFilter(XMLReader parent) {
        super(parent);
        this.buffer = new StringBuffer();
    }

    /**
     * Filter a character data event and cache the data.
     * 
     * @param text
     *            an array of characters.
     * @param start
     *            the starting position in the array.
     * @param length
     *            the number of characters to use from the array.
     * 
     * @exception SAXException
     *                The client may throw an exception during processing.
     */
    @Override
    public void characters(char[] text, int start, int length)
            throws SAXException {
        String value = new String(text, start, length).trim();

        if (value.equals(""))
            return;
        else {
            this.buffer.append(value);
        }
    }

    /**
     * Filter an end document event and fire a character data event if
     * necessary.
     * 
     * @exception SAXException
     *                the client may throw an exception during processing.
     */
    @Override
    public void endDocument() throws SAXException {
        checkCharacters();
        super.endDocument();
    }

    /**
     * Filter an end element event and fire a character data event if necessary.
     * 
     * @param uri
     *            the element's Namespace URI, or the empty string.
     * @param localName
     *            the element's local name, or the empty string.
     * @param qName
     *            the element's qualified (prefixed) name, or the empty string.
     * 
     * @exception SAXException
     *                the client may throw an exception during processing.
     */
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        checkCharacters();
        super.endElement(uri, localName, qName);
    }

    /**
     * Filter an end Namespace prefix mapping event and fire a character data
     * event if necessary.
     * 
     * @param prefix
     *            the Namespace prefix.
     * 
     * @exception SAXException
     *                the client may throw an exception during processing.
     */
    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        checkCharacters();
        super.endPrefixMapping(prefix);
    }

    /**
     * Filter an ignorable whitespace event and fire a character data event if
     * necessary.
     * 
     * @param text
     *            an array of characters.
     * @param start
     *            the starting position in the array.
     * @param length
     *            the number of characters to use from the array.
     * 
     * @exception SAXException
     *                the client may throw an exception during processing.
     */
    @Override
    public void ignorableWhitespace(char[] text, int start, int length)
            throws SAXException {
        checkCharacters();
        super.ignorableWhitespace(text, start, length);
    }

    /**
     * Filter a processing instruction event and fire a character data event if
     * necessary.
     * 
     * @param target
     *            the processing instruction target.
     * @param data
     *            the text following the target.
     * 
     * @exception SAXException
     *                the client may throw an exception during processing.
     */
    @Override
    public void processingInstruction(String target, String data)
            throws SAXException {
        checkCharacters();
        super.processingInstruction(target, data);
    }

    /**
     * Filter a skipped entity event and fire a character data event if
     * necessary.
     * 
     * @param name
     *            the name of the skipped entity.
     * 
     * @exception SAXException
     *                the client may throw an exception during processing.
     */
    @Override
    public void skippedEntity(String name) throws SAXException {
        checkCharacters();
        super.skippedEntity(name);
    }

    /**
     * Filter a start element event and fire a character data event if
     * necessary.
     * 
     * @param uri
     *            the element's Namespace URI, or the empty string.
     * @param localName
     *            the element's local name, or the empty string.
     * @param qName
     *            the element's qualified (prefixed) name, or the empty string.
     * @param atts
     *            the element's attributes.
     * 
     * @exception SAXException
     *                the client may throw an exception during processing.
     */
    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes atts) throws SAXException {
        checkCharacters();
        super.startElement(uri, localName, qName, atts);
    }

    /**
     * Filter a start Namespace prefix mapping event and fire a character data
     * event if necessary.
     * 
     * @param prefix
     *            the Namespace prefix.
     * @param uri
     *            the Namespace URI.
     * 
     * @exception SAXException
     *                the client may throw an exception during processing.
     */
    @Override
    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
        checkCharacters();
        super.startPrefixMapping(prefix, uri);
    }

    /**
     * Checks if there are some characters in the buffer such that the
     * <code>characters()</code> method of the parent filter should be called
     * before handling the next event.
     * 
     * @throws SAXException
     *             DOCUMENT ME!
     */
    private void checkCharacters() throws SAXException {
        int length = this.buffer.length();

        if (length > 0) {
            super.characters(this.buffer.toString().toCharArray(), 0, length);
            this.buffer = new StringBuffer();
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
