package org.graffiti.plugins.ios.importers.ontology.xml;

import org.graffiti.plugins.ios.importers.ontology.model.Class;
import org.graffiti.plugins.ios.importers.ontology.xml.util.ElementState;
import org.graffiti.util.xml.XmlElement;

/**
 * The rdfs:subClassOf element.
 * 
 * @author Harald Frankenberger
 */
public class RdfsSubClassOf extends XmlElement {

    /**
     * This element's rdf:resource attribute.
     */
    public String rdfResource = null;

    private Object classId;

    /**
     * Notifies this element that its {@link OwlClass} child-element has been
     * parsed.
     * 
     * @param child
     *            this element's {@link OwlClass} child-element.
     */
    public void end(OwlClass child) {
        classId = child.id();
    }

    /**
     * Notifies this element that its {@link OwlRestriction} child-element has
     * been parsed.
     * 
     * @param child
     *            this element's {@link OwlRestriction} child-element.
     */
    public void end(OwlRestriction child) {
        classId = child.classId();
    }

    /**
     * Returns the id of the {@link Class} this element refers to.
     * 
     * @return the id of the {@link Class} this element refers to.
     */
    public Object classId() {
        if (classId == null) {
            classId = ElementState.generateId(rdfResource);
        }
        return classId;
    }

}