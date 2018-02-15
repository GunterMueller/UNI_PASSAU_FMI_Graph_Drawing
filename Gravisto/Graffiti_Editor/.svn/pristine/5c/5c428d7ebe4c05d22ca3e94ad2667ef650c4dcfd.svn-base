package org.graffiti.plugins.ios.importers.ontology.xml;

import org.graffiti.plugins.ios.importers.ontology.model.Class;
import org.graffiti.plugins.ios.importers.ontology.xml.util.ElementState;
import org.graffiti.util.xml.XmlElement;

/**
 * The owl:HasValue element.
 * 
 * @author Harald Frankenberger
 */
public class OwlHasValue extends XmlElement {
    /**
     * This element's rdf:resource attribute.
     */
    public String rdfResource = null;

    private Object childId = null;

    /**
     * Returns the id of the {@link Class} this element refers to.
     * 
     * @return the id of the {@link Class} this element refers to.
     */
    public Object classId() {
        String[] state = { rdfResource };
        if (childId == null) {
            childId = ElementState.generateId(state, new Object());
        }
        return childId;
    }

}
