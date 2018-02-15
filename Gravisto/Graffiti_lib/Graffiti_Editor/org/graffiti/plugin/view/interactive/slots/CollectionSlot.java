// =============================================================================
//
//   CollectionSlot.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view.interactive.slots;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

import org.graffiti.plugin.view.interactive.Slot;

/**
 * Slot for collections. The intention of this class is to provide an
 * {@link #acceptsValue(Object)} method that is strictly type safe despite of
 * generic type erasure.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @param <S>
 *            the type of accepted collections, e.g. {@link Set} or
 *            {@link LinkedList}.
 * @param <T>
 *            The type of elements to be held in accepted collections.
 */
public class CollectionSlot<S extends Collection<T>, T> extends Slot<S> {
    /**
     * {@code Class} object modeling the type of elements to be held in accepted
     * collections.
     */
    private Class<T> interiorType;

    /**
     * Constructs a {@code CollectionSlot}.
     * 
     * @param id
     *            the id of the slot.
     * @param collectionType
     *            the {@code Class} object modeling the type of accepted
     *            collections.
     * @param interiorType
     *            the {@code Class} object modeling the type of elements to be
     *            held in accepted collections.
     */
    public CollectionSlot(String id, Class<S> collectionType,
            Class<T> interiorType) {
        super(id, collectionType, false);
        this.interiorType = interiorType;
    }

    /**
     * {@inheritDoc}
     * 
     * This implementation checks both if the specified value is of the desired
     * collection type and if each of the elements contained in the specified
     * collection is an instance of the desired interior type.
     */
    @Override
    public boolean acceptsValue(Object value) {
        if (!type.isInstance(value))
            return false;
        Collection<?> collection = (Collection<?>) value;
        for (Object element : collection) {
            if (!interiorType.isInstance(element))
                return false;
        }
        return true;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
