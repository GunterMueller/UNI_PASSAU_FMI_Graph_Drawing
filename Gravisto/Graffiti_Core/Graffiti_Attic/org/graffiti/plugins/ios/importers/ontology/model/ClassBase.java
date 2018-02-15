package org.graffiti.plugins.ios.importers.ontology.model;

import org.graffiti.util.ext.HashMap;
import org.graffiti.util.ext.HashSet;
import org.graffiti.util.ext.Map;
import org.graffiti.util.ext.ParameterList;
import org.graffiti.util.ext.Set;

/**
 * A collection of classes. A class-base typically contains defined and declared
 * classes. A declared class is a class whose id is known to this class-base but
 * whose presence is not defined, yet. To define a class, the class must be
 * declared first. Defined classes are known and present in this class-base,
 * i.e. they will appear in this class-base's {@link #classSet()}.
 * <p>
 * Methods that do not contain 'declared' in their name (such as
 * {@link #classSet()} or {@link #containsClass(Object)}) will consider only
 * defined classes. Methods that do contain 'declared' in their name (such as
 * {@link #declaredClassForId(Object)}) will consider both: declared and defined
 * classes.
 */
public class ClassBase {

    private final Map<Object, Class> classMap;

    /**
     * Creates a new <tt>ClassBase</tt>.
     */
    public ClassBase() {
        classMap = new HashMap<Object, Class>();
    }

    /**
     * Returns the set of ids of all defined classes in this class-base.
     * 
     * @return the set of ids of all defined classes in this class-base.
     */
    public Set<Object> idSet() {
        Set<Object> idSet = new HashSet<Object>();
        for (Class c : classMap.values()) {
            c.addToIdSet(idSet);
        }
        return idSet;
    }

    /**
     * Returns the set of all defined classes in this class-base.
     * 
     * @return the set of all defined classes in this class-base.
     */
    public Set<Class> classSet() {
        Set<Class> classSet = new HashSet<Class>();
        for (Class c : classMap.values()) {
            c.addToClassSet(classSet);
        }
        return classSet;
    }

    /**
     * Declares a class with the given id in this class-base.
     * 
     * @param classId
     *            the id of the class to be declared.
     * @throws IllegalArgumentException
     *             if <tt>classId</tt> is <tt>null</tt>
     */
    public void addDeclaredClass(Object classId) {
        ParameterList.checkNotNull(classId);
        classMap.putIfNotPresent(classId, new Class(classId, this));
    }

    /**
     * Returns the defined class for the given id.
     * 
     * @param id
     *            the id of the defined class to be returned.
     * @return the defined class for the given id.
     */
    public Class classForId(Object id) {
        return declaredClassForId(id).asDefinedClass();
    }

    /**
     * Returns the declared class for the given id.
     * 
     * @param id
     *            the id of the declared class to be returned.
     * @return the declared class for the given id.
     */
    public Class declaredClassForId(Object id) {
        return classMap.getIfPresent(id);
    }

    /**
     * Returns <tt>true</tt> if this class-base contains a defined class with
     * the given id.
     * 
     * @param id
     *            the id of the defined class whose presence is queried.
     * @return <tt>true</tt> if this class-base contains a defined class with
     *         the given id.
     */
    public boolean containsClass(Object id) {
        return idSet().contains(id);
    }

    /**
     * Returns the number of defined classes in this class-base.
     * 
     * @return the number of defined classes in this class-base.
     */
    public int classCount() {
        return classSet().size();
    }

    /**
     * Returns the number of defined super-classes for the given id.
     * 
     * @param classId
     *            the id of the class whose number of super-classes is queried.
     * @return the number of defined super-classes for the given id.
     */
    public int countSuperClasses(Object classId) {
        if (!containsClass(classId))
            return 0;
        else
            return declaredClassForId(classId).superClassCount();
    }

    /**
     * Returns the number of defined super-classes of this class-base.
     * 
     * @return the number of defined super-classes of this class-base.
     */
    public int superClassCount() {
        int superClassCount = 0;
        for (Class c : classSet()) {
            superClassCount += c.superClassCount();
        }
        return superClassCount;
    }

    /**
     * Returns the set of super-class-properties in this class-base.
     * 
     * @return the set of super-class-properties in this class-base.
     */
    public Set<Property> superClassProperties() {
        Set<Property> result = new HashSet<Property>();
        for (Class c : classSet()) {
            c.addAllSuperClassPropertiesTo(result);
        }
        return result;
    }

    /**
     * Adds a class with the given id and label to this class-base.
     * 
     * @param id
     *            the id of the class to be added to this class-base.
     * @param label
     *            the label of the class to be added to this class-base.
     * @throws IllegalArgumentException
     *             if <tt>id</tt> is <tt>null</tt>
     */
    public void addClass(Object id, String label) {
        ParameterList.checkNotNull(id);
        declaredClassForId(id).defineWith(label);
    }

    /**
     * Adds a class with the given id to this class-base.
     * 
     * @param id
     *            the id of the class to be added
     * @throws IllegalArgumentException
     *             if <code>id</code> is <code>null</code>
     */
    public void addClass(Object id) {
        ParameterList.checkNotNull(id);
        declaredClassForId(id).define();
    }

}
