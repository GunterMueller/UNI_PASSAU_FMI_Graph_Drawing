package org.graffiti.plugins.ios.importers.ontology.xml;

import org.graffiti.plugins.ios.importers.ontology.model.Class;
import org.graffiti.plugins.ios.importers.ontology.xml.util.ElementState;
import org.graffiti.util.xml.XmlElement;

/**
 * The rdfs:range element.
 * 
 * @author Harald Frankenberger
 */
public class RdfsRange extends XmlElement {

    /**
     * This element's rdf:resource element.
     */
    public String rdfResource = null;

    /**
     * Returns the id of the {@link Class} this element refers to.
     * 
     * @return the id of the {@link Class} this element refers to.
     */
    public Object classId() {
        return ElementState.generateId(rdfResource);
    }

}