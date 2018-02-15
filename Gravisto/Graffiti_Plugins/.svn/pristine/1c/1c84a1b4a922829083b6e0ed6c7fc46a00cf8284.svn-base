package org.graffiti.plugins.ios.importers.ontology.xml.util;

import org.graffiti.util.ext.State;

/**
 * Extension methods for {@link String} arrays that represent the state of an
 * rdf- or owl-element.
 * 
 * @author Harald Frankenberger
 */
public class ElementState {

    private ElementState() {
    }

    /**
     * Generates an id from this element-state; if all components of this state
     * are <code>null</code> an {@link IllegalStateException} is thrown. This
     * implementation will extract an id via
     * {@link ResourceString#extractId(String)} from the first non-
     * <code>null</code> string it encounters in this element-state.
     * 
     * @param this_
     *            this element-state.
     * @throws IllegalStateException
     *             if all components of this state are <code>null</code>
     */
    public static Object generateId(String... this_) {
        Object id = generateId(this_, null);
        State.checkNotNull(id);
        return id;
    }

    /**
     * Generates an id from this element-state; returns the given
     * <code>defaultId</code> if all components of this state are
     * <code>null</code>. This implementation will extract an id via
     * {@link ResourceString#extractId(String)} from the first non-
     * <code>null</code> string it encounters in this element-state.
     * 
     * @param this_
     *            this state.
     * @param defaultId
     *            the default-id to return if all components of this state are
     *            <code>null</code>
     * @return the generated id from this element-state or
     *         <code>defaultId</code> if all components of this state are
     *         <code>null</code>
     */
    public static Object generateId(String[] this_, Object defaultId) {
        for (String each : this_)
            if (each != null)
                return ResourceString.extractId(each);
        return defaultId;
    }

}
