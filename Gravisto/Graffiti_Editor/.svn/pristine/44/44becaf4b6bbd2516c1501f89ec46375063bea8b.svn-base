package org.graffiti.plugins.ios.importers.ontology.xml;

import java.util.List;

import org.graffiti.plugins.ios.importers.ontology.model.Class;
import org.graffiti.plugins.ios.importers.ontology.model.Ontology;
import org.graffiti.plugins.ios.importers.ontology.xml.util.ElementState;
import org.graffiti.util.xml.XmlElement;

/**
 * The owl:Class element.
 * 
 * @author Harald Frankenberger
 */
public class OwlClass extends XmlElement {

    /**
     * The resource-string identifying the resource this element describes.
     */
    public String rdfAbout = null;

    /**
     * The resource-string identifying this element.
     */
    public String rdfID = null;

    private Object id = null;

    private List<Object> superClasses = new java.util.ArrayList<Object>();

    /**
     * Notifies this element that its {@link RdfsSubClassOf} child element has
     * been parsed.
     * 
     * @param child
     *            this element's {@link RdfsSubClassOf} child element
     */
    public void end(RdfsSubClassOf child) {
        superClasses.add(child.classId());
    }

    /**
     * Processes this element with the given ontology.
     * 
     * @param ontology
     *            the ontology to process this element with
     */
    @Override
    public void processWith(Object ontology) {

        Ontology ontology_ = Ontology.cast(ontology);
        if (rdfAbout == null && rdfID == null) {
            ontology_.defineClass(id());
        } else {
            ontology_.defineClass(id(), id().toString());
        }
        for (Object superClassId : superClasses) {
            ontology_.defineSuperClassProperty(id(), superClassId);
        }
    }

    /**
     * Returns the id of this element's {@link Class}.
     * 
     * @return the id of this element's {@link Class}.
     */
    public Object id() {
        String[] state = { rdfID, rdfAbout };
        if (id == null) {
            id = ElementState.generateId(state, new Object());
        }
        return id;
    }
}
