package org.graffiti.plugins.ios.importers.ontology.xml;

import org.graffiti.plugins.ios.importers.ontology.model.Class;
import org.graffiti.plugins.ios.importers.ontology.xml.util.ElementState;
import org.graffiti.util.xml.XmlElement;

/**
 * The owl:AllValuesFrom element.
 * 
 * @author Harald Frankenberger
 */
public class OwlAllValuesFrom extends XmlElement {

    /**
     * The resource-string identifying the class this element refers to.
     */
    public String rdfResource = null;

    private Object childId;

    /**
     * Notifies this element that its {@link OwlClass} child-element has been
     * parsed.
     * 
     * @param child
     *            the child element of this element
     */
    public void end(OwlClass child) {
        childId = child.id();
    }

    /**
     * Returns the id of the {@link Class} this element refers to.
     * 
     * @return the id of the {@link Class} this element refers to.
     */
    public Object classId() {
        if (childId == null) {
            childId = ElementState.generateId(rdfResource);
        }
        return childId;
    }

}
