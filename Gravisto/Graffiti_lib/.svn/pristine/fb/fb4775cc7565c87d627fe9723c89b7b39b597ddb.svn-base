package org.graffiti.util.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

/**
 * Parses xml-documents.
 * 
 * @author Harald Frankenberger
 */
public class XmlParser {
    private Object processor;

    private final String packageName;

    /**
     * Creates an xml-parser.
     * 
     * @param packageName
     *            the package used to load xml-elements.
     * @param elementProcessor
     *            the processor used to process xml-elements.
     */
    public XmlParser(String packageName, Object elementProcessor) {
        this.packageName = packageName;
        processor = elementProcessor;
    }

    /**
     * Parses the given file.
     * 
     * @param file
     *            the file to parse.
     */
    public void parse(File file) throws SAXException, IOException,
            ParserConfigurationException {
        parser().parse(file, new XmlHandler(packageName, processor));
    }

    /**
     * Parses the given input-stream.
     * 
     * @param stream
     *            the input-stream to parse from.
     */
    public void parse(InputStream stream) throws SAXException, IOException,
            ParserConfigurationException {
        parser().parse(stream, new XmlHandler(packageName, processor));
    }

    // Helpers

    private SAXParser parser() throws ParserConfigurationException,
            SAXException {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        parserFactory.setValidating(true);
        parserFactory.setNamespaceAware(true);
        SAXParser parser = parserFactory.newSAXParser();
        return parser;
    }

}
