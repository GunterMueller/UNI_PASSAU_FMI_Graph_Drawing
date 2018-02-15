// =============================================================================
//
//   TriggerParameter.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view.interactive;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;

import org.graffiti.plugin.view.interactive.slots.CollectionSlot;
import org.graffiti.plugin.view.interactive.slots.MapSlot;

/**
 * {@code Slot}s are typed containers that hold the values supplied by triggers
 * and used by triggers and actions as parameters. Instances of {@code Slot} do
 * not directly hold the values but rather describe its type, name, default
 * values etc. The association of slots with values is maintained by
 * {@link SlotMap}.
 * <p>
 * <a href="package-summary.html#ConceptOverview"> Overview of the
 * trigger/action paradigm</a>
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @param <T>
 *            the type of the slot.
 */
public class Slot<T> {
    /**
     * The id to address this slot from script code.
     */
    private String id;

    /**
     * The name as seen by the user when graphically editing the tools.
     */
    private String name;

    /**
     * The {@code Class} object modeling the type of this slot.
     */
    protected Class<T> type;

    /**
     * The description as seen by the user when graphically editing the tools.
     * 
     */
    private String description;

    /**
     * When using this slot to define the parameter of a trigger, builder can be
     * used to graphically edit that parameter. May be {@code null}. Then the
     * tool system tries to infer an adequate component based on the slot type.
     * If this fails, the parameter is not graphically editable.
     */
    private SlotEditorComponentBuilder<T> builder;

    /**
     * The default value of this, when used as a parameter slot.
     */
    protected T defaultValue;

    /**
     * Denotes if {@code null} may be assigned to this slot.
     * 
     */
    private boolean acceptsNull;

    /**
     * When this slot is constructed without explicitly specifying the default
     * value, {@code createDefaultDefaultValue(Class)} tries to create a default
     * value by using the default constructor for the slot type. If the default
     * constructor does not exist or is not accessible, {@code null} is
     * returned.
     * 
     * @param type
     *            the slot type.
     * @return the created default value or {@code null} if the default
     *         constructor does not exist or is not accessible.
     */
    private static <S> S createDefaultDefaultValue(Class<S> type) {
        try {
            return type.newInstance();
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    /**
     * Creates a new slot with the specified id and {@code Object} as slot type.
     * The returned slot will not accept {@code null}.
     * 
     * @param id
     *            the id of the slot.
     * @return a new Slot with the specified id and {@code Object} as slot type.
     * @see #acceptsNull()
     */
    static Slot<Object> create(String id) {
        return new Slot<Object>(id, Object.class, null, false);
    }

    /**
     * Creates a new slot with the specified id and slot type. The returned slot
     * will not accept {@code null}.
     * 
     * @param <S>
     *            the type of the slot.
     * @param id
     *            the id of the slot.
     * @param type
     *            the {@code Class} representing the slot type.
     * @return a new Slot with the specified id and slot type.
     * @see #acceptsNull()
     */
    public static <S> Slot<S> create(String id, Class<S> type) {
        return new Slot<S>(id, type, false);
    }

    /**
     * Creates a new slot with the specified id and slot type.
     * 
     * @param <S>
     *            the type of the slot.
     * @param id
     *            the id of the slot.
     * @param type
     *            the {@code Class} representing the slot type.
     * @param acceptsNull
     *            specifies whether this slot accepts {@code null}.
     * @return a new Slot with the specified id and slot type.
     * @see #acceptsNull()
     */
    public static <S> Slot<S> create(String id, Class<S> type,
            boolean acceptsNull) {
        return new Slot<S>(id, type, acceptsNull);
    }

    /**
     * Creates a new slot with the specified id and {@code Set} of the the
     * specified type as its slot type. The returned slot will not accept
     * {@code null}.
     * 
     * @param <S>
     *            {@link Set}{@code <S>} is the slot type of the returned slot.
     * @param id
     *            the id of the slot.
     * @param type
     *            the {@code Class} representing {@code S}.
     * @return a new Slot with the specified id and {@code Set} of the the
     *         specified type as its slot type.
     * @see #acceptsNull()
     */
    @SuppressWarnings("unchecked")
    public static <S> Slot<Set<S>> createSetSlot(String id, Class<S> type) {
        return new CollectionSlot<Set<S>, S>(id, (Class<Set<S>>) Set.class
                .asSubclass(Set.class), type);
    }

    /**
     * Creates a new slot with the specified id and {@code List} of the the
     * specified type as its slot type. The returned slot will not accept
     * {@code null}.
     * 
     * @param <S>
     *            {@link List}{@code <S>} is the slot type of the returned slot.
     * @param id
     *            the id of the slot.
     * @param type
     *            the {@code Class} representing {@code S}.
     * @return a new Slot with the specified id and {@code List} of the the
     *         specified type as its slot type.
     * @see #acceptsNull()
     */
    @SuppressWarnings("unchecked")
    public static <S> Slot<List<S>> createListSlot(String id, Class<S> type) {
        return new CollectionSlot<List<S>, S>(id, (Class<List<S>>) List.class
                .asSubclass(List.class), type);
    }

    /**
     * Creates a new slot with the specified id and {@code Map} of the the
     * specified types as its slot type. The returned slot will not accept
     * {@code null}.
     * 
     * @param <K>
     *            {@link Map}{@code <K, V>} is the slot type of the returned
     *            slot.
     * @param <V>
     *            {@code Map}{@code <K, V>} is the slot type of the returned
     *            slot.
     * @param id
     *            the id of the slot.
     * @param keyType
     *            the {@code Class} representing {@code K}.
     * @param valueType
     *            the {@code Class} representing {@code V}.
     * @return a new Slot with the specified id and {@code Map} of the the
     *         specified type as its slot type.
     * @see #acceptsNull()
     */
    public static <K, V> Slot<Map<K, V>> createMapSlot(String id,
            Class<K> keyType, Class<V> valueType) {
        return new MapSlot<K, V>(id, keyType, valueType);
    }

    /**
     * Constructs a new slot with the specified id and slot type.
     * 
     * @param id
     *            the id of the slot.
     * @param type
     *            the {@code Class} representing the slot type.
     * @param acceptsNull
     *            specifies whether this slot accepts {@code null}.
     * @see #acceptsNull()
     */
    public Slot(String id, Class<T> type, boolean acceptsNull) {
        this(id, type, createDefaultDefaultValue(type), acceptsNull);
    }

    /**
     * Constructs a new slot with the specified id, slot type and default value.
     * 
     * @param id
     *            the id of the slot.
     * @param type
     *            the {@code Class} representing the slot type.
     * @param acceptsNull
     *            specifies whether this slot accepts {@code null}.
     * @param defaultValue
     *            the default value of the slot.
     * @see #acceptsNull()
     */
    public Slot(String id, Class<T> type, T defaultValue, boolean acceptsNull) {
        this(id, "", type, "", null, defaultValue, acceptsNull);
    }

    /**
     * Constructs a new slot.
     * 
     * @param id
     *            the id of the slot.
     * @param name
     *            the name as seen by the user when graphically editing the
     *            tools.
     * @param type
     *            the {@code Class} representing the slot type.
     * @param description
     *            the description of the slot as seen by the user when
     *            graphically editing the tools.
     * @param builder
     *            the {@code SlotEditorComponentBuilder}. When using this slot
     *            to define the parameter of a trigger, builder can be used to
     *            graphically edit that parameter. May be null. Then the tool
     *            system tries to infer an adequate component based on the slot
     *            type. If this fails, the parameter is not graphically
     *            editable.
     * @param defaultValue
     *            the default value.
     * @param acceptsNull
     *            specifies whether this slot accepts {@code null}.
     * @see #acceptsNull()
     */
    public Slot(String id, String name, Class<T> type, String description,
            SlotEditorComponentBuilder<T> builder, T defaultValue,
            boolean acceptsNull) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.builder = builder;
        this.defaultValue = defaultValue;
        this.acceptsNull = acceptsNull;
    }

    /**
     * Creates a new slot with the specified id, slot type and description. The
     * returned slot will not accept {@code null}.
     * 
     * @param id
     *            the id of the slot.
     * @param name
     *            the name of the slot as seen by the user when graphically
     *            editing the tools.
     * @param type
     *            the {@code Class} representing the slot type.
     * @param description
     *            the description as seen by the user when graphically editing
     *            the tools.
     * @see #acceptsNull()
     */
    public Slot(String id, String name, Class<T> type, String description) {
        this(id, name, type, description, createDefaultDefaultValue(type));
    }

    /**
     * Constructs a new slot. The returned slot will not accept {@code null}.
     * 
     * @param id
     *            the id of the slot.
     * @param name
     *            the name of the slot as seen by the user when graphically
     *            editing the tools.
     * @param type
     *            the {@code Class} representing the slot type.
     * @param description
     *            the description of the slot as seen by the user when
     *            graphically editing the tools.
     * @param defaultValue
     *            the default value.
     * @see #acceptsNull()
     */
    public Slot(String id, String name, Class<T> type, String description,
            T defaultValue) {
        this(id, name, type, description, null, defaultValue, false);
    }

    /**
     * Returns the id to address this slot from script code.
     * 
     * @return the id to address this slot from script code.
     */
    public final String getId() {
        return id;
    }

    /**
     * Returns the name of this slot as seen by the user when graphically
     * editing the tools.
     * 
     * @return the name of this slot as seen by the user when graphically
     *         editing the tools.
     */
    public final String getName() {
        return name;
    }

    /**
     * Returns the description of this slot as seen by the user when graphically
     * editing the tools.
     * 
     * @return the description of this slot as seen by the user when graphically
     *         editing the tools.
     */
    public final String getDescription() {
        return description;
    }

    /**
     * Returns the type of this slot.
     * 
     * @return the type of this slot.
     */
    public final Class<T> getType() {
        return type;
    }

    /**
     * Returns the default value of this, when used as a parameter slot.
     * 
     * @return the default value of this, when used as a parameter slot.
     */
    public final T getDefaultValue() {
        return defaultValue;
    }

    /**
     * Returns whether the specified value may be assigned to this slot. If one
     * tries to assign an unsupported value to a slot,
     * {@link SlotAssignmentException} may be thrown. At least values, that are
     * not assignment-compatible with the slot type, and {@code null}, if
     * {@link #acceptsNull()} returns {@code false}, are not accepted.
     * 
     * @param value
     *            the value in question.
     * @return {@code true}, if the specified value may be assigned to this
     *         slot.
     * @see #acceptsNull()
     * @see SlotMap#put(Slot, Object)
     * @see SlotMap#put(String, Object)
     */
    public boolean acceptsValue(Object value) {
        return ((value == null) && acceptsNull) || type.isInstance(value);
    }

    /**
     * Returns whether {@code null} may be assigned to this slot.
     * 
     * @return {@code true}, if {@code null} may be assigned to this slot.
     * @see #acceptsValue(Object)
     * @see SlotMap#put(Slot, Object)
     * @see SlotMap#put(String, Object)
     */
    public boolean acceptsNull() {
        return acceptsNull;
    }

    /**
     * Returns whether the other object is a {@code Slot} and has the same id as
     * this.
     * 
     * @return {@code true}, if the other object is a {@code Slot} and has the
     *         same id as this.
     */
    @Override
    public final boolean equals(Object other) {
        return (other instanceof Slot<?>) && id.equals(((Slot<?>) other).id);
    }

    /**
     * Returns a hash code for this slot. This method is overridden in order to
     * comply the general contract for the {@link #equals(Object)} method.
     * 
     * @return a hash code for this slot.
     */
    @Override
    public final int hashCode() {
        return id.hashCode();
    }

    /**
     * Returns a {@code SlotEditorComponentBuilder} for this slot. When using
     * this slot to define the parameter of a trigger, the builder can be used
     * to graphically edit that parameter. May return {@code null}. Then the
     * tool system tries to infer an adequate component based on the slot type.
     * If this fails, the parameter is not graphically editable.
     * 
     * @return a {@code SlotEditorComponentBuilder} for this slot.
     */
    public final SlotEditorComponentBuilder<T> getBuilder() {
        return builder;
    }

    /**
     * Prepares the specified preferences tree to store the value assigned to
     * this slot. The default implementation throws an
     * {@link UnsupportedOperationException}.
     * 
     * @param preferences
     *            the preferences tree where the value assigned to this slot
     *            will be stored.
     */
    public void createDefaultPreferences(Preferences preferences) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the value that is assigned to this slot according to the
     * specified preferences tree. The default implementation throws an
     * {@link UnsupportedOperationException}.
     * 
     * @param preferences
     *            the preferences tree from where the value is to be obtained.
     * @return the value that is assigned to this slot according to the
     *         specified preferences tree.
     */
    public T loadValue(Preferences preferences) {
        throw new UnsupportedOperationException();
    }

    /**
     * Stores the specified value in the specified preferences tree by means of
     * this slot. The default implementation throws an
     * {@link UnsupportedOperationException}.
     * 
     * @param preferences
     *            the preferences tree where the value is to be stored.
     * @param value
     *            the value that is to be stored.
     */
    public void saveValue(Preferences preferences, T value) {
        throw new UnsupportedOperationException();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
