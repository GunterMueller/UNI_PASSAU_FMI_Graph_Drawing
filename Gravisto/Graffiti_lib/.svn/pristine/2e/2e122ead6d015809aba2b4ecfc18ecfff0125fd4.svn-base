package org.graffiti.plugins.ios.importers.ontology.xml;

import java.util.List;

import org.graffiti.plugins.ios.importers.ontology.model.Ontology;
import org.graffiti.plugins.ios.importers.ontology.xml.util.ElementState;
import org.graffiti.util.xml.XmlElement;

/**
 * The rdfs:Class element.
 */
public class RdfsClass extends XmlElement {

    /**
     * This element's rdf:about attribute.
     */
    public String rdfAbout = null;

    /**
     * This element's rdfs:label attribute.
     */
    public String rdfsLabel = null;

    private Object id = null;

    private List<Object> superClasses = new java.util.ArrayList<Object>();

    /**
     * Notifies this element that its {@link RdfsSubClassOf} child-element has
     * been parsed.
     * 
     * @param child
     *            this element's {@link RdfsSubClassOf} child-element.
     */
    public void end(RdfsSubClassOf child) {
        superClasses.add(child.classId());
    }

    /**
     * Adds this class to the given ontology.
     * 
     * @param ontology
     *            the ontology to add this class to.
     */
    @Override
    public void processWith(Object ontology) {
        Ontology ontology_ = Ontology.cast(ontology);
        ontology_.defineClass(id(), rdfsLabel);
        for (Object superClass : superClasses) {
            ontology_.defineSuperClassProperty(id(), superClass);
        }
    }

    private Object id() {
        String[] state = { rdfAbout };
        if (id == null) {
            id = ElementState.generateId(state, new Object());
        }
        return id;
    }

}