// =============================================================================
//
//   GraphMLGravistoFilter.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphMLGravistoFilter.java 5780 2010-05-10 20:33:09Z gleissner $

package org.graffiti.plugins.ios.importers.graphml.parser;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graffiti.util.logging.GlobalLoggerSetting;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * This class handles events from the underlying XML parser filtering features
 * not supported by Gravisto and doing error handling. Gravisto does not support
 * nested graphs, hyperedges and locators as specified in the <a
 * href="http://graphml.graphdrawing.org/specification">graphML
 * specification</a>.
 * 
 * @author ruediger
 */
class GraphMLGravistoFilter extends XMLFilterImpl {

    /** The logger for this class. */
    private static final Logger logger = Logger
            .getLogger(GraphMLGravistoFilter.class.getName());
    
    static {
        logger.setLevel(GlobalLoggerSetting.LOGGER_LEVEL);
    }

    /** The basic namespace for the graphml schema definition(s). */
    private static final String NAMESPACE = "http://graphml.graphdrawing.org/"
            + "xmlns/graphml";

    /** An alternative namespace for the graphml schema definition(s). */
    private static final String NAMESPACE2 = "http://graphml.graphdrawing.org/"
            + "xmlns";

    /** The alternative namespace. */
    private static final String OLD_NAMESPACE = "http://graphml.graphdrawing"
            + ".org/xmlns/1.0rc";

    /**
     * A stack containing the qualified name of element from foreign namespaces
     * used for filtering.
     */
    private ElementStack foreignNSElts;

    /** Indicates the presence of a description. */
    private boolean desc;

    /** Indicates that the parser is inside a hyperedge declaration. */
    private boolean hyperedge;

    /** Indicates that the parser is inside a locator definition. */
    private boolean loc;

    /** The nesting level when nested graphs occur. */
    private int nesting;

    /**
     * Constructs a new <code>GraphMLGravistoFilter</code>.
     * 
     * @param reader
     *            the parent <code>XMLReader</code>.
     */
    public GraphMLGravistoFilter(XMLReader reader) {
        super(reader);
        assert this.getParent() != null;
        this.nesting = -1;
        this.setEntityResolver(new GraphMLEntityResolver());
        this.hyperedge = false;
        this.loc = false;
        this.desc = false;
        this.foreignNSElts = new ElementStack();
    }

    /**
     * Filter a character data event. Checks if the element that is currently
     * being processed is ignorable and if not forwards the character data event
     * to the parent parser.
     * 
     * @param ch
     *            nn array of characters.
     * @param start
     *            the starting position in the array.
     * @param length
     *            the number of characters to use from the array.
     * 
     * @exception SAXException
     *                the client may throw an exception during processing.
     */
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if (isIgnorable() || (this.foreignNSElts.top() != null))
            return;

        if ((length) > 0) {
            String chars = new String(ch, start, length).trim();

            if (chars.length() > 0) {
                super.characters(chars.toCharArray(), 0, chars.length());
            } else {
                // logger.info("whitespace character string.");
            }
        } else {
            // logger.info("empty character string.");
        }
    }

    /**
     * Filter an end element event.
     * 
     * @param uri
     *            the element's Namespace URI, or the empty string.
     * @param localName
     *            the element's local name, or the empty string.
     * @param qName
     *            the element's qualified (prefixed) name, or the empty string.
     * 
     * @exception SAXException
     *                The client may throw an exception during processing.
     */
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        // logger.info("end of " + (isIgnorable() ? "ignorable " : "") +
        // "element " + qName + ".");
        if (qName.equals(this.foreignNSElts.top())) {
            this.foreignNSElts.pop();

            return;
        }

        if (this.foreignNSElts.top() != null)
            return;

        if (qName.equals("graph")) {
            --this.nesting;
            assert this.nesting >= -1 : "nesting level less than -1 ("
                    + this.nesting + ").";

            if (logger.isLoggable(Level.FINE)) {
                logger.fine("end of "
                        + ((this.nesting == 0) ? "" : "embedded ")
                        + "graph detected");
            }

            return;
        } else if (qName.equals("hyperedge")) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("end of " + (isIgnorable() ? "ignorable " : "")
                        + "element hyperedge detected");
            }
            this.hyperedge = false;

            return;
        } else if (qName.equals("locator")) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("end of " + (isIgnorable() ? "ignorable " : "")
                        + "element locator detected");
            }

            this.loc = false;

            return;
        } else if (qName.equals("graphml")) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("end of " + (isIgnorable() ? "ignorable " : "")
                        + "element graphml detected");
            }

            return;
        } else if (qName.equals("desc")) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("end of " + (isIgnorable() ? "ignorable " : "")
                        + "element desc detected");
            }
            this.desc = false;

            return;
        }

        // let the parent parser handle the rest
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("call the parent parser to handle element " + qName
                    + ".");
        }

        super.endElement(uri, localName, qName);
    }

    /**
     * Filter an error event.
     * 
     * @param e
     *            the error as an exception.
     * 
     * @exception SAXException
     *                the client may throw an exception during processing.
     */
    @Override
    public void error(SAXParseException e) throws SAXException {
        logger.severe("SAXParseException: " + e.getMessage());
        throw e;
    }

    /**
     * Filter a fatal error event.
     * 
     * @param e
     *            the error as an exception.
     * 
     * @exception SAXException
     *                the client may throw an exception during processing.
     */
    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        logger.severe("SAXParseException: " + e.getMessage());
        throw e;
    }

    /**
     * Filter a start element event. Filters elements that are not supported as
     * well as foreign namespaces and nested graphs. The rest is passed to the
     * parent filter.
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
     *                The client may throw an exception during processing.
     */
    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes atts) throws SAXException {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("start " + (isIgnorable() ? "ignorable " : "")
                    + "element " + qName + "\n\tnamespace: " + uri
                    + "\n\tqName: " + qName + "\n\tstack: "
                    + this.foreignNSElts.toString());
        }

        // the element can be ignored if the graph nesting level is greater
        // than zero, or we are inside an element that is not supported
        if (isIgnorable() || (this.foreignNSElts.top() != null))
            // logger.info("");
            return;

        // only a subset of the elements of the known namespaces can be
        // processed, the namespace is empty in case of a dtd instead of
        // xml schema
        if (uri.equals(NAMESPACE) || uri.equals(NAMESPACE2)
                || uri.equals(OLD_NAMESPACE) || uri.equals("")) {
            // handle nested graphs
            if (qName.equals("graph")) {
                ++this.nesting;
                assert this.nesting >= 0 : "nesting level less than zero.";

                if (logger.isLoggable(Level.FINE)) {
                    logger.fine(((this.nesting == 0) ? "" : "embedded ")
                            + "graph detected");
                }
            }

            // hyperedges are ignored
            else if (qName.equals("hyperedge")) {
                logger.fine("hyperedge detected");
                this.hyperedge = true;
            }

            // locators are ignored
            else if (qName.equals("locator")) {
                logger.fine("locator detected");
                this.loc = true;
            }

            // nothing to be done for graphml elements
            else if (qName.equals("graphml")) {
                logger.fine("graphml detected");

                return;
            }

            // descriptions are also ignored
            else if (qName.equals("desc")) {
                logger.fine("desc detected - ignored.");
                this.desc = true;
            }

            // the rest is passed to the parent parser
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("call the parent parser to handle element " + qName
                        + ".");
            }
            super.startElement(uri, localName, qName, atts);
        }

        // handle elements from foreign namespaces
        else {
            this.foreignNSElts.push(qName);
        }
    }

    /**
     * Returns <code>true</code> if the corresponding data - i.e. element or
     * character data can be ignored. This is the case if it occurs within an
     * embedded graph, a hyperedge etc. Otherwise it returns <code>false</code>.
     * 
     * @return <code>true</code> if the corresponding data can be ignored,
     *         otherwise <code>false</code>.
     */
    private boolean isIgnorable() {
        return (this.nesting > 0) || this.hyperedge || this.loc || this.desc;
    }

    /**
     * This stack is designed in order to keep track of the nesting of elements
     * from foreign namespaces.
     */
    private class ElementStack {
        /** The stack. */
        private List<String> stack;

        /**
         * Creates a new <code>ElementStack</code> object.
         */
        ElementStack() {
            this.stack = new LinkedList<String>();
        }

        /**
         * Returns a <code>String</code> representation of the current content
         * of the stack - only for debugging purposes!
         * 
         * @return a <code>String</code> representation of the current content
         *         of the stack.
         */
        @Override
        public String toString() {
            if (this.stack.isEmpty())
                return "";
            else {
                String s = "";

                for (String string : this.stack) {
                    s += string + " ";
                }

                return s;
            }
        }

        /**
         * Removes and returns the top element of the stack, returns
         * <code>null</code> if the stack is empty
         * 
         * @return the top element of the stack, <code>null</code> if the stack
         *         is empty.
         */
        String pop() {
            if (this.stack.isEmpty())
                return null;
            else
                return this.stack.remove(0);
        }

        /**
         * Adds another element on top of the stack.
         * 
         * @param s
         *            the element to be added.
         * 
         * @throws RuntimeException
         *             DOCUMENT ME!
         */
        void push(String s) {
            if (s.equals("graphml"))
                throw new RuntimeException("should not happen");

            this.stack.add(s);
        }

        /**
         * Returns the top element, <code>null</code> if the stack is empty.
         * 
         * @return the top element, <code>null</code> if the stack is empty.
         */
        String top() {
            if (this.stack.isEmpty())
                return null;
            else
                return this.stack.get(this.stack.size() - 1);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
