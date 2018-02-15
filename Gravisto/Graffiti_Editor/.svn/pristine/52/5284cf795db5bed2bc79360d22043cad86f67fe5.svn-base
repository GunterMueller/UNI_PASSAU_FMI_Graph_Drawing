package org.graffiti.plugins.ios.importers.ontology.xml;

import org.graffiti.plugins.ios.importers.ontology.model.Ontology;
import org.graffiti.plugins.ios.importers.ontology.model.Property;
import org.graffiti.plugins.ios.importers.ontology.xml.util.ElementState;
import org.graffiti.util.xml.XmlElement;

/**
 * The owl:FunctionalProperty element.
 * 
 * @author Harald Frankenberger
 */
public class OwlFunctionalProperty extends XmlElement {

    /**
     * The rdf:ID attribute of this element.
     */
    public String rdfID = null;

    /**
     * The rdf:about attribute of this element.
     */
    public String rdfAbout = null;

    /**
     * The rdfs:label attribute of this element.
     */
    public String rdfsLabel = null;

    private Object id = null;

    private Object domain;

    private Object range;

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
     * @param child
     *            this element's {@link RdfsRange} child-element
     */
    public void end(RdfsRange child) {
        range = child.classId();
    }

    /**
     * Adds this functional property to the given {@link Ontology}.
     * 
     * @param ontology
     *            the {@link Ontology} to add this functional property to
     */
    @Override
    public void processWith(Object ontology) {
        Ontology ontology_ = Ontology.cast(ontology);
        if (domain != null && range != null) {
            ontology_.defineFunctionalProperty(propertyId(), domain,
                    propertyId().toString(), range);
        }
    }

    /**
     * Returns the id of this element's {@link Property}.
     * 
     * @return the id of this element's {@link Property}.
     */
    public Object propertyId() {
        if (id == null) {
            id = ElementState.generateId(rdfID, rdfAbout);
        }
        return id;
    }
}
