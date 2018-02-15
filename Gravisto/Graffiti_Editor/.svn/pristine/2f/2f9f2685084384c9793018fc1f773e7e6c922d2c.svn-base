package org.graffiti.plugins.ios.importers.treeml;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.io.AbstractInputSerializer;
import org.graffiti.plugin.io.InputSerializer;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Reads a tree from a TreeML file.
 */
public class TreeMLReader extends AbstractInputSerializer implements
        InputSerializer {

    /**
     * Creates the tree given in the input stream in the given Graph.
     * 
     * @param in
     *            The stream containing the TreeML file.
     * @param graph
     *            The Graph in which the parsed tree is to be saved.
     */
    @Override
    public void read(InputStream in, Graph graph) throws IOException {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        spf.setValidating(true);

        SAXParser parser = null;
        try {
            parser = spf.newSAXParser();
        } catch (ParserConfigurationException e) {
            throw new IOException("Cannot configure parser.");
        } catch (SAXException e) {
            throw new IOException(e.getMessage());
        }

        DefaultHandler handler = new TreeMLSAXHandler(graph);
        try {
            parser.parse(in, handler);
        } catch (SAXException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Returns the extensions used for treeML files.
     * 
     * <code>.xml</code> will not be used, because in Gravisto this is
     * associated with GraphML files. Instead <code>.tml</code> and
     * <code>.treeml</code> are used.
     * 
     * @return The extensions used for TreeML.
     */
    public String[] getExtensions() {
        String[] extensions = { ".tml", ".treeml" };
        return extensions;
    }

    public String getName() {
        return "TreeML Importer";
    }

}
