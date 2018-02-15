package org.graffiti.plugins.ios.importers.ontology.model;

import java.util.NoSuchElementException;

import org.graffiti.plugins.ios.importers.ontology.Graph;
import org.graffiti.util.ext.HashMap;
import org.graffiti.util.ext.HashSet;
import org.graffiti.util.ext.Map;
import org.graffiti.util.ext.ParameterList;
import org.graffiti.util.ext.Set;

/**
 * An ontology. An ontology typically consists of classes, object-properties,
 * and super-class-properties. Object-properties and super-class-properties
 * together are simply referred to as "properties". Every class and property is
 * identified via an id-attribute. Super-class-properties are identified by
 * their sub-class and super-class.
 */
public class Ontology {

    private final ClassBase classBase;

    private Map<Object, Property> properties = new HashMap<Object, Property>();

    /**
     * Creates a new Ontology.
     */
    public Ontology() {
        classBase = new ClassBase();
    }

    /**
     * Returns the number of classes in this ontology.
     * 
     * @return the number of classes in this ontology.
     */
    public int classCount() {
        return classBase.classCount();
    }

    /**
     * Returns the set of class-ids for this ontology.
     * 
     * @return the set of class-ids for this ontology.
     */
    public Set<Object> classIdSet() {
        return classBase.idSet();
    }

    /**
     * Defines a class with the given id and label in this ontology.
     * 
     * @param id
     *            the id of the new class
     * @param label
     *            the label of the new class
     */
    public Object defineClass(Object id, String label) {
        classBase.addDeclaredClass(id);
        classBase.addClass(id, label);
        return id;
    }

    /**
     * Defines a class with the given id within this ontology.
     * 
     * @param id
     *            the id of the new class
     */
    public void defineClass(Object id) {
        classBase.addDeclaredClass(id);
        classBase.addClass(id);
    }

    /**
     * Returns <tt>true</tt> if this ontology defines a class with the given id.
     * 
     * @return <tt>true</tt> if this ontology defines a class with the given id.
     */
    public boolean definesClass(Object id) {
        return classBase.containsClass(id);
    }

    /**
     * Defines a super-class-property with the given parameters in this
     * ontology.
     * 
     * @param subClassId
     *            the domain of the new super-class-property
     * @param superClassId
     *            the range of the new super-class-property
     */
    public void defineSuperClassProperty(Object subClassId, Object superClassId) {
        classBase.addDeclaredClass(subClassId);
        classBase.addDeclaredClass(superClassId);
        Class subClass = classBase.declaredClassForId(subClassId);
        subClass.addSuperClassId(superClassId);
    }

    /**
     * Returns <tt>true</tt> if this ontology defines the specified
     * super-class-property.
     * 
     * @param subClassId
     *            the domain of the super-class-property whose presence is
     *            queried.
     * @param superClassId
     *            the range of the super-class-property whose presence is
     *            queried.
     * @return <tt>true</tt> if this ontology defines the specified
     *         super-class-property.
     */
    public boolean definesSuperClassProperty(Object subClassId,
            Object superClassId) {
        if (!classBase.containsClass(subClassId)
                || !classBase.containsClass(superClassId))
            return false;
        return classBase.classForId(subClassId).hasSuperClass(superClassId);
    }

    /**
     * Returns the number of super-classes for the given class-id.
     * 
     * @return the number of super-classes for the given class-id.
     */
    public int countSuperClasses(Object classId) {
        return classBase.countSuperClasses(classId);
    }

    /**
     * Returns the number of properties in this ontology; i.e. the sum of this
     * ontology's {@link #objectPropertyCount()} and
     * {@link #superClassPropertyCount()}
     * 
     * @return the number of properties in this ontology.
     */
    public int propertyCount() {
        return objectPropertyCount() + superClassPropertyCount();
    }

    /**
     * Returns the number of super-class-properties in this ontology.
     * 
     * @return the number of super-class-properties in this ontology.
     */
    public int superClassPropertyCount() {
        return classBase.superClassCount();
    }

    /**
     * Returns <tt>true</tt> if this ontology defines an object-property with
     * the given id.
     * 
     * @param id
     *            the id of the object-property whose presence is queried.
     */
    public boolean definesObjectProperty(Object id) {
        return properties.containsKey(id);
    }

    /**
     * Returns the number of properties with the given class-id as domain.
     * 
     * @param classId
     *            the class that appears as the domain of queried properties.
     * @return the number of properties with the given class-id as domain.
     */
    public int countAsDomain(Object classId) {
        int domainCount = 0;
        for (Property p : properties()) {
            domainCount = incrementIf(p.hasDomain(classId), domainCount);
        }
        return domainCount;
    }

    /**
     * Returns the set of all properties in this ontology, i.e. the union of its
     * object-properties and super-class-properties.
     * 
     * @return the set of all properties in this ontology
     */
    public Set<Property> properties() {
        Set<Property> result = classBase.superClassProperties();
        result.addAll(properties.values());
        return result;
    }

    /**
     * Returns the number of properties with the given class-id as range.
     * 
     * @param classId
     *            the class that appears as the range of queried properties.
     * @return the number of properties with the given class-id as range.
     */
    public int countAsRange(Object classId) {
        int rangeCount = 0;
        for (Property p : properties()) {
            rangeCount = incrementIf(p.hasRange(classId), rangeCount);
        }
        return rangeCount;
    }

    /**
     * Imports this ontology to the given graph.
     * 
     * @param graph
     *            the graph to import this ontology to.
     */
    public void importTo(Graph graph) {
        importClasses(graph);
        importSuperClassProperties(graph);
        importProperties(graph);
    }

    /**
     * Returns the object-property with the given id.
     * 
     * @return the object-property with the given id.
     */
    public Property objectPropertyFor(Object id) {
        return properties.getIfPresent(id);
    }

    /**
     * Returns the property-ids of the properties whose domain is the given
     * class.
     * 
     * @param classId
     *            the class that participates as the domain of the properties
     *            whose id-set is to be returned.
     * @return the property-ids of the properties whose domain is the given
     *         class.
     */
    public Set<Object> propertyIdSetForDomain(Object classId) {
        Set<Object> result = new HashSet<Object>();
        for (Map.Entry<Object, Property> entry : properties.entrySet()) {
            Property property = entry.getValue();
            Object id = entry.getKey();
            result.addIf(property.hasDomain(classId), id);
        }
        return result;
    }

    /**
     * Defines the object-property with the given parameters in this ontology,
     * if it does not already exist.
     * 
     * @param id
     *            the id of the new property
     * @param domainId
     *            the domain-class of the new property
     * @param label
     *            the label of the new property
     * @param rangeId
     *            the range-class of the new property
     */
    public void defineObjectProperty(Object id, Object domainId, String label,
            Object rangeId) {
        ParameterList.checkNotNull(id);
        ParameterList.checkNotNull(domainId);
        ParameterList.checkNotNull(rangeId);
        Property property = new Property(id, domainId, label, rangeId);
        properties.putIfNotPresent(id, property);
    }

    /**
     * Defines a functional property with the given parameters within this
     * ontology, if it does not already exist.
     * 
     * @param id
     *            the id of the property to be defined
     * @param domain
     *            id of the domain-class of the property to be defined
     * @param label
     *            the label of the property to be defined
     * @param range
     *            the id of the range-class of the property to be defined
     */
    public void defineFunctionalProperty(Object id, Object domain,
            String label, Object range) {
        defineObjectProperty(id, domain, label, range);
    }

    /**
     * Returns the number of object-properties in this ontology.
     * 
     * @return the number of object-properties in this ontology.
     */
    public int objectPropertyCount() {
        int result = 0;
        for (Property each : properties.values()) {
            if (each.hasClassesIn(classBase)) {
                result++;
            }
        }
        return result;
    }

    /**
     * Returns the class with the given id.
     * 
     * @param id
     *            the id of the class to be returned
     * @throws NoSuchElementException
     *             if the queried class is not present in this ontology.
     */
    public Class classForId(Object id) {
        return classBase.classForId(id);
    }

    /**
     * Casts the given object to an ontology.
     * 
     * @param object
     *            the object to cast.
     * @return the given object cast to an ontology.
     */
    public static Ontology cast(Object object) {
        return (Ontology) object;
    }

    private void importSuperClassProperties(Graph graph) {
        for (Class c : classBase.classSet()) {
            c.importSuperClassPropertiesTo(graph);
        }
    }

    private void importProperties(Graph graph) {
        for (Property p : properties.values()) {
            p.importTo(graph);
        }
    }

    private void importClasses(Graph graph) {
        for (Object id : classIdSet()) {
            classBase.classForId(id).importTo(graph);
        }
    }

    private int incrementIf(boolean condition, int count) {
        return condition ? count + 1 : count;
    }

}
