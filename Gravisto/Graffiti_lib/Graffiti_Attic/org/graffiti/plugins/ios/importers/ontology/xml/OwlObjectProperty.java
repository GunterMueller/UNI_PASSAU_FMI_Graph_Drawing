package org.graffiti.plugins.ios.importers.ontology.xml;

import org.graffiti.plugins.ios.importers.ontology.model.Ontology;
import org.graffiti.plugins.ios.importers.ontology.model.Property;
import org.graffiti.plugins.ios.importers.ontology.xml.util.ElementState;
import org.graffiti.util.xml.XmlElement;

/**
 * The owl:ObjectProperty element.
 * 
 * @author Harald Frankenberger
 */
public class OwlObjectProperty extends XmlElement {

    private Object id = null;

    private Object domain;

    private Object range;

    /**
     * This element's rdf:about attribute
     */
    public String rdfAbout = null;

    /**
     * This element's rdf:ID attribute
     */
    public String rdfID = null;

    /**
     * Notifies this element that its {@link RdfsDomain} child-element has been
     * parsed.
     * 
     * @param child
     *            this element's {@link RdfsDomain} child-element
     */
    public void end(RdfsDomain child) {
        domain = child.classId();
    }

    /**
     * Notifies this element that its {@link RdfsRange} child-element has been
     * parsed.
     * 
     * @param r
     *            this element's {@link RdfsRange} child-element
     */
    public void end(RdfsRange r) {
        range = r.classId();
    }

    /**
     * Adds this object property to the given ontology.
     * 
     * @param ontology
     *            the ontology to add this property to
     */
    @Override
    public void processWith(Object ontology) {
        if (domain != null && range != null) {
            Ontology.cast(ontology).defineObjectProperty(
                    propertyId().toString(), domain, propertyId().toString(),
                    range);
        }
    }

    /**
     * Returns the id of the {@link Property} this element represents.
     * 
     * @return the id of the {@link Property} this element represents.
     */
    public Object propertyId() {
        if (id == null) {
            id = ElementState.generateId(rdfID, rdfAbout);
        }
        return id;
    }
}
