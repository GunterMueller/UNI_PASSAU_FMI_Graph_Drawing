// =============================================================================
//
//   Trigger.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view.interactive;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class of all triggers, which help to interpret user gestures. As the
 * kind of generated {@link UserGesture}s is closely related to the originating
 * {@link InteractiveView} and may include data from new kinds of input devices
 * that were unknown at the time the <a href="package-summary.html#Tools">tool
 * system</a> was created, each view class provides its own hierarchy of
 * triggers. Every trigger (besides the root trigger, which matches all user
 * gestures) has a an associated parent trigger and matches a subset of the user
 * gestures matched by its parent. The child-parent association is used instead
 * of inheritance in order to avoid the Call-super antipattern. When a trigger
 * or one of its children is used in the currently active tool and matches a
 * user gesture, it fills its {@link Slot}s with related data.
 * <p>
 * <a href="package-summary.html#ConceptOverview"> Overview of the
 * trigger/action paradigm</a>
 * <p>
 * To define a new kind of trigger, create a direct subclass of {@code Trigger}
 * and override {@link #matches(InSlotMap, InSlotMap, UserGesture)}. If the
 * trigger should extract information form the user gestures not covered by its
 * ancestors, also override
 * {@link #apply(InSlotMap, InSlotMap, OutSlotMap, UserGesture)} and add new
 * output {@link Slot}s by {@link #addOutSlot(Slot)} in the constructor.
 * <p>
 * The functionality of both {@code matches} and {@code apply} is implemented
 * employing the template method pattern. The methods that the {@code
 * UserGesture} is passed to as first parameter have the r&ocirc;le of template
 * methods and traverse the trigger hierarchy, while the primitive operation is
 * realized by the methods that get the {@code UserGesture} as last parameter.
 * <p>
 * <center> <img src="doc-files/Trigger-1.png"></img><br />
 * <b>Diagram 1: Template method pattern in Trigger class.</b> </center>
 * <p>
 * Each trigger returned by the providing view class can be addressed from
 * script code by an unique id. The id of the root trigger is the empty string.
 * The id of a non-root trigger is the concatenation of the id of its parent, a
 * dot and its relative id.
 * <h3>Example</h3>
 * The following example demonstrates the trigger concept with a hypothetical
 * view supporting some triggers for the handling of usergestures regarding the
 * mouse. Diagram 2 shows the involved trigger classes.
 * <p>
 * <center> <img src="doc-files/Trigger-2.png"></img><br />
 * <b>Diagram 2: Class diagram of the example scenario.</b> </center>
 * <p>
 * Diagram 3 shows the trigger hierarchy as returned by
 * {@link ViewFamily#getRootTrigger()}. The hierarchy returned by real views
 * will be rather a tree of triggers than a linear list, which is used here for
 * simplicity.
 * <p>
 * <center> <img src="doc-files/Trigger-3.png"></img><br />
 * <b>Diagram 3: Object diagram of the example scenario.</b> </center>
 * <p>
 * Diagram 4 shows the course of actions when one tries to match a mouse click
 * user gesture on a node against a {@code MousePressOnNodeTrigger}. Note the
 * signature of the methods corresponding to their r&ocirc;le in the template
 * method pattern.
 * <p>
 * <center> <img src="doc-files/Trigger-4.png"></img><br />
 * <b>Diagram 4: Sequence diagram of matching a mouse click user gesture on a
 * node against a MousePressOnNodeTrigger.</b> </center>
 * <p>
 * Diagram 5 shows what happens when one tries to match a key press user gesture
 * against a {@code MousePressOnNodeTrigger}.
 * <p>
 * <center> <img src="doc-files/Trigger-5.png"></img><br />
 * <b>Diagram 5: Sequence diagram of matching a key press user gesture on a node
 * against a MousePressOnNodeTrigger.</b></center>
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class Trigger {
    /**
     * Relative id of the root trigger.
     * 
     * @see #getId()
     */
    private static final String RELATIVE_ROOT_ID = "root";

    /**
     * The parent of this trigger. If it is null, this is the root trigger.
     */
    private Trigger parent;

    /**
     * A map holding all children of this trigger. The children are obtained by
     * their relativeId.
     */
    private Map<String, Trigger> children;

    /**
     * The relative id of this trigger. The (absolute) id of this is a string
     * containing the relative ids of all ancestors separated by '.'.
     */
    private String relativeId;

    /**
     * The name of this trigger as seen by the user when graphically editing the
     * tools.
     */
    private String name;

    /**
     * The description of this trigger as seen by the user when graphically
     * editing the tools.
     */
    private String description;

    /**
     * The parameter slots, which help to constrain the set of matched user
     * gestures. The slots are obtained by their id.
     */
    private Map<String, Slot<?>> parameters;

    /**
     * The output slots, which are filled by a call to
     * {@link #apply(UserGesture, SlotMap, SlotMap, SlotMap)}. The slots are
     * obtained by their id.
     */
    private Map<String, Slot<?>> outSlots;

    /**
     * Constructs a root {@code Trigger} with the specified name and
     * description. Its parent will be {@code null} and its {@code relativeId}
     * will be {@link #RELATIVE_ROOT_ID}.
     * 
     * @param name
     *            the name of the new {@code Trigger} as seen by the user when
     *            graphically editing the tools.
     * @param description
     *            the description of the new {@code Trigger} as seen by the user
     *            when graphically editing the tools.
     */
    protected Trigger(String name, String description) {
        this.name = name;
        this.description = description;
        parent = null;
        relativeId = RELATIVE_ROOT_ID;
        addAnnotatedSlots();
    }

    /**
     * Constructs a {@code Trigger}.
     * 
     * @param parent
     *            the parent of the {@code Trigger} to create.
     * @param relativeId
     *            the {@code relativeId} of the {@code Trigger} to create.
     * @param name
     *            the name of the new {@code Trigger} as seen by the user when
     *            graphically editing the tools.
     * @param description
     *            the description of the new {@code Trigger} as seen by the user
     *            when graphically editing the tools.
     */
    protected Trigger(Trigger parent, String relativeId, String name,
            String description) {
        this(name, description);
        this.parent = parent;
        this.relativeId = relativeId;
        parent.addChild(this);
    }

    /**
     * Adds the specified trigger as child.
     * 
     * @param child
     *            the trigger to add as child.
     */
    private void addChild(Trigger child) {
        if (children == null) {
            children = new HashMap<String, Trigger>();
        }
        children.put(child.relativeId, child);
    }

    /**
     * Returns the trigger with the specified id from the same trigger hierarchy
     * as this.
     * 
     * @param id
     *            the id specifying the trigger to be returned.
     * @return the trigger with the specified id from the same trigger hierarchy
     *         as this or {@code null} if no such trigger exists.
     * @see #getId()
     */
    public final Trigger getById(String id) {
        Trigger trigger = this;
        while (trigger.parent != null) {
            trigger = trigger.parent;
        }
        if (id.equals(RELATIVE_ROOT_ID))
            return trigger;
        String[] rids = id.split("\\.");
        for (String rid : rids) {
            if (trigger == null || trigger.children == null)
                return null;
            trigger = trigger.children.get(rid);
        }
        return trigger;
    }

    /**
     * Returns the id of this trigger. The id of the root trigger is the empty
     * string. The id of a non-root trigger is the concatenation of the id of
     * its parent, a dot and its {@link #relativeId}. The id is used to address
     * a trigger from script code.
     * 
     * @return the id of this trigger.
     * @see #getById(String)
     * @see #relativeId
     */
    public final String getId() {
        if (parent == null)
            return "";
        else {
            String parentId = parent.getId();
            if (parentId.length() == 0)
                return relativeId;
            else
                return parentId + "." + relativeId;
        }
    }

    /**
     * Returns the name of this trigger as seen by the user when graphically
     * editing the tools.
     * 
     * @return the name of this trigger as seen by the user when graphically
     *         editing the tools.
     */
    public final String getName() {
        return name;
    }

    /**
     * {@inheritDoc} This implementation returns the name of this trigger.
     * 
     * @see #getName()
     */
    @Override
    public final String toString() {
        return name;
    }

    /**
     * Returns all children of this trigger. The returned collection must not be
     * modified.
     * 
     * @return all children of this trigger.
     */
    public final Collection<Trigger> getChildren() {
        if (children == null)
            return Collections.emptyList();
        else
            return children.values();
    }

    /**
     * Returns the parent of this trigger.
     * 
     * @return the parent of this trigger.
     */
    public Trigger getParent() {
        return parent;
    }

    /**
     * Returns the description of this trigger as seen by the user when
     * graphically editing the tools.
     * 
     * @return the description of this trigger as seen by the user when
     *         graphically editing the tools.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Adds a parameter slot to this trigger. Parameter slots are used to
     * constrain the set of matched user gestures. Must only be called by
     * constructors of classes derived from {@code Trigger}.
     * 
     * @param slot
     *            the parameter slot to add.
     */
    protected void addParameter(Slot<?> slot) {
        if (parameters == null) {
            parameters = new HashMap<String, Slot<?>>();
        }
        parameters.put(slot.getId(), slot);
    }

    /**
     * Adds an output slot to this trigger. Output slots are filled by a call to
     * {@link #apply(UserGesture, SlotMap, SlotMap, SlotMap)}. Must only be
     * called by constructors of classes derived from {@code Trigger}.
     * 
     * @param slot
     *            the output slot to add.
     */
    protected void addOutSlot(Slot<?> slot) {
        if (outSlots == null) {
            outSlots = new HashMap<String, Slot<?>>();
        }
        outSlots.put(slot.getId(), slot);
    }

    /**
     * Creates a new {@code Map} containing all parameter slots of this trigger
     * and its ancestors. Parameter slots are used to constrain the set of
     * matched user gestures. The slots are obtained from the returned map by
     * their id.
     * 
     * @return a new {@code Map} containing all parameter slots of this trigger
     *         and its ancestors.
     * @see #parameters
     */
    public Map<String, Slot<?>> createParameters() {
        Map<String, Slot<?>> result;
        if (parent == null) {
            result = new HashMap<String, Slot<?>>();
        } else {
            result = parent.createParameters();
        }
        if (parameters != null) {
            result.putAll(parameters);
        }
        return result;
    }

    /**
     * Returns the output slots of this trigger. They can be filled by a call to
     * {@link #apply(UserGesture, SlotMap, SlotMap, SlotMap)}. The slots are
     * obtained from the returned map by their id.
     * 
     * @return the output slots of this trigger.
     * @see #outSlots
     */
    public Map<String, Slot<?>> getOutSlots() {
        if (outSlots != null)
            return outSlots;
        else
            return Collections.emptyMap();
    }

    /**
     * Tests if this trigger matches the specified user gesture. In the employed
     * template method pattern, this method plays the r&ocirc;le of the template
     * method, which automatically asks all ancestors of this trigger first. To
     * define the specific behavior of a derived trigger class, override
     * {@link #matches(InSlotMap, InSlotMap, UserGesture)}.
     * 
     * @param userGesture
     *            the user gesture to match.
     * @param parameters
     *            the values of the parameter slots.
     * @param in
     *            the values of the input slots.
     * @return {@code true} if this trigger matches the specified user gesture.
     * @see #parameters
     */
    public final boolean matches(UserGesture userGesture, SlotMap parameters,
            SlotMap in) {
        if (parent != null) {
            if (!parent.matches(userGesture, parameters, in))
                return false;
        }
        return matches(parameters, in, userGesture);
    }

    /**
     * Extracts information from the specified user gesture and stores it in the
     * output slots. In the employed template method pattern, this method plays
     * the r&ocirc;le of the template method, which automatically calls all
     * ancestors of this trigger first. To define the specific behavior of a
     * derived trigger class, override
     * {@link #apply(InSlotMap, InSlotMap, OutSlotMap, UserGesture)}. Before
     * calling this method, {@link #matches(UserGesture, SlotMap, SlotMap)} has
     * to be called and to return {@code true} in order to assure that this
     * trigger actually matches the specified user gesture.
     * 
     * @param userGesture
     *            the user gesture to extract information from.
     * @param parameters
     *            the values of the parameter slots.
     * @param in
     *            the values of the input slots.
     * @param out
     *            the container to hold the values of the output slots.
     * @see #parameters
     */
    public final void apply(UserGesture userGesture, SlotMap parameters,
            SlotMap in, SlotMap out) {
        if (parent != null) {
            parent.apply(userGesture, parameters, in, out);
        }
        apply(parameters, in, out, userGesture);
    }

    /**
     * Tests if this trigger matches the specified user gesture. In the employed
     * template method pattern, this method plays the r&ocirc;le of the
     * primitive operation. As this method is automatically called for all
     * ancestors of a trigger, the overridden method has only to check the
     * additional constraints of this trigger and can safely assume that the
     * constraints of its parent are already satisfied.
     * 
     * @param parameters
     *            the values of the parameter slots.
     * @param in
     *            the values of the input slots.
     * @param userGesture
     *            the user gesture to match.
     * @return {@code true} if this trigger matches the specified user gesture.
     * @see #parameters
     */
    protected abstract boolean matches(InSlotMap parameters, InSlotMap in,
            UserGesture userGesture);

    /**
     * Extracts information from the specified user gesture and stores it in the
     * output slots. In the employed template method pattern, this method plays
     * the r&ocirc;le of the primitive operation. As this method is
     * automatically called for all ancestors of a trigger, the overridden
     * method has only to fill the output slots created by this trigger itself.
     * It can safely assume that this trigger matches the specified user
     * gesture. The default implementation does nothing.
     * 
     * @param parameters
     *            the values of the parameter slots.
     * @param in
     *            the values of the input slots.
     * @param out
     *            the container to hold the values of the output slots.
     * @param userGesture
     *            the user gesture to extract information from.
     * @see #parameters
     */
    protected void apply(InSlotMap parameters, InSlotMap in, OutSlotMap out,
            UserGesture userGesture) {
    }

    private void addAnnotatedSlots() {
        Class<?> thisClass = getClass();
        for (Field field : thisClass.getFields()) {
            try {
                ParamSlot paramSlot = field.getAnnotation(ParamSlot.class);
                if (paramSlot != null) {
                    if (Slot.class.isAssignableFrom(field.getType())) {
                        addParameter((Slot<?>) field.get(null));
                    } else
                        throw new RuntimeException(
                                "Annotated field must be a slot.");
                    continue;
                }
                OutSlot outSlot = field.getAnnotation(OutSlot.class);
                if (outSlot != null) {
                    if (Slot.class.isAssignableFrom(field.getType())) {
                        addOutSlot((Slot<?>) field.get(null));
                    } else
                        throw new RuntimeException(
                                "Annotated field must be a slot.");
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(
                        "Annotated field must be accessible.");
            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
