package org.graffiti.plugins.ios.importers.ontology.model;

import org.graffiti.plugins.ios.importers.ontology.Graph;

/**
 * A property of an ontology.
 */
public class Property {
    private final Object id;

    private String label;

    Object domain;

    Object range;

    /**
     * Creates a property with the given parameters. If the id parameter is
     * <tt>null</tt> the id of this property is set to a random object.
     * 
     * @param id
     *            the id of this property
     * @param domainId
     *            the domain-class of this property
     * @param label
     *            the label of this property
     * @param rangeId
     *            the range-class of this property
     */
    public Property(Object id, Object domainId, String label, Object rangeId) {
        this.label = label;
        this.domain = domainId;
        this.id = id == null ? new Object() : id;
        this.range = rangeId;
    }

    /**
     * Returns <tt>true</tt> if this property has the given domain.
     * 
     * @param domainId
     *            the domain-class that is supposed to be the domain of this
     *            property.
     * @return <tt>true</tt> if this property has the given domain.
     */
    public boolean hasDomain(Object domainId) {
        return domainId == null ? this.domain == null : domainId.equals(domain);
    }

    /**
     * Returns <tt>true</tt> if this property has the given range.
     * 
     * @param rangeId
     *            the range-class that is supposed to be the range of this
     *            property.
     * @return <tt>true</tt> if this property has the given range.
     */
    public boolean hasRange(Object rangeId) {
        return rangeId == null ? this.range == null : rangeId.equals(range);
    }

    /**
     * Imports this property as an edge to the given graph.
     * 
     * @param graph
     *            the graph that should contain this property as an edge.
     */
    public void importTo(Graph graph) {
        if (domain == null || range == null)
            return;
        graph.addEdge(domain, id.toString(), label, range);
    }

    /**
     * Returns a string-representation of this property.
     * 
     * @return a string-representation of this property.
     */
    @Override
    public String toString() {
        return "Property(id=" + id + ", domain=" + domain + ", label=" + label
                + ", range=" + range + ")";
    }

    /**
     * Returns <tt>true</tt> if the specified object is a property and all of
     * the following conditions hold:
     * <ol>
     * <li>has the same id as this property
     * <li>has the same domain as this property
     * <li>has the same range as this property
     * <li>has the same label as this property
     * </ol>
     * 
     * @param obj
     *            the object to compare this property to for equality
     * @return <tt>true</tt> if this property is equal to the given object.
     */
    @Override
    public boolean equals(Object obj) {
        Property p = (Property) obj;
        return p.hasId(id) && p.hasDomain(domain) && p.hasRange(range)
                && p.hasLabel(label);
    }

    /**
     * Returns the hash-code for this property.
     * 
     * @return the hash-code for this property.
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Returns <tt>true</tt> if this property has the given label.
     * 
     * @param label
     *            the string that is supposed to be the label of this property.
     * @return <tt>true</tt> if this property has the given label.
     */
    public boolean hasLabel(String label) {
        return label == null ? this.label == null : label.equals(label);
    }

    /**
     * Returns <tt>true</tt> if this property has the given id.
     * 
     * @param id
     *            the object that is supposed to be the identifier of this
     *            property.
     * @return <tt>true</tt> if this property has the given id.
     */
    public boolean hasId(Object id) {
        return id == null ? this.id == null : id.equals(this.id);
    }

    boolean hasClassesIn(ClassBase classBase) {
        return hasDomainIn(classBase) && hasRangeIn(classBase);
    }

    private boolean hasDomainIn(ClassBase classBase) {
        return classBase.containsClass(domain);
    }

    private boolean hasRangeIn(ClassBase classBase) {
        return classBase.containsClass(range);
    }

}
