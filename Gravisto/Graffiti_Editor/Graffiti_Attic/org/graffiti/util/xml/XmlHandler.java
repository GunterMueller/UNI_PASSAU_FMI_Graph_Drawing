package org.graffiti.util.xml;

import java.util.Stack;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handles callbacks by a sax-parser; used by the {@link XmlParser}-class in the
 * same package. Callbacks are basically reflective method invocations on
 * {@link XmlDocument}s or {@link XmlElement}s. For each callback
 * {@link #startElement(String, String, String, org.xml.sax.Attributes)} this
 * class invokes {@link CallbackHandler#invokeStart(XmlElement)} on the parent
 * of the element that is getting started. The same procedure is followed for
 * {@link #endElement(String, String, String)} callbacks with one difference:
 * Before {@link CallbackHandler#end(XmlElement)} is called the child element is
 * given a chance to process itself with this handler's processor. This happens
 * as a call to {@link XmlElement#processWith(Object)}.
 * 
 * @see XmlParser
 * @see CallbackHandler
 * @see XmlElement
 * @see XmlElementFactory
 * @author Harald Frankenberger
 */
public class XmlHandler extends DefaultHandler {

    private final Object processor;

    private final XmlElementFactory xmlElementFactory;

    private final XmlDocument document;

    private final Stack<XmlElement> stack;

    /**
     * The default-document used by xml-handlers.
     */
    public static final XmlDocument DEFAULT_DOCUMENT = new XmlDocument();

    /**
     * Creates a new xml-handler using the given {@link XmlDocument}, an
     * {@link XmlElementFactory} for the given package and the given processor.
     * 
     * @param d
     *            the {@link XmlDocument} to use with this xml-handler
     * @param packageName
     *            the package where the {@link XmlElement}s reside
     * @param elementProcessor
     *            the processor to use with this xml-handler
     */
    public XmlHandler(XmlDocument d, String packageName, Object elementProcessor) {
        document = d;
        xmlElementFactory = new XmlElementFactory(packageName);
        processor = elementProcessor;
        stack = new Stack<XmlElement>();
    }

    /**
     * Creates an xml-handler using {@link XmlHandler#DEFAULT_DOCUMENT} as this
     * handler's document, an {@link XmlElementFactory} for the given
     * package-name and the given processor.
     * 
     * @param packageName
     *            the package where the {@link XmlElement}s reside
     * @param elementProcessor
     *            the processor to use with this xml-handler
     */
    public XmlHandler(String packageName, Object elementProcessor) {
        this(DEFAULT_DOCUMENT, packageName, elementProcessor);
    }

    /**
     * Starts this xml-handlers document.
     * 
     * @see XmlDocument#start()
     */
    @Override
    public void startDocument() throws SAXException {
        document.start();
    }

    /**
     * Starts the specified element. This method first creates an
     * {@link XmlElement} using this handler's {@link XmlElementFactory}. It
     * sets the fields on the newly created {@link XmlElement} to their
     * respective attribute values and invokes
     * {@link CallbackHandler#start(XmlElement)} on the newly created
     * {@link XmlElement}'s parent.
     * 
     * @see XmlElementFactory#newXmlElement(String)
     * @see XmlElement#set(String, String)
     * @see CallbackHandler#invokeStart(XmlElement)
     */
    @Override
    public void startElement(String uri, String localName, String qName,
            org.xml.sax.Attributes attributes) throws SAXException {
        Stack<XmlElement> stack = this.stack;
        XmlDocument bottom = document;
        CallbackHandler parent = peek(stack, bottom);
        XmlElement child = xmlElementFactory.newXmlElement(qName);
        for (int i = 0; i < attributes.getLength(); i++) {
            String attribute = attributes.getQName(i);
            String value = attributes.getValue(i);
            child.set(attribute, value);
        }
        parent.invokeStart(child);
        stack.push(child);
    }

    /**
     * Ends the specified element. This method will first call
     * {@link XmlElement#processWith(Object)} on the specified element. Then it
     * will invoke {@link CallbackHandler#invokeEnd(XmlElement)} on the
     * specified {@link XmlElement}'s parent element.
     * 
     * @see XmlElement#processWith(Object)
     * @see CallbackHandler#invokeEnd(XmlElement)
     */
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        XmlElement child = stack.pop();
        child.processWith(processor);
        CallbackHandler parent = peek(stack, document);
        parent.invokeEnd(child);
    }

    /**
     * Ends this handler's document.
     * 
     * @see XmlDocument#end()
     */
    @Override
    public void endDocument() throws SAXException {
        document.end();
    }

    /**
     * Sets the character data for the {@link XmlElement} that was started last.
     * This method will call {@link XmlElement#characterData(String)} with the
     * string that starts at <code>ch[start]</code> and ends at
     * <code>ch[start + length - 1]</code>.
     * 
     * @see XmlElement#characterData(String)
     */
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        char[] characterData = new char[length];
        System.arraycopy(ch, start, characterData, 0, length);
        String value = new String(characterData);
        XmlElement element = stack.peek();
        element.characterData(value);
    }

    private CallbackHandler peek(Stack<XmlElement> stack, XmlDocument bottom) {
        CallbackHandler parent;
        if (stack.isEmpty()) {
            parent = bottom;
        } else {
            parent = stack.peek();
        }
        return parent;
    }

}
