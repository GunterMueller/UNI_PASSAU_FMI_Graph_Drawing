package org.graffiti.plugins.ios.importers.ontology.xml;

import org.graffiti.plugins.ios.importers.ontology.model.Class;
import org.graffiti.util.ext.Arrays;
import org.graffiti.util.ext.State;
import org.graffiti.util.xml.XmlElement;

/**
 * The owl:Restriction element.
 * 
 * @author Harald Frankenberger
 */
public class OwlRestriction extends XmlElement {

    private Object propertyId = null;

    private String restriction = null;

    private Object classId = null;

    /**
     * Notifies this element that its {@link OwlHasValue} child-element has been
     * parsed.
     * 
     * @param v
     *            this element's {@link OwlHasValue} child-element.
     */
    public void end(OwlHasValue v) {
        classId = v.classId();
        restriction = "has";
    }

    /**
     * Notifies this element that its {@link OwlOnProperty} child-element has
     * been parsed.
     * 
     * @param child
     *            this element's {@link OwlOnProperty} child-element.
     */
    public void end(OwlOnProperty child) {
        propertyId = child.propertyId();
    }

    /**
     * Notifies this element that its {@link OwlSomeValuesFrom} child-element
     * has been parsed.
     * 
     * @param child
     *            this element's {@link OwlSomeValuesFrom} child-element.
     */
    public void end(OwlSomeValuesFrom child) {
        classId = child.classId();
        restriction = "some";
    }

    /**
     * Notifies this element that its {@link OwlAllValuesFrom} child-element has
     * been parsed.
     * 
     * @param child
     *            this element's {@link OwlAllValuesFrom} child-element.
     */
    public void end(OwlAllValuesFrom child) {
        classId = child.classId();
        restriction = "all";
    }

    /**
     * Notifies this element that its {@link OwlMinCardinality} child-element
     * has been parsed.
     * 
     * @param child
     *            this element's {@link OwlMinCardinality} child-element.
     */
    public void end(OwlMinCardinality child) {
        restriction = Arrays.concat(Arrays.toStrings("min ", child.value()));
    }

    /**
     * Returns the id for the {@link Class} represented by this restriction.
     * 
     * @return the id for the {@link Class} represented by this restriction.
     */
    public Object classId() {
        State.checkNotNull(propertyId, restriction);
        String property = propertyId.toString();
        if (classId == null)
            return Arrays.concat(property, restriction);
        String class_ = classId.toString();
        return Arrays.concat(property, restriction, class_);
    }
}
