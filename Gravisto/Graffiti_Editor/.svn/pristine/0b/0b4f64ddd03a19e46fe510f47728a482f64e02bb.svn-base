package tests.graffiti.util.xml;

import static org.junit.Assert.assertNotSame;

import org.junit.Test;

public class XmlElementFactoryTest {
    @Test
    public void newXmlElement() throws ClassNotFoundException,
            InstantiationException, IllegalAccessException {
        XmlElementFactory factory = new XmlElementFactory(
                "org.graffiti.plugins.ios.importers.ontology.xml");
        String qName = "owl:Class";
        XmlElement previousElement = factory.newXmlElement(qName);
        assertNotSame(previousElement, factory.newXmlElement(qName));
    }
}
