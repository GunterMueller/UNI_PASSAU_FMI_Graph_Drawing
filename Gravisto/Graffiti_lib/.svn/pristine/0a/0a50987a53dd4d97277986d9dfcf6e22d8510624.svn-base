package tests.graffiti.io.ontology;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.lowagie.text.xml.XmlParser;

public class XmlParserTest {
    Ontology ontology = new Ontology();
    XmlParser parser = new XmlParser(
            "org.graffiti.plugins.ios.importers.ontology.xml", ontology);

    @Test
    public void parses_Pizza_owl() throws SAXException, IOException,
            ParserConfigurationException {
        parser.parse(new File("files/Pizza.owl"));
        String domainConcept = "DomainConcept";
        String pizza = "Pizza";
        assertTrue(ontology.definesSuperClassProperty(pizza, domainConcept));
    }

    @Test
    public void parses_Newpaper_rdfs() throws SAXException, IOException,
            ParserConfigurationException {
        parser.parse(new File("files/Newspaper.rdfs"));
        String ns = "rdf";
        String reporter = ns + "Reporter";
        String author = ns + "Author";
        String employee = ns + "Employee";
        assertTrue(ontology.definesSuperClassProperty(reporter, author));
        assertTrue(ontology.definesSuperClassProperty(reporter, employee));

    }

    @Test
    public void parseObjectProperty() throws SAXException, IOException,
            ParserConfigurationException {
        parser.parse(new File("files/3OwlObjectProperties.owl"));
        assertEquals(ontology.properties().toString(), 3, ontology
                .propertyCount());
        assertTrue(ontology.properties().toString(), ontology
                .definesObjectProperty("objectProperty_1"));
    }

    @Test
    public void parseFunctionalProperty() throws SAXException, IOException,
            ParserConfigurationException {
        parser.parse(new File("files/FunctionalProperty.owl"));
        assertEquals(ontology.properties().toString(), 3, ontology
                .propertyCount());
    }

    @Test
    public void parseDifferentReferences() throws SAXException, IOException,
            ParserConfigurationException {
        parser.parse(new File("files/DifferentReferences.owl"));
        assertEquals(ontology.classIdSet().toString(), 2, ontology.classCount());
    }
}
