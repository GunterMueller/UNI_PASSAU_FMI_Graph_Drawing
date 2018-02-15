package org.graffiti.plugins.ios.importers.ontology.xml;

import org.graffiti.plugins.ios.importers.ontology.model.Ontology;
import org.graffiti.plugins.ios.importers.ontology.xml.util.ElementState;
import org.graffiti.util.xml.XmlElement;

/**
 * The rdf:Property element.
 * 
 * @author Harald Frankenberger
 */
public class RdfProperty extends XmlElement {
    /**
     * This element's rdf:about attribute.
     */
    public String rdfAbout = null;

    /**
     * This element's rdf:label attribute.
     */
    public String rdfsLabel = null;

    private Object id = null;

    private Object domain = null;

    private Object range = null;

    /**
     * Notifies this element that its {@link RdfsDomain} child-element has been
     * parsed.
     * 
     * @param child
     *            this element's {@link RdfsDomain} child-element.
     */
    public void end(RdfsDomain child) {
        domain = child.classId();
    }

    /**
     * Notifies this element that its {@link RdfsRange} child-element has been
     * parsed.
     * 
     * @param child
     *            this element's {@link RdfsRange} child-element.
     */
    public void end(RdfsRange child) {
        range = child.classId();
    }

    /**
     * Adds this property to the given ontology.
     * 
     * @param ontology
     *            the ontology to add this property to.
     */
    @Override
    public void processWith(Object ontology) {
        Object id = id();
        String label = rdfsLabel;
        if (domain != null && range != null) {
            Ontology.cast(ontology).defineObjectProperty(id, domain.toString(),
                    label, range.toString());
        }
    }

    private Object id() {
        if (id == null) {
            id = ElementState.generateId(rdfAbout);
        }
        return id;
    }
}
