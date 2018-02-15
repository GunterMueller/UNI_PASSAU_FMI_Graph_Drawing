package org.graffiti.plugins.ios.importers.ontology;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.graffiti.plugin.io.AbstractInputSerializer;
import org.graffiti.plugins.ios.importers.ontology.model.Ontology;
import org.graffiti.util.xml.XmlParser;
import org.xml.sax.SAXException;

/**
 * Builds graphs from rdfs- and owl-files.
 * 
 * @author Harald Frankenberger
 */
public class OntologyReader extends AbstractInputSerializer {

    /**
     * Builds a graph from the given <tt>InputStream</tt>.
     * 
     * @param in
     *            the <tt>InputStream</tt> pointing to an rdfs- or owl-file.
     * @param g
     *            the <tt>Graph</tt> that will contain the classes and
     *            properties as nodes and edges.
     */
    @Override
    public void read(InputStream in, org.graffiti.graph.Graph g)
            throws IOException {
        Ontology ontology = new Ontology();
        try {
            new XmlParser("org.graffiti.plugins.ios.importers.ontology.xml",
                    ontology).parse(in);
        } catch (ParserConfigurationException e) {
            throw new OntologyImportException(e);
        } catch (SAXException e) {
            throw new OntologyImportException(e);
        }
        ontology.importTo(new Graph(g));
    }

    /**
     * Returns <code>new String[]{".rdfs",".owl"}</code>
     * 
     * @return <code>new String[]{".rdfs",".owl"}</code>
     */
    public String[] getExtensions() {
        return new String[] { ".rdfs", ".owl" };
    }

    public String getName() {
        return "Ontology Importer";
    }

}