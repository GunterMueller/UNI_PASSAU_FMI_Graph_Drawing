package org.graffiti.plugins.ios.importers.ontology.model;

import java.util.NoSuchElementException;

import org.graffiti.plugins.ios.importers.ontology.Graph;
import org.graffiti.util.ext.HashSet;
import org.graffiti.util.ext.Set;

/**
 * A class of an ontology.
 * 
 * @author Harald Frankenberger
 * @see ClassBase
 */
public class Class {
    private final Object id;

    private final Set<Object> superClasses = new HashSet<Object>();

    private String label;

    private boolean isDefined = false;

    private ClassBase classBase;

    /**
     * Creates a new class with the given id and a new class-base.
     */
    public Class(ClassBase classBase) {
        this(new Object(), new ClassBase());
    }

    /**
     * Creates a new class with the given id and class-base.
     * 
     * @param id
     *            the id of this class.
     * @param classBase
     *            the <tt>ClassBase</tt> for this class.
     */
    public Class(Object id, ClassBase classBase) {
        this.id = id;
        this.label = null;
        this.classBase = classBase;
    }

    /**
     * Adds a super-class to this class if it does not already exist.
     * 
     * @param superClassId
     *            the id of this class's new super-class
     */
    public void addSuperClassId(Object superClassId) {
        superClasses.add(superClassId);
    }

    /**
     * Returns the label of this class.
     * 
     * @return the label of this class.
     */
    public String label() {
        return label == null ? id.toString() : label;
    }

    /**
     * Imports this class as a node to the given graph.
     * 
     * @param graph
     *            the graph that will contain this class as a node.
     */
    public void importTo(Graph graph) {
        graph.addNode(id.toString(), label());
    }

    /**
     * Imports this class's super-class-properties as edges to the given graph.
     * 
     * @param graph
     *            the graph that will contain this class's
     *            super-class-properties as edges.
     */
    public void importSuperClassPropertiesTo(Graph graph) {
        for (Object superClass : superClasses) {
            graph.addSuperClassEdge(id, superClass);
        }
    }

    /**
     * Adds the id of this class to the given id-set if this class is defined.
     * 
     * @param idSet
     *            the id-set to add this class's id to.
     */
    public void addToIdSet(Set<Object> idSet) {
        idSet.addIf(isDefined, id);
    }

    /**
     * Returns the number of super-classes of this class.
     * 
     * @return the number of super-classes of this class.
     */
    public int superClassCount() {
        return superClassIdSet().size();
    }

    /**
     * Returns the set consisting of the ids of all super-classes of this class.
     * 
     * @return the set consisting of the ids of all super-classes of this class.
     */
    public Set<Object> superClassIdSet() {
        Set<Object> result = new HashSet<Object>();
        for (Object superClass : superClasses) {
            result.addIf(classBase.containsClass(superClass), superClass);
        }
        return result;
    }

    /**
     * Returns the set of super-class-properties whose domain is this class.
     * 
     * @return the set of super-class-properties whose domain is this class.
     */
    public Set<Property> superClassProperties() {
        Set<Property> result = new HashSet<Property>();
        for (Object superClassId : superClassIdSet()) {
            result.add(new Property(new Object(), id.toString(), "is-a",
                    superClassId.toString()));
        }
        return result;
    }

    /**
     * Returns <tt>true</tt> if this class has a super-class with the given id.
     * 
     * @return <tt>true</tt> if this class has a super-class with the given id.
     */
    public boolean hasSuperClass(Object superClassId) {
        return superClasses.contains(superClassId);
    }

    /**
     * Adds all super-class-properties of this class to the given set.
     * 
     * @param superClassProperties
     *            the set to add all super-class-properties to.
     */
    public void addAllSuperClassPropertiesTo(Set<Property> superClassProperties) {
        superClassProperties.addAll(superClassProperties());
    }

    /**
     * Adds this class to the given set if this class is defined.
     * 
     * @param classSet
     *            the set to add this class to.
     */
    public void addToClassSet(Set<Class> classSet) {
        classSet.addIf(isDefined, this);
    }

    /**
     * Returns this class if it is defined.
     * 
     * @return this class if it is defined.
     * @throws NoSuchElementException
     *             if this class has not been defined, yet.
     */
    public Class asDefinedClass() {
        if (!isDefined)
            throw new NoSuchElementException();
        return this;
    }

    /**
     * Defines this class with the given label.
     * 
     * @param label
     *            the new label of this class.
     */
    public void defineWith(String label) {
        this.label = label;
        isDefined = true;
    }

    /**
     * Defines this class with as an anonymous class.
     */
    public void define() {
        this.label = "blank";
        isDefined = true;
    }

}
