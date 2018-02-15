package org.graffiti.plugins.ios.importers.ontology.xml;

import org.graffiti.plugins.ios.importers.ontology.model.Property;
import org.graffiti.plugins.ios.importers.ontology.xml.util.ElementState;
import org.graffiti.util.xml.XmlElement;

/**
 * The owl:onProperty element.
 * 
 * @author Harald Frankenberger
 */
public class OwlOnProperty extends XmlElement {

    /**
     * This element's rdf:resource attribute
     */
    public String rdfResource = null;

    private Object propertyId = null;

    /**
     * Notifies this element that its {@link OwlObjectProperty} child-element
     * has been parsed.
     * 
     * @param child
     *            this element's {@link OwlObjectProperty} child-element
     */
    public void end(OwlObjectProperty child) {
        propertyId = child.propertyId();
    }

    /**
     * Notifies this element that its {@link OwlFunctionalProperty}
     * child-element has been parsed.
     * 
     * @param child
     *            this element's {@link OwlFunctionalProperty} child-element.
     */
    public void end(OwlFunctionalProperty child) {
        propertyId = child.propertyId();
    }

    /**
     * Returns the id of the {@link Property} this element refers to.
     * 
     * @return the id of the {@link Property} this element refers to.
     */
    public Object propertyId() {
        if (propertyId == null) {
            propertyId = ElementState.generateId(rdfResource);
        }
        return propertyId;
    }
}
