package org.graffiti.plugins.ios.importers.ontology.xml;

import org.graffiti.plugins.ios.importers.ontology.model.Class;
import org.graffiti.plugins.ios.importers.ontology.xml.util.ElementState;
import org.graffiti.util.xml.XmlElement;

/**
 * The owl:someValuesFrom element.
 * 
 * @author Harald Frankenberger
 */
public class OwlSomeValuesFrom extends XmlElement {

    /**
     * This element's rdf:resource attribute.
     */
    public String rdfResource = null;

    private Object classId = null;

    /**
     * Notifies this element that its {@link OwlClass} child-element has been
     * parsed.
     * 
     * @param c
     *            this element's {@link OwlClass} child-element.
     */
    public void end(OwlClass c) {
        classId = c.id();
    }

    /**
     * Returns the id of the {@link Class} referred to by this element.
     * 
     * @return the id of the {@link Class} referred to by this element.
     */
    public Object classId() {
        if (classId == null) {
            classId = ElementState.generateId(rdfResource);
        }
        return classId;
    }

}
