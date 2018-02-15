package org.graffiti.plugins.ios.importers.treeOfLife;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.io.InputSerializer;
import org.graffiti.plugins.ios.importers.treeml.TreeMLReader;

/**
 * Class to read and process files in the Tree Of Life XML format.
 * 
 * The ToL-XML files are transformed into XML files conforming to the TreeML
 * standard for trees using XSLT and automatically loaded by the TreeML Reader.
 */
public class ToLReader extends TreeMLReader implements InputSerializer {

    /**
     * Reads an input stream consisting of a ToL XML file and loads it into the
     * given Graph.
     * 
     * @param in
     *            The stream containing the ToL file.
     * @param g
     *            The Graph in which the parsed tree is to be saved.
     */
    @Override
    public void read(InputStream in, Graph g) throws IOException {
        URL pathToXSL = getClass().getResource("tolxml2treeml.xsl");

        if (pathToXSL == null)
            throw new IOException("Cannot parse file: XSL file is missing");

        try {
            Source tolSource = new StreamSource(in);
            StreamSource xslSource = new StreamSource(pathToXSL
                    .toExternalForm());

            TransformerFactory factory = TransformerFactory.newInstance();

            Transformer transformer = factory.newTransformer(xslSource);

            ByteArrayOutputStream transformedXML = new ByteArrayOutputStream();
            Result res = new StreamResult(transformedXML);

            transformer.transform(tolSource, res);

            in = new ByteArrayInputStream(transformedXML.toByteArray());
            super.read(in, g);
        } catch (TransformerConfigurationException e) {
            throw new IOException(e.getMessage());
        } catch (TransformerException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Returns the extensions used for Tree of Life files.
     * 
     * <code>.xml</code> will not be used, because in Gravisto this is
     * associated with GraphML files. Instead <code>.tol</code> is used.
     * 
     * @return The extensions used for files in the TreeOfLife-XML-format.
     */
    @Override
    public String[] getExtensions() {
        String[] extensions = { ".tol" };
        return extensions;
    }
}
