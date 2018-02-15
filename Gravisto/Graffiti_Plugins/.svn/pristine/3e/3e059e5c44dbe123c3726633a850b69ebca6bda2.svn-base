package tests.graffiti.io.ontology;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.lowagie.text.xml.XmlParser;

public class XmlHandlerTest {
    // XMLGraph graph = new XMLGraph();

    Ontology ontology = new Ontology();

    private XmlHandler xmlHandler = new XmlHandler(
            "org.graffiti.plugins.ios.importers.ontology.xml", ontology);

    private XmlParser parser = new XmlParser(
            "org.graffiti.plugins.ios.importers.ontology.xml", ontology);// ,
                                                                         // rdfOwlElements);

    private String uri = "";

    private String localName = "";

    private String type = "";

    @Test
    public void handlesOwlClasses() throws ParserConfigurationException,
            SAXException, IOException {
        File _3Classes = new File("files/3Classes.owl");
        parser.parse(_3Classes);
        assertEquals(3, ontology.classCount());
    }

    @Test
    public void handlesMultipleInheritanceInOwlFiles()
            throws ParserConfigurationException, SAXException, IOException {
        File _OwlMultipleInheritance = new File(
                "files/OwlMultipleInheritance.owl");
        parser.parse(_OwlMultipleInheritance);
        assertEquals(ontology.classIdSet().toString(), 3, ontology.classCount());
        assertEquals(ontology.properties().toString(), 2, ontology
                .propertyCount());
    }

    @Test
    @Ignore
    public void superClassClosureForReporter() throws SAXException,
            IOException, ParserConfigurationException {
        File _SuperClassClosureForReporter = new File(
                "files/SuperClassClosureForReporter.rdfs");
        parser.parse(_SuperClassClosureForReporter);
        assertEquals(ontology.classIdSet().toString(), 4, ontology.classCount());
        assertEquals(ontology.properties().toString(), 3, ontology
                .propertyCount());

    }

    @Test
    public void handlesOwlFiles() throws ParserConfigurationException,
            SAXException, IOException {
        /*
         * SAXParserFactory parserFactory = SAXParserFactory.newInstance();
         * parserFactory.setNamespaceAware(true); SAXParser parser =
         * parserFactory.newSAXParser();
         */
        File _3Classes3Properties = new File("files/3Classes3Properties.owl");
        /*
         * parser.parse(_3Classes3Properties, xmlHandler);
         */
        parser.parse(_3Classes3Properties);
        assertEquals(3, ontology.classCount());
        assertEquals(3, ontology.propertyCount());
    }

    @Test
    public void handlesMultipleInheritanceCorrectly() throws SAXException,
            ParserConfigurationException, IOException {
        File multipleInheritance = new File("files/MultipleInheritance.rdfs");
        parser.parse(multipleInheritance);
        assertEquals(ontology.classIdSet().toString(), 3, ontology.classCount());
        assertEquals(ontology.properties().toString(), 2, ontology
                .propertyCount());
    }

    @Test
    public void parsesATestFileCorrectly() throws ParserConfigurationException,
            SAXException, IOException {
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        File _3Classes2PropertiesRdfs = new File(
                "files/3Classes2Properties.rdfs");
        parser.parse(_3Classes2PropertiesRdfs, xmlHandler);
        assertEquals(ontology.classIdSet().toString(), 3, ontology.classCount());
        assertEquals(ontology.properties().toString(), 2, ontology
                .propertyCount());
    }

    @Test
    public void transformsEveryRdfsClassIntoANode() throws SAXException {
        AttributesImpl attributes = new AttributesImpl();
        xmlHandler.startDocument();
        attributes.addAttribute("", "", "rdf:about", "", "Class");
        xmlHandler.startElement("", "", "rdfs:Class", attributes);
        xmlHandler.endElement("", "", "rdfs:Class");
        xmlHandler.endDocument();
        assertEquals(1, ontology.classCount());
        assertTrue(ontology.definesClass("Class"));
    }

    @Test
    public void labelsEveryNodeWithTheCorrespondingRdfsClass_label_attribute()
            throws SAXException {
        AttributesImpl attributes = new AttributesImpl();
        xmlHandler.startDocument();
        attributes.addAttribute("", "", "rdf:about", "", "Class1");
        attributes.addAttribute("", "", "rdfs:label", "", "Class1");
        xmlHandler.startElement("", "", "rdfs:Class", attributes);
        xmlHandler.endElement("", "", "rdfs:Class");
        xmlHandler.endDocument();
        assertTrue(ontology.definesClass("Class1"));
    }

    @Test
    public void setsTheIdOfEveryNodeToTheCorrespondingRdfsClass_about_attribute()
            throws SAXException {
        AttributesImpl attributes = new AttributesImpl();
        xmlHandler.startDocument();
        attributes.addAttribute("", "", "rdf:about", "", "http://test#Class1");
        attributes.addAttribute("", "", "rdfs:label", "", "Class1");
        xmlHandler.startElement("", "", "rdfs:Class", attributes);
        xmlHandler.endElement("", "", "rdfs:Class");
        xmlHandler.endDocument();
        assertTrue(ontology.definesClass("Class1"));
    }

    @Test
    public void generatesEdgesForProperties() throws SAXException, IOException,
            ParserConfigurationException {
        File _3OwlObjectProperties = new File("files/3OwlObjectProperties.owl");
        parser.parse(_3OwlObjectProperties);
        assertEquals(3, ontology.propertyCount());
    }

    @Test
    public void characters() {

    }

    @Test
    public void labelsEveryEdgeWithTheCorrespondingRdfProperty_label_attribute()
            throws SAXException {
        xmlHandler.startDocument();
        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute("", "", "rdfs:label", "", "Property1");
        attributes
                .addAttribute(uri, localName, "rdf:about", type, "#Property1");
        xmlHandler.startElement("", "", "rdf:Property", attributes);
        attributes.clear();
        attributes.addAttribute(uri, localName, "rdf:resource", type,
                "http://test/Class");
        xmlHandler.startElement(uri, localName, "rdfs:domain", attributes);
        xmlHandler.endElement(uri, localName, "rdfs:domain");
        xmlHandler.startElement(uri, localName, "rdfs:range", attributes);
        xmlHandler.endElement(uri, localName, "rdfs:range");
        xmlHandler.endElement("", "", "rdf:Property");
        xmlHandler.endDocument();
        assertTrue(ontology.properties().toString(), ontology
                .definesObjectProperty("Property1"));
    }

    @Test
    public void transformsEveryDomainElementIntoTheSourceNodeOfTheCorrespondingEdge()
            throws SAXException {
        xmlHandler.startDocument();
        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute("", "", "rdf:about", "", "Class");
        xmlHandler.startElement("", "", "rdfs:Class", attributes);
        xmlHandler.endElement("", "", "rdfs:Class");
        AttributesImpl attributes2 = new AttributesImpl();
        attributes2.addAttribute("", "", "rdf:about", "", "Property");
        xmlHandler.startElement("", "", "rdf:Property", attributes2);
        AttributesImpl attributes1 = new AttributesImpl();
        attributes1.addAttribute("", "", "rdf:resource", "", "Class");
        xmlHandler.startElement("", "", "rdfs:domain", attributes1);
        xmlHandler.endElement("", "", "rdfs:domain");
        attributes.clear();
        attributes.addAttribute(uri, localName, "rdf:resource", type, "Class");
        xmlHandler.startElement(uri, localName, "rdfs:range", attributes);
        xmlHandler.endElement(uri, localName, "rdfs:range");
        xmlHandler.endElement("", "", "rdf:Property");
        xmlHandler.endDocument();
        assertTrue(ontology.definesClass("Class"));
        assertEquals(new Property("Property", "Class", "Property", "Class"),
                ontology.objectPropertyFor("Property"));
    }

    @Test
    public void transformsEveryRangeElementIntoTheTargetNodeOfTheCorrespondingEdge()
            throws SAXException {
        xmlHandler.startDocument();
        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute("", "", "rdf:about", "", "Class1");
        xmlHandler.startElement("", "", "rdfs:Class", attributes);
        xmlHandler.endElement("", "", "rdfs:Class");
        AttributesImpl attributes2 = new AttributesImpl();
        attributes2.addAttribute("", "", "rdf:about", "", "Class2");
        xmlHandler.startElement("", "", "rdfs:Class", attributes2);
        xmlHandler.endElement("", "", "rdfs:Class");
        AttributesImpl attributes4 = new AttributesImpl();
        attributes4.addAttribute("", "", "rdf:about", "", "Property");
        xmlHandler.startElement("", "", "rdf:Property", attributes4);
        AttributesImpl attributes3 = new AttributesImpl();
        attributes3.addAttribute("", "", "rdf:resource", "", "Class1");
        xmlHandler.startElement("", "", "rdfs:domain", attributes3);
        xmlHandler.endElement("", "", "rdfs:domain");
        AttributesImpl attributes1 = new AttributesImpl();
        attributes1.addAttribute("", "", "rdf:resource", "", "Class2");
        xmlHandler.startElement("", "", "rdfs:range", attributes1);
        xmlHandler.endElement("", "", "rdfs:range");
        xmlHandler.endElement("", "", "rdf:Property");
        xmlHandler.endDocument();
        assertEquals(new Property("Property", "Class1", "Property", "Class2"),
                ontology.objectPropertyFor("Property"));
    }

    @Test
    public void ignoresAllOtherElements() throws SAXException {
        xmlHandler.startDocument();
        xmlHandler.startElement("", "", "rdfs:element", new AttributesImpl());
        xmlHandler.endElement(uri, localName, "rdfs:element");
        xmlHandler.startElement("", "", "rdfs:element", new AttributesImpl());
        xmlHandler.endElement(uri, localName, "rdfs:element");
        xmlHandler.startElement("", "", "rdfs:element", new AttributesImpl());
        xmlHandler.endElement(uri, localName, "rdfs:element");
        xmlHandler.endDocument();
        assertEquals(0, ontology.classCount());
    }

}
